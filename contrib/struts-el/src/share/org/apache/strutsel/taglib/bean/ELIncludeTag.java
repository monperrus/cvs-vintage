/*
 * $Header: /tmp/cvs-vintage/struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/bean/ELIncludeTag.java,v 1.5 2004/03/14 07:15:04 sraeburn Exp $
 * $Revision: 1.5 $
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

import org.apache.struts.taglib.bean.IncludeTag;
import javax.servlet.jsp.JspException;
import org.apache.strutsel.taglib.utils.EvalHelper;

/**
 * Generate a URL-encoded include to the specified URI.
 *<p>
 * This class is a subclass of the class
 * <code>org.apache.struts.taglib.bean.IncludeTag</code> which
 * provides most of the described functionality.  This subclass allows all
 * attribute values to be specified as expressions utilizing the JavaServer
 * Pages Standard Library expression language.
 *
 * @version $Revision: 1.5 $
 */
public class ELIncludeTag extends IncludeTag {

    /**
     * Instance variable mapped to "anchor" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String anchorExpr;
    /**
     * Instance variable mapped to "forward" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String forwardExpr;
    /**
     * Instance variable mapped to "href" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String hrefExpr;
    /**
     * Instance variable mapped to "id" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String idExpr;
    /**
     * Instance variable mapped to "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String nameExpr;
    /**
     * Instance variable mapped to "page" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String pageExpr;
    /**
     * Instance variable mapped to "transaction" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String transactionExpr;

    /**
     * Getter method for "anchor" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getAnchorExpr() { return (anchorExpr); }
    /**
     * Getter method for "forward" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getForwardExpr() { return (forwardExpr); }
    /**
     * Getter method for "href" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getHrefExpr() { return (hrefExpr); }
    /**
     * Getter method for "id" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getIdExpr() { return (idExpr); }
    /**
     * Getter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getNameExpr() { return (nameExpr); }
    /**
     * Getter method for "page" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getPageExpr() { return (pageExpr); }
    /**
     * Getter method for "transaction" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getTransactionExpr() { return (transactionExpr); }

    /**
     * Setter method for "anchor" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setAnchorExpr(String anchorExpr) { this.anchorExpr = anchorExpr; }
    /**
     * Setter method for "forward" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setForwardExpr(String forwardExpr) { this.forwardExpr = forwardExpr; }
    /**
     * Setter method for "href" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setHrefExpr(String hrefExpr) { this.hrefExpr = hrefExpr; }
    /**
     * Setter method for "id" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setIdExpr(String idExpr) { this.idExpr = idExpr; }
    /**
     * Setter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setNameExpr(String nameExpr) { this.nameExpr = nameExpr; }
    /**
     * Setter method for "page" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setPageExpr(String pageExpr) { this.pageExpr = pageExpr; }
    /**
     * Setter method for "transaction" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setTransactionExpr(String transactionExpr) { this.transactionExpr = transactionExpr; }

    /**
     * Resets attribute values for tag reuse.
     */
    public void release()
    {
        super.release();
        setAnchorExpr(null);
        setForwardExpr(null);
        setHrefExpr(null);
        setIdExpr(null);
        setNameExpr(null);
        setPageExpr(null);
        setTransactionExpr(null);
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

        if ((string = EvalHelper.evalString("anchor", getAnchorExpr(),
                                            this, pageContext)) != null)
            setAnchor(string);

        if ((string = EvalHelper.evalString("forward", getForwardExpr(),
                                            this, pageContext)) != null)
            setForward(string);

        if ((string = EvalHelper.evalString("href", getHrefExpr(),
                                            this, pageContext)) != null)
            setHref(string);

        if ((string = EvalHelper.evalString("id", getIdExpr(),
                                            this, pageContext)) != null)
            setId(string);

        if ((string = EvalHelper.evalString("name", getNameExpr(),
                                            this, pageContext)) != null)
            setName(string);

        if ((string = EvalHelper.evalString("page", getPageExpr(),
                                            this, pageContext)) != null)
            setPage(string);

        if ((bool = EvalHelper.evalBoolean("transaction", getTransactionExpr(),
                                           this, pageContext)) != null)
            setTransaction(bool.booleanValue());
    }
}
