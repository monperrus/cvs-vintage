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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.columba.core.gui.util.*;

import java.util.Vector;

/**
 * @version 	1.0
 * @author
 */
public class SubjectDialog implements ActionListener {
	private JDialog dialog;
	private boolean bool = false;
	private JTextField subjectTextField;
	private String subject;
	
	JButton[] buttons = new JButton[1];

	public SubjectDialog() {
		
	}

	public void showDialog(String subject) {
		this.subject = subject;

		JLabel questionLabel = new JLabel("Please enter subject!");
		JLabel subjectLabel = new JLabel("Subject:");

		buttons[0] = new JButton("Ok");
		buttons[0].addActionListener(this);
		buttons[0].setActionCommand("OK");
		buttons[0].setDefaultCapable(true);
		buttons[0].setSelected(true);

		subjectTextField = new JTextField(subject, 30);
		subjectTextField.setCaretPosition( subject.length() );
		subjectTextField.selectAll();
		subjectTextField.getDocument().addDocumentListener(new MyDocumentListener());
		

		dialog = DialogStore.getDialog();
		dialog.setTitle( "Enter Subject..." );
		//dialog.getContentPane().setLayout( new BoxLayout( dialog.getContentPane(), BoxLayout.Y_AXIS ) );
		dialog.getContentPane().setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		//centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.Y_AXIS ) );
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		/*
		TitledBorder etched = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), " Login ");
		centerPanel.setBorder( etched );
		*/

		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());

		JPanel leftInformationPanel = new JPanel();
		leftInformationPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		leftInformationPanel.setLayout(new GridLayout(0, 1, 50, 5));
		panel.add(leftInformationPanel, BorderLayout.CENTER);

		JPanel rightInformationPanel = new JPanel();
		rightInformationPanel.setLayout(new GridLayout(0, 1, 50, 5));
		rightInformationPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		panel.add(rightInformationPanel, BorderLayout.EAST);

		leftInformationPanel.add(subjectLabel);
		rightInformationPanel.add(subjectTextField);

		//centerPanel.add( Box.createRigidArea( new java.awt.Dimension(0,5) ) );

		centerPanel.add(panel, BorderLayout.NORTH);

		/*	
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
		panel.add( hostLabel );
		centerPanel.add( panel );
		*/

		//centerPanel.add( Box.createRigidArea( new java.awt.Dimension(0,5) ) );

		//centerPanel.add( Box.createRigidArea( new java.awt.Dimension(0,5) ) );

		dialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));
		buttonPanel.add(buttons[0]);
		

		bottomPanel.add(buttonPanel, BorderLayout.EAST);

		dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		dialog.pack();

		dialog.getRootPane().setDefaultButton(buttons[0]);

		/*
		java.awt.Dimension dim = new Dimension(300, 200);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		dialog.setLocation(
			screenSize.width / 2 - dim.width / 2,
			screenSize.height / 2 - dim.height / 2);
		*/
		dialog.setLocationRelativeTo(null);
		dialog.show();
	}

	public boolean success() {
		return bool;

	}

	public String getSubject() {
		return subject;
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (action.equals("OK")) {

			subject = subjectTextField.getText();

			bool = true;

			dialog.dispose();
		}

	}

	class MyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			if (subjectTextField.getText().length() == 0)
				buttons[0].setEnabled(false);
			else
				buttons[0].setEnabled(true);

		}
		public void removeUpdate(DocumentEvent e) {
			if (subjectTextField.getText().length() == 0)
				buttons[0].setEnabled(false);
			else
				buttons[0].setEnabled(true);
		}
		public void changedUpdate(DocumentEvent e) {
			//Plain text components don't fire these events
		}

	};
}