/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.jrmp.server;

import java.awt.Component;
import java.beans.beancontext.BeanContextChildComponentProxy;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.ServerException;
import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Properties;

import javax.ejb.EJBMetaData;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.Name;

import org.jboss.ejb.deployment.Entity;
import org.jboss.ejb.deployment.Session;
import org.jboss.ejb.deployment.ContainerConfiguration;
import org.jboss.ejb.deployment.JRMPContainerInvokerConfiguration;

import org.jboss.ejb.Container;
import org.jboss.ejb.Interceptor;
import org.jboss.ejb.ContainerInvoker;
import org.jboss.ejb.plugins.jrmp.interfaces.HomeProxy;
import org.jboss.ejb.plugins.jrmp.interfaces.StatelessSessionProxy;
import org.jboss.ejb.plugins.jrmp.interfaces.StatefulSessionProxy;
import org.jboss.ejb.plugins.jrmp.interfaces.EntityProxy;
import org.jboss.ejb.plugins.jrmp.interfaces.EntityProxy;
import org.jboss.ejb.plugins.jrmp.interfaces.ContainerRemote;
import org.jboss.ejb.plugins.jrmp.interfaces.MethodInvocation;
import org.jboss.ejb.plugins.jrmp.interfaces.IteratorImpl;
import org.jboss.ejb.plugins.jrmp.interfaces.EJBMetaDataImpl;
import org.jboss.ejb.plugins.jrmp.interfaces.SecureSocketFactory;

/**
 *	<description> 
 *      
 *	@see <related>
 *	@author Rickard �berg (rickard.oberg@telkel.com)
 *	@version $Revision: 1.1 $
 */
public final class JRMPContainerInvoker
   extends RemoteServer
   implements ContainerRemote, ContainerInvoker
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   boolean optimize = false;
   Container con;
   String jndiName;
   EJBMetaDataImpl ejbMetaData;
   EJBHome home;
   
   static Object semaphore = new Object();
   static int lock = 5;
   
   // Static --------------------------------------------------------
   static HashMap invokers = new HashMap(); // Prevent DGC
   public static ContainerRemote getLocal(String jndiName) { return (ContainerRemote)invokers.get(jndiName); }
   
   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   public void setOptimized(boolean optimize)
   {
      this.optimize = optimize;
   }
   
   public boolean isOptimized()
   {
      return optimize;
   }
   
   public EJBMetaData getEJBMetaData()
   {
      return ejbMetaData;
   }
   
   public EJBHome getEJBHome()
   {
      if (home == null)
      {
         this.home = (EJBHome)Proxy.newProxyInstance(con.getHomeClass().getClassLoader(),
                                              new Class[] { con.getHomeClass() },
                                              new HomeProxy(jndiName, this, optimize));
      }
      return home;
   }
   
   public EJBObject getStatelessSessionEJBObject()
   {
      return (EJBObject)Proxy.newProxyInstance(con.getRemoteClass().getClassLoader(),
                                        new Class[] { con.getRemoteClass() },
                                        new StatelessSessionProxy(jndiName, this, optimize));
   }

   public EJBObject getStatefulSessionEJBObject(Object id)
   {
      return (EJBObject)Proxy.newProxyInstance(con.getRemoteClass().getClassLoader(),
                                           new Class[] { con.getRemoteClass() },
                                           new StatefulSessionProxy(jndiName, this, id, optimize));
   }

   public EJBObject getEntityEJBObject(Object id)
   {
      return (EJBObject)Proxy.newProxyInstance(con.getRemoteClass().getClassLoader(),
                                           new Class[] { con.getRemoteClass() },
                                           new EntityProxy(jndiName, this, id, optimize));
   }

   public Collection getEntityCollection(Collection ids)
   {
      ArrayList list = new ArrayList(ids.size());
      Iterator idEnum = ids.iterator();
      while(idEnum.hasNext())
      {
         list.add(Proxy.newProxyInstance(con.getRemoteClass().getClassLoader(),
                                           new Class[] { con.getRemoteClass() },
                                           new EntityProxy(jndiName, this, idEnum.next(), optimize)));
      }
      return list;
   }
   
   // ContainerRemote implementation --------------------------------
   public Object invokeHome(MarshalledObject mimo, Object tx, Principal user)
      throws Exception
   {
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(con.getClassLoader());
      
      MethodInvocation mi = (MethodInvocation)mimo.get();
      
      try
      {
         return con.invokeHome(mi.getMethod(), mi.getArguments());
//         return new MarshalledObject(con.invoke(mi.getId(), mi.getMethod(), mi.getArguments()));
      } finally
      {
         Thread.currentThread().setContextClassLoader(oldCl);
      }
   }
      
   public Object invoke(MarshalledObject mimo, Object tx, Principal user)
      throws Exception
   {
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(con.getClassLoader());
      try
      {
         
         MethodInvocation mi = (MethodInvocation)mimo.get();
      
         return con.invoke(mi.getId(), mi.getMethod(), mi.getArguments());
//         return new MarshalledObject(con.invoke(mi.getId(), mi.getMethod(), mi.getArguments()));
      } catch (Throwable e)
      {
         e.printStackTrace();
         throw (Exception)e;
      } finally
      {
         Thread.currentThread().setContextClassLoader(oldCl);
      }
   }
    
   public Object invokeHome(Method m, Object[] args, Object tx, Principal user)
      throws Exception
   {
      begin();
      
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(con.getClassLoader());
      
      try
      {
         return con.invokeHome(m, args);
      } finally
      {
         Thread.currentThread().setContextClassLoader(oldCl);
         finished();
      }
   }
      
   public Object invoke(Object id, Method m, Object[] args, Object tx, Principal user)
      throws Exception
   {
      begin();
      
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(con.getClassLoader());
      
      try
      {
         return con.invoke(id, m, args);
      } finally
      {
         finished();
         Thread.currentThread().setContextClassLoader(oldCl);
      }
      
   }
   
   // ContainerService implementation -------------------------------
   public void setContainer(Container con)
   {
      this.con = con;
      jndiName = con.getMetaData().getJndiName();
   }
   
   public void init()
      throws Exception
   {
      ContainerConfiguration conConf = con.getMetaData().getContainerConfiguration();
      if (conConf != null)
      {
         JRMPContainerInvokerConfiguration conf = (JRMPContainerInvokerConfiguration)conConf.getContainerInvokerConfiguration();
         optimize = conf.isOptimized();
      }
      
      // Create metadata
      if (con.getMetaData() instanceof Entity)
      {
         ejbMetaData = new EJBMetaDataImpl(con.getRemoteClass(), con.getHomeClass(), con.getClassLoader().loadClass(((Entity)con.getMetaData()).getPrimaryKeyClass()), false, false, getEJBHome());
      }
      else
      {
         if (((Session)con.getMetaData()).getSessionType().equals("Stateless"))
            ejbMetaData = new EJBMetaDataImpl(con.getRemoteClass(), con.getHomeClass(), null, true, false, getEJBHome());
         else
            ejbMetaData = new EJBMetaDataImpl(con.getRemoteClass(), con.getHomeClass(), null, true, true, getEJBHome());
      }      

   }
   
   public void start()
      throws Exception
   {
      try
      {
/*         UnicastRemoteObject.exportObject(this, 
                                          4444, 
                                          new SecureSocketFactory(), 
                                          new SecureSocketFactory());
*/         
         UnicastRemoteObject.exportObject(this);
         invokers.put(con.getMetaData().getJndiName(), this);
         
      } catch (IOException e)
      {
         throw new ServerException("Could not create secure socket factory", e);
      }
   }
   
   public void stop()
   {
      invokers.remove(this);
   }

   public void destroy()
   {
   }
   
   // Package protected ---------------------------------------------
    
   // Protected -----------------------------------------------------
    
   // Private -------------------------------------------------------
   private void begin()
   {
/*      synchronized (semaphore)
      {
         while (lock == 0)
            try { semaphore.wait(); } catch (InterruptedException e) {}
            
         lock--;
      }
*/      
   }

   private void finished()
   {
/*      
      synchronized (semaphore)
      {
         lock++;
         semaphore.notify();
      }
*/      
   }

   // Inner classes -------------------------------------------------
}
