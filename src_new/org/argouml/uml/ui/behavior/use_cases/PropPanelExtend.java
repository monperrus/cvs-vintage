// $Id: PropPanelExtend.java,v 1.41 2004/11/01 19:55:07 mvw Exp $
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

package org.argouml.uml.ui.behavior.use_cases;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UseCasesFactory;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.UMLConditionExpressionModel;
import org.argouml.uml.ui.UMLExpressionBodyField;
import org.argouml.uml.ui.UMLExpressionModel2;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

/**
 * Builds the property panel for an Extend relationship.<p>
 *
 * This is a type of Relationship, but, since Relationship has no semantic
 *   meaning of its own, we derive directly from PropPanelModelElement (as
 *   other children of Relationship do).<p>
 *
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 *
 * @author mail@jeremybennett.com
 */
public class PropPanelExtend extends PropPanelModelElement {


    /**
     * Constructor. Builds up the various fields required.
     * TODO: improve the conditionfield so it can be checked and the
     * OCL editor can be used.
     */

    public PropPanelExtend() {
        super("Extend", ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
		 getNameTextField());
        addField(Translator.localize("label.stereotype"), 
                getStereotypeBox());
        addField(Translator.localize("label.namespace"),
		 getNamespaceScroll());

        addSeperator();


        // Link to the two ends. This is done as a drop down. First for the
        // base use case.

        addField(Translator.localize("label.usecase-base"),
		 new UMLComboBox2(new UMLExtendBaseComboBoxModel(),
				  ActionSetExtendBase.getInstance()));

        addField(Translator.localize("label.extension"),
		 new UMLComboBox2(new UMLExtendExtensionComboBoxModel(),
				  ActionSetExtendExtension.getInstance()));

        JList extensionPointList =
	    new UMLMutableLinkedList(new UMLExtendExtensionPointListModel(),
		ActionAddExtendExtensionPoint.getInstance(),
		ActionNewExtendExtensionPoint.SINGLETON);
        addField(Translator.localize("label.extension-points"),
		new JScrollPane(extensionPointList));

        addSeperator();

//        UMLExpressionModel conditionModel =
//            new UMLExpressionModel(this, 
//                                   (Class) ModelFacade.EXTEND,
//                                   "condition",
//				   (Class) ModelFacade.BOOLEAN_EXPRESSION,
//                                   "getCondition", 
//                                   "setCondition");
        UMLExpressionModel2 conditionModel =
            new UMLConditionExpressionModel(this, "condition");

        JTextArea conditionArea = new UMLExpressionBodyField(conditionModel,
							     true);
        conditionArea.setRows(5);
        JScrollPane conditionScroll =
            new JScrollPane(conditionArea);

        addField("Condition:", conditionScroll);

        // Add the toolbar.

        addButton(new PropPanelButton2(this, 
                new ActionNavigateNamespace()));
        new PropPanelButton(this, getButtonPanel(), 
                lookupIcon("ExtensionPoint"),
                localize("New Extension Point"),
                "newExtensionPoint",
                null);
        addButton(new PropPanelButton2(this, new ActionRemoveFromModel()));
    }


    /**
     * Get the condition associated with the extend relationship.<p>
     *
     * The condition is actually of type {@link
     * ru.novosoft.uml.foundation.data_types.MBooleanExpression},
     * which defines both a language and a body. We are only
     * interested in the body, which is just a string.<p>
     *
     * @return The body of the {@link
     * ru.novosoft.uml.foundation.data_types.MBooleanExpression} which
     * is the condition associated with this extend relationship, or
     * <code>null</code> if there is none.
     */
    public String getCondition() {
        String condBody = null;
        Object target   = getTarget();

        if (ModelFacade.isAExtend(target)) {
            Object condition = ModelFacade.getCondition(target);

            if (condition != null) {
                condBody = (String) ModelFacade.getBody(condition);
            }
        }

        return condBody;
    }


    /**
     * Set the condition associated with the extend relationship.<p>
     *
     * The condition is actually of type {@link
     * ru.novosoft.uml.foundation.data_types.MBooleanExpression},
     * which defines both a language and a body. We are only
     * interested in setting the body, which is just a string.<p>
     *
     * @param condBody  The body of the condition to associate with this
     *                  extend relationship.
     */
    public void setCondition(String condBody) {

        // Give up if we are not an extend relationship

        Object target = getTarget();

        if (!(org.argouml.model.ModelFacade.isAExtend(target))) {
            return;
        }

        // Set the condition body.

        ModelFacade.setCondition(target,
				 UmlFactory.getFactory().getDataTypes()
				     .createBooleanExpression(null, condBody));
    }


    /**
     * Invoked by the "New Extension Point" toolbar button to create a new
     *   extension point for this extend relationship in the same namespace as
     *   the current extend relationship.<p>
     *
     * This code uses getFactory and adds the extension point to
     *   the current extend relationship.<p>
     */
    public void newExtensionPoint() {
        Object target = getTarget();

        if (org.argouml.model.ModelFacade.isAExtend(target)) {
            Object    extend    = /*(MExtend)*/ target;
            Object ns = ModelFacade.getNamespace(extend);

            if (ns != null) {
                if (ModelFacade.getBase(extend) != null) {

		    Object extensionPoint =
			UseCasesFactory.getFactory()
			    .buildExtensionPoint(ModelFacade.getBase(extend));

		    ModelFacade.addExtensionPoint(extend, extensionPoint);
                }

            }
        }
    }

} /* end class PropPanelExtend */
