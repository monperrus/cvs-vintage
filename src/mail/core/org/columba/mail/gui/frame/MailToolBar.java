// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package org.columba.mail.gui.frame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.columba.core.gui.util.CButton;

public class MailToolBar extends JToolBar {

	ResourceBundle toolbarLabels;
	GridBagConstraints gridbagConstraints;
	GridBagLayout gridbagLayout;
	int i;

	MailFrameController frame;

	public MailToolBar(MailFrameController f) {
		super();
		this.frame = f;

		addCButtons();
		putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		setFloatable(false);
	}

	public void addButton(CButton button) {

		button.setRolloverEnabled(true);

		add(button);

	}

	public void addCButtons() {

		MouseAdapter handler = frame.getMouseTooltipHandler();
		CButton button;

		i = 0;

		button = new CButton(frame.globalActionCollection.newMessageAction);

		addButton(button);

		button = new CButton(frame.globalActionCollection.receiveSendAction);

		addButton(button);

		addSeparator();

		button =
			new CButton(frame.tableController.getActionListener().replyAction);
		addButton(button);

		button =
			new CButton(
				frame.tableController.getActionListener().forwardAction);
		addButton(button);

		addSeparator();

		button =
			new CButton(
				frame.tableController.getActionListener().copyMessageAction);
		addButton(button);

		button =
			new CButton(
				frame.tableController.getActionListener().moveMessageAction);
		addButton(button);

		button =
			new CButton(
				frame.tableController.getActionListener().deleteMessageAction);
		addButton(button);

		addSeparator();

		button = new CButton(frame.getStatusBar().getCancelAction());

		addButton(button);

		add(Box.createHorizontalGlue());

		add(frame.getStatusBar().getImageSequenceTimer());

	}

}
