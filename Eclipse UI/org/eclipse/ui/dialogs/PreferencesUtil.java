/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.dialogs;

import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

/**
 * The PreferencesUtil class is the class that opens a properties or preference
 * dialog on a set of ids.
 */
public final class PreferencesUtil {

	/**
	 * Creates a workbench preference dialog to a particular preference page.
	 * Show the other pages as filtered results using whatever filtering
	 * criteria the search uses. It is the responsibility of the caller to then
	 * call <code>open()</code>. The call to <code>open()</code> will not
	 * return until the dialog closes, so this is the last chance to manipulate
	 * the dialog.
	 * 
	 * @param preferencePageId
	 *            The identifier of the preference page to open; may be
	 *            <code>null</code>. If it is <code>null</code>, then the
	 *            preference page is not selected or modified in any way.
	 * @param filteredIds
	 *            The ids of the other pages to be highlighted using the same
	 *            filtering criterea as search. If this is <code>null</code>,
	 *            then the all preference pages are shown.
	 * @param data
	 *            Data that will be passed to all of the preference pages to be
	 *            applied as specified within the page as they are created. If
	 *            the data is <code>null</code> nothing will be called.
	 * 
	 * @return a preference dialog.
	 * @since 3.1
	 */
	public static final PreferenceDialog createPreferenceDialogOn(
			String preferencePageId, String[] filteredIds, Object data) {
		WorkbenchPreferenceDialog dialog = WorkbenchPreferenceDialog
				.createDialogOn(preferencePageId);
		dialog.setPageData(data);
		if (filteredIds != null)
			dialog.setSearchResults(filteredIds);

		if (data != null) {
			IPreferencePage page = dialog.getCurrentPage();
			if (page instanceof PreferencePage)
				((PreferencePage) page).applyData(data);

		}
		return dialog;
	}

	/**
	 * Creates a workbench preference dialog to a particular preference page.
	 * Show the other pages as filtered results using whatever filtering
	 * criteria the search uses. It is the responsibility of the caller to then
	 * call <code>open()</code>. The call to <code>open()</code> will not
	 * return until the dialog closes, so this is the last chance to manipulate
	 * the dialog.
	 * 
	 * @param propertyPageId
	 *            The identifier of the preference page to open; may be
	 *            <code>null</code>. If it is <code>null</code>, then the
	 *            dialog is opened with no selected page.
	 * @param selection
	 *            ISelection A selection that holds the object the properties
	 *            are being shown for.
	 * @param data
	 *            Data that will be passed to all of the preference pages to be
	 *            applied as specified within the page as they are created. If
	 *            the data is <code>null</code> nothing will be called.
	 * 
	 * @return A preference dialog showing properties for the selection or
	 *         <code>null</code> if it could not be created.
	 * @since 3.1
	 */
	public static final PreferenceDialog createPropertyDialogOn(
			String propertyPageId, ISelectionProvider selection, Object data) {

		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		Shell parentShell = null;
		if (workbenchWindow != null) 
			parentShell = workbenchWindow.getShell();

		PropertyDialogAction action = new PropertyDialogAction(parentShell,
				selection);
		action.select(propertyPageId, data);
		return action.createDialog();

	}
}
