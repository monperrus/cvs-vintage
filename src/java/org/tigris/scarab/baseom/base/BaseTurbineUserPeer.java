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
public abstract class BaseTurbineUserPeer extends BasePeer
{
    /** the mapbuilder for this class */
    private static final TurbineUserMapBuilder mapBuilder = 
        (TurbineUserMapBuilder)getMapBuilder(TurbineUserMapBuilder.CLASS_NAME);

    /** the table name for this class */
    public static final String TABLE_NAME = mapBuilder.getTable();

    /** the column name for the USER_ID field */
    public static final String USER_ID = mapBuilder.getTurbineUser_UserId();

    /** number of columns for this peer */
    public static final int numColumns =  1;

    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT = 
        "org.tigris.scarab.baseom.TurbineUser";

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
                           return BasePeer.doInsert( criteria, dbCon );
    }

    /** Add all the columns needed to create a new object */
    public static void addSelectColumns (Criteria criteria) throws Exception
    {
            criteria.addSelectColumn( USER_ID );
        }


    /** 
     * Create a new object of type cls from a resultset row starting
     * from a specified offset.  This is done so that you can select
     * other rows than just those needed for this object.  You may
     * for example want to create two objects from the same row.
     */
    public static TurbineUser row2Object (Record row, 
                                              int offset, 
                                              Class cls ) 
        throws Exception
    {
        TurbineUser obj = (TurbineUser)cls.newInstance();
                                                        obj.setUserId(
                new SimpleKey( row.getValue(offset+0).asString() ));
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
                                selectCriteria.put( USER_ID, criteria.remove(USER_ID) );
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
                                selectCriteria.put( USER_ID, criteria.remove(USER_ID) );
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
                            BasePeer.doDelete ( criteria, dbCon );
     }

    /** Method to do inserts */
    public static void doInsert( TurbineUser obj ) throws Exception
    {
                doInsert(buildCriteria(obj));
                obj.setNew(false);
    }

    /**
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(TurbineUser obj) throws Exception
    {
        doUpdate(buildCriteria(obj));
    }
    /**
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(TurbineUser obj) throws Exception
    {
        doDelete(buildCriteria(obj));
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(TurbineUser) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to insert into the database.
     */
    public static void doInsert( TurbineUser obj, DBConnection dbCon) throws Exception
    {
                doInsert(buildCriteria(obj), dbCon);
                obj.setNew(false);
    }

    /**
     * Method to do update.  This method is to be used during a transaction,
     * otherwise use the doUpdate(TurbineUser) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(TurbineUser obj, DBConnection dbCon) throws Exception
    {
        doUpdate(buildCriteria(obj), dbCon);
    }
    /**
     * Method to delete.  This method is to be used during a transaction,
     * otherwise use the doDelete(TurbineUser) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(TurbineUser obj, DBConnection dbCon) throws Exception
    {
        doDelete(buildCriteria(obj), dbCon);
    }

    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria( TurbineUser obj )
    {
        Criteria criteria = new Criteria();
                                criteria.add( USER_ID, obj.getUserId() );
                return criteria;
    }

    /** 
     * Retrieve a single object by pk
     *
     * @param ObjectKey pk
     */
    public static TurbineUser retrieveByPK( ObjectKey pk )
        throws Exception
    {
        Criteria criteria = new Criteria();
            criteria.add( USER_ID, pk );
        Vector v = doSelect(criteria);
        if ( v.size() != 1)
        {
            throw new Exception("Failed to select one and only one row.");
        }
        else
        {
            return (TurbineUser)v.firstElement();
        }
    }





    
 

  




}

