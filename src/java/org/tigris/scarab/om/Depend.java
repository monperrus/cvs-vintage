package org.tigris.scarab.om;

/* ================================================================
 * Copyright (c) 2000-2002 CollabNet.  All rights reserved.
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

// Turbine classes
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.torque.TorqueException;

import org.apache.torque.om.NumberKey;

// Scarab classes
import org.tigris.scarab.om.Module;
import org.tigris.scarab.util.ScarabException;

/** 
 * This class represents a Dependency object from the SCARAB_DEPEND table.
 *
 * @author <a href="mailto:jmcnally@collab.new">John McNally</a>
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:elicia@collab.net">Elicia David</a>
 * @version $Id: Depend.java,v 1.13 2002/10/04 01:51:32 jon Exp $
 */
public class Depend 
    extends BaseDepend
    implements Persistent
{

    /**
     * A Module object which represents the default module.
     */
    private Module defaultModule = null;

    /**
     * A Module object which represents the default module.
     */
    private String observerUniqueId = null;

    /**
     * A new Depend object
     * @deprecated See DependManager.getInstance()
     */
    public static Depend getInstance()
        throws TorqueException
    {
        return DependManager.getInstance();
    }

    /**
     * Sets default module.
     */
    public void setDefaultModule(Module me) 
    {
        this.defaultModule = me;
    }

    /**
     * Returns default module.
     */
    public Module getDefaultModule()
    {
        return defaultModule;
    }

    /**
     * Getter method to get observerId as a String
     */
    public String getObserverUniqueId()
         throws Exception
    {
         return observerUniqueId;
    }

    public void setDependType(String type)
        throws TorqueException
    {
        super.setDependType(DependTypeManager.getInstance(type));
    }

    /**
     * Setter method which takes a String - the unique id.
     */
    public void setObserverUniqueId(String uniqueId)
         throws Exception
    {
        if (getDefaultModule() == null)
        {
            throw new ScarabException("You need to call " + 
                "setDefaultModule() before you can call this method.");
        }
        Issue childIssue = null;
	    childIssue = Issue.getIssueById(uniqueId);
	    if (childIssue == null)
        {
           String code = getDefaultModule().getCode();
           uniqueId = code + uniqueId;
           childIssue = Issue.getIssueById(uniqueId);
        }
        super.setObserverId(childIssue.getIssueId());
    }
        
}



