// Copyright (c) 1995, 1996 Regents of the University of California.
// All rights reserved.
//
// This software was developed by the Arcadia project
// at the University of California, Irvine.
//
// Redistribution and use in source and binary forms are permitted
// provided that the above copyright notice and this paragraph are
// duplicated in all such forms and that any documentation,
// advertising materials, and other materials related to such
// distribution and use acknowledge that the software was developed
// by the University of California, Irvine.  The name of the
// University may not be used to endorse or promote products derived
// from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
// WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.

// File: CrUnconventionalAttrName.java.java
// Classes: CrUnconventionalAttrName.java
// Original Author: jrobbins@ics.uci.edu
// $Id: CrUnconventionalAttrName.java,v 1.7 1998/06/11 21:09:02 jrobbins Exp $

package uci.uml.critics;

import java.util.*;
import uci.argo.kernel.*;
import uci.util.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Foundation.Data_Types.*;
import uci.uml.Model_Management.*;


public class CrUnconventionalAttrName extends CrUML {

  public CrUnconventionalAttrName() {
    setHeadline("Choose a Better Attribute Name");
    sd("Normally attributes begin with a lowercase letter. "+
       "The name '{name}' is unconventional because it does not.\n\n"+
       "Following good naming conventions help to improve "+
       "the understandability and maintainability of the design. \n\n"+
       "To fix this, use the FixIt button, or manually select {name} "+
       "and use the Properties tab to give it a name.");
    addSupportedDecision(CrUML.decNAMING);
  }

  protected void sd(String s) { setDescription(s); }
  
  public boolean predicate(Object dm, Designer dsgr) {
    if (!(dm instanceof Attribute)) return NO_PROBLEM;
    Attribute attr = (Attribute) dm;
    Name myName = attr.getName();
    if (myName == null || myName.equals(Name.UNSPEC)) return NO_PROBLEM;
    String nameStr = myName.getBody();
    if (nameStr.length() == 0) return NO_PROBLEM;
    char initalChar = nameStr.charAt(0);
    ChangeableKind ck = attr.getChangeable();
    if (ck != null && ck.equals(ChangeableKind.FROZEN)) return NO_PROBLEM;
    if (!Character.isLowerCase(initalChar)) return PROBLEM_FOUND;
    return NO_PROBLEM;
  }

} /* end class CrUnconventionalAttrName */

