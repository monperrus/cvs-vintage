package org.tigris.scarab.actions;

/* ================================================================
 * Copyright (c) 2000 Collab.Net.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement: "This product includes
 * software developed by Collab.Net <http://www.Collab.Net/>."
 * Alternately, this acknowlegement may appear in the software itself, if
 * and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The hosted project names must not be used to endorse or promote
 * products derived from this software without prior written
 * permission. For written permission, please contact info@collab.net.
 * 
 * 5. Products derived from this software may not use the "Tigris" or 
 * "Scarab" names nor may "Tigris" or "Scarab" appear in their names without 
 * prior written permission of Collab.Net.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL COLLAB.NET OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Collab.Net.
 */ 

// Turbine Stuff 
import org.apache.turbine.TemplateAction;
import org.apache.turbine.services.template.TemplateContext;
import org.apache.turbine.RunData;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.services.pull.TurbinePull;

// Scarab Stuff
import org.tigris.scarab.om.ScarabUser;
import org.tigris.scarab.om.ScarabUserImpl;
import org.tigris.scarab.tools.ScarabRequestTool;
import org.tigris.scarab.util.ScarabConstants;

/**
    This class is responsible for dealing with the Confirm
    Action.
    
    @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
    @version $Id: Confirm.java,v 1.11 2001/07/17 01:38:40 jmcnally Exp $
*/
public class Confirm extends TemplateAction
{
    /**
        This manages clicking the Register button which will end up sending
        the user to the RegisterConfirm screen.
    */
    public void doConfirm( RunData data, TemplateContext context ) throws Exception
    {
        String template = data.getParameters().getString(ScarabConstants.TEMPLATE, null);
        String nextTemplate = data.getParameters().getString(
            ScarabConstants.NEXT_TEMPLATE, template );

        String username = data.getParameters().getString ( "Email", "" );
        String confirm = data.getParameters().getString ( "Confirm", "" );

        // check to see if the user's confirmation code is valid.
        if (ScarabUserImpl.checkConfirmationCode(username, confirm))
        {
            // update the database to confirm the user
            if(ScarabUserImpl.confirmUser(username))
            {
                // NO PROBLEMS! :-)
                data.setMessage("Your account has been confirmed. Welcome to Scarab!");
                setTarget(data, nextTemplate);
            }
            else
            {
                data.setMessage("Your account has not been confirmed. There has been an error.");
                setTarget(data, template);
            }
        }
        else // we don't have confirmation! :-(
        {
            // grab the ScarabRequestTool object so that we can populate the internal User object
            // for redisplay of the form data on the screen
            ApplicationTool srt = TurbinePull.getTool(context, 
                ScarabConstants.SCARAB_REQUEST_TOOL);
            if (srt != null)
            {
                ((ScarabRequestTool)srt).setUser((ScarabUser)data.getUser().getTemp( 
                    ScarabConstants.SESSION_REGISTER));
            }
        
            data.setMessage("Sorry, that email address and/or confirmation code is invalid.");
            setTarget(data, template);
        }
    }
    /**
        This manages clicking the Cancel button
    */
    public void doCancel( RunData data, TemplateContext context ) throws Exception
    {
        setTarget(data, data.getParameters().getString(
                ScarabConstants.CANCEL_TEMPLATE, "Login.vm"));
    }
    /**
        calls doCancel()
    */
    public void doPerform( RunData data, TemplateContext context ) throws Exception
    {
        doCancel(data, context);
    }
}
