/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.monitor;

import org.jboss.monitor.client.BeanCacheSnapshot;

/**
 * The JMX management interface for the {@link BeanCacheMonitor} MBean.
 * 
 * @see Monitorable
 * @author <a href="mailto:simone.bordet@compaq.com">Simone Bordet</a>
 * @version $Revision: 1.4 $
 */
public interface BeanCacheMonitorMBean
{
   /**
    * Returns the cache data at the call instant.
    * @return null if a problem is encountered while sampling the cache,
    * 
    * otherwise an array (possibly of size 0) with the cache data.
    */
   BeanCacheSnapshot[] getSnapshots();
}
