/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkingSetFilterActionGroup;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * Clears the selected working set in the working set action group.
 * 
 * @since 2.1
 */
public class ClearWorkingSetAction extends Action {
    private WorkingSetFilterActionGroup actionGroup;

    /**
     * Creates a new instance of the receiver.
     * 
     * @param actionGroup the action group this action is created in
     */
    public ClearWorkingSetAction(WorkingSetFilterActionGroup actionGroup) {
        super(WorkbenchMessages.ClearWorkingSetAction_text);
        Assert.isNotNull(actionGroup);
        setToolTipText(WorkbenchMessages.ClearWorkingSetAction_toolTip);
        setEnabled(actionGroup.getWorkingSet() != null);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IWorkbenchHelpContextIds.CLEAR_WORKING_SET_ACTION);
        this.actionGroup = actionGroup;
    }

    /**
     * Overrides method from Action
     * 
     * @see Action#run
     */
    public void run() {
        actionGroup.setWorkingSet(null);
    }
}