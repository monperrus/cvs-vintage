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

// File: FigSeqObject.java
// Original Author: agauthie@ics.uci.edu
// $Id: FigSeqObject.java,v 1.1 2000/09/29 16:02:26 boger Exp $

package org.argouml.uml.diagram.sequence.ui;

import java.awt.*;
import java.util.*;
import java.util.Enumeration;
import java.beans.*;
import java.awt.event.*;
import javax.swing.*;

import ru.novosoft.uml.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.behavior.collaborations.*;
import ru.novosoft.uml.behavior.common_behavior.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;

import org.tigris.gef.presentation.*;
import org.tigris.gef.graph.*;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.Globals;


import org.argouml.kernel.*;
import org.argouml.ui.*;
import org.argouml.uml.generator.*;
import org.argouml.uml.diagram.ui.*;

/** Class to display graphics for a UML sequence in a diagram. */

public class FigSeqObject extends FigNodeModelElement
  implements MouseMotionListener, MElementListener{

  ////////////////////////////////////////////////////////////////
  // constants
  ////////////////////////////////////////////////////////////////
  // instance variables

  FigRect _bigPort;
  FigRect _cover, _lifeline, fr;
  FigDynPort _port; 
  public Vector _ports; 
  FigActivation _activation;
  public Vector _activations;
  boolean _created = false;
  boolean _terminated = false;
  int _createHeight = 0;
  int _terminateHeight = 0;
  int _terminatePortsSize = 0;
  public Vector _dynVector;  
  // add other Figs here aes needed
    //
  FigLine _terminateLine1;
  FigLine _terminateLine2;


  ////////////////////////////////////////////////////////////////
  // constructors

  public FigSeqObject() {
    _bigPort = new FigRect(10, 10, 90, 50, Color.cyan, Color.cyan);
    _cover = new FigRect(10, 10, 90, 50, Color.black, Color.white);
    _lifeline = new FigRect(10, 10, 10, 40, Color.black, Color.white);

    //AK object's termination symbol
    _terminateLine1 = new FigLine(10,49,19,49, Color.black);
    _terminateLine2 = new FigLine(19,49,10,49, Color.black);


    _name.setLineWidth(0);
    _name.setFilled(false);
    _name.setUnderline(true);
    Dimension nameMin = _name.getMinimumSize();
    _name.setBounds(10, 10, 90, nameMin.height);

    _ports = new Vector(); 
    _activations = new Vector(); 
    _dynObjects = ""; 
    _dynVector = new Vector(); 

    // add Figs to the FigNode in back-to-front order
    addFig(_bigPort);
    addFig(_cover);
    addFig(_name);
    addFig(_lifeline);

    // add terminate symbol
    addFig(_terminateLine1);
    addFig(_terminateLine2);

    Rectangle r = getBounds();

    setBounds(r.x, r.y, r.width, r.height, 0);
  }

  public FigSeqObject(GraphModel gm, Object node) {
    this();
    setOwner(node);
  }


 // AK
  public void terminateSymbolSetBounds( int x1, int y1, int x2, int y2) {
    _terminateLine1.setX1( x1);
    _terminateLine1.setY1( y1);
    _terminateLine1.setX2( x2);
    _terminateLine1.setY2( y2);

    _terminateLine2.setX1( x2);
    _terminateLine2.setY1( y1);
    _terminateLine2.setX2( x1);
    _terminateLine2.setY2( y2);
  }








  /*
  public void addFigTerminate() {
    Vector figs = getFigs();
    if ( ! figs.contains( _terminateLine1 ) ) {
      Rectangle rect = _lifeline.getBounds();
      int x = new Double(rect.getX()).intValue();
      int y = new Double(rect.getY()).intValue();
      int w = new Double(rect.getWidth()).intValue();
      int h = new Double(rect.getHeight()).intValue();
     _terminateLine1.setX1(x);
     _terminateLine1.setY1(y+h);
     _terminateLine1.setX2(x+w);
     _terminateLine1.setY2(y+h+w*2);

     addFig( _terminateLine1);

     _terminateLine2.setX1(x+w);
     _terminateLine2.setY1(y+h);
     _terminateLine2.setX2(x);
     _terminateLine2.setY2(y+h+w*2);

      addFig( _terminateLine2);
    }
  }

  public void removeFigTerminate() {
    Vector figs = getFigs();
    if (  figs.contains( _terminateLine1 ) ) {
      figs.removeElement( _terminateLine1);
      figs.removeElement( _terminateLine2);
    }
  }
  */

  public String placeString() { return "new MObject"; }

  public Object clone() {
    FigSeqObject figClone = (FigSeqObject) super.clone();
    Vector v = figClone.getFigs();
    figClone._bigPort = (FigRect) v.elementAt(0);
    figClone._cover = (FigRect) v.elementAt(1);
    figClone._lifeline = (FigRect) v.elementAt(2);
    figClone._name = (FigText) v.elementAt(3);

    // AK clone terminate symbol
    figClone._terminateLine1 = (FigLine) v.elementAt(4);
    figClone._terminateLine2 = (FigLine) v.elementAt(5);


    Enumeration e1 = _ports.elements();
    int i=0;
    while (e1.hasMoreElements()) { 
      fr = (FigDynPort) e1.nextElement(); 
      //  AK index:4->6 
      figClone.fr = (FigDynPort) v.elementAt(i+6); 
      i++; 
    } 
    Enumeration e2 = _activations.elements(); 
    int j=0; 
    while (e2.hasMoreElements()) { 
      _activation = (FigActivation) e2.nextElement(); 
      // AK index 4->6 
      figClone._activation = (FigActivation) v.elementAt(j+i+6); 
      j++; 
    } 
    return figClone; 
  } 


  ////////////////////////////////////////////////////////////////
  // Fig accessors

  public void parseDynObjects(String dynobjs) { 
    _dynObjects = dynobjs; 
 
    StringTokenizer st1 = new StringTokenizer(dynobjs, ",[]"); 
    while (st1.hasMoreTokens()) { 
      String element = st1.nextToken().trim(); 
      _dynVector.addElement(element); 
    } 
    int count = 0; 
    StringTokenizer st = new StringTokenizer(dynobjs, ",|[]"); 
    while (st.hasMoreTokens()) { 
      String next = st.nextToken().trim(); 
      if (next.equals("a")) { 
        FigActivation fa = new FigActivation(10, 10, 21, 10, 0, 0); 
        addFig(fa); 
        _activations.addElement(fa); 
        bindPort(getOwner(), fa); 
 
        String fromStr = st.nextToken(); 
        String toStr = st.nextToken(); 
        String begStr = st.nextToken(); 
        String endStr = st.nextToken(); 
 
        Integer fromInt = new Integer(fromStr); 
        int from = fromInt.intValue(); 
        fa.setFromPosition(from); 
 
        Integer toInt = new Integer(toStr); 
        int to = toInt.intValue(); 
        fa.setToPosition(to); 
 
        if (begStr.equals("true")) { fa.setFromTheBeg(true); } 
        else fa.setFromTheBeg(false); 
        if (endStr.equals("true")) { fa.setEnd(true); } 
        else fa.setEnd(false); 
 
        fa.setDynVectorPos(count); 
        count++; 
      } 
      if (next.equals("b")) { 
        FigDynPort fsp = new FigDynPort(10, 10, 21, 1, 0); 
        addFig(fsp); 
        _ports.addElement(fsp); 
        bindPort(getOwner(), fsp); 
 
        String posStr = st.nextToken(); 
        Integer posInt = new Integer(posStr); 
        int pos = posInt.intValue(); 
        fsp.setPosition(pos); 
 
        fsp.setDynVectorPos(count); 
        count++; 
      } 
    } 
  }     

  public void setOwner(Object node) {
    super.setOwner(node);
    Object onlyPort = node;
    bindPort(onlyPort, _lifeline);
  }

  public Dimension getMinimumSize() {
    Dimension bigPortMin = _bigPort.getMinimumSize();
    Dimension coverMin = _cover.getMinimumSize();
    Dimension nameMin = _name.getMinimumSize();

    int h = Math.max(bigPortMin.height, Math.max(coverMin.height, nameMin.height));
    int w = Math.max(bigPortMin.width, Math.max(coverMin.width, nameMin.width));
    return new Dimension(w, h);
  }

  /* Override setBounds to keep shapes looking right */
  public void setBounds(int x, int y, int w, int h, int edges) {
    if (_name == null) return;

    //This is the first line for getting the _name-rect in front of the others
    // Layer lay = Globals.curEditor().getLayerManager().getActiveLayer();

    Rectangle oldBounds = getBounds();

    Dimension nameMin = _name.getMinimumSize();

    int mainX = 0; int mainY = 0; int mainW = 0; int mainH = 0;
    int lifeX = 0; int lifeY = 0; int lifeW = 0; int lifeH = 0;
    int portX = 0; int portY = 0; int portW = 0; int portH = 0;

    //System.out.println("terminateHeight: " + _terminateHeight);


    if (_created && _terminated) {
      mainX = x;        mainY = y+40*(_createHeight+1)+10;
      mainW = nameMin.width+20;     mainH = nameMin.height+10;
      lifeX = x+nameMin.width/2+6;    lifeY = y+nameMin.height+40*(_createHeight+1)+20;
      lifeW = 10;       lifeH = 25+40*(_terminateHeight- _createHeight-1);
      portX = x;        portY = y+nameMin.height+45;
      portW = 10;       portH = 1;

    }

    if (_created && (!(_terminated))) {
      	mainX = x; 				mainY = y+40*(_createHeight+1)+10;
      	mainW = nameMin.width+20; 		mainH = nameMin.height+10;
      	lifeX = x+nameMin.width/2+6;		lifeY = y+nameMin.height+40*(_createHeight+1)+20;
      	lifeW = 10;				lifeH = 30+40*(edges- _createHeight-1);
      	portX = x;				portY = y+nameMin.height+45;
      	portW = 10;				portH = 1;



    }

    if ((!(_created)) && _terminated) {
      	mainX = x; 				mainY = y;
      	mainW = nameMin.width+20; 		mainH = nameMin.height+10;
      	lifeX = x+nameMin.width/2+6;		lifeY = y+nameMin.height+10;
      	lifeW = 10;				lifeH = 40*_terminateHeight+35;
      	portX = lifeX-3;			portY = y+nameMin.height+45;
      	portW = 21;				portH = 1;


    }
    if ((!(_created)) && (!(_terminated))) {
      	mainX = x; 				mainY = y;
      	mainW = nameMin.width+20; 		mainH = nameMin.height+10;
      	lifeX = x+nameMin.width/2+6;		lifeY = y+nameMin.height+10;
      	lifeW = 10;				lifeH = 40*edges+40;
      	portX = lifeX-3;		 	portY = y+nameMin.height+45;
      	portW = 21;				portH = 1;


    }
    int activX = lifeX-6;	int activY = 0;
    int activW = 21;		int activH = 0;

    _bigPort.setBounds(mainX, mainY, mainW, mainH);
    _cover.setBounds(mainX, mainY, mainW, mainH);
     _name.setBounds(mainX, mainY, mainW, mainH);
    _lifeline.setBounds(lifeX, lifeY, lifeW, lifeH);


    // AK
    int termX1=lifeX;
    int termY1=lifeY+lifeH-1;
    int termX2=lifeX+lifeW-1;
    int termY2=termY1;

    if  ( _terminated ) {
      // make terminate symbol visible
      termX1=lifeX-lifeW;
      termY1=lifeY+lifeH-1;
      termX2=lifeX+2*lifeW;
      termY2=lifeY+lifeH+lifeW*2;
    }
    terminateSymbolSetBounds( termX1, termY1, termX2, termY2);



    Enumeration e = _ports.elements(); 
    while (e.hasMoreElements()) { 
      FigDynPort r = (FigDynPort) e.nextElement(); 
      int number = r.getPosition(); 
      if (_created) { 
        if (number == _createHeight) { 
         r.setBounds(mainX, mainY, mainW, mainH);
          //r.setBounds(mainX, mainY+ mainH/2, 1, 1);

	// The second line for getting teh_name-rect in front of the others 
	 // lay.bringToFront(_name);
        } else r.setBounds(portX+((nameMin.width)/2), portY+40*number, 21, portH);

      } 
      if (!(_created)) { 
        r.setBounds(portX-3, portY+40*number, 21, portH); 
      } 
    } 

    /*
    Enumeration e = _ports.elements();
    while (e.hasMoreElements()) {
      FigDynPort r = (FigDynPort) e.nextElement();
      int number = r.getPosition();
      r.setBounds(portX-3, portY+40*number, 21, portH);
    }
    */
    int actSize = _activations.size();
    for (int i=0; i<actSize; i++) {
      FigActivation figAct = (FigActivation) _activations.elementAt(i);
      int from = figAct.getFromPosition();
      int to = figAct.getToPosition();
      if (i == 0) {
        if (figAct.isFromTheBeg()) {
          if (_created) {
            activY = lifeY;
            activH = 40*(to-_createHeight)-15;
          } else {
            activY = lifeY;
            activH = 40*(to)+35;
          }
        } else {
          if (_created) {
            activY = lifeY;
            activH = 40*(to-_createHeight)-15;
          } else {
            activY = portY+40*from;
            activH = 40*(to-from);
          }
          if ((to-from) == 0 && (!(figAct.isEnd()))) {
            activH = 10;
          }
        }
        figAct.setBounds(activX, activY, activW, activH);
      } else {
        activY = portY+40*from;
        activH = 40*(to-from);
        if ((to-from) == 0 && (!(figAct.isEnd()))) {
          activH = 10;
        }
        figAct.setBounds(activX, activY, activW, activH);
      }
    }


    //_bigPort.setBounds(x+1, y+1, w-2, h-2);
    _x = x; _y = y; _w = w; _h = h;

    firePropChange("bounds", oldBounds, getBounds());
    calcBounds(); //_x = x; _y = y; _w = w; _h = h;
    updateEdges();

    // AK lifeline test
//    Rectangle r= _lifeline.getBounds();
 //   if (_terminated) System.out.println("Lifeline: x,y,w,h = " +r.getX()+ ","+r.getY()+","+r.getWidth()+","+r.getHeight() );
  }

  ////////////////////////////////////////////////////////////////
  // event handlers

  protected void textEdited(FigText ft) throws PropertyVetoException {
	  // super.textEdited(ft);
    MObject obj = (MObject) getOwner();
    if (ft == _name) {
       String s = ft.getText();

      // needs more Work:
      ParserDisplay.SINGLETON.parseObject(obj, s);
    }
  }

  protected void modelChanged() {
   // System.out.println("FigSeqObject.modelCHanged()");
    super.modelChanged();
    MObject obj = (MObject) getOwner();
    if (obj == null) return;
    String nameStr = GeneratorDisplay.Generate(obj.getName()).trim();
	String baseString = "";
	Vector bases = new Vector(obj.getClassifiers());
	if (bases.size() == 1)
		baseString = ((MClassifier)bases.elementAt(0)).getName();
	else if (bases.size() > 1)
		baseString = "(multiple)";

    if (_readyToEdit) {
      if( nameStr == "" && baseString == "")
	_name.setText("");
      else
	_name.setText(nameStr.trim() + " : " + baseString);
    }
    setEnclosingFig(this);
  }

  /** In this method not the enclosing fig
   *    will be set but all figs will be moved
   *    into the right place */

  public void setEnclosingFig(Fig encloser) {
  //  System.out.println("FigSeqObject.setEnclosingFig");
    Vector contents = getContents();
    int size = contents.size();
    int countEdges = edgesCount(contents);
    if (contents != null && size > 0) {
      int gap = calculateGap(contents, size);

      for (int i=0; i<size; i++) {
        if (contents.elementAt(i) instanceof FigSeqObject) {
          FigSeqObject obj = (FigSeqObject) contents.elementAt(i);
          obj.moveIntoPosition(contents, countEdges,gap, i);
        }
      }
      int countPorts = 0;
      for (int i=0; i<size; i++) {
        if (contents.elementAt(i) instanceof FigSeqLink) {
          FigSeqLink fsl = (FigSeqLink) contents.elementAt(i);
          fsl.moveIntoPosition(contents,countEdges, countPorts);
          countPorts++;
        }
      }
      int gap2 = calculateGap(contents, size);
      for (int i=0; i<size; i++) {
        if (contents.elementAt(i) instanceof FigSeqObject) {
          FigSeqObject obj = (FigSeqObject) contents.elementAt(i);
          obj.moveIntoPosition(contents, countEdges, gap2, i);
        }
      }
    }
  }

  /** Sets the port (some object in an underlying model) for Fig f.  f
   *  must already be contained in the FigNode. f will now represent
   *  the given port. */
  public void bindPort(Object port, Fig f) {
    Fig oldPortFig = getPortFig(port);
    f.setOwner(port);
  }

  /** This method is called, when the FigSeqObject is
   *    moving around. Changes the position of the
   *    FigSeqObjects. */
  public void changePosition(Vector contents) {
    int size = contents.size();
    for (int k=0; k<size; k++) {
      if (contents.elementAt(k) instanceof FigSeqObject) {
        FigSeqObject figure = (FigSeqObject) contents.elementAt(k);
        if (figure != this) {
          Rectangle rect = figure.getBounds();
          if (((this.getBounds()).x + (this.getBounds()).width) > (rect.x + rect.width)) {
            int indexFigure = contents.indexOf(figure);
            int indexThis = contents.indexOf(this);
            if (indexFigure>indexThis) {
              contents.setElementAt(this, indexFigure);
              contents.setElementAt(figure, indexThis);
            }
          }
          if (((this.getBounds()).x) < (rect.x)) {
            int indexFigure = contents.indexOf(figure);
            int indexThis = contents.indexOf(this);
            if (indexFigure<indexThis) {
              contents.setElementAt(this, indexFigure);
              contents.setElementAt(figure, indexThis);
            }
          }
        }
      }
    }
  }

  /** Moves the FigSeqObject into the right place in the diagram */
  public void moveIntoPosition(Vector contents,int edges, int gap, int position) {
   // System.out.println("FigSeqObject.moveIntoPossition()");
    FigSeqObject objWidth = null;
    int globalWidth = 10;
    Dimension nameMin = _name.getMinimumSize();
    for (int j=0; j<position; j++) {
      if (contents.elementAt(j) instanceof FigSeqObject) {
        objWidth = (FigSeqObject) contents.elementAt(j);
        int w = objWidth.getBounds().width;
        globalWidth = globalWidth + w + 60 + gap;
      }
    }
    startTrans();
    setBounds(globalWidth, 10, nameMin.width, nameMin.height, edges);
    endTrans();
  }

  /** Count the edges that are in this diagram */
  public int edgesCount(Vector contents) {
    int size = contents.size();
    int countEdges = 0;
    if (contents != null && size > 0) {
      for (int i=0; i<size; i++) {
        if (contents.elementAt(i) instanceof FigSeqLink) {
          countEdges++;
        }
      }
    }
    return countEdges;
  }

  /** If the FigSeqLink linkFig is connected to a create-action
   *    this FigSeqObject will be updated with the right values */
  public void setForCreate(FigSeqLink linkFig, String connectTo, boolean isCreate) {

    Vector contents = getContents(); 
    int portNumber = linkFig.getPortNumber(contents); 
    FigDynPort fsp = (FigDynPort) linkFig.getSourcePortFig();       
    if (connectTo == "Dest") { 
      fsp = (FigDynPort) linkFig.getDestPortFig(); 
    } 
 
    int firstPos = 10000; 
    Vector edges = getFigEdges(); 
    for (int i=0; i<edges.size(); i++) { 
      FigSeqLink fsl = (FigSeqLink) edges.elementAt(i); 
      int fslNumber = fsl.getPortNumber(contents); 
      if (fslNumber < firstPos) { firstPos = fslNumber; } 
    }   
    if (isCreate == true) { 
      if (firstPos == portNumber) { 
        _created = isCreate; 
        _createHeight = firstPos;  
      } 
      else { 
        linkFig.setDefaultAction(); 
      } 
    } 
    else if (isCreate == false) { 
      if (firstPos == portNumber) { 
        _created = isCreate; 
        _createHeight = 0; 
      } 
    } 
  } 


  /** If the FigSeqLink linkFig is connected to a destroy-action
   *    this FigSeqObject will be updated with the right values */
  public void setForDestroy(FigSeqLink linkFig, String connectTo, boolean isTerminate) {

    //System.out.println("SetForDestroy()");
    Vector contents = getContents();
    int portNumber = linkFig.getPortNumber(contents);
    //boolean canDestroy1 = canDo(false, linkFig, portNumber);
    //boolean canDestroy2 = canDo(true, linkFig, portNumber);

    FigDynPort fsp = (FigDynPort) linkFig.getSourcePortFig();       
    if (connectTo == "Dest") fsp = (FigDynPort) linkFig.getDestPortFig(); 
    int lastPos = 0; 
    Vector edges = getFigEdges();
    //System.out.println("edges:"+ edges.size());
    for (int i=0; i<edges.size(); i++) { 
      FigSeqLink fsl = (FigSeqLink) edges.elementAt(i); 
      int fslNumber = fsl.getPortNumber(contents);
      //System.out.println("fslNumber:"+fslNumber);
      if (fslNumber > lastPos) { lastPos = fslNumber; } 
    }   
    if (isTerminate == true) {
       //System.out.println("SetForDestroy().isTerminat=true");
      if (lastPos == portNumber) {
         //System.out.println("SetForDestroy():_terminateHeight=" + lastPos);
        _terminated = isTerminate; 
        _terminateHeight = lastPos;  
      } 
      else { 
        linkFig.setDefaultAction(); 
      } 
      //addFigTerminate(); 
    } 
    else if (isTerminate == false) { 
      if (lastPos == portNumber) { 
        _terminated = isTerminate; 
        _terminateHeight = 0; 
      } 
      //removeFigTerminate(); 
    } 
  } 

  /** If the connected action of the FigSeqLink figLink is
   *    a return-action, the corresponding FigActivation will
   *    be cut in two FigActivations */
  public void breakActivation(FigSeqLink figLink, Vector contents) {
    int size = contents.size(); 
    int portNumber = figLink.getPortNumber(contents); 
    int edges = edgesCount(contents); 
 
    for (int i=0; i<_activations.size(); i++) { 
      FigActivation figAct = (FigActivation) _activations.elementAt(i); 
      if (figAct.getFromPosition() <= portNumber && figAct.getToPosition() > portNumber) { 
        Vector figEdges = getFigEdges(); 
        Vector _nearest = nearestLink(figEdges, portNumber, contents); 
        int nearest = ((Integer)_nearest.elementAt(0)).intValue(); 
        int high = ((Integer)_nearest.elementAt(1)).intValue(); 
 
        if (nearest > portNumber) { 
          //FigActivation newAct = new FigActivation(0, 0, 21, 40, nearest, high); 
          FigActivation newAct = new FigActivation(0, 0, 21, 40, nearest, figAct.getToPosition()); 
          _activations.addElement(newAct); 
          String dynStr = "a|"+newAct.getFromPosition()+"|"+newAct.getToPosition()+"|"+newAct.isFromTheBeg()+"|"+newAct.isEnd(); 
          _dynVector.addElement(dynStr); 
          _dynObjects = _dynVector.toString(); 
          newAct.setDynVectorPos(_dynVector.indexOf(dynStr)); 
          addFig(newAct); 
          bindPort(getOwner(), newAct); 
        } 
        figAct.setToPosition(portNumber); 
        figAct.setEnd(true); 
      } 
      else if (figAct.getFromPosition() <= portNumber && figAct.getToPosition() == portNumber) { 
        figAct.setEnd(true); 
      } 
//      else if (figAct.getFromPosition() == portNumber) { 
//        figLink.setDefaultAction(); 
//      } 
      int dynPos = figAct.getDynVectorPos(); 
      _dynVector.removeElementAt(dynPos); 
      String newDynStr = "a|"+figAct.getFromPosition()+"|"+figAct.getToPosition()+"|"+figAct.isFromTheBeg()+"|"+figAct.isEnd(); 
      _dynVector.insertElementAt(newDynStr, dynPos); 
      _dynObjects = _dynVector.toString();               
    } 
  }

  /** This method decides, if two FigActivations can be
   *    replaced with the first of this two FigActivations with
   *    the added lenght of both FigActivations */
  public void concatActivation(FigSeqLink figLink, Vector contents) {
    int portNumber = figLink.getPortNumber(contents); 
    boolean canConcat = false; 
    FigActivation figConcat = canConcat(portNumber); 
    if (figConcat != null) canConcat = true; 
 
    if (canConcat) { 
      figConcat.setEnd(false); 
      int max = 10000; 
      FigActivation nextAct = null; 
      for (int i=0; i<_activations.size(); i++) { 
        FigActivation fa = (FigActivation) _activations.elementAt(i); 
        if (fa.getFromPosition() > portNumber && fa.getFromPosition() < max)  { 
          nextAct = fa; 
          max = fa.getFromPosition(); 
        } 
      } 
      if (nextAct != null) { 
        figConcat.setToPosition(nextAct.getToPosition()); 
        _activations.removeElement(nextAct); 
        int dynPos = nextAct.getDynVectorPos(); 
        _dynVector.removeElementAt(dynPos); 
        Vector dynFigs = getFigs(); 
        for (int i=0; i<dynFigs.size(); i++) { 
          Fig df = (Fig) dynFigs.elementAt(i); 
          if (df instanceof FigActivation) { 
            FigActivation dfa = (FigActivation) df; 
            if (dfa.getDynVectorPos() > dynPos) dfa.setDynVectorPos(dfa.getDynVectorPos()-1);  
          } 
          else if (df instanceof FigDynPort) { 
            FigDynPort dfsp = (FigDynPort) df; 
            if (dfsp.getDynVectorPos() > dynPos) dfsp.setDynVectorPos(dfsp.getDynVectorPos()-1);  
          } 
        } 
        _dynObjects = _dynVector.toString();  
        removeFig(nextAct); 
        nextAct.delete(); 
      } 
      else { 
        // do nothing 
      } 
    } 
  }

  /** Get the Vector of all figures, that are shown in
   *    the diagram, is important because in sequence-
   *    diagrams often you have to update all figures */
  public Vector getContents() {
    Editor _editor = Globals.curEditor();
    Layer lay = _editor.getLayerManager().getActiveLayer();
    Vector contents = lay.getContents();
    return contents;
  }

  /** If the FigSeqLink fsl has a return- or destroy-action,
   *    this method decides, if this link can have this action
   *    --> returns true */
  public boolean canDo(boolean side, FigSeqLink fsl, int portNumber) {
    FigSeqObject fso = (FigSeqObject) fsl.getDestFigNode();
    if (side) fso = (FigSeqObject) fsl.getSourceFigNode();
    boolean _canDo = true;
    for (int j=0; j<fso._activations.size(); j++) {
      FigActivation fa = (FigActivation) fso._activations.elementAt(j);
      if (fa.getFromPosition() == portNumber) _canDo = false;
    }

    if (!(side)) {
      FigSeqObject dest = (FigSeqObject) fsl.getSourceFigNode();
      for (int i=0; i<dest._activations.size(); i++) {
        FigActivation fa = (FigActivation) dest._activations.elementAt(i);
        if (fa.getFromPosition() == portNumber) _canDo = false;
      }
    }

    return _canDo;
  }

  /** Returns true, if  two FigActivations at the given
   *    port-number can be replaced by one FigActivation */
  public FigActivation canConcat(int portNumber) {
    FigActivation _figConcat = null;
    for (int j=0; j<_activations.size(); j++) {
      FigActivation fa = (FigActivation) _activations.elementAt(j);
      if (fa.getToPosition() == portNumber && fa.isEnd()) {
        _figConcat = fa;
      }
    }
    return _figConcat;
  }


  /** Returns two Integers, one is the port-number of the FigSeqLink
   *    which is next to the given portNumber. The second Integer is
   *    the highest port-number */
  public Vector nearestLink(Vector edges, int portNumber, Vector contents) {
    Vector _nearest = new Vector();
    Enumeration e = edges.elements();
    int max = 100000;
    int nearest = 0;
    int high = 0;
    while (e.hasMoreElements()) {
      FigSeqLink  fsl = (FigSeqLink) e.nextElement();
      int pos = fsl.getPortNumber(contents);
      if (pos < max && pos > portNumber) {
        max = pos;
        nearest = max;
      }
      if (pos > high) high = pos;
    }
    _nearest.addElement(new Integer(nearest));
    _nearest.addElement(new Integer(high));
    return _nearest;
  }

  /** Calculate the gap that will be set between
   *    two FigSeqObjects */
  public int calculateGap(Vector contents, int size) {
    int stimulusWidth = 0;
    int objWidth = 100000;
    int gap = 0;
    for (int i=0; i<size; i++) {
      if (contents.elementAt(i) instanceof FigSeqStimulus) {
        FigSeqStimulus fss = (FigSeqStimulus) contents.elementAt(i);
        if (fss.getBounds().width > stimulusWidth) {
          stimulusWidth = fss.getBounds().width;
        }
      }
      if (contents.elementAt(i) instanceof FigSeqObject) {
        FigSeqObject fso = (FigSeqObject) contents.elementAt(i);
        if (fso.getBounds().width < objWidth) {
          objWidth = fso.getBounds().width;
        }
      }
    }
    if (stimulusWidth > objWidth) {
      gap = stimulusWidth - objWidth;
    }
    return gap;
  }

///////////////////////////////////////////////////////////////////////////////
// MouseMotionListener-implementation

  public void mouseDragged(MouseEvent me) {
    Vector contents = getContents();
    int size = contents.size();
    if (contents != null && size > 0) {
      for (int i=0; i<size; i++) {
        changePosition(contents);
      }
    }
  }

  public void mouseMoved(MouseEvent me) {
  }

///////////////////////////////////////////////////////////////////////////////
// MElementListener-implementation

  public void removed(MElementEvent mee) {

/*
    Vector contents = getContents();
    int content_size = contents.size();

    Vector fsl_delete = new Vector();
    for (int i=0; i<content_size; i++) {
      if (contents.elementAt(i) instanceof FigSeqLink) {
        FigSeqLink figLink = (FigSeqLink) contents.elementAt(i);
        if ((figLink.getDestFigNode() == this) || (figLink.getSourceFigNode() == this)) {
          fsl_delete.addElement(figLink);
        }
      }
    }
System.out.println("size = "+fsl_delete.size());
    for (int i=0; i<fsl_delete.size(); i++) {
      FigSeqLink fsl = (FigSeqLink) fsl_delete.elementAt(i);
      fsl.delete();
System.out.println("i = "+i+"    deleted");
    }
System.out.println("fertig");
    this.delete();

    for (int i=0; i<content_size; i++) {
      if (contents.elementAt(i) instanceof FigSeqObject) {
        FigSeqObject fso = (FigSeqObject) contents.elementAt(i);
        fso.setEnclosingFig(fso);
      }
    }
*/
     Vector edges = getFigEdges();
     if (edges == null || edges.size() == 0) {
       this.delete();
     }
     else System.out.println("Object not deleted, there are some Links to or from this object left!");
  }


} /* end class FigSeqObject */
