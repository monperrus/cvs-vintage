/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.cmp.jdbc.metadata;

import java.util.Iterator;
import org.jboss.ejb.DeploymentException;
import org.jboss.metadata.MetaData;
import org.jboss.metadata.RelationMetaData;
import org.jboss.metadata.RelationshipRoleMetaData;
import org.w3c.dom.Element;

/**
 * Imutable class that represents one ejb-relation element found in the ejb-jar.xml
 * file's relationships elements.
 *    
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 *	@version $Revision: 1.3 $
 */
public final class JDBCRelationMetaData {
	private final static int TABLE = 1;
	private final static int FOREIGN_KEY = 2;
	
	/** Name of the relation. Loaded from the ejb-relation-name element. */
	private final String relationName;

	/** 
	 * The left jdbc relationship role. Loaded from an ejb-relationship-role.
	 * Left/right assignment is completely arbitrary.
	 */
	private final JDBCRelationshipRoleMetaData left;

	/** 
	 * The right relationship role. Loaded from an ejb-relationship-role.
	 * Left/right assignment is completely arbitrary.
	 */
	private final JDBCRelationshipRoleMetaData right;
	
	/**
	 * The mapping style for this relation (i.e., TABLE or FOREIGN_KEY).
	 */
	private final int mappingStyle;
	
	/** data source name in jndi */
	private final String dataSourceName;
	
	/** type mapping used for the relation table */
	private final JDBCTypeMappingMetaData typeMapping;
	
	/** should excess debug information be logged */
	private final boolean debug;
	
	/** the name of the table to use for this bean */
	private final String tableName;
	
	/** should we create the table when deployed */
	private final boolean createTable;
	
	/** should we drop the table when deployed */
	private final boolean removeTable;
	
	/** should we use 'SELECT ... FOR UPDATE' syntax? */
	private final boolean selectForUpdate;
	
	/** should the table have a primary key constraint? */
	private final boolean primaryKeyConstraint;
	
	/** is the relationship read-only? */
	private final boolean readOnly;
	
	/** how long is read valid */
	private final int readTimeOut;
		
	/**
	 * Constructs jdbc relation meta data with the data from the relation meta data loaded
	 * from the ejb-jar.xml file.
	 *
	 * @param jdbcApplication used to retrieve the entities of this relation
	 * @param relationMetaData relation meta data loaded from the ejb-jar.xml file
	 */
	public JDBCRelationMetaData(JDBCApplicationMetaData jdbcApplication, RelationMetaData relationMetaData) throws DeploymentException {
		relationName = relationMetaData.getRelationName();
		
		RelationshipRoleMetaData leftRole = relationMetaData.getLeftRelationshipRole();
		RelationshipRoleMetaData rightRole = relationMetaData.getRightRelationshipRole();

		// set the default mapping style
		if(leftRole.isMultiplicityMany() && rightRole.isMultiplicityMany()) {
			mappingStyle = TABLE;
		} else {
			mappingStyle = FOREIGN_KEY;
		}

		dataSourceName = null;
		typeMapping = null;
		debug = false;
		createTable = false;
		removeTable = false;
		selectForUpdate = false;
		primaryKeyConstraint = false;
		readOnly = false;
		readTimeOut = -1;		

		left = new JDBCRelationshipRoleMetaData(
						this, 
						jdbcApplication,
						leftRole);
						
		right = new JDBCRelationshipRoleMetaData(
						this, 
						jdbcApplication,		
						rightRole); 
						
		if(mappingStyle == TABLE) {
			tableName = createDefaultTableName();			
		} else {
		   tableName = null;
		}
	}

	/**
	 * Constructs relation meta data with the data contained in the ejb-relation element 
	 * or the defaults element from a jbosscmp-jdbc xml file. Optional values of the xml
	 * element that are not present are loaded from the defalutValues parameter.
	 *
	 * @param jdbcApplication used to retrieve type mappings in table mapping style
	 * @param element the xml Element which contains the metadata about this relation
	 * @param defaultValues the JDBCApplicationMetaData which contains the values
	 * 		for optional elements of the element
	 * @throws DeploymentException if the xml element is not semantically correct
	 */
	public JDBCRelationMetaData(JDBCApplicationMetaData jdbcApplication, Element element, JDBCRelationMetaData defaultValues) throws DeploymentException {
		relationName = defaultValues.getRelationName();

		// enable extra debugging?
		String debugString = MetaData.getOptionalChildContent(element, "debug");
		if(debugString != null) {
			debug = Boolean.valueOf(debugString).booleanValue();
		} else {
			debug = defaultValues.isDebug();
		}	
		
		// get the mapping element; may be the defaults, table-mapping, or foreigh-key-mapping
		Element mappingElement;
		if("defaults".equals(element.getTagName())) {
			mappingElement = element;
			// set mapping style based on perferred-relation-mapping (if possible) 
			String perferredRelationMapping = MetaData.getOptionalChildContent(element, "preferred-relation-mapping");
			if("table".equals(perferredRelationMapping) || defaultValues.isManyToMany()) {
				mappingStyle = TABLE;
			} else {
				mappingStyle = FOREIGN_KEY;
			}
		} else {
			// check for table mapping style
			mappingElement = MetaData.getOptionalChild(element, "table-mapping");
			if(mappingElement != null) {
				mappingStyle = TABLE;
			} else {
				// check for foreign key mapping 
				mappingElement = MetaData.getOptionalChild(element, "foreign-key-mapping");
				if(mappingElement != null) {
					mappingStyle = FOREIGN_KEY;
					if(defaultValues.isManyToMany()) {
						throw new DeploymentException("Foreign key mapping-style is not allowed for many-to-many relationsips.");
					}
				} else {
					// no mapping style element, will use defaultValues
					if(defaultValues.isForeignKeyMappingStyle()) {
						mappingStyle = FOREIGN_KEY;
					} else {
						mappingStyle = TABLE;
					}
				}
			}
		}
				
		// if no mapping element given, use defaultValues
		if(mappingElement == null) {
			dataSourceName = defaultValues.getDataSourceName();
			typeMapping = defaultValues.getTypeMapping();
			tableName = defaultValues.getTableName();
			createTable = defaultValues.getCreateTable();
			removeTable = defaultValues.getRemoveTable();
			selectForUpdate = defaultValues.hasSelectForUpdate();
			primaryKeyConstraint = defaultValues.hasPrimaryKeyConstraint();
			readOnly = defaultValues.isReadOnly();
			readTimeOut = defaultValues.getReadTimeOut();
			
			left = new JDBCRelationshipRoleMetaData(
							this,
							jdbcApplication,							
						   defaultValues.getLeftRelationshipRole());
							
			right = new JDBCRelationshipRoleMetaData(
							this,
							jdbcApplication,
						   defaultValues.getRightRelationshipRole());
			
			return;		
		} 
	   
		if(mappingStyle == TABLE) {
			// datasource name
			String dataSourceNameString = MetaData.getOptionalChildContent(mappingElement, "datasource");
			if(dataSourceNameString != null) {
				dataSourceName = dataSourceNameString;
			} else {
				dataSourceName = defaultValues.getDataSourceName();
			}
			
			// get the type mapping for this datasource (optional, but always set in standardjbosscmp-jdbc.xml)
			String typeMappingString = MetaData.getOptionalChildContent(mappingElement, "type-mapping");		
			if(typeMappingString != null) {
				typeMapping = jdbcApplication.getTypeMappingByName(typeMappingString);
			
				if(typeMapping == null) {
					throw new DeploymentException("Error in jbosscmp-jdbc.xml : type-mapping " + typeMappingString + " not found");
				}
			} else {
				typeMapping = defaultValues.getTypeMapping();
			}
			
			// get table name
			String tableNameString = MetaData.getOptionalChildContent(mappingElement, "table-name");
			if(tableNameString == null) {
				tableNameString = defaultValues.getTableName();
				if(tableNameString == null) {
					// use defaultValues to create default, because left/right 
					// have not been assigned yet, and values used to generate
					// default table name never change
					tableNameString = defaultValues.createDefaultTableName();
				}
			}
			tableName = tableNameString;
				
			// create table?  If not provided, keep default.
			String createString = MetaData.getOptionalChildContent(mappingElement, "create-table");
			if(createString != null) {
				createTable = Boolean.valueOf(createString).booleanValue();
			} else {
				createTable = defaultValues.getCreateTable();
			}
				
			// remove table?  If not provided, keep default.
			String removeString = MetaData.getOptionalChildContent(mappingElement, "remove-table");
			if(removeString != null) {
				removeTable = Boolean.valueOf(removeString).booleanValue();
			} else {
				removeTable = defaultValues.getRemoveTable();
			}
	
			// select for update
			String sForUpString = MetaData.getOptionalChildContent(mappingElement, "select-for-update");
			if(sForUpString != null) {
				selectForUpdate = !isReadOnly() && (Boolean.valueOf(sForUpString).booleanValue());
			} else {
				selectForUpdate = defaultValues.hasSelectForUpdate();
			}
	
			// primary key constraint?  If not provided, keep default.
			String pkString = MetaData.getOptionalChildContent(mappingElement, "pk-constraint");
			if(pkString != null) {
				primaryKeyConstraint = Boolean.valueOf(pkString).booleanValue();
			} else {
				primaryKeyConstraint = defaultValues.hasPrimaryKeyConstraint();
			}
			
			// read-only
			String readOnlyString = MetaData.getOptionalChildContent(mappingElement, "read-only");
			if(readOnlyString != null) {
				readOnly = Boolean.valueOf(readOnlyString).booleanValue();
			} else {
				readOnly = defaultValues.isReadOnly();
			}
	
			// read-time-out
			String readTimeOutString = MetaData.getOptionalChildContent(mappingElement, "read-time-out");
			if(readTimeOutString != null) {
				readTimeOut = Integer.parseInt(readTimeOutString);
			} else {
				readTimeOut = defaultValues.getReadTimeOut();
			}		
		} else {
			dataSourceName = null;
			typeMapping = null;
			tableName = null;
			createTable = false;
			removeTable = false;
			selectForUpdate = false;
			primaryKeyConstraint = false;
			readOnly = false;
			readTimeOut = -1;		
		}	

		//
		// load metadata for each specified role
		//
		String leftRoleName = defaultValues.getLeftRelationshipRole().getRelationshipRoleName();
		String rightRoleName = defaultValues.getRightRelationshipRole().getRelationshipRoleName();
		JDBCRelationshipRoleMetaData leftRole = null;
		JDBCRelationshipRoleMetaData rightRole = null;
		
		Iterator iter = MetaData.getChildrenByTagName(mappingElement, "ejb-relationship-role");
		for(int i=0; iter.hasNext(); i++) {
			
			// only 2 roles are allow 
			if(i > 1) {
				throw new DeploymentException("Expected only 2 ejb-relationship-role but found more then 2");
			}
			
			Element relationshipRoleElement = (Element)iter.next();
			String relationshipRoleName = MetaData.getUniqueChildContent(relationshipRoleElement, "ejb-relationship-role-name");
			if(leftRoleName.equals(relationshipRoleName)) {
				leftRole = new JDBCRelationshipRoleMetaData(
								this,
							   jdbcApplication,
								relationshipRoleElement, 
								defaultValues.getLeftRelationshipRole());
			} else if(rightRoleName.equals(relationshipRoleName)) {
				rightRole = new JDBCRelationshipRoleMetaData(
								this,
							   jdbcApplication,
								relationshipRoleElement, 
								defaultValues.getRightRelationshipRole());
			} else {
				throw new DeploymentException("Found ejb-relationship-role '" + relationshipRoleName + "' in jboss-cmp.xml, but no matching role exits in ejb-jar.xml");
			}
		}
		
		// if left role was not specified create a new one for this relation
		if(leftRole == null) {
			leftRole = new JDBCRelationshipRoleMetaData(
							this,
							jdbcApplication,							
						   defaultValues.getLeftRelationshipRole());
							
		}
		
		// if right role was not specified create a new one for this relation
		if(rightRole == null) {
			rightRole = new JDBCRelationshipRoleMetaData(
							this,
							jdbcApplication,
						   defaultValues.getRightRelationshipRole());
		}
		
		// assign the final roles
		left = leftRole;
		right = rightRole;
	}

	/** 
	 * Gets the relation name. 
	 * Relation name is loaded from the ejb-relation-name element.
	 * @return the name of this relation
	 */
	public String getRelationName() {
	   return relationName;
	}
	
	/** 
	 * Gets the left jdbc relationship role. 
	 * The relationship role is loaded from an ejb-relationship-role.
	 * Left/right assignment is completely arbitrary.
	 * @return the left JDBCRelationshipRoleMetaData
	 */
	public JDBCRelationshipRoleMetaData getLeftRelationshipRole() {
		return left;
	}

	/** 
	 * Gets the right jdbc relationship role.
	 * The relationship role is loaded from an ejb-relationship-role.
	 * Left/right assignment is completely arbitrary.
	 * @return the right JDBCRelationshipRoleMetaData
	 */
	public JDBCRelationshipRoleMetaData getRightRelationshipRole() {
		return right;
	}
	
	/** 
	 * Gets the relationship role related to the specified role.
	 * @param role the relationship role that the related role is desired
	 * @return the elationship role related to the specified role.
	 * @throws DeploymentException if the role parameter is not the left or right role of this relation
	 */
	public JDBCRelationshipRoleMetaData getOtherRelationshipRole(JDBCRelationshipRoleMetaData role) {
		if(left == role) {
			return right;
		} else if(right == role) {
			return left;
		} else {
			throw new IllegalArgumentException("Specified role is not the left or right role. role=" + role);
		}
	}
	
	/**
	 * Should this relation be mapped to a relation table.
	 * @return true if this relation is mapped to a table
	 */
	public boolean isTableMappingStyle() {
		return mappingStyle == TABLE;
	}
	
	/**
	 * Should this relation use foreign keys for storage.
	 * @return true if this relation is mapped to foreign keys
	 */
	public boolean isForeignKeyMappingStyle() {
		return mappingStyle == FOREIGN_KEY;
	}
	
	/**
	 * Gets the name of the datasource in jndi for this entity
	 * @return the name of datasource in jndi
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * Gets the jdbc type mapping for this entity
	 * @return the jdbc type mapping for this entity
	 */
	public JDBCTypeMappingMetaData getTypeMapping() {
		return typeMapping;
	}
	
	/**
	 * Is extra debug info being logged?
	 * @return true if extra debug info is being logged
	 */
	public boolean isDebug() {
		return debug;
	}
	
	/**
	 * Gets the name of the relation table.
	 * @return the name of the relation table to which is relation is mapped
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Should the relation table be created on startup.
	 * @return true if the store mananager should attempt to create the relation table
	 */
	public boolean getCreateTable() {
		return createTable;
	}
	
	/**
	 * Should the relation table be removed on shutdown.
	 * @return true if the store mananager should attempt to remove the relation table
	 */
	public boolean getRemoveTable() {
		return removeTable;
	}
	
	/**
	 * When the relation table is created, should it have a primary key constraint.
	 * @return true if the store mananager should add a primary key constraint to the
	 * 		the create table sql statement
	 */
	public boolean hasPrimaryKeyConstraint() {
		return primaryKeyConstraint;
	}
	
	/**
	 * Is this relation read-only?
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * Gets the read time out length.
	 */
	public int getReadTimeOut() {
		return readTimeOut;
	}
	
	/**
	 * Should select queries use the for update clause.
	 */
	public boolean hasSelectForUpdate() {
		return selectForUpdate;
	}
	
	private String createDefaultTableName() {
		String defaultTableName = left.getEntity().getName();
		if(left.getCMRFieldName() != null) {
			defaultTableName += "_" + left.getCMRFieldName();
		}
		defaultTableName += "_" + right.getEntity().getName();
		if(right.getCMRFieldName() != null) {
			defaultTableName += "_" + right.getCMRFieldName();
		}
		return defaultTableName;
	}
	
	private boolean isManyToMany() {
		return left.isMultiplicityMany() && right.isMultiplicityMany();
	}
}
