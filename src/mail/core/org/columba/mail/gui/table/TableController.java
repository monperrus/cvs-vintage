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
package org.columba.mail.gui.table;

import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import org.columba.core.folder.IFolder;
import org.columba.core.folder.IFolderCommandReference;
import org.columba.core.gui.focus.FocusManager;
import org.columba.core.gui.frame.FrameMediator;
import org.columba.core.gui.menu.ExtendablePopupMenu;
import org.columba.core.gui.menu.MenuXMLDecoder;
import org.columba.mail.command.IMailFolderCommandReference;
import org.columba.mail.folder.IMailFolder;
import org.columba.mail.folder.IMailbox;
import org.columba.mail.folder.event.FolderEventDelegator;
import org.columba.mail.folderoptions.FolderOptionsController;
import org.columba.mail.gui.frame.MailFrameMediator;
import org.columba.mail.gui.table.action.ViewMessageAction;
import org.columba.mail.gui.table.model.HeaderTableModel;
import org.columba.mail.gui.table.model.MessageNode;
import org.columba.mail.gui.table.model.TableModelChangedEvent;
import org.columba.mail.gui.table.model.TableModelChangedListener;
import org.columba.mail.gui.table.model.TableModelSorter;
import org.columba.mail.gui.table.model.TableModelThreadedView;
import org.columba.mail.gui.table.util.MarkAsReadTimer;
import org.columba.mail.message.IColumbaHeader;
import org.columba.mail.message.IHeaderList;
import org.columba.mail.util.MailResourceLoader;
import org.frapuccino.treetable.Tree;

/**
 * Shows the message list. By default, this is the read/unread state if a
 * message, Subject:, Date:, From: and Size headerfields.
 * <p>
 * Folder-specific configuration options are handled by
 * {@link FolderOptionsController}and can be configured by the user in the
 * AbstractMessageFolder Options Dialog.
 * 
 * @author fdietz
 */
public class TableController implements ListSelectionListener,
		TableModelChangedListener, ITableController {

	/** JDK 1.4+ logging framework logger, used for logging. */
	private static final Logger LOG = Logger
			.getLogger("org.columba.mail.gui.table");

	/**
	 * table model
	 */
	private HeaderTableModel headerTableModel;

	/**
	 * mouse listener responsible for sorting actions fired by the user
	 * selecting the column headers of the table
	 */
	private HeaderTableMouseListener headerTableMouseListener;

	/**
	 * table view
	 */
	protected TableView view;

	/**
	 * reference to mail framemediator
	 */
	protected FrameMediator frameController;

	/**
	 * timer which marks a message as read after a certain amount of time.
	 */
	protected MarkAsReadTimer markAsReadTimer;

	/**
	 * table view context menu
	 */
	protected ExtendablePopupMenu menu;

	/**
	 * sorting model
	 */
	protected TableModelSorter tableModelSorter;

	/**
	 * threaded-view model
	 */
	protected TableModelThreadedView tableModelThreadedView;

	/**
	 * previously selected rows
	 */
	protected int[] previouslySelectedRows;

	private MessageNode[] previouslySelectedNodes;

	/**
	 * previously selected folder
	 */
	private IMailbox previouslySelectedFolder;

	/**
	 * tooltip mouse handler
	 */
	private ColumnHeaderTooltips tips;

	/**
	 * Constructor
	 * 
	 * @param mailFrameController
	 *            mail framemediator
	 */
	public TableController(FrameMediator frameController) {
		this.frameController = frameController;

		// init table model
		headerTableModel = new HeaderTableModel();

		// init threaded-view model
		tableModelThreadedView = new TableModelThreadedView();
		headerTableModel.registerVisitor(tableModelThreadedView);

		// init sorting model
		tableModelSorter = new TableModelSorter();
		headerTableModel.registerVisitor(tableModelSorter);

		// init view
		view = new TableView(headerTableModel, tableModelSorter);

		// pass tree to model, used by the threaded-view
		headerTableModel.setTree((Tree) view.getTree());

		// init mouse listener for the column header
		headerTableMouseListener = new HeaderTableMouseListener(this);
		view.addMouseListener(headerTableMouseListener);

		// create a new markAsReadTimer
		markAsReadTimer = new MarkAsReadTimer(this);

		getView().setTransferHandler(
				new TableViewTransferHandler(getFrameController()));
		getView().setDragEnabled(true);

		// MouseListener sorts table when clicking on a column header
		new TableHeaderMouseListener(this, getTableModelSorter());

		// we need this for the focus manager
		getView().getSelectionModel().addListSelectionListener(this);

		JTableHeader header = view.getTableHeader();

		tips = new ColumnHeaderTooltips();
		header.addMouseMotionListener(tips);

		// register interest on folder events
		FolderEventDelegator.getInstance().addTableListener(this);
		
	}

	/**
	 * Assigns a tooltip for each column
	 * <p>
	 * Tooltips for columns can be found in
	 * org.columba.mail.i18n.header.header.properties.
	 * 
	 * @see org.columba.mail.folderoptions.ColumnOptionsPlugin
	 * 
	 */
	public void initTooltips() {
		tips.clear();

		// Assign a tooltip for each of the columns
		for (int c = 0; c < view.getColumnCount(); c++) {
			TableColumn col = view.getColumnModel().getColumn(c);

			// column IDs are all lower case
			String lookup = ((String) col.getIdentifier()).toLowerCase();

			// append "_tooltip"
			lookup = lookup + "_tooltip";

			// get translation
			String s = MailResourceLoader.getString("header", "header", lookup);

			tips.setToolTip(col, s);
		}
	}

	/**
	 * Get view of table controller
	 * 
	 * @return table view
	 */
	public TableView getView() {
		return view;
	}

	/**
	 * Get table model
	 * 
	 * @return table model
	 */
	public IHeaderTableModel getHeaderTableModel() {
		return headerTableModel;
	}

	/**
	 * Select messages with UIDs.
	 * <p>
	 * Message UIDs are converted to {@link MessageNode}objects.
	 * 
	 * @param uids
	 *            array of message UIDs
	 */
	public void setSelected(Object[] uids) {

		// select nodes
		MessageNode[] nodes = new MessageNode[uids.length];

		for (int i = 0; i < uids.length; i++) {
			nodes[i] = ((HeaderTableModel) getHeaderTableModel())
					.getMessageNode(uids[i]);
		}

		int[] rows = new int[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			TreePath path = new TreePath(nodes[i].getPath());
			rows[i] = view.getTree().getRowForPath(path);

		}
		view.selectRow(rows[0]);

	}

	/** ************************ actions ******************************* */
	/**
	 * create the PopupMenu
	 */
	public void createPopupMenu() {
		menu = new MenuXMLDecoder(getFrameController())
		.createPopupMenu(
				"org/columba/mail/action/table_contextmenu.xml");
	}

	/**
	 * Get popup menu
	 * 
	 * @return popup menu
	 */
	public JPopupMenu getPopupMenu() {
		return menu;
	}

	/**
	 * Method is called if folder data changed.
	 * <p>
	 * It is responsible for updating the correct underlying model.
	 * 
	 * @param event
	 *            update event
	 */
	public void tableChanged(TableModelChangedEvent event) {

		// selected rows before updating the model
		// -> used later to restore the selection
		previouslySelectedRows = view.getSelectedRows();

		// folder in which the update occurs
		IFolder folder = event.getSrcFolder();

		if (folder == null) {
			return;
		}

		LOG.info("source folder=" + folder.getName());

		// get current selection
		IFolderCommandReference r = (IFolderCommandReference) ((MailFrameMediator) frameController)
				.getTableSelection();
		IFolder srcFolder = r.getSourceFolder();

		// its always possible that no folder is currenlty selected
		if (srcFolder != null) {
			LOG.info("selected folder=" + srcFolder.getName());
		}

		// update infopanel (gray panel below the toolbar)
		// showing total/unread/recent messages count
		/*
		 * if (getFrameController() instanceof MailFrameMediator) { if
		 * (srcFolder != null) { ((ThreePaneMailFrameController)
		 * getFrameController()) .getFolderInfoPanel().setFolder((IMailFolder)
		 * srcFolder); } }
		 */

		// only update table if, this folder is the same
		// as the currently selected
		if (!folder.equals(srcFolder)) {
			return;
		}

		switch (event.getEventType()) {
		case -1: {
			getHeaderTableModel().set(event.getHeaderList());

			// FIXME threaded-view auto collapse
			/*
			 * if (getTableModelThreadedView().isEnabled()) { // expand all
			 * unread message nodes for (int i = 0; i < getView().getRowCount();
			 * i++) { System.out.println("i=" + i + " count=" +
			 * getView().getRowCount());
			 * 
			 * TreePath path = getView().getTree().getPathForRow(i); MessageNode
			 * node = (MessageNode) path .getLastPathComponent(); IColumbaHeader
			 * h = node.getHeader(); boolean unseen = !h.getFlags().getSeen();
			 * if (unseen) { getView().getTree().expandPath(path); } } }
			 */
			break;
		}

		case TableModelChangedEvent.UPDATE: {
			getHeaderTableModel().update();

			break;
		}

		case TableModelChangedEvent.REMOVE: {
			getHeaderTableModel().remove(event.getUids());

			break;
		}

		case TableModelChangedEvent.MARK: {
			getHeaderTableModel().modify(event.getUids());

			break;
		}
		}

		// when marking messages, don't touch selection
		if (event.getEventType() == TableModelChangedEvent.MARK)
			return;

		// re-select previous selection
		if (previouslySelectedRows != null) {
			// only re-select if only a single row was formerly selected
			if ((previouslySelectedRows.length == 1)
					&& (previouslySelectedNodes.length > 0)) {
				int row = ((HeaderTableModel) getHeaderTableModel())
						.getRow(previouslySelectedNodes[0]);

				// if message was removed from JTable
				if (row == -1)
					row = previouslySelectedRows[0];

				// select row
				view.selectRow(row);

				// scrolling to the selected row
				getView().makeRowVisible(row);

			}
		}

	}

	/**
	 * Returns the mailFrameController.
	 * 
	 * @return MailFrameController
	 */
	public FrameMediator getFrameController() {
		return frameController;
	}

	/**
	 * Returns the markAsReadTimer.
	 * 
	 * @return MarkAsReadTimer
	 */
	public MarkAsReadTimer getMarkAsReadTimer() {
		return markAsReadTimer;
	}

	/**
	 * Show the headerlist of currently selected folder.
	 * <p>
	 * Additionally, implements folderoptions plugin entrypoint.
	 * 
	 * @see org.columba.mail.folder.folderoptions
	 * @see org.columba.mail.gui.frame.ViewHeaderListInterface#showHeaderList(org.columba.mail.folder.Folder,
	 *      org.columba.mail.message.HeaderList)
	 */
	public void showHeaderList(IMailbox folder, IHeaderList headerList)
			throws Exception {

		// save previously selected folder options
		if (previouslySelectedFolder != null) {
			((MailFrameMediator) getFrameController())
					.getFolderOptionsController()
					.save(previouslySelectedFolder);
		}

		// load options of newly selected folder
		((MailFrameMediator) getFrameController()).getFolderOptionsController()
				.load(folder, FolderOptionsController.STATE_BEFORE);

		getHeaderTableModel().set(headerList);

		// load options of newly selected folder
		((MailFrameMediator) getFrameController()).getFolderOptionsController()
				.load(folder, FolderOptionsController.STATE_AFTER);

		// remember previously selected folder
		previouslySelectedFolder = folder;
	}

	/**
	 * Show empty messagelist with no elements.
	 * 
	 */
	public void clear() {
		// clear model
		getHeaderTableModel().set(null);

	}

	/** *********** implement getter/setter methods ******************** */
	/**
	 * return the table model sorter
	 */
	public TableModelSorter getTableModelSorter() {
		return tableModelSorter;
	}

	/**
	 * return the threaded view model
	 */
	public TableModelThreadedView getTableModelThreadedView() {
		return tableModelThreadedView;
	}

	/** ************************* ListSelectionListener interface ************* */

	/**
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent arg0) {

		// enable/disable cut/copy/paste/selectall actions
		FocusManager.getInstance().updateActions();

		// if user is currently changing selection, don't do anything
		// -> wait until the final selection is available
		if (arg0.getValueIsAdjusting())
			return;

		// @author fdietz
		// bug #983931, message jumps while downloading new messages
		if (getView().getSelectedNodes().length == 0) {
			// skip if no message selected

			if (getView().getRowCount() > 0)
				// if folder contains messages
				// -> skip to fix above bug
				return;
		}

		// rememember selected nodes
		previouslySelectedNodes = getView().getSelectedNodes();

		// show message
		new ViewMessageAction(getFrameController()).actionPerformed(null);
	}

	/**
	 * @see org.columba.mail.gui.table.model.TableModelChangedListener#isInterestedIn(IFolder)
	 */
	public boolean isInterestedIn(IMailFolder folder) {

		return folder == previouslySelectedFolder;
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#getSelectedNodes()
	 */
	public IMessageNode[] getSelectedNodes() {
		return getView().getSelectedNodes();
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#selectFirstRow()
	 */
	public Object selectFirstRow() {
		Object result = getView().selectFirstRow();

		return result;
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#selectLastRow()
	 */
	public Object selectLastRow() {
		Object result = getView().selectLastRow();

		return result;
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#selectRow(int)
	 */
	public void selectRow(int row) {
		getView().selectRow(row);

	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#getMessagNode(java.lang.Object)
	 */
	public IMessageNode getMessageNode(Object uid) {
		return getView().getMessagNode(uid);
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#enableThreadedView(boolean)
	 */
	public void enableThreadedView(boolean enableThreadedMode,
			boolean updateModel) {
		getTableModelThreadedView().setEnabled(enableThreadedMode);
		getHeaderTableModel().enableThreadedView(enableThreadedMode);
		getView().enableThreadedView(enableThreadedMode);

		if (updateModel)
			getHeaderTableModel().update();

		if (enableThreadedMode) {
			// expand all unread message nodes
			for (int i = 0; i < getView().getRowCount(); i++) {
				System.out.println("i=" + i + " count="
						+ getView().getRowCount());

				TreePath path = getView().getTree().getPathForRow(i);
				MessageNode node = (MessageNode) path.getLastPathComponent();
				IColumbaHeader h = node.getHeader();
				boolean unseen = !h.getFlags().getSeen();
				if (unseen) {
					getView().getTree().expandPath(path);
				}
			}
		}
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#clearSelection()
	 */
	public void clearSelection() {
		getView().getSelectionModel().clearSelection();
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#makeSelectedRow
	 */
	public void makeSelectedRowVisible() {
		getView().scrollRectToVisible(
				getView().getCellRect(getView().getSelectedRow(), 0, false));
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#getSortingStateObservable()
	 */
	public Observable getSortingStateObservable() {
		return getTableModelSorter().getSortingStateObservable();
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#setSortingOrder(boolean)
	 */
	public void setSortingOrder(boolean order) {
		getTableModelSorter().setSortingOrder(order);
		// getHeaderTableModel().update();
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#setSortingColumn(java.lang.String)
	 */
	public void setSortingColumn(String column) {
		getTableModelSorter().setSortingColumn(column);
		// getHeaderTableModel().update();
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#restartMarkAsReadTimer(org.columba.mail.command.IFolderCommandReference)
	 */
	public void restartMarkAsReadTimer(IMailFolderCommandReference reference) {
		getMarkAsReadTimer().restart(reference);
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#getSortingColumn()
	 */
	public String getSortingColumn() {
		return getTableModelSorter().getSortingColumn();
	}

	/**
	 * @see org.columba.mail.gui.table.ITableController#getSortingOrder()
	 */
	public boolean getSortingOrder() {
		return getTableModelSorter().getSortingOrder();
	}

}