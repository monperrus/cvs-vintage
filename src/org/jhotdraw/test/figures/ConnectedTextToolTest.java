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

import java.awt.Point;

// JUnitDoclet begin import
import CH.ifa.draw.figures.ConnectedTextTool;
import CH.ifa.draw.figures.RectangleFigure;
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
 * TestCase ConnectedTextToolTest is generated by
 * JUnitDoclet to hold the tests for ConnectedTextTool.
 * @see CH.ifa.draw.figures.ConnectedTextTool
 */
// JUnitDoclet end javadoc_class
public class ConnectedTextToolTest
// JUnitDoclet begin extends_implements
extends JHDTestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private ConnectedTextTool connectedtexttool;
	// JUnitDoclet end class

	/**
	 * Constructor ConnectedTextToolTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public ConnectedTextToolTest(String name) {
		// JUnitDoclet begin method ConnectedTextToolTest
		super(name);
		// JUnitDoclet end method ConnectedTextToolTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public CH.ifa.draw.figures.ConnectedTextTool createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		return new CH.ifa.draw.figures.ConnectedTextTool(getDrawingEditor(), new RectangleFigure(new Point(10, 10), new Point(100, 100)));
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
		connectedtexttool = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		connectedtexttool = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method mouseDown()
	/**
	 * Method testMouseDown is testing mouseDown
	 * @see CH.ifa.draw.figures.ConnectedTextTool#mouseDown(java.awt.event.MouseEvent, int, int)
	 */
	// JUnitDoclet end javadoc_method mouseDown()
	public void testMouseDown() throws Exception {
		// JUnitDoclet begin method mouseDown
		// JUnitDoclet end method mouseDown
	}

	// JUnitDoclet begin javadoc_method getConnectedFigure()
	/**
	 * Method testGetConnectedFigure is testing getConnectedFigure
	 * @see CH.ifa.draw.figures.ConnectedTextTool#getConnectedFigure()
	 */
	// JUnitDoclet end javadoc_method getConnectedFigure()
	public void testGetConnectedFigure() throws Exception {
		// JUnitDoclet begin method getConnectedFigure
		// JUnitDoclet end method getConnectedFigure
	}

	// JUnitDoclet begin javadoc_method activate()
	/**
	 * Method testActivate is testing activate
	 * @see CH.ifa.draw.figures.ConnectedTextTool#activate()
	 */
	// JUnitDoclet end javadoc_method activate()
	public void testActivate() throws Exception {
		// JUnitDoclet begin method activate
		// JUnitDoclet end method activate
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
