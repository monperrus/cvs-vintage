/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.commands;

import org.eclipse.ui.internal.commands.CompoundCommandHandlerService;
import org.eclipse.ui.internal.commands.MutableCommandHandlerService;

/**
 * This class allows clients to broker instances of <code>ICommandHandlerService</code>.
 * <p>
 * This class is not intended to be extended by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 */
public final class CommandHandlerServiceFactory {

	/**
	 * Creates a new instance of <code>ICompoundCommandHandlerService</code>.
	 * 
	 * @return a new instance of <code>ICompoundCommandHandlerService</code>.
	 *         Clients should not make assumptions about the concrete
	 *         implementation outside the contract of the interface. Guaranteed
	 *         not to be <code>null</code>.
	 */
	public static ICompoundCommandHandlerService getCompoundCommandHandlerService() {
		return new CompoundCommandHandlerService();
	}

	/**
	 * Creates a new instance of <code>IMutableCommandHandlerService</code>.
	 * 
	 * @return a new instance of <code>IMutableCommandHandlerService</code>.
	 *         Clients should not make assumptions about the concrete
	 *         implementation outside the contract of the interface. Guaranteed
	 *         not to be <code>null</code>.
	 */
	public static IMutableCommandHandlerService getMutableCommandHandlerService() {
		return new MutableCommandHandlerService();
	}

	private CommandHandlerServiceFactory() {
	}
}
