/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.dialogs;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 *	New wizard selection tab that allows the user to either select a
 *	registered 'New' wizard to be launched, or to select a solution or
 *	projects to be retrieved from an available server.  This page
 *	contains two visual tabs that allow the user to perform these tasks.
 *
 *  Temporarily has two inner pages.  The new format page is used if the system 
 *  is currently aware of activity categories.
 */
class NewWizardSelectionPage extends WorkbenchWizardSelectionPage {
    private IWizardCategory wizardCategories;

    // widgets
    private NewWizardNewPage newResourcePage;

    private IWizardDescriptor [] primaryWizards;

	private boolean projectsOnly;
    
    /**
     * Create an instance of this class.
     *
     * @param workbench the workbench
     * @param selection the current selection
     * @param root the wizard root element
     * @param primary the primary wizard elements
     * @param projectsOnly if only projects should be shown
     */
    public NewWizardSelectionPage(IWorkbench workbench,
			IStructuredSelection selection, IWizardCategory root,
			IWizardDescriptor[] primary, boolean projectsOnly) {
        super("newWizardSelectionPage", workbench, selection, null);//$NON-NLS-1$
        
        setTitle(WorkbenchMessages.NewWizardSelectionPage_description); 
        wizardCategories = root;
        primaryWizards = primary;
        this.projectsOnly = projectsOnly;
	}

    /**
     * Makes the next page visible.
     */
    public void advanceToNextPage() {
        getContainer().showPage(getNextPage());
    }

    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent) {
        IDialogSettings settings = getDialogSettings();
        newResourcePage = new NewWizardNewPage(this, wizardCategories,
				primaryWizards, projectsOnly);
        newResourcePage.setDialogSettings(settings);

        Control control = newResourcePage.createControl(parent);
        getWorkbench().getHelpSystem().setHelp(control,
				IWorkbenchHelpContextIds.NEW_WIZARD_SELECTION_WIZARD_PAGE);
        setControl(control);
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they
     *will persist into the next invocation of this wizard page
     */
    protected void saveWidgetValues() {
        newResourcePage.saveWidgetValues();
    }
}
