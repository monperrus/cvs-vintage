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

package org.columba.mail.gui.message.filter;

import org.columba.core.gui.frame.FrameMediator;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.MessageFolder;
import org.columba.mail.folder.temp.TempFolder;
import org.columba.mail.gui.attachment.AttachmentSelectionHandler;
import org.columba.mail.gui.table.selection.TableSelectionHandler;
import org.columba.mail.main.MailInterface;
import org.columba.mail.message.ColumbaMessage;

/**
 * Should be used by every filter, which alters the message contents. This
 * is done the following way:
 * <p>
 * A new message is created and added to a temporary folder. All references of
 * the sources folder are re-mapped to the message in the temporary folder.
 * <p>
 *
 * @author fdietz
 */
public abstract class AbstractFilter implements Filter {

    private FrameMediator mediator;

    public AbstractFilter(FrameMediator mediator) {
        this.mediator = mediator;
    }
    /**
     * @return TODO
     * @see org.columba.mail.gui.message.filter.Filter#filter(org.columba.mail.folder.Folder, java.lang.Object)
     */
    public FolderCommandReference filter(MessageFolder folder, Object uid, ColumbaMessage message) throws Exception {
//      map selection to this temporary message
        TempFolder tempFolder = MailInterface.treeModel.getTempFolder();

        // add message to temporary folder
        uid = tempFolder.addMessage(message);

        
        // create reference to this message
        FolderCommandReference local = new FolderCommandReference(tempFolder,
                new Object[] {uid});

        // if we don't use this here - actions like reply would only work
        // on the
        // the encrypted message
        TableSelectionHandler h1 = ((TableSelectionHandler) mediator
                .getSelectionManager().getHandler("mail.table"));

        h1.setLocalReference(local);

        // this is needed to be able to open attachments of the decrypted
        // message
        AttachmentSelectionHandler h = ((AttachmentSelectionHandler) mediator
                .getSelectionManager().getHandler("mail.attachment"));
        h.setLocalReference(local);
        
        return local;
    }

    /**
     * @return Returns the mediator.
     */
    public FrameMediator getMediator() {
        return mediator;
    }
}
