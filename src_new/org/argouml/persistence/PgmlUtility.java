// $Id: PgmlUtility.java,v 1.2 2006/03/25 22:09:47 linus Exp $
// Copyright (c) 2005-2006 The Regents of the University of California. All
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

package org.argouml.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.argouml.uml.diagram.static_structure.ui.FigEdgeNote;
import org.tigris.gef.base.Layer;

/**
 * Utility class for use by pgml.tee.
 *
 * @author Bob Tarling
 */
public final class PgmlUtility {

    /**
     * Constructor.
     */
    private PgmlUtility() {
    }

    /**
     * Return just the comment edges for a specific layer.
     *
     * @param lay The {@link Layer}.
     * @return a {@link List} with the edges.
     */
    public static List getCommentEdges(Layer lay) {
        Collection edges = lay.getContentsEdgesOnly();
        List comments = new ArrayList(edges.size());
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof FigEdgeNote) {
                comments.add(o);
            }
        }
        return comments;
    }


    /**
     * Return just the edges for a specific layer that are not comment edges.
     *
     * @param lay The {@link Layer}.
     * @return a {@link List} with the edges.
     */
    public static List getNonCommentEdges(Layer lay) {
        Collection edges = lay.getContentsEdgesOnly();
        List nonComments = new ArrayList(edges.size());
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof FigEdgeNote)) {
                nonComments.add(o);
            }
        }
        return nonComments;
    }

}
