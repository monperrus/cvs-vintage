// $Id: CrCompInstanceWithoutNode.java,v 1.12 2004/08/29 14:51:54 mvw Exp $
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

// File: CrCompInstanceWithoutNode.java
// Classes: CrCompInstanceWithoutNode
// Original Author: 5eichler@informatik.uni-hamburg.de
// $Id: CrCompInstanceWithoutNode.java,v 1.12 2004/08/29 14:51:54 mvw Exp $

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import java.util.Iterator;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.uml.cognitive.UMLToDoItem;
import org.argouml.uml.diagram.deployment.ui.FigComponentInstance;
import org.argouml.uml.diagram.deployment.ui.FigMNodeInstance;
import org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram;
import org.argouml.model.ModelFacade;

import org.tigris.gef.util.VectorSet;

/**
 * A critic to detect when there are component-instances that
 * are not inside a node-instance
 **/

public class CrCompInstanceWithoutNode extends CrUML {

    /**
     * The constructor.
     * 
     */
    public CrCompInstanceWithoutNode() {
	setHeadline("ComponentInstances normally are inside nodes");
	addSupportedDecision(CrUML.decPATTERNS);
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(dm instanceof UMLDeploymentDiagram)) return NO_PROBLEM;
	UMLDeploymentDiagram dd = (UMLDeploymentDiagram) dm;
	VectorSet offs = computeOffenders(dd); 
	if (offs == null) return NO_PROBLEM; 
	return PROBLEM_FOUND; 
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#toDoItem(java.lang.Object, 
     * org.argouml.cognitive.Designer)
     */
    public ToDoItem toDoItem(Object dm, Designer dsgr) { 
	UMLDeploymentDiagram dd = (UMLDeploymentDiagram) dm;
	VectorSet offs = computeOffenders(dd); 
	return new UMLToDoItem(this, offs, dsgr); 
    } 
 
    /**
     * @see org.argouml.cognitive.Poster#stillValid(
     * org.argouml.cognitive.ToDoItem, org.argouml.cognitive.Designer)
     */
    public boolean stillValid(ToDoItem i, Designer dsgr) { 
	if (!isActive()) return false; 
	VectorSet offs = i.getOffenders(); 
	UMLDeploymentDiagram dd = (UMLDeploymentDiagram) offs.firstElement();
	//if (!predicate(dm, dsgr)) return false; 
	VectorSet newOffs = computeOffenders(dd); 
	boolean res = offs.equals(newOffs); 
	return res; 
    } 

    /**
     * If there are component-instances that have no enclosing FigMNodeInstance
     * the returned vector-set is not null. Then in the vector-set
     * are the UMLDeploymentDiagram and all FigComponentInstances with no
     * enclosing FigMNodeInstance
     *
     * @param deploymentDiagram the diagram to check
     * @return the set of offenders
     */
    public VectorSet computeOffenders(UMLDeploymentDiagram deploymentDiagram) { 

	Collection figs = deploymentDiagram.getLayer().getContents(null);
	VectorSet offs = null;
	boolean isNode = false;
        Iterator it = figs.iterator();
        Object obj = null;
	while (it.hasNext()) {
	    obj = it.next();
	    if (obj instanceof FigMNodeInstance) {
                isNode = true;
            }
	}
        it = figs.iterator();
	while (it.hasNext()) {
	    obj = it.next();
	    if (!(obj instanceof FigComponentInstance)) {
                continue;
            }
	    FigComponentInstance fc = (FigComponentInstance) obj;
	    if ((fc.getEnclosingFig() == null) && isNode) {
		if (offs == null) {
		    offs = new VectorSet();
		    offs.addElement(deploymentDiagram);
		}
		offs.addElement(fc);
	    } else if (fc.getEnclosingFig() != null
		     && ((ModelFacade.getNodeInstance(fc.getOwner()))
			 == null))
	    {
		if (offs == null) {
		    offs = new VectorSet();
		    offs.addElement(deploymentDiagram);
		}
		offs.addElement(fc);
	    }
     
	}

	return offs; 
    } 

} /* end class CrCompInstanceWithoutNode.java */

