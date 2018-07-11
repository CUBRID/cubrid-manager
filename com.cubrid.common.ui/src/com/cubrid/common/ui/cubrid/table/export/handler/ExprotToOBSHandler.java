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
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.cubrid.table.event.ExportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * ExprotToOBSHandler Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class ExprotToOBSHandler extends
		AbsExportDataHandler {

	private static final Logger LOGGER = LogUtil.getLogger(ExprotToOBSHandler.class);

	public ExprotToOBSHandler(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler) {
		super(dbInfo, exportConfig, exportDataEventHandler);
	}

	public void handle(String tableName) throws IOException, SQLException { // FIXME move this logic to core module

		String schemaFile = exportConfig.getSchemaFilePath();
		String indexFile = exportConfig.getIndexFilePath();
		String triggerFile = exportConfig.getTriggerFilePath();
		Set<String> tableSet = new HashSet<String>();
		tableSet.addAll(exportConfig.getTableNameList());

		try {
			try {
				if (exportConfig.isExportSchema()) {
					exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(schemaFile));
				}

				if (exportConfig.isExportIndex()) {
					exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(indexFile));
				}

				if (exportConfig.isExportTrigger()) {
					exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(triggerFile));
				}

				exportSchemaToOBSFile(dbInfo, exportDataEventHandler, tableSet, schemaFile,
						indexFile, triggerFile, exportConfig.getFileCharset(),
						exportConfig.isExportSerialStartValue(), false);

				if (exportConfig.isExportSchema()) {
					exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(schemaFile));
					exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(schemaFile));
				}
				if (exportConfig.isExportIndex()) {
					exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(indexFile));
					exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(indexFile));
				}
				if (exportConfig.isExportTrigger()) {
					exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(triggerFile));
					exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(triggerFile));
				}

				if (exportConfig.isExportData()) { //data
					long totalRecord = exportConfig.getTotalCount(tableName);
					if (totalRecord == 0) {
						return;
					}
					String path = exportConfig.getDataFilePath(tableName);
					BufferedWriter fs = null;
					Connection conn = null;

					try {
						fs = FileUtil.getBufferedWriter(path, exportConfig.getFileCharset());
						conn = getConnection();

						if (exportConfig.isExportData()) {

							exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(
									path));

							String sql = getSelectSQL(conn, tableName);

							// [TOOLS-2425]Support shard broker
							sql = DatabaseInfo.wrapShardQuery(dbInfo, sql);

							exportLoad(conn, tableName, fs, path, sql);

							exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(
									path));
						}
					} finally {
						QueryUtil.freeQuery(conn);
						try {
							if (fs != null) {
								fs.close();
							}
						} catch (IOException e) {
							LOGGER.error("", e);
						}
					}
				}

			} catch (Exception e) {
				LOGGER.error("create schema index error : ", e);
				exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(tableName));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}

	}

	/**
	 * Export data as CUBRID load format
	 *
	 * @param stmt Statement
	 * @param tableName String
	 * @param monitor IProgressMonitor
	 * @param fs String
	 * @param fileName BufferedWriter
	 * @throws SQLException The exception
	 * @throws IOException The exception
	 */
	private void exportLoad(Connection conn, String tableName, BufferedWriter fs, String fileName,
			String sql) throws SQLException, IOException { // FIXME move this logic to core module
		CUBRIDPreparedStatementProxy pStmt = null;
		CUBRIDResultSetProxy rs = null;

		boolean hasNextPage = true;
		long beginIndex = 1;
		int exportedCount = 0;
		long totalRecord = exportConfig.getTotalCount(tableName);
		String whereCondition = exportConfig.getWhereCondition(tableName);
		isPaginating = isPagination(tableName, sql, whereCondition);
		boolean isNeedWriteHeader = true;
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

				if (isNeedWriteHeader) {
					StringBuffer header = new StringBuffer("%class ");
					header.append(QuerySyntax.escapeKeyword(tableName));
					header.append(" (");
					for (int i = 1; i < rsmt.getColumnCount() + 1; i++) {
						if (i > 1) {
							header.append(" ");
						}
						header.append(QuerySyntax.escapeKeyword(rsmt.getColumnName(i)));
					}
					header.append(")\n");
					fs.write(header.toString());
					isNeedWriteHeader = false;
				}

				while (rs.next()) {
					StringBuffer values = new StringBuffer();
					for (int j = 1; j < rsmt.getColumnCount() + 1; j++) {
						String columnType = rsmt.getColumnTypeName(j);
						int precision = rsmt.getPrecision(j);
						columnType = FieldHandlerUtils.amendDataTypeByResult(rs, j, columnType);
						setIsHasBigValue(columnType, precision);
						values.append(FieldHandlerUtils.getRsValueForExportOBS(columnType, rs, j).toString());
					}
					values.append("\n");

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
				LOGGER.error("export date write load db error : ", e);
			} finally {
				fs.flush();
				QueryUtil.freeQuery(pStmt, rs);
			}

			if (hasNextPage(beginIndex, totalRecord)) {
				hasNextPage = true;
			} else {
				hasNextPage = false;
			}

			System.gc();
		}
	}
}
