// Copyright (c) 1996-2002 The Regents of the University of California. All
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

// File: PropPanelTransition.java
// Classes: PropPanelTransition
// Original Author: jrobbins@ics.uci.edu
// $Id: PropPanelTransition.java,v 1.13 2002/12/15 17:51:35 kataka Exp $

package org.argouml.uml.ui.behavior.state_machines;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.apache.log4j.Category;
import org.argouml.application.api.Argo;
import org.argouml.swingext.LabelledLayout;
import org.argouml.uml.ui.PropPanelButton;
import org.argouml.uml.ui.UMLComboBoxNavigator;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.UMLMutableLinkedList;
import org.argouml.uml.ui.foundation.core.PropPanelModelElement;
import org.argouml.util.ConfigLoader;

public class PropPanelTransition extends PropPanelModelElement {
    protected static Category cat = Category.getInstance(PropPanelTransition.class);

    ////////////////////////////////////////////////////////////////
    // contructors
    public PropPanelTransition() {
        super("Transition", _transitionIcon, ConfigLoader.getTabPropsOrientation());

        addField(Argo.localize("UMLMenu", "label.name"), nameField);
        addField(Argo.localize("UMLMenu", "label.stereotype"), new UMLComboBoxNavigator(this, Argo.localize("UMLMenu", "tooltip.nav-stereo"), stereotypeBox));
        JList statemachineList = new UMLLinkedList(this, new UMLTransitionStatemachineListModel(this));
        statemachineList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.statemachine"), new JScrollPane(statemachineList));
        JList stateList = new UMLLinkedList(this, new UMLTransitionStateListModel(this));
        stateList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.state"), new JScrollPane(stateList));

        add(LabelledLayout.getSeperator());

        JList sourceList = new UMLLinkedList(this, new UMLTransitionSourceListModel(this));
        sourceList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.source"), new JScrollPane(sourceList));
        JList targetList = new UMLLinkedList(this, new UMLTransitionTargetListModel(this));
        targetList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.target"), new JScrollPane(targetList));
        JList triggerList = new UMLTransitionTriggerList(this, new UMLTransitionTriggerListModel(this));
        triggerList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.trigger"), new JScrollPane(triggerList));
        JList guardList = new UMLMutableLinkedList(this, new UMLTransitionGuardListModel(this), null, ActionNewGuard.SINGLETON);
        guardList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.guard"), new JScrollPane(guardList));
        JList effectList = new UMLTransitionEffectList(this, new UMLTransitionEffectListModel(this));
        effectList.setVisibleRowCount(1);
        addField(Argo.localize("UMLMenu", "label.effect"), new JScrollPane(effectList));

        new PropPanelButton(this, buttonPanel, _navUpIcon, Argo.localize("UMLMenu", "button.go-up"), "navigateUp", null);
        new PropPanelButton(this, buttonPanel, _navBackIcon, Argo.localize("UMLMenu", "button.go-back"), "navigateBackAction", "isNavigateBackEnabled");
        new PropPanelButton(this, buttonPanel, _navForwardIcon, Argo.localize("UMLMenu", "button.go-forward"), "navigateForwardAction", "isNavigateForwardEnabled");
        new PropPanelButton(this, buttonPanel, _deleteIcon, localize("Delete"), "removeElement", null);
    }

} /* end class PropPanelTransition */
