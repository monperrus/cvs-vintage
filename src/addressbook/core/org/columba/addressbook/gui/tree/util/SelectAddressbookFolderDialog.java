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

package org.columba.addressbook.gui.tree.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;

import org.columba.addressbook.config.FolderItem;
import org.columba.addressbook.folder.AddressbookFolder;
import org.columba.addressbook.folder.Folder;
import org.columba.addressbook.gui.tree.AddressbookTreeNode;
import org.columba.addressbook.util.AddressbookResourceLoader;
import org.columba.core.gui.util.ButtonWithMnemonic;
import org.columba.core.gui.util.DialogStore;

public class SelectAddressbookFolderDialog
	implements ActionListener, TreeSelectionListener {
	private String name;

	//private MainInterface mainInterface;

	private boolean bool = false;

	//public SelectFolderTree tree;

	private JTree tree;

	private JButton[] buttons;

	//private TreeView treeViewer;

	private Folder selectedFolder;

	private TreeModel model;

	private JDialog dialog;

	public SelectAddressbookFolderDialog(TreeModel model) {
		dialog =
			DialogStore.getDialog(
				AddressbookResourceLoader.getString(
					"tree",
					"folderdialog",
					"select_folder"));
		//super(AddressbookResourceLoader.getString("tree", "folderdialog", "select_folder"), true);

		this.model = model;

		//this.mainInterface = mainInterface;
		//this.treeViewer = treeViewer;

		name = new String("name");

		init();
	}

	public void init() {
		buttons = new JButton[3];

		JLabel label2 =
			new JLabel(
				AddressbookResourceLoader.getString(
					"tree",
					"folderdialog",
					"select_folder"));

		buttons[0] = new ButtonWithMnemonic(
				AddressbookResourceLoader.getString("global", "cancel"));
		buttons[0].setActionCommand("CANCEL");
		buttons[0].setDefaultCapable(true);
		buttons[1] = new ButtonWithMnemonic(
				AddressbookResourceLoader.getString("global", "ok"));
		buttons[1].setEnabled(true);
		buttons[1].setActionCommand("OK");
		buttons[2] =
			new JButton(
				AddressbookResourceLoader.getString(
					"tree",
					"folderdialog",
					"new_subFolder"));
		buttons[2].setActionCommand("NEW");
		buttons[2].setEnabled(false);

		dialog.getRootPane().setDefaultButton(buttons[1]);

		//tree = new SelectFolderTree( mainInterface, mainInterface.config.getFolderConfig().getRootNode()  );
		//tree.getTree().addTreeSelectionListener( this );

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new BorderLayout());

		dialog.getContentPane().setLayout(new BorderLayout());

		//getContentPane().setLayout( new BoxLayout( getContentPane() , BoxLayout.Y_AXIS ) );

		//getContentPane().add(  Box.createRigidArea( new java.awt.Dimension(0,10) )  );

		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				AddressbookResourceLoader.getString(
					"tree",
					"folderdialog",
					"choose_folder")));
		centerPanel.setLayout(new BorderLayout());

		tree = new JTree(model);
		tree.expandRow(0);
		tree.expandRow(1);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new AddressbookTreeCellRenderer(true));

		//FolderTreeCellRenderer renderer = new FolderTreeCellRenderer( true );
		//tree.setCellRenderer(renderer);

		JPanel treePanel = new JPanel();
		treePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		treePanel.setLayout(new BorderLayout());
		JScrollPane treePane = new JScrollPane(tree);
		treePanel.add(treePane, BorderLayout.CENTER);
		centerPanel.add(treePanel, BorderLayout.CENTER);

		panel.add(centerPanel, BorderLayout.CENTER);

		//getContentPane().add(  Box.createRigidArea( new java.awt.Dimension(0,10) )  );

		JPanel lowerpanel = new JPanel();
		lowerpanel.setLayout(new BoxLayout(lowerpanel, BoxLayout.X_AXIS));
		lowerpanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		lowerpanel.add(Box.createHorizontalGlue());
		//lowerpanel.add(  Box.createRigidArea( new java.awt.Dimension(20,0) )  );
		lowerpanel.add(buttons[0]);
		lowerpanel.add(Box.createRigidArea(new java.awt.Dimension(10, 0)));
		lowerpanel.add(Box.createHorizontalGlue());
		lowerpanel.add(buttons[2]);
		lowerpanel.add(Box.createHorizontalGlue());
		lowerpanel.add(Box.createRigidArea(new java.awt.Dimension(10, 0)));
		lowerpanel.add(buttons[1]);
		//lowerpanel.add(  Box.createRigidArea( new java.awt.Dimension(20,0) )  );
		lowerpanel.add(Box.createHorizontalGlue());

		panel.add(lowerpanel, BorderLayout.SOUTH);

		dialog.getContentPane().add(panel, BorderLayout.CENTER);

		//getContentPane().add(  Box.createRigidArea( new java.awt.Dimension(0,10) )  );

		for (int i = 0; i < 3; i++) {
			buttons[i].addActionListener(this);
		}

		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public boolean success() {
		return bool;
	}

	public Folder getSelectedFolder() {
		return selectedFolder;
	}

	public int getUid() {
		/*
		  FolderTreeNode node = tree.getSelectedNode();
		
		  FolderItem item = node.getFolderItem();
		*/
		return 101;

		//return item.getUid();

	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("OK")) {
			//name = textField.getText();

			bool = true;
			dialog.dispose();
		} else if (action.equals("CANCEL")) {
			bool = false;
			dialog.dispose();
		} else if (action.equals("NEW")) {
			/*
			EditFolderDialog dialog = treeViewer.getEditFolderDialog( "New Folder" );
			dialog.showDialog();
			
			String name;
			
			if ( dialog.success() == true )
			{
			      // ok pressed
			    name = dialog.getName();
			}
			else
			{
			      // cancel pressed
			    return;
			}
			
			treeViewer.getFolderTree().addUserFolder( getSelectedFolder(), name );
			
			//TreeNodeEvent updateEvent2 = new TreeNodeEvent( getSelectedFolder(), TreeNodeEvent.STRUCTURE_CHANGED );
			//treeViewer.mainInterface.crossbar.fireTreeNodeChanged(updateEvent2);
			
			*/

		}
	}

	/******************************* tree selection listener ********************************/

	public void valueChanged(TreeSelectionEvent e) {

		AddressbookTreeNode node =
			(AddressbookTreeNode) tree.getLastSelectedPathComponent();
		if (node == null)
			return;

		FolderItem item = node.getFolderItem();

		if (item.get("type").equals("AddressbookFolder")) {
			buttons[1].setEnabled(true);
			selectedFolder = (AddressbookFolder) node;
		} else {
			buttons[1].setEnabled(false);
		}

		

	}
}
