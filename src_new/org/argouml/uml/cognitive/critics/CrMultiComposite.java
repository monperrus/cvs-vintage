// $Id: CrMultiComposite.java,v 1.8 2004/08/29 15:43:00 mvw Exp $
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
// File: CrMultiComposite.java
// Classes: CrMultiComposite
// Original Author: jrobbins@ics.uci.edu
// $Id: CrMultiComposite.java,v 1.8 2004/08/29 15:43:00 mvw Exp $

package org.argouml.uml.cognitive.critics;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.critics.Critic;
import org.argouml.model.ModelFacade;

/** Well-formedness rule [2] for MAssociationEnd. See page 28 of UML
 *  1.1 Semantics. OMG document ad/97-08-04.  */

public class CrMultiComposite extends CrUML {
    
    /**
     * The constructor.
     * 
     */
    public CrMultiComposite() {
        setHeadline("Composite Role with MMultiplicity > 1");
        addSupportedDecision(CrUML.decCONTAINMENT);
        setKnowledgeTypes(Critic.KT_SEMANTICS);
        addTrigger("aggregation");
        addTrigger("multiplicity");
    }
    
    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
        boolean problem = NO_PROBLEM;
        if (ModelFacade.isAAssociationEnd(dm)) {
            if (ModelFacade.isComposite(dm)) {
                if (ModelFacade.getUpper(dm) > 1) {
                    problem = PROBLEM_FOUND;
                }
            }
        }
        return problem;
    }
    
    /**
     * @see org.argouml.cognitive.critics.Critic#getWizardClass(org.argouml.cognitive.ToDoItem)
     */
    public Class getWizardClass(ToDoItem item) {
        return WizAssocComposite.class;
    }
    
} /* end class CrMultiComposite */
