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

package org.columba.mail.gui.tree.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.columba.mail.message.*;

import org.columba.main.*;

public class EditFolderDialog
{
    private String name;
    private JDialog dialog;
    private boolean bool = false;
    private JTextField textField;
    private JFrame frame;


    public EditFolderDialog(JFrame frame)
    {
        this(frame,"New Folder");
    }

    public EditFolderDialog(JFrame frame, String name )
    {
        this.frame = frame;
        this.name = name;
    }


    public void showDialog()
    {
        JButton[] buttons = new JButton[2];
        JLabel label2 = new JLabel("Choose Name");
        buttons[0] = new JButton("Cancel");
	buttons[0].setDefaultCapable(true);
        buttons[1] = new JButton("Ok");
        textField = new JTextField( name,15);


        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        dialog = new JDialog( frame, true );
        dialog.getContentPane().setLayout( layout );
	dialog.getRootPane().setDefaultButton(buttons[1]);


        c.gridx=0;
        c.gridy=0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.insets = new Insets(10,10,10,10);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(label2, c);

        c.gridx=0;
        c.gridy=1;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(textField, c);

        c.gridx=0;
        c.gridy=2;
        c.weightx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHWEST;
        layout.setConstraints(buttons[0], c);

        c.gridx=1;
        c.gridy=2;
        c.weightx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        layout.setConstraints(buttons[1], c);

        dialog.getContentPane().add ( label2 );
        dialog.getContentPane().add ( textField );
        dialog.getContentPane().add( buttons[0] );
        dialog.getContentPane().add( buttons[1] );
        dialog.pack();
	dialog.setLocationRelativeTo(null);
		
        for ( int i=0; i<2; i++ )
        {
            buttons[i].addActionListener(
                new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            String action = e.getActionCommand();

                            if  ( action.equals("Ok") )
                                {
                                    name = textField.getText();

                                    bool = true;

                                    dialog.dispose();
                                }
                            else if ( action.equals("Cancel") )
                                {
                                    bool = false;

                                    dialog.dispose();
                                }

                        }
                    }
                );
        }



        dialog.show();

            /*

              Integer selectedValue = (Integer) pane.getValue();

              if(selectedValue == null) return -1;
              for(int i = 0; i < options.length; i++) {
              if (options[i].equals(selectedValue)) return i;
              }
            */

    }

    public String getName()
    {
        return name;
    }

    public boolean success()
    {
        return bool;
    }
}
