/**
* JBoss, the OpenSource J2EE webOS
*
* Distributable under LGPL license.
* See terms of license at gnu.org.
*/
package org.jboss.ejb.plugins;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

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

import org.jboss.ejb.BeanLock;
import org.jboss.ejb.BeanLockManager;
import org.jboss.ejb.Container;
import org.jboss.ejb.EntityContainer;
import org.jboss.ejb.EntityPersistenceManager;
import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.InstanceCache;
import org.jboss.ejb.InstancePool;
import org.jboss.ejb.MethodInvocation;
import org.jboss.metadata.ConfigurationMetaData;
import org.jboss.logging.log4j.JBossCategory;
import org.jboss.util.Sync;

/**
* The role of this interceptor is to synchronize the state of the cache with
* the underlying storage.  It does this with the ejbLoad and ejbStore
* semantics of the EJB specification.  In the presence of a transaction this
* is triggered by transaction demarcation. It registers a callback with the
* underlying transaction monitor through the JTA interfaces.  If there is no
* transaction the policy is to store state upon returning from invocation.
* The synchronization polices A,B,C of the specification are taken care of
* here.
*
* <p><b>WARNING: critical code</b>, get approval from senior developers
*    before changing.
*
* @author <a href="mailto:marc.fleury@jboss.org">Marc Fleury</a>
* @author <a href="mailto:Scott.Stark@jboss.org">Scott Stark</a>
* @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
* @version $Revision: 1.46 $
*
* <p><b>Revisions:</b><br>
* <p><b>2001/06/28: marcf</b>
* <ol>
*   <li>Moved to new synchronization
*   <li>afterCompletion doesn't return to pool anymore, idea is to simplify
*       design by not mucking with reuse of the instances
*   <li>before completion checks for a rolledback tx and doesn't call the
*       store in case of a rollback we are notified but we don't register
*       the resource
* </ol>
* <p><b>2001/07/12: starksm</b>
* <ol>
*   <li>Fixed invalid use of the ctx.transaction in beforeCompletion
*   <li>Added clearContextTx to make sure clean up of ctx.tx was done consistently
*   <li>Clean up the EntityContainer cast-fest
* </ol>
* <p><b>2001/07/26: billb</b>
* <ol>
*   <li>Locking is now separate from EntityEnterpriseContext objects and is now
*   encapsulated in BeanLock and BeanLockManager.  Did this because the lifetime
*   of an EntityLock is sometimes longer than the lifetime of the ctx.
* </ol>
* <p><b>2001/08/01: marcf</b>
* <ol>
*   <li>Updated BeanLock to work in new interceptor and out of InstanceInterceptor so we update
*   here as well to use the new interceptor lock logic and the BeanLock that bill introduced
*   <li>Make use of clearContextTx in all exceptional cases
* </ol>
* <p><b>2001/08/02: marcf</b>
* <ol>
*   <li>Moved policy to pluggable framework. Work through the interface of the lock only
*   Use of "endTransaction" and "wontSynchronize" to communicate with the lock
* </ol>
*/
public class EntitySynchronizationInterceptor
extends AbstractInterceptor
{
	// Constants -----------------------------------------------------
	
	// Attributes ----------------------------------------------------
	/** Use a JBoss custom log4j category for trace level logging */
	static JBossCategory log = (JBossCategory) JBossCategory.getInstance(EntityInstanceInterceptor.class);
	
	/**
	*  The current commit option.
	*/
	protected int commitOption;
	
	/**
	*  The refresh rate for commit option d
	*/
	protected long optionDRefreshRate;
	
	/**
	*  The container of this interceptor.
	*/
	protected EntityContainer container;
	
	/**
	*  Optional isModified method
	*/
	protected Method isModified;
	
	/**
	*  For commit option D this is the cache of valid entities
	*/
	protected HashSet validContexts;
	
	// Static --------------------------------------------------------
	
	// Constructors --------------------------------------------------
	
	// Public --------------------------------------------------------
	
	public void setContainer(Container container)
	{
		this.container = (EntityContainer)container;
	}
	
	public void init()
	throws Exception
	{
		
		try
		{         
			validContexts = new HashSet();
			ConfigurationMetaData configuration = container.getBeanMetaData().getContainerConfiguration();
			commitOption = configuration.getCommitOption();
			optionDRefreshRate = configuration.getOptionDRefreshRate();
			
			//start up the validContexts thread if commit option D
			if(commitOption == ConfigurationMetaData.D_COMMIT_OPTION)
			{
				ValidContextsRefresher vcr = new ValidContextsRefresher(validContexts, optionDRefreshRate);
				new Thread(vcr).start();
			}
			
			isModified = container.getBeanClass().getMethod("isModified", new Class[0]);
			if (!isModified.getReturnType().equals(Boolean.TYPE))
				isModified = null; // Has to have "boolean" as return type!
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public Container getContainer()
	{
		return container;
	}
	
	/**
	*  Register a transaction synchronization callback with a context.
	*/
	private void register(EntityEnterpriseContext ctx, Transaction tx)
	{
		boolean trace = log.isTraceEnabled();
		if( trace )
			log.trace("register, ctx="+ctx+", tx="+tx);
		
		// Create a new synchronization
		InstanceSynchronization synch = new InstanceSynchronization(tx, ctx);
		
		EntityContainer ctxContainer = null;
		try
		{
			ctxContainer = (EntityContainer) ctx.getContainer();
			// associate the entity bean with the transaction so that
			// we can do things like synchronizeEntitiesWithinTransaction
			ctxContainer.getTxEntityMap().associate(tx, ctx);
			
			// We want to be notified when the transaction commits
			tx.registerSynchronization(synch);
			
			ctx.hasTxSynchronization(true);
		}
		catch (RollbackException e)
		{
			//Indicates that the transaction is already marked for rollback
			ctxContainer.getTxEntityMap().disassociate(tx, ctx);
			
			// The state in the instance is to be discarded, we force a reload of state
			synchronized (ctx)
			{ctx.setValid(false);}
			
			// This is really a mistake from the JTA spec, the fact that the tx is marked rollback should not be relevant
			// We should still hear about the demarcation
			clearContextTx("RollbackException", ctx, tx, trace);         
		}
		catch (Exception e)
		{
			if( ctxContainer != null )
				ctxContainer.getTxEntityMap().disassociate(tx, ctx);
			// If anything goes wrong with the association remove the ctx-tx association
			clearContextTx("Exception", ctx, tx, trace);
			throw new EJBException(e);
		}
	}
	
	/**
	* As per the spec 9.6.4, entities must be synchronized with the datastore
	* when an ejbFind<METHOD> is called.
	*/
	private void synchronizeEntitiesWithinTransaction(Transaction tx) throws Exception
	{
		Object[] entities = container.getTxEntityMap().getEntities(tx);
		for (int i = 0; i < entities.length; i++)
		{
			EntityEnterpriseContext ctx = (EntityEnterpriseContext)entities[i];
			storeEntity(ctx);
		}
	}
	
	private void storeEntity(EntityEnterpriseContext ctx) throws Exception
	{
		if (ctx.getId() != null)
		{
			boolean dirty = true;
			// Check isModified bean method flag
			if (isModified != null)
			{
				Object[] args = {};
				Boolean modified = (Boolean) isModified.invoke(ctx.getInstance(), args);
				dirty = modified.booleanValue();
			}
			
			// Store entity
			if (dirty)
			{
				System.out.println("storing");
				container.getPersistenceManager().storeEntity(ctx);
			}
		}
	}
	
	// Interceptor implementation --------------------------------------
	
	public Object invokeHome(MethodInvocation mi)
	throws Exception
	{
		
		EntityEnterpriseContext ctx = (EntityEnterpriseContext)mi.getEnterpriseContext();
		Transaction tx = mi.getTransaction();
		
		try
		{
			if (tx != null && mi.getMethod().getName().startsWith("find"))
			{
				// As per the spec EJB2.0 9.6.4, entities must be synchronized with the datastore
				// when an ejbFind<METHOD> is called.
				synchronizeEntitiesWithinTransaction(tx);
			}
			
			return getNext().invokeHome(mi);
		
		} finally
		{
			
			// An anonymous context was sent in, so if it has an id it is a real instance now
			if (ctx.getId() != null)
			{
				
				// Currently synched with underlying storage
				ctx.setValid(true);
				
				if (tx!= null) register(ctx, tx); // Set tx
			}
		}
	}
	
	public Object invoke(MethodInvocation mi)
	throws Exception
	{
		// We are going to work with the context a lot
		EntityEnterpriseContext ctx = (EntityEnterpriseContext)mi.getEnterpriseContext();
		
		// The Tx coming as part of the Method Invocation
		Transaction tx = mi.getTransaction();
		
		if( log.isTraceEnabled() )
			log.trace("invoke called for ctx "+ctx+", tx="+tx);
		
		//Commit Option D.... 
		if(commitOption == ConfigurationMetaData.D_COMMIT_OPTION && !validContexts.contains(ctx.getId()))
		{
			//bean isn't in cache
			//so set valid to false so that we load...
			ctx.setValid(false);
		}
		
		// Is my state valid?
		if (!ctx.isValid())
		{
			try
			{
				// If not tell the persistence manager to load the state
				container.getPersistenceManager().loadEntity(ctx);
			}
			catch (Exception ex)
			{
				clearContextTx("loadEntity Exception", ctx, tx, log.isTraceEnabled());
				throw ex;
			}
			
			
			// Now the state is valid
			ctx.setValid(true);
		}
		
		// So we can go on with the invocation
		
		// Invocation with a running Transaction
		if (tx != null && tx.getStatus() != Status.STATUS_NO_TRANSACTION)
		{
			try
			{     
				//Invoke down the chain
				return getNext().invoke(mi);  
			}
			finally
			{
				//register the wrapper with the transaction monitor (but only register once).
				// The transaction demarcation will trigger the storage operations
				if (!ctx.hasTxSynchronization()) register(ctx,tx);
			}
		}
		//
		else
		{ // No tx
			try
			{
				
				Object result = getNext().invoke(mi);
				
				// Store after each invocation -- not on exception though, or removal
				// And skip reads too ("get" methods)
				if (ctx.getId() != null)
				{
					storeEntity(ctx);
				}
				
				return result;
			
			}
			catch (Exception e)
			{
				// Exception - force reload on next call
				ctx.setValid(false);
				throw e;
			}
		}
	}
	
	
	// Protected  ----------------------------------------------------
	
	// Inner classes -------------------------------------------------
	
	private class InstanceSynchronization
	implements Synchronization
	{
		/**
		*  The transaction we follow.
		*/
		private Transaction tx;
		
		/**
		*  The context we manage.
		*/
		private EntityEnterpriseContext ctx;
		
		/**
		* The context lock
		*/
		private BeanLock lock;
		
		/**
		*  Create a new instance synchronization instance.
		*/
		InstanceSynchronization(Transaction tx, EntityEnterpriseContext ctx)
		{
			this.tx = tx;
			this.ctx = ctx;
			this.lock = container.getLockManager().getLock(ctx.getCacheKey());
		}
		
		// Synchronization implementation -----------------------------
		
		public void beforeCompletion()
		{
			boolean trace = log.isTraceEnabled();
			if( trace )
				log.trace("beforeCompletion called for ctx "+ctx);
			
			if (ctx.getId() != null)
			{
				// This is an independent point of entry. We need to make sure the
				// thread is associated with the right context class loader
				ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().setContextClassLoader(container.getClassLoader());
				
				try
				{
					try
					{
						// Store instance if business method was invoked
						if( trace )
							log.trace("Checking ctx="+ctx+", for status of tx="+tx);
						if (tx.getStatus() != Status.STATUS_MARKED_ROLLBACK)
						{
							// Check if the bean defines the isModified method
							boolean dirty = true;
							if (isModified != null)
							{
								try
								{
									Object[] args = {};
									Boolean modified = (Boolean) isModified.invoke(ctx.getInstance(), args);
									dirty = modified.booleanValue();
								}
								catch (Exception ignored)
								{
								}
							}
							
							if( trace )
								log.trace("sync calling store on ctx "+ctx+", dirty="+dirty);
							if (dirty)
								container.getPersistenceManager().storeEntity(ctx);
						}
					}
					catch (NoSuchEntityException ignored)
					{
						if( trace )
							log.trace("Ignoring NSEE", ignored);
					}
				}
				// EJB 1.1 12.3.2: all exceptions from ejbStore must be marked for rollback
				// and the instance must be discarded
				catch (Exception e)
				{
					log.error("Store failed", e);
					// Store failed -> rollback!
					try
					{
						tx.setRollbackOnly();
					}
					catch (SystemException ex)
					{
						if( trace )
							log.trace("Ignoring SE", ex);
					}
					catch (IllegalStateException ex)
					{
						if( trace )
							log.trace("Ignoring ISE", ex);
					}
				}
				finally
				{
					Thread.currentThread().setContextClassLoader(oldCl);
				}
			}
		}
		
		public void afterCompletion(int status)
		{
			boolean trace = log.isTraceEnabled();
			
			// This is an independent point of entry. We need to make sure the
			// thread is associated with the right context class loader
			ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(container.getClassLoader());
			
			lock.sync();
			try
			{
				try
				{
					// If rolled back -> invalidate instance
					if (status == Status.STATUS_ROLLEDBACK)
					{
						// remove from the cache
						container.getInstanceCache().remove(ctx.getCacheKey());
					}
					else
					{
						switch (commitOption)
						{
							// Keep instance cached after tx commit
							case ConfigurationMetaData.A_COMMIT_OPTION:
								// The state is still valid (only point of access is us)
								ctx.setValid(true);
							break;
							
							// Keep instance active, but invalidate state
							case ConfigurationMetaData.B_COMMIT_OPTION:
								// Invalidate state (there might be other points of entry)
								ctx.setValid(false);
							break;
							// Invalidate everything AND Passivate instance
							case ConfigurationMetaData.C_COMMIT_OPTION:
								try
								{
									container.getInstanceCache().release(ctx);
								}
								catch (Exception e)
								{
									log.debug(e);
								}
							break;
							case ConfigurationMetaData.D_COMMIT_OPTION:
								//if the local cache is emptied then valid is set to false(see invoke() )
								validContexts.add(ctx.getId());
							break;
						}
					}
				}
				finally
				{
					// finish the transaction association
					container.getTxEntityMap().disassociate(tx, ctx);
					if( trace )
						log.trace("afterCompletion, clear tx for ctx="+ctx+", tx="+tx);
					// The context is no longer synchronized on the TX
					ctx.hasTxSynchronization(false);
					
					ctx.setTransaction(null);
					
					lock.endTransaction(tx);
					
					if( trace )
						log.trace("afterCompletion, sent notify on TxLock for ctx="+ctx);
				}
			} // synchronized(lock)
			finally
			{
				lock.releaseSync();
				container.getLockManager().removeLockRef(lock.getId());
				Thread.currentThread().setContextClassLoader(oldCl);               
			}
		}
	
	}
	
	private void clearContextTx(String msg, EntityEnterpriseContext ctx, Transaction tx, boolean trace)
	{
		BeanLock lock = container.getLockManager().getLock(ctx.getCacheKey());
		lock.sync();
		try
		{
			if( trace )
				log.trace(msg+", clear tx for ctx="+ctx+", tx="+tx);
			// The context is no longer synchronized on the TX
			ctx.hasTxSynchronization(false);
			ctx.setTransaction(null);
			
			lock.wontSynchronize(tx);
		}
		finally
		{
			lock.releaseSync();
			
			container.getLockManager().removeLockRef(lock.getId());
		}
	}
	
	class ValidContextsRefresher implements Runnable
	{
		private HashSet validContexts;
		private long refreshRate;
		
		public ValidContextsRefresher(HashSet validContexts,long refreshRate)
		{
			this.validContexts = validContexts;
			this.refreshRate = refreshRate;
		}
		
		public void run()
		{
			while(true)
			{
				validContexts.clear();
				log.trace("Flushing the valid contexts");
				try
				{
					Thread.sleep(refreshRate);
				}
				catch(Exception e)
				{
					log.debug("Interrupted from sleep", e);
				}
			}
		}
	}
}
