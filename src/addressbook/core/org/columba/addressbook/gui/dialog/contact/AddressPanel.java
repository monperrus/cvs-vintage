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
package org.columba.addressbook.gui.dialog.contact;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.columba.addressbook.folder.ContactCard;
import org.columba.addressbook.gui.util.LabelTextFieldPanel;


public class AddressPanel extends JPanel {
    JTextField phone1TextField;
    AttributComboBox phone1ComboBox;

    /*
    JTextField phone2TextField;
    AttributComboBox phone2ComboBox;

    JTextField phone3TextField;
    AttributComboBox phone3ComboBox;

    JTextField phone4TextField;
    AttributComboBox phone4ComboBox;
    */
    JTextArea addressTextArea;
    AttributComboBox addressComboBox;

    public AddressPanel() {
        initComponent();
    }

    /*
    protected void set( AdapterNode rootNode, String key, JTextField textField )
    {
            AdapterNode node = rootNode.getChild(key);
            if ( node != null )
            {
                    textField.setText( node.getValue() );
            }
    }
    */
    public void updateComponents(ContactCard card, boolean b) {
        phone1ComboBox.updateComponents(card, b);

        /*
        phone2ComboBox.updateComponents(rootNode,b);
        phone3ComboBox.updateComponents(rootNode,b);
        phone4ComboBox.updateComponents(rootNode,b);
        */
        addressComboBox.updateComponents(card, b);

        if (b == true) {
        } else {
        }
    }

    protected void initComponent() {
        setLayout(new BorderLayout());

        //setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

        LabelTextFieldPanel panel = new LabelTextFieldPanel();
        add(panel, BorderLayout.NORTH);

        List v = new Vector();
        v.add("home"); //$NON-NLS-1$
        v.add("work"); //$NON-NLS-1$
        v.add("pref"); //$NON-NLS-1$
        v.add("voice"); //$NON-NLS-1$
        v.add("fax"); //$NON-NLS-1$
        v.add("msg"); //$NON-NLS-1$
        v.add("cell"); //$NON-NLS-1$
        v.add("pager"); //$NON-NLS-1$
        v.add("bbs"); //$NON-NLS-1$
        v.add("modem"); //$NON-NLS-1$
        v.add("car"); //$NON-NLS-1$
        v.add("isdn"); //$NON-NLS-1$
        v.add("video"); //$NON-NLS-1$
        v.add("pcs"); //$NON-NLS-1$

        phone1TextField = new JTextField(20);
        phone1ComboBox = new AttributComboBox("tel", v, phone1TextField); //$NON-NLS-1$

        panel.addLabel(phone1ComboBox);
        panel.addTextField(phone1TextField);

        /*
        phone2TextField = new JTextField(20);
        phone2ComboBox = new AttributComboBox(v, phone2TextField);

        panel.addLabel( phone2ComboBox );
        panel.addTextField( phone2TextField );

        phone3TextField = new JTextField(20);
        phone3ComboBox = new AttributComboBox(v, phone3TextField);

        panel.addLabel( phone3ComboBox );
        panel.addTextField( phone3TextField );

        phone4TextField = new JTextField(20);
        phone4ComboBox = new AttributComboBox(v, phone4TextField);

        panel.addLabel( phone4ComboBox );
        panel.addTextField( phone4TextField );
        */
        v = new Vector();
        v.add("home"); //$NON-NLS-1$
        v.add("work"); //$NON-NLS-1$
        v.add("pref"); //$NON-NLS-1$
        v.add("dom"); //$NON-NLS-1$
        v.add("intl"); //$NON-NLS-1$
        v.add("postal"); //$NON-NLS-1$
        v.add("parcel"); //$NON-NLS-1$

        addressTextArea = new JTextArea(5, 20);
        addressTextArea.setEnabled(false);
        addressComboBox = new AttributComboBox("adr", v, addressTextArea); //$NON-NLS-1$
        addressComboBox.setEnabled(false);
        panel.addLabel(addressComboBox);
        panel.addTextField(new JScrollPane(addressTextArea));
    }
}
