/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.metadata;

import org.w3c.dom.Element;

import org.jboss.ejb.DeploymentException;

/** 
 * The meta data object for the cluster-config element.
 * This element only defines the HAPartition name at this time.  It will be
 * expanded to include other cluster configuration parameters later on.

 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>.
 * @version $Revision: 1.2 $
 */
public class ClusterConfigMetaData extends MetaData
{
   public final static String DEFAULT_PARTITION = "DefaultPartition";
   private String partitionName = DEFAULT_PARTITION;
   private String homeLoadBalancePolicy = null;
   private String beanLoadBalancePolicy = null;

   public String getPartitionName()
   {
      return partitionName;
   }

   public String getHomeLoadBalancePolicy()
   {
      return homeLoadBalancePolicy;
   }

   public String getBeanLoadBalancePolicy()
   {
      return homeLoadBalancePolicy;
   }

   public void importJbossXml(Element element) throws DeploymentException 
   {
      partitionName = getElementContent(getOptionalChild(element, "partition-name"), DEFAULT_PARTITION);
      homeLoadBalancePolicy = getElementContent(getOptionalChild(element, "home-load-balance-policy"), null);
      beanLoadBalancePolicy = getElementContent(getOptionalChild(element, "bean-load-balance-policy"), null);
   }
}
