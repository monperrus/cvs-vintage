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

package org.columba.core.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.columba.core.action.AbstractSelectableAction;
import org.columba.core.gui.frame.FrameMediator;
import org.columba.core.main.ConnectionStateImpl;
import org.columba.core.util.GlobalResourceLoader;

/**
 * Toggles the system's connection state.
 */
public class WorkOfflineAction extends AbstractSelectableAction
    implements ChangeListener {
    
    public WorkOfflineAction(FrameMediator controller) {
        super(controller, GlobalResourceLoader.getString(
            null, null, "menu_file_workoffline"));
        ConnectionStateImpl.getInstance().addChangeListener(this);
        stateChanged(null);
    }
    
    public void actionPerformed(ActionEvent e) {
    	ConnectionStateImpl.getInstance().setOnline(
                !ConnectionStateImpl.getInstance().isOnline());
    }
    
    public void stateChanged(ChangeEvent e) {
        setState(!ConnectionStateImpl.getInstance().isOnline());
    }
}
