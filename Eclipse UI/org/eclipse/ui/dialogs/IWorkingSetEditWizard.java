/**********************************************************************
Copyright (c) 2002 IBM Corp. and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html

Contributors:
    IBM Corporation - Initial implementation
**********************************************************************/
package org.eclipse.ui.dialogs;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IWorkingSet;

/**
 * A working set edit wizard allows editing a working set using
 * the IWorkingSetPage associated with the working set.
 * See the org.eclipse.ui.workingSets extension point for details.
 * <p>
 * Use org.eclipse.ui.IWorkingSetManager#createWorkingSetEditWizard(IWorkingSet)
 * to create an instance of this wizard.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @see org.eclipse.ui.IWorkingSetManager
 * @since 2.1
 */
public interface IWorkingSetEditWizard extends IWizard {
	/**
	 * Returns the working set edited in the wizard.
	 * 
	 * @return the working set edited in the wizard.
	 */	
	public IWorkingSet getSelection();
}
