// The contents of this file are subject to the Mozilla Public License Version
// 1.1
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
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.addressbook.gui.table.model;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.columba.addressbook.folder.HeaderItem;
import org.columba.addressbook.folder.HeaderItemList;

/**
 * Decorates TableModel which additional sorting functionality.
 * <p>
 * Using bubble-sort. Note, that we use an index array, which maps to the
 * real-model decorated by this class. So, we don't change the order of the real
 * model data.
 * <p>
 * TODO:add sorting order (ascending/descending)
 * 
 * @author fdietz
 */
public class SortDecorator extends TableModelDecorator {
	/** ****************** sorting algorithm ******************* */
	private int[] indexes;

	public SortDecorator(HeaderListTableModel model) {
		super(model);
		allocate();
	}

	/**
	 * @see org.columba.addressbook.gui.table.model.HeaderListTableModel#setHeaderList(org.columba.addressbook.folder.HeaderItemList)
	 */
	public void setHeaderList(HeaderItemList list) {
		super.setHeaderList(list);

		tableChanged(new TableModelEvent(getRealModel()));
	}

	/**
	 * @see org.columba.addressbook.gui.table.model.HeaderListTableModel#getHeaderItem(int)
	 */
	public HeaderItem getHeaderItem(int index) {
		return getRealModel().getHeaderItem(indexes[index]);
	}

	public void tableChanged(TableModelEvent e) {
		allocate();
	}

	public Object getValueAt(int row, int column) {
		return getRealModel().getValueAt(indexes[row], column);
	}

	public void setValueAt(Object aValue, int row, int column) {
		getRealModel().setValueAt(aValue, indexes[row], column);
	}

	public void sort(int column) {
		int rowCount = getRowCount();

		for (int i = 0; i < rowCount; i++) {
			for (int j = i + 1; j < rowCount; j++) {
				if (compare(indexes[i], indexes[j], column) < 0) {
					swap(i, j);
				}
			}
		}
	}

	private void swap(int i, int j) {
		int tmp = indexes[i];
		indexes[i] = indexes[j];
		indexes[j] = tmp;
	}

	private int compare(int i, int j, int column) {
		TableModel realModel = getRealModel();
		Object io = realModel.getValueAt(i, column);
		Object jo = realModel.getValueAt(j, column);

		if ((io == null) || (jo == null)) {
			return 0;
		}

		int c = jo.toString().compareTo(io.toString());

		return (c < 0) ? (-1) : ((c > 0) ? 1 : 0);
	}

	private void allocate() {
		indexes = new int[getRowCount()];

		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
	}
}