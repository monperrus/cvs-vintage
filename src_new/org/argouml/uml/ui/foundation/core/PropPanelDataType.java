// $Id: PropPanelDataType.java,v 1.51 2004/11/22 19:34:15 mvw Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.CoreFactory;
import org.argouml.model.uml.UmlFactory;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.ActionNavigateContainerElement;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.util.ConfigLoader;

/**
 * TODO: this property panel needs refactoring to remove dependency on old gui
 * components.
 */
public class PropPanelDataType extends PropPanelClassifier {

    private JScrollPane attributeScroll;

    private JScrollPane operationScroll;

    private static UMLClassAttributeListModel attributeListModel = 
        new UMLClassAttributeListModel();

    private static UMLClassOperationListModel operationListModel = 
        new UMLClassOperationListModel();

    /**
     * The constructor.
     * 
     */
    public PropPanelDataType() {
        super("DataType", lookupIcon("DataType"), 
                ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeBox());
        addField(Translator.localize("label.namespace"),
                getNamespaceComboBox());
        add(getModifiersPanel());

        addSeperator();

        add(getNamespaceVisibilityPanel());
        addField(Translator.localize("label.client-dependencies"),
                getClientDependencyScroll());
        addField(Translator.localize("label.supplier-dependencies"),
                getSupplierDependencyScroll());
        addField(Translator.localize("label.generalizations"),
                getGeneralizationScroll());
        addField(Translator.localize("label.specializations"),
                getSpecializationScroll());

        addSeperator();

        addField(Translator.localize("label.operations"),
                getOperationScroll());

        addField(Translator.localize("label.literals"),
                getAttributeScroll());

        addButton(new PropPanelButton2(this,
                new ActionNavigateContainerElement()));
        new PropPanelButton(this, lookupIcon("DataType"), 
                Translator.localize("button.new-datatype"), 
                "newDataType", null);
        new PropPanelButton(this, lookupIcon("NewAttribute"), 
            Translator.localize("button.new-enumeration-literal"),
            "addAttribute", null);

        new PropPanelButton(this, lookupIcon("NewOperation"), 
                Translator.localize(
                "button.new-operation"), "addOperation", null);
        addButton(new PropPanelButton2(this, new ActionRemoveFromModel()));
    }

    /**
     * @see org.argouml.uml.ui.foundation.core.PropPanelClassifier#addAttribute()
     */
    public void addAttribute() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAClassifier(target)) {
            Object classifier = /* (MClassifier) */target;
            Object stereo = null;
            if (ModelFacade.getStereotypes(classifier).size() > 0) {
                stereo = ModelFacade.getStereotypes(classifier).iterator()
                        .next();
            }
            if (stereo == null) {
                //
                //  if there is not an enumeration stereotype as
                //     an immediate child of the model, add one
                Object model = ModelFacade.getModel(classifier);
                Object ownedElement;
                boolean match = false;
                if (model != null) {
                    Collection ownedElements = ModelFacade
                            .getOwnedElements(model);
                    if (ownedElements != null) {
                        Iterator iter = ownedElements.iterator();
                        while (iter.hasNext()) {
                            ownedElement = iter.next();
                            if (org.argouml.model.ModelFacade
                                    .isAStereotype(ownedElement)) {
                                stereo = /* (MStereotype) */ownedElement;
                                String stereoName = ModelFacade.getName(stereo);
                                if (stereoName != null
                                        && stereoName.equals("enumeration")) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (!match) {
                            stereo = UmlFactory.getFactory()
                                    .getExtensionMechanisms()
                                    .createStereotype();
                            ModelFacade.setName(stereo, "enumeration");
                            ModelFacade.addOwnedElement(model, stereo);
                        }
                        ModelFacade.setStereotype(classifier, stereo);
                    }
                }
            }

            Object attr = CoreFactory.getFactory().buildAttribute(classifier);
            ModelFacade.setChangeable(attr, false);
            TargetManager.getInstance().setTarget(attr);
        }

    }

    /**
     * @see org.argouml.uml.ui.foundation.core.PropPanelClassifier#addOperation()
     */
    public void addOperation() {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAClassifier(target)) {
            Object newOper = UmlFactory.getFactory().getCore().buildOperation(
            /* (MClassifier) */target);
            // due to Well Defined rule [2.5.3.12/1]
            ModelFacade.setQuery(newOper, true);
            TargetManager.getInstance().setTarget(newOper);
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

    /**
     * Create a new datatype.
     */
    public void newDataType() {
        Object target = getTarget();
        if (ModelFacade.isADataType(target)) {
            Object dt = /* (MDataType) */target;
            Object ns = ModelFacade.getNamespace(dt);
            Object newDt = CoreFactory.getFactory().createDataType();
            ModelFacade.addOwnedElement(ns, newDt);
            TargetManager.getInstance().setTarget(newDt);
        }
    }

} /* end class PropPanelDataType */