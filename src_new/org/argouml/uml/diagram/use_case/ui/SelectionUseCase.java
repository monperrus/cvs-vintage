// $Id: SelectionUseCase.java,v 1.16 2003/09/17 23:25:52 bobtarling Exp $
// Copyright (c) 1996-2002 The Regents of the University of California. All
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

// File: SelectionUseCase.java
// Classes: SelectionUseCase
// Original Author: jrobbins@ics.uci.edu

package org.argouml.uml.diagram.use_case.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.ModelFacade;
import org.argouml.uml.diagram.ui.ModeCreateEdgeAndNode;
import org.argouml.uml.diagram.ui.SelectionWButtons;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.ModeManager;
import org.tigris.gef.base.ModeModify;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

public class SelectionUseCase extends SelectionWButtons {
    protected static Logger cat =
        Logger.getLogger(SelectionUseCase.class);
    ////////////////////////////////////////////////////////////////
    // constants
    public static Icon inherit =
        ResourceLoaderWrapper.getResourceLoaderWrapper().lookupIconResource(
									    "Generalization");
    public static Icon assoc =
        ResourceLoaderWrapper.getResourceLoaderWrapper().lookupIconResource(
									    "Association");

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new SelectionUseCase for the given Fig */
    public SelectionUseCase(Fig f) {
        super(f);
    }

    /** Return a handle ID for the handle under the mouse, or -1 if
     *  none. TODO: in the future, return a Handle instance or
     *  null. <p>
     *  <pre>
     *   0-------1-------2
     *   |               |
     *   3               4
     *   |               |
     *   5-------6-------7
     * </pre>
     */
    public void hitHandle(Rectangle r, Handle h) {
        super.hitHandle(r, h);
        if (h.index != -1)
            return;
        if (!_paintButtons)
            return;
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.size() != 1)
            return;
        ModeManager mm = ce.getModeManager();
        if (mm.includes(ModeModify.class) && _pressedButton == -1)
            return;
        int cx = _content.getX();
        int cy = _content.getY();
        int cw = _content.getWidth();
        int ch = _content.getHeight();
        int iw = inherit.getIconWidth();
        int ih = inherit.getIconHeight();
        int aw = assoc.getIconWidth();
        int ah = assoc.getIconHeight();
        if (hitAbove(cx + cw / 2, cy, iw, ih, r)) {
            h.index = 10;
            h.instructions = "Add a more general use case";
        } else if (hitBelow(cx + cw / 2, cy + ch, iw, ih, r)) {
            h.index = 11;
            h.instructions = "Add a specialized use case";
        } else if (hitLeft(cx + cw, cy + ch / 2, aw, ah, r)) {
            h.index = 12;
            h.instructions = "Add an associated actor";
        } else if (hitRight(cx, cy + ch / 2, aw, ah, r)) {
            h.index = 13;
            h.instructions = "Add an associated actor";
        } else {
            h.index = -1;
            h.instructions = "Move object(s)";
        }
    }

    /** Paint the handles at the four corners and midway along each edge
     * of the bounding box.  */
    public void paintButtons(Graphics g) {
        int cx = _content.getX();
        int cy = _content.getY();
        int cw = _content.getWidth();
        int ch = _content.getHeight();
        paintButtonAbove(inherit, g, cx + cw / 2, cy, 10);
        paintButtonBelow(inherit, g, cx + cw / 2, cy + ch, 11);
        paintButtonLeft(assoc, g, cx + cw, cy + ch / 2, 12);
        paintButtonRight(assoc, g, cx, cy + ch / 2, 13);
    }

    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
        if (hand.index < 10) {
            _paintButtons = false;
            super.dragHandle(mX, mY, anX, anY, hand);
            return;
        }
        int cx = _content.getX(), cy = _content.getY();
        int cw = _content.getWidth(), ch = _content.getHeight();
        int newX = cx, newY = cy, newW = cw, newH = ch;
        Dimension minSize = _content.getMinimumSize();
        int minWidth = minSize.width, minHeight = minSize.height;
        Class edgeClass = null;
        Class nodeClass = null;
        if (hand.index == 10 || hand.index == 11)
            nodeClass = (Class)ModelFacade.USE_CASE;
        else
            nodeClass = (Class)ModelFacade.ACTOR;

        int bx = mX, by = mY;
        boolean reverse = false;
        switch (hand.index) {
	case 10 : //add superclass
	    edgeClass = (Class)ModelFacade.GENERALIZATION;
	    by = cy;
	    bx = cx + cw / 2;
	    break;
	case 11 : //add subclass
	    edgeClass = (Class)ModelFacade.GENERALIZATION;
	    reverse = true;
	    by = cy + ch;
	    bx = cx + cw / 2;
	    break;
	case 12 : //add assoc
	    edgeClass = (Class)ModelFacade.ASSOCIATION;
	    by = cy + ch / 2;
	    bx = cx + cw;
	    break;
	case 13 : // add assoc
	    edgeClass = (Class)ModelFacade.ASSOCIATION;
	    reverse = true;
	    by = cy + ch / 2;
	    bx = cx;
	    break;
	default :
	    cat.warn("invalid handle number");
	    break;
        }
        if (edgeClass != null && nodeClass != null) {
            Editor ce = Globals.curEditor();
            ModeCreateEdgeAndNode m =
                new ModeCreateEdgeAndNode(ce, edgeClass, nodeClass, false);
            m.setup((FigNode) _content, _content.getOwner(), bx, by, reverse);
            ce.mode(m);
        }

    }

    
    

    /**
     * @see
     * org.argouml.uml.diagram.ui.SelectionWButtons#createEdgeAbove(org.tigris.gef.graph.MutableGraphModel,
     * java.lang.Object)
     */
    protected Object createEdgeAbove(MutableGraphModel gm, Object newNode) {
        return gm.connect(_content.getOwner(), newNode, (Class)ModelFacade.GENERALIZATION);
    }

    /**
     * @see
     * org.argouml.uml.diagram.ui.SelectionWButtons#createEdgeLeft(org.tigris.gef.graph.MutableGraphModel,
     * java.lang.Object)
     */
    protected Object createEdgeLeft(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, _content.getOwner(), (Class)ModelFacade.ASSOCIATION);
    }

    /**
     * @see
     * org.argouml.uml.diagram.ui.SelectionWButtons#createEdgeRight(org.tigris.gef.graph.MutableGraphModel,
     * java.lang.Object)
     */
    protected Object createEdgeRight(MutableGraphModel gm, Object newNode) {
        return gm.connect(_content.getOwner(), newNode, (Class)ModelFacade.ASSOCIATION);
    }

    /**
     * @see
     * org.argouml.uml.diagram.ui.SelectionWButtons#createEdgeUnder(org.tigris.gef.graph.MutableGraphModel,
     * java.lang.Object)
     */
    protected Object createEdgeUnder(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, _content.getOwner(), (Class)ModelFacade.GENERALIZATION);
    }

    /**
     * @see org.argouml.uml.diagram.ui.SelectionWButtons#getNewNode(int)
     */
    protected Object getNewNode(int buttonCode) {
        Object newNode = null;
        if (buttonCode == 10 || buttonCode == 11)
            newNode = UmlFactory.getFactory().getUseCases().createUseCase();
        else
            newNode = UmlFactory.getFactory().getUseCases().createActor();
        return newNode;
    }

} /* end class SelectionUseCase */