/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.system;

import javax.management.ObjectName;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/** 
 * This is the main Service Controller API.
 * 
 * <p>A controller can deploy a service to a JBOSS-SYSTEM
 *    It installs by delegating, it configures by delegating
 *
 * @see Service
 *
 * @author <a href="mailto:marc.fleury@jboss.org">Marc Fleury</a>
 * @version $Revision: 1.2 $
 *
 * <p><b>20010830 marc fleury:</b>
 * <ul>
 *   <li>Initial import
 * </ul>
 */
public interface ServiceControllerMBean
   extends Service
{
   /** The default object name. */
   String OBJECT_NAME = "JBOSS-SYSTEM:spine=ServiceController";

   //
   // high level calls on the MBean deployment
   //
   
   ObjectName deploy(Element mbean) throws Exception;
   void undeploy(Element mbean) throws Exception;
   void undeploy(ObjectName mbeanName) throws Exception;

   //
   // State calls, init, start, stop, destroy
   //
   
   void init(ObjectName mbean) throws Exception;
   void start(ObjectName mbean) throws Exception;
   void stop(ObjectName mbean) throws Exception;
   void destroy(ObjectName mbean) throws Exception;

   /** Get a list of deployed elements, in the order they were deployed */
   ObjectName[] getDeployed();

   /** Get configuration will output an XML file in the array order */
   String getConfiguration(ObjectName[] objectNames) throws Exception;
}
