package CH.ifa.draw.test.samples.javadraw;

import junit.framework.TestCase;
// JUnitDoclet begin import
import CH.ifa.draw.samples.javadraw.JavaDrawApplet;
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
* TestCase JavaDrawAppletTest is generated by
* JUnitDoclet to hold the tests for JavaDrawApplet.
* @see CH.ifa.draw.samples.javadraw.JavaDrawApplet
*/
// JUnitDoclet end javadoc_class
public class JavaDrawAppletTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // instance variables, helper methods, ... put them in this marker
  CH.ifa.draw.samples.javadraw.JavaDrawApplet javadrawapplet = null;
  // JUnitDoclet end class
  
  /**
  * Constructor JavaDrawAppletTest is
  * basically calling the inherited constructor to
  * initiate the TestCase for use by the Framework.
  */
  public JavaDrawAppletTest(String name) {
    // JUnitDoclet begin method JavaDrawAppletTest
    super(name);
    // JUnitDoclet end method JavaDrawAppletTest
  }
  
  /**
  * Factory method for instances of the class to be tested.
  */
  public CH.ifa.draw.samples.javadraw.JavaDrawApplet createInstance() throws Exception {
    // JUnitDoclet begin method testcase.createInstance
    return new CH.ifa.draw.samples.javadraw.JavaDrawApplet();
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
    javadrawapplet = createInstance();
    // JUnitDoclet end method testcase.setUp
  }
  
  /**
  * Method tearDown is overwriting the framework method to
  * clean up after each single test of this TestCase.
  * It's called from the JUnit framework only.
  */
  protected void tearDown() throws Exception {
    // JUnitDoclet begin method testcase.tearDown
    javadrawapplet = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  // JUnitDoclet begin javadoc_method destroy()
  /**
  * Method testDestroy is testing destroy
  * @see CH.ifa.draw.samples.javadraw.JavaDrawApplet#destroy()
  */
  // JUnitDoclet end javadoc_method destroy()
  public void testDestroy() throws Exception {
    // JUnitDoclet begin method destroy
    // JUnitDoclet end method destroy
  }
  
  // JUnitDoclet begin javadoc_method startAnimation()
  /**
  * Method testStartAnimation is testing startAnimation
  * @see CH.ifa.draw.samples.javadraw.JavaDrawApplet#startAnimation()
  */
  // JUnitDoclet end javadoc_method startAnimation()
  public void testStartAnimation() throws Exception {
    // JUnitDoclet begin method startAnimation
    // JUnitDoclet end method startAnimation
  }
  
  // JUnitDoclet begin javadoc_method endAnimation()
  /**
  * Method testEndAnimation is testing endAnimation
  * @see CH.ifa.draw.samples.javadraw.JavaDrawApplet#endAnimation()
  */
  // JUnitDoclet end javadoc_method endAnimation()
  public void testEndAnimation() throws Exception {
    // JUnitDoclet begin method endAnimation
    // JUnitDoclet end method endAnimation
  }
  
  // JUnitDoclet begin javadoc_method toggleAnimation()
  /**
  * Method testToggleAnimation is testing toggleAnimation
  * @see CH.ifa.draw.samples.javadraw.JavaDrawApplet#toggleAnimation()
  */
  // JUnitDoclet end javadoc_method toggleAnimation()
  public void testToggleAnimation() throws Exception {
    // JUnitDoclet begin method toggleAnimation
    // JUnitDoclet end method toggleAnimation
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
    junit.textui.TestRunner.run(JavaDrawAppletTest.class);
    // JUnitDoclet end method testcase.main
  }
}
