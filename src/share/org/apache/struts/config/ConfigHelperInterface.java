/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/config/ConfigHelperInterface.java,v 1.10 2004/01/10 21:03:33 dgraham Exp $
 * $Revision: 1.10 $
 * $Date: 2004/01/10 21:03:33 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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


package org.apache.struts.config;


import javax.sql.DataSource;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionFormBean;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.MessageResources;


/**
 * NOTE: THIS CLASS IS UNDER ACTIVE DEVELOPMENT.
 * THE CURRENT CODE IS WRITTEN FOR CLARITY NOT EFFICIENCY.
 * NOT EVERY API FUNCTION HAS BEEN IMPLEMENTED YET.
 *
 * A helper object to expose the Struts shared resources,
 * which are be stored in the application, session, or
 * request contexts, as appropriate.
 *
 * An instance should be created for each request
 * processed. The  methods which return resources from
 * the request or session contexts are not thread-safe.
 *
 * Provided for use by other servlets in the application
 * so they can easily access the Struts shared resources.
 *
 * The resources are stored under attributes in the
 * application, session, or request contexts.
 *
 * The ActionConfig methods simply return the resources
 * from under the context and key used by the Struts
 * ActionServlet when the resources are created.
 *
 * @since Struts 1.1
 * @version $Revision: 1.10 $ $Date: 2004/01/10 21:03:33 $
 */
public interface ConfigHelperInterface {


// ------------------------------------------------ Application Context

    public DataSource getDataSource();

    /**
     * The <code>org.apache.struts.action.ActionFormBeans</code> collection
     * for this application.
     */
    public ActionMessages getActionMessages();

    /**
     * The application resources for this application.
     */
    public MessageResources getMessageResources();

    /**
     * The path-mapped pattern (<code>/action/*</code>) or
     * extension mapped pattern ((<code>*.do</code>)
     * used to determine our Action URIs in this application.
     */
    public String getServletMapping();


// ---------------------------------------------------- Session Context

    /**
     * The transaction token stored in this session, if it is used.
     */
    public String getToken();


// ---------------------------------------------------- Request Context


    /**
     * The runtime JspException that may be been thrown by a Struts tag
     * extension, or compatible presentation extension, and placed
     * in the request.
     */
    public Throwable getException();


    /**
     * The multipart object for this request.
     */
    public MultipartRequestWrapper getMultipartRequestWrapper();


   /**
     * The <code>org.apache.struts.ActionMapping</code>
     * instance for this request.
     */
    public ActionMapping getMapping();



// ---------------------------------------------------- Utility Methods

    /**
     * Return true if a message string for the specified message key
     * is present for the user's Locale.
     *
     * @param key Message key
     */
    public boolean isMessage(String key);



    /*
     * Retrieve and return the <code>ActionForm</code> bean associated with
     * this mapping, creating and stashing one if necessary.  If there is no
     * form bean associated with this mapping, return <code>null</code>.
     *
     */
     public ActionForm getActionForm();



    /**
     * Return the form bean definition associated with the specified
     * logical name, if any; otherwise return <code>null</code>.
     *
     * @param name Logical name of the requested form bean definition
     */
    public ActionFormBean getFormBean(String name);



    /**
     * Return the forwarding associated with the specified logical name,
     * if any; otherwise return <code>null</code>.
     *
     * @param name Logical name of the requested forwarding
     */
    public ActionForward getActionForward(String name);



    /**
     * Return the mapping associated with the specified request path, if any;
     * otherwise return <code>null</code>.
     *
     * @param path Request path for which a mapping is requested
     */
    public ActionMapping getActionMapping(String path);



    /**
     * Return the form action converted into an action mapping path.  The
     * value of the <code>action</code> property is manipulated as follows in
     * computing the name of the requested mapping:
     * <ul>
     * <li>Any filename extension is removed (on the theory that extension
     *     mapping is being used to select the controller servlet).</li>
     * <li>If the resulting value does not start with a slash, then a
     *     slash is prepended.</li>
     * </ul>
     * :FIXME: Bad assumption =:o)
     */
    public String getActionMappingName(String action);



    /**
     * Return the form action converted into a server-relative URL.
     */
    public String getActionMappingURL(String action);



    /**
     * Return the url encoded to maintain the user session, if any.
     */
    public String getEncodeURL(String url);



// ------------------------------------------------ Presentation API


    /**
     * Renders the reference for a HTML <base> element
     */
    public String getOrigRef();



    /**
     * Renders the reference for a HTML <base> element
     */
    public String getBaseRef();



    /**
     * Return the path for the specified forward,
     * otherwise return <code>null</code>.
     *
     * @param name Name given to local or global forward.
     */
     public String getLink(String name);


    /**
     * Return the localized message for the specified key,
     * otherwise return <code>null</code>.
     *
     * @param key Message key
     */
    public String getMessage(String key);


    /**
     * Look up and return a message string, based on the specified parameters.
     *
     * @param key Message key to be looked up and returned
     * @param args Replacement parameters for this message
     */
    public String getMessage(String key, Object args[]);


    /**
     * Return the URL for the specified ActionMapping,
     * otherwise return <code>null</code>.
     *
     * @param path Name given to local or global forward.
     */
    public String getAction(String path);


}
