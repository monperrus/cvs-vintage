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
package CH.ifa.draw.test.figures;

// JUnitDoclet begin import
import CH.ifa.draw.figures.PolyLineFigure;
import CH.ifa.draw.figures.PolyLineHandle;
import CH.ifa.draw.test.JHDTestCase;
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
 * TestCase PolyLineHandleTest is generated by
 * JUnitDoclet to hold the tests for PolyLineHandle.
 * @see CH.ifa.draw.figures.PolyLineHandle
 */
// JUnitDoclet end javadoc_class
public class PolyLineHandleTest
// JUnitDoclet begin extends_implements
extends JHDTestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private PolyLineHandle polylinehandle;
	// JUnitDoclet end class

	/**
	 * Constructor PolyLineHandleTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public PolyLineHandleTest(String name) {
		// JUnitDoclet begin method PolyLineHandleTest
		super(name);
		// JUnitDoclet end method PolyLineHandleTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public CH.ifa.draw.figures.PolyLineHandle createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		PolyLineFigure figure = new PolyLineFigure(20, 20);
		figure.addPoint(30, 30);
		figure.addPoint(40, 40);
		return new CH.ifa.draw.figures.PolyLineHandle(figure, PolyLineFigure.locator(2), 2);
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
		polylinehandle = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		polylinehandle = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method invokeStart()
	/**
	 * Method testInvokeStart is testing invokeStart
	 * @see CH.ifa.draw.figures.PolyLineHandle#invokeStart(int, int, CH.ifa.draw.framework.DrawingView)
	 */
	// JUnitDoclet end javadoc_method invokeStart()
	public void testInvokeStart() throws Exception {
		// JUnitDoclet begin method invokeStart
		// JUnitDoclet end method invokeStart
	}

	// JUnitDoclet begin javadoc_method invokeStep()
	/**
	 * Method testInvokeStep is testing invokeStep
	 * @see CH.ifa.draw.figures.PolyLineHandle#invokeStep(int, int, int, int, CH.ifa.draw.framework.DrawingView)
	 */
	// JUnitDoclet end javadoc_method invokeStep()
	public void testInvokeStep() throws Exception {
		// JUnitDoclet begin method invokeStep
		// JUnitDoclet end method invokeStep
	}

	// JUnitDoclet begin javadoc_method invokeEnd()
	/**
	 * Method testInvokeEnd is testing invokeEnd
	 * @see CH.ifa.draw.figures.PolyLineHandle#invokeEnd(int, int, int, int, CH.ifa.draw.framework.DrawingView)
	 */
	// JUnitDoclet end javadoc_method invokeEnd()
	public void testInvokeEnd() throws Exception {
		// JUnitDoclet begin method invokeEnd
		// JUnitDoclet end method invokeEnd
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
