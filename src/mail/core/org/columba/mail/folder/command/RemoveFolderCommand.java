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
package org.columba.mail.folder.command;

import javax.swing.tree.TreeNode;

import org.columba.core.command.Command;
import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.Worker;
import org.columba.core.command.WorkerStatusController;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.AbstractMessageFolder;

/**
 * Delete this folder.
 * 
 * @author fdietz
 */
public class RemoveFolderCommand extends Command {

	private TreeNode parentFolder;

	private int[] childIndicies;

	private Object[] childObjects;

	private boolean success;

	/**
	 * Constructor for RemoveFolder.
	 * 
	 * @param references
	 *            command arguments.
	 */
	public RemoveFolderCommand(DefaultCommandReference reference) {
		super(reference);

		success = false;
	}

	/**
	 * @see org.columba.core.command.Command#updateGUI()
	 */
	/*
	 * public void updateGUI() throws Exception { // update treemodel with more
	 * relevant data. MailInterface.treeModel.nodesWereRemoved(parentFolder,
	 * childIndicies, childObjects); }
	 */

	/**
	 * @see org.columba.core.command.Command#execute(Worker)
	 */
	public void execute(WorkerStatusController worker) throws Exception {
		// get source folder
		AbstractMessageFolder childFolder = (AbstractMessageFolder) ((FolderCommandReference) getReference())
				.getFolder();

		// need to store the data for the proper event generation.
		parentFolder = childFolder.getParent();
		childIndicies = new int[] { parentFolder.getIndex(childFolder) };
		childObjects = new Object[] { childFolder };

		// remove source folder
		childFolder.removeFolder();
	}
}