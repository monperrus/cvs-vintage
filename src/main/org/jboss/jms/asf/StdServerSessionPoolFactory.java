/*
 * Copyright (c) 2000 Peter Antman DN <peter.antman@dn.se>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jboss.jms.asf;

import java.io.Serializable;

import javax.jms.ServerSessionPool;
import javax.jms.MessageListener;
import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * An implementation of ServerSessionPoolFactory.
 *
 * <p>Created: Fri Dec 22 09:47:41 2000
 *
 * @author <a href="mailto:peter.antman@tim.se">Peter Antman</a>.
 * @version $Revision: 1.5 $
 */
public class StdServerSessionPoolFactory
   implements ServerSessionPoolFactory, Serializable
{
   /** The name of this factory. */
   private String name;

   /**
    * Construct a <tt>StdServerSessionPoolFactory</tt>.
    */
   public StdServerSessionPoolFactory() {
      super();
   }

   /**
    * Set the name of the factory.
    *
    * @param name    The name of the factory.
    */
   public void setName(final String name) {
      this.name = name;
   }
   
   /**
    * Get the name of the factory.
    *
    * @return    The name of the factory.
    */
   public String getName() {
      return name;
   }

   /**
    * Create a new <tt>ServerSessionPool</tt>.
    *
    * @param con
    * @param maxSession
    * @param isTransacted
    * @param ack
    * @param listener
    * @return                A new pool.
    *
    * @throws JMSException
    */
   public ServerSessionPool getServerSessionPool(final Connection con,
                                                 final int maxSession,
                                                 final boolean isTransacted,
                                                 final int ack,
                                                 final MessageListener listener)
      throws JMSException
   {
      ServerSessionPool pool = (ServerSessionPool)
         new StdServerSessionPool(con,
                                  isTransacted,
                                  ack,
                                  listener,
                                  maxSession);
      return pool;
   }
}
