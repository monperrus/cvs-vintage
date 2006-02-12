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
package org.columba.calendar.ui.action;

import java.awt.event.ActionEvent;

import org.columba.api.gui.frame.IFrameMediator;
import org.columba.calendar.model.VEventModel;
import org.columba.calendar.store.CalendarStoreFactory;
import org.columba.calendar.store.ICalendarStore;
import org.columba.calendar.ui.dialog.EditEventDialog;
import org.columba.calendar.ui.util.ResourceLoader;
import org.columba.core.gui.action.AbstractColumbaAction;
import org.columba.core.util.InternalException;

/**
 * @author fdietz
 * 
 */
public class NewAppointmentAction extends AbstractColumbaAction {

	/**
	 * @param frameMediator
	 * @param name
	 */
	public NewAppointmentAction(IFrameMediator frameMediator) {
		super(frameMediator, "New Appointment");

		putValue(AbstractColumbaAction.TOOLBAR_NAME, "New Appointment");
		setShowToolBarText(true);

		putValue(AbstractColumbaAction.LARGE_ICON, ResourceLoader
				.getImageIcon("new_appointment-32.png"));
		putValue(AbstractColumbaAction.SMALL_ICON, ResourceLoader
				.getImageIcon("new_appointment.png"));
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		VEventModel model = new VEventModel();

		EditEventDialog dialog = new EditEventDialog(null, model);

		if (dialog.success()) {

			ICalendarStore store = CalendarStoreFactory.getInstance()
					.getLocaleStore();

			try {
				store.add(model);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InternalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

}
