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

package org.eclipse.ui.commands;

/**
 * <p>
 * TODO javadoc
 * </p>
 * <p>
 * This class is not intended to be extended by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 */
public class HandlerServiceEvent {

	private IHandlerService handlerService;

	/**
	 * TODO javadoc
	 * 
	 * @param handlerService
	 * @throws IllegalArgumentException
	 */	
	public HandlerServiceEvent(IHandlerService handlerService)
		throws IllegalArgumentException {		
		super();
		
		if (handlerService == null)
			throw new IllegalArgumentException();
		
		this.handlerService = handlerService;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param object
	 */
	public boolean equals(Object object) {
		if (!(object instanceof HandlerServiceEvent))
			return false;

		HandlerServiceEvent handlerServiceEvent = (HandlerServiceEvent) object;	
		return handlerService.equals(handlerServiceEvent.handlerService);
	}

	/**
	 * TODO javadoc
	 * 
	 * @return
	 */		
	public IHandlerService getHandlerService() {
		return handlerService;
	}
}
