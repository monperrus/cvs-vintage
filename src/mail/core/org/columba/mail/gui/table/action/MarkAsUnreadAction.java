/*
 * Created on 02.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.mail.gui.table.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import org.columba.core.action.FrameAction;
import org.columba.core.gui.frame.AbstractFrameController;
import org.columba.core.gui.selection.SelectionChangedEvent;
import org.columba.core.gui.selection.SelectionListener;
import org.columba.core.gui.util.ImageLoader;
import org.columba.core.main.MainInterface;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.command.MarkMessageCommand;
import org.columba.mail.gui.frame.MailFrameController;
import org.columba.mail.gui.table.selection.TableSelectionChangedEvent;

/**
 * @author frd
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MarkAsUnreadAction
	extends FrameAction
	implements SelectionListener {

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
	public MarkAsUnreadAction(AbstractFrameController frameController) {
		super(
			frameController,
			"As Unread",
			"As Unread",
			"MARK_AS_UNREAD",
			ImageLoader.getSmallImageIcon("mail-new.png"),
			ImageLoader.getImageIcon("mail-new.png"),
			'0',
			KeyStroke.getKeyStroke("M"));
		setEnabled(false);
		((MailFrameController) frameController).registerTableSelectionListener(
			this);
	}

	/* (non-Javadoc)
	* @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	*/
	public void actionPerformed(ActionEvent evt) {
		FolderCommandReference[] r =
			((MailFrameController) getFrameController()).getTableSelection();
		r[0].setMarkVariant(MarkMessageCommand.MARK_AS_UNREAD);

		MarkMessageCommand c = new MarkMessageCommand(r);

		MainInterface.processor.addOp(c);

	}
	/* (non-Javadoc)
			 * @see org.columba.core.gui.util.SelectionListener#selectionChanged(org.columba.core.gui.util.SelectionChangedEvent)
			 */
	public void selectionChanged(SelectionChangedEvent e) {

		if (((TableSelectionChangedEvent) e).getUids().length > 0)
			setEnabled(true);
		else
			setEnabled(false);

	}

}
