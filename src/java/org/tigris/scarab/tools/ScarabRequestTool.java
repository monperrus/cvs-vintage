package org.tigris.scarab.tools;

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
import java.util.ArrayList;
import java.util.Iterator;

// Turbine
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.ComboKey;
import org.apache.turbine.RunData;
import org.apache.turbine.modules.Module;
import org.apache.turbine.tool.IntakeTool;
import org.apache.fulcrum.intake.Intake;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.pool.RecyclableSupport;
import org.apache.fulcrum.util.parser.StringValueParser;
import org.apache.commons.util.SequencedHashtable;

// Scarab
import org.tigris.scarab.om.ScarabUser;
import org.tigris.scarab.services.user.UserManager;
import org.tigris.scarab.om.Issue;
import org.tigris.scarab.om.IssuePeer;
import org.tigris.scarab.om.Query;
import org.tigris.scarab.om.QueryPeer;
import org.tigris.scarab.om.IssueTemplateInfo;
import org.tigris.scarab.om.IssueTemplateInfoPeer;
import org.tigris.scarab.om.Depend;
import org.tigris.scarab.om.DependPeer;
import org.tigris.scarab.om.ScarabModulePeer;
import org.tigris.scarab.om.Attribute;
import org.tigris.scarab.om.Attachment;
import org.tigris.scarab.om.AttachmentPeer;
import org.tigris.scarab.om.AttributeOption;
import org.tigris.scarab.om.ROptionOption;
import org.tigris.scarab.om.AttributeOptionPeer;
import org.tigris.scarab.om.RModuleAttribute;
import org.tigris.scarab.om.RModuleAttributePeer;
import org.tigris.scarab.om.AttributeValue;
import org.tigris.scarab.om.ParentChildAttributeOption;
import org.tigris.scarab.services.module.ModuleEntity;
import org.tigris.scarab.services.module.ModuleManager;
import org.tigris.scarab.util.ScarabConstants;
import org.tigris.scarab.util.word.IssueSearch;
import org.tigris.scarab.util.ReportGenerator;

/**
 * This class is used by the Scarab API
 */
public class ScarabRequestTool
    extends RecyclableSupport
    implements ScarabRequestScope
{
    /** the object containing request specific data */
    private RunData data;

    /**
     * A User object for use within the Scarab API.
     */
    private ScarabUser user = null;

    /**
     * A Issue object for use within the Scarab API.
     */
    private Issue issue = null;

    /**
     * A Attribute object for use within the Scarab API.
     */
    private Attribute attribute = null;

    /**
     * A Attachment object for use within the Scarab API.
     */
    private Attachment attachment = null;

    /**
     * A Depend object for use within the Scarab API.
     */
    private Depend depend = null;

    /**
     * A Query object for use within the Scarab API.
     */
    private Query query = null;

    /**
     * An IssueTemplateInfo object for use within the Scarab API.
     */
    private IssueTemplateInfo templateInfo = null;

    /**
     * A ModuleEntity object which represents the current module
     * selected by the user within a request.
     */
    private ModuleEntity currentModule = null;

    /**
     * A ModuleEntity object
     */
    private ModuleEntity module = null;

    /**
     * A AttributeOption object for use within the Scarab API.
     */
    private AttributeOption attributeOption = null;

    /**
     * A ROptionOption
     */
    private ROptionOption roo = null;

    /**
     * A ParentChildAttributeOption
     */
    private ParentChildAttributeOption pcao = null;

    /**
     * A ReportGenerator
     */
    private ReportGenerator reportGenerator = null;

    /**
     * A AttributeOption object for use within the Scarab API.
     */
    private int nbrPages = 0;
    private int prevPage = 0;
    private int nextPage = 0;
    
    public void init(Object data)
    {
        this.data = (RunData)data;
    }

    /**
     * nulls out the issue and user objects
     */
    public void refresh()
    {
        this.user = null;
        this.issue = null;
    }

    /**
     * Constructor does initialization stuff
     */    
    public ScarabRequestTool()
    {
        //intake = new IntakeSystem();
    }

    /**
     * A Attribute object for use within the Scarab API.
     */
    public void setAttribute (Attribute attribute)
    {
        this.attribute = attribute;
    }

    /**
     * A Depend object for use within the Scarab API.
     */
    public void setDepend (Depend depend)
    {
        this.depend = depend;
    }

    /**
     * A Query object for use within the Scarab API.
     */
    public void setQuery (Query query)
    {
        this.query = query;
    }

    /**
     * Get the intake tool. FIXME: why is it getting it
     * from the Module and not from the IntakeService?
     */
    private IntakeTool getIntakeTool()
    {
        return (IntakeTool)Module.getTemplateContext(data)
            .get(ScarabConstants.INTAKE_TOOL);
    }

    /**
     * Gets an instance of a ROptionOption from this tool.
     * if it is null it will return a new instance of an 
     * empty ROptionOption and set it within this tool.
     */
    public ROptionOption getROptionOption()
    {
        if (roo == null)
        {
            roo = ROptionOption.getInstance();
        }
        return roo;
    }

    /**
     * Sets an instance of a ROptionOption
     */
    public void setROptionOption(ROptionOption roo)
    {
        this.roo = roo;
    }

    /**
     * A IssueTemplateInfo object for use within the Scarab API.
     */
    public void setIssueTemplateInfo (IssueTemplateInfo templateInfo)
    {
        this.templateInfo = templateInfo;
    }

    /**
     * Gets an instance of a ParentChildAttributeOption from this tool.
     * if it is null it will return a new instance of an 
     * empty ParentChildAttributeOption and set it within this tool.
     */
    public ParentChildAttributeOption getParentChildAttributeOption()
    {
        if (pcao == null)
        {
            pcao = ParentChildAttributeOption.getInstance();
        }
        return pcao;
    }

    /**
     * Sets an instance of a ParentChildAttributeOption
     */
    public void setParentChildAttributeOption(ParentChildAttributeOption roo)
    {
        this.pcao = pcao;
    }

    /**
     * A Attribute object for use within the Scarab API.
     */
    public void setAttributeOption (AttributeOption option)
    {
        this.attributeOption = option;
    }

    /**
     * A Attribute object for use within the Scarab API.
     */
    public AttributeOption getAttributeOption()
        throws Exception
    {
try{
        if (attributeOption == null)
        {
            String optId = getIntakeTool()
                .get("AttributeOption", IntakeTool.DEFAULT_KEY)
                .get("OptionId").toString();
            if ( optId == null || optId.length() == 0 )
            {
                attributeOption = AttributeOption.getInstance();
            }
            else 
            {
                attributeOption = AttributeOptionPeer
                    .retrieveByPK(new NumberKey(optId));
            }
        }
}catch(Exception e){e.printStackTrace();}
        return attributeOption;
    }

    /**
     * @see org.tigris.scarab.tools.ScarabRequestScope#setUser(ScarabUser)
     */
    public void setUser (ScarabUser user)
    {
        this.user = user;
    }

    /**
     * @see org.tigris.scarab.tools.ScarabRequestScope#getUser()
     */
    public ScarabUser getUser()
    {
        return this.user;
    }

    /**
     * Return a specific User by ID from within the system.
     * You can pass in either a NumberKey or something that
     * will resolve to a String object as id.toString() is 
     * called on everything that isn't a NumberKey.
     */
    public ScarabUser getUser(Object id)
     throws Exception
    {
        ScarabUser su = null;
        try
        {
            ObjectKey pk = null;
            if (id instanceof NumberKey)
            {
                pk = (ObjectKey) id;
            }
            else
            {
                pk = (ObjectKey)new NumberKey(id.toString());
            }
            su = UserManager.getInstance(pk);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return su;
    }

    /**
     * A Attribute object for use within the Scarab API.
     */
    public Attribute getAttribute()
     throws Exception
    {
try{
        if (attribute == null)
        {
            String attId = getIntakeTool()
                .get("Attribute", IntakeTool.DEFAULT_KEY)
                .get("Id").toString();
            if ( attId == null || attId.length() == 0 )
            {
                attribute = Attribute.getInstance();
            }
            else 
            {
                attribute = Attribute.getInstance(new NumberKey(attId));
            }
        }        
}catch(Exception e){e.printStackTrace();}
        return attribute;
 
   }

    /**
     * A Query object for use within the Scarab API.
     */
    public Query getQuery()
     throws Exception
    {
        try
        {
            if (query == null)
            {
                String queryId = data.getParameters()
                    .getString("queryId"); 
                if ( queryId == null || queryId.length() == 0 )
                {
                    query = Query.getInstance();
                }
                else 
                {
                    query = QueryPeer.retrieveByPK(new NumberKey(queryId));
                }
            }        
        }        
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return query;
    }

    /**
     * A IssueTemplateInfo object for use within the Scarab API.
     */
    public IssueTemplateInfo getIssueTemplateInfo()
     throws Exception
    {
        try
        {
            if (templateInfo == null)
            {
                String issueId = data.getParameters()
                    .getString("issue_id"); 

                if ( issueId == null || issueId.length() == 0 )
                {
                    templateInfo = IssueTemplateInfo.getInstance();
                }
                else 
                {
                    templateInfo = IssueTemplateInfoPeer
                        .retrieveByPK(new NumberKey(issueId));
                }
            }        
        }        
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return templateInfo;
    }

    /**
     * A Depend object for use within the Scarab API.
     */
    public Depend getDepend()
     throws Exception
    {
        try
        {
            if (depend == null)
            {
                String dependId = getIntakeTool()
                    .get("Depend", IntakeTool.DEFAULT_KEY).get("Id").toString();
                if ( dependId == null || dependId.length() == 0 )
                {
                    depend = Depend.getInstance();
                }
                else 
                {
                    depend = DependPeer.retrieveByPK(new NumberKey(dependId));
                }
            }        
        }        
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return depend;
    }

    /**
     * A Attachment object for use within the Scarab API.
     */
    public Attachment getAttachment()
     throws Exception
    {
try{
        if (attachment == null)
        {
            Group att = getIntakeTool()
                .get("Attachment", IntakeTool.DEFAULT_KEY, false);
            if ( att != null ) 
            {            
                String attId =  att.get("Id").toString();
                if ( attId == null || attId.length() == 0 )
                {
                    attachment = new Attachment();
                }
                else 
                {
                    attachment = AttachmentPeer
                        .retrieveByPK(new NumberKey(attId));
                }
            }
            else 
            {
                attachment = new Attachment();
            }
        }        
}catch(Exception e){e.printStackTrace(); throw e;}
        return attachment;
    }

    /**
     * Get an RModuleAttribute object. 
     *
     * @return a <code>Module</code> value
     */
    public RModuleAttribute getRModuleAttribute()
        throws Exception
    {
        RModuleAttribute rma = null;
      try{
            ComboKey rModAttId = (ComboKey)getIntakeTool()
                .get("RModuleAttribute", IntakeTool.DEFAULT_KEY)
                .get("Id").getValue();
            if ( rModAttId == null )
            {
                NumberKey attId = (NumberKey)getIntakeTool()
                    .get("Attribute", IntakeTool.DEFAULT_KEY)
                    .get("Id").getValue();
                ModuleEntity currentModule = getCurrentModule();
                if ( attId != null && currentModule != null )
                {
                    NumberKey[] nka = {attId, currentModule.getModuleId()};
                    rma = RModuleAttributePeer.retrieveByPK(new ComboKey(nka));
                }
                else 
                {
                    rma = new RModuleAttribute();
                }
            }
            else 
            {
                rma = RModuleAttributePeer.retrieveByPK(rModAttId);
            }
      }catch(Exception e){e.printStackTrace();}
        return rma;
    }

    /**
     * A Module object for use within the Scarab API.
     */
    public void setModule(ModuleEntity module)
    {
        this.module = module;
    }

    /**
     * Get an Module object. 
     *
     * @return a <code>ModuleEntity</code> value
     */
    public ModuleEntity getModule()
        throws Exception
    {
      try{
        String modId = getIntakeTool()
            .get("Module", IntakeTool.DEFAULT_KEY).get("Id").toString();
        if ( modId == null || modId.length() == 0 )
        {
            module = ModuleManager.getInstance();
        }
        else 
        {
            module = ModuleManager.getInstance(new NumberKey(modId));
        }
      }catch(Exception e){e.printStackTrace();}
       return module;
    }

    /**
     * Get a specific module by key value. Returns null if
     * the Module could not be found
     *
     * @param key a <code>String</code> value
     * @return a <code>Module</code> value
     */
    public ModuleEntity getModule(String key)
    {
        ModuleEntity me = null;
        try
        {
            me = (ModuleEntity) 
                ScarabModulePeer.retrieveByPK(new NumberKey(key));
        }
        catch (Exception e)
        {
        }
        return me;
    }

    /**
     * Gets the ModuleEntity associated with the information
     * passed around in the query string. Returns null if
     * the Module could not be found.
     */
    public ModuleEntity getCurrentModule()
    {
        if (currentModule == null)
        {
            currentModule = getModule(
                data.getParameters()
                .getString(ScarabConstants.CURRENT_MODULE));
        }
        return currentModule;
    }

    /**
     * Sets the current ModuleEntity
     */
    public void setCurrentModule(ModuleEntity me)
    {
        currentModule = me;
    }

    /**
     * A Issue object for use within the Scarab API.
     */
    public void setIssue(Issue issue)
    {
        this.issue = issue;
    }

    /**
     * Get an Issue object. If it is the first time calling,
     * it will be a new blank issue object.
     *
     * @return a <code>Issue</code> value
     */
    public Issue getIssue()
        throws Exception
    {
        if (issue == null)
        {
            Group issueGroup = getIntakeTool()
                .get("Issue", IntakeTool.DEFAULT_KEY, false);
            if ( issueGroup != null ) 
            {            
                String issueId =  issueGroup.get("Id").toString();
                if ( issueId == null || issueId.length() == 0 )
                {
                    issue = new Issue();
                }
                else 
                {
                    issue = IssuePeer
                        .retrieveByPK(new NumberKey(issueId));
                }
            }
            else if ( data.getParameters().getString("issue_id") != null ) 
            {                
                String issueId = data.getParameters().getString("issue_id");
                if ( issueId.length() == 0 )
                {
                    issue = new Issue();
                }
                else 
                {
                    issue = IssuePeer
                        .retrieveByPK(new NumberKey(issueId));
                }
            }
            else 
            {
                issue = new Issue();
            }
        }        
        return issue;
    }

    /**
     * The id may be a primary key or an issue id.
     *
     * @param key a <code>String</code> value
     * @return a <code>Issue</code> value
     */
    public Issue getIssue(String key)
    {
        Issue issue = null;
        try
        {
            issue = IssuePeer.retrieveByPK(new NumberKey(key));
        }
        catch (Exception e)
        {
            // was not a primary key, try fid
            try
            {
                Issue.FederatedId fid = new Issue.FederatedId(key);
                if ( fid.getDomain() == null ) 
                {
                    // handle null (always null right now)
                }
                issue = Issue.getIssueById(fid);
            }
            catch (NumberFormatException nfe)
            {
                // invalid id, just return null
            }
        }
        return issue;
    }

    /**
     * Get a list of Issue objects.
     *
     * @return a <code>Issue</code> value
     */
    public List getIssues()
        throws Exception
    {
        List issues = null;

        Group issueGroup = getIntakeTool()
            .get("Issue", IntakeTool.DEFAULT_KEY, false);
        if ( issueGroup != null ) 
        {            
            NumberKey[] issueIds =  (NumberKey[])
                issueGroup.get("Ids").getValue();
            if ( issueIds != null ) 
            {            
                issues = new ArrayList(issueIds.length);
                for ( int i=0; i<issueIds.length; i++ ) 
                {
                    issues.add(IssuePeer.retrieveByPK(issueIds[i]));
                }
            }
        }
        else if ( data.getParameters().getString("issue_ids") != null ) 
        {                
            String[] issueIdStrings = data.getParameters()
                .getStrings("issue_ids");
            issues = new ArrayList(issueIdStrings.length);
            for ( int i=0; i<issueIdStrings.length; i++ ) 
            {
                issues.add(IssuePeer
                           .retrieveByPK(new NumberKey(issueIdStrings[i])));
            }
        }
        return issues;
    }

    /**
     * Get a new SearchIssue object. 
     *
     * @return a <code>Issue</code> value
     */
    public IssueSearch getSearch()
        throws Exception
    {
        IssueSearch search = new IssueSearch();
        if (getCurrentModule() == null)
        {
            throw new Exception ("SRT:getSearch() current module is null");
        }
        search.setModuleCast(getCurrentModule());
        return search;
    }

    public Intake getConditionalIntake(String parameter)
        throws Exception
    {
        Intake intake = null;
        String param = data.getParameters().getString(parameter);
        if ( param == null ) 
        {            
            intake = getIntakeTool();
        }
        else 
        {
            intake = new Intake();
            StringValueParser parser = new StringValueParser();
            parser.parse(param, '&', '=', true);
            intake.init(parser);
        }

        return intake;
    }

    public List getCurrentSearchResults()
        throws Exception
    {
        Intake intake = getConditionalIntake(ScarabConstants.CURRENT_QUERY);
        
        IssueSearch search = new IssueSearch();
        Group searchGroup = intake.get("SearchIssue", 
                                       getSearch().getQueryKey() );
        searchGroup.setProperties(search);
        
        search.setModuleCast(getCurrentModule());
        SequencedHashtable avMap = search.getModuleAttributeValuesMap();
        Iterator i = avMap.iterator();
        while (i.hasNext()) 
        {
            AttributeValue aval = (AttributeValue)avMap.get(i.next());
            Group group = intake.get("AttributeValue", aval.getQueryKey());
            if ( group != null ) 
            {
                group.setProperties(aval);
            }                
        }
        
        return search.getMatchingIssues();
    }

    /**
     * a report helper class
     */
    public ReportGenerator getReport()
    {
        if ( reportGenerator == null ) 
        {
            reportGenerator = new ReportGenerator();
            reportGenerator.setModule(getCurrentModule());
        }
        
        return reportGenerator;
    }
    
    /**
     * Return a subset of the passed-in list.
     */
    public List getPaginatedList( List fullList, int pgNbr, 
                                  int nbrItemsPerPage)
    {
        this.nbrPages =  (int)Math.ceil((float)fullList.size() 
                                               / nbrItemsPerPage);
        this.nextPage = pgNbr + 1;
        this.prevPage = pgNbr - 1;
        return fullList.subList
           ((pgNbr - 1) * nbrItemsPerPage,
            Math.min(pgNbr * nbrItemsPerPage, fullList.size()));
    }

    /**
     * Get the cached list of issue id's resulting from a search
     * And return the list of issues.
     */
    public List getIssueList() throws Exception
    {
        return getCurrentSearchResults();
    }

    /**
     * Return the number of paginated pages.
     *
     */
    public int getNbrPages()
    {
        return nbrPages;
    }

    /**
     * Return the next page in the paginated list.
     *
     */
    public int getNextPage()
    {
        if (nextPage <= nbrPages)
        {
            return nextPage;
        }
        else
        {
            return 0;
        }       
    }

    /**
     * Return the previous page in the paginated list.
     *
     */
    public int getPrevPage()
    {
        return prevPage;
    }


    // ****************** Recyclable implementation ************************

    /**
     * Disposes the object after use. The method is called when the
     * object is returned to its pool.  The dispose method must call
     * its super.
     */
    public void dispose()
    {
        super.dispose();

        data = null;
        user = null;
        issue = null;
        attribute = null;
    }
}
