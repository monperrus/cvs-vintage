/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/bean/ELResourceTag.java,v 1.8 2004/03/14 07:15:04 sraeburn Exp $
 * $Revision: 1.8 $
 * $Date: 2004/03/14 07:15:04 $
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

package org.apache.strutsel.taglib.bean;

import org.apache.struts.taglib.bean.ResourceTag;
import javax.servlet.jsp.JspException;
import org.apache.strutsel.taglib.utils.EvalHelper;

/**
 * Define a scripting variable based on the contents of the specified
 * web application resource.
 *<p>
 * This class is a subclass of the class
 * <code>org.apache.struts.taglib.bean.ResourceTag</code> which provides most of
 * the described functionality.  This subclass allows all attribute values to
 * be specified as expressions utilizing the JavaServer Pages Standard Library
 * expression language.
 *
 * @version $Revision: 1.8 $
 */
public class ELResourceTag extends ResourceTag {

    /**
     * Instance variable mapped to "id" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String idExpr;
    /**
     * Instance variable mapped to "input" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String inputExpr;
    /**
     * Instance variable mapped to "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String nameExpr;

    /**
     * Getter method for "id" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getIdExpr() { return (idExpr); }
    /**
     * Getter method for "input" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getInputExpr() { return (inputExpr); }
    /**
     * Getter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getNameExpr() { return (nameExpr); }

    /**
     * Setter method for "id" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setIdExpr(String idExpr) { this.idExpr = idExpr; }
    /**
     * Setter method for "input" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setInputExpr(String inputExpr) { this.inputExpr = inputExpr; }
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
        setIdExpr(null);
        setInputExpr(null);
        setNameExpr(null);
    }
    
    /**
     * Process the start tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
        evaluateExpressions();
        return (super.doStartTag());
    }

    /**
     * Processes all attribute values which use the JSTL expression evaluation
     * engine to determine their values.
     *
     * @exception JspException if a JSP exception has occurred
     */
    private void evaluateExpressions() throws JspException {
        String  string  = null;

        if ((string = EvalHelper.evalString("id", getIdExpr(),
                                            this, pageContext)) != null)
            setId(string);

        if ((string = EvalHelper.evalString("input", getInputExpr(),
                                            this, pageContext)) != null)
            setInput(string);

        if ((string = EvalHelper.evalString("name", getNameExpr(),
                                            this, pageContext)) != null)
            setName(string);
    }
}
