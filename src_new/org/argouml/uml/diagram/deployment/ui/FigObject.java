// $Id: FigObject.java,v 1.22 2004/08/01 16:28:26 mvw Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

// File: FigObject.java
// Classes: FigObject
// Original Author: 5eichler@informatik.uni-hamburg.de
// $Id: FigObject.java,v 1.22 2004/08/01 16:28:26 mvw Exp $

package org.argouml.uml.diagram.deployment.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.Vector;

import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlFactory;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.argouml.uml.generator.ParserDisplay;
import org.tigris.gef.base.Selection;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/** Class to display graphics for a UML Object in a diagram. */

public class FigObject extends FigNodeModelElement {

    ////////////////////////////////////////////////////////////////
    // instance variables

    private FigRect cover;
    private FigRect bigPort;
    private Object resident =
	UmlFactory.getFactory().getCore().createElementResidence();


    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Main constructor.
     */
    public FigObject() {
	bigPort = new FigRect(10, 10, 90, 50, Color.cyan, Color.cyan);
	cover = new FigRect(10, 10, 90, 50, Color.black, Color.white);
	getNameFig().setLineWidth(0);
	getNameFig().setFilled(false);
	getNameFig().setUnderline(true);
	Dimension nameMin = getNameFig().getMinimumSize();
	getNameFig().setBounds(10, 10, nameMin.width + 20, nameMin.height);

	// add Figs to the FigNode in back-to-front order
	addFig(bigPort);
	addFig(cover);
	addFig(getNameFig());

	Rectangle r = getBounds();
	setBounds(r.x, r.y, nameMin.width, nameMin.height);
    }

    /**
     * Constructor that hooks the Fig to an existing UML element
     * @param gm ignored
     * @param node the UML element
     */
    public FigObject(GraphModel gm, Object node) {
	this();
	setOwner(node);
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#placeString()
     */
    public String placeString() { return "new Object"; }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
	FigObject figClone = (FigObject) super.clone();
	Iterator it = figClone.getFigs(null).iterator();
	figClone.bigPort = (FigRect) it.next();
	figClone.cover = (FigRect) it.next();
	figClone.setNameFig((FigText) it.next());
	return figClone;
    }


    ////////////////////////////////////////////////////////////////
    // Fig accessors

    /**
     * @see org.tigris.gef.presentation.Fig#setLineColor(java.awt.Color)
     */
    public void setLineColor(Color col) { cover.setLineColor(col); }

    /**
     * @see org.tigris.gef.presentation.Fig#getLineColor()
     */
    public Color getLineColor() { return cover.getLineColor(); }

    /**
     * @see org.tigris.gef.presentation.Fig#setFillColor(java.awt.Color)
     */
    public void setFillColor(Color col) { cover.setFillColor(col); }
    
    /**
     * @see org.tigris.gef.presentation.Fig#getFillColor()
     */
    public Color getFillColor() { return cover.getFillColor(); }

    /**
     * @see org.tigris.gef.presentation.Fig#setFilled(boolean)
     */
    public void setFilled(boolean f) { cover.setFilled(f); }
    
    /**
     * @see org.tigris.gef.presentation.Fig#getFilled()
     */
    public boolean getFilled() { return cover.getFilled(); }

    /**
     * @see org.tigris.gef.presentation.Fig#setLineWidth(int)
     */
    public void setLineWidth(int w) { cover.setLineWidth(w); }
    
    /**
     * @see org.tigris.gef.presentation.Fig#getLineWidth()
     */
    public int getLineWidth() { return cover.getLineWidth(); }

  
    /**
     * @see org.tigris.gef.presentation.Fig#makeSelection()
     */
    public Selection makeSelection() {
	return new SelectionObject(this);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getMinimumSize()
     */
    public Dimension getMinimumSize() {
	Dimension bigPortMin = bigPort.getMinimumSize();
	Dimension coverMin = cover.getMinimumSize();
	Dimension nameMin = getNameFig().getMinimumSize();

	int w = nameMin.width + 10;
	int h = nameMin.height + 5;
	return new Dimension(w, h);
    }

    /** Override setBounds to keep shapes looking right 
     * @see org.tigris.gef.presentation.Fig#setBounds(int, int, int, int)
     */
    public void setBounds(int x, int y, int w, int h) {
	if (getNameFig() == null) {
	    return;
	}

	Rectangle oldBounds = getBounds();

	Dimension nameMin = getNameFig().getMinimumSize();

	bigPort.setBounds(x, y, w, h);
	cover.setBounds(x, y, w, h);
	getNameFig().setBounds(x, y, nameMin.width + 10, nameMin.height + 4);

	//_bigPort.setBounds(x+1, y+1, w-2, h-2);
	_x = x; _y = y; _w = w; _h = h;

	firePropChange("bounds", oldBounds, getBounds());
	calcBounds(); //_x = x; _y = y; _w = w; _h = h;
	updateEdges();
    }

  
    ////////////////////////////////////////////////////////////////
    // user interaction methods

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEdited(org.tigris.gef.presentation.FigText)
     */
    protected void textEdited(FigText ft) throws PropertyVetoException {
	Object obj = /*(MObject)*/ getOwner();
	if (ft == getNameFig()) {
	    String s = ft.getText().trim();
	    if (s.length() > 0 && (s.endsWith(":"))) {
		s = s.substring(0, (s.length() - 1));
	    }
	    ParserDisplay.SINGLETON.parseObject(obj, s);
	}
    }


    /**
     * @see org.tigris.gef.presentation.Fig#setEnclosingFig(org.tigris.gef.presentation.Fig)
     */
    public void setEnclosingFig(Fig encloser) {
	// super.setEnclosingFig(encloser);

	if (ModelFacade.isAObject(getOwner())) {
	    Object me = /*(MObject)*/ getOwner();
	    Object mcompInst = null;
	    Object mcomp = null;

	    if (encloser != null
		&& (ModelFacade.isAComponentInstance(encloser.getOwner()))) {

		mcompInst = /*(MComponentInstance)*/ encloser.getOwner();
		ModelFacade.setComponentInstance(me, mcompInst);

	    }
	    else if (ModelFacade.getComponentInstance(me) != null) {
                ModelFacade.setComponentInstance(me, null);
	    }
	    if (encloser != null
		&& (ModelFacade.isAComponent(encloser.getOwner()))) {

		mcomp = /*(MComponent)*/ encloser.getOwner();
		Object obj = /*(MObject)*/ getOwner();
		ModelFacade.setImplementationLocation(resident, mcomp);
		ModelFacade.setResident(resident, obj);

	    }
	    else {
		if (ModelFacade.getImplementationLocation(resident) != null) {
		    ModelFacade.setImplementationLocation(resident, null);
		    ModelFacade.setResident(resident, null);
		}
	    }
	}

	if (encloser != _encloser) {
	    if (_encloser instanceof FigNodeModelElement) {
		((FigNodeModelElement) _encloser).removeEnclosedFig(this);
            }
	    if (encloser instanceof FigNodeModelElement) {
		((FigNodeModelElement) encloser).addEnclosedFig(this);
            }
	}
        _encloser = encloser;
    }

    static final long serialVersionUID = -185736690375678962L;

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateNameText()
     */
    protected void updateNameText() {
        Object obj = /*(MObject)*/ getOwner();
	if (obj == null) return;
	String nameStr = "";
	if (ModelFacade.getName(obj) != null) {
	    nameStr = ModelFacade.getName(obj).trim();
	}

	Vector bases = new Vector(ModelFacade.getClassifiers(obj));
 
	String baseString = "";

	if (ModelFacade.getClassifiers(obj) != null
	        && ModelFacade.getClassifiers(obj).size() > 0) {

	    baseString += ModelFacade.getName(bases.elementAt(0));
	    for (int i = 1; i < bases.size(); i++)
		baseString +=
		    ", "  + ModelFacade.getName(bases.elementAt(i));
	}

	if (_readyToEdit) {
	    if ( nameStr == "" && baseString == "") {
		getNameFig().setText("");
	    } else {
		getNameFig().setText(nameStr.trim() + " : " + baseString);
	    }
	}
	Dimension nameMin = getNameFig().getMinimumSize();
	Rectangle r = getBounds();
	setBounds(r.x, r.y, nameMin.width + 10, nameMin.height + 4);
    }

} /* end class FigObject */
