// $Id: CrInvalidBranch.java,v 1.8 2004/09/21 19:03:26 mvw Exp $
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

// File: CrInvalidBranch.java
// Classes: CrInvalidBranch
// Original Author: jrobbins@ics.uci.edu
// $Id: CrInvalidBranch.java,v 1.8 2004/09/21 19:03:26 mvw Exp $

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import org.argouml.cognitive.Designer;
import org.argouml.model.ModelFacade;


/** 
 * A critic to detect when a Branch (i.e. Choice or Junction) 
 * state has the wrong number of transitions.  
 * Implements constraint [5] and [6] on PseudoState in the UML
 * Semantics v1.3, p. 2-140: 
 * 
 * [5] A junction vertex must have at least one incoming and 
 * one outgoing transition.
 * (self.kind = #junction) implies
 *     ((self.incoming->size >= 1) and (self.outgoing->size >= 1))
 * 
 * [6] A choice vertex must have at least one incoming and 
 * one outgoing transition.
 * (self.kind = #choice) implies
 *     ((self.incoming->size >= 1) and (self.outgoing->size >= 1))
 * 
 * */

public class CrInvalidBranch extends CrUML {

    /** the constructor
     */
    public CrInvalidBranch() {
	setHeadline(""); // parameter ignored, so "" suffices.
	addSupportedDecision(CrUML.DEC_STATE_MACHINES);
	addTrigger("incoming");
    }

    /** This is the decision routine for the critic. 
     * 
     * @param dm is the UML entity (an NSUML object) that is being checked. 
     * @param dsgr is for future development and can be ignored.
     * 
     * @return boolean problem found
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAPseudostate(dm))) return NO_PROBLEM;
	Object k = ModelFacade.getPseudostateKind(dm);
	if ((!ModelFacade.equalsPseudostateKind(k,
				ModelFacade.BRANCH_PSEUDOSTATEKIND))
            && (!ModelFacade.equalsPseudostateKind(k,
                                ModelFacade.JUNCTION_PSEUDOSTATEKIND)))
	    return NO_PROBLEM;
	Collection outgoing = ModelFacade.getOutgoings(dm);
	Collection incoming = ModelFacade.getIncomings(dm);
	int nOutgoing = outgoing == null ? 0 : outgoing.size();
	int nIncoming = incoming == null ? 0 : incoming.size();
	if (nIncoming < 1) return PROBLEM_FOUND;
	if (nOutgoing < 1) return PROBLEM_FOUND;
	return NO_PROBLEM;
    }

} /* end class CrInvalidBranch */

