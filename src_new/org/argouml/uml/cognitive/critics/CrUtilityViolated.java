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
// File: CrUtilityViolated.java
// Classes: CrUtilityViolated
// Original Author: jrobbins@ics.uci.edu
// $Id: CrUtilityViolated.java,v 1.8 2003/02/12 11:04:27 mkl Exp $

package org.argouml.uml.cognitive.critics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.argouml.cognitive.Designer;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.foundation.core.CoreHelper;

/** A critic to detect when a class can never have instances (of
 *  itself of any subclasses). */
public class CrUtilityViolated extends CrUML {
    
    public CrUtilityViolated() {
        setHeadline("Remove instance variables from Utility Class");
        addSupportedDecision(CrUML.decSTORAGE);
        addSupportedDecision(CrUML.decSTEREOTYPES);
        addSupportedDecision(CrUML.decCLASS_SELECTION);
        addTrigger("stereotype");
        addTrigger("behavioralFeature");
    }
    
    public boolean predicate2(Object dm, Designer dsgr) {
        boolean problem = NO_PROBLEM;
        // we could check for base class of the stereotype but the condition normally covers it all.
        if (ModelFacade.isAClassifier(dm)
            && ModelFacade.getStereoType(dm) != null && ModelFacade.getName(ModelFacade.getStereoType(dm)).equals(
                "utility")) {
            Collection classesToCheck = new ArrayList();
            classesToCheck.addAll(CoreHelper.getHelper().getSupertypes(dm));
            classesToCheck.addAll(
                CoreHelper.getHelper().getAllRealizedInterfaces(dm));
            classesToCheck.add(dm);
            Iterator it = classesToCheck.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (!ModelFacade.isAInterface(o)) {
                    Iterator it2 = ModelFacade.getAttributes(o).iterator();
                    while (it2.hasNext()) {
                        if (ModelFacade.isInstanceScope(it2.next())) {
                            problem = PROBLEM_FOUND;
                            break;
                        }
                    }
                    if (problem) {
                        break;
                    }
                }
                Iterator it2 = ModelFacade.getOperations(o).iterator();
                while (it2.hasNext()) {
                    if (ModelFacade.isInstanceScope(it2.next())) {
                        problem = PROBLEM_FOUND;
                        break;
                    }
                }
                if (problem) {
                    break;
                }
            }
        }
        return problem;
    }
    
} /* end class CrUtilityViolated */
