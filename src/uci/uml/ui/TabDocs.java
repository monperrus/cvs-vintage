package uci.uml.ui;

//import jargo.kernel.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;
//import com.sun.java.swing.border.*;

import uci.util.*;
import uci.gef.*;


public class TabDocs extends TabText {
  ////////////////////////////////////////////////////////////////
  // constructor
  public TabDocs() {
    setTitle("Javadocs");
    System.out.println("making TabDocs");
  }

  ////////////////////////////////////////////////////////////////
  // accessors
  protected String genText() {
    //System.out.println("Docstab getting docs for " + _target);
    Object modelObject = _target;
    if (_target instanceof FigNode)
      modelObject = ((FigNode)_target).getOwner();
    if (_target instanceof FigEdge)
      modelObject = ((FigEdge)_target).getOwner();
    if (modelObject == null) return null;
    return DocumentationManager.getDocs(modelObject);
  }

  protected void parseText(String s) {
    //System.out.println("Docstab   setting docs for "+ _target);
    Object modelObject = _target;
    if (_target instanceof FigNode)
      modelObject = ((FigNode)_target).getOwner();
    if (_target instanceof FigEdge)
      modelObject = ((FigEdge)_target).getOwner();
    if (modelObject == null) return;
    DocumentationManager.setDocs(modelObject, s);
  }

  
} /* end class TabDocs */
