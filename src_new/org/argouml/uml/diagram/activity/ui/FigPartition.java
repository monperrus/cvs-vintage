// $Id: FigPartition.java,v 1.1 2004/07/18 12:13:15 mkl Exp $
// Copyright (c) 2003-2004 The Regents of the University of California. All
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
import java.util.Iterator;

import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/**
 * This class represents a Partition or Swimlane for Activity diagrams.
 * 
 * @author mkl
 *  
 */
public class FigPartition extends FigNodeModelElement {

    private FigLine _leftLine, _rightLine;

    private final int PADDING = 8;

    /**
     * Constructor.
     *  
     */
    public FigPartition() {
        _bigPort = new FigRect(10, 10, 150, 200, Color.white, Color.white);
        _bigPort.setFilled(true);
        _leftLine = new FigLine(10, 10, 10, 300, Color.gray);
        _rightLine = new FigLine(150, 10, 150, 300, Color.gray);
        _leftLine.setDashed(true);
        _rightLine.setDashed(true);

        getNameFig().setLineWidth(0);
        getNameFig().setBounds(10 + PADDING, 10, 50 - PADDING * 2, 25);
        getNameFig().setFilled(false);
        getNameFig().setMultiLine(true);

        addFig(_bigPort);
        addFig(_rightLine);
        addFig(_leftLine);
        addFig(getNameFig());

        Rectangle r = getBounds();
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * Constructor.
     * 
     * @param gm
     * @param node
     */
    public FigPartition(GraphModel gm, Object node) {
        this();
        setOwner(node);
    }

    /**
     * 
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        FigPartition figClone = (FigPartition) super.clone();
        Iterator it = figClone.getFigs(null).iterator();
        figClone._bigPort = (FigRect) it.next();
        figClone._rightLine = (FigLine) it.next();
        figClone._leftLine = (FigLine) it.next();
        figClone.setNameFig((FigText) it.next());
        return figClone;
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#setLineColor(java.awt.Color)
     */
    public void setLineColor(Color col) {
        _rightLine.setLineColor(col);
        _leftLine.setLineColor(col);
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#getLineColor()
     */
    public Color getLineColor() {
        return _rightLine.getLineColor();
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#setFillColor(java.awt.Color)
     */
    public void setFillColor(Color col) {
        _bigPort.setFillColor(col);
        getNameFig().setFillColor(col);
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#getFillColor()
     */
    public Color getFillColor() {
        return _bigPort.getFillColor();
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#setFilled(boolean)
     */
    public void setFilled(boolean f) {
        _bigPort.setFilled(f);
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#getFilled()
     */
    public boolean getFilled() {
        return _bigPort.getFilled();
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#setLineWidth(int)
     */
    public void setLineWidth(int w) {
        _rightLine.setLineWidth(w);
        _leftLine.setLineWidth(w);
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#getLineWidth()
     */
    public int getLineWidth() {
        return _rightLine.getLineWidth();
    }

    /**
     * 
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#placeString()
     */
    public String placeString() {
        return "new Swimlane";
    }

    public Dimension getMinimumSize() {
        Dimension nameDim = getNameFig().getMinimumSize();
        int w = nameDim.width + PADDING * 2;
        int h = nameDim.height;
        return new Dimension(w, h);
    }

    /**
     * Using a traprect enables us to move containing figs easily.
     * 
     * @return <code>true</code>
     * 
     * @see org.tigris.gef.presentation.Fig#getUseTrapRect()
     */
    public boolean getUseTrapRect() {
        return true;
    }

    /**
     * 
     * @see org.tigris.gef.presentation.Fig#setBounds(int, int, int, int)
     */
    public void setBounds(int x, int y, int w, int h) {
        if (getNameFig() == null) return;
        Rectangle oldBounds = getBounds();

        Rectangle nameBounds = getNameFig().getBounds();
        getNameFig().setBounds(x + PADDING, y, w - PADDING * 2,
                nameBounds.height);

        _bigPort.setBounds(x, y, w, h);
        _leftLine.setBounds(x, y, 0, h);
        _rightLine.setBounds(x + w , y, 0, h);

        firePropChange("bounds", oldBounds, getBounds());
        calcBounds(); //_x = x; _y = y; _w = w; _h = h;
        updateEdges();
    }

}

