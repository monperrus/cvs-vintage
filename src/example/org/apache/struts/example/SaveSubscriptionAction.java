/*
 * $Header: /tmp/cvs-vintage/struts/src/example/org/apache/struts/example/Attic/SaveSubscriptionAction.java,v 1.2 2000/06/15 18:00:02 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2000/06/15 18:00:02 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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


package org.apache.struts.example;


import java.io.IOException;
import java.util.Locale;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;


/**
 * Implementation of <strong>Action</strong> that validates and creates or updates
 * the mail subscription entered by the user.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2000/06/15 18:00:02 $
 */

public final class SaveSubscriptionAction extends ActionBase {


    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     *
     * @param servlet The ActionServlet making this request
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void perform(ActionServlet servlet,
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException, ServletException {


	// Extract attributes and parameters we will need
	Locale locale = getLocale(request);
	MessageResources messages = getResources(servlet);
	HttpSession session = request.getSession();
	SubscriptionForm subform = (SubscriptionForm) form;
	String action = request.getParameter("action");
	if (action == null)
	    action = "?";

	// Is there a currently logged on user?
	User user = (User) session.getAttribute(Constants.USER_KEY);
	if (user == null) {
	    String uri = Constants.LOGON_PAGE;
	    RequestDispatcher rd =
	      servlet.getServletContext().getRequestDispatcher(uri);
	    rd.forward(request, response);
	    return;
	}

	// Is there a related Subscription object?
	Subscription subscription =
	  (Subscription) session.getAttribute(Constants.SUBSCRIPTION_KEY);
	if (subscription == null) {
	    servlet.log("SaveSubscriptionAction:  " +
	                "Missing subscription for user '" +
	                 user.getUsername() + "'");
	    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
	                       messages.getMessage("error.noSubscription"));
	    return;
	}

	// Was this transaction cancelled?
	String submit = request.getParameter("submit");
	if (submit == null)
	    submit = "Submit";
	if (submit.equals(messages.getMessage(locale, "button.cancel"))) {
	    if (servlet.getDebug() >= 1)
	        servlet.log("SaveSubscriptionAction:  Transaction '" + action +
	                    "' was cancelled");
	    if (mapping.getFormAttribute() != null)
	        session.removeAttribute(mapping.getFormAttribute());
	    session.removeAttribute(Constants.SUBSCRIPTION_KEY);
	    String uri = ((ApplicationMapping) mapping).getSuccess();
	    RequestDispatcher rd =
	      servlet.getServletContext().getRequestDispatcher(uri);
	    rd.forward(request, response);
	    return;
	}

	// Was this transaction a Delete?
	if (action.equals("Delete")) {
	    if (servlet.getDebug() >= 1)
	        servlet.log("SaveSubscriptionAction:  Deleting mail server '" +
	                    subscription.getHost() + "' for user '" +
	                    user.getUsername() + "'");
	    subscription.setHost(null);
	    subscription.setUser(null);
	    if (mapping.getFormAttribute() != null)
	        session.removeAttribute(mapping.getFormAttribute());
	    session.removeAttribute(Constants.SUBSCRIPTION_KEY);
	    String uri = ((ApplicationMapping) mapping).getSuccess();
	    RequestDispatcher rd =
	      servlet.getServletContext().getRequestDispatcher(uri);
	    rd.forward(request, response);
	    return;
	}

	// Validate the request parameters specified by the user
	String value = null;
	Vector errors = new Vector();
	value = subform.getHost();
	if ((value == null) || (value.length() < 1))
	    errors.addElement("error.host.required");
	value = subform.getUsername();
	if ((value == null) || (value.length() < 1))
	    errors.addElement("error.username.required");
	value = subform.getPassword();
	if ((value == null) || (value.length() < 1))
	    errors.addElement("error.password.required");
	value = subform.getType();
	if ((value == null) || (value.length() < 1))
	    errors.addElement("error.type.required");
	else if (!"imap".equals(value) && !"pop3".equals(value))
	    errors.addElement("error.type.invalid");

	// Report any errors we have discovered back to the original form
	if (errors.size() > 0) {
	    saveErrors(request, errors);
	    String uri = ((ApplicationMapping) mapping).getFailure();
	    RequestDispatcher rd =
	      servlet.getServletContext().getRequestDispatcher(uri);
	    rd.forward(request, response);
	    return;
	}

	// Update the persistent subscription information
	if (subform.getHost().length() > 0)
	    subscription.setHost(subform.getHost());
	if (subform.getUsername().length() > 0)
	    subscription.setUsername(subform.getUsername());
	if (subform.getPassword().length() > 0)
	    subscription.setPassword(subform.getPassword());
	if (subform.getType().length() > 0)
	    subscription.setType(subform.getType());

	// Remove any obsolete session objects
	if (mapping.getFormAttribute() != null)
	    session.removeAttribute(mapping.getFormAttribute());
	session.removeAttribute(Constants.SUBSCRIPTION_KEY);

	// Forward control to the specified success URI
	String uri = ((ApplicationMapping) mapping).getSuccess();
	RequestDispatcher rd =
	  servlet.getServletContext().getRequestDispatcher(uri);
	rd.forward(request, response);


    }


}
