// $Id: DeploymentDiagramGraphModel.java,v 1.34 2004/08/03 01:25:45 bobtarling Exp $
// Copyright (c) 2003 The Regents of the University of California. All
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

package org.argouml.uml.diagram.deployment;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.*;
import org.argouml.model.ModelFacade;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.argouml.uml.diagram.static_structure.ui.CommentEdge;

import org.argouml.model.uml.foundation.core.CoreHelper;
import org.argouml.model.uml.behavioralelements.commonbehavior.CommonBehaviorHelper;
import org.apache.log4j.Logger;

public class DeploymentDiagramGraphModel extends UMLMutableGraphSupport
    implements VetoableChangeListener 
{
    private static final Logger LOG =
	    Logger.getLogger(DeploymentDiagramGraphModel.class);

    /** The "home" UML model of this diagram, not all ModelElements in this
     *  graph are in the home model, but if they are added and don't
     *  already have a model, they are placed in the "home model".
     *  Also, elements from other models will have their FigNodes add a
     *  line to say what their model is. */

    protected Object _model;

    ////////////////////////////////////////////////////////////////
    // accessors

    /** get the homemodel. */
    public Object getNamespace() { return _model; }

    /** set the homemodel. */
    public void setNamespace(Object namespace) {
        
        if(!ModelFacade.isANamespace(namespace))
            throw new IllegalArgumentException();
	_model = namespace;
    }

    ////////////////////////////////////////////////////////////////
    // GraphModel implementation


    /** Return all ports on node or edge */
    public Vector getPorts(Object nodeOrEdge) {
	Vector res = new Vector();  //wasteful!
	if (ModelFacade.isANode(nodeOrEdge)) res.addElement(nodeOrEdge);
	if (ModelFacade.isANodeInstance(nodeOrEdge))
	    res.addElement(nodeOrEdge);
	if (ModelFacade.isAComponent(nodeOrEdge)) res.addElement(nodeOrEdge);
	if (ModelFacade.isAComponentInstance(nodeOrEdge)) 
	    res.addElement(nodeOrEdge);
	if (ModelFacade.isAClass(nodeOrEdge)) res.addElement(nodeOrEdge);    
	if (ModelFacade.isAInterface(nodeOrEdge)) res.addElement(nodeOrEdge);
	if (ModelFacade.isAObject(nodeOrEdge)) res.addElement(nodeOrEdge);
	return res;
    }

    /** Return the node or edge that owns the given port */
    public Object getOwner(Object port) {
	return port;
    }


    /** Return all edges going to given port */
    public Vector getInEdges(Object port) {
	Vector res = new Vector(); //wasteful!
	if (ModelFacade.isANode(port)) {
	    Collection ends = ModelFacade.getAssociationEnds(port);
	    if (ends == null) return res; // empty Vector
	    Iterator iter = ends.iterator();
	    while (iter.hasNext()) {
		Object aec = /*(MAssociationEnd)*/ iter.next();
		res.add(ModelFacade.getAssociation(aec));
	    }
	}
	if (ModelFacade.isANodeInstance(port)) {
	    Object noi = /*(MNodeInstance)*/ port;
	    Collection ends = ModelFacade.getLinkEnds(noi);
	    res.addAll(ends);
	}
	if (ModelFacade.isAComponent(port)) {
	    Collection ends = ModelFacade.getAssociationEnds(port);
	    if (ends == null) return res; // empty Vector
	    Iterator endEnum = ends.iterator();
	    while (endEnum.hasNext()) {
		Object aec = /*(MAssociationEnd)*/ endEnum.next();
		res.addElement(ModelFacade.getAssociation(aec));
	    }
	}
	if (ModelFacade.isAComponentInstance(port)) {
	    Object coi = /*(MComponentInstance)*/ port;
	    Collection ends = ModelFacade.getLinkEnds(coi);
	    res.addAll(ends);
	}
	if (ModelFacade.isAClass(port)) {
	    Collection ends = ModelFacade.getAssociationEnds(port);
	    if (ends == null) return res; // empty Vector 
	    Iterator endEnum = ends.iterator();
	    while (endEnum.hasNext()) {
		Object ae = /*(MAssociationEnd)*/ endEnum.next();
		res.addElement(ModelFacade.getAssociation(ae));
	    }
	}
	if (ModelFacade.isAInterface(port)) {
	    Collection ends = ModelFacade.getAssociationEnds(port);
	    if (ends == null) return res; // empty Vector 
	    Iterator endEnum = ends.iterator();
	    while (endEnum.hasNext()) {
		Object ae = /*(MAssociationEnd)*/ endEnum.next();
		res.addElement(ModelFacade.getAssociation(ae));
	    }
	}
	if (ModelFacade.isAObject(port)) {
	    Object clo = /*(MInstance)*/ port;
	    Collection ends = ModelFacade.getLinkEnds(clo);
	    res.addAll(ends);
	}


	return res;
    }

    /** Return all edges going from given port */
    public Vector getOutEdges(Object port) {
	return new Vector(); // TODO?
    }


    /** Return one end of an edge */
    public Object getSourcePort(Object edge) {
	if (ModelFacade.isARelationship(edge)) {
	    return CoreHelper.getHelper().getSource(/*(MRelationship)*/ edge);
	} else
	    if (ModelFacade.isALink(edge)) {
		return CommonBehaviorHelper.getHelper().getSource(/*(MLink)*/ edge);
	    }
    
	cat.debug("TODO getSourcePort");

	return null;
    }


    /** Return  the other end of an edge */
    public Object getDestPort(Object edge) {
	if (ModelFacade.isARelationship(edge)) {
	    return CoreHelper.getHelper().getDestination(/*(MRelationship)*/ edge);
	} else if (ModelFacade.isALink(edge)) {
	    return CommonBehaviorHelper.getHelper()
		.getDestination(/*(MLink)*/ edge);
	}
    
	cat.debug("TODO getDestPort");

	return null;
    }



    ////////////////////////////////////////////////////////////////
    // MutableGraphModel implementation

    /** Return true if the given object is a valid node in this graph */
    public boolean canAddNode(Object node) {
	if (node == null) return false;
	if (_nodes.contains(node)) return false;
	return (ModelFacade.isANode(node)) || 
	    (ModelFacade.isAComponent(node)) || 
	    (ModelFacade.isAClass(node)) || 
	    (ModelFacade.isAInterface(node)) ||
	    (ModelFacade.isAObject(node)) ||
	    (ModelFacade.isANodeInstance(node)) || 
	    (ModelFacade.isAComponentInstance(node) ||
	            (ModelFacade.isAComment(node)));
    }

    /** Return true if the given object is a valid edge in this graph */
    public boolean canAddEdge(Object edge)  {
	if (edge == null) return false;
	if (_edges.contains(edge)) return false;
	Object end0 = null, end1 = null;
	if (ModelFacade.isARelationship(edge)) {
	    end0 = CoreHelper.getHelper().getSource(/*(MRelationship)*/ edge);
	    end1 = CoreHelper.getHelper().getDestination(/*(MRelationship)*/ edge);
	}
	else if (ModelFacade.isALink(edge)) {
	    end0 = CommonBehaviorHelper.getHelper().getSource(/*(MLink)*/ edge);
	    end1 =
		CommonBehaviorHelper.getHelper().getDestination(/*(MLink)*/ edge);
	} else if (edge instanceof CommentEdge) {
	    end0 = ((CommentEdge)edge).getSource();
	    end1 = ((CommentEdge)edge).getDestination();
	}
	if (end0 == null || end1 == null) return false;
	if (!_nodes.contains(end0)) return false;
	if (!_nodes.contains(end1)) return false;
	return true;
    }

 
    /** Add the given node to the graph, if valid. */
    public void addNode(Object node) {
	cat.debug("adding class node!!");
	if (!canAddNode(node)) return;
	_nodes.addElement(node);
	// TODO: assumes public, user pref for default visibility?
	//do I have to check the namespace here? (Toby)
	if (ModelFacade.isAModelElement(node) &&
	    (ModelFacade.getNamespace(node) == null)) {
	    ModelFacade.addOwnedElement(_model, node);
	}
	fireNodeAdded(node);
    }

    /** Add the given edge to the graph, if valid. */
    public void addEdge(Object edge) {
	cat.debug("adding class edge!!!!!!");
	if (!canAddEdge(edge)) return;
	_edges.addElement(edge);
	// TODO: assumes public
	if (ModelFacade.isAModelElement(edge)) {
	    ModelFacade.addOwnedElement(_model, edge);
	}
	fireEdgeAdded(edge);
    }

    public void addNodeRelatedEdges(Object node) {
	if (ModelFacade.isAClassifier(node) ) {
	    Collection ends = ModelFacade.getAssociationEnds(node);
	    Iterator iter = ends.iterator();
	    while (iter.hasNext()) {
		Object ae = /*(MAssociationEnd)*/ iter.next();
		if (canAddEdge(ModelFacade.getAssociation(ae)))
		    addEdge(ModelFacade.getAssociation(ae));
		return;
	    }
	}
	if ( ModelFacade.isAInstance(node) ) {
	    Collection ends = ModelFacade.getLinkEnds(node);
	    Iterator iter = ends.iterator();
	    while (iter.hasNext()) {
		Object link = ModelFacade.getLink(iter.next());
		if (canAddEdge(link))
		    addEdge(link);
		return;
	    }
	}
	if ( ModelFacade.isAGeneralizableElement(node) ) {
	    Iterator iter = ModelFacade.getGeneralizations(node).iterator();
	    while (iter.hasNext()) {
		// g contains a Generalization
		Object g = iter.next();
		if (canAddEdge(g))
		    addEdge(g);
		return;
	    }
	    iter = ModelFacade.getSpecializations(node).iterator();
	    while (iter.hasNext()) {
		// s contains a specialization
		Object s = iter.next();
		if (canAddEdge(s))
		    addEdge(s);
		return;
	    }
	}
	if ( ModelFacade.isAModelElement(node) ) {
	    Vector specs =
		new Vector(ModelFacade.getClientDependencies(node));
	    specs.addAll(ModelFacade.getSupplierDependencies(node));
	    Iterator iter = specs.iterator();
	    while (iter.hasNext()) {
		Object dep = /*(MDependency)*/ iter.next();
		if (canAddEdge(dep))
		    addEdge(dep);
		return;
	    }
	}
    }


    public void vetoableChange(PropertyChangeEvent pce) {
	if ("ownedElement".equals(pce.getPropertyName())) {
	    Vector oldOwned = (Vector) pce.getOldValue();
	    Object eo = /*(MElementImport)*/ pce.getNewValue();
	    Object me = ModelFacade.getModelElement(eo);
	    if (oldOwned.contains(eo)) {
		cat.debug("model removed " + me);
		if (ModelFacade.isANode(me)) removeNode(me);
		if (ModelFacade.isANodeInstance(me)) removeNode(me);
		if (ModelFacade.isAComponent(me)) removeNode(me);
		if (ModelFacade.isAComponentInstance(me)) removeNode(me);
		if (ModelFacade.isAClass(me)) removeNode(me);
		if (ModelFacade.isAInterface(me)) removeNode(me);
		if (ModelFacade.isAObject(me)) removeNode(me);
		if (ModelFacade.isAAssociation(me)) removeEdge(me);
		if (ModelFacade.isADependency(me)) removeEdge(me);
		if (ModelFacade.isALink(me)) removeEdge(me);
	    }
	    else {
		cat.debug("model added " + me);
	    }
	}
    }

    static final long serialVersionUID = 1003748292917485298L;

} /* end class DeploymentDiagramGraphModel */