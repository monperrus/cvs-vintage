package org.tigris.scarab.baseom.map;

// JDK classes
import java.util.*;
import java.math.*;

// Turbine classes
import org.apache.turbine.services.db.PoolBrokerService;
import org.apache.turbine.services.db.TurbineDB;
import org.apache.turbine.util.db.map.MapBuilder;
import org.apache.turbine.util.db.map.DatabaseMap;
import org.apache.turbine.util.db.map.TableMap;

/** This class was autogenerated by GenerateMapBuilder on: Wed Feb 07 17:08:09 PST 2001 */
public class ScarabAttachmentTypeMapBuilder implements MapBuilder
{
    /** the name of this class */
    public static final String CLASS_NAME = "org.tigris.scarab.baseom.map.ScarabAttachmentTypeMapBuilder";

    /** item */
    public static String getTable( )
    {
        return "SCARAB_ATTACHMENT_TYPE";
    }


    /** SCARAB_ATTACHMENT_TYPE.ATTACHMENT_TYPE_ID */
    public static String getScarabAttachmentType_AttachmentTypeId()
    {
        return getTable() + ".ATTACHMENT_TYPE_ID";
    }

    /** SCARAB_ATTACHMENT_TYPE.ATTACHMENT_TYPE_NAME */
    public static String getScarabAttachmentType_Name()
    {
        return getTable() + ".ATTACHMENT_TYPE_NAME";
    }


    /**  the database map  */
    private DatabaseMap dbMap = null;

    /**
        tells us if this DatabaseMapBuilder is built so that we don't have
        to re-build it every time
    */
    public boolean isBuilt()
    {
        if ( dbMap != null )
            return true;
        return false;
    }

    /**  gets the databasemap this map builder built.  */
    public DatabaseMap getDatabaseMap()
    {
        return this.dbMap;
    }
    /** the doBuild() method builds the DatabaseMap */
    public void doBuild ( ) throws Exception
    {
        dbMap = TurbineDB.getDatabaseMap("defauult");

        dbMap.addTable(getTable());
        TableMap tMap = dbMap.getTable(getTable());

        tMap.setPrimaryKeyMethod(TableMap.IDBROKERTABLE);



                  tMap.addPrimaryKey ( getScarabAttachmentType_AttachmentTypeId(), new Integer(0) );
          
                  tMap.addColumn ( getScarabAttachmentType_Name(), new String() );
          
    }

}
