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


// File: CmdSequence.java
// Classes: CmdSequence
// Original Author: jrobbins@ics.uci.edu
// $Id: CmdSequence.java,v 1.4 1998/04/18 01:26:05 jrobbins Exp $

package uci.gef;

import java.util.*;
import java.awt.*;

/** Cmd to execute a sequence of Cmd's.  Needs-More-Work: Not done
 *  yet. This could be part of a user macro recording feature.  Or an
 *  easy way for programmers to define new Cmds that are basically
 *  sequence of existing Cmds.  */

public class CmdSequence extends Cmd {

  private Vector _cmds;

  /** Construct a new CmdSequence */
  public CmdSequence() { super("Sequence of Commands"); }
  public CmdSequence(String name) { super(name); }
  public CmdSequence(Cmd a1, String name) {
    super(name);
    add(a1);
  }
  public CmdSequence(Cmd a1, Cmd a2, String name) {
    super(name);
    add(a1);
    add(a2);
  }
  public CmdSequence(Cmd a1, Cmd a2, Cmd a3, String name) {
    super(name);
    add(a1);
    add(a2);
    add(a3);
  }

  public void add(Cmd a) { _cmds.addElement(a); };

  public String dbgString() {
    String n = "Seq: ";
    Enumeration acts = _cmds.elements();
    while (acts.hasMoreElements()) {
      Cmd a = (Cmd) acts.nextElement();
      n = n + a.getName();
      if (acts.hasMoreElements()) n = n + ", ";
    }
    return n;
  }

  /** Call the undoIt method of each subCmd. */
  public void doIt() {
    Editor ce = Globals.curEditor();
    Enumeration acts = _cmds.elements();
    while (acts.hasMoreElements()) {
      Cmd a = (Cmd) acts.nextElement();
      ce.executeCmd(a, null);
    }
  }

  /** Call the undoIt method of each subCmd, in reverse order! */
  public void undoIt() {
    for (int i = _cmds.size() - 1; i >= 0; --i)
      ((Cmd)_cmds.elementAt(i)).undoIt();
  }

} /* end class CmdSequence */

