/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.jms;

import javax.jms.JMSException;
import javax.jms.Connection;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XATopicConnectionFactory;

import org.apache.log4j.Category;

/**
 * A helper for creating connections from jms connection factories.
 *      
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Revision: 1.4 $
 */
public class ConnectionFactoryHelper
{
   /** Class logger. */
   private static Category log =
      Category.getInstance(ConnectionFactoryHelper.class);
   
   /**
    * Create a queue connection from the given factory.  An XA connection will
    * be created if possible.
    *
    * @param factory     An object that implements QueueConnectionFactory,
    *                    XAQueueConnectionFactory
    * @param username    The username to use or null for no user.
    * @param password    The password for the given username or null if no
    *                    username was specified.
    * @return            A queue connection.
    *                    
    * @throws JMSException                Failed to create connection.
    * @throws IllegalArgumentException    Factory is null or invalid.
    */
   public static QueueConnection createQueueConnection(final Object factory,
                                                       final String username,
                                                       final String password)
      throws JMSException
   {
      if (factory == null)
         throw new IllegalArgumentException("factory is null");

      if (log.isDebugEnabled()) {
         log.debug("using connection factory: " + factory);
         log.debug("using username/password: " +
                   String.valueOf(username) + "/" +
                   String.valueOf(password));
      }

      QueueConnection connection;
      
      if (factory instanceof XAQueueConnectionFactory) {
         XAQueueConnectionFactory qFactory = (XAQueueConnectionFactory)factory;
         if (username != null) {
            connection = qFactory.createXAQueueConnection(username, password);
         }
         else {
            connection = qFactory.createXAQueueConnection();
         }
         log.debug("created XAQueueConnection: " + connection);
      }
      else if (factory instanceof QueueConnectionFactory) {
         QueueConnectionFactory qFactory = (QueueConnectionFactory)factory;
         if (username != null) {
            connection = qFactory.createQueueConnection(username, password);
         }
         else {
            connection = qFactory.createQueueConnection();
         }
         log.debug("created QueueConnection: " + connection);
      }
      else {
         throw new IllegalArgumentException("factory is invalid");
      }
      
      return connection;
   }

   /**
    * Create a queue connection from the given factory.  An XA connection will
    * be created if possible.
    *
    * @param factory     An object that implements QueueConnectionFactory,
    *                    XAQueueConnectionFactory
    * @return            A queue connection.
    *                    
    * @throws JMSException                Failed to create connection.
    * @throws IllegalArgumentException    Factory is null or invalid.
    */
   public static QueueConnection createQueueConnection(final Object factory)
      throws JMSException
   {
      return createQueueConnection(factory, null, null);
   }
   
   /**
    * Create a topic connection from the given factory.  An XA connection will
    * be created if possible.
    *
    * @param factory     An object that implements TopicConnectionFactory,
    *                    XATopicConnectionFactory
    * @param username    The username to use or null for no user.
    * @param password    The password for the given username or null if no
    *                    username was specified.
    * @return            A topic connection.
    *                    
    * @throws JMSException                Failed to create connection.
    * @throws IllegalArgumentException    Factory is null or invalid.
    */
   public static TopicConnection createTopicConnection(final Object factory,
                                                       final String username,
                                                       final String password)
      throws JMSException
   {
      if (factory == null)
         throw new IllegalArgumentException("factory is null");

      if (log.isDebugEnabled()) {
         log.debug("using connection factory: " + factory);
         log.debug("using username/password: " +
                   String.valueOf(username) + "/" +
                   String.valueOf(password));
      }

      TopicConnection connection;
      
      if (factory instanceof XATopicConnectionFactory) {
         XATopicConnectionFactory tFactory = (XATopicConnectionFactory)factory;
         if (username != null) {
            connection = tFactory.createXATopicConnection(username, password);
         }
         else {
            connection = tFactory.createXATopicConnection();
         }
         log.debug("created XATopicConnection: " + connection);
      }
      else if (factory instanceof TopicConnectionFactory) {
         TopicConnectionFactory tFactory = (TopicConnectionFactory)factory;
         if (username != null) {
            connection = tFactory.createTopicConnection(username, password);
         }
         else {
            connection = tFactory.createTopicConnection();
         }
         log.debug("created TopicConnection: " + connection);
      }
      else {
         throw new IllegalArgumentException("factory is invalid");
      }
      
      return connection;
   }

   /**
    * Create a topic connection from the given factory.  An XA connection will
    * be created if possible.
    *
    * @param factory     An object that implements TopicConnectionFactory,
    *                    XATopicConnectionFactory
    * @return            A topic connection.
    *                    
    * @throws JMSException                Failed to create connection.
    * @throws IllegalArgumentException    Factory is null or invalid.
    */
   public static TopicConnection createTopicConnection(final Object factory)
      throws JMSException
   {
      return createTopicConnection(factory, null, null);
   }
}
