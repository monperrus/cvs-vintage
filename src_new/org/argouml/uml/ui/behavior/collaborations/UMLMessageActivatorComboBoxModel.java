// Copyright (c) 1996-99 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.collaborations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.argouml.model.uml.behavioralelements.collaborations.CollaborationsHelper;
import org.argouml.uml.ui.UMLComboBoxModel;
import org.argouml.uml.ui.UMLComboBoxModel2;
import org.argouml.uml.ui.UMLUserInterfaceComponent;
import org.argouml.uml.ui.UMLUserInterfaceContainer;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;
import ru.novosoft.uml.behavior.collaborations.MMessage;
import ru.novosoft.uml.foundation.core.MModelElement;

/**
 * The model behind the UMLMessageActivatorComboBox. I don't use the UMLComboBoxModel
 * since this mixes the GUI and the model too much and is much more maintainance 
 * intensive then this implementation.
 */
public class UMLMessageActivatorComboBoxModel extends UMLComboBoxModel2 {
		


    /**
     * Constructor for UMLMessageActivatorComboBoxModel.
     * @param container
     */
    public UMLMessageActivatorComboBoxModel(UMLUserInterfaceContainer container) {
        super(container, false);
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#buildModelList()
     */
    protected void buildModelList() {
        Object target = getContainer().getTarget();
        if (target instanceof MMessage) {
            MMessage mes = (MMessage)target;
            removeAllElements();
            // fill the list with items
            setElements(CollaborationsHelper.getHelper().getAllPossibleActivators(mes));
        }
    }
    
    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidPropertySet(ru.novosoft.uml.MElementEvent)
     */
    protected boolean isValidPropertySet(MElementEvent e) {
        return e.getSource() == getTarget() && e.getName().equals("activator");
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#isValidRoleAdded(ru.novosoft.uml.MElementEvent)
     */
    protected boolean isValidRoleAdded(MElementEvent e) {
        MModelElement m = (MModelElement)getChangedElement(e);
        return ((m instanceof MMessage)  && 
            m != getContainer().getTarget() && 
            !((MMessage)(getContainer().getTarget())).getPredecessors().contains(m) &&
            ((MMessage)m).getInteraction() == ((MMessage)(getContainer().getTarget())).getInteraction());
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBoxModel2#getSelectedModelElement()
     */
    protected Object getSelectedModelElement() {
        if (getTarget() != null) {
            return ((MMessage)getTarget()).getActivator();
        }
        return null;
    }

}
