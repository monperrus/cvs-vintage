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

// File: EdgePower.java
// Classes: EdgePower
// Original Author: jrobbins@ics.uci.edu
// $Id: EdgePower.java,v 1.1 1998/03/09 22:20:33 abonner Exp $

package uci.gef.demo;

import uci.gef.*;

/** A sample NetEdge subclass for use in the Example application. */

public class EdgePower extends NetEdge {

  ////////////////////////////////////////////////////////////////
  // instance variables

  /** Voltage currently on line. */
  protected int _voltage;

  /** Maximum Voltage that this line can handle. */
  protected int _maxVoltage;

  /** Some power cords have a third grounding prong, some don't. */
  protected boolean _hasGroundProng;

  ////////////////////////////////////////////////////////////////
  // constructor

  public EdgePower() { } /* needs-more-work */

  ////////////////////////////////////////////////////////////////
  // NetEdge API

  public FigEdge makePresentation(Layer lay) {
    return new FigEdgeLine();
  }

} /* end class EdgePower */
