// $Id: ActionSetAssociationEndOrdering.java,v 1.13 2005/01/30 14:05:18 linus Exp $
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

package org.argouml.uml.ui.foundation.core;

import java.awt.event.ActionEvent;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.UMLAction;
import org.argouml.uml.ui.UMLCheckBox2;


/**
 *
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 4, 2003
 */
public class ActionSetAssociationEndOrdering extends UMLAction {

    private static final ActionSetAssociationEndOrdering SINGLETON =
	new ActionSetAssociationEndOrdering();

    /**
     * Constructor for ActionSetElementOwnershipSpecification.
     */
    protected ActionSetAssociationEndOrdering() {
        super(Translator.localize("Set"), true, NO_ICON);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() instanceof UMLCheckBox2) {
            UMLCheckBox2 source = (UMLCheckBox2) e.getSource();
            Object target = source.getTarget();
            if (ModelFacade.isAAssociationEnd(target)) {
                Object m = /*(MAssociationEnd)*/ target;
                if (source.isSelected()) {
                    Model.getCoreHelper().setOrdering(m,
                            Model.getOrderingKind().getOrdered());
                } else {
                    Model.getCoreHelper().setOrdering(m,
                            Model.getOrderingKind().getUnordered());
                }
            }
        }
    }

    /**
     * @return Returns the sINGLETON.
     */
    public static ActionSetAssociationEndOrdering getInstance() {
        return SINGLETON;
    }

}
