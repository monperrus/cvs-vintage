/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/test/org/apache/strutsel/taglib/html/TestELFileTag.java,v 1.2 2002/09/28 04:43:06 dmkarr Exp $
 * $Revision: 1.2 $
 * $Date: 2002/09/28 04:43:06 $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
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

package org.apache.strutsel.taglib.html;

import com.meterware.httpunit.*;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.xml.transform.*;

import junit.framework.*;

import org.apache.cactus.*;
import org.apache.struts.taglib.html.*;
import org.apache.strutsel.taglib.utils.*;
import org.apache.taglibs.standard.tag.common.core.*;
import org.apache.taglibs.standard.tag.el.core.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class TestELFileTag
    extends JspTagTestCase {

    protected static final String PROPERTY_ATTR_VALUE         =
        "stringProperty";
    protected static final String REQUIRED_DISABLED_VALUE_KEY = 
            "RequiredDisabledValue";
    protected static final String REQUIRED_TYPE_VALUE_KEY  = 
            "RequiredTypeValue";
    protected static final String REQUIRED_VALUE_VALUE_KEY = 
            "RequiredValueValue";
    protected static final String REQUIRED_NAME_VALUE_KEY = 
            "RequiredNameValue";
    protected ELFileTag         elFileTag              = null;

    public TestELFileTag(String theName) {
        super(theName);
    }

    public static void main(String[] args) {
        junit.awtui.TestRunner.main(
                new String[] { TestELFileTag.class.getName() });
    }

    public static Test suite() {
        return new TestSuite(TestELFileTag.class);
    }

    public void setUp() {
        elFileTag = new ELFileTag();
        elFileTag.setPageContext(pageContext);
        elFileTag.setProperty(PROPERTY_ATTR_VALUE);
    }

    public void tearDown() {
        elFileTag = null;
    }

    /**
     * Tests a plain "file" tag, with all default attribute values.
     */
    public void testPlain()
                   throws ServletException, JspException {
        HttpServletResponse response          = (HttpServletResponse)pageContext.getResponse();

        TestFormBean formBean = new TestFormBean();
        pageContext.setAttribute("testFormBean", formBean);
        elFileTag.setName("testFormBean");

        String requiredNameValue = PROPERTY_ATTR_VALUE;
        response.addHeader(REQUIRED_NAME_VALUE_KEY, requiredNameValue);
        String requiredTypeValue = "file";
        response.addHeader(REQUIRED_TYPE_VALUE_KEY, requiredTypeValue);

        String requiredValueValue = "";
        response.addHeader(REQUIRED_VALUE_VALUE_KEY, requiredValueValue);

        int startTagReturn  = elFileTag.doStartTag();
        int afterBodyReturn = elFileTag.doAfterBody();
        int endTagReturn    = elFileTag.doEndTag();
    }

    public void endPlain(com.meterware.httpunit.WebResponse testResponse) {
        try {
            TestHelper.printResponse(testResponse);

            org.w3c.dom.Document document = testResponse.getDOM();
            DOMHelper.printNode(document.getDocumentElement());

            HashMap attrMap = new HashMap();
            DOMHelper.recordFoundAttributes(testResponse.getDOM(), 
                                            "/html/body/input", attrMap);
            DOMHelper.verifyAttributesPresent(attrMap, 
                                              new String[] {
                "name", "type", "value" }, 
                                              false);
            checkAttrValue(attrMap, testResponse, REQUIRED_NAME_VALUE_KEY, 
                           "file", "name");
            checkAttrValue(attrMap, testResponse, REQUIRED_TYPE_VALUE_KEY, 
                           "file", "type");
            checkAttrValue(attrMap, testResponse, REQUIRED_VALUE_VALUE_KEY, 
                           "file", "value");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * Tests the "disabled" attribute.
     */
    public void testDisabled()
                      throws ServletException, JspException {

        TestFormBean formBean = new TestFormBean();
        pageContext.setAttribute("testFormBean", formBean);
        elFileTag.setName("testFormBean");

        elFileTag.setDisabled(true);

        String requiredDisabledValue = "disabled";
        response.addHeader(REQUIRED_DISABLED_VALUE_KEY, requiredDisabledValue);

        int startTagReturn  = elFileTag.doStartTag();
        int afterBodyReturn = elFileTag.doAfterBody();
        int endTagReturn    = elFileTag.doEndTag();
    }

    public void endDisabled(com.meterware.httpunit.WebResponse testResponse) {
        try {
            TestHelper.printResponse(testResponse);

            org.w3c.dom.Document document = testResponse.getDOM();
            DOMHelper.printNode(document.getDocumentElement());

            HashMap attrMap = new HashMap();
            DOMHelper.recordFoundAttributes(testResponse.getDOM(), 
                                            "/html/body/input", attrMap);
            DOMHelper.verifyAttributesPresent(attrMap, 
                                              new String[] { "disabled" }, 
                                              true);
            checkAttrValue(attrMap, testResponse, REQUIRED_DISABLED_VALUE_KEY, 
                           "file", "disabled");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
}
