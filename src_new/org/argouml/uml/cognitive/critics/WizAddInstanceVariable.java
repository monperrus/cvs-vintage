// $Id: WizAddInstanceVariable.java,v 1.10 2004/12/28 19:33:19 mvw Exp $
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

package org.argouml.uml.cognitive.critics;

import java.util.Collection;

import javax.swing.JPanel;

import org.argouml.cognitive.ui.WizStepTextField;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;

/**
 * A wizard to add attributes to a classifier
 *
 * @author  d00mst (copied from WizAddOperation by mkl)
 * @since February 6, 2004, 11:40 PM
 */
public class WizAddInstanceVariable extends UMLWizard {
    
    private WizStepTextField step1 = null;
    private String label = Translator.localize("label.name");
    private String instructions =
	"Please change the name of the offending model element.";
 
    /**
     * Creates a new instance of WizAddInstanceVariable
     */
    public WizAddInstanceVariable() {
        super();
    }
    
    /**
     * @see org.argouml.cognitive.ui.Wizard#doAction(int)
     */
    public void doAction(int oldStep) {
        Object attr;
        
        switch (oldStep) {
        case 1:
            String newName = suggestion;
            if (step1 != null)
        	newName = step1.getText();
            Object me = getModelElement();
            Collection propertyChangeListeners = ProjectManager.getManager()
                .getCurrentProject().findFigsForMember(me);
            Object intType = ProjectManager.getManager()
                .getCurrentProject().findType("int");
            Object model = ProjectManager.getManager()
                .getCurrentProject().getModel();
            attr = UmlFactory.getFactory().getCore()
                .buildAttribute(me, model, intType, propertyChangeListeners);
            ModelFacade.setName(attr, newName);
        }
    }
    
    
    /**
     * @param s set a new instruction string
     */
    public void setInstructions(String s) {
	instructions = s;
    }
    
    /**
     * @param b
     */
    /*public void setMustEdit(boolean b) {
	mustEdit = b;
    }*/
    
    /**
     * Create a new panel for the given step.
     *
     * @see org.argouml.cognitive.ui.Wizard#makePanel(int)
     */
    public JPanel makePanel(int newStep) {
        switch (newStep) {
	case 1:
	    if (step1 == null) {
		step1 = new WizStepTextField(this, instructions,
					      label, getSuggestion());
	    }
	    return step1;
        }
        return null;
    }
}

