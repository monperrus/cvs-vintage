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

package org.eclipse.ui.internal.commands;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.commands.IHandler;
import org.eclipse.ui.commands.IPropertyListener;

public class ActionHandler implements IHandler {

	private IAction action;

	public ActionHandler(IAction action) {
		super();
		this.action = action;
	}

	public IAction getAction() {
		return action;
	}

	public void addPropertyListener(IPropertyListener propertyListener) {
	}

	public void execute()
		throws Exception {	
		action.run();
	}

	public void execute(Event event) 
		throws Exception {
		action.runWithEvent(event);
	}

	public Object getProperty(String name)
		throws Exception {
		return null;
	}

	public boolean isEnabled() {
		return action.isEnabled();
	}
	
	public void removePropertyListener(IPropertyListener propertyListener) {	
	}
}
