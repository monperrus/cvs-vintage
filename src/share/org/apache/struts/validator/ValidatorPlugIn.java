/*
 * $Header: /tmp/cvs-vintage/struts/src/share/org/apache/struts/validator/ValidatorPlugIn.java,v 1.26 2004/08/07 04:17:52 martinc Exp $
 * $Revision: 1.26 $
 * $Date: 2004/08/07 04:17:52 $
 *
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.struts.validator;

import org.xml.sax.SAXException;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * Loads <code>ValidatorResources</code> based on configuration in the
 * struts-config.xml file.
 *
 * @version $Revision: 1.26 $ $Date: 2004/08/07 04:17:52 $
 * @since Struts 1.1
 */
public class ValidatorPlugIn implements PlugIn {

    /**
     * Commons Logging instance.
     */
    private static Log log = LogFactory.getLog(ValidatorPlugIn.class);

    /**
     * The module configuration for our owning module.
     */
    private ModuleConfig config = null;

    /**
     * The {@link ActionServlet} owning this application.
     */
    private ActionServlet servlet = null;

    /**
     * Delimitter for Validator resources.
     */
    private final static String RESOURCE_DELIM = ",";

    /**
     * Application scope key that <code>ValidatorResources</code> is stored under.
     */
    public final static String VALIDATOR_KEY =
            "org.apache.commons.validator.VALIDATOR_RESOURCES";

    /**
     * Application scope key that <code>StopOnError</code> is stored under.
     *
     * @since Struts 1.2
     */
    public final static String STOP_ON_ERROR_KEY =
            "org.apache.struts.validator.STOP_ON_ERROR";

    /**
     * The set of Form instances that have been created and initialized,
     * keyed by the struts form name.
     */
    protected ValidatorResources resources = null;


    // ------------------------------------------------------------- Properties

    /**
     * A comma delimitted list of Validator resource.
     */
    private String pathnames = null;

    /**
     * Gets a comma delimitted list of Validator resources.
     *
     * @return comma delimited list of Validator resource path names
     */
    public String getPathnames() {
        return pathnames;
    }

    /**
     * Sets a comma delimitted list of Validator resources.
     *
     * @param pathnames delimited list of Validator resource path names
     */
    public void setPathnames(String pathnames) {
        this.pathnames = pathnames;
    }

    /**
     * Informs the Validators if it has to stop validation when finding the
     * first error or if it should continue.  Default to <code>true</code> to
     * keep Struts 1.1 backwards compatibility.
     */
    private boolean stopOnFirstError = true;

    /**
     * Gets the value for stopOnFirstError.
     *
     * @return A boolean indicating whether JavaScript validation should stop
     *         when it finds the first error (Struts 1.1 behaviour) or continue
     *         validation.
     * @since Struts 1.2
     */
    public boolean isStopOnFirstError() {
        return this.stopOnFirstError;
    }

    /**
     * Sets the value for stopOnFirstError.
     *
     * @param stopOnFirstError A boolean indicating whether JavaScript
     *                         validation should stop when it finds the first error
     *                         (Struts 1.1 behaviour) or continue validation.
     * @since Struts 1.2
     */
    public void setStopOnFirstError(boolean stopOnFirstError) {
        this.stopOnFirstError = stopOnFirstError;
    }

    /**
     * Initialize and load our resources.
     *
     * @param servlet The ActionServlet for our application
     * @param config  The ModuleConfig for our owning module
     * @throws ServletException if we cannot configure ourselves correctly
     */
    public void init(ActionServlet servlet, ModuleConfig config)
            throws ServletException {

        // Remember our associated configuration and servlet
        this.config = config;
        this.servlet = servlet;

        // Load our database from persistent storage
        try {
            this.initResources();

            servlet.getServletContext().setAttribute(VALIDATOR_KEY + config.getPrefix(),
                                                     resources);

            servlet.getServletContext().setAttribute(STOP_ON_ERROR_KEY + '.' + config.getPrefix(),
                                                     Boolean.valueOf(this.stopOnFirstError));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UnavailableException("Cannot load a validator resource from '" + pathnames + "'");
        }

    }

    /**
     * Gracefully shut down, releasing any resources
     * that were allocated at initialization.
     */
    public void destroy() {

        if (log.isDebugEnabled()) {
            log.debug("Destroying ValidatorPlugin");
        }

        servlet = null;
        config = null;

        destroyResources();
    }

    /**
     * Initialize the validator resources for this module.
     *
     * @throws IOException      if an input/output error is encountered
     * @throws ServletException if we cannot initialize these resources
     */
    protected void initResources() throws IOException, ServletException {

        if (pathnames == null || pathnames.length() <= 0) {
            return;
        }
        StringTokenizer st = new StringTokenizer(pathnames, RESOURCE_DELIM);

        List streamList = new ArrayList();
        try {
            while (st.hasMoreTokens()) {
                String validatorRules = st.nextToken().trim();
                if (log.isInfoEnabled()) {
                    log.info("Loading validation rules file from '" + validatorRules + "'");
                }

                InputStream input = servlet.getServletContext().getResourceAsStream(validatorRules);

                if (input != null) {
                    BufferedInputStream bis = new BufferedInputStream(input);
                    streamList.add(bis);
                } else {
                    throw new ServletException("Skipping validation rules file from '"
                              + validatorRules + "'.  No stream could be opened.");
                }
            }
            int streamSize = streamList.size();
            InputStream[] streamArray = new InputStream[streamSize];
            for (int streamIndex = 0;streamIndex < streamSize;streamIndex++) {
                InputStream is = (InputStream) streamList.get(streamIndex);
                streamArray[streamIndex] = is;
            }

            this.resources = new ValidatorResources(streamArray);
        } catch (SAXException sex) {
            log.error("Skipping all validation",sex);
            throw new ServletException(sex);
        } finally {
            Iterator streamIterator = streamList.iterator();
            while (streamIterator.hasNext()) {
                InputStream is = (InputStream) streamIterator.next();
                is.close();
            }
        }

    }

    /**
     * Destroy <code>ValidatorResources</code>.
     */
    protected void destroyResources() {
        resources = null;
    }

}
