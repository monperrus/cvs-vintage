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

package org.eclipse.ui.commands;

/**
 * An instance of this interface provides support for managing commands at the
 * <code>IWorkbenchWindow</code> level.
 * <p>
 * This interface is not intended to be extended or implemented by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 * @see IWorkbenchWindow#getAdaptable
 */
public interface IWorkbenchWindowCommandSupport {

	/**
	 * Returns the command handler service for the workbench window.
	 * 
	 * @return the command handler for the workbench window. Guaranteed not
	 *         to be <code>null</code>.
	 */
	ICommandHandlerService getCommandHandlerService();
}
