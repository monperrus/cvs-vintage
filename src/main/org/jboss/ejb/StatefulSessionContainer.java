/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collection;
import java.util.ArrayList;

import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.EJBObject;
import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import org.jboss.logging.Logger;

/**
 *   <description> 
 *      
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.17 $
 */
public class StatefulSessionContainer
   extends Container
    implements ContainerInvokerContainer, InstancePoolContainer
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   // These are the mappings between the home interface methods and the container methods
   protected Map homeMapping;
   
   // These are the mappings between the remote interface methods and the bean methods
   protected Map beanMapping;
   
   // This is the container invoker for this container
   protected ContainerInvoker containerInvoker;
   
   // This is the first interceptor in the chain. The last interceptor must be provided by the container itself
   protected Interceptor interceptor;
    
   // This is the instancepool that is to be used
   protected InstancePool instancePool;
    
   // This is the persistence manager for this container
   protected StatefulSessionPersistenceManager persistenceManager;
    
   protected InstanceCache instanceCache;
   
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   public void setContainerInvoker(ContainerInvoker ci) 
   { 
      if (ci == null) 
        throw new IllegalArgumentException("Null invoker");
        
      this.containerInvoker = ci; 
      ci.setContainer(this);
   }

   public ContainerInvoker getContainerInvoker() 
   { 
    return containerInvoker; 
   }
    
   public void setInstanceCache(InstanceCache ic)
   { 
      this.instanceCache = ic; 
      ic.setContainer(this);
   }
   
   public InstanceCache getInstanceCache() 
   { 
      return instanceCache; 
   }
   
   public void setInstancePool(InstancePool ip) 
   { 
      if (ip == null)
        throw new IllegalArgumentException("Null pool");
        
      this.instancePool = ip; 
      ip.setContainer(this);
   }

   public InstancePool getInstancePool() 
   { 
    return instancePool; 
   }
    
   public StatefulSessionPersistenceManager getPersistenceManager() 
   { 
      return persistenceManager; 
   }
   
   public void setPersistenceManager(StatefulSessionPersistenceManager pm) 
   { 
      persistenceManager = pm; 
      pm.setContainer(this);
   }
   
   public void addInterceptor(Interceptor in) 
   { 
      if (interceptor == null)
      {
         interceptor = in;
      } else
      {
         
         Interceptor current = interceptor;
         while ( current.getNext() != null)
         {
            current = current.getNext();
         }
            
         current.setNext(in);
      }
   }
   
   public Interceptor getInterceptor() 
   { 
    return interceptor; 
   }
    
   public Class getHomeClass()
   {
      return homeInterface;
   }
   
   public Class getRemoteClass()
   {
      return remoteInterface;
   }
    
   // Container implementation --------------------------------------
   public void init()
      throws Exception
   {
      // Associate thread with classloader
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(getClassLoader());
      
      // Acquire classes from CL
      homeInterface = classLoader.loadClass(metaData.getHome());
      remoteInterface = classLoader.loadClass(metaData.getRemote());
          
      // Call default init
     super.init();
      
      // Map the bean methods
      setupBeanMapping();
      
      // Map the home methods
     setupHomeMapping();
      
     // Init container invoker
     containerInvoker.init();
      
      // Init instance cache
     instanceCache.init();
        
     // Initialize pool 
     instancePool.init();
      
     // Init persistence
     persistenceManager.init();
    
     // Initialize the interceptor by calling the chain
     Interceptor in = interceptor;
     while (in != null)
     {
        in.setContainer(this);
        in.init();
        in = in.getNext();
     }
      
     // Reset classloader  
     Thread.currentThread().setContextClassLoader(oldCl);
   }
   
   public void start()
      throws Exception
   {
      // Associate thread with classloader
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(getClassLoader());
      
      // Call default start
      super.start();
      
      // Start container invoker
      containerInvoker.start();
      
      // Start instance cache
      instanceCache.start();
      
      // Start pool 
      instancePool.start();
        
      // Start persistence
      persistenceManager.start();
      
        // Start all interceptors in the chain      
        Interceptor in = interceptor;
        while (in != null)
        {
           in.start();
           in = in.getNext();
        }
        
        // Reset classloader
      Thread.currentThread().setContextClassLoader(oldCl);
      
   }
   
    public void stop() 
    {
        // Associate thread with classloader
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());

        // Call default stop
        super.stop();

        // Stop container invoker
        containerInvoker.stop();

        // Stop instance cache
        instanceCache.stop();

        // Stop pool 
        instancePool.stop();
        
        // Stop persistence
        persistenceManager.stop();

        // Stop the instance pool
        instancePool.stop();

        // Stop all interceptors in the chain       
        Interceptor in = interceptor;
        while (in != null)
        {
            in.stop();
            in = in.getNext();
        }

        // Reset classloader
        Thread.currentThread().setContextClassLoader(oldCl);
   }
   
    public void destroy()
   {
       // Associate thread with classloader
       ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
       Thread.currentThread().setContextClassLoader(getClassLoader());
       
       // Call default destroy
       super.destroy();
       
       // Destroy container invoker
       containerInvoker.destroy();
       
       // Destroy instance cache
       instanceCache.destroy();
       
       // Destroy pool 
       instancePool.destroy();
        
       // Destroy persistence
       persistenceManager.destroy();
       
       // Destroy all the interceptors in the chain     
       Interceptor in = interceptor;
       while (in != null)
       {
          in.destroy();
          in = in.getNext();
       }
        
       // Reset classloader
       Thread.currentThread().setContextClassLoader(oldCl);
   }
   
   public Object invokeHome(MethodInvocation mi)
      throws Exception
   {
       return getInterceptor().invokeHome(mi);
   }

   /**
    *   This method retrieves the instance from an object table, and invokes the method
    *   on the particular instance through the chain of interceptors
    *
    * @param   mi  
    * @return     
    * @exception   Exception  
    */
   public Object invoke(MethodInvocation mi)
      throws Exception
   {
      // Invoke through interceptors
      return getInterceptor().invoke(mi);
   }
   
   // EJBObject implementation --------------------------------------
   public void remove(MethodInvocation mi)
      throws java.rmi.RemoteException, RemoveException
   {
      // Remove from storage 
      getPersistenceManager().removeSession((StatefulSessionEnterpriseContext)mi.getEnterpriseContext());
      
      // We signify "removed" with a null id
      mi.getEnterpriseContext().setId(null);
   }
   
   /**
   *  MF FIXME these are implemented on the client
   */
  
   public Handle getHandle(MethodInvocation mi)
      throws java.rmi.RemoteException
   {
      // TODO
      return null;
   }

   public Object getPrimaryKey(MethodInvocation mi)
      throws java.rmi.RemoteException
   {
      // TODO
      return null;
   }
   
   public EJBHome getEJBHome(MethodInvocation mi)
      throws java.rmi.RemoteException
   {
       
      return containerInvoker.getEJBHome();
   }
   
   public boolean isIdentical(MethodInvocation mi)
      throws java.rmi.RemoteException
   {
      return false; // TODO
   }
   
   // Home interface implementation ---------------------------------
   public EJBObject createHome(MethodInvocation mi)
      throws java.rmi.RemoteException, CreateException
   {
      getPersistenceManager().createSession(mi.getMethod(), mi.getArguments(), (StatefulSessionEnterpriseContext)mi.getEnterpriseContext());
     return ((StatefulSessionEnterpriseContext)mi.getEnterpriseContext()).getEJBObject();
   }
   
     /**
    * A method for the getEJBObject from the handle
    * 
    */
    public EJBObject getEJBObject(MethodInvocation mi) 
        throws java.rmi.RemoteException  {
          
        // All we need is an EJBObject for this Id, the first argument is the Id
        return containerInvoker.getStatefulSessionEJBObject(mi.getArguments()[0]);        
    
    }
        

   // EJBHome implementation ----------------------------------------
   /**
   *  These are implemented in the local proxy
   */
   
   public void removeHome(MethodInvocation mi)
    throws java.rmi.RemoteException, RemoveException
   {
       throw new Error("Not Yet Implemented");
    }
   
   public EJBMetaData getEJBMetaDataHome(MethodInvocation mi)
    throws java.rmi.RemoteException
   {
       return getContainerInvoker().getEJBMetaData();
   }
   
   public HomeHandle getHomeHandleHome(MethodInvocation mi)
       throws java.rmi.RemoteException   
   {
       throw new Error("Not Yet Implemented");
   }
   
      
   // Private -------------------------------------------------------
    protected void setupHomeMapping()
    throws NoSuchMethodException
    {
        Map map = new HashMap();
        
        Method[] m = homeInterface.getMethods();
        for (int i = 0; i < m.length; i++)
        {
            try
            {
                // Implemented by container
                map.put(m[i], getClass().getMethod(m[i].getName()+"Home", new Class[] { MethodInvocation.class }));
            } catch (NoSuchMethodException e)
            {
                Logger.log(m[i].getName() + " in bean has not been mapped");
            }
        }
        
        try {
            
            // Get getEJBObject from on Handle, first get the class
            Class handleClass = Class.forName("javax.ejb.Handle");
            
            //Get only the one called handle.getEJBObject 
            Method getEJBObjectMethod = handleClass.getMethod("getEJBObject", new Class[0]);
                        
            //Map it in the home stuff
            map.put(getEJBObjectMethod, getClass().getMethod("getEJBObject", new Class[] {MethodInvocation.class}));
        }
        catch (NoSuchMethodException e) {
                    
            Logger.debug("Couldn't find getEJBObject method on container");
        }
        catch (Exception e) { Logger.exception(e);}
        
        homeMapping = map;
    }

   protected void setupBeanMapping()
      throws NoSuchMethodException
   {
      Map map = new HashMap();
      
      Method[] m = remoteInterface.getMethods();
      for (int i = 0; i < m.length; i++)
      {
         try
         {
            if (!m[i].getDeclaringClass().getName().equals("javax.ejb.EJBObject"))
            {
                // Implemented by bean
                map.put(m[i], beanClass.getMethod(m[i].getName(), m[i].getParameterTypes()));
            }
            else
            {
               // Implemented by container
               map.put(m[i], getClass().getMethod(m[i].getName(), new Class[] { MethodInvocation.class }));
            
            }
         } catch (NoSuchMethodException e)
         {
            Logger.error(m[i].getName() + " in bean has not been mapped");
         }
      }
      
      beanMapping = map;
   }
   
   
   
   protected Interceptor createContainerInterceptor()
   {
      return new ContainerInterceptor();
   }
   
   // This is the last step before invocation - all interceptors are done
   class ContainerInterceptor
      implements Interceptor
   {
      public void setContainer(Container con) {}
      
      public void setNext(Interceptor interceptor) {}
      public Interceptor getNext() { return null; }
      
      public void init() {}
      public void start() {}
      public void stop() {}
      public void destroy() {}
      
      public Object invokeHome(MethodInvocation mi)
         throws Exception
      {
         Method m = (Method)homeMapping.get(mi.getMethod());
         // Invoke and handle exceptions
         
         Logger.debug("SSC:invokeHome:mi is "+mi.getMethod().getName()+" map is "+m.getName());
         try
         {          
            return m.invoke(StatefulSessionContainer.this, new Object[] { mi });
         } catch (InvocationTargetException e)
         {
             Logger.debug(e.getMessage());
            Throwable ex = e.getTargetException();
            if (ex instanceof Exception)
               throw (Exception)ex;
            else
               throw (Error)ex;
         }
      }
         
      public Object invoke(MethodInvocation mi)
         throws Exception
      {
         // Get method
         Method m = (Method)beanMapping.get(mi.getMethod());
         
         Logger.debug("SSC:invoke:mi is "+mi.getMethod().getName()+" map is "+m.getName());
         // Select instance to invoke (container or bean)
         if (m.getDeclaringClass().equals(StatefulSessionContainer.this.getClass()))
         {
			 //wire the transaction on the context, this is how the instance remember the tx
              if (mi.getEnterpriseContext().getTransaction() == null) mi.getEnterpriseContext().setTransaction(mi.getTransaction());
                
            // Invoke and handle exceptions
            try
            {
               return m.invoke(StatefulSessionContainer.this, new Object[] { mi });
            } catch (InvocationTargetException e)
            {
               Throwable ex = e.getTargetException();
               if (ex instanceof Exception)
                  throw (Exception)ex;
               else
                  throw (Error)ex;
            } 
         } else
         {
            // Invoke and handle exceptions
            try
            {
               return m.invoke(mi.getEnterpriseContext().getInstance(), mi.getArguments());
            } catch (InvocationTargetException e)
            {
               Throwable ex = e.getTargetException();
               if (ex instanceof Exception)
                  throw (Exception)ex;
               else
                  throw (Error)ex;
            } 
         }
      }
   }
}

