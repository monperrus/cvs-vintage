/*
 * $Header: /tmp/cvs-vintage/struts/src/example/org/apache/struts/webapp/example/SaveSubscriptionAction.java,v 1.19 2004/03/14 06:23:44 sraeburn Exp $
 * $Revision: 1.19 $
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
import org.apache.struts.util.MessageResources;

/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the mail subscription entered by the user.
 *
 * @version $Revision: 1.19 $ $Date: 2004/03/14 06:23:44 $
 */

public final class SaveSubscriptionAction extends Action {

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
        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();
        SubscriptionForm subform = (SubscriptionForm) form;
        String action = subform.getAction();
        if (action == null) {
            action = "?";
        }
        if (log.isDebugEnabled()) {
            log.debug("SaveSubscriptionAction:  Processing " + action + " action");
        }

        // Is there a currently logged on user?
        User user = (User) session.getAttribute(Constants.USER_KEY);
        if (user == null) {
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

        // Is there a related Subscription object?
        Subscription subscription =
            (Subscription) session.getAttribute(Constants.SUBSCRIPTION_KEY);
        if ("Create".equals(action)) {
            subscription = user.createSubscription(request.getParameter("host"));
        }
        if (subscription == null) {
            if (log.isTraceEnabled()) {
                log.trace(
                    " Missing subscription for user '" + user.getUsername() + "'");
            }
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                messages.getMessage("error.noSubscription"));
            return (null);
        }

        // Was this transaction a Delete?
        if (action.equals("Delete")) {
            if (log.isTraceEnabled()) {
                log.trace(
                    " Deleting mail server '"
                        + subscription.getHost()
                        + "' for user '"
                        + user.getUsername()
                        + "'");
            }
            user.removeSubscription(subscription);
            session.removeAttribute(Constants.SUBSCRIPTION_KEY);
            try {
                UserDatabase database =
                    (UserDatabase) servlet.getServletContext().getAttribute(
                        Constants.DATABASE_KEY);
                database.save();
            } catch (Exception e) {
                log.error("Database save", e);
            }
            return (mapping.findForward("success"));
        }

        // All required validations were done by the form itself

        // Update the persistent subscription information
        if (log.isTraceEnabled()) {
            log.trace(" Populating database from form bean");
        }
        try {
            PropertyUtils.copyProperties(subscription, subform);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null)
                t = e;
            log.error("Subscription.populate", t);
            throw new ServletException("Subscription.populate", t);
        } catch (Throwable t) {
            log.error("Subscription.populate", t);
            throw new ServletException("Subscription.populate", t);
        }

        try {
            UserDatabase database =
                (UserDatabase) servlet.getServletContext().getAttribute(
                    Constants.DATABASE_KEY);
            database.save();
        } catch (Exception e) {
            log.error("Database save", e);
        }

        // Remove the obsolete form bean and current subscription
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getAttribute());
            else
                session.removeAttribute(mapping.getAttribute());
        }
        session.removeAttribute(Constants.SUBSCRIPTION_KEY);

        // Forward control to the specified success URI
        if (log.isTraceEnabled()) {
            log.trace(" Forwarding to success page");
        }
        return (mapping.findForward("success"));

    }

}
