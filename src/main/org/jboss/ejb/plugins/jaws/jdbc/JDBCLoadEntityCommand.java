/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.ejb.plugins.jaws.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Iterator;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.ServerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.plugins.jaws.JAWSPersistenceManager;
import org.jboss.ejb.plugins.jaws.JPMLoadEntityCommand;
import org.jboss.ejb.plugins.jaws.CMPFieldInfo;
import org.jboss.ejb.plugins.jaws.deployment.JawsEntity;
import org.jboss.ejb.plugins.jaws.deployment.JawsCMPField;

/**
 * JAWSPersistenceManager JDBCLoadEntityCommand
 *
 * @see <related>
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:marc.fleury@telkel.com">Marc Fleury</a>
 * @author <a href="mailto:shevlandj@kpi.com.au">Joe Shevland</a>
 * @author <a href="mailto:justin@j-m-f.demon.co.uk">Justin Forder</a>
 * @version $Revision: 1.4 $
 */
public class JDBCLoadEntityCommand
   extends JDBCQueryCommand
   implements JPMLoadEntityCommand
{
   // Constructors --------------------------------------------------

   public JDBCLoadEntityCommand(JDBCCommandFactory factory)
   {
      super(factory, "Load");

      // Select SQL
      String sql = "SELECT ";

      Iterator it = metaInfo.getCMPFieldInfos();
      boolean first = true;

      while (it.hasNext())
      {
         CMPFieldInfo fieldInfo = (CMPFieldInfo)it.next();

         if (fieldInfo.isEJBReference())
         {
            JawsCMPField[] pkFields = fieldInfo.getForeignKeyCMPFields();

            for (int i = 0; i < pkFields.length; i++)
            {
               sql += (first ? "" : ",") +
                      fieldInfo.getColumnName() + "_" +
                      pkFields[i].getColumnName();
               first = false;
            }
         } else
         {
            sql += (first ? "" : ",") +
                   fieldInfo.getColumnName();
            first = false;
         }
      }

      sql += " FROM " + metaInfo.getTableName() +
             " WHERE " + getPkColumnWhereList();

      setSQL(sql);
   }

   // JPMLoadEntityCommand implementation ---------------------------

   public void execute(EntityEnterpriseContext ctx)
      throws RemoteException
   {
      if ( !metaInfo.isReadOnly() || isTimedOut(ctx) )
      {
         try
         {
            jdbcExecute(ctx);
         } catch (Exception e)
         {
            throw new ServerException("Load failed", e);
         }
      }
   }

   // JDBCQueryCommand overrides ------------------------------------

   protected void setParameters(PreparedStatement stmt, Object argOrArgs)
      throws Exception
   {
      EntityEnterpriseContext ctx = (EntityEnterpriseContext)argOrArgs;

      setPrimaryKeyParameters(stmt, 1, ctx.getId());
   }

   protected Object handleResult(ResultSet rs, Object argOrArgs) throws Exception
   {
      EntityEnterpriseContext ctx = (EntityEnterpriseContext)argOrArgs;

      if (!rs.next())
      {
         throw new NoSuchObjectException("Entity "+ctx.getId()+" not found");
      }

      // Set values
      int idx = 1;

      Iterator iter = metaInfo.getCMPFieldInfos();
      while (iter.hasNext())
      {
         CMPFieldInfo fieldInfo = (CMPFieldInfo)iter.next();

         if (fieldInfo.isEJBReference())
         {
            // Create pk
            JawsCMPField[] pkFields = fieldInfo.getForeignKeyCMPFields();
            JawsEntity referencedEntity = (JawsEntity)pkFields[0].getBeanContext();
            Object pk;
            if (referencedEntity.getPrimaryKeyField().equals(""))
            {
               // Compound key
               pk = factory.getContainer().getClassLoader().loadClass(referencedEntity.getPrimaryKeyClass()).newInstance();
               Field[] fields = pk.getClass().getFields();
               for(int j = 0; j < fields.length; j++)
               {
                  Object val = getResultObject(rs, idx++, fields[j].getType());
                  fields[j].set(pk, val);

                  if (debug)
                  {
                     log.debug("Referenced pk field:" + val);
                  }
               }
            } else
            {
               // Primitive key
               pk = getResultObject(rs, idx++, fieldInfo.getField().getType());

               if (debug)
               {
                  log.debug("Referenced pk:" + pk);
               }
            }

            // Find referenced entity
            try
            {
               Object home = factory.getJavaCtx().lookup(fieldInfo.getSQLType());
               Method[] homeMethods = home.getClass().getMethods();
               Method finder = null;

               // We have to locate fBPK iteratively since we don't
               // really know the pk-class
               for (int j = 0; j < homeMethods.length; j++)
               {
                  if (homeMethods[j].getName().equals("findByPrimaryKey"))
                  {
                     finder = homeMethods[j];
                     break;
                  }
               }

               if (finder == null)
               {
                  throw new NoSuchMethodException(
                     "FindByPrimaryKey method not found in home interface");
               }

               log.debug("PK=" + pk);
               Object ref = finder.invoke(home, new Object[] { pk });

               // Set found entity
               setCMPFieldValue(ctx.getInstance(), fieldInfo, ref);
            } catch (Exception e)
            {
               throw new ServerException("Could not restore reference", e);
            }
         } else
         {
            // Load primitive

            // TODO: this probably needs to be fixed for BLOB's etc.
            setCMPFieldValue(ctx.getInstance(),
                             fieldInfo,
                             getResultObject(rs, idx++, fieldInfo.getField().getType()));
         }
      }

      // Store state to be able to do tuned updates
      JAWSPersistenceManager.PersistenceContext pCtx =
         (JAWSPersistenceManager.PersistenceContext)ctx.getPersistenceContext();
      if (metaInfo.isReadOnly()) pCtx.lastRead = System.currentTimeMillis();
      pCtx.state = getState(ctx);

      return null;
   }

   // Protected -----------------------------------------------------

   protected boolean isTimedOut(EntityEnterpriseContext ctx)
   {
      JAWSPersistenceManager.PersistenceContext pCtx =
         (JAWSPersistenceManager.PersistenceContext)ctx.getPersistenceContext();

      return (System.currentTimeMillis() - pCtx.lastRead) > metaInfo.getReadOnlyTimeOut();
   }
}
