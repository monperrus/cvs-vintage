/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins;

import java.rmi.RemoteException;

import org.jboss.ejb.deployment.EntityInstancePoolConfiguration;
   
import org.jboss.ejb.Container;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.EntityEnterpriseContext;

/**
 *	<description> 
 *      
 *	@see <related>
 *	@author Rickard �berg (rickard.oberg@telkel.com)
 *	@version $Revision: 1.1 $
 */
public class EntityInstancePool
   extends AbstractInstancePool
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   
   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   /**
    *   Return an instance to the free pool. Reset state
    *
    *   Called in 3 cases:
    *   a) Done with finder method
    *   b) Removed
    *   c) Passivated
    *
    * @param   ctx  
    */
   public synchronized void free(EnterpriseContext ctx)
   {
      // Reset instance
      ((EntityEnterpriseContext)ctx).setSynchronized(false);
      ((EntityEnterpriseContext)ctx).setInvoked(false);
      
      super.free(ctx);
   }
   
   // Z implementation ----------------------------------------------
   public void start()
      throws Exception
   {
      EntityInstancePoolConfiguration conf = (EntityInstancePoolConfiguration)getContainer().getMetaData().getContainerConfiguration().getInstancePoolConfiguration();
      maxSize = conf.getMaximumSize();
   }
    
   // Package protected ---------------------------------------------
    
   // Protected -----------------------------------------------------
   protected EnterpriseContext create(Object instance, Container con)
      throws RemoteException
   {
      return new EntityEnterpriseContext(instance, getContainer());
   }
    
   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------

}

