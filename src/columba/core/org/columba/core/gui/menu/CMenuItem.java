//The contents of this file are subject to the Mozilla Public License Version 1.1
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
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.

package org.columba.core.gui.menu;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.columba.core.action.BasicAction;
import org.columba.core.help.HelpManager;

/**
 * Default MenuItem which automatically sets a JavaHelp topic ID
 * based on the AbstractAction name attribute.
 * <p>
 * This is necessary to provide a complete context-specific help.
 * 
 *
 * @author fdietz
 */
public class CMenuItem extends JMenuItem {

	public CMenuItem( AbstractAction action )
	{
		super(action);
		
		HelpManager.enableHelpOnButton(this, ((BasicAction) action).getName());
	}
}
