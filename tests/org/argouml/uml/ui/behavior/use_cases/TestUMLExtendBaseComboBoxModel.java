// $Id: TestUMLExtendBaseComboBoxModel.java,v 1.11 2004/11/01 19:55:10 mvw Exp $
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.uml.ui.behavior.use_cases;

import junit.framework.TestCase;

import org.argouml.kernel.ProjectManager;
import org.argouml.model.uml.ModelManagementFactory;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UseCasesFactory;
import org.argouml.ui.targetmanager.TargetEvent;

import ru.novosoft.uml.MFactoryImpl;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MUseCase;
import ru.novosoft.uml.model_management.MModel;

/**
 * @since Oct 31, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class TestUMLExtendBaseComboBoxModel extends TestCase {

    private int oldEventPolicy;
    private MUseCase[] bases;
    private UMLExtendBaseComboBoxModel model;
    private MExtend elem;
    
    /**
     * Constructor for TestUMLExtendBaseComboBoxModel.
     * @param arg0 is the name of the test case.
     */
    public TestUMLExtendBaseComboBoxModel(String arg0) {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        elem = UseCasesFactory.getFactory().createExtend();
        oldEventPolicy = MFactoryImpl.getEventPolicy();
        MFactoryImpl.setEventPolicy(MFactoryImpl.EVENT_POLICY_IMMEDIATE);
        model = new UMLExtendBaseComboBoxModel();
        bases = new MUseCase[10];
        MModel m = ModelManagementFactory.getFactory().createModel();
        ProjectManager.getManager().getCurrentProject().setRoot(m);
        for (int i = 0; i < 10; i++) {
            bases[i] = UseCasesFactory.getFactory().createUseCase();
            m.addOwnedElement(bases[i]);
        }
        model.targetSet(new TargetEvent(this, "set", new Object[0], 
                new Object[] {elem}));
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        UmlFactory.getFactory().delete(elem);
        for (int i = 0; i < 10; i++) {
            UmlFactory.getFactory().delete(bases[i]);
        }
        MFactoryImpl.setEventPolicy(oldEventPolicy);
        model = null;
    }
    
    /**
     * Test setup.
     */
    public void testSetUp() {
        assertEquals(10, model.getSize());
        assertTrue(model.contains(bases[5]));
        assertTrue(model.contains(bases[0]));
        assertTrue(model.contains(bases[9]));
    }
    
    /**
     * Test setBase().
     */
    public void testSetBase() {
        elem.setBase(bases[0]);
        assertTrue(model.getSelectedItem() == bases[0]);
    }
    
    /**
     * Test setBase() with null argument.
     */
    public void testSetBaseToNull() {
        elem.setBase(null);
        assertNull(model.getSelectedItem());
    }
    
    /**
     * Test removing a Base.
     */
    public void testRemoveBase() {
        UmlFactory.getFactory().delete(bases[9]);
        assertEquals(9, model.getSize());
        assertTrue(!model.contains(bases[9]));
    } 
        
        
}
