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

package org.columba.mail.gui.tree.action;

import java.awt.event.ActionEvent;

import org.columba.core.action.FrameAction;
import org.columba.core.gui.frame.FrameMediator;
import org.columba.core.gui.selection.SelectionChangedEvent;
import org.columba.core.gui.selection.SelectionListener;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.config.FolderItem;
import org.columba.mail.folder.Folder;
import org.columba.mail.gui.frame.AbstractMailFrameController;
import org.columba.mail.gui.tree.selection.TreeSelectionChangedEvent;
import org.columba.mail.util.MailResourceLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FilterPreferencesAction
	extends FrameAction
	implements SelectionListener {

	public FilterPreferencesAction(FrameMediator frameController) {
		super(
				frameController,
				MailResourceLoader.getString(
					"menu", "mainframe", "menu_folder_filterconfig"));
		
		// tooltip text
		setTooltipText(
				MailResourceLoader.getString(
					"menu", "mainframe", "menu_folder_filterconfig"));
		
		// action command
		setActionCommand("FILTER_PREFERENCES");
		
		setEnabled(false);
		((AbstractMailFrameController) frameController).registerTreeSelectionListener(
			this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		FolderCommandReference[] r =
			(FolderCommandReference[]) frameMediator
				.getSelectionManager()
				.getSelection(
				"mail.tree");
		Folder folder = (Folder) r[0].getFolder();

		if (folder == null)
			return;

		FolderItem item = folder.getFolderItem();
		if (item == null)
			return;

		folder.showFilterDialog(((AbstractMailFrameController) getFrameMediator()));
	}
	/* (non-Javadoc)
					 * @see org.columba.core.gui.util.SelectionListener#selectionChanged(org.columba.core.gui.util.SelectionChangedEvent)
					 */
	public void selectionChanged(SelectionChangedEvent e) {

		if (((TreeSelectionChangedEvent) e).getSelected().length > 0)
			setEnabled(true);
		else
			setEnabled(false);

	}
}
