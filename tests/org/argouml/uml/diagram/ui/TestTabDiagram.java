// Copyright (c) 1996-2003 The Regents of the University of California. All
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

// $Id: TestTabDiagram.java,v 1.2 2003/04/19 20:49:32 kataka Exp $
package org.argouml.uml.diagram.ui;

import java.util.Date;

import junit.framework.TestCase;

import org.argouml.application.security.ArgoSecurityManager;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.uml.UmlFactory;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.tigris.gef.graph.presentation.JGraph;

import ru.novosoft.uml.MFactoryImpl;

/**
 * @author jaap.branderhorst@xs4all.nl
 * @since Apr 13, 2003
 */
public class TestTabDiagram extends TestCase {
    
    private final static int NUMBER_OF_DIAGRAMS = 1000;

    private UMLDiagram _diagram;
    
    /**
     * Constructor for TestTabDiagram.
     * @param arg0
     */
    public TestTabDiagram(String arg0) {
        super(arg0);
    }
    
    protected void setUp() throws Exception {
    	super.setUp();
        _diagram = new UMLClassDiagram();  
		ArgoSecurityManager.getInstance().setAllowExit(true); 
    }
    
    protected void tearDown() throws Exception {
    	super.tearDown();
        _diagram = null;

    }
    
    public void testConstruction() {
        TabDiagram tabDiagram = new TabDiagram();
        assertEquals(tabDiagram.getTitle(), "Diagram");        
    }
    
    /**
     * Tests the setTarget method when a diagram is the target.  
     */
    public void testSetTargetWithDiagram() {
        TabDiagram tabDiagram = new TabDiagram();
        tabDiagram.setTarget(_diagram);
        assertEquals(tabDiagram.getJGraph().getGraphModel(), _diagram.getGraphModel());
        assertEquals(tabDiagram.getTarget(), _diagram);
        assertTrue(tabDiagram.shouldBeEnabled(_diagram));        
    }
    
    /**
     * Tests the settarget method when the target is not a diagram but a simple
     * object.
     *
     */
    public void testSetTargetWithNoDiagram() {
        TabDiagram tabDiagram = new TabDiagram();
        Object o = new Object();
        JGraph graph = tabDiagram.getJGraph();
        tabDiagram.setTarget(o);
        // the graph should stay the same.
        assertEquals(tabDiagram.getJGraph(), graph);        
    }
    
    /**
     * Test the performance of adding an operation to 1 class that's represented on 100 different
     * diagrams. The last created diagram is the one selected.
     *
     */
    public void testFireModelEventPerformance() {
    	// setup
    	UMLDiagram[] diagrams = new UMLDiagram[NUMBER_OF_DIAGRAMS]; 
    	Project project = ProjectManager.getManager().getCurrentProject();
    	Object clazz = UmlFactory.getFactory().getCore().buildClass();
    	for (int i = 0; i <NUMBER_OF_DIAGRAMS; i++) {
            diagrams[i] = new UMLClassDiagram(project.getRoot());
    		diagrams[i].add(new FigClass(diagrams[i].getGraphModel(), clazz)); 
            ProjectBrowser.TheInstance.setTarget(diagrams[i]);   		
    	}
        MFactoryImpl.setEventPolicy(MFactoryImpl.EVENT_POLICY_IMMEDIATE);
    	// real test
        long currentTime = (new Date()).getTime();
        UmlFactory.getFactory().getCore().buildOperation(clazz);
    	System.out.println("Time needed for adding operation: " + ((new Date()).getTime() - currentTime));
    }

}
