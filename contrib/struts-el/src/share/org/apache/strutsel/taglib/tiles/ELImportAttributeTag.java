/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/tiles/ELImportAttributeTag.java,v 1.3 2004/03/14 07:15:04 sraeburn Exp $
 * $Revision: 1.3 $
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

package org.apache.strutsel.taglib.tiles;

import org.apache.struts.taglib.tiles.ImportAttributeTag;
import javax.servlet.jsp.JspException;
import org.apache.strutsel.taglib.utils.EvalHelper;

/**
  *  Import attribute from component to requested scope.
  *  Attribute name and scope are optional. If not specified, all component
  *  attributes are imported in page scope.
 *<p>
 * This class is a subclass of the class
 * <code>org.apache.struts.taglib.tiles.ImportAttributeTag</code> which provides most of
 * the described functionality.  This subclass allows all attribute values to
 * be specified as expressions utilizing the JavaServer Pages Standard Library
 * expression language.
 *
 * @version $Revision: 1.3 $
 */
public class ELImportAttributeTag extends ImportAttributeTag {

    /**
     * Instance variable mapped to "scope" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String scopeExpr;
    /**
     * Instance variable mapped to "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String nameExpr;
    /**
     * Instance variable mapped to "ignore" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String ignoreExpr;

    /**
     * Getter method for "scope" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getScopeExpr() { return (scopeExpr); }
    /**
     * Getter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getNameExpr() { return (nameExpr); }
    /**
     * Getter method for "ignore" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getIgnoreExpr() { return (ignoreExpr); }

    /**
     * Setter method for "scope" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setScopeExpr(String scopeExpr) { this.scopeExpr = scopeExpr; }
    /**
     * Setter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setNameExpr(String nameExpr) { this.nameExpr = nameExpr; }
    /**
     * Setter method for "ignore" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setIgnoreExpr(String ignoreExpr) { this.ignoreExpr = ignoreExpr; }

    /**
     * Resets attribute values for tag reuse.
     */
    public void release()
    {
        super.release();
        setScopeExpr(null);
        setNameExpr(null);
        setIgnoreExpr(null);
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
        Boolean bool    = null;

        if ((string = EvalHelper.evalString("scope", getScopeExpr(),
                                            this, pageContext)) != null)
            setScope(string);
        if ((string = EvalHelper.evalString("name", getNameExpr(),
                                            this, pageContext)) != null)
            setName(string);
        if ((bool = EvalHelper.evalBoolean("ignore", getIgnoreExpr(),
                                           this, pageContext)) != null)
            setIgnore(bool.booleanValue());
    }
}
