/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
 
package org.jboss.ejb.plugins.jaws.jdbc;

import org.jboss.ejb.plugins.jaws.JPMStartCommand;

/**
 * JAWSPersistenceManager JDBCStartCommand
 *    
 * @see <related>
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:justin@j-m-f.demon.co.uk">Justin Forder</a>
 * @version $Revision: 1.1 $
 */
public class JDBCStartCommand implements JPMStartCommand
{
   // Constructors --------------------------------------------------
   
   public JDBCStartCommand(JDBCCommandFactory factory)
   {
   }
   
   // JPMStartCommand implementation --------------------------------
   
   public void execute() throws Exception
   {
   }
}
