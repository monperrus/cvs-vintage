package org.tigris.scarab.baseom;

// JDK classes
import java.util.*;
import java.math.*;

// Turbine classes
import org.apache.turbine.om.BaseObject;
import org.apache.turbine.om.peer.BasePeer;
import org.tigris.scarab.baseom.peer.*;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.db.pool.DBConnection;

/** 
 * This class was autogenerated by Torque on: Wed Feb 07 17:08:09 PST 2001
 * You should not use this class directly.  It should not even be
 * extended all references should be to ScarabRModuleAttribute 
 */
public abstract class BaseScarabRModuleAttribute extends BaseObject
{
    /** the value for the attribute_id field */
    private int attribute_id;
    /** the value for the module_id field */
    private int module_id;
    /** the value for the deleted field */
    private boolean deleted;


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
                  if ( aScarabAttribute != null && !aScarabAttribute.isNew())
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
                  if ( aScarabModule != null && !aScarabModule.isNew())
        {
            throw new Exception("Can't set a foreign key directly after an "
                + " association is already made based on saved data.");
        }
  
  

           if (this.module_id != v)
           {
              this.module_id = v;
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
     * Declares an association between this object and a ScarabAttribute object
     *
     * @param ScarabAttribute v
     */
    private ScarabAttribute aScarabAttribute;
    public void setScarabAttribute(ScarabAttribute v) throws Exception
    {
        aScarabAttribute = null;
           setAttributeId(v.getAttributeId());
           aScarabAttribute = v;
    }

                     
    public ScarabAttribute getScarabAttribute() throws Exception
    {
        if ( aScarabAttribute==null && (this.attribute_id>0) )
        {
            aScarabAttribute = ScarabAttributePeer.retrieveByPK(this.attribute_id);
            // The following can be used instead of the line above to
            // guarantee the related object contains a reference
            // to this object, but this level of coupling 
            // may be undesirable in many circumstances.
            // As it can lead to a db query with many results that may
            // never be used.  
            // ScarabAttribute obj = ScarabAttributePeer.retrieveByPK(this.attribute_id);
            // obj.addScarabRModuleAttributes(this);
        }
        return aScarabAttribute;
    }

 
   
             
   
   
    /**
     * Declares an association between this object and a ScarabModule object
     *
     * @param ScarabModule v
     */
    private ScarabModule aScarabModule;
    public void setScarabModule(ScarabModule v) throws Exception
    {
        aScarabModule = null;
           setModuleId(v.getModuleId());
           aScarabModule = v;
    }

                     
    public ScarabModule getScarabModule() throws Exception
    {
        if ( aScarabModule==null && (this.module_id>0) )
        {
            aScarabModule = ScarabModulePeer.retrieveByPK(this.module_id);
            // The following can be used instead of the line above to
            // guarantee the related object contains a reference
            // to this object, but this level of coupling 
            // may be undesirable in many circumstances.
            // As it can lead to a db query with many results that may
            // never be used.  
            // ScarabModule obj = ScarabModulePeer.retrieveByPK(this.module_id);
            // obj.addScarabRModuleAttributes(this);
        }
        return aScarabModule;
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
            fieldNames_.add("AttributeId");
            fieldNames_.add("ModuleId");
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
            if (name.equals("AttributeId"))
	{
	  	    return new Integer(getAttributeId());
	  	}
            if (name.equals("ModuleId"))
	{
	  	    return new Integer(getModuleId());
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
                ScarabRModuleAttributePeer.getMapBuilder()
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
                ScarabRModuleAttributePeer.doInsert((ScarabRModuleAttribute)this, dbCon);
            }
            else
            {
                ScarabRModuleAttributePeer.doUpdate((ScarabRModuleAttribute)this, dbCon);
                setNew(false);
            }
        }

              alreadyInSave = false;
      }
      }

                                            
    /** 
     * Set the Id using pk values.
     *
     * @param int attribute_id
     * @param int module_id
     */
    public void setPrimaryKey(
                      int attribute_id
                                      , int module_id
                                         ) throws Exception
    {
                     setAttributeId(attribute_id);
                             setModuleId(module_id);
                            }

    /** 
     * Set the Id using a : separated String of pk values.
     */
    public void setPrimaryKey(Object id) throws Exception
    {
        StringTokenizer st = new StringTokenizer(id.toString(), ":");
                           setAttributeId( Integer.parseInt(st.nextToken()) );
                                          setModuleId( Integer.parseInt(st.nextToken()) );
                                    }


    /** 
     * returns an id that differentiates this object from others
     * of its class.
     */
    public Object getPrimaryKey() 
    {
        return ""
                      + getAttributeId()
                                      + ":"  + getModuleId()
                                         ;
    } 

    /** 
     * returns an id that can be used to specify this object in
     * a query string.
     */
    public String getQueryOID() 
    {
        return "ScarabRModuleAttribute[" + getPrimaryKey() + "]";
    }

}
