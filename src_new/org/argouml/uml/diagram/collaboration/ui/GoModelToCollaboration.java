// $Id: GoModelToCollaboration.java,v 1.7 2003/09/13 22:06:05 alexb Exp $
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

package org.argouml.uml.diagram.collaboration.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.argouml.model.ModelFacade;

import org.argouml.model.uml.modelmanagement.ModelManagementHelper;
import org.argouml.ui.AbstractGoRule;

/**
 * @since Oct 1, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class GoModelToCollaboration extends AbstractGoRule {

    public String getRuleName() { return "Model->Collaboration"; }

    /**
     * @see org.argouml.ui.AbstractGoRule#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object parent) {
	if (org.argouml.model.ModelFacade.isAModel(parent)) {
            Object model = /*(MModel)*/ parent;
            Collection col = ModelManagementHelper.getHelper()
		.getAllModelElementsOfKind(model,
                    (Class)ModelFacade.COLLABORATION);
            List returnList = new ArrayList();
            Iterator it = col.iterator();
            while (it.hasNext()) {
                Object collab = /*(MCollaboration)*/ it.next();
                if (ModelFacade.getRepresentedClassifier(collab) == null && 
                    ModelFacade.getRepresentedOperation(collab) == null) {
                        
                    returnList.add(collab);
                }
            }
            return returnList;
        }
        return null;
    }

}