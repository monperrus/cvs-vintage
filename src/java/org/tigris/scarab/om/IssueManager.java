package org.tigris.scarab.om;

/* ================================================================
 * Copyright (c) 2000-2003 CollabNet.  All rights reserved.
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
 * software developed by CollabNet <http://www.Collab.Net/>."
 * Alternately, this acknowlegement may appear in the software itself, if
 * and wherever such third-party acknowlegements normally appear.
 *
 * 4. The hosted project names must not be used to endorse or promote
 * products derived from this software without prior written
 * permission. For written permission, please contact info@collab.net.
 *
 * 5. Products derived from this software may not use the "Tigris" or
 * "Scarab" names nor may "Tigris" or "Scarab" appear in their names without
 * prior written permission of CollabNet.
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
 * individuals on behalf of CollabNet.
 */

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.manager.CacheListener;
import org.apache.torque.util.Criteria;
import org.tigris.scarab.util.Log;

/** 
 * This class manages Issue objects.  
 * The skeleton for this class was autogenerated by Torque  * You should add additional methods to this class to meet the
 * application requirements.  This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public class IssueManager
    extends BaseIssueManager
    implements CacheListener
{
    private static final String ISSUE = 
        "Issue";
    protected static final String GET_ISSUE_BY_ID = 
        "getIssueById";

    /**
     * Creates a new <code>IssueManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public IssueManager()
        throws TorqueException
    {
        super();
        setRegion(getClassName().replace('.', '_'));
    }

    /**
     * If the id is not specified, return null otherwise return the 
     * issue object.
     */
    public static Issue getIssueById(String id)
    {
        if (id == null || id.length() == 0)
        {
            return null;
        }
        Issue.FederatedId fid = new Issue.FederatedId(id);
        return getIssueById(fid);
    }

    public static Issue getIssueById(Issue.FederatedId fid)
    {
        return getManager().getIssueByIdImpl(fid);
    }

    public static Issue getIssueByIdImpl(Issue.FederatedId fid)
    {
        Issue result = null;
        Object obj = getMethodResult().get(ISSUE, GET_ISSUE_BY_ID, fid); 
        if (obj != null) 
        {        
            try 
            {
                Issue cachedById = (Issue)obj;
                Issue cachedByPk = getInstance(cachedById.getIssueId());
                // we need to compare this to the cached by pk, issue, in case
                // the issue was moved.
                if (cachedById.getFederatedId().equals(cachedByPk.getFederatedId())) 
                {
                    result = cachedByPk;
                }
                else 
                {
                    getMethodResult().remove(ISSUE, GET_ISSUE_BY_ID, fid);
                }
            }
            catch (TorqueException e)
            {
                Log.get().error("", e);
            }
        }

        if (result == null) 
        {        
            Criteria crit = new Criteria(5)
                .add(IssuePeer.ID_PREFIX, fid.getPrefix())
                .add(IssuePeer.ID_COUNT, fid.getCount());
            crit.setIgnoreCase(true);
            
            if ( fid.getDomain() != null) 
            {
                crit.add(IssuePeer.ID_DOMAIN, fid.getDomain());    
            }
            
            try
            {
                result = (Issue)IssuePeer.doSelect(crit).get(0);
                IssueManager.putInstance(result);
                getMethodResult().put(result, ISSUE, GET_ISSUE_BY_ID, fid);
            }
            catch (Exception e) 
            {
                Log.get().error("", e);
                // return null
            }
        }

        return result;
    }

    protected Persistent putInstanceImpl(Persistent om)
        throws TorqueException
    {
        Persistent oldOm = super.putInstanceImpl(om);
        // saving an issue object could affect some cached results, since it could be a move
        Serializable obj = (Serializable)om;
        getMethodResult().remove(obj, Issue.GET_MODULE_ATTRVALUES_MAP);
        getMethodResult().remove(obj, Issue.GET_USER_ATTRIBUTEVALUES);
        return oldOm;
    }


    /**
     * Notify other managers with relevant CacheEvents.
     */
    protected void registerAsListener()
    {
        AttributeValueManager.addCacheListener(this);
        AttachmentManager.addCacheListener(this);
        DependManager.addCacheListener(this);
        ActivityManager.addCacheListener(this);
        AttributeManager.addCacheListener(this);
    }


    // -------------------------------------------------------------------
    // CacheListener implementation

    public void addedObject(Persistent om)
    {
        if (om instanceof AttributeValue) 
        {
            AttributeValue castom = (AttributeValue)om;
            ObjectKey key = castom.getIssueId();
            Serializable obj = (Serializable)cacheGet(key);
            if (obj != null) 
            {
                getMethodResult().remove(obj, Issue.GET_MODULE_ATTRVALUES_MAP);
                getMethodResult().remove(obj, Issue.GET_USER_ATTRIBUTEVALUES);
            }
        }
        else if (om instanceof Attachment) 
        {
            Attachment castom = (Attachment)om;
            ObjectKey key = castom.getIssueId();
            Serializable obj = (Serializable)cacheGet(key);
            if (obj != null) 
            {
                getMethodResult().remove(obj, Issue.GET_URLS);
                getMethodResult().removeAll(obj, Issue.GET_COMMENTS);
                getMethodResult().removeAll(obj, 
                    Issue.GET_EXISTING_ATTACHMENTS);
            }
        }
        else if (om instanceof Depend) 
        {
            Depend castom = (Depend)om;
            ObjectKey key = castom.getObserverId();
            Serializable obj = (Serializable)cacheGet(key);
            if (obj != null) 
            {
                getMethodResult().removeAll(obj, Issue.GET_PARENTS);
            }
            key = castom.getObservedId();
            obj = (Serializable)cacheGet(key);
            if (obj != null) 
            {
                getMethodResult().removeAll(obj, Issue.GET_CHILDREN);
            }
        }
        else if (om instanceof Activity) 
        {
            Activity castom = (Activity)om;
            ObjectKey key = castom.getIssueId();
            Serializable obj = (Serializable)cacheGet(key);
            if (obj != null) 
            {
                getMethodResult().removeAll(obj, Issue.GET_ACTIVITY);
            }
        }
        else if (om instanceof Attribute) 
        {
            getMethodResult().clear();
        }
    }

    public void refreshedObject(Persistent om)
    {
        addedObject(om);            
    }

    /** fields which interest us with respect to cache events */
    public List getInterestedFields()
    {
        List interestedCacheFields = new LinkedList();
        interestedCacheFields.add(AttributeValuePeer.ISSUE_ID);
        interestedCacheFields.add(AttachmentPeer.ISSUE_ID);
        interestedCacheFields.add(DependPeer.OBSERVER_ID);
        interestedCacheFields.add(DependPeer.OBSERVED_ID);
        interestedCacheFields.add(AttributePeer.ATTRIBUTE_ID);
        return interestedCacheFields;
    }
}





