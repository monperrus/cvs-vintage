/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.ejb.plugins.cmp.jdbc.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.ejb.DeploymentException;
import org.jboss.metadata.MetaData;
import org.jboss.metadata.RelationshipRoleMetaData;
import org.w3c.dom.Element;

/** 
 * Imutable class which represents one ejb-relationship-role element found in the
 * ejb-jar.xml file's ejb-relation elements.
 *
 * @author <a href="mailto:dain@daingroup.com">Dain Sundstrom</a>
 * @version $Revision: 1.4 $
 */
public final class JDBCRelationshipRoleMetaData {
	/**
	 * Relation to which this role belongs.
	 */
	private final JDBCRelationMetaData relationMetaData;

	/**
	 * Role name
	 */
	private final String relationshipRoleName;
	
	/**
	 * Is the multiplicity one? If not, multiplicity is many.
	 */
	private final boolean multiplicityOne;
	
	/**
	 * Should this entity be deleted when related entity is deleted.
	 */
	private final boolean cascadeDelete;
	
	/**
	 * The entity that has this role.
	 */
	private final JDBCEntityMetaData entity;
	
	/**
	 * Name of the entity's cmr field for this role.
	 */
	private final String cmrFieldName;
		
	/**
	 * Type of the cmr field (i.e., collection or set)
	 */
	private final String cmrFieldType;
	
	private final Map tableKeyFields = new HashMap();
	private final Map foreignKeyFields = new HashMap();
	
	public JDBCRelationshipRoleMetaData(
			JDBCRelationMetaData relationMetaData,
			JDBCApplicationMetaData application,
			RelationshipRoleMetaData relationshipRole)  throws DeploymentException {
		
		this.relationMetaData = relationMetaData;
		
		relationshipRoleName = relationshipRole.getRelationshipRoleName();
		multiplicityOne = relationshipRole.isMultiplicityOne();
		cascadeDelete = relationshipRole.isCascadeDelete();
		
		cmrFieldName = relationshipRole.getCMRFieldName();
		cmrFieldType = relationshipRole.getCMRFieldType();

		entity = application.getBeanByEjbName(relationshipRole.getEntityName());
		
		if(relationMetaData.isTableMappingStyle()) {
			// load all pks of entity into tableKeys map
			for(Iterator i = entity.getCMPFields().iterator(); i.hasNext(); ) {
				JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)i.next();
				if(cmpField.isPrimaryKeyMember()) {
					cmpField = new JDBCCMPFieldMetaData(entity, cmpField, entity.getName() + "_" + cmpField.getFieldName(), false);
					tableKeyFields.put(cmpField.getFieldName(), cmpField);
				}
			}
		} else if(relationshipRole.getRelatedRoleMetaData().isMultiplicityOne()){	
			// load all pks of related entity into foreignKeys map
			String relatedEntityName = relationshipRole.getRelatedRoleMetaData().getEntityName();
			JDBCEntityMetaData relatedEntity = application.getBeanByEjbName(relatedEntityName);
			for(Iterator i = relatedEntity.getCMPFields().iterator(); i.hasNext(); ) {
				JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)i.next();
				if(cmpField.isPrimaryKeyMember()) {
					cmpField = new JDBCCMPFieldMetaData(entity, cmpField, relatedEntity.getName() + "_" + cmpField.getFieldName(), false);
					foreignKeyFields.put(cmpField.getFieldName(), cmpField);
				}
			}
		}
	}

	public JDBCRelationshipRoleMetaData(
			JDBCRelationMetaData relationMetaData,
			JDBCApplicationMetaData application,
			JDBCRelationshipRoleMetaData defaultValues) throws DeploymentException {

		this.relationMetaData = relationMetaData;

		entity = application.getBeanByEjbName(defaultValues.getEntity().getName());

		relationshipRoleName = defaultValues.getRelationshipRoleName();
		multiplicityOne = defaultValues.isMultiplicityOne();
		cascadeDelete = defaultValues.isCascadeDelete();
		
		cmrFieldName = defaultValues.getCMRFieldName();
		cmrFieldType = defaultValues.getCMRFieldType();	
		
		if(relationMetaData.isTableMappingStyle()) {
			// load all pks of entity into tableKeys map
			for(Iterator i = entity.getCMPFields().iterator(); i.hasNext(); ) {
				JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)i.next();
				if(cmpField.isPrimaryKeyMember()) {
					cmpField = new JDBCCMPFieldMetaData(entity, cmpField, entity.getName() + "_" + cmpField.getFieldName(), false);
					tableKeyFields.put(cmpField.getFieldName(), cmpField);
				}
			}
		} else if(defaultValues.getRelatedRole().isMultiplicityOne()){	
			// load all pks of related entity into foreignKeys map
			String relatedEntityName = defaultValues.getRelatedRole().getEntity().getName();
			JDBCEntityMetaData relatedEntity = application.getBeanByEjbName(relatedEntityName);
			for(Iterator i = relatedEntity.getCMPFields().iterator(); i.hasNext(); ) {
				JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)i.next();
				if(cmpField.isPrimaryKeyMember()) {
					cmpField = new JDBCCMPFieldMetaData(entity, cmpField, relatedEntity.getName() + "_" + cmpField.getFieldName(), false);
					foreignKeyFields.put(cmpField.getFieldName(), cmpField);
				}
			}
		}
	}

	public JDBCRelationshipRoleMetaData(
			JDBCRelationMetaData relationMetaData,
			JDBCApplicationMetaData application,
			Element element, 
			JDBCRelationshipRoleMetaData defaultValues) throws DeploymentException {
		
		this.relationMetaData = relationMetaData;
		this.entity = application.getBeanByEjbName(defaultValues.getEntity().getName());
		
		relationshipRoleName = defaultValues.getRelationshipRoleName();
		multiplicityOne = defaultValues.isMultiplicityOne();
		cascadeDelete = defaultValues.isCascadeDelete();
		
		cmrFieldName = defaultValues.getCMRFieldName();
		cmrFieldType = defaultValues.getCMRFieldType();		
		
		if(relationMetaData.isTableMappingStyle()) {
			loadTableKeyFields(element);
		} else if(defaultValues.getRelatedRole().isMultiplicityOne()){	
			String relatedEntityName = defaultValues.getRelatedRole().getEntity().getName();
			loadForeignKeyFields(element, application.getBeanByEjbName(relatedEntityName));
		}
	}

	/**
	 * Gets the relation to which this role belongs.
	 */
	public JDBCRelationMetaData getRelationMetaData() {
		return relationMetaData;
	}
	
	/**
	 * Gets the name of this role.
	 */
	public String getRelationshipRoleName() {
		return relationshipRoleName;
	}
	
	/**
	 * Checks if the multiplicity is one.
	 */
	public boolean isMultiplicityOne() {
		return multiplicityOne;
	}
	
	/**
	 * Checks if the multiplicity is many.
	 */
	public boolean isMultiplicityMany() {
		return !multiplicityOne;
	}
	
	/**
	 * Should this entity be deleted when related entity is deleted.
	 */
	public boolean isCascadeDelete() {
		return cascadeDelete;
	}
	
	/**
	 * Gets the name of the entity that has this role.
	 */
	public JDBCEntityMetaData getEntity() {
		return entity;
	}
	
	/**
	 * Gets the name of the entity's cmr field for this role.
	 */
	public String getCMRFieldName() {
		return cmrFieldName;
	}
	
	/**
	 * Gets the type of the cmr field (i.e., collection or set)
	 */
	public String getCMRFieldType() {
		return cmrFieldType;
	}	

	/**
	 * Gets the related role's jdbc meta data.
	 */
	public JDBCRelationshipRoleMetaData getRelatedRole() {
		return relationMetaData.getOtherRelationshipRole(this);
	}
	
	/**
	 * Gets the foreign key fields of this role. The foreign key fields hold the
	 * primary keys of the related entity. A relationship role has foreign key 
	 * fields if the relation mapping style is foreign key and the other side of
	 * the relationship has a multiplicity of one.
	 * @return an unmodifiable collection of JDBCCMPFieldMetaData objects
	 */
	public Collection getForeignKeyFields() {
		return Collections.unmodifiableCollection(foreignKeyFields.values());
	}
	
	/**
	 * Gets the key fields of this role in the relation table. The table key fields
	 * hold the primary keys of this role's entity. A relationship role has table key 
	 * fields if the relation is mapped to a relation table.
	 * @return an unmodifiable collection of JDBCCMPFieldMetaData objects
	 */
	public Collection getTableKeyFields() {
		return Collections.unmodifiableCollection(tableKeyFields.values());
	}
	
	/**
	 * Loads the foreign key fields for this role based on the primary keys of the
	 * specified related entity and the override data from the xml element.
	 */
	private void loadForeignKeyFields(Element element, JDBCEntityMetaData relatedEntity) throws DeploymentException {
		// load all pks of related entity into foreignKeys map
		for(Iterator i = relatedEntity.getCMPFields().iterator(); i.hasNext(); ) {
			JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)i.next();
			if(cmpField.isPrimaryKeyMember()) {
				cmpField = new JDBCCMPFieldMetaData(entity, cmpField, relatedEntity.getName() + "_" + cmpField.getFieldName(), false);
				foreignKeyFields.put(cmpField.getFieldName(), cmpField);
			}
		}

		Element foreignKeysElement = MetaData.getOptionalChild(element,"foreign-key-fields");
		
		// no field overrides, we're done
		if(foreignKeysElement == null) {
			return;
		}
		
		// load overrides
		Iterator fkIter = MetaData.getChildrenByTagName(foreignKeysElement, "foreign-key-field");
		
		// if empty foreign-key-fields element, no fk should be used
		if(!fkIter.hasNext()) {
			foreignKeyFields.clear();
		}
		
		while(fkIter.hasNext()) {
			Element foreignKeyElement = (Element)fkIter.next();
			String foreignKeyName = MetaData.getUniqueChildContent(foreignKeyElement, "field-name");
			JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)foreignKeyFields.get(foreignKeyName);
			if(cmpField == null) {
				throw new DeploymentException("CMP field for foreign key not found: field name="+foreignKeyName);
			}
			cmpField = new JDBCCMPFieldMetaData(entity, foreignKeyElement, cmpField, false);
			foreignKeyFields.put(cmpField.getFieldName(), cmpField);
		}
	}

	/**
	 * Loads the table key fields for this role based on the primary keys of the
	 * this entity and the override data from the xml element.
	 */
	private void loadTableKeyFields(Element element) throws DeploymentException {
		// load all pks of entity into tableKeys map
		for(Iterator i = entity.getCMPFields().iterator(); i.hasNext(); ) {
			JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)i.next();
			if(cmpField.isPrimaryKeyMember()) {
				cmpField = new JDBCCMPFieldMetaData(entity, cmpField, entity.getName() + "_" + cmpField.getFieldName(), false);
				tableKeyFields.put(cmpField.getFieldName(), cmpField);
			}
		}

		Element tableKeysElement = MetaData.getOptionalChild(element,"table-key-fields");
		
		// no field overrides, we're done
		if(tableKeysElement == null) {
			return;
		}
		
		// load overrides
		for(Iterator i = MetaData.getChildrenByTagName(tableKeysElement, "table-key-field"); i.hasNext(); ) {
			Element tableKeyElement = (Element)i.next();
			String tableKeyName = MetaData.getUniqueChildContent(tableKeyElement, "field-name");
			JDBCCMPFieldMetaData cmpField = (JDBCCMPFieldMetaData)tableKeyFields.get(tableKeyName);
			if(cmpField == null) {
				throw new DeploymentException("CMP field for table key not found: field name="+tableKeyName);
			}
			cmpField = new JDBCCMPFieldMetaData(entity, tableKeyElement, cmpField, false);
			tableKeyFields.put(cmpField.getFieldName(), cmpField);
		}
	}
}
