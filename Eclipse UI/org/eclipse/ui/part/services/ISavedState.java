/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.part.services;

import org.eclipse.ui.IMemento;

/**
 * Service that provides access to a part's previously-saved state
 * 
 * @since 3.1
 */
public interface ISavedState {
    /**
     * Returns the saved state for this part, or null if none
     *
     * @return the saved state for this part or null if none
     */
    public IMemento getState();
}
