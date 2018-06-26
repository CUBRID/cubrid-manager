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

import java.math.BigDecimal;
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
 * The Progress class that gets the number of record size in the table.
 *
 * @author hun-a
 *
 */
public class LoadTableRecordSizeProgress extends LoadTableProgress {
	private final int RECORD_SIZE = 0;
	private final int UNCOUNT_COLUMN_SIZE = 1;

	public LoadTableRecordSizeProgress(CubridDatabase database,
			List<TableDetailInfo> tableList, String taskName, String subTaskName) {
		super(database, tableList, taskName, subTaskName);
	}

	@Override
	protected Object count(Connection conn, String tableName) {
		Object[] values = new Object[2];
		try {
			if (conn == null || conn.isClosed()) {
				return values;
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
		}

		String sql = "SELECT "
				+ "    CAST(SUM( "
				+ "    CASE "
				+ "          WHEN "
				+ "              data_type = 'BIGINT' THEN 8.0 "
				+ "          WHEN "
				+ "              data_type = 'INTEGER' THEN 4.0 "
				+ "          WHEN "
				+ "              data_type = 'SMALLINT' THEN 2.0 "
				+ "          WHEN "
				+ "              data_type = 'FLOAT' THEN 4.0 "
				+ "          WHEN "
				+ "              data_type = 'DOUBLE' THEN 8.0 "
				+ "          WHEN "
				+ "              data_type = 'MONETARY' THEN 12.0 "
				+ "          WHEN "
				+ "              data_type = 'STRING' THEN prec "
				+ "          WHEN "
				+ "              data_type = 'VARCHAR' THEN prec "
				+ "          WHEN "
				+ "              data_type = 'NVARCHAR' THEN prec "
				+ "          WHEN "
				+ "              data_type = 'CHAR' THEN prec "
				+ "          WHEN "
				+ "              data_type = 'NCHAR' THEN prec "
				+ "          WHEN "
				+ "              data_type = 'TIMESTAMP' THEN 8.0 "
				+ "          WHEN "
				+ "              data_type = 'DATE' THEN 4.0 "
				+ "          WHEN "
				+ "              data_type = 'TIME' THEN 4.0 "
				+ "          WHEN "
				+ "              data_type = 'DATETIME' THEN 4.0 "
				+ "          WHEN "
				+ "              data_type = 'BIT' THEN FLOOR(prec / 8.0) "
				+ "          WHEN "
				+ "              data_type = 'BIT VARYING' THEN FLOOR(prec / 8.0) "
				+ "          ELSE 0 "
				+ "    END ) AS BIGINT) AS size_column, "
				+ "    SUM( "
				+ "    CASE "
				+ "          WHEN "
				+ "              data_type = 'STRING' THEN 1 "
				+ "          WHEN "
				+ "              data_type = 'VARCHAR' THEN 1 "
				+ "          WHEN "
				+ "              data_type = 'NVARCHAR' THEN 1 "
				+ "          WHEN "
				+ "              data_type = 'NCHAR' THEN 1 "
				+ "          WHEN "
				+ "              data_type = 'BIT VARYING' THEN 1 "
				+ "          ELSE 0 "
				+ "    END ) AS size_over_column "
				+ "FROM "
				+ "    db_attribute "
				+ "WHERE "
				+ "    class_name = ?";

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
				values[RECORD_SIZE] = rs.getBigDecimal(1);
				values[UNCOUNT_COLUMN_SIZE] = rs.getInt(2) > 0;
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
			e.printStackTrace();
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return values;
	}

	@Override
	protected void setCount(TableDetailInfo tablesDetailInfo, Object count) {
		Object[] values = (Object[]) count;
		tablesDetailInfo.setRecordsSize((BigDecimal) values[RECORD_SIZE]);
		tablesDetailInfo.setHasUnCountColumnSize(Boolean.parseBoolean((values[UNCOUNT_COLUMN_SIZE]).toString()));
	}

}
