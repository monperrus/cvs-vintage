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

import java.util.Date;
import java.util.List;
import java.util.Iterator;

import org.apache.torque.util.Criteria; 
import org.apache.torque.om.NumberKey;
import org.apache.commons.util.ObjectUtils;
import org.apache.commons.util.StringUtils;

import org.apache.fulcrum.template.TemplateContext;
import org.apache.fulcrum.template.DefaultTemplateContext;
import org.apache.fulcrum.template.TemplateEmail;

import org.apache.turbine.Turbine;
import org.apache.turbine.Log;
import org.apache.torque.om.Persistent;

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
public class Transaction 
    extends BaseTransaction
    implements Persistent
{
    /**
     * The Attachment associated with this Transaction
     */
    private Attachment aAttachment = null;                 

    /**
     * Populates a new transaction object.
     */
    public void create(NumberKey typeId, ScarabUser user, Attachment attachment)
        throws Exception
    {
        if (attachment != null && attachment.getAttachmentId() == null) 
        {
            String mesg = 
                "Attachment must be saved before starting transaction";
            throw new ScarabException(mesg);
        }
        setTypeId(typeId);
        setCreatedBy(user.getUserId());
        setCreatedDate(new Date());
        if (attachment != null)
        {
            setAttachment(attachment);
        }
        save();
    }

    /**
     * Gets the Attachment associated with this Transaction record
     */
    public Attachment getAttachment() throws Exception
    {
        try
        {
            if ( aAttachment == null && getAttachmentId() != null )
            {
                aAttachment = AttachmentPeer.
                    retrieveByPK(new NumberKey(getAttachmentId()));
                
                // make sure the parent attribute is in synch.
                super.setAttachment(aAttachment);            
            }
        }
        catch (Exception e)
        {
            aAttachment = null;
        }
        return aAttachment;
    }

    /**
     * Sets the Attachment associated with this Transaction record
     */
    public void setAttachment(Attachment v) throws Exception
    {
        aAttachment = v;
        super.setAttachment(v);
    }

    /**
     * Returns a list of Activity objects associated with this Transaction.
     */
    public List getActivityList() throws Exception
    {
        Criteria crit = new Criteria()
            .add(ActivityPeer.TRANSACTION_ID, getTransactionId());
        return (List) ActivityPeer.doSelect(crit);
    }

    /** 
        Sends email to the users associated with the issue.
        That is associated with this transaction.
        If no subject and template specified, assume modify issue action.
        throws Exception
    */
    public void sendEmail( TemplateContext context, Issue issue, 
                           String subject, String template )
         throws Exception
    {
        TemplateEmail te = new TemplateEmail();
        if ( context == null ) 
        {
            context = new DefaultTemplateContext();
        }
        
        // add data to context
        context.put("issue", issue);
        context.put("attachment", aAttachment);
        context.put("activityList", getActivityList());
        te.setContext(context);

        te.setFrom(
            Turbine.getConfiguration().getString
                ("scarab.email.modifyissue.fromName", "Scarab System"), 
            Turbine.getConfiguration().getString
                ("scarab.email.modifyissue.fromAddress",
                 "help@scarab.tigris.org"));

        if (subject == null)
        {
            te.setSubject("[" + issue.getModule().getRealName().toUpperCase() + "] Issue #" + issue.getUniqueId() + " modified");
        }
        else
        {
            te.setSubject(subject);
        }

        if (template == null)
        {
           te.setTemplate(Turbine.getConfiguration().
               getString("scarab.email.modifyissue.template",
                         "email/ModifyIssue.vm"));
        }
        else
        {
           te.setTemplate(template);
        }

        List associatedUsers = issue.getAssociatedUsers();
        Iterator iter = associatedUsers.iterator();
        while ( iter.hasNext() ) 
        {
            ScarabUser toUser = (ScarabUser)iter.next();
            te.setTo(toUser.getFirstName() + " " + toUser.getLastName(), 
                     toUser.getUserName());
            te.send();
        }
    }

    /** 
        If no subject and template have been specified,
        Pass null arguments to the email method to specify  
        Using default values for modify issue.
        @throws Exception
    */
    public void sendEmail(TemplateContext context, Issue issue)
         throws Exception
    {
        sendEmail(context, issue, null, null);
    }

    public void sendEmail(Issue issue)
         throws Exception
    {
        sendEmail(null, issue, null, null);
    }


    /** 
        Convenience method for emails that require no extra context info. 
        @throws Exception
    */
    public void sendEmail(Issue issue, String subject, String template)
         throws Exception
    {
        sendEmail(null, issue, subject, template);
    }
}
