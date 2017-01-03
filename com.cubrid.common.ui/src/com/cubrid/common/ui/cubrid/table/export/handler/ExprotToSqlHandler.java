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
package com.cubrid.common.ui.cubrid.table.export.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.export.ResultSetDataCache;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * Exprot To Sql Handler
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class ExprotToSqlHandler extends
		AbsExportDataHandler {
	private static final Logger LOGGER = LogUtil.getLogger(ExprotToSqlHandler.class);

	public ExprotToSqlHandler(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler) {
		super(dbInfo, exportConfig, exportDataEventHandler);
	}

	public void handle(String tableName) throws IOException, SQLException { // FIXME move this logic to core module
		if(exportConfig.isExportFromCache()){
			exportFromCache(tableName);
		}else{
			exportByQuerying(tableName);
		}
	}
	
	public void exportByQuerying(String tableName) throws IOException, SQLException {
		if (StringUtil.isEmpty(tableName)) {
			return;
		}
		
		long totalRecord = exportConfig.getTotalCount(tableName);
		if (totalRecord == 0) {
			return;
		}

		BufferedWriter fs = null;
		String whereCondition = exportConfig.getWhereCondition(tableName);
		boolean hasNextPage = true;
		long beginIndex = 1;
		int exportedCount = 0;
		Connection conn = null;
		CUBRIDPreparedStatementProxy pStmt = null;
		CUBRIDResultSetProxy rs = null;

		try {
			conn = getConnection();
			fs = FileUtil.getBufferedWriter(exportConfig.getDataFilePath(tableName),
					exportConfig.getFileCharset());
			String sql = QueryUtil.getSelectSQL(conn, tableName); 
			isPaginating = isPagination(tableName, sql, whereCondition);
			while (hasNextPage) {
				try {
					String executeSQL = null;
					if (isPaginating) {
						long endIndex = beginIndex + RSPAGESIZE;
						executeSQL = getExecuteSQL(sql, beginIndex, endIndex, whereCondition);
						executeSQL = dbInfo.wrapShardQuery(executeSQL);
						beginIndex = endIndex + 1;

					} else {
						executeSQL = getExecuteSQL(sql, whereCondition);
						executeSQL = dbInfo.wrapShardQuery(sql);
						beginIndex = totalRecord + 1;
					}

					pStmt = getStatement(conn, executeSQL, tableName);
					rs = (CUBRIDResultSetProxy) pStmt.executeQuery();

					CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();

					StringBuffer insert = new StringBuffer("INSERT INTO ");
					insert.append(QuerySyntax.escapeKeyword(tableName));
					insert.append(" (");
					for (int i = 1; i < rsmt.getColumnCount() + 1; i++) {
						if (i > 1) {
							insert.append(", ");
						}
						insert.append(QuerySyntax.escapeKeyword(rsmt.getColumnName(i)));
					}
					insert.append(") ");

					while (rs.next()) {
						StringBuffer values = new StringBuffer("VALUES (");
						for (int j = 1; j < rsmt.getColumnCount() + 1; j++) {
							if (j > 1) {
								values.append(", ");
							}
							String columnType = rsmt.getColumnTypeName(j);
							int precision = rsmt.getPrecision(j);
							columnType = FieldHandlerUtils.amendDataTypeByResult(rs, j, columnType);
							setIsHasBigValue(columnType, precision);
							values.append(FieldHandlerUtils.getRsValueForExportSQL(columnType, rs,
									j).toString());
						}
						values.append(");\n");
						fs.write(insert.toString());
						fs.write(values.toString());
						exportedCount++;
						if (exportedCount >= COMMIT_LINES) {
							fs.flush();
							exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(
									tableName, exportedCount));
							exportedCount = 0;
						}

						if (stop) {
							break;
						}
					}
					exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(tableName,
							exportedCount));
					exportedCount = 0;
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(tableName));
				} finally {
					QueryUtil.freeQuery(pStmt, rs);
				}

				if (hasNextPage(beginIndex, totalRecord)) {
					hasNextPage = true;
				} else {
					hasNextPage = false;
				}

				System.gc();
			}
		} finally {
			QueryUtil.freeQuery(conn);
			FileUtil.close(fs);
		}
	}
	
	public void exportFromCache(String tableName) throws IOException {
		if (StringUtil.isEmpty(tableName)) {
			return;
		}

		BufferedWriter fs = null;
		int exportedCount = 0;
		ResultSetDataCache resultSetDataCache = exportConfig.getResultSetDataCache();

		try {
			fs = FileUtil.getBufferedWriter(exportConfig.getDataFilePath(tableName),
					exportConfig.getFileCharset());
			try {
				List<ColumnInfo> columnInfos = resultSetDataCache.getColumnInfos();
				int colCount = columnInfos.size();
				StringBuffer insert = new StringBuffer("INSERT INTO ");
				insert.append(QuerySyntax.escapeKeyword(tableName));
				insert.append(" (");
				for (int i = 0; i < colCount; i++) {
					if (i > 0) {
						insert.append(", ");
					}
					insert.append(QuerySyntax.escapeKeyword(columnInfos.get(i).getName()));
				}
				insert.append(") ");

				List<ArrayList<Object>> datas = resultSetDataCache.getDatas();
				for (ArrayList<Object> rowData : datas) {
					StringBuffer values = new StringBuffer("VALUES (");
					for (int j = 0; j < colCount; j++) {
						if (j > 0) {
							values.append(", ");
						}
						int precision = columnInfos.get(j).getPrecision();
						String columnType = columnInfos.get(j).getType();
						setIsHasBigValue(columnType, precision);
						Object value = rowData.get(j);
						if (DataType.DATATYPE_BLOB.equals(columnType)
								|| DataType.DATATYPE_CLOB.equals(columnType)) {
							value = DataType.VALUE_NULL;
						}
						values.append(value.toString());
					}
					values.append(");\n");
					fs.write(insert.toString());
					fs.write(values.toString());
					exportedCount++;
					if (exportedCount >= COMMIT_LINES) {
						fs.flush();
						exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(tableName,
								exportedCount));
						exportedCount = 0;
					}

					if (stop) {
						break;
					}
				}
				exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(tableName,
						exportedCount));
				exportedCount = 0;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(tableName));
			}
			System.gc();
		} finally {
			FileUtil.close(fs);
		}
	}
}
