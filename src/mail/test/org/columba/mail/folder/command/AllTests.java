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
package org.columba.mail.folder.command;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author fdietz
 *
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
            "Test for org.columba.mail.folder.command");
        //$JUnit-BEGIN$
        suite.addTestSuite(CopyMessageCommandTest.class);
        suite.addTestSuite(MoveFolderCommandTest.class);
        suite.addTestSuite(MarkMessageTest.class);
        suite.addTestSuite(MoveMessageTest.class);
        //$JUnit-END$
        return suite;
    }
}