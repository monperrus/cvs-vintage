

package org.tigris.scarab.om;

import java.util.List;
import java.util.HashMap;

import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;
import org.apache.torque.om.ComboKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.om.NumberKey;

/** 
 * This class manages RModuleAttribute objects.  
 * The skeleton for this class was autogenerated by Torque  * You should add additional methods to this class to meet the
 * application requirements.  This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public class RModuleAttributeManager
    extends BaseRModuleAttributeManager
{
    /**
     * Creates a new <code>RModuleAttributeManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public RModuleAttributeManager()
        throws TorqueException
    {
        super();
        validFields = new HashMap();
        validFields.put(RModuleAttributePeer.MODULE_ID, null);
    }

    protected Persistent putInstanceImpl(Persistent om)
        throws TorqueException
    {
        Persistent oldOm = super.putInstanceImpl(om);
        List listeners = (List)listenersMap
            .get(RModuleAttributePeer.MODULE_ID);
        notifyListeners(listeners, oldOm, om);
        return oldOm;
    }

    public static final RModuleAttribute getInstance(Integer moduleId,  
        Integer attributeId, Integer issueTypeId)
        throws TorqueException
    {
        SimpleKey[] keys = {
            new NumberKey(moduleId.toString()), 
            new NumberKey(attributeId.toString()), 
            new NumberKey(issueTypeId.toString())
        };
        return getInstance(new ComboKey(keys));
    }
}





