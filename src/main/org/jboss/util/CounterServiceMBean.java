package org.jboss.util;

import java.util.Vector;

/** 
 * MBean interface for the CounterService.
 * @author <a href="mailto:danch@nvisia.com">Dan Christopherson</href>
 */
public interface CounterServiceMBean 
   extends org.jboss.system.ServiceMBean
{
   public String list();
}