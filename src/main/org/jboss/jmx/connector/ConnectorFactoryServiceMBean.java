/*
* JBoss, the OpenSource J2EE webOS
*
* Distributable under LGPL license.
* See terms of license at gnu.org.
*/
package org.jboss.jmx.connector;

import java.util.Iterator;
import java.util.Hashtable;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;

import org.jboss.jmx.connector.JMXConnector;
import org.jboss.system.ServiceMBean;

/**
* Factory delivering a list of servers and its available protocol connectors
* and after selected to initiate the connection
*
* This is just the (incomplete) interface of it
*
* @author <A href="mailto:andreas.schaefer@madplanet.com">Andreas &quot;Mad&quot; Schaefer</A>
**/
public interface ConnectorFactoryServiceMBean
	extends ServiceMBean
{

	// Constants -----------------------------------------------------
	public static final String OBJECT_NAME = "Factory:name=JMX";
	
	// Public --------------------------------------------------------

   /**
   * @return JMS Queue Name and if not null then JMS will be used
   **/
   public String getJMSName();
   
   /**
   * Sets the JMS Queue Factory Name which allows the server to send
   * the notifications asynchronous to the client
   *
   * @param pName If null the notification will be transferred
   *              by using RMI Callback objects otherwise it
   *              will use JMS
   **/
   public void setJMSName( String pName );
   
   /**
   * @return EJB Adaptor JNDI Name used by the EJB-Connector
   **/
   public String getEJBAdaptorName();
   
   /**
   * Sets the JNDI Name of the EJB-Adaptor
   *
   * @param pName If null the default JNDI name (ejb/jmx/ejb/adaptor) will
   *              be used for EJB-Connectors otherwise it will use this one
   **/
   public void setEJBAdaptorName( String pName );
   
   /**
   * Look up for all registered JMX Connector at a given JNDI server
   *
   * @param pProperties List of properties defining the JNDI server
   * @param pTester Connector Tester implementation to be used
   *
   * @return An iterator on the list of ConnectorNames representing
   *         the found JMX Connectors
   **/
   public Iterator getConnectors( Hashtable pProperties, ConnectorFactoryImpl.IConnectorTester pTester );

   /**
   * Initiate a connection to the given server with the given protocol
   *
   * @param pConnector Connector Name used to identify the remote JMX Connector
   *
   * @return JMX Connector or null if server or protocol is not supported
   **/
   public JMXConnector createConnection(
      ConnectorFactoryImpl.ConnectorName pConnector
   );

   /**
   * Removes the given connection and frees the resources
   *
   * @param pConnector Connector Name used to identify the remote JMX Connector
   **/
   public void removeConnection(
      ConnectorFactoryImpl.ConnectorName pConnector
   );
}
