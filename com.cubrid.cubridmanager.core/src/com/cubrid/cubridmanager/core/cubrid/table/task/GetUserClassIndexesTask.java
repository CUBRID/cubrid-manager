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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableIndex;

/**
 * This task is responsible for getting the columns of a class
 * 
 * @author lizhiqiang
 * @version 1.0 - 2010-12-7 created by lizhiqiang
 */
public class GetUserClassIndexesTask extends
		JDBCTask {

	public GetUserClassIndexesTask(DatabaseInfo dbInfo) {
		super("getUserClassColumns", dbInfo);
	}

	/**
	 *Get all column name and type info into list
	 * 
	 * @param tableName String the table name
	 * @return List<String[]>
	 */
	public List<TableIndex> getIndexesNames(String tableName) {
		List<TableIndex> indexes = new ArrayList<TableIndex>();
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return indexes;
			}

			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return indexes;
			}

			String sql = "SELECT * FROM db_index WHERE class_name=?";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			stmt = connection.prepareStatement(sql);
			((PreparedStatement) stmt).setString(1,
					tableName.toLowerCase(Locale.getDefault()));
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String indexName = rs.getString("index_name");
				String pk = rs.getString("is_primary_key");
				String fk = rs.getString("is_foreign_key");
				String unique = rs.getString("is_unique");
				String reverse = rs.getString("is_reverse");
				TableIndex dbIndex = new TableIndex();
				dbIndex.setIndexName(indexName);
				dbIndex.setPrimaryKey(pk);
				dbIndex.setForeignKey(fk);
				dbIndex.setUnique(unique);
				dbIndex.setReverse(reverse);
				indexes.add(dbIndex);
			}
			QueryUtil.freeQuery(stmt, rs);

			boolean isSupportFunIndex = CompatibleUtil.isSupportFuncIndex(databaseInfo);
			String funcIndex = isSupportFunIndex ? ", func" : "";

			sql = "SELECT key_attr_name" + funcIndex
					+ " FROM db_index_key WHERE index_name=? AND class_name=?";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			stmt = connection.prepareStatement(sql);
			for (TableIndex dbIndex : indexes) {
				String indexName = dbIndex.getIndexName();
				((PreparedStatement) stmt).setString(1,
						indexName.toLowerCase(Locale.getDefault()));
				((PreparedStatement) stmt).setString(2,
						tableName.toLowerCase(Locale.getDefault()));
				rs = ((PreparedStatement) stmt).executeQuery();
				List<String> columns = new ArrayList<String>();
				while (rs.next()) {
					String attrName = rs.getString("key_attr_name");
					if (isSupportFunIndex && attrName == null) {
						columns.add(rs.getString("func").trim());
					} else {
						columns.add(rs.getString("key_attr_name"));
					}
				}
				dbIndex.setColumns(columns);
			}
		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
		return indexes;
	}
}
