// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products
// must be negotiated with University of California. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "as is",
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




// File: CmdUngroup.java
// Classes: CmdUngroup
// Original Author: jrobbins@ics.uci.edu
// $Id: CmdUngroup.java,v 1.6 1998/10/20 00:22:47 jrobbins Exp $

package uci.gef;

import java.util.*;
import java.awt.*;

/** Cmd to ungroup a selected group object.
 *
 * @see CmdGroup
 * @see FigGroup */

public class CmdUngroup extends Cmd {

  public CmdUngroup() { super("Ungroup"); }

  public void doIt() {
    Vector ungroupedItems = new Vector();
    Editor ce = Globals.curEditor();
    Vector selectedFigs = ce.getSelectionManager().getFigs();
    Enumeration eachDE = selectedFigs.elements();
    while (eachDE.hasMoreElements()) {
      Object o = eachDE.nextElement();
      if (o instanceof FigGroup) {
	FigGroup fg = (FigGroup) o;
	Enumeration eachFig = fg.elements();//?
	while (eachFig.hasMoreElements()) {
	  Fig f = (Fig) eachFig.nextElement();
	  ce.add(f);
	  ungroupedItems.addElement(f);
	}
	ce.remove(fg);
      }
    } /* end while each selected object */
    ce.getSelectionManager().deselectAll();
    ce.getSelectionManager().select(ungroupedItems);
  }

  public void undoIt() { System.out.println("not implemented yet"); }

  static final long serialVersionUID = -3576991253148770113L;

} /* end class CmdUngroup */

