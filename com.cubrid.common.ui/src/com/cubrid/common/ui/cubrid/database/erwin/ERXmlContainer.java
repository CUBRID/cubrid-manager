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
package com.cubrid.common.ui.cubrid.database.erwin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinDBAttribute;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinSchemaInfo;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERXmlModelConstant;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;

/**
 * ERXMLContainer Description
 *
 * @author Jason You
 * @version 1.0 - 2012-11-20 created by Jason You
 */
public class ERXmlContainer {
	private static final Logger LOGGER = LogUtil.getLogger(ERXmlContainer.class);
	private static final int MAXCOUNT_LIMIT = 80;
	private String modelName = "";
	private ERXmlDocumentHandler handler;
	private CubridDatabase database;
	private Node subjectId;
	private Map<String, TableSchema> tableSchemas = new HashMap<String, TableSchema>();
	private Map<String, ERWinSchemaInfo> schemaInfos = new HashMap<String, ERWinSchemaInfo>();
	private Map<String, Node> nodeMap = new HashMap<String, Node>();
	private List<Node> entityList = new ArrayList<Node>();
	private Map<String, String> physicalNameMap = new HashMap<String, String>();
	private Map<String, String> columnIdMap = new HashMap<String, String>();
	private Map<String, Constraint> foreignKeyMap = new HashMap<String, Constraint>();
	private Map<String, ViewModel> viewModelMap = new HashMap<String, ERXmlContainer.ViewModel>();
	private Map<String, Integer> nameCounter = new HashMap<String, Integer>();
	private final int NONE_ACTION = 0;
	private final int PARENT_UPDATE_NOACTION = 9998;
	private final int PARENT_UPDATE_RESTRICT = 10000;
	private final int PARENT_UPDATE_CASCADE = PARENT_UPDATE_RESTRICT + 1;
	//private final int PARENT_UPDATE_NONE = NONE_ACTION;
	//private final int CHILD_UPDATE_RESTRICT = 10012;
	//private final int CHILD_UPDATE_CASCADE = CHILD_UPDATE_RESTRICT + 1;
	//private final int CHILD_UPDATE_NOACTION = 9995;
	//private final int CHILD_UPDATE_NONE = NONE_ACTION;
	private final int PARENT_DELETE_RESTRICT = 10004;
	private final int PARENT_DELETE_CASCADE = PARENT_DELETE_RESTRICT + 1;
	private final int PARENT_DELETE_NOACTION = 9999;
	//private final int PARENT_DELETE_NONE = NONE_ACTION;
	//private final int CHILD_DELETE_RESTRICT = 10016;
	//private final int CHILD_DELETE_CASCADE = CHILD_DELETE_RESTRICT + 1;
	//private final int CHILD_DELETE_NOACTION = 9995;
	//private final int CHILD_DELETE_NONE = NONE_ACTION;
	//private final int PARENT_INSERT_RESTRICT = 10020;
	//private final int PARENT_INSERT_CASCADE = PARENT_INSERT_RESTRICT + 1;
	//private final int PARENT_INSERT_NOACTION = 9997;
	//private final int PARENT_INSERT_NONE = NONE_ACTION;
	//private final int CHILD_INSERT_RESTRICT = 10008;
	//private final int CHILD_INSERT_CASCADE = CHILD_INSERT_RESTRICT + 1;
	//private final int CHILD_INSERT_NOACTION = 9994;
	//private final int CHILD_INSERT_NONE = NONE_ACTION;
	private Document doc;
	private Map<String, DBAttribute> attributeMap = new HashMap<String, DBAttribute>();
	private Map<String, Constraint> constraints = new HashMap<String, Constraint>();
	private Map<String, Constraint> relationConstrantMap = new HashMap<String, Constraint>();
	private String errMsg = "";

	public ERXmlContainer() {
	}

	/**
	 * read the input xml file and parse into ERXmlNode
	 */
	private void createTempDatabase() {
		handler = new ERXmlDocumentHandler(doc);
		List<String> filter = initFilter(subjectId);
		parseEntities(filter);
	}

	private List<String> initFilter(Node subjectId) {
		Node subjectNode = subjectId;
		if (subjectNode == null) {
			return null;
		}

		NodeList entityRef = handler.getChildNodeList(subjectNode, "Subject_AreaProps");
		if (entityRef == null || entityRef.getLength() == 0) {
			return null;
		}

		List<String> filterIds = new ArrayList<String>();
		for (int i = 0; i < entityRef.getLength(); i++) {
			Node entity = entityRef.item(i);
			if (entity.getNodeName().equals("Referenced_Entities") && entity.getFirstChild() != null) {
				filterIds.add(entity.getFirstChild().getNodeValue().trim());
			}
		}

		return filterIds;
	}

	public String getDatabaseName() {
		if (modelName.length() != 0) {
			return modelName;
		}

		NodeList modelProps = doc.getElementsByTagName("ModelProps");
		if (modelProps.getLength() == 1) {
			Node child = modelProps.item(0).getFirstChild();
			while (child != null) {
				if (child.getNodeName().equals("Name")) {
					modelName = child.getFirstChild().getNodeValue().trim();
				}

				child = child.getNextSibling();
			}
		}

		return modelName;
	}

	private boolean validateTableName(List<String> filter) {
		boolean duplicate = false;
		NodeList entities = doc.getElementsByTagName("Entity");
		Map<String, String> nameMap = new HashMap<String, String>();
		boolean useFilter = false;
		if (filter != null && filter.size() > 0) {
			useFilter = true;
		}
		int length = entities.getLength();
		List<Node> removeEntity = new ArrayList<Node>();
		for (int i = 0; i < length; i++) {
			Node entity = entities.item(i);
			String id = entity.getAttributes().getNamedItem("id").getNodeValue();
			String name = handler.getChildValueByProperty(entity, "EntityProps.Physical_Name");
			if (useFilter && filter.contains(id)) {
				this.entityList.add(entity);
				// String name = handler.getChildValueByProperty(entity, "EntityProps.Physical_Name");
				if (name == null) {
					name = entity.getAttributes().getNamedItem("Name").getNodeValue().trim();
				}
				if (nameMap.containsKey(name.toLowerCase())) {
					nameMap.put(name.toLowerCase(), nameMap.get(name.toLowerCase()) + " " + name);
					duplicate = true;
				} else {
					nameMap.put(name.toLowerCase(), name);
				}
			} else {
				if (useFilter) {
					removeEntity.add(entity);
				} else {
					entityList.add(entity);
				}
			}
		}

		for (int i = 0; i < removeEntity.size(); i++) {
			Node node = removeEntity.get(i);
			node.getParentNode().removeChild(node);
		}
		if (duplicate) {
			int counter = 1;
			StringBuilder sb = new StringBuilder(Messages.msgDuplicateTableName);
			for (Entry<String, String> entry : nameMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.length() == value.length()) {
					continue;
				}

				sb.append("\n").append(counter++);
				sb.append(".").append(value.replaceAll(" ", ","));
			}
			errMsg = sb.toString();
		}

		return duplicate;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * parse the &lt;Entity&gt; node
	 *
	 * @param nodes
	 * @param entities
	 * @param filter
	 */
	private void parseEntities(List<String> filter) {
		NodeList entities = doc.getElementsByTagName("Entity");
		if (validateList(entities, Messages.errMissingEntity)) {
			return;
		}

		if (validateTableName(filter)) {
			return;
		}

		for (int i = 0; i < entityList.size(); i++) {
			Node entity = entityList.get(i);
			if (entity.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			TableSchema tableSchema = createTableSchema(entity);
			if (tableSchema != null) {
				tableSchemas.put(tableSchema.getName(), tableSchema);
			}
		}

		createSchemaInfo();
	}

	public Map<String, TableSchema> getTableSchemas() {
		return new HashMap<String, TableSchema>(tableSchemas);
	}

	public void setTableSchemas(Map<String, TableSchema> tableSchemas) {
		this.tableSchemas = tableSchemas;
	}

	public Map<String, ERWinSchemaInfo> getSchemaInfos() {
		return new HashMap<String, ERWinSchemaInfo>(schemaInfos);
	}

	public void setSchemaInfos(Map<String, ERWinSchemaInfo> schemaInfos) {
		this.schemaInfos = schemaInfos;
	}

	/**
	 * parse the entity attribute to create the table schema info.
	 * &lt;Attribute_Groups&gt;
	 *
	 * @param childNodes
	 */
	private void createSchemaInfo() {
		for (String tableName : tableSchemas.keySet()) {
			ERWinSchemaInfo schemaInfo = new ERWinSchemaInfo();
			schemaInfo.setType("user");
			schemaInfo.setClassname(tableName);
			schemaInfos.put(tableName, schemaInfo);
		}

		initCache();
		parseKeyGroupGroups();

		parseEntityProps();
		parseRelationGroups();
		parseAttributes();

		createSchemaDDL();
	}

	private void initCache() {
		NodeList attributes = doc.getElementsByTagName("Attribute");
		handler.initAttributeCache(attributes);
	}

	private void createSchemaDDL() {
		DatabaseInfo info = database.getDatabaseInfo();
		if (info == null) {
			LOGGER.error("The databaseInfo is a null.");
			return;
		}

		WrappedDatabaseInfo wrappedDatabaseInfo = new WrappedDatabaseInfo(
				info.getDbName(), info.getServerInfo());

		ERXmlDatabaseInfoMapper.addWrappedDatabaseInfo(info, wrappedDatabaseInfo);
		Map<String, SchemaInfo> dbSchemaInfos = new HashMap<String, SchemaInfo>();
		Collection<ERWinSchemaInfo> erwinSchemas = schemaInfos.values();
		for (ERWinSchemaInfo erwinSchema : erwinSchemas) {
			SchemaInfo schemaInfo = (SchemaInfo) erwinSchema;
			dbSchemaInfos.put(schemaInfo.getClassname(), schemaInfo);
		}
		wrappedDatabaseInfo.addSchemaInfos(dbSchemaInfos);
		wrappedDatabaseInfo.addTableSchemas(tableSchemas);

		SchemaDDL ddl = new SchemaDDL(null, wrappedDatabaseInfo);
		for (String tableName : tableSchemas.keySet()) {
			TableSchema tableSchema = tableSchemas.get(tableName);
			SchemaInfo schemaInfo = schemaInfos.get(tableName);
			if (schemaInfo == null) {
				continue;
			}

			String strDDL = "";
			if (schemaInfo.getVirtual().equals(ClassType.VIEW.getText())) {
				strDDL = createViewSchema(schemaInfo);
			} else {
				strDDL = ddl.getSchemaDDL(schemaInfo);
			}

			tableSchema.setSchemaInfo(strDDL);
		}
	}

	private String createViewSchema(SchemaInfo schemaInfo) {
		ViewModel model = viewModelMap.get(schemaInfo.getClassname());
		if (model != null) {
			return model.returnViewDDL();
		}

		return "";
	}

	/**
	 * create a temporary SchemaInfo for compared usage
	 *
	 * @param entity
	 * @return
	 */
	private TableSchema createTableSchema(Node entity) {
		String physicalName = handler.getChildValueByProperty(entity,
				"EntityProps.Physical_Name");
		String name = entity.getAttributes().getNamedItem("Name").getNodeValue().trim();
		String id = entity.getAttributes().getNamedItem("id").getNodeValue().trim();
		nodeMap.put(id, entity);
		TableSchema tableSchema = new TableSchema("", "");
		if (physicalName != null) {
			physicalNameMap.put(id, physicalName);
			tableSchema.setName(physicalName);
		} else {
			tableSchema.setName(name);
		}

		return tableSchema;
	}

	/**
	 * parse &lt;Key_Group&gt; node
	 *
	 * @param schemaInfo
	 * @param schema
	 * @param property
	 */
	private List<Constraint> parseKeyGroupGroups() {
		NodeList nodeList = doc.getElementsByTagName("Key_Group");

		// create constraint with name key
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node keyGroup = nodeList.item(i);

			// skip the default Keys without any column definition
			NodeList columns = handler.getChildNodeList(keyGroup,
					"Key_Group_Member_Groups");
			if (columns == null || columns.getLength() == 0) {
				continue;
			}

			Constraint constraint = new Constraint(false);
			if (keyGroup.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			String name = keyGroup.getAttributes().getNamedItem("Name").getNodeValue().trim();
			String id = keyGroup.getAttributes().getNamedItem("id").getNodeValue().trim();
			nodeMap.put(id, keyGroup);

			String physicalName = handler.getChildValueByProperty(keyGroup,
					"Key_GroupProps.Physical_Name");
			String keyGroupType = handler.getChildValueByProperty(keyGroup,
					"Key_GroupProps.Key_Group_Type");
			String relationShipId = handler.getChildValueByProperty(keyGroup,
					"Key_GroupProps.Key_Group_Relationship_Pointer");
			physicalNameMap.put(id, physicalName);

			// get the schemaInfo instance to set the constraint
			Node pNode = findPNode(keyGroup, "Entity");
			String pId = pNode.getAttributes().getNamedItem("id").getNodeValue().trim();
			nodeMap.put(pId, pNode);
			String tableName = physicalNameMap.get(pId);
			if (tableName == null) {
				tableName = pNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
			}
			if (physicalName == null) {
				physicalName = "I" + keyGroupType + "_" + tableName;
				int count = getNameCount(physicalName);
				physicalName = physicalName + "_" + count;
			}
			constraint.setName((physicalName != null) ? physicalName : name);

			SchemaInfo schemaInfo = schemaInfos.get(tableName);
			schemaInfo.addConstraint(constraint);
			if (keyGroupType != null && keyGroupType.length() > 0) {
				if (keyGroupType.equals("PK")) {
					constraint.setType(Constraint.ConstraintType.PRIMARYKEY.getText());
				} else if (keyGroupType.indexOf("IF") != -1) {
					constraint.setType(ConstraintType.FOREIGNKEY.getText());
				} else if (keyGroupType.indexOf("AK") != -1) {
					constraint.setType(Constraint.ConstraintType.UNIQUE.getText());
				} else if (keyGroupType.indexOf("IE") != -1) {
					constraint.setType(Constraint.ConstraintType.INDEX.getText());
				}
			}

			constraints.put(id, constraint);
			if (relationShipId != null) {
				relationConstrantMap.put(relationShipId, constraint);
			}
		}

		// add attributes of each constraint
		final NodeList keyGroupMememberColumns = doc.getElementsByTagName("Key_Group_Member_Column");
		List<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < keyGroupMememberColumns.getLength(); i++) {
			nodes.add(keyGroupMememberColumns.item(i));
		}
		if (keyGroupMememberColumns.getLength() > MAXCOUNT_LIMIT) {
			int colPerTask = keyGroupMememberColumns.getLength()
					/ Runtime.getRuntime().availableProcessors();
			int taskcount = keyGroupMememberColumns.getLength() / colPerTask;
			int lastOffset = keyGroupMememberColumns.getLength() % colPerTask;
			if (lastOffset > 0) {
				taskcount++;
			}

			CountDownLatch latch = new CountDownLatch(taskcount);

			Executor threadPool = Executors.newCachedThreadPool();
			ParseKeyGroupColumnTask task = null;
			for (int i = 0; i < taskcount; i++) {
				List<Node> copy = new ArrayList<Node>();
				Collections.copy(nodes, copy);
				if (i == taskcount - 1) {
					task = new ParseKeyGroupColumnTask(copy, i * colPerTask, lastOffset);
				} else {
					task = new ParseKeyGroupColumnTask(copy, i * colPerTask, colPerTask);
				}
				task.setLatch(latch);
				threadPool.execute(task);
			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			parseKeyGroupColumn(nodes, 0, keyGroupMememberColumns.getLength());
		}

		return new ArrayList<Constraint>(constraints.values());
	}

	private class ParseKeyGroupColumnTask implements Runnable {
		private int start;
		private int length;
		private List<Node> nodeList;
		private CountDownLatch latch;

		public ParseKeyGroupColumnTask(List<Node> nodeList, int start, int length) {
			this.start = start;
			this.length = length;
			this.nodeList = nodeList;
		}

		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		public void run() {
			try {
				parseKeyGroupColumn(nodeList, start, length);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (latch != null) {
					latch.countDown();
				}
			}
		}
	}

	private void parseKeyGroupColumn(List<Node> nodeList, int begin, int length) {
		int max = 3;
		for (int i = begin; i < begin + length; i++) {
			try {
				Node keyColumnNode = nodeList.get(i);
				if (keyColumnNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Node pNode = findPNode(keyColumnNode, "Key_Group");
				String id = pNode.getAttributes().getNamedItem("id").getNodeValue().trim();
				String columnId = keyColumnNode.getFirstChild().getNodeValue().trim();
				Constraint constraint = constraints.get(id);
				Node attributeNode = getNodeById(columnId, "Attribute");
				if (attributeNode == null) {
					continue;
				}

				String physicalName = handler.getChildValueByProperty(
						attributeNode, "AttributeProps.Physical_Name");
				if (physicalName == null ) {
					physicalName = handler.getChildValueByProperty(
							attributeNode, "AttributeProps.Name");
				}
				if (physicalName == null ) {
					physicalName = attributeNode.getAttributes().getNamedItem("Name").getNodeValue();
				}
				// Other attributes
				if (constraint.getType().equals(ConstraintType.PRIMARYKEY.getText())
						|| constraint.getType().equals(ConstraintType.FOREIGNKEY.getText())) {
					constraint.addAttribute(physicalName);
				} else {
					Node keyGroupSortOrder = keyColumnNode.getParentNode().getFirstChild().getNextSibling();
					while (keyGroupSortOrder != null) {
						if (!keyGroupSortOrder.getNodeName().equals("Key_Group_Sort_Order")) {
							keyGroupSortOrder = keyGroupSortOrder.getNextSibling();
							continue;
						}
						String order = keyGroupSortOrder.getFirstChild().getNodeValue().trim();
						if (StringUtil.isNotEmpty(order)) {
							physicalName = physicalName + " " + order;
						}
						break;
					}
					constraint.addRule(physicalName);
				}
			} catch (Exception e) {
				max--;
				if (max != 0) {
					i--;
				} else {
					max = 3;
				}
				continue;
			}
		}

	}

	private void parseAttributes() {
		NodeList attributes = doc.getElementsByTagName("Attribute");
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			ERWinDBAttribute erwinAttribute = new ERWinDBAttribute();
			String attrId = attribute.getAttributes().getNamedItem("id").getNodeValue().trim();
			String attrName = attribute.getAttributes().getNamedItem("Name").getNodeValue().trim();//logical name
			erwinAttribute.setLogicalName(attrName);
			attributeMap.put(attrId, erwinAttribute);
			String physicalName = "";
			String fkAttrName = "";
			int attributeType = -1;
			boolean duplicate = false;

			Node pNode = findPNode(attribute, "Entity");
			if (pNode == null) {
				continue;
			}

			String pId = pNode.getAttributes().getNamedItem("id").getNodeValue().trim();
			String tableName = physicalNameMap.get(pId);
			if (tableName == null) {
				tableName = pNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
			}
			ERWinSchemaInfo schemaInfo = schemaInfos.get(tableName);

			for (int ii = 0; ii < attribute.getChildNodes().getLength(); ii++) {
				Node tempNode = attribute.getChildNodes().item(ii);
				if (tempNode.getNodeName().equals("AttributeProps")) {
					physicalName = handler.getChildValueByProperty(tempNode,
							"Physical_Name");
					if (physicalName == null) {
						physicalName = attrName;
					}
					DBAttribute duplicateAttr = schemaInfo.getDBAttributeByName(
							physicalName, false);
					if (duplicateAttr != null) {
						duplicate = true;
						break;
					}
					erwinAttribute.setName(physicalName);
					columnIdMap.put(attrId, physicalName);
					Node attributePropsChild = tempNode.getFirstChild();
					while (attributePropsChild != null) {
						String attributeName = attributePropsChild.getNodeName();
						if (attributeName.equals("Datatype")) {
							String dataType = attributePropsChild.getFirstChild().getNodeValue().trim();
							if (dataType.startsWith(DataType.getUpperEnumType())) {//convert to show type
								dataType = dataType.replaceFirst(DataType.getUpperEnumType(),
										DataType.getLowerEnumType());
							}
							String enumeration = DataType.getEnumeration(dataType);
							if (!StringUtil.isEmpty(enumeration)) {
								dataType = DataType.getUpperEnumType();
								erwinAttribute.setEnumeration(enumeration);
							}
							erwinAttribute.setType(dataType);
							erwinAttribute.setDefault(updateData(dataType,
									erwinAttribute.getDefault()));
						} else if (attributeName.equals("Type")) {
							attributeType = Integer.parseInt(attributePropsChild.getFirstChild().getNodeValue().trim());
						} else if (attributeName.equals("Null_Option")) {
							int value = Integer.parseInt(attributePropsChild.getFirstChild().getNodeValue().trim());
							switch (value) {
							case 1:
							case 8:
								erwinAttribute.setNotNull(true);
								break;
							default:
								break;
							}
						} else if (attributeName.equals("Default")) {
							String id = attributePropsChild.getFirstChild().getNodeValue().trim();
							Node defaultValueNode = getNodeById(id, "Default_Value");
							if (defaultValueNode == null) {
								attributePropsChild = attributePropsChild.getNextSibling();
								continue;
							}
							String value = handler.getChildValueByProperty(
									defaultValueNode,
									"Default_ValueProps.Server_Value");
							if (erwinAttribute.getType() != null) {
								value = updateData(erwinAttribute.getType(), value);
							}
							erwinAttribute.setDefault(value);
						} else if (attributeName.equals("Identity_Seed")) {
							SerialInfo serialInfo = new SerialInfo();
							serialInfo.setIncrementValue(attributePropsChild.getFirstChild().getNodeValue().trim());
							erwinAttribute.setAutoIncrement(serialInfo);
						} else if (attributeName.equals("Parent_Domain")) {
							String id = attributePropsChild.getFirstChild().getNodeValue();
							String value = handler.getNodeChildValueById(
									"Domain", id, "DomainProps.Datatype");
							if (value == null) {
								// if it has reference-id (DomainProps.Parent_Domain)
								String refId = handler.getNodeChildValueById(
										"Domain", id, "DomainProps.Parent_Domain");
								value = handler.getNodeChildValueById(
										"Domain", refId, "DomainProps.Datatype");
							}
							if (StringUtil.isEmpty(erwinAttribute.getType())) {
								erwinAttribute.setType(DataType.getRealType(StringUtil.toUpper(value)));
							}
						} else if (attributeName.equals("Parent_Relationship")) {
							String id = attributePropsChild.getFirstChild().getNodeValue().trim();
							Constraint relType = foreignKeyMap.get(id);
							if (relType != null) {
								boolean isPk = relType.getType().equals(Constraint.ConstraintType.PRIMARYKEY.getText());
								boolean isFk = relType.getType().equals(Constraint.ConstraintType.FOREIGNKEY.getText());
								if (isPk || isFk) {
									erwinAttribute.setNotNull(true);
									erwinAttribute.setUnique(true);
								}
							}
						} else if (attributeName.equals("Physical_Name")) {
							physicalName = attributePropsChild.getFirstChild().getNodeValue().trim();
						} else if (attributeName.equals("Logical_Datatype")) {
							String logicalDataType = attributePropsChild.getFirstChild().getNodeValue().trim();
							erwinAttribute.setLogicalDataType(logicalDataType);
						} else if (attributeName.equals("Parent_Attribute")) {
							String id = attributePropsChild.getFirstChild().getNodeValue().trim();

							Node fkAttrNode = getNodeById(id, "Attribute");
							if (fkAttrNode == null) {
								attributePropsChild = attributePropsChild.getNextSibling();
								continue;
							}
							fkAttrName = handler.getChildValueByProperty(
									fkAttrNode, "AttributeProps.Physical_Name");
							if (fkAttrName == null) {
								fkAttrName = fkAttrNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
							}
							Node fkTableNode = findPNode(fkAttrNode, "Entity");
							String fkTableId = fkTableNode.getAttributes().getNamedItem("id").getNodeValue().trim();
							String fkTableName = physicalNameMap.get(fkTableId);
							erwinAttribute.setInherit(fkTableName);

							// SchemaInfo schemaInfo = schemaInfos.get(tName);
							// schemaInfo.addSuperClass(tableName);
						} else if (attributeName.equals("View_Expression")) {
							fkAttrName = attributePropsChild.getFirstChild().getNodeValue().trim();
						}
						attributePropsChild = attributePropsChild.getNextSibling();
					}
				}
			}
			if (duplicate) {
				continue;
			}

			if (attributeType != ERXmlModelConstant.ATTRIBUTE_TYPE_VIEW) {
				if (attributeType == ERXmlModelConstant.ATTRIBUTE_TYPE_PK) {
					erwinAttribute.setUnique(true);
					erwinAttribute.setNotNull(true);
				}
				erwinAttribute.setInherit(tableName);
				schemaInfo.addAttribute(erwinAttribute);
			} else {
				String viewName = tableName;
				if (viewName == null) {
					viewName = pNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
				}
				ViewModel model = viewModelMap.get(viewName);
				String pTable = erwinAttribute.getInherit();
				if (pTable == null) {
					pTable = "DEFAULT";
				}
				model.addTableColumnAlias(pTable, fkAttrName, erwinAttribute.getName());
			}
		}

	}

	private String updateData(String dataType, String value) {
		// do not need to wrap string with single quote char because of already having quote char.
		// <Default_Value id="xxx" Name="DEFAULT_5">-<Default_ValueProps><Name>DEFAULT_5</Name><Usage_Count>0</Usage_Count><Server_Value>'M'</Server_Value><Physical_Name>DEFAULT_5</Physical_Name><LogicalDefault_Value>'M'</LogicalDefault_Value><Gen_SQL_92_Format>true</Gen_SQL_92_Format></Default_ValueProps></Default_Value>
//		if (value != null) {
//			if (!(dataType.toLowerCase().equals("integer")
//					|| dataType.toLowerCase().equals("bigint")
//					|| dataType.toLowerCase().startsWith("numeric"))) {
//				value = "'" + value + "'";
//			}
//		}
		return value;
	}

	/**
	 * validate the input NodeList if it is null or Empty and show the error
	 * messages;
	 *
	 * @param list
	 * @param error
	 * @return
	 */
	private boolean validateList(NodeList list, String error) {
		if (list == null || list.getLength() == 0) {
			CommonUITool.openErrorBox(error);
			return true;
		}
		return false;
	}

	/**
	 * parse the &lt;EntityProps&gt; node
	 *
	 * @param schema
	 * @param database
	 * @param property
	 */
	private void parseEntityProps() {
		NodeList entityProps = doc.getElementsByTagName("EntityProps");

		for (int i = 0; i < entityProps.getLength(); i++) {
			Node node = entityProps.item(i);
			Node childNode = node.getFirstChild();
			Node pNode = findPNode(childNode, "Entity");
			if (pNode == null) {
				continue;
			}
			String name = pNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
			if (name != null) {
				name = name.trim();
			}
			String pId = pNode.getAttributes().getNamedItem("id").getNodeValue().trim();
			String physicalName = physicalNameMap.get(pId);

			int type = 0;
			while (childNode != null) {
				if (childNode.getNodeName().equals("Type")) {
					if (childNode.getFirstChild().getNodeValue().trim() != null) {
						type = Integer.parseInt(childNode.getFirstChild().getNodeValue().trim());
					}
					if (type == ERXmlModelConstant.ENTITYTYPE_VIEW_INT) {
						ViewModel model = new ViewModel();
						model.setViewName(name);
						viewModelMap.put(name, model);
					}
				} else if (childNode.getNodeName().equals("Physical_Name")) {
					physicalName = childNode.getFirstChild().getNodeValue().trim();
				} else if (childNode.getNodeName().equals("View_With_Check")) {
					ViewModel model = viewModelMap.get(name);
					model.setWithCheck(true);
				} else if (childNode.getNodeName().equals("View_Where")) {
					ViewModel model = viewModelMap.get(name);
					model.setWhereSql(childNode.getFirstChild().getNodeValue().trim());
				} else if (childNode.getNodeName().equals("View_Distinct")) {
					ViewModel model = viewModelMap.get(name);
					model.setDistinct(Boolean.parseBoolean(childNode.getFirstChild().getNodeValue().trim()));
				} else if (childNode.getNodeName().equals("View_Group_By")) {
					ViewModel model = viewModelMap.get(name);
					model.setGroupBySql(childNode.getFirstChild().getNodeValue().trim());
				} else if (childNode.getNodeName().equals("View_Having")) {
					ViewModel model = viewModelMap.get(name);
					model.setHavingSql(childNode.getFirstChild().getNodeValue().trim());
				}

				childNode = childNode.getNextSibling();
			}

			if (physicalName != null) {
				if (type == ERXmlModelConstant.ENTITYTYPE_VIEW_INT) {
					ViewModel model = viewModelMap.get(name);
					model.setViewName(physicalName);
					viewModelMap.remove(name);
					viewModelMap.put(physicalName, model);
				}
			} else {
				physicalName = name;
			}
			ERWinSchemaInfo schema = schemaInfos.get(physicalName);
			schema.setVirtual((type != ERXmlModelConstant.ENTITYTYPE_VIEW_INT) ? ClassType.NORMAL.getText() : ClassType.VIEW.getText());
			schema.setLogicalName(name);
		}
	}

	private String text(int type) {
		switch (type) {
		case PARENT_DELETE_CASCADE:
		case PARENT_UPDATE_CASCADE:
			return "CASCADE";

		case PARENT_DELETE_RESTRICT:
		case PARENT_UPDATE_RESTRICT:
			return "RESTRICT";

		case PARENT_DELETE_NOACTION:
		case PARENT_UPDATE_NOACTION:
			return "NO ACTION";

		case NONE_ACTION:
			return "SET NULL";

		default:
			return "NO ACTION";
		}
	}

	private void parseRelationGroups() {
		NodeList relations = doc.getElementsByTagName("Relationship");
		if (relations == null || relations.getLength() == 0) {
			return;
		}

		for (int i = 0; i < relations.getLength(); i++) {
			Node node = relations.item(i);
			String id = node.getAttributes().getNamedItem("id").getNodeValue().trim();
			Constraint constraint = null;
			if (relationConstrantMap.containsKey(id)) {
				constraint = relationConstrantMap.get(id);
			}
			if (constraint == null) {
				continue;
			}

			String type = handler.getChildValueByProperty(node,
					"RelationshipProps.Type");

			String parentId = handler.getChildValueByProperty(node,
					"RelationshipProps.Relationship_Parent_Entity");
			Node pnode = handler.getNodeById("Entity", parentId);
			if (pnode == null) {
				continue;
			}
			String pName = pnode.getAttributes().getNamedItem("Name").getNodeValue().trim();
			String pId = pnode.getAttributes().getNamedItem("id").getNodeValue().trim();
			String pPhysicalName = physicalNameMap.get(pId);

			String childId = handler.getChildValueByProperty(node,
					"RelationshipProps.Relationship_Child_Entity");
			Node childNode = handler.getNodeById("Entity", childId);
			if (childNode == null) {
				continue;
			}
			String childName = childNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
			String childPhysicalName = physicalNameMap.get(childId);

			if (type != null && type.equals(ERXmlModelConstant.REL_VIEW)) {
				String physicalName = handler.getChildValueByProperty(node,
						"RelationshipProps.Physical_Name");

				ViewModel model = viewModelMap.get((childPhysicalName != null) ? childPhysicalName
						: childName);
				if (model == null) {
					model = new ViewModel();
					viewModelMap.put(childName, model);
				}
				model.addTableAlias(
						(pPhysicalName != null) ? pPhysicalName : pName,
						(physicalName == null) ? (pPhysicalName != null) ? pPhysicalName
								: pName
								: physicalName);
				continue;
			}

			if (constraint.getType() != null)
				constraint.setType(ConstraintType.FOREIGNKEY.getText());
			// XML ID -> Constraint(FK)
			foreignKeyMap.put(id, constraint);

			Node tableNode = handler.getNodeById("Entity", parentId);
			if (tableNode == null) {
				continue;
			}
			String tableId = tableNode.getAttributes().getNamedItem("id").getNodeValue().trim();
			String tableName = physicalNameMap.get(tableId);

			if (tableName == null) {
				tableName = tableNode.getAttributes().getNamedItem("Name").getNodeValue().trim();
			}
			constraint.addRule("REFERENCES " + tableName);
		}

		setRelationType("Relationship_Parent_Update_Rule", true);
		setRelationType("Relationship_Parent_Delete_Rule", false);
	}

	private void setRelationType(String tagName, boolean update) {
		NodeList relUpdates = doc.getElementsByTagName(tagName);
		if (relUpdates != null) {
			for (int i = 0; i < relUpdates.getLength(); i++) {
				Node relUpdate = relUpdates.item(i);
				int value = 0;
				if (relUpdate.getFirstChild().getNodeValue().trim() != null) {
					value = Integer.parseInt(relUpdate.getFirstChild().getNodeValue().trim());
				}
				Node pNode = findPNode(relUpdate, "Relationship");
				if (pNode == null) {
					continue;
				}
				String id = pNode.getAttributes().getNamedItem("id").getNodeValue().trim();

				Constraint constraint = foreignKeyMap.get(id);
				if (constraint == null) {
					continue;
				}

				if (update) {
					constraint.addRule("ON UPDATE " + text(value));
				} else {
					constraint.addRule("ON DELETE " + text(value));
				}
			}
		}
	}

	private Node findPNode(Node node, String nodeTag) {
		Node pNode = node.getParentNode();
		while (pNode != null) {
			String name = pNode.getNodeName();
			if (nodeTag.equals(name)) {
				break;
			}
			pNode = pNode.getParentNode();
		}

		return pNode;
	}

	private int getNameCount(String name) {
		if (name == null) {
			return 0;
		}

		if (nameCounter.containsKey(name)) {
			int count = nameCounter.get(name);
			nameCounter.put(name, ++count);
			return count;
		} else {
			nameCounter.put(name, 1);
			return 1;
		}
	}

	private Node getNodeById(String id, String tagName) {
		if (nodeMap.containsKey(id)) {
			return nodeMap.get(id);
		}

		Node node = handler.getNodeById(tagName, id);
		if (node != null) {
			nodeMap.put(id, node);
		}

		return node;
	}

	public void parse(CubridDatabase database, Document document) {
		parse(database, document, null);
	}

	public void parse(CubridDatabase database, Document document, Node subjectId) {
		this.doc = document;
		this.database = database;
		this.subjectId = subjectId;
		createTempDatabase();
	}

	class RelationType {
		int updateType;
		int insertType;
		int deleteType;
		SchemaInfo parent;

		public int getUpdateType() {
			return updateType;
		}

		public void setUpdateType(int updateType) {
			this.updateType = updateType;
		}

		public String getInsertType() {
			return text(insertType);
		}

		public void setInsertType(int insertType) {
			this.insertType = insertType;
		}

		public String getDeleteType() {
			return text(deleteType);
		}

		public void setDeleteType(int deleteType) {
			this.deleteType = deleteType;
		}
	}

	class ViewModel {
		private Map<String, List<String>> tableColumnsMap = new HashMap<String, List<String>>();
		private Map<String, Map<String, String>> tableColumnAliasMap = new HashMap<String, Map<String, String>>();
		private Map<String, String> tableAlias = new HashMap<String, String>();
		private String viewName;
		private String whereSql;
		private String groupBySql;
		private String havingSql;
		private boolean withCheck;
		private boolean distinct = false;

		public ViewModel() {
			tableColumnAliasMap.put("DEFAULT", new HashMap<String, String>());
		}

		public String getViewName() {
			return viewName;
		}

		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		public void addTableColumn(String table, String column) {
			if (!validate(table, column)) {
				return;
			}

			if (!tableColumnsMap.containsKey(table)) {
				tableColumnsMap.put(table, new ArrayList<String>());
			}

			List<String> tmp = tableColumnsMap.get(table);
			tmp.add(column);
		}

		public void addTableAlias(String table, String alias) {
			if (!validate(table, alias)) {
				return;
			}

			tableAlias.put(table, alias);
		}

		public void addTableColumnAlias(String table, String column, String alias) {
			if (!validate(table, column, alias)) {
				return;
			}

			if (!tableColumnAliasMap.containsKey(table)) {
				tableColumnAliasMap.put(table, new HashMap<String, String>());
			}

			Map<String, String> tmp = tableColumnAliasMap.get(table);
			tmp.put(column, alias);
		}

		public boolean validate(String... strs) {
			if (strs == null) {
				return false;
			}

			for (String str : strs) {
				if (StringUtil.isEmpty(str)) {
					return false;
				}
			}

			return true;
		}

		public String getWhereSql() {
			return whereSql;
		}

		public void setWhereSql(String whereSql) {
			this.whereSql = whereSql;
		}

		public String getGroupBySql() {
			return groupBySql;
		}

		public void setGroupBySql(String groupBySql) {
			this.groupBySql = groupBySql;
		}

		public String getHavingSql() {
			return havingSql;
		}

		public void setHavingSql(String havingSql) {
			this.havingSql = havingSql;
		}

		public boolean isWithCheck() {
			return withCheck;
		}

		public void setWithCheck(boolean withCheck) {
			this.withCheck = withCheck;
		}

		public boolean isDistinct() {
			return distinct;
		}

		public void setDistinct(boolean distinct) {
			this.distinct = distinct;
		}

		public String returnViewDDL() {
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE VIEW ").append(QuerySyntax.escapeKeyword(viewName));

			StringBuilder viewColumn = new StringBuilder("(");

			// SELECT
			StringBuilder select = new StringBuilder("SELECT ");
			if (distinct) {
				select.append("DISTINCT ");
			}
			if (tableColumnAliasMap.size() > 1) {
				for (Entry<String, Map<String, String>> entry : tableColumnAliasMap.entrySet()) {
					String tableName = entry.getKey();
					if (tableName.length() == 0) {
						continue;
					}

					if (tableName.equals("DEFAULT")) {
						tableName = "";
					}

					String aliasTable = tableAlias.get(tableName);
					if (aliasTable == null || aliasTable.length() == 0) {
						aliasTable = tableName;
					}

					Map<String, String> columnAlias = entry.getValue();
					if (columnAlias == null) {
						continue;
					}

					for (Entry<String, String> columnPair : columnAlias.entrySet()) {
						viewColumn.append(QuerySyntax.escapeKeyword(columnPair.getValue())).append(",");
						if (aliasTable.length() == 0) {
							select.append(QuerySyntax.escapeKeyword(columnPair.getKey())).append(",");
						} else {
							select.append(QuerySyntax.escapeKeyword(aliasTable)).append(".");
							select.append(QuerySyntax.escapeKeyword(columnPair.getKey())).append(",");
						}
					}
				}
				if (viewColumn.lastIndexOf(",") != -1) {
					viewColumn.deleteCharAt(viewColumn.lastIndexOf(","));
				}
				if (select.lastIndexOf(",") != -1) {
					select.deleteCharAt(select.lastIndexOf(","));
				}

				// FROM
				select.append(" FROM ");
				if (tableAlias.size() > 0) {
					for (Entry<String, String> entry : tableAlias.entrySet()) {
						select.append(entry.getKey());
						if (entry.getValue() != null && entry.getValue().length() != 0) {
							String escapedTableName = QuerySyntax.escapeKeyword(entry.getValue());
							select.append(" ").append(escapedTableName).append(",");
						}
					}
				}
				select.deleteCharAt(select.lastIndexOf(","));

				viewColumn.append(") ");

				sb.append(viewColumn).append(" AS \n").append(select);
			}
			if (whereSql != null && whereSql.length() != 0) {
				sb.append("\n WHERE ").append(whereSql).append("\n");
			}
			if (groupBySql != null && groupBySql.length() != 0) {
				sb.append(" GROUP BY ").append(groupBySql).append("\n");
			}
			if (havingSql != null && havingSql.length() != 0) {
				sb.append(" HAVING ").append(havingSql).append("\n");
			}
			if (withCheck) {
				sb.append(" WITH CHECK OPTION ").append("\n");
			}

			return sb.toString();
		}
	}
}
