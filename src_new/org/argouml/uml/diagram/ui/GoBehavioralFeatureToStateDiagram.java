// $Id: GoBehavioralFeatureToStateDiagram.java,v 1.3 2003/06/29 23:52:20 linus Exp $
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

// $Id: GoBehavioralFeatureToStateDiagram.java,v 1.3 2003/06/29 23:52:20 linus Exp $
package org.argouml.uml.diagram.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.AbstractGoRule;
import org.argouml.ui.ArgoDiagram;
import org.argouml.uml.diagram.state.ui.UMLStateDiagram;

import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MOperation;

/**
 * 
 * @author jaap.branderhorst@xs4all.nl	
 * @since Dec 30, 2002
 */
public class GoBehavioralFeatureToStateDiagram extends AbstractGoRule {

    /**
     * @see org.argouml.ui.AbstractGoRule#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object parent) {
        
        if (parent instanceof MBehavioralFeature) {
            MBehavioralFeature operation = (MBehavioralFeature) parent;
            Collection col = operation.getBehaviors();
            Vector ret = new Vector();
            Project p = ProjectManager.getManager().getCurrentProject();
            Vector diagrams = p.getDiagrams();
            Iterator it = diagrams.iterator();
            while (it.hasNext()) {
                ArgoDiagram diagram = (ArgoDiagram) it.next();
                if (diagram instanceof UMLStateDiagram &&
                    col.contains(((UMLStateDiagram) diagram).getStateMachine())) {
                    ret.add(diagram);
                }
                
            }
            return ret;
        }
        return null;
    }

    /**
     * @see org.argouml.ui.AbstractGoRule#getRuleName()
     */
    public String getRuleName() {
        return "Behavioral Feature -> State diagram";
    }

}
