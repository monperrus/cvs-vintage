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
package org.columba.mail.gui.composer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author frd
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PriorityController implements ItemListener{
	
	PriorityView view;
	ComposerController controller;
		
	public PriorityController(ComposerController controller)
	{
		this.controller = controller;
		
		view = new PriorityView( this );
		
	
	}
	
	public void installListener()
	{
		view.installListener(this);
	}
	
	
	public void updateComponents( boolean b )
	{
		if ( b == true )
		{
			//view.setSelectedItem( model.getHeaderField("X-Priority") );
		}
		else
		{
			((ComposerModel)controller.getModel()).setPriority( (String) view.getSelectedItem() );
			//model.setHeaderField("X-Priority",(String) view.getSelectedItem());
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
			((ComposerModel)controller.getModel()).setPriority( (String) view.getSelectedItem() );

        } 
    }
	
}
