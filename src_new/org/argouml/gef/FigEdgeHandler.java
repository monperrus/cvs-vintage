// $Id: FigEdgeHandler.java,v 1.1 2005/04/28 19:37:17 bobtarling Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.gef;

import java.util.Collection;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigPoly;

/**
 * The handler for elements that represent FigEdge objects.
 */
public class FigEdgeHandler extends BaseHandler
implements Container
{
    private FigEdge _edge;

    /**
     * @param parser The overall parser object.
     * @param edge The FigEdge object created to correspond to the current
     * XML element.
     */
    public FigEdgeHandler( PGMLStackParser parser,
                           FigEdge edge)
    {
        super( parser);
        _edge=edge;
    }

    /**
     * @return The FigEdge object that corresponds to the current XML element.
     */
    public FigEdge getFigEdge()
    {
        return _edge;
    }

    /**
     * Adds the owner of the FigEdge object to the collection of edge owners
     * in the diagram associated with the {@link PGMLStackParser}.
     */
    public void endElement( String uri, String localname, String qname)
    throws SAXException
    {
        Object owner = _edge.getOwner();
        Collection edges = getPGMLStackParser().getDiagram().getEdges(null);
        if (!edges.contains(owner))
            edges.add(owner);
        super.endElement( uri, localname, qname);
    }

    /**
     * Incorporates a contained element into this FigEdge object.
     * Three types of contained elements are supported:
     * FigLine or FigPoly become the Fig associated with this FigEdge;
     * String valued elements (i.e., <b>private</b> elements) are themselves
     * parsed to determin the source and destination PortFig's for this
     * FigEdge.
     */
    public void addObject( Object o)
    throws SAXException
    {
        if ( o instanceof FigLine || o instanceof FigPoly)
            _edge.setFig( (Fig)o);
        if ( o instanceof String)
        {
            PGMLStackParser parser=getPGMLStackParser();
            Fig spf = null;
            Fig dpf = null;
            FigNode sfn = null;
            FigNode dfn = null;
            String body = (String)o;
            StringTokenizer st2 = new StringTokenizer(body, "=\"' \t\n");
            while(st2.hasMoreElements()) {
                String attribute = st2.nextToken();
                String value = st2.nextToken();
                if(attribute.equals("sourcePortFig")) {
                    spf = parser.findFig(value);
                }

                if(attribute.equals("destPortFig")) {
                    dpf = parser.findFig(value);
                }

                if(attribute.equals("sourceFigNode")) {
                    sfn = (FigNode)parser.findFig(value);
                }

                if(attribute.equals("destFigNode")) {
                    dfn = (FigNode)parser.findFig(value);
                }
            }

            if ( spf==null && sfn!=null)
                spf=(Fig)sfn.getPortFigs().get(0);

            if ( dpf==null && dfn!=null)
                dpf=(Fig)dfn.getPortFigs().get(0);

            if(spf == null || dpf == null || sfn == null || dfn == null) {
                throw new SAXException( "Can't find nodes for FigEdge: "+
                                        (String)o + ":" + _edge.toString());
            }
            else {
                _edge.setSourcePortFig(spf);
                _edge.setDestPortFig(dpf);
                _edge.setSourceFigNode(sfn);
                _edge.setDestFigNode(dfn);
            }

        }
    }
}