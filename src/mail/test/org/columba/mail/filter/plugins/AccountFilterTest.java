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
package org.columba.mail.filter.plugins;

import org.columba.core.xml.XmlElement;
import org.columba.mail.filter.FilterCriteria;
import org.columba.mail.folder.MailboxTstFactory;


/**
 * @author fdietz
 *
 */
public class AccountFilterTest extends AbstractFilterTestCase {

    /**
     * @param arg0
     */
    public AccountFilterTest(MailboxTstFactory factory, String arg0) {
        super(factory, arg0);
        
    }

    public void testAccountWithUid0() throws Exception {
        // add message to folder
        Object uid = addMessage();

        getSourceFolder().setAttribute(uid, "columba.accountuid", new Integer(0));

        // create filter configuration
        FilterCriteria criteria = new FilterCriteria(new XmlElement("criteria"));
        criteria.setType("Account");
        criteria.setCriteria("is");
        criteria.set("account.uid",0);

        // create filter
        AccountFilter filter = new AccountFilter();

        // init configuration
        filter.setUp(criteria);

        // execute filter
        boolean result = filter.process(getSourceFolder(), uid);
        assertEquals("filter result", true, result);
    }
    
    public void testAccountWithUid1() throws Exception {
        // add message to folder
        Object uid = addMessage();

        getSourceFolder().setAttribute(uid, "columba.accountuid", new Integer(1));

        // create filter configuration
        FilterCriteria criteria = new FilterCriteria(new XmlElement("criteria"));
        criteria.setType("Account");
        criteria.setCriteria("is");
        criteria.set("account.uid",1);

        // create filter
        AccountFilter filter = new AccountFilter();

        // init configuration
        filter.setUp(criteria);

        // execute filter
        boolean result = filter.process(getSourceFolder(), uid);
        assertEquals("filter result", true, result);
    }
}
