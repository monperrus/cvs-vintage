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
package org.columba.mail.gui.frame;

import org.columba.core.charset.CharsetManager;
import org.columba.core.charset.CharsetOwnerInterface;
import org.columba.core.config.ViewItem;
import org.columba.core.gui.frame.AbstractFrameController;
import org.columba.core.gui.frame.AbstractFrameView;
import org.columba.core.gui.selection.SelectionListener;
import org.columba.core.gui.toolbar.ToolBar;
import org.columba.core.xml.XmlElement;

import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.gui.attachment.AttachmentController;
import org.columba.mail.gui.message.MessageController;


/**
 *
 * Abstract frame controller for all mail windows.
 * <p>
 * The ThreePane and the message frame use this as basis.
 * <p>
 * It adds all mail specific stuff every mail frame has
 * in common.
 * <p>
 * So, please note that the only reason it exists is first of
 * all to save code duplication.<br>
 * The other reason is that every action in Columba, needs to
 * have a reference on a mail frame controller class.
 * <p>
 * So, changing this class will affect every action.
 * <p>
 * For more specific examples:<br>
 * @see ThreePaneMailFrameController
 * @see MessageFrameController
 *
 * @author fdietz
 *
 */
public abstract class AbstractMailFrameController
    extends AbstractFrameController implements MailFrameMediator,
        MessageViewOwner, AttachmentViewOwner, CharsetOwnerInterface {
    private ToolBar toolBar;
    public MessageController messageController;
    public AttachmentController attachmentController;
    protected CharsetManager charsetManager;

    public AbstractMailFrameController(String id, ViewItem viewItem) {
        super(id, viewItem);
    }

    public FolderCommandReference[] getTableSelection() {
        FolderCommandReference[] r = (FolderCommandReference[]) getSelectionManager()
                                                                    .getSelection("mail.table");

        return r;
    }

    public void setTableSelection(FolderCommandReference[] r) {
        getSelectionManager().setSelection("mail.table", r);
    }

    public FolderCommandReference[] getTreeSelection() {
        FolderCommandReference[] r = (FolderCommandReference[]) getSelectionManager()
                                                                    .getSelection("mail.tree");

        return r;
    }

    public void setTreeSelection(FolderCommandReference[] r) {
        getSelectionManager().setSelection("mail.tree", r);
    }

    public FolderCommandReference[] getAttachmentSelection() {
        FolderCommandReference[] r = (FolderCommandReference[]) getSelectionManager()
                                                                    .getSelection("mail.attachment");

        return r;
    }

    public void setAttachmentSelection(FolderCommandReference[] r) {
        getSelectionManager().setSelection("mail.attachment", r);
    }

    public void registerTableSelectionListener(SelectionListener l) {
        getSelectionManager().registerSelectionListener("mail.table", l);
    }

    public void registerTreeSelectionListener(SelectionListener l) {
        getSelectionManager().registerSelectionListener("mail.tree", l);
    }

    public void registerAttachmentSelectionListener(SelectionListener l) {
        getSelectionManager().registerSelectionListener("mail.attachment", l);
    }

    public AbstractFrameView getView() {
        return view;
    }

    protected void registerSelectionHandlers() {
    }

    protected void initInternActions() {
    }

    protected XmlElement createDefaultConfiguration(String id) {
        XmlElement child = super.createDefaultConfiguration(id);

        XmlElement splitpanes = new XmlElement("splitpanes");
        splitpanes.addAttribute("main", "200");
        splitpanes.addAttribute("header", "200");
        splitpanes.addAttribute("attachment", "100");
        child.addElement(splitpanes);

        return child;
    }

    protected void init() {
        setCharsetManager(new CharsetManager(null));

        attachmentController = new AttachmentController(this);

        messageController = new MessageController(this, attachmentController);
    }

    public CharsetManager getCharsetManager() {
        return charsetManager;
    }

    public void setCharsetManager(CharsetManager manager) {
        charsetManager = manager;
    }

    public MessageController getMessageController() {
        return messageController;
    }

    public AttachmentController getAttachmentController() {
        return attachmentController;
    }
}
