package org.columba.mail.gui.tree.command;

import org.columba.core.command.Command;
import org.columba.core.command.DefaultCommandReference;
import org.columba.core.command.Worker;
import org.columba.core.gui.FrameController;
import org.columba.core.logging.ColumbaLogger;
import org.columba.main.MainInterface;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.FolderTreeNode;

/**
 * @author freddy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FetchSubFolderListCommand extends Command {
	FolderTreeNode treeNode;
	
	/**
	 * Constructor for FetchSubFolderListCommand.
	 * @param references
	 */
	public FetchSubFolderListCommand(FrameController frame, DefaultCommandReference[] references) {
		super(frame, references);		
	}

	/**
	 * @see org.columba.core.command.Command#updateGUI()
	 */
	public void updateGUI() throws Exception {
		MainInterface.frameController.treeController.getModel().nodeStructureChanged(treeNode);
	}

	/**
	 * @see org.columba.core.command.Command#execute(Worker)
	 */
	public void execute(Worker worker) throws Exception {
		ColumbaLogger.log.debug("reference="+getReferences(Command.UNDOABLE_OPERATION));
		
		FolderCommandReference[] r = (FolderCommandReference[]) getReferences(Command.FIRST_EXECUTION);
		
		treeNode = (FolderTreeNode) r[0].getFolder();
		
		treeNode.createChildren(worker);
	}

	/**
	 * @see org.columba.core.command.Command#undo(Worker)
	 */
	public void undo(Worker worker) throws Exception {
	}

	/**
	 * @see org.columba.core.command.Command#redo(Worker)
	 */
	public void redo(Worker worker) throws Exception {
	}

}
