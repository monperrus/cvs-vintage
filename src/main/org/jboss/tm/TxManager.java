/*
 * jBoss, the OpenSource EJB server
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.tm;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.Transaction;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;

import org.jboss.logging.Logger;

/**
 *	<description> 
 *      
 *	@see <related>
 *	@author Rickard �berg (rickard.oberg@telkel.com)
 *	@version $Revision: 1.1 $
 */
public class TxManager
   implements TransactionManager // , TransactionManagerRemote
{
   // Constants -----------------------------------------------------
    
   // Attributes ----------------------------------------------------
   ThreadLocal tx = new ThreadLocal();
   Transaction noTx = new TransactionImpl(null);
   
   int timeOut = 60*1000; // Timeout in milliseconds
   
   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   public void begin()
      throws NotSupportedException,SystemException
   {
//      System.out.println("begin tx");
      tx.set(new TransactionImpl(this));
   }
   
   public void commit()
            throws RollbackException,
                   HeuristicMixedException,
                   HeuristicRollbackException,
                   java.lang.SecurityException,
                   java.lang.IllegalStateException,
                   SystemException
   {
//      System.out.println("commit tx");
      getTransaction().commit();
   }
   
   public int getStatus()
              throws SystemException
   {
      Transaction current = getTransaction();
      if (current == null)
         return Status.STATUS_NO_TRANSACTION;
      else
         return getTransaction().getStatus();
   }
   
   public Transaction getTransaction()
                           throws SystemException
   {
      Transaction current = (Transaction)tx.get();
      
//DEBUG      Logger.debug("Current="+current);
      
      if (current == null)
         return noTx;
      else
         return current;
   }

   public void resume(Transaction tobj)
            throws InvalidTransactionException,
                   java.lang.IllegalStateException,
                   SystemException
   {
//DEBUG      Logger.debug("resume tx");
      tx.set(tobj);
   }
                   
   public void rollback()
              throws java.lang.IllegalStateException,
                     java.lang.SecurityException,
                     SystemException
   {
//      System.out.println("rollback tx");
      getTransaction().rollback();
   }
   
   public void setRollbackOnly()
                     throws java.lang.IllegalStateException,
                            SystemException
   {
//      System.out.println("set rollback only tx");
      getTransaction().setRollbackOnly();
   }
   
   public void setTransactionTimeout(int seconds)
                           throws SystemException
   {
      timeOut = seconds;
   }
   
   public Transaction suspend()
                    throws SystemException
   {
//      System.out.println("suspend tx");
      
      Transaction current = (Transaction)tx.get();
      tx.set(null);
      
      return current;
   }
   
   // Package protected ---------------------------------------------
   void removeTransaction()
   {
      tx.set(null);
   }
   
   int getTransactionTimeout()
   {
      return timeOut;
   }
    
   // Protected -----------------------------------------------------
    
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
