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
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

// Turbine classes
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.TurbineSecurityException;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;

// Scarab classes
import org.tigris.scarab.services.module.ModuleEntity;
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
public class ScarabModule
    extends BaseScarabModule
    implements Persistent, ModuleEntity, Group, Comparable
{
    protected static final NumberKey ROOT_ID = new NumberKey("0");

    private Attribute[] activeAttributes;
    private Attribute[] dedupeAttributes;
    private Attribute[] quicksearchAttributes;
    private Attribute[] requiredAttributes;
    private List allRModuleAttributes;
    private List activeRModuleAttributes;

    private Map allRModuleOptionsMap = new HashMap();
    private Map activeRModuleOptionsMap = new HashMap();


    /**
     * Wrapper method to perform the proper cast to the BaseModule method
     * of the same name.
     */
    public void setModuleRelatedByParentId(ModuleEntity v) throws Exception
    {
        super.setScarabModuleRelatedByParentId((ScarabModule)v);
    }

    public ModuleEntity getModuleRelatedByParentIdCast() throws Exception
    {
        return (ModuleEntity) super.getScarabModuleRelatedByParentId();
    }

    /**
     * Creates a new Issue.
     *
     */
    public Issue getNewIssue(ScarabUser user)
        throws Exception
    {
        Issue issue = new Issue();
        issue.setModuleCast( (ModuleEntity) this );
        issue.setDeleted(false);
        return issue;
    }

    /**
     * List of Query objects associated with this module.
     *
     */
    public Vector getQueries()
        throws Exception
    {
        Vector queries = null;
        Criteria crit = new Criteria(2)
            .add(QueryPeer.MODULE_ID, getModuleId())
            .add(QueryPeer.DELETED, 0);
        queries = QueryPeer.doSelect(crit);

        return queries;
    }

    /**
     * gets a list of all of the Attributes in a Module based on the Criteria.
     */
    public Attribute[] getAttributes(Criteria criteria)
        throws Exception
    {
        List moduleAttributes = getRModuleAttributes(criteria);

        Attribute[] attributes = new Attribute[moduleAttributes.size()];
        for ( int i=0; i<moduleAttributes.size(); i++ )
        {
            attributes[i] =
               ((RModuleAttribute) moduleAttributes.get(i)).getAttribute();
        }
        return attributes;
    }


    /**
     * Overridden method.  Calls the super method and if no results are
     * returned the call is passed on to the parent module.
     */
    public Vector getRModuleAttributes(Criteria crit)
        throws Exception
    {
        Vector rModAtts = super.getRModuleAttributes(crit);

        if ( rModAtts == null || rModAtts.size() == 0 ) 
        {
            ModuleEntity parent = 
                (ModuleEntity) this.getModuleRelatedByParentIdCast();
            if ( !ROOT_ID.equals(this.getModuleId()) ) 
            {
                rModAtts = parent.getRModuleAttributes(crit);
            }
        }

        return rModAtts;
    }


    /**
     * Array of Attributes used for deduping.
     *
     * @return an <code>Attribute[]</code> value
     */
    public Attribute[] getDedupeAttributes()
        throws Exception
    {
        if ( dedupeAttributes == null )
        {
            Criteria crit = new Criteria(3)
                .add(RModuleAttributePeer.DEDUPE, true);
            addActiveAndOrderByClause(crit);
            dedupeAttributes = getAttributes(crit);
        }

        return dedupeAttributes;
    }


    /**
     * Array of Attributes used for quick search.
     *
     * @return an <code>Attribute[]</code> value
     */
    public Attribute[] getQuickSearchAttributes()
        throws Exception
    {
        if ( quicksearchAttributes == null )
        {
            Criteria crit = new Criteria(3)
                .add(RModuleAttributePeer.QUICK_SEARCH, true);
            addActiveAndOrderByClause(crit);
            quicksearchAttributes = getAttributes(crit);
        }
        return quicksearchAttributes;
    }


    /**
     * Array of Attributes which are active and required by this module.
     *
     * @param inOrder flag determines whether the attribute order is important
     * @return an <code>Attribute[]</code> value
     */
    public Attribute[] getRequiredAttributes()
        throws Exception
    {
        if ( requiredAttributes == null )
        {
            Criteria crit = new Criteria(3)
                .add(RModuleAttributePeer.REQUIRED, true);
            addActiveAndOrderByClause(crit);
            requiredAttributes = getAttributes(crit);
        }
        return requiredAttributes;
    }

    /**
     * Array of active Attributes.
     *
     * @return an <code>Attribute[]</code> value
     */
    public Attribute[] getActiveAttributes()
        throws Exception
    {
        if ( activeAttributes == null )
        {
            Criteria crit = new Criteria(2);
            addActiveAndOrderByClause(crit);
            activeAttributes = getAttributes(crit);
        }
        return activeAttributes;
    }

    private void addActiveAndOrderByClause(Criteria crit)
    {
        crit.add(RModuleAttributePeer.ACTIVE, true);
        crit.addAscendingOrderByColumn(RModuleAttributePeer.PREFERRED_ORDER);
        crit.addAscendingOrderByColumn(RModuleAttributePeer.DISPLAY_VALUE);
    }

    public RModuleAttribute getRModuleAttribute(Attribute attribute)
        throws Exception
    {
        RModuleAttribute rma = null;
        List rmas = getRModuleAttributes(false);
        Iterator i = rmas.iterator();
        while ( i.hasNext() )
        {
            rma = (RModuleAttribute)i.next();
            if ( rma.getAttribute().equals(attribute) )
            {
                break;
            }
        }

        return rma;
    }

    public List getRModuleAttributes(boolean activeOnly)
        throws Exception
    {
        List allRModuleAttributes = null;
        List activeRModuleAttributes = null;
        // note this code could potentially read information from the
        // db multiple times (MT), but this is okay
        if ( this.allRModuleAttributes == null )
        {
            allRModuleAttributes = getAllRModuleAttributes();
            this.allRModuleAttributes = allRModuleAttributes;
        }
        else
        {
            allRModuleAttributes = this.allRModuleAttributes;
        }

        if ( activeOnly )
        {
            if ( this.activeRModuleAttributes == null )
            {
                activeRModuleAttributes =
                    new ArrayList(allRModuleAttributes.size());
                for ( int i=0; i<allRModuleAttributes.size(); i++ )
                {
                    RModuleAttribute rma =
                        (RModuleAttribute)allRModuleAttributes.get(i);
                    if ( rma.getActive() )
                    {
                        activeRModuleAttributes.add(rma);
                    }
                }

                this.activeRModuleAttributes = activeRModuleAttributes;
            }
            else
            {
                activeRModuleAttributes = this.activeRModuleAttributes;
            }

            return activeRModuleAttributes;
        }
        else
        {
            return allRModuleAttributes;
        }
    }

    private List getAllRModuleAttributes()
        throws Exception
    {
        Criteria crit = new Criteria(0);
        crit.addAscendingOrderByColumn(RModuleAttributePeer.PREFERRED_ORDER);
        crit.addAscendingOrderByColumn(RModuleAttributePeer.DISPLAY_VALUE);

        List rModAtts = null;
        ModuleEntity module = this;
        ModuleEntity prevModule = null;
        do
        {
            rModAtts = module.getRModuleAttributes(crit);
            prevModule = module;
            module = (ModuleEntity) prevModule.getModuleRelatedByParentIdCast();
        }
        while ( rModAtts.size() == 0 &&
               !ROOT_ID.equals(prevModule.getModuleId()));
        return rModAtts;
    }

    /**
     * gets a list of all of the Attributes.
     */
    public Attribute[] getAllAttributes()
        throws Exception
    {
        return getAttributes(new Criteria());
    }

    public List getRModuleOptions(Attribute attribute)
        throws Exception
    {
        return getRModuleOptions(attribute, true);
    }

    public List getRModuleOptions(Attribute attribute, boolean activeOnly)
        throws Exception
    {
        List allRModuleOptions = (List)allRModuleOptionsMap.get(attribute);
        if ( allRModuleOptions == null )
        {
            allRModuleOptions = getAllRModuleOptions(attribute);
            allRModuleOptionsMap.put(attribute, allRModuleAttributes);
        }

        if ( activeOnly )
        {
            List activeRModuleOptions =
                (List)activeRModuleOptionsMap.get(attribute);
            if ( activeRModuleOptions == null )
            {
                activeRModuleOptions =
                    new ArrayList(allRModuleOptions.size());
                for ( int i=0; i<allRModuleOptions.size(); i++ )
                {
                    RModuleOption rmo =
                        (RModuleOption)allRModuleOptions.get(i);
                    if ( rmo.getActive() )
                    {
                        activeRModuleOptions.add(rmo);
                    }
                }

                activeRModuleOptionsMap
                    .put(attribute, activeRModuleOptions);
            }

            return activeRModuleOptions;
        }
        else
        {
            return allRModuleOptions;
        }
    }

    private List getAllRModuleOptions(Attribute attribute)
        throws Exception
    {
        List options = attribute.getAttributeOptions(false);
        NumberKey[] optIds = null;
        if (options == null)
        {
            optIds = new NumberKey[0];
        }
        else
        {
            optIds = new NumberKey[options.size()];
        }
        for ( int i=optIds.length-1; i>=0; i-- )
        {
            optIds[i] = ((AttributeOption)options.get(i)).getOptionId();
        }

        Criteria crit = new Criteria(2);
        crit.addIn(RModuleOptionPeer.OPTION_ID, optIds);
        crit.addAscendingOrderByColumn(RModuleOptionPeer.PREFERRED_ORDER);
        crit.addAscendingOrderByColumn(RModuleOptionPeer.DISPLAY_VALUE);

        List rModOpts = null;
        ScarabModule module = this;
        ScarabModule prevModule = null;
        do
        {
            rModOpts = module.getRModuleOptions(crit);
            prevModule = module;
            module = prevModule.getScarabModuleRelatedByParentId();
        }
        while ( rModOpts.size() == 0 &&
               !ROOT_ID.equals((NumberKey)prevModule.getPrimaryKey()));
        return rModOpts;
    }

    /**
     * Gets the modules list of attribute options. Uses the
     * RModuleOption table to do the join. returns null if there
     * is any error.
     */
    public List getAttributeOptions (Attribute attribute)
        throws Exception
    {
        List attributeOptions = null;
        try
        {
            List rModuleOptions = getOptionTree(attribute, false);
            attributeOptions = new ArrayList(rModuleOptions.size());
            for ( int i=0; i<rModuleOptions.size(); i++ )
            {
                attributeOptions.add(
                    ((RModuleOption)rModuleOptions.get(i)).getAttributeOption());
            }
        }
        catch (Exception e)
        {
        }
        return attributeOptions;
    }

    public List getLeafRModuleOptions(Attribute attribute)
        throws Exception
    {
        try
        {
        return getLeafRModuleOptions(attribute, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List getLeafRModuleOptions(Attribute attribute, boolean activeOnly)
        throws Exception
    {
        List rModOpts = getRModuleOptions(attribute, activeOnly);

        // put options in a map for searching
        Map optionsMap = new HashMap((int)(rModOpts.size()*1.5));
        for ( int i=rModOpts.size()-1; i>=0; i-- )
        {
            RModuleOption rmo = (RModuleOption)rModOpts.get(i);
            optionsMap.put(rmo.getOptionId(), null);
        }

        // remove options with descendants in the list
        for ( int i=rModOpts.size()-1; i>=0; i-- )
        {
            AttributeOption option =
                ((RModuleOption)rModOpts.get(i)).getAttributeOption();
            List descendants = option.getDescendants();
            if ( descendants != null )
            {
                for ( int j=descendants.size()-1; j>=0; j-- )
                {
                    AttributeOption descendant =
                        (AttributeOption)descendants.get(j);
                    if ( optionsMap.containsKey(descendant.getOptionId()) )
                    {
                        rModOpts.remove(i);
                        break;
                    }
                }
            }
        }

        return rModOpts;
    }

    /**
     * Gets a list of active RModuleOptions which have had their level
     * within the options for this module set.
     *
     * @param attribute an <code>Attribute</code> value
     * @return a <code>List</code> value
     * @exception Exception if an error occurs
     */
    public List getOptionTree(Attribute attribute)
        throws Exception
    {
        return getOptionTree(attribute, true);
    }

    /**
     * Gets a list of RModuleOptions which have had their level
     * within the options for this module set.
     *
     * @param attribute an <code>Attribute</code> value
     * @param activeOnly a <code>boolean</code> value
     * @return a <code>List</code> value
     * @exception Exception if an error occurs
     */
    public List getOptionTree(Attribute attribute, boolean activeOnly)
        throws Exception
    {
        List moduleOptions = null;
try{
        moduleOptions = getRModuleOptions(attribute, activeOnly);
        int size = moduleOptions.size();
        List[] ancestors = new List[size];

        // put option id's in a map for searching and find all ancestors
        Map optionsMap = new HashMap((int)(size*1.5));
        for ( int i=size-1; i>=0; i-- )
        {
            AttributeOption option =
                ((RModuleOption)moduleOptions.get(i)).getAttributeOption();
            optionsMap.put(option.getOptionId(), null);

            List moduleOptionAncestors = option.getAncestors();
            ancestors[i] = moduleOptionAncestors;
        }

        for ( int i=0; i<size; i++ )
        {
            RModuleOption moduleOption = (RModuleOption)moduleOptions.get(i);
            AttributeOption attributeOption =
                moduleOption.getAttributeOption();

            int level = 1;
            if ( ancestors[i] != null )
            {
                for ( int j=ancestors[i].size()-1; j>=0; j-- )
                {
                    AttributeOption option =
                        (AttributeOption)ancestors[i].get(j);

                    if ( optionsMap.containsKey(option.getOptionId()) &&
                         !option.getOptionId()
                         .equals(moduleOption.getOptionId()) )
                    {
                        moduleOption.setLevel(level++);
                    }
                }
            }
        }
}catch (Exception e){e.printStackTrace();}

        return moduleOptions;

    }


    /**
     * Gets users which are currently associated (relationship has not
     * been deleted) with this module who have the given permssion.
     *
     * @param partialUserName username fragment to match against
     * @param permissions a <code>String[]</code> permission
     * @return a <code>List</code> of ScarabUsers
     * @exception Exception if an error occurs
     */
    public List getUsers(String permission)
        throws Exception
    {
        return getUsers(null, permission);
    }

    /**
     * Gets users which are currently associated (relationship has not
     * been deleted) with this module who have the given permssion.
     *
     * @param partialUserName username fragment to match against
     * @param permissions a <code>String[]</code> permission
     * @return a <code>List</code> of ScarabUsers
     * @exception Exception if an error occurs
     */
    public List getUsers(String partialUserName, String permission)
        throws Exception
    {
        String[] perms = new String[1];
        perms[0] = permission;
        return getUsers(partialUserName, perms);
    }

    /**
     * Determines whether this module allows users to vote many times for
     * the same issue.  This feature needs schema change to allow a
     * configuration screen.  Currently only one vote per issue is supported
     *
     * @return false
     */
    public boolean allowsMultipleVoting()
    {
        return false;
    }

    /**
     * How many votes does the user have left to cast.  Currently always
     * returns 1, so a user has unlimited voting rights.  Should look to
     * UserVote for the answer when implemented properly.
     */
    public int getUnusedVoteCount(ScarabUser user)
    {
        return 1;
    }

    /**
     * Gets users which are currently associated (relationship has not
     * been deleted) with this module who have the given permssions.
     *
     * @param partialUserName username fragment to match against
     * @param permissions a <code>String[]</code> permissions
     * @return a <code>List</code> of ScarabUsers
     * @exception Exception if an error occurs
     public abstract List getUsers(String partialUserName, String[] permissions)
        throws Exception;
     */


    /**
     * Gets users which are currently associated (relationship has not 
     * been deleted) with this module with Roles specified in includeRoles
     * and excluding Roles in the exclude list. 
     *
     * @param partialUserName username fragment to match against
     * @param includeRoles a <code>Role[]</code> value
     * @param excludeRoles a <code>Role[]</code> value
     * @return a <code>List</code> of ScarabUsers
     * @exception Exception if an error occurs
     */
    public List getUsers(String partialUserName, String[] permissions)
        throws Exception
    {
        Criteria crit = new Criteria(3)
            .add(RModuleUserRolePeer.DELETED, false);
        /* 
           Criteria.Criterion c = null;
        if ( includeRoles != null ) 
        {            
            crit.addIn(RModuleUserRolePeer.ROLE_ID, includeRoles);
            c = crit.getCriterion(RModuleUserRolePeer.ROLE_ID);
        }
        if ( excludeRoles != null ) 
        {   
            if ( c == null ) 
            {
                crit.addNotIn(RModuleUserRolePeer.ROLE_ID, excludeRoles);
            }
            else 
            {
                c.and(crit
                      .getNewCriterion(RModuleUserRolePeer.ROLE_ID, 
                                       excludeRoles, Criteria.NOT_IN));
            }
        }
        if ( partialUserName != null && partialUserName.length() != 0 ) 
        {
            crit.add(ScarabUserPeer.USERNAME, 
                     (Object)("%" + partialUserName + "%"), Criteria.LIKE);
        }
        */
        List moduleRoles = getRModuleUserRolesJoinScarabUserImpl(crit);

        // rearrange so list contains Users
        List users = new ArrayList(moduleRoles.size());
        Iterator i = moduleRoles.iterator();
        while (i.hasNext()) 
        {
            ScarabUser user = ((RModuleUserRole)i.next()).getScarabUserImpl();
            users.add(user);
        }
        
        return users;
    }

    /**
     * Returns list of queries needing approval.
     */
    public Vector getUnapprovedQueries() throws Exception
    {
        Criteria crit = new Criteria(3);
        crit.add(QueryPeer.APPROVED, 0)
           .add(QueryPeer.DELETED, 0);
        return QueryPeer.doSelect(crit);
    }

    /**
     * Saves the module into the database
    public void save() throws Exception
    {
        // if new, make sure the code has a value.
        if ( isNew() )
        {
            String code = getCode();
            if ( code == null || code.length() == 0 )
            {
                if ( getParentId().equals(ROOT_ID) )
                {
                    throw new ScarabException("A top level module addition was"
                        + " attempted without assigning a Code");
                }

                setCode(getModuleRelatedByParentId().getCode());

                // insert a row into the id_table.
                Criteria criteria = new Criteria(
                    ModulePeer.getTableMap().getDatabaseMap().getName(), 5)
                    .add(IDBroker.TABLE_NAME, getCode())
                    .add(IDBroker.NEXT_ID, 1)
                    .add(IDBroker.QUANTITY, 1);
                BasePeer.doInsert(criteria);
            }
        }
        super.save();
    }
     */

    /**
     * Saves the module into the database
     */
    public void save() throws TurbineSecurityException
    {
        try
        {
            // if new, relate the Module to the user who created it.
            if ( isNew() ) 
            {
                RModuleUserRole relation = new RModuleUserRole();
                if ( getOwnerId() == null ) 
                {
                    throw new ScarabException(
                    "Can't save a project without first assigning an owner.");
                }         
                relation.setUserId(getOwnerId());
                // !FIXME! this needs to be set to the Module Owner Role
                relation.setRoleId(new NumberKey("1"));
                relation.setDeleted(false);
                addRModuleUserRole(relation);
            }
            super.save();
        }
        catch (Exception e)
        {
            throw new TurbineSecurityException(e.getMessage(), e);
        }
    }


    /*
    public class OptionInList
    {
        public int Level;
        private AttributeOption attOption;
        private RModuleOption modOption;

        public OptionInList(int level, AttributeOption option)
        {
            Level = level;
            attOption = option;
        }

        public OptionInList(int level, RModuleOption option)
        {
            Level = level;
            modOption = option;
        }

        public boolean equals(Object obj)
        {
            OptionInList oil = (OptionInList)obj;
            return Level == oil.Level
                && (attOption == null || attOption.equals(oil.attOption))
                && (modOption == null || modOption.equals(oil.modOption));
        }

        public NumberKey getOptionId()
        {
            if ( attOption != null )
            {
                return attOption.getOptionId();
            }
            else
            {
                return modOption.getOptionId();
            }

        }

        public String getDisplayValue()
        {
            if ( attOption != null )
            {
                return attOption.getName();
            }
            else
            {
                return modOption.getDisplayValue();
            }
        }
    }
    */

    // *******************************************************************
    // Turbine Group implementation get/setName and save are defined above
    // *******************************************************************


    /**
     * Removes a group from the system.
     *
     * @throws TurbineSecurityException if the Group could not be removed.
     */
    public void remove()
        throws TurbineSecurityException
    {
        throw new TurbineSecurityException("Not implemented");
    }

    /**
     * Renames the group.
     *
     * @param name The new Group name.
     * @throws TurbineSecurityException if the Group could not be renamed.
     */
    public void rename(String name)
        throws TurbineSecurityException
    {
        throw new TurbineSecurityException("Not implemented");
    }

    /**
     * Grants a Role in this Group to an User.
     *
     * @param user An User.
     * @param role A Role.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Role.
     */
    public void grant(User user, Role role)
        throws TurbineSecurityException
    {
        throw new TurbineSecurityException("Not implemented");
    }

    /**
     * Grants Roles in this Group to an User.
     *
     * @param user An User.
     * @param roleSet A RoleSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Roles.
     */
    public void grant(User user, RoleSet roleSet)
        throws TurbineSecurityException
    {
        throw new TurbineSecurityException("Not implemented");
    }

    /**
     * Revokes a Role in this Group from an User.
     *
     * @param user An User.
     * @param role A Role.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Role.
     */
    public void revoke(User user, Role role)
        throws TurbineSecurityException
    {
        throw new TurbineSecurityException("Not implemented");
    }

    /**
     * Revokes Roles in this group from an User.
     *
     * @param user An User.
     * @param roleSet a RoleSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Roles.
     */
    public void revoke(User user, RoleSet roleSet)
        throws TurbineSecurityException
    {
        throw new TurbineSecurityException("Not implemented");
    }

    /**
     * Used for ordering Groups.
     *
     * @param obj The Object to compare to.
     * @return -1 if the name of the other object is lexically greater than 
     * this group, 1 if it is lexically lesser, 0 if they are equal.
     */
    public int compareTo(Object obj)
    {
        if(this.getClass() != obj.getClass())
            throw new ClassCastException();
        String name1 = ((Group)obj).getName();
        String name2 = this.getName();

        return name2.compareTo(name1);
    }

}
