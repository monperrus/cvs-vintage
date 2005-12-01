// $Id: PropPanelEnumeration.java,v 1.1 2005/12/01 00:06:31 bobtarling Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNewModelElement;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.util.CollectionUtil;
import org.argouml.util.ConfigLoader;

/**
 * The properties panel for a Datatype.
 */
public class PropPanelEnumeration extends PropPanelDataType {

    private JScrollPane literalsScroll;

    private static UMLEnumerationLiteralsListModel literalsListModel =
        new UMLEnumerationLiteralsListModel();

    /**
     * The constructor.
     */
    public PropPanelEnumeration() {
        super("Enumeration", lookupIcon("Enumeration"),
                ConfigLoader.getTabPropsOrientation());

        addField(Translator.localize("label.literals"),
                getLiteralsScroll());

        addAction(new ActionAddLiteral());
    }


    /**
     * Returns the attributeScroll.
     *
     * @return JScrollPane
     */
    public JScrollPane getLiteralsScroll() {
        if (literalsScroll == null) {
            JList list = new UMLLinkedList(literalsListModel);
            literalsScroll = new JScrollPane(list);
        }
        return literalsScroll;
    }

    private class ActionAddLiteral extends AbstractActionNewModelElement {

        /**
         * The constructor.
         */
        public ActionAddLiteral() {
            super("button.new-enumeration-literal");
            putValue(Action.NAME, Translator.localize(
                "button.new-enumeration-literal"));
        }
    
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object target = TargetManager.getInstance().getModelTarget();
            if (Model.getFacade().isAClassifier(target)) {
                Object stereotype = CollectionUtil.getFirstItemOrNull(
                        Model.getFacade().getStereotypes(target));
    
                Collection propertyChangeListeners =
                    ProjectManager.getManager()
                        .getCurrentProject().findFigsForMember(target);
                Object intType =
                    ProjectManager.getManager()
                        .getCurrentProject().findType("int");
                Object model =
                    ProjectManager.getManager()
                        .getCurrentProject().getModel();
                Object attr =
                    Model.getCoreFactory().buildAttribute(target,
                            model, intType, propertyChangeListeners);
                Model.getCoreHelper().setChangeable(attr, false);
                TargetManager.getInstance().setTarget(attr);
                super.actionPerformed(e);
            }
        }
    }
}
