/************************************************************************
Copyright (c) 2003 IBM Corporation and others.
All rights reserved.   This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html

Contributors:
    IBM - Initial implementation
************************************************************************/

package org.eclipse.ui.dialogs;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

public class ListDialog extends SelectionDialog {

	private IStructuredContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;
	private Object fInput;
	private TableViewer fTableViewer;
	private boolean fAddCancelButton = true;
	private int widthInChars = 55;
	private int heightInChars = 15;

	public ListDialog(Shell parent) {
		super(parent);
	}

	public void setInput(Object input) {
		fInput = input;
	}

	public void setContentProvider(IStructuredContentProvider sp) {
		fContentProvider = sp;
	}

	public void setLabelProvider(ILabelProvider lp) {
		fLabelProvider = lp;
	}

	public void setAddCancelButton(boolean addCancelButton) {
		fAddCancelButton = addCancelButton;
	}

	public TableViewer getTableViewer() {
		return fTableViewer;
	}

	public void create() {
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		super.create();
	}

	protected void createButtonsForButtonBar(Composite parent) {
		if (!fAddCancelButton)
			createButton(
				parent,
				IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL,
				true);
		else
			super.createButtonsForButtonBar(parent);
	}

	protected Control createDialogArea(Composite container) {
		Composite parent = (Composite) super.createDialogArea(container);
		createMessageArea(parent);
		fTableViewer = new TableViewer(parent, getTableStyle());
		fTableViewer.setContentProvider(fContentProvider);
		fTableViewer.setLabelProvider(fLabelProvider);
		fTableViewer.setInput(fInput);
		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (fAddCancelButton)
					okPressed();
			}
		});
		List initialSelection = getInitialElementSelections();
		if (initialSelection != null)
			fTableViewer.setSelection(
				new StructuredSelection(initialSelection));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = convertHeightInCharsToPixels(heightInChars);
		gd.widthHint = convertWidthInCharsToPixels(widthInChars);
		Table table = fTableViewer.getTable();
		table.setLayoutData(gd);
		table.setFont(container.getFont());
		return parent;
	}

	protected int getTableStyle() {
		return SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
	}
	
	/*
	 * Overrides method from Dialog
	 */
	protected void okPressed() {
		// Build a list of selected children.
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();
		setResult(selection.toList());
		super.okPressed();
	}
	
	/**
	 * Returns the initial height of the dialog in number of characters.
	 * 
	 * @return the initial height of the dialog in number of characters
	 */
	public int getHeightInChars() {
		return heightInChars;
	}

	/**
	 * Returns the initial width of the dialog in number of characters.
	 * 
	 * @return the initial width of the dialog in number of characters
	 */
	public int getWidthInChars() {
		return widthInChars;
	}

	/**
	 * Sets the initial height of the dialog in number of characters.
	 * 
	 * @param heightInChars the initialheight of the dialog in number of characters
	 */
	public void setHeightInChars(int heightInChars) {
		this.heightInChars = heightInChars;
	}

	/**
	 * Sets the initial width of the dialog in number of characters.
	 * 
	 * @param widthInChars the initial width of the dialog in number of characters
	 */
	public void setWidthInChars(int widthInChars) {
		this.widthInChars = widthInChars;
	}

}