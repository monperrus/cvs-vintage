// $Id: TestUmlUseCase.java,v 1.8 2005/01/17 22:31:02 linus Exp $
// Copyright (c) 2003-2005 The Regents of the University of California. All
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

package org.argouml.model.uml;

import org.argouml.model.Model;
import org.argouml.model.ModelFacade;


/**
 * @author Thierry Lach
 */
public class TestUmlUseCase extends GenericUmlObjectTestFixture {
    /**
     * Constructor for FakeUmlModelFacadeTest.
     * @param arg0 test name
     */
    public TestUmlUseCase(String arg0) {
	super(arg0, ModelFacade.USE_CASE);
	validateTestClassIsGeneric(this);
    }

    /**
     * Test creating a usecase.
     */
    public void testUseCase() {
	Object o = Model.getUmlFactory().buildNode(ModelFacade.USE_CASE);
	assertNotNull("Didn't create object", o);
	assertTrue("Should be a base", ModelFacade.isABase(o));
	assertTrue("Should be a use case", ModelFacade.isAUseCase(o));
	runTruthTests(o);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        setTruth(ModelFacade.MODELELEMENT, true);
        setTruth(ModelFacade.GENERALIZABLE_ELEMENT, true);
        setTruth(ModelFacade.NAMESPACE, true);
        setTruth(ModelFacade.CLASSIFIER, true);
        setTruth(ModelFacade.USE_CASE, true);
    }

}
