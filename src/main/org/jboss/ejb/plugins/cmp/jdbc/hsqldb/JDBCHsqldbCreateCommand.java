/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.ejb.plugins.cmp.jdbc.hsqldb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jboss.ejb.plugins.cmp.jdbc.JDBCAbstractVendorCreateCommand;
import org.jboss.ejb.plugins.cmp.jdbc.JDBCUtil;

/**
 * JDBCHsqldbCreateCommand executes an INSERT INTO query.
 * The command uses a CALL IDENTITY() statement from hsqldb native
 * to fetch the generated PK.
 * It works under JDK versions 1.3 and 1.4.
 *
 * @author <a href="mailto:julien_viet@yahoo.fr">Julien Viet</a>
 * @author <a href="mailto:loubyansky@hotmail.com">Alex Loubyansky</a>
 *
 * @version $Revision: 1.2 $
 */
public class JDBCHsqldbCreateCommand
   extends JDBCAbstractVendorCreateCommand
{

   /** Hsqldb statement that will fetch the last inserted key */
   private static final String IDENTITY_SQL = "CALL IDENTITY()";

   protected Object fetchPK(PreparedStatement statement) throws Exception
   {
      PreparedStatement ps = null;
      try
      {
         Object pk = null;
         //Retrieve the last identity created for this Connection
         ps = statement.getConnection().prepareStatement("IDENTITY_SQL");
         ResultSet rs = ps.executeQuery();

         if (rs.next())
         {
            pk = rs.getObject(1);
         }
         return pk;
      }
      finally
      {
         JDBCUtil.safeClose(ps);
      }
   }

}
