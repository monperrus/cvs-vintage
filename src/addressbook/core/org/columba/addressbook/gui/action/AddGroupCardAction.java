/*
 * Created on 26.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.addressbook.gui.action;

import java.awt.event.ActionEvent;

import org.columba.addressbook.util.AddressbookResourceLoader;
import org.columba.core.action.FrameAction;
import org.columba.core.gui.FrameController;
import org.columba.core.gui.util.ImageLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AddGroupCardAction extends FrameAction {

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
	public AddGroupCardAction(FrameController frameController) {
		super(
			frameController,
			AddressbookResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_file_addgroup"),
			AddressbookResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_file_addgroup"),
			"ADDGROUP",
			ImageLoader.getSmallImageIcon("group_small.png"),
			ImageLoader.getImageIcon("group.png"),
			'0',
			null);

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		super.actionPerformed(evt);
	}

}
