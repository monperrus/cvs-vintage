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
public abstract class BaseScarabDependPeer extends BasePeer
{
    /** the mapbuilder for this class */
    private static final ScarabDependMapBuilder mapBuilder = 
        (ScarabDependMapBuilder)getMapBuilder(ScarabDependMapBuilder.CLASS_NAME);

    /** the table name for this class */
    public static final String TABLE_NAME = mapBuilder.getTable();

    /** the column name for the OBSERVED_ID field */
    public static final String OBSERVED_ID = mapBuilder.getScarabDepend_ObservedId();
    /** the column name for the OBSERVER_ID field */
    public static final String OBSERVER_ID = mapBuilder.getScarabDepend_ObserverId();
    /** the column name for the DEPEND_TYPE_ID field */
    public static final String DEPEND_TYPE_ID = mapBuilder.getScarabDepend_TypeId();
    /** the column name for the DELETED field */
    public static final String DELETED = mapBuilder.getScarabDepend_Deleted();

    /** number of columns for this peer */
    public static final int numColumns =  4;

    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT = 
        "org.tigris.scarab.baseom.ScarabDepend";


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
            criteria.addSelectColumn( OBSERVED_ID );
            criteria.addSelectColumn( OBSERVER_ID );
            criteria.addSelectColumn( DEPEND_TYPE_ID );
            criteria.addSelectColumn( DELETED );
        }


    /** 
     * Create a new object of type cls from a resultset row starting
     * from a specified offset.  This is done so that you can select
     * other rows than just those needed for this object.  You may
     * for example want to create two objects from the same row.
     */
    public static ScarabDepend row2Object (Record row, 
                                              int offset, 
                                              String cls ) 
        throws Exception
    {
        ScarabDepend obj = 
            (ScarabDepend)Class.forName(cls).newInstance();
                                            obj.setObservedId(row.getValue(offset+0).asInt());
                                                    obj.setObserverId(row.getValue(offset+1).asInt());
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
                                selectCriteria.put( OBSERVED_ID, criteria.remove(OBSERVED_ID) );
                                         selectCriteria.put( OBSERVER_ID, criteria.remove(OBSERVER_ID) );
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
                                selectCriteria.put( OBSERVED_ID, criteria.remove(OBSERVED_ID) );
                                         selectCriteria.put( OBSERVER_ID, criteria.remove(OBSERVER_ID) );
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
    public static void doInsert( ScarabDepend obj ) throws Exception
    {
                doInsert(buildCriteria(obj));
                obj.setNew(false);
    }

    /**
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(ScarabDepend obj) throws Exception
    {
        doUpdate(buildCriteria(obj));
    }
    /**
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(ScarabDepend obj) throws Exception
    {
        doDelete(buildCriteria(obj));
    }

    /** 
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(ScarabDepend) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to insert into the database.
     */
    public static void doInsert( ScarabDepend obj, DBConnection dbCon) throws Exception
    {
                doInsert(buildCriteria(obj), dbCon);
                obj.setNew(false);
    }

    /**
     * Method to do update.  This method is to be used during a transaction,
     * otherwise use the doUpdate(ScarabDepend) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to update in the database.
     */
    public static void doUpdate(ScarabDepend obj, DBConnection dbCon) throws Exception
    {
        doUpdate(buildCriteria(obj), dbCon);
    }
    /**
     * Method to delete.  This method is to be used during a transaction,
     * otherwise use the doDelete(ScarabDepend) method.  It will take 
     * care of the connection details internally. 
     *
     * @param obj the data object to delete in the database.
     */
    public static void doDelete(ScarabDepend obj, DBConnection dbCon) throws Exception
    {
        doDelete(buildCriteria(obj), dbCon);
    }

    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria( ScarabDepend obj )
    {
        Criteria criteria = new Criteria();
                                criteria.add( OBSERVED_ID, obj.getObservedId() );
                                criteria.add( OBSERVER_ID, obj.getObserverId() );
                                criteria.add( DEPEND_TYPE_ID, obj.getTypeId() );
                                criteria.add( DELETED, obj.getDeleted() );
                return criteria;
    }

    /** 
     * Retrieve a single object by pk where multiple PK's are separated
     * by colons
     *
     * @param int observed_id
     * @param int observer_id
     */
    public static ScarabDepend retrieveById(Object pkid) 
        throws Exception
    {
        StringTokenizer stok = new StringTokenizer((String)pkid, ":");
        if ( stok.countTokens() < 2 )
        {   
            throw new TurbineException(
                "id tokens did not match number of primary keys" );
        }
           int observed_id = Integer.parseInt(stok.nextToken());;
           int observer_id = Integer.parseInt(stok.nextToken());;

       return retrieveByPK(
             observed_id
              , observer_id
              );
    }

    /** 
     * Retrieve a single object by pk
     *
     * @param int observed_id
     * @param int observer_id
     */
    public static ScarabDepend retrieveByPK(
                      int observed_id
                                      , int observer_id
                                                     ) throws Exception
    {
        Criteria criteria = new Criteria();
                       if( observed_id > 0 )
                  criteria.add( ScarabDependPeer.OBSERVED_ID, observed_id );
                            if( observer_id > 0 )
                  criteria.add( ScarabDependPeer.OBSERVER_ID, observer_id );
                                   Vector ScarabDependVector = doSelect(criteria);
        if (ScarabDependVector.size() != 1)
        {
            throw new Exception("Failed to select one and only one row.");
        }
        else
        {
            return (ScarabDepend) ScarabDependVector.firstElement();
        }
    }


       
        
                               
     
          


   /**
    * selects a collection of ScarabDepend objects pre-filled with their
    * ScarabIssue objects.
    */
    public static Vector doSelectJoinScarabIssueRelatedByObservedId(Criteria c)
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset = numColumns + 1;
        ScarabIssuePeer.addSelectColumns(c);

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

            ScarabDepend obj1 = row2Object( row,1, CLASSNAME_DEFAULT );


            ScarabIssue obj2 = ScarabIssuePeer
                .row2Object(row, offset, 
                    ScarabIssuePeer.CLASSNAME_DEFAULT);
            
            boolean newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabIssue temp_obj2 = temp_obj1.getScarabIssueRelatedByObservedId();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabDependsRelatedByObservedId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabDependsRelatedByObservedId();
                obj2.addScarabDependsRelatedByObservedId(obj1);
            }
            results.add(obj1);

        }

        return results;
    }
         
                               
     
          


   /**
    * selects a collection of ScarabDepend objects pre-filled with their
    * ScarabIssue objects.
    */
    public static Vector doSelectJoinScarabIssueRelatedByObserverId(Criteria c)
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset = numColumns + 1;
        ScarabIssuePeer.addSelectColumns(c);

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

            ScarabDepend obj1 = row2Object( row,1, CLASSNAME_DEFAULT );


            ScarabIssue obj2 = ScarabIssuePeer
                .row2Object(row, offset, 
                    ScarabIssuePeer.CLASSNAME_DEFAULT);
            
            boolean newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabIssue temp_obj2 = temp_obj1.getScarabIssueRelatedByObserverId();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabDependsRelatedByObserverId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabDependsRelatedByObserverId();
                obj2.addScarabDependsRelatedByObserverId(obj1);
            }
            results.add(obj1);

        }

        return results;
    }
         
                       
     
          


   /**
    * selects a collection of ScarabDepend objects pre-filled with their
    * ScarabDependType objects.
    */
    public static Vector doSelectJoinScarabDependType(Criteria c)
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset = numColumns + 1;
        ScarabDependTypePeer.addSelectColumns(c);

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

            ScarabDepend obj1 = row2Object( row,1, CLASSNAME_DEFAULT );


            ScarabDependType obj2 = ScarabDependTypePeer
                .row2Object(row, offset, 
                    ScarabDependTypePeer.CLASSNAME_DEFAULT);
            
            boolean newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabDependType temp_obj2 = temp_obj1.getScarabDependType();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabDepends(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabDepends();
                obj2.addScarabDepends(obj1);
            }
            results.add(obj1);

        }

        return results;
    }
    

  
  
     
                               
          


   /**
    * selects a collection of ScarabDepend objects pre-filled with 
    * all related objects.
    */
    public static Vector doSelectJoinAllExceptScarabIssueRelatedByObservedId(Criteria c) 
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset2 = numColumns + 1;
                    
                                      
                                      
                ScarabDependTypePeer.addSelectColumns(c);
        int offset3 = offset2 + ScarabDependTypePeer.numColumns;
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

                ScarabDepend obj1 = row2Object( row,1, CLASSNAME_DEFAULT );
  
                    
                                  
                                  
                                                
                        
            

            ScarabDependType obj2 = ScarabDependTypePeer
                .row2Object(row, offset2, 
                    ScarabDependTypePeer.CLASSNAME_DEFAULT);
            
             boolean  newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabDependType temp_obj2 = temp_obj1.getScarabDependType();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabDepends(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabDepends();
                obj2.addScarabDepends(obj1);
            }
                        results.add(obj1);

        }

        return results;
    }
 
     
                               
          


   /**
    * selects a collection of ScarabDepend objects pre-filled with 
    * all related objects.
    */
    public static Vector doSelectJoinAllExceptScarabIssueRelatedByObserverId(Criteria c) 
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset2 = numColumns + 1;
                    
                                      
                                      
                ScarabDependTypePeer.addSelectColumns(c);
        int offset3 = offset2 + ScarabDependTypePeer.numColumns;
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

                ScarabDepend obj1 = row2Object( row,1, CLASSNAME_DEFAULT );
  
                    
                                  
                                  
                                                
                        
            

            ScarabDependType obj2 = ScarabDependTypePeer
                .row2Object(row, offset2, 
                    ScarabDependTypePeer.CLASSNAME_DEFAULT);
            
             boolean  newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabDependType temp_obj2 = temp_obj1.getScarabDependType();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabDepends(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabDepends();
                obj2.addScarabDepends(obj1);
            }
                        results.add(obj1);

        }

        return results;
    }
 
     
                       
          


   /**
    * selects a collection of ScarabDepend objects pre-filled with 
    * all related objects.
    */
    public static Vector doSelectJoinAllExceptScarabDependType(Criteria c) 
        throws Exception
    {
         c.setDbName(mapBuilder.getDatabaseMap().getName());

        addSelectColumns(c);
        int offset2 = numColumns + 1;
                    
                ScarabIssuePeer.addSelectColumns(c);
        int offset3 = offset2 + ScarabIssuePeer.numColumns;
                                          
                ScarabIssuePeer.addSelectColumns(c);
        int offset4 = offset3 + ScarabIssuePeer.numColumns;
                                          
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

                ScarabDepend obj1 = row2Object( row,1, CLASSNAME_DEFAULT );
  
                    
                                                                
                        
            

            ScarabIssue obj2 = ScarabIssuePeer
                .row2Object(row, offset2, 
                    ScarabIssuePeer.CLASSNAME_DEFAULT);
            
             boolean  newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabIssue temp_obj2 = temp_obj1.getScarabIssueRelatedByObservedId();
                if ( temp_obj2.getPrimaryKey().equals(obj2.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj2.addScarabDependsRelatedByObservedId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj2.initScarabDependsRelatedByObservedId();
                obj2.addScarabDependsRelatedByObservedId(obj1);
            }
                        
                                                                
                        
            

            ScarabIssue obj3 = ScarabIssuePeer
                .row2Object(row, offset3, 
                    ScarabIssuePeer.CLASSNAME_DEFAULT);
            
             newObject = true;
            for (int j=0; j<results.size(); j++)
            {
                ScarabDepend temp_obj1 = (ScarabDepend)results.elementAt(j);
                ScarabIssue temp_obj3 = temp_obj1.getScarabIssueRelatedByObserverId();
                if ( temp_obj3.getPrimaryKey().equals(obj3.getPrimaryKey() ) )
                {
                    newObject = false;
                    temp_obj3.addScarabDependsRelatedByObserverId(obj1);
                    break;
                }
            }
            if (newObject)
            {
                obj3.initScarabDependsRelatedByObserverId();
                obj3.addScarabDependsRelatedByObserverId(obj1);
            }
                        
                                  results.add(obj1);

        }

        return results;
    }
  




}








