// $Id: PropPanelStimulus.java,v 1.49 2005/01/02 16:43:49 linus Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.common_behavior;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLStimulusActionTextField;
import org.argouml.uml.ui.UMLStimulusActionTextProperty;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;

import ru.novosoft.uml.MElementEvent;

/**
 * The properties panel for a Stimulus.
 * 
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 *
 * @author agauthie
 */
public class PropPanelStimulus extends PropPanelModelElement {

    /**
     * The constructor.
     * 
     */
    public PropPanelStimulus() {
        super("Stimulus Properties", lookupIcon("Stimulus"), 
                ConfigLoader.getTabPropsOrientation());

        Object[] namesToWatch = {
	    ModelFacade.ACTION
	};
        setNameEventListening(namesToWatch);

        addField(Translator.localize("label.name"), 
                getNameTextField());
        addField("Action:", new UMLStimulusActionTextField(this, 
                new UMLStimulusActionTextProperty("name")));
        addField(Translator.localize("label.stereotype"), 
                getStereotypeBox());

        JList senderList = new UMLLinkedList(new UMLStimulusSenderListModel());
	senderList.setVisibleRowCount(1);
	JScrollPane senderScroll = new JScrollPane(senderList);
	addField(Translator.localize("label.sender"), senderScroll);

        JList receiverList =
	    new UMLLinkedList(new UMLStimulusReceiverListModel());
	receiverList.setVisibleRowCount(1);
	JScrollPane receiverScroll = new JScrollPane(receiverList);
	addField(Translator.localize("label.receiver"), 
            receiverScroll);
        
        addField(Translator.localize("label.namespace"), 
                getNamespaceComboBox());

        addButton(new PropPanelButton2(new ActionNavigateNamespace()));  
        addButton(new PropPanelButton2(new ActionNewStereotype(), 
                lookupIcon("Stereotype")));
        addButton(new PropPanelButton2(new ActionRemoveFromModel()));
    }


    /**
     * @see ru.novosoft.uml.MElementListener#removed(ru.novosoft.uml.MElementEvent)
     */
    public void removed(MElementEvent mee) {
    }

    /**
     * @return the sender of this stimulus
     */
    public Object getSender() {
        Object sender = null;
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            sender =  ModelFacade.getSender(target);
        }
        return sender;
    }

    /**
     * @param element the sender of this stimulus
     */
    public void setSender(Object/*MInstance*/ element) {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAStimulus(target)) {
            ModelFacade.setSender(target, element);
        }
    }


    /**
     * @return the receiver of this stimulus
     */
    public Object getReceiver() {
        Object receiver = null;
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            receiver =  ModelFacade.getReceiver(target);
        }
        return receiver;
    }

    /**
     * @param element the receiver of this stimulus
     */
    public void setReceiver(Object/*MInstance*/ element) {
        Object target = getTarget();
        if (org.argouml.model.ModelFacade.isAStimulus(target)) {
            ModelFacade.setReceiver(target, element);
        }
    }

    /**
     * @param modelelement the given modelelement
     * @return true if it is acceptable, i.e. it is an association
     */
    public boolean isAcceptibleAssociation(
            Object/*MModelElement*/ modelelement) {
        return org.argouml.model.ModelFacade.isAAssociation(modelelement);
    }

    /**
     * @return the association of the link of the stimulus
     */
    public Object getAssociation() {
        Object association = null;
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            Object link = ModelFacade.getCommunicationLink(target);
            if (link != null) {
                association = ModelFacade.getAssociation(link);
            }
        }
        return association;
    }

    /**
     * @param element the association of the link of the stimulus
     */
    public void setAssociation(Object/*MAssociation*/ element) {
        Object target = getTarget();
        if (ModelFacade.isAStimulus(target)) {
            Object stimulus = /*(MStimulus)*/ target;
            Object link = ModelFacade.getCommunicationLink(stimulus);
            if (link == null) {
                link = Model.getUmlFactory().getCommonBehavior().createLink();
                //((MStimulus)stimulus).getFactory().createLink();
                if (link != null) {
                    ModelFacade.addStimulus(link, stimulus);
                    ModelFacade.setCommunicationLink(stimulus, /*(MLink)*/link);
                }
            }
            Object oldAssoc = ModelFacade.getAssociation(link);
            if (oldAssoc != element) {
                ModelFacade.setAssociation(link, element);
                //
                //  TODO: more needs to go here
                //
            }
        }
    }
}
