/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.metadata;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.util.StringPropertyReplacer;

/**
 * An abstract base class for metadata containers.
 *
 * @author <a href="mailto:sebastien.alborini@m4x.org">Sebastien Alborini</a>
 * @version $Revision: 1.31 $
 */
public abstract class MetaData
   implements Cloneable, XmlLoadable
{
   // Constants -----------------------------------------------------

   protected static Logger log = Logger.getLogger(MetaData.class);
   
   // These do not really belong here

   public static final byte TX_NOT_SUPPORTED  = 0;
   public static final byte TX_REQUIRED       = 1;
   public static final byte TX_SUPPORTS       = 2;
   public static final byte TX_REQUIRES_NEW   = 3;
   public static final byte TX_MANDATORY      = 4;
   public static final byte TX_NEVER          = 5;
   public static final byte TX_UNKNOWN        = 6;

   // Attributes ----------------------------------------------------

   // Static --------------------------------------------------------

   /**
    * Returns an iterator over the children of the given element with
    * the given tag name.
    *
    * @param element    The parent element
    * @param tagName    The name of the desired child
    * @return           An interator of children or null if element is null.
    */
   public static Iterator getChildrenByTagName(Element element,
                                               String tagName)
   {
      if (element == null) return null;
      // getElementsByTagName gives the corresponding elements in the whole 
      // descendance. We want only children

      NodeList children = element.getChildNodes();
      ArrayList goodChildren = new ArrayList();
      for (int i=0; i<children.getLength(); i++) {
         Node currentChild = children.item(i);
         if (currentChild.getNodeType() == Node.ELEMENT_NODE && 
             ((Element)currentChild).getTagName().equals(tagName)) {
            goodChildren.add(currentChild);
         }
      }
      return goodChildren.iterator();
   }

   /**
    * Gets the child of the specified element having the specified unique
    * name.  If there are more than one children elements with the same name
    * and exception is thrown.
    *
    * @param element    The parent element
    * @param tagName    The name of the desired child
    * @return           The named child.
    *
    * @throws DeploymentException   Child was not found or was not unique.
    */
   public static Element getUniqueChild(Element element, String tagName)
      throws DeploymentException
   {
      Iterator goodChildren = getChildrenByTagName(element, tagName);

      if (goodChildren != null && goodChildren.hasNext()) {
         Element child = (Element)goodChildren.next();
         if (goodChildren.hasNext()) {
            throw new DeploymentException
               ("expected only one " + tagName + " tag");
         }
         return child;
      } else {
         throw new DeploymentException
            ("expected one " + tagName + " tag");
      }
   }

   /**
    * Gets the child of the specified element having the
    * specified name. If the child with this name doesn't exist
    * then null is returned instead.
    *
    * @param element the parent element
    * @param tagName the name of the desired child
    * @return either the named child or null
    */
   public static Element getOptionalChild(Element element, String tagName)
      throws DeploymentException
   {
      return getOptionalChild(element, tagName, null);
   }

   /**
    * Gets the child of the specified element having the
    * specified name. If the child with this name doesn't exist
    * then the supplied default element is returned instead.
    *
    * @param element the parent element
    * @param tagName the name of the desired child
    * @param defaultElement the element to return if the child
    *                       doesn't exist
    * @return either the named child or the supplied default
    */
   public static Element getOptionalChild(Element element,
                                          String tagName,
                                          Element defaultElement)
      throws DeploymentException
   {
      Iterator goodChildren = getChildrenByTagName(element, tagName);

      if (goodChildren != null && goodChildren.hasNext()) {
         Element child = (Element)goodChildren.next();
         if (goodChildren.hasNext()) {
            throw new DeploymentException
               ("expected only one " + tagName + " tag");
         }
         return child;
      } else {
         return defaultElement;
      }
   }

   /**
    * Get an attribute value of the given element.
    *
    * @param element    The element to get the attribute value for.
    * @return           The attribute value or null.
    */
   public static String getElementAttribute(final Element element, final String attrName)
      throws DeploymentException
   {
      if (element == null)
         return null;

      if (attrName == null || element.hasAttribute(attrName) == false)
         return null;

      String result = element.getAttribute(attrName);
      return StringPropertyReplacer.replaceProperties(result.trim());
   }

   /**
    * Get the content of the given element.
    *
    * @param element    The element to get the content for.
    * @return           The content of the element or null.
    */
   public static String getElementContent(final Element element)
      throws DeploymentException
   {
      return getElementContent(element, null);
   }

   /**
    * Get the content of the given element.
    *
    * @param element       The element to get the content for.
    * @param defaultStr    The default to return when there is no content.
    * @return              The content of the element or the default.
    */
   public static String getElementContent(Element element, String defaultStr)
      throws DeploymentException
   {
      if (element == null)
         return defaultStr;

      NodeList children = element.getChildNodes();
      String result = "";
      for (int i = 0; i < children.getLength(); i++)
      {
         if (children.item(i).getNodeType() == Node.TEXT_NODE || 
             children.item(i).getNodeType() == Node.CDATA_SECTION_NODE)
         {
            result += children.item(i).getNodeValue();
         }
         else if( children.item(i).getNodeType() == Node.COMMENT_NODE )
         {
            // Ignore comment nodes
         }
         else
         {
            result += children.item(i).getFirstChild();
         }
      }
      return StringPropertyReplacer.replaceProperties(result.trim());
   }

   public static String getFirstElementContent(Element element, String defaultStr)
      throws DeploymentException
   {
      if (element == null)
         return defaultStr;

      NodeList children = element.getChildNodes();
      String result = "";
      for (int i = 0; i < children.getLength(); i++)
      {
         if (children.item(i).getNodeType() == Node.TEXT_NODE ||
             children.item(i).getNodeType() == Node.CDATA_SECTION_NODE)
         {
            String val = children.item(i).getNodeValue();
            result += val;
         }
         else if( children.item(i).getNodeType() == Node.COMMENT_NODE )
         {
            // Ignore comment nodes
         }
         /*  For some reason, the original version gets the text of the first child
         else
         {
            result += children.item(i).getFirstChild();
         }
         */
      }
      return StringPropertyReplacer.replaceProperties(result.trim());
   }

   /**
    * Macro to get the content of a unique child element.
    *
    * @param element    The parent element.
    * @param tagName    The name of the desired child.
    * @return           The element content or null.
    */
   public static String getUniqueChildContent(Element element,
                                              String tagName)
      throws DeploymentException
   {
      return getElementContent(getUniqueChild(element, tagName));
   }

   /**
    * Macro to get the content of an optional child element.
    * 
    * @param element    The parent element.
    * @param tagName    The name of the desired child.
    * @return           The element content or null.
    */
   public static String getOptionalChildContent(Element element,
                                                String tagName)
      throws DeploymentException
   {
      return getElementContent(getOptionalChild(element, tagName));
   }

   /**
    * Macro to get the content of an optional child element with default value.
    *
    * @param element    The parent element.
    * @param tagName    The name of the desired child.
    * @return           The element content or null.
    */
   public static String getOptionalChildContent(Element element, String tagName, String defaultValue)
           throws DeploymentException
   {
      return getElementContent(getOptionalChild(element, tagName), defaultValue);
   }

   public static boolean getOptionalChildBooleanContent(Element element, String name) throws DeploymentException
   {
      Element child = getOptionalChild(element, name);
      if(child != null)
      {
         String value = getElementContent(child).toLowerCase();
         return value.equals("true") || value.equals("yes");
      }

      return false;
   }

   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   /** Create a field wise copy of the object.
   */
   public Object clone()
   {
      Object clone = null;
      try
      {
         clone = super.clone();
      }
      catch(CloneNotSupportedException ignore)
      {
      }
      return clone;
   }

   /**
    * Imports either the jboss or ejb-jar from the given element.
    *
    * @param element   The element to import.
    * 
    * @throws DeploymentException    Unrecognized root tag.
    */
   public void importXml(Element element) throws DeploymentException {
      String rootTag = element.getOwnerDocument().
         getDocumentElement().getTagName();

      if (rootTag.equals("jboss")) {
         // import jboss.xml
         importJbossXml(element);
      } else if (rootTag.equals("ejb-jar")) {
         // import ejb-jar.xml
         importEjbJarXml(element);
      } else {
         throw new DeploymentException("Unrecognized root tag : "+ rootTag);
      }
   }

   /**
    * Non-operation.
    *
    * @param element
    *
    * @throws DeploymentException
    */
   public void importEjbJarXml(Element element) throws DeploymentException {
      // empty
   }

   /**
    * Non-operation.
    *
    * @param element
    *
    * @throws DeploymentException
    */
   public void importJbossXml(Element element) throws DeploymentException {
      // empty
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   /**
    * Check if we are running in a JDK v1.3 virtual machine or better.
    *
    * @return    True if the virtual machine is v1.3 or better.
    */
   protected boolean jdk13Enabled() {
      // should use "java.version" ?
      String javaVersion = System.getProperty("java.vm.version");
      return javaVersion.compareTo("1.3") >= 0;
   }

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
/*
vim:ts=3:sw=3:et
*/
