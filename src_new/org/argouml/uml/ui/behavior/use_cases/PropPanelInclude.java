// $Id: PropPanelInclude.java,v 1.30 2004/12/02 19:30:02 mvw Exp $
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

import javax.swing.JComboBox;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLComboBox2;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

/**
 * Builds the property panel for an Include relationship.<p>
 *
 * This is a type of Relationship, but, since Relationship has no
 * semantic meaning of its own, we derive directly from
 * PropPanelModelElement (as other children of Relationship do).<p>
 *
 * @author Jeremy Bennett
 */
public class PropPanelInclude extends PropPanelModelElement {

    /**
     * Constructor. Builds up the various fields required.
     */
    public PropPanelInclude() {
        super("Include", ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.name"),
		 getNameTextField());
        addField(Translator.localize("label.stereotype"), 
                getStereotypeBox());
        addField(Translator.localize("label.namespace"),
		 getNamespaceScroll());

        addSeperator();

        JComboBox baseBox =
	    new UMLComboBox2(new UMLIncludeBaseComboBoxModel(),
			     ActionSetIncludeBase.getInstance());
        addField(Translator.localize("label.usecase-base"),
		 baseBox);

        JComboBox additionBox =
	    new UMLComboBox2(new UMLIncludeAdditionComboBoxModel(),
			     ActionSetIncludeAddition.getInstance());
        addField(Translator.localize("label.addition"),
		 additionBox);

   /*
        // TODO:
        // FIXME - Why is this code commented out - 
        // is there work to do here - Bob Tarling
    *
        // The addition use case (reuse earlier variables). Note that because
        // of the NSUML bug we look for the "base" event, rather than the
        // "addition" event" here.

        addSeperator();

        model = new UMLComboBoxModel(this, "isAcceptableUseCase",
                                     "base", "getAddition",
                                     "setAddition", true, MUseCase.class,
                                     true);
        box   = new UMLComboBox(model);
        nav   = new UMLComboBoxNavigator(this, "NavUseCase", box);

        addCaption("Addition:", 1, 1, 0);
        addField(nav, 1, 1, 0);
*/
        // Add the toolbar. Just the four basic buttons for now.

        addButton(new PropPanelButton2(this, new ActionNavigateNamespace()));
        new PropPanelButton(this, lookupIcon("Delete"), Translator.localize(
            "action.delete-from-model"), new ActionRemoveFromModel());
    }


    /**
     * Get the current base use case of the include relationship.<p>
     *
     * <em>Note</em>. There is a bug in NSUML, where the "include" and
     * "include2" associations of a use case are back to front, i.e
     * "include" is used as the opposite end of "addition" to point to
     * an including use case, rather than an included use case.  Fixed
     * within the include relationship, rather than the use case, by
     * reversing the use of access functions for the "base" and
     * "addition" associations in the code.<p>
     *
     * @return The {@link ru.novosoft.uml.behavior.use_cases.MUseCase}
     * that is the base of this include relationship or
     * <code>null</code> if there is none. Returned as type {@link
     * ru.novosoft.uml.behavior.use_cases.MUseCase} to fit in with the
     * type specified for the {@link org.argouml.uml.ui.UMLComboBoxModel}.
     */
    public Object getBase() {
        Object base   = null;
        Object      target = getTarget();

        // Note that because of the NSUML bug, we must use getAddition() rather
        // than getBase() to get the base use case.

        if (ModelFacade.isAInclude(target)) {
            base = ModelFacade.getAddition(target);
        }
        return base;
    }

    /**
     * Set the base use case of the include relationship.<p>
     *
     * <em>Note</em>. There is a bug in NSUML, where the "include" and
     * "include2" associations of a use case are back to front, i.e
     * "include" is used as the opposite end of "addition" to point to
     * an including use case, rather than an included use case.  Fixed
     * within the include relationship, rather than the use case, by
     * reversing the use of access functions for the "base" and
     * "addition" associations in the code.<p>
     *
     * @param base The {@link
     * ru.novosoft.uml.behavior.use_cases.MUseCase} to set as the base
     * of this include relationship. Supplied as type {@link
     * ru.novosoft.uml.behavior.use_cases.MUseCase} to fit in with the
     * type specified for the {@link org.argouml.uml.ui.UMLComboBoxModel}.
     */
    public void setBase(Object/*MUseCase*/ base) {
        Object target = getTarget();

        // Note that because of the NSUML bug, we must use setAddition() rather
        // than setBase() to set the base use case.

        if (ModelFacade.isAInclude(target)) {
            ModelFacade.setAddition(target, base);
        }
    }


    /**
     * Get the current addition use case of the include relationship.<p>
     *
     *
     * @return The {@link ru.novosoft.uml.behavior.use_cases.MUseCase}
     * that is the addition of this include relationship or
     * <code>null</code> if there is none. Returned as type {@link
     * ru.novosoft.uml.behavior.use_cases.MUseCase} to fit in with the
     * type specified for the {@link org.argouml.uml.ui.UMLComboBoxModel}.
     */
    public Object getAddition() {
        Object addition   = null;
        Object target = getTarget();

        if (ModelFacade.isAInclude(target)) {
            addition = ModelFacade.getAddition(target);
        }

        return addition;
    }

    /**
     * Set the addition use case of the include relationship.<p>
     *   
     *
     * @param addition The {@link
     * ru.novosoft.uml.behavior.use_cases.MUseCase} to set as the
     * addition of this include relationship. Supplied as type {@link
     * ru.novosoft.uml.behavior.use_cases.MUseCase} to fit in with the
     * type specified for the {@link org.argouml.uml.ui.UMLComboBoxModel}.
     */
    public void setAddition(Object/*MUseCase*/ addition) {
        Object target = getTarget();

        // Note that because of the NSUML bug, we must use setBase() rather
        // than setAddition() to set the addition use case.

        if (ModelFacade.isAInclude(target)) {
            ModelFacade.setAddition(target, addition);
        }
    }


    /**
     * Predicate to test if a model element may appear in the list of
     * potential use cases.<p>
     *
     * <em>Note</em>. We don't try to prevent the user setting up
     * circular include relationships. This may be necessary
     * temporarily, for example while reversing a relationship. It is
     * up to a critic to track this.<p>
     *
     * @param modElem the {@link
     * ru.novosoft.uml.foundation.core.MModelElement} to test.
     *
     * @return <code>true</code> if modElem is a use case,
     * <code>false</code> otherwise.
     */
    public boolean isAcceptableUseCase(Object/*MModelElement*/ modElem) {

        return ModelFacade.isAUseCase(modElem);
    }


} /* end class PropPanelInclude */
