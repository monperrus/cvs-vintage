// Copyright (c) 1995, 1996 Regents of the University of California.
// All rights reserved.
//
// This software was developed by the Arcadia project
// at the University of California, Irvine.
//
// Redistribution and use in source and binary forms are permitted
// provided that the above copyright notice and this paragraph are
// duplicated in all such forms and that any documentation,
// advertising materials, and other materials related to such
// distribution and use acknowledge that the software was developed
// by the University of California, Irvine.  The name of the
// University may not be used to endorse or promote products derived
// from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
// WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.

// File: EquipmentPalette.java
// Classes: EquipmentPalette
// Original Author: jrobbins@ics.uci.edu
// $Id: EquipmentPalette.java,v 1.1 1998/03/09 22:20:34 abonner Exp $

package uci.gef.demo;

import java.awt.*;
import java.util.*;
import uci.gef.*;

/** A class to define the left hand column of buttons in the Example
 *  application. Right now it just has one kind of node.
 *
 * @see uci.gef.demo.FlexibleApplet */

public class EquipmentPalette extends Palette {

  /** The class for the NetNode's to make when the user clicks the
   * SampleNode button. */
  static final String CPU_CLASS = "uci.gef.demo.NodeCPU";
  static final String PRINTER_CLASS = "uci.gef.demo.NodePrinter";
  static final String WALL_CLASS = "uci.gef.demo.NodeWall";

  /** Construct a new palette of example nodes for the Example application */
  public EquipmentPalette() { super(2, 1, "Equipment"); }


  /** Define a button to make for the Example application */
  public Vector defineButtons() {
    Vector v = new Vector();
    Action addCPU = new ActionCreateNode(CPU_CLASS, null, false);
    v.addElement(new ActiveButton("CPU", addCPU));
    Action addPrinter = new ActionCreateNode(PRINTER_CLASS, null, false);
    v.addElement(new ActiveButton("Printer", addPrinter));
    Action addWall = new ActionCreateNode(WALL_CLASS, null, false);
    v.addElement(new ActiveButton("Wall", addWall));
    return v;
  }


} /* end class EquipmentPalette */
