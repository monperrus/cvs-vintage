package org.tigris.scarab.baseom.base;


// JDK classes
import java.util.*;
import java.math.*;

// Turbine classes
import org.apache.turbine.om.*;
import org.apache.turbine.om.peer.BasePeer;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.db.pool.DBConnection;

import org.tigris.scarab.baseom.*;
import org.tigris.scarab.baseom.peer.*;

/** 
 * This class was autogenerated by Torque on:
 *
 * [Thu Feb 15 16:11:32 PST 2001]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to ScarabModule 
 */
public abstract class BaseScarabModule extends BaseObject
{
    /** the value for the module_id field */
    private int module_id;
    /** the value for the module_name field */
    private String module_name;
    /** the value for the module_description field */
    private String module_description;
    /** the value for the module_url field */
    private String module_url;
    /** the value for the parent_id field */
    private int parent_id;
    /** the value for the owner_id field */
    private int owner_id;
    /** the value for the qa_contact_id field */
    private int qa_contact_id;
    /** the value for the deleted field */
    private boolean deleted;


    /**
     * Get the ModuleId
     * @return int
     */
     public int getModuleId()
     {
          return module_id;
     }

                            
    /**
     * Set the value of ModuleId
     */
     public void setModuleId(int v ) throws Exception
     {
  
       
        
                
          // update associated ScarabIssue
          if (collScarabIssues != null )
          {
              for (int i=0; i<collScarabIssues.size(); i++)
              {
                  ((ScarabIssue)collScarabIssues.get(i))
                      .setModuleId(v);
              }
          }
                   
        
                
          // update associated ScarabRModuleAttribute
          if (collScarabRModuleAttributes != null )
          {
              for (int i=0; i<collScarabRModuleAttributes.size(); i++)
              {
                  ((ScarabRModuleAttribute)collScarabRModuleAttributes.get(i))
                      .setModuleId(v);
              }
          }
            
        
                
          // update associated ScarabRModuleUser
          if (collScarabRModuleUsers != null )
          {
              for (int i=0; i<collScarabRModuleUsers.size(); i++)
              {
                  ((ScarabRModuleUser)collScarabRModuleUsers.get(i))
                      .setModuleId(v);
              }
          }
       

           if (this.module_id != v)
           {
              this.module_id = v;
              setModified(true);
          }
     }
    /**
     * Get the Name
     * @return String
     */
     public String getName()
     {
          return module_name;
     }

        
    /**
     * Set the value of Name
     */
     public void setName(String v ) 
     {
  
  

           if ( !ObjectUtils.equals(this.module_name, v) )
           {
              this.module_name = v;
              setModified(true);
          }
     }
    /**
     * Get the Description
     * @return String
     */
     public String getDescription()
     {
          return module_description;
     }

        
    /**
     * Set the value of Description
     */
     public void setDescription(String v ) 
     {
  
  

           if ( !ObjectUtils.equals(this.module_description, v) )
           {
              this.module_description = v;
              setModified(true);
          }
     }
    /**
     * Get the Url
     * @return String
     */
     public String getUrl()
     {
          return module_url;
     }

        
    /**
     * Set the value of Url
     */
     public void setUrl(String v ) 
     {
  
  

           if ( !ObjectUtils.equals(this.module_url, v) )
           {
              this.module_url = v;
              setModified(true);
          }
     }
    /**
     * Get the ParentId
     * @return int
     */
     public int getParentId()
     {
          return parent_id;
     }

            
    /**
     * Set the value of ParentId
     */
     public void setParentId(int v ) throws Exception
     {
                  if ( aScarabModuleRelatedByParentId != null && !aScarabModuleRelatedByParentId.isNew())
        {
            throw new Exception("Can't set a foreign key directly after an "
                + " association is already made based on saved data.");
        }
  
  

           if (this.parent_id != v)
           {
              this.parent_id = v;
              setModified(true);
          }
     }
    /**
     * Get the OwnerId
     * @return int
     */
     public int getOwnerId()
     {
          return owner_id;
     }

        
    /**
     * Set the value of OwnerId
     */
     public void setOwnerId(int v ) 
     {
  
  

           if (this.owner_id != v)
           {
              this.owner_id = v;
              setModified(true);
          }
     }
    /**
     * Get the QaContactId
     * @return int
     */
     public int getQaContactId()
     {
          return qa_contact_id;
     }

        
    /**
     * Set the value of QaContactId
     */
     public void setQaContactId(int v ) 
     {
  
  

           if (this.qa_contact_id != v)
           {
              this.qa_contact_id = v;
              setModified(true);
          }
     }
    /**
     * Get the Deleted
     * @return boolean
     */
     public boolean getDeleted()
     {
          return deleted;
     }

        
    /**
     * Set the value of Deleted
     */
     public void setDeleted(boolean v ) 
     {
  
  

           if (this.deleted != v)
           {
              this.deleted = v;
              setModified(true);
          }
     }

 
 
   
                 
      
   
    /**
     * Declares an association between this object and a ScarabModule object
     *
     * @param ScarabModule v
     */
    private ScarabModule aScarabModuleRelatedByParentId;
    public void setScarabModuleRelatedByParentId(ScarabModule v) throws Exception
    {
        aScarabModuleRelatedByParentId = null;
           setParentId(v.getModuleId());
           aScarabModuleRelatedByParentId = v;
    }

                     
    public ScarabModule getScarabModuleRelatedByParentId() throws Exception
    {
        if ( aScarabModuleRelatedByParentId==null && (this.parent_id>0) )
        {
            aScarabModuleRelatedByParentId = ScarabModulePeer.retrieveByPK(this.parent_id);
            // The following can be used instead of the line above to
            // guarantee the related object contains a reference
            // to this object, but this level of coupling 
            // may be undesirable in many circumstances.
            // As it can lead to a db query with many results that may
            // never be used.  
            // ScarabModule obj = ScarabModulePeer.retrieveByPK(this.parent_id);
            // obj.addScarabModulesRelatedByParentId(this);
        }
        return aScarabModuleRelatedByParentId;
    }

    
                
      
    /**
     * Collection to store aggregation of collScarabIssues
     */
    private Vector collScarabIssues;
    /**
     * Temporary storage of collScarabIssues to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
//    private Vector tempcollScarabIssues;

    public void initScarabIssues()
    {
        if (collScarabIssues == null)
            collScarabIssues = new Vector();
    }

    /**
     * Method called to associate a ScarabIssue object to this object
     * through the ScarabIssue foreign key attribute
     *
     * @param ScarabIssue l
     */
    public void addScarabIssues(ScarabIssue l) throws Exception
    {
        /*
        if (collScarabIssues == null)
        {
            if (tempcollScarabIssues == null)
            {
                tempcollScarabIssues = new Vector();
            }
            tempcollScarabIssues.add(l);
        }
        else
        {
            collScarabIssues.add(l);
        }
        */
        getScarabIssues().add(l);
        l.setScarabModule((ScarabModule)this);
    }

    /**
     * The criteria used to select the current contents of collScarabIssues
     */
    private Criteria lastScarabIssuesCriteria = null;

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of 
     * getScarabIssues(new Criteria())
     */
    public Vector getScarabIssues() throws Exception
    {
        if (collScarabIssues == null)
        {
            collScarabIssues = getScarabIssues(new Criteria(10));
        }
        return collScarabIssues;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this ScarabModule is new, it will return
     * an empty collection; or if this ScarabModule has previously
     * been saved, it will retrieve related ScarabIssues from storage.
     */
    public Vector getScarabIssues(Criteria criteria) throws Exception
    {
        if (collScarabIssues == null)
        {
            if ( isNew() ) 
            {
               collScarabIssues = new Vector();       
            } 
            else
            {
                   criteria.add(ScarabIssuePeer.MODULE_ID, getModuleId() );               
                   collScarabIssues = ScarabIssuePeer.doSelect(criteria);
            }
/*
            if (tempcollScarabIssues != null)
            {
                for (int i=0; i<tempcollScarabIssues.size(); i++)
                {
                    collScarabIssues.add(tempcollScarabIssues.get(i));
                }
                tempcollScarabIssues = null;
            }
*/
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(ScarabIssuePeer.MODULE_ID, getModuleId() );               
               if ( !lastScarabIssuesCriteria.equals(criteria)  )
            {
                collScarabIssues = ScarabIssuePeer.doSelect(criteria);  
            }
        }
        lastScarabIssuesCriteria = criteria; 

        return collScarabIssues;
    }
   

        
      
         
          
                    
                
        
        
      



   



             
      
    /**
     * Collection to store aggregation of collScarabRModuleAttributes
     */
    private Vector collScarabRModuleAttributes;
    /**
     * Temporary storage of collScarabRModuleAttributes to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
//    private Vector tempcollScarabRModuleAttributes;

    public void initScarabRModuleAttributes()
    {
        if (collScarabRModuleAttributes == null)
            collScarabRModuleAttributes = new Vector();
    }

    /**
     * Method called to associate a ScarabRModuleAttribute object to this object
     * through the ScarabRModuleAttribute foreign key attribute
     *
     * @param ScarabRModuleAttribute l
     */
    public void addScarabRModuleAttributes(ScarabRModuleAttribute l) throws Exception
    {
        /*
        if (collScarabRModuleAttributes == null)
        {
            if (tempcollScarabRModuleAttributes == null)
            {
                tempcollScarabRModuleAttributes = new Vector();
            }
            tempcollScarabRModuleAttributes.add(l);
        }
        else
        {
            collScarabRModuleAttributes.add(l);
        }
        */
        getScarabRModuleAttributes().add(l);
        l.setScarabModule((ScarabModule)this);
    }

    /**
     * The criteria used to select the current contents of collScarabRModuleAttributes
     */
    private Criteria lastScarabRModuleAttributesCriteria = null;

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of 
     * getScarabRModuleAttributes(new Criteria())
     */
    public Vector getScarabRModuleAttributes() throws Exception
    {
        if (collScarabRModuleAttributes == null)
        {
            collScarabRModuleAttributes = getScarabRModuleAttributes(new Criteria(10));
        }
        return collScarabRModuleAttributes;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this ScarabModule is new, it will return
     * an empty collection; or if this ScarabModule has previously
     * been saved, it will retrieve related ScarabRModuleAttributes from storage.
     */
    public Vector getScarabRModuleAttributes(Criteria criteria) throws Exception
    {
        if (collScarabRModuleAttributes == null)
        {
            if ( isNew() ) 
            {
               collScarabRModuleAttributes = new Vector();       
            } 
            else
            {
                   criteria.add(ScarabRModuleAttributePeer.MODULE_ID, getModuleId() );               
                   collScarabRModuleAttributes = ScarabRModuleAttributePeer.doSelect(criteria);
            }
/*
            if (tempcollScarabRModuleAttributes != null)
            {
                for (int i=0; i<tempcollScarabRModuleAttributes.size(); i++)
                {
                    collScarabRModuleAttributes.add(tempcollScarabRModuleAttributes.get(i));
                }
                tempcollScarabRModuleAttributes = null;
            }
*/
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(ScarabRModuleAttributePeer.MODULE_ID, getModuleId() );               
               if ( !lastScarabRModuleAttributesCriteria.equals(criteria)  )
            {
                collScarabRModuleAttributes = ScarabRModuleAttributePeer.doSelect(criteria);  
            }
        }
        lastScarabRModuleAttributesCriteria = criteria; 

        return collScarabRModuleAttributes;
    }
    

        
      
      
          
                    
                
        
        
   
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this ScarabModule is new, it will return
     * an empty collection; or if this ScarabModule has previously
     * been saved, it will retrieve related ScarabRModuleAttributes from storage.
     */
    public Vector getScarabRModuleAttributesJoinScarabAttribute(Criteria criteria) 
        throws Exception
    {
        if (collScarabRModuleAttributes == null)
        {
            if ( isNew() ) 
            {
               collScarabRModuleAttributes = new Vector();       
            } 
            else
            {
                   criteria.add(ScarabRModuleAttributePeer.MODULE_ID, getModuleId() );               
                   collScarabRModuleAttributes = ScarabRModuleAttributePeer.doSelectJoinScarabAttribute(criteria);
            }
            
            
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(ScarabRModuleAttributePeer.MODULE_ID, getModuleId() );               
               if ( !lastScarabRModuleAttributesCriteria.equals(criteria)  )
            {
                collScarabRModuleAttributes = ScarabRModuleAttributePeer.doSelectJoinScarabAttribute(criteria);
            }
        }
        lastScarabRModuleAttributesCriteria = criteria; 

        return collScarabRModuleAttributes;
    }
      
      
         
          
                    
                
        
        
      



             
      
    /**
     * Collection to store aggregation of collScarabRModuleUsers
     */
    private Vector collScarabRModuleUsers;
    /**
     * Temporary storage of collScarabRModuleUsers to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
//    private Vector tempcollScarabRModuleUsers;

    public void initScarabRModuleUsers()
    {
        if (collScarabRModuleUsers == null)
            collScarabRModuleUsers = new Vector();
    }

    /**
     * Method called to associate a ScarabRModuleUser object to this object
     * through the ScarabRModuleUser foreign key attribute
     *
     * @param ScarabRModuleUser l
     */
    public void addScarabRModuleUsers(ScarabRModuleUser l) throws Exception
    {
        /*
        if (collScarabRModuleUsers == null)
        {
            if (tempcollScarabRModuleUsers == null)
            {
                tempcollScarabRModuleUsers = new Vector();
            }
            tempcollScarabRModuleUsers.add(l);
        }
        else
        {
            collScarabRModuleUsers.add(l);
        }
        */
        getScarabRModuleUsers().add(l);
        l.setScarabModule((ScarabModule)this);
    }

    /**
     * The criteria used to select the current contents of collScarabRModuleUsers
     */
    private Criteria lastScarabRModuleUsersCriteria = null;

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of 
     * getScarabRModuleUsers(new Criteria())
     */
    public Vector getScarabRModuleUsers() throws Exception
    {
        if (collScarabRModuleUsers == null)
        {
            collScarabRModuleUsers = getScarabRModuleUsers(new Criteria(10));
        }
        return collScarabRModuleUsers;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this ScarabModule is new, it will return
     * an empty collection; or if this ScarabModule has previously
     * been saved, it will retrieve related ScarabRModuleUsers from storage.
     */
    public Vector getScarabRModuleUsers(Criteria criteria) throws Exception
    {
        if (collScarabRModuleUsers == null)
        {
            if ( isNew() ) 
            {
               collScarabRModuleUsers = new Vector();       
            } 
            else
            {
                   criteria.add(ScarabRModuleUserPeer.MODULE_ID, getModuleId() );               
                   collScarabRModuleUsers = ScarabRModuleUserPeer.doSelect(criteria);
            }
/*
            if (tempcollScarabRModuleUsers != null)
            {
                for (int i=0; i<tempcollScarabRModuleUsers.size(); i++)
                {
                    collScarabRModuleUsers.add(tempcollScarabRModuleUsers.get(i));
                }
                tempcollScarabRModuleUsers = null;
            }
*/
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(ScarabRModuleUserPeer.MODULE_ID, getModuleId() );               
               if ( !lastScarabRModuleUsersCriteria.equals(criteria)  )
            {
                collScarabRModuleUsers = ScarabRModuleUserPeer.doSelect(criteria);  
            }
        }
        lastScarabRModuleUsersCriteria = criteria; 

        return collScarabRModuleUsers;
    }
    

        
      
         
          
                    
                
        
        
       
      
      
          
                    
                
        
        
   
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this ScarabModule is new, it will return
     * an empty collection; or if this ScarabModule has previously
     * been saved, it will retrieve related ScarabRModuleUsers from storage.
     */
    public Vector getScarabRModuleUsersJoinTurbineUser(Criteria criteria) 
        throws Exception
    {
        if (collScarabRModuleUsers == null)
        {
            if ( isNew() ) 
            {
               collScarabRModuleUsers = new Vector();       
            } 
            else
            {
                   criteria.add(ScarabRModuleUserPeer.MODULE_ID, getModuleId() );               
                   collScarabRModuleUsers = ScarabRModuleUserPeer.doSelectJoinTurbineUser(criteria);
            }
            
            
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(ScarabRModuleUserPeer.MODULE_ID, getModuleId() );               
               if ( !lastScarabRModuleUsersCriteria.equals(criteria)  )
            {
                collScarabRModuleUsers = ScarabRModuleUserPeer.doSelectJoinTurbineUser(criteria);
            }
        }
        lastScarabRModuleUsersCriteria = criteria; 

        return collScarabRModuleUsers;
    }
     



     
    
    private static Vector fieldNames_ = null;

    /**
     * Generate a list of field names.
     */
    public static Vector getFieldNames()
    {
      if (fieldNames_ == null)
      {
        fieldNames_ = new Vector();
            fieldNames_.add("ModuleId");
            fieldNames_.add("Name");
            fieldNames_.add("Description");
            fieldNames_.add("Url");
            fieldNames_.add("ParentId");
            fieldNames_.add("OwnerId");
            fieldNames_.add("QaContactId");
            fieldNames_.add("Deleted");
          }
      return fieldNames_;
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.
     */
    public Object getByName(String name)
    {
            if (name.equals("ModuleId"))
	{
	  	    return new Integer(getModuleId());
	  	}
            if (name.equals("Name"))
	{
	  	    return getName();
	  	}
            if (name.equals("Description"))
	{
	  	    return getDescription();
	  	}
            if (name.equals("Url"))
	{
	  	    return getUrl();
	  	}
            if (name.equals("ParentId"))
	{
	  	    return new Integer(getParentId());
	  	}
            if (name.equals("OwnerId"))
	{
	  	    return new Integer(getOwnerId());
	  	}
            if (name.equals("QaContactId"))
	{
	  	    return new Integer(getQaContactId());
	  	}
            if (name.equals("Deleted"))
	{
	  	    return new Boolean(getDeleted());
	  	}
            return null; 
    }
     	

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     */
    public void save() throws Exception
    {
         DBConnection dbCon = null;
        try
        {
            dbCon = BasePeer.beginTransaction(
                ScarabModulePeer.getMapBuilder()
                .getDatabaseMap().getName());
            save(dbCon);
        }
        catch(Exception e)
        {
            BasePeer.rollBackTransaction(dbCon);
            throw e;
        }
        BasePeer.commitTransaction(dbCon);

     }

      // flag to prevent endless save loop, if this object is referenced
    // by another object which falls in this transaction.
    private boolean alreadyInSave = false;
      /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     */
    public void save(DBConnection dbCon) throws Exception
    {
        if (!alreadyInSave)
      {
        alreadyInSave = true;
          if (isModified())
        {
            if (isNew())
            {
                ScarabModulePeer.doInsert((ScarabModule)this, dbCon);
            }
            else
            {
                ScarabModulePeer.doUpdate((ScarabModule)this, dbCon);
                setNew(false);
            }
        }

                                    
                
          if (collScarabIssues != null )
          {
              for (int i=0; i<collScarabIssues.size(); i++)
              {
                  ((ScarabIssue)collScarabIssues.get(i)).save(dbCon);
              }
          }
                                               
                
          if (collScarabRModuleAttributes != null )
          {
              for (int i=0; i<collScarabRModuleAttributes.size(); i++)
              {
                  ((ScarabRModuleAttribute)collScarabRModuleAttributes.get(i)).save(dbCon);
              }
          }
                                        
                
          if (collScarabRModuleUsers != null )
          {
              for (int i=0; i<collScarabRModuleUsers.size(); i++)
              {
                  ((ScarabRModuleUser)collScarabRModuleUsers.get(i)).save(dbCon);
              }
          }
                  alreadyInSave = false;
      }
      }

                                                                
    /** 
     * Set the Id using pk values.
     *
     * @param int module_id
     */
    public void setPrimaryKey(
                      int module_id
                                                                                                                 ) throws Exception
    {
                     setModuleId(module_id);
                                                                                                    }

    /** 
     * Set the Id using a : separated String of pk values.
     */
    public void setPrimaryKey(Object id) throws Exception
    {
        StringTokenizer st = new StringTokenizer(id.toString(), ":");
                           setModuleId( Integer.parseInt(st.nextToken()) );
                                                                                                            }


    /** 
     * returns an id that differentiates this object from others
     * of its class.
     */
    public Object getPrimaryKey() 
    {
        return ""
                      + getModuleId()
                                                                                                                 ;
    } 

    /** 
     * returns an id that can be used to specify this object in
     * a query string.
     */
    public String getQueryOID() 
    {
        return "ScarabModule[" + getPrimaryKey() + "]";
    }

    /**
     * Makes a copy of this object.  
     * It creates a new object filling in the simple attributes.
      * It then fills all the association collections and sets the
     * related objects to isNew=true.
      */
    public ScarabModule copy() throws Exception
    {
        ScarabModule copyObj = new ScarabModule();
         copyObj.setModuleId(module_id);
         copyObj.setName(module_name);
         copyObj.setDescription(module_description);
         copyObj.setUrl(module_url);
         copyObj.setParentId(parent_id);
         copyObj.setOwnerId(owner_id);
         copyObj.setQaContactId(qa_contact_id);
         copyObj.setDeleted(deleted);
 
                                  
                
         List v = copyObj.getScarabIssues();
         for (int i=0; i<v.size(); i++)
         {
             ((BaseObject)v.get(i)).setNew(true);
         }
                                              
                
         v = copyObj.getScarabRModuleAttributes();
         for (int i=0; i<v.size(); i++)
         {
             ((BaseObject)v.get(i)).setNew(true);
         }
                                         
                
         v = copyObj.getScarabRModuleUsers();
         for (int i=0; i<v.size(); i++)
         {
             ((BaseObject)v.get(i)).setNew(true);
         }
         
                       
        copyObj.setModuleId(NEW_ID);
                                                            return copyObj;
    }             
}
