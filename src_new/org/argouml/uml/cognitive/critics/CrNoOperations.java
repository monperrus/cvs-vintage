// $Id: CrNoOperations.java,v 1.24 2004/11/01 10:55:23 mkl Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

// File: CrNoOperations.javoa
// Classes: CrNoOperations
// Original Author: jrobbins@ics.uci.edu

package org.argouml.uml.cognitive.critics;

import java.util.Iterator;
import javax.swing.Icon;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.critics.Critic;
import org.argouml.cognitive.ui.Wizard;
import org.argouml.model.ModelFacade;


// Uses Model through ModelFacade.


/** A critic to detect when a class or interface or its base class doesn't 
 * have any operations.
 */
public class CrNoOperations extends CrUML {

    /**
     * The constructor.
     * 
     */
    public CrNoOperations() {
	setHeadline("Add Operations to <ocl>self</ocl>");
	addSupportedDecision(CrUML.DEC_BEHAVIOR);
	setKnowledgeTypes(Critic.KT_COMPLETENESS);
	addTrigger("behavioralFeature");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAClass(dm) 
            || ModelFacade.isAInterface(dm))) return NO_PROBLEM;

	if (!(ModelFacade.isPrimaryObject(dm))) return NO_PROBLEM;

        // if the object does not have a name,
        // than no problem
        if ((ModelFacade.getName(dm) == null) 
                || ("".equals(ModelFacade.getName(dm))))
            return NO_PROBLEM;

 	// types can probably contain operations, but we should not nag at them
	// not having any.
	if (ModelFacade.isType(dm)) return NO_PROBLEM;

	// utility is a namespace collection - also not strictly 
	// required to have operations.
	if (ModelFacade.isUtility(dm)) return NO_PROBLEM;

	//TODO: different critic or special message for classes
	//that inherit all ops but define none of their own.
	
	if (findInstanceOperationInInherited(dm, 0))
	    return NO_PROBLEM;

	return PROBLEM_FOUND;
    }

    /**
     * @see org.argouml.cognitive.Poster#getClarifier()
     */
    public Icon getClarifier() {
	return ClOperationCompartment.getTheInstance();
    }

    private boolean findInstanceOperationInInherited(Object dm, int depth)
    {
	Iterator ops = ModelFacade.getOperations(dm).iterator();

	while (ops.hasNext()) {
	    if (ModelFacade.isInstanceScope(ops.next()))
		return true;
	}

	if (depth > 50)
	    return false;

	Iterator iter = ModelFacade.getGeneralizations(dm).iterator();

	while (iter.hasNext()) {
	    Object parent = ModelFacade.getParent(iter.next());

	    if (parent == dm)
		continue;

	    if (ModelFacade.isAClassifier(parent))
		if (findInstanceOperationInInherited(parent, depth + 1))
		    return true;
	}

	return false;
    }
    
    /**
     * @see org.argouml.cognitive.critics.Critic#initWizard(org.argouml.kernel.Wizard)
     */
    public void initWizard(Wizard w) {
	if (w instanceof WizAddOperation) {
	    ToDoItem item = (ToDoItem) w.getToDoItem();
	    Object me = /*(MModelElement)*/ item.getOffenders().elementAt(0);
	    String ins = "Set the name of the new operation.";
	    String sug = "newOperation";
	    ((WizAddOperation) w).setInstructions(ins);
	    ((WizAddOperation) w).setSuggestion(sug);
	}
    }
    
    /**
     * @see org.argouml.cognitive.critics.Critic#getWizardClass(org.argouml.cognitive.ToDoItem)
     */
    public Class getWizardClass(ToDoItem item) { return WizAddOperation.class; }
} /* end class CrNoOperations */

