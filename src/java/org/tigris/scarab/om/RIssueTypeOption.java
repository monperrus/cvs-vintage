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

import java.util.List;
import java.util.ArrayList;
import org.apache.torque.util.Criteria;
import org.apache.torque.om.Persistent;
import org.tigris.scarab.util.ScarabConstants;
import org.tigris.scarab.util.ScarabException;
import org.tigris.scarab.services.security.ScarabSecurity;

/** 
 * You should add additional methods to this class to meet the
 * application requirements.  This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public  class RIssueTypeOption 
    extends org.tigris.scarab.om.BaseRIssueTypeOption
    implements Persistent
{

    public void delete( ScarabUser user, Module module )
         throws Exception
    {                
        if (user.hasPermission(ScarabSecurity.DOMAIN__EDIT, module))
        {
            List rios = getIssueType().getRIssueTypeOptions(getAttributeOption().getAttribute(), false);
            Criteria c = new Criteria()
                .add(RIssueTypeOptionPeer.ISSUE_TYPE_ID, getIssueTypeId())
                .add(RIssueTypeOptionPeer.OPTION_ID, getOptionId());
            RIssueTypeOptionPeer.doDelete(c);
            rios.remove(this);
            // Correct the ordering of the remaining options
            ArrayList optIds = new ArrayList();
            for (int i=0; i<rios.size();i++)
            {
                RIssueTypeOption rio = (RIssueTypeOption)rios.get(i);
                optIds.add(rio.getOptionId());
            }
            Criteria c2 = new Criteria()
                .addIn(RIssueTypeOptionPeer.OPTION_ID, optIds)
                .add(RIssueTypeOptionPeer.PREFERRED_ORDER, getOrder(), Criteria.GREATER_THAN);
            List adjustRios = RIssueTypeOptionPeer.doSelect(c2);
            for (int j=0; j<adjustRios.size();j++)
            {
                RIssueTypeOption rio = (RIssueTypeOption)adjustRios.get(j);
                //rmos.remove(rmo);
                rio.setOrder(rio.getOrder() -1);
                rio.save();
                //rmos.add(rmo);
            }
        }
        else
        {
            throw new ScarabException(ScarabConstants.NO_PERMISSION_MESSAGE);
        }            
    }

    /**
     * Copies this object's properties.
     */
    public RIssueTypeOption copyRio()
         throws Exception
    {                
        RIssueTypeOption rio = new RIssueTypeOption();
        rio.setIssueTypeId(getIssueTypeId());
        rio.setOptionId(getOptionId());
        rio.setActive(getActive());
        rio.setOrder(getOrder());
        rio.setWeight(getWeight());
        return rio;
    }
}
