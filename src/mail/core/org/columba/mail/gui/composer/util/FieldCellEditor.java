// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package org.columba.mail.gui.composer.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author frd
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FieldCellEditor extends DefaultCellEditor 
{
	JComboBox comboBox;
	AddressbookTableView table;
	
	public FieldCellEditor( JComboBox comboBox, AddressbookTableView table ) {
		
		super(comboBox);
		
		this.comboBox = (JComboBox) comboBox;
		this.table = table;
		
		
		editorComponent = comboBox;
		setClickCountToStart(1); //This is usually 1 or 2.


			
		//Must do this so that editing stops when appropriate.
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("fieldcelleditor->fireEditingStopped");
				
				
				
				fireEditingStopped();
				
				
			}
		});
		
	}
	
	protected void fireEditingStopped() {
		super.fireEditingStopped();
		
		table.appendRow();
		
		
	}

	public Object getCellEditorValue() {
		return comboBox.getSelectedItem();
	}

	public Component getTableCellEditorComponent(
		JTable table,
		Object value,
		boolean isSelected,
		int row,
		int column) {
		
		comboBox.setSelectedItem( value.toString() );
		
		return editorComponent;
	}
	
	

}
