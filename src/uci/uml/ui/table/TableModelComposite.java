// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.


package uci.uml.ui.table;

//import jargo.kernel.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;
import com.sun.java.swing.table.*;

import uci.util.*;
import uci.uml.ui.*;
// import uci.uml.Foundation.Core.*;
// import uci.uml.Foundation.Data_Types.*;
// import uci.uml.Model_Management.*;
// import uci.uml.Behavioral_Elements.Use_Cases.*;

class TableModelComposite extends AbstractTableModel
implements TableModelTarget {

  ////////////////
  // instance varables
  Vector _rowObjects = new Vector();
  Vector _colDescs = new Vector();
  boolean _allowAddition = false;
  boolean _allowRemoval = false;
  Predicate _pred = PredicateTrue.theInstance();

  ////////////////
  // constructor
  public TableModelComposite() {
    initColumns();
  }

  public void initColumns() { }

  ////////////////
  // accessors

  public void addColumn(ColumnDescriptor cd) { _colDescs.addElement(cd); }
  public void setAllowAddition(boolean b) { _allowAddition = b; }
  public void setAllowRemoval(boolean b) { _allowRemoval = b; }

  public void setTarget(Object target) {
    Vector rowObjects = rowObjectsFor(target);
    _rowObjects = rowObjects;
    fireTableStructureChanged();
  }

  public Vector rowObjectsFor(Object t) {
    System.out.println("default rowObjectsFor called. bad!");
    return new Vector();
  }

  public Vector getRowObjects() { return _rowObjects; }

  public void setFilter(Predicate p) { _pred = p; }

  ////////////////
  // TableModel implemetation
  public int getColumnCount() {
    return _colDescs.size();
  }

  public String  getColumnName(int c) {
    if (c < _colDescs.size()) {
      ColumnDescriptor cd = (ColumnDescriptor) _colDescs.elementAt(c);
      return cd.getName();
    }
    return "XXX";
  }

  public Class getColumnClass(int c) {
    if (c < _colDescs.size()) {
      ColumnDescriptor cd = (ColumnDescriptor) _colDescs.elementAt(c);
      return cd.getColumnClass();
    }
    System.out.println("asdwasd");
    return String.class;
  }

  public boolean isCellEditable(int row, int col) {
    if (row < 0 || row >= _rowObjects.size()) return false;
    if (col < 0 || col >= _colDescs.size()) return false;
    Object rowObj = _rowObjects.elementAt(row);
    ColumnDescriptor cd = (ColumnDescriptor) _colDescs.elementAt(col);
    return cd.isEditable(rowObj);
  }

  public int getRowCount() {
    int numRows = _rowObjects.size();
    if (_allowAddition) numRows++;
    return numRows;
  }

  public Object getValueAt(int row, int col) {
    if (row >= 0 && row < _rowObjects.size()) {
      if (col >= 0 && col < _colDescs.size()) {
	Object rowObj = _rowObjects.elementAt(row);
	ColumnDescriptor cd = (ColumnDescriptor) _colDescs.elementAt(col);
	return cd.getValueFor(rowObj);
      }
    }
    return "TC-" + row +","+col; // for debugging
  }

  public void setValueAt(Object val, int row, int col)  {
    if (row >= 0 && row < _rowObjects.size()) {
      if (col >= 0 && col < _colDescs.size()) {
	Object rowObj = _rowObjects.elementAt(row);
	ColumnDescriptor cd = (ColumnDescriptor) _colDescs.elementAt(col);
	cd.setValueFor(rowObj, val);
	return;
      }
    }

    if (_allowAddition && row >= _rowObjects.size()) {
      //@ needs-more-work
      //_rowObjects.addElement(val);
      fireTableStructureChanged();
    }
    else if (_allowRemoval && val.equals("")) {
      _rowObjects.removeElementAt(row);
      fireTableStructureChanged();
    }
  }

  ////////////////
  // event handlers

  public void vetoableChange(PropertyChangeEvent pce) {
    DelayedChangeNotify delayedNotify = new DelayedChangeNotify(this, pce);
    SwingUtilities.invokeLater(delayedNotify);
  }

  public void delayedVetoableChange(PropertyChangeEvent pce) {
    fireTableStructureChanged(); //?
  }

} /* end class TableModelComposite */

