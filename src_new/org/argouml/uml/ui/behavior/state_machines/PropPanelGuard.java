// $Id: PropPanelGuard.java,v 1.32 2004/12/03 18:55:08 mvw Exp $
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

package org.argouml.uml.ui.behavior.state_machines;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.i18n.Translator;
import org.argouml.uml.ui.ActionNavigateTransition;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLExpressionBodyField;
import org.argouml.uml.ui.UMLExpressionExpressionModel;
import org.argouml.uml.ui.UMLExpressionLanguageField;
import org.argouml.uml.ui.UMLExpressionModel2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

/**
 * A property panel for Guards. Rewrote this class to comply to 
 * Bob Tarling's layout mechanism and to include all valid 
 * properties as defined in the UML 1.3 spec.
 * @since Dec 14, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class PropPanelGuard extends PropPanelModelElement {

    /**
     * The constructor.
     * 
     */
    public PropPanelGuard() {
        super("Guard", ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"), 
                getNameTextField());
        addField(Translator.localize("label.stereotype"), 
                getStereotypeBox());
        addField(Translator.localize("label.namespace"), 
                getNamespaceScroll());

        JList transitionList = new UMLLinkedList(
                new UMLGuardTransitionListModel());
        // MVW: TODO: Why is this is list? There's only one!
        addField(Translator.localize("label.transition"), 
                new JScrollPane(transitionList));

        addSeperator();

//        UMLExpressionModel expressionModel = new UMLExpressionModel(this, 
//                (Class) ModelFacade.GUARD, "expression",
//		(Class) ModelFacade.BOOLEAN_EXPRESSION, "getExpression", 
//                "setExpression");
        UMLExpressionModel2 expressionModel = 
            new UMLExpressionExpressionModel(this, "expression");
        addField(Translator.localize("label.expression"), 
            new JScrollPane(new UMLExpressionBodyField(expressionModel, true),
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        addField(Translator.localize("label.language"), 
                new UMLExpressionLanguageField(expressionModel, true));

	new PropPanelButton(this,
            ResourceLoaderWrapper.lookupIconResource("NavigateUp"), 
            Translator.localize("button.go-up"), 
            new ActionNavigateTransition());
    
        new PropPanelButton(this, lookupIcon("Delete"), Translator.localize(
            "action.delete-from-model"), new ActionRemoveFromModel());
    }

} /* end class PropPanelState */

