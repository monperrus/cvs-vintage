

package org.tigris.scarab.om;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;
import org.apache.torque.om.ObjectKey;

/** 
 * This class manages RModuleIssueType objects.  
 * The skeleton for this class was autogenerated by Torque  * You should add additional methods to this class to meet the
 * application requirements.  This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public class RModuleIssueTypeManager
    extends BaseRModuleIssueTypeManager
{
    /**
     * Creates a new <code>RModuleIssueTypeManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public RModuleIssueTypeManager()
        throws TorqueException
    {
        super();
        setRegion(getClassName().replace('.', '_'));
        validFields = new HashMap();
        validFields.put(RModuleIssueTypePeer.MODULE_ID, null);
    }

    protected Persistent putInstanceImpl(Persistent om)
        throws TorqueException
    {
        Persistent oldOm = super.putInstanceImpl(om);
        List listeners = (List)listenersMap
            .get(RModuleIssueTypePeer.MODULE_ID);
        notifyListeners(listeners, oldOm, om);
        return oldOm;
    }
}
