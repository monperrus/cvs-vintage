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
package org.columba.addressbook.gui.tree.util;

import org.columba.addressbook.util.AddressbookResourceLoader;

import org.columba.core.gui.util.ButtonWithMnemonic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class EditAddressbookFolderDialog {
    private String name;

    //private MainInterface mainInterface;
    private JDialog dialog;
    private boolean bool = false;
    private JTextField textField;
    private JFrame frame;

    public EditAddressbookFolderDialog(JFrame frame) {
        this.frame = frame;
        name = new String("New Folder");
    }

    public EditAddressbookFolderDialog(JFrame frame, String name) {
        this.frame = frame;
        this.name = name;
    }

    public void showDialog() {
        JButton[] buttons = new JButton[2];
        JLabel label2 = new JLabel("Choose Name");
        buttons[0] = new ButtonWithMnemonic(AddressbookResourceLoader.getString(
                    "global", "cancel"));
        buttons[0].setActionCommand("Cancel");
        buttons[0].setDefaultCapable(true);
        buttons[1] = new ButtonWithMnemonic(AddressbookResourceLoader.getString(
                    "global", "ok"));
        buttons[1].setActionCommand("Ok");
        textField = new JTextField(name, 15);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        dialog = new JDialog(frame, true);
        dialog.getContentPane().setLayout(layout);
        dialog.getRootPane().setDefaultButton(buttons[1]);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(label2, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(textField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHWEST;
        layout.setConstraints(buttons[0], c);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        layout.setConstraints(buttons[1], c);

        dialog.getContentPane().add(label2);
        dialog.getContentPane().add(textField);
        dialog.getContentPane().add(buttons[0]);
        dialog.getContentPane().add(buttons[1]);

        //dialog.setSize( new Dimension( 320, 200 ) );
        dialog.pack();

        java.awt.Dimension dim = dialog.getPreferredSize();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        dialog.setLocation((screenSize.width / 2) - (dim.width / 2),
            (screenSize.height / 2) - (dim.height / 2));

        for (int i = 0; i < 2; i++) {
            buttons[i].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String action = e.getActionCommand();

                        if (action.equals("Ok")) {
                            name = textField.getText();

                            bool = true;

                            dialog.dispose();
                        } else if (action.equals("Cancel")) {
                            bool = false;

                            dialog.dispose();
                        }
                    }
                });
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

    public String getName() {
        return name;
    }

    public boolean success() {
        return bool;
    }
}
