// $Id: PropPanelInstance.java,v 1.36 2004/12/18 14:36:53 mvw Exp $
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

// File: PropPanelInstance.java
// Classes: PropPanelInstance
// Original Author: jrobbins@ics.uci.edu
// $Id: PropPanelInstance.java,v 1.36 2004/12/18 14:36:53 mvw Exp $

package org.argouml.uml.ui.behavior.common_behavior;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.tigris.swidgets.Orientation;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

/**
 * The properties panel for an Instance.
 *
 */
public class PropPanelInstance extends PropPanelModelElement {

    private JScrollPane stimuliSenderScroll;

    private JScrollPane stimuliReceiverScroll;

    private static UMLInstanceSenderStimulusListModel 
        stimuliSenderListModel = new UMLInstanceSenderStimulusListModel();

    private static UMLInstanceReceiverStimulusListModel 
        stimuliReceiverListModel = new UMLInstanceReceiverStimulusListModel();

    /**
     * The constructor.
     * 
     */
    public PropPanelInstance() {
        super("Instance Properties", lookupIcon("Instance"), 
                ConfigLoader.getTabPropsOrientation());
        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeBox());
        addField(Translator.localize("label.namespace"),
                getNamespaceComboBox());

        addButton(new PropPanelButton2(new ActionNavigateNamespace()));
    }

    /**
     * The constructor.
     * 
     * @param name the name for the properties panel
     * @param icon the icon shown next to the name
     * @param orientation the orientation
     */
    public PropPanelInstance(String name, ImageIcon icon,
            Orientation orientation) {
        super(name, icon, orientation);
    }

    /**
     * @param me the given object
     * @return true if the given modelelement is acceptable for this panel, 
     *         i.e. is a Classifier
     */
    public boolean isAcceptibleClassifier(Object/* MModelElement */me) {
        return org.argouml.model.ModelFacade.isAClassifier(me);
    }

    /**
     * @return the classifier that owns this panel
     */
    public Object getClassifier() {
        Object classifier = null;
        Object target = getTarget();
        if (ModelFacade.isAInstance(target)) {
            //    UML 1.3 apparently has this a 0..n multiplicity
            //    I'll have to figure out what that means
            //            classifier = ((MInstance) target).getClassifier();

            // at the moment , we only deal with one classifier
            Collection col = ModelFacade.getClassifiers(target);
            if (col != null) {
                Iterator iter = col.iterator();
                if (iter != null && iter.hasNext()) {
                    classifier = iter.next();
                }
            }
        }
        return classifier;
    }

    /**
     * @param element the owning UML element
     */
    public void setClassifier(Object/* MClassifier */element) {
        Object target = getTarget();

        if (org.argouml.model.ModelFacade.isAInstance(target)) {
            Object inst = /* (MInstance) */target;
            // ((MInstance) target).setClassifier((MClassifier) element);

            // delete all classifiers
            Collection col = ModelFacade.getClassifiers(inst);
            if (col != null) {
                Iterator iter = col.iterator();
                if (iter != null && iter.hasNext()) {
                    Object classifier = /* (MClassifier) */iter.next();
                    ModelFacade.removeClassifier(inst, classifier);
                }
            }
            // add classifier
            ModelFacade.addClassifier(inst, element);
        }
    }

    /**
     * @return the scrollpane for stimuli sender
     */
    protected JScrollPane getStimuliSenderScroll() {
        if (stimuliSenderScroll == null) {
            JList stimuliSenderList = new UMLLinkedList(stimuliSenderListModel);
            stimuliSenderList.setVisibleRowCount(1);
            stimuliSenderScroll = new JScrollPane(stimuliSenderList);
        }
        return stimuliSenderScroll;
    }

    /**
     * @return the scrollpane for stimuli receiver
     */
    protected JScrollPane getStimuliReceiverScroll() {
        if (stimuliReceiverScroll == null) {
            JList stimuliReceiverList = new UMLLinkedList(
                    stimuliReceiverListModel);
            stimuliReceiverList.setVisibleRowCount(1);
            stimuliReceiverScroll = new JScrollPane(stimuliReceiverList);
        }
        return stimuliReceiverScroll;
    }
} /* end class PropPanelInstance */