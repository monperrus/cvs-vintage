/*
 * Created on 25.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.mail.gui.composer.action;

import java.awt.event.ActionEvent;

import org.columba.core.gui.util.ImageLoader;
import org.columba.mail.action.ComposerAction;
import org.columba.mail.gui.composer.ComposerController;
import org.columba.mail.util.MailResourceLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SpellcheckAction extends ComposerAction {

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
	 * @param showToolbarText
	 */
	public SpellcheckAction(ComposerController composerController) {
		super(
			composerController,
			MailResourceLoader.getString(
				"menu",
				"composer",
				"menu_message_spellCheck"),
			MailResourceLoader.getString(
				"menu",
				"composer",
				"menu_message_spellCheck"),
			MailResourceLoader.getString(
				"menu",
				"composer",
				"menu_message_spellCheck"),
			"SPELLCHECK",
			ImageLoader.getSmallImageIcon("stock_spellcheck_16.png"),
			ImageLoader.getImageIcon("stock_spellcheck_24.png"),
			MailResourceLoader.getMnemonic(
				"menu",
				"composer",
				"menu_message_spellCheck"),
			null,
			false);

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String checked =
			composerInterface.composerSpellCheck.checkText(
				composerInterface.editorController.getView().getText());

		composerInterface.editorController.getView().setText(checked);
	}

}
