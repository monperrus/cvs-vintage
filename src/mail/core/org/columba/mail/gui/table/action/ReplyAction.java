/*
 * Created on 11.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.mail.gui.table.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.columba.core.action.FrameAction;
import org.columba.core.gui.FrameController;
import org.columba.core.gui.util.ImageLoader;
import org.columba.mail.util.MailResourceLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReplyAction extends FrameAction {

	/**
	 * @param frameController
	 * @param name
	 * @param longDescription
	 * @param actionCommand
	 * @param small_icon
	 * @param big_icon
	 * @param mnemonic
	 * @param keyStroke
	 */
	public ReplyAction(
		FrameController frameController,
		String name,
		String longDescription,
		String actionCommand,
		ImageIcon small_icon,
		ImageIcon big_icon,
		int mnemonic,
		KeyStroke keyStroke) {
		super(
			frameController,
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_message_reply"),
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_message_reply_tooltip"),
			"REPLY",
			ImageLoader.getSmallImageIcon("reply_small.png"),
			ImageLoader.getImageIcon("reply.png"),
			'R',
			KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));

	}

	/**
	 * @param frameController
	 * @param name
	 * @param longDescription
	 * @param tooltip
	 * @param actionCommand
	 * @param small_icon
	 * @param big_icon
	 * @param mnemonic
	 * @param keyStroke
	 */
	public ReplyAction(
		FrameController frameController,
		String name,
		String longDescription,
		String tooltip,
		String actionCommand,
		ImageIcon small_icon,
		ImageIcon big_icon,
		int mnemonic,
		KeyStroke keyStroke) {
		super(
			frameController,
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_message_reply"),
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_message_reply_tooltip"),
			"REPLY",
			ImageLoader.getSmallImageIcon("reply_small.png"),
			ImageLoader.getImageIcon("reply.png"),
			'R',
			KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		super.actionPerformed(evt);
	}

}
