package org.tigris.scarab.baseom.base;

// JDK classes
import java.util.*;
import java.math.*;

// Village classes
import com.workingdogs.village.*;

// Turbine classes
import org.apache.turbine.om.*;
import org.apache.turbine.om.peer.*;
import org.apache.turbine.util.*;
import org.apache.turbine.util.db.*;
import org.apache.turbine.util.db.map.*;
import org.apache.turbine.util.db.pool.DBConnection;


// Local classes
import org.tigris.scarab.baseom.map.*;
import org.tigris.scarab.baseom.peer.*;
import org.tigris.scarab.baseom.*;


/**
  * This class was autogenerated by Torque on:
  *
  * [Thu Feb 22 18:38:08 PST 2001]
  *
  */
public abstract class BaseModulePeer extends BasePeer
{
    /** the mapbuilder for this class */
    private static final ModuleMapBuilder mapBuilder = 
        (ModuleMapBuilder)getMapBuilder(ModuleMapBuilder.CLASS_NAME);

    /** the table name for this class */
    public static final String TABLE_NAME = mapBuilder.getTable();

    /** the column name for the MODULE_ID field */
    public static final String MODULE_ID = mapBuilder.getModule_ModuleId();
    /** the column name for the MODULE_NAME field */
    public static final String MODULE_NAME = mapBuilder.getModule_Name();
    /** the column name for the MODULE_DESCRIPTION field */
    public static final String MODULE_DESCRIPTION = mapBuilder.getModule_Description();
    /** the column name for the MODULE_URL field */
    public static final String MODULE_URL = mapBuilder.getModule_Url();
    /** the column name for the PARENT_ID field */
    public static final String PARENT_ID = mapBuilder.getModule_ParentId();
    /** the column name for the OWNER_ID field */
    public static final String OWNER_ID = mapBuilder.getModule_OwnerId();
    /** the column name for the QA_CONTACT_ID field */
    public static final String QA_CONTACT_ID = mapBuilder.getModule_QaContactId();
    /** the column name for the DELETED field */
    public static final String DELETED = mapBuilder.getModule_Deleted();

    /** number of columns for this peer */
    public static final int numColumns =  8;

    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT = 
        "org.tigris.scarab.baseom.Module";

    /** A class that can be returned by this peer. */
    protected static final Class CLASS_DEFAULT = initClass();

    /** Initialization method for static CLASS_DEFAULT attribute */
    private static Class initClass()
    {
        Class c = null;
        try
        { 
            c = Class.forName(CLASSNAME_DEFAULT);
        }
        catch (Exception e)
        {
            Log.error("A FATAL ERROR has occurred which should not" +
                "have happened under any circumstance.  Please notify" +
                "Turbine and give as many details as possible including the " +
                "error stacktrace.", e);
        }
        return c;
    }


    /** Method to do inserts */
    public static ObjectKey doInsert( Criteria criteria ) throws Exception
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
                                                                                                                        // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
                      return BasePeer.doInsert( criteria );
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(Criteria) method.  It will take care of 
     * the connection details internally. 
     */
    public static ObjectKey doInsert( Criteria criteria, DBConnection dbCon ) 
        throws Exception
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
                                                                                                                        // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
                      return BasePeer.doInsert( criteria, dbCon );
    }

    /** Add all the columns needed to create a new object */
    public static void addSelectColumns (Criteria criteria) throws Exception
    {
            criteria.addSelectColumn( MODULE_ID );
            criteria.addSelectColumn( MODULE_NAME );
            criteria.addSelectColumn( MODULE_DESCRIPTION );
            criteria.addSelectColumn( MODULE_URL );
            criteria.addSelectColumn( PARENT_ID );
            criteria.addSelectColumn( OWNER_ID );
            criteria.addSelectColumn( QA_CONTACT_ID );
            criteria.addSelectColumn( DELETED );
        }


    /** 
     * Create a new object of type cls from a resultset row starting
     * from a specified offset.  This is done so that you can select
     * other rows than just those needed for this object.  You may
     * for example want to create two objects from the same row.
     */
    public static Module row2Object (Record row, 
                                              int offset, 
                                              Class cls ) 
        throws Exception
    {
        Module obj = (Module)cls.newInstance();
                                                        obj.setModuleId(
                new SimpleKey( row.getValue(offset+0).asString() ));
                                                                            obj.setName(row.getValue(offset+1).asString());
                                                                            obj.setDescription(row.getValue(offset+2).asString());
                                                                            obj.setUrl(row.getValue(offset+3).asString());
                                                                            obj.setParentId(
                new SimpleKey( row.getValue(offset+4).asString() ));
                                                                            obj.setOwnerId(
                new SimpleKey( row.getValue(offset+5).asString() ));
                                                                            obj.setQaContactId(
                new SimpleKey( row.getValue(offset+6).asString() ));
                                                            obj.setDeleted
                (1 == row.getValue(offset+7).asInt());
                                        obj.setModified(false);
                obj.setNew(false);

        return obj;
    }

    /** Method to do selects */
    public static Vector doSelect( Criteria criteria ) throws Exception
    {
        return populateObjects( doSelectVillageRecords(criteria) ); 
    }


    /** Method to do selects within a transaction */
    public static Vector doSelect( Criteria criteria, 
                                   DBConnection dbCon ) 
        throws Exception
    {
        return populateObjects( doSelectVillageRecords(criteria, dbCon) ); 
    }

    /** 
     * Grabs the raw Village records to be formed into objects.
     * This method handles connections internally 
     */
    public static Vector doSelectVillageRecords( Criteria criteria ) 
        throws Exception
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
        if (criteria.getSelectColumns().size() == 0)
        {
            addSelectColumns ( criteria );
        }

                                                                                                                        // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
              
        // BasePeer returns a Vector of Value (Village) arrays.  The array
        // order follows the order columns were placed in the Select clause.
        return BasePeer.doSelect(criteria);
    }


    /** 
     * Grabs the raw Village records to be formed into objects.
     * This method should be used for transactions 
     */
    public static Vector doSelectVillageRecords( Criteria criteria, 
                                                 DBConnection dbCon ) 
        throws Exception
    {
        addSelectColumns ( criteria );

                                                                                                                        // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
              
        // BasePeer returns a Vector of Value (Village) arrays.  The array
        // order follows the order columns were placed in the Select clause.
        return BasePeer.doSelect(criteria, dbCon);
    }

    /** 
     * The returned vector will contain objects of the default type or
     * objects that inherit from the default.
     */
    public static Vector populateObjects(Vector records) 
        throws Exception
    {
        Vector results = new Vector(records.size());

        // populate the object(s)
        for ( int i=0; i<records.size(); i++ )
        {
            Record row = (Record)records.elementAt(i);
            results.add(row2Object( row,1, CLASS_DEFAULT ));
        }
        return results;
    }




    /**
     * Method to do updates. 
     *
     * @param Criteria object containing data that is used to create the UPDATE statement.
     */
    public static void doUpdate(Criteria criteria) throws Exception
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
        Criteria selectCriteria = new
            Criteria(mapBuilder.getDatabaseMap().getName(), 2);
                                selectCriteria.put( MODULE_ID, criteria.remove(MODULE_ID) );
                                                                                                                                                                         // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) ) 
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
                                BasePeer.doUpdate( selectCriteria, criteria );
    }

    /** 
     * Method to do updates.  This method is to be used during a transaction,
     * otherwise use the doUpdate(Criteria) method.  It will take care of 
     * the connection details internally. 
     *
     * @param Criteria object containing data that is used to create the UPDATE statement.
     */
    public static void doUpdate(Criteria criteria, DBConnection dbCon) throws Exception
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
        Criteria selectCriteria = new
            Criteria(mapBuilder.getDatabaseMap().getName(), 2);
                                selectCriteria.put( MODULE_ID, criteria.remove(MODULE_ID) );
                                                                                                                                                                         // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
                                BasePeer.doUpdate( selectCriteria, criteria, dbCon );
     }

    /** 
     * Method to do deletes.
     *
     * @param Criteria object containing data that is used DELETE from database.
     */
     public static void doDelete(Criteria criteria) throws Exception
     {
         criteria.setDbName(mapBuilder.getDatabaseMap().getName());
                                                                                                                        // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
                       BasePeer.doDelete ( criteria );
     }

    /** 
     * Method to do deletes.  This method is to be used during a transaction,
     * otherwise use the doDelete(Criteria) method.  It will take care of 
     * the connection details internally. 
     *
     * @param Criteria object containing data that is used DELETE from database.
     */
     public static void doDelete(Criteria criteria, DBConnection dbCon) throws Exception
     {
         criteria.setDbName(mapBuilder.getDatabaseMap().getName());
                                                                                                                        // check for conversion from boolean to int
        if ( criteria.containsKey(DELETED) )
        {
            Object possibleBoolean = criteria.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    criteria.add(DELETED, 1);
                }
                else
                {   
                    criteria.add(DELETED, 0);
                }
            }                     
         }
                       BasePeer.doDelete ( criteria, dbCon );
     }

    /** Method to do inserts */
    public static void doInsert( Module obj ) throws Exception
    {
                obj.setPrimaryKey(doInsert(buildCriteria(obj)));
                obj.setNew(false);
    }

    /**
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(Module obj) throws Exception
    {
        doUpdate(buildCriteria(obj));
    }
    /**
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(Module obj) throws Exception
    {
        doDelete(buildCriteria(obj));
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(Module) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to insert into the database.
     */
    public static void doInsert( Module obj, DBConnection dbCon) throws Exception
    {
                obj.setPrimaryKey(doInsert(buildCriteria(obj), dbCon));
                obj.setNew(false);
    }

    /**
     * Method to do update.  This method is to be used during a transaction,
     * otherwise use the doUpdate(Module) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(Module obj, DBConnection dbCon) throws Exception
    {
        doUpdate(buildCriteria(obj), dbCon);
    }
    /**
     * Method to delete.  This method is to be used during a transaction,
     * otherwise use the doDelete(Module) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(Module obj, DBConnection dbCon) throws Exception
    {
        doDelete(buildCriteria(obj), dbCon);
    }

    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria( Module obj )
    {
        Criteria criteria = new Criteria();
                            if ( !obj.isNew() )
    	                criteria.add( MODULE_ID, obj.getModuleId() );
                                criteria.add( MODULE_NAME, obj.getName() );
                                criteria.add( MODULE_DESCRIPTION, obj.getDescription() );
                                criteria.add( MODULE_URL, obj.getUrl() );
                                criteria.add( PARENT_ID, obj.getParentId() );
                                criteria.add( OWNER_ID, obj.getOwnerId() );
                                criteria.add( QA_CONTACT_ID, obj.getQaContactId() );
                                criteria.add( DELETED, obj.getDeleted() );
                return criteria;
    }

    /** 
     * Retrieve a single object by pk
     *
     * @param ObjectKey pk
     */
    public static Module retrieveByPK( ObjectKey pk )
        throws Exception
    {
        Criteria criteria = new Criteria();
            criteria.add( MODULE_ID, pk );
        Vector v = doSelect(criteria);
        if ( v.size() != 1)
        {
            throw new Exception("Failed to select one and only one row.");
        }
        else
        {
            return (Module)v.firstElement();
        }
    }





       
               
                               
     
          


   /**
    * selects a collection of Module objects pre-filled with their
    * TurbineUser objects.
    */
    public static Vector doSelectJoinTurbineUserRelatedByOwnerId(Criteria c)
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset = numColumns + 1;
        TurbineUserPeer.addSelectColumns(c);

                                                                                                                        // check for conversion from boolean to int
        if ( c.containsKey(DELETED) )
        {
            Object possibleBoolean = c.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    c.add(DELETED, 1);
                }
                else
                {   
                    c.add(DELETED, 0);
                }
            }                     
         }
                      
        Vector rows = BasePeer.doSelect(c);
        Vector results = new Vector();

        for (int i=0; i<rows.size(); i++)
        {
            Record row = (Record)rows.elementAt(i);

            Module obj1 = row2Object( row,1, CLASS_DEFAULT );


            TurbineUser obj2 = TurbineUserPeer
                .row2Object(row, offset, 
                    TurbineUserPeer.CLASS_DEFAULT);
            
            boolean newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                Module temp_obj1 = (Module)results.elementAt(j);
                TurbineUser temp_obj2 = temp_obj1.getTurbineUserRelatedByOwnerId();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addModulesRelatedByOwnerId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initModulesRelatedByOwnerId();
                obj2.addModulesRelatedByOwnerId(obj1);
            }
            results.add(obj1);

        }

        return results;
    }
         
                               
     
          


   /**
    * selects a collection of Module objects pre-filled with their
    * TurbineUser objects.
    */
    public static Vector doSelectJoinTurbineUserRelatedByQaContactId(Criteria c)
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset = numColumns + 1;
        TurbineUserPeer.addSelectColumns(c);

                                                                                                                        // check for conversion from boolean to int
        if ( c.containsKey(DELETED) )
        {
            Object possibleBoolean = c.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    c.add(DELETED, 1);
                }
                else
                {   
                    c.add(DELETED, 0);
                }
            }                     
         }
                      
        Vector rows = BasePeer.doSelect(c);
        Vector results = new Vector();

        for (int i=0; i<rows.size(); i++)
        {
            Record row = (Record)rows.elementAt(i);

            Module obj1 = row2Object( row,1, CLASS_DEFAULT );


            TurbineUser obj2 = TurbineUserPeer
                .row2Object(row, offset, 
                    TurbineUserPeer.CLASS_DEFAULT);
            
            boolean newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                Module temp_obj1 = (Module)results.elementAt(j);
                TurbineUser temp_obj2 = temp_obj1.getTurbineUserRelatedByQaContactId();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addModulesRelatedByQaContactId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initModulesRelatedByQaContactId();
                obj2.addModulesRelatedByQaContactId(obj1);
            }
            results.add(obj1);

        }

        return results;
    }
    

  
  
     
                       
          


   /**
    * selects a collection of Module objects pre-filled with 
    * all related objects.
    */
    public static Vector doSelectJoinAllExceptModule(Criteria c) 
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset2 = numColumns + 1;
                                    
                TurbineUserPeer.addSelectColumns(c);
        int offset3 = offset2 + TurbineUserPeer.numColumns;
                                          
                TurbineUserPeer.addSelectColumns(c);
        int offset4 = offset3 + TurbineUserPeer.numColumns;
                                                                                                                                                      // check for conversion from boolean to int
        if ( c.containsKey(DELETED) )
        {
            Object possibleBoolean = c.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    c.add(DELETED, 1);
                }
                else
                {   
                    c.add(DELETED, 0);
                }
            }                     
         }
              
        Vector rows = BasePeer.doSelect(c);
        Vector results = new Vector();

        for (int i=0; i<rows.size(); i++)
        {
            Record row = (Record)rows.elementAt(i);


                Module obj1 = row2Object( row,1, CLASS_DEFAULT );
  
                                  
                                                                
                        
            

            TurbineUser obj2 = TurbineUserPeer
                .row2Object(row, offset2, 
                    TurbineUserPeer.CLASS_DEFAULT);
            
             boolean  newObject = true;
            for (int j=0; j<results.size(); j++)    
            {
                Module temp_obj1 = (Module)results.elementAt(j);
                TurbineUser temp_obj2 = temp_obj1.getTurbineUserRelatedByOwnerId();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addModulesRelatedByOwnerId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initModulesRelatedByOwnerId();
                obj2.addModulesRelatedByOwnerId(obj1);
            }
                        
                                                                
                        
            

            TurbineUser obj3 = TurbineUserPeer
                .row2Object(row, offset3, 
                    TurbineUserPeer.CLASS_DEFAULT);
            
             newObject = true;
            for (int j=0; j<results.size(); j++)    
            {
                Module temp_obj1 = (Module)results.elementAt(j);
                TurbineUser temp_obj3 = temp_obj1.getTurbineUserRelatedByQaContactId();
                if ( temp_obj3.getPrimaryKey().equals(obj3.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj3.addModulesRelatedByQaContactId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj3.initModulesRelatedByQaContactId();
                obj3.addModulesRelatedByQaContactId(obj1);
            }
                        results.add(obj1);

        }

        return results;
    }
 
     
                               
          


   /**
    * selects a collection of Module objects pre-filled with 
    * all related objects.
    */
    public static Vector doSelectJoinAllExceptTurbineUserRelatedByOwnerId(Criteria c) 
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset2 = numColumns + 1;
                                    
                                      
                                                                                                                                                  // check for conversion from boolean to int
        if ( c.containsKey(DELETED) )
        {
            Object possibleBoolean = c.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    c.add(DELETED, 1);
                }
                else
                {   
                    c.add(DELETED, 0);
                }
            }                     
         }
              
        Vector rows = BasePeer.doSelect(c);
        Vector results = new Vector();

        for (int i=0; i<rows.size(); i++)
        {
            Record row = (Record)rows.elementAt(i);


                Module obj1 = row2Object( row,1, CLASS_DEFAULT );
  
                                  
                                  
                                  results.add(obj1);

        }

        return results;
    }
 
     
                               
          


   /**
    * selects a collection of Module objects pre-filled with 
    * all related objects.
    */
    public static Vector doSelectJoinAllExceptTurbineUserRelatedByQaContactId(Criteria c) 
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset2 = numColumns + 1;
                                    
                                      
                                                                                                                                                  // check for conversion from boolean to int
        if ( c.containsKey(DELETED) )
        {
            Object possibleBoolean = c.get(DELETED);
            if ( possibleBoolean instanceof Boolean )
            {
                if ( ((Boolean)possibleBoolean).booleanValue() )
                {
                    c.add(DELETED, 1);
                }
                else
                {   
                    c.add(DELETED, 0);
                }
            }                     
         }
              
        Vector rows = BasePeer.doSelect(c);
        Vector results = new Vector();

        for (int i=0; i<rows.size(); i++)
        {
            Record row = (Record)rows.elementAt(i);


                Module obj1 = row2Object( row,1, CLASS_DEFAULT );
  
                                  
                                  
                                  results.add(obj1);

        }

        return results;
    }
  




}

