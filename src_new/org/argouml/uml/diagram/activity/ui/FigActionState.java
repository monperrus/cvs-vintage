// $Id: FigActionState.java,v 1.26 2005/06/25 10:00:41 bobtarling Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.uml.diagram.activity.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.Iterator;

import org.argouml.application.api.Notation;
import org.argouml.model.Model;
import org.argouml.uml.diagram.state.ui.FigStateVertex;
import org.argouml.uml.generator.ParserDisplay;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigRRect;
import org.tigris.gef.presentation.FigText;

/**
 * Class to display graphics for a UML ActionState in a diagram.
 *
 * @author ics 125b silverbullet team
 */
public class FigActionState extends FigStateVertex {

    ////////////////////////////////////////////////////////////////
    // constants

    private static final int MARGIN = 2;

    private static final int PADDING = 8;

    ////////////////////////////////////////////////////////////////
    // instance variables

    private FigRRect cover;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Constructor FigActionState
     */
    public FigActionState() {
        setBigPort(new FigRRect(10 + 1, 10 + 1, 90 - 2, 25 - 2, Color.cyan,
                Color.cyan));
        ((FigRRect) getBigPort()).setCornerRadius(getBigPort().getHalfHeight());
        cover = new FigRRect(10, 10, 90, 25, Color.black, Color.white);
        cover.setCornerRadius(getHalfHeight());

        getBigPort().setLineWidth(0);
        getNameFig().setLineWidth(0);
        getNameFig().setBounds(10 + PADDING, 10, 90 - PADDING * 2, 25);
        getNameFig().setFilled(false);
        getNameFig().setMultiLine(true);

        // add Figs to the FigNode in back-to-front order
        addFig(getBigPort());
        addFig(cover);
        addFig(getNameFig());

        //setBlinkPorts(false); //make port invisble unless mouse enters
        Rectangle r = getBounds();
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * Constructor FigActionState
     * @param gm ignored!
     * @param node owner
     */
    public FigActionState(GraphModel gm, Object node) {
        this();
        setOwner(node);
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#placeString()
     */
    public String placeString() {
        return "new ActionState";
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        FigActionState figClone = (FigActionState) super.clone();
        Iterator it = figClone.getFigs().iterator();
        figClone.setBigPort((FigRRect) it.next());
        figClone.cover = (FigRRect) it.next();
        figClone.setNameFig((FigText) it.next());
        return figClone;
    }

    ////////////////////////////////////////////////////////////////
    // Fig accessors

    /**
     * @see org.tigris.gef.presentation.Fig#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        Dimension nameDim = getNameFig().getMinimumSize();
        int w = nameDim.width + PADDING * 2;
        int h = nameDim.height + PADDING;
        return new Dimension(w, h);
    }

    /**
     * Override setBounds to keep shapes looking right
     * @see org.tigris.gef.presentation.Fig#setBounds(int, int, int, int)
     */
    protected void setBoundsInternal(int x, int y, int w, int h) {
        if (getNameFig() == null) return;
        Rectangle oldBounds = getBounds();

        getNameFig().setBounds(x + PADDING, y, w - PADDING * 2, h - PADDING);
        getBigPort().setBounds(x + 1, y + 1, w - 2, h - 2);
        cover.setBounds(x, y, w, h);
        ((FigRRect) getBigPort()).setCornerRadius(h);
        cover.setCornerRadius(h);

        calcBounds();
        updateEdges();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setLineColor(java.awt.Color)
     */
    public void setLineColor(Color col) {
        cover.setLineColor(col);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getLineColor()
     */
    public Color getLineColor() {
        return cover.getLineColor();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setFillColor(java.awt.Color)
     */
    public void setFillColor(Color col) {
        cover.setFillColor(col);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getFillColor()
     */
    public Color getFillColor() {
        return cover.getFillColor();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setFilled(boolean)
     */
    public void setFilled(boolean f) {
        cover.setFilled(f);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getFilled()
     */
    public boolean getFilled() {
        return cover.getFilled();
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setLineWidth(int)
     */
    public void setLineWidth(int w) {
        cover.setLineWidth(w);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getLineWidth()
     */
    public int getLineWidth() {
        return cover.getLineWidth();
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent mee) {
        super.modelChanged(mee);
        if (mee.getSource() == getOwner()
                && mee.getPropertyName().equals("entry")) {
            if (mee.getNewValue() != null) {
                Model.getPump().addModelEventListener(this,
                                            mee.getNewValue(), "script");
            } else
                if (mee.getOldValue() != null) {
                    Model.getPump().removeModelEventListener(this,
                                            mee.getOldValue(), "script");
                }
            updateNameText();
            damage();
        } else
            if (Model.getFacade().getEntry(getOwner()) == mee.getSource()) {
                updateNameText();
                damage();
            }

    }


    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateNameText()
     */
    protected void updateNameText() {
        if (getOwner() != null)
            getNameFig().setText(Notation.generate(this, getOwner()));
    }


    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEdited(org.tigris.gef.presentation.FigText)
     */
    protected void textEdited(FigText ft) throws PropertyVetoException {
        if (ft == getNameFig() && this.getOwner() != null) {
            ParserDisplay.SINGLETON.parseActionState(ft.getText(),
                    this.getOwner());
        } else
            super.textEdited(ft);
    }
} /* end class FigActionState */
