/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.internal.activities.ws.ActivityEnabler;
import org.eclipse.ui.internal.activities.ws.ActivityMessages;

/**
 * Activates the Activity configuration dialog.
 * 
 * @since 3.0
 */
public class ActivityEnablerAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchActivitySupport activitySupport;
	private ActivityEnabler enabler;
	private IWorkbenchWindow workbenchWindow;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {
		workbenchWindow = null;
		activitySupport = null;
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		this.activitySupport = window.getWorkbench().getActivitySupport();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
		if (workbenchWindow == null) {
			// action has been disposed
			return;
		}
		Dialog d = new Dialog(workbenchWindow.getShell()) {

            /* (non-Javadoc)
             * @see org.eclipse.jface.window.Window#create()
             */
            public void create() {
                setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
                super.create();
            }         

			/* (non-Javadoc)
			 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
			 */
			protected void configureShell(Shell shell) {
				super.configureShell(shell);
				shell.setText(ActivityMessages.getString("ActivityEnablementAction.title")); //$NON-NLS-1$
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
			 */
			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super.createDialogArea(parent);
				GridData data = new GridData(GridData.FILL_BOTH);
				data.widthHint = 600;
				data.heightHint = 240;
				composite.setFont(parent.getFont());

				enabler = new ActivityEnabler(activitySupport);
				enabler.createControl(composite).setLayoutData(data);

				return composite;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
			 */
			protected void okPressed() {
				if (enabler != null) {
					enabler.updateActivityStates();
				}
				super.okPressed();
			}
		};
		d.open();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        //no-op
    }
}
