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




// File: ActionCreatePseudostate.java
// Classes: ActionCreatePseudostate
// Original Author: jrobbins@ics.uci.edu
// $Id: ActionCreatePseudostate.java,v 1.1 1998/07/15 19:09:47 jrobbins Exp $

package uci.uml.ui;

import java.util.*;
import java.beans.*;

import uci.gef.*;
import uci.graph.*;
import uci.uml.Behavioral_Elements.State_Machines.*;
import uci.uml.Foundation.Data_Types.*;

/**  */

public class ActionCreatePseudostate extends CmdCreateNode {

  ////////////////////////////////////////////////////////////////
  // constructors

  /** Construct a new Cmd with the given classes for the NetNode
   *  and its FigNode. */
  public ActionCreatePseudostate(PseudostateKind kind, String name) {
    super(new Hashtable(), name);
    setArg("className", Pseudostate.class);
    setArg("kind", kind);
  }

  ////////////////////////////////////////////////////////////////
  // Cmd API

  /** Actually instanciate the NetNode and FigNode objects and
   * set the global next mode to ModePlace */
  // needs-more-work: should call super, reduce code volume!
  public Object makeNode() {
    Object newNode = super.makeNode();
    try {
      PseudostateKind kind = (PseudostateKind) _args.get("kind");
      ((Pseudostate)newNode).setKind(kind);
    }
    catch (PropertyVetoException pve) {
      System.out.println("PropertyVetoException in seting pseudo kind");
    }
    return newNode;
  }

} /* end class ActionCreatePseudostate */
