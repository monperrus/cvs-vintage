// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products may
// be obtained by contacting the University of California. David F. Redmiles
// Department of Information and Computer Science (ICS) University of
// California Irvine, California 92697-3425 Phone: 714-824-3823. This software
// program and documentation are copyrighted by The Regents of the University
// of California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.


package uci.uml.ui;

//import jargo.kernel.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;
import com.sun.java.swing.text.*;
import com.sun.java.swing.border.*;

import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Model_Management.*;


// needs-more-work: list of implemented interfaces
// needs-more-work: setting base class

public class PropPanelClass extends PropPanel
implements ItemListener, DocumentListener {

  ////////////////////////////////////////////////////////////////
  // constants
  public static final VisibilityKind
  VISIBILITIES[] = { VisibilityKind.PUBLIC, VisibilityKind.PACKAGE };
  public static final String CLASSKEYWORDS[] = { "None", "abstract", "final"};

  
  ////////////////////////////////////////////////////////////////
  // instance vars
  JLabel _visLabel = new JLabel("Visibility: ");
  JComboBox _visField = new JComboBox(VISIBILITIES);
  JLabel _keywordsLabel = new JLabel("Keywords: ");
  JComboBox _keywordsField = new JComboBox(CLASSKEYWORDS);
  JLabel _extendsLabel = new JLabel("Extends: ");
  JComboBox _extendsField = new JComboBox();
  JLabel _impleLabel = new JLabel("Implements: ");
  JList _implList = new JList();
  SpacerPanel _spacer = new SpacerPanel();

  ////////////////////////////////////////////////////////////////
  // contructors
  public PropPanelClass() {
    super("Class Properties");
    GridBagLayout gb = (GridBagLayout) getLayout();    
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.0;
    c.ipadx = 0; c.ipady = 0;


    //_visField.getEditor().getEditorComponent().setBackground(Color.white);
    //_keywordsField.getEditor().getEditorComponent().setBackground(Color.white);
    _extendsField.setEditable(true);
    _extendsField.getEditor().getEditorComponent().setBackground(Color.white);

    Component ed = _extendsField.getEditor().getEditorComponent();
    Document extendsDoc = ((JTextField)ed).getDocument();
    extendsDoc.addDocumentListener(this);
    _visField.addItemListener(this);
    _keywordsField.addItemListener(this);
    _extendsField.addItemListener(this);


    //_extendsField.setRenderer(new ModelElementRenderer());
    
    _implList.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    //_implList.addList...Listener(this);
    
    c.gridx = 0;
    c.gridwidth = 1;
    c.gridy = 1;
    gb.setConstraints(_visLabel, c);
    add(_visLabel);
    c.gridy = 2;
    gb.setConstraints(_keywordsLabel, c);
    add(_keywordsLabel);
    c.gridy = 3;
    gb.setConstraints(_extendsLabel, c);
    add(_extendsLabel);


    
    c.weightx = 1.0;
    c.gridx = 1;
    //c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridy = 1;
    gb.setConstraints(_visField, c);
    add(_visField);
    c.gridy = 2;
    gb.setConstraints(_keywordsField, c);
    add(_keywordsField);
    c.gridy = 3;
    gb.setConstraints(_extendsField, c);
    add(_extendsField);

    c.weightx = 0.0;
    c.gridx = 2;
    c.gridy = 0;
    gb.setConstraints(_spacer, c);
    add(_spacer);
    

    c.weightx = 1.0;
    c.gridwidth = 3;
    c.gridx = 3;
    //c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridy = 0;
    gb.setConstraints(_impleLabel, c);
    add(_impleLabel);
    c.gridy = 1;
    c.gridheight = GridBagConstraints.REMAINDER;
    gb.setConstraints(_implList, c);
    add(_implList);

  }

  ////////////////////////////////////////////////////////////////
  // accessors

  public void setTarget(Object t) {
    super.setTarget(t);
    MMClass cls = (MMClass) t;

    VisibilityKind vk = cls.getElementOwnership().getVisibility();
    _visField.setSelectedItem(vk);

    if (cls.getIsAbstract())
      _keywordsField.setSelectedItem("abstract");
    else if (cls.getIsLeaf())
      _keywordsField.setSelectedItem("final");
    else 
      _keywordsField.setSelectedItem("None");

    Vector gens = cls.getGeneralization();
    Generalization gen = null;
    if (gens != null && gens.size() == 1)
      gen = (Generalization) gens.firstElement();
    if (gen == null) {
      System.out.println("null base class");
      _extendsField.setSelectedItem(null);
    }
    else {
      System.out.println("base class found");
      _extendsField.setSelectedItem(gen.getSupertype());
    }
    
    updateExtendsChoices();
  }


  public void setTargetExtends() {
    if (_target == null) return;
    Object base = _extendsField.getSelectedItem();
    System.out.println("base = " + base);
    // needs-more-work: this could involve changes to the graph model
  }

  public void setTargetVisibility() {
    if (_target == null) return;
    VisibilityKind vk = (VisibilityKind) _visField.getSelectedItem();
    MMClass cls = (MMClass) _target;
    cls.getElementOwnership().setVisibility(vk);
  }

  public void setTargetKeywords() {
    if (_target == null) return;
    String keys = (String) _keywordsField.getSelectedItem();
    if (keys == null) {
      System.out.println("keywords are null");
      return;
    }
    MMClass cls = (MMClass) _target;
    try {
      if (keys.equals("None")) {
	cls.setIsAbstract(false);
	cls.setIsLeaf(false);
      }
      else if (keys.equals("abstract")) {
	cls.setIsAbstract(true);
	cls.setIsLeaf(false);
      }
      else if (keys.equals("final")) {
	cls.setIsAbstract(false);
      cls.setIsLeaf(true);
      }
    }
    catch (PropertyVetoException pve) {
      System.out.println("could not set keywords!");
    }
  }

  ////////////////////////////////////////////////////////////////
  // utility functions

  public void updateExtendsChoices() {
    // needs-more-work: build a list of existing (non-final) classes
  }

  
  ////////////////////////////////////////////////////////////////
  // event handling

  public void insertUpdate(DocumentEvent e) {
    System.out.println(getClass().getName() + " insert");
    Component ed = _extendsField.getEditor().getEditorComponent();
    Document extendsDoc = ((JTextField)ed).getDocument();
    if (e.getDocument() == extendsDoc) setTargetExtends();
    super.insertUpdate(e);
  }

  public void removeUpdate(DocumentEvent e) { insertUpdate(e); }

  public void changedUpdate(DocumentEvent e) {
    System.out.println(getClass().getName() + " changed");
    // Apparently, this method is never called.
  }


  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    if (src == _keywordsField) {
      System.out.println("class keywords now is " +
			 _keywordsField.getSelectedItem());
      setTargetKeywords();
    }
    else if (src == _visField) {
      System.out.println("class VisibilityKind now is " +
			 _visField.getSelectedItem());
      setTargetVisibility();
    }
    else if (src == _extendsField) {
      System.out.println("class extends now is " +
			 _extendsField.getSelectedItem());
      setTargetExtends();
    }
    
  }

  
} /* end class PropPanelClass */
