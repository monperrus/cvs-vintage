/******************************************************************************* 
 * Copyright (c) 2000, 2003 IBM Corporation and others. 
 * All rights reserved. This program and the accompanying materials! 
 * are made available under the terms of the Common Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html 
 * 
 * Contributors: 
 *   IBM Corporation - initial API and implementation 
 *   Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog
 *     font should be activated and used by other components.
 *****************************************************************************/

package org.eclipse.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.misc.ContainerSelectionGroup;

/**
 * A standard selection dialog which solicits a container resource from the user.
 * The <code>getResult</code> method returns the selected container resource.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * <pre>
 * ContainerSelectionDialog dialog =
 *    new ContainerSelectionDialog(getShell(), initialSelection, allowNewContainerName(), msg);
 *	dialog.open();
 *	Object[] result = dialog.getResult();
 * </pre> 	
 * </p>
 */
public class ContainerSelectionDialog extends SelectionDialog {
	// the widget group;
	ContainerSelectionGroup group;

	// the root resource to populate the viewer with
	private IContainer initialSelection;

	// allow the user to type in a new container name
	private boolean allowNewContainerName = true;

	// the validation message
	Label statusMessage;

	//for validating the selection
	ISelectionValidator validator;

	// show closed projects by default
	private boolean showClosedProjects = true;
	/**
	 * Creates a resource container selection dialog rooted at the given resource.
	 * All selections are considered valid. 
	 *
	 * @param parentShell the parent shell
	 * @param initialRoot the initial selection in the tree
	 * @param allowNewContainerName <code>true</code> to enable the user to type in
	 *  a new container name, and <code>false</code> to restrict the user to just
	 *  selecting from existing ones
	 * @param message the message to be displayed at the top of this dialog, or
	 *    <code>null</code> to display a default message
	 */
	public ContainerSelectionDialog(
		Shell parentShell,
		IContainer initialRoot,
		boolean allowNewContainerName,
		String message) {
		super(parentShell);
		setTitle(WorkbenchMessages.getString("ContainerSelectionDialog.title")); //$NON-NLS-1$
		this.initialSelection = initialRoot;
		this.allowNewContainerName = allowNewContainerName;
		if (message != null)
			setMessage(message);
		else
			setMessage(WorkbenchMessages.getString("ContainerSelectionDialog.message")); //$NON-NLS-1$
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	/* (non-Javadoc)
	 * Method declared in Window.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		WorkbenchHelp.setHelp(
			shell,
			IHelpContextIds.CONTAINER_SELECTION_DIALOG);
	}
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		// create composite 
		Composite area = (Composite) super.createDialogArea(parent);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (statusMessage != null && validator != null) {
					String errorMsg =
						validator.isValid(group.getContainerFullPath());
					if (errorMsg == null || errorMsg.equals("")) { //$NON-NLS-1$
						statusMessage.setText(""); //$NON-NLS-1$
						getOkButton().setEnabled(true);
					} else {
						statusMessage.setForeground(
							JFaceColors.getErrorText(
								statusMessage.getDisplay()));
						statusMessage.setText(errorMsg);
						getOkButton().setEnabled(false);
					}
				}
			}
		};

		// container selection group
		group =
			new ContainerSelectionGroup(
				area,
				listener,
				allowNewContainerName,
				getMessage(),
				showClosedProjects);
		if (initialSelection != null) {
			group.setSelectedContainer(initialSelection);
		}

		statusMessage = new Label(parent, SWT.NONE);
		statusMessage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		statusMessage.setFont(parent.getFont());

		return dialogArea;
	}
	/**
	 * The <code>ContainerSelectionDialog</code> implementation of this 
	 * <code>Dialog</code> method builds a list of the selected resource containers
	 * for later retrieval by the client and closes this dialog.
	 */
	protected void okPressed() {

		List chosenContainerPathList = new ArrayList();
		IPath returnValue = group.getContainerFullPath();
		if (returnValue != null)
			chosenContainerPathList.add(returnValue);
		setResult(chosenContainerPathList);
		super.okPressed();
	}
	/**
	 * Sets the validator to use.  
	 * 
	 * @param validator A selection validator
	 */
	public void setValidator(ISelectionValidator validator) {
		this.validator = validator;
	}
	/**
	 * Set whether or not closed projects should be shown
	 * in the selection dialog.
	 * 
	 * @param show Whether or not to show closed projects.
	 */
	public void showClosedProjects(boolean show) {
		this.showClosedProjects = show;
	}
}
