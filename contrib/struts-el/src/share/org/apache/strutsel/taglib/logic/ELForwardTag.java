/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/logic/ELForwardTag.java,v 1.8 2004/03/14 07:15:05 sraeburn Exp $
 * $Revision: 1.8 $
 * $Date: 2004/03/14 07:15:05 $
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

package org.apache.strutsel.taglib.logic;

import org.apache.struts.taglib.logic.ForwardTag;
import javax.servlet.jsp.JspException;
import org.apache.strutsel.taglib.utils.EvalHelper;

/**
 * Perform a forward or redirect to a page that is looked up in the
 * configuration information associated with our application.
 *<p>
 * This class is a subclass of the class
 * <code>org.apache.struts.taglib.logix.ForwardTag</code> which provides most of
 * the described functionality.  This subclass allows all attribute values to
 * be specified as expressions utilizing the JavaServer Pages Standard Library
 * expression language.
 *
 * @version $Revision: 1.8 $
 */
public class ELForwardTag extends ForwardTag {

    /**
     * Instance variable mapped to "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String nameExpr;

    /**
     * Getter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getNameExpr() { return (nameExpr); }

    /**
     * Setter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setNameExpr(String nameExpr) { this.nameExpr = nameExpr; }

    /**
     * Resets attribute values for tag reuse.
     */
    public void release()
    {
        super.release();
        setNameExpr(null);
    }
    
    /**
     * Process the start tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
        evaluateExpressions();
        return(super.doStartTag());
    }

    /**
     * Processes all attribute values which use the JSTL expression evaluation
     * engine to determine their values.
     *
     * @exception JspException if a JSP exception has occurred
     */
    private void evaluateExpressions() throws JspException {
        String  string  = null;

        if ((string = EvalHelper.evalString("name", getNameExpr(),
                                            this, pageContext)) != null)
            setName(string);
    }
}
