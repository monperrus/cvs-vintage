package org.tigris.scarab.om;

/* ================================================================
 * Copyright (c) 2000-2001 CollabNet.  All rights reserved.
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

// Turbine classes
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.turbine.Log;

import org.tigris.scarab.services.security.ScarabSecurity;
import org.tigris.scarab.services.module.ModuleEntity;
import org.tigris.scarab.util.ScarabConstants;
import org.tigris.scarab.util.ScarabException;

/** 
  * The skeleton for this class was autogenerated by Torque on:
  *
  * [Wed Feb 28 16:36:26 PST 2001]
  *
  * You should add additional methods to this class to meet the
  * application requirements.  This class will only be generated as
  * long as it does not already exist in the output directory.

  */
public class RModuleAttribute 
    extends BaseRModuleAttribute
    implements Persistent
{
    // need a local reference
    private Attribute aAttribute = null;
    
    public Attribute getAttribute() throws Exception
    {
        if ( aAttribute==null && (getAttributeId() != null) )
        {
            aAttribute = Attribute.getInstance(getAttributeId());
            
            // make sure the parent attribute is in synch.
            super.setAttribute(aAttribute);            
        }
        return aAttribute;
    }

    public void setAttribute(Attribute v) throws Exception
    {
        aAttribute = v;
        super.setAttribute(v);
    }

    /**
     * Get the Display Value for the attribute.  In the event that this
     * is a new RModuleAttribute that has not been assigned a Display 
     * Value, this method will return the Attribute Name.
     */
    public String getDisplayValue()
    {
        String dispVal = super.getDisplayValue();
        if ( dispVal == null ) 
        {
            try
            {
                dispVal = getAttribute().getName();
            }
            catch (Exception e)
            {
                Log.error(e);
                dispVal = "!Error-Check Logs!";
            }
        }
        return dispVal;
    }

    public void delete( ScarabUser user )
         throws Exception
    {                
        ModuleEntity module = getScarabModule();

        if (user.hasPermission(ScarabSecurity.MODULE__EDIT, module))
        {
            Criteria c = new Criteria()
                .add(RModuleAttributePeer.MODULE_ID, getModuleId())
                .add(RModuleAttributePeer.ISSUE_TYPE_ID, getIssueTypeId())
                .add(RModuleAttributePeer.ATTRIBUTE_ID, getAttributeId());
            RModuleAttributePeer.doDelete(c);
        } 
        else
        {
            throw new ScarabException(ScarabConstants.NO_PERMISSION_MESSAGE);
        }            
    }


    /**
     * if this RMA is the chosen attribute for email subjects then return
     * true.  if not explicitly chosen, check the other RMA's for this module
     * and if none is chosen as the email attribute, choose the highest
     * ordered text attribute.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsDefaultText()
        throws Exception
    {
        boolean isDefault = getDefaultTextFlag();
        if ( !isDefault && getAttribute().isTextAttribute() ) 
        {
            // get related RMAs
            Criteria crit = new Criteria()
                .add(RModuleAttributePeer.MODULE_ID, getModuleId())
                .add(RModuleAttributePeer.ISSUE_TYPE_ID, getIssueTypeId());
            crit.addAscendingOrderByColumn(
                RModuleAttributePeer.PREFERRED_ORDER);
            List rmas = RModuleAttributePeer.doSelect(crit);
            
            // check if another is chosen
            boolean anotherIsDefault = false;
            for ( int i=0; i<rmas.size(); i++ ) 
            {
                RModuleAttribute rma = (RModuleAttribute)rmas.get(i);
                if ( rma.getDefaultTextFlag() ) 
                {
                    anotherIsDefault = true;
                    break;
                }
            }
            
            if ( !anotherIsDefault ) 
            {
                // locate the default text attribute
                for ( int i=0; i<rmas.size(); i++ ) 
                {
                    RModuleAttribute rma = (RModuleAttribute)rmas.get(i);
                    if ( rma.getAttribute().isTextAttribute() ) 
                    {
                        if ( rma.getAttributeId().equals(getAttributeId()) ) 
                        {
                            isDefault = true;
                        }
                        else 
                        {
                            anotherIsDefault = true;
                        }
                        
                        break;
                    }
                }
            }            
        }
        return isDefault;
    }

    /**
     * This method sets the defaultTextFlag property and also makes sure 
     * that no other related RMA is defined as the default.  It should be
     * used instead of setDefaultTextFlag in application code.
     *
     * @param b a <code>boolean</code> value
     */
    public void setIsDefaultText(boolean b)
        throws Exception
    {
        if (b && !getDefaultTextFlag()) 
        {
            // get related RMAs
            Criteria crit = new Criteria()
                .add(RModuleAttributePeer.MODULE_ID, getModuleId())
                .add(RModuleAttributePeer.ISSUE_TYPE_ID, getIssueTypeId());
            List rmas = RModuleAttributePeer.doSelect(crit);
            
            // make sure no other rma is selected
            for ( int i=0; i<rmas.size(); i++ ) 
            {
                RModuleAttribute rma = (RModuleAttribute)rmas.get(i);
                if ( rma.getDefaultTextFlag() ) 
                {
                    rma.setDefaultTextFlag(false);
                    rma.save();
                    break;
                }
            }
        }
        setDefaultTextFlag(b);
    }
}
