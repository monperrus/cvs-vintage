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
package org.columba.addressbook.facade;

import org.columba.addressbook.folder.AbstractFolder;
import org.columba.addressbook.gui.autocomplete.AddressCollector;
import org.columba.addressbook.gui.autocomplete.IAddressCollector;
import org.columba.addressbook.gui.tree.AddressbookTreeModel;
import org.columba.addressbook.model.Contact;
import org.columba.addressbook.model.ContactItem;
import org.columba.addressbook.model.GroupItem;
import org.columba.addressbook.model.HeaderItem;
import org.columba.addressbook.model.HeaderItemList;
import org.columba.addressbook.model.IContact;
import org.columba.addressbook.model.IContactItem;
import org.columba.addressbook.model.IGroupItem;
import org.columba.addressbook.model.IHeaderItem;
import org.columba.addressbook.model.IHeaderItemList;
import org.columba.addressbook.model.VCARD;
import org.columba.core.main.Main;
import org.columba.mail.message.HeaderList;
import org.columba.mail.message.IHeaderList;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.parser.ParserException;

/**
 * Provides contact management helper methods.
 * 
 * @author fdietz   
 */
public final class ContactFacade implements IContactFacade{

	
	/**
	 * Add contact to selected addressbook.
	 * 
	 * @param uid		selected addressbook	
	 * @param address	email address
	 */
	public void addContact(int uid, String address) {
		if (address == null) {
            return;
        }
		
		if (address.length() == 0)
			return;

		Address adr;
		try {
			adr = Address.parse(address);
		} catch (ParserException e1) {
			if ( Main.DEBUG)
			e1.printStackTrace();
			return;
		}
		
		System.out.println("address:" + address);

		AbstractFolder selectedFolder = (AbstractFolder) AddressbookTreeModel.getInstance()
				.getFolder(uid);
		try {
			if (selectedFolder.exists(adr.getMailAddress()) == null) {
				IContact card = new Contact();

				String fn = adr.getShortAddress();
				
				card.set(VCARD.FN, fn);
				card.set(VCARD.DISPLAYNAME, fn);
				card.set(VCARD.EMAIL, VCARD.EMAIL_TYPE_INTERNET, adr.getMailAddress());
				card.fillFullName(fn);
				selectedFolder.add(card);

			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
	/**
	 * Add contact to "Collected Addresses" addressbook
	 * 
	 * @param address		email address
	 */
	public void addContactToCollectedAddresses(String address) {
		addContact(102, address);
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#getAddressCollector()
	 */
	public IAddressCollector getAddressCollector() {
		return AddressCollector.getInstance();
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#createHeaderItemList()
	 */
	public IHeaderItemList createHeaderItemList() {
		return new HeaderItemList();
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#createHeaderItem()
	 */
	public IHeaderItem createHeaderItem() {
		return new HeaderItem();
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#createContact()
	 */
	public IContact createContact() {
		return new Contact();
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#createHeaderList()
	 */
	public IHeaderList createHeaderList() {
		return new HeaderList();
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#createContactItem()
	 */
	public IContactItem createContactItem() {
		return new ContactItem();
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#createGroupItem()
	 */
	public IGroupItem createGroupItem() {
		return new GroupItem();
	}

}