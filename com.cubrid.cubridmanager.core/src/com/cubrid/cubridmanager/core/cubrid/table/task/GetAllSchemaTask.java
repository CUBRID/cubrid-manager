/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.cubrid.table.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;

/**
 * Get all schema informations via JDBC (except system schemas)
 *
 * @author Isaiah Choe
 * @version 1.0 - 2009-6-16 created by Isaiah Choe
 */
public class GetAllSchemaTask extends
		JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(GetAllSchemaTask.class);

	private static final String VIRTUAL_NORMAL = "normal";
	private static final String VIRTUAL_VIEW = "view";
	private Map<String, SchemaInfo> schemas = null;
	private Map<String, SchemaComment> comments = null;
	/*Is load collation information*/
	private volatile boolean isNeedCollationInfo = true;
	/*It may be null*/
	private IProgressMonitor monitor;

	static final Map<Integer, String> FOREIGN_KEY_ACTION_MAP = new HashMap<Integer, String>();
	static {
		FOREIGN_KEY_ACTION_MAP.put(0, "CASCADE");
		FOREIGN_KEY_ACTION_MAP.put(1, "RESTRICT");
		FOREIGN_KEY_ACTION_MAP.put(2, "SET NULL");
		FOREIGN_KEY_ACTION_MAP.put(3, "NO ACTION");
	}

	public GetAllSchemaTask(DatabaseInfo dbInfo) {
		super("GetAllSchemaTask", dbInfo);
	}

	public GetAllSchemaTask(DatabaseInfo dbInfo, IProgressMonitor monitor) {
		super("GetAllSchemaTask", dbInfo);
		this.monitor = monitor;
	}

	/**
	 * Execute the tasks
	 */
	public void execute() {
		boolean isAutocommit = false;

		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return;
			}
			/*Check is canceled*/
			if (monitor != null && monitor.isCanceled()) {
				setCancel(true);
				setErrorMsg("Current user canceled.");
				return;
			}
			schemas = new HashMap<String, SchemaInfo>();

			// [TOOLS-2425]Support shard broker
			if (databaseInfo != null && databaseInfo.isShard()) {
				isAutocommit = connection.getAutoCommit();
				connection.setAutoCommit(true);
			}
			// get table/view information
			getTableInfo();
			
			// get super class information
			getSuperClassInfo();
			// get auto increment information from db_serial table, which is a system table accessed by all users
			getAutoIncrementInfo();
			
			/*Check is canceled*/
			if (monitor != null && monitor.isCanceled()) {
				setCancel(true);
				setErrorMsg("Current user canceled.");
				return;
			}
			//get set(object) type information from db_attr_setdomain_elm view
			getTypeInfo();
			//get pk, fk, index(unique,reverse index, reverse unique)
			getConstraintInfo();
			
			/*Check is canceled*/
			if (monitor != null && monitor.isCanceled()) {
				setCancel(true);
				setErrorMsg("Current user canceled.");
				return;
			}
			//get query specs if it is a view.
			getQuerySpecs();
			//get the partition information of table.
			getPartitionInfo();

			if (SchemaCommentHandler.isInstalledMetaTable(databaseInfo, connection)) {
				comments = SchemaCommentHandler.loadDescriptions(databaseInfo, connection);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			errorMsg = e.getMessage();
		} finally {
			// [TOOLS-2425]Support shard broker
			if (databaseInfo != null && databaseInfo.isShard()) {
				try {
					connection.setAutoCommit(isAutocommit);
				} catch (SQLException ignored) {
				}
			}

			finish();
		}
	}

	/**
	 * Get the partition information of table.
	 *
	 * @param schemaInfo SchemaInfo
	 */
	private void getPartitionInfo() throws SQLException {
		final String sql = "SELECT class_name, partition_name, partition_class_name,"
				+ " partition_type, partition_expr, partition_values"
				+ " FROM db_partition"
				+ " ORDER BY class_name";
		stmt = connection.createStatement();
		rs = stmt.executeQuery(sql);
		String exprDataType = null;
		while (rs.next()) {
			String className = rs.getString("class_name");
			String partitionName = rs.getString("partition_name");
			String partitionClassName = rs.getString("partition_class_name");
			String partitionExpr = rs.getString("partition_expr");

			SchemaInfo schemaInfo = schemas.get(className);
			if (schemaInfo == null) {
				LOGGER.error("Table " + className + " not found on the schema info.");
				continue;
			}

			PartitionType partitionType = null;
			String partitionTypeStr = rs.getString("partition_type");
			if (partitionTypeStr.equalsIgnoreCase("HASH")) {
				partitionType = PartitionType.HASH;
			} else if (partitionTypeStr.equalsIgnoreCase("LIST")) {
				partitionType = PartitionType.LIST;
			} else if (partitionTypeStr.equalsIgnoreCase("RANGE")) {
				partitionType = PartitionType.RANGE;
			}

			List<String> partitionValues = new ArrayList<String>();
			if (partitionType != PartitionType.HASH) {
				Object obj = rs.getObject("partition_values");
				if (obj == null) {
					continue;
				}

				Object[] arr = (Object[]) obj;
				for (int i = 0, len = arr.length; i < len; i++) {
					if (arr[i] == null) {
						partitionValues.add(null);
					} else {
						partitionValues.add(arr[i].toString());
					}
				}
			}

			PartitionInfo partitionItem = new PartitionInfo(className,
					partitionName, partitionClassName, partitionType,
					partitionExpr, partitionValues, -1);

			List<PartitionInfo> result = schemaInfo.getPartitionList();
			if (result == null) {
				result = new ArrayList<PartitionInfo>();
				schemaInfo.setPartitionList(result);
			}

			if (exprDataType == null && partitionExpr != null
					&& partitionExpr.trim().length() > 0) {
				exprDataType = getExprDataType(className, partitionExpr);
			}

			partitionItem.setPartitionExprType(exprDataType);
			result.add(partitionItem);
		}
	}

	/**
	 *
	 * Get partition expression data type
	 *
	 * @param tableName The table name
	 * @param expr The partition expression
	 * @return The string
	 */
	private String getExprDataType(String tableName, String expr) {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT " + expr + " FROM " + QuerySyntax.escapeKeyword(tableName) + " WHERE ROWNUM = 1";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmt = rs.getMetaData();
			return rsmt.getColumnTypeName(1);
		} catch (SQLException e) {
			LOGGER.error("", e);
			return null;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * Retieves the main information of table.
	 *
	 * @return schemaInfo SchemaInfo
	 * @throws SQLException the SQLException
	 */
	private void getTableInfo() throws SQLException {
		boolean isSupportReuseOid = CompatibleUtil.isSupportReuseOID(databaseInfo);
		boolean isSupportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);
		String reuseOidCoulmn = isSupportReuseOid ? ",	c.is_reuse_oid_class\n" : "\n";
		String dbName = databaseInfo.getDbName();

		boolean isSupportComment = SchemaCommentHandler.isInstalledMetaTable(databaseInfo, connection);
		Map<String, SchemaComment> descriptions = null;
		if (isSupportComment) {
			descriptions = SchemaCommentHandler.loadDescriptions(databaseInfo, connection);
		}

		String sql = "SELECT a.attr_name, a.attr_type, a.from_class_name,"
			+ " a.data_type, a.prec, a.scale, a.is_nullable,"
			+ " a.domain_class_name, a.default_value, a.def_order,"
			+ " c.is_system_class, c.class_type, c.partitioned, c.owner_name, c.class_name,"
			+ " a.from_attr_name"
			+ reuseOidCoulmn
			+ " FROM db_attribute a, db_class c"
			+ " WHERE c.class_name=a.class_name"
			+ " ORDER BY a.class_name, a.def_order";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.prepareStatement(sql);
			rs = ((PreparedStatement) stmt).executeQuery();
			SchemaInfo schemaInfo = null;
			while (rs.next()) {
				String type = rs.getString("class_type");
				boolean isTable = "CLASS".equals(type);
				boolean isUserClass = "NO".equals(rs.getString("is_system_class"));
				String owner = rs.getString("owner_name");
				String className = rs.getString("class_name");
				String partitioned = rs.getString("partitioned");

				schemaInfo = schemas.get(className);
				if (schemaInfo == null) {
					schemaInfo = new SchemaInfo();
					schemas.put(className, schemaInfo);
				}

				if (isTable) {
					schemaInfo.setVirtual(VIRTUAL_NORMAL);
				} else {
					schemaInfo.setVirtual(VIRTUAL_VIEW);
				}

				if (isUserClass) {
					schemaInfo.setType("user");
				} else {
					schemaInfo.setType("system");
				}

				if (isSupportReuseOid) {
					String isReuseOid = rs.getString("is_reuse_oid_class");
					if ("NO".equals(isReuseOid)) {
						schemaInfo.setReuseOid(false);
					} else {
						schemaInfo.setReuseOid(true);
					}
				}

				SchemaComment tableComment = isSupportComment ?
						descriptions.get(className + "*") : null;
				if (tableComment != null) {
					schemaInfo.setDescription(tableComment.getDescription());
				}

				schemaInfo.setOwner(owner);
				schemaInfo.setClassname(className);
				schemaInfo.setDbname(dbName);
				schemaInfo.setPartitionGroup(partitioned);
				getColumnInfo(rs, schemaInfo, isSupportCharset, descriptions);

				String fromAttrName = rs.getString("from_attr_name");
				String attrName = rs.getString("attr_name");
				if (StringUtil.isNotEmpty(fromAttrName) && !fromAttrName.equals(attrName)) {
					DBResolution dbr = new DBResolution();
					dbr.setAlias(attrName);
					dbr.setClassName(rs.getString("from_class_name"));
					dbr.setClassResolution(!rs.getString("attr_type").equals("INSTANCE"));
					dbr.setName(fromAttrName);
					schemaInfo.addResolution(dbr);
				}
				
				if (isSupportCharset && isNeedCollationInfo) {
					GetSchemaTask.getTableCollation(connection, schemaInfo);
				}
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * Get column information
	 *
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getColumnInfo(ResultSet rs, SchemaInfo schemaInfo, boolean supportCharset,
			Map<String, SchemaComment> descriptions) throws SQLException {
		if (schemaInfo == null) {
			return;
		}

		Map<String, String> columnCollMap = null;
		if (supportCharset && isNeedCollationInfo) {
			columnCollMap = GetSchemaTask.extractColumnCollationMap(connection, schemaInfo);
		}

		String attrName = rs.getString("attr_name");
		String type = rs.getString("attr_type");
		String inherit = rs.getString("from_class_name");
		String dataType = rs.getString("data_type");
		String prec = rs.getString("prec");
		String scale = rs.getString("scale");
		String isNull = rs.getString("is_nullable");
		String defaultValue = rs.getString("default_value");

		DBAttribute attr = new DBAttribute();
		attr.setName(attrName);

		if (inherit == null) {
			attr.setInherit(schemaInfo.getClassname());
		} else {
			attr.setInherit(inherit);
		}

		if ("YES".equals(isNull)) { //null
			attr.setNotNull(false);
		} else {
			attr.setNotNull(true);
		}
		dataType = DataType.convertAttrTypeString(dataType, prec, scale);
		attr.setType(dataType);
		//Fix bug TOOLS-3093
		defaultValue = DataType.convertDefaultValue(dataType, defaultValue, databaseInfo);
		attr.setDefault(defaultValue);

		//Fixe bug TOOLS-259
		//Different solution between CQB and CM on default value  make some problem
		attr.resetDefault();

		// after 9.1
		if (supportCharset && columnCollMap != null) {
			String collation = columnCollMap.get(attrName);
			attr.setCollation(collation);
		}

		SchemaComment columnSchema = descriptions != null ?
				descriptions.get(schemaInfo.getClassname() + "*" + attrName) : null;
		if (columnSchema != null) {
			attr.setDescription(columnSchema.getDescription());
		}

		if ("INSTANCE".equals(type)) { //INSTANCE
			schemaInfo.addAttribute(attr);
		} else if ("CLASS".equals(type)) {
			schemaInfo.addClassAttribute(attr);
		} else {
			attr.setShared(true);
			schemaInfo.addAttribute(attr);
		}
	}

	/**
	 * Get view's query specs.
	 *
	 * @param schemaInfo SchemaInfo
	 * @param isView boolean
	 * @throws SQLException the SQLException
	 */
	private void getQuerySpecs() throws SQLException {
		String sql = "SELECT vclass_name, vclass_def FROM db_vclass ORDER BY vclass_name";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);
		try {
			stmt = connection.prepareStatement(sql);
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String vClassName = rs.getString("vclass_name");
				String vClassDef = rs.getString("vclass_def");

				SchemaInfo schemaInfo = schemas.get(vClassName);
				if (schemaInfo == null) {
					LOGGER.error("View " + vClassName + " not found on the schema info.");
					continue;
				}

				schemaInfo.addQuerySpec(vClassDef);
			}	
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * Retrieves the foreign keys information
	 *
	 * @return Map<String, Map<String, String>> key name of reference
	 *         table,foreign key information
	 * @throws SQLException the SQLException
	 */
	private Map<String, Map<String, String>> getForeignKeyInfo() throws SQLException {
		Statement metaStmt = null;
		ResultSet metaRs = null;
		try {
			List<String> tableNamesContainedFK = new ArrayList<String>();
			String sql = "SELECT class_name"
					+ " FROM db_index"
					+ " WHERE is_foreign_key='YES'"
					+ " GROUP BY class_name";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			metaStmt = connection.prepareStatement(sql);
			metaRs = ((PreparedStatement) metaStmt).executeQuery();
			while (metaRs.next()) {
				tableNamesContainedFK.add(metaRs.getString("class_name"));
			}
			QueryUtil.freeQuery(metaStmt, metaRs);

			Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

			for (String className : tableNamesContainedFK) {
				metaRs = connection.getMetaData().getImportedKeys("", "", className);
				while (metaRs.next()) {
					String fkColName = metaRs.getString("FKCOLUMN_NAME");
					String key = className + "." + fkColName;
					Map<String, String> fkInfo = new HashMap<String, String>();
					fkInfo.put("PKTABLE_CAT", metaRs.getString("PKTABLE_CAT"));
					fkInfo.put("PKTABLE_SCHEM", metaRs.getString("PKTABLE_SCHEM"));
					fkInfo.put("PKTABLE_NAME", metaRs.getString("PKTABLE_NAME"));
					fkInfo.put("PKCOLUMN_NAME", metaRs.getString("PKCOLUMN_NAME"));
					fkInfo.put("FKTABLE_CAT", metaRs.getString("FKTABLE_CAT"));
					fkInfo.put("FKTABLE_SCHEM", metaRs.getString("FKTABLE_SCHEM"));
					fkInfo.put("FKTABLE_NAME", metaRs.getString("FKTABLE_NAME"));
					fkInfo.put("FKCOLUMN_NAME", fkColName);
					fkInfo.put("KEY_SEQ", metaRs.getString("KEY_SEQ"));
					fkInfo.put("UPDATE_RULE", FOREIGN_KEY_ACTION_MAP.get(metaRs.getInt("UPDATE_RULE")));
					fkInfo.put("DELETE_RULE", FOREIGN_KEY_ACTION_MAP.get(metaRs.getInt("DELETE_RULE")));
					fkInfo.put("FK_NAME", metaRs.getString("FK_NAME"));
					fkInfo.put("PK_NAME", metaRs.getString("PK_NAME"));
					fkInfo.put("DEFERRABILITY", metaRs.getString("DEFERRABILITY"));
					result.put(key, fkInfo);
				}
				QueryUtil.freeQuery(metaRs);
			}

			return result;
		} finally {
			QueryUtil.freeQuery(metaStmt, metaRs);
		}
	}

	/**
	 * Get constraint information
	 *
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getConstraintInfo() throws SQLException {
		Map<String, Map<String, String>> foreignKeys = getForeignKeyInfo();
		boolean isSupportPrefixIndexLength = CompatibleUtil.isSupportPrefixIndexLength(databaseInfo);
		boolean isSupportFuncIndex = CompatibleUtil.isSupportFuncIndex(databaseInfo);

		String extraColumns = null;
		if (isSupportPrefixIndexLength) {
			extraColumns = ", key_prefix_length";
		} else {
			extraColumns = "";
		}

		String funcDef = isSupportFuncIndex ? ", k.func" : "";

		Map<String, Constraint> constraintMap = new HashMap<String, Constraint>();

		String sql = "SELECT i.class_name, i.index_name, i.is_unique, i.is_reverse,"
				+ " i.is_primary_key, i.is_foreign_key, i.key_count,"
				+ " k.key_attr_name, k.asc_desc, k.key_order" + extraColumns + funcDef
				+ " FROM db_index i, db_index_key k"
				+ " WHERE i.class_name=k.class_name AND i.index_name=k.index_name"
				+ " ORDER BY i.class_name, i.index_name, k.key_order";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);
		try {
			stmt = connection.prepareStatement(sql);
			rs = ((PreparedStatement) stmt).executeQuery();
			Map<String, String> constraint2Unique = new HashMap<String, String>();
			while (rs.next()) {
				String className = rs.getString("class_name");
				String constraintName = rs.getString("index_name");
				String pk = rs.getString("is_primary_key");
				String fk = rs.getString("is_foreign_key");
				String unique = rs.getString("is_unique");
				String reverse = rs.getString("is_reverse");
				int keyCount = rs.getInt("key_count");

				SchemaInfo schemaInfo = schemas.get(className);
				if (schemaInfo == null) {
					LOGGER.error("Table " + className + " not found on the schema info.");
					continue;
				}

				String constraintKey = className + "_" + constraintName;
				Constraint constraint = constraintMap.get(constraintKey);
				if (constraint == null) {
					constraint = new Constraint(false);
					constraint.setName(constraintName);
					schemaInfo.addConstraint(constraint);
					constraintMap.put(constraintKey, constraint);

					if (StringUtil.booleanValueWithYN(pk)) {
						constraint.setType(Constraint.ConstraintType.PRIMARYKEY.getText());
					} else if (StringUtil.booleanValueWithYN(fk)) {
						constraint.setType(Constraint.ConstraintType.FOREIGNKEY.getText());
					} else {
						if (StringUtil.booleanValueWithYN(unique)
								&& !StringUtil.booleanValueWithYN(reverse)) {
							constraint.setType(Constraint.ConstraintType.UNIQUE.getText());
						} else if (!StringUtil.booleanValueWithYN(unique)
								&& StringUtil.booleanValueWithYN(reverse)) {
							constraint.setType(Constraint.ConstraintType.REVERSEINDEX.getText());
						} else if (StringUtil.booleanValueWithYN(unique)
								&& StringUtil.booleanValueWithYN(reverse)) {
							constraint.setType(Constraint.ConstraintType.REVERSEUNIQUE.getText());
						} else if (!StringUtil.booleanValueWithYN(unique)
								&& !StringUtil.booleanValueWithYN(reverse)) {
							constraint.setType(Constraint.ConstraintType.INDEX.getText());
						}
					}
					constraint.setKeyCount(keyCount);
					constraint2Unique.put(constraintName, unique);
				}

				String attrName = rs.getString("key_attr_name");
				String ascDesc = rs.getString("asc_desc");
				String indexPrefix = "";
				if (isSupportPrefixIndexLength) {
					int indexLength = rs.getInt("key_prefix_length");
					if (indexLength > 0) {
						indexPrefix = "(" + indexLength + ")";
					}
				}

				if (isSupportFuncIndex && attrName == null) {
					attrName = rs.getString("func").trim();
				}

				constraint.addAttribute(attrName);

				if (Constraint.ConstraintType.FOREIGNKEY.getText().equals(constraint.getType())) {
					String key = className + "." + attrName;
					Map<String, String> fkInfo = foreignKeys.get(key);
					if (null != fkInfo) {
						//.append(" ON CACHE OBJECT {3}");
						constraint.addRule("REFERENCES " + fkInfo.get("PKTABLE_NAME"));
						constraint.addRule("ON DELETE " + fkInfo.get("DELETE_RULE"));
						constraint.addRule("ON UPDATE " + fkInfo.get("UPDATE_RULE"));
					}
				} else {
					constraint.addRule(attrName + indexPrefix + " " + ascDesc);
				}

				//set the db attributes' unique property.
				if (StringUtil.booleanValueWithYN(constraint2Unique.get(constraint.getName()))) {
					DBAttribute dba = schemaInfo.getDBAttributeByName(attrName, true);
					if (dba == null) {
						dba = schemaInfo.getDBAttributeByName(attrName, false);
					}
					if (null != dba) {
						dba.setUnique(true);
					}
				}
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

	}

	/**
	 * Get type information
	 *
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getTypeInfo() throws SQLException {
		String sql = "SELECT a.class_name, a.attr_name, a.attr_type,"
				+ " a.data_type, a.prec, a.scale"
				+ " FROM db_attr_setdomain_elm a"
				+ " ORDER BY a.class_name, a.attr_name";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.prepareStatement(sql);
			rs = ((PreparedStatement) stmt).executeQuery();
			Map<String, Map<String, List<SubAttribute>>> schemaColumnMap = new HashMap<String, Map<String, List<SubAttribute>>>();
			while (rs.next()) {
				String className = rs.getString("class_name");
				String attrName = rs.getString("attr_name");
				String type = rs.getString("attr_type");
				String dateType = rs.getString("data_type");
				String prec = rs.getString("prec");
				String scale = rs.getString("scale");

				String subType = DataType.convertAttrTypeString(dateType, prec, scale);

				Map<String, List<SubAttribute>> columnMap = schemaColumnMap.get("className");
				if (columnMap == null) {
					columnMap = new HashMap<String, List<SubAttribute>>();
					schemaColumnMap.put(className, columnMap);
				}

				List<SubAttribute> subList = columnMap.get(attrName);
				if (subList == null) {
					subList = new ArrayList<SubAttribute>();
					columnMap.put(attrName, subList);
				}
				
				subList.add(new SubAttribute(type, subType));
			}
			for (Entry<String, Map<String, List<SubAttribute>>> entryMap : schemaColumnMap.entrySet()) {
				String className = entryMap.getKey();
				Map<String, List<SubAttribute>> columnMap = entryMap.getValue();

				SchemaInfo schemaInfo = schemas.get(className);
				if (schemaInfo == null) {
					LOGGER.error("Table " + className
							+ " not found on the schema info.");
					continue;
				}

				for (Entry<String, List<SubAttribute>> entry : columnMap.entrySet()) {
					String name = entry.getKey();
					List<SubAttribute> subList = entry.getValue();

					DBAttribute attr = null;
					if ("INSTANCE".equals(subList.get(0).getParentInstanceType())) { //INSTANCE
						attr = schemaInfo.getDBAttributeByName(name, false);
					} else if ("CLASS".equals(subList.get(0).getParentInstanceType())) {
						attr = schemaInfo.getDBAttributeByName(name, true);
					} else {
						attr = schemaInfo.getDBAttributeByName(name, false);
					}

					StringBuilder sb = new StringBuilder();
					sb.append(attr.getType()).append("(");
					int size = subList.size();
					for (int i = 0; i < size; i++) {
						SubAttribute subType = subList.get(i);
						sb.append(subType.getsubDataType());
						if (i + 1 < size) {
							sb.append(",");
						}
					}
					sb.append(")");
					attr.setType(sb.toString());
				}
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * Get auto increment information
	 *
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getAutoIncrementInfo() throws SQLException {
		List<SerialInfo> serialInfoList = new ArrayList<SerialInfo>();
		boolean isSupportCache = CompatibleUtil.isSupportCache(databaseInfo);
		String sql = "SELECT owner.name, db_serial.*"
				+ " FROM db_serial"
				+ " WHERE class_name IS NOT NULL";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);
		try {
			stmt = connection.prepareStatement(sql);
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String owner = rs.getString("owner.name");
				String currentVal = rs.getString("current_val");
				String incrementVal = rs.getString("increment_val");
				String maxVal = rs.getString("max_val");
				String minVal = rs.getString("min_val");
				String cyclic = rs.getString("cyclic");
				String startVal = rs.getString("started");
				String className = rs.getString("class_name");
				String attName = rs.getString("att_name");
				String cacheCount = null;
				if (isSupportCache) {
					cacheCount = rs.getString("cached_num");
				}
				boolean isCycle = false;
				if ("1".equals(cyclic)) {
					isCycle = true;
				}

				SerialInfo serialInfo = new SerialInfo(name, owner, currentVal,
						incrementVal, maxVal, minVal, isCycle, startVal,
						cacheCount, className, attName);
				serialInfoList.add(serialInfo);
			}

			for (SerialInfo autoIncrement : serialInfoList) {
				String className = autoIncrement.getClassName();
				String attrName = autoIncrement.getAttName();
				assert (null != attrName);
				SchemaInfo schemaInfo = schemas.get(className);
				if (schemaInfo == null) {
					LOGGER.error("Can't find a table with auto increment column : " + attrName);
					continue;
				}

				DBAttribute a = schemaInfo.getDBAttributeByName(attrName, false);
				if (a != null) {
					a.setAutoIncrement(autoIncrement);
				}
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

//	/**
//	 * Convert data type string from db to strings to displayed in client.
//	 *
//	 * @param dataType String
//	 * @param prec String
//	 * @param scale String
//	 * @return local string of data type
//	 * 
//	 * @deprecated Use following static method instead:
//	 *             <br>com.cubrid.cubridmanager.core.cubrid.table.model.
//	 *             DataType.convertAttrTypeString(String, String, String);
//	 */
//	private String convertAttrTypeString(String dataType, String prec,
//			String scale) {
//		String dt = dataType;
//		if ("SHORT".equals(dt)) {
//			dt = ("smallint");
//		} else if ("STRING".equals(dt)) {
//			dt = ("character varying" + "(" + prec + ")");
//		} else if ("CHAR".equals(dt)) {
//			dt = ("character(" + prec + ")");
//		} else if ("VARCHAR".equals(dt)) {
//			dt = ("character varying(" + prec + ")");
//		} else if ("NCHAR".equals(dt)) {
//			dt = ("national character(" + prec + ")");
//		} else if ("VARNCHAR".equals(dt)) {
//			dt = ("national character varying(" + prec + ")");
//		} else if ("BIT".equals(dt)) {
//			dt = ("bit(" + prec + ")");
//		} else if ("VARBIT".equals(dt)) {
//			dt = ("bit varying(" + prec + ")");
//		} else if ("NUMERIC".equals(dt)) {
//			dt = ("numeric(" + prec + "," + scale + ")");
//		} else if ("SET".equals(dt)) {
//			dt = ("set_of");
//		} else if ("MULTISET".equals(dt)) {
//			dt = ("multiset_of");
//		} else if ("SEQUENCE".equals(dt)) {
//			dt = ("sequence_of");
//		} else {
//			dt = (dt.toLowerCase(Locale.getDefault()));
//		}
//		return dt;
//	}

	/**
	 * Get super class information (partitioned table)
	 *
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getSuperClassInfo() throws SQLException {
		String sql = "SELECT class_name, super_class_name"
				+ " FROM db_direct_super_class"
				+ " ORDER BY class_name, super_class_name";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.prepareStatement(sql);
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String className = rs.getString("class_name");
				String superClass = rs.getString("super_class_name");
				SchemaInfo schemaInfo = schemas.get(className);
				if (schemaInfo != null) {
					schemaInfo.addSuperClass(superClass);
				} else {
					LOGGER.error("Table " + className + " not found on the schema info.");
				}
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	public Map<String, SchemaInfo> getSchemas() {
		return schemas;
	}

	public Map<String, SchemaComment> getComments() {
		return comments;
	}

	public List<SchemaInfo> getSchemaList() {
		List<SchemaInfo> list = new ArrayList<SchemaInfo>();
		if (schemas == null) {
			return list;
		}

		for (SchemaInfo schemaInfo : schemas.values()) {
			list.add(schemaInfo);
		}

		Collections.sort(list, new Comparator<SchemaInfo>() {
			public int compare(SchemaInfo o1, SchemaInfo o2) {
				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return -1;
				}

				return o1.getClassname().compareToIgnoreCase(o2.getClassname());
			}
		});

		return list;
	}

	/**
	 * @param isNeedCollationInfo the isNeedCollationInfo to set
	 */
	public void setNeedCollationInfo(boolean isNeedCollationInfo) {
		this.isNeedCollationInfo = isNeedCollationInfo;
	}
}
