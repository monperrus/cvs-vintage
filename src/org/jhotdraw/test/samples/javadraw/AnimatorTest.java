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
package CH.ifa.draw.test.samples.javadraw;

import java.awt.Point;

// JUnitDoclet begin import
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.samples.javadraw.Animator;
import CH.ifa.draw.samples.javadraw.BouncingDrawing;
import CH.ifa.draw.test.JHDTestCase;
import CH.ifa.draw.util.Animatable;
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
 * TestCase AnimatorTest is generated by
 * JUnitDoclet to hold the tests for Animator.
 * @see CH.ifa.draw.samples.javadraw.Animator
 */
// JUnitDoclet end javadoc_class
public class AnimatorTest
// JUnitDoclet begin extends_implements
extends JHDTestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private Animator animator;
	// JUnitDoclet end class

	/**
	 * Constructor AnimatorTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public AnimatorTest(String name) {
		// JUnitDoclet begin method AnimatorTest
		super(name);
		// JUnitDoclet end method AnimatorTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public CH.ifa.draw.samples.javadraw.Animator createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		Drawing drawing = new BouncingDrawing();
		Animatable animatable = (Animatable)drawing.add(new RectangleFigure(new Point(10, 10), new Point(100, 100)));
		return new CH.ifa.draw.samples.javadraw.Animator(animatable, getDrawingEditor().view());
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
		animator = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		animator = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method start()
	/**
	 * Method testStart is testing start
	 * @see CH.ifa.draw.samples.javadraw.Animator#start()
	 */
	// JUnitDoclet end javadoc_method start()
	public void testStart() throws Exception {
		// JUnitDoclet begin method start
		// JUnitDoclet end method start
	}

	// JUnitDoclet begin javadoc_method end()
	/**
	 * Method testEnd is testing end
	 * @see CH.ifa.draw.samples.javadraw.Animator#end()
	 */
	// JUnitDoclet end javadoc_method end()
	public void testEnd() throws Exception {
		// JUnitDoclet begin method end
		// JUnitDoclet end method end
	}

	// JUnitDoclet begin javadoc_method run()
	/**
	 * Method testRun is testing run
	 * @see CH.ifa.draw.samples.javadraw.Animator#run()
	 */
	// JUnitDoclet end javadoc_method run()
	public void testRun() throws Exception {
		// JUnitDoclet begin method run
		// JUnitDoclet end method run
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
