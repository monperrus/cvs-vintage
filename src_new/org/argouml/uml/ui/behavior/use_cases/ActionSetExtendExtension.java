// $Id: ActionSetExtendExtension.java,v 1.15 2005/01/30 20:47:46 linus Exp $
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

package org.argouml.uml.ui.behavior.use_cases;

import java.awt.event.ActionEvent;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLAction;
import org.argouml.uml.ui.UMLComboBox2;

/**
 * @since Oct 6, 2002
 * @author jaap.branderhorst@xs4all.nl
 * @stereotype singleton
 */
public class ActionSetExtendExtension extends UMLAction {

    private static final ActionSetExtendExtension SINGLETON =
        new ActionSetExtendExtension();

    /**
     * Constructor for ActionSetExtendBase.
     */
    protected ActionSetExtendExtension() {
        super(Translator.localize("action.set"), false, NO_ICON);
    }

    /**
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object source = e.getSource();
        Object newExtension = null;
        Object oldExtension = null;
        Object extend = null;
        if (source instanceof UMLComboBox2) {
            UMLComboBox2 combo = (UMLComboBox2) source;
            newExtension = /*(MUseCase)*/ combo.getSelectedItem();
            Object o = combo.getTarget();
            if (Model.getFacade().isAExtend(o)) {
                extend = /*(MExtend)*/ o;
                o = combo.getSelectedItem();
                if (Model.getFacade().isAUseCase(o)) {
                    newExtension = /*(MUseCase)*/ o;
                    oldExtension = Model.getFacade().getExtension(extend);
                    if (newExtension != oldExtension) {
                        Model.getUseCasesHelper().setExtension(
                                extend,
                                newExtension);
                    }
                } else {
                    if (o != null && o.equals("")) {
                        Model.getUseCasesHelper().setExtension(extend, null);
                    }
                }
            }
        }
    }

    /**
     * @return Returns the SINGLETON.
     */
    public static ActionSetExtendExtension getInstance() {
        return SINGLETON;
    }
}
