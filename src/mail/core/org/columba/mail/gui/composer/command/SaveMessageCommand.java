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
package org.columba.mail.gui.composer.command;

import java.io.InputStream;

import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.ProgressObservedInputStream;
import org.columba.core.command.Worker;
import org.columba.core.command.WorkerStatusController;
import org.columba.mail.command.ComposerCommandReference;
import org.columba.mail.command.FolderCommand;
import org.columba.mail.composer.MessageComposer;
import org.columba.mail.composer.SendableMessage;
import org.columba.mail.config.AccountItem;
import org.columba.mail.folder.AbstractMessageFolder;
import org.columba.mail.gui.composer.ComposerController;
import org.columba.mail.gui.composer.ComposerModel;
import org.columba.mail.gui.frame.TableUpdater;
import org.columba.mail.gui.table.model.TableModelChangedEvent;
import org.columba.mail.gui.tree.TreeModel;
import org.columba.mail.util.MailResourceLoader;


/**
 * @author freddy
 */
public class SaveMessageCommand extends FolderCommand {
    private AbstractMessageFolder folder;

    /**
     * Constructor for SaveMessageCommand.
     *
     * @param frameMediator
     * @param references
     */
    public SaveMessageCommand(DefaultCommandReference reference) {
        super(reference);
    }

    public void updateGUI() throws Exception {
        // update the table
        TableModelChangedEvent ev = new TableModelChangedEvent(TableModelChangedEvent.UPDATE,
                folder);

        TableUpdater.tableChanged(ev);

        TreeModel.getInstance().nodeChanged(folder);
    }

    /**
     * @see org.columba.core.command.Command#execute(Worker)
     */
    public void execute(WorkerStatusController worker)
        throws Exception {
        ComposerCommandReference r = (ComposerCommandReference) getReference();

        ComposerController composerController = r.getComposerController();

        AccountItem item = ((ComposerModel) composerController.getModel()).getAccountItem();

        SendableMessage message = (SendableMessage) r.getMessage();

        if (message == null) {
            message = new MessageComposer(((ComposerModel) composerController.getModel())).compose(worker);
        }
        folder = (AbstractMessageFolder) r.getFolder();
        
        worker.setDisplayText(MailResourceLoader.getString("statusbar",
				"message", "save_message"));
        

        InputStream sourceStream = new ProgressObservedInputStream( message.getSourceStream(), worker );
        folder.addMessage(sourceStream,
            message.getHeader().getAttributes(), message.getHeader().getFlags());
        sourceStream.close();
    }
}
