/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.ejb.EJBException;

import org.jboss.ejb.Container;
import org.jboss.ejb.BeanLock;
import org.jboss.ejb.BeanLockManager;
import org.jboss.ejb.EntityContainer;
import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.InstanceCache;
import org.jboss.ejb.InstancePool;
import org.jboss.invocation.Invocation;

/**
 * The instance interceptors role is to acquire a context representing
 * the target object from the cache.
 *
 * <p>This particular container interceptor implements pessimistic locking
 * on the transaction that is associated with the retrieved instance.  If
 * there is a transaction associated with the target component and it is
 * different from the transaction associated with the Invocation
 * coming in then the policy is to wait for transactional commit. 
 *   
 * <p>We also implement serialization of calls in here (this is a spec
 * requirement). This is a fine grained notify, notifyAll mechanism. We
 * notify on ctx serialization locks and notifyAll on global transactional
 * locks.
 *   
 * <p><b>WARNING: critical code</b>, get approval from senior developers
 * before changing.
 *    
 * @author <a href="mailto:marc.fleury@jboss.org">Marc Fleury</a>
 * @author <a href="mailto:Scott.Stark@jboss.org">Scott Stark</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 * @version $Revision: 1.58 $
 */
public class EntityInstanceInterceptor extends AbstractInterceptor
{
   public Object invoke(Invocation invocation) throws Exception
   {
      EntityContainer container = (EntityContainer)getContainer();
      Object key = null;
      EntityEnterpriseContext ctx = null;
      boolean trace = log.isTraceEnabled();

      // Get the context from the pool or cache
      if(invocation.getType().isHome()) 
      {
         if(trace)
         {
            log.trace("Begin home invoke");
         }
         ctx = (EntityEnterpriseContext)container.getInstancePool().get();
      }
      else
      {
         key = invocation.getId();
         if(trace)
         {
            log.trace("Begin invoke, key=" + key);
         }
         ctx = (EntityEnterpriseContext) container.getInstanceCache().get(key);
      }

      if(ctx == null)
      {
         throw new EJBException("The instance cache returned a null context");
      }

      // Pass it to the method invocation
      invocation.setEnterpriseContext(ctx);

      // Set the current security information
      ctx.setPrincipal(invocation.getPrincipal());

      // Associate transaction, in the new design the lock already has the 
      // transaction from the previous interceptor.
      // Don't set the transction if a read-only method.  With a read-only 
      // method, the ctx can be shared between multiple transactions.
      if(!isReadOnlyInvocation(invocation)) 
      {
         ctx.setTransaction(invocation.getTransaction());
      }

      Throwable exceptionThrown = null;
      try
      {
         return getNext().invoke(invocation);
      }
      catch(RemoteException e)
      {
         exceptionThrown = e;
         throw e;
      }
      catch(RuntimeException e)
      {
         exceptionThrown = e;
         throw e;
      }
      catch(Error e)
      {
         exceptionThrown = e;
         throw e;
      }
      finally
      {
         if(invocation.getType().isHome())
         {
            // when a home invocation comes back with an id we need to insert
            // into the cache
            if(ctx.getId() != null)
            {
               // lock all access to BeanLock while we stick it in the cache
               BeanLock lock = 
                  container.getLockManager().getLock(ctx.getCacheKey());
               lock.sync(); 
               try
               {
                  // Check there isn't a context already in the cache
                  // e.g. commit-option B where the entity was
                  // created then removed externally
                  InstanceCache cache = container.getInstanceCache();
                  cache.remove(ctx.getCacheKey());

                  // marcf: possible race on creation and usage
                  // insert instance in cache,
                  cache.insert(ctx);
               }
               finally
               {
                  // assure to unlock the bean
                  lock.releaseSync();
               }

               if(trace)
               {
                  log.trace("End home invocation, key=" + ctx.getId() + 
                        ", ctx=" + ctx);
               }
            }
            else
            {
               {
                  log.trace("End home invocation, ctx=" + ctx);
               }
            }
         }
         // If an exception has been thrown, and the ctx has been registered 
         // in a transaction InstanceSynchronization, the synchronization will
         // remove the context, so we shouldn't.  Otherwise, we need to 
         // remove the context from the cache here.
         else if(exceptionThrown != null && !ctx.hasTxSynchronization())
         {
            // Discard instance [EJB 1.1 spec 12.3.1]
            container.getInstanceCache().remove(key);

            if(trace)
            {
               log.trace("Ending invoke, exceptionThrown, ctx=" + ctx, exceptionThrown);
            }
         }
         // The current code signals that an instance should be deleted by 
         // setting the context id to null.  If it is null, we need to remove
         // the context from the cache here.
         else if(ctx.getId() == null)
         {
            // The key from the Invocation still identifies the 
            // right cachekey
            container.getInstanceCache().remove(key);

            if(trace)
            {
               log.trace("Ending invoke, cache removal, ctx=" + ctx);
            }
            // no more pool return
         }
         // All is good.  Just log it.
         else
         {
            if(trace)
            {
               log.trace("End invoke, key=" + key + ", ctx=" + ctx);
            }
         }
      }
   }

   private boolean isReadOnlyInvocation(Invocation invocation)
   {
      EntityContainer container = (EntityContainer)getContainer();

      if(invocation.getType().isHome() || !container.isReadOnly()) 
      {
         return false;
      }

      Method method = invocation.getMethod();
      return method == null ||
         !container.getBeanMetaData().isMethodReadOnly(method.getName());
   }
}
