// $Id: CrInterfaceAllPublic.java,v 1.7 2003/09/11 00:07:16 bobtarling Exp $
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



// File: CrInterfaceAllPublic.java
// Classes: CrInterfaceAllPublic
// Original Author: jrobbins@ics.uci.edu
// $Id: CrInterfaceAllPublic.java,v 1.7 2003/09/11 00:07:16 bobtarling Exp $

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import java.util.Iterator;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.critics.Critic;
import org.argouml.model.ModelFacade;
import ru.novosoft.uml.foundation.data_types.MVisibilityKind;



/** Well-formedness rule [3] for MInterface. See page 32 of UML 1.1
 *  Semantics. OMG document ad/97-08-04. */


public class CrInterfaceAllPublic extends CrUML {

    public CrInterfaceAllPublic() {
	setHeadline("Operations in Interfaces must be public");
	addSupportedDecision(CrUML.decPLANNED_EXTENSIONS);
	setKnowledgeTypes(Critic.KT_SYNTAX);
	addTrigger("behavioralFeature");
    }

    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(ModelFacade.isAInterface(dm))) return NO_PROBLEM;
	Object inf = /*(MInterface)*/ dm;
	Collection bf = ModelFacade.getFeatures(inf);
	if (bf == null) return NO_PROBLEM;
	Iterator enum = bf.iterator();
	while (enum.hasNext()) {
	    Object f = /*(MFeature)*/ enum.next();
	    if (ModelFacade.getVisibility(f) == null) return NO_PROBLEM;
	    if (!ModelFacade.getVisibility(f).equals(MVisibilityKind.PUBLIC))
		return PROBLEM_FOUND;
	}
	return NO_PROBLEM;
    }

} /* end class CrInterfaceAllPublic.java */