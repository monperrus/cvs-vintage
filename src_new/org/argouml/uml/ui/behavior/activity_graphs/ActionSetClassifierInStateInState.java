// $Id: ActionSetClassifierInStateInState.java,v 1.3 2004/12/30 12:34:08 mvw Exp $
// Copyright (c) 2004 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.activity_graphs;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.argouml.i18n.Translator;
import org.argouml.model.ModelFacade;
import org.argouml.uml.ui.UMLAction;
import org.argouml.uml.ui.UMLComboBox2;

/**
 * The Action to set the "inState" of a ClassifierInState.
 * 
 * TODO: This is not correct yet! Should set all the states from a list!
 * 
 * @author mvw
 *
 */
class ActionSetClassifierInStateInState extends UMLAction {

    /**
     * The constructor.
     */
    public ActionSetClassifierInStateInState() {
        super(Translator.localize("Set"), false, NO_ICON);
    }
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource(); // the source UI element of the event
        if (source instanceof UMLComboBox2) {
            UMLComboBox2 box = (UMLComboBox2) source;
            Object cis = box.getTarget();
            if (ModelFacade.isAClassifierInState(cis)) {
                Collection c = ModelFacade.getInStates(cis);
                
                Object state = box.getSelectedItem();
                if (ModelFacade.isAState(state)) {
                    if (!c.contains(state)) {
                        ModelFacade.addInState(cis, state);
                    
                        super.actionPerformed(e);
                    }
                }
            }
        }
    }
}
