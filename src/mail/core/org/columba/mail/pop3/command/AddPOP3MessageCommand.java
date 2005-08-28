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
package org.columba.mail.pop3.command;

import org.columba.api.command.ICommandReference;
import org.columba.api.command.IWorkerStatusController;
import org.columba.core.command.Command;
import org.columba.core.command.CommandProcessor;
import org.columba.core.command.CompoundCommand;
import org.columba.core.filter.Filter;
import org.columba.core.filter.FilterList;
import org.columba.mail.command.IMailFolderCommandReference;
import org.columba.mail.command.MailFolderCommandReference;
import org.columba.mail.config.AccountItem;
import org.columba.mail.filter.FilterCompoundCommand;
import org.columba.mail.folder.AbstractFolder;
import org.columba.mail.folder.IMailbox;
import org.columba.mail.folder.RootFolder;
import org.columba.mail.folder.command.MarkMessageCommand;
import org.columba.mail.folder.command.MoveMessageCommand;
import org.columba.mail.gui.tree.FolderTreeModel;
import org.columba.mail.message.IColumbaMessage;
import org.columba.mail.spam.command.CommandHelper;
import org.columba.mail.spam.command.ScoreMessageCommand;
import org.columba.ristretto.io.SourceInputStream;

/**
 * After downloading the message from the POP3 server, its added to the Inbox
 * folder.
 * <p>
 * The spam filter is executed on this message.
 * <p>
 * The Inbox filters are applied to the message.
 * 
 * @author fdietz
 */
public class AddPOP3MessageCommand extends Command {

	private IMailbox inboxFolder;

	/**
	 * @param references
	 *            command arguments
	 */
	public AddPOP3MessageCommand(ICommandReference reference) {
		super(reference);
	}

	/** {@inheritDoc} */
	public void execute(IWorkerStatusController worker) throws Exception {
		IMailFolderCommandReference r = (IMailFolderCommandReference) getReference();

		inboxFolder = (IMailbox) r.getSourceFolder();

		IColumbaMessage message = (IColumbaMessage) r.getMessage();

		// add message to folder
		SourceInputStream messageStream = new SourceInputStream(message
				.getSource());
		Object uid = inboxFolder.addMessage(messageStream);
		messageStream.close();

		// mark message as recent
		r.setSourceFolder(inboxFolder);
		r.setUids(new Object[] { uid });
		r.setMarkVariant(MarkMessageCommand.MARK_AS_RECENT);
		new MarkMessageCommand(r).execute(worker);

		// apply spam filter
		boolean messageWasMoved = applySpamFilter(uid, worker);

		if (messageWasMoved == false) {
			// apply filter on message
			applyFilters(uid);
		}
	}

	/**
	 * Apply spam filter engine on message.
	 * <p>
	 * Message is marked as ham or spam.
	 * 
	 * @param uid
	 *            message uid.
	 * @throws Exception
	 */
	private boolean applySpamFilter(Object uid, IWorkerStatusController worker)
			throws Exception {
		// message belongs to which account?
		AccountItem item = CommandHelper.retrieveAccountItem(inboxFolder, uid);

		// if spam filter is not enabled -> return
		if (!item.getSpamItem().isEnabled()) {
			return false;
		}

		// create reference
		IMailFolderCommandReference r = new MailFolderCommandReference(
				inboxFolder, new Object[] { uid });

		// score message and mark as "spam" or "not spam"
		new ScoreMessageCommand(r).execute(worker);

		// is message marked as spam
		boolean spam = ((Boolean) inboxFolder.getAttribute(uid, "columba.spam"))
				.booleanValue();
		if (spam == false)
			return false;

		if (item.getSpamItem().isMoveIncomingJunkMessagesEnabled()) {
			if (item.getSpamItem().isIncomingTrashSelected()) {
				// move message to trash
				IMailbox trash = (IMailbox) ((RootFolder) inboxFolder
						.getRootFolder()).getTrashFolder();

				// create reference
				MailFolderCommandReference ref2 = new MailFolderCommandReference(
						inboxFolder, trash, new Object[] { uid });

				CommandProcessor.getInstance().addOp(
						new MoveMessageCommand(ref2));
			} else {
				// move message to user-configured folder (generally "Junk"
				// folder)
				IMailbox destFolder = (IMailbox) FolderTreeModel
						.getInstance()
						.getFolder(item.getSpamItem().getIncomingCustomFolder());

				// create reference
				MailFolderCommandReference ref2 = new MailFolderCommandReference(
						inboxFolder, destFolder, new Object[] { uid });

				CommandProcessor.getInstance().addOp(
						new MoveMessageCommand(ref2));

			}

			return true;
		}

		return false;
	}

	/**
	 * Apply filters on new message.
	 * 
	 * @param uid
	 *            message uid
	 */
	private void applyFilters(Object uid) throws Exception {
		FilterList list = inboxFolder.getFilterList();

		for (int j = 0; j < list.count(); j++) {
			Filter filter = list.get(j);

			Object[] result = inboxFolder.searchMessages(filter,
					new Object[] { uid });

			if (result.length != 0) {
				CompoundCommand command = new FilterCompoundCommand(filter,
						inboxFolder, result);

				CommandProcessor.getInstance().addOp(command);
			}
		}
	}

}