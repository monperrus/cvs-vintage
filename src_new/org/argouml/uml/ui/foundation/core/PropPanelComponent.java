// $Id: PropPanelComponent.java,v 1.41 2004/12/02 19:30:02 mvw Exp $
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

import org.argouml.i18n.Translator;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.util.ConfigLoader;

/**
 * PropPanel for a UML component.<p>
 *
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 *
 * @author 5eichler@informatik.uni-hamburg.de
 */
public class PropPanelComponent extends PropPanelClassifier {

    /**
     * The constructor.
     * 
     */
    public PropPanelComponent() {
	super("Component", ConfigLoader.getTabPropsOrientation());
	addField(Translator.localize("label.name"), 
            getNameTextField());
	addField(Translator.localize("label.stereotype"), 
            getStereotypeBox());
	addField(Translator.localize("label.namespace"), 
            getNamespaceComboBox());
	add(getModifiersPanel());

	addSeperator();

	addField(Translator.localize("label.generalizations"), 
            getGeneralizationScroll());
	addField(Translator.localize("label.specializations"), 
            getSpecializationScroll());

	addSeperator();

	addField(Translator.localize("label.client-dependencies"), 
            getClientDependencyScroll());
	addField(Translator.localize("label.supplier-dependencies"), 
            getSupplierDependencyScroll());

        addButton(new PropPanelButton2(this, 
                new ActionNavigateNamespace()));
	new PropPanelButton(this, lookupIcon("Reception"), 
            Translator.localize("button.new-reception"), 
            getActionNewReception());
	new PropPanelButton(this, lookupIcon("Delete"), Translator.localize(
	    "action.delete-from-model"), new ActionRemoveFromModel());

	//    addCaption(Translator.localize("label.name"),1,0,0);
	//    addField(getNameTextField(),1,0,0);
	//
	//    addCaption(Translator.localize("label.stereotype"),
        //        2,0,0);
	//    addField(getStereotypeBox(),2,0,0);
	//
	//    addCaption(Translator.localize("label.namespace"),
        //        3,0,0);
	//    addField(getNamespaceComboBox(),3,0,0);
	//
	//    addCaption(Translator.localize("label.modifiers"),
        //        4,0,1);
	//    JPanel modifiersPanel = new JPanel(new GridLayout(0,3));
	//    modifiersPanel.add(new UMLCheckBox(Translator.localize(
        //        "checkbox.abstract-lc"),this,new UMLReflectionBooleanProperty(
        //        "isAbstract",ModelFacade.COMPONENT,"isAbstract",
	//        "setAbstract")));
	//    modifiersPanel.add(new UMLCheckBox(Translator.localize(
        //        "checkbox.final-lc"),this,new UMLReflectionBooleanProperty(
        //        "isLeaf",ModelFacade.COMPONENT,"isLeaf","setLeaf")));
	//    modifiersPanel.add(new UMLCheckBox(localize("root"),this,
        //        new UMLReflectionBooleanProperty("isRoot",
	//                                         ModelFacade.COMPONENT,
	//                                         "isRoot",
        //                                         "setRoot")));
	//    addField(modifiersPanel,4,0,0);
	//
	//    addCaption("Generalizations:",0,1,1);
	//    addField(getGeneralizationScroll(),0,1,1);
	//
	//    addCaption("Specializations:",1,1,1);
	//    addField(getSpecializationScroll(),1,1,1);
	//
	//    new PropPanelButton(this,buttonPanel,_navUpIcon, 
        //        Translator.localize("button.go-up"),
        //        "navigateUp",null);
	//    new PropPanelButton(this,buttonPanel,_deleteIcon,localize(
        //        "Delete component"),"removeElement",null);
    }


} /* end class PropPanelComponent */


