// $Id: ModeCreateLink.java,v 1.2 2003/12/02 22:05:27 kataka Exp $
// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// Original author: jaap.branderhorst@xs4all.nl
package org.argouml.uml.diagram.sequence.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.LayerManager;
import org.tigris.gef.base.ModeCreate;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigNode;

/**
 * Mode to create a link between two figobjects. This mode extends ModeCreate and
 * not ModeCreateEdge because ModeCreateEdge hides its variables a bit too much...
 */
public class ModeCreateLink extends ModeCreate {

    /** The NetPort where the arc is paintn from */
    private Object _startPort;

    /** The Fig that presents the starting NetPort */
    private Fig _startPortFig;

    /** The FigNode on the NetNode that owns the start port */
    private FigNode _sourceFigNode;

    /** The new NetEdge that is being created */
    private Object _newEdge;

    public ModeCreateLink() {
        super();
    }

    public ModeCreateLink(Editor par) {
        super(par);
    }

    public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigLine(
            snapX,
            snapY,
            me.getX(),
            snapY,
            Globals.getPrefs().getRubberbandColor());
    }
    
    public String instructions() {
        return "Drag to define a link to another port";
    }

    /** On mousePressed determine what port the user is dragging from.
     *  The mousePressed event is sent via ModeSelect. */
    public void mousePressed(MouseEvent me) {
        if (me.isConsumed())
            return;
        int x = me.getX(), y = me.getY();
        Editor ce = Globals.curEditor();
        Fig underMouse = ce.hit(x, y);
        if (underMouse == null) {
            underMouse = ce.hit(x - 16, y - 16, 32, 32);
        }
        if (underMouse == null) {
            done();
            me.consume();
            return;
        }
        if (!(underMouse instanceof FigNode)) {
            done();
            me.consume();
            return;
        }
        _sourceFigNode = (FigNode) underMouse;
        _startPort = _sourceFigNode.deepHitPort(x, y);
        if (_startPort == null) {
            done();
            me.consume();
            return;
        }
        _startPortFig = _sourceFigNode.getPortFig(_startPort);
        start();
        Point snapPt = new Point();
        synchronized (snapPt) {
            snapPt.setLocation(
                _startPortFig.getX() + FigObject.WIDTH / 2,
                _startPortFig.getY());
            editor.snap(snapPt);
            anchorX = snapPt.x;
            anchorY = snapPt.y;
        }
        _newItem = createNewItem(me, anchorX, anchorY);
        me.consume();
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /** 
     * On mouseReleased, find the port in the destination node that's on the same height
     * as the source port fig, ask the GraphModel
     *  to connect the two ports.  If that connection is allowed, then
     *  construct a new FigEdge and add it to the Layer and send it to
     *  the back. */
    public void mouseReleased(MouseEvent me) {
        if (me.isConsumed())
            return;
        if (_sourceFigNode == null) {
            done();
            me.consume();
            return;
        }

        int x = me.getX(), y = me.getY();
        Class arcClass;
        Editor ce = Globals.curEditor();
        Fig f = ce.hit(x, y);
        if (f == null) {
            f = ce.hit(x - 16, y - 16, 32, 32);
        }
        GraphModel gm = ce.getGraphModel();
        if (!(gm instanceof MutableGraphModel))
            f = null;
        MutableGraphModel mgm = (MutableGraphModel) gm;
        // needs-more-work: potential class cast exception

        if (f instanceof FigNode) {
            FigNode destFigNode = (FigNode) f;
            // If its a FigNode, then check within the  
            // FigNode to see if a port exists 
            Object foundPort = null;
            if (destFigNode != _sourceFigNode) {
                y = _startPortFig.getY();
                foundPort = destFigNode.deepHitPort(x, y);
            } else {
                foundPort = destFigNode.deepHitPort(x, y);
            }

            if (foundPort != null && foundPort != _startPort) {
                Fig destPortFig = destFigNode.getPortFig(foundPort);
                Class edgeClass = (Class) getArg("edgeClass");
                if (edgeClass != null)
                    _newEdge = mgm.connect(_startPort, foundPort, edgeClass);
                else
                    _newEdge = mgm.connect(_startPort, foundPort);

                // Calling connect() will add the edge to the GraphModel and
                // any LayerPersectives on that GraphModel will get a
                // edgeAdded event and will add an appropriate FigEdge
                // (determined by the GraphEdgeRenderer).

                if (null != _newEdge) {
                    LayerManager lm = ce.getLayerManager();                    
                    ce.damaged(_newItem);
                    _sourceFigNode.damage();
                    destFigNode.damage();
                    _newItem = null;
                    FigLink fe =
                        (FigLink) ce
                            .getLayerManager()
                            .getActiveLayer()
                            .presentationFor(
                            _newEdge);
                    fe.setSourcePortFig(_startPortFig);
                    fe.setSourceFigNode(_sourceFigNode);
                    fe.setDestPortFig(destPortFig);
                    fe.setDestFigNode(destFigNode);                    
                    //					set the new edge in place
                    if (_sourceFigNode != null)
                        _sourceFigNode.updateEdges();
                    if (destFigNode != null)
                        destFigNode.updateEdges();
                    if (fe != null)
                        ce.getSelectionManager().select(fe);
                    done();
                    me.consume();
                    return;
                } else
                    System.out.println("connection return null");
            }
        }
        _sourceFigNode.damage();
        ce.damaged(_newItem);
        _newItem = null;
        done();
        me.consume();
    }

    public void mouseDragged(MouseEvent me) {
        if (me.isConsumed())
            return;
        if (_newItem != null) {
            editor.damaged(_newItem);
            creationDrag(me.getX(), _startPortFig.getY());
            editor.damaged(_newItem);
        }
        editor.scrollToShow(me.getX(), _startPortFig.getY());
        me.consume();
    }
} /* end class ModeCreateEdge */
