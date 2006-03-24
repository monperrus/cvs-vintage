// The contents of this file are subject to the Mozilla Public License Version
// 1.1
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
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.addressbook.gui.tree.util;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.javaprog.ui.wizard.plaf.basic.SingleSideEtchedBorder;

import org.columba.addressbook.config.FolderItem;
import org.columba.addressbook.folder.AddressbookFolder;
import org.columba.addressbook.folder.AddressbookTreeNode;
import org.columba.addressbook.folder.IFolder;
import org.columba.addressbook.gui.tree.AddressbookTreeModel;
import org.columba.addressbook.util.AddressbookResourceLoader;
import org.columba.core.gui.base.ButtonWithMnemonic;

public class SelectAddressbookFolderDialog extends JDialog implements
		ActionListener, TreeSelectionListener, ISelectFolderDialog {

	// private MainInterface mainInterface;
	private boolean bool = false;

	// public SelectFolderTree tree;
	private JTree tree;

	private JButton[] buttons;

	// private TreeView treeViewer;
	private IFolder selectedFolder;

	private TreeModel model;

	public SelectAddressbookFolderDialog(TreeModel model) {
		super(new JFrame(), true);

		setTitle(AddressbookResourceLoader.getString("tree", "folderdialog",
				"select_folder"));

		this.model = model;

		initComponents();

		layoutComponents();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void layoutComponents() {
		getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		mainPanel.add(new JScrollPane(tree), BorderLayout.CENTER);

		getContentPane().add(mainPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new SingleSideEtchedBorder(SwingConstants.TOP));

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 6, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		buttonPanel.add(buttons[0]);
		buttonPanel.add(buttons[1]);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);

		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void initComponents() {
		buttons = new JButton[3];

		buttons[0] = new ButtonWithMnemonic(AddressbookResourceLoader
				.getString("global", "cancel"));
		buttons[0].setActionCommand("CANCEL");
		buttons[0].setDefaultCapable(true);
		buttons[1] = new ButtonWithMnemonic(AddressbookResourceLoader
				.getString("global", "ok"));
		buttons[1].setEnabled(true);
		buttons[1].setActionCommand("OK");
		buttons[2] = new JButton(AddressbookResourceLoader.getString("tree",
				"folderdialog", "new_subFolder"));
		buttons[2].setActionCommand("NEW");
		buttons[2].setEnabled(false);

		getRootPane().setDefaultButton(buttons[1]);

		tree = new JTree(model);
		tree.expandRow(0);
		tree.expandRow(1);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);

		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new AddressbookTreeCellRenderer(true));

		for (int i = 0; i < 3; i++) {
			buttons[i].addActionListener(this);
		}

		tree.setSelectionPath(new TreePath(AddressbookTreeModel.getInstance()
				.getFolder(101).getPath()));
	}

	public boolean success() {
		return bool;
	}

	public IFolder getSelectedFolder() {
		return selectedFolder;
	}

	public int getUid() {
		/*
		 * FolderTreeNode node = tree.getSelectedNode(); FolderItem item =
		 * node.getFolderItem();
		 */
		return 101;

		// return item.getUid();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (action.equals("OK")) {
			bool = true;
			setVisible(false);
		} else if (action.equals("CANCEL")) {
			bool = false;
			setVisible(false);
		} else if (action.equals("NEW")) {
			/*
			 * EditFolderDialog dialog = treeViewer.getEditFolderDialog( "New
			 * Folder" ); dialog.showDialog(); String name; if (
			 * dialog.success() == true ) { // ok pressed name =
			 * dialog.getName(); } else { // cancel pressed return; }
			 * treeViewer.getFolderTree().addUserFolder( getSelectedFolder(),
			 * name ); //TreeNodeEvent updateEvent2 = new TreeNodeEvent(
			 * getSelectedFolder(), TreeNodeEvent.STRUCTURE_CHANGED );
			 * //treeViewer.mainInterface.crossbar.fireTreeNodeChanged(updateEvent2);
			 */
		}
	}

	/**
	 * ***************************** tree selection listener
	 * *******************************
	 */
	public void valueChanged(TreeSelectionEvent e) {
		AddressbookTreeNode node = (AddressbookTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		FolderItem item = node.getFolderItem();

		if (item.get("type").equals("AddressbookFolder")) {
			buttons[1].setEnabled(true);
			selectedFolder = (AddressbookFolder) node;
		} else {
			buttons[1].setEnabled(false);
		}
	}
}