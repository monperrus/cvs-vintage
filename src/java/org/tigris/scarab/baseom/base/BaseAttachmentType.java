package org.tigris.scarab.baseom.base;


// JDK classes
import java.util.*;
import java.math.*;

// Turbine classes
import org.apache.turbine.om.*;
import org.apache.turbine.om.peer.BasePeer;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.StringUtils;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.db.pool.DBConnection;

import org.tigris.scarab.baseom.*;
import org.tigris.scarab.baseom.peer.*;

/** 
 * This class was autogenerated by Torque on:
 *
 * [Thu Feb 22 18:38:08 PST 2001]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to AttachmentType 
 */
public abstract class BaseAttachmentType extends BaseObject
{
    /** the value for the attachment_type_id field */
    private SimpleKey attachment_type_id;
    /** the value for the attachment_type_name field */
    private String attachment_type_name;


    /**
     * Get the AttachmentTypeId
     * @return SimpleKey
     */
     public SimpleKey getAttachmentTypeId()
     {
          return attachment_type_id;
     }

                            
    /**
     * Set the value of AttachmentTypeId
     */
     public void setAttachmentTypeId(SimpleKey v ) throws Exception
     {
  
       
        
                
          // update associated Attachment
          if (collAttachments != null )
          {
              for (int i=0; i<collAttachments.size(); i++)
              {
                  ((Attachment)collAttachments.get(i))
                      .setTypeId(v);
              }
          }
       

         if ( !ObjectUtils.equals(this.attachment_type_id, v) )
        {
                       if (this.attachment_type_id == null)
            {
                this.attachment_type_id = v;
            }
            else
            {
                this.attachment_type_id.set(v);
            }
                      setModified(true);
        }
     }
    /**
     * Get the Name
     * @return String
     */
     public String getName()
     {
          return attachment_type_name;
     }

        
    /**
     * Set the value of Name
     */
     public void setName(String v ) 
     {
  
  

         if ( !ObjectUtils.equals(this.attachment_type_name, v) )
        {
                       this.attachment_type_name = v;
                      setModified(true);
        }
     }

 
    
                
      
    /**
     * Collection to store aggregation of collAttachments
     */
    private Vector collAttachments;
    /**
     * Temporary storage of collAttachments to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
//    private Vector tempcollAttachments;

    public void initAttachments()
    {
        if (collAttachments == null)
            collAttachments = new Vector();
    }

    /**
     * Method called to associate a Attachment object to this object
     * through the Attachment foreign key attribute
     *
     * @param Attachment l
     */
    public void addAttachments(Attachment l) throws Exception
    {
        /*
        if (collAttachments == null)
        {
            if (tempcollAttachments == null)
            {
                tempcollAttachments = new Vector();
            }
            tempcollAttachments.add(l);
        }
        else
        {
            collAttachments.add(l);
        }
        */
        getAttachments().add(l);
        l.setAttachmentType((AttachmentType)this);
    }

    /**
     * The criteria used to select the current contents of collAttachments
     */
    private Criteria lastAttachmentsCriteria = null;

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of 
     * getAttachments(new Criteria())
     */
    public Vector getAttachments() throws Exception
    {
        if (collAttachments == null)
        {
            collAttachments = getAttachments(new Criteria(10));
        }
        return collAttachments;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this AttachmentType is new, it will return
     * an empty collection; or if this AttachmentType has previously
     * been saved, it will retrieve related Attachments from storage.
     */
    public Vector getAttachments(Criteria criteria) throws Exception
    {
        if (collAttachments == null)
        {
            if ( isNew() ) 
            {
               collAttachments = new Vector();       
            } 
            else
            {
                   criteria.add(AttachmentPeer.ATTACHMENT_TYPE_ID, getAttachmentTypeId() );               
                   collAttachments = AttachmentPeer.doSelect(criteria);
            }
/*
            if (tempcollAttachments != null)
            {
                for (int i=0; i<tempcollAttachments.size(); i++)
                {
                    collAttachments.add(tempcollAttachments.get(i));
                }
                tempcollAttachments = null;
            }
*/
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(AttachmentPeer.ATTACHMENT_TYPE_ID, getAttachmentTypeId() );               
               if ( !lastAttachmentsCriteria.equals(criteria)  )
            {
                collAttachments = AttachmentPeer.doSelect(criteria);  
            }
        }
        lastAttachmentsCriteria = criteria; 

        return collAttachments;
    }
    

        
      
      
          
                    
                
        
        
   
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this AttachmentType is new, it will return
     * an empty collection; or if this AttachmentType has previously
     * been saved, it will retrieve related Attachments from storage.
     */
    public Vector getAttachmentsJoinIssue(Criteria criteria) 
        throws Exception
    {
        if (collAttachments == null)
        {
            if ( isNew() ) 
            {
               collAttachments = new Vector();       
            } 
            else
            {
                   criteria.add(AttachmentPeer.ATTACHMENT_TYPE_ID, getAttachmentTypeId() );               
                   collAttachments = AttachmentPeer.doSelectJoinIssue(criteria);
            }
            
            
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(AttachmentPeer.ATTACHMENT_TYPE_ID, getAttachmentTypeId() );               
               if ( !lastAttachmentsCriteria.equals(criteria)  )
            {
                collAttachments = AttachmentPeer.doSelectJoinIssue(criteria);
            }
        }
        lastAttachmentsCriteria = criteria; 

        return collAttachments;
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
            fieldNames_.add("AttachmentTypeId");
            fieldNames_.add("Name");
          }
      return fieldNames_;
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.
     */
    public Object getByName(String name)
    {
            if (name.equals("AttachmentTypeId"))
	{
	  	    return getAttachmentTypeId();
	  	}
            if (name.equals("Name"))
	{
	  	    return getName();
	  	}
            return null; 
    }
    /**
     * Retrieves a field from the object by name passed in
     * as a String.  The String must be one of the static
     * Strings defined in this Class' Peer.
     */
    public Object getByPeerName(String name)
    {
            if (name == AttachmentTypePeer.ATTACHMENT_TYPE_ID )
	    {
	  	    return getAttachmentTypeId();
	  	}
            if (name == AttachmentTypePeer.ATTACHMENT_TYPE_NAME )
	    {
	  	    return getName();
	  	}
            return null; 
    }

    /**
     * Retrieves a field from the object by Position as specified
     * in the xml schema.  Zero-based.
     */
    public Object getByPosition(int pos)
    {
            if ( pos == 0 )
	{
	  	    return getAttachmentTypeId();
	  	}
            if ( pos == 1 )
	{
	  	    return getName();
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
                AttachmentTypePeer.getMapBuilder()
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
                AttachmentTypePeer.doInsert((AttachmentType)this, dbCon);
            }
            else
            {
                AttachmentTypePeer.doUpdate((AttachmentType)this, dbCon);
                setNew(false);
            }
        }

                                    
                
          if (collAttachments != null )
          {
              for (int i=0; i<collAttachments.size(); i++)
              {
                  ((Attachment)collAttachments.get(i)).save(dbCon);
              }
          }
                  alreadyInSave = false;
      }
      }

                                        
    /** 
     * Set the Id using pk values.
     *
     * @param SimpleKey attachment_type_id
     */
    public void setPrimaryKey(
     SimpleKey attachment_type_id
                ) throws Exception
    {
         setAttachmentTypeId(attachment_type_id);
    }
    



    /** 
     * returns an id that differentiates this object from others
     * of its class.
     */
    public ObjectKey getPrimaryKey() 
    {
        return getAttachmentTypeId();
    }

    /**
     * Makes a copy of this object.  
     * It creates a new object filling in the simple attributes.
     * It then fills all the association collections and sets the
     * related objects to isNew=true.
     */
    public AttachmentType copy() throws Exception
    {
        AttachmentType copyObj = new AttachmentType();
        copyObj.setAttachmentTypeId(attachment_type_id);
        copyObj.setName(attachment_type_name);

                                
                
         List v = copyObj.getAttachments();
         for (int i=0; i<v.size(); i++)
         {
             ((BaseObject)v.get(i)).setNew(true);
         }
            
            
        copyObj.setAttachmentTypeId(null);
            return copyObj;
    }             
}

