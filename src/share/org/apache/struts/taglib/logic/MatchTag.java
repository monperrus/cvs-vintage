/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/taglib/logic/MatchTag.java,v 1.14 2004/03/14 06:23:44 sraeburn Exp $
 * $Revision: 1.14 $
 * $Date: 2004/03/14 06:23:44 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.struts.taglib.logic;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import org.apache.struts.taglib.TagUtils;


/**
 * Evalute the nested body content of this tag if the specified value
 * is a substring of the specified variable.
 *
 * @version $Revision: 1.14 $ $Date: 2004/03/14 06:23:44 $
 */

public class MatchTag extends ConditionalTagBase {


    // ------------------------------------------------------------- Properties


    /**
     * The location where the match must exist (<code>start</code> or
     * <code>end</code>), or <code>null</code> for anywhere.
     */
    protected String location = null;

    public String getLocation() {
        return (this.location);
    }

    public void setLocation(String location) {
        this.location = location;
    }


    /**
     * The value to which the variable specified by other attributes of this
     * tag will be matched.
     */
    protected String value = null;

    public String getValue() {
        return (this.value);
    }

    public void setValue(String value) {
        this.value = value;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Release all allocated resources.
     */
    public void release() {

        super.release();
        location = null;
        value = null;

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Evaluate the condition that is being tested by this particular tag,
     * and return <code>true</code> if the nested body content of this tag
     * should be evaluated, or <code>false</code> if it should be skipped.
     * This method must be implemented by concrete subclasses.
     *
     * @exception JspException if a JSP exception occurs
     */
    protected boolean condition() throws JspException {

        return (condition(true));

    }


    /**
     * Evaluate the condition that is being tested by this particular tag,
     * and return <code>true</code> if the nested body content of this tag
     * should be evaluated, or <code>false</code> if it should be skipped.
     * This method must be implemented by concrete subclasses.
     *
     * @param desired Desired value for a true result
     *
     * @exception JspException if a JSP exception occurs
     */
    protected boolean condition(boolean desired) throws JspException {

        // Acquire the specified variable
        String variable = null;
        if (cookie != null) {
            Cookie cookies[] =
                ((HttpServletRequest) pageContext.getRequest()).
                getCookies();
            if (cookies == null)
                cookies = new Cookie[0];
            for (int i = 0; i < cookies.length; i++) {
                if (cookie.equals(cookies[i].getName())) {
                    variable = cookies[i].getValue();
                    break;
                }
            }
        } else if (header != null) {
            variable =
                ((HttpServletRequest) pageContext.getRequest()).
                getHeader(header);
        } else if (name != null) {
            Object value =
                TagUtils.getInstance().lookup(pageContext, name, property, scope);
            if (value != null)
                variable = value.toString();
        } else if (parameter != null) {
            variable = pageContext.getRequest().getParameter(parameter);
        } else {
            JspException e = new JspException
                (messages.getMessage("logic.selector"));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }
        if (variable == null) {
            JspException e = new JspException
                (messages.getMessage("logic.variable", value));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Perform the comparison requested by the location attribute
        boolean matched = false;
        if (location == null) {
            matched = (variable.indexOf(value) >= 0);
        } else if (location.equals("start")) {
            matched = variable.startsWith(value);
        } else if (location.equals("end")) {
            matched = variable.endsWith(value);
        } else {
            JspException e = new JspException
                (messages.getMessage("logic.location", location));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Return the final result
        return (matched == desired);

    }


}
