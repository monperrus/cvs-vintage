//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the 
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.
package org.columba.mail.pop3;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.columba.core.logging.ColumbaLogger;
import org.columba.mail.config.AccountItem;
import org.columba.mail.config.AccountList;
import org.columba.mail.config.MailConfig;
import org.columba.mail.config.PopItem;

public class POP3ServerCollection //implements ActionListener
{
	private List serverList;
	private POP3Server popServer;
	private List listeners;

	public POP3ServerCollection() {
		serverList= new Vector();
		listeners= new Vector();

		AccountList list= MailConfig.getAccountList();

		for (int i= 0; i < list.count(); i++) {
			AccountItem accountItem= list.get(i);

			if (accountItem.isPopAccount()) {
				add(accountItem);
			}
		}
	}

	public ListIterator getServerIterator() {
		return serverList.listIterator();
	}

	public POP3Server[] getList() {
		POP3Server[] list= new POP3Server[count()];

		((Vector) serverList).copyInto(list);

		return list;
	}

	public void add(AccountItem item) {
		POP3Server server= new POP3Server(item);
		serverList.add(server);

		/*
		notifyListeners(new ModelChangedEvent(ModelChangedEvent.ADDED, server));
		*/
	}

	public POP3Server uidGet(int uid) {
		int index= getIndex(uid);

		if (index != -1) {
			return get(index);
		} else {
			return null;
		}
	}

	public POP3Server get(int index) {
		return (POP3Server) serverList.get(index);
	}

	public int count() {
		return serverList.size();
	}

	public void removePopServer(int uid) {
		int index= getIndex(uid);
		POP3Server server;

		if (index == -1) {
			ColumbaLogger.log.severe("could not find popserver");

			return;
		} else {
			server= (POP3Server) serverList.remove(index);
		}

		/*
		notifyListeners(new ModelChangedEvent(ModelChangedEvent.REMOVED));
		*/
	}

	public int getIndex(int uid) {
		POP3Server c;
		int number;
		PopItem item;

		for (int i= 0; i < count(); i++) {
			c= get(i);
			number= c.getAccountItem().getUid();

			if (number == uid) {
				return i;
			}
		}

		return -1;
	}

	public void saveAll() {
		POP3Server c;

		for (int i= 0; i < count(); i++) {
			c= get(i);

			try {
				c.save();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public POP3Server getSelected() {
		return popServer;
	}

	/*
	public void addModelListener(ModelChangeListener l) {
	    listeners.add(l);
	}
	
	private void notifyListeners(ModelChangedEvent e) {
	    for (Iterator it = listeners.iterator(); it.hasNext();) {
	        ((ModelChangeListener) it.next()).modelChanged(e);
	
	        // for (int i = 0; i < listeners.size(); i++) {
	        // ((ModelChangeListener) listeners.get(i)).modelChanged(e);
	    }
	}
	*/
}
