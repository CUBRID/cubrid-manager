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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.PartitionInfo;
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
 * Get schema information via JDBC
 * 
 * @author moulinwang
 * @version 1.0 - 2009-6-16 created by moulinwang
 */
public class GetSchemaTask extends JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(GetSchemaTask.class);
	
	//private final Logger logger = LogUtil.getLogger(GetSchemaTask.class);
	private final String tableName;
	private SchemaInfo schema = null;
	/*Is load collation information*/
	private volatile boolean isNeedCollationInfo = true;
	private IProgressMonitor monitor;
	public GetSchemaTask(DatabaseInfo dbInfo, String tableName) {
		super("GetSchemaTask", dbInfo);
		this.tableName = tableName.toLowerCase(Locale.getDefault());
	}
	
	public GetSchemaTask(DatabaseInfo dbInfo, String tableName, IProgressMonitor monitor) {
		this(dbInfo, tableName);
		this.monitor = monitor;
	}

	public GetSchemaTask(Connection connection, DatabaseInfo dbInfo, String tableName) {
		super("GetSchemaTask", dbInfo, connection);
		this.tableName = tableName.toLowerCase(Locale.getDefault());
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
			
			// [TOOLS-2425]Support shard broker
			if (databaseInfo != null && databaseInfo.isShard()) {
				isAutocommit = connection.getAutoCommit();
				connection.setAutoCommit(true);
			}

			SchemaInfo schemaInfo = getTableInfo();
			// get super class information
			getSuperClassInfo(schemaInfo);
			// get column information
			getColumnInfo(schemaInfo);
				
			/*Check is canceled*/
			if (monitor != null && monitor.isCanceled()) {
				setCancel(true);
				setErrorMsg("Current user canceled.");
				return;
			}
			// get auto increment information from db_serial table, which is a system table accessed by all users
			getAutoIncrementInfo(schemaInfo);
			//get set(object) type information from db_attr_setdomain_elm view
			getTypeInfo(schemaInfo);
			//get pk, fk, index(unique,reverse index, reverse unique)
			getConstraintInfo(schemaInfo);
			
			/*Check is canceled*/
			if (monitor != null && monitor.isCanceled()) {
				setCancel(true);
				setErrorMsg("Current user canceled.");
				return;
			}
			//get db resolutions
			getDBResolutionInfo(schemaInfo);
			//get query specs if it is a view.
			getQuerySpecs(schemaInfo);
			//get the partition information of table.
			getPartitionInfo(schemaInfo);

			schema = schemaInfo;
		} catch (SQLException e) {
			errorMsg = e.getMessage();
			LOGGER.error("", e);
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
	private void getPartitionInfo(SchemaInfo schemaInfo) {
		// loading of a partition info
		GetPartitionedClassListTask partitionedClassListTask = null;
		if (schemaInfo != null
				&& StringUtil.booleanValueWithYN(schemaInfo.isPartitionGroup())) {
			if (partitionedClassListTask == null) {
				partitionedClassListTask = new GetPartitionedClassListTask(
						databaseInfo);
			}
			List<PartitionInfo> partitionInfoList = partitionedClassListTask.getPartitionItemList(schemaInfo.getClassname());
			schemaInfo.setPartitionList(partitionInfoList);
		}
	}

	/**
	 * Retieves the main information of table.
	 * 
	 * @return schemaInfo SchemaInfo
	 * @throws SQLException the SQLException
	 */
	private SchemaInfo getTableInfo() throws SQLException {
		boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);

		// get table comment
		boolean supportComment = SchemaCommentHandler.isInstalledMetaTable(databaseInfo, connection);
		String description = null;

		if (supportComment) {
			description = SchemaCommentHandler.loadDescription(
					databaseInfo, connection, tableName).get(tableName + "*")
					.getDescription();
		}

		//get table information
		String sql = "SELECT * FROM db_class WHERE class_name=?";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);
		SchemaInfo schemaInfo = null;
		try {
			stmt = connection.prepareStatement(sql);
			((PreparedStatement) stmt).setString(1, tableName);
			rs = ((PreparedStatement) stmt).executeQuery();
			// databaseInfo.getServerInfo().compareVersionKey("8.2.2") >= 0;
			boolean isSupportReuseOid = CompatibleUtil.isSupportReuseOID(databaseInfo);
			if (rs.next()) {
				String type = rs.getString("class_type");
				String isSystemClass = rs.getString("is_system_class");
				String owner = rs.getString("owner_name");
				schemaInfo = new SchemaInfo();
				if ("CLASS".equals(type)) {
					schemaInfo.setVirtual(SchemaInfo.VIRTUAL_NORMAL);
				} else {
					schemaInfo.setVirtual(SchemaInfo.VIRTUAL_VIEW);
				}
				if ("NO".equals(isSystemClass)) {
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
				schemaInfo.setDescription(description);
				schemaInfo.setOwner(owner);
				schemaInfo.setClassname(tableName);
				schemaInfo.setDbname(databaseInfo.getDbName());
				schemaInfo.setPartitionGroup(rs.getString("partitioned"));
			}

			// after cubrid 9.1
			if (supportCharset && schemaInfo != null
					&& StringUtil.isEqual(SchemaInfo.VIRTUAL_NORMAL, schemaInfo.getVirtual())) {
				getTableCollation(connection, schemaInfo);
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return schemaInfo;
	}

	/**
	 * Get the sql of "SHOW CREATE TABLE ..." or "SHOW CREATE VIEW ..."
	 * 
	 * @param schemaInfo
	 * @return
	 */
	private static String getCreateSQL(SchemaInfo schemaInfo) {

		String key = "TABLE";
		if (SchemaInfo.VIRTUAL_VIEW.equalsIgnoreCase(schemaInfo.getVirtual())) {
			key = SchemaInfo.VIRTUAL_VIEW.toUpperCase();
		}
		String sql = "SHOW CREATE " + key + " "
				+ QuerySyntax.escapeKeyword(schemaInfo.getClassname());

		return sql;
	}

	/**
	 * Get collation for the table.
	 * 
	 * @param schemaInfo
	 */
	public static void getTableCollation(Connection conn, SchemaInfo schemaInfo) throws SQLException {
		String ddl = null;
		String sql = getCreateSQL(schemaInfo);

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				ddl = rs.getString(2);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		schemaInfo.setCollation(null);
		if (ddl == null) {
			return;
		}

		String findString = "COLLATE";
		int sp = ddl.lastIndexOf(findString);
		if (sp == -1) {
			return;
		}

		sp += findString.length();
		String collationRaw = ddl.substring(sp).trim();
		int ep = collationRaw.indexOf(' ');
		if (ep == -1) {
			ep = collationRaw.indexOf('\t');
		}
		if (ep == -1) {
			ep = collationRaw.indexOf('\r');
		}
		if (ep == -1) {
			ep = collationRaw.indexOf('\n');
		}
		
		String collation = null;
		if (ep == -1) {
			collation = ddl.substring(sp).trim();
		} else {
			collation = collationRaw.substring(0, ep).trim();
		}

		schemaInfo.setCollation(collation);
	}
	
	/**
	 * Get view's query specs.
	 * 
	 * @param schemaInfo SchemaInfo
	 * @param isView boolean
	 * @throws SQLException the SQLException
	 */
	private void getQuerySpecs(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo == null || !SchemaInfo.VIRTUAL_VIEW.equals(schemaInfo.getVirtual())) {
			return;
		}
		if (schemaInfo != null) {
			String sql = "SELECT vclass_name, vclass_def FROM db_vclass WHERE vclass_name=?";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			try{
				stmt = connection.prepareStatement(sql);
				((PreparedStatement) stmt).setString(1, tableName);
				rs = ((PreparedStatement) stmt).executeQuery();
				while (rs.next()) {
					schemaInfo.addQuerySpec(rs.getString("vclass_def"));
				}
			} finally {
				QueryUtil.freeQuery(stmt, rs);
			}
		}
	}

	/**
	 * Retrieves the resolutions of database
	 * 
	 * @param schemaInfo SchemaInfo
	 * @throws SQLException the SQLException
	 */
	private void getDBResolutionInfo(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo != null) {
			String sql = "SELECT attr_name, from_class_name, attr_type, from_attr_name" +
					" FROM db_attribute WHERE class_name=? AND attr_name<>from_attr_name" +
					" AND from_attr_name IS NOT NULL";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			try{
				stmt = connection.prepareStatement(sql);
				((PreparedStatement) stmt).setString(1, tableName);
				rs = ((PreparedStatement) stmt).executeQuery();
				while (rs.next()) {
					DBResolution dbr = new DBResolution();
					dbr.setAlias(rs.getString("attr_name"));
					dbr.setClassName(rs.getString("from_class_name"));
					dbr.setClassResolution(!rs.getString("attr_type").equals(
							"INSTANCE"));
					dbr.setName(rs.getString("from_attr_name"));
					schemaInfo.addResolution(dbr);
				}
			} finally {
				QueryUtil.freeQuery(stmt, rs);
			}
		}

	}

	static final Map<Integer, String> FOREIGN_KEY_ACTION_MAP = new HashMap<Integer, String>();
	static {
		FOREIGN_KEY_ACTION_MAP.put(0, "CASCADE");
		FOREIGN_KEY_ACTION_MAP.put(1, "RESTRICT");
		FOREIGN_KEY_ACTION_MAP.put(2, "SET NULL");
		FOREIGN_KEY_ACTION_MAP.put(3, "NO ACTION");
	}

	/**
	 * Retrieves the foreign keys information
	 * 
	 * @return Map<String, Map<String, String>> key name of reference
	 *         table,foreign key information
	 * @throws SQLException the SQLException
	 */
	private Map<String, Map<String, String>> getForeignKeyInfo() throws SQLException {
		try {
			rs = connection.getMetaData().getImportedKeys("", "", tableName);
			Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> fkInfo = new HashMap<String, String>();
				fkInfo.put("PKTABLE_CAT", rs.getString("PKTABLE_CAT"));
				fkInfo.put("PKTABLE_SCHEM", rs.getString("PKTABLE_SCHEM"));
				fkInfo.put("PKTABLE_NAME", rs.getString("PKTABLE_NAME"));
				fkInfo.put("PKCOLUMN_NAME", rs.getString("PKCOLUMN_NAME"));
				fkInfo.put("FKTABLE_CAT", rs.getString("FKTABLE_CAT"));
				fkInfo.put("FKTABLE_SCHEM", rs.getString("FKTABLE_SCHEM"));
				fkInfo.put("FKTABLE_NAME", rs.getString("FKTABLE_NAME"));
				String fkColName = rs.getString("FKCOLUMN_NAME");
				fkInfo.put("FKCOLUMN_NAME", fkColName);
				fkInfo.put("KEY_SEQ", rs.getString("KEY_SEQ"));
				fkInfo.put("UPDATE_RULE",
						FOREIGN_KEY_ACTION_MAP.get(rs.getInt("UPDATE_RULE")));
				fkInfo.put("DELETE_RULE",
						FOREIGN_KEY_ACTION_MAP.get(rs.getInt("DELETE_RULE")));
				fkInfo.put("FK_NAME", rs.getString("FK_NAME"));
				fkInfo.put("PK_NAME", rs.getString("PK_NAME"));
				fkInfo.put("DEFERRABILITY", rs.getString("DEFERRABILITY"));
				result.put(fkColName, fkInfo);
			}
			return result;
		} finally {
			QueryUtil.freeQuery(rs);
		}
	}

	/**
	 * Get constraint information
	 * 
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getConstraintInfo(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo == null) {
			return;
		}

		Map<String, Map<String, String>> foreignKeys = getForeignKeyInfo();
		String sql = "SELECT index_name, is_unique, is_reverse, is_primary_key, is_foreign_key, key_count"
				+ " FROM db_index WHERE class_name=? ORDER BY index_name";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);
		Map<String, String> constraint2Unique = new HashMap<String, String>();

		try {
			stmt = connection.prepareStatement(sql);
			((PreparedStatement) stmt).setString(1, tableName);
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String constraintName = rs.getString("index_name");
				String pk = rs.getString("is_primary_key");
				String fk = rs.getString("is_foreign_key");
				String unique = rs.getString("is_unique");
				String reverse = rs.getString("is_reverse");
				int keyCount = rs.getInt("key_count");
				Constraint c = new Constraint(false);
				c.setName(constraintName);
				constraint2Unique.put(constraintName, unique);
				if (StringUtil.booleanValueWithYN(pk)) {
					c.setType(Constraint.ConstraintType.PRIMARYKEY.getText());
				} else if (StringUtil.booleanValueWithYN(fk)) {
					c.setType(Constraint.ConstraintType.FOREIGNKEY.getText());
				} else {
					if (StringUtil.booleanValueWithYN(unique)
							&& !StringUtil.booleanValueWithYN(reverse)) {
						c.setType(Constraint.ConstraintType.UNIQUE.getText());
					} else if (!StringUtil.booleanValueWithYN(unique)
							&& StringUtil.booleanValueWithYN(reverse)) {
						c.setType(Constraint.ConstraintType.REVERSEINDEX.getText());
					} else if (StringUtil.booleanValueWithYN(unique)
							&& StringUtil.booleanValueWithYN(reverse)) {
						c.setType(Constraint.ConstraintType.REVERSEUNIQUE.getText());
					} else if (!StringUtil.booleanValueWithYN(unique)
							&& !StringUtil.booleanValueWithYN(reverse)) {
						c.setType(Constraint.ConstraintType.INDEX.getText());
					}
				}
				c.setKeyCount(keyCount);
				schemaInfo.addConstraint(c);
			}
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		List<Constraint> cList = schemaInfo.getConstraints();
		boolean isSupportPrefixIndexLength = CompatibleUtil.isSupportPrefixIndexLength(databaseInfo);
		sql = "SELECT key_attr_name, asc_desc, key_order";
		if (isSupportPrefixIndexLength) {
			sql += ", key_prefix_length";
		}
		sql += " FROM db_index_key WHERE index_name=? AND class_name=? ORDER BY key_order";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);
		for (Constraint c : cList) {
			try {
				stmt = connection.prepareStatement(sql);
				final String constraintName = c.getName();
				((PreparedStatement) stmt).setString(1, constraintName);
				((PreparedStatement) stmt).setString(2, tableName);
				rs = ((PreparedStatement) stmt).executeQuery();
				while (rs.next()) {
					String attrName = rs.getString("key_attr_name");
					String ascDesc = rs.getString("asc_desc");
					String indexPrefix = "";
					if (isSupportPrefixIndexLength) {
						int indexLength = rs.getInt("key_prefix_length");
						if (indexLength > 0) {
							indexPrefix = "(" + indexLength + ")";
						}
					}
					c.addAttribute(attrName);
					if (Constraint.ConstraintType.FOREIGNKEY.getText().equals(c.getType())) {
						Map<String, String> fkInfo = foreignKeys.get(attrName);
						if (null != fkInfo) {
							String referencedTable = c.getReferencedTable();
							String pkTable = fkInfo.get("PKTABLE_NAME");
							if(StringUtil.isEqual(referencedTable, pkTable)){
								continue;
							}
							c.addRule("REFERENCES " + pkTable);
							c.addRule("ON DELETE " + fkInfo.get("DELETE_RULE"));
							c.addRule("ON UPDATE " + fkInfo.get("UPDATE_RULE"));
						}
					} else {
						c.addRule(attrName + indexPrefix + " " + ascDesc);
					}

					// set the db attributes' unique property.
					if (StringUtil.booleanValueWithYN(constraint2Unique.get(c.getName()))) {
						DBAttribute dba = schemaInfo.getDBAttributeByName(attrName,
								true);
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
	}

	/**
	 * Get type information
	 * 
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getTypeInfo(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo != null) {
			String sql = "SELECT a.attr_name, a.attr_type,"
					+ " a.data_type, a.prec, a.scale"
					+ " FROM db_attr_setdomain_elm a" + " WHERE a.class_name=?";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			try {
				stmt = connection.prepareStatement(sql);
				((PreparedStatement) stmt).setString(1, tableName);
				rs = ((PreparedStatement) stmt).executeQuery();
				Map<String, List<SubAttribute>> columnMap = new HashMap<String, List<SubAttribute>>();
				while (rs.next()) {
					String attrName = rs.getString("attr_name");
					String type = rs.getString("attr_type");
					String dateType = rs.getString("data_type");
					String prec = rs.getString("prec");
					String scale = rs.getString("scale");

					String subType = DataType.convertAttrTypeString(dateType, prec, scale);

					List<SubAttribute> subList = columnMap.get(attrName);
					if (subList == null) {
						subList = new ArrayList<SubAttribute>();
						columnMap.put(attrName, subList);
					}
					subList.add(new SubAttribute(type, subType));
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
			} finally {
				QueryUtil.freeQuery(stmt, rs);
			}
		}
	}

	/**
	 * Get auto increment information
	 * 
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getAutoIncrementInfo(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo != null) {
			List<SerialInfo> serialInfoList = new ArrayList<SerialInfo>();
			//databaseInfo.getServerInfo().compareVersionKey("8.2.2") >= 0;
			boolean isSupportCache = CompatibleUtil.isSupportCache(databaseInfo);
			String sql = "SELECT owner.name, db_serial.* FROM db_serial WHERE class_name=?";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);
			try {
				stmt = connection.prepareStatement(sql);
				((PreparedStatement) stmt).setString(1, tableName);
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
					if (cyclic != null && cyclic.equals("1")) {
						isCycle = true;
					}
					SerialInfo serialInfo = new SerialInfo(name, owner, currentVal,
							incrementVal, maxVal, minVal, isCycle, startVal,
							cacheCount, className, attName);
					serialInfoList.add(serialInfo);
				}
				for (SerialInfo autoIncrement : serialInfoList) {
					String attrName = autoIncrement.getAttName();
					assert (null != attrName);
					DBAttribute a = schemaInfo.getDBAttributeByName(attrName, false);
					if (a != null) {
						a.setAutoIncrement(autoIncrement);
					}
				}
			}finally {
				QueryUtil.freeQuery(stmt, rs);
			}
		}
	}

	/**
	 * Get column information
	 * 
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getColumnInfo(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo != null) {
			String sql = null;

			Map<String, String> columnCollMap = null;
			boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);
			if (supportCharset && isNeedCollationInfo && StringUtil.isEqual(schemaInfo.getVirtual(), SchemaInfo.VIRTUAL_NORMAL)) {
				columnCollMap = extractColumnCollationMap(connection, schemaInfo);
			}

			// get table comment
			boolean supportComment = SchemaCommentHandler.isInstalledMetaTable(databaseInfo, connection);
			Map<String, SchemaComment> comments = null;
			if (supportComment) {
				comments = SchemaCommentHandler.loadDescriptions(databaseInfo, connection);
			}

			sql = "SELECT *"
					+ " FROM db_attribute"
					+ " WHERE class_name=? "
					+ " ORDER BY def_order";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);
			List<String> enumColumnList = new ArrayList<String>();
			try{
				stmt = connection.prepareStatement(sql);
				((PreparedStatement) stmt).setString(1, tableName);
				rs = ((PreparedStatement) stmt).executeQuery();
								
				while (rs.next()) {
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
						attr.setInherit(tableName);
					} else {
						attr.setInherit(inherit);
					}
					if ("YES".equals(isNull)) { //null
						attr.setNotNull(false);
					} else {
						attr.setNotNull(true);
					}
					if(DataType.DATATYPE_ENUM.equalsIgnoreCase(dataType)) {
						enumColumnList.add(attrName);
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
					if (supportCharset && columnCollMap != null && DataType.canUseCollation(attr.getType())) {
						String collation = columnCollMap.get(attrName);
						attr.setCollation(collation);
					}

					if (comments != null) {
						attr.setDescription(comments.get(tableName + "*" + attrName).getDescription());
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
			} finally {				
				QueryUtil.freeQuery(stmt, rs);
			}
			
			// Get enumeration
			if (CompatibleUtil.isSupportEnumVersion(databaseInfo) && enumColumnList.size() > 0) {
				String escapedTableName = QuerySyntax.escapeKeyword(schemaInfo.getClassname());
				StringBuilder sb = new StringBuilder();
				sb.append("SHOW COLUMNS FROM ").append(escapedTableName).append(" WHERE FIELD IN (");
				try{
					stmt = connection.createStatement();
					for (int i = 0; i < enumColumnList.size(); i++) {
						sb.append("'").append(enumColumnList.get(i)).append("'");
						if (i + 1 < enumColumnList.size()) {
							sb.append(",");
						}
					}
					sb.append(");");
					rs = stmt.executeQuery(sb.toString());
					while (rs.next()) {
						String name = rs.getString("Field");
						String type = rs.getString("Type");
						
						DBAttribute attr = schemaInfo.getDBAttributeByName(name, false);
						attr.setEnumeration(StringUtil.getEnumeration(type));
					}
				} finally{
					QueryUtil.freeQuery(stmt, rs);
				}
			}
		}
	}

	/**
	 * Extract collations map of columns
	 *
	 * @param schemaInfo
	 * @return
	 */
	public static Map<String, String> extractColumnCollationMap(Connection conn, SchemaInfo schemaInfo) {
		final String findString = " COLLATE ";
		Map<String, String> columnCollMap = new HashMap<String, String>();
		String ddl = null;
		String sql = getCreateSQL(schemaInfo);
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				ddl = rs.getString(2);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		if (ddl == null) {
			ddl = "";
		}

		int sp = -1;
		sp = ddl.indexOf('(');
		if (sp != -1) {
			sp += 1;
		}
		while (sp != -1) {
			int ep = ddl.indexOf(',', sp);
			if (ep == -1) {
				ep = ddl.length();
			}
			String row = ddl.substring(sp, ep);
			String column = extractColumnName(row);
			if (column != null) {
				int rowp = row.indexOf(findString);
				if (rowp != -1) {
					rowp += findString.length();
					String collation = row.substring(rowp).trim();
					rowp = collation.indexOf(' ');
					if (rowp != -1) {
						collation = collation.substring(0, rowp).trim();
					}
					rowp = collation.indexOf(')');
					if (rowp != -1) {
						collation = collation.substring(0, rowp).trim();
					}
					columnCollMap.put(column, collation);
				}
			}

			sp = ep + 1;
			if (sp > ddl.length()) {
				break;
			}
		}

		return columnCollMap;
	}

	/**
	 * Extract a column name by a row of the column on a ddl.
	 * 
	 * @param columnSpecOfDdlSpec
	 * @return
	 */
	private static String extractColumnName(String columnSpecOfDdlSpec) {
		String[] arr = columnSpecOfDdlSpec.trim().split(" ");
		if (arr == null || arr.length <= 0 || arr[0] == null) {
			return null;
		}
		String column = arr[0].replaceAll("[\\[\\]\\\",\\' ]*", "");
		return column;
	}

	/**
	 * Get super class information
	 * 
	 * @param schemaInfo the SchemaInfo
	 * @throws SQLException the exception
	 */
	private void getSuperClassInfo(SchemaInfo schemaInfo) throws SQLException {
		if (schemaInfo == null) {
			return;
		}

		String sql = "SELECT super_class_name FROM db_direct_super_class"
				+ " WHERE class_name=?";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.prepareStatement(sql);
			((PreparedStatement) stmt).setString(1, tableName);
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String superClass = rs.getString(1);
				schemaInfo.addSuperClass(superClass);
			}	
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	public SchemaInfo getSchema() {
		return schema;
	}

	/**
	 * @param isNeedCollationInfo the isNeedCollationInfo to set
	 */
	public void setNeedCollationInfo(boolean isNeedCollationInfo) {
		this.isNeedCollationInfo = isNeedCollationInfo;
	}
	
}

class SubAttribute{
	private String parentInstanceType;
	private String subDataType;
	
	public SubAttribute(String parentType, String dataType) {
		this.parentInstanceType = parentType;
		this.subDataType = dataType;
	}

	/**
	 * 
	 * @return the parentType
	 */
	public String getParentInstanceType() {
		return parentInstanceType;
	}

	/**
	 * 
	 * @return the dataType
	 */
	public String getsubDataType() {
		return subDataType;
	}
	
	
}
