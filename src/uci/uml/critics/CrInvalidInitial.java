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
// $Id: CrInvalidInitial.java,v 1.2 1999/02/19 22:22:52 jrobbins Exp $

package uci.uml.critics;

import java.util.*;
import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Behavioral_Elements.State_Machines.*;

/** A critic to detect when an initial state has more than one
 *  outgoing transitions.  Implements a constraint from the UML
 *  1.1 standard: page 10, Pseudostate [1]. */

public class CrInvalidInitial extends CrUML {

  public CrInvalidInitial() {
    setHeadline("Remove Extra Outgoing Transitions");
    sd("This initial state has more than one outgoing transitions. Normally "+
       "initial states have exactly one outgoing transition. \n\n"+
       "Defining correct state transitions is needed to complete the  "+
       "behavioral specification part of your design.  \n\n"+
       "To fix this, press the \"Next>\" button, or remove transitions  "+
       "manually by clicking on transition in the diagram and pressing the "+
       "Delete key. ");

    addSupportedDecision(CrUML.decSTATE_MACHINES);
    addTrigger("outgoing");
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof Pseudostate)) return NO_PROBLEM;
    Pseudostate ps = (Pseudostate) dm;
    PseudostateKind k = ps.getKind();
    if (!PseudostateKind.INITIAL.equals(k)) return NO_PROBLEM;
    Vector outgoing = ps.getOutgoing();
    //Vector incoming = ps.getIncoming();
    int nOutgoing = outgoing == null ? 0 : outgoing.size();
    //int nIncoming = incoming == null ? 0 : incoming.size();
    if (nOutgoing > 1) return PROBLEM_FOUND;
    return NO_PROBLEM;
  }

} /* end class CrInvalidInitial */

