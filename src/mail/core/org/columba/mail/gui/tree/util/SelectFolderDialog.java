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
package org.columba.mail.gui.tree.util;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.columba.core.gui.util.DialogStore;
import org.columba.core.main.MainInterface;
import org.columba.mail.command.FolderCommandReference;
import org.columba.mail.config.FolderItem;
import org.columba.mail.folder.Folder;
import org.columba.mail.gui.tree.command.CreateSubFolderCommand;

public class SelectFolderDialog
	implements ActionListener, TreeSelectionListener {
	private String name;

	private boolean bool = false;

	//public SelectFolderTree tree;

	private JTree tree;

	private JButton okButton, cancelButton, newButton;

	//private TreeController treeController;
	//private TreeModel treeModel;

	private Folder selectedFolder;

	//private JFrame frame;

	protected JDialog dialog;
	
	

	public SelectFolderDialog( ) {
		
		
		dialog = DialogStore.getDialog("Select Folder...");

		//this.treeController = treeController;
		//this.frame = frame;

		name = new String("name");

		init();

	}

	public void init() {

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

		//tree = new SelectFolderTree( mainInterface, MainInterface.config.getFolderConfig().getRootNode()  );
		//tree.getTree().addTreeSelectionListener( this );

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(),
					" Choose Folder "),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		tree = new JTree(MainInterface.treeModel);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
				
		FolderTreeCellRenderer renderer = new FolderTreeCellRenderer(true);
		tree.setCellRenderer(renderer);
		
		
		centerPanel.add(new JScrollPane(tree), BorderLayout.CENTER);
		contentPane.add(centerPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(17, 0, 0, 0));
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
		okButton = new JButton("Ok");
		okButton.setEnabled(true);
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		newButton = new JButton("New Subfolder...");
		newButton.setEnabled(true);
		newButton.setActionCommand("NEW");
		newButton.addActionListener(this);
		buttonPanel.add(newButton);
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		dialog.setContentPane(contentPane);
		dialog.getRootPane().setDefaultButton(okButton);
		dialog.getRootPane().registerKeyboardAction(
			this,
			"CANCEL",
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
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

			EditFolderDialog dialog = new EditFolderDialog("New Folder");
			dialog.showDialog();

			String name;

			if (dialog.success() == true) {
				// ok pressed
				name = dialog.getName();
			} else {
				// cancel pressed
				return;
			}

			/*
			FolderCommandReference[] r =
				(FolderCommandReference[]) treeController
					.getTreeSelectionManager()
					.getSelection();
			*/
			FolderCommandReference[] r = new FolderCommandReference[1];
			r[0] = new FolderCommandReference( getSelectedFolder() );
			r[0].setFolderName(name);

			MainInterface.processor.addOp(new CreateSubFolderCommand(r));
			/*
			EditFolderDialog dialog =
				treeController.getEditFolderDialog("New Folder");
			dialog.showDialog();
			
			String name;
			
			if (dialog.success()) {
				// ok pressed
				name = dialog.getName();
			} else {
				// cancel pressed
				return;
			}
			*/

			// FIXME

			//MainInterface.treeModel.addUserFolder( getSelectedFolder(), name );

			//TreeNodeEvent updateEvent2 = new TreeNodeEvent( getSelectedFolder(), TreeNodeEvent.STRUCTURE_CHANGED );
			//TreeController.MainInterface.crossbar.fireTreeNodeChanged(updateEvent2);

			//FIXME

			/*
			FolderTreeNode parent = tree.getSelectedNode();
			                    //FolderItem parentItem = parent.getItem();
			
			AdapterNode adapterNode = MainInterface.config.getFolderConfig().addFolderNode( parent.getNode() , name, "user", "true","columba", "true", "true", "true", -1 );
			
			FolderItem item = MainInterface.config.getFolderConfig().getFolderItem( adapterNode );
			if ( item == null )
			{
			    System.out.println("failed");
			}
			*/
			//FIXME

			/*
			FolderTreeNode child = new FolderTreeNode( item, adapterNode );
			tree.getFolderTree().add( parent, child );
			
			AdapterNode parentNode = parent.getNode();
			AdapterNode uidNode = parentNode.getChild("uid");
			String uidString = uidNode.getValue();
			int uid = Integer.parseInt( uidString );
			
			FolderTreeNode mainParent = MainInterface.TreeController.getFolderTreeNode( uid );
			
			
			
			MainInterface.treeViewer.getFolderTree().add(
			    mainParent, child );
			
			
			
			Object[] nodeList = child.getPath();
			TreeNodeList list = new TreeNodeList();
			for ( int j=1; j<nodeList.length; j++)
			{
			    TreeNode n = (TreeNode) nodeList[j];
			    list.add( n.toString() );
			    System.out.println("path: "+n.toString() );
			
			}
			
			ColumbaFolder folder = new ColumbaFolder( list, item.getUid(), mainInterface );
			child.setFolder( folder );
			
			*/
		}

	}

	/*
	public String getTreePath()
	{
	
	    FolderTreeNode node = tree.getSelectedNode();
	    String item;
	    StringBuffer buf = new StringBuffer();
	    TreeNode[] nodeList = node.getPath();
	
	    for ( int i=1; i<nodeList.length; i++)
	    {
	        node = (FolderTreeNode) nodeList[i];
	        item = node.toString();
	        buf.append( "/" );
	        buf.append( item );
	
	        System.out.println("node: "+ buf.toString());
	    }
	
	    return buf.toString();
	
	}
	*/
	/******************************* tree selection listener ********************************/

	public void valueChanged(TreeSelectionEvent e) {
		Folder node = (Folder) tree.getLastSelectedPathComponent();
		if (node == null)
			return;
		selectedFolder = node;
		FolderItem item = node.getFolderItem();
		//okButton.setEnabled(item.isAddAllowed());
		//newButton.setEnabled(item.isSubfolderAllowed());
	}
}
