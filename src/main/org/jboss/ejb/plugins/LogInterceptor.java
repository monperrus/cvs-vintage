/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.EJBObject;
import javax.ejb.EJBMetaData;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import org.jboss.ejb.Container;
import org.jboss.ejb.EnterpriseContext;
import org.jboss.ejb.MethodInvocation;

import org.jboss.logging.Log;

/**
 *   <description> 
 *      
 *   @see <related>
 *   @author Rickard �berg (rickard.oberg@telkel.com)
 *   @version $Revision: 1.6 $
 */
public class LogInterceptor
   extends AbstractInterceptor
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   protected Log log;
   
	protected boolean callLogging;
	
	protected Container container;
	
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   public void setContainer(Container container) 
   { 
   	this.container = container; 
   }
	
   public  Container getContainer()
   {
   	return container;
   }

   // Container implementation --------------------------------------
   public void init()
      throws Exception
   {
      super.start();
      
      String name = getContainer().getBeanMetaData().getEjbName();
		
		// Should we log all calls?
		callLogging = getContainer().getBeanMetaData().getContainerConfiguration().getCallLogging();
		
      log = new Log(name);
   }
   
   public Object invokeHome(MethodInvocation mi)
      throws Exception
   {
      Log.setLog(log);
      
		// Log calls?
		if (callLogging)
		{
			StringBuffer str = new StringBuffer();
			str.append(mi.getMethod().getName());
			str.append("(");
			Object[] args = mi.getArguments();
			if (args != null)
			   for (int i = 0; i < args.length; i++)
				{
			      str.append(i==0?"":",");
					str.append(args[i]);
				}
			str.append(")");
			log.log(str.toString());
		}
      
      try
      {
         return getNext().invokeHome(mi);
      } catch (Exception e)
      {
			// Log system exceptions
         if (e instanceof RemoteException ||
				 e instanceof RuntimeException)
			{
				e.printStackTrace();
			}
			
         throw e;
      } finally
      {
         Log.unsetLog();
      }
   }

   /**
    *   This method does invocation interpositioning of tx and security, 
    *   retrieves the instance from an object table, and invokes the method
    *   on the particular instance
    *
    * @param   id  
    * @param   m  
    * @param   args  
    * @return     
    * @exception   Exception  
    */
   public Object invoke(MethodInvocation mi)
      throws Exception
   {
      Log.setLog(log);
      
      // Log calls?
      if (callLogging)
      {
      	StringBuffer str = new StringBuffer();
         str.append(mi.getId() == null ? "" : "["+mi.getId().toString()+"] ");
      	str.append(mi.getMethod().getName());
      	str.append("(");
         Object[] args = mi.getArguments();
         if (args != null)
            for (int i = 0; i < args.length; i++)
      		{
               str.append(i==0?"":",");
      			str.append(args[i]);
      		}
      	str.append(")");
         log.log(str.toString());
      }
		
      try
      {
         return getNext().invoke(mi);
      } catch (Exception e)
      {
         log.exception(e);
         throw e;
      } finally
      {
         Log.unsetLog();
      }
   }
   
   // Private -------------------------------------------------------
}

