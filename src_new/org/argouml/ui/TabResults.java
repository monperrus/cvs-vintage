
// $Id: TabResults.java,v 1.12 2003/08/25 23:57:44 bobtarling Exp $
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Category;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.PredicateFind;
import org.argouml.uml.TMResults;
import org.argouml.uml.cognitive.ChildGenRelated;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.util.ChildGenerator;

public class TabResults extends TabSpawnable
    implements Runnable, MouseListener, ActionListener, ListSelectionListener, KeyListener
{
    protected static Category cat = 
        Category.getInstance(TabResults.class);
    

    public static int _numJumpToRelated = 0;
  
    ////////////////////////////////////////////////////////////////
    // insatnce variables
    PredicateFind _pred;
    ChildGenerator _cg = null;
    Object _root = null;
    JSplitPane _mainPane;
    Vector _results = new Vector();
    Vector _related = new Vector();
    Vector _diagrams = new Vector();
    boolean _showRelated = false;

    JLabel    _resultsLabel = new JLabel("Results:");
    JTable    _resultsTable = new JTable(10, 4);
    TMResults _resultsModel = new TMResults();

    JLabel    _relatedLabel = new JLabel("Related Elements:");
    JTable    _relatedTable = new JTable(4, 4);
    TMResults _relatedModel = new TMResults();

    ////////////////////////////////////////////////////////////////
    // constructor
    public TabResults() {
	this(true);
    }

    public TabResults(boolean showRelated) {
	super("Results", true);
	_showRelated = showRelated;
	setLayout(new BorderLayout());

	JPanel resultsW = new JPanel();
	JScrollPane resultsSP = new JScrollPane(_resultsTable);
	resultsW.setLayout(new BorderLayout());
	resultsW.add(_resultsLabel, BorderLayout.NORTH);
	resultsW.add(resultsSP, BorderLayout.CENTER);
	_resultsTable.setModel(_resultsModel);
	_resultsTable.addMouseListener(this);
    _resultsTable.addKeyListener(this);
	_resultsTable.getSelectionModel().addListSelectionListener(this);
    _resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	resultsW.setMinimumSize(new Dimension(100, 100));

	JPanel relatedW = new JPanel();
	if (_showRelated) {
	    JScrollPane relatedSP = new JScrollPane(_relatedTable);
	    relatedW.setLayout(new BorderLayout());
	    relatedW.add(_relatedLabel, BorderLayout.NORTH);
	    relatedW.add(relatedSP, BorderLayout.CENTER);
	    _relatedTable.setModel(_relatedModel);
	    _relatedTable.addMouseListener(this);
        _relatedTable.addKeyListener(this);
	    relatedW.setMinimumSize(new Dimension(100, 100));
	}

	if (_showRelated) {
	    _mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				       resultsW, relatedW);
	    _mainPane.setDividerSize(2);
	    add(_mainPane, BorderLayout.CENTER);
	} else {
	    add(resultsW, BorderLayout.CENTER);
	}

    }

    ////////////////////////////////////////////////////////////////
    // accessors

    public void setPredicate(PredicateFind p) { _pred = p; }
    public void setRoot(Object root) { _root = root; }
    public void setGenerator(ChildGenerator gen) { _cg = gen; }

    public void setResults(Vector res, Vector dia) {
	_results = res;
	_diagrams = dia;
	_resultsLabel.setText("Results: " + _results.size() + " items");
	_resultsModel.setTarget(_results, _diagrams);
	_relatedModel.setTarget(null, null);
	_relatedLabel.setText("Related Elements: ");
    }

    public TabSpawnable spawn() {
	TabResults newPanel = (TabResults) super.spawn();
	newPanel.setResults(_results, _diagrams);
	return newPanel;
    }
    
    public void doDoubleClick() {
        myDoubleClick(_resultsTable);       
    }
    
    public void selectResult(int index) {
        if (index < _resultsTable.getRowCount()) {
            _resultsTable.getSelectionModel().setSelectionInterval(index, index);
        }
    }
    
    ////////////////////////////////////////////////////////////////
    // ActionListener implementation

    public void actionPerformed(ActionEvent ae) {
    }

    ////////////////////////////////////////////////////////////////
    // MouseListener implementation

    public void mousePressed(MouseEvent me) { }
    public void mouseReleased(MouseEvent me) { }
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() >= 2) myDoubleClick(me.getSource());
    }
    public void mouseEntered(MouseEvent me) { }
    public void mouseExited(MouseEvent me) { }

    public void myDoubleClick(Object src) {
	Object sel = null;
	Diagram d = null;
	if (src == _resultsTable) {
        int row = _resultsTable.getSelectionModel().getMinSelectionIndex();
        if (row < 0) return;
	    sel = _results.elementAt(row);
	    d = (Diagram) _diagrams.elementAt(row);
	}
	else if (src == _relatedTable) {
        int row = _relatedTable.getSelectionModel().getMinSelectionIndex();
        if (row < 0) return;
	    _numJumpToRelated++;
	    sel = _related.elementAt(row);
	}

	if (d != null) cat.debug("go " + sel + " in " + d.getName());    
	if (d != null) TargetManager.getInstance().setTarget(d);
	TargetManager.getInstance().setTarget(sel);
    }

    ////////////////////////////////////////////////////////////////
    // KeyListener implementation

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            e.consume();
            myDoubleClick(e.getSource());
        }
    }

    public void keyReleased(KeyEvent e) { }

    public void keyTyped(KeyEvent e) { }

    ////////////////////////////////////////////////////////////////
    // ListSelectionListener implementation

    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting())
            return;
        Object src = lse.getSource();
        if (_showRelated) {
            int row = lse.getFirstIndex();
            Object sel = _results.elementAt(row);
            cat.debug("selected " + sel);
            _related.removeAllElements();
            java.util.Enumeration enum = ChildGenRelated.SINGLETON.gen(sel);
            if (enum != null) {
                while (enum.hasMoreElements()) {
                    _related.addElement(enum.nextElement());
                }
            }
            _relatedModel.setTarget(_related, null);
            _relatedLabel.setText("Related Elements: " + _related.size()
				  + " items");
        }
    }

    ////////////////////////////////////////////////////////////////
    // actions

    public void run() {
	_resultsLabel.setText("Searching...");
	_results.removeAllElements();
	depthFirst(_root, null);
	setResults(_results, _diagrams);
	_resultsLabel.setText("Results: " + _results.size() + " items");
	_resultsModel.setTarget(_results, _diagrams);
    }

    public void depthFirst(Object node, Diagram lastDiagram) {
	if (node instanceof Diagram) {
	    lastDiagram = (Diagram) node;
	    if (!_pred.matchDiagram(lastDiagram)) return;
	    // diagrams are not placed in search results
	}
	java.util.Enumeration enum =  _cg.gen(node);
	while (enum.hasMoreElements()) {
	    Object c = enum.nextElement();
	    if (_pred.predicate(c)) {
		_results.addElement(c);
		_diagrams.addElement(lastDiagram);
	    }
	    depthFirst(c, lastDiagram);
	}
    }

} /* end class TabResults */

