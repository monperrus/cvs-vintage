/******************************************************************************* 
 * Copyright (c) 2000, 2003 IBM Corporation and others. 
 * All rights reserved. This program and the accompanying materials! 
 * are made available under the terms of the Common Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html 
 * 
 * Contributors: 
 *      IBM Corporation - initial API and implementation 
 *  	Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog font should be
 *      activated and used by other components.
************************************************************************/

package org.eclipse.ui.internal.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.activities.ws.ActivityMessages;
import org.eclipse.ui.model.PerspectiveLabelProvider;

/**
 * A dialog for perspective creation
 */
public class SelectPerspectiveDialog
	extends Dialog
	implements ISelectionChangedListener {

	final private static int LIST_HEIGHT = 300;
	final private static int LIST_WIDTH = 200;
	private TableViewer filteredList, unfilteredList;
	private Button okButton;
	private StackLayout stackLayout;
	private Composite stackComposite;
	private Button showAllCheck;	
	private IPerspectiveDescriptor perspDesc;
	private IPerspectiveRegistry perspReg;

	/**
	 * PerspectiveDialog constructor comment.
	 */
	public SelectPerspectiveDialog(
		Shell parentShell,
		IPerspectiveRegistry perspReg) {
		super(parentShell);
		this.perspReg = perspReg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed() {
		perspDesc = null;
		super.cancelPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(WorkbenchMessages.getString("SelectPerspective.shellTitle")); //$NON-NLS-1$
		WorkbenchHelp.setHelp(shell, IHelpContextIds.SELECT_PERSPECTIVE_DIALOG);
	}

	/**
	 * Adds buttons to this dialog's button bar.
	 * <p>
	 * The default implementation of this framework method adds standard ok and
	 * cancel buttons using the <code>createButton</code> framework method.
	 * Subclasses may override.
	 * </p>
	 * 
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		okButton =
			createButton(
				parent,
				IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL,
				true);
		createButton(
			parent,
			IDialogConstants.CANCEL_ID,
			IDialogConstants.CANCEL_LABEL,
			false);
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		// Run super.
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setFont(parent.getFont());

		stackComposite = new Composite(composite, SWT.NONE);
		stackLayout = new StackLayout();
		stackComposite.setLayout(stackLayout);
		layoutTopControl(stackComposite);
		filteredList = createViewer(stackComposite, true);
		
		if (WorkbenchActivityHelper.showAll()) {

			unfilteredList = createViewer(stackComposite, false);

			showAllCheck = new Button(composite, SWT.CHECK);
			showAllCheck.setSelection(false);
			showAllCheck.setText(ActivityMessages.getString("ActivityFiltering.showAll")); //$NON-NLS-1$
						
			// flipping tabs updates the selected node
			showAllCheck.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (!showAllCheck.getSelection()) {
					    filteredList.setSelection(unfilteredList.getSelection());
						stackLayout.topControl = filteredList.getControl();	
						stackComposite.layout();						
						selectionChanged(
							new SelectionChangedEvent(
								filteredList,
								filteredList.getSelection()));
					} else {
					    unfilteredList.setSelection(filteredList.getSelection());
						stackLayout.topControl = unfilteredList.getControl();	
						stackComposite.layout();						
						selectionChanged(
							new SelectionChangedEvent(
								unfilteredList,
								unfilteredList.getSelection()));
					}
				}
			});
		} 
		
		stackLayout.topControl = filteredList.getControl();

		// Return results.
		return composite;
	}

	/**
	 * Create a new viewer in the parent.
	 * 
	 * @param parent the parent <code>Composite</code>.
	 * @param filtering whether the viewer should be filtering based on
	 *            activities.
	 * @return <code>TableViewer</code>
	 */
	private TableViewer createViewer(Composite parent, boolean filtering) {
		// Add perspective list.
		TableViewer list =
			new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		list.getTable().setFont(parent.getFont());
		list.setLabelProvider(new PerspectiveLabelProvider());
		list.setContentProvider(new PerspContentProvider(filtering));
		list.setSorter(new ViewerSorter());
		list.setInput(perspReg);
		list.addSelectionChangedListener(this);
		list.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClickEvent();
			}
		});
		return list;
	}

	/**
	 * Returns the current selection.
	 */
	public IPerspectiveDescriptor getSelection() {
		return perspDesc;
	}

	/**
	 * Handle a double click event on the list
	 */
	protected void handleDoubleClickEvent() {
		okPressed();
	}

	/**
	 * Layout the top control.
	 * 
	 * @param control the control.
	 */
	private void layoutTopControl(Control control) {
		GridData spec = new GridData(GridData.FILL_BOTH);
		spec.widthHint = LIST_WIDTH;
		spec.heightHint = LIST_HEIGHT;
		control.setLayoutData(spec);
	}

	/**
	 * Notifies that the selection has changed.
	 * 
	 * @param event event object describing the change
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		updateSelection(event);
		updateButtons();
	}

	/**
	 * Update the button enablement state.
	 */
	protected void updateButtons() {
		okButton.setEnabled(getSelection() != null);
	}

	/**
	 * Update the selection object.
	 */
	protected void updateSelection(SelectionChangedEvent event) {
		perspDesc = null;
		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		if (!sel.isEmpty()) {
			Object obj = sel.getFirstElement();
			if (obj instanceof IPerspectiveDescriptor)
				perspDesc = (IPerspectiveDescriptor) obj;
		}
	}
}
