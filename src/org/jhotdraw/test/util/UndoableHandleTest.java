/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.test.util;

import java.awt.Point;

// JUnitDoclet begin import
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.standard.BoxHandleKit;
import CH.ifa.draw.test.JHDTestCase;
import CH.ifa.draw.util.UndoableHandle;
// JUnitDoclet end import

/*
 * Generated by JUnitDoclet, a tool provided by
 * ObjectFab GmbH under LGPL.
 * Please see www.junitdoclet.org, www.gnu.org
 * and www.objectfab.de for informations about
 * the tool, the licence and the authors.
 */

// JUnitDoclet begin javadoc_class
/**
 * TestCase UndoableHandleTest is generated by
 * JUnitDoclet to hold the tests for UndoableHandle.
 * @see CH.ifa.draw.util.UndoableHandle
 */
// JUnitDoclet end javadoc_class
public class UndoableHandleTest
// JUnitDoclet begin extends_implements
extends JHDTestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private UndoableHandle undoablehandle;
	// JUnitDoclet end class

	/**
	 * Constructor UndoableHandleTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public UndoableHandleTest(String name) {
		// JUnitDoclet begin method UndoableHandleTest
		super(name);
		// JUnitDoclet end method UndoableHandleTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public UndoableHandle createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		return new UndoableHandle(BoxHandleKit.south(new RectangleFigure(new Point(44, 44), new Point(88, 88))));
		// JUnitDoclet end method testcase.createInstance
	}

	/**
	 * Method setUp is overwriting the framework method to
	 * prepare an instance of this TestCase for a single test.
	 * It's called from the JUnit framework only.
	 */
	protected void setUp() throws Exception {
		// JUnitDoclet begin method testcase.setUp
		super.setUp();
		undoablehandle = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		undoablehandle = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method locate()
	/**
	 * Method testLocate is testing locate
	 * @see CH.ifa.draw.util.UndoableHandle#locate()
	 */
	// JUnitDoclet end javadoc_method locate()
	public void testLocate() throws Exception {
		// JUnitDoclet begin method locate
		// JUnitDoclet end method locate
	}

	// JUnitDoclet begin javadoc_method invokeStart()
	/**
	 * Method testInvokeStart is testing invokeStart
	 * @see CH.ifa.draw.util.UndoableHandle#invokeStart(int, int, CH.ifa.draw.framework.Drawing)
	 */
	// JUnitDoclet end javadoc_method invokeStart()
	public void testInvokeStart() throws Exception {
		// JUnitDoclet begin method invokeStart
		// JUnitDoclet end method invokeStart
	}

	// JUnitDoclet begin javadoc_method invokeStep()
	/**
	 * Method testInvokeStep is testing invokeStep
	 * @see CH.ifa.draw.util.UndoableHandle#invokeStep(int, int, CH.ifa.draw.framework.Drawing)
	 */
	// JUnitDoclet end javadoc_method invokeStep()
	public void testInvokeStep() throws Exception {
		// JUnitDoclet begin method invokeStep
		// JUnitDoclet end method invokeStep
	}

	// JUnitDoclet begin javadoc_method invokeEnd()
	/**
	 * Method testInvokeEnd is testing invokeEnd
	 * @see CH.ifa.draw.util.UndoableHandle#invokeEnd(int, int, int, int, CH.ifa.draw.framework.DrawingView)
	 */
	// JUnitDoclet end javadoc_method invokeEnd()
	public void testInvokeEnd() throws Exception {
		// JUnitDoclet begin method invokeEnd
		// JUnitDoclet end method invokeEnd
	}

	// JUnitDoclet begin javadoc_method owner()
	/**
	 * Method testOwner is testing owner
	 * @see CH.ifa.draw.util.UndoableHandle#owner()
	 */
	// JUnitDoclet end javadoc_method owner()
	public void testOwner() throws Exception {
		// JUnitDoclet begin method owner
		// JUnitDoclet end method owner
	}

	// JUnitDoclet begin javadoc_method displayBox()
	/**
	 * Method testDisplayBox is testing displayBox
	 * @see CH.ifa.draw.util.UndoableHandle#displayBox()
	 */
	// JUnitDoclet end javadoc_method displayBox()
	public void testDisplayBox() throws Exception {
		// JUnitDoclet begin method displayBox
		// JUnitDoclet end method displayBox
	}

	// JUnitDoclet begin javadoc_method containsPoint()
	/**
	 * Method testContainsPoint is testing containsPoint
	 * @see CH.ifa.draw.util.UndoableHandle#containsPoint(int, int)
	 */
	// JUnitDoclet end javadoc_method containsPoint()
	public void testContainsPoint() throws Exception {
		// JUnitDoclet begin method containsPoint
		// JUnitDoclet end method containsPoint
	}

	// JUnitDoclet begin javadoc_method draw()
	/**
	 * Method testDraw is testing draw
	 * @see CH.ifa.draw.util.UndoableHandle#draw(java.awt.Graphics)
	 */
	// JUnitDoclet end javadoc_method draw()
	public void testDraw() throws Exception {
		// JUnitDoclet begin method draw
		// JUnitDoclet end method draw
	}

	// JUnitDoclet begin javadoc_method getDrawingView()
	/**
	 * Method testGetDrawingView is testing getDrawingView
	 * @see CH.ifa.draw.util.UndoableHandle#getDrawingView()
	 */
	// JUnitDoclet end javadoc_method getDrawingView()
	public void testGetDrawingView() throws Exception {
		// JUnitDoclet begin method getDrawingView
		// JUnitDoclet end method getDrawingView
	}

	// JUnitDoclet begin javadoc_method setUndoActivity()
	/**
	 * Method testSetGetUndoActivity is testing setUndoActivity
	 * and getUndoActivity together by setting some value
	 * and verifying it by reading.
	 * @see CH.ifa.draw.util.UndoableHandle#setUndoActivity(CH.ifa.draw.util.Undoable)
	 * @see CH.ifa.draw.util.UndoableHandle#getUndoActivity()
	 */
	// JUnitDoclet end javadoc_method setUndoActivity()
	public void testSetGetUndoActivity() throws Exception {
		// JUnitDoclet begin method setUndoActivity getUndoActivity
		// Do nothing: UndoableHandle.setUndoActivity() is a no-op.
		// JUnitDoclet end method setUndoActivity getUndoActivity
	}

	// JUnitDoclet begin javadoc_method testVault
	/**
	 * JUnitDoclet moves marker to this method, if there is not match
	 * for them in the regenerated code and if the marker is not empty.
	 * This way, no test gets lost when regenerating after renaming.
	 * <b>Method testVault is supposed to be empty.</b>
	 */
	// JUnitDoclet end javadoc_method testVault
	public void testVault() throws Exception {
		// JUnitDoclet begin method testcase.testVault
		// JUnitDoclet end method testcase.testVault
	}

}
