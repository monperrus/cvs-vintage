/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.deployment;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.beans.beancontext.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dreambean.awt.GenericCustomizer;
import com.dreambean.awt.GenericMethodDialog;
import com.dreambean.awt.BeanContextPanel;
import com.dreambean.awt.BeanContextTreeView;
import com.dreambean.ejx.xml.XMLManager;
import com.dreambean.ejx.xml.XmlExternalizable;
import com.dreambean.ejx.Util;

/**
 *   <description> 
 *      
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.3 $
 */
public class ContainerConfigurations
   extends BeanContextSupport
   implements BeanContextChildComponentProxy, XmlExternalizable
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   Component c;
    
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
    
   // Public --------------------------------------------------------
   public void createContainerConfiguration(String name, String clazz)
      throws IOException, ClassNotFoundException
   {
      
      ((ContainerConfiguration)instantiateChild(clazz)).setName(name);
   }
   
   public ContainerConfiguration addContainerConfiguration(String clazz)
      throws IOException, ClassNotFoundException
   {
      return (ContainerConfiguration)instantiateChild(clazz);
   }   
   
   public ContainerConfiguration getContainerConfiguration(String name)
   {
      
      for (Iterator enum = iterator(); enum.hasNext();)
      {
         ContainerConfiguration cc = (ContainerConfiguration)enum.next();
         if (cc.getName().equals(name))
            return cc;
      }
      return null;
   }
   
   // BeanContextChildComponentProxy implementation -----------------
   public Component getComponent()
   {
      if (c == null)
      {
			c = new BeanContextPanel(this);
			JSplitPane sp = (JSplitPane)c;
			JScrollPane scrollPane = (JScrollPane)sp.getLeftComponent();
			((BeanContextTreeView)scrollPane.getViewport().getView()).expandPath(((BeanContextTreeView)scrollPane.getViewport().getView()).getPathForRow(0));
			((BeanContextTreeView)scrollPane.getViewport().getView()).setRootVisible(false);
			
			JToolBar toolBar = new JToolBar();
			toolBar.add(new AbstractAction("New")
			{
				public void actionPerformed(ActionEvent evt)
				{
					try
					{
						BeanInfo bi = Introspector.getBeanInfo(ContainerConfigurations.class);
						
						MethodDescriptor[] mdList = bi.getMethodDescriptors();
						for (int i = 0; i < mdList.length; i++)
						{
							if (mdList[i].getName().equals("createContainerConfiguration"))
							{
								new GenericMethodDialog(ContainerConfigurations.this, mdList[i], (Frame)SwingUtilities.getRoot(c));
								break;
							}
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			
			JPanel p = new JPanel(new BorderLayout());
			p.add("Center", c);
			p.add("North", toolBar);
			
			c = p;
			c.setName("Container configurations");
		}
			
      return c;
   }
   
   // XmlExternalizable implementation ------------------------------
   public Element exportXml(Document doc)
      throws Exception
   {
      Element containerconfigurations = doc.createElement("container-configurations");
      
      for (Iterator enum = iterator(); enum.hasNext();)
      {
         containerconfigurations.appendChild(((XmlExternalizable)enum.next()).exportXml(doc));
      }
      
      return containerconfigurations;
   }
   
   public void importXml(Element elt)
      throws Exception
   {
      if (elt.getOwnerDocument().getDocumentElement().getTagName().equals(jBossEjbJar.JBOSS_DOCUMENT))
      {
         NodeList nl = elt.getChildNodes();
         for (int i = 0; i < nl.getLength(); i++)
         {
            Node n = nl.item(i);
            String name = n.getNodeName();
            
            if (name.equals("container-configuration"))
            {
               String confType = ((Element)n).getAttribute("configuration-class");
               
               XmlExternalizable conConf = (XmlExternalizable)addContainerConfiguration(confType);
               
               conConf.importXml((Element)n);
            } 
         }
      } 
   }
    
   // Package protected ---------------------------------------------
    
   // Protected -----------------------------------------------------
    
   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
