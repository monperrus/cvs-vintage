/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.invocation.trunk.client;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import javax.resource.spi.work.WorkManager;
import org.jboss.logging.Logger;

/**
 * The base class for client connections to the server.  This class is sub-classed to provide
 * Blocking and Non Blocking client implemenations.
 * 
 * This class allows you to assign ITrunkListners to receive Invocations from the server.
 * It also keeps track of the when the connection was last used so that a connection can 
 * be closed after a long period of inactivity.
 * 
 * @author    <a href="mailto:hiram.chirino@jboss.org">Hiram Chirino</a>
 */
public abstract class AbstractClient implements ITrunkListner
{
   private final static Logger log = Logger.getLogger(AbstractClient.class);

   /** 
    * The connection manager that is pooling
    * this connection.
    */
   ConnectionManager connectionManager;

   /**
    * Used to determine when a connection should be closed
    * due to inactivity.
    */
   private long lastUsed = System.currentTimeMillis();
   private int keepOpenRequests = 0;
   private boolean isValid = true;

   /**
    * Clients can setup Request listners.
    */
   HashMap requestListners = new HashMap();
   int requestListnerCounter = 0;

   protected WorkManager workManager;

   synchronized public void keepOpen(boolean enabled)
   {
      keepOpenRequests = enabled ? keepOpenRequests + 1 : keepOpenRequests - 1;
   }

   boolean isValid()
   {
      return isValid;
   }

   synchronized void checkExpired(long expirationPeriod)
   {
      if (keepOpenRequests > 0)
         return;
      if ((lastUsed + expirationPeriod) > System.currentTimeMillis())
         return;

      // The connection has expired.. close it up.
      stop();
      isValid = false;
      connectionManager.connectionClosed(this, new Exception("This connection has expired due to inactivity."));
   }

   public void exceptionEvent(ICommTrunk trunk, Exception e)
   {
      stop();
      isValid = false;
      connectionManager.connectionClosed(this, e);
      Iterator i = requestListners.values().iterator();
      while (i.hasNext())
      {
         ITrunkListner rl = (ITrunkListner) i.next();
         rl.exceptionEvent(trunk, e);
      }
   }

   public void requestEvent(ICommTrunk trunk, TunkRequest request)
   {
      connectionManager.handleRequest(this, request);
      lastUsed = System.currentTimeMillis();

      Integer rlID = (Integer) request.invocation.getObjectName();
      if (rlID == null)
      {
         log.debug("ObjectName not set in the Invocation.");
         return;
      }
      ITrunkListner rl = (ITrunkListner) requestListners.get(rlID);
      rl.requestEvent(trunk, request);
   }

   public TrunkResponse synchRequest(TunkRequest request)
      throws IOException, InterruptedException, ClassNotFoundException
   {
      lastUsed = System.currentTimeMillis();
      try
      {
         return getCommTrunk().getCommTrunkRamp().synchRequest(request);
      }
      catch (IOException e)
      {
         exceptionEvent(getCommTrunk(), e);
         throw e;
      }
      catch (InterruptedException e)
      {
         exceptionEvent(getCommTrunk(), e);
         throw e;
      }
      catch (ClassNotFoundException e)
      {
         exceptionEvent(getCommTrunk(), e);
         throw e;
      }
      finally
      {
         lastUsed = System.currentTimeMillis();
      }
   }

   public void sendResponse(TrunkResponse response) throws IOException
   {
      lastUsed = System.currentTimeMillis();
      try
      {
         getCommTrunk().sendResponse(response);
      }
      catch (IOException e)
      {
         exceptionEvent(getCommTrunk(), e);
         throw e;
      }
      finally
      {
         lastUsed = System.currentTimeMillis();
      }
   }

   public Integer addRequestListner(ITrunkListner rl)
   {
      Integer requestListnerID = new Integer(requestListnerCounter++);
      synchronized (requestListners)
      {
         HashMap t = (HashMap) requestListners.clone();
         t.put(requestListnerID, rl);
         requestListners = t;
      }
      return requestListnerID;
   }

   public void removeRequestListner(Integer requestListnerID)
   {
      synchronized (requestListners)
      {
         HashMap t = (HashMap) requestListners.clone();
         t.remove(requestListnerID);
         requestListners = t;
      }
   }

   public ConnectionManager getConnectionManager()
   {
      return connectionManager;
   }

   public void setConnectionManager(ConnectionManager connectionManager)
   {
      this.connectionManager = connectionManager;
   }

   
   
   /**
    * mbean get-set pair for field workManager
    * Get the value of workManager
    * @return value of workManager
    *
    * @jmx:managed-attribute
    */
   public WorkManager getWorkManager()
   {
      return workManager;
   }
   
   
   /**
    * Set the value of workManager
    * @param workManager  Value to assign to workManager
    *
    * @jmx:managed-attribute
    */
   public void setWorkManager(WorkManager workManager)
   {
      this.workManager = workManager;
   }
   
   


   /**
    * Established a connection to the server.
    */
   abstract public void connect(ServerAddress serverAddress, ThreadGroup threadGroup) throws IOException;

   /**
    * Used to start the current connection with the server
    */
   abstract public void start();

   /**
    * Used to stop the current connection with the server
    */
   abstract public void stop();

   /**
    * Used to get the comm trunk that is used by this connection.
    */
   abstract protected ICommTrunk getCommTrunk();

   /**
    * Used to get the comm trunk that is used by this connection.
    */
   abstract public ServerAddress getServerAddress();

}
