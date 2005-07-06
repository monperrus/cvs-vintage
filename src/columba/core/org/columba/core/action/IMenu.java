// The contents of this file are subject to the Mozilla Public License Version
// 1.1
//(the "License"); you may not use this file except in compliance with the
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.core.action;

import org.columba.core.gui.frame.FrameMediator;
import org.columba.core.gui.menu.CMenu;
import org.columba.core.plugin.IExtensionInterface;

public class IMenu extends CMenu implements IExtensionInterface{
	protected FrameMediator controller;

	public IMenu(FrameMediator controller, String caption,String id) {
		super(caption,id);
		this.controller = controller;
	}

	/**
	 * @return FrameController
	 */
	public FrameMediator getFrameMediator() {
		return controller;
	}

	/**
	 * Sets the controller.
	 * 
	 * @param controller
	 *            The controller to set
	 */
	public void setFrameMediator(FrameMediator controller) {
		this.controller = controller;
	}
}