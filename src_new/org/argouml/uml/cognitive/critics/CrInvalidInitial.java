// $Id: CrInvalidInitial.java,v 1.8 2004/09/21 19:03:26 mvw Exp $
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

// File: CrInvalidInitial.java
// Classes: CrInvalidInitial
// Original Author: jrobbins@ics.uci.edu
// $Id: CrInvalidInitial.java,v 1.8 2004/09/21 19:03:26 mvw Exp $

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import org.argouml.cognitive.Designer;
import org.argouml.model.ModelFacade;


/** A critic to detect when an initial state has more than one
 *  outgoing transitions.  Implements a constraint from the UML
 *  1.1 standard: page 10, MPseudostate [1]. */

public class CrInvalidInitial extends CrUML {

    /**
     * The constructor.
     * 
     */
    public CrInvalidInitial() {
	setHeadline("Remove Extra Outgoing Transitions");
	addSupportedDecision(CrUML.DEC_STATE_MACHINES);
	addTrigger("outgoing");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAPseudostate(dm))) return NO_PROBLEM;
	Object k = ModelFacade.getPseudostateKind(dm);
	if (!ModelFacade.
	    equalsPseudostateKind(k,
				  ModelFacade.INITIAL_PSEUDOSTATEKIND))
	    return NO_PROBLEM;
	Collection outgoing = ModelFacade.getOutgoings(dm);
	int nOutgoing = outgoing == null ? 0 : outgoing.size();
	if (nOutgoing > 1) return PROBLEM_FOUND;
	return NO_PROBLEM;
    }

} /* end class CrInvalidInitial */

