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
package org.columba.mail.gui.table;

import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.MessageFolder;
import org.columba.mail.gui.frame.MailFrameMediator;
import org.columba.mail.gui.table.action.OpenMessageWithMessageFrameAction;
import org.columba.mail.gui.table.action.ViewMessageAction;
import org.columba.mail.gui.table.model.MessageNode;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;


/**
 * Mouse listener used to handle the table selection and the popup
 * menu viewing.
 * 
 *
 * @author fdietz
 */
public class HeaderTableMouseListener extends MouseAdapter {
    private TableController headerTableViewer;
    private ViewMessageAction viewMessageAction;

    public HeaderTableMouseListener(TableController headerTableViewer) {
        super();
        this.headerTableViewer = headerTableViewer;
        viewMessageAction = new ViewMessageAction(headerTableViewer.getFrameController());
    }

    protected void processPopup(final MouseEvent event) {
        int selectedRows = headerTableViewer.getView().getSelectedRowCount();

        if (selectedRows <= 1) {
            // select node
            int row = headerTableViewer.getView().rowAtPoint(new Point(
                        event.getX(), event.getY()));
            headerTableViewer.getView().setRowSelectionInterval(row, row);
        }

        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    headerTableViewer.getPopupMenu().show(event.getComponent(),
                        event.getX(), event.getY());
                }
            });
    }

    public void mousePressed(MouseEvent event) {
        if (event.isPopupTrigger()) {
            processPopup(event);
        }
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            processDoubleClick();
        } else {
            if (SwingUtilities.isLeftMouseButton(event)) {
                //if (event.getModifiers() == InputEvent.BUTTON1_MASK) {
                int row = headerTableViewer.getView().getSelectedRow();
                MessageNode node = (MessageNode) headerTableViewer.getView()
                                                                  .getValueAt(row,
                        0);

                FolderCommandReference[] ref = ((MailFrameMediator)headerTableViewer.getFrameController())
                                                                .getTableSelection();
                ((MessageFolder) ref[0].getFolder()).setLastSelection(node.getUid());
                viewMessageAction.actionPerformed(null);
            }
        }
    }

    protected void processDoubleClick() {
        // open message in new message-frame
        new OpenMessageWithMessageFrameAction(headerTableViewer.getFrameController()).actionPerformed(null);
    }

    public void mouseReleased(MouseEvent event) {
        if (event.isPopupTrigger()) {
            processPopup(event);
        }
    }
}
