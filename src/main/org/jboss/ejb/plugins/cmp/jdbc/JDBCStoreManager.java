/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.cmp.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.ejb.DeploymentException;
import org.jboss.ejb.EntityEnterpriseContext;
import org.jboss.ejb.plugins.cmp.CMPStoreManager;
import org.jboss.ejb.plugins.cmp.CommandFactory;
import org.jboss.ejb.plugins.cmp.bridge.EntityBridgeInvocationHandler;
import org.jboss.ejb.plugins.cmp.jdbc.bridge.JDBCCMPFieldBridge;
import org.jboss.ejb.plugins.cmp.jdbc.bridge.JDBCCMRFieldBridge;
import org.jboss.ejb.plugins.cmp.jdbc.bridge.JDBCEntityBridge;
import org.jboss.ejb.plugins.cmp.jdbc.metadata.JDBCApplicationMetaData;
import org.jboss.ejb.plugins.cmp.jdbc.metadata.JDBCEntityMetaData;
import org.jboss.ejb.plugins.cmp.jdbc.metadata.JDBCXmlFileLoader;
import org.jboss.metadata.ApplicationMetaData;
import org.jboss.proxy.Proxy;

/**
 * JDBCStoreManager manages storage of persistence data into a table.
 * Other then loading the initial jbosscmp-jdbc.xml file this class
 * does very little. The interesting tasks are performed by the command
 * classes.
 *
 * Life-cycle:
 *		Tied to the life-cycle of the entity container.
 *
 * Multiplicity:	
 *		One per cmp entity bean. This could be less if another implementaion of 
 * EntityPersistenceStore is created and thoes beans use the implementation 		
 *
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 * @see org.jboss.ejb.EntityPersistenceStore
 * @version $Revision: 1.7 $
 */                            
public class JDBCStoreManager extends CMPStoreManager {
	protected DataSource dataSource;
	
	protected JDBCTypeFactory typeFactory;
	protected boolean debug;
	
	protected JDBCEntityMetaData metaData;
	protected JDBCEntityBridge entityBridge;
	
	protected JDBCLoadFieldCommand loadFieldCommand;
	protected JDBCFindByForeignKeyCommand findByForeignKeyCommand;
	protected JDBCLoadRelationCommand loadRelationCommand;
	protected JDBCDeleteRelationsCommand deleteRelationsCommand;
	protected JDBCInsertRelationsCommand insertRelationsCommand;
	
   public void init() throws Exception {
		initTxDataMap();
		
		metaData = loadJDBCEntityMetaData();

		// set debug flag
		debug = metaData.isDebug();
		
		// find the datasource
		try {
			dataSource = (DataSource)new InitialContext().lookup(metaData.getDataSourceName());
		} catch(NamingException e) {
			throw new DeploymentException("Error: can't find data source: " + metaData.getDataSourceName());
		}
		
		typeFactory = new JDBCTypeFactory(metaData.getTypeMapping(), metaData.getJDBCApplication().getValueClasses());
		entityBridge = new JDBCEntityBridge(metaData, log, this);

		super.init();
		
		loadFieldCommand = getCommandFactory().createLoadFieldCommand();
		findByForeignKeyCommand = getCommandFactory().createFindByForeignKeyCommand();
		loadRelationCommand = getCommandFactory().createLoadRelationCommand();
		deleteRelationsCommand = getCommandFactory().createDeleteRelationsCommand();
		insertRelationsCommand = getCommandFactory().createInsertRelationsCommand();
	}
	
   public void start() throws Exception {
      super.start();
		JDBCFindEntitiesCommand find = (JDBCFindEntitiesCommand)findEntitiesCommand;
		find.start();
   }

	public JDBCEntityBridge getEntityBridge() {
		return entityBridge;
	}
	
	public JDBCTypeFactory getJDBCTypeFactory() {
	   return typeFactory;
	}
	
	public boolean getDebug() {
		return debug;
	}

	public JDBCEntityMetaData getMetaData() {
		return metaData;
	}
	
	protected CommandFactory createCommandFactory() throws Exception {
		return new JDBCCommandFactory(this);
	}
	
	public JDBCCommandFactory getCommandFactory() {
		return (JDBCCommandFactory) commandFactory;
	}

	public void loadField(JDBCCMPFieldBridge field, EntityEnterpriseContext ctx) {
      loadFieldCommand.execute(field, ctx);
   }
   
 	/**
	* Returns a new instance of a class which implemnts the bean class.
	* 
	* @see java.lang.Class#newInstance 
	* @return the new instance
	*/
	public Object createBeanClassInstance() throws Exception {
		Class beanClass = container.getBeanClass();
		
		Class[] classes = new Class[] { beanClass };
		EntityBridgeInvocationHandler handler = new EntityBridgeInvocationHandler(entityBridge, beanClass);           
		ClassLoader classLoader = beanClass.getClassLoader();

		return Proxy.newProxyInstance(classLoader, classes, handler);
	}
	
	/** 
	 * Returns a database connection
	 */
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	public Set findByForeignKey(Object foreignKey, JDBCCMPFieldBridge[] foreignKeyFields) {
		return findByForeignKeyCommand.execute(foreignKey, foreignKeyFields);
	}
	
	public Set loadRelation(JDBCCMRFieldBridge cmrField, Object pk) {  
		return loadRelationCommand.execute(cmrField, pk);
	}

	public void deleteRelations(RelationData relationData) {  
		deleteRelationsCommand.execute(relationData);
	}
	
	public void insertRelations(RelationData relationData) {  
		insertRelationsCommand.execute(relationData);
	}
	
	public Map getTxDataMap() {
		ApplicationMetaData amd = container.getBeanMetaData().getApplicationMetaData();

		// Get Tx Hashtable
		return (Map)amd.getPluginData("CMP-JDBC-TX-DATA");
	}
	
	private void initTxDataMap() {
		ApplicationMetaData amd = container.getBeanMetaData().getApplicationMetaData();

		// Get Tx Hashtable
		Map txDataMap = (Map)amd.getPluginData("CMP-JDBC-TX-DATA");
		if(txDataMap == null) {
			// we are the first JDBC CMP manager to get to initTxDataMap.
			txDataMap = Collections.synchronizedMap(new HashMap());
			amd.addPluginData("CMP-JDBC-TX-DATA", txDataMap);
		}
	}
	
	private JDBCEntityMetaData loadJDBCEntityMetaData() throws DeploymentException {
		ApplicationMetaData amd = container.getBeanMetaData().getApplicationMetaData();

		// Get JDBC MetaData
		JDBCApplicationMetaData jamd = (JDBCApplicationMetaData)amd.getPluginData("CMP-JDBC");
		if (jamd == null) {
			// we are the first cmp entity to need jbosscmp-jdbc. Load jbosscmp-jdbc.xml for the whole application
			JDBCXmlFileLoader jfl = new JDBCXmlFileLoader(amd, container.getClassLoader(), container.getLocalClassLoader(), log);
			jamd = jfl.load();
			amd.addPluginData("CMP-JDBC", jamd);
		}
		
		// Get JDBC Bean MetaData
		String ejbName = container.getBeanMetaData().getEjbName();
		JDBCEntityMetaData metadata = jamd.getBeanByEjbName(ejbName);
		if(metadata == null) {
			throw new DeploymentException("No metadata found for bean " + ejbName);
		}
		return metadata;
	}
}
