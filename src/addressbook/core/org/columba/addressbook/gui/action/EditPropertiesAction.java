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

package org.columba.addressbook.gui.action;

import java.awt.event.ActionEvent;

import org.columba.addressbook.folder.AddressbookFolder;
import org.columba.addressbook.folder.ContactCard;
import org.columba.addressbook.folder.GroupListCard;
import org.columba.addressbook.folder.HeaderItem;
import org.columba.addressbook.folder.HeaderItemList;
import org.columba.addressbook.gui.EditGroupDialog;
import org.columba.addressbook.gui.dialog.contact.ContactDialog;
import org.columba.addressbook.gui.frame.AddressbookFrameController;
import org.columba.addressbook.util.AddressbookResourceLoader;
import org.columba.core.action.FrameAction;
import org.columba.core.gui.frame.FrameMediator;
import org.columba.core.gui.util.ImageLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EditPropertiesAction extends FrameAction {

	public EditPropertiesAction(FrameMediator frameController) {
		super(
				frameController,
				AddressbookResourceLoader.getString(
					"menu", "mainframe", "menu_file_properties"));
					
		// tooltip text
		setTooltipText(
				AddressbookResourceLoader.getString(
					"menu", "mainframe", "menu_file_properties_tooltip"));
					
		setToolBarText(
						AddressbookResourceLoader.getString(
							"menu", "mainframe", "menu_file_properties_toolbar"));
		enableToolBarText(true);
					
		// action command
		setActionCommand("PROPERTIES");
		
		// icons
		setSmallIcon(ImageLoader.getSmallImageIcon("stock_edit-16.png"));
		setLargeIcon(ImageLoader.getImageIcon("stock_edit.png"));

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {

		AddressbookFrameController addressbookFrameController =
			(AddressbookFrameController) frameMediator;

		Object uid =
			addressbookFrameController.getTable().getView().getSelectedUid();
		if (uid == null)
			return;
		HeaderItem item =
			addressbookFrameController.getTable().getView().getSelectedItem();
		/*
		AddressbookXmlConfig config =
			AddressbookConfig.getAddressbookConfig();
		*/
		AddressbookFolder folder =
			(AddressbookFolder) addressbookFrameController
				.getTree()
				.getView()
				.getSelectedFolder();

		if (item.isContact()) {
			ContactCard card = (ContactCard) folder.get(uid);
			System.out.println("card:" + card);

			ContactDialog dialog =
				new ContactDialog(addressbookFrameController.getView());

			dialog.updateComponents(card, true);
			dialog.setVisible(true);

			if (dialog.getResult()) {
				System.out.println("saving contact");

				// Ok

				dialog.updateComponents(card, false);
				folder.modify(card, uid);

				addressbookFrameController.getTable().getView().setFolder(
					folder);
			}
		} else {
			GroupListCard card = (GroupListCard) folder.get(uid);

			EditGroupDialog dialog =
				new EditGroupDialog(
					addressbookFrameController.getView(),
					addressbookFrameController,
					null);

			dialog.setHeaderList(folder.getHeaderItemList());
			Object[] uids = card.getUids();
			HeaderItemList members = folder.getHeaderItemList(uids);
			dialog.updateComponents(card, members, true);

			dialog.setVisible(true);

			if (dialog.getResult()) {
				dialog.updateComponents(card, null, false);
				folder.modify(card, uid);
				addressbookFrameController.getTable().getView().setFolder(
					folder);
			}
		}
	}

}
