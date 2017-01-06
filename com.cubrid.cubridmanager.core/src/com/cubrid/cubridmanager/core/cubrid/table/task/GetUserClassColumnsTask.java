/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import com.cubrid.cubridmanager.core.utils.SchemaUtil;

/**
 * This task is responsible for getting the columns of a class
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-7 created by lizhiqiang
 */
public class GetUserClassColumnsTask extends
		JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(GetUserClassColumnsTask.class);
	private boolean isInTransation;

	public GetUserClassColumnsTask(DatabaseInfo dbInfo) {
		super("getUserClassColumns", dbInfo);
	}

	/**
	 * Get all column name and type info into list
	 *
	 * @param tableName String the table name
	 * @return List<String[]>
	 */
	public List<TableColumn> getColumns(String tableName) {
		List<TableColumn> columns = new ArrayList<TableColumn>();
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return columns;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return columns;
			}
			columns = SchemaUtil.getTableColumn(databaseInfo, connection, tableName);
			List<String> pkColumns = QueryUtil.getPrimaryKeys(connection, tableName);

			for (TableColumn dbColumn : columns) {
				String columnName = dbColumn.getColumnName();
				for (String pkColumn : pkColumns) {
					if (columnName.equals(pkColumn)) {
						dbColumn.setPrimaryKey(true);
					}
				}
			}
			Collections.sort(columns);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			if (e.getErrorCode() == -74) {
				isInTransation = true;
			}
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
		return columns;
	}

	/**
	 * Get all column name and type info into list
	 *
	 * @param tableNames String the table name
	 * @return Map<String, List<TableColumn>>
	 */
	public Map<String, List<TableColumn>> getColumns(List<String> tableNames) {
		Map<String, List<TableColumn>> columnsOfTable = new HashMap<String, List<TableColumn>>();
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return columnsOfTable;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return columnsOfTable;
			}
			// make talble_pk column hashed string list
			Set<String> hashSet = new HashSet<String>();
			Statement pkStmt = null;
			ResultSet pkRs = null;
			try {
				String sql = new StringBuilder()
				.append("SELECT \n")
				.append("    LOWER(CONCAT(i.class_name, '-', k.key_attr_name)) AS hashcode \n")
				.append("FROM \n")
				.append("    db_index i, \n")
				.append("    db_index_key k \n")
				.append("WHERE \n")
				.append("    i.index_name = k.index_name \n")
				.append("    AND \n")
				.append("    i.class_name = k.class_name \n")
				.append("    AND \n")
				.append("    i.is_primary_key = 'YES'\n")
				.toString();

				// [TOOLS-2425]Support shard broker
				sql = databaseInfo.wrapShardQuery(sql);

				pkStmt = connection.createStatement();
				pkRs = pkStmt.executeQuery(sql.toString());
				while (pkRs.next()) {
					hashSet.add(pkRs.getString(1));
				}
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				QueryUtil.freeQuery(pkStmt, pkRs);
			}

			for (String tableName : tableNames) {
				try {
					List<TableColumn> columns = SchemaUtil.getTableColumn(databaseInfo, connection,
							tableName);

					for (TableColumn dbColumn : columns) {
						String columnName = dbColumn.getColumnName();
						String tableColumnHash = (tableName + "-" + columnName).trim().toLowerCase();
						if (hashSet.contains(tableColumnHash)) {
							dbColumn.setPrimaryKey(true);
						}
					}

					Collections.sort(columns);
					columnsOfTable.put(tableName, columns);
				} catch (SQLException e) {
					LOGGER.error(e.getMessage(), e);
					if (e.getErrorCode() == -74) {
						isInTransation = true;
					}
					if (errorMsg == null) {
						errorMsg = e.getMessage() + "\r\n";
					} else {
						errorMsg += e.getMessage() + "\r\n";
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			isInTransation = false;
			if (errorMsg == null) {
				errorMsg = e.getMessage() + "\r\n";
			} else {
				errorMsg += e.getMessage() + "\r\n";
			}
		} finally {
			finish();
		}
		return columnsOfTable;
	}

	/**
	 * Whether the field isInTransation is true
	 *
	 * @return the isInTransation
	 */
	public boolean isInTransation() {
		return isInTransation;
	}
}
