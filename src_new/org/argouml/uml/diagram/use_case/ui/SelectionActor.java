// $Id: SelectionActor.java,v 1.22 2005/01/02 16:43:51 linus Exp $
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

package org.argouml.uml.diagram.use_case.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.Model;
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

/**
 * @author jrobbins@ics.uci.edu
 */
public class SelectionActor extends SelectionWButtons {

    private static final Logger LOG = Logger.getLogger(SelectionActor.class);

    /**
     * The icon for an association.
     */
    private static Icon assoc =
	ResourceLoaderWrapper.lookupIconResource("Association");



    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new SelectionActor for the given Fig.
     *
     * @param f The given Fig.
     */
    public SelectionActor(Fig f) { super(f); }

    /**
     * @see org.tigris.gef.base.Selection#hitHandle(java.awt.Rectangle, 
     * org.tigris.gef.presentation.Handle)
     */
    public void hitHandle(Rectangle r, Handle h) {
	super.hitHandle(r, h);
	if (h.index != -1) return;
	if (!isPaintButtons()) return;
	Editor ce = Globals.curEditor();
	SelectionManager sm = ce.getSelectionManager();
	if (sm.size() != 1) return;
	ModeManager mm = ce.getModeManager();
	if (mm.includes(ModeModify.class) && getPressedButton() == -1) return;
	int cx = _content.getX();
	int cy = _content.getY();
	int cw = _content.getWidth();
	int ch = _content.getHeight();
	int aw = assoc.getIconWidth();
	int ah = assoc.getIconHeight();
	if (hitLeft(cx + cw, cy + ch / 2, aw, ah, r)) {
	    h.index = 12;
	    h.instructions = "Add an associated use case";
	}
	else if (hitRight(cx, cy + ch / 2, aw, ah, r)) {
	    h.index = 13;
	    h.instructions = "Add an associated use case";
	}
	else {
	    h.index = -1;
	    h.instructions = "Move object(s)";
	}
    }


    /**
     * @see SelectionWButtons#paintButtons(Graphics)
     */
    public void paintButtons(Graphics g) {
	int cx = _content.getX();
	int cy = _content.getY();
	int cw = _content.getWidth();
	int ch = _content.getHeight();
	paintButtonLeft(assoc, g, cx + cw, cy + ch / 2, 12);
	paintButtonRight(assoc, g, cx, cy + ch / 2, 13);
    }


    /**
     * @see org.tigris.gef.base.Selection#dragHandle(int, int, int, int, 
     * org.tigris.gef.presentation.Handle)
     */
    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
	if (hand.index < 10) {
	    setPaintButtons(false);
	    super.dragHandle(mX, mY, anX, anY, hand);
	    return;
	}
	int cx = _content.getX(), cy = _content.getY();
	int cw = _content.getWidth(), ch = _content.getHeight();
	int newX = cx, newY = cy, newW = cw, newH = ch;
	Dimension minSize = _content.getMinimumSize();
	int minWidth = minSize.width, minHeight = minSize.height;
	Class edgeClass = null;
	Class nodeClass = (Class) ModelFacade.USE_CASE;
	int bx = mX, by = mY;
	boolean reverse = false;
	switch (hand.index) {
	case 12: //add assoc
	    edgeClass = (Class) ModelFacade.ASSOCIATION;
	    by = cy + ch / 2;
	    bx = cx + cw;
	    break;
	case 13: // add assoc
	    edgeClass = (Class) ModelFacade.ASSOCIATION;
	    reverse = true;
	    by = cy + ch / 2;
	    bx = cx;
	    break;
	default:
	    LOG.warn("invalid handle number");
	    break;
	}
	if (edgeClass != null && nodeClass != null) {
	    Editor ce = Globals.curEditor();
	    ModeCreateEdgeAndNode m = new
		ModeCreateEdgeAndNode(ce, edgeClass, nodeClass, false);
	    m.setup((FigNode) _content, _content.getOwner(), bx, by, reverse);
	    ce.pushMode(m);
	}

    }


  


    /**
     * @see org.argouml.uml.diagram.ui.SelectionWButtons#getNewNode(int)
     */
    protected Object getNewNode(int buttonCode) {
        return Model.getUmlFactory().getUseCases().createUseCase();
    }

    /**
     * @see SelectionWButtons#createEdgeLeft(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeLeft(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, _content.getOwner(),
			  (Class) ModelFacade.ASSOCIATION);
    }

    /**
     * @see SelectionWButtons#createEdgeRight(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeRight(MutableGraphModel gm, Object newNode) {
        return gm.connect(_content.getOwner(), newNode ,
			  (Class) ModelFacade.ASSOCIATION);
    }

} /* end class SelectionActor */

