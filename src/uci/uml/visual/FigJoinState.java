// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products may
// be obtained by contacting the University of California. David F. Redmiles
// Department of Information and Computer Science (ICS) University of
// California Irvine, California 92697-3425 Phone: 714-824-3823. This software
// program and documentation are copyrighted by The Regents of the University
// of California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.

// File: FigJoinState.java
// Classes: FigJoinState
// Original Author: jrobbins@ics.uci.edu
// $Id: FigJoinState.java,v 1.4 1999/02/06 03:07:48 jrobbins Exp $

package uci.uml.visual;

import java.awt.*;
import java.util.*;
import java.beans.*;
import com.sun.java.swing.*;

import uci.gef.*;
import uci.graph.*;
import uci.uml.ui.*;
import uci.uml.generate.*;
import uci.uml.Foundation.Core.*;
import uci.uml.Behavioral_Elements.State_Machines.*;

/** Class to display graphics for a UML State in a diagram. */

public class FigJoinState extends FigStateVertex {

  ////////////////////////////////////////////////////////////////
  // constants

  public static final int MARGIN = 2;
  public static final int X = 10;
  public static final int Y = 10;
  public static final int WIDTH = 80;
  public static final int HEIGHT = 9;

  ////////////////////////////////////////////////////////////////
  // instance variables

  FigRect _bigPort;
  FigRect _head;

  ////////////////////////////////////////////////////////////////
  // constructors

  public FigJoinState() {
    _bigPort = new FigRect(X,Y,WIDTH,HEIGHT, Color.cyan, Color.cyan);
    _head = new FigRect(X,Y,WIDTH,HEIGHT, Color.black, Color.black);
    // add Figs to the FigNode in back-to-front order
    addFig(_bigPort);
    addFig(_head);

    setBlinkPorts(false); //make port invisble unless mouse enters
    Rectangle r = getBounds();
  }

  public FigJoinState(GraphModel gm, Object node) {
    this();
    setOwner(node);
  }

  public Object clone() {
    FigJoinState figClone = (FigJoinState) super.clone();
    Vector v = figClone.getFigs();
    figClone._bigPort = (FigRect) v.elementAt(0);
    figClone._head = (FigRect) v.elementAt(1);
    return figClone;
  }

  ////////////////////////////////////////////////////////////////
  // Fig accessors

  public void setOwner(Object node) {
    super.setOwner(node);
    bindPort(node, _bigPort);
    // if it is a UML meta-model object, register interest in any change events
    if (node instanceof ElementImpl)
      ((ElementImpl)node).addVetoableChangeListener(this);
  }

  /** Initial states are fixed size. */
  //public boolean isResizable() { return false; }

  /* Override setBounds to keep shapes looking right */
  public void setBounds(int x, int y, int w, int h) {
    Rectangle oldBounds = getBounds();
    if (w > h) h = 9; else w = 9;
    _bigPort.setBounds(x, y, w, h);
    _head.setBounds(x, y, w, h);

    calcBounds(); //_x = x; _y = y; _w = w; _h = h;
    updateEdges();
    firePropChange("bounds", oldBounds, getBounds());
  }

  public void setLineColor(Color col) { _head.setLineColor(col); }
  public Color getLineColor() { return _head.getLineColor(); }

  public void setFillColor(Color col) { _head.setFillColor(col); }
  public Color getFillColor() { return _head.getFillColor(); }

  public void setFilled(boolean f) { }
  public boolean getFilled() { return true; }

  public void setLineWidth(int w) { _head.setLineWidth(w); }
  public int getLineWidth() { return _head.getLineWidth(); }

  static final long serialVersionUID = 2075803883819230367L;

} /* end class FigJoinState */
