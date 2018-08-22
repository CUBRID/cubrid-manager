/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.core.schemacomment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.model.CommentType;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;

public class SchemaCommentHandler {
	private static final Logger LOGGER = LogUtil.getLogger(SchemaCommentHandler.class);
	
	/**
	 * Check whether installed tables and columns description meta table.
	 * 
	 * @param conn Connection
	 * @return
	 */
	public static boolean isInstalledMetaTable(IDatabaseSpec dbSpec, Connection conn) {
		if (CompatibleUtil.isCommentSupports(dbSpec)) {
			return true;
		}

		// Are there the description table?
		String sql = "SELECT COUNT(*)"
				+ " FROM db_class"
				+ " WHERE class_name='" + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE + "'";

		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
		
		return false;
	}
	
	/**
	 * Install tables and columns description meta table.
	 * 
	 * @param conn Connection it can be rollback transaction if it failed to
	 *        create the table.
	 * @return
	 */
	public static boolean installMetaTable(IDatabaseSpec dbSpec, Connection conn) {
		if (conn == null) {
			return false;
		}

		// I will create the description table if there aren't it.
		String sql = new StringBuilder()
			.append("CREATE TABLE " + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE + " \n")
			.append("(\n")
			.append("   table_name VARCHAR(255) NOT NULL, \n")
			.append("   column_name VARCHAR(255) NOT NULL, \n")
			.append("   description VARCHAR(4096), \n")
			.append("   last_updated TIMESTAMP, \n")
			.append("   last_updated_user VARCHAR(4096), \n")
			.append("   CONSTRAINT pk" + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE + " \n")
			.append("      PRIMARY KEY(table_name, column_name) \n")
			.append(")\n")
			.toString();

		boolean isSupportReuseOid = CompatibleUtil.isSupportReuseOID(dbSpec);
		if (isSupportReuseOid) {
			sql += "REUSE_OID\n";
		}

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			QueryUtil.rollback(conn);
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt);
		}
		
		sql = "CALL CHANGE_OWNER('" + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE + "', 'PUBLIC')"
				+ " ON CLASS db_authorizations";

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		try {
			stmt = conn.createStatement();
			stmt.executeQuery(sql);
		} catch (Exception e) {
			QueryUtil.rollback(conn);
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt);
		}
		
		QueryUtil.commit(conn);
		return true;
	}
	
	private static SchemaComment resultToMetaDesc(ResultSet rs) 
			throws SQLException {
		SchemaComment meta = new SchemaComment();
		meta.setTable(rs.getString("table_name"));
		String columnName = rs.getString("column_name");
		if (StringUtil.isEqual(columnName, "*")) {
			columnName = null;
		}
		meta.setColumn(columnName);
		meta.setDescription(rs.getString("description"));
		return meta;
	}

	public static Map<String, SchemaComment> loadDescriptions(IDatabaseSpec dbSpec, Connection conn)
			throws SQLException {
		return loadDescription(dbSpec, conn, null);
	}

	public static Map<String, SchemaComment> loadTableDescriptions(IDatabaseSpec dbSpec, Connection conn) 
			throws SQLException {
		boolean isSupportInEngine = CompatibleUtil.isCommentSupports(dbSpec);
		String sql = null;

		if(isSupportInEngine) {
			sql = "SELECT class_name as table_name, null as column_name, comment as description "
					+ "FROM db_class "
					+ "WHERE is_system_class='NO'";
		} else {
			sql = "SELECT LOWER(table_name) as table_name, LOWER(column_name) as column_name, description"
					+ " FROM " + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE
					+ " WHERE LOWER(table_name) LIKE '%' AND column_name = '*'";
		}

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		Map<String, SchemaComment> results = new HashMap<String, SchemaComment>();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				SchemaComment meta = resultToMetaDesc(rs);
				results.put(meta.getId(), meta);
			}
		} catch (SQLException e) {
			QueryUtil.rollback(conn);
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
		
		return results;
	}

	public static Map<String, SchemaComment> loadDescription(IDatabaseSpec dbSpec, 
			Connection conn, String tableName) throws SQLException {
		boolean isSupportInEngine = CompatibleUtil.isCommentSupports(dbSpec);
		String sql = null;
		String tableCondition = null;
		String columnCondition = null;

		if (isSupportInEngine) {
			sql = "SELECT class_name as table_name, null as column_name, comment as description "
					+ "FROM db_class "
					+ "WHERE is_system_class='NO' %s"
					+ "UNION ALL "
					+ "SELECT class_name as table_name, attr_name as column_name, comment as description "
					+ "FROM db_attribute %s";
			if (StringUtil.isNotEmpty(tableName)) {
				tableCondition = "AND class_name = '" + tableName + "' ";
				columnCondition = "WHERE class_name = '" + tableName + "'";
			} else {
				tableCondition = "AND comment is not null ";
				columnCondition = "WHERE comment is not null";
			}
			sql = String.format(sql, tableCondition, columnCondition);
		} else {
			sql = "SELECT LOWER(table_name) as table_name, LOWER(column_name) as column_name, description"
					+ " FROM " + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE;
			if (StringUtil.isNotEmpty(tableName)) {
				String pureTableName = tableName.replace("\"", "");
				sql += " WHERE LOWER(table_name)='" + pureTableName.toLowerCase() + "'";
			}
		}

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		Map<String, SchemaComment> results = new HashMap<String, SchemaComment>();
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				SchemaComment meta = resultToMetaDesc(rs);
				results.put(meta.getId(), meta);
			}
		} catch (SQLException e) {
			QueryUtil.rollback(conn);
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
		
		return results;
	}

	public static SchemaComment loadObjectDescription(IDatabaseSpec dbSpec,
			Connection conn, String objName, CommentType type) throws SQLException {
		String sql = null;

		switch (type) {
		case INDEX:
			sql = "SELECT index_name, comment " +
					"FROM db_index " +
					"WHERE index_name = ?";
			break;
		case VIEW:
			sql = "SELECT vclass_name, comment " +
					"FROM db_vclass " +
					"WHERE vclass_name = ?";
			break;
		case SP:
			sql = "SELECT sp_name, comment " +
					"FROM db_stored_procedure " +
					"WHERE sp_name = ?";
			break;
		case TRIGGER:
			sql = "SELECT name, comment " +
			"FROM db_trigger " +
			"WHERE name = ?";
			break;
		case SERIAL:
			sql = "SELECT name, comment " +
					"FROM db_serial " +
					"WHERE name = ?";
			break;
		case USER:
			sql = "SELECT name, comment " +
					"FROM db_user " +
					"WHERE name = ?";
			break;
		case PARTITION:
			sql = "SELECT partition_name, comment " +
					"FROM db_partition " +
					"WHERE partition_name = ?";
			break;
		}

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		SchemaComment schemaComment = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, objName);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				schemaComment = new SchemaComment();
				schemaComment.setType(type);
				schemaComment.setObjectName(rs.getString(1));
				schemaComment.setDescription(rs.getString(2));
			}
		} catch (SQLException e) {
			QueryUtil.rollback(conn);
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
			QueryUtil.freeQuery(pstmt, rs);
		}

		return schemaComment;
	}

	public static void updateDescription(IDatabaseSpec dbSpec, Connection conn,
			String tableName, String columnName, String description) throws SQLException {

		String pureTableName = tableName.replace("\"", "");
		String pureColumnName = StringUtil.isEmpty(columnName) ? "*" : columnName.replace("\"\\[\\]\\'", "");

		if (CompatibleUtil.isCommentSupports(dbSpec)) {
			description = StringUtil.escapeQuotes("'" + description + "'");
			String sql = null;
			sql = generateDescriptionSql(conn, pureTableName, pureColumnName, description);

			// [TOOLS-2425]Support shard broker
			if (dbSpec.isShard()) {
				sql = dbSpec.wrapShardQuery(sql);
			}

			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(sql);
				stmt.execute();
				QueryUtil.commit(conn);
			} catch (SQLException e) {
				if (e.getErrorCode() != -670) {
					LOGGER.error(e.getMessage(), e);
				}
			} finally {
				QueryUtil.freeQuery(stmt);
			}
		} else {
			String sql = "INSERT INTO " + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE +" ("
					+ "table_name, column_name, description, last_updated,"
					+ " last_updated_user) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_USER)";

			// [TOOLS-2425]Support shard broker
			if (dbSpec.isShard()) {
				sql = dbSpec.wrapShardQuery(sql);
			}

			PreparedStatement stmt = null;
			try {
				int i = 1;
				stmt = conn.prepareStatement(sql);
				stmt.setString(i++, pureTableName);
				stmt.setString(i++, pureColumnName);
				stmt.setString(i++, description);
				stmt.executeUpdate();
				QueryUtil.commit(conn);
				return;
			} catch (SQLException e) {
				if (e.getErrorCode() != -670) {
					LOGGER.error(e.getMessage(), e);
				}
			} finally {
				QueryUtil.freeQuery(stmt);
			}

			sql = "UPDATE " + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE
					+ " SET description=?, last_updated=CURRENT_TIMESTAMP,"
					+ " last_updated_user=CURRENT_USER"
					+ " WHERE LOWER(table_name)=? AND LOWER(column_name)=?";

			// [TOOLS-2425]Support shard broker
			if (dbSpec.isShard()) {
				sql = dbSpec.wrapShardQuery(sql);
			}

			try {
				int i = 1;
				stmt = conn.prepareStatement(sql);
				stmt.setString(i++, description);
				stmt.setString(i++, pureTableName.toLowerCase());
				stmt.setString(i++, pureColumnName.toLowerCase());
				stmt.executeUpdate();
				QueryUtil.commit(conn);
			} catch (SQLException e) {
				QueryUtil.rollback(conn);
				LOGGER.error(e.getMessage(), e);
				throw e;
			} finally {
				QueryUtil.freeQuery(stmt);
			}
		}
	}

	public static String generateDescriptionSql(Connection conn, String tableName,
			String columnName, String description) throws SQLException {
		String sql = null;
		if (columnName.equals("*")) {	// '*' means description is for table
			sql = "ALTER TABLE " + QuerySyntax.escapeKeyword(tableName) +
					" COMMENT " + description;
		} else {	// description for column
			sql = QueryUtil.getColumnDescSql(conn, tableName, columnName);
			sql = String.format(sql, description);
		}
		return sql + ";";
	}

	public static void deleteDescription(IDatabaseSpec dbSpec, Connection conn,
			String tableName) throws SQLException {
		if (CompatibleUtil.isCommentSupports(dbSpec)) {
			return;
		}
		String pureTableName = tableName.replace("\"", "");
		String sql = "DELETE FROM " + ConstantsUtil.SCHEMA_DESCRIPTION_TABLE
				+ " WHERE LOWER(table_name)='" + pureTableName.toLowerCase() + "'";

		// [TOOLS-2425]Support shard broker
		if (dbSpec.isShard()) {
			sql = dbSpec.wrapShardQuery(sql);
		}

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			QueryUtil.rollback(conn);
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
			QueryUtil.freeQuery(stmt);
		}
		
		QueryUtil.commit(conn);
	}

	/**
	 * find MetaDesc object on metaMap by tableName, columnName
	 * 
	 * @param metaMap
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public static SchemaComment find(Map<String, SchemaComment> metaMap, String tableName, String columnName) {
		if (metaMap == null || StringUtil.isEmpty(tableName)) {
			return null;
		}

		String key = null;
		if (!StringUtil.isEmpty(columnName)) {
			key = tableName + "*" + columnName;
		} else {
			key = tableName + "*";
		}
		
		return metaMap.get(key);
	}

	/**
	 * Bind schema descriptions to SchemaInfo object.
	 *
	 * @param comments
	 * @param schema
	 */
	public static void bindSchemaInfo(Map<String, SchemaComment> comments, SchemaInfo schema) {
		if (comments == null || schema == null) {
			return;
		}
		
		String tableName = schema.getClassname();
		SchemaComment cmt = find(comments, tableName, null);
		if (cmt != null) {
			schema.setDescription(cmt.getDescription());
		}

		if (schema.getAttributes() == null) {
			return;
		}

		for (DBAttribute attr : schema.getAttributes()) {
			if (attr.getName() == null) {
				continue;
			}

			cmt = find(comments, tableName, attr.getName());
			if (cmt != null) {
				attr.setDescription(cmt.getDescription());
			}
		}
	}
	
	/**
	 * Build insert sql based on current time and current user paras.
	* 
	* @param tableName
	* @param columnName
	* @param desc
	* @return String
	 */
	public static String buildInsertSQL(String tableName, String columnName, String desc) {
		String pureTableName = tableName.replace("\"", "");
		String pureColumnName = StringUtil.isEmpty(columnName) ? "*" : columnName.replace(
				"\"\\[\\]\\'", "");
		StringBuilder sqlSB = new StringBuilder("INSERT INTO ");
		sqlSB.append(ConstantsUtil.SCHEMA_DESCRIPTION_TABLE );
		sqlSB.append(" (table_name, column_name, description, last_updated, last_updated_user) VALUES ('");
		sqlSB.append(pureTableName);
		sqlSB.append("', '");
		sqlSB.append(pureColumnName);
		sqlSB.append("', '");
		sqlSB.append(desc);
		sqlSB.append("', CURRENT_TIMESTAMP, CURRENT_USER);");
		return sqlSB.toString();
	}
	
	/**
	 * Build update sql based on current time and current user paras.
	* 
	* @param tableName
	* @param columnName
	* @param desc
	* @return String
	 */
	public static String buildUpdateSQL(String tableName, String columnName, String desc) {
		String pureTableName = tableName.replace("\"", "");
		String pureColumnName = StringUtil.isEmpty(columnName) ? "*" : columnName.replace(
				"\"\\[\\]\\'", "");
		StringBuilder sqlSB = new StringBuilder("UPDATE ");
		sqlSB.append(ConstantsUtil.SCHEMA_DESCRIPTION_TABLE );
		sqlSB.append(" SET description = '");
		sqlSB.append(desc);
		sqlSB.append("', last_updated = CURRENT_TIMESTAMP, last_updated_user = CURRENT_USER WHERE LOWER(table_name) = '");
		sqlSB.append(pureTableName.toLowerCase());
		sqlSB.append("' AND LOWER(column_name) = '");
		sqlSB.append(pureColumnName.toLowerCase());
		sqlSB.append("';");
		return sqlSB.toString();
	}
}
