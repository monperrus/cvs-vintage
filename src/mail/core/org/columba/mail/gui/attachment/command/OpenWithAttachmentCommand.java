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
package org.columba.mail.gui.attachment.command;

import org.columba.core.command.Command;
import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.Worker;
import org.columba.core.command.WorkerStatusController;
import org.columba.core.io.StreamUtils;
import org.columba.core.io.TempFileStore;

import org.columba.mail.command.FolderCommand;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.MessageFolder;
import org.columba.mail.gui.mimetype.MimeTypeViewer;

import org.columba.ristretto.coder.Base64DecoderInputStream;
import org.columba.ristretto.coder.QuotedPrintableDecoderInputStream;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * @author freddy
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
public class OpenWithAttachmentCommand extends FolderCommand {
    LocalMimePart part;
    File tempFile;

    public OpenWithAttachmentCommand(DefaultCommandReference[] references) {
        super(references);

        priority = Command.REALTIME_PRIORITY;
        commandType = Command.NORMAL_OPERATION;
    }

    /**
     * @see org.columba.core.command.Command#updateGUI()
     */
    public void updateGUI() throws Exception {
        MimeHeader header = part.getHeader();

        MimeTypeViewer viewer = new MimeTypeViewer();
        viewer.openWith(header, tempFile);
    }

    /**
     * @see org.columba.core.command.Command#execute(Worker)
     */
    public void execute(WorkerStatusController worker)
        throws Exception {
        FolderCommandReference[] r = (FolderCommandReference[]) getReferences();
        MessageFolder folder = (MessageFolder) r[0].getFolder();
        Object[] uids = r[0].getUids();

        Integer[] address = r[0].getAddress();

        part = (LocalMimePart) folder.getMimePart(uids[0], address);

        MimeHeader header;
        tempFile = null;

        header = part.getHeader();

        try {
            String filename = part.getHeader().getFileName();

            if (filename != null) {
                tempFile = TempFileStore.createTempFile(filename);
            } else {
                tempFile = TempFileStore.createTempFile();
            }

            InputStream bodyStream = part.getInputStream();
            int encoding = header.getContentTransferEncoding();

            switch (encoding) {
            case MimeHeader.QUOTED_PRINTABLE: {
                bodyStream = new QuotedPrintableDecoderInputStream(bodyStream);

                break;
            }

            case MimeHeader.BASE64: {
                bodyStream = new Base64DecoderInputStream(bodyStream);

                break;
            }
            }

            // *20031019, karlpeder* Closing output stream after copying
            FileOutputStream output = new FileOutputStream(tempFile);
            StreamUtils.streamCopy(bodyStream, output);
            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
