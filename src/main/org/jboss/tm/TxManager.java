/*
* jBoss, the OpenSource EJB server
*
* Distributable under GPL license.
* See terms of license at gnu.org.
*/
package org.jboss.tm;

import java.util.Hashtable;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.XAException;


import org.jboss.logging.Logger;

/**
*	<description>
*
*	@see <related>
*	@author Rickard �berg (rickard.oberg@telkel.com)
*  @author <a href="mailto:marc.fleury@telkel.com">Marc Fleury</a>
*	@version $Revision: 1.17 $
*/
public class TxManager
implements TransactionManager
{
    // Constants -----------------------------------------------------
    
    // Attributes ----------------------------------------------------
    // threadTx keeps track of a thread local association of tx
    ThreadLocal threadTx = new ThreadLocal();
    // transactions maps
    Hashtable txCapsules = new Hashtable();
    
    int timeOut = 60*1000; // Timeout in milliseconds
    
    // Static --------------------------------------------------------
    
    // Constructors --------------------------------------------------
    
    // Public --------------------------------------------------------
    public void begin()
    throws NotSupportedException,
    SystemException
    {
        try {
            Logger.debug("begin tx");
            
            // create tx capsule
            TxCapsule txCap = new TxCapsule(this, timeOut);
            
            // Store it
            txCapsules.put(txCap.getTransaction(), txCap);
            
            // Associate it with the Thread
            threadTx.set(txCap.getTransaction());
        } catch (RuntimeException ex) {
            System.err.println("Exception: " + ex);
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public void commit()
    throws RollbackException,
    HeuristicMixedException,
    HeuristicRollbackException,
    java.lang.SecurityException,
    java.lang.IllegalStateException,
    SystemException
    {   
        getTransaction().commit();
    }
    
    public int getStatus()
    throws SystemException
    {
        // Get the txCapsule running now with the thread
        Object current = threadTx.get();
        if (current != null) {
            TxCapsule txCap = (TxCapsule) txCapsules.get(current);
            
            if (txCap == null)
                return Status.STATUS_NO_TRANSACTION;
            else
                return txCap.getStatus();
        } else {
            return Status.STATUS_NO_TRANSACTION;
        }
    }
    
    public Transaction getTransaction()
    throws SystemException
    {
        return (Transaction)threadTx.get();
    }
    
    public void resume(Transaction tobj)
    throws InvalidTransactionException,
    java.lang.IllegalStateException,
    SystemException
    {
        //Useless
        
        //throw new Exception("txMan.resume() NYI");
    }
    
    
    public Transaction suspend()
    throws SystemException
    {
        //      Logger.debug("suspend tx");
        
        // Useless
        
        return null;
        //throw new Exception("txMan.suspend() NYI");
    }
    
    
    public void rollback()
    throws java.lang.IllegalStateException,
    java.lang.SecurityException,
    SystemException
    { 
    getTransaction().rollback();
    }
    
    public void setRollbackOnly()
    throws java.lang.IllegalStateException,
    SystemException
    {
        //      Logger.debug("set rollback only tx");
        getTransaction().setRollbackOnly();
    }
    
    public void setTransactionTimeout(int seconds)
    throws SystemException
    {
        timeOut = seconds;
    }
    
    /*
    * The following 2 methods are here to provide association and disassociation of the thread
    */
    public Transaction disassociateThread() {
        Transaction current = (Transaction) threadTx.get();
        
        threadTx.set(null);
        
		//DEBUG Logger.debug("DisassociateThread " + ((current==null) ? "null" : Integer.toString(current.hashCode())));
		Logger.debug("disassociateThread " + ((current==null) ? "null" : Integer.toString(current.hashCode())));
        
		return current;
    }
    
    public void associateThread(Transaction transaction) {
        // If the tx has traveled it needs the TxManager
        ((TransactionImpl) transaction).setTxManager(this);
        
        // Associate with the thread
        threadTx.set(transaction);
		
		//DEBUG Logger.debug("DisassociateThread " + ((transaction==null) ? "null" : Integer.toString(transaction.hashCode())));
		Logger.debug("associateThread " + ((transaction==null) ? "null" : Integer.toString(transaction.hashCode())));
        
    }
    
    
    // Package protected ---------------------------------------------
    
    // There has got to be something better :)
    static TxManager getTransactionManager() {
        try {
            
            javax.naming.InitialContext context = new javax.naming.InitialContext();
            
            //One tx in naming
            Logger.debug("Calling get manager from JNDI");
            TxManager manager = (TxManager) context.lookup("TransactionManager");
            Logger.debug("Returning TM "+manager.hashCode());
            
            return manager;
        
        } catch (Exception e ) { return null;}
    }
    
    int getTransactionTimeout()
    {
        return timeOut;
    }
    
    
    // Public --------------------------------------------------------
    
    public void commit(Transaction tx)
    throws RollbackException,
    HeuristicMixedException,
    HeuristicRollbackException,
    java.lang.SecurityException,
    java.lang.IllegalStateException,
    SystemException
    {
         Logger.debug("txManager commit tx "+tx.hashCode());
        try {
            // Look up the txCapsule and delegate
            ((TxCapsule) txCapsules.get(tx)).commit();
        }
        finally {
            // Disassociation
            threadTx.set(null);
            
            //Remove from the internal maps, txCapsule should be GC'ed
            txCapsules.remove(tx);
        }
    }
    
    public boolean delistResource(Transaction tx, XAResource xaRes, int flag)
    throws java.lang.IllegalStateException,
    SystemException
    {
        // Look up the txCapsule and delegate
        return ((TxCapsule) txCapsules.get(tx)).delistResource(xaRes, flag);
    }
    
    public boolean enlistResource(Transaction tx, XAResource xaRes)
    throws RollbackException,
    java.lang.IllegalStateException,
    SystemException
    {
        // Look up the txCapsule and delegate
        return ((TxCapsule) txCapsules.get(tx)).enlistResource(xaRes);
    }
    
    public int getStatus(Transaction tx)
    throws SystemException
    {
        // Look up the txCapsule and delegate
        TxCapsule txCap = ((TxCapsule) txCapsules.get(tx));
        return txCap == null ? Status.STATUS_NO_TRANSACTION : txCap.getStatus();
    }
    
    public void registerSynchronization(Transaction tx, Synchronization s)
    throws RollbackException,
    java.lang.IllegalStateException,
    SystemException
    {
        // Look up the txCapsule and delegate
        ((TxCapsule) txCapsules.get(tx)).registerSynchronization(s);
    }
    
    public void rollback(Transaction tx)
    throws java.lang.IllegalStateException,
    java.lang.SecurityException,
    SystemException
    {
          Logger.debug("rollback tx "+tx.hashCode());
     
        try {
            // Look up the txCapsule and delegate
            ((TxCapsule) txCapsules.get(tx)).rollback();
        }
        finally {
            // Disassociation
            threadTx.set(null);
            
            //Remove from the internal maps, txCapsule should be GC'ed
            txCapsules.remove(tx);
        }
    }
    
    public void setRollbackOnly(Transaction tx)
    throws java.lang.IllegalStateException,
    SystemException
    {
        // Look up the txCapsule and delegate
        ((TxCapsule) txCapsules.get(tx)).setRollbackOnly();
    }
    
    
    
    // Protected -----------------------------------------------------
    
    // Private -------------------------------------------------------
    
    // Inner classes -------------------------------------------------
}
