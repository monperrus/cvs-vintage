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


// File: EquipmentPalette.java
// Classes: EquipmentPalette
// Original Author: jrobbins@ics.uci.edu
// $Id: EquipmentPalette.java,v 1.4 1998/04/13 22:48:34 jrobbins Exp $

package uci.gef.demo;

import java.awt.*;
import java.util.*;
import uci.gef.*;

/** A class to define the left hand column of buttons in the Example
 *  application. Right now it just has one kind of node.
 *
 * @see uci.gef.demo.FlexibleApplet */

public class EquipmentPalette extends uci.ui.ToolBar {

  /** Construct a new palette of example nodes for the Example application */
  public EquipmentPalette() { defineButtons(); }


  /** Define a button to make for the Example application */
  public void  defineButtons() {
    Vector v = new Vector();
    add(new CmdCreateNode(NodeCPU.class, "CPU"), "CPU", "NodeOne");
    add(new CmdCreateNode(NodePrinter.class, "Printer"), "Printer", "NodeOne");
    add(new CmdCreateNode(NodeWall.class, "Wall"), "Wall", "NodeOne");
  }


} /* end class EquipmentPalette */
