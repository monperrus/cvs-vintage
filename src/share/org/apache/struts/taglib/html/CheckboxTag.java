/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/taglib/html/CheckboxTag.java,v 1.24 2004/03/14 06:23:46 sraeburn Exp $
 * $Revision: 1.24 $
 * $Date: 2004/03/14 06:23:46 $
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

package org.apache.struts.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;

/**
 * Tag for input fields of type "checkbox".
 *
 * @version $Revision: 1.24 $ $Date: 2004/03/14 06:23:46 $
 */
public class CheckboxTag extends BaseHandlerTag {

    // ----------------------------------------------------- Instance Variables

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
        MessageResources.getMessageResources(Constants.Package + ".LocalStrings");

    /**
     * The name of the bean containing our underlying property.
     */
    protected String name = Constants.BEAN_KEY;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The property name for this field.
     */
    protected String property = null;

    /**
     * The body content of this tag (if any).
     */
    protected String text = null;

    /**
     * The server value for this option.
     */
    protected String value = null;

    // ------------------------------------------------------------- Properties

    /**
     * Return the property name.
     */
    public String getProperty() {

        return (this.property);

    }

    /**
     * Set the property name.
     *
     * @param property The new property name
     */
    public void setProperty(String property) {

        this.property = property;

    }

    /**
     * Return the server value.
     */
    public String getValue() {

        return (this.value);

    }

    /**
     * Set the server value.
     *
     * @param value The new server value
     */
    public void setValue(String value) {

        this.value = value;

    }

    // --------------------------------------------------------- Public Methods

    /**
     * Generate the required input tag.
     * <p>
     * Support for indexed property since Struts 1.1
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Create an appropriate "input" element based on our parameters
        StringBuffer results = new StringBuffer("<input type=\"checkbox\"");
        results.append(" name=\"");

        if (indexed) {
            prepareIndex(results, name);
        }

        results.append(this.property);
        results.append("\"");
        if (accesskey != null) {
            results.append(" accesskey=\"");
            results.append(accesskey);
            results.append("\"");
        }

        if (tabindex != null) {
            results.append(" tabindex=\"");
            results.append(tabindex);
            results.append("\"");
        }

        results.append(" value=\"");

        if (value == null) {
            results.append("on");
        } else {
            results.append(value);
        }

        results.append("\"");
        
		if (this.isChecked()) {
			results.append(" checked=\"checked\"");
		}

        results.append(prepareEventHandlers());
        results.append(prepareStyles());
        results.append(getElementClose());

        // Print this field to our output writer
        TagUtils.getInstance().write(pageContext, results.toString());

        // Continue processing this page
        this.text = null;
        return (EVAL_BODY_TAG);

    }
    
    /**
     * Determines if the checkbox should be checked.
     * @return true if checked="checked" should be rendered.
     * @throws JspException
     * @since Struts 1.2
     */
	protected boolean isChecked() throws JspException {
		Object result =
			TagUtils.getInstance().lookup(pageContext, name, property, null);

		if (result == null) {
			result = "";
		}

		result = result.toString();

		String checked = (String) result;
		return (
			checked.equalsIgnoreCase(this.value)
				|| checked.equalsIgnoreCase("true")
				|| checked.equalsIgnoreCase("yes")
				|| checked.equalsIgnoreCase("on"));

	}

    /**
     * Save the associated label from the body content.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() > 0) {
                text = value;
            }
        }
        return (SKIP_BODY);

    }

    /**
     * Process the remainder of this page normally.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doEndTag() throws JspException {

        // Render any description for this checkbox
        if (text != null) {
            TagUtils.getInstance().write(pageContext, text);
        }

        // Evaluate the remainder of this page
        return (EVAL_PAGE);

    }

    /**
     * Release any acquired resources.
     */
    public void release() {

        super.release();
        name = Constants.BEAN_KEY;
        property = null;
        text = null;
        value = null;

    }

}
