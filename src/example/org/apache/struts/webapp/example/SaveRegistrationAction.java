/*
 * $Header: /tmp/cvs-vintage/struts/src/example/org/apache/struts/webapp/example/SaveRegistrationAction.java,v 1.22 2004/03/14 06:23:44 sraeburn Exp $
 * $Revision: 1.22 $
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

package org.apache.struts.webapp.example;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user registration information entered by the user.  If a new
 * registration is created, the user is also implicitly logged on.
 *
 * @version $Revision: 1.22 $ $Date: 2004/03/14 06:23:44 $
 */

public final class SaveRegistrationAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    private Log log = LogFactory.getLog("org.apache.struts.webapp.Example");

    // --------------------------------------------------------- Public Methods

        // See superclass for Javadoc
    public ActionForward execute(
        ActionMapping mapping,
        ActionForm form,
        HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {

        // Extract attributes and parameters we will need
        HttpSession session = request.getSession();
        RegistrationForm regform = (RegistrationForm) form;
        String action = regform.getAction();
        if (action == null) {
            action = "Create";
        }
        
        UserDatabase database =
            (UserDatabase) servlet.getServletContext().getAttribute(
                Constants.DATABASE_KEY);
                
        if (log.isDebugEnabled()) {
            log.debug("SaveRegistrationAction:  Processing " + action + " action");
        }

        // Is there a currently logged on user (unless creating)?
        User user = (User) session.getAttribute(Constants.USER_KEY);
        if (!"Create".equals(action) && (user == null)) {
            if (log.isTraceEnabled()) {
                log.trace(" User is not logged on in session " + session.getId());
            }
            return (mapping.findForward("logon"));
        }

        // Was this transaction cancelled?
        if (isCancelled(request)) {
            if (log.isTraceEnabled()) {
                log.trace(" Transaction '" + action + "' was cancelled");
            }
            session.removeAttribute(Constants.SUBSCRIPTION_KEY);
            return (mapping.findForward("success"));
        }

        // Validate the transactional control token
        ActionMessages errors = new ActionMessages();
        if (log.isTraceEnabled()) {
            log.trace(" Checking transactional control token");
        }
        
        if (!isTokenValid(request)) {
            errors.add(
                ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage("error.transaction.token"));
        }
        
        resetToken(request);

        // Validate the request parameters specified by the user
        if (log.isTraceEnabled()) {
            log.trace(" Performing extra validations");
        }
        
        String value = null;
        value = regform.getUsername();
        if (("Create".equals(action)) && (database.findUser(value) != null)) {
            errors.add(
                "username",
                new ActionMessage("error.username.unique", regform.getUsername()));
        }
        
        if ("Create".equals(action)) {
            value = regform.getPassword();
            if ((value == null) || (value.length() < 1)) {
                errors.add("password", new ActionMessage("error.password.required"));
            }
            
            value = regform.getPassword2();
            
            if ((value == null) || (value.length() < 1)) {
                errors.add(
                    "password2",
                    new ActionMessage("error.password2.required"));
            }
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            this.saveErrors(request, errors);
            this.saveToken(request);
            return (mapping.getInputForward());
        }

        // Update the user's persistent profile information
        try {
            if ("Create".equals(action)) {
                user = database.createUser(regform.getUsername());
            }
            
            String oldPassword = user.getPassword();
            PropertyUtils.copyProperties(user, regform);
            if ((regform.getPassword() == null)
                || (regform.getPassword().length() < 1)) {
                    
                user.setPassword(oldPassword);
            }
            
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null) {
                t = e;
            }
            
            log.error("Registration.populate", t);
            throw new ServletException("Registration.populate", t);
            
        } catch (Throwable t) {
            log.error("Registration.populate", t);
            throw new ServletException("Subscription.populate", t);
        }

        try {
            database.save();
        } catch (Exception e) {
            log.error("Database save", e);
        }

        // Log the user in if appropriate
        if ("Create".equals(action)) {
            session.setAttribute(Constants.USER_KEY, user);
            if (log.isTraceEnabled()) {
                log.trace(
                    " User '"
                        + user.getUsername()
                        + "' logged on in session "
                        + session.getId());
            }
        }

        // Remove the obsolete form bean
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getAttribute());
            else
                session.removeAttribute(mapping.getAttribute());
        }

        // Forward control to the specified success URI
        if (log.isTraceEnabled()) {
            log.trace(" Forwarding to success page");
        }
        
        return (mapping.findForward("success"));

    }

}
