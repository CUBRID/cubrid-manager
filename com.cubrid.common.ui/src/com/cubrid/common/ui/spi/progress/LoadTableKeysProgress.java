/*
 * Copyright (C) 2018 CUBRID Co., Ltd. All rights reserved by CUBRID Co., Ltd.
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

package com.cubrid.common.ui.spi.progress;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;

/**
 * The Progress class that gets the number of keys in the table.
 *
 * @author hun-a
 *
 */
public class LoadTableKeysProgress extends LoadTableProgress {
	private final int PK = 0;
	private final int UK = 1;
	private final int FK = 2;
	private final int INDEX = 3;

	public LoadTableKeysProgress(CubridDatabase database,
			List<TableDetailInfo> tableList, String taskName, String subTaskName) {
		super(database, tableList, taskName, subTaskName);
	}

	@Override
	protected Object count(Connection conn, String tableName) {
		int keyCount[] = new int[4];
		try {
			if (conn == null || conn.isClosed()) {
				return keyCount;
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
		}

		String sql = "SELECT "
				+ "		SUM(CASE WHEN is_unique = 'YES' AND is_primary_key = 'YES' THEN 1 ELSE 0 END ) AS count_primary_key, "
				+ "		SUM(CASE WHEN is_unique = 'YES' AND is_primary_key = 'NO' THEN 1 ELSE 0 END ) AS count_unique, " 
				+ "		SUM(DECODE(is_foreign_key, 'YES', 1, 0)) AS count_foreign_key, "
				+ "		SUM(CASE WHEN is_unique = 'NO' AND is_primary_key = 'NO' THEN 1 ELSE 0 END ) AS count_index " 
				+ "FROM "
				+ "		db_index "
				+ "WHERE "
				+ "		class_name = ?";

		// [TOOLS-2425]Support shard broker
		if (CubridDatabase.hasValidDatabaseInfo(database)) {
			sql = database.getDatabaseInfo().wrapShardQuery(sql);
		}

		CUBRIDPreparedStatementProxy stmt = null;
		ResultSet rs = null;
		try {
			stmt = QueryExecuter.getStatement(conn, sql, false, false);
			stmt.setString(1, tableName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				keyCount[PK] = rs.getInt(1);
				keyCount[UK] = rs.getInt(2);
				keyCount[FK] = rs.getInt(3);
				keyCount[INDEX] = rs.getInt(4);
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
			e.printStackTrace();
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return keyCount;
	}

	@Override
	protected void setCount(TableDetailInfo tablesDetailInfo, Object count) {
		int[] keyCount = (int[]) count;
		tablesDetailInfo.setPkCount(keyCount[PK]);
		tablesDetailInfo.setUkCount(keyCount[UK]);
		tablesDetailInfo.setFkCount(keyCount[FK]);
		tablesDetailInfo.setIndexCount(keyCount[INDEX]);
	}

}
