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

import org.columba.addressbook.folder.ContactCard;
import org.columba.addressbook.gui.tree.util.SelectAddressbookFolderDialog;
import org.columba.addressbook.main.AddressbookInterface;
import org.columba.addressbook.parser.AddressParser;

import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.StatusObservableImpl;
import org.columba.core.command.WorkerStatusController;
import org.columba.core.gui.frame.FrameMediator;

import org.columba.mail.command.FolderCommand;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.MessageFolder;

import org.columba.ristretto.message.Header;


/**
 * Add all senders contained in the selected messages to the addressbook.
 * <p>
 * A dialog asks the user to choose the destination addressbook.
 *
 * @author fdietz
 */
public class AddAllSendersToAddressbookCommand extends FolderCommand {
    org.columba.addressbook.folder.Folder selectedFolder;

    /**
 * Constructor for AddAllSendersToAddressbookCommand.
 *
 * @param references
 */
    public AddAllSendersToAddressbookCommand(
        DefaultCommandReference[] references) {
        super(references);
    }

    /**
 * Constructor for AddAllSendersToAddressbookCommand.
 *
 * @param frame
 * @param references
 */
    public AddAllSendersToAddressbookCommand(FrameMediator frame,
        DefaultCommandReference[] references) {
        super(frame, references);
    }

    /**
 * @see org.columba.core.command.Command#execute(org.columba.core.command.Worker)
 */
    public void execute(WorkerStatusController worker)
        throws Exception {
        // get reference
        FolderCommandReference[] r = (FolderCommandReference[]) getReferences();

        // selected messages
        Object[] uids = r[0].getUids();

        // selected folder
        MessageFolder folder = (MessageFolder) r[0].getFolder();

        // register for status events
        ((StatusObservableImpl) folder.getObservable()).setWorker(worker);

        // open addressbook selection dialog
        SelectAddressbookFolderDialog dialog = AddressbookInterface.addressbookTreeModel.getSelectAddressbookFolderDialog();

        selectedFolder = dialog.getSelectedFolder();

        if (selectedFolder == null) {
            return;
        }

        // for every message
        for (int i = 0; i < uids.length; i++) {
            // get header of message
            Header header = folder.getHeaderFields(uids[i],
                    new String[] { "From", "Cc", "Bcc" });

            String sender = (String) header.get("From");

            // add sender to addressbook
            addSender(sender);

            sender = (String) header.get("Cc");

            addSender(sender);

            sender = (String) header.get("Bcc");

            addSender(sender);
        }
    }

    /**
 * Add sender to selected addressbook.
 * <p>
 * TODO: This code should most probably moved to the addressbook component
 *
 * @param sender
 *            email address of sender
 */
    public void addSender(String sender) {
        if (sender == null) {
            return;
        }

        if (sender.length() > 0) {
            String address = AddressParser.getAddress(sender);
            System.out.println("address:" + address);

            if (!selectedFolder.exists(address)) {
                ContactCard card = new ContactCard();

                String fn = AddressParser.getDisplayname(sender);
                System.out.println("fn=" + fn);

                card.set("fn", fn);
                card.set("displayname", fn);
                card.set("email", "internet", address);

                selectedFolder.add(card);
            }
        }
    }
}
