/*
 * Created on 11.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.columba.mail.gui.tree.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.columba.core.action.FrameAction;
import org.columba.core.gui.frame.AbstractFrameController;
import org.columba.core.gui.selection.SelectionChangedEvent;
import org.columba.core.gui.selection.SelectionListener;
import org.columba.core.gui.util.ImageLoader;
import org.columba.core.main.MainInterface;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.folder.FolderFactory;
import org.columba.mail.gui.frame.AbstractMailFrameController;
import org.columba.mail.gui.tree.selection.TreeSelectionChangedEvent;
import org.columba.mail.gui.tree.util.CreateFolderDialog;
import org.columba.mail.util.MailResourceLoader;

/**
 * @author frd
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CreateVirtualFolderAction
	extends FrameAction
	implements SelectionListener {

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
	public CreateVirtualFolderAction(AbstractFrameController frameController) {
		super(
			frameController,
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_folder_newvirtualfolder"),
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_folder_newvirtualfolder"),
			MailResourceLoader.getString(
				"menu",
				"mainframe",
				"menu_folder_newvirtualfolder"),
			"CREATE_VIRTUAL_SUBFOLDER",
			ImageLoader.getSmallImageIcon("virtualfolder.png"),
			ImageLoader.getImageIcon("virtualfolder.png"),
			'0',
			KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.ALT_MASK));

		setEnabled(false);
		(
			(
				AbstractMailFrameController) frameController)
					.registerTreeSelectionListener(
			this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		CreateFolderDialog dialog = new CreateFolderDialog(null);
		dialog.showDialog();

		String name;

		if (dialog.success()) {
			// ok pressed
			name = dialog.getName();

			try {
				FolderCommandReference[] r =
					(FolderCommandReference[]) frameController
						.getSelectionManager()
						.getSelection(
						"mail.tree");
				FolderFactory.getInstance().createChild( r[0].getFolder(), name, "VirtualFolder");

				FolderCommandReference[] reference =
					(FolderCommandReference[])
						((AbstractMailFrameController) getFrameController())
						.getTreeSelection();
				MainInterface.treeModel.nodeStructureChanged(
					reference[0].getFolder());

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} else {
			// cancel pressed
			return;
		}
	}

	/* (non-Javadoc)
				 * @see org.columba.core.gui.util.SelectionListener#selectionChanged(org.columba.core.gui.util.SelectionChangedEvent)
				 */
	public void selectionChanged(SelectionChangedEvent e) {

		if (((TreeSelectionChangedEvent) e).getSelected().length > 0)
			setEnabled(true);
		else
			setEnabled(false);

	}

}
