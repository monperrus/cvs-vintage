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
import java.util.List;

import org.columba.core.command.Command;
import org.columba.core.command.CommandProcessor;
import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.StatusObservableImpl;
import org.columba.core.command.Worker;
import org.columba.core.command.WorkerStatusController;
import org.columba.mail.command.FolderCommand;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.config.AccountItem;
import org.columba.mail.folder.AbstractFolder;
import org.columba.mail.folder.AbstractMessageFolder;
import org.columba.mail.folder.RootFolder;
import org.columba.mail.gui.tree.TreeModel;
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
 * Additionally, if message is marked as spam or non-spam the bayesian filter is
 * trained.
 * 
 * @see MarkMessageCommand
 * @author fdietz
 */
public class ToggleMarkCommand extends FolderCommand {

	private WorkerStatusController worker;

	private List commandList;

	/**
	 * Constructor for ToggleMarkCommand.
	 * 
	 * @param frameMediator
	 * @param references
	 */
	public ToggleMarkCommand(DefaultCommandReference reference) {
		super(reference);

		commandList = new ArrayList();
	}

	/**
	 * @see org.columba.core.command.Command#execute(Worker)
	 */
	public void execute(WorkerStatusController worker) throws Exception {
		this.worker = worker;

		/*
		 * // use wrapper class for easier handling of references array adapter =
		 * new FolderCommandAdapter( (FolderCommandReference[])
		 * getReferences());
		 *  // get array of source references FolderCommandReference[] r =
		 * adapter.getSourceFolderReferences();
		 */
		FolderCommandReference r = (FolderCommandReference) getReference();

		// get array of message UIDs
		Object[] uids = r.getUids();

		// get source folder
		AbstractMessageFolder srcFolder = (AbstractMessageFolder) r.getFolder();

		// register for status events
		((StatusObservableImpl) srcFolder.getObservable()).setWorker(worker);

		// which kind of mark?
		int markVariant = r.getMarkVariant();

		List list1 = new ArrayList();
		List list2 = new ArrayList();

		for (int j = 0; j < uids.length; j++) {
			Flags flags = srcFolder.getFlags(uids[j]);

			boolean result = false;
			if (markVariant == MarkMessageCommand.MARK_AS_READ) {
				if (flags.getSeen())
					result = true;
			} else if (markVariant == MarkMessageCommand.MARK_AS_FLAGGED) {
				if (flags.getFlagged())
					result = true;
			} else if (markVariant == MarkMessageCommand.MARK_AS_EXPUNGED) {
				if (flags.getDeleted())
					result = true;
			} else if (markVariant == MarkMessageCommand.MARK_AS_ANSWERED) {
				if (flags.getAnswered())
					result = true;
			} else if (markVariant == MarkMessageCommand.MARK_AS_DRAFT) {
				if (flags.getDraft())
					result = true;
			} else if (markVariant == MarkMessageCommand.MARK_AS_SPAM) {
				boolean spam = ((Boolean) srcFolder.getAttribute(uids[j],
						"columba.spam")).booleanValue();
				if (spam)
					result = true;
			}

			if (result)
				list1.add(uids[j]);
			else
				list2.add(uids[j]);
		}

		FolderCommandReference ref = null;

		if (list1.size() > 0) {
			ref = new FolderCommandReference(srcFolder, list1.toArray());
			ref.setMarkVariant(-markVariant);
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
			ref = new FolderCommandReference(srcFolder, list2.toArray());
			ref.setMarkVariant(markVariant);
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
	private void processSpamFilter(Object[] uids, AbstractMessageFolder srcFolder,
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
			if (item == null)
				continue;

			// if spam filter is not enabled -> return
			if (item.getSpamItem().isEnabled() == false)
				continue;

			System.out.println("learning uid=" + uids[j]);

			// create reference
			FolderCommandReference ref = new FolderCommandReference(srcFolder,
					new Object[] { uids[j] });

			// create command
			Command c = null;
			if (markVariant == MarkMessageCommand.MARK_AS_SPAM)
				c = new LearnMessageAsSpamCommand(ref);
			else
				c = new LearnMessageAsHamCommand(ref);

			// execute command
			c.execute(worker);

			// skip if message is *not* marked as spam
			if (markVariant == MarkMessageCommand.MARK_AS_NOTSPAM)
				continue;

			// skip if user didn't enable this option
			if (item.getSpamItem().isMoveMessageWhenMarkingEnabled() == false)
				continue;

			if (item.getSpamItem().isMoveTrashSelected() == false) {
				// move message to user-configured folder (generally "Junk"
				// folder)
				AbstractFolder destFolder = TreeModel.getInstance()
						.getFolder(item.getSpamItem().getMoveCustomFolder());

				// create reference
				FolderCommandReference ref2 = new FolderCommandReference(
						srcFolder, destFolder, new Object[] { uids[j] });
				CommandProcessor.getInstance().addOp(new MoveMessageCommand(ref2));

			} else {
				// move message to trash
				AbstractMessageFolder trash = (AbstractMessageFolder) ((RootFolder) srcFolder
						.getRootFolder()).getTrashFolder();

				// create reference
				FolderCommandReference ref2 = new FolderCommandReference(
						srcFolder, trash, new Object[] { uids[j] });

				CommandProcessor.getInstance().addOp(new MoveMessageCommand(ref2));

			}

		}
	}
}