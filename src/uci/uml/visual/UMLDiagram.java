// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.




package uci.uml.visual;

import java.util.*;
import java.awt.*;
import java.beans.*;

import uci.gef.*;
import uci.graph.*;
import uci.ui.*;
import uci.uml.Foundation.Core.*;


public class UMLDiagram extends Diagram {

  ////////////////////////////////////////////////////////////////
  // instance variables
  protected Namespace _namespace;
  protected DiagramInfo _diagramName = new DiagramInfo(this);


  ////////////////////////////////////////////////////////////////
  // constructors

  public UMLDiagram() { }

  public UMLDiagram(Namespace ns) {
    _namespace = ns;
  }

  public UMLDiagram(String diagramName, Namespace ns) {
    try { setName(diagramName); }
    catch (PropertyVetoException pve) { }
    _namespace = ns;
  }

  public void initialize(Object owner) {
    super.initialize(owner);
    if (owner instanceof Namespace) setNamespace((Namespace) owner);
    else System.out.println("unknown object in UMLDiagram initialize:"
			    + owner);
  }


  ////////////////////////////////////////////////////////////////
  // accessors

  public Namespace getNamespace() { return _namespace; }
  public void setNamespace(Namespace m) { _namespace = m; }

  public String getClassAndModelID() {
    String s = super.getClassAndModelID();
    if (getNamespace() == null) return s;
    return s + "|" + getNamespace().getId();
  }


  public void setName(String n) throws PropertyVetoException {
    super.setName(n);
    _diagramName.updateName();
  }

  static final long serialVersionUID = -401219134410459387L;

} /* end class UMLDiagram */
