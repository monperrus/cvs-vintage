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

package org.columba.mail.gui.config.filter;

import org.columba.mail.config.*;
import org.columba.mail.message.*;
import org.columba.main.*;
import org.columba.mail.filter.*;

import org.columba.mail.gui.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;


import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.text.*;

public class DateCriteriaRow extends DefaultCriteriaRow implements ActionListener
{

	private JComboBox matchComboBox;
	private JButton dateButton;

	public DateCriteriaRow(
		CriteriaList criteriaList,
		FilterCriteria c)
	{
		super(criteriaList, c);

	}

	protected void updateComponents(boolean b)
	{
		super.updateComponents(b);

		if (b)
		{
			matchComboBox.setSelectedItem(criteria.getCriteriaString());
			//textField.setText(criteria.getPattern());
			dateButton.setText( criteria.getPattern() );
		}
		else
		{
			criteria.setCriteria((String) matchComboBox.getSelectedItem());
			//criteria.setPattern((String) textField.getText());
			criteria.setPattern( (String) dateButton.getText() );
		}

	}

	public void initComponents()
	{
		super.initComponents();

		matchComboBox = new JComboBox();
		matchComboBox.addItem("before");
		matchComboBox.addItem("after");
		c.gridx = 1;
		gridbag.setConstraints(matchComboBox, c);
		add(matchComboBox);

		//textField = new JTextField("date", 12);
		dateButton = new JButton("date");
		dateButton.setActionCommand("DATE");
		dateButton.addActionListener( this );

		c.gridx = 2;
		gridbag.setConstraints(dateButton, c);
		add(dateButton);

		finishRow();
	}

	public void actionPerformed( ActionEvent ev )
	{
		String action = ev.getActionCommand();

		if ( action.equals("DATE") )
		{
			DateFormat f = DateFormat.getDateInstance();
			Date d = null;
			try
			{
				d = f.parse( dateButton.getText() );
			}
			catch ( Exception ex )
			{
				//ex.printStackTrace();
			}


			DateChooserDialog dialog = new DateChooserDialog(MainInterface.frameController.getView());
			if ( d != null ) dialog.setDate( d );
			dialog.setVisible(true);

			if ( dialog.success() == true )
			{
				// Ok
				Date date = dialog.getDate();
				dateButton.setText( f.format(date) );
			}
			else
			{
				// cancel
			}
		}
	}

}