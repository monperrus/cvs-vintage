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
package org.columba.mail.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.columba.core.config.ViewItem;
import org.columba.core.gui.frame.AbstractFrameController;
import org.columba.core.gui.frame.AbstractFrameView;
import org.columba.core.gui.menu.Menu;
import org.columba.core.gui.statusbar.StatusBar;
import org.columba.core.gui.toolbar.ToolBar;
import org.columba.mail.config.MailConfig;
import org.columba.mail.gui.composer.HeaderView;
import org.columba.mail.gui.infopanel.FolderInfoPanel;
import org.columba.mail.gui.menu.MailMenu;
import org.columba.mail.gui.message.MessageView;
import org.columba.mail.gui.table.FilterToolbar;
import org.columba.mail.gui.table.TableView;
import org.columba.mail.gui.tree.TreeView;

public class MailFrameView extends AbstractFrameView {

	public static final String FOLDERINFOPANEL = "folderinfopanel";

	//private StatusBar statusBar;
	public JSplitPane mainSplitPane;
	public JSplitPane rightSplitPane;

	private FolderInfoPanel folderInfoPanel;

	public ResourceBundle guiLabels;

	private JPanel tablePanel;

	FilterToolbar filterToolbar;
	HeaderView header;

	public MailFrameView(AbstractFrameController frameController) {
		super(frameController);

		//MainInterface.mainFrame = this;

		//changeToolbars();
		//MainInterface.frameModel.register(this);

	}

	/*
	public void showAttachmentViewer() {
		rightSplitPane.showAttachmentViewer();
	}
	
	public void hideAttachmentViewer() {
		rightSplitPane.hideAttachmentViewer();
	}
	*/

	public void setFolderInfoPanel(FolderInfoPanel f) {
		this.folderInfoPanel = f;
	}

	public void init(
		TreeView tree,
		TableView table,
		FilterToolbar filterToolbar,
		MessageView message,
		StatusBar statusBar) {

		this.filterToolbar = filterToolbar;

		//this.statusBar = statusBar;

		mainSplitPane = new JSplitPane();
		//mainSplitPane.setDividerSize(5);

		//mainSplitPane.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

		this.getContentPane().add(mainSplitPane, BorderLayout.CENTER);

		mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		mainSplitPane.add(new JScrollPane(tree), JSplitPane.LEFT);

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		//messagePanel.add( header, BorderLayout.NORTH );
		messagePanel.add(message, BorderLayout.CENTER);

		tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());

		ViewItem viewItem = getFrameController().getViewItem();

		
		//if (viewItem.getBoolean("toolbars", "filter", true) == true)
		tablePanel.add(filterToolbar, BorderLayout.NORTH);

		JScrollPane tableScrollPane = new JScrollPane(table);

		tableScrollPane.getViewport().setBackground(Color.white);
		tablePanel.add(tableScrollPane, BorderLayout.CENTER);
		rightSplitPane = new JSplitPane();
		rightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		rightSplitPane.add(tablePanel, JSplitPane.LEFT);
		rightSplitPane.add(messagePanel, JSplitPane.RIGHT);

		mainSplitPane.add(rightSplitPane, JSplitPane.RIGHT);

		// same as menu

		if (frameController.isToolbarEnabled(MailFrameView.FOLDERINFOPANEL) == true)
			toolbarPane.add(folderInfoPanel);

		int count = MailConfig.getAccountList().count();

		if (count == 0) {
			pack();
			rightSplitPane.setDividerLocation(150);
		} else {
			mainSplitPane.setDividerLocation(
				viewItem.getInteger("splitpanes", "main"));

			rightSplitPane.setDividerLocation(
				viewItem.getInteger("splitpanes", "header"));
		}

	}

	public void setToolBar(ToolBar toolBar) {
		this.toolbar = toolBar;
	}

	/*
	public void hideToolbar(boolean b) {
		toolbarPane.remove(toolbar);

		validate();
		repaint();

	}

	public void hideFolderInfo(boolean b) {

		toolbarPane.remove(folderInfoPanel);
		validate();
		repaint();

	}

	public void showFolderInfo(boolean b) {

		if (b) {
			toolbarPane.removeAll();
			toolbarPane.add(toolbar);
			toolbarPane.add(folderInfoPanel);

			validate();
			repaint();
		} else {

			toolbarPane.add(folderInfoPanel);

			validate();
			repaint();
		}

	}
	*/
	
	public void showFilterToolbar() {
		tablePanel.add(filterToolbar, BorderLayout.NORTH);
		tablePanel.validate();
		repaint();
	}

	public void hideFilterToolbar() {
		tablePanel.remove(filterToolbar);
		tablePanel.validate();
		repaint();
	}

	public void saveWindowPosition() {
		super.saveWindowPosition();

		ViewItem viewItem = frameController.getViewItem();

		viewItem.set("splitpanes", "main", mainSplitPane.getDividerLocation());
		viewItem.set(
			"splitpanes",
			"header",
			rightSplitPane.getDividerLocation());

	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.FrameView#createMenu(org.columba.core.gui.FrameController)
	 */
	protected Menu createMenu(AbstractFrameController controller) {
		Menu menu =
			new MailMenu("org/columba/core/action/menu.xml", controller);

		return menu;
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.FrameView#createToolbar(org.columba.core.gui.FrameController)
	 */
	protected ToolBar createToolbar(AbstractFrameController controller) {
		return new ToolBar(
			MailConfig.get("main_toolbar").getElement("toolbar"),
			controller);
	}

	/* (non-Javadoc)
	 * @see org.columba.core.gui.frame.AbstractFrameView#showToolbar()
	 */
	public void showToolbar() {

		boolean b = isToolbarVisible();

		if (toolbar == null)
			return;

		if (b) {
			toolbarPane.remove(toolbar);
			frameController.enableToolbar(MAIN_TOOLBAR, false);

		} else {
			if (isFolderInfoPanelVisible()) {
				toolbarPane.removeAll();
				toolbarPane.add(toolbar);
				toolbarPane.add(getFolderInfoPanel());
			} else
				toolbarPane.add(toolbar);

			frameController.enableToolbar(MAIN_TOOLBAR, true);

		}

		validate();
		repaint();
	}

	public void showFolderInfoPanel() {
		boolean b = isFolderInfoPanelVisible();

		if (b) {
			toolbarPane.remove(getFolderInfoPanel());
			frameController.enableToolbar(FOLDERINFOPANEL, false);
		} else {

			toolbarPane.add(getFolderInfoPanel());

			frameController.enableToolbar(FOLDERINFOPANEL, true);
		}
		
		validate();
				repaint();
	}

	public boolean isFolderInfoPanelVisible() {
		return frameController.isToolbarEnabled(FOLDERINFOPANEL);
	}

	/**
	 * @return
	 */
	public FilterToolbar getFilterToolbar() {
		return filterToolbar;
	}

	/**
	 * @return
	 */
	public FolderInfoPanel getFolderInfoPanel() {
		return folderInfoPanel;
	}

}