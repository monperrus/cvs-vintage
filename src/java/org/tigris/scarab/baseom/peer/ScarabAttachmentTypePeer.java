package org.tigris.scarab.baseom.peer;

// JDK classes
import java.util.*;

// Village classes
import com.workingdogs.village.*;

// Turbine classes
import org.apache.turbine.om.peer.*;
import org.apache.turbine.util.*;
import org.apache.turbine.util.db.*;
import org.apache.turbine.util.db.map.*;
import org.apache.turbine.util.db.pool.DBConnection;

import org.tigris.scarab.baseom.ScarabAttachmentType;

// Local classes
import org.tigris.scarab.baseom.map.*;
import org.tigris.scarab.baseom.*;

/** This class was autogenerated by GenerateMapBuilder on: Fri Dec 15 13:47:21 PST 2000 */
public class ScarabAttachmentTypePeer extends BasePeer
{
    /** the mapbuilder for this class */
    private static final ScarabAttachmentTypeMapBuilder mapBuilder = 
        (ScarabAttachmentTypeMapBuilder)getMapBuilder(ScarabAttachmentTypeMapBuilder.CLASS_NAME);

    /** the table name for this class */
    public static final String TABLE_NAME = mapBuilder.getTable();

    /** the column name for the ATTACHMENT_TYPE_ID field */
    public static final String ATTACHMENT_TYPE_ID = mapBuilder.getScarabAttachmentType_AttachmentTypeId();
    /** the column name for the ATTACHMENT_TYPE_NAME field */
    public static final String ATTACHMENT_TYPE_NAME = mapBuilder.getScarabAttachmentType_Name();

    /** number of columns for this peer */
    public static final int numColumns =  2;;

    /** Method to do inserts */
    public static Object doInsert( Criteria criteria ) throws Exception
    {
        criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
        return BasePeer.doInsert( criteria );
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(Criteria) method.  It will take care of 
     * the connection details internally. 
     */
    public static Object doInsert( Criteria criteria, DBConnection dbCon ) throws Exception
    {
        criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
        return BasePeer.doInsert( criteria, dbCon );
    }

    /** Add all the columns needed to create a new object */
    public static void addSelectColumns (Criteria criteria) throws Exception
    {
            criteria.addSelectColumn( ATTACHMENT_TYPE_ID );
            criteria.addSelectColumn( ATTACHMENT_TYPE_NAME );
        }


    /** Create a new object of type cls from a resultset row starting
      * from a specified offset.  This is done so that you can select
      * other rows than just those needed for this object.  You may
      * for example want to create two objects from the same row.
      */
    public static ScarabAttachmentType row2Object (Record row, int offset, Class cls ) throws Exception
    {
        ScarabAttachmentType obj = (ScarabAttachmentType)cls.newInstance();
                            obj.setAttachmentTypeId (row.getValue(offset+0).asInt());
                                obj.setName (row.getValue(offset+1).asString());
                                        obj.setModified(false);
            obj.setNew(false);
                return obj;
    }

    /** Method to do selects */
    public static Vector doSelect( Criteria criteria ) throws Exception
    {
        criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
        return doSelect (criteria,"org.tigris.scarab.baseom.ScarabAttachmentType", null);
    }

    /** 
     * Method to do selects.  This method is to be used during a transaction,
     * otherwise use the doSelect(Criteria) method.  It will take care of 
     * the connection details internally. 
     */
    public static Vector doSelect( Criteria criteria, DBConnection dbCon ) throws Exception
    {
        criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
        return doSelect (criteria,"org.tigris.scarab.baseom.ScarabAttachmentType", dbCon);
    }

    /** Method to do selects. The returned vector will have object
      * of className
      */
    public static Vector doSelect( Criteria criteria, String className, DBConnection dbCon) throws Exception
    {
        addSelectColumns ( criteria );

        // BasePeer returns a Vector of Value (Village) arrays.  The array
        // order follows the order columns were placed in the Select clause.
        Vector rows = null;
        if (dbCon == null)
        {
            rows = BasePeer.doSelect(criteria);
        }
        else
        {
            rows = BasePeer.doSelect(criteria, dbCon);
        }
        Vector results = new Vector();

        // populate the object(s)
        for ( int i=0; i<rows.size(); i++ )
        {
            Record row = (Record)rows.elementAt(i);
            results.add (row2Object (row,1,Class.forName (className)));
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
         criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
         Criteria selectCriteria = new Criteria(2);
                       selectCriteria.put( ATTACHMENT_TYPE_ID, criteria.remove(ATTACHMENT_TYPE_ID) );
                                     BasePeer.doUpdate( selectCriteria, criteria );
     }
    /**
    /** 
     * Method to do updates.  This method is to be used during a transaction,
     * otherwise use the doUpdate(Criteria) method.  It will take care of 
     * the connection details internally. 
     *
     * @param Criteria object containing data that is used to create the UPDATE statement.
     */
     public static void doUpdate(Criteria criteria, DBConnection dbCon) throws Exception
     {
         criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
         Criteria selectCriteria = new Criteria(2);
                       selectCriteria.put( ATTACHMENT_TYPE_ID, criteria.remove(ATTACHMENT_TYPE_ID) );
                                     BasePeer.doUpdate( selectCriteria, criteria, dbCon );
     }

    /** 
     * Method to do deletes.
     *
     * @param Criteria object containing data that is used DELETE from database.
     */
     public static void doDelete(Criteria criteria) throws Exception
     {
         criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
         BasePeer.doDelete ( criteria );
     }

    /** 
     * Method to do deletes.  This method is to be used during a transaction,
     * otherwise use the doInsert(Criteria) method.  It will take care of 
     * the connection details internally. 
     *
     * @param Criteria object containing data that is used DELETE from database.
     */
     public static void doDelete(Criteria criteria, DBConnection dbCon) throws Exception
     {
         criteria.setDbName(getMapBuilder().getDatabaseMap().getName());
         BasePeer.doDelete ( criteria, dbCon );
     }

    /** Method to do inserts */
    public static void doInsert( ScarabAttachmentType obj ) throws Exception
    {
        obj.setId(doInsert(buildCriteria(obj)));
    }

    /**
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(ScarabAttachmentType obj) throws Exception
    {
        doUpdate(buildCriteria(obj));
    }
    /**
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(ScarabAttachmentType obj) throws Exception
    {
        doDelete(buildCriteria(obj));
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(ScarabAttachmentType) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to insert into the database.
     */
    public static void doInsert( ScarabAttachmentType obj, DBConnection dbCon) throws Exception
    {
        obj.setId(doInsert(buildCriteria(obj), dbCon));
    }

    /**
     * Method to do update.  This method is to be used during a transaction,
     * otherwise use the doUpdate(ScarabAttachmentType) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(ScarabAttachmentType obj, DBConnection dbCon) throws Exception
    {
        doUpdate(buildCriteria(obj), dbCon);
    }
    /**
     * Method to delete.  This method is to be used during a transaction,
     * otherwise use the doDelete(ScarabAttachmentType) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(ScarabAttachmentType obj, DBConnection dbCon) throws Exception
    {
        doDelete(buildCriteria(obj), dbCon);
    }

    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria( ScarabAttachmentType obj )
    {
        Criteria criteria = new Criteria();
                        if( obj.getAttachmentTypeId() > 0 )
            criteria.add( ScarabAttachmentTypePeer.ATTACHMENT_TYPE_ID, obj.getAttachmentTypeId() );
                                criteria.add( ATTACHMENT_TYPE_NAME, obj.getName() );
                        return criteria;
    }

    /** 
     * Retrieve a single object by pk
     *
     * @param int attachment_type_id
     */
    public static ScarabAttachmentType retrieveByPK(
                      int attachment_type_id
                                         ) throws Exception
    {
        Criteria criteria = new Criteria();
                       if( attachment_type_id > 0 )
                  criteria.add( ScarabAttachmentTypePeer.ATTACHMENT_TYPE_ID, attachment_type_id );
                          Vector ScarabAttachmentTypeVector = doSelect(criteria);
        if (ScarabAttachmentTypeVector.size() != 1)
        {
            throw new Exception("Failed to select one and only one row.");
        }
        else
        {
            return (ScarabAttachmentType) ScarabAttachmentTypeVector.firstElement();
        }
    }

    
}





