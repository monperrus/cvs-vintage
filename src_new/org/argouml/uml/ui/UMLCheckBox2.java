// $Id: UMLCheckBox2.java,v 1.20 2004/10/23 11:05:44 mvw Exp $
// Copyright (c) 2002-2004 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import javax.swing.Action;
import javax.swing.JCheckBox;
import org.argouml.model.ModelFacade;

import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.tigris.gef.presentation.Fig;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;

/**
 * The checkbox to be used to show boolean attributes in the GUI's. Mostly used
 * on proppanels. The other new GUI elements (like UMLLinkedList) divide the 
 * responsibility of showing an attribute and maintaining the state of the 
 * attribute between a GUI element and a model. This is not the case for the 
 * UMLCheckBox2. Reason for this is that the model is just to simple to allow
 * extra classes for the model. 
 * 
 * @since Oct 12, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public abstract class UMLCheckBox2
    extends JCheckBox
    implements TargetListener, MElementListener {

    private Object checkBoxTarget;
    private String propertySetName;
    
    /**
     * Constructor for UMLCheckBox2.
     * @param text the text of the check box
     * @param a the action we're going to listen to
     * @param name the property set name
     */
    public UMLCheckBox2(String text, Action a, String name) {
        super(text);
        setFont(LookAndFeelMgr.getInstance().getSmallFont());
        propertySetName = name;
        addActionListener(a);

        setActionCommand((String) a.getValue(Action.ACTION_COMMAND_KEY));
    }

    /**
     * @see ru.novosoft.uml.MElementListener#propertySet(
     *          ru.novosoft.uml.MElementEvent)
     */
    public void propertySet(MElementEvent e) {
        if (e.getName().equals(propertySetName))
            buildModel();
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleAdded(
     *          ru.novosoft.uml.MElementEvent)
     */
    public void roleAdded(MElementEvent e) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleRemoved(
     *          ru.novosoft.uml.MElementEvent)
     */
    public void roleRemoved(MElementEvent e) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#listRoleItemSet(
     *          ru.novosoft.uml.MElementEvent)
     */
    public void listRoleItemSet(MElementEvent e) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#removed(
     *          ru.novosoft.uml.MElementEvent)
     */
    public void removed(MElementEvent e) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#recovered(
     *          ru.novosoft.uml.MElementEvent)
     */
    public void recovered(MElementEvent e) {
    }

    /**
     * Returns the target. The target is directly asked from the
     * _target attribute and not from the _container attribute to make
     * the move to a new targeting system as easy as possible.
     *
     * @return Object
     */
    public Object getTarget() {
        return checkBoxTarget;
    }

    /**
     * Sets the target. This method will not be used untill the target does
     * not come via the container.
     * @param target The target to set
     */
    public void setTarget(Object target) {
        target = target instanceof Fig ? ((Fig) target).getOwner() : target;
        if (ModelFacade.isABase(checkBoxTarget)) {
            UmlModelEventPump.getPump().removeModelEventListener(this, 
                    checkBoxTarget, propertySetName);
        }
       
        if (ModelFacade.isABase(target)) {
            checkBoxTarget = target;
	    // UmlModelEventPump.getPump()
	    // .removeModelEventListener(this, (MBase)_target,
	    // _propertySetName);
	    UmlModelEventPump.getPump()
		.addModelEventListener(this, checkBoxTarget, propertySetName);
            buildModel();
        }
            
    }
    
    /**
     * Builds the model. That is: it sets the checkbox to true or
     * false. The name of this method is choosen to be compliant with
     * for example UMLModelElementListModel2.
     */
    public abstract void buildModel();
       

    /**
     * TODO: Is this used? I cannot find the reference to
     * org.argouml.uml.ui.TargetChangedListener#targetChanged(Object)
     *
     * @param newTarget the new target
     *
    public void targetChanged(Object newTarget) {
        if (checkBoxTarget != newTarget)
            setTarget(newTarget);
    }*/

    /**
     * TODO: Is this used? I cannot find the reference to
     * org.argouml.uml.ui.TargetChangedListener#targetReasserted(Object)
     *
    public void targetReasserted(Object newTarget) {
        if (newTarget != checkBoxTarget)
            setTarget(newTarget);
    }*/

    /**
     * @see TargetListener#targetAdded(TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    /**
     * @see TargetListener#targetRemoved(TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

    /**
     * @see TargetListener#targetSet(TargetEvent)
     */
    public void targetSet(TargetEvent e) {
        setTarget(e.getNewTarget());
    }

}
