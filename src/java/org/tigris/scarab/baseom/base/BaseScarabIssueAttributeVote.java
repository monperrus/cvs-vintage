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
 * extended all references should be to ScarabIssueAttributeVote 
 */
public abstract class BaseScarabIssueAttributeVote extends BaseObject
{
    /** the value for the issue_id field */
    private int issue_id;
    /** the value for the attribute_id field */
    private int attribute_id;
    /** the value for the user_id field */
    private int user_id;
    /** the value for the option_id field */
    private int option_id;


    /**
     * Get the IssueId
     * @return int
     */
     public int getIssueId()
     {
          return issue_id;
     }

            
    /**
     * Set the value of IssueId
     */
     public void setIssueId(int v ) throws Exception
     {
                  if ( aScarabIssueAttributeValue != null && !aScarabIssueAttributeValue.isNew())
        {
            throw new Exception("Can't set a foreign key directly after an "
                + " association is already made based on saved data.");
        }
  
  

           if (this.issue_id != v)
           {
              this.issue_id = v;
              setModified(true);
          }
     }
    /**
     * Get the AttributeId
     * @return int
     */
     public int getAttributeId()
     {
          return attribute_id;
     }

            
    /**
     * Set the value of AttributeId
     */
     public void setAttributeId(int v ) throws Exception
     {
                  if ( aScarabIssueAttributeValue != null && !aScarabIssueAttributeValue.isNew())
        {
            throw new Exception("Can't set a foreign key directly after an "
                + " association is already made based on saved data.");
        }
  
  

           if (this.attribute_id != v)
           {
              this.attribute_id = v;
              setModified(true);
          }
     }
    /**
     * Get the UserId
     * @return int
     */
     public int getUserId()
     {
          return user_id;
     }

            
    /**
     * Set the value of UserId
     */
     public void setUserId(int v ) throws Exception
     {
                  if ( aTurbineUser != null && !aTurbineUser.isNew())
        {
            throw new Exception("Can't set a foreign key directly after an "
                + " association is already made based on saved data.");
        }
  
  

           if (this.user_id != v)
           {
              this.user_id = v;
              setModified(true);
          }
     }
    /**
     * Get the OptionId
     * @return int
     */
     public int getOptionId()
     {
          return option_id;
     }

            
    /**
     * Set the value of OptionId
     */
     public void setOptionId(int v ) throws Exception
     {
                  if ( aScarabAttributeOption != null && !aScarabAttributeOption.isNew())
        {
            throw new Exception("Can't set a foreign key directly after an "
                + " association is already made based on saved data.");
        }
  
  

           if (this.option_id != v)
           {
              this.option_id = v;
              setModified(true);
          }
     }

 
 
   
             
   
   
    /**
     * Declares an association between this object and a ScarabAttributeOption object
     *
     * @param ScarabAttributeOption v
     */
    private ScarabAttributeOption aScarabAttributeOption;
    public void setScarabAttributeOption(ScarabAttributeOption v) throws Exception
    {
        aScarabAttributeOption = null;
           setOptionId(v.getOptionId());
           aScarabAttributeOption = v;
    }

                     
    public ScarabAttributeOption getScarabAttributeOption() throws Exception
    {
        if ( aScarabAttributeOption==null && (this.option_id>0) )
        {
            aScarabAttributeOption = ScarabAttributeOptionPeer.retrieveByPK(this.option_id);
            // The following can be used instead of the line above to
            // guarantee the related object contains a reference
            // to this object, but this level of coupling 
            // may be undesirable in many circumstances.
            // As it can lead to a db query with many results that may
            // never be used.  
            // ScarabAttributeOption obj = ScarabAttributeOptionPeer.retrieveByPK(this.option_id);
            // obj.addScarabIssueAttributeVotes(this);
        }
        return aScarabAttributeOption;
    }

 
   
                    
   
   
    /**
     * Declares an association between this object and a ScarabIssueAttributeValue object
     *
     * @param ScarabIssueAttributeValue v
     */
    private ScarabIssueAttributeValue aScarabIssueAttributeValue;
    public void setScarabIssueAttributeValue(ScarabIssueAttributeValue v) throws Exception
    {
        aScarabIssueAttributeValue = null;
           setIssueId(v.getIssueId());
           setAttributeId(v.getAttributeId());
           aScarabIssueAttributeValue = v;
    }

                                
    public ScarabIssueAttributeValue getScarabIssueAttributeValue() throws Exception
    {
        if ( aScarabIssueAttributeValue==null && (this.issue_id>0 && this.attribute_id>0) )
        {
            aScarabIssueAttributeValue = ScarabIssueAttributeValuePeer.retrieveByPK(this.issue_id, this.attribute_id);
            // The following can be used instead of the line above to
            // guarantee the related object contains a reference
            // to this object, but this level of coupling 
            // may be undesirable in many circumstances.
            // As it can lead to a db query with many results that may
            // never be used.  
            // ScarabIssueAttributeValue obj = ScarabIssueAttributeValuePeer.retrieveByPK(this.issue_id, this.attribute_id);
            // obj.addScarabIssueAttributeVotes(this);
        }
        return aScarabIssueAttributeValue;
    }

 
   
             
   
   
    /**
     * Declares an association between this object and a TurbineUser object
     *
     * @param TurbineUser v
     */
    private TurbineUser aTurbineUser;
    public void setTurbineUser(TurbineUser v) throws Exception
    {
        aTurbineUser = null;
           setUserId(v.getUserId());
           aTurbineUser = v;
    }

                     
    public TurbineUser getTurbineUser() throws Exception
    {
        if ( aTurbineUser==null && (this.user_id>0) )
        {
            aTurbineUser = TurbineUserPeer.retrieveByPK(this.user_id);
            // The following can be used instead of the line above to
            // guarantee the related object contains a reference
            // to this object, but this level of coupling 
            // may be undesirable in many circumstances.
            // As it can lead to a db query with many results that may
            // never be used.  
            // TurbineUser obj = TurbineUserPeer.retrieveByPK(this.user_id);
            // obj.addScarabIssueAttributeVotes(this);
        }
        return aTurbineUser;
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
            fieldNames_.add("IssueId");
            fieldNames_.add("AttributeId");
            fieldNames_.add("UserId");
            fieldNames_.add("OptionId");
          }
      return fieldNames_;
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.
     */
    public Object getByName(String name)
    {
            if (name.equals("IssueId"))
	{
	  	    return new Integer(getIssueId());
	  	}
            if (name.equals("AttributeId"))
	{
	  	    return new Integer(getAttributeId());
	  	}
            if (name.equals("UserId"))
	{
	  	    return new Integer(getUserId());
	  	}
            if (name.equals("OptionId"))
	{
	  	    return new Integer(getOptionId());
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
                ScarabIssueAttributeVotePeer.getMapBuilder()
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
                ScarabIssueAttributeVotePeer.doInsert((ScarabIssueAttributeVote)this, dbCon);
            }
            else
            {
                ScarabIssueAttributeVotePeer.doUpdate((ScarabIssueAttributeVote)this, dbCon);
                setNew(false);
            }
        }

              alreadyInSave = false;
      }
      }

                                                                
    /** 
     * Set the Id using pk values.
     *
     * @param int issue_id
     * @param int attribute_id
     * @param int user_id
     */
    public void setPrimaryKey(
                      int issue_id
                                      , int attribute_id
                                      , int user_id
                                         ) throws Exception
    {
                     setIssueId(issue_id);
                             setAttributeId(attribute_id);
                             setUserId(user_id);
                            }

    /** 
     * Set the Id using a : separated String of pk values.
     */
    public void setPrimaryKey(Object id) throws Exception
    {
        StringTokenizer st = new StringTokenizer(id.toString(), ":");
                           setIssueId( Integer.parseInt(st.nextToken()) );
                                          setAttributeId( Integer.parseInt(st.nextToken()) );
                                          setUserId( Integer.parseInt(st.nextToken()) );
                                    }


    /** 
     * returns an id that differentiates this object from others
     * of its class.
     */
    public Object getPrimaryKey() 
    {
        return ""
                      + getIssueId()
                                      + ":"  + getAttributeId()
                                      + ":"  + getUserId()
                                         ;
    } 

    /** 
     * returns an id that can be used to specify this object in
     * a query string.
     */
    public String getQueryOID() 
    {
        return "ScarabIssueAttributeVote[" + getPrimaryKey() + "]";
    }

}
