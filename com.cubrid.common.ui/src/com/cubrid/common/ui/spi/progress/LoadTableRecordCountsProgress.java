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

package com.cubrid.common.ui.spi.progress;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;

public class LoadTableRecordCountsProgress extends LoadTableProgress {

	public LoadTableRecordCountsProgress(CubridDatabase database,
			List<TableDetailInfo> tableList, String taskName, String subTaskName) {
		super(database, tableList, taskName, subTaskName);
	}

	@Override
	protected Object count(Connection conn, String tableName) {
		int recordsCount = 0;
		try {
			if (conn == null || conn.isClosed()) {
				return recordsCount;
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
		}

		String sql = "SELECT COUNT(*) FROM " + QuerySyntax.escapeKeyword(tableName);

		// [TOOLS-2425]Support shard broker
		if (CubridDatabase.hasValidDatabaseInfo(database)) {
			sql = database.getDatabaseInfo().wrapShardQuery(sql);
		}

		CUBRIDPreparedStatementProxy stmt = null;
		ResultSet rs = null;
		try {
			stmt = QueryExecuter.getStatement(conn, sql, false, false);
			rs = stmt.executeQuery();
			if (rs.next()) {
				recordsCount = rs.getInt(1);
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
			e.printStackTrace();
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return recordsCount;
	}

	@Override
	protected void setCount(TableDetailInfo tablesDetailInfo, Object count) {
		tablesDetailInfo.setRecordsCount(Integer.parseInt(count.toString()));
	}
}
