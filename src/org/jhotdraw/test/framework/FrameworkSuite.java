package CH.ifa.draw.test.framework;



import junit.framework.TestSuite;
// JUnitDoclet begin import
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
* TestSuite FrameworkSuite
*/
// JUnitDoclet end javadoc_class
public class FrameworkSuite
// JUnitDoclet begin extends_implements
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // JUnitDoclet end class
  
  public static TestSuite suite() {
    
    TestSuite suite;
    
    suite = new TestSuite("CH.ifa.draw.test.framework");
    
    suite.addTestSuite(CH.ifa.draw.test.framework.FigureAttributeConstantTest.class);
    suite.addTestSuite(CH.ifa.draw.test.framework.FigureChangeEventTest.class);
    suite.addTestSuite(CH.ifa.draw.test.framework.DrawingChangeEventTest.class);
    
    
    
    // JUnitDoclet begin method suite()
    // JUnitDoclet end method suite()
    
    return suite;
  }
  
  /**
  * Method to execute the TestSuite from command line
  * using JUnit's textui.TestRunner .
  */
  public static void main(String[] args) {
    // JUnitDoclet begin method testsuite.main
    junit.textui.TestRunner.run(suite());
    // JUnitDoclet end method testsuite.main
  }
}
