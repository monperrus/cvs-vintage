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



// File: CrMissingStateName.java
// Classes: CrMissingStateName
// Original Author: jrobbins@ics.uci.edu
// $Id: CrMissingStateName.java,v 1.3 1999/02/19 22:22:57 jrobbins Exp $

package uci.uml.critics;

import java.util.*;
import com.sun.java.swing.*;

import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Model_Management.*;

public class CrMissingStateName extends CrUML {

  public CrMissingStateName() {
    setHeadline("Choose a Name");
    sd("Every state within a state machine should have a name. \n\n"+
       "Clear and unambiguous naming is key to code generation and "+
       "the understandability and maintainability of the design. \n\n"+
       "To fix this, use the \"Next>\" button, or manually select the "+
       "state and use the Properties tab to give it a name, or select "+
       "the state and type a name.");
    addSupportedDecision(CrUML.decNAMING);
    setKnowledgeTypes(Critic.KT_COMPLETENESS, Critic.KT_SYNTAX);
    addTrigger("name");
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof Element)) return NO_PROBLEM;
    Element e = (Element) dm;
    Name myName = e.getName();
    if (myName == null || myName.equals(Name.UNSPEC) ||
	 myName.getBody() == null || myName.getBody().length() == 0)
      return PROBLEM_FOUND;
    return NO_PROBLEM;
  }

  public Icon getClarifier() {
    return ClClassName.TheInstance;
  }

} /* end class CrMissingStateName.java */

