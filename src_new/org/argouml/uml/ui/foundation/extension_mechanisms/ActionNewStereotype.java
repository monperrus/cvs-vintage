// $Id: ActionNewStereotype.java,v 1.3 2004/12/27 15:51:45 bobtarling Exp $
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

package org.argouml.uml.ui.foundation.extension_mechanisms;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.Action;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.ExtensionMechanismsFactory;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNewModelElement;
import org.tigris.gef.presentation.Fig;

import ru.novosoft.uml.model_management.MModel;


/**
 * This action creates a new Stereotype in the current Model.
 * 
 * @author mvw@tigris.org
 */
public class ActionNewStereotype extends AbstractActionNewModelElement {

    /**
     * The constructor.
     */
    public ActionNewStereotype() {
        super("button.new-stereotype");
        putValue(Action.NAME, Translator.localize("button.new-stereotype"));
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object t = TargetManager.getInstance().getTarget();
        if (t instanceof Fig) t = ((Fig) t).getOwner();
        Object model = ProjectManager.getManager().getCurrentProject().getModel();
        Collection models = ProjectManager.getManager().getCurrentProject().getModels();
        Object newStereo = ExtensionMechanismsFactory.getFactory()
            .buildStereotype(
                    ModelFacade.isAModelElement(t) ? t : null,
                    (String) null,
                    model,
                    models
                    );
        if (ModelFacade.isAModelElement(t)) { 
            Object ns = ModelFacade.getNamespace(t);
            if (ModelFacade.isANamespace(ns)) 
                ModelFacade.setNamespace(newStereo, ns);
        }
        TargetManager.getInstance().setTarget(newStereo);
        super.actionPerformed(e);
    }
}