/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.deployment;

// $Id: J2eeApplicationMetaData.java,v 1.9 2004/08/12 23:58:03 starksm Exp $

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.metadata.MetaData;
import org.jboss.metadata.SecurityRoleMetaData;
import org.jboss.mx.util.MBeanServerLocator;
import org.w3c.dom.Element;

/**
 * A representation of the application.xml and jboss-app.xml deployment
 * descriptors.
 *
 * @author Thomas.Diesler@jboss.org
 * @version $Revision: 1.9 $
 * @see org.jboss.metadata.XmlLoadable
 */
public class J2eeApplicationMetaData
        extends MetaData
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------
   private String displayName;
   private String description;
   private String smallIcon;
   private String largeIcon;

   /**
    * The security-roles
    */
   private HashMap securityRoles = new HashMap();
   /**
    * The jboss-app.xml JNDI name of the security domain implementation
    */
   private String securityDomain;
   /**
    * The  unauthenticated-principal value assigned to the application
    */
   private String unauthenticatedPrincipal;
   /** The application.xml modules in definition order */
   private Collection modules = new ArrayList();
   
   /** The jmx name */
   private String jmxName;

   // Static --------------------------------------------------------

   // Public --------------------------------------------------------

   public String getDisplayName()
   {
      return displayName;
   }

   public String getDescription()
   {
      return description;
   }

   public String getSmallIcon()
   {
      return smallIcon;
   }

   public String getLargeIcon()
   {
      return largeIcon;
   }

   public Iterator getModules()
   {
      return modules.iterator();
   }

   public Map getSecurityRoles()
   {
      return new HashMap(securityRoles);
   }

   public String getSecurityDomain()
   {
      return securityDomain;
   }

   public String getUnauthenticatedPrincipal()
   {
      return unauthenticatedPrincipal;
   }

   public String getJMXName()
   {
      return jmxName;
   }
   
   /**
    * Imports either the application.xml or jboss-app.xml from the given element.
    *
    * @param rootElement The element to import.
    * @throws DeploymentException Unrecognized root tag.
    */
   public void importXml(Element rootElement) throws DeploymentException
   {
      String rootTag = rootElement.getOwnerDocument().getDocumentElement().getTagName();
      if (rootTag.equals("application"))
      {
         importApplicationXml(rootElement);
      }
      else if (rootTag.equals("jboss-app"))
      {
         importJBossAppXml(rootElement);
      }
      else
      {
         throw new DeploymentException("Unrecognized root tag: " + rootTag);
      }
   }

   protected void importApplicationXml(Element rootElement) throws DeploymentException
   {
      displayName = getElementContent(getUniqueChild(rootElement, "display-name"));

      Element descrElement = getOptionalChild(rootElement, "description");
      description = descrElement != null ? getElementContent(descrElement) : "";

      Element iconElement = getOptionalChild(rootElement, "icon");
      if (iconElement != null)
      {
         Element element = getOptionalChild(iconElement, "small-icon");
         smallIcon = element != null ? getElementContent(element) : "";

         element = getOptionalChild(iconElement, "large-icon");
         largeIcon = element != null ? getElementContent(element) : "";
      }
      else
      {
         smallIcon = "";
         largeIcon = "";
      }

      // extract modules...
      for (Iterator it = getChildrenByTagName(rootElement, "module"); it.hasNext();)
      {
         J2eeModuleMetaData moduleMetaData = new J2eeModuleMetaData();
         moduleMetaData.importXml((Element) it.next());
         modules.add(moduleMetaData);
      }
   }

   protected void importJBossAppXml(Element rootElement) throws DeploymentException
   {
      // Get the security domain name
      Element securityDomainElement = getOptionalChild(rootElement, "security-domain");
      if (securityDomainElement != null)
      {
         securityDomain = getElementContent(securityDomainElement);
      }

      // Get the unauthenticated-principal name
      Element unauth = getOptionalChild(rootElement, "unauthenticated-principal");
      if (unauth != null)
      {
         unauthenticatedPrincipal = getElementContent(unauth);
      }
      else
      {
         try
         {
            MBeanServer server = MBeanServerLocator.locateJBoss();
            ObjectName oname = new ObjectName("jboss.security:service=JaasSecurityManager");
            unauthenticatedPrincipal = (String) server.getAttribute(oname, "DefaultUnauthenticatedPrincipal");
         }
         catch (Exception e)
         {
            log.error("Cannot obtain unauthenticated principal");
         }
      }

      // set the security roles (optional)
      Iterator iterator = getChildrenByTagName(rootElement, "security-role");
      while (iterator.hasNext())
      {
         Element securityRole = (Element) iterator.next();
         String roleName = getElementContent(getUniqueChild(securityRole, "role-name"));
         SecurityRoleMetaData srMetaData = new SecurityRoleMetaData(roleName);

         Iterator itPrincipalNames = getChildrenByTagName(securityRole, "principal-name");
         while (itPrincipalNames.hasNext())
         {
            String principalName = getElementContent((Element) itPrincipalNames.next());
            srMetaData.addPrincipalName(principalName);
         }
         securityRoles.put(roleName, srMetaData);
      }

      // Get any user defined JMX name
      Element jmxNameElement = getOptionalChild(rootElement, "jmx-name");
      if (jmxNameElement != null)
         jmxName = getElementContent(jmxNameElement);

      // extract modules...
      for (Iterator it = getChildrenByTagName(rootElement, "module"); it.hasNext();)
      {
         J2eeModuleMetaData moduleMetaData = new J2eeModuleMetaData();
         moduleMetaData.importXml((Element) it.next());
         modules.add(moduleMetaData);
      }
   }
}
