package CH.ifa.draw.test.framework;

import junit.framework.TestCase;
// JUnitDoclet begin import
import CH.ifa.draw.framework.DrawingChangeEvent;
import CH.ifa.draw.standard.StandardDrawing;
import java.awt.Rectangle;
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
* TestCase DrawingChangeEventTest is generated by
* JUnitDoclet to hold the tests for DrawingChangeEvent.
* @see CH.ifa.draw.framework.DrawingChangeEvent
*/
// JUnitDoclet end javadoc_class
public class DrawingChangeEventTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // instance variables, helper methods, ... put them in this marker
  CH.ifa.draw.framework.DrawingChangeEvent drawingchangeevent = null;
  // JUnitDoclet end class
  
  /**
  * Constructor DrawingChangeEventTest is
  * basically calling the inherited constructor to
  * initiate the TestCase for use by the Framework.
  */
  public DrawingChangeEventTest(String name) {
    // JUnitDoclet begin method DrawingChangeEventTest
    super(name);
    // JUnitDoclet end method DrawingChangeEventTest
  }
  
  /**
  * Factory method for instances of the class to be tested.
  */
  public CH.ifa.draw.framework.DrawingChangeEvent createInstance() throws Exception {
    // JUnitDoclet begin method testcase.createInstance
    return new CH.ifa.draw.framework.DrawingChangeEvent(new StandardDrawing(), new Rectangle(10, 10, 100, 100));
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
    drawingchangeevent = createInstance();
    // JUnitDoclet end method testcase.setUp
  }
  
  /**
  * Method tearDown is overwriting the framework method to
  * clean up after each single test of this TestCase.
  * It's called from the JUnit framework only.
  */
  protected void tearDown() throws Exception {
    // JUnitDoclet begin method testcase.tearDown
    drawingchangeevent = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  // JUnitDoclet begin javadoc_method getDrawing()
  /**
  * Method testGetDrawing is testing getDrawing
  * @see CH.ifa.draw.framework.DrawingChangeEvent#getDrawing()
  */
  // JUnitDoclet end javadoc_method getDrawing()
  public void testGetDrawing() throws Exception {
    // JUnitDoclet begin method getDrawing
    // JUnitDoclet end method getDrawing
  }
  
  // JUnitDoclet begin javadoc_method getInvalidatedRectangle()
  /**
  * Method testGetInvalidatedRectangle is testing getInvalidatedRectangle
  * @see CH.ifa.draw.framework.DrawingChangeEvent#getInvalidatedRectangle()
  */
  // JUnitDoclet end javadoc_method getInvalidatedRectangle()
  public void testGetInvalidatedRectangle() throws Exception {
    // JUnitDoclet begin method getInvalidatedRectangle
    // JUnitDoclet end method getInvalidatedRectangle
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
  
  /**
  * Method to execute the TestCase from command line
  * using JUnit's textui.TestRunner .
  */
  public static void main(String[] args) {
    // JUnitDoclet begin method testcase.main
    junit.textui.TestRunner.run(DrawingChangeEventTest.class);
    // JUnitDoclet end method testcase.main
  }
}
