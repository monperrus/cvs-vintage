/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.naming;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.jnp.server.Main;

import org.jboss.management.j2ee.JNDIResource;
import org.jboss.system.ServiceMBeanSupport;

/**
 * A JBoss service that starts the jnp JNDI server.
 *
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:Scott.Stark@jboss.org">Scott Stark</a>.
 * @author <a href="mailto:andreas@jboss.org">Andreas Schaefer</a>.
 * @version $Revision: 1.31 $
 *
 * @jmx:mbean name="jboss:service=Naming"
 *            extends="org.jboss.system.ServiceMBean, org.jnp.server.MainMBean"
 *            
 * <p><b>Revisions:</b>
 *
 * <p><b>20010622 scott.stark:</b>
 * <ul>
 * <li> Report IntialContext env for problem tracing
 * </ul>
 * <p><b>20011202 Andreas Schaefer:</b>
 * <ul>
 * <li> Added JSR-77 representation, see {@liink #postRegister postRegister()}
 *      and {@link #postDeregister postDeregister()}.
 * </ul>
 **/
public class NamingService
   extends ServiceMBeanSupport
   implements NamingServiceMBean
{
   Main naming;
   
   /** Object Name of the JSR-77 representant of this servie **/
   ObjectName mJNDI;
   
   public NamingService()
   {
      naming = new Main(this.getClass().getName());
   }

   public void setPort(int port)
   {
      naming.setPort(port);
   }

   public int getPort()
   {
      return naming.getPort();
   }
   
   public void setRmiPort(int port)
   {
      naming.setRmiPort(port);
   }
   
   public int getRmiPort()
   {
      return naming.getRmiPort();
   }

   public String getBindAddress()
   {
      return naming.getBindAddress();
   }
   
   public void setBindAddress(String host) throws UnknownHostException
   {
      naming.setBindAddress(host);
   }

   public int getBacklog()
   {
      return naming.getBacklog();
   }
   
   public void setBacklog(int backlog)
   {
      naming.setBacklog(backlog);
   }

   public String getClientSocketFactory()
   {
      return naming.getClientSocketFactory();
   }
   
   public void setClientSocketFactory(String factoryClassName)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      naming.setClientSocketFactory(factoryClassName);
   }

   public String getServerSocketFactory()
   {
      return naming.getServerSocketFactory();
   }
   
   public void setServerSocketFactory(String factoryClassName)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      naming.setServerSocketFactory(factoryClassName);
   }

   public void setJNPServerSocketFactory(String factoryClassName)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      naming.setJNPServerSocketFactory(factoryClassName);
   }

   protected ObjectName getObjectName(MBeanServer server, ObjectName name)
      throws javax.management.MalformedObjectNameException
   {
      return name == null ? OBJECT_NAME : name;
   }
   
   protected void startService()
      throws Exception
   {
      boolean debug = log.isDebugEnabled();
      
      // Read jndi.properties into system properties
      // RO: this is necessary because some components (=Tomcat servlets) use a 
      // buggy classloader that disallows finding the resource properly
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      InputStream is = loader.getResourceAsStream("jndi.properties");
      Properties props = new Properties();
      props.load(is);

      for (Enumeration keys = props.propertyNames(); keys.hasMoreElements(); )
      {
         String key = (String) keys.nextElement();
         String value = props.getProperty(key);
         if (debug) {
            log.debug("System.setProperty, key="+key+", value="+value);
         }
         System.setProperty(key, value);
      }
      naming.start();
      
      /* Create a default InitialContext and dump out its env to show what properties
         were used in its creation. If we find a Context.PROVIDER_URL property
         issue a warning as this means JNDI lookups are going through RMI.
      */
      InitialContext iniCtx = new InitialContext();
      Hashtable env = iniCtx.getEnvironment();
      if (debug)
         log.debug("InitialContext Environment:");
      String providerURL = null;
      for (Enumeration keys = env.keys(); keys.hasMoreElements(); )
      {
         String key = (String) keys.nextElement();
         String value = (String) env.get(key);
         if (debug) {
            log.debug("key="+key+", value="+value);
         }
         if( key.equals(Context.PROVIDER_URL) )
            providerURL = value;
      }
      // Warn if there was a Context.PROVIDER_URL
      if( providerURL != null )
         log.warn("Context.PROVIDER_URL in server jndi.properties, url="+providerURL);

      /* Bind an ObjectFactory to "java:comp" so that "java:comp/env" lookups
         produce a unique context for each thread contexxt ClassLoader that
         performs the lookup.
      */
      ClassLoader topLoader = Thread.currentThread().getContextClassLoader();
      ENCFactory.setTopClassLoader(topLoader);
      RefAddr refAddr = new StringRefAddr("nns", "ENC");
      Reference envRef = new Reference("javax.naming.Context", refAddr, ENCFactory.class.getName(), null);
      Context ctx = (Context)iniCtx.lookup("java:");
      ctx.rebind("comp", envRef);
      if (log.isInfoEnabled())
         log.info("Listening on port "+naming.getPort());
   }

   protected void stopService()
      throws Exception
   {
      naming.stop();
      log.debug("JNP server stopped");
   }
   
   public void postRegister( Boolean pRegistrationDone )
   {
      super.postRegister( pRegistrationDone );
      if( pRegistrationDone.booleanValue() ) {
         // Create the JSR-77 management representation
         mJNDI = JNDIResource.create( getServer(), "LocalJNDI", getServiceName() );
      }
   }
   
   public void postDeregister()
   {
      super.postDeregister();
      if( mJNDI != null ) {
         // Destroy the JSR-77 management representation
         JNDIResource.destroy( getServer(), "LocalJNDI" );
      }
   }
}
