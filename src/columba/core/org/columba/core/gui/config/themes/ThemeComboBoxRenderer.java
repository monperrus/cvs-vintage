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
package org.columba.core.gui.config.themes;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.columba.core.gui.util.NotifyDialog;
import org.columba.core.main.MainInterface;
import org.columba.core.plugin.PluginHandlerNotFoundException;
import org.columba.core.plugin.ThemePluginHandler;

/**
 * @author frd
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ThemeComboBoxRenderer extends JLabel implements ListCellRenderer {

	ThemePluginHandler pluginHandler;

	protected static Border noFocusBorder;

	public ThemeComboBoxRenderer() {
		super();
		try {

			pluginHandler =
				(ThemePluginHandler) MainInterface.pluginManager.getHandler(
					"org.columba.core.theme");
		} catch (PluginHandlerNotFoundException ex) {
			NotifyDialog d = new NotifyDialog();
			d.showDialog(ex);
		}

		if (noFocusBorder == null) {
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
		}
		setOpaque(true);
		setBorder(noFocusBorder);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setBorder(
			(cellHasFocus)
				? UIManager.getBorder("List.focusCellHighlightBorder")
				: noFocusBorder);

		// id = org.columba.example.HelloWorld$HelloWorldPlugin
		String id = (String) value;

		String userVisibleName = pluginHandler.getUserVisibleName(id);

		setText(userVisibleName);

		return this;
	}

}
