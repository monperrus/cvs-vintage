/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

// $Id: WebserviceClientDeployer.java,v 1.2 2003/11/23 18:24:57 tdiesler Exp $

package org.jboss.deployment;

import org.jboss.logging.Logger;
import org.jboss.mx.util.MBeanServerLocator;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

/**
 * A deployment helper that manages JSR109 compliant webservice clients.
 *
 * @since 10-Nov-2003
 * @author <a href="mailto:thomas.diesler@arcor.de">Thomas Diesler</a>
 * @version $Revision: 1.2 $
 */
public class WebserviceClientDeployer
{
   private static final Logger log = Logger.getLogger(WebserviceClientDeployer.class);

   public static final String JSR109_CLIENT_SERVICE_NAME = "jboss.net:service=JSR109ClientService";

   /** Parse the webservicesclient.xml and return the metadata,
    *  or null if the JSR109ClientService is not available.
    */
   public Object create(DeploymentInfo di) throws DeploymentException
   {
      log.debug("create: " + di.url);

      Object webservicesClient = null;
      try
      {
         MBeanServer server = MBeanServerLocator.locateJBoss();
         ObjectName objectName = new ObjectName(JSR109_CLIENT_SERVICE_NAME);
         if (server.isRegistered(objectName))
         {
            webservicesClient = server.invoke(objectName, "create", new Object[]{di}, new String[]{DeploymentInfo.class.getName()});
         }
         else
         {
            log.warn ("This is a webservice client deployment, but '" + JSR109_CLIENT_SERVICE_NAME + "' is not registered: " + di.url);
         }
      }
      catch (Exception e)
      {
         throw new DeploymentException("Cannot create webservice client", e);
      }

      return webservicesClient;
   }

   /**
    * Bind the service interfaces to the ENC.
    */
   public void setupEnvironment(Context envCtx, DeploymentInfo di, Object webservicesClient) throws DeploymentException
   {
      log.debug("setupEnvironment: " + di.url);

      try
      {
         MBeanServer server = MBeanServerLocator.locateJBoss();
         ObjectName objectName = new ObjectName(JSR109_CLIENT_SERVICE_NAME);
         server.invoke(objectName, "setupEnvironment",
                       new Object[]{envCtx, di, webservicesClient},
                       new String[]{Context.class.getName(), DeploymentInfo.class.getName(), Object.class.getName()});
      }
      catch (Exception e)
      {
         throw new DeploymentException("Cannot bind webservice client to ENC", e);
      }
   }
}
