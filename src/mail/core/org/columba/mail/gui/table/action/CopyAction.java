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
package org.columba.mail.gui.table.action;

import org.columba.core.action.AbstractColumbaAction;
import org.columba.core.gui.ClipboardManager;
import org.columba.core.main.MainInterface;

import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.gui.frame.AbstractMailFrameController;
import org.columba.mail.gui.frame.TableViewOwner;
import org.columba.mail.gui.table.TableController;

import java.awt.event.ActionEvent;


/**
 * @author frd
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CopyAction extends AbstractColumbaAction {
    AbstractMailFrameController frameController;

    public CopyAction(AbstractMailFrameController frameController) {
        super(frameController, "CopyAction");
        this.frameController = frameController;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        // add current selection to clipboard
        // copy action
        MainInterface.clipboardManager.setOperation(ClipboardManager.COPY_ACTION);

        MainInterface.clipboardManager.setMessageSelection((FolderCommandReference[]) frameController.getTableSelection());
    }
}
