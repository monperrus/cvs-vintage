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

package org.columba.mail.imap.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.columba.core.main.MainInterface;
import org.columba.mail.coder.CoderRouter;

/**
 * @author frd
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AllTests {

	public static Test suite() {

		// enable debugging for testsuite
		MainInterface.DEBUG = true;

		// enable decoder/encoder for testsuite
		new CoderRouter();

		TestSuite suite =
			new TestSuite("Test for org.columba.modules.mail.parser");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(FlagsParserTest.class));
		suite.addTest(new TestSuite(LSubParserTest.class));
		suite.addTest(new TestSuite(ListInfoTest.class));
		suite.addTest(new TestSuite(MessageFolderInfoParserTest.class));
		suite.addTest(new TestSuite(MessageSetTest.class));
		suite.addTest(new TestSuite(MessageSourceParserTest.class));
		suite.addTest(new TestSuite(MimePartParserTest.class));
		suite.addTest(new TestSuite(MimePartTreeParserTest.class));
		suite.addTest(new TestSuite(SearchResultParserTest.class));
		suite.addTest(new TestSuite(UIDParserTest.class));
		suite.addTest(new TestSuite(HeaderParserTest.class));
		//$JUnit-END$
		return suite;
	}
}
