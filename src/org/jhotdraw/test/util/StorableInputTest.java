package CH.ifa.draw.test.util;

import junit.framework.TestCase;
// JUnitDoclet begin import
import CH.ifa.draw.util.StorableInput;
import java.io.FileInputStream;
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
* TestCase StorableInputTest is generated by
* JUnitDoclet to hold the tests for StorableInput.
* @see CH.ifa.draw.util.StorableInput
*/
// JUnitDoclet end javadoc_class
public class StorableInputTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // instance variables, helper methods, ... put them in this marker
  CH.ifa.draw.util.StorableInput storableinput = null;
  // JUnitDoclet end class
  
  /**
  * Constructor StorableInputTest is
  * basically calling the inherited constructor to
  * initiate the TestCase for use by the Framework.
  */
  public StorableInputTest(String name) {
    // JUnitDoclet begin method StorableInputTest
    super(name);
    // JUnitDoclet end method StorableInputTest
  }
  
  /**
  * Factory method for instances of the class to be tested.
  */
  public CH.ifa.draw.util.StorableInput createInstance() throws Exception {
    // JUnitDoclet begin method testcase.createInstance
	// todo
	String fileName = "";
	FileInputStream stream = new FileInputStream(fileName);
    return new CH.ifa.draw.util.StorableInput(stream);
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
    storableinput = createInstance();
    // JUnitDoclet end method testcase.setUp
  }
  
  /**
  * Method tearDown is overwriting the framework method to
  * clean up after each single test of this TestCase.
  * It's called from the JUnit framework only.
  */
  protected void tearDown() throws Exception {
    // JUnitDoclet begin method testcase.tearDown
    storableinput = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  // JUnitDoclet begin javadoc_method readStorable()
  /**
  * Method testReadStorable is testing readStorable
  * @see CH.ifa.draw.util.StorableInput#readStorable()
  */
  // JUnitDoclet end javadoc_method readStorable()
  public void testReadStorable() throws Exception {
    // JUnitDoclet begin method readStorable
    // JUnitDoclet end method readStorable
  }
  
  // JUnitDoclet begin javadoc_method readString()
  /**
  * Method testReadString is testing readString
  * @see CH.ifa.draw.util.StorableInput#readString()
  */
  // JUnitDoclet end javadoc_method readString()
  public void testReadString() throws Exception {
    // JUnitDoclet begin method readString
    // JUnitDoclet end method readString
  }
  
  // JUnitDoclet begin javadoc_method readInt()
  /**
  * Method testReadInt is testing readInt
  * @see CH.ifa.draw.util.StorableInput#readInt()
  */
  // JUnitDoclet end javadoc_method readInt()
  public void testReadInt() throws Exception {
    // JUnitDoclet begin method readInt
    // JUnitDoclet end method readInt
  }
  
  // JUnitDoclet begin javadoc_method readLong()
  /**
  * Method testReadLong is testing readLong
  * @see CH.ifa.draw.util.StorableInput#readLong()
  */
  // JUnitDoclet end javadoc_method readLong()
  public void testReadLong() throws Exception {
    // JUnitDoclet begin method readLong
    // JUnitDoclet end method readLong
  }
  
  // JUnitDoclet begin javadoc_method readColor()
  /**
  * Method testReadColor is testing readColor
  * @see CH.ifa.draw.util.StorableInput#readColor()
  */
  // JUnitDoclet end javadoc_method readColor()
  public void testReadColor() throws Exception {
    // JUnitDoclet begin method readColor
    // JUnitDoclet end method readColor
  }
  
  // JUnitDoclet begin javadoc_method readDouble()
  /**
  * Method testReadDouble is testing readDouble
  * @see CH.ifa.draw.util.StorableInput#readDouble()
  */
  // JUnitDoclet end javadoc_method readDouble()
  public void testReadDouble() throws Exception {
    // JUnitDoclet begin method readDouble
    // JUnitDoclet end method readDouble
  }
  
  // JUnitDoclet begin javadoc_method readBoolean()
  /**
  * Method testReadBoolean is testing readBoolean
  * @see CH.ifa.draw.util.StorableInput#readBoolean()
  */
  // JUnitDoclet end javadoc_method readBoolean()
  public void testReadBoolean() throws Exception {
    // JUnitDoclet begin method readBoolean
    // JUnitDoclet end method readBoolean
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
    junit.textui.TestRunner.run(StorableInputTest.class);
    // JUnitDoclet end method testcase.main
  }
}
