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

// JDK classes
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Date;
import java.sql.Connection;

// Turbine classes
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.commons.util.SequencedHashtable;
import org.apache.torque.pool.DBConnection;
import org.apache.torque.map.DatabaseMap;

// Scarab classes
import org.tigris.scarab.services.module.ModuleEntity;
import org.tigris.scarab.security.ScarabSecurity;
import org.tigris.scarab.security.SecurityFactory;
import org.tigris.scarab.util.ScarabException;
import org.tigris.scarab.attribute.TotalVotesAttribute;
import org.tigris.scarab.util.ScarabConstants;
import org.tigris.scarab.tools.ScarabRequestTool;
import org.tigris.scarab.security.ScarabSecurity;
import org.tigris.scarab.security.SecurityFactory;
import org.tigris.scarab.services.user.UserManager;

/** 
  * The skeleton for this class was autogenerated by Torque on:
  *
  * [Wed Feb 28 16:36:26 PST 2001]
  *
  * You should add additional methods to this class to meet the
  * application requirements.  This class will only be generated as
  * long as it does not already exist in the output directory.

  */
public class Issue 
    extends BaseIssue
    implements Persistent
{

    /**
     * Gets an issue associated to a ModuleEntity
     */
    public static Issue getInstance(ModuleEntity me)
        throws Exception
    {
        Issue issue = new Issue();
        issue.setModuleCast( me );
        issue.setDeleted(false);
        return issue;
    }

    /**
     * Gets the UniqueId for this Issue.
     */
    public String getUniqueId()
        throws Exception
    {
        if (getIdPrefix() == null)
        {
            setIdPrefix(getScarabModule().getCode());
        }
        return getIdPrefix() + getIdCount();
    }

    public String getFederatedId()
        throws Exception
    {
        if ( getIdDomain() != null ) 
        {
            return getIdDomain() + getUniqueId();
        }
        return getUniqueId();
    }

    public void setFederatedId(String id)
    {
        FederatedId fid = new FederatedId(id);
        setIdDomain(fid.getDomain());
        setIdPrefix(fid.getPrefix());
        setIdCount(fid.getCount());
    }

    public static class FederatedId
    {
        String domainId;
        String prefix;
        int count;

        public FederatedId(String id)
        {
            int dash = id.indexOf('-');
            if ( dash > 0 ) 
            {
                domainId = id.substring(0, dash);
                setUniqueId(id.substring(dash+1));
            }
            else 
            {
                setUniqueId(id);
            }
        }

        public FederatedId(String domain, String prefix, int count)
        {
            domainId = domain;
            this.prefix = prefix;
            this.count = count;
        }

        public void setUniqueId(String id)
        {
            // we could start at 1 here, if the spec says one char is 
            // required, will keep it safe for now.
            StringBuffer code = new StringBuffer(4);
            int max = id.length() < 4 ? id.length() : 4;
            for ( int i=0; i<max; i++) 
            {
                char c = id.charAt(i);
                if ( c != '0' && c != '1' && c != '2' && c != '3' && c != '4'
                     && c != '5' && c != '6' && c != '7' && c!='8' && c!='9' )
                {
                    code.append(c);
                }
            }
            if ( code.length() != 0 ) 
            {
                prefix = code.toString();                 
            }
            count = Integer.parseInt( id.substring(code.length()) );
            
        }

        
        /**
         * Get the IdInstance
         * @return String
         */
        public String getDomain()
        {
            return domainId;
        }

        /**
         * Get the Prefix
         * @return String
         */
        public String getPrefix()
        {
            return prefix;
        }
                
        /**
         * Get the Count
         * @return int
         */
        public int getCount()
        {
            return count;
        }
        
        /**
         * Set the IdInstance
         * @return String
         */
        public void setDomain(String domainId)
        {
            this.domainId = domainId;
        }
        /**
         * Set the Prefix
         * @param String
         */
        public void setPrefix(String prefix)
        {
            this.prefix = prefix;
        }
        
        /**
         * Set the Count
         * @param int
         */
        public void setCount(int count)
        {
            this.count = count;
        }
    }

    /**
     * Adds a comment to this issue.
     */
    public void addComment(Attachment attachment)
        throws Exception
    {
        attachment.setIssue(this);
        attachment.setTypeId(Attachment.COMMENT__PK);
        attachment.setName("");
        attachment.setMimeType("text/plain");
        attachment.save();
    }

    /**
     * FIXME: Casting to ScarabModule here is probably bad.
     */
    public void setModuleCast(ModuleEntity me)
        throws Exception
    {
        super.setScarabModule((ScarabModule)me);
    }

    public static Issue getIssueById(String id)
    {
        FederatedId fid = new FederatedId(id);
        return getIssueById(fid);
    }

    public static Issue getIssueById(FederatedId fid)
    {
        Criteria crit = new Criteria(5)
            .add(IssuePeer.ID_PREFIX, fid.getPrefix())
            .add(IssuePeer.ID_COUNT, fid.getCount());

        if (  fid.getDomain() != null ) 
        {
            crit.add(IssuePeer.ID_DOMAIN, fid.getDomain());    
        }
        
        Issue issue = null;
        try
        {
            issue = (Issue)IssuePeer.doSelect(crit).get(0);
        }
        catch (Exception e) 
        {
            // return null
        }
        return issue;
    }

    /**
     * AttributeValues that are relevant to the issue's current module.
     * Empty AttributeValues that are relevant for the module, but have 
     * not been set for the issue are included.  The values are ordered
     * according to the module's preference
     */
    public SequencedHashtable getModuleAttributeValuesMap() throws Exception
    {
        SequencedHashtable map = null; 
        Attribute[] attributes = null;
        HashMap siaValuesMap = null;

        attributes = getScarabModule().getActiveAttributes();
        siaValuesMap = getAttributeValuesMap();

        map = new SequencedHashtable( (int)(1.25*attributes.length + 1) );
        for ( int i=0; i<attributes.length; i++ ) 
        {
            String key = attributes[i].getName().toUpperCase();

            if ( siaValuesMap.containsKey(key) ) 
            {
                map.put( key, siaValuesMap.get(key) );
            }
            else 
            {
                AttributeValue aval = AttributeValue
                    .getNewInstance(attributes[i], this);
                addAttributeValue(aval);
                map.put( key, aval );
            }
        }

        return map;
    }

    public AttributeValue getAttributeValue(Attribute attribute)
       throws Exception
    {
        Criteria crit = new Criteria(2)
            .add(AttributeValuePeer.DELETED, false)        
            .add(AttributeValuePeer.ATTRIBUTE_ID, attribute.getAttributeId());

        List avals = getAttributeValues(crit);
        AttributeValue aval = null;
        if ( avals.size() == 1 ) 
        {
            aval = (AttributeValue)avals.get(0);
        }

        return aval;
    }

    /*
    public AttributeValue[] getAttributeValues(Attribute attribute)
       throws Exception
    {
        Criteria crit = new Criteria(2)
            .add(AttributeValuePeer.DELETED, false)        
            .add(AttributeValuePeer.ATTRIBUTE_ID, attribute.getAttributeId());

        List avals = getAttributeValues(crit);
        AttributeValue[] avalsArray = new AttributeValue[avals.size()];
        
        return (AttributeValue[]) avals.toArray(avalsArray);
    }
    */

    /**
     * AttributeValues that are set for this Issue in the order
     * that is preferred for this module
     */
    public AttributeValue[] getOrderedAttributeValues() throws Exception
    {        
        Map values = getAttributeValuesMap();
        Attribute[] attributes = getScarabModule().getActiveAttributes();

        return orderAttributeValues(values, attributes);
    }


    /**
     * Extract the AttributeValues from the Map according to the 
     * order in the Attribute[]
     */
    private AttributeValue[] orderAttributeValues(Map values, 
                                                  Attribute[] attributes) 
        throws Exception
    {
        AttributeValue[] orderedValues = new AttributeValue[values.size()];

        int i=0;
        for ( int j=0; j<attributes.length; j++ ) 
        {
            AttributeValue av = (AttributeValue) values
                .remove( attributes[j].getName().toUpperCase() );
            if ( av != null ) 
            {
                orderedValues[i++] = av;                
            }
        }
        Iterator iter = values.values().iterator();
        while ( iter.hasNext() ) 
        {
            orderedValues[i++] = (AttributeValue)iter.next();
        }

        return orderedValues;
    }


    /**
     * AttributeValues that are set for this Issue
     */
    public HashMap getAttributeValuesMap() throws Exception
    {
        Criteria crit = new Criteria(2)
            .add(AttributeValuePeer.DELETED, false);        
        List siaValues = getAttributeValues(crit);
        HashMap map = new HashMap( (int)(1.25*siaValues.size() + 1) );
        for ( int i=0; i<siaValues.size(); i++ ) 
        {
            AttributeValue att = (AttributeValue) siaValues.get(i);
            String name = att.getAttribute().getName();
            map.put(name.toUpperCase(), att);
        }

        return map;
    }


    /**
     * AttributeValues that are set for this issue and
     * Empty AttributeValues that are relevant for the module, but have 
     * not been set for the issue are included.
     */
    public HashMap getAllAttributeValuesMap() throws Exception
    {
        Map moduleAtts = getModuleAttributeValuesMap();
        Map issueAtts = getAttributeValuesMap();
        HashMap allValuesMap = new HashMap( (int)(1.25*(moduleAtts.size() + 
                                            issueAtts.size())+1) );

        allValuesMap.putAll(moduleAtts);
        allValuesMap.putAll(issueAtts);
        return allValuesMap;
    }


    /**
     * Describe <code>containsMinimumAttributeValues</code> method here.
     *
     * @return a <code>boolean</code> value
     * @exception Exception if an error occurs
     */
    public boolean containsMinimumAttributeValues()
        throws Exception
    {
        Attribute[] attributes = getScarabModule().getRequiredAttributes();

        boolean result = true;
        SequencedHashtable avMap = getModuleAttributeValuesMap(); 
        Iterator i = avMap.iterator();
        while (i.hasNext()) 
        {
            AttributeValue aval = (AttributeValue)avMap.get(i.next());
            
            if ( aval.getOptionId() == null && aval.getValue() == null ) 
            {
                for ( int j=attributes.length-1; j>=0; j-- ) 
                {
                    if ( aval.getAttribute().getPrimaryKey().equals(
                         attributes[j].getPrimaryKey() )) 
                    {
                        result = false;
                        break;
                    }                    
                }
                if ( !result ) 
                {
                    break;
                }
            }
        }

        return result;
    }       

    /**
     * Users who can be assigned to this issue.  if a user has already
     * been assigned to this issue, they will not show up in this list.
     *
     * @return a <code>List</code> value
     */
    public List getEligibleAssignees()
        throws Exception
    {
        ScarabUser[] users = getScarabModule().getEligibleAssignees();
        // remove those already assigned
        List assigneeAVs = getAssigneeAttributeValues();
        if ( users != null && assigneeAVs != null ) 
        {        
            for ( int i=users.length-1; i>=0; i-- ) 
            {
                for ( int j=assigneeAVs.size()-1; j>=0; j-- ) 
                {
                    AttributeValue av = (AttributeValue)assigneeAVs.get(j);
                    NumberKey avUserId = av.getUserId();
                    NumberKey userUserId = users[i].getUserId();
                    if ( av != null && avUserId != null && 
                         userUserId != null && 
                         avUserId.equals( userUserId ) )
                    {
                        users[i] = null;
                        break;
                    }
                }
            }
        }

        List eligibleUsers = new ArrayList(users.length);
        for ( int i=0; i<users.length; i++ ) 
        {
            if ( users[i] != null )
            {
                eligibleUsers.add(users[i]);
            }
        }

        return eligibleUsers;
    }

    /**
     * Returns userids, the value of the "AssignedTo" Attribute 
     */
    public List getAssigneeAttributeValues() throws Exception
    {
        ArrayList assignees = new ArrayList();
        Criteria crit = new Criteria()
            .add(AttributeValuePeer.ATTRIBUTE_ID,AttributePeer.ASSIGNED_TO__PK)
            .add(AttributeValuePeer.DELETED, false);
        List attValues = getAttributeValues(crit);
        for ( int i=0; i<attValues.size(); i++ ) 
        {
            AttributeValue attVal = (AttributeValue) attValues.get(i);
            assignees.add(attVal.getValue());
        }
        return attValues;
    }

    /**
     * Returns list of user(s) who are assigned to the issue,
     * Plus the user who created the issue, and who last modified it.
     */
    public List getAssociatedUsers() throws Exception
    {
        List associatedUsersIds = new ArrayList();
        List associatedUsers = new ArrayList();
        associatedUsersIds.add(getCreatedBy().getUserId());
        associatedUsersIds.add(getModifiedBy().getUserId());

        Iterator iter =  getAssigneeAttributeValues().iterator();   
        while ( iter.hasNext() ) 
        {
           associatedUsersIds.add(((AttributeValue)iter.next()).getUserId()); 
        }

        for ( int i=0; i<associatedUsersIds.size(); i++ ) 
        {
            ScarabUser user = (ScarabUser) ScarabUserImplPeer
                              .retrieveByPK((NumberKey)associatedUsersIds
                                                      .get(i));
            if (!associatedUsers.contains(user))
            {
                associatedUsers.add(user);
            }
        }
            
        return associatedUsers;
    }

    
    /**
     * The date the issue was created.
     *
     * @return a <code>Date</code> value
     * @exception Exception if an error occurs
     */
    public Date getCreatedDate()
        throws Exception
    {
        Date date = null;
        if ( !isNew() ) 
        {
            Criteria crit = new Criteria();
            crit.addJoin(TransactionPeer.TRANSACTION_ID, 
                         ActivityPeer.TRANSACTION_ID);
            crit.add(ActivityPeer.ISSUE_ID, getIssueId());
            crit.add(TransactionPeer.TYPE_ID, 
                     TransactionTypePeer.CREATE_ISSUE__PK);
            // there could be multiple attributes modified during the creation
            // which will lead to duplicates
            crit.setDistinct();
            List transactions = TransactionPeer.doSelect(crit);
            Transaction t = (Transaction)transactions.get(0);
            date = t.getCreatedDate();
        }
        
        return date;
    }

    /**
     * The user that created the issue.
     *
     * @return a <code>ScarabUser</code> value
     * @exception Exception if an error occurs
     */
    public ScarabUser getCreatedBy()
        throws Exception
    {
        ScarabUser user = null;
        if ( !isNew() ) 
        {
            Criteria crit = new Criteria();
            crit.addJoin(TransactionPeer.TRANSACTION_ID, 
                         ActivityPeer.TRANSACTION_ID);
            crit.add(ActivityPeer.ISSUE_ID, getIssueId());
            crit.add(TransactionPeer.TYPE_ID, 
                     TransactionTypePeer.CREATE_ISSUE__PK);
            // there could be multiple attributes modified during the creation
            // which will lead to duplicates
            crit.setDistinct();
            List transactions = TransactionPeer.doSelect(crit);
            Transaction t = (Transaction)transactions.get(0);
            user = UserManager.getInstance(t.getCreatedBy());
        }
        
        return user;
    }
         

    /**
     * The last user to modify the issue.
     *
     * @return a <code>ScarabUser</code> value
     */
    public ScarabUser getModifiedBy()
        throws Exception
    {
        ScarabUser user = null;
        if ( !isNew() ) 
        {
            Criteria crit = new Criteria();
            crit.addJoin(TransactionPeer.TRANSACTION_ID, 
                         ActivityPeer.TRANSACTION_ID);
            crit.add(ActivityPeer.ISSUE_ID, getIssueId());
            crit.add(TransactionPeer.TYPE_ID, 
                     TransactionTypePeer.EDIT_ISSUE__PK);
            // there could be multiple attributes modified during the creation
            // which will lead to duplicates
            crit.setDistinct();
            crit.addDescendingOrderByColumn(TransactionPeer.CREATED_DATE);
            List transactions = TransactionPeer.doSelect(crit);
            if ( transactions.size() > 0 ) 
            {
                Transaction t = (Transaction)transactions.get(0);
                user = UserManager.getInstance(t.getCreatedBy());
            }
            else 
            {
                user = getCreatedBy();
            }
        }
        
        return user;
    }


    /**
     * Returns a list of Attachment objects with type "Comment"
     * That are associated with this issue.
     */
    public Vector getComments() throws Exception
    {
        Criteria crit = new Criteria()
            .add(AttachmentPeer.ISSUE_ID, getIssueId())
            .addJoin(AttachmentTypePeer.ATTACHMENT_TYPE_ID,
                     AttachmentPeer.ATTACHMENT_TYPE_ID)
            .add(AttachmentTypePeer.ATTACHMENT_TYPE_ID, 
                                    Attachment.COMMENT__PK)
            .addAscendingOrderByColumn(AttachmentPeer.CREATED_DATE);

        return  AttachmentPeer.doSelect(crit);
    }

    /**
     * Returns a list of Attachment objects with type "URL"
     * That are associated with this issue.
     */
    public Vector getUrls() throws Exception
    {
        Criteria crit = new Criteria()
            .add(AttachmentPeer.ISSUE_ID, getIssueId())
            .addJoin(AttachmentTypePeer.ATTACHMENT_TYPE_ID,
                     AttachmentPeer.ATTACHMENT_TYPE_ID)
            .add(AttachmentTypePeer.ATTACHMENT_TYPE_ID, 
                                    Attachment.URL__PK)
            .add(AttachmentPeer.DELETED, 0);
        return  AttachmentPeer.doSelect(crit);
    }

    /**
     * Determines whether the history list is longer than
     * The default limit (10).
     */
    public boolean isHistoryLong() throws Exception
    {
        return isHistoryLong(10);
    }

    /**
     * Determines whether the history list is longer than
     * The limit.
     */
    public boolean isHistoryLong(int limit) throws Exception
    {
        return (getActivity(true).size() > limit);
    }

    /**
     * Returns list of Activity objects associated with this Issue.
     * Limits it to 10 history items (this is the default)
     */
    public Vector getActivity() throws Exception  
    {
        return getActivity(false, 10);
    }

    /**
     * Returns limited list of Activity objects associated with this Issue.
     */
    public Vector getActivity(int limit) throws Exception  
    {
        return getActivity(false, limit);
    }

    /**
     * Returns limited list of Activity objects associated with this Issue.
     * If fullHistory is false, it limits it to 10 history items 
     * (this is the default)
     */
    public Vector getActivity(boolean fullHistory) throws Exception  
    {
        return getActivity(fullHistory, 10);
    }

    /**
     * Returns full list of Activity objects associated with this Issue.
     */
    private Vector getActivity(boolean fullHistory, int limit) throws Exception  
    {
        Criteria crit = new Criteria()
            .add(ActivityPeer.ISSUE_ID, getIssueId())
            .addAscendingOrderByColumn(ActivityPeer.TRANSACTION_ID);
        if (!fullHistory)
        {
            crit.setLimit(limit);
        }
        return ActivityPeer.doSelect(crit);
    }

    /**
     * Returns list of child issues
     * i.e., related to this issue through the DEPEND table.
     */
    public List getChildren() throws Exception  
    {
        ArrayList children = new ArrayList();
        Criteria crit = new Criteria()
            .add(DependPeer.OBSERVED_ID, getIssueId());
        Vector depends = DependPeer.doSelect(crit);
        for ( int i=0; i<depends.size(); i++ ) 
        {
            Depend depend = (Depend) depends.get(i); 
            Issue childIssue = (Issue) IssuePeer
                      .retrieveByPK(new NumberKey(depend.getObserverId()));
            children.add(childIssue);
        }
        return children;
    }

    /**
     * Returns list of parent issues
     * i.e., related to this issue through the DEPEND table.
     */
    public List getParents() throws Exception  
    {
        ArrayList parents = new ArrayList();
        Criteria crit = new Criteria()
            .add(DependPeer.OBSERVER_ID, getIssueId());
        Vector depends = DependPeer.doSelect(crit);
        for ( int i=0; i<depends.size(); i++ ) 
        {
            Depend depend = (Depend) depends.get(i); 
            Issue parentIssue = (Issue) IssuePeer
                      .retrieveByPK(new NumberKey(depend.getObservedId()));
            parents.add(parentIssue);
        }
        return parents;
    }
        
    /**
     * Returns list of all types of dependencies an issue can have
     * On another issue.
     */
    public Vector getAllDependencyTypes() throws Exception
    {
        return DependTypePeer.doSelect(new Criteria());
    }

    /**
     * Returns type of dependency the passed-in issue has on
     * This issue.
     */
    public Depend getDependency(Issue childIssue) throws Exception
    {
        Depend depend = null;
        Criteria crit = new Criteria(2)
            .add(DependPeer.OBSERVED_ID, getIssueId() )        
            .add(DependPeer.OBSERVER_ID, childIssue.getIssueId() );
        Vector depends = DependPeer.doSelect(crit);

        Criteria crit2 = new Criteria(2)
            .add(DependPeer.OBSERVER_ID, getIssueId() )        
            .add(DependPeer.OBSERVED_ID, childIssue.getIssueId() );
        Vector depends2 = DependPeer.doSelect(crit2);

        if (depends.size() > 0 )
            depend = (Depend)depends.get(0);
        else if (depends2.size() > 0 )
            depend = (Depend)depends2.get(0);
        return depend;
    }

    /**
     * Removes any unset attributes and sets the issue # prior to saving
     * for the first time.  Calls super.save()
     *
     * @param dbCon a <code>DBConnection</code> value
     * @exception Exception if an error occurs
     */
    public void save(DBConnection dbCon)
        throws Exception
    {
        // remove unset AttributeValues before saving
        List attValues = getAttributeValues();
        // reverse order since removing from list
        for ( int i=attValues.size()-1; i>=0; i-- ) 
        {
            AttributeValue attVal = (AttributeValue) attValues.get(i);
            if ( !attVal.isSet() ) 
            {
                attValues.remove(i);
            }
        }

        // set the issue id
        if ( isNew() ) 
        {
            String prefix = getScarabModule().getCode();

            /* thinking of keeping this in separate column
            String instanceCode = TurbineResources
                .getString(ScarabConstants.INSTANCE_NAME);
            if ( instanceCode != null && instanceCode.length() > 0 ) 
            {
                prefix = instanceCode + "-" + prefix;
            }
            */

            DatabaseMap dbMap = IssuePeer.getTableMap().getDatabaseMap();
            Connection con = dbCon.getConnection();
            int numId = dbMap.getIDBroker().getIdAsInt(con, prefix);

            setIdPrefix(prefix);
            setIdCount(numId);
        }

        super.save(dbCon);
    }


    /**
     * Performs a search over an issue's attribute values.
     *
     * @param keywords a <code>String[]</code> value
     * @param useAnd, an AND search if true, otherwise OR
     * @return a <code>List</code> value
     */
    public static List searchKeywords(String[] keywords, boolean useAnd)
        throws Exception
    {
        Criteria c = new Criteria(0);
        return IssuePeer.doSelect(c);
    }


    /**
     * Returns list of issue template types.
     */
    public List getTemplateTypes() throws Exception
    {
        Criteria crit = new Criteria()
            .add(IssueTypePeer.ISSUE_TYPE_ID, IssueType.ISSUE__PK, Criteria.NOT_EQUAL);
        return IssueTypePeer.doSelect(crit);
    }

    /**
     * Get IssueTemplateInfo by Issue Id.
     */
    public IssueTemplateInfo getTemplateInfo() 
          throws Exception
    {
        Criteria crit = new Criteria(1);
        crit.add(IssueTemplateInfoPeer.ISSUE_ID, getIssueId());
        return (IssueTemplateInfo)(IssueTemplateInfoPeer.doSelect(crit).get(0));
    }


    /**
     * The Date when this issue was closed.
     *
     * @return a <code>Date</code> value, null if status has not been
     * set to Closed
     */
    public Date getClosedDate()
        throws Exception
    {
        Date closedDate = null;
        AttributeValue status = 
            getAttributeValue(Attribute.getInstance(AttributePeer.STATUS__PK));
        if ( status != null && 
             status.getOptionId().equals(AttributeOption.STATUS__CLOSED__PK) ) 
        {
            // the issue is currently in a closed state, we can get the date
            Criteria crit = new Criteria()
                .add(ActivityPeer.ISSUE_ID, getIssueId())
                .add(ActivityPeer.ATTRIBUTE_ID, AttributePeer.STATUS__PK)
                .addJoin(ActivityPeer.TRANSACTION_ID, 
                         TransactionPeer.TRANSACTION_ID)
                .add( ActivityPeer.NEW_VALUE, 
                      AttributeOption.STATUS__CLOSED__PK.toString() )
                .addDescendingOrderByColumn(TransactionPeer.CREATED_DATE);
 
            List transactions = TransactionPeer.doSelect(crit);
            if ( transactions.size() > 0 ) 
            {
                closedDate = ((Transaction)transactions.get(0))
                    .getCreatedDate();
            }
            else 
            {
                throw new ScarabException("Issue " + getIssueId() + 
                    " was in a closed state, but" +
                    "no transaction is associated with the change.");   
            }
        }
          
        return closedDate;
    }

    public void addVote(ScarabUser user)
        throws ScarabException, Exception
    {
        // check to see if the user has voted for this issue
        int previousVotes = 0;
        IssueVote issueVote = null;
        Criteria crit = new Criteria()
            .add(IssueVotePeer.ISSUE_ID, getIssueId())
            .add(IssueVotePeer.USER_ID, user.getUserId());
        List votes = IssueVotePeer.doSelect(crit);
        if ( votes != null && votes.size() != 0 ) 
        {
            issueVote = (IssueVote)votes.get(0);
            previousVotes = issueVote.getVotes();
        }
        else 
        {
            issueVote = new IssueVote();
            issueVote.setIssueId(getIssueId());
            issueVote.setUserId(user.getUserId());
        }

        // check if the module accepts multiple votes
        if ( !getScarabModule().allowsMultipleVoting() && previousVotes > 0 )
        {
            throw new ScarabException("User " + user.getUserName() + 
                " attempted to vote multiple times for issue " + getUniqueId()
                + " which was not allowed in this project.");
        }
        
        // save the user's vote
        issueVote.setVotes(previousVotes+1);
        issueVote.save();

        // update the total votes for the issue
        crit = new Criteria()
            .add(AttributeValuePeer.ATTRIBUTE_ID, 
                 AttributePeer.TOTAL_VOTES__PK);
        List voteValues = getAttributeValues(crit);
        TotalVotesAttribute voteValue = null;
        if ( voteValues.size() == 0 ) 
        {
            voteValue = new TotalVotesAttribute();
            voteValue.setIssue(this);
            voteValue.setAttributeId(AttributePeer.TOTAL_VOTES__PK);
        }
        else 
        {
            voteValue = (TotalVotesAttribute)voteValues.get(0);
        }
        // Updating attribute values requires a transaction
        Transaction transaction = new Transaction();
        transaction
            .create(TransactionTypePeer.RETOTAL_ISSUE_VOTE__PK, user, null);
        voteValue.startTransaction(transaction);
        voteValue.addVote();
        voteValue.save();
    }



    /**
     * Brings the current list of users assigned to this issue in
     * line with the list given by newAssignees.  Users currently
     * assigned will be deleted, if not in the new list.  This method
     * will cause this issue to be saved.
     *
     * @param newAssignees a <code>List</code> value
     * @param attachmentText a <code>String</code> value
     * @param assigner a <code>ScarabUser</code> value
     * @exception Exception if an error occurs
     */
    public void assignUsers(
        List newAssignees, String attachmentText, ScarabUser assigner)
        throws Exception
    {                
        Attachment attachment = new Attachment();
        attachment.setDataAsString(attachmentText);
        attachment.setName("Assignee Note");
        attachment.setTextFields(assigner, this, 
                                 Attachment.MODIFICATION__PK);
        attachment.save();

        // Save transaction record
        Transaction transaction = new Transaction();
        transaction.create(TransactionTypePeer.EDIT_ISSUE__PK, 
                           assigner, attachment);

        // take care of users who were removed or already assigned
        List assignees = getAssigneeAttributeValues();
        Iterator iter = assignees.iterator();
        while ( iter.hasNext() ) 
        {
            AttributeValue oldAV = (AttributeValue)iter.next();
            oldAV.startTransaction(transaction);
            boolean deleted = true;
            if ( newAssignees != null ) 
            {
                for ( int i=newAssignees.size()-1; i>=0; i-- ) 
                {
                    if ( oldAV.getValue().equals( 
                        ((ScarabUser)newAssignees.get(i)).getUserName() ))
                    {
                        // a current user was left in the list of assignees
                        // so remove from list of new assignees and mark as
                        // not to be deleted.
                        newAssignees.remove(i);
                        deleted = false;
                        break;
                    }
                }
            }
            oldAV.setDeleted(deleted);
        }

        // add new values
        if ( newAssignees != null ) 
        {        
            for ( int i=0; i<newAssignees.size(); i++ ) 
            {
                ScarabUser user = (ScarabUser)newAssignees.get(i);
                AttributeValue av = AttributeValue
                    .getNewInstance(AttributePeer.ASSIGNED_TO__PK, this);
                av.startTransaction(transaction);
                av.setUserId(user.getUserId());
                av.setValue(user.getUserName());
                assignees.add(av);
            }
        }
        save();
    }

    /**
     * Checks permission and approves or rejects issue template. 
     * If template is approved, template type set to "global", else set to "personal".
     */
    public void approve( ScarabUser user, boolean approved )
         throws Exception, ScarabException

    {                
        ScarabSecurity security = SecurityFactory.getInstance();
        ModuleEntity module = getScarabModule();

        if (security.hasPermission(ScarabSecurity.ITEM__APPROVE, user,
                                   module))
        {
            IssueTemplateInfo templateInfo = getTemplateInfo();
            templateInfo.setApproved(true);
            templateInfo.save();
            if (approved)
            {
                setTypeId(IssueType.GLOBAL_TEMPLATE__PK);
            }
            save();
        } 
        else
        {
            throw new ScarabException(ScarabConstants.NO_PERMISSION_MESSAGE);
        }            
    }

    /**
     * Checks if user has permission to delete issue template.
     * Only the creating user can delete a personal template.
     * Only project owner or admin can delete a project-wide template.
     */
    public void delete( ScarabUser user )
         throws Exception, ScarabException
    {                
        ModuleEntity module = getScarabModule();
        ScarabSecurity security = SecurityFactory.getInstance();

        if (security.hasPermission(ScarabSecurity.ITEM__APPROVE, 
                                   user, module)
          || (user.equals(getCreatedBy()) 
             && getTypeId().equals(IssueType.USER_TEMPLATE__PK)))
        {
            setDeleted(true);
            save();
        } 
        else
        {
            throw new ScarabException(ScarabConstants.NO_PERMISSION_MESSAGE);
        }            
    }


    // *******************************************************************
    // Permissions methods
    // *******************************************************************

    /**
     * Checks if user has permission to enter issue.
     */
    public boolean hasEnterPermission( ScarabUser user, ModuleEntity module)
        throws Exception
    {                
        boolean hasPerm = false;
        ScarabSecurity security = SecurityFactory.getInstance();

        if (security.hasPermission(ScarabSecurity.ISSUE__ENTER, user,
                                   module))
        {
             hasPerm = true;
        } 
        return hasPerm;
    }


    /**
     * Checks if user has permission to edit issue.
     */
    public boolean hasEditPermission( ScarabUser user, ModuleEntity module)
        throws Exception
    {                
        boolean hasPerm = false;
        ScarabSecurity security = SecurityFactory.getInstance();

        if (security.hasPermission(ScarabSecurity.ISSUE__EDIT, user,
                                   module)
            || user.equals(getCreatedBy()))
        {
            hasPerm = true;
        } 
        return hasPerm;
    }
}
