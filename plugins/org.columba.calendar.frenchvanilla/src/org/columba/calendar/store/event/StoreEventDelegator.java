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
package org.columba.calendar.store.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.columba.calendar.CalendarComponent;
import org.columba.calendar.config.Config;
import org.columba.calendar.model.ICalendarModel;
import org.columba.calendar.store.ICalendarStore;
import org.columba.calendar.ui.calendar.CalendarHelper;
import org.columba.core.base.Mutex;
import org.columba.core.util.InternalException;

import com.miginfocom.calendar.activity.Activity;
import com.miginfocom.calendar.activity.ActivityDepository;

public class StoreEventDelegator implements StoreListener, ActionListener {

	/** JDK 1.4+ logging framework logger, used for logging. */
	private static final Logger LOG = Logger
			.getLogger("org.columba.calendar.store.event");

	private static final int UPDATE_DELAY = 50;

	private static StoreEventDelegator instance;

	private Timer timer;

	private Mutex mutex;

	private int swap = 0;

	private List[] itemRemovedList;

	private List[] itemChangedList;

	private List[] itemAddedList;

	public StoreEventDelegator() {
		super();

		itemRemovedList = new List[] { new ArrayList(500), new ArrayList(500) };
		itemChangedList = new List[] { new ArrayList(500), new ArrayList(500) };
		itemAddedList = new List[] { new ArrayList(500), new ArrayList(500) };

		mutex = new Mutex();

		timer = new Timer(UPDATE_DELAY, this);
		timer.start();

	}

	public static StoreEventDelegator getInstance() {
		if (instance == null)
			instance = new StoreEventDelegator();

		return instance;
	}

	private void clearAllLists() {
		itemAddedList[swap].clear();
		itemRemovedList[swap].clear();
		itemChangedList[swap].clear();
	}

	public void processCalendarEvents() {

		if (itemAddedList[swap].size() > 0) {
			LOG.info("process item added calendar events");

			Collections.sort(itemAddedList[swap], StoreEventComparator
					.getInstance());

			// Process the events
			for (int i = 0; i < itemAddedList[swap].size(); i++) {
				StoreEvent next = (StoreEvent) itemAddedList[swap].get(i);

				ICalendarStore store = (ICalendarStore) next.getSource();
				try {
					ICalendarModel model = store.get(next.getChanges());
					Activity act = CalendarHelper.createEvent(model);

					act
							.setCategoryIDs(new Object[] { Config.PERSONAL_NODE_ID });
					ActivityDepository.getInstance().addBrokedActivity(act,
							CalendarComponent.class);

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		if (itemRemovedList[swap].size() > 0) {
			Collections.sort(itemRemovedList[swap], StoreEventComparator
					.getInstance());

			ICalendarStore lastFolder = (ICalendarStore) ((StoreEvent) itemRemovedList[swap]
					.get(0)).getSource();

			// Process the events
			for (int i = 1; i < itemRemovedList[swap].size(); i++) {
				StoreEvent next = (StoreEvent) itemRemovedList[swap].get(i);
				ICalendarStore store = (ICalendarStore) next.getSource();
				// TODO item removed process event
				// 

			}
		}
		if (itemChangedList[swap].size() > 0) {
			Collections.sort(itemChangedList[swap], StoreEventComparator
					.getInstance());

			ICalendarStore lastFolder = (ICalendarStore) ((StoreEvent) itemChangedList[swap]
					.get(0)).getSource();

			// Process the events
			for (int i = 1; i < itemChangedList[swap].size(); i++) {
				StoreEvent next = (StoreEvent) itemChangedList[swap].get(i);

				ICalendarStore store = (ICalendarStore) next.getSource();
				// TODO item changed process event
				// 

			}
		}
	}

	public void itemAdded(StoreEvent e) {
		LOG.info(e.toString());

		mutex.lock();

		itemAddedList[1 - swap].add(e);

		mutex.release();

	}

	public void itemRemoved(StoreEvent e) {
		LOG.info(e.toString());

		mutex.lock();

		itemRemovedList[1 - swap].add(e);

		mutex.release();
	}

	public void itemChanged(StoreEvent e) {
		LOG.info(e.toString());

		mutex.lock();

		itemChangedList[1 - swap].add(e);

		mutex.release();
	}

	public void actionPerformed(ActionEvent e) {

		// process all events collected until now
		mutex.lock();

		swap = 1 - swap;

		mutex.release();

		processCalendarEvents();

		clearAllLists();
	}

}
