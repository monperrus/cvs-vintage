package CH.ifa.draw.test.contrib;

import junit.framework.TestCase;
// JUnitDoclet begin import
import CH.ifa.draw.contrib.GraphicalCompositeFigure;
import CH.ifa.draw.contrib.SimpleLayouter;
import CH.ifa.draw.figures.RectangleFigure;

import java.awt.*;
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
* TestCase GraphicalCompositeFigureTest is generated by
* JUnitDoclet to hold the tests for GraphicalCompositeFigure.
* @see CH.ifa.draw.contrib.GraphicalCompositeFigure
*/
// JUnitDoclet end javadoc_class
public class GraphicalCompositeFigureTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // instance variables, helper methods, ... put them in this marker
  CH.ifa.draw.contrib.GraphicalCompositeFigure graphicalcompositefigure = null;
  // JUnitDoclet end class
  
  /**
  * Constructor GraphicalCompositeFigureTest is
  * basically calling the inherited constructor to
  * initiate the TestCase for use by the Framework.
  */
  public GraphicalCompositeFigureTest(String name) {
    // JUnitDoclet begin method GraphicalCompositeFigureTest
    super(name);
    // JUnitDoclet end method GraphicalCompositeFigureTest
  }
  
  /**
  * Factory method for instances of the class to be tested.
  */
  public CH.ifa.draw.contrib.GraphicalCompositeFigure createInstance() throws Exception {
    // JUnitDoclet begin method testcase.createInstance
    return new CH.ifa.draw.contrib.GraphicalCompositeFigure();
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
    graphicalcompositefigure = createInstance();
    // JUnitDoclet end method testcase.setUp
  }
  
  /**
  * Method tearDown is overwriting the framework method to
  * clean up after each single test of this TestCase.
  * It's called from the JUnit framework only.
  */
  protected void tearDown() throws Exception {
    // JUnitDoclet begin method testcase.tearDown
    graphicalcompositefigure = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  // JUnitDoclet begin javadoc_method clone()
  /**
  * Method testClone is testing clone
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#clone()
  */
  // JUnitDoclet end javadoc_method clone()
  public void testClone() throws Exception {
    // JUnitDoclet begin method clone
    // JUnitDoclet end method clone
  }
  
  // JUnitDoclet begin javadoc_method displayBox()
  /**
  * Method testDisplayBox is testing displayBox
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#displayBox()
  */
  // JUnitDoclet end javadoc_method displayBox()
  public void testDisplayBox() throws Exception {
    // JUnitDoclet begin method displayBox
    // JUnitDoclet end method displayBox
  }
  
  // JUnitDoclet begin javadoc_method basicDisplayBox()
  /**
  * Method testBasicDisplayBox is testing basicDisplayBox
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#basicDisplayBox(java.awt.Point, java.awt.Point)
  */
  // JUnitDoclet end javadoc_method basicDisplayBox()
  public void testBasicDisplayBox() throws Exception {
    // JUnitDoclet begin method basicDisplayBox
    // JUnitDoclet end method basicDisplayBox
  }
  
  // JUnitDoclet begin javadoc_method update()
  /**
  * Method testUpdate is testing update
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#update()
  */
  // JUnitDoclet end javadoc_method update()
  public void testUpdate() throws Exception {
    // JUnitDoclet begin method update
    // JUnitDoclet end method update
  }
  
  // JUnitDoclet begin javadoc_method draw()
  /**
  * Method testDraw is testing draw
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#draw(java.awt.Graphics)
  */
  // JUnitDoclet end javadoc_method draw()
  public void testDraw() throws Exception {
    // JUnitDoclet begin method draw
    // JUnitDoclet end method draw
  }
  
  // JUnitDoclet begin javadoc_method handles()
  /**
  * Method testHandles is testing handles
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#handles()
  */
  // JUnitDoclet end javadoc_method handles()
  public void testHandles() throws Exception {
    // JUnitDoclet begin method handles
    // JUnitDoclet end method handles
  }
  
  // JUnitDoclet begin javadoc_method getAttribute()
  /**
  * Method testGetAttribute is testing getAttribute
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#getAttribute(java.lang.String)
  */
  // JUnitDoclet end javadoc_method getAttribute()
  public void testGetAttribute() throws Exception {
    // JUnitDoclet begin method getAttribute
    // JUnitDoclet end method getAttribute
  }
  
  // JUnitDoclet begin javadoc_method setAttribute()
  /**
  * Method testSetAttribute is testing setAttribute
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#setAttribute(java.lang.String, java.lang.Object)
  */
  // JUnitDoclet end javadoc_method setAttribute()
  public void testSetAttribute() throws Exception {
    // JUnitDoclet begin method setAttribute
    // JUnitDoclet end method setAttribute
  }
  
  // JUnitDoclet begin javadoc_method setPresentationFigure()
  /**
  * Method testSetGetPresentationFigure is testing setPresentationFigure
  * and getPresentationFigure together by setting some value
  * and verifying it by reading.
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#setPresentationFigure(CH.ifa.draw.framework.Figure)
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#getPresentationFigure()
  */
  // JUnitDoclet end javadoc_method setPresentationFigure()
  public void testSetGetPresentationFigure() throws Exception {
    // JUnitDoclet begin method setPresentationFigure getPresentationFigure
    CH.ifa.draw.framework.Figure[] tests = {new RectangleFigure(new Point(10,10), new Point(100,100)), null};
    
    for (int i = 0; i < tests.length; i++) {
      graphicalcompositefigure.setPresentationFigure(tests[i]);
      assertEquals(tests[i], graphicalcompositefigure.getPresentationFigure());
    }
    // JUnitDoclet end method setPresentationFigure getPresentationFigure
  }
  
  // JUnitDoclet begin javadoc_method layout()
  /**
  * Method testLayout is testing layout
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#layout()
  */
  // JUnitDoclet end javadoc_method layout()
  public void testLayout() throws Exception {
    // JUnitDoclet begin method layout
    // JUnitDoclet end method layout
  }
  
  // JUnitDoclet begin javadoc_method setLayouter()
  /**
  * Method testSetGetLayouter is testing setLayouter
  * and getLayouter together by setting some value
  * and verifying it by reading.
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#setLayouter(CH.ifa.draw.contrib.Layouter)
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#getLayouter()
  */
  // JUnitDoclet end javadoc_method setLayouter()
  public void testSetGetLayouter() throws Exception {
    // JUnitDoclet begin method setLayouter getLayouter
    CH.ifa.draw.contrib.Layouter[] tests = {new SimpleLayouter(graphicalcompositefigure), null};
    
    for (int i = 0; i < tests.length; i++) {
      graphicalcompositefigure.setLayouter(tests[i]);
      assertEquals(tests[i], graphicalcompositefigure.getLayouter());
    }
    // JUnitDoclet end method setLayouter getLayouter
  }
  
  // JUnitDoclet begin javadoc_method figureRequestRemove()
  /**
  * Method testFigureRequestRemove is testing figureRequestRemove
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#figureRequestRemove(CH.ifa.draw.framework.FigureChangeEvent)
  */
  // JUnitDoclet end javadoc_method figureRequestRemove()
  public void testFigureRequestRemove() throws Exception {
    // JUnitDoclet begin method figureRequestRemove
    // JUnitDoclet end method figureRequestRemove
  }
  
  // JUnitDoclet begin javadoc_method read()
  /**
  * Method testRead is testing read
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#read(CH.ifa.draw.util.StorableInput)
  */
  // JUnitDoclet end javadoc_method read()
  public void testRead() throws Exception {
    // JUnitDoclet begin method read
    // JUnitDoclet end method read
  }
  
  // JUnitDoclet begin javadoc_method write()
  /**
  * Method testWrite is testing write
  * @see CH.ifa.draw.contrib.GraphicalCompositeFigure#write(CH.ifa.draw.util.StorableOutput)
  */
  // JUnitDoclet end javadoc_method write()
  public void testWrite() throws Exception {
    // JUnitDoclet begin method write
    // JUnitDoclet end method write
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
    junit.textui.TestRunner.run(GraphicalCompositeFigureTest.class);
    // JUnitDoclet end method testcase.main
  }
}
