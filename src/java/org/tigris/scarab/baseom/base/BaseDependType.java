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
 * extended all references should be to DependType 
 */
public abstract class BaseDependType extends BaseObject
{
    /** the value for the depend_type_id field */
    private SimpleKey depend_type_id;
    /** the value for the depend_type_name field */
    private String depend_type_name;


    /**
     * Get the DependTypeId
     * @return SimpleKey
     */
     public SimpleKey getDependTypeId()
     {
          return depend_type_id;
     }

                            
    /**
     * Set the value of DependTypeId
     */
     public void setDependTypeId(SimpleKey v ) throws Exception
     {
  
       
        
                
          // update associated Depend
          if (collDepends != null )
          {
              for (int i=0; i<collDepends.size(); i++)
              {
                  ((Depend)collDepends.get(i))
                      .setTypeId(v);
              }
          }
       

         if ( !ObjectUtils.equals(this.depend_type_id, v) )
        {
                       if (this.depend_type_id == null)
            {
                this.depend_type_id = v;
            }
            else
            {
                this.depend_type_id.set(v);
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
          return depend_type_name;
     }

        
    /**
     * Set the value of Name
     */
     public void setName(String v ) 
     {
  
  

         if ( !ObjectUtils.equals(this.depend_type_name, v) )
        {
                       this.depend_type_name = v;
                      setModified(true);
        }
     }

 
    
                
      
    /**
     * Collection to store aggregation of collDepends
     */
    private Vector collDepends;
    /**
     * Temporary storage of collDepends to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
//    private Vector tempcollDepends;

    public void initDepends()
    {
        if (collDepends == null)
            collDepends = new Vector();
    }

    /**
     * Method called to associate a Depend object to this object
     * through the Depend foreign key attribute
     *
     * @param Depend l
     */
    public void addDepends(Depend l) throws Exception
    {
        /*
        if (collDepends == null)
        {
            if (tempcollDepends == null)
            {
                tempcollDepends = new Vector();
            }
            tempcollDepends.add(l);
        }
        else
        {
            collDepends.add(l);
        }
        */
        getDepends().add(l);
        l.setDependType((DependType)this);
    }

    /**
     * The criteria used to select the current contents of collDepends
     */
    private Criteria lastDependsCriteria = null;

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of 
     * getDepends(new Criteria())
     */
    public Vector getDepends() throws Exception
    {
        if (collDepends == null)
        {
            collDepends = getDepends(new Criteria(10));
        }
        return collDepends;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this DependType is new, it will return
     * an empty collection; or if this DependType has previously
     * been saved, it will retrieve related Depends from storage.
     */
    public Vector getDepends(Criteria criteria) throws Exception
    {
        if (collDepends == null)
        {
            if ( isNew() ) 
            {
               collDepends = new Vector();       
            } 
            else
            {
                   criteria.add(DependPeer.DEPEND_TYPE_ID, getDependTypeId() );               
                   collDepends = DependPeer.doSelect(criteria);
            }
/*
            if (tempcollDepends != null)
            {
                for (int i=0; i<tempcollDepends.size(); i++)
                {
                    collDepends.add(tempcollDepends.get(i));
                }
                tempcollDepends = null;
            }
*/
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(DependPeer.DEPEND_TYPE_ID, getDependTypeId() );               
               if ( !lastDependsCriteria.equals(criteria)  )
            {
                collDepends = DependPeer.doSelect(criteria);  
            }
        }
        lastDependsCriteria = criteria; 

        return collDepends;
    }
     

        
      
      
              
                            
                
        
        
   
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this DependType is new, it will return
     * an empty collection; or if this DependType has previously
     * been saved, it will retrieve related Depends from storage.
     */
    public Vector getDependsJoinIssueRelatedByObservedId(Criteria criteria) 
        throws Exception
    {
        if (collDepends == null)
        {
            if ( isNew() ) 
            {
               collDepends = new Vector();       
            } 
            else
            {
                   criteria.add(DependPeer.DEPEND_TYPE_ID, getDependTypeId() );               
                   collDepends = DependPeer.doSelectJoinIssueRelatedByObservedId(criteria);
            }
            
            
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(DependPeer.DEPEND_TYPE_ID, getDependTypeId() );               
               if ( !lastDependsCriteria.equals(criteria)  )
            {
                collDepends = DependPeer.doSelectJoinIssueRelatedByObservedId(criteria);
            }
        }
        lastDependsCriteria = criteria; 

        return collDepends;
    }
      
      
      
              
                            
                
        
        
   
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection. 
     * Otherwise if this DependType is new, it will return
     * an empty collection; or if this DependType has previously
     * been saved, it will retrieve related Depends from storage.
     */
    public Vector getDependsJoinIssueRelatedByObserverId(Criteria criteria) 
        throws Exception
    {
        if (collDepends == null)
        {
            if ( isNew() ) 
            {
               collDepends = new Vector();       
            } 
            else
            {
                   criteria.add(DependPeer.DEPEND_TYPE_ID, getDependTypeId() );               
                   collDepends = DependPeer.doSelectJoinIssueRelatedByObserverId(criteria);
            }
            
            
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                   criteria.add(DependPeer.DEPEND_TYPE_ID, getDependTypeId() );               
               if ( !lastDependsCriteria.equals(criteria)  )
            {
                collDepends = DependPeer.doSelectJoinIssueRelatedByObserverId(criteria);
            }
        }
        lastDependsCriteria = criteria; 

        return collDepends;
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
            fieldNames_.add("DependTypeId");
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
            if (name.equals("DependTypeId"))
	{
	  	    return getDependTypeId();
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
            if (name == DependTypePeer.DEPEND_TYPE_ID )
	    {
	  	    return getDependTypeId();
	  	}
            if (name == DependTypePeer.DEPEND_TYPE_NAME )
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
	  	    return getDependTypeId();
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
                DependTypePeer.getMapBuilder()
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
                DependTypePeer.doInsert((DependType)this, dbCon);
            }
            else
            {
                DependTypePeer.doUpdate((DependType)this, dbCon);
                setNew(false);
            }
        }

                                    
                
          if (collDepends != null )
          {
              for (int i=0; i<collDepends.size(); i++)
              {
                  ((Depend)collDepends.get(i)).save(dbCon);
              }
          }
                  alreadyInSave = false;
      }
      }

                                        
    /** 
     * Set the Id using pk values.
     *
     * @param SimpleKey depend_type_id
     */
    public void setPrimaryKey(
     SimpleKey depend_type_id
                ) throws Exception
    {
         setDependTypeId(depend_type_id);
    }
    



    /** 
     * returns an id that differentiates this object from others
     * of its class.
     */
    public ObjectKey getPrimaryKey() 
    {
        return getDependTypeId();
    }

    /**
     * Makes a copy of this object.  
     * It creates a new object filling in the simple attributes.
     * It then fills all the association collections and sets the
     * related objects to isNew=true.
     */
    public DependType copy() throws Exception
    {
        DependType copyObj = new DependType();
        copyObj.setDependTypeId(depend_type_id);
        copyObj.setName(depend_type_name);

                                
                
         List v = copyObj.getDepends();
         for (int i=0; i<v.size(); i++)
         {
             ((BaseObject)v.get(i)).setNew(true);
         }
            
            
        copyObj.setDependTypeId(null);
            return copyObj;
    }             
}

