/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins;

import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.EJBObject;
import javax.ejb.EJBMetaData;
import javax.ejb.EJBException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.transaction.TransactionRolledbackException;

import org.apache.log4j.NDC;

import org.jboss.ejb.Container;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.Interceptor;
import org.jboss.invocation.Invocation;
import org.jboss.metadata.BeanMetaData;
import org.jboss.system.Registry;
import org.jboss.naming.ENCThreadLocalKey;
/** 
 *   This interceptor injects the ProxyFactory into the ThreadLocal container variable
 *   @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 *   @author <a href="mailto:Scott.Stark@jboss.org">Scott Stark</a>
 *   @version $Revision: 1.1 $
 */
public class ProxyFactoryFinderInterceptor
   extends AbstractInterceptor
{
   // Static --------------------------------------------------------
   
   // Attributes ----------------------------------------------------
   protected Container container;
   
   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   public void setContainer(Container container)
   {
      this.container = container;
   }
   
   public Container getContainer()
   {
      return container;
   }
   
   // Container implementation --------------------------------------
   public void create()
      throws Exception
   {
   }
   

   protected void setProxyFactory(String invokerBinding, Invocation mi) throws Exception
   {
      //      if (BeanMetaData.LOCAL_INVOKER_PROXY_BINDING.equals(invokerBinding)) return;
      if (invokerBinding == null)
      {
         System.out.println("********* invokerBInding is null in ProxyFactoryFinder");
         return;
      }
      /*
      if (invokerBinding == null)
      {
         log.error("***************** invokerBinding is null ********");
         log.error("Method name: " + mi.getMethod().getName());
         log.error("jmx name: " + container.getJmxName().toString());
         new Throwable().printStackTrace();
         log.error("*************************");
         throw new ServerException("Couldn't insert proxy factory, invokerBinding was null");
      }
      */
      Object proxyFactory = container.lookupProxyFactory(invokerBinding);
      if (proxyFactory == null)
      {
         log.error("***************** proxyFactory is null ********");
         log.error("Method name: " + mi.getMethod().getName());
         log.error("jmx name: " + container.getJmxName().toString());
         log.error("invokerBinding: " + invokerBinding);
         new Throwable().printStackTrace();
         log.error("*************************");
         throw new ServerException("Couldn't find proxy factory");
      }
      container.setProxyFactory(proxyFactory);
   }

   public Object invokeHome(Invocation mi)
      throws Exception
   {
      String invokerBinding = (String)mi.getValue(org.jboss.invocation.InvocationContext.INVOKER_PROXY_BINDING);
      setProxyFactory(invokerBinding, mi);

      String oldInvokerBinding = ENCThreadLocalKey.getKey();
      // Only override current ENC binding if we're not local
      //      if ((!BeanMetaData.LOCAL_INVOKER_PROXY_BINDING.equals(invokerBinding)) || oldInvokerBinding == null)
      if (invokerBinding != null || oldInvokerBinding == null)
      {
         ENCThreadLocalKey.setKey(invokerBinding);
      }

      Interceptor next = getNext();
      Object value = null;
      try
      {
         value = next.invokeHome(mi);
      }
      finally
      {
         ENCThreadLocalKey.setKey(oldInvokerBinding);
      }

      return value;
   }

   public Object invoke(Invocation mi)
      throws Exception
   {
      String invokerBinding = (String)mi.getValue(org.jboss.invocation.InvocationContext.INVOKER_PROXY_BINDING);
      setProxyFactory(invokerBinding, mi);

      String oldInvokerBinding = ENCThreadLocalKey.getKey();
      // Only override current ENC binding if we're not local or there has not been a previous call
      //      if ((!BeanMetaData.LOCAL_INVOKER_PROXY_BINDING.equals(invokerBinding)) || oldInvokerBinding == null)
      if (invokerBinding != null || oldInvokerBinding == null)
      {
         ENCThreadLocalKey.setKey(invokerBinding);
      }

      Interceptor next = getNext();
      Object value = null;
      try
      {
         value = next.invoke(mi);
      }
      finally
      {
         ENCThreadLocalKey.setKey(oldInvokerBinding);
      }

      return value;
   }

}
