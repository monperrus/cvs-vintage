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
package org.columba.mail.gui.table.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.columba.core.gui.selection.SelectionChangedEvent;
import org.columba.core.gui.selection.SelectionListener;
import org.columba.core.logging.ColumbaLogger;
import org.columba.core.main.MainInterface;
import org.columba.core.xml.XmlElement;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.config.MailConfig;
import org.columba.mail.folder.command.MarkMessageCommand;
import org.columba.mail.gui.table.TableController;
import org.columba.mail.gui.table.selection.TableSelectionChangedEvent;

/**
 * Title:
 * Description: The MarkAsReadTimer marks a Message as read after a user defined
 * time. This class self implements a actionListener. The class as a ActionListener is
 * added to his own timer as ActionListener. So if the timer is started an then finished
 * the timer calls the actionPerfomred Method of this class to do the marking thinks.
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author fdietz, waffel
 * @version 1.0
 */

public class MarkAsReadTimer implements ActionListener, SelectionListener {
	// timer to use
	private Timer timer;
	// definition of a second
	private final static int ONE_SECOND = 1000;

	private int value;
	private int maxValue;

	// reference to the message that should be marked
	private FolderCommandReference message;
	// the tableController is not used
	private TableController tableController;

	/**
	* Creates a new MarkAsReadTimer. This should be only onced in a Session. The contructor
	* fetched the time delay that the user configured.
	*/
	public MarkAsReadTimer(TableController tableController) {

		this.tableController = tableController;
		XmlElement markasread =
			MailConfig.get("options").getElement("/options/markasread");

		String delay = markasread.getAttribute("delay", "2");
		this.maxValue = Integer.parseInt(delay);

		timer = new Timer(ONE_SECOND * maxValue, this);

	}

	/**
	* currently not used
	*/
	public void setMaxValue(int i) {
		maxValue = i;

		timer = new Timer(ONE_SECOND * maxValue, this);
	}

	/**
	* Stops the timer.
	*/
	public synchronized void stopTimer() {
		value = 0;

		ColumbaLogger.log.debug("MarkAsRead-timer stopped");

		timer.stop();
	}

	/**
	* Restarts the timer. The given message is used later in the actionPerfomed mathod.
	* This method is for example used by the ViewMessageCommand to restart the timer if a
	* message is shown
	*/
	public synchronized void restart(FolderCommandReference reference) {

		ColumbaLogger.log.debug("MarkAsRead-timer started");

		message = reference;
		value = 0;
		timer.restart();
	}

	/**
	* Stops the timer. Then the message (currently setting is made in restart) is marked as
	* read. The MarkMessageCommand is called.
	*/
	public void actionPerformed(ActionEvent e) {

		ColumbaLogger.log.debug("action perfomed");
		timer.stop();

		FolderCommandReference[] r = new FolderCommandReference[] { message };

		r[0].setMarkVariant(MarkMessageCommand.MARK_AS_READ);

		MarkMessageCommand c = new MarkMessageCommand(r);

		MainInterface.processor.addOp(c);

		value++;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.util.SelectionListener#selectionChanged(org.columba.core.gui.util.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent e) {
		// if a selection is changed we stopping the MarkAsReadTimer
		Object uids[] = ((TableSelectionChangedEvent) e).getUids();
		if (uids.length > 0) {
			stopTimer();
		}
		

	}

}