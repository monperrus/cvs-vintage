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

// JUnitDoclet begin import
import junit.framework.TestCase;
import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.standard.ChangeConnectionStartHandle;
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
 * TestCase ChangeConnectionStartHandleTest is generated by
 * JUnitDoclet to hold the tests for ChangeConnectionStartHandle.
 * @see CH.ifa.draw.standard.ChangeConnectionStartHandle
 */
// JUnitDoclet end javadoc_class
public class ChangeConnectionStartHandleTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private ChangeConnectionStartHandle changeconnectionstarthandle;
	// JUnitDoclet end class

	/**
	 * Constructor ChangeConnectionStartHandleTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public ChangeConnectionStartHandleTest(String name) {
		// JUnitDoclet begin method ChangeConnectionStartHandleTest
		super(name);
		// JUnitDoclet end method ChangeConnectionStartHandleTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public CH.ifa.draw.standard.ChangeConnectionStartHandle createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		return new CH.ifa.draw.standard.ChangeConnectionStartHandle(new LineConnection());
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
		changeconnectionstarthandle = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		changeconnectionstarthandle = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method locate()
	/**
	 * Method testLocate is testing locate
	 * @see CH.ifa.draw.standard.ChangeConnectionStartHandle#locate()
	 */
	// JUnitDoclet end javadoc_method locate()
	public void testLocate() throws Exception {
		// JUnitDoclet begin method locate
		// JUnitDoclet end method locate
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
