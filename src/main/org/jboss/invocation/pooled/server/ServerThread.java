/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.invocation.pooled.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.rmi.MarshalledObject;
import org.jboss.invocation.Invocation;
import org.jboss.logging.Logger;
import java.util.LinkedList;
import java.lang.reflect.Method;

/**
 * This Thread object hold a single Socket connection to a client
 * and is kept alive until a timeout happens, or it is aged out of the
 * PooledInvoker's LRU cache.
 *
 * There is also a separate thread pool that is used if the client disconnects.
 * This thread/object is re-used in that scenario and that scenario only.
 *
 * This class will demarshal then delegate to PooledInvoker for invocation.
 *
 *
 * *NOTES* ObjectStreams were found to be better performing than the Custom marshalling
 * done by the TrunkInvoker.
 *
 * @author    <a href="mailto:bill@jboss.org">Bill Burke</a>
 */
public class ServerThread extends Thread
{
   final static private Logger log = Logger.getLogger(ServerThread.class);

   protected boolean running = true;
   protected ObjectInputStream in;
   protected ObjectOutputStream out;
   protected Socket socket;
   protected PooledInvoker invoker;
   protected LRUPool clientpool;
   protected LinkedList threadpool;
   protected boolean handlingResponse = false;
   protected boolean shutdown = false;
   protected static int id = 0;
   protected static Method resetStream;

   static
   {
      Method[] methods = ObjectInputStream.class.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++)
      {
         if (methods[i].getName().equals("resetStream")) 
         {
            resetStream = methods[i];
            break;
         }
      }
      resetStream.setAccessible(true);
   }

   public ServerThread(Socket socket, PooledInvoker invoker, LRUPool clientpool, LinkedList threadpool, int timeout) throws Exception
   {
      super("PooledInvokerThread-" + id++);
      this.socket = socket;
      this.invoker = invoker;
      this.clientpool = clientpool;
      this.threadpool = threadpool;
      socket.setSoTimeout(timeout);
   }

   public void shutdown()
   {
      shutdown = true;
      running = false;
      // This is a race and there is a chance
      // that a invocation is going on at the time
      // of the interrupt.  But I see know way right
      // now to protect for this.

      // NOTE ALSO!:
      // Shutdown should never be synchronized.
      // We don't want to hold up accept() thread! (via LRUpool)
      if (!handlingResponse)
      {
         try
         {
            this.interrupt();
            this.interrupted(); // clear
         }
         catch (Exception ignored) {}
      }
      
   }


   public synchronized void wakeup(Socket socket, int timeout) throws Exception
   {
      //System.out.println("**** reused");
      this.socket = socket;
      socket.setSoTimeout(timeout);
      this.notify();
   }

   public void run()
   {
      try
      {
         while (true)
         {
            dorun();
            //System.out.println("finished....");
            if (shutdown)
            {
               //System.out.println("doing shutdown");
               synchronized (clientpool)
               {
                  clientpool.remove(this);
               }
               return; // exit thread
            }
            else
            {
               //System.out.println("save thread");
               synchronized (this)
               {
                  //System.out.println("synch on client pool");
                  synchronized(clientpool)
                  {
                     //System.out.println("synch on thread pool");
                     synchronized(threadpool)
                     {
                        //System.out.println("removing myself from the pool: " + clientpool.size());
                        clientpool.remove(this);
                        if (clientpool.size() + threadpool.size() < invoker.getMaxPoolSize())
                        {
                           //System.out.println("adding myself to threadpool");
                           threadpool.add(this);
                        }
                     }
                  }
                  this.wait();
               }
            }
         }
      }
      catch (Exception ignored) 
      {
         ignored.printStackTrace();
      }
   }

   protected void acknowledge() throws Exception
   {
      //System.out.println("****acknowledge " + Thread.currentThread());
      // Perform acknowledgement to convince client
      // that the socket is still active
      byte ACK = in.readByte();
      //System.out.println("****acknowledge read byte" + Thread.currentThread());
      handlingResponse = true;
      
      out.writeByte(ACK);
      out.flush();
   }

   protected void processInvocation() throws Exception
   {
      handlingResponse = true;
      // Ok, now read invocation and invoke
      Invocation invocation = (Invocation)in.readObject();
      in.readObject(); // for stupid ObjectInputStream reset
      Object response = null;
      try
      {
         response = invoker.invoke(invocation);
      }
      catch (Exception ex)
      {
         response = ex;
      }
      out.writeObject(response);
      out.reset();
      // to make sure stream gets reset
      // Stupid ObjectInputStream holds object graph
      // can only be set by the client/server sending a TC_RESET
      out.writeObject(Boolean.TRUE); 
      out.flush();
      out.reset();
      handlingResponse = false;
   }

   /**
    * This is needed because Object*Streams leak
    */
   protected void dorun()
   {
      running = true;
      handlingResponse = true;
      try
      {
         out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
         out.flush();
         in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
      }
      catch (Exception e)
      {
         log.error("Failed to initialize", e);
      }

      // Always do first one without an ACK because its not needed
      try
      {
         processInvocation();
      }
      catch (Exception ex)
      {
         running = false;
      }

      //System.out.println("****entering re-use loop");
      // Re-use loop
      while (running)
      {
         try
         {
            acknowledge();
            processInvocation();
         }
         catch (InterruptedIOException iex)
         {
            //System.out.println("exception found!");
            log.debug("socket timed out");
            running = false;
         }
         catch (Exception ex)
         {
            //System.out.println("exception found!");
            //log.error("failed", ex);
            running = false;
         }
      }
      //System.out.println("finished loop:" + Thread.currentThread());
      // Ok, we've been shutdown.  Do appropriate cleanups.
      try
      {
         if (in != null) in.close();
         if (out != null) out.close();
      }
      catch (Exception ex)
      {
      }
      try
      {
         socket.close();
      }
      catch (Exception ex)
      {
         log.error("Failed cleanup", ex);
      }
      socket = null;
      in = null;
      out = null;
   }
}
