// $Id: TablePanel.java,v 1.15 2003/09/04 20:11:45 thierrylach Exp $
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

package org.argouml.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.Document;

import org.apache.log4j.Logger;
import org.argouml.model.ModelFacade;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.AttrKeyword;
import org.argouml.uml.OperKeyword;
import org.argouml.uml.TableModelComposite;
import org.argouml.uml.diagram.static_structure.MMClassKeyword;
import org.argouml.uml.diagram.static_structure.MMClassVisibility;
import org.argouml.uml.ui.TabModelTarget;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.ui.JSortedTable;

/**
 * @deprecated this class is deprecated since 0.15.1 and should be removed
 *             in 0.15.2, due to the fact that the TableModel classes
 *             have never been used, and are not maintained,
 *
 *             There is no reason why someone cannot try to complete the
 *             TableModel implementation, however, a higher priority
 *             at the moment is to clean argouml of un maintained code.
 */
public class TablePanel extends TabSpawnable
    implements TabModelTarget, 
	       ItemListener, DocumentListener,
	       ListSelectionListener, ActionListener 
{
    protected static Logger cat = 
        Logger.getLogger(TablePanel.class);

    ////////////////////////////////////////////////////////////////
    // instance vars
    protected Object    _target;
    protected JLabel    _contextLabel = new JLabel("Table view of: XXX");
    protected JLabel    _sizeLabel    = new JLabel("   [Rows: 000]");
    protected java.util.Vector    _tableModels  = new java.util.Vector();
    protected JComboBox _persCombo    = null;
    protected JLabel    _persLabel    = new JLabel("Table:");
    protected JButton   _config       = new JButton("Config");
    protected JComboBox _filterCombo  = new JComboBox();
    protected JLabel    _filterLabel  = new JLabel("Filter:");

    // TODO: line to select specified table rows
    //protected JComboBox _selectCombo = new JComboBox();
    //protected JLabel    _selectLabel = new JLabel("Select:");

    protected JSortedTable _table    = new JSortedTable();
    protected JPanel _mainTablePane  = new JPanel();
    protected JPanel _north          = new JPanel();
    protected JPanel _content        = new JPanel();
    protected TableModelComposite _tableModel = null;
    protected JScrollPane _sp1;

    ////////////////////////////////////////////////////////////////
    // constructors

    public TablePanel(String title) {
	super(title);
	initTableModels();
	setLayout(new BorderLayout());
	_content.setLayout(new BorderLayout());

	GridBagLayout gb = new GridBagLayout(); 
	_north.setLayout(gb);
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.weightx = 0.0; c.weighty = 0.0;
	c.ipadx = 3; c.ipady = 3;

	_persCombo = new JComboBox(_tableModels);

	_filterCombo.setEditable(true);
	_filterCombo.getEditor()
	    .getEditorComponent().setBackground(Color.white);
	_filterLabel.setEnabled(false);
	_filterCombo.setEnabled(false);

	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 0.20; c.weighty = 0.0;
	c.gridwidth = 2;
	gb.setConstraints(_contextLabel, c);
	_north.add(_contextLabel);

	c.gridx = 2;
	c.gridy = 0;
	c.weightx = 0.80; c.weighty = 0.0;
	c.gridwidth = 1;
	gb.setConstraints(_sizeLabel, c);
	_north.add(_sizeLabel);

	c.gridx = 0;
	c.gridy = 1;
	c.weightx = 0.0; c.weighty = 0.0;
	c.gridwidth = 1;
	gb.setConstraints(_persLabel, c);
	_north.add(_persLabel);

	c.gridx = 1;
	c.gridwidth = 3;
	c.weightx = 1.0; c.weighty = 0.0;
	gb.setConstraints(_persCombo, c);
	_north.add(_persCombo);

	c.gridx = 5;
	c.gridwidth = 1;
	c.weightx = 0.0; c.weighty = 0.0;
	_config.setMaximumSize(_config.getMinimumSize());
	_config.setPreferredSize(_config.getMinimumSize());
	gb.setConstraints(_config, c);
	_north.add(_config);

	_config.setEnabled(false);

	c.gridx = 0;
	c.gridy = 2;
	c.gridwidth = 1;
	gb.setConstraints(_filterLabel, c);
	_north.add(_filterLabel);

	c.gridx = 1;
	c.gridwidth = 5;
	c.weightx = 1.0; c.weighty = 0.0;
	gb.setConstraints(_filterCombo, c);
	_north.add(_filterCombo);

	_content.add(_north, BorderLayout.NORTH);

	_sp1 = new JScrollPane(_table,
			       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	_sp1.setPreferredSize(new Dimension(300, 400));
	_sp1.setSize(new Dimension(300, 400));

	_content.add(_sp1, BorderLayout.CENTER);
	// TODO: scrollpane does not resize when main divider moved


	_content.setPreferredSize(new Dimension(600, 440));
	_content.setSize(new Dimension(600, 440));

	JScrollPane mainSP =
	    new JScrollPane(_content,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	mainSP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	mainSP.setPreferredSize(new Dimension(800, 450));
	mainSP.setSize(new Dimension(800, 450));
	add(mainSP, BorderLayout.CENTER);

	Font labelFont = MetalLookAndFeel.getSubTextFont();
	_table.setFont(labelFont);
	setEditors(_table);

	_config.addActionListener(this);

	Component ed = _filterCombo.getEditor().getEditorComponent();
	Document filterDoc = ((JTextField) ed).getDocument();
	filterDoc.addDocumentListener(this);
	_persCombo.addItemListener(this);
	_table.getSelectionModel().addListSelectionListener(this);
    }


    protected void setEditors(JTable t) {
	// this is no longer dynamically geneerated since nsuml
	// doesn't have a list of all possible visibilities
	// this should be placed in a file MMVisibility, like MMClassVis...
	java.util.Vector visibilityList = new java.util.Vector();
	visibilityList.addElement(ModelFacade.PUBLIC_VISIBILITYKIND);
	visibilityList.addElement(ModelFacade.PRIVATE_VISIBILITYKIND);
	visibilityList.addElement(ModelFacade.PROTECTED_VISIBILITYKIND);

	JComboBox visCombo = new JComboBox(visibilityList);
	t.setDefaultEditor((Class) ModelFacade.VISIBILITYKIND, 
			   new DefaultCellEditor(visCombo));

	JComboBox clsKeyCombo = new JComboBox(MMClassKeyword.POSSIBLES);
	t.setDefaultEditor(MMClassKeyword.class,
			   new DefaultCellEditor(clsKeyCombo));

	JComboBox clsVisCombo = new JComboBox(MMClassVisibility.POSSIBLES);
	t.setDefaultEditor(MMClassVisibility.class,
			   new DefaultCellEditor(clsVisCombo));

	JComboBox operKeyCombo = new JComboBox(OperKeyword.POSSIBLES);
	t.setDefaultEditor(OperKeyword.class,
			   new DefaultCellEditor(operKeyCombo));

	JComboBox attrKeyCombo = new JComboBox(AttrKeyword.POSSIBLES);
	t.setDefaultEditor(AttrKeyword.class,
			   new DefaultCellEditor(attrKeyCombo));
    }

    public void initTableModels() { }

    ////////////////////////////////////////////////////////////////
    // accessors

    public void setTarget(Object t) {
	_target = t;
	if (null == _persCombo.getSelectedItem())
	    _persCombo.setSelectedIndex(0);

	if (_tableModel != null) {
	    _tableModel.setTarget(_target);
	    _table.setModel(_tableModel);
	    _table.sizeColumnsToFit(0);
	    updateContext();
	}
	else setTablePerspective();
    }

    public void updateContext() {
	String targetName = "" + _target;
	if (ModelFacade.isAModelElement(_target)) {
	    String ocl = "";
	    ocl = ModelFacade.getUMLClassName(_target);
	    targetName = ModelFacade.getName(_target);
	    if ("".equals(targetName)) targetName = "(anon " + ocl + ")";
	}
	if (_target instanceof Diagram) {
	    targetName = ((Diagram) _target).getName();
	}
	int numRows = 0;
	if (_tableModel != null) numRows = _tableModel.getRowCount();
	_contextLabel.setText("Table view of: " + targetName);
	String numRowsStr = "0000" + numRows;
	numRowsStr = numRowsStr.substring(numRowsStr.length() - 3);
	_sizeLabel.setText("   [Rows: " + numRowsStr + "]");
    }

    public Object getTarget() { return _target; }

    public void refresh() {
	_tableModel.setTarget(_target);
	_table.setModel(_tableModel);
	updateContext();
    }

    /**
     * the target must not be null.
     */
    public boolean shouldBeEnabled(Object target) { return target != null; }

    ////////////////////////////////////////////////////////////////
    // actions

    public void setFilter() {
	cat.debug("filter set to: ");
	cat.debug(_filterCombo.getSelectedItem());
    }

    public void setTablePerspective() {
	_tableModel = (TableModelComposite) _persCombo.getSelectedItem();
	_tableModel.setTarget(_target);
	_table.setModel(_tableModel);
	_table.sizeColumnsToFit(0);
	updateContext();
    }


    ////////////////////////////////////////////////////////////////
    // document event handling

    public void insertUpdate(DocumentEvent e) {
	cat.debug(getClass().getName() + " insert");

	Component ed = _filterCombo.getEditor().getEditorComponent();
	Document filterDoc = ((JTextField) ed).getDocument();
	if (e.getDocument() == filterDoc) setFilter();
    }

    public void removeUpdate(DocumentEvent e) { insertUpdate(e); }

    public void changedUpdate(DocumentEvent e) {
    }

    ////////////////////////////////////////////////////////////////
    // combobox event handling

    public void itemStateChanged(ItemEvent e) {
	Object src = e.getSource();
	if (src == _persCombo) {
	    setTablePerspective();
	}
	else if (src == _filterCombo) {
	    setFilter();
	}
    }

    /////////////////////////////////////////////////////////////////
    // ListSelectionListener implemention

    public void valueChanged(ListSelectionEvent lse) {
	if (lse.getValueIsAdjusting()) return;
	Object src = lse.getSource();
	if (src == _table.getSelectionModel()) {
	    int row =
		((DefaultListSelectionModel) lse.getSource())
		.getMinSelectionIndex();
	    if (_tableModel != null) {
		Vector rowObjects = _tableModel.getRowObjects();
		if (row >= 0 && row < rowObjects.size()) {
		    Object sel = rowObjects.elementAt(row);
		    objectSelected(sel);
		    return;
		}
	    }
	}
	objectSelected(null);
    }

    public void objectSelected(Object sel) {   
	TargetManager.getInstance().setTarget(sel);
    }
  
    /////////////////////////////////////////////////////////////////
    // ActionListener implementation

    public void actionPerformed(ActionEvent ae) {
	Object src = ae.getSource();
	//if (src == _config) doConfig();
    }

  
    /* (non-Javadoc)
     * @see
     * org.argouml.ui.targetmanager.TargetListener#targetAdded(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see
     * org.argouml.ui.targetmanager.TargetListener#targetRemoved(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see
     * org.argouml.ui.targetmanager.TargetListener#targetSet(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        // TODO Auto-generated method stub

    }

} /* end class TablePanel */



