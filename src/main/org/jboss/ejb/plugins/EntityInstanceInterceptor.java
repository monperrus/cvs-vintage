/*
* JBoss, the OpenSource J2EE webOS
*
* Distributable under LGPL license.
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
import org.jboss.ejb.BeanLock;
import org.jboss.ejb.BeanLockManager;
import org.jboss.ejb.EntityContainer;
import org.jboss.ejb.EntityPersistenceManager;
import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.InstanceCache;
import org.jboss.ejb.InstancePool;
import org.jboss.ejb.MethodInvocation;
import org.jboss.ejb.CacheKey;
import org.jboss.logging.log4j.JBossCategory;
import org.jboss.metadata.EntityMetaData;
import org.jboss.tm.TxManager;

/**
* The instance interceptors role is to acquire a context representing
* the target object from the cache.
*
* <p>This particular container interceptor implements pessimistic locking
*    on the transaction that is associated with the retrieved instance.  If
*    there is a transaction associated with the target component and it is
*    different from the transaction associated with the MethodInvocation
*    coming in then the policy is to wait for transactional commit. 
*   
* <p>We also implement serialization of calls in here (this is a spec
*    requirement). This is a fine grained notify, notifyAll mechanism. We
*    notify on ctx serialization locks and notifyAll on global transactional
*    locks.
*   
* <p><b>WARNING: critical code</b>, get approval from senior developers
*    before changing.
*    
* @author <a href="mailto:marc.fleury@jboss.org">Marc Fleury</a>
* @author <a href="mailto:Scott.Stark@jboss.org">Scott Stark</a>
* @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
* @version $Revision: 1.41 $
*
* <p><b>Revisions:</b><br>
* <p><b>2001/06/28: marcf</b>
* <ol>
*   <li>Moved to new synchronization
*   <li>Pools are gone simple design
*   <li>two levels of syncrhonization with Tx and ctx
*   <li>remove busy wait from previous mechanisms
* </ol>
* <p><b>2001/07/11: starksm</b>
* <ol>
*   <li>Fix a thread starvation problem due to incomplete condition notification
*   <li>Add support for trace level diagnositics
* </ol>
* <p><b>2001/07/12: starksm</b>
* <ol>
*   <li>Handle a race condition when there is no ctx transaction
* </ol>
* </ol>
* <p><b>2001/07/16: billb</b>
* <ol>
*   <li>Added wait(timeout) code, commented out so that we can easily turn it on
*   when this new code is done with it's trial period.
*   <li>Fixed bug when ejbLoad threw an exception and threads waiting 
*   on TxLock did not get awakened.
* </ol>
* <p><b>2001/07/26: billb</b>
* <ol>
*   <li>Locking is now separate from EntityEnterpriseContext objects and is now
*   encapsulated in BeanLock and BeanLockManager.  Did this because the lifetime
*   of an EntityLock is sometimes longer than the lifetime of the ctx.
* </ol>
* <p><b>2001/08/1: marcf</b>
* <ol>
*   <li> Taking the lock out pointed to something new, the fact that the locking policies
*    can be fully automated and factored out of the instance interceptor this results in a 
*    new interceptor EntityLockInterceptor that by design sits in front of this one in the 
*    chain of interceptors. 
*   <li> This interceptor is greatly simplified and all it does is retrieve the instance from cache
*   <li> Scheduling is implemented in new interceptor, all notify policies gone, simple
*   <li> For the record, locking went from cache (early 2.0) -> this interceptor -> new interceptor
*   <li> new locking is pluggable
* </ol>
*/
public class EntityInstanceInterceptor
extends AbstractInterceptor
{
	// Constants -----------------------------------------------------
	
	// Attributes ----------------------------------------------------
	
	protected EntityContainer container;
	
	// Static --------------------------------------------------------
	
	/** Use a JBoss custom log4j category for trace level logging */
	static JBossCategory log = (JBossCategory) JBossCategory.getInstance(EntityInstanceInterceptor.class);
	
	// Constructors --------------------------------------------------
	
	// Public --------------------------------------------------------
	
	public void setContainer(Container container)
	{
		this.container = (EntityContainer)container;
	}
	
	public Container getContainer()
	{
		return container;
	}
	
	// Interceptor implementation --------------------------------------
	
	public Object invokeHome(MethodInvocation mi)
	throws Exception
	{
		// Get context
		EntityEnterpriseContext ctx = (EntityEnterpriseContext)((EntityContainer)getContainer()).getInstancePool().get();
		
		// Pass it to the method invocation
		mi.setEnterpriseContext(ctx);
		
		// Give it the transaction
		ctx.setTransaction(mi.getTransaction());
		
		try
		{
			// Invoke through interceptors
			return getNext().invokeHome(mi);
		} 
		finally
		{         
			
			// Is the context now with an identity? in which case we need to insert
			if (ctx.getId() != null)
			{
				
				BeanLock lock = container.getLockManager().getLock(ctx.getCacheKey());
				
				lock.sync(); // lock all access to BeanLock
				
				try {
					
					// Set the transaction on the lock it will protect the instance
					lock.setTransaction(ctx.getTransaction());
					
					// marcf: possible race on creation and usage
					// insert instance in cache, 
					container.getInstanceCache().insert(ctx);
					
				}
				finally
				{
					lock.releaseSync();
					
					container.getLockManager().removeLockRef(ctx.getCacheKey());
				}
			}
			//Do not send back to pools in any case, let the instance be GC'ed
		}
	}
	
	public Object invoke(MethodInvocation mi)
	throws Exception
	{
		
		// The key
		CacheKey key = (CacheKey) mi.getId();
		
		// The context
		EntityEnterpriseContext ctx = (EntityEnterpriseContext) container.getInstanceCache().get(key);
		
		boolean trace = log.isTraceEnabled();
		if( trace ) log.trace("Begin invoke, key="+key);
			
		// Associate transaction, in the new design the lock already has the transaction from the 
		// previous interceptor
		ctx.setTransaction(mi.getTransaction());
		
		// Set context on the method invocation
		mi.setEnterpriseContext(ctx);
		
		boolean exceptionThrown = false;
		
		try
		{	
			return getNext().invoke(mi);
		}
		catch (RemoteException e)
		{
			exceptionThrown = true;
			throw e;
		} catch (RuntimeException e)
		{
			exceptionThrown = true;
			throw e;
		} catch (Error e)
		{
			exceptionThrown = true;
			throw e;
		} 
		finally
		{
			// ctx can be null if cache.get throws an Exception, for
			// example when activating a bean.
			if (ctx != null)
			{				
				// If an exception has been thrown, 
				if (exceptionThrown && 					
					// if tx, the ctx has been registered in an InstanceSynchronization. 
					// that will remove the context, so we shouldn't.
					// if no synchronization then we need to do it by hand
					!ctx.hasTxSynchronization()) 
				{
					// Discard instance
					// EJB 1.1 spec 12.3.1
					container.getInstanceCache().remove(key);
					
					if( trace ) log.trace("Ending invoke, exceptionThrown, ctx="+ctx);
				}
				else if (ctx.getId() == null)
				{
					// The key from the MethodInvocation still identifies the right cachekey
					container.getInstanceCache().remove(key);
					
					if( trace )	log.trace("Ending invoke, cache removal, ctx="+ctx);
						// no more pool return
				}
			}
			
			if( trace )	log.trace("End invoke, key="+key+", ctx="+ctx);
		
		}	// end invoke		
	}
	
	// Private --------------------------------------------------------
	
	private static Method getEJBHome;
	private static Method getHandle;
	private static Method getPrimaryKey;
	private static Method isIdentical;
	private static Method remove;
	
	static
	{
		try
		{
			Class[] noArg = new Class[0];
			getEJBHome = EJBObject.class.getMethod("getEJBHome", noArg);
			getHandle = EJBObject.class.getMethod("getHandle", noArg);
			getPrimaryKey = EJBObject.class.getMethod("getPrimaryKey", noArg);
			isIdentical = EJBObject.class.getMethod("isIdentical", new Class[] {EJBObject.class});
			remove = EJBObject.class.getMethod("remove", noArg);
		}
		catch (Exception x) {x.printStackTrace();}
	}
	
	private boolean isCallAllowed(MethodInvocation mi)
	{
		boolean reentrant = ((EntityMetaData)container.getBeanMetaData()).isReentrant();
		
		if (reentrant)
		{
			return true;
		}
		else
		{
			Method m = mi.getMethod();
			if (m.equals(getEJBHome) ||
				m.equals(getHandle) ||
				m.equals(getPrimaryKey) ||
				m.equals(isIdentical) ||
				m.equals(remove))
			{
				return true;
			}
		}
		
		return false;
	}
}



