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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.columba.addressbook.folder.AbstractFolder;
import org.columba.addressbook.folder.importfilter.DefaultAddressbookImporter;
import org.columba.addressbook.model.Contact;
import org.columba.addressbook.util.AddressbookResourceLoader;

/**
 * 
 * Import a CSV, comma separated list of contacts
 * 
 * @version 1.0
 * @author fdietz
 */
public class CSVAddressbookImporter extends DefaultAddressbookImporter {

	public CSVAddressbookImporter() {
		super();
	}

	public CSVAddressbookImporter(File sourceFile, AbstractFolder destinationFolder) {
		super(sourceFile, destinationFolder);
	}

	public void importAddressbook(File file) throws Exception {

		// open file
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str;

		while ((str = in.readLine()) != null) {
			// start parsing line
			int counter = -1;

			// create new contact card
			Contact card = new Contact();

			StringBuffer token = new StringBuffer();
			int pos = 0;
			while (pos < str.length()) {
				char ch = str.charAt(pos);

				if (ch == ',') {
					// found new token
					counter++;

					if (counter == 0) {

						card.set("n", "given", token.toString());
					} else if (counter == 1) {

						card.set("n", "family", token.toString());
					} else if (counter == 2) {

						card.set("displayname", token.toString());
					} else if (counter == 3) {
						card.set("nickname", token.toString());
					} else if (counter == 4) {

						card.set("email", "internet", token.toString());
					} else if (counter == 5) {

						card.set("email", "x-email2", token.toString());
					} else if (counter == 8) {

						card.set("tel", "work", token.toString());
					} else if (counter == 9) {

						card.set("tel", "home", token.toString());
					}

					token = new StringBuffer();

				} else {
					token.append(ch);
				}

				pos++;

			}

			// add contact to addressbook
			saveContact(card);

		}

		in.close();
	}

	public String getDescription() {
		return AddressbookResourceLoader.getString(
			"dialog",
			"addressbookimport",
			"mozillacsvaddressbook_description");
	}

}
