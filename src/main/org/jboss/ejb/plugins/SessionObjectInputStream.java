/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.Collection;
import java.util.ArrayList;

import javax.ejb.EJBObject;
import javax.ejb.EJBHome;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.EntityBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;
import javax.ejb.DuplicateKeyException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.transaction.UserTransaction;

import org.jboss.ejb.Container;
import org.jboss.ejb.EntityContainer;
import org.jboss.ejb.EntityPersistenceManager;
import org.jboss.ejb.StatefulSessionEnterpriseContext;

/**
 * The SessionObjectInputStream is used to deserialize stateful session beans when they are activated
 *      
 *	@see org.jboss.ejb.plugins.SessionObjectOutputStream
 *	@author Rickard �berg (rickard.oberg@telkel.com)
 *	@author <a href="mailto:sebastien.alborini@m4x.org">Sebastien Alborini</a>
 *	@version $Revision: 1.2 $
 */
class SessionObjectInputStream
	extends ObjectInputStream
{
	StatefulSessionEnterpriseContext ctx;
   ClassLoader appCl;

	// Constructors -------------------------------------------------
	public SessionObjectInputStream(StatefulSessionEnterpriseContext ctx, InputStream in)
      throws IOException
   {
      super(in);
      enableResolveObject(true);
		
		this.ctx = ctx;
      
      // cache the application classloader
      appCl = Thread.currentThread().getContextClassLoader();
   }
      
   // ObjectInputStream overrides -----------------------------------
   protected Object resolveObject(Object obj)
      throws IOException
   {
      // section 6.4.1 of the ejb1.1 specification states what must be taken care of 
      
      // ejb reference (remote interface) : resolve handle to EJB
      if (obj instanceof Handle)
         return ((Handle)obj).getEJBObject();
      
      // ejb reference (home interface) : resolve handle to EJB Home
      else if (obj instanceof HomeHandle)
         return ((HomeHandle)obj).getEJBHome();
      
      // naming context: the jnp implementation of contexts is serializable, do nothing

      else if (obj instanceof StatefulSessionBeanField) {
         byte type = ((StatefulSessionBeanField)obj).type; 
       
         // session context: recreate it
         if (type == StatefulSessionBeanField.SESSION_CONTEXT)          
            return ctx.getSessionContext();

         // user transaction: restore it
         else if (type == StatefulSessionBeanField.USER_TRANSACTION) 
            return ctx.getSessionContext().getUserTransaction();      
      }
      return obj;
   }
   
   protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
      try {
         // use the application classloader to resolve the class
         return appCl.loadClass(v.getName());
         
      } catch (ClassNotFoundException e) {
         // we should probably never get here
         return super.resolveClass(v);
      }
   }
      
}
