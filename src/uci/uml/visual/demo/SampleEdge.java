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

// File: SampleEdge.java
// Classes: SampleEdge
// Original Author: ics125b spring 1996
// $Id: SampleEdge.java,v 1.1 1998/03/09 22:20:39 abonner Exp $

package uci.gef.demo;

import uci.gef.*;

/** A sample NetEdge subclass for use in the Example application. There
 * are no real details here yet. If I was to expand this Example more
 * the Edge could have its own attributes, e.g. bandwidth... and it
 * could have its own subclasses of FigEdge to make it look a
 * certain way. */

public class SampleEdge extends NetEdge {
  /** Construct a new SampleEdge. */
  public SampleEdge() { } /* needs-more-work */

  public FigEdge makePresentation(Layer lay) {
    FigEdge foo = new FigEdgeLine();
    foo.setSourceArrowHead(new ArrowHeadTriangle());
    return foo;
  }

} /* end class SampleEdge */
