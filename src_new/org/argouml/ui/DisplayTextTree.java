// Copyright (c) 1996-99,2002 The Regents of the University of California. All
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

// File: DisplayTextTree.java
// Classes: DisplayTextTree, DipslayTextTreeRun
// Original Author: ?

package org.argouml.ui;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Category;

import org.argouml.application.api.Configuration;
import org.argouml.application.api.Notation;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.ToDoList;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.uml.generator.GeneratorDisplay;
import org.argouml.uml.ui.UMLTreeCellRenderer;

import org.tigris.gef.base.Diagram;
import org.tigris.gef.presentation.Fig;

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;
import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.behavior.use_cases.MExtensionPoint;
import ru.novosoft.uml.foundation.core.MElement;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;

/**
 * This is the JTree that is the gui component view of the model navigation and
 * todo list.
 *
 * <pre>
 * // 26 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Patch to give a better
 * // naming for extension points in convertValueToText.
 * </pre>
 *
 * $Id: DisplayTextTree.java,v 1.17 2003/04/11 14:23:06 alexb Exp $
 */
public class DisplayTextTree 
    extends JTree
    implements MElementListener{
    
    protected static Category cat = Category.getInstance(DisplayTextTree.class);
    
    /** needs documenting */
    public static final int DEPTH_LIMIT = 10;
    /** needs documenting */
    public static final int CHANGE = 1;
    /** needs documenting */
    public static final int ADD = 2;
    /** needs documenting */
    public static final int REMOVE = 3;
    
    /** needs documenting */
    Hashtable _expandedPathsInModel;
    
    /** needs documenting */
    boolean _reexpanding;
    
    /** needs documenting */
    private boolean showStereotype;
    
    /** needs documenting */
    private DisplayTextTreeRun _doit;
    
    /** needs documenting */
    public DisplayTextTree() {
        
        setCellRenderer(new UMLTreeCellRenderer());
        putClientProperty("JTree.lineStyle", "Angled");
        showStereotype = Configuration
                            .getBoolean(Notation.KEY_SHOW_STEREOTYPES, false);
        _expandedPathsInModel = new Hashtable();
        _reexpanding = false;
        _doit = new DisplayTextTreeRun(cat, this);
    }
    
    // ------------ methods that override JTree methods ---------
    
    /**
     * override default JTree implementation to display the
     * appropriate text for any object that will be displayed in
     * the Nav pane or todo list.
     */
    public String convertValueToText(
                    Object value,
                    boolean selected,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus) {
        
        // do model elements first
        if (value instanceof MModelElement){
            
            String name=null;
            
             // Jeremy Bennett patch
            if(value instanceof MTransition || value instanceof MExtensionPoint){
                name = GeneratorDisplay.Generate(value);
            }
            else{
                name = ((MModelElement)value).getName();
            }
            
            if (name == null || name.equals("")){
                
                name = "(anon " +((MModelElement)value).getUMLClassName() + ")";
            }
            
            // Look for stereotype
            if (showStereotype) {
                MStereotype st = ((MModelElement) value).getStereotype();
                if (st != null) {
                    name += " " + GeneratorDisplay.Generate(st);
                }
            }
            
            return name;
        }
        
        if (value instanceof ToDoItem) {
            return ((ToDoItem) value).getHeadline();
        }
        if (value instanceof ToDoList) {
            return "ToDoList";
        }
        if (value instanceof MTaggedValue) {
            String tagName = ((MTaggedValue) value).getTag();
            if (tagName == null || tagName.equals(""))
                tagName = "(anon)";
            return ("1-" + tagName);
        }
        
        if (value instanceof Diagram) {
            return ((Diagram) value).getName();
        }
        
        return value.toString();
    }
    
    /** needs documenting */
    public void fireTreeWillExpand(TreePath path) {
        
        showStereotype = Configuration
        .getBoolean(Notation.KEY_SHOW_STEREOTYPES, false);
    }
    
    /**
     * Tree MModel Expansion notification.
     *
     * @param e  a Tree node insertion event
     */
    public void fireTreeExpanded(TreePath path) {
        
        super.fireTreeExpanded(path);
        if (_reexpanding)
            return;
        if (path == null || _expandedPathsInModel == null)
            return;
        Vector expanded = getExpandedPaths();
        expanded.removeElement(path);
        expanded.addElement(path);
        addListenerToPath(path);
    }
    
    /** needs documenting */
    public void fireTreeCollapsed(TreePath path) {
        
        super.fireTreeCollapsed(path);
        if (path == null || _expandedPathsInModel == null)
            return;
        Vector expanded = getExpandedPaths();
        expanded.removeElement(path);
    }
    
    /** needs documenting */
    public void setModel(TreeModel newModel) {
        
        Object r = newModel.getRoot();
        if (r != null)
            super.setModel(newModel);
        
        if (r instanceof MBase) {
            // UmlModelEventPump.getPump().removeModelEventListener(this, (MBase)r);
            UmlModelEventPump.getPump().addModelEventListener(this, (MBase) r);
        }
        
        int childCount = newModel.getChildCount(r);
        for (int i = 0; i < childCount; i++) {
            Object child = newModel.getChild(r, i);
            if (child instanceof MBase) {
                // UmlModelEventPump.getPump().removeModelEventListener(this, (MBase)child);
                UmlModelEventPump.getPump().addModelEventListener(
                this,
                (MBase) child);
            }
        }
        reexpand();
    }
    
    // ------------- other methods ------------------
    
    /** needs documenting */
    protected Vector getExpandedPaths() {
        TreeModel tm = getModel();
        Vector res = (Vector) _expandedPathsInModel.get(tm);
        if (res == null) {
            res = new Vector();
            _expandedPathsInModel.put(tm, res);
        }
        return res;
    }
    
    /** needs documenting */
    protected void addListenerToPath(TreePath path) {
        
        Object node = path.getLastPathComponent();
        addListenerToNode(node);
    }
    
    /** needs documenting */
    protected void addListenerToNode(Object node) {
        
        if (node instanceof MBase) {
            // UmlModelEventPump.getPump().removeModelEventListener(this, ((MBase)node));
            UmlModelEventPump.getPump().addModelEventListener(
                                            this,
                                            ((MBase) node));
        }
        TreeModel tm = getModel();
        int childCount = tm.getChildCount(node);
        for (int i = 0; i < childCount; i++) {
            Object child = tm.getChild(node, i);
            if (child instanceof MBase) {
                UmlModelEventPump.getPump().removeModelEventListener(
                                                this,
                                                ((MBase) child));
                UmlModelEventPump.getPump().addModelEventListener(
                                                this,
                                                ((MBase) child));
            }
        }
    }
    
    /** Signals to the tree that something has changed and it is best
     * to update the tree.
     * <P>
     * For complex operations such as import(?) and add attribute(8), that
     * does several calls to this it is better if we defer the actual update
     * until later and if it is not performed don't do an extra.
     * Since import is done from invokeLater() we try to move this down
     * in priority by not running until the second invokeLater().
     * Depending on the queue order in invokeLater() this might result in
     * updates but it is probably far from every file.
     */
    public void forceUpdate() {
        _doit.onceMore();
    }
    
    /**
     * This is the real update function. It won't return until the tree
     * really is updated.
     * <P>
     * Never call this one from any code.
     *
     * @since 0.13.1
     */
    public void doForceUpdate() {
        
        Object rootArray[] = new Object[1];
        rootArray[0] = getModel().getRoot();
        Object noChildren[] = null;
        int noIndexes[] = null;
        TreeModelEvent tme = new TreeModelEvent(this, new TreePath(rootArray));
        treeModelListener.treeStructureChanged(tme);
        TreeModel tm = getModel();
        if (tm instanceof NavPerspective) {
            NavPerspective np = (NavPerspective) tm;
            np.fireTreeStructureChanged(this, rootArray, noIndexes, noChildren);
        }
        reexpand();
    }
    
    /** needs documenting */
    public void reexpand() {
        
        if (_expandedPathsInModel == null)
            return;
        _reexpanding = true;
        Object[] path2 = new Object[1];
        path2[0] = getModel().getRoot();
        TreeModelEvent tme = new TreeModelEvent(this, path2, null, null);
        treeModelListener.treeStructureChanged(tme);
        treeDidChange();
        
        java.util.Enumeration enum = getExpandedPaths().elements();
        while (enum.hasMoreElements()) {
            TreePath path = (TreePath) enum.nextElement();
            tme = new TreeModelEvent(this, path, null, null);
            treeModelListener.treeStructureChanged(tme);
            expandPath(path);
            // addListenerToPath(path);
        }
        _reexpanding = false;
    }
    
    /**
     * This methods sets the target of the treemodel to the given object. It's
     * a means to set the target programmatically from within the setTarget
     * method in the ProjectBrowser.
     * @param target
     */
    public void setTarget(Object target) {
        
        if (getModel() instanceof NavPerspective) {
            if (target instanceof Fig) {
                target = ((Fig)target).getOwner();
            }
            int index =
            getModel().getIndexOfChild(getModel().getRoot(), target);
            if (index > -1)
                setSelectionRow(index);
        }
    }
    
    // ------------ MElementListener impl. --------------
    
    /** this is only needed so that this class can be added
     * as a listener to the model event pump.
     *
     * empty implementation.
     */
    public void listRoleItemSet(MElementEvent e) {
    }
    
    /** this is only needed so that this class can be added
     * as a listener to the model event pump.
     *
     * empty implementation.
     */
    public void propertySet(MElementEvent e) {
    }
    
    /** this is only needed so that this class can be added
     * as a listener to the model event pump.
     *
     * empty implementation.
     */
    public void recovered(MElementEvent e) {
    }
    
    /** this is only needed so that this class can be added
     * as a listener to the model event pump.
     *
     * empty implementation.
     */
    public void removed(MElementEvent e) {
    }
    
    /** this is only needed so that this class can be added
     * as a listener to the model event pump.
     *
     * empty implementation.
     */
    public void roleAdded(MElementEvent e) {
    }
    
    /** this is only needed so that this class can be added
     * as a listener to the model event pump.
     *
     * empty implementation.
     */
    public void roleRemoved(MElementEvent e) {
    }
    
} /* end class DisplayTextTree */


/**
 * Because there <strong>may</strong> be many calls from Argo to update the tree view in
 * a very short space of time (eg. during some automatic processing)
 * , this class will discard all update calls
 * except one in order to improve the <strong>performance</strong> of Argo.
 *
 * <p>This class is used to defer the actual update until "late"
 * in the invokeLater()-call chain.
 *
 * <P>The real update will hopefully take place at the end of whatever long
 * chain of forceUpdate:s that will be performed.
 */
class DisplayTextTreeRun implements Runnable {

    /** needs documenting */
    protected Category cat;
    
    /** needs documenting */
    private DisplayTextTree _tree;
    
    /** needs documenting */
    int _timesToRun;
    
    /** needs documenting */
    boolean _queued;
    
    /** needs documenting */
    public DisplayTextTreeRun(Category c, DisplayTextTree t) {
        cat = c;
        _tree = t;
        _timesToRun = 0;
        _queued = false;
    }
    
    /** needs documenting */
    public synchronized void onceMore() {
        if (!_queued) {
            _queued = true;
            SwingUtilities.invokeLater(this);
        }
        _timesToRun++;
    }
    
    /** needs documenting */
    public synchronized void run() {
        if (_timesToRun > 100)
            cat.debug("" + _timesToRun + " forceUpdates encountered.");
        
        if (_timesToRun > 0) {
            // another forceUpdate was seen, wait again
            _queued = true;
            SwingUtilities.invokeLater(this);
            _timesToRun = 0;
        } else if (_queued) {
            _queued = false;
            SwingUtilities.invokeLater(this);
        } else {
            _tree.doForceUpdate();
        }
    }
}
