/*
 * $Header: /tmp/cvs-vintage/struts/src/examples/org/apache/struts/webapp/validator/MultiRegistrationAction.java,v 1.3 2004/03/14 06:23:49 sraeburn Exp $
 * $Revision: 1.3 $
 * $Date: 2004/03/14 06:23:49 $
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

package org.apache.struts.webapp.validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that validates a multi-page
 * registration form.
 *
 */
public final class MultiRegistrationAction extends Action {

    /**
     * Commons Logging instance.
     */
    private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception Exception if an input/output error or servlet exception occurs
     */
    public ActionForward execute(
        ActionMapping mapping,
        ActionForm form,
        HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {

        // Extract attributes we will need
        RegistrationForm info = (RegistrationForm) form;


        // Was this transaction cancelled?
        if (isCancelled(request)) {
            if (log.isInfoEnabled()) {
                log.info(
                    " "
                        + mapping.getAttribute()
                        + " - Registration transaction was cancelled");
            }

            removeFormBean(mapping, request);

            return mapping.findForward("success");
        }

        ActionMessages errors = info.validate(mapping, request);

        if (errors != null && errors.isEmpty()) {
            if (info.getPage() == 1)
                return mapping.findForward("input2");
                
            if (info.getPage() == 2)
                return mapping.findForward("success");
                
        } else {
            this.saveErrors(request, errors);
            
            if (info.getPage() == 1){
                return mapping.findForward("input" + info.getPage());
            }
                
            if (info.getPage() == 2){
                return mapping.findForward("input" + info.getPage());
            }
        }

        return mapping.findForward("input1");
    }

    /**
     * Convenience method for removing the obsolete form bean.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param request The HTTP request we are processing
     */
    protected void removeFormBean(
        ActionMapping mapping,
        HttpServletRequest request) {
            
        // Remove the obsolete form bean
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope())) {
                request.removeAttribute(mapping.getAttribute());
            } else {
                HttpSession session = request.getSession();
                session.removeAttribute(mapping.getAttribute());
            }
        }
    }
}
