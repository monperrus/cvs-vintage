package org.tigris.scarab.services.security;

/* ================================================================
 * Copyright (c) 2001 Collab.Net.  All rights reserved.
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


import org.apache.commons.collections.ExtendedProperties;
import org.apache.fulcrum.Service;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.TurbineServices;
import org.apache.torque.om.ObjectKey;
import org.tigris.scarab.om.ScarabUser;
import java.util.List;

/**
 * This class provides access to security properties
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id: ScarabSecurity.java,v 1.1 2001/10/26 23:12:26 jmcnally Exp $
 */
public class ScarabSecurity 
    extends BaseService
    implements Service
{
    /** The name of the service */
    public static final String SERVICE_NAME = "ScarabSecurity";

    public static final String TOOL_KEY = 
        "services.PullService.tool.request.security";

    private static final String SCREEN_PREFIX = "screen.";

    private static final String MAP_PREFIX = "map.";

    /** 
     * Specifies that a User is valid as an assignee for an issue.
     */
    public static final String ISSUE__EDIT = "Issue__Edit";

    /** 
     * Specifies that a User is allowed to enter an issue.
     */
    public static final String ISSUE__ENTER = "Issue__Enter";

    /** 
     * Specifies that a User is allowed to view an issue.
     */
    public static final String ISSUE__VIEW = "Issue__View";

    /** 
     * Specifies that a User is allowed to search for issues.
     */
    public static final String ISSUE__SEARCH = "Issue__Search";

    /** 
     * Specifies that a User is allowed to approve a query.
     */
    public static final String ITEM__APPROVE = "Item__Approve";

    /** 
     * Specifies that a User is allowed to delete a query.
     */
    public static final String ITEM__DELETE = "Item__Delete";

    /** 
     * Specifies that a User is allowed to edit preferences.
     */
    public static final String USER__EDIT_PREFERENCES = 
        "User__Edit_Preferences";

    /** 
     * Specifies that a User is allowed to edit a domain.
     */
    public static final String DOMAIN__EDIT = "Domain__Edit";

    /** 
     * Specifies that a User is allowed to modify a project.
     */
    public static final String MODULE__EDIT = "Module__Edit";

    /** 
     * Specifies that a User is allowed to add a project.
     */
    public static final String MODULE__ADD = "Module__Add";

    private ExtendedProperties props;

    public ScarabSecurity()
    {
    }

    public void init()
    {
        props = getConfiguration();
        setInit(true);
    }

    protected String getScreenPermissionImpl(String screen)
    {
        return props.getString(SCREEN_PREFIX + screen);
    }

    protected String getPermissionImpl(String permConstant)
    {
        return props.getString(MAP_PREFIX + permConstant);
    }


    // *******************************************************************
    // static accessors
    // *******************************************************************

    public static String getScreenPermission(String screen)
    {
        return getService().getScreenPermissionImpl(screen);
    }

    public static String getPermission(String permConstant)
    {
        return getService().getPermissionImpl(permConstant);
    }

    public static ExtendedProperties getProps()
    {
        return getService().getConfiguration();
    }

    /**
     * Gets the <code>LocalizationService</code> implementation.
     *
     * @return the LocalizationService implementation.
     */
    protected static final ScarabSecurity getService()
    {
        return (ScarabSecurity) TurbineServices.getInstance()
                .getService(ScarabSecurity.SERVICE_NAME);
    }

}
