// $Id: CollabDiagramGraphModel.java,v 1.40 2004/09/28 11:12:20 bobtarling Exp $
// Copyright (c) 1996-2002 The Regents of the University of California. All
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

// File: CollabDiagramGraphModel.java
// Classes: CollabDiagramGraphModel
// Original Author: agauthie@ics.uci.edu

package org.argouml.uml.diagram.collaboration;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.argouml.model.ModelFacade;

import org.argouml.model.uml.foundation.core.CoreHelper;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

/** This class defines a bridge between the UML meta-model
 *  representation of the design and the GraphModel interface used by
 *  GEF.  This class handles only UML Use Case Digrams.  */

public class CollabDiagramGraphModel extends UMLMutableGraphSupport
    implements VetoableChangeListener 
{
    private static final Logger LOG =
        Logger.getLogger(CollabDiagramGraphModel.class);

    /** The "home" UML model of this diagram, not all ModelElements in this
     *  graph are in the home model, but if they are added and don't
     *  already have a model, they are placed in the "home model".
     *  Also, elements from other models will have their FigNodes add a
     *  line to say what their model is. */

    /** The collaboration / interaction we are diagramming */
    private Object collab;
    private Object interaction;

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @see org.argouml.uml.diagram.UMLMutableGraphSupport#getNamespace()
     */
    public Object getNamespace() { return collab; }
    
    /**
     * @param m the namespace to be set for this diagram
     */
    public void setNamespace(Object m) {
        if (!(ModelFacade.isACollaboration(m))) {
            throw new IllegalArgumentException("invalid namespace");
        }
        collab = /*(MCollaboration)*/ m;
    }


    ////////////////////////////////////////////////////////////////
    // GraphModel implementation

 
    /** Return all ports on node or edge 
     * 
     * @see org.tigris.gef.graph.GraphModel#getPorts(java.lang.Object)
     */
    public List getPorts(Object nodeOrEdge) {
	Vector res = new Vector();  //wasteful!
	if (ModelFacade.isAClassifierRole(nodeOrEdge)) 
	    res.addElement(nodeOrEdge);
	return res;
    }

    /** Return the node or edge that owns the given port 
     * 
     * @see org.tigris.gef.graph.BaseGraphModel#getOwner(java.lang.Object)
     */
    public Object getOwner(Object port) {
	return port;
    }

    /** Return all edges going to given port 
     * 
     * @see org.tigris.gef.graph.GraphModel#getInEdges(java.lang.Object)
     */
    public List getInEdges(Object port) {
	Vector res = new Vector(); //wasteful!
	if (ModelFacade.isAClassifierRole(port)) {
	    Object cr = /*(MClassifierRole)*/ port;
	    Collection ends = ModelFacade.getAssociationEnds(cr);
	    if (ends == null) return res; // empty Vector
	    Iterator iter = ends.iterator();
	    while (iter.hasNext()) {
		Object aer = /*(MAssociationEndRole)*/ iter.next();
		res.addElement(ModelFacade.getAssociation(aer));
	    }
	}
	return res;
    }

    /** Return all edges going from given port 
     * 
     * @see org.tigris.gef.graph.GraphModel#getOutEdges(java.lang.Object)
     */
    public List getOutEdges(Object port) {
	return new Vector(); // TODO:?
    }

    /** Return one end of an edge 
     * 
     * @see org.tigris.gef.graph.BaseGraphModel#getSourcePort(java.lang.Object)
     */
    public Object getSourcePort(Object edge) {
	if (ModelFacade.isARelationship(edge)) {
	    return CoreHelper.getHelper().getSource(/*(MRelationship)*/ edge);
	}
	LOG.debug("TODO: getSourcePort");
	return null;
    }

    /** Return  the other end of an edge 
     * 
     * @see org.tigris.gef.graph.BaseGraphModel#getDestPort(java.lang.Object)
     */
    public Object getDestPort(Object edge) {
	if (ModelFacade.isARelationship(edge)) {
	    return CoreHelper.getHelper().getDestination(edge);
	}
	LOG.debug("TODO: getDestPort");
	return null;
    }


    ////////////////////////////////////////////////////////////////
    // MutableGraphModel implementation

    /** Return true if the given object is a valid node in this graph 
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#canAddNode(java.lang.Object)
     */
    public boolean canAddNode(Object node) {
	if (node == null) return false;
	if (containsNode(node)) return false;
	return (ModelFacade.isAClassifierRole(node) 
            || ModelFacade.isAMessage(node));
    }

    /** Return true if the given object is a valid edge in this graph 
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#canAddEdge(java.lang.Object)
     */
    public boolean canAddEdge(Object edge)  {
	if (edge == null) return false;
	if (containsEdge(edge)) return false;
	Object end0 = null;
        Object end1 = null;
	if (ModelFacade.isAAssociationRole(edge)) {
	    Collection conns = ModelFacade.getConnections(edge);
            Iterator iter = conns.iterator();
	    if (conns.size() < 2) return false;
	    Object associationEndRole0 = iter.next();
	    Object associationEndRole1 = iter.next();
	    if (associationEndRole0 == null || associationEndRole1 == null) 
	        return false;
	    end0 = ModelFacade.getType(associationEndRole0);
	    end1 = ModelFacade.getType(associationEndRole1);
	}
	if (ModelFacade.isAGeneralization(edge)) {
	    Object gen = /*(MGeneralization)*/ edge;
	    end0 = ModelFacade.getParent(gen);
	    end1 = ModelFacade.getChild(gen);
	}
	if (ModelFacade.isADependency(edge)) {
	    Collection clients = ModelFacade.getClients(edge);
	    Collection suppliers = ModelFacade.getSuppliers(edge);
	    if (clients == null || suppliers == null) return false;
	    end0 = (clients.toArray())[0];
	    end1 = (suppliers.toArray())[0];
	}
	if (edge instanceof CommentEdge) {
	    end0 = ((CommentEdge) edge).getSource();
	    end1 = ((CommentEdge) edge).getDestination();
	}
	if (end0 == null || end1 == null) return false;
	if (!containsNode(end0)) return false;
	if (!containsNode(end1)) return false;
	return true;
    }


    /** Add the given node to the graph, if valid. 
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#addNode(java.lang.Object)
     */
    public void addNode(Object node) {
	LOG.debug("adding MClassifierRole node!!");
	if (!canAddNode(node)) return;
	getNodes().add(node);
	// TODO: assumes public, user pref for default visibility?
	if (ModelFacade.isAClassifier(node)) {
	    ModelFacade.addOwnedElement(collab, /*(MClassifier)*/ node);
	    // ((MClassifier)node).setNamespace(_collab.getNamespace());
	}
    
	fireNodeAdded(node);
    }

    /** Add the given edge to the graph, if valid. 
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#addEdge(java.lang.Object)
     */
    public void addEdge(Object edge) {
        LOG.debug("adding class edge!!!!!!");
        if (!canAddEdge(edge)) return;
        getEdges().add(edge);
        // TODO: assumes public
        if (ModelFacade.isAModelElement(edge)
	    && ModelFacade.getNamespace(edge) == null) {
            ModelFacade.addOwnedElement(collab, /*(MModelElement)*/ edge);
        }
        fireEdgeAdded(edge);
    }

    /**
     * @see org.tigris.gef.graph.MutableGraphModel#addNodeRelatedEdges(java.lang.Object)
     */
    public void addNodeRelatedEdges(Object node) {
	if ( ModelFacade.isAClassifier(node) ) {
	    Collection ends = ModelFacade.getAssociationEnds(node);
	    Iterator iter = ends.iterator();
	    while (iter.hasNext()) {
		Object ae = /*(MAssociationEndRole)*/ iter.next();
		if (canAddEdge(ModelFacade.getAssociation(ae)))
		    addEdge(ModelFacade.getAssociation(ae));
	    }
	}
	if ( ModelFacade.isAGeneralizableElement(node) ) {
	    Collection gn = ModelFacade.getGeneralizations(node);
	    Iterator iter = gn.iterator();
	    while (iter.hasNext()) {
		Object g = /*(MGeneralization)*/ iter.next();
		if (canAddEdge(g)) {
		    addEdge(g);
		    return;
		}
	    }
	    Collection sp = ModelFacade.getSpecializations(node);
	    iter = sp.iterator();
	    while (iter.hasNext()) {
		Object s = /*(MGeneralization)*/ iter.next();
		if (canAddEdge(s)) {
		    addEdge(s);
		    return;
		}
	    }
	}
	if ( ModelFacade.isAModelElement(node) ) {
	    Vector specs =
		new Vector(ModelFacade.getClientDependencies(node));
	    specs.addAll(ModelFacade.getSupplierDependencies(node));
	    Iterator iter = specs.iterator();
	    while (iter.hasNext()) {
		Object dep = /*(MDependency)*/ iter.next();
		if (canAddEdge(dep)) {
		    addEdge(dep);
		    return;
		}
	    }
	}
    }


    /** Return true if the two given ports can be connected by a
     * kind of edge to be determined by the ports. 
     * 
     * @see org.tigris.gef.graph.MutableGraphModel#canConnect(java.lang.Object, 
     * java.lang.Object)
     */
    public boolean canConnect(Object fromP, Object toP) {
	if ((ModelFacade.isAClassifierRole(fromP))
	    && (ModelFacade.isAClassifierRole(toP)))
	    return true;
	return false;
    }

    ////////////////////////////////////////////////////////////////
    // VetoableChangeListener implementation

    /**
     * @see java.beans.VetoableChangeListener#vetoableChange(java.beans.PropertyChangeEvent)
     */
    public void vetoableChange(PropertyChangeEvent pce) {
	//throws PropertyVetoException

	if ("ownedElement".equals(pce.getPropertyName())) {
	    Vector oldOwned = (Vector) pce.getOldValue();
	    Object eo = /*(MElementImport)*/ pce.getNewValue();
	    Object me = ModelFacade.getModelElement(eo);
	    if (oldOwned.contains(eo)) {
		LOG.debug("model removed " + me);
		if (ModelFacade.isAClassifier(me)) removeNode(me);
		if (ModelFacade.isAMessage(me)) removeNode(me);
		if (ModelFacade.isAAssociation(me)) removeEdge(me);
	    }
	    else {
		LOG.debug("model added " + me);
	    }
	}
    }

} /* end class CollabDiagramGraphModel */