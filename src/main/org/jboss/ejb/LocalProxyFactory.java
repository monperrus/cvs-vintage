/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb;

import java.util.Collection;

import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;

/**
 * This is an extension to the EJBProxyFactory interface. Although some
 * implementations of the ProxyFactory interface may provide access
 * to local interfaces, others (e.g. which provide remote distribution)
 * will not. Good example: the JRMP delegates do not need to implement
 * this interface.
 *
 * @see EJBProxyFactory
 * 
 * @author <a href="mailto:docodan@mvcsoft.com">Daniel OConnor</a>
 * @version $Revision: 1.4 $
 */
public interface LocalProxyFactory
   extends GenericEntityObjectFactory, ContainerPlugin
{
   /**
    * This method is called whenever the EJBLocalHome implementation for this
    * container is needed.
    *
    * @return    an implementation of the local home interface for this
    *            container
    */
   EJBLocalHome getEJBLocalHome();

   /**
    * This method is called whenever an EJBLocalObject implementation for a
    * stateless session bean is needed.
    *
    * @return    an implementation of the local interface for this container
    */
   EJBLocalObject getStatelessSessionEJBLocalObject();

   /**
    * This method is called whenever an EJBLocalObject implementation for a
    * stateful session bean is needed.
    *
    * @param id    the id of the session
    * @return       an implementation of the local interface for this container
    */
   EJBLocalObject getStatefulSessionEJBLocalObject(Object id);
      
   /**
    * This method is called whenever an EJBLocalObject implementation for an
    * entitybean is needed.
    *
    * @param id    the primary key of the entity
    * @return      an implementation of the local interface for this container
    */
   EJBLocalObject getEntityEJBLocalObject(Object id);
   
   /**
    * This method is called whenever a collection of EJBLocalObjects for a
    * collection of primary keys is needed.
    *
    * @param enum    enumeration of primary keys
    * @return        a collection of EJBLocalObjects implementing the remote
    *                interface for this container
    */
   Collection getEntityLocalCollection(Collection enum);
}
