/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
 
package org.jboss.tm;

import java.io.File;
import java.net.URL;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.Reference;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import javax.transaction.TransactionManager;

import org.jboss.logging.Log;
import org.jboss.util.ServiceMBeanSupport;

/**
 *   This is a JMX service which manages the TransactionManager.
 *	  The service creates it and binds a Reference to it into JNDI.
 *      
 *   @see TxManager
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.1 $
 */
public class TransactionManagerService
   extends ServiceMBeanSupport
   implements TransactionManagerServiceMBean, ObjectFactory
{
   // Constants -----------------------------------------------------
   public static String JNDI_NAME = "TransactionManager";
    
   // Attributes ----------------------------------------------------
	MBeanServer server;
   
   // Static --------------------------------------------------------
   static TransactionManager tm;

   // ServiceMBeanSupport overrides ---------------------------------
   public String getName()
   {
      return "Transaction manager";
	}
   
   protected ObjectName getObjectName(MBeanServer server, ObjectName name)
      throws javax.management.MalformedObjectNameException
   {
   	this.server = server;
      return new ObjectName(OBJECT_NAME);
   }
	
   protected void initService()
      throws Exception
   {
	   // Create a new TM
	   tm = new TxManager();
   }
	
   protected void startService()
      throws Exception
   {
		// Bind reference to TM in JNDI
	   Reference ref = new Reference(tm.getClass().toString(), getClass().getName(), null);
	   new InitialContext().bind(JNDI_NAME, ref);
   }
   
   protected void stopService()
   {
		try
		{
			// Remove TM from JNDI
			new InitialContext().unbind(JNDI_NAME);
		} catch (Exception e)
		{
			log.exception(e);
		}
   }
	
	// ObjectFactory implementation ----------------------------------
	public Object getObjectInstance(Object obj,
                                Name name,
                                Context nameCtx,
                                Hashtable environment)
                         throws Exception
	{
		// Return the transaction manager
		return tm;
	}
}

