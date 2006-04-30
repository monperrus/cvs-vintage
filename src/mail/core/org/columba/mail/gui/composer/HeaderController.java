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
package org.columba.mail.gui.composer;

import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.columba.addressbook.facade.IContactFacade;
import org.columba.addressbook.facade.IModelFacade;
import org.columba.addressbook.folder.StoreException;
import org.columba.addressbook.model.IHeaderItem;
import org.columba.addressbook.model.IHeaderItemList;
import org.columba.api.exception.ServiceNotFoundException;
import org.columba.mail.connector.ServiceConnector;
import org.columba.mail.gui.composer.util.AddressCollector;
import org.columba.mail.parser.ListBuilder;
import org.columba.mail.parser.ListParser;
import org.columba.mail.util.MailResourceLoader;

/**
 * Recipients editor component.
 * 
 * @author fdietz
 */
public class HeaderController {

	private ComposerController controller;

	private HeaderView view;

	private AddressCollector addressCollector;

	public HeaderController(ComposerController controller) {
		this.controller = controller;

		view = new HeaderView(this);

		addressCollector = AddressCollector.getInstance();

		if (addressCollector != null) {
			// clear autocomplete hashmap
			addressCollector.clear();

			try {
				IContactFacade facade = ServiceConnector.getContactFacade();

				// fill hashmap with all available contacts and groups
				try {

					// personal addressbook 
					List<IHeaderItem> list = facade.getAllHeaderItems("101");
					addressCollector.addAllContacts(list, true);

					// collected addresses
					List<IHeaderItem> list2 = facade.getAllHeaderItems("102");
					addressCollector.addAllContacts(list2, true);
				} catch (StoreException e) {
					e.printStackTrace();
				}

			} catch (ServiceNotFoundException e) {
			}

		}

		view.initAutocompletion();
	}

	public ComposerController getComposerController() {
		return controller;
	}

	public HeaderView getView() {
		return view;
	}

	public boolean checkState() {

		Iterator it = getHeaderItemList(0).iterator();

		while (it.hasNext()) {
			IHeaderItem item = (IHeaderItem) it.next();
			if (isValid(item))
				return true;
		}

		JOptionPane.showMessageDialog(null, MailResourceLoader.getString(
				"menu", "mainframe", "composer_no_recipients_found"));

		return false;
	}

	protected boolean isValid(IHeaderItem headerItem) {
		if (headerItem.isContact()) {
			/*
			 * String address = (String) headerItem.get("email;internet");
			 * 
			 * if (AddressParser.isValid(address)) { return true; }
			 * 
			 * address = (String) headerItem.get("displayname");
			 * 
			 * if (AddressParser.isValid(address)) { return true; }
			 */
			return true;
		} else {
			return true;
		}

	}

	public void installListener() {
		// view.table.getModel().addTableModelListener(this);
	}

	public void updateComponents(boolean b) {
		if (b) {

			String s = ListParser.createStringFromList(controller.getModel()
					.getToList());
			getView().getToComboBox().setText(s);

			s = ListParser.createStringFromList(controller.getModel()
					.getCcList());
			getView().getCcComboBox().setText(s);

			s = ListParser.createStringFromList(controller.getModel()
					.getBccList());
			getView().getBccComboBox().setText(s);

		} else {

			String s = getView().getToComboBox().getText();
			List list = ListParser.createListFromString(s);
			controller.getModel().setToList(list);

			s = getView().getCcComboBox().getText();
			list = ListParser.createListFromString(s);
			controller.getModel().setCcList(list);

			s = getView().getBccComboBox().getText();
			list = ListParser.createListFromString(s);
			controller.getModel().setBccList(list);

		}
	}

	private IHeaderItemList getHeaderItemList(int recipient) {

		IHeaderItemList list = null;
		try {
			IModelFacade c = ServiceConnector.getModelFacade();
			list = c.createHeaderItemList();
		} catch (ServiceNotFoundException e1) {
			e1.printStackTrace();
		}

		String header = null;
		String str = null;
		switch (recipient) {
		case 0:
			str = getView().getToComboBox().getText();
			header = "To";
			break;
		case 1:
			str = getView().getCcComboBox().getText();
			header = "Cc";
			break;
		case 2:
			str = getView().getBccComboBox().getText();
			header = "Bcc";
			break;

		}

		List l = ListParser.createListFromString(str);
		if (l == null)
			return list;

		Iterator it = l.iterator();

		while (it.hasNext()) {
			String s = (String) it.next();
			// skip empty strings
			if (s.length() == 0)
				continue;

			IHeaderItem item = null;
			if (addressCollector != null)
				item = addressCollector.getHeaderItem(s);
			if (item == null) {

				try {
					IModelFacade c = ServiceConnector.getModelFacade();
					item = c.createContactItem();
					item.setDisplayName(s);
					item.setHeader(header);
				} catch (ServiceNotFoundException e) {

					e.printStackTrace();
				}

			} else {
				item.setHeader(header);
			}

			list.add(item);
		}

		return list;
	}

	public IHeaderItemList[] getHeaderItemLists() {
		IHeaderItemList[] lists = new IHeaderItemList[3];
		lists[0] = getHeaderItemList(0);
		lists[1] = getHeaderItemList(1);
		lists[2] = getHeaderItemList(2);

		return lists;
	}

	public void setHeaderItemLists(IHeaderItemList[] lists) {
		((ComposerModel) controller.getModel()).setToList(ListBuilder
				.createStringListFromItemList(lists[0]));

		((ComposerModel) controller.getModel()).setCcList(ListBuilder
				.createStringListFromItemList(lists[1]));

		((ComposerModel) controller.getModel()).setBccList(ListBuilder
				.createStringListFromItemList(lists[2]));

		updateComponents(true);
	}

}