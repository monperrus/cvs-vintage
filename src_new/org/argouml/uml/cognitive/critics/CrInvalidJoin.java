// $Id: CrInvalidJoin.java,v 1.7 2004/03/18 10:00:10 mkl Exp $
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

// File: CrInvalidJoin.java
// Classes: CrInvalidJoin
// Original Author: jrobbins@ics.uci.edu
// $Id: CrInvalidJoin.java,v 1.7 2004/03/18 10:00:10 mkl Exp $

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import org.argouml.cognitive.Designer;
import org.argouml.model.ModelFacade;


/** A critic to detect when a join state has the wrong number of
 *  transitions.  Implements constraint [4] on MPseudostate in the UML
 *  Semantics v1.1, pp. 104. */


public class CrInvalidJoin extends CrUML {

    public CrInvalidJoin() {
	setHeadline("Change Join Transitions");
	addSupportedDecision(CrUML.decSTATE_MACHINES);
	addTrigger("outgoing");
    }

    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAPseudostate(dm))) return NO_PROBLEM;
	Object k = ModelFacade.getPseudostateKind(dm);
	if (!ModelFacade.
	    equalsPseudostateKind(k,
				  ModelFacade.JOIN_PSEUDOSTATEKIND))
	    return NO_PROBLEM;
	Collection outgoing = ModelFacade.getOutgoings(dm);
	Collection incoming = ModelFacade.getIncomings(dm);
	int nOutgoing = outgoing == null ? 0 : outgoing.size();
	int nIncoming = incoming == null ? 0 : incoming.size();
	if (nOutgoing > 1) return PROBLEM_FOUND;
	if (nIncoming == 1) return PROBLEM_FOUND;
	return NO_PROBLEM;
    }

} /* end class CrInvalidJoin */

