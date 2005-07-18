// $Id: DiagramFactory.java,v 1.2 2005/07/18 13:43:28 bobtarling Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.model.ActivityDiagram;
import org.argouml.model.ClassDiagram;
import org.argouml.model.CollaborationDiagram;
import org.argouml.model.DeploymentDiagram;
import org.argouml.model.DiDiagram;
import org.argouml.model.Model;
import org.argouml.model.SequenceDiagram;
import org.argouml.model.StateDiagram;
import org.argouml.model.UseCaseDiagram;
import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.GraphChangeAdapter;
import org.argouml.uml.diagram.activity.ui.ActivityDiagramRenderer;
import org.argouml.uml.diagram.activity.ui.UMLActivityDiagram;
import org.argouml.uml.diagram.collaboration.ui.CollabDiagramRenderer;
import org.argouml.uml.diagram.collaboration.ui.UMLCollaborationDiagram;
import org.argouml.uml.diagram.deployment.ui.DeploymentDiagramRenderer;
import org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram;
import org.argouml.uml.diagram.sequence.ui.SequenceDiagramRenderer;
import org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram;
import org.argouml.uml.diagram.state.ui.StateDiagramRenderer;
import org.argouml.uml.diagram.state.ui.UMLStateDiagram;
import org.argouml.uml.diagram.static_structure.ui.ClassDiagramRenderer;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.diagram.use_case.ui.UMLUseCaseDiagram;
import org.argouml.uml.diagram.use_case.ui.UseCaseDiagramRenderer;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;

/**
* Provide a factory method to create different UML diagrams.
* @author Bob Tarling
*/
public class DiagramFactory {
 
    private static Logger LOG = Logger.getLogger(DiagramFactory.class);
    
    private static DiagramFactory diagramFactory = new DiagramFactory();
 
    private List diagrams = new Vector();
    
    private DiagramFactory() {
    }

    public static DiagramFactory getInstance() {
        return diagramFactory;
    }
 
    public List getDiagram() {
    		return diagrams;
    }
    
    /**
     * Factory method to create a new instance of a Class Diagram
     * @param type The class of rendering diagram to create
     * @param model The model that this class diagram represents
     * @param owningElement The modelElement which own this diagram (can be the model)
     * @return the newly instantiated class diagram
     */
    public ArgoDiagram createDiagram(Class type, Object model, Object owningElement) {
        
        ArgoDiagram diagram = null;
        
        if (type == ClassDiagram.class) {
            diagram = new UMLClassDiagram(model);
        } else if (type == UseCaseDiagram.class) {
            diagram = new UMLUseCaseDiagram(model);
        } else if (type == StateDiagram.class) {
            diagram = new UMLStateDiagram(model, owningElement);
        } else if (type == DeploymentDiagram.class) {
            diagram = new UMLDeploymentDiagram(model);
        } else if (type == CollaborationDiagram.class) {
            diagram = new UMLCollaborationDiagram(model);
        } else if (type == ActivityDiagram.class) {
            diagram = new UMLActivityDiagram(model, owningElement);
        } else if (type == SequenceDiagram.class) {
            diagram = new UMLSequenceDiagram(model);
        }
            
        if (diagram == null) {
            throw new IllegalArgumentException ("Unknown diagram type");
        }
        
        if (Model.getDiagramInterchangeModel() != null) {
            diagram.getGraphModel().addGraphEventListener(
                 GraphChangeAdapter.getInstance());
            // The diagram are always owned by the model in this first implementation
            DiDiagram dd =
                GraphChangeAdapter.getInstance().createDiagram(type,model);
            ((UMLMutableGraphSupport)diagram.getGraphModel()).setDiDiagram(dd);
        }
        
        //keep a reference on it in the case where we must add all the diagrams
        //as project members (loading)
        diagrams.add(diagram);
        return diagram;
    }
    
    /**
     * Factory method to create a new instance of a Class Diagram
     * @param model The model that this class diagram represents
     * @return the newly instantiated class diagram
     */
    public ArgoDiagram removeDiagram(ArgoDiagram diagram) {
        
        DiDiagram dd =
            ((UMLMutableGraphSupport)diagram.getGraphModel()).getDiDiagram();
        if (dd != null) {
            GraphChangeAdapter.getInstance().removeDiagram(dd);
        }
        return diagram;
    }

    public DiDiagram getDiDiagram(Object graphModel) {
    		if (graphModel instanceof UMLMutableGraphSupport)
    			return ((UMLMutableGraphSupport)graphModel).getDiDiagram();
    		throw new IllegalArgumentException("graphModel: "+graphModel);
    }
    
    public void addElement(Object diagram, Object element) {
		if (!(diagram instanceof ArgoDiagram))
			throw new IllegalArgumentException("diagram: "+diagram);
		if (!(element instanceof Fig))
			throw new IllegalArgumentException("fig: "+element);
		((ArgoDiagram)diagram).add((Fig)element);
    }

    
    private UmlDiagramRenderer classDiagramRenderer = new ClassDiagramRenderer();

    private UmlDiagramRenderer collabDiagramRenderer = new CollabDiagramRenderer();
    
    private UmlDiagramRenderer deploymentDiagramRenderer = new DeploymentDiagramRenderer();
    
    private UmlDiagramRenderer sequenceDiagramRenderer = new SequenceDiagramRenderer();
    
    private UmlDiagramRenderer stateDiagramRenderer = new StateDiagramRenderer();
    
    private UmlDiagramRenderer useCaseDiagramRenderer = new UseCaseDiagramRenderer();

    private UmlDiagramRenderer activityDiagramRenderer = new ActivityDiagramRenderer();
	
	private final Map noStyleProperties = new HashMap();
    
	public Object createRenderingElement(Object diagram, Object model) {
		Object renderingElement = null;
		if (diagram instanceof UMLClassDiagram) {
			renderingElement = classDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else if (diagram instanceof UMLActivityDiagram) {
			renderingElement = activityDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else if (diagram instanceof UMLCollaborationDiagram) {
			renderingElement = collabDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else if (diagram instanceof UMLDeploymentDiagram) {
			renderingElement = deploymentDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else if (diagram instanceof UMLSequenceDiagram) {
			renderingElement = sequenceDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else if (diagram instanceof UMLStateDiagram) {
			renderingElement = stateDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else if (diagram instanceof UMLUseCaseDiagram) {
			renderingElement = useCaseDiagramRenderer.getFigNodeFor(model,noStyleProperties);
		} else {
			throw new IllegalArgumentException("diag: "+diagram+", model: "+model);
		}		
		return renderingElement;
	}    
}
