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
package org.columba.mail.gui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.columba.api.gui.frame.IContentPane;
import org.columba.api.selection.ISelectionListener;
import org.columba.api.selection.SelectionChangedEvent;
import org.columba.core.config.ViewItem;
import org.columba.core.gui.base.UIFSplitPane;
import org.columba.core.gui.docking.DockingPanel;
import org.columba.core.gui.menu.MenuXMLDecoder;
import org.columba.core.io.DiskIO;
import org.columba.mail.command.IMailFolderCommandReference;
import org.columba.mail.config.MailConfig;
import org.columba.mail.folder.IMailFolder;
import org.columba.mail.folder.IMailbox;
import org.columba.mail.gui.composer.HeaderController;
import org.columba.mail.gui.infopanel.FolderInfoPanel;
import org.columba.mail.gui.message.action.ViewMessageAction;
import org.columba.mail.gui.table.FilterToolbar;
import org.columba.mail.gui.table.ITableController;
import org.columba.mail.gui.table.TableController;
import org.columba.mail.gui.table.action.DeleteAction;
import org.columba.mail.gui.table.action.OpenMessageWithComposerAction;
import org.columba.mail.gui.table.action.OpenMessageWithMessageFrameAction;
import org.columba.mail.gui.table.action.ViewHeaderListAction;
import org.columba.mail.gui.table.model.HeaderTableModel;
import org.columba.mail.gui.table.model.MessageNode;
import org.columba.mail.gui.table.selection.TableSelectionChangedEvent;
import org.columba.mail.gui.table.selection.TableSelectionHandler;
import org.columba.mail.gui.tree.FolderTreeModel;
import org.columba.mail.gui.tree.ITreeController;
import org.columba.mail.gui.tree.TreeController;
import org.columba.mail.gui.tree.action.MoveDownAction;
import org.columba.mail.gui.tree.action.MoveUpAction;
import org.columba.mail.gui.tree.action.RenameFolderAction;
import org.columba.mail.gui.tree.action.SortFoldersMenu;
import org.columba.mail.gui.tree.selection.TreeSelectionChangedEvent;
import org.columba.mail.gui.tree.selection.TreeSelectionHandler;
import org.columba.mail.util.MailResourceLoader;
import org.columba.ristretto.message.MailboxInfo;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.StandardBorderManager;
import org.flexdock.docking.drag.effects.EffectsManager;
import org.flexdock.docking.drag.preview.GhostPreview;
import org.flexdock.docking.state.PersistenceException;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.FilePersistenceHandler;
import org.flexdock.perspective.persist.PersistenceHandler;
import org.flexdock.plaf.common.border.ShadowBorder;

/**
 * @author fdietz
 * 
 */
public class ThreePaneMailFrameController extends AbstractMailFrameController
		implements TreeViewOwner, TableViewOwner, IContentPane,
		ISelectionListener {

	private static final String FLEXDOCK_MAIL_CONFIG_XML = "flexdock.xml";

	public TreeController treeController;

	public TableController tableController;

	public HeaderController headerController;

	public FilterToolbar filterToolbar;

	public JSplitPane mainSplitPane;

	public JSplitPane rightSplitPane;

	private JPanel tablePanel;

	private JPanel messagePanel;

	public FolderInfoPanel folderInfoPanel;

	/**
	 * true, if the messagelist table selection event was triggered by a popup
	 * event. False, otherwise.
	 */
	public boolean isTablePopupEvent;

	/**
	 * true, if the tree selection event was triggered by a popup event. False,
	 * otherwise.
	 */
	public boolean isTreePopupEvent;

	private DockingPanel treePanel;

	private DockingPanel messageListPanel;

	private DockingPanel messageViewerPanel;
	
	private DefaultDockingPort portCenter;

	/**
	 * @param container
	 */
	public ThreePaneMailFrameController(ViewItem viewItem) {
		super(viewItem);

		treeController = new TreeController(this, FolderTreeModel.getInstance());
		tableController = new TableController(this);

		// create selection handlers
		TableSelectionHandler tableHandler = new TableSelectionHandler(
				tableController);
		getSelectionManager().addSelectionHandler(tableHandler);
		tableHandler.addSelectionListener(this);

		TreeSelectionHandler treeHandler = new TreeSelectionHandler(
				treeController.getView());
		getSelectionManager().addSelectionHandler(treeHandler);

		// double-click mouse listener
		tableController.getView().addMouseListener(new TableMouseListener());

		treeController.getView().addMouseListener(new TreeMouseListener());

		folderInfoPanel = new FolderInfoPanel(this);

		// table registers interest in tree selection events
		treeHandler.addSelectionListener(tableHandler);

		// also register interest in tree seleciton events
		// for updating the title
		treeHandler.addSelectionListener(this);

		filterToolbar = new FilterToolbar(tableController);

		RenameFolderAction renameFolderAction = new RenameFolderAction(this);

		// Register F2 hotkey for renaming folder when the message panel has
		// focus
		tableController.getView().getActionMap().put("F2", renameFolderAction);
		tableController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "F2");

		// Register F2 hotkey for renaming folder when the folder tree itself
		// has focus
		treeController.getView().getActionMap().put("F2", renameFolderAction);
		treeController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "F2");

		// Register Alt-Up hotkey for moving up folder when folder tree or
		// table have focus
		MoveUpAction moveUpAction = new MoveUpAction(this);
		tableController.getView().getActionMap().put("ALT_UP", moveUpAction);
		tableController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK),
				"ALT_UP");

		treeController.getView().getActionMap().put("ALT_UP", moveUpAction);
		treeController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK),
				"ALT_UP");

		// Register Alt-Down hotkey for moving up folder when folder tree or
		// table have focus
		MoveDownAction moveDownAction = new MoveDownAction(this);
		tableController.getView().getActionMap()
				.put("ALT_DOWN", moveDownAction);
		tableController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK),
				"ALT_DOWN");

		treeController.getView().getActionMap().put("ALT_DOWN", moveDownAction);
		treeController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK),
				"ALT_DOWN");

		DeleteAction deleteAction = new DeleteAction(this);
		tableController.getView().getActionMap().put("DEL", deleteAction);
		tableController.getView().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DEL");

	}

	public void enableMessagePreview(boolean enable) {
		getViewItem().setBoolean("header_enabled", enable);

		if (enable) {
			rightSplitPane = new UIFSplitPane();
			rightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			rightSplitPane.add(tablePanel, JSplitPane.LEFT);
			rightSplitPane.add(messagePanel, JSplitPane.RIGHT);

			mainSplitPane.add(rightSplitPane, JSplitPane.RIGHT);
		} else {
			rightSplitPane = null;

			mainSplitPane.add(tablePanel, JSplitPane.RIGHT);
		}

		mainSplitPane.setDividerLocation(viewItem.getIntegerWithDefault(
				"splitpanes", "main", 100));

		if (enable)
			rightSplitPane.setDividerLocation(viewItem.getIntegerWithDefault(
					"splitpanes", "header", 100));

		getContainer().getFrame().validate();
	}

	/**
	 * @return Returns the filterToolbar.
	 */
	public FilterToolbar getFilterToolbar() {
		return filterToolbar;
	}

	/**
	 * @see org.columba.mail.gui.frame.TreeViewOwner#getTreeController()
	 */
	public ITreeController getTreeController() {
		return treeController;
	}

	/**
	 * @see org.columba.mail.gui.frame.TableViewOwner#getTableController()
	 */
	public ITableController getTableController() {
		return tableController;
	}

	/**
	 * @see org.columba.api.gui.frame.IContentPane#getComponent()
	 */
	public JComponent getComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		// enable flexdock ghost preview
		EffectsManager.setPreview(new GhostPreview());
		// create the dockingPort
		portCenter = new DefaultDockingPort();
		portCenter.setBorderManager(new StandardBorderManager(
				new ShadowBorder()));
		panel.add(portCenter, BorderLayout.CENTER);

		// init dockable panels
		treePanel = new DockingPanel("mail_foldertree", "Folder Tree");
		InputStream is = null;
		// FIXME: still playing around with menu items
		// try {
		// is = DiskIO
		// .getResourceStream("org/columba/mail/action/tree_dockmenu.xml");
		// treePanel
		// .setPopupMenu(new MenuXMLDecoder(this).createPopupMenu(is));
		// } catch (IOException e1) {
		// }
		treePanel.setPopupMenu(new SortFoldersMenu(this));

		treePanel.add(new JScrollPane(treeController.getView()));

		messageListPanel = new DockingPanel("mail_folderlist", "Message List");
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JScrollPane(tableController.getView()), BorderLayout.CENTER);
		p.add(filterToolbar, BorderLayout.NORTH);
		messageListPanel.add(p);
		try {
			is = DiskIO
					.getResourceStream("org/columba/mail/action/table_dockmenu.xml");
			messageListPanel.setPopupMenu(new MenuXMLDecoder(this)
					.createPopupMenu(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		messageViewerPanel = new DockingPanel("mail_messageviewer",
				"Message Viewer");

		messageViewerPanel.add(messageController);
		try {
			is = DiskIO
					.getResourceStream("org/columba/mail/action/message_dockmenu.xml");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		messageViewerPanel.setPopupMenu(new MenuXMLDecoder(this)
				.createPopupMenu(is));

		try {
			is = DiskIO.getResourceStream("org/columba/mail/action/menu.xml");
			getContainer().extendMenu(this, is);

			File configDirectory = MailConfig.getInstance().getConfigDirectory();
			InputStream is2 = new FileInputStream(new File(configDirectory,
					"main_toolbar.xml"));
			getContainer().extendToolbar(this, is2);

		} catch (IOException e) {
			e.printStackTrace();
		}

		tableController.createPopupMenu();
		treeController.createPopupMenu();
		messageController.createPopupMenu();

		// simply example showing how-to add a new action to the menu
		// JFrame frame = (JFrame) getContainer().getFrame();
		// ColumbaMenu menu = (ColumbaMenu) frame.getJMenuBar();
		// menu.addMenuItem("my_reply_action_id", new ReplyAction(this),
		// ColumbaMenu.MENU_VIEW, ColumbaMenu.PLACEHOLDER_BOTTOM);

		return panel;
	}

	public void showFilterToolbar() {
		tablePanel.add(filterToolbar, BorderLayout.NORTH);
		tablePanel.validate();

	}

	public void hideFilterToolbar() {
		tablePanel.remove(filterToolbar);
		tablePanel.validate();

	}

	// public void savePositions(ViewItem viewItem) {
	// super.savePositions(viewItem);
	//
	// // splitpanes
	// viewItem.setInteger("splitpanes", "main", mainSplitPane
	// .getDividerLocation());
	//
	// if (rightSplitPane != null)
	// viewItem.setInteger("splitpanes", "header", rightSplitPane
	// .getDividerLocation());
	// viewItem.setBoolean("splitpanes", "header_enabled",
	// rightSplitPane != null);
	//
	//		
	// }

	/**
	 * @return Returns the folderInfoPanel.
	 */
	public FolderInfoPanel getFolderInfoPanel() {
		return folderInfoPanel;
	}

	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#getString(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String getString(String sPath, String sName, String sID) {
		return MailResourceLoader.getString(sPath, sName, sID);
	}

	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#getContentPane()
	 */
	public IContentPane getContentPane() {
		return this;
	}

	/**
	 * @see org.columba.api.selection.ISelectionListener#selectionChanged(org.columba.api.selection.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent e) {

		if (e instanceof TreeSelectionChangedEvent) {
			// tree selection event
			TreeSelectionChangedEvent event = (TreeSelectionChangedEvent) e;

			IMailFolder[] selectedFolders = event.getSelected();

			if (isTreePopupEvent == false) {
				// view headerlist in message list viewer
				new ViewHeaderListAction(this).actionPerformed(null);

				getFolderInfoPanel().selectionChanged(e);

				// update frame title
				if (selectedFolders.length == 1 && selectedFolders[0] != null) {
					getContainer().getFrame().setTitle(
							selectedFolders[0].getName());

					// simply demonstration of how to change the docking title
					if (selectedFolders[0] instanceof IMailbox) {
						MailboxInfo info = ((IMailbox) selectedFolders[0])
								.getMessageFolderInfo();
						StringBuffer buf = new StringBuffer();
						buf.append("total: " + info.getExists());
						buf.append(" unread: " + info.getUnseen());
						buf.append(" recent: " + info.getRecent());
						treePanel.setTitle(buf.toString());
					} else
						treePanel.setTitle(selectedFolders[0].getName());
				} else {
					getContainer().getFrame().setTitle("");
				}
			}

			isTreePopupEvent = false;

		} else if (e instanceof TableSelectionChangedEvent) {
			// messagelist table selection event
			TableSelectionChangedEvent event = (TableSelectionChangedEvent) e;

			if (isTablePopupEvent == false)
				// show message content
				new ViewMessageAction(this).actionPerformed(null);

			isTablePopupEvent = false;
		} else
			throw new IllegalArgumentException(
					"unknown selection changed event");
	}

	/**
	 * Double-click mouse listener for message list table component.
	 * <p>
	 * If message is marked as draft, the composer will be opened to edit the
	 * message. Otherwise, the message will be viewed in the message frame.
	 * 
	 * @author Frederik Dietz
	 */
	class TableMouseListener extends MouseAdapter {

		/**
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent event) {
			if (event.isPopupTrigger()) {
				processPopup(event);
			}
		}

		/**
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent event) {
			if (event.isPopupTrigger()) {
				processPopup(event);
			}
		}

		/**
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent event) {
			// if mouse button was pressed twice times
			if (event.getClickCount() == 2) {
				// get selected row
				int selectedRow = tableController.getView().getSelectedRow();

				// get message node at selected row
				MessageNode node = (MessageNode) ((HeaderTableModel) tableController
						.getHeaderTableModel())
						.getMessageNodeAtRow(selectedRow);

				// is the message marked as draft ?
				boolean markedAsDraft = node.getHeader().getFlags().getDraft();

				if (markedAsDraft) {
					// edit message in composer
					new OpenMessageWithComposerAction(
							ThreePaneMailFrameController.this)
							.actionPerformed(null);
				} else {
					// open message in new message-frame
					new OpenMessageWithMessageFrameAction(
							ThreePaneMailFrameController.this)
							.actionPerformed(null);
				}
			}
		}

		protected void processPopup(final MouseEvent event) {

			isTablePopupEvent = true;

			JTable table = tableController.getView();

			int selectedRows = table.getSelectedRowCount();

			if (selectedRows <= 1) {
				// select node
				int row = table
						.rowAtPoint(new Point(event.getX(), event.getY()));
				table.setRowSelectionInterval(row, row);
			}

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					tableController.getPopupMenu().show(event.getComponent(),
							event.getX(), event.getY());
					isTablePopupEvent = false;
				}
			});
		}
	}

	class TreeMouseListener extends MouseAdapter {

		/**
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent event) {
			if (event.isPopupTrigger()) {
				processPopup(event);
			}
		}

		/**
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent event) {
			if (event.isPopupTrigger()) {
				processPopup(event);
			}
		}

		/**
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent event) {
			// if mouse button was pressed twice times
			if (event.getClickCount() == 2) {
				// get selected row

			}
		}

		protected void processPopup(final MouseEvent event) {

			isTreePopupEvent = true;

			Point point = event.getPoint();
			TreePath path = treeController.getView().getClosestPathForLocation(
					point.x, point.y);
			treeController.getView().setSelectionPath(path);

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					treeController.getPopupMenu().show(event.getComponent(),
							event.getX(), event.getY());
					isTreePopupEvent = false;
				}
			});
		}
	}

	/**
	 * @see org.columba.core.gui.frame.DefaultFrameController#close()
	 */
	public void close() {
		super.close();

		

		IMailFolderCommandReference r = getTreeSelection();

		if (r != null) {
			IMailFolder folder = (IMailFolder) r.getSourceFolder();

			// folder-based configuration

			if (folder instanceof IMailbox)
				getFolderOptionsController().save((IMailbox) folder);
		}
	}

	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#loadPositions()
	 */
	public void loadPositions() {
		boolean restoreSuccess = false;
		// load docking settings from last user session
		File configDirectory = MailConfig.getInstance().getConfigDirectory();

		try {
			// load persistence handler
			PersistenceHandler persister = new FilePersistenceHandler(new File(
					configDirectory,
					ThreePaneMailFrameController.FLEXDOCK_MAIL_CONFIG_XML));
			PerspectiveManager.setPersistenceHandler(persister);
			// load layout into memory
			restoreSuccess = DockingManager.loadLayoutModel();
			// store ui using in-memory layout model
			if (restoreSuccess)
				restoreSuccess = DockingManager.restoreLayout();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}

		if (!restoreSuccess) {
			// make sure there is nothing within the root dockingport
			portCenter.clear();

			// DockingManager.dock(messageListPanel, (DockingPort) portCenter);
			portCenter.dock(messageListPanel, DockingConstants.CENTER_REGION);
			messageListPanel
					.dock(treePanel, DockingConstants.WEST_REGION, 0.3f);
			messageListPanel.dock(messageViewerPanel,
					DockingConstants.SOUTH_REGION, 0.3f);

			DockingManager.setSplitProportion(treePanel, 0.3f);
			DockingManager.setSplitProportion(messageListPanel, 0.35f);
		}
	}

	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#savePositions()
	 */
	public void savePositions() {
		try {
			File configDirectory = MailConfig.getInstance()
					.getConfigDirectory();
			PersistenceHandler persister = new FilePersistenceHandler(new File(
					configDirectory,
					ThreePaneMailFrameController.FLEXDOCK_MAIL_CONFIG_XML));
			PerspectiveManager.setPersistenceHandler(persister);
			DockingManager.storeLayoutModel();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}
}