/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.jrmp13.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBMetaData;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;

import org.jboss.ejb.Container;
import org.jboss.ejb.ContainerInvoker;
import org.jboss.ejb.ContainerInvokerContainer;
import org.jboss.metadata.EntityMetaData;
import org.jboss.metadata.SessionMetaData;

import org.jboss.ejb.plugins.jrmp13.interfaces.HomeProxy;
import org.jboss.ejb.plugins.jrmp13.interfaces.StatelessSessionProxy;
import org.jboss.ejb.plugins.jrmp13.interfaces.StatefulSessionProxy;
import org.jboss.ejb.plugins.jrmp13.interfaces.EntityProxy;
import org.jboss.logging.Logger;

/**
 *	<description> 
 *      
 *	@see <related>
 *	@author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 *  @author <a href="mailto:marc.fleury@telkel.com">Marc Fleury</a>
 *	@version $Revision: 1.12 $
 */
public final class JRMPContainerInvoker
   implements ContainerInvoker
{
   EJBHome home;
   EJBObject statelessObject;

   Container container;
   org.jboss.ejb.plugins.jrmp.server.JRMPContainerInvoker ci; // Parent invoker
   
   public JRMPContainerInvoker(org.jboss.ejb.plugins.jrmp.server.JRMPContainerInvoker ci)
   {
      this.ci = ci;
   }
   
   public void setContainer(Container con)
   {
      this.container = con;
   }
   
   public void init()
   {
      // Create EJBHome object
      // We add the Handle methods to the Home
      Class handleClass;
      try 
      { 
         handleClass = Class.forName("javax.ejb.Handle");
      } catch (Exception e)
      {
         Logger.exception(e);handleClass = null;
      }

      this.home = (EJBHome)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getHomeClass().getClassLoader(),
         new Class[] { ((ContainerInvokerContainer)container).getHomeClass(), handleClass },
         new HomeProxy(ci.getJndiName(), ci.getEJBMetaData(), ci, ci.isOptimized()));

      // Create stateless session object
      // Same instance is used for all objects      
      if (!(container.getBeanMetaData() instanceof EntityMetaData) &&
          ((SessionMetaData)container.getBeanMetaData()).isStateless())
      {
         this.statelessObject = (EJBObject)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
            new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() } ,
            new StatelessSessionProxy(ci.getJndiName(), ci, ci.isOptimized()));
      }
      
   		     Logger.debug("JRMP 1.3 CI initialized");
   }

   public void start()
   {
   }

   public void stop()
   {
   }

   public void destroy()
   {
   }

   public EJBMetaData getEJBMetaData()
   {
      // Ignore, never called
      return null;
   }
   
   public EJBHome getEJBHome()
   {
      return home;
   }

   public EJBObject getStatelessSessionEJBObject()
   {
      return statelessObject;
   }

   public EJBObject getStatefulSessionEJBObject(Object id)
   {
      return (EJBObject)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
                  new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
                  new StatefulSessionProxy(ci.getJndiName(), ci, id, ci.isOptimized()));
   }

   public EJBObject getEntityEJBObject(Object id)
   {
      return (EJBObject)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
                  new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
                  new EntityProxy(ci.getJndiName(), ci, id, ci.isOptimized()));
   }

   public Collection getEntityCollection(Collection ids)
   {                                                          
      ArrayList list = new ArrayList(ids.size());
      Iterator idEnum = ids.iterator();
      while(idEnum.hasNext())
      {
         list.add(Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
                  new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
                  new EntityProxy(ci.getJndiName(), ci, idEnum.next(), ci.isOptimized())));
      }
      return list;
   }
}


/**
 *	<description> 
 *      
 *	@see <related>
 *	@author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 *  @author <a href="mailto:marc.fleury@telkel.com">Marc Fleury</a>
 *	@version $Revision: 1.12 $
 */
 
 /*
public final class JRMPContainerInvoker
   implements ContainerInvoker
{
    EJBHome home;
    EJBObject statelessObject;
 
    public void init()
    {
       // Create EJBHome object
       // We add the Handle methods to the Home
       Class handleClass;
       try 
       { 
         handleClass = Class.forName("javax.ejb.Handle");
       } catch (Exception e) 
       {
         Logger.exception(e);handleClass = null;
       }
       
       this.home = (EJBHome)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getHomeClass().getClassLoader(),
           new Class[] { ((ContainerInvokerContainer)container).getHomeClass(), handleClass },
           new HomeProxy(jndiName,ejbMetaData, this, optimize));
 
       // Create stateless session object
       // Same instance is used for all objects      
       if (!(container.getBeanMetaData() instanceof EntityMetaData) &&
           ((SessionMetaData)container.getBeanMetaData()).isStateless())
       {
          this.statelessObject = (EJBObject)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
               new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
               new StatelessSessionProxy(jndiName, this, optimize));
       }
       
    }
 
    public void start()
    {
    }
 
    public void stop()
    {
    }
 
    public void destroy()
    {
    }
    
    public EJBMetaData getEJBMetaData()
    {
       // Ignore, never called
       return null;
    }
    
    public EJBHome getEJBHome()
    {
        return home;
    }
    
    public EJBObject getStatelessSessionEJBObject()
    {
        return statelessObject;
    }

   public EJBObject getStatefulSessionEJBObject(Object id)
   {
      return (EJBObject)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
                                           new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
                                           new StatefulSessionProxy(jndiName, this, id, optimize));
   }

   public EJBObject getEntityEJBObject(Object id)
   {
      return (EJBObject)Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
                                           new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
                                           new EntityProxy(jndiName, this, id, optimize));
   }

   public Collection getEntityCollection(Collection ids)
   {
      ArrayList list = new ArrayList(ids.size());
      Iterator idEnum = ids.iterator();
      while(idEnum.hasNext())
      {
         list.add(Proxy.newProxyInstance(((ContainerInvokerContainer)container).getRemoteClass().getClassLoader(),
                                           new Class[] { ((ContainerInvokerContainer)container).getRemoteClass() },
                                           new EntityProxy(jndiName, this,idEnum.next(), optimize)));
      }
      return list;
   }
}

*/
