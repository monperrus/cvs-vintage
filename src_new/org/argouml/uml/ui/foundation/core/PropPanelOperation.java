// $Id: PropPanelOperation.java,v 1.100 2006/04/15 18:57:35 mvw Exp $
// Copyright (c) 1996-2006 The Regents of the University of California. All
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

package org.argouml.uml.ui.foundation.core;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNewModelElement;
import org.argouml.uml.ui.ActionDeleteSingleModelElement;
import org.argouml.uml.ui.ActionNavigateOwner;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLTextArea2;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;

/**
 * A property panel for operations.
 */
public class PropPanelOperation extends PropPanelFeature {

    /**
     * The serial version.
     */
    private static final long serialVersionUID = -8231585002039922761L;
    
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(PropPanelOperation.class);


    /**
     * The constructor.
     */
    public PropPanelOperation() {
        super("Operation", lookupIcon("Operation"), ConfigLoader
                .getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.owner"),
                getOwnerScroll());
        addField(Translator.localize("label.parameters"),
                new JScrollPane(new UMLLinkedList(
                new UMLClassifierParameterListModel())));

        addSeparator();

        add(getVisibilityPanel());

        JPanel modifiersPanel = createBorderPanel(Translator.localize(
                "label.modifiers"));
        modifiersPanel.add(new UMLGeneralizableElementAbstractCheckBox());
        modifiersPanel.add(new UMLGeneralizableElementLeafCheckBox());
        modifiersPanel.add(new UMLGeneralizableElementRootCheckBox());
        modifiersPanel.add(new UMLBehavioralFeatureQueryCheckBox());
        modifiersPanel.add(new UMLFeatureOwnerScopeCheckBox());
        add(modifiersPanel);

        add(new UMLOperationConcurrencyRadioButtonPanel(
                Translator.localize("label.concurrency"), true));

        addSeparator();

        addField(Translator.localize("label.raisedsignals"),
               new JScrollPane(new UMLLinkedList(
                       new UMLOperationRaisedSignalsListModel())));

        addField(Translator.localize("label.methods"),
               new JScrollPane(new UMLLinkedList(
                       new UMLOperationMethodsListModel())));

        addField(Translator.localize("label.specification"), 
                new JScrollPane(new UMLTextArea2(
                        new UMLOperationSpecificationDocument())));

        addAction(new ActionNavigateOwner());
        addAction(TargetManager.getInstance().getAddOperationAction());
        addAction(new ActionNewParameter());
        addAction(new ActionNewRaisedSignal());
        addAction(new ActionNewMethod());
        addAction(new ActionAddDataType());
        addAction(new ActionAddEnumeration());
        addAction(new ActionNewStereotype());
        addAction(new ActionDeleteSingleModelElement());
    }


    /**
     * Add a new RaisedSignal to the current target.
     */
    public void addRaisedSignal() {
        Object target = getTarget();
        if (Model.getFacade().isAOperation(target)) {
            Object oper = /* (MOperation) */target;
            Object newSignal = Model.getCommonBehaviorFactory()
                    .createSignal();
                    //((MOperation)oper).getFactory().createSignal();

            Model.getCoreHelper().addOwnedElement(
                    Model.getFacade().getNamespace(
                            Model.getFacade().getOwner(oper)),
                    newSignal);
            Model.getCoreHelper().addRaisedSignal(oper, newSignal);
            TargetManager.getInstance().setTarget(newSignal);
        }
    }

    /**
     * Add a Method to the current target.
     */
    public void addMethod() {
        Object target = getTarget();
        if (Model.getFacade().isAOperation(target)) {
            Object oper = /* (MOperation) */target;
            String name = Model.getFacade().getName(oper);
            Object newMethod = Model.getCoreFactory().buildMethod(name);
            Model.getCoreHelper().addMethod(oper, newMethod);
            Model.getCoreHelper().addFeature(Model.getFacade().getOwner(oper),
                newMethod);
            TargetManager.getInstance().setTarget(newMethod);
        }
    }

    /**
     * @deprecated before 0.21.2 by tfmorris - use the argumentless version of
     *             this method
     * @param index
     *            ignored
     */
    public void addRaisedSignal(Integer index) {
        LOG.warn("Ignoring index parameter to addRaisedSignal: " + index);
        addRaisedSignal();
    }
    
    /**
     * @deprecated before 0.21.2 by tfmorris - use the argumentless version of
     *             this method
     * @param index
     *            ignored
     */
    public void addMethod(Integer index) {
        LOG.warn("Ignoring index parameter to addMethod: " + index);
        addMethod();
    }


    private class ActionNewRaisedSignal extends AbstractActionNewModelElement {

        /**
         * The serial version.
         */
        private static final long serialVersionUID = -2380798799656866520L;

        /**
         * Construct an action to create a new RaisedSignal.
         */
        public ActionNewRaisedSignal() {
            super("button.new-raised-signal");
            putValue(Action.NAME,
                    Translator.localize("button.new-raised-signal"));
            Icon icon = ResourceLoaderWrapper.lookupIcon("SignalSending");
            putValue(Action.SMALL_ICON, icon);
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object target = TargetManager.getInstance().getModelTarget();
            if (Model.getFacade().isAOperation(target)) {
                addRaisedSignal();
                super.actionPerformed(e);
            }
        }
    }


    private class ActionNewMethod extends AbstractActionNewModelElement {

        /**
         * The serial version.
         */
        private static final long serialVersionUID = 1605755146025527381L;

        /**
         * Construct an action to create a new Method.
         */
        public ActionNewMethod() {
            super("button.new-method");
            putValue(Action.NAME,
                    Translator.localize("button.new-method"));
            Icon icon = ResourceLoaderWrapper.lookupIcon("Method");
            putValue(Action.SMALL_ICON, icon);
        }
        
        /**
         * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
         */
        public boolean shouldBeEnabled() {
        	Object target = TargetManager.getInstance().getModelTarget();
        	boolean result = true;
        	if (Model.getFacade().isAOperation(target)) {
        		Object owner = Model.getFacade().getOwner(target);
        		if (owner == null || Model.getFacade().isAInterface(owner)) {
        			result = false;
        		}
        	}
        	return super.shouldBeEnabled() && result;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object target = TargetManager.getInstance().getModelTarget();
            if (Model.getFacade().isAOperation(target)) {
                addMethod();
                super.actionPerformed(e);
            }
        }
    }


    /**
     * Appropriate namespace is the namespace of our class,
     * not the class itself.
     *
     * @see org.argouml.uml.ui.PropPanel#getDisplayNamespace()
     */
    protected Object getDisplayNamespace() {
        Object namespace = null;
        Object target = getTarget();
        if (Model.getFacade().isAAttribute(target)) {
            if (Model.getFacade().getOwner(target) != null) {
                namespace =
                    Model.getFacade().getNamespace(
                            Model.getFacade().getOwner(target));
            }
        }
        return namespace;
    }

} /* end class PropPanelOperation */
