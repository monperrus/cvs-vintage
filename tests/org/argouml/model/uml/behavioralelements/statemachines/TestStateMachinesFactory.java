// $Id: TestStateMachinesFactory.java,v 1.5 2004/02/24 08:28:18 linus Exp $
// Copyright (c) 2002-2004 The Regents of the University of California. All
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

package org.argouml.model.uml.behavioralelements.statemachines;

import junit.framework.TestCase;

import org.argouml.util.CheckUMLModelHelper;

public class TestStateMachinesFactory extends TestCase {

    static String[] allModelElements =
    {
	"CallEvent",
	"ChangeEvent",
	"CompositeState",
	"Event",
	"FinalState",
	"Guard",
	"Pseudostate",
	"SignalEvent",
	"SimpleState",
	"State",
	"StateMachine",
	"StateVertex",
	"StubState",
	"SubmachineState",
	"SynchState",
	"TimeEvent",
	"Transition",
    };

    public TestStateMachinesFactory(String n) {
	super(n);
    }

    public void testSingleton() {

	Object o1 = StateMachinesFactory.getFactory();

	Object o2 = StateMachinesFactory.getFactory();

	assertTrue("Different singletons", o1 == o2);

    }

    public void testCreates() {
	// Do not test Event or StateVertex.
	// These are abstract.
	String[] objs = {
	    "CallEvent", "ChangeEvent", "CompositeState",

	    // "Event",
	    "FinalState",
	    "Guard",
	    "Pseudostate",
	    "SignalEvent",
	    "SimpleState",
	    "State",
	    "StateMachine",
	    // "StateVertex",
	    "StubState",
	    "SubmachineState",
	    "SynchState",
	    "TimeEvent",
	    "Transition",
	    null 
	};

	CheckUMLModelHelper.createAndRelease(
					     this,
					     StateMachinesFactory.getFactory(),
					     objs);
    }

    public void testDeleteComplete() {
	CheckUMLModelHelper.deleteComplete(
					   this,
					   StateMachinesFactory.getFactory(),
					   allModelElements);
    }
}
