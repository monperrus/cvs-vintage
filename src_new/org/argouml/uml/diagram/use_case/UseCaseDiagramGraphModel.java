// $Id: UseCaseDiagramGraphModel.java,v 1.20 2003/06/29 23:52:21 linus Exp $
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


// File: UseCaseDiagramGraphModel.java
// Classes: UseCaseDiagramGraphModel
// Original Author: your email address here
// $Id: UseCaseDiagramGraphModel.java,v 1.20 2003/06/29 23:52:21 linus Exp $

// 3 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// the Extend and Include relationships. JavaDoc added for clarity. Adding edge
// now correctly places dependency in the namespace of the
// model. Vetoablechange picks up all edges removed (ignored dependencies).


package org.argouml.uml.diagram.use_case;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;

import org.apache.log4j.Category;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.foundation.core.CoreHelper;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import ru.novosoft.uml.behavior.use_cases.MActor;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MInclude;
import ru.novosoft.uml.behavior.use_cases.MUseCase;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MDependency;
import ru.novosoft.uml.foundation.core.MGeneralizableElement;
import ru.novosoft.uml.foundation.core.MGeneralization;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.foundation.core.MRelationship;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;
import ru.novosoft.uml.model_management.MElementImport;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Mode;
import org.tigris.gef.base.ModeManager;

/**
 * <p>This class defines a bridge between the UML meta-model representation of
 *   the design and the GraphModel interface used by GEF.</p>
 *
 * <p>This class handles only UML Use Case Diagrams.</p>
 */

public class UseCaseDiagramGraphModel extends UMLMutableGraphSupport
    implements VetoableChangeListener {
    protected static Category cat = Category.getInstance(UseCaseDiagramGraphModel.class);

    ///////////////////////////////////////////////////////////////////////////
    //
    // instance variables
    //
    ///////////////////////////////////////////////////////////////////////////

    

    /**
     * <p>The "home" UML model of this diagram, not all ModelElements in this
     *   graph are in the home model, but if they are added and don't already
     *   have a model, they are placed in the "home model".  Also, elements
     *   from other models will have their FigNodes add a line to say what
     *   their model is.</p>  */

    protected MNamespace _model;


    ///////////////////////////////////////////////////////////////////////////
    //
    // Accessors
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * <p>Accessor to get the namespace.</p>
     *
     * @return  The namespace associated with this graph model.
     */

    public MNamespace getNamespace() {
        return _model;
    }


    /**
     * <p>Accessor to set the namespace.</p>
     *
     * <p>Clears the current listener if we have a namespace at present. Sets a
     *   new listener if we set a new namespace (i.e. m is non-null).</p>
     *
     * @param m  The namespace to use for this graph model
     */

    public void setNamespace(MNamespace m) {
        _model = m;
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    // Methods that implement the GraphModel itself
    //
    ///////////////////////////////////////////////////////////////////////////


    /**
     * <p>Return all ports on a node or edge supplied as argument.</p>
     *
     * <p>The only objects on our diagram that have any ports are use cases and
     *   actors, and they each have one - themself.</p>
     *
     * @param nodeOrEdge  A model element, for whom the list of ports is
     *                    wanted.
     *
     * @return            A vector of the ports found.
     */

    public Vector getPorts(Object nodeOrEdge) {
        Vector res = new Vector();  //wasteful!

        if (nodeOrEdge instanceof MActor) {
            res.addElement(nodeOrEdge);
        }
        else if (nodeOrEdge instanceof MUseCase) {
            res.addElement(nodeOrEdge);
        }

        return res;
    }


    /* <p>Return the node or edge that owns the given port.</p>
     *
     * <p>In our implementation the only objects with ports, use themselves as
     *   the port, so are there own owner.</p>
     *
     * @param port  The port, whose owner is wanted.
     *
     * @return      The owner of the port.
     */

    public Object getOwner(Object port) {
        return port;
    }


    /**
     * <p>Return all edges going to given port.</p>
     *
     * <p>The only objects with ports on the use case diagram are actors and
     *   use cases. In each case we find the attached association ends, and
     *   build a list of them as the incoming ports.</p>
     *
     * @param port  The port for which we want to know the incoming edges.
     *
     * @return      A vector of objects which are the incoming edges.
     */

    public Vector getInEdges(Object port) {
        Vector res = new Vector(); //wasteful!

        // The actor case

        if (port instanceof MActor) {
            MActor act  = (MActor) port;
            Vector ends = new Vector(act.getAssociationEnds());

            // If there are no ends, return the empty vector

            if (ends == null) {
                return res;
            }

            // Enumerate the elements and add to the result vector

            java.util.Enumeration endEnum = ends.elements();

            while (endEnum.hasMoreElements()) {
                MAssociationEnd ae = (MAssociationEnd) endEnum.nextElement();
                res.addElement(ae.getAssociation());
            }
        }

        // The use case

        else if (port instanceof MUseCase) {
            MUseCase use  = (MUseCase) port;
            Vector   ends = new Vector(use.getAssociationEnds());

            // If there are no ends, return the empty vector

            if (ends == null) {
                return res;
            }

            // Enumerate the elements and add to the result vector

            java.util.Enumeration endEnum = ends.elements();

            while (endEnum.hasMoreElements()) {
                MAssociationEnd ae = (MAssociationEnd) endEnum.nextElement();
                res.addElement(ae.getAssociation());
            }
        }

        // Return what we have built up

        return res;
    }


    /**
     * <p>Return all edges going from the given port.</p>
     *
     * <p><em>Needs more work</em>. This would seem superficially to be
     *   identical to {@link #getInEdges}, but in our implementation we return
     *   an empty vector.</p>
     *
     * @param port  The port for which we want to know the outgoing edges.
     *
     * @return      A vector of objects which are the outgoing edges. Currently
     *              return the empty vector.
     */

    public Vector getOutEdges(Object port) {
        return new Vector();
    }


    /**
     * <p>Return the source end of an edge.</p>
     *
     * <p><em>Needs more work</em>. In the current implementation we only know
     *   how to handle associations, returning the first of its
     *   connections&mdash;which, if set, will be a use case or an actor.</p>
     *
     * @param edge  The edge for which we want the source port.
     *
     * @return      The source port for the edge, or <code>null</code> if the
     *              edge given is not an association or has no source defined.
     */

    public Object getSourcePort(Object edge) {
        
        if (edge instanceof MRelationship) {
            return CoreHelper.getHelper().getSource((MRelationship) edge);
	}

        // Don't know what to do otherwise

        cat.debug(this.getClass().toString() + ": getSourcePort(" +
		  edge.toString() + ") - can't handle");

        return null;
    }


    /**
     * <p>Return the destination end of an edge.</p>
     *
     * <p><em>Needs more work</em>. In the current implementation we only know
     *   how to handle associations, returning the second of its
     *   connections&mdash;which, if set, will be a use case or an actor.</p>
     *
     * @param edge  The edge for which we want the destination port.
     *
     * @return      The destination port for the edge, or <code>null</code> if
     *              the edge given is not an association or has no destination
     *              defined.
     */

    public Object getDestPort(Object edge) {

        // Know what to do for an association

        if (edge instanceof MAssociation) {
            MAssociation assoc = (MAssociation) edge;
            Vector       conns = new Vector(assoc.getConnections());

            return conns.elementAt(1);
        }

        // Don't know what to do otherwise

        cat.debug(this.getClass().toString() + ": getDestPort(" +
		  edge.toString() + ") - can't handle");

        return null;
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    // Methods that implement the MutableGraphModel interface
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * <p>Determine if the given node can validly be placed on this graph.</p>
     *
     * <p>This is simply a matter of determining if the node is an actor or use
     *   case.</p>
     *
     * <p><em>Note</em>. This is inconsistent with {@link #addNode}, which will
     *   not allow a node to be added to the graph if it is already there.</p>
     *
     * @param node  The node to be considered
     *
     * @return      <code>true</code> if the given object is a valid node in
     *              this graph, <code>false</code> otherwise.
     */

    public boolean canAddNode(Object node) {
        if (_nodes.contains(node)) return false;
        return (node instanceof MActor) || (node instanceof MUseCase);
    }


    /**
     * <p>Determine if the given edge can validly be placed on this graph.</p>
     *
     * <p>We cannot do so if the edge is already on the graph (unlike nodes
     *   they may not appear more than once).<p>
     *
     * <p>Otherwise, for all valid types of edge (binary association,
     *   generalization, extend, include, dependency) we get the two ends. If
     *   they are both nodes already on the graph we are OK, otherwise we
     *   cannot place the edge on the graph.</p>
     *
     * @param edge  The edge to be considered
     *
     * @return      <code>true</code> if the given object is a valid edge in
     *              this graph, <code>false</code> otherwise.
     */

    public boolean canAddEdge(Object edge)  {

        // Give up if we are already on the graph
        if (edge == null) return false;
        if (_edges.contains(edge)) {
            return false;
        }

        // Get the two ends of any valid edge

        Object end0 = null;
        Object end1 = null;

        if (edge instanceof MAssociation) {

            // Only allow binary associations

            List conns = ((MAssociation) edge).getConnections();

            if (conns.size() < 2) {
                return false;
            }

            MAssociationEnd ae0 = (MAssociationEnd) conns.get(0);
            MAssociationEnd ae1 = (MAssociationEnd) conns.get(1);

            // Give up if the assocation ends don't have a type defined

            if ((ae0 == null) || (ae1 == null)) {
                return false;
            }

            end0 = ae0.getType();
            end1 = ae1.getType();
        }
        else if (edge instanceof MGeneralization) {
            end0 = ((MGeneralization) edge).getChild();
            end1 = ((MGeneralization) edge).getParent();
        }
        else if (edge instanceof MExtend) {
            end0 = ((MExtend) edge).getBase();
            end1 = ((MExtend) edge).getExtension();
        }
        else if (edge instanceof MInclude) {

            // There is a bug in NSUML which gets the addition and base
            // relationships back to front for include relationships. Solve
            // by reversing their accessors in the code

            end0 = ((MInclude) edge).getAddition();
            end1 = ((MInclude) edge).getBase();
        }
        else if (edge instanceof MDependency) {

            // A dependency potentially has many clients and suppliers. We only
            // consider the first of each (not clear that we should really
            // accept the case where there is more than one of either)

            Collection clients   = ((MDependency) edge).getClients();
            Collection suppliers = ((MDependency) edge).getSuppliers();

            // Give up if either clients or suppliers is undefined

            if ((clients == null) || (suppliers == null)) {
                return false;
            }

            end0 = ((Object[]) clients.toArray())[0];
            end1 = ((Object[]) suppliers.toArray())[0];
        }

        // Both ends must be defined and nodes that are on the graph already.

        if ((end0 == null) || (end1 == null) ||
            (!(_nodes.contains(end0))) || (!(_nodes.contains(end1)))) {
            return false;
        }

        // AOK

        return true;
    }

    
    /** 
     * <p>Add the given node to the graph, if valid.</p>
     *
     * <p>We add the node if it is not already on the graph, and (assuming it
     *   to be an actor or use case) add it to the owned elements for the
     *   model.</p>
     *
     * <p><em>Needs more work</em>. In adding the node to the owned elements of
     *   the model namespace, we are implicitly making it public visibility (it
     *   could be private to this namespace).</p>
     *
     * <p><em>Note</em>. This method is inconsistent with {@link #canAddNode},
     *   which will allow a node to be added to the graph if it is already
     *   there.</p>
     *
     * @param node  The node to be added to the graph.
     */

    public void addNode(Object node) {

        cat.debug("adding usecase node!!");

        // Give up if we are already on the graph. This is a bit inconistent
        // with canAddNode above.

        if (!canAddNode(node)) return;

        // Add the node, check that it is an actor or use case and add it to
        // the model namespace.

        _nodes.addElement(node);
        /*
         * 2002-07-14
         * Jaap Branderhorst
         * Issue 324
         * Next line of code didn't check if the node allready was
         * owned by some namespace. So when a node was added that was allready
         * owned by a namespace, the node was moved to the namespace the 
         * usecase diagram belongs to.
         * OLD CODE

	 if ((node instanceof MActor) ||
	 (node instanceof MUseCase)) {
         
         * NEW CODE:
         */
	if (((node instanceof MActor) || (node instanceof MUseCase)) && 
	    (((MModelElement) node).getNamespace() == null)) {
	    // end NEW CODE
            cat.debug("setting namespace " + _model +
		      " to element " + node);
            _model.addOwnedElement((MModelElement) node);
        }

        // Tell GEF its changed

        fireNodeAdded(node);
    }


    /** 
     * <p>Add the given edge to the graph, if valid.</p>
     *
     * <p>We add the edge if it is not already on the graph, and (assuming it
     *   to be an association, generalization, extend, include or dependency)
     *   add it to the owned elements for the model.</p>
     *
     * <p><em>Needs more work</em>. In adding the edge to the owned elements of
     *   the model namespace, we are implicitly making it public visibility (it
     *   could be private to this namespace).</p>
     *
     * @param edge  The edge to be added to the graph.
     */

    public void addEdge(Object edge) {
        cat.debug("adding class edge!!!!!!");
        if (!canAddEdge(edge)) return;

        // Add the element and place it in the namespace of the model
        _edges.addElement(edge);

        if (((MModelElement) edge).getNamespace() == null) {
            _model.addOwnedElement((MModelElement) edge);
        }

        // Tell GEF

        fireEdgeAdded(edge);
    }

    /**
     * <p>Add the various types of edge that may be connected with the given
     *   node.</p>
     *
     * <p>For use cases we may find extend and include relationships. For
     *   classifiers (effectively actors and use cases) we may find
     *   associations. For generalizable elements (effectively actors and use
     *   cases again) we may find generalizations and
     *   specializations. For ModelElements (effectively actors and use cases
     *   again) we may find dependencies.</p>
     *
     * @param node  The node whose edges are to be added.
     */

    public void addNodeRelatedEdges(Object node) {

        // Extend and include relationships for use cases. Collect all the
        // relationships of which the use case is either end and iterate to see
        // if they can be added.

        if (node instanceof MUseCase) {
            Vector ends = new Vector();

            // Collect all the includes at either end.

            ends.addAll(((MUseCase) node).getIncludes());
            ends.addAll(((MUseCase) node).getIncludes2());
            ends.addAll(((MUseCase) node).getExtends());
            ends.addAll(((MUseCase) node).getExtends2());

            Iterator iter = ends.iterator();

            while (iter.hasNext()) {
                MRelationship rel = (MRelationship) iter.next();

                if (canAddEdge(rel)) {
                    addEdge(rel);
                }
            }
        }

        // Associations for classifiers. Iterate over all the association ends
        // to find the associations.

        if (node instanceof MClassifier) {
            Collection ends = ((MClassifier) node).getAssociationEnds();
            Iterator   iter = ends.iterator();

            while (iter.hasNext()) {
                MAssociationEnd ae = (MAssociationEnd) iter.next();

                if (canAddEdge(ae.getAssociation())) {
                    addEdge(ae.getAssociation());
                }
            }
        }

        // Generalizations and specializations for generalizable
        // elements. Iterate over each set in turn

        if (node instanceof MGeneralizableElement) {

            // The generalizations

            Collection gn = ((MGeneralizableElement) node).getGeneralizations();

            Iterator iter = gn.iterator();

            while (iter.hasNext()) {
                MGeneralization g = (MGeneralization) iter.next();

                if (canAddEdge(g)) {
                    addEdge(g);
                }
            }

            // The specializations

            Collection sp = ((MGeneralizableElement) node).getSpecializations();

            iter = sp.iterator();

            while (iter.hasNext()) {
                MGeneralization s = (MGeneralization) iter.next();

                if (canAddEdge(s)) {
                    addEdge(s);
                }
            }
        }

        // Dependencies for model elements. Iterate over client and suppliers
        // together.

        if ( node instanceof MModelElement ) {
            Vector specs =
                new Vector(((MModelElement) node).getClientDependencies());

            specs.addAll(((MModelElement) node).getSupplierDependencies());

            Iterator iter = specs.iterator();

            while (iter.hasNext()) {
                MDependency dep = (MDependency) iter.next();

                if (canAddEdge(dep)) {
                    addEdge(dep);
                }
            }
        }
    }


 
    /**
     * <p>Determine if the two given ports can be connected by a
     *   kind of edge to be determined by the ports.</p>
     *
     * <p><em>Note</em>. There appears to be a problem with the implementation,
     *   since it suggests actors cannot connect. In fact generalization is
     *   permitted, and this works, suggesting this method is not actually
     *   invoked in the current implementation of ArgoUML.</p>
     *
     * @param fromP  The source port of the connection
     *
     * @param toP    The destination port of the connection.
     *
     * @return       <code>true</code> if the two given ports can be connected
     *               by a kind of edge to be determined by the
     *               ports. <code>false</code> otherwise.
     */

    public boolean canConnect(Object fromP, Object toP) {

        // Suggest that actors may not connect (see JavaDoc comment about
        // this).

        if ((fromP instanceof MActor) && (toP instanceof MActor)) {
            return false;
        }

        // Everything else is OK

        return true;
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    // Methods that implement the VetoableChangeListener interface
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * <p>Called when a property of interest has been changed - in this case
     *   the owned elements of the model. Provided to implement the {@link
     *   VetoableChangeListener} interface.</p>
     *
     * <p>We could throw a {@link PropertyVetoException} if we wished to allow
     *   the change to be rolled back, but we don't.</p>
     *
     * @param pce  The event that triggered us, and from which we can extract
     *             the name of the property that triggered us.
     */

    public void vetoableChange(PropertyChangeEvent pce) {

        // Only interested in the "ownedElement" property. Either something has
        // been added to the namespace for this model, or removed. In the
        // latter case the "something" will be in the old value of the
        // property, which is the vector of owned elements, and the new value
        // will be the element import describing the model element and the
        // model from which it was removed

        if ("ownedElement".equals(pce.getPropertyName())) {
            Vector oldOwned = (Vector) pce.getOldValue();

            MElementImport eo = (MElementImport) pce.getNewValue();
            MModelElement  me = eo.getModelElement();

            // If the element import is in the old owned, it means it must have
            // been removed. Make sure the associated model element is removed.

            if (oldOwned.contains(eo)) {

                cat.debug("model removed " + me);

                // Remove a node

                if ((me instanceof MActor) ||
                    (me instanceof MUseCase)) {

                    removeNode(me);
                }

                // Remove an edge

                else if ((me instanceof MAssociation) ||
                         (me instanceof MGeneralization) ||
                         (me instanceof MExtend) ||
                         (me instanceof MInclude) ||
                         (me instanceof MDependency)) {

                    removeEdge(me);
                }
            }

            // Something was added - nothing for us to worry about
            else {
                cat.debug("model added " + me);
            }
        }
    }

    static final long serialVersionUID = -8516841965639203796L;

} /* end class UseCaseDiagramGraphModel */

