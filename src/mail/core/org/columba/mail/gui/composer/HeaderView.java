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
//All Rights Reserved.ndation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
package org.columba.mail.gui.composer;

import org.columba.addressbook.folder.HeaderItem;
import org.columba.addressbook.gui.table.AddressbookTableModel;

import org.columba.mail.gui.composer.util.AddressbookTableView;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;


/**
 * JTable including a nested JComboBox.
 * <p>
 * Table contains two column. The first for choosing To:, Cc: or Bcc:,
 * the second column for recipients;
 * <p>
 * TODO: HeaderView should extend AddressbookTableView !
 *
 * @author fdietz
 */
public class HeaderView extends JScrollPane {
    AddressbookTableView table;
    HeaderController controller;

    public HeaderView(HeaderController controller) {
        super();

        this.controller = controller;

        table = new AddressbookTableView();

        getViewport().setBackground(Color.white);

        setViewportView(table);

        setPreferredSize(new Dimension(200, 100));
    }

    public AddressbookTableView getTable() {
        return table;
    }

    public AddressbookTableModel getAddressbookTableModel() {
        return table.getAddressbookTableModel();
    }

    public int count() {
        return getTable().getRowCount();
    }

    public int getSelectedCount() {
        return getTable().getSelectedRowCount();
    }

    public void removeSelected() {
        int[] indices = getTable().getSelectedRows();
        HeaderItem[] items = new HeaderItem[indices.length];

        for (int i = 0; i < indices.length; i++) {
            items[i] = getAddressbookTableModel().getHeaderItem(indices[i]);
        }

        getAddressbookTableModel().removeItem(items);
    }
}
