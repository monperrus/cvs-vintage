

package org.tigris.scarab.om;

import java.util.List;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;

/** 
 * This class manages DependType objects.  
 * The skeleton for this class was autogenerated by Torque  * You should add additional methods to this class to meet the
 * application requirements.  This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public class DependTypeManager
    extends BaseDependTypeManager
{
    // the following Strings are method names that are used in caching results
    private static final String GET_ALL = 
        "getAll";

    /**
     * Creates a new <code>DependTypeManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public DependTypeManager()
        throws TorqueException
    {
        super();
        setRegion(getClassName().replace('.', '_'));
    }

    public static List getAll()
        throws TorqueException
    {
        return getManager().getAllImpl();
    }

    public List getAllImpl()
        throws TorqueException
    {
        List result = null;
        Object obj = getMethodResult().get(this.toString(), GET_ALL); 
        if ( obj == null ) 
        {        
            result = DependTypePeer.doSelect(new Criteria());
            getMethodResult().put(result, this.toString(), GET_ALL);
        }
        else 
        {
            result = (List)obj;
        }
        return result;
    }

    protected Persistent putInstanceImpl(Persistent om)
        throws TorqueException
    {
        Persistent oldOm = super.putInstanceImpl(om);
        getMethodResult().remove(this, GET_ALL);
        return oldOm;
    }
}
