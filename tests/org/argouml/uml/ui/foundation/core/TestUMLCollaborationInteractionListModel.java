// $Id: TestUMLCollaborationInteractionListModel.java,v 1.10 2005/01/02 16:43:47 linus Exp $
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

// $header$
package org.argouml.uml.ui.foundation.core;

import org.argouml.model.Model;
import org.argouml.uml.ui.AbstractUMLModelElementListModel2Test;
import org.argouml.uml.ui.behavior.collaborations.UMLCollaborationInteractionListModel;

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.behavior.collaborations.MCollaboration;
import ru.novosoft.uml.behavior.collaborations.MInteraction;

/**
 * @since Oct 28, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class TestUMLCollaborationInteractionListModel
    extends AbstractUMLModelElementListModel2Test {

    /**
     * Constructor for TestUMLCollaborationInteractionListModel.
     * @param arg0 is the name of the test case.
     */
    public TestUMLCollaborationInteractionListModel(String arg0) {
        super(arg0);
    }

    /**
     * @see org.argouml.uml.ui.AbstractUMLModelElementListModel2Test#buildElement()
     */
    protected void buildElement() {
        setElem(Model.getCollaborationsFactory().createCollaboration());
    }

    /**
     * @see org.argouml.uml.ui.AbstractUMLModelElementListModel2Test#buildModel(org.argouml.uml.ui.UMLUserInterfaceContainer)
     */
    protected void buildModel() {
        setModel(new UMLCollaborationInteractionListModel());
    }

    /**
     * @see org.argouml.uml.ui.AbstractUMLModelElementListModel2Test#fillModel()
     */
    protected MBase[] fillModel() {
        MInteraction[] inter = new MInteraction[10];
        for (int i = 0; i < 10; i++) {
            inter[i] = Model.getCollaborationsFactory().createInteraction();
            inter[i].setContext((MCollaboration) getElem());
        }
        return inter;
    }

    /**
     * @see org.argouml.uml.ui.AbstractUMLModelElementListModel2Test#removeHalfModel(ru.novosoft.uml.MBase)
     */
    protected void removeHalfModel(MBase[] elements) {
        for (int i = 0; i < 5; i++) {
            ((MCollaboration) getElem()).removeInteraction(
                    (MInteraction) elements[i]);
        }
    }

}
