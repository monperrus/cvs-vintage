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



// File: CrSingletonViolated.java
// Classes: CrSingletonViolated
// Original Author: jrobbins@ics.uci.edu
// $Id: CrSingletonViolated.java,v 1.2 2001/10/19 13:22:30 mkl Exp $

package org.argouml.pattern.cognitive.critics;

import java.util.*;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;

import org.argouml.cognitive.*;
import org.argouml.uml.*;
import org.argouml.uml.cognitive.critics.*;

/** A critic to detect whether a class violates the conditions required for
    using a Singleton Stereotype. */
public class CrSingletonViolated extends CrUML {

  public CrSingletonViolated() {
    setResource("CrSingletonViolated");
    addSupportedDecision(CrUML.decPATTERNS);
    setPriority(ToDoItem.LOW_PRIORITY);
    addTrigger("stereotype");
    addTrigger("structuralFeature");
    addTrigger("associationEnd");
  }

  protected void sd(String s) { setDescription(s); }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof MClass)) return NO_PROBLEM;
    MClass cls = (MClass) dm;
    Vector str = new Vector(MMUtil.SINGLETON.getAttributes(cls));
    Vector ends = new Vector(cls.getAssociationEnds());

    boolean markedSingleton = false;
    //if it is not marked Singleton, nevermind
    MStereotype st = cls.getStereotype();
    if (st != null && st.getName().equals("Singleton")) {
		return PROBLEM_FOUND;
		//markedSingleton = true;
      }
	else return NO_PROBLEM;

    //needs-more-work
    // (1) The class must have at least one static attr with of the same type
    // (2) All constructors should be private
    // (3) There must be at least on constructor to override default constructor

  }

} /* end class CrSingletonViolated */

