package CH.ifa.draw.test.samples.nothing;



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
* TestSuite NothingSuite
*/
// JUnitDoclet end javadoc_class
public class NothingSuite
// JUnitDoclet begin extends_implements
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // JUnitDoclet end class
  
  public static TestSuite suite() {
    
    TestSuite suite;
    
    suite = new TestSuite("CH.ifa.draw.test.samples.nothing");
    
    suite.addTestSuite(CH.ifa.draw.test.samples.nothing.NothingAppletTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.nothing.NothingAppTest.class);
    
    
    
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
