// The contents of this file are subject to the Mozilla Public License Version
// 1.1
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
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.mail.folder.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import org.columba.core.util.Mutex;
import org.columba.mail.folder.AbstractFolder;
import org.columba.mail.gui.table.model.TableModelChangedEvent;
import org.columba.mail.gui.table.model.TableModelChangedListener;
import org.columba.mail.main.MailInterface;

/**
 * Collects all folder events generated by AbstractFolder and MessageFolder.
 * <p>
 * Accumulates matching events to one big event. Afterwards it delegates this
 * final event to TreeModel and all TableModel objects.
 * <p>
 * A swing timer is used to trigger ui updates.
 * 
 * @author fdietz
 * @author tstich
 */
public class FolderEventDelegator implements ActionListener, FolderListener {

	private static final int UPDATE_DELAY = 50;

	private static FolderEventDelegator instance;

	private Timer timer;

	private Mutex mutex;

	private List[] messageRemovedList;

	private List[] messageFlagChangedList;

	private List[] messageAddedList;

	private List[] folderAddedList;

	private List[] folderRemovedList;

	private List[] folderPropertyChangedList;

	private int swap = 0;

	protected EventListenerList tableListenerList = new EventListenerList();

	private FolderEventDelegator() {
		super();

		messageRemovedList = new List[] { new ArrayList(500),
				new ArrayList(500) };
		messageFlagChangedList = new List[] { new ArrayList(500),
				new ArrayList(500) };
		messageAddedList = new List[] { new ArrayList(500), new ArrayList(500) };

		folderAddedList = new List[] { new ArrayList(500), new ArrayList(500) };
		folderRemovedList = new List[] { new ArrayList(500), new ArrayList(500) };
		folderPropertyChangedList = new List[] { new ArrayList(500),
				new ArrayList(500) };

		mutex = new Mutex();

		timer = new Timer(UPDATE_DELAY, this);
		timer.start();
	}

	public static FolderEventDelegator getInstance() {
		if (instance == null)
			instance = new FolderEventDelegator();

		return instance;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		// process all events collected until now
		mutex.lock();

		swap = 1 - swap;

		mutex.release();

		processTableEvents();

		processTreeEvents();

		clearAllLists();

	}

	/**
	 *  
	 */
	private void clearAllLists() {
		messageAddedList[swap].clear();
		messageRemovedList[swap].clear();
		messageFlagChangedList[swap].clear();

		folderAddedList[swap].clear();
		folderRemovedList[swap].clear();
		folderPropertyChangedList[swap].clear();
	}

	/**
	 * @param events
	 */
	private void processTableEvents() {

		if (messageAddedList[swap].size() > 0) {

			// First sort so that Events from one folder stick together
			Collections.sort(messageAddedList[swap], FolderEventComparator
					.getInstance());
			AbstractFolder lastFolder = (AbstractFolder) ((FolderEvent) messageAddedList[swap]
					.get(0)).getSource();

			fireTableEvent(new TableModelChangedEvent(
					TableModelChangedEvent.UPDATE, lastFolder));

			// Process the events
			for (int i = 1; i < messageAddedList[swap].size(); i++) {
				FolderEvent next = (FolderEvent) messageAddedList[swap].get(i);
				if (next.getSource() != lastFolder) {
					lastFolder = (AbstractFolder) next.getSource();
					fireTableEvent(new TableModelChangedEvent(
							TableModelChangedEvent.UPDATE, lastFolder));
				}
			}
		}

		if (messageRemovedList[swap].size() > 0) {
			// First sort so that Events from one folder stick together
			Collections.sort(messageRemovedList[swap], FolderEventComparator
					.getInstance());
			FolderEvent event = (FolderEvent) messageRemovedList[swap].get(0);
			AbstractFolder lastFolder = (AbstractFolder) event.getSource();

			// Collect the uids for one folder
			List collectedUids = new ArrayList();
			collectedUids.add(event.getChanges());

			// Process the events
			for (int i = 1; i < messageRemovedList[swap].size(); i++) {
				FolderEvent next = (FolderEvent) messageRemovedList[swap]
						.get(i);
				if (next.getSource() != lastFolder) {
					// new folder, fire to the old folder
					fireTableEvent(new TableModelChangedEvent(
							TableModelChangedEvent.REMOVE, lastFolder,
							collectedUids.toArray()));

					// clear list and collect uids for new folder
					collectedUids.clear();
					collectedUids.add(next.getChanges());

					lastFolder = (AbstractFolder) next.getSource();
				} else {
					collectedUids.add(next.getChanges());
				}
			}

			fireTableEvent(new TableModelChangedEvent(
					TableModelChangedEvent.REMOVE, lastFolder, collectedUids
							.toArray()));
		}

		if (messageFlagChangedList[swap].size() > 0) {

			// First sort so that Events from one folder stick together
			Collections.sort(messageFlagChangedList[swap],
					FolderEventComparator.getInstance());
			FolderEvent event = (FolderEvent) messageFlagChangedList[swap]
					.get(0);
			AbstractFolder lastFolder = (AbstractFolder) event.getSource();

			// Collect the uids for one folder
			List collectedUids = new ArrayList();
			collectedUids.add(event.getChanges());

			// Process the events
			for (int i = 1; i < messageFlagChangedList[swap].size(); i++) {
				FolderEvent next = (FolderEvent) messageFlagChangedList[swap]
						.get(i);
				if (next.getSource() != lastFolder) {
					// new folder, fire to the old folder
					fireTableEvent(new TableModelChangedEvent(
							TableModelChangedEvent.MARK, lastFolder,
							collectedUids.toArray()));

					// clear list and collect uids for new folder
					collectedUids.clear();
					collectedUids.add(next.getChanges());

					lastFolder = (AbstractFolder) next.getSource();
				} else {
					collectedUids.add(next.getChanges());
				}
			}

			fireTableEvent(new TableModelChangedEvent(
					TableModelChangedEvent.MARK, lastFolder, collectedUids
							.toArray()));
		}

	}

	/**
	 * @param events
	 */
	private void processTreeEvents() {

		if (folderAddedList[swap].size() > 0) {

			// First sort so that Events from one folder stick together
			Collections.sort(folderAddedList[swap], FolderEventComparator
					.getInstance());

			AbstractFolder lastFolder = (AbstractFolder) ((FolderEvent) folderAddedList[swap]
					.get(0)).getSource();

			MailInterface.treeModel.nodeStructureChanged(lastFolder);

			// Process the events
			for (int i = 1; i < folderAddedList[swap].size(); i++) {
				FolderEvent next = (FolderEvent) folderAddedList[swap].get(i);
				if (next.getSource() != lastFolder) {
					lastFolder = (AbstractFolder) next.getSource();
					MailInterface.treeModel.nodeStructureChanged(lastFolder);
				}
			}
		}

		if (folderPropertyChangedList[swap].size() > 0) {

			// First sort so that Events from one folder stick together
			Collections.sort(folderPropertyChangedList[swap],
					FolderEventComparator.getInstance());

			AbstractFolder lastFolder = (AbstractFolder) ((FolderEvent) folderPropertyChangedList[swap]
					.get(0)).getSource();

			MailInterface.treeModel.nodeChanged(lastFolder);

			// Process the events
			for (int i = 1; i < folderPropertyChangedList[swap].size(); i++) {
				FolderEvent next = (FolderEvent) folderPropertyChangedList[swap]
						.get(i);
				if (next.getSource() != lastFolder) {
					lastFolder = (AbstractFolder) next.getSource();
					MailInterface.treeModel.nodeChanged(lastFolder);
				}
			}
		}

		if (folderRemovedList[swap].size() > 0) {
			AbstractFolder lastFolder = null;

			// Process the events
			for (int i = 0; i < folderRemovedList[swap].size(); i++) {
				FolderEvent next = (FolderEvent) folderRemovedList[swap].get(i);

				lastFolder = (AbstractFolder) next.getSource();
				MailInterface.treeModel.removeNodeFromParent(lastFolder);

			}
		}

	}

	/** ********************* Folder Listener *************************** */
	/**
	 * @see org.columba.mail.folder.event.FolderListener#folderAdded(org.columba.mail.folder.FolderEvent)
	 */
	public void folderAdded(FolderEvent e) {
		mutex.lock();

		Iterator it = folderAddedList[1 - swap].iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			FolderEvent event = (FolderEvent) it.next();
			found = (event.getSource() == e.getSource());
		}

		if (!found)
			folderAddedList[1 - swap].add(e);

		mutex.release();
	}

	/**
	 * @see org.columba.mail.folder.event.FolderListener#folderRemoved(org.columba.mail.folder.FolderEvent)
	 */
	public void folderRemoved(FolderEvent e) {
		mutex.lock();

		Iterator it = folderRemovedList[1 - swap].iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			FolderEvent event = (FolderEvent) it.next();
			found = (event.getSource() == e.getSource());
		}

		if (!found)
			folderRemovedList[1 - swap].add(e);

		mutex.release();
	}

	/**
	 * @see org.columba.mail.folder.event.FolderListener#folderPropertyChanged(org.columba.mail.folder.FolderEvent)
	 */
	public void folderPropertyChanged(FolderEvent e) {
		mutex.lock();

		Iterator it = folderPropertyChangedList[1 - swap].iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			FolderEvent event = (FolderEvent) it.next();
			found = (event.getSource() == e.getSource());
		}

		if (!found)
			folderPropertyChangedList[1 - swap].add(e);

		mutex.release();

	}

	/**
	 * @see org.columba.mail.folder.event.FolderListener#messageAdded(org.columba.mail.folder.FolderEvent)
	 */
	public void messageAdded(FolderEvent e) {
		mutex.lock();

		Iterator it = messageAddedList[1 - swap].iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			FolderEvent event = (FolderEvent) it.next();
			found = (event.getSource() == e.getSource());
		}

		if (!found)
			messageAddedList[1 - swap].add(e);

		mutex.release();
	}

	/**
	 * @see org.columba.mail.folder.event.FolderListener#messageFlagChanged(org.columba.mail.folder.FolderEvent)
	 */
	public void messageFlagChanged(FolderEvent e) {
		mutex.lock();

		messageFlagChangedList[1 - swap].add(e);

		mutex.release();

	}

	/**
	 * @see org.columba.mail.folder.event.FolderListener#messageRemoved(org.columba.mail.folder.FolderEvent)
	 */
	public void messageRemoved(FolderEvent e) {
		mutex.lock();

		messageRemovedList[1 - swap].add(e);

		mutex.release();
	}

	/** ********************* Table Listener **************************** */

	/**
	 * Adds a listener.
	 */
	public void addTableListener(TableModelChangedListener l) {
		tableListenerList.add(TableModelChangedListener.class, l);
	}

	/**
	 * Removes a previously registered listener.
	 */
	public void removeTableListener(TableModelChangedListener l) {
		tableListenerList.remove(TableModelChangedListener.class, l);
	}

	private void fireTableEvent(TableModelChangedEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = tableListenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableModelChangedListener.class) {
				TableModelChangedListener listener = (TableModelChangedListener) listeners[i + 1];
				if (listener.isInterestedIn(e.getSrcFolder())) {
					listener.tableChanged(e);
				}
			}
		}
	}

}

class FolderEventComparator implements Comparator {

	private static FolderEventComparator instance = new FolderEventComparator();

	private FolderEventComparator() {
	}

	public static FolderEventComparator getInstance() {
		return instance;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		FolderEvent a = (FolderEvent) arg0;
		FolderEvent b = (FolderEvent) arg1;

		return (a.getSource() != b.getSource()) ? 0 : 1;
	}

}