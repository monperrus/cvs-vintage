// The contents of this file are subject to the Mozilla Public License Version
//1.1
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
//Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.mail.folder.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.columba.core.command.Command;
import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.StatusObservableImpl;
import org.columba.core.command.WorkerStatusController;
import org.columba.core.main.MainInterface;
import org.columba.mail.command.FolderCommand;
import org.columba.mail.command.FolderCommandAdapter;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.config.AccountItem;
import org.columba.mail.folder.AbstractFolder;
import org.columba.mail.folder.MessageFolder;
import org.columba.mail.folder.RootFolder;
import org.columba.mail.main.MailInterface;
import org.columba.mail.spam.command.CommandHelper;
import org.columba.mail.spam.command.LearnMessageAsHamCommand;
import org.columba.mail.spam.command.LearnMessageAsSpamCommand;
import org.columba.ristretto.message.Flags;

/**
 * Toggle flag.
 * <p>
 * Creates two sets of messages and uses {@link MarkMessageCommand}, which does
 * the flag change.
 * <p>
 * Additionally, if message is marked as spam or non-spam the bayesian filter
 * is trained. 
 *
 * @see MarkMessageCommand
 * @author fdietz
 */
public class ToggleMarkCommand extends FolderCommand {

    protected FolderCommandAdapter adapter;

    private WorkerStatusController worker;

    private List commandList;

    /**
     * Constructor for ToggleMarkCommand.
     * 
     * @param frameMediator
     * @param references
     */
    public ToggleMarkCommand(DefaultCommandReference[] references) {
        super(references);

        commandList = new ArrayList();
    }

    public void updateGUI() throws Exception {

        Iterator it = commandList.iterator();
        while (it.hasNext()) {
            FolderCommand c = (FolderCommand) it.next();

            c.updateGUI();
        }
    }

    /**
     * @see org.columba.core.command.Command#execute(Worker)
     */
    public void execute(WorkerStatusController worker) throws Exception {
        this.worker = worker;

        // use wrapper class for easier handling of references array
        adapter = new FolderCommandAdapter(
                (FolderCommandReference[]) getReferences());

        // get array of source references
        FolderCommandReference[] r = adapter.getSourceFolderReferences();

        // for every folder
        for (int i = 0; i < r.length; i++) {
            // get array of message UIDs
            Object[] uids = r[i].getUids();

            // get source folder
            MessageFolder srcFolder = (MessageFolder) r[i].getFolder();

            // register for status events
            ((StatusObservableImpl) srcFolder.getObservable())
                    .setWorker(worker);

            // which kind of mark?
            int markVariant = r[i].getMarkVariant();

            List list1 = new ArrayList();
            List list2 = new ArrayList();

            for (int j = 0; j < uids.length; j++) {
                Flags flags = srcFolder.getFlags(uids[j]);

                boolean result = false;
                if (markVariant == MarkMessageCommand.MARK_AS_READ) {
                    if (flags.getSeen()) result = true;
                } else if (markVariant == MarkMessageCommand.MARK_AS_FLAGGED) {
                    if (flags.getFlagged()) result = true;
                } else if (markVariant == MarkMessageCommand.MARK_AS_EXPUNGED) {
                    if (flags.getDeleted()) result = true;
                } else if (markVariant == MarkMessageCommand.MARK_AS_ANSWERED) {
                    if (flags.getAnswered()) result = true;
                }  else if (markVariant == MarkMessageCommand.MARK_AS_DRAFT) {
                    if (flags.getDraft()) result = true;
                } else if (markVariant == MarkMessageCommand.MARK_AS_SPAM) {
                    boolean spam = ((Boolean) srcFolder.getAttribute(uids[j],
                            "columba.spam")).booleanValue();
                    if (spam) result = true;
                }

                if (result)
                    list1.add(uids[j]);
                else
                    list2.add(uids[j]);
            }

            FolderCommandReference[] ref = new FolderCommandReference[1];

            if (list1.size() > 0) {
                ref[0] = new FolderCommandReference(srcFolder, list1.toArray());
                ref[0].setMarkVariant(-markVariant);
                MarkMessageCommand c = new MarkMessageCommand(ref);
                commandList.add(c);
                c.execute(worker);

                // train bayesian filter
                if ((markVariant == MarkMessageCommand.MARK_AS_SPAM)
                        || (markVariant == MarkMessageCommand.MARK_AS_NOTSPAM)) {
                    processSpamFilter(uids, srcFolder, -markVariant);
                }
            }

            if (list2.size() > 0) {
                ref[0] = new FolderCommandReference(srcFolder, list2.toArray());
                ref[0].setMarkVariant(markVariant);
                MarkMessageCommand c = new MarkMessageCommand(ref);
                commandList.add(c);
                c.execute(worker);

                // train bayesian filter
                if ((markVariant == MarkMessageCommand.MARK_AS_SPAM)
                        || (markVariant == MarkMessageCommand.MARK_AS_NOTSPAM)) {
                    processSpamFilter(uids, srcFolder, markVariant);
                }
            }
        }
    }

    /**
     * Train spam filter.
     * <p>
     * Move message to specified folder or delete message immediately based on
     * account configuration.
     * 
     * @param uids
     *            message uid
     * @param srcFolder
     *            source folder
     * @param markVariant
     *            mark variant (spam/not spam)
     * @throws Exception
     */
    private void processSpamFilter(Object[] uids, MessageFolder srcFolder,
            int markVariant) throws Exception {

        // update status message
        worker.setDisplayText("Training messages...");
        worker.setProgressBarMaximum(uids.length);

        // mark as/as not spam
        // for each message
        for (int j = 0; j < uids.length; j++) {

            worker.setDisplayText("Training messages...");
            worker.setProgressBarMaximum(uids.length);
            // increase progressbar value
            worker.setProgressBarValue(j);

            // cancel here if user requests
            if (worker.cancelled()) {
                break;
            }

            // message belongs to which account?
            AccountItem item = CommandHelper.retrieveAccountItem(srcFolder,
                    uids[j]);
            // skip if account information is not available
            if (item == null) continue;

            // if spam filter is not enabled -> return
            if (item.getSpamItem().isEnabled() == false) continue;

            System.out.println("learning uid=" + uids[j]);

            // create reference
            FolderCommandReference[] ref = new FolderCommandReference[1];
            ref[0] = new FolderCommandReference(srcFolder,
                    new Object[] { uids[j]});

            // create command
            Command c = null;
            if (markVariant == MarkMessageCommand.MARK_AS_SPAM)
                c = new LearnMessageAsSpamCommand(ref);
            else
                c = new LearnMessageAsHamCommand(ref);

            // execute command
            c.execute(worker);

            // skip if message is *not* marked as spam
            if (markVariant == MarkMessageCommand.MARK_AS_NOTSPAM) continue;

            // skip if user didn't enable this option
            if (item.getSpamItem().isMoveMessageWhenMarkingEnabled() == false)
                    continue;

            if (item.getSpamItem().isMoveTrashSelected() == false) {
                // move message to user-configured folder (generally "Junk"
                // folder)
                AbstractFolder destFolder = MailInterface.treeModel
                        .getFolder(item.getSpamItem().getMoveCustomFolder());

                // create reference
                FolderCommandReference[] ref2 = new FolderCommandReference[2];
                ref2[0] = new FolderCommandReference(srcFolder,
                        new Object[] { uids[j]});
                ref2[1] = new FolderCommandReference(destFolder);
                MainInterface.processor.addOp(new MoveMessageCommand(ref2));

            } else {
                // move message to trash
                MessageFolder trash = (MessageFolder) ((RootFolder) srcFolder
                        .getRootFolder()).getTrashFolder();

                // create reference
                FolderCommandReference[] ref2 = new FolderCommandReference[2];
                ref2[0] = new FolderCommandReference(srcFolder,
                        new Object[] { uids[j]});
                ref2[1] = new FolderCommandReference(trash);

                MainInterface.processor.addOp(new MoveMessageCommand(ref2));

            }

        }
    }
}