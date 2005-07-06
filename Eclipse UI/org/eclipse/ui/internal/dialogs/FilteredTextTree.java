/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * The FilteredTextTree is a filtered tree that uses an editable text.
 */
public class FilteredTextTree extends FilteredTree {
	// A list contains all strings in search history
	private List searchHistory;

	private static final String SEARCHHISTORY = "search"; //$NON-NLS-1$

	// A table which contains only strings begin with typed strings
	private Table currentSeachTable;

	/**
	 * Create a new instance of the receiver.
	 * 
	 * @param parent
	 * @param treeStyle
	 */
	public FilteredTextTree(Composite parent, int treeStyle) {
		super(parent, treeStyle);
	}

	/**
	 * Create a new instance of the receiver with a supplied filter.
	 * 
	 * @param parent
	 * @param treeStyle
	 * @param filter
	 */
	public FilteredTextTree(Composite parent, int treeStyle,
			PatternItemFilter filter) {
		super(parent, treeStyle, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.dialogs.FilteredTree#createFilterControl(org.eclipse.swt.widgets.Composite)
	 */
	protected void createFilterControl(final Composite parent) {
		filterText = new Text(parent, SWT.DROP_DOWN | SWT.BORDER);
		filterText.setFont(parent.getFont());
		searchHistory = getPreferenceSearchHistory();

		final Shell shell = new Shell(parent.getShell(), SWT.NO_TRIM);
		shell
				.setBackground(parent.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
		GridLayout shellGL = new GridLayout();
		shellGL.marginHeight = 0;
		shellGL.marginWidth = 0;
		shell.setLayout(shellGL);
		shell.setLayoutData(new GridData(
				(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL)));

		currentSeachTable = new Table(shell, SWT.SINGLE | SWT.BORDER);
		currentSeachTable.setLayoutData(new GridData(
				(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL)));

		filterText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					shell.setVisible(false);
					if (getViewer().getTree().getItemCount() == 0) {
						Display.getCurrent().beep();
						setFilterText(""); //$NON-NLS-1$
					} else {
						getViewer().getTree().setFocus();
					}
				}
			}
		});

		filterText.addKeyListener(new KeyAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			public void keyReleased(KeyEvent e) {

				if (e.keyCode == SWT.ARROW_DOWN) {
					if (currentSeachTable.isVisible()) {
						// Make selection at popup table
						if (currentSeachTable.getSelectionCount() < 1)
							currentSeachTable.setSelection(0);
						currentSeachTable.setFocus();
					} else
						// Make selection be on the left tree
						treeViewer.getTree().setFocus();
				} else {
					if (e.character == SWT.CR)
						return;

					textChanged();
					List result = new ArrayList();
					result = reduceSearch(searchHistory, filterText.getText());
					upDateTable(currentSeachTable, result);

					if (currentSeachTable.getItemCount() > 0) {
						Rectangle textBounds = filterText.getBounds();
						Point point = getDisplay().map(parent, null,
								textBounds.x, textBounds.y);
						int space = currentSeachTable.getItemHeight();
						shell.setBounds(point.x, point.y + textBounds.height,
								textBounds.width, currentSeachTable
										.getItemHeight()
										* currentSeachTable.getItemCount()
										+ space);

						if (shell.isDisposed())
							shell.open();

						if (!shell.getVisible()) {
							shell.setVisible(true);
							filterText.setFocus();
						}

					} else
						shell.setVisible(false);
				}

			}
		});

		parent.getDisplay().addFilter(SWT.MouseDown, new Listener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Events)
			 */
			public void handleEvent(Event event) {
				if (!shell.isDisposed())
					shell.setVisible(false);
			}
		});

		getShell().addControlListener(new ControlAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
			 */
			public void controlMoved(ControlEvent e) {
				shell.setVisible(false);
			}
		});

		currentSeachTable.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {

				setFilterText(currentSeachTable.getSelection()[0].getText());
				textChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});

		filterText.addDisposeListener(new DisposeListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
			 */
			public void widgetDisposed(DisposeEvent e) {
				saveDialogSettings();
			}
		});

		filterText.getAccessible().addAccessibleListener(
				getAccessibleListener());

	}

	/**
	 * Find all items which start with typed words list the list contains all
	 * strings of the search history
	 * 
	 * @param list
	 *            the list to search
	 * @param wordsEntered
	 *            String
	 * @return a list in which all strings start from the typed letter(s)
	 */
	public List reduceSearch(List list, String wordsEntered) {
		List result = new ArrayList();
		if (list == null)
			return result;
		for (int i = 0; i < list.size(); i++) {
			if (filterText.getText() == "") //$NON-NLS-1$
				return result;
			else if (((String) list.get(i)).startsWith(wordsEntered))
				result.add(list.get(i));
		}

		return result;
	}

	/**
	 * Copy all elements from a list to a table
	 * 
	 * @param table
	 * @param list
	 */
	public void upDateTable(Table table, List list) {
		table.removeAll();
		if (list.size() > 0) {
			TableItem newItem;
			for (int i = 0; i < list.size(); i++) {
				newItem = new TableItem(table, SWT.NULL, i);
				newItem.setText((String) list.get(i));

			}
		}

	}

	/**
	 * Return a dialog setting section for this dialog
	 * 
	 * @return IDialogSettings
	 */
	private IDialogSettings getDialogSettings() {
		IDialogSettings settings = WorkbenchPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings thisSettings = settings
				.getSection(getClass().getName());
		if (thisSettings == null)
			thisSettings = settings.addNewSection(getClass().getName());

		return thisSettings;
	}

	/**
	 * Get the preferences search history for this eclipse's start, Note that
	 * this history will not be cleared until this eclipse closes
	 * 
	 * @return a list
	 */
	public List getPreferenceSearchHistory() {

		List searchList = new ArrayList();
		IDialogSettings settings = getDialogSettings();
		String[] search = settings.getArray(SEARCHHISTORY); //$NON-NLS-1$

		if (search != null) {
			for (int i = 0; i < search.length; i++)
				searchList.add(search[i]);
		}
		return searchList;

	}

	/**
	 * Saves the search history.
	 */
	private void saveDialogSettings() {
		IDialogSettings settings = getDialogSettings();

		// If the settings contains the same key, the previous value will be
		// replaced by new one
		String[] result = new String[searchHistory.size()];
		listToArray(searchHistory, result);
		settings.put(SEARCHHISTORY, result);

	}

	private void listToArray(List list, String[] string) {
		int size = list.size();
		for (int i = 0; i < size; i++)
			string[i] = (String) list.get(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.dialogs.FilteredTree#filterFocusLost()
	 */
	protected void filterFocusLost() {
		String newText = filterText.getText();
		Object[] textValues = searchHistory.toArray();

		if ((newText.equals("")) || (newText.equals(initialText)))//$NON-NLS-1$
			return;

		for (int i = 0; i < textValues.length; i++) {
			if (((String) textValues[i]).equals(newText))
				return;
		}
		searchHistory.add(newText);
	}

}
