/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.metadata;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.jboss.ejb.DeploymentException;

/** The meta data information specific to session beans.
 *
 *   @author <a href="mailto:sebastien.alborini@m4x.org">Sebastien Alborini</a>
 *   @author Scott_Stark@displayscape.com
 *   @version $Revision: 1.5 $
 */
public class SessionMetaData extends BeanMetaData {
    // Constants -----------------------------------------------------
    
    // Attributes ----------------------------------------------------
	private boolean stateful;
	private boolean containerManagedTx;

    // Static --------------------------------------------------------
    
    // Constructors --------------------------------------------------
    public SessionMetaData(ApplicationMetaData app)
    {
		super(app, BeanMetaData.SESSION_TYPE);
	}
	
    // Public --------------------------------------------------------
    public boolean isStateful() { return stateful; }
    public boolean isStateless() { return !stateful; }
    public boolean isContainerManagedTx() { return containerManagedTx; }
	public boolean isBeanManagedTx() { return !containerManagedTx; }
		
	public String getDefaultConfigurationName() {
		if (isStateful()) {
			return jdk13Enabled() ? ConfigurationMetaData.STATEFUL_13 : ConfigurationMetaData.STATEFUL_12;
		} else {
			return jdk13Enabled() ? ConfigurationMetaData.STATELESS_13 : ConfigurationMetaData.STATELESS_12;
		}
	}
	
	public void importEjbJarXml(Element element) throws DeploymentException {
		super.importEjbJarXml(element);
		
		// set the session type 
		String sessionType = getElementContent(getUniqueChild(element, "session-type"));
		if (sessionType.equals("Stateful")) {
			stateful = true;
		} else if (sessionType.equals("Stateless")) {
			stateful = false;
		} else {
			throw new DeploymentException("session type should be 'Stateful' or 'Stateless'");
		}
			
		// set the transaction type
		String transactionType = getElementContent(getUniqueChild(element, "transaction-type"));
		if (transactionType.equals("Bean")) {
			containerManagedTx = false;
		} else if (transactionType.equals("Container")) {
			containerManagedTx = true;
		} else {
			throw new DeploymentException("transaction type should be 'Bean' or 'Container'");
		}
	}
			
	// Package protected ---------------------------------------------
    
    // Protected -----------------------------------------------------
    
    // Private -------------------------------------------------------
    
    // Inner classes -------------------------------------------------
}
