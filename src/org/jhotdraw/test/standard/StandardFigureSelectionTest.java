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
package CH.ifa.draw.test.standard;

import java.awt.Point;
import java.util.List;

import junit.framework.TestCase;

// JUnitDoclet begin import
import CH.ifa.draw.figures.PolyLineFigure;
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.standard.FigureEnumerator;
import CH.ifa.draw.standard.StandardFigureSelection;
import CH.ifa.draw.util.CollectionsFactory;
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
 * TestCase StandardFigureSelectionTest is generated by
 * JUnitDoclet to hold the tests for StandardFigureSelection.
 * @see CH.ifa.draw.standard.StandardFigureSelection
 */
// JUnitDoclet end javadoc_class
public class StandardFigureSelectionTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private StandardFigureSelection standardfigureselection;
	// JUnitDoclet end class

	/**
	 * Constructor StandardFigureSelectionTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public StandardFigureSelectionTest(String name) {
		// JUnitDoclet begin method StandardFigureSelectionTest
		super(name);
		// JUnitDoclet end method StandardFigureSelectionTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public CH.ifa.draw.standard.StandardFigureSelection createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		List l = CollectionsFactory.current().createList();
		l.add(new RectangleFigure(new Point(10, 10), new Point(100, 100)));
		l.add(new PolyLineFigure(20, 20));
		FigureEnumeration fenum = new FigureEnumerator(l);
		return new CH.ifa.draw.standard.StandardFigureSelection(fenum, l.size());
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
		standardfigureselection = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		standardfigureselection = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method getType()
	/**
	 * Method testGetType is testing getType
	 * @see CH.ifa.draw.standard.StandardFigureSelection#getType()
	 */
	// JUnitDoclet end javadoc_method getType()
	public void testGetType() throws Exception {
		// JUnitDoclet begin method getType
		// JUnitDoclet end method getType
	}

	// JUnitDoclet begin javadoc_method getData()
	/**
	 * Method testGetData is testing getData
	 * @see CH.ifa.draw.standard.StandardFigureSelection#getData(java.lang.String)
	 */
	// JUnitDoclet end javadoc_method getData()
	public void testGetData() throws Exception {
		// JUnitDoclet begin method getData
		// JUnitDoclet end method getData
	}

	// JUnitDoclet begin javadoc_method duplicateFigures()
	/**
	 * Method testDuplicateFigures is testing duplicateFigures
	 * @see CH.ifa.draw.standard.StandardFigureSelection#duplicateFigures(CH.ifa.draw.framework.FigureEnumeration, int)
	 */
	// JUnitDoclet end javadoc_method duplicateFigures()
	public void testDuplicateFigures() throws Exception {
		// JUnitDoclet begin method duplicateFigures
		// JUnitDoclet end method duplicateFigures
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
