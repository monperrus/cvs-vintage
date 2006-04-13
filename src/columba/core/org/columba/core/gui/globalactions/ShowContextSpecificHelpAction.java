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

package org.columba.core.gui.globalactions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.help.CSH;

import org.columba.api.gui.frame.IFrameMediator;
import org.columba.core.gui.action.AbstractColumbaAction;
import org.columba.core.help.HelpManager;
import org.columba.core.resourceloader.GlobalResourceLoader;

/**
 * Brings up helpful information for a specific item.
 *
 * @author frd
 */
public class ShowContextSpecificHelpAction extends AbstractColumbaAction {
    protected ActionListener target;

    public ShowContextSpecificHelpAction(IFrameMediator frameMediator) {
        super(frameMediator, GlobalResourceLoader.getString(null, null,
            "menu_help_context"));

       
        putValue(SHORT_DESCRIPTION, GlobalResourceLoader.getString(null, null,
            "menu_help_context_tooltip"));

        setShowToolBarText(false);

        target = new CSH.DisplayHelpAfterTracking(HelpManager.getInstance()
                                                             .getHelpBroker());
    }

    public void actionPerformed(ActionEvent e) {
        target.actionPerformed(e);
    }
}
