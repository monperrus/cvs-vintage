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

// File: ActionResize.java
// Classes: ActionResize
// Original Author: jrobbins@ics.uci.edu
// $Id: ActionResize.java,v 1.1 1998/01/26 22:19:20 jrobbins Exp $

package uci.gef;

import java.awt.*;
import java.util.*;

/** needs-more-work  */

public class ActionResize extends Action {

  public ActionResize() { }

  public String name() {
    return "Allow user to Resize selected figs";
  }

  public void doIt(Event e) {
    Editor ce = Globals.curEditor();
    SelectionManager sm = ce.getSelectionManager();
    Enumeration sels = ((Vector)sm.selections().clone()).elements();
    while (sels.hasMoreElements()) {
      Selection s = (Selection) sels.nextElement();
      if (s instanceof Selection && !(s instanceof SelectionResize)) {
	Fig f = s.getContent();
	if (f.isReshapable()) {
	  ce.damaged(s);
	  sm.removeSelection(s);
	  SelectionResize sr = new SelectionResize(f);
	  sm.addSelection(sr);
	  ce.damaged(sr);
	}
      }
    }
  }

  public void undoIt() { System.out.println("not done yet"); }

} /* end class ActionResize */

