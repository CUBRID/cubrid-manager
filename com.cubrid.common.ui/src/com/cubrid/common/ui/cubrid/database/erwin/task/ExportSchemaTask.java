/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.cubrid.database.erwin.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERXmlModelConstant;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Attribute;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.Datatype;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.LogicalDataType;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.NullOption;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.Order;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.ParentAttribute;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.ParentRelationship;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.PhysicalName;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DBMSVersion;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DOReferenceObject;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Default;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DefaultPropsList;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DefaultPropsList.LogicalDefaultValue;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DefaultPropsList.ServerValue;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectEntity;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectEntityProps;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectEntityProps.DOEntityHeightAutoResizeable;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectEntityProps.DOEntityWidthAutoResizeable;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectEntityProps.DOLocation;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectRelation;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectRelationshipProps;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectRelationshipProps.DORelationshipPath;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DrawingObjectRelationshipProps.DOUserControlledPath;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.ERwin4;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Entity;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Entity.AttributeGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Entity.KeyGroupGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.EntityPropsList;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.EntityPropsList.Name;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.EntityPropsList.Type;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroup;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroup.KeyGroupMemberGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupMember;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupMemberPropsList;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupMemberPropsList.KeyGroupMemberColumn;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupMemberPropsList.KeyGroupPosition;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupMemberPropsList.KeyGroupSortOrder;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupPropsList;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupPropsList.KeyGroupRelationPointer;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.KeyGroupPropsList.KeyGroupType;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Model;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Model.DefaultValueGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Model.EntityGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Model.RelationshipGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Model.SubjectAreaGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.Relationship;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipChildDeleteRule;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipChildEntity;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipChildInsertRule;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipChildUpdateRule;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipNoNulls;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipParentDeleteRule;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipParentEntity;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipParentInsertRule;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipParentUpdateRule;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.RelationshipPropsList.RelationshipSequence;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.StoredDisplay;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.StoredDisplay.DrawingObjectEntityGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.StoredDisplay.DrawingObjectRelationshipGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.SubjectArea;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.SubjectArea.StoredDisplayGroups;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.SubjectAreaProps;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.SubjectAreaProps.ReferencedEntity;
import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.TargetServer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;

/**
 *
 * Export Erwin Schema Task
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-24 created by Yu Guojia
 */
public class ExportSchemaTask extends
		AbstractTask {
	private static final Logger LOGGER = LogUtil.getLogger(ExportSchemaTask.class);

	public static Marshaller marshaller = null;

	private final Map<String, Entity> entityMap = new HashMap<String, Entity>();
	private final Map<String, Relationship> relationshipMap = new HashMap<String, Relationship>();
	private final Map<String, Map<String, Attribute>> entityAttrMap = new HashMap<String, Map<String, Attribute>>();
	private Map<String, String> tablePLMap;//physical table name<-->logical table name
	private Map<String, Map<String, List>> tablesPhysicalLogicalMap;//physical table name<-->columns pysical/logical info

	private final Map<String, SchemaInfo> allSchemaInfos;
	private final String filename;
	private final Map<String, Default> defaultNodeMap = new HashMap<String, Default>();

	public ExportSchemaTask(Map<String, SchemaInfo> allSchemaInfos, String fout) {
		this.allSchemaInfos = allSchemaInfos;
		this.filename = fout;
	}

	private boolean isCancel = false;
	private boolean isSuccess = false;

	public void cancel() {
		isCancel = true;
	}

	public void finish() {
		isSuccess = true;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public Marshaller initMarshaller() throws JAXBException {
		if (marshaller == null) {
			JAXBContext ctx = JAXBContext.newInstance(ERwin4.class);
			marshaller = ctx.createMarshaller();
		}
		return marshaller;
	}

	public void execute() { // FIXME logic code move to core module
		entityMap.clear();
		relationshipMap.clear();
		entityAttrMap.clear();

		ERwin4 root = new ERwin4();

		Model model = new Model();
		// DEFUALT database type "ODBC" "194"
		model.setTargetServer("194");
		model.setDbmsVersion("3");
		model.setModelProps(new Model.ModelProps());

		model.getModelProps().setDbmsVersion(new DBMSVersion());
		model.getModelProps().getDbmsVersion().setValue("3");

		model.getModelProps().setTargetServer(new TargetServer());
		model.getModelProps().getTargetServer().setValue("194");

		model.setId(getUUID());
		root.setModel(model);

		RelationshipGroups relationShipGroups = new RelationshipGroups();
		model.setRelationshipGroups(relationShipGroups);

		model.setDefaultValueGroups(new DefaultValueGroups());

		EntityGroups groups = new EntityGroups();
		model.setEntityGroups(groups);
		for (Entry<String, SchemaInfo> entry : allSchemaInfos.entrySet()) {
			String physicalTableName = entry.getKey();
			String logicalTableName = physicalTableName;
			if (tablePLMap != null) {
				String logical = tablePLMap.get(physicalTableName);
				logicalTableName = StringUtil.isEmpty(logical) ? physicalTableName : logical;
			}
			SchemaInfo schemaInfo = entry.getValue();
			if (schemaInfo.isSystemClass()) {
				continue;
			}

			Entity entity = new Entity();
			groups.getEntity().add(entity);
			entity.setId(getUUID());
			entity.setName(logicalTableName);
			entityMap.put(physicalTableName, entity);
			entityAttrMap.put(physicalTableName, new HashMap<String, Attribute>());

			EntityPropsList entityPropsList = new EntityPropsList();
			entity.setEntityProps(entityPropsList);

			entityPropsList.setPhysicalName(new EntityPropsList.PhysicalName());
			entityPropsList.getPhysicalName().setValue(physicalTableName);

			Type type = new Type();
			if (schemaInfo.getVirtual().equals(ClassType.NORMAL.getText())) {
				type.setValue(ERXmlModelConstant.ENTITYTYPE_TABLE_STR);
			} else {
				type.setValue(ERXmlModelConstant.ENTITYTYPE_VIEW_STR);
			}
			entityPropsList.setType(type);
			Name nameEntityProps = new Name();
			nameEntityProps.setValue(logicalTableName);
			entityPropsList.setName(nameEntityProps);

			List<DBAttribute> attributes = schemaInfo.getAttributes();
			AttributeGroups attributeGroups = new AttributeGroups();
			entity.setAttributeGroups(attributeGroups);

			Map<String, Attribute> schemaAttrMap = new HashMap<String, Attribute>();
			int attrOrder = 1;
			for (DBAttribute dbAttr : attributes) {
				Attribute attribute = new Attribute();
				attributeGroups.getAttribute().add(attribute);

				attribute.setId(getUUID());
				String logicalAttrName = getLogicalAttrName(physicalTableName, dbAttr.getName());
				if (StringUtil.isEmpty(logicalAttrName)) {
					logicalAttrName = dbAttr.getName();
				}
				attribute.setName(logicalAttrName);
				schemaAttrMap.put(dbAttr.getName(), attribute);

				AttributePropsList attributePropsList = new AttributePropsList();
				attribute.setAttributeProps(attributePropsList);

				attributePropsList.setPhysicalName(new PhysicalName());
				attributePropsList.getPhysicalName().setValue(dbAttr.getName());
				com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.Type attrType = new com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.Type();
				attributePropsList.setType(attrType);
				if (dbAttr.isNotNull() && dbAttr.isUnique()) {
					attrType.setValue("" + ERXmlModelConstant.ATTRIBUTE_TYPE_PK);
				} else {
					attrType.setValue("100");
				}
				Datatype dataType = new Datatype();
				String typeValue = dbAttr.getType();
				if (!StringUtil.isEmpty(dbAttr.getEnumeration())) {
					typeValue += dbAttr.getEnumeration();
				}
				dataType.setValue(typeValue);
				attributePropsList.setDataType(dataType);

				com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.Name propName = new com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.AttributePropsList.Name();
				propName.setValue(logicalAttrName);
				attributePropsList.setName(propName);

				LogicalDataType logicalDataType = new LogicalDataType();
				String logicalAttrType = getLogicalAttrType(physicalTableName, dbAttr.getName());
				if (StringUtil.isEmpty(logicalAttrType)) {
					logicalAttrType = typeValue;
				}
				logicalDataType.setValue(logicalAttrType);
				attributePropsList.setLogicalDataType(logicalDataType);

				if (dbAttr.isNotNull()) {
					NullOption nullOption = new NullOption();
					nullOption.setValue("" + 1);
					attributePropsList.setNullOption(nullOption);
				}

				Order order = new Order();
				attributePropsList.setOrder(order);
				order.setValue("" + attrOrder++);
				if (dbAttr.getDefault() != null) {
					String defaultNameStr = handleSpace(dbAttr.getDefault());
					String defaultName = "default_" + defaultNameStr;
					Default defaultValue = null;
					attributePropsList.setDefaultValue(new AttributePropsList.DefaultValue());
					if (defaultNodeMap.containsKey(defaultName)) {
						defaultValue = defaultNodeMap.get(defaultName);
						attributePropsList.getDefaultValue().setValue(defaultValue.getId());
						continue;
					}

					String defaultId = getUUID();

					DefaultValueGroups defaultValueGroups = model.getDefaultValueGroups();
					attributePropsList.getDefaultValue().setValue(defaultId);

					defaultValue = new Default();
					defaultValue.setId(defaultId);
					defaultValue.setName("default_" + defaultNameStr);
					defaultValueGroups.getDefault().add(defaultValue);
					defaultNodeMap.put(defaultValue.getName(), defaultValue);

					DefaultPropsList defaultPropsList = new DefaultPropsList();
					defaultValue.setDefaultProps(defaultPropsList);

					defaultPropsList.setName(new DefaultPropsList.Name());
					defaultPropsList.getName().setValue(defaultValue.getName());

					defaultPropsList.setServerValue(new ServerValue());
					defaultPropsList.getServerValue().setValue(dbAttr.getDefault());

					defaultPropsList.setLogicalDefaultValue(new LogicalDefaultValue());
					defaultPropsList.getLogicalDefaultValue().setValue(dbAttr.getDefault());

				}
			}

			entityAttrMap.put(physicalTableName, schemaAttrMap);
		}

		List<String> relIds = new ArrayList<String>();
		for (Entry<String, SchemaInfo> entry : allSchemaInfos.entrySet()) {

			Entity entity = entityMap.get(entry.getKey());
			if (entity == null)
				continue;
			SchemaInfo schemaInfo = entry.getValue();

			Map<String, Attribute> schemaAttrMap = entityAttrMap.get(schemaInfo.getClassname());

			KeyGroupGroups keyGroups = new KeyGroupGroups();
			entity.setKeyGroupGroups(keyGroups);

			// counter for index
			int fk = 1;
			int idx = 1;
			int uidx = 1;
			List<Constraint> constraints = schemaInfo.getConstraints();
			for (Constraint constraint : constraints) {
				if (!isValid(constraint)) {
					continue;
				}
				KeyGroup keyGroup = new KeyGroup();
				keyGroups.getKeyGroup().add(keyGroup);

				keyGroup.setId(getUUID());
				keyGroup.setName(constraint.getName());

				KeyGroupPropsList keyGroupPropsList = new KeyGroupPropsList();
				keyGroup.setKeyGroupProps(keyGroupPropsList);
				KeyGroupMemberGroups keyGroupMemberGroups = new KeyGroupMemberGroups();
				keyGroup.setKeyGroupMemberGroups(keyGroupMemberGroups);

				KeyGroupType keyGrouptype = new KeyGroupType();
				keyGroupPropsList.setKeyGroupType(keyGrouptype);

				if (constraint.getType().equals(ConstraintType.PRIMARYKEY.getText())) {
					keyGrouptype.setValue("PK");
					List<String> keyAttr = constraint.getAttributes();
					int order = 1;
					for (String keyName : keyAttr) {
						KeyGroupMember keyGroupMember = new KeyGroupMember();
						keyGroupMemberGroups.getKeyGroupMember().add(keyGroupMember);

						keyGroupMember.setId(getUUID());
						keyGroupMember.setName("" + order);

						KeyGroupMemberPropsList propList = new KeyGroupMemberPropsList();
						keyGroupMember.setKeyGroupMemberProps(propList);

						KeyGroupPosition position = new KeyGroupPosition();
						propList.setKeyGroupPosition(position);
						position.setValue("" + order++);

						KeyGroupMemberColumn column = new KeyGroupMemberColumn();
						Attribute attrTemp = schemaAttrMap.get(keyName);
						column.setValue(attrTemp.getId());
						propList.setKeyGroupMemberColumn(column);
					}
				} else if (constraint.getType().equals(ConstraintType.FOREIGNKEY.getText())) {
					keyGrouptype.setValue("IF" + fk++);
					String relId = getUUID();

					keyGroupPropsList.setKeyGroupRelationPointer(new KeyGroupRelationPointer());
					keyGroupPropsList.getKeyGroupRelationPointer().setValue(relId);
					relIds.add(relId);

					List<String> keyAttr = constraint.getAttributes();
					String inherit = constraint.getReferencedTable();
					Entity pEntity = entityMap.get(inherit);
					if (pEntity == null) {
						continue;
					}
					Map<String, Attribute> tempAttrMap = entityAttrMap.get(schemaInfo.getClassname());
					Map<String, Attribute> parentAttrMap = entityAttrMap.get(inherit);

					Relationship relationShip = new Relationship();
					relationShip.setId(relId);
					relationshipMap.put(relId, relationShip);

					relationShip.setName("R/" + fk);
					model.getRelationshipGroups().getRelationship().add(relationShip);

					RelationshipPropsList relationShipPropsList = new RelationshipPropsList();
					relationShip.setRelationshipProps(relationShipPropsList);

					relationShipPropsList.setName(new RelationshipPropsList.Name());
					relationShipPropsList.getName().setValue(relationShip.getName());

					// type == 7 : non-identify fk
					relationShipPropsList.setType(new RelationshipPropsList.Type());
					relationShipPropsList.getType().setValue("2");

					relationShipPropsList.setRelationshipNoNulls(new RelationshipNoNulls());
					relationShipPropsList.getRelationshipNoNulls().setValue("101");

					relationShipPropsList.setRelationshipSequence(new RelationshipSequence());
					relationShipPropsList.getRelationshipSequence().setValue("1");

					relationShipPropsList.setRelationshipParentInsertRule(new RelationshipParentInsertRule());
					relationShipPropsList.getRelationshipParentInsertRule().setValue("0");

					relationShipPropsList.setRelationshipParentUpdateRule(new RelationshipParentUpdateRule());
					relationShipPropsList.setRelationshipParentDeleteRule(new RelationshipParentDeleteRule());

					relationShipPropsList.setRelationshipChildInsertRule(new RelationshipChildInsertRule());
					relationShipPropsList.getRelationshipChildInsertRule().setValue("0");
					relationShipPropsList.setRelationshipChildUpdateRule(new RelationshipChildUpdateRule());
					relationShipPropsList.getRelationshipChildUpdateRule().setValue("0");
					relationShipPropsList.setRelationshipChildDeleteRule(new RelationshipChildDeleteRule());
					relationShipPropsList.getRelationshipChildDeleteRule().setValue("0");

					List<String> rules = constraint.getRules();
					for (String rule : rules) {
						if (rule.indexOf("ON UPDATE") != -1) {
							int tmp = rule.replace("ON UPDATE ", "").hashCode();
							RelationshipParentUpdateRule updateRule = relationShipPropsList.getRelationshipParentUpdateRule();
							if (tmp == "CASCADE".hashCode()) {
								updateRule.setValue("10001");
							} else if (tmp == "NO ACTION".hashCode()) {
								updateRule.setValue("9998");
							} else if (tmp == "RESTRICT".hashCode()) {
								updateRule.setValue("10000");
							}
						} else if (rule.indexOf("ON DELETE") != -1) {
							int tmp = rule.replace("ON DELETE ", "").hashCode();
							RelationshipParentDeleteRule deleteRule = relationShipPropsList.getRelationshipParentDeleteRule();
							if (tmp == "CASCADE".hashCode()) {
								deleteRule.setValue("10005");
							} else if (tmp == "NO ACTION".hashCode()) {
								deleteRule.setValue("9999");
							} else if (tmp == "RESTRICT".hashCode()) {
								deleteRule.setValue("10004");
							}
						}
					}

					relationShipPropsList.setRelationshipParentEntity(new RelationshipParentEntity());
					relationShipPropsList.getRelationshipParentEntity().setValue(pEntity.getId());

					relationShipPropsList.setRelationshipChildEntity(new RelationshipChildEntity());
					relationShipPropsList.getRelationshipChildEntity().setValue(
							entityMap.get(schemaInfo.getClassname()).getId());
					LinkedList<String> pkAttr = new LinkedList<String>();

					SchemaInfo parentTable = allSchemaInfos.get(inherit);
					for (Constraint con : parentTable.getConstraints()) {
						if (con.getType().equals(ConstraintType.PRIMARYKEY.getText())) {
							pkAttr.addAll(con.getAttributes());
							break;
						}
					}

					int order = 1;
					for (String keyName : keyAttr) {
						KeyGroupMember keyGroupMember = new KeyGroupMember();
						keyGroupMemberGroups.getKeyGroupMember().add(keyGroupMember);

						keyGroupMember.setId(getUUID());
						keyGroupMember.setName("" + order);

						KeyGroupMemberPropsList propList = new KeyGroupMemberPropsList();
						keyGroupMember.setKeyGroupMemberProps(propList);

						KeyGroupPosition position = new KeyGroupPosition();
						propList.setKeyGroupPosition(position);
						position.setValue("" + order++);

						KeyGroupMemberColumn column = new KeyGroupMemberColumn();
						String parentPkAttr = pkAttr.remove();
						Attribute attrTemp = tempAttrMap.get(keyName);
						column.setValue(attrTemp.getId());
						propList.setKeyGroupMemberColumn(column);

						// Attribute parent part
						Attribute schemaAttr = schemaAttrMap.get(keyName);
						AttributePropsList schemaPropList = schemaAttr.getAttributeProps();

						Attribute parentPkAttrNode = parentAttrMap.get(parentPkAttr);
						ParentAttribute pAttribute = new ParentAttribute();
						pAttribute.setValue(parentPkAttrNode.getId());
						schemaPropList.setParentAttribute(pAttribute);

						schemaAttr.getAttributeProps().setParentRelationship(
								new ParentRelationship());
						schemaAttr.getAttributeProps().getParentRelationship().setValue(relId);
					}

				} else if (constraint.getType().equals(ConstraintType.INDEX.getText())
						|| constraint.getType().equals(ConstraintType.REVERSEINDEX.getText())) {
					keyGrouptype.setValue("IE" + idx++);
					List<String> keyAttr = constraint.getAttributes();
					int order = 1;
					for (String keyName : keyAttr) {
						KeyGroupMember keyGroupMember = new KeyGroupMember();
						keyGroupMemberGroups.getKeyGroupMember().add(keyGroupMember);

						keyGroupMember.setId(getUUID());
						keyGroupMember.setName("" + order);

						KeyGroupMemberPropsList propList = new KeyGroupMemberPropsList();
						keyGroupMember.setKeyGroupMemberProps(propList);

						for (String rule : constraint.getRules()) {
							if (rule.toLowerCase().startsWith(keyName.toLowerCase())) {
								if (rule.toLowerCase().indexOf(" desc") != -1) {
									propList.setKeyGroupSortOrder(new KeyGroupSortOrder());
									propList.getKeyGroupSortOrder().setValue("DESC");
								}

							}
						}

						KeyGroupPosition position = new KeyGroupPosition();
						propList.setKeyGroupPosition(position);
						position.setValue("" + order++);

						KeyGroupMemberColumn column = new KeyGroupMemberColumn();
						Attribute attrTemp = schemaAttrMap.get(keyName);
						column.setValue(attrTemp.getId());
						propList.setKeyGroupMemberColumn(column);
					}
				} else if (constraint.getType().equals(ConstraintType.UNIQUE.getText())
						|| constraint.getType().equals(ConstraintType.REVERSEUNIQUE.getText())) {
					keyGrouptype.setValue("AK" + uidx++);
					List<String> keyAttr = constraint.getAttributes();
					int order = 1;
					for (String keyName : keyAttr) {
						KeyGroupMember keyGroupMember = new KeyGroupMember();
						keyGroupMemberGroups.getKeyGroupMember().add(keyGroupMember);

						keyGroupMember.setId(getUUID());
						keyGroupMember.setName("" + order);

						KeyGroupMemberPropsList propList = new KeyGroupMemberPropsList();
						keyGroupMember.setKeyGroupMemberProps(propList);

						KeyGroupPosition position = new KeyGroupPosition();
						propList.setKeyGroupPosition(position);
						position.setValue("" + order++);

						KeyGroupMemberColumn column = new KeyGroupMemberColumn();
						Attribute attrTemp = schemaAttrMap.get(keyName);
						column.setValue(attrTemp.getId());
						propList.setKeyGroupMemberColumn(column);

						KeyGroupSortOrder sortOrder = new KeyGroupSortOrder();
						propList.setKeyGroupSortOrder(sortOrder);
						for (String rule : constraint.getRules()) {
							if (rule.toLowerCase().startsWith(keyName.toLowerCase())) {
								String orderStr = rule.replace(keyName.toLowerCase(), "").replaceAll(
										" ", "");
								sortOrder.setValue(orderStr);
							}
						}
					}
				}

			}
		}

		setDrawData(model);
		FileOutputStream fout = null;
		try {
			File f = new File(filename);
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					CommonUITool.openErrorBox(Messages.bind(Messages.errCreateFile, filename));
					return;
				}
			}
			fout = new FileOutputStream(f);
			marshaller.marshal(root, fout);
			fout.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			isSuccess = false;
		} finally {
			try {
				if (fout != null) {
					fout.flush();
					fout.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		isSuccess = true;
	}

	private String handleSpace(String defaultStr) {
		if (defaultStr == null)
			return null;

		String trim = defaultStr.trim();
		if (trim.length() == 0)
			return "EMPTYSTR";
		return trim;
	}

	private boolean isValid(Constraint constraint) {
		if (constraint.isIndex() || constraint.isFK() || constraint.isPK()) {
			if (constraint.isEmptyAttrList()) {
				return false;
			}
		}

		return true;
	}

	private String arrayToString(int[] array) {
		if (array == null || array.length == 0)
			return null;

		String str = Arrays.toString(array);
		str = "(" + str.substring(1, str.length() - 1).replaceAll(" ", "") + ")";
		return str;
	}

	private int[] parseLocation(String cl) {
		if (cl == null || cl.length() == 0) {
			return null;
		}
		String[] temp = cl.split(",");
		int[] col = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			// relation coordinate is half of the entity coordinate
			col[i] = Integer.parseInt(temp[i]) / 2;
		}
		return col;
	}

	private String getUUID() {
		return "{" + UUID.randomUUID().toString() + "}+00000000";
	}

	private void setDrawData(Model model) {
		int ENTITY_PER_ROW = 5;

		model.setSubjectAreaGroups(new SubjectAreaGroups());
		SubjectArea subjectArea = new SubjectArea();
		model.getSubjectAreaGroups().getSubjectAreaList().add(subjectArea);
		String name = "<Main Subject Area>";
		subjectArea.setId(getUUID());
		subjectArea.setName(name);

		subjectArea.setSubjectAreaProps(new SubjectAreaProps());
		subjectArea.getSubjectAreaProps().setName(new SubjectAreaProps.Name());
		subjectArea.getSubjectAreaProps().getName().setValue(name);

		subjectArea.setStoredDisplayGroups(new StoredDisplayGroups());

		StoredDisplay storedDisplay = new StoredDisplay();
		storedDisplay.setId(getUUID());
		storedDisplay.setName("Display1");
		subjectArea.getStoredDisplayGroups().getStoredDisplay().add(storedDisplay);

		DrawingObjectEntityGroups groups = new DrawingObjectEntityGroups();
		storedDisplay.setDrawingObjectEntityGroups(groups);

		Map<String, String> locationMap = new HashMap<String, String>();
		int idx = 1;
		int row = 1;
		for (Entry<String, Entity> entry : entityMap.entrySet()) {
			if (idx > ENTITY_PER_ROW) {
				idx = 1;
				row++;
			}
			String id = entry.getValue().getId();

			ReferencedEntity entity = new ReferencedEntity();
			entity.setValue(id);
			subjectArea.getSubjectAreaProps().getReferencedEntities().add(entity);

			DrawingObjectEntity dEntity = new DrawingObjectEntity();
			dEntity.setId(getUUID());
			dEntity.setName(entry.getValue().getName());

			dEntity.setDrawingObjectEntityProps(new DrawingObjectEntityProps());

			dEntity.getDrawingObjectEntityProps().setDoEntityHeightAutoResizeable(
					new DOEntityHeightAutoResizeable());
			dEntity.getDrawingObjectEntityProps().getDoEntityHeightAutoResizeable().setValue(
					Boolean.TRUE.toString());
			dEntity.getDrawingObjectEntityProps().setDoEntityWidthAutoResizeable(
					new DOEntityWidthAutoResizeable());
			dEntity.getDrawingObjectEntityProps().getDoEntityWidthAutoResizeable().setValue(
					Boolean.TRUE.toString());

			dEntity.getDrawingObjectEntityProps().setDoReferenceObject(new DOReferenceObject());
			dEntity.getDrawingObjectEntityProps().getDoReferenceObject().setValue(id);

			dEntity.getDrawingObjectEntityProps().setDolocation(new DOLocation());
			String location = row * 500 + "," + idx * 500;
			dEntity.getDrawingObjectEntityProps().getDolocation().setValue("(" + location + ",0,0)");
			groups.getDrawingObjectEntity().add(dEntity);

			locationMap.put(id, location);

			idx++;
		}

		DrawingObjectRelationshipGroups shipGroups = new DrawingObjectRelationshipGroups();
		storedDisplay.setDrawingObjectRelationshipGroups(shipGroups);
		for (Entry<String, Relationship> entry : relationshipMap.entrySet()) {
			String relId = entry.getKey();
			Relationship relEntity = entry.getValue();

			ReferencedEntity entity = new ReferencedEntity();
			entity.setValue(relId);
			subjectArea.getSubjectAreaProps().getReferencedEntities().add(entity);

			DrawingObjectRelation relation = new DrawingObjectRelation();
			relation.setId(getUUID());
			relation.setName(relEntity.getName());
			shipGroups.getDrawingObjectRelationshipEntity().add(relation);

			relation.setDrawingObjectRelationshipProps(new DrawingObjectRelationshipProps());
			relation.getDrawingObjectRelationshipProps().setDoReferenceObject(
					new DOReferenceObject());
			relation.getDrawingObjectRelationshipProps().getDoReferenceObject().setValue(relId);

			String pId = relEntity.getRelationshipProps().getRelationshipParentEntity().getValue();
			String cId = relEntity.getRelationshipProps().getRelationshipChildEntity().getValue();

			String pl = locationMap.get(pId);
			String cl = locationMap.get(cId);

			int[] pco = parseLocation(pl);
			int[] cco = parseLocation(cl);

			int ydis = pco[0] - cco[0];
			int xdis = pco[1] - cco[1];

			boolean x = (Math.abs(xdis) > Math.abs(ydis)) ? false : true;
			//			int[] p1 = pco;
			//			int[] p2 = (x) ? new int[] { pco[0], pco[1] - (xdis / 2) }
			//					: new int[] { pco[0] - (ydis / 2), pco[1] };
			//			int[] p3 = (x) ? new int[] { cco[0], cco[1] + (xdis / 2) }
			//					: new int[] { cco[0] + (ydis / 2), cco[1] };
			//			int[] p4 = cco;

			int[] p1 = new int[] { pco[1] + 10, pco[0] + 10 };
			int[] p4 = new int[] { cco[1] + 10, cco[0] + 10 };
			int[] p2 = (x) ? new int[] { p1[0] - (xdis / 2), p1[1] } : new int[] { p1[0],
					p1[1] - (ydis / 2) };
			int[] p3 = (x) ? new int[] { p4[0] + (xdis / 2), p4[1] } : new int[] { p4[0],
					p4[1] + (ydis / 2) };

			DORelationshipPath path = new DORelationshipPath();
			path.setValue(arrayToString(p4));

			path = new DORelationshipPath();
			path.setValue(arrayToString(p3));
			relation.getDrawingObjectRelationshipProps().getDoRelationshipPathList().add(path);

			path = new DORelationshipPath();
			path.setValue(arrayToString(p2));
			relation.getDrawingObjectRelationshipProps().getDoRelationshipPathList().add(path);

			path = new DORelationshipPath();
			path.setValue(arrayToString(p1));

			relation.getDrawingObjectRelationshipProps().setDoUserControlledPath(
					new DOUserControlledPath());
			relation.getDrawingObjectRelationshipProps().getDoUserControlledPath().setValue("true");
		}
	}

	private String getLogicalAttrName(String physicalTableName, String physicalAttrName) {
		if (tablesPhysicalLogicalMap != null) {
			Map<String, List> columnPLInfo = tablesPhysicalLogicalMap.get(physicalTableName);
			if (columnPLInfo != null) {
				List logicals = columnPLInfo.get(physicalAttrName);
				return (String) logicals.get(0);
			}
		}
		return null;
	}

	private String getLogicalAttrType(String physicalTableName, String physicalAttrType) {
		if (tablesPhysicalLogicalMap != null) {
			Map<String, List> columnPLInfo = tablesPhysicalLogicalMap.get(physicalTableName);
			if (columnPLInfo != null) {
				List logicals = columnPLInfo.get(physicalAttrType);
				return (String) logicals.get(1);
			}
		}
		return null;
	}

	public void setTablePLMap(Map<String, String> tablePLMap) {
		this.tablePLMap = tablePLMap;
	}

	public void setTablesPhysicalLogicalMap(Map<String, Map<String, List>> tablesPhysicalLogicalMap) {
		this.tablesPhysicalLogicalMap = tablesPhysicalLogicalMap;
	}
}
