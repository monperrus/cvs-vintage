

package org.tigris.scarab.om;


import java.util.List;
import java.util.Iterator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;

import org.tigris.scarab.om.ScarabUser;

/** 
 * This class manages MITList objects.  
 * The skeleton for this class was autogenerated by Torque  * You should add additional methods to this class to meet the
 * application requirements.  This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public class MITListManager
    extends BaseMITListManager
{
    /**
     * Creates a new <code>MITListManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public MITListManager()
        throws TorqueException
    {
        super();
    }

    public static MITList getCurrentModuleAllIssueTypesList(ScarabUser user)
        throws TorqueException
    {
        MITList list = getInstance(MITListPeer.CURRENT_MODULE_ALL_ISSUETYPES)
            .copy();
        list.setScarabUser(user);
        return list;
    }

    public static MITList getAllModulesAllIssueTypesList(ScarabUser user)
        throws TorqueException
    {
        MITList list = getInstance(MITListPeer.ALL_MODULES_ISSUETYPES)
            .copy();
        list.setScarabUser(user);
        return list;
    }

    public static MITList getAllModulesCurrentIssueTypeList(ScarabUser user)
        throws TorqueException
    {
        MITList list = getInstance(MITListPeer.ALL_MODULES_CURRENT_ISSUETYPE)
            .copy();
        list.setScarabUser(user);
        return list;
    }


    public static MITList getCurrentModuleAllIssueTypesList()
        throws TorqueException
    {
        return getInstance(MITListPeer.CURRENT_MODULE_ALL_ISSUETYPES);
    }

    public static MITList getAllModulesAllIssueTypesList()
        throws TorqueException
    {
        return getInstance(MITListPeer.ALL_MODULES_ISSUETYPES);
    }

    public static MITList getAllModulesCurrentIssueTypeList()
        throws TorqueException
    {
        return getInstance(MITListPeer.ALL_MODULES_CURRENT_ISSUETYPE);
    }


    /**
     * An issue has an associated Module and IssueType, this method takes
     * a list of issues and creates an MITList from these associations.
     *
     * @param issues a <code>List</code> value
     * @param user a <code>ScarabUser</code> value
     * @return a <code>MITList</code> value
     * @exception TorqueException if an error occurs
     */
    public static MITList getInstanceFromIssueList(List issues, 
                                                   ScarabUser user)
        throws TorqueException
    {
        if (issues == null) 
        {
            throw new IllegalArgumentException("Null issue list is not allowed.");
        }        
        if (user == null) 
        {
            throw new IllegalArgumentException("Null user is not allowed.");
        }
        
        MITList list = getInstance();
        list.setScarabUser(user);
        List dupeCheck = list.getMITListItems();
        Iterator i = issues.iterator();
        if (i.hasNext()) 
        {
            Issue issue = (Issue)i.next();
            MITListItem item = MITListItemManager.getInstance();
            item.setModule(issue.getModule());
            item.setIssueType(issue.getIssueType());
            if (!dupeCheck.contains(item)) 
            {
                list.addMITListItem(item);
            }
        }
        
        return list;
    }

}





