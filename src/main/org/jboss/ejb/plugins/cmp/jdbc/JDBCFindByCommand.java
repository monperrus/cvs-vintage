/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.ejb.plugins.cmp.jdbc;

import java.lang.reflect.Method;

import java.sql.PreparedStatement;

import java.util.Iterator;

import org.jboss.ejb.plugins.cmp.jdbc.bridge.JDBCCMPFieldBridge;
import org.jboss.ejb.plugins.cmp.jdbc.metadata.JDBCQueryMetaData;

/**
 * JDBCFindByCommand automatic finder used in CMP 1.x.  This should 
 * be disabled for 2.x.  I will finish this command in CMP 2.x phase 3.
 *
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:marc.fleury@telkel.com">Marc Fleury</a>
 * @author <a href="mailto:shevlandj@kpi.com.au">Joe Shevland</a>
 * @author <a href="mailto:justin@j-m-f.demon.co.uk">Justin Forder</a>
 * @author <a href="mailto:danch@nvisia.com">danch (Dan Christopherson)</a>
 * @version $Revision: 1.3 $
 */
public class JDBCFindByCommand extends JDBCFinderCommand
{
   // Attributes ----------------------------------------------------
   
   // The meta-info for the field we are finding by
   private JDBCCMPFieldBridge cmpField;
   
   // Constructors --------------------------------------------------
   
   public JDBCFindByCommand(JDBCStoreManager manager, JDBCQueryMetaData q)
      throws IllegalArgumentException
   {
      super(manager, q);
      
		String finderName = q.getMethod().getName();
		
		// finder name will be like findByFieldName
		// we need to convert it to fieldName.
      String cmpFieldName = Character.toLowerCase(finderName.charAt(6)) +
				finderName.substring(7);

      log.debug("Finder: " + cmpFieldName);
      
		cmpField = (JDBCCMPFieldBridge)entity.getCMPFieldByName(cmpFieldName);
		if(cmpField == null) {
			throw new IllegalArgumentException(
				"No finder for this method: " + finderName);
		}
      
		// Compute SQL      
      StringBuffer sql = new StringBuffer();
		sql.append("SELECT ").append(SQLUtil.getColumnNamesClause(entity.getJDBCPrimaryKeyFields()));
		sql.append(" FROM ").append(entityMetaData.getTableName());
	   sql.append(" WHERE ").append(SQLUtil.getWhereClause(cmpField));
      
      setSQL(sql.toString());
   }
   
   // JDBCFinderCommand overrides -----------------------------------
   
   protected void setParameters(PreparedStatement ps, Object argOrArgs) 
      throws Exception
   {
		Object[] args = (Object[])argOrArgs;
		
		if (cmpField != null) {
			cmpField.setArgumentParameters(ps, 1, args[0]);			
		}
	}
}
