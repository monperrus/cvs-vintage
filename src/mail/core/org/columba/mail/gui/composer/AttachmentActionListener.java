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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author frd
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AttachmentActionListener implements ActionListener {
    AttachmentController controller;

    public AttachmentActionListener(AttachmentController c) {
        this.controller = c;
    }

    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();

        if (action.equals("ADD")) {
            controller.addFileAttachment();
        } else if (action.equals("REMOVE")) {
            controller.removeSelected();
        }
    }
}
