/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.jrmp12.interfaces;

import java.rmi.RemoteException;
import java.rmi.ServerException;
import javax.ejb.Handle;
import javax.ejb.EJBObject;
import javax.naming.InitialContext;
import java.lang.reflect.Method;


/**
 *	<description> 
 *      
 *	@see <related>
 *	@author Rickard �berg (rickard.oberg@telkel.com)
 *	@version $Revision: 1.1 $
 */
public class EntityHandleImpl
   implements Handle
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   String name;
   Object id;
   
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   public EntityHandleImpl(String name, Object id)
   {
      this.name = name;
      this.id = id;
   }
   
   // Public --------------------------------------------------------

   // Handle implementation -----------------------------------------
   public EJBObject getEJBObject()
      throws RemoteException
   {
      try
      {
         System.out.println("Resolve handle:"+name+"#"+id);
         Object home = new InitialContext().lookup(name);
         
         Method finder = home.getClass().getMethod("findByPrimaryKey", new Class[] { id.getClass() });
         return (EJBObject)finder.invoke(home, new Object[] { id });
      } catch (Exception e)
      {
         throw new ServerException("Could not get EJBObject", e);
      }
   }

   // Package protected ---------------------------------------------
    
   // Protected -----------------------------------------------------
    
   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}

