/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.struts.taglib.logic;

import javax.servlet.*;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;


import junit.framework.*;
import org.apache.commons.cactus.*;

/**
 * Suite of unit tests for the
 * <code>org.apache.struts.taglib.logic.PresentTag</code> class.
 *
 * @author David Winterfeldt
 */
public class TestNotPresentTag extends JspTestCase {
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestNotPresentTag(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(new String[] {TestNotPresentTag.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNotPresentTag.class);
    }

    //----- Test initApplication() method --------------------------------------

    /**
     * Verify that there is an application scope object in scope using the <code>NotPresentTag</code>.
    */
    public void testApplicationScopeObjectPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testApplicationScopePresent";
        String testStringValue = "abc";
        
        pageContext.setAttribute(testKey, testStringValue, PageContext.APPLICATION_SCOPE);
        pt.setPageContext(pageContext);
	pt.setName(testKey);
	pt.setScope("application");
	
        assertEquals("Value present (not null)", false, pt.condition(false));
    }

    /**
     * Verify that there is an application scope object is not in scope using the <code>NotPresentTag</code>.
    */
    public void testApplicationScopeObjectNotPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testApplicationScopeNotPresent";

        pt.setPageContext(pageContext);
	pt.setName(testKey);
	pt.setScope("application");
	
	
        assertEquals("Value not present (null)", true, pt.condition(false));
    }
    
    /**
     * Verify that there is an session scope object in scope using the <code>NotPresentTag</code>.
    */
    public void testSessionScopeObjectPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testSessionScopePresent";
        String testStringValue = "abc";
        
        pageContext.setAttribute(testKey, testStringValue, PageContext.SESSION_SCOPE);
        pt.setPageContext(pageContext);
	pt.setName(testKey);
	pt.setScope("session");
	
	
        assertEquals("Value present (not null)", false, pt.condition(false));
    }

    /**
     * Verify that there is an session scope object is not in scope using the <code>NotPresentTag</code>.
    */
    public void testSessionScopeObjectNotPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testSessionScopeNotPresent";

        pt.setPageContext(pageContext);
	pt.setName(testKey);
	pt.setScope("session");
	
	
        assertEquals("Value present (not null)", true, pt.condition(false));
    }
    
    /**
     * Verify that there is an request scope object in scope using the <code>NotPresentTag</code>.
    */
    public void testRequestScopeObjectPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testRequestScopePresent";
        String testStringValue = "abc";
        pt.setScope("request");
        
        pageContext.setAttribute(testKey, testStringValue, PageContext.REQUEST_SCOPE);
        pt.setPageContext(pageContext);
	pt.setName(testKey);
	
	
        assertEquals("Value present (not null)", false, pt.condition(false));
    }

    /**
     * Verify that there is an request scope object is not in scope using the <code>NotPresentTag</code>.
    */
    public void testRequestScopeObjectNotPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testRequestScopeNotPresent";

        pt.setPageContext(pageContext);
	pt.setName(testKey);
	pt.setScope("request");
	
	
        assertEquals("Value not present (null)", true, pt.condition(false));
    }
    
    /**
     * Verify that there is an page scope object in scope using the <code>NotPresentTag</code>.
    */
    public void testPageScopeObjectPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testPageScopePresent";
        String testStringValue = "abc";
        pt.setScope("page");
        
        pageContext.setAttribute(testKey, testStringValue, PageContext.PAGE_SCOPE);
        pt.setPageContext(pageContext);
	pt.setName(testKey);
	
	
        assertEquals("Value present (not null)", false, pt.condition(false));
    }

    /**
     * Verify that there is an page scope object is not in scope using the <code>NotPresentTag</code>.
    */
    public void testPageScopeObjectNotPresent() throws ServletException,  javax.servlet.jsp.JspException {
        NotPresentTag pt = new NotPresentTag();
        String testKey = "testPageScopeNotPresent";

        pt.setPageContext(pageContext);
	pt.setName(testKey);
	pt.setScope("page");
	
	
        assertEquals("Value not present (null)", true, pt.condition(false));
    }

}
