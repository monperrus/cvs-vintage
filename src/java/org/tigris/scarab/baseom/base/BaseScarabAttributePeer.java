package org.tigris.scarab.baseom.base;

// JDK classes
import java.util.*;
import java.math.*;

// Village classes
import com.workingdogs.village.*;

// Turbine classes
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
  * [Thu Feb 15 16:11:32 PST 2001]
  *
  */
public abstract class BaseScarabAttributePeer extends BasePeer
{
    /** the mapbuilder for this class */
    private static final ScarabAttributeMapBuilder mapBuilder = 
        (ScarabAttributeMapBuilder)getMapBuilder(ScarabAttributeMapBuilder.CLASS_NAME);

    /** the table name for this class */
    public static final String TABLE_NAME = mapBuilder.getTable();

    /** the column name for the ATTRIBUTE_ID field */
    public static final String ATTRIBUTE_ID = mapBuilder.getScarabAttribute_AttributeId();
    /** the column name for the ATTRIBUTE_NAME field */
    public static final String ATTRIBUTE_NAME = mapBuilder.getScarabAttribute_Name();
    /** the column name for the ATTRIBUTE_TYPE_ID field */
    public static final String ATTRIBUTE_TYPE_ID = mapBuilder.getScarabAttribute_TypeId();
    /** the column name for the DELETED field */
    public static final String DELETED = mapBuilder.getScarabAttribute_Deleted();

    /** number of columns for this peer */
    public static final int numColumns =  4;

    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT = 
        "org.tigris.scarab.baseom.ScarabAttribute";


    /** Method to do inserts */
    public static Object doInsert( Criteria criteria ) throws Exception
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
    public static Object doInsert( Criteria criteria, DBConnection dbCon ) throws Exception
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
            criteria.addSelectColumn( ATTRIBUTE_ID );
            criteria.addSelectColumn( ATTRIBUTE_NAME );
            criteria.addSelectColumn( ATTRIBUTE_TYPE_ID );
            criteria.addSelectColumn( DELETED );
        }


    /** 
     * Create a new object of type cls from a resultset row starting
     * from a specified offset.  This is done so that you can select
     * other rows than just those needed for this object.  You may
     * for example want to create two objects from the same row.
     */
    public static ScarabAttribute row2Object (Record row, 
                                              int offset, 
                                              String cls ) 
        throws Exception
    {
        ScarabAttribute obj = 
            (ScarabAttribute)Class.forName(cls).newInstance();
                                            obj.setAttributeId(row.getValue(offset+0).asInt());
                                                    obj.setName(row.getValue(offset+1).asString());
                                                    obj.setTypeId(row.getValue(offset+2).asInt());
                                                    obj.setDeleted
                (1 == row.getValue(offset+3).asInt());
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
            results.add(row2Object( row,1, CLASSNAME_DEFAULT ));
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
                                selectCriteria.put( ATTRIBUTE_ID, criteria.remove(ATTRIBUTE_ID) );
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
                                selectCriteria.put( ATTRIBUTE_ID, criteria.remove(ATTRIBUTE_ID) );
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
    public static void doInsert( ScarabAttribute obj ) throws Exception
    {
                obj.setPrimaryKey(doInsert(buildCriteria(obj)));
                obj.setNew(false);
    }

    /**
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(ScarabAttribute obj) throws Exception
    {
        doUpdate(buildCriteria(obj));
    }
    /**
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(ScarabAttribute obj) throws Exception
    {
        doDelete(buildCriteria(obj));
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(ScarabAttribute) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to insert into the database.
     */
    public static void doInsert( ScarabAttribute obj, DBConnection dbCon) throws Exception
    {
                obj.setPrimaryKey(doInsert(buildCriteria(obj), dbCon));
                obj.setNew(false);
    }

    /**
     * Method to do update.  This method is to be used during a transaction,
     * otherwise use the doUpdate(ScarabAttribute) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(ScarabAttribute obj, DBConnection dbCon) throws Exception
    {
        doUpdate(buildCriteria(obj), dbCon);
    }
    /**
     * Method to delete.  This method is to be used during a transaction,
     * otherwise use the doDelete(ScarabAttribute) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(ScarabAttribute obj, DBConnection dbCon) throws Exception
    {
        doDelete(buildCriteria(obj), dbCon);
    }

    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria( ScarabAttribute obj )
    {
        Criteria criteria = new Criteria();
                            if ( !obj.isNew() )
    	                criteria.add( ATTRIBUTE_ID, obj.getAttributeId() );
                                criteria.add( ATTRIBUTE_NAME, obj.getName() );
                                criteria.add( ATTRIBUTE_TYPE_ID, obj.getTypeId() );
                                criteria.add( DELETED, obj.getDeleted() );
                return criteria;
    }

    /** 
     * Retrieve a single object by pk where multiple PK's are separated
     * by colons
     *
     * @param int attribute_id
     */
    public static ScarabAttribute retrieveById(Object pkid) 
        throws Exception
    {
        StringTokenizer stok = new StringTokenizer((String)pkid, ":");
        if ( stok.countTokens() < 1 )
        {   
            throw new TurbineException(
                "id tokens did not match number of primary keys" );
        }
           int attribute_id = Integer.parseInt(stok.nextToken());;

       return retrieveByPK(
             attribute_id
              );
    }

    /** 
     * Retrieve a single object by pk
     *
     * @param int attribute_id
     */
    public static ScarabAttribute retrieveByPK(
                      int attribute_id
                                                                 ) throws Exception
    {
        Criteria criteria = new Criteria();
                       if( attribute_id > 0 )
                  criteria.add( ScarabAttributePeer.ATTRIBUTE_ID, attribute_id );
                                            Vector ScarabAttributeVector = doSelect(criteria);
        if (ScarabAttributeVector.size() != 1)
        {
            throw new Exception("Failed to select one and only one row.");
        }
        else
        {
            return (ScarabAttribute) ScarabAttributeVector.firstElement();
        }
    }


     
        
                       
     
          


   /**
    * selects a collection of ScarabAttribute objects pre-filled with their
    * ScarabAttributeType objects.
    */
    public static Vector doSelectJoinScarabAttributeType(Criteria c)
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset = numColumns + 1;
        ScarabAttributeTypePeer.addSelectColumns(c);

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

            ScarabAttribute obj1 = row2Object( row,1, CLASSNAME_DEFAULT );


            ScarabAttributeType obj2 = ScarabAttributeTypePeer
                .row2Object(row, offset, 
                    ScarabAttributeTypePeer.CLASSNAME_DEFAULT);
            
            boolean newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabAttribute temp_obj1 = (ScarabAttribute)results.elementAt(j);
                ScarabAttributeType temp_obj2 = temp_obj1.getScarabAttributeType();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabAttributes(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabAttributes();
                obj2.addScarabAttributes(obj1);
            }
            results.add(obj1);

        }

        return results;
    }
    

  




}








