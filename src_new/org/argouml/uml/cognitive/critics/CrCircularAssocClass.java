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



// File: CrCircularAssocClass.java
// Classes: CrCircularAssocClass
// Original Author: jrobbins@ics.uci.edu
// $Id: CrCircularAssocClass.java,v 1.3 2002/10/20 21:11:15 linus Exp $

package org.argouml.uml.cognitive.critics;

import java.util.*;

import ru.novosoft.uml.foundation.core.*;

import org.argouml.cognitive.*;
import org.argouml.cognitive.critics.*;

/** Well-formedness rule [2] for MAssociationClass. See page 28 of UML 1.1
 *  Semantics. OMG document ad/97-08-04. */

public class CrCircularAssocClass extends CrUML {

  public CrCircularAssocClass() {
    setHeadline("Circular MAssociation");

    addSupportedDecision(CrUML.decRELATIONSHIPS);
    setKnowledgeTypes(Critic.KT_SEMANTICS);
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    // TODO: not implemented
    return NO_PROBLEM;
  }

} /* end class CrCircularAssocClass.java */

