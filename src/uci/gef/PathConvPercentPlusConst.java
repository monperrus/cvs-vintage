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

// File: PathConvPercentPlusConst.java
// Classes: PathConvPercentPlusConst
// Original Author: abonner@ics.uci.edu
// $Id: PathConvPercentPlusConst.java,v 1.1 1998/06/04 20:05:24 jrobbins Exp $

package uci.gef;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/** Used to place labels as specific positions along a FigEdge.  For
 *  example, a label can be placed in the middle of a FigEdge by using 50%. */

public class PathConvPercentPlusConst extends PathConv {
  float percent = 0;
  int _delta = 0;
  int offset = 0;

  public PathConvPercentPlusConst(Fig theFig,
				  float newPercent, int delta,
				  int newOffset) {
    super(theFig);
    setPercentOffset(newPercent, newOffset);
    _delta = delta;
  }

  public Point getPoint() {
    int figLength = _pathFigure.getPerimeterLength();
    int pointToGet = (int) (figLength * percent) + _delta;

    Point linePoint = _pathFigure.pointAlongPerimeter(pointToGet);

    //System.out.println("lP=" + linePoint + " ptG=" + pointToGet +
    //" figLen=" + figLength);

    Point offsetAmount =
      getOffsetAmount(_pathFigure.pointAlongPerimeter(pointToGet + 5),
		      _pathFigure.pointAlongPerimeter(pointToGet - 5), offset);

    return new Point(linePoint.x + offsetAmount.x,
		     linePoint.y + offsetAmount.y);
  }

  public void setPercentOffset(float newPercent, int newOffset) {
    percent = newPercent;
    offset = newOffset;
  }

  public void setClosestPoint(Point newPoint) { }

}/* end class PathConvPercentPlusConst */
