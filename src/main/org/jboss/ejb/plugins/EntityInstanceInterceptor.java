/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.ejb.EJBObject;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.NoSuchEntityException;
import javax.ejb.RemoveException;
import javax.ejb.EntityBean;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.ejb.Container;
import org.jboss.ejb.EntityContainer;
import org.jboss.ejb.EntityPersistenceManager;
import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.InstanceCache;
import org.jboss.ejb.InstancePool;
import org.jboss.ejb.MethodInvocation;

/**
 *   This container acquires the given instance. 
 *
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.4 $
 */
public class EntityInstanceInterceptor
   extends AbstractInterceptor
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
	protected EntityContainer container;
   
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   public void setContainer(Container container) 
   { 
   	this.container = (EntityContainer)container; 
   }
	
   public  Container getContainer()
   {
   	return container;
   }

   // Interceptor implementation --------------------------------------
   public Object invokeHome(MethodInvocation mi)
      throws Exception
   {
      // Get context
      mi.setEnterpriseContext(((EntityContainer)getContainer()).getInstancePool().get());
      
      try
      {
         // Invoke through interceptors
         return getNext().invokeHome(mi);
      } finally
      {
         // Still free? Not free if create() was called successfully
         if (mi.getEnterpriseContext().getId() == null)
         {
            container.getInstancePool().free(mi.getEnterpriseContext());
         } else
         {
//            System.out.println("Entity was created; not returned to pool");
            ((EntityContainer)getContainer()).getInstanceCache().release(mi.getEnterpriseContext());
         }
      }
   }

   public Object invoke(MethodInvocation mi)
      throws Exception
   {
      // Get context
	  // The cache will properly managed the tx-ctx locking, in case the mi transaction is different.
      mi.setEnterpriseContext(((EntityContainer)getContainer()).getInstanceCache().get(mi.getId()));
      try
      {
         // Invoke through interceptors
         return getNext().invoke(mi);
      } catch (RemoteException e)
		{
			// Discard instance
			// EJB 1.1 spec 12.3.1
			((EntityContainer)getContainer()).getInstanceCache().remove(mi.getId());
			
			throw e;
		} catch (RuntimeException e)
		{
			// Discard instance
			// EJB 1.1 spec 12.3.1
			((EntityContainer)getContainer()).getInstanceCache().remove(mi.getId());
			
			throw e;
		} catch (Error e)
		{
			// Discard instance
			// EJB 1.1 spec 12.3.1
			((EntityContainer)getContainer()).getInstanceCache().remove(mi.getId());
			
			throw e;
		} finally
      {
//         System.out.println("Release instance for "+id);
			EnterpriseContext ctx = mi.getEnterpriseContext();
			if (ctx != null)
			{
				if (ctx.getId() == null)
				{
				   // Remove from cache
				   ((EntityContainer)getContainer()).getInstanceCache().remove(mi.getId());
				   
				   // It has been removed -> send to free pool
				   container.getInstancePool().free(ctx);
				}
				{
				   // Return context
				   ((EntityContainer)getContainer()).getInstanceCache().release(ctx);
				}
			}
      }
   }
}

