package org.tigris.scarab.om;

// JDK classes
import java.util.*;

// Turbine classes
import org.apache.turbine.om.*;
import org.apache.turbine.om.peer.BasePeer;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.StringUtils;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.db.pool.DBConnection;

/** 
  * The skeleton for this class was autogenerated by Torque on:
  *
  * [Tue Apr 17 12:40:34 PDT 2001]
  *
  * You should add additional methods to this class to meet the
  * application requirements.  This class will only be generated as
  * long as it does not already exist in the output directory.

  */
public class OptionRelationship 
    extends org.tigris.scarab.om.BaseOptionRelationship
    implements Persistent
{
    public static final NumberKey PARENT_CHILD = new NumberKey("1");
}



