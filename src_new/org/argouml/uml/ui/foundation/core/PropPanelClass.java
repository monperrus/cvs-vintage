// $Id: PropPanelClass.java,v 1.64 2004/11/22 19:34:15 mvw Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.CoreFactory;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.ui.ActionAddAttribute;
import org.argouml.uml.diagram.ui.ActionAddOperation;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.util.ConfigLoader;

/**
 * The properties panel for a Class.
 * 
 * TODO: this property panel needs refactoring to remove dependency on old gui
 * components.
 */
public class PropPanelClass extends PropPanelClassifier {

    private JScrollPane attributeScroll;

    private JScrollPane operationScroll;

    private static UMLClassAttributeListModel attributeListModel = 
        new UMLClassAttributeListModel();

    private static UMLClassOperationListModel operationListModel = 
        new UMLClassOperationListModel();

    ////////////////////////////////////////////////////////////////
    // contructors
    /**
     * The constructor.
     */
    public PropPanelClass() {
        super("Class", ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeBox());
        addField(Translator.localize("label.namespace"),
                getNamespaceComboBox());
        getModifiersPanel().add(new UMLClassActiveCheckBox());
        add(getModifiersPanel());
        add(getNamespaceVisibilityPanel());

        addSeperator();

        addField(Translator.localize("label.client-dependencies"),
                getClientDependencyScroll());
        addField(Translator.localize("label.supplier-dependencies"),
                getSupplierDependencyScroll());
        addField(Translator.localize("label.generalizations"),
                getGeneralizationScroll());
        addField(Translator.localize("label.specializations"),
                getSpecializationScroll());

        addSeperator();

        addField(Translator.localize("label.attributes"),
                getAttributeScroll());
        addField(Translator.localize("label.association-ends"),
                getAssociationEndScroll());
        addField(Translator.localize("label.operations"),
                getOperationScroll());
        addField(Translator.localize("label.owned-elements"),
                getOwnedElementsScroll());

        addButton(new PropPanelButton2(this,
                new ActionNavigateNamespace()));
        addButton(new PropPanelButton2(this, 
                        new ActionAddAttribute()));
        addButton(new PropPanelButton2(this, 
                        new ActionAddOperation()));
        addButton(new PropPanelButton2(this, getActionNewReception()));
        new PropPanelButton(this, lookupIcon("InnerClass"), 
                Translator.localize("button.new-inner-class"),
                "addInnerClass", null);
        new PropPanelButton(this, lookupIcon("Class"), 
                Translator.localize(
                "button.new-class"), "newClass", null);
        addButton(new PropPanelButton2(this, new ActionRemoveFromModel()));
    }

    /**
     * Add an inner class. 
     */
    public void addInnerClass() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAClassifier(target)) {
            Object classifier = /* (MClassifier) */target;
            Object inner = CoreFactory.getFactory().buildClass(classifier);
            TargetManager.getInstance().setTarget(inner);
        }
    }

    /**
     * Add a new class.
     */
    public void newClass() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAClassifier(target)) {
            Object classifier = /* (MClassifier) */target;
            Object ns = ModelFacade.getNamespace(classifier);
            if (ns != null) {
                Object peer = CoreFactory.getFactory().buildClass(ns);
                TargetManager.getInstance().setTarget(peer);
            }
        }

    }

    /**
     * Returns the operationScroll.
     * 
     * @return JScrollPane
     */
    public JScrollPane getOperationScroll() {
        if (operationScroll == null) {
            JList list = new UMLLinkedList(operationListModel);
            operationScroll = new JScrollPane(list);
        }
        return operationScroll;
    }

    /**
     * Returns the attributeScroll.
     * 
     * @return JScrollPane
     */
    public JScrollPane getAttributeScroll() {
        if (attributeScroll == null) {
            JList list = new UMLLinkedList(attributeListModel);
            attributeScroll = new JScrollPane(list);
        }
        return attributeScroll;
    }

} /* end class PropPanelClass */
