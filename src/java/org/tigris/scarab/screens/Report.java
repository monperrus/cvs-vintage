package org.tigris.scarab.screens;

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

import java.util.*;

// Velocity  Stuff 
import org.apache.turbine.services.velocity.*; 
import org.apache.velocity.*; 
import org.apache.velocity.context.*; 

// Turbine Stuff 
import org.apache.turbine.modules.*; 
import org.apache.turbine.modules.screens.*; 
import org.apache.turbine.util.*; 

// Scarab Stuff
import org.tigris.scarab.om.*;
import org.tigris.scarab.baseom.*;
import org.tigris.scarab.baseom.peer.*;

/**
    This class is responsible for building the Context up
    for the Report Screen.

    @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
    @version $Id: Report.java,v 1.3 2001/01/23 22:43:24 jmcnally Exp $
*/
public class Report extends VelocityScreen
{
    /**
        builds up the context for display of variables on the page.
    */
    public void doBuildTemplate( RunData data, Context context ) 
        throws Exception 
    {
        HashMap classes = new HashMap();
        context.put("classes", classes);

        HashMap report = new HashMap();
        context.put("report", report);
        
        Module module = new Module( ScarabModulePeer.retrieveByPK(5) );

        ScarabUser user = new ScarabUser();
        user.setPrimaryKey(new Integer(2));
        user.setCurrentModule(module);
        context.put("user", user );

        System.out.println("getting user modules");
        user.getModules();
        System.out.println("done");


        classes.put("Module", module);
        
        Issue issue = new Issue();
        report.put( "issue", issue );
        ScarabIssue sIssue = issue.getScarabIssue();
        sIssue.setModuleId( module.getScarabModule().getModuleId() );
        sIssue.setModifiedBy(user.getPrimaryKeyAsInt());
        sIssue.setCreatedBy(user.getPrimaryKeyAsInt());
        java.util.Date now = new java.util.Date();
        sIssue.setModifiedDate(now);
        sIssue.setCreatedDate(now);
        sIssue.setDeleted(false);
    }
}




