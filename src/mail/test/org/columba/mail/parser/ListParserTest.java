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
package org.columba.mail.parser;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * @author fdietz
 * 
 */
public class ListParserTest extends TestCase {

	public void testCreateListFromStringNull() {

		try {
			ListParser.createListFromString(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateListFromStringEmpty() {
		String s = "";

		List<String> l = ListParser.createListFromString(s);
		assertEquals("list size 0", 0, l.size());
	}

	public void testCreateListFromString() {
		String s = "test@test.de";

		List<String> l = ListParser.createListFromString(s);
		assertEquals("list size 1", 1, l.size());

		assertEquals("test@test.de", l.get(0));
	}

	public void testCreateListFromString2() {
		String s = "test@test.de; test2@test2.de";

		List<String> l = ListParser.createListFromString(s);
		assertEquals("list size 2", 2, l.size());

		assertEquals("test@test.de", l.get(0));
		assertEquals("test2@test2.de", l.get(1));

	}

	public void testCreateStringFromListNull() {
		try {
			ListParser.createStringFromList(new Vector<String>(), null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateStringFromListNull2() {
		try {
			ListParser.createStringFromList(null, ";");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateStringFromListEmpty() {

		List<String> list = new Vector<String>();

		String result = ListParser.createStringFromList(list, ";");
		assertEquals("", result);
	}
	
	public void testCreateStringFromList() {

		List<String> list = new Vector<String>();
		list.add("test@test.de");
		list.add("test2@test2.de");

		String result = ListParser.createStringFromList(list, ";");
		assertEquals("test@test.de;test2@test2.de", result);
	}

}
