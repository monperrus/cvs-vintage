// $Id: GenCompositeClasses.java,v 1.9 2003/09/08 20:11:53 bobtarling Exp $
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

package org.argouml.uml;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.argouml.model.ModelFacade;
import org.tigris.gef.util.ChildGenerator;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;

/** Utility class to generate the children of a class.  In this case
 *  the "children" of a class are the other classes that are
 *  assocaiated with the parent class, and that MAssociation has a
 *  COMPOSITE end at the parent.  This is used in one of the
 *  NavPerspectives. 
 *  @stereotype singleton
 */

public class GenCompositeClasses implements ChildGenerator {
    public static GenCompositeClasses SINGLETON = new GenCompositeClasses();

    public Enumeration gen(Object o) {
	Vector res = new Vector();
	if (!(ModelFacade.isAClassifier(o))) return res.elements();
	Object cls = /*(MClassifier)*/ o;
	Vector ends = new Vector(ModelFacade.getAssociationEnds(cls));
	if (ends == null) return res.elements();
	Iterator enum = ends.iterator();
	while (enum.hasNext()) {
	    Object ae = /*(MAssociationEnd)*/ enum.next();
	    if (MAggregationKind.COMPOSITE.equals(ModelFacade.getAggregation(ae))) {
		Object asc = ModelFacade.getAssociation(ae);
		ArrayList conn = new ArrayList(ModelFacade.getConnections(asc));
		if (conn == null || conn.size() != 2) continue;
		Object otherEnd = (ae == conn.get(0)) ?
		    conn.get(1) : conn.get(0);
		res.add(ModelFacade.getType(otherEnd));
	    }
	}
	return res.elements();
    }
} /* end class GenCompositeClasses */
  