/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
 
package org.jboss.util;

import javax.naming.InitialContext;

/**
 * AutoNumberFactory can persistently auto number items. 
 *
 * @author <a href="mailto:michel.anke@wolmail.nl">Michel de Groot</a>
 * @version $Revision: 1.2 $
 */
public class AutoNumberFactory {
	private static AutoNumberHome autoNumberHome;
	
	/**
	 * Gets the next key for the given collection.
	 * Note 1: you must deploy EJB AutoNumber
	 * Note 2: the keys are persistent in your database, independent of 
	 * the actual table
	 * Note 3: you can only add instances to the collection which have a 
	 * key generated by this method, otherwise the keys are not guaranteed
	 * to be unique
	 * Note 4: key values are >= 0
	 * @param collectionName the name of the collection for which you want an autonumber
	 * @throws ArrayIndexOutOfBoundsException if no more numbers are available
	 */
	public static Integer getNextInteger(String collectionName) throws ArrayIndexOutOfBoundsException {
		Integer value = null;
		AutoNumber autoNumber = null;
		if (autoNumberHome == null) {
			try {
				autoNumberHome = (AutoNumberHome)new InitialContext().lookup("JBossUtilAutoNumber");
			} catch (javax.naming.NamingException e) {
				e.printStackTrace();
			}
		}
		try {
			autoNumber = (AutoNumber)autoNumberHome.findByPrimaryKey(collectionName);
		} catch (javax.ejb.FinderException e) {
			// autonumber does not exist yet, create one at value 0
			try {
				autoNumber = autoNumberHome.create(collectionName);
			} catch (javax.ejb.CreateException e11) {
				e11.printStackTrace();
			} catch (java.rmi.RemoteException e12) {
				e12.printStackTrace();
			}
			try {
				autoNumber.setValue(new Integer(0));
			} catch (java.rmi.RemoteException e21) {
				e21.printStackTrace();
			}
		} catch (java.rmi.RemoteException e2) {
			e2.printStackTrace();
		}
		try {
			value = autoNumber.getValue();
			autoNumber.setValue(new Integer(value.intValue()+1));
		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
		}
		
		return value;
	}

	/**
	 * Resets the given autonumber to zero.
	 * Use with extreme care!
	 */
	public static void resetAutoNumber(String collectionName) {
		setAutoNumber(collectionName,new Integer(0));
	}
	
	/**
	 * Sets the given autonumber to the given value so that it starts
	 * counting at the given value.
	 * Use with extreme care!
	 */
	public static void setAutoNumber(String collectionName, Integer value) {
		AutoNumber autoNumber = null;
		if (autoNumberHome == null) {
			try {
				autoNumberHome = (AutoNumberHome)new InitialContext().lookup("JBossUtilAutoNumber");
			} catch (javax.naming.NamingException e) {
				e.printStackTrace();
			}
		}
		try {
			autoNumber = (AutoNumber)autoNumberHome.findByPrimaryKey(collectionName);
		} catch (javax.ejb.FinderException e) {
			// autonumber does not exist yet, create one
			try {
				autoNumber = autoNumberHome.create(collectionName);
			} catch (javax.ejb.CreateException e11) {
				e11.printStackTrace();
			} catch (java.rmi.RemoteException e12) {
				e12.printStackTrace();
			}
		} catch (java.rmi.RemoteException e2) {
			e2.printStackTrace();
		}
		try {
			autoNumber.setValue(value);
		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
		}
	}	
}