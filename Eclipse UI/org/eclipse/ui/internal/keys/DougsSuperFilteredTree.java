/******************************************************************************* * Copyright (c) 2005 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation ******************************************************************************/package org.eclipse.ui.internal.keys;import java.util.ArrayList;import java.util.List;import org.eclipse.core.runtime.IProgressMonitor;import org.eclipse.core.runtime.IStatus;import org.eclipse.core.runtime.Status;import org.eclipse.core.runtime.jobs.Job;import org.eclipse.jface.action.Action;import org.eclipse.jface.action.IAction;import org.eclipse.jface.action.ToolBarManager;import org.eclipse.jface.dialogs.IDialogSettings;import org.eclipse.jface.resource.JFaceResources;import org.eclipse.jface.viewers.TreeViewer;import org.eclipse.swt.SWT;import org.eclipse.swt.accessibility.AccessibleAdapter;import org.eclipse.swt.accessibility.AccessibleEvent;import org.eclipse.swt.events.ControlAdapter;import org.eclipse.swt.events.ControlEvent;import org.eclipse.swt.events.DisposeEvent;import org.eclipse.swt.events.DisposeListener;import org.eclipse.swt.events.KeyAdapter;import org.eclipse.swt.events.KeyEvent;import org.eclipse.swt.events.SelectionAdapter;import org.eclipse.swt.events.SelectionEvent;import org.eclipse.swt.events.TraverseEvent;import org.eclipse.swt.events.TraverseListener;import org.eclipse.swt.graphics.Point;import org.eclipse.swt.graphics.Rectangle;import org.eclipse.swt.layout.GridData;import org.eclipse.swt.layout.GridLayout;import org.eclipse.swt.widgets.Composite;import org.eclipse.swt.widgets.Display;import org.eclipse.swt.widgets.Event;import org.eclipse.swt.widgets.Listener;import org.eclipse.swt.widgets.Shell;import org.eclipse.swt.widgets.Table;import org.eclipse.swt.widgets.TableItem;import org.eclipse.swt.widgets.Text;import org.eclipse.swt.widgets.ToolBar;import org.eclipse.ui.internal.WorkbenchMessages;import org.eclipse.ui.internal.WorkbenchPlugin;import org.eclipse.ui.internal.dialogs.PatternFilter;import org.eclipse.ui.internal.dialogs.PreferenceNodeFilter;import org.eclipse.ui.progress.WorkbenchJob;/** * <p> * A table-tree with filtering and grouping capabilities. This provides a * flexible way to display multi-dimensional data to the user. The user may * change the dimension on which the data is grouped, and may type in some * filter text. * </p> *  * @since 3.2 */public final class DougsSuperFilteredTree extends Composite {	private static final String CLEAR_ICON = "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; //$NON-NLS-1$	private static final String DCLEAR_ICON = "org.eclipse.ui.internal.dialogs.DCLEAR_ICON"; //$NON-NLS-1$	/**	 * The dialog settings key for the search history.	 */	private static final String SEARCHHISTORY = "search"; //$NON-NLS-1$	private String cachedTitle;	protected Text filterText;	private ToolBarManager filterToolBar;	protected String initialText = ""; //$NON-NLS-1$	private PatternFilter patternFilter;	private PreferenceNodeFilter preferenceFilter;	/**	 * The job for refreshing the tree.	 */	private Job refreshJob;	/**	 * A list containing all the strings in the search history.	 */	private List searchHistory;	protected TreeViewer viewer;	/**	 * Constructs a new instance of <code>DougsSuperFilteredTableTree</code>.	 * 	 * @param parent	 *            The composite in which the filtered table tree will appear;	 *            must not be <code>null</code>.	 * @param style	 *            The style to be applied to the table tree.	 */	public DougsSuperFilteredTree(final Composite parent, final int style) {		super(parent, SWT.NONE);		GridData gridData;		// The composite containing the tree.		final Composite treeComposite = new Composite(this, SWT.NONE);		GridLayout layout = new GridLayout(2, false);		layout.marginHeight = 0;		layout.marginWidth = 0;		treeComposite.setLayout(layout);		treeComposite.setFont(parent.getFont());		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.grabExcessVerticalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.verticalAlignment = SWT.FILL;		treeComposite.setLayoutData(gridData);		// Create the topmost filter control.		createFilterControl(treeComposite);		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		getFilterControl().setLayoutData(gridData);		// Place a tool bar with the clear button next to it.		final ToolBar toolBar = new ToolBar(treeComposite, SWT.FLAT				| SWT.HORIZONTAL);		filterToolBar = new ToolBarManager(toolBar);		createClearText(filterToolBar);		filterToolBar.update(false);		filterToolBar.getControl().setVisible(false);		filterToolBar.getControl().setLayoutData(new GridData());		// Create a table tree viewer.		viewer = new TreeViewer(treeComposite, style);		viewer.getControl().addDisposeListener(new DisposeListener() {			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)			 */			public void widgetDisposed(DisposeEvent e) {				refreshJob.cancel();			}		});		gridData = new GridData();		gridData.grabExcessHorizontalSpace = true;		gridData.grabExcessVerticalSpace = true;		gridData.horizontalAlignment = SWT.FILL;		gridData.verticalAlignment = SWT.FILL;		gridData.horizontalSpan = 2;		viewer.getControl().setLayoutData(gridData);		/*		 * Attach the pattern filter, and create the job for refreshing the		 * table tree viewer.		 */		patternFilter = new PatternFilter();		viewer.addFilter(patternFilter);		createRefreshJob();	}	/**	 * clear the text in the filter text widget	 */	protected void clearText() {		setFilterText(""); //$NON-NLS-1$		if (preferenceFilter != null) {			getViewer().removeFilter(preferenceFilter);			preferenceFilter = null;			getShell().setText(cachedTitle);		}		textChanged();	}	/**	 * Create the button that clears the text.	 * 	 * @param filterToolBar	 */	private void createClearText(ToolBarManager filterToolBar) {		IAction clearTextAction = new Action("", IAction.AS_PUSH_BUTTON) {//$NON-NLS-1$			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.jface.action.Action#run()			 */			public void run() {				clearText();			}		};		clearTextAction				.setToolTipText(WorkbenchMessages.FilteredTree_ClearToolTip);		clearTextAction.setImageDescriptor(JFaceResources.getImageRegistry()				.getDescriptor(CLEAR_ICON));		clearTextAction.setDisabledImageDescriptor(JFaceResources				.getImageRegistry().getDescriptor(DCLEAR_ICON));		filterToolBar.add(clearTextAction);	}	/**	 * Creates the filter control for the table tree.	 * 	 * @param parent	 *            The parent composite in which to place the filter control;	 *            must not be <code>null</code>	 */	public void createFilterControl(final Composite parent) {		filterText = new Text(parent, SWT.DROP_DOWN | SWT.BORDER);		filterText.setFont(parent.getFont());		searchHistory = getPreferenceSearchHistory();		final Shell shell = new Shell(parent.getShell(), SWT.NO_TRIM);		shell				.setBackground(parent.getDisplay().getSystemColor(						SWT.COLOR_WHITE));		GridLayout shellGL = new GridLayout();		shellGL.marginHeight = 0;		shellGL.marginWidth = 0;		shell.setLayout(shellGL);		shell.setLayoutData(new GridData(				(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL)));		final Table currentSeachTable = new Table(shell, SWT.SINGLE				| SWT.BORDER);		currentSeachTable.setLayoutData(new GridData(				(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL)));		filterText.addTraverseListener(new TraverseListener() {			public void keyTraversed(TraverseEvent e) {				if (e.detail == SWT.TRAVERSE_RETURN) {					e.doit = false;					shell.setVisible(false);					if (getViewer().getTree().getItemCount() == 0) {						Display.getCurrent().beep();						setFilterText(""); //$NON-NLS-1$					} else {						getViewer().getTree().setFocus();					}				}			}		});		filterText.addKeyListener(new KeyAdapter() {			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)			 */			public void keyReleased(KeyEvent e) {				if (e.keyCode == SWT.ARROW_DOWN) {					if (currentSeachTable.isVisible()) {						// Make selection at popup table						if (currentSeachTable.getSelectionCount() < 1)							currentSeachTable.setSelection(0);						currentSeachTable.setFocus();					} else						// Make selection be on the left tree						getViewer().getTree().setFocus();				} else {					if (e.character == SWT.CR)						return;					textChanged();					List result = new ArrayList();					result = reduceSearch(searchHistory, filterText.getText());					updateTable(currentSeachTable, result);					if (currentSeachTable.getItemCount() > 0) {						Rectangle textBounds = filterText.getBounds();						Point point = getDisplay().map(parent, null,								textBounds.x, textBounds.y);						int space = currentSeachTable.getItemHeight();						shell.setBounds(point.x, point.y + textBounds.height,								textBounds.width, currentSeachTable										.getItemHeight()										* currentSeachTable.getItemCount()										+ space);						if (shell.isDisposed())							shell.open();						if (!shell.getVisible()) {							shell.setVisible(true);							filterText.setFocus();						}					} else						shell.setVisible(false);				}			}		});		parent.getDisplay().addFilter(SWT.MouseDown, new Listener() {			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Events)			 */			public void handleEvent(Event event) {				if (!shell.isDisposed())					shell.setVisible(false);			}		});		getShell().addControlListener(new ControlAdapter() {			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)			 */			public void controlMoved(ControlEvent e) {				shell.setVisible(false);			}		});		currentSeachTable.addSelectionListener(new SelectionAdapter() {			public void widgetDefaultSelected(SelectionEvent e) {				shell.setVisible(false);			}			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)			 */			public void widgetSelected(SelectionEvent e) {				setFilterText(currentSeachTable.getSelection()[0].getText());				textChanged();			}		});		filterText.addDisposeListener(new DisposeListener() {			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)			 */			public void widgetDisposed(DisposeEvent e) {				saveDialogSettings();			}		});		filterText.getAccessible().addAccessibleListener(				getAccessibleListener());	}	/**	 * Create the refresh job for the receiver.	 * 	 */	private void createRefreshJob() {		refreshJob = new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)			 */			public IStatus runInUIThread(IProgressMonitor monitor) {				if (getViewer().getControl().isDisposed())					return Status.CANCEL_STATUS;				String filterText = getFilterText();				boolean initial = initialText != null						&& filterText.equals(initialText);				if (initial) {					patternFilter.setPattern(null);				} else {					patternFilter.setPattern(getFilterText());				}				getViewer().getControl().setRedraw(false);				getViewer().refresh(true);				getViewer().getControl().setRedraw(true);				if (filterText.length() > 0 && !initial) {					getViewer().expandAll();					// enabled toolbar is a hint that there is text to clear					// and the list is currently being filtered					filterToolBar.getControl().setVisible(true);				} else {					// disabled toolbar is a hint that there is no text to clear					// and the list is currently not filtered					filterToolBar.getControl().setVisible(							preferenceFilter != null);				}				return Status.OK_STATUS;			}		};		refreshJob.setSystem(true);	}	protected AccessibleAdapter getAccessibleListener() {		return new AccessibleAdapter() {			/*			 * (non-Javadoc)			 * 			 * @see org.eclipse.swt.accessibility.AccessibleListener#getName(org.eclipse.swt.accessibility.AccessibleEvent)			 */			public void getName(AccessibleEvent e) {				String filterTextString = getFilterText();				if (filterTextString.length() == 0) {					e.result = initialText;				} else					e.result = filterTextString;			}		};	}	/**	 * Return a dialog setting section for this dialog	 * 	 * @return IDialogSettings	 */	private final IDialogSettings getDialogSettings() {		final IDialogSettings settings = WorkbenchPlugin.getDefault()				.getDialogSettings();		IDialogSettings thisSettings = settings				.getSection(getClass().getName());		if (thisSettings == null)			thisSettings = settings.addNewSection(getClass().getName());		return thisSettings;	}	/**	 * Get the filter text field associated with this control.	 * 	 * @return the text field	 */	public Text getFilterControl() {		return filterText;	}	/**	 * Get the text from the filter widget.	 * 	 * @return String	 */	protected String getFilterText() {		return filterText.getText();	}	/**	 * Get the preferences search history for this eclipse's start, Note that	 * this history will not be cleared until this eclipse closes	 * 	 * @return a list	 */	public final List getPreferenceSearchHistory() {		final List searchList = new ArrayList();		final IDialogSettings settings = getDialogSettings();		final String[] search = settings.getArray(SEARCHHISTORY); //$NON-NLS-1$		if (search != null) {			for (int i = 0; i < search.length; i++)				searchList.add(search[i]);		}		return searchList;	}	/**	 * Get the table tree viewer associated with this control.	 * 	 * @return the table tree viewer; never <code>null</code>.	 */	public final TreeViewer getViewer() {		return viewer;	}	private void listToArray(List list, String[] string) {		int size = list.size();		for (int i = 0; i < size; i++)			string[i] = (String) list.get(i);	}	/**	 * Find all items which start with typed words list the list contains all	 * strings of the search history	 * 	 * @param list	 *            the list to search	 * @param wordsEntered	 *            String	 * @return a list in which all strings start from the typed letter(s)	 */	public List reduceSearch(List list, String wordsEntered) {		List result = new ArrayList();		if (list == null)			return result;		for (int i = 0; i < list.size(); i++) {			if (filterText.getText() == "") //$NON-NLS-1$				return result;			else if (((String) list.get(i)).startsWith(wordsEntered))				result.add(list.get(i));		}		return result;	}	/**	 * Saves the search history.	 */	private void saveDialogSettings() {		IDialogSettings settings = getDialogSettings();		// If the settings contains the same key, the previous value will be		// replaced by new one		String[] result = new String[searchHistory.size()];		listToArray(searchHistory, result);		settings.put(SEARCHHISTORY, result);	}	protected void selectAll() {		filterText.selectAll();	}	/**	 * Set the text in the filter area.	 * 	 * @param string	 */	protected void setFilterText(String string) {		filterText.setText(string);		selectAll();	}	/**	 * Schedules a job to update the table tree after the text has changed.	 */	protected void textChanged() {		refreshJob.schedule(200);	}	/**	 * Copy all elements from a list to a table	 * 	 * @param table	 * @param list	 */	public void updateTable(Table table, List list) {		table.removeAll();		if (list.size() > 0) {			TableItem newItem;			for (int i = 0; i < list.size(); i++) {				newItem = new TableItem(table, SWT.NULL, i);				newItem.setText((String) list.get(i));			}		}	}}