/*
 * Created on 25.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.mail.gui.composer.action;

import java.awt.event.ActionEvent;

import org.columba.core.main.MainInterface;
import org.columba.mail.action.ComposerAction;
import org.columba.mail.command.ComposerCommandReference;
import org.columba.mail.config.AccountItem;
import org.columba.mail.config.SpecialFoldersItem;
import org.columba.mail.folder.Folder;
import org.columba.mail.gui.composer.ComposerController;
import org.columba.mail.gui.composer.command.SaveMessageCommand;
import org.columba.mail.util.MailResourceLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SaveAsDraftAction extends ComposerAction {

	/**
	 * @param composerController
	 * @param name
	 * @param longDescription
	 * @param tooltip
	 * @param actionCommand
	 * @param small_icon
	 * @param big_icon
	 * @param mnemonic
	 * @param keyStroke
	 */
	public SaveAsDraftAction(ComposerController composerController) {
		super(
			composerController,
			MailResourceLoader.getString(
				"menu",
				"composer",
				"menu_file_savedraft"),
			MailResourceLoader.getString(
				"menu",
				"composer",
				"menu_file_savedraft"),
			MailResourceLoader.getString(
				"menu",
				"composer",
				"menu_file_savedraft"),
			"SAVEDRAFT",
			null,
			null,
			MailResourceLoader.getMnemonic(
				"menu",
				"composer",
				"menu_file_savedraft"),
			null);

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		if (composerInterface.composerController.checkState() == false)
			return;

		AccountItem item =
			composerInterface.composerController.getModel().getAccountItem();
		SpecialFoldersItem folderItem = item.getSpecialFoldersItem();
		String str = folderItem.get("drafts");
		int destUid = Integer.parseInt(str);
		Folder destFolder = (Folder) MainInterface.treeModel.getFolder(destUid);

		ComposerCommandReference[] r = new ComposerCommandReference[1];
		r[0] =
			new ComposerCommandReference(
				composerInterface.composerController,
				destFolder);

		SaveMessageCommand c = new SaveMessageCommand(r);

		MainInterface.processor.addOp(c);

	}

}
