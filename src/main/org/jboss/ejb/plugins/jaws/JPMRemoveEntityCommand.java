/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
 
package org.jboss.ejb.plugins.jaws;

import org.jboss.ejb.EntityEnterpriseContext;
import java.rmi.RemoteException;
import javax.ejb.RemoveException;

/**
 * Interface for JAWSPersistenceManager RemoveEntity Command.
 *      
 * @author <a href="mailto:justin@j-m-f.demon.co.uk">Justin Forder</a>
 * @version $Revision: 1.3 $
 */
public interface JPMRemoveEntityCommand
{
   // Public --------------------------------------------------------
   
   public void execute(EntityEnterpriseContext ctx)
      throws RemoteException, RemoveException;
}
