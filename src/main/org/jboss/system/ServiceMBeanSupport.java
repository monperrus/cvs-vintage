/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.system;

import java.util.Date;

import javax.management.NotificationBroadcasterSupport;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;

import org.apache.log4j.Category;
   
import org.jboss.logging.Log;
import org.jboss.logging.LogToCategory;
import org.jboss.logging.log4j.JBossCategory;

/**
 * An abstract base class JBoss services can subclass to implement a
 * service that conforms to the ServiceMBean interface. Subclasses must
 * override {@link #getName} method and should override 
 * {@link #initService}, {@link #startService}, {@link #stopService} AND
 * {@link #destroyService} as approriate.
 * 
 * 
 * @see ServiceMBean
 * 
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>
 * @author <a href="mailto:Scott_Stark@displayscape.com">Scott Stark</a>
 * @version $Revision: 1.2 $
 * 
 * Revisions:
 * 20010619 scott.stark: use the full service class name as the log4j
 *                       category name
 */
public abstract class ServiceMBeanSupport
   extends NotificationBroadcasterSupport
   implements ServiceMBean, MBeanRegistration
{
   // Attributes ----------------------------------------------------

   private int state;
   private MBeanServer server;
   private int id = 0;

   protected Log log;
   protected JBossCategory category;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   public ServiceMBeanSupport()
   {
      category = (JBossCategory)JBossCategory.getInstance(getClass());
      log = new LogToCategory(category);
   }

   // Public --------------------------------------------------------

   public abstract String getName();

   public MBeanServer getServer()
   {
      return server;
   }

   public int getState()
   {
      return state;
   }
   
   public String getStateString()
   {
      return states[state];
   }
   
   public void init()
      throws Exception
   {
      log.setLog(log);
      category.info("Initializing");
      try
      {
         initService();
      } catch (Exception e)
      {
         category.error("Initialization failed", e);
         throw e;
      } finally
      {
         log.unsetLog();
      }
      category.info("Initialized");
   }
	
   public void start()
      throws Exception
   {
      if (getState() != STOPPED)
         return;
			
      state = STARTING;
      //AS It seems that the first attribute is not needed anymore and use a long instead of a Date
      sendNotification(new AttributeChangeNotification(this, id++, new Date().getTime(), getName()+" starting", "State", "java.lang.Integer", new Integer(STOPPED), new Integer(STARTING)));
      category.info("Starting");
      log.setLog(log);
      try
      {
         startService();
      } catch (Exception e)
      {
         state = STOPPED;
         //AS It seems that the first attribute is not needed anymore and use a long instead of a Date
         sendNotification(new AttributeChangeNotification(this, id++, new Date().getTime(), getName()+" stopped", "State", "java.lang.Integer", new Integer(STARTING), new Integer(STOPPED)));
         category.error("Stopped", e);
         throw e;
      } finally
      {
         log.unsetLog();
      }
      state = STARTED;
      //AS It seems that the first attribute is not needed anymore and use a long instead of a Date
      sendNotification(new AttributeChangeNotification(this, id++, new Date().getTime(), getName()+" started", "State", "java.lang.Integer", new Integer(STARTING), new Integer(STARTED)));
      category.info("Started");
   }
   
   public void stop()
   {
      if (getState() != STARTED)
         return;
	
      state = STOPPING;
      //AS It seems that the first attribute is not needed anymore and use a long instead of a Date
      sendNotification(new AttributeChangeNotification(this, id++, new Date().getTime(), getName()+" stopping", "State", "java.lang.Integer", new Integer(STARTED), new Integer(STOPPING)));
      category.info("Stopping");
      log.setLog(log);
      
      try
      {
         stopService();
      } catch (Throwable e)
      {
         category.error(e);
      }
      
      state = STOPPED;
      //AS It seems that the first attribute is not needed anymore and use a long instead of a Date
      sendNotification(new AttributeChangeNotification(this, id++, new Date().getTime(), getName()+" stopped", "State", "java.lang.Integer", new Integer(STOPPING), new Integer(STOPPED)));
      category.info("Stopped");
      log.unsetLog();
   }
   
   public void destroy()
   {
      if (getState() != STOPPED)
         stop();
	
      category.info("Destroying");
      log.setLog(log);
      try
      {
         destroyService();
      } catch (Exception e)
      {
         category.error(e);
      }
   	
      log.unsetLog();
      category.info("Destroyed");
   }
	
   public ObjectName preRegister(MBeanServer server, ObjectName name)
      throws Exception
   {
      name = getObjectName(server, name);
      this.server = server;
      return name;
   }

   public void postRegister(Boolean registrationDone)
   {
      if (!registrationDone.booleanValue())
      {
         category.info( "Registration is not done -> destroy" );
         destroy();
      }
   }
   
   public void preDeregister()
      throws Exception
   {
   }
   
   public void postDeregister()
   {
      destroy();
   }
   
   // Protected -----------------------------------------------------
   
   protected ObjectName getObjectName(MBeanServer server, ObjectName name)
      throws MalformedObjectNameException
   {
      return name;
   }
   
   protected void initService()
      throws Exception
   {
   }
	
   protected void startService()
      throws Exception
   {
   }
   
   protected void stopService()
   {
   }
	
   protected void destroyService()
   {
   }
}
