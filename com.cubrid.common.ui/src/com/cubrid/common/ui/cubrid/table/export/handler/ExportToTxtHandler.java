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
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
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
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * ExportToTxtHandler Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class ExportToTxtHandler extends
		AbsExportDataHandler {
	private static final Logger LOGGER = LogUtil.getLogger(ExportToTxtHandler.class);

	protected String surround = "";
	protected String columnSeprator;
	protected String rowSeprator;

	public ExportToTxtHandler(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler) {
		super(dbInfo, exportConfig, exportDataEventHandler);

		this.columnSeprator = exportConfig.getColumnDelimeter();
		this.rowSeprator = exportConfig.getRowDelimeter();
		if (columnSeprator.equals("")) {
			columnSeprator = ",";
		}
		if (rowSeprator.equals("")) {
			rowSeprator = StringUtil.NEWLINE;
		}
	}

	public void handle(String tableName) throws IOException, SQLException { // FIXME move this logic to core module
		if (StringUtil.isEmpty(tableName)) {
			return;
		}
		
		long totalRecord = exportConfig.getTotalCount(tableName);
		if (totalRecord == 0) {
			return;
		}
		
		if(exportConfig.isExportFromCache()){
			exportFromCache(tableName);
		}else{
			exportByQuerying(tableName);
		}
	}

	public void exportByQuerying(String tableName) throws IOException, SQLException {
		BufferedWriter fs = null;
		Connection conn = null;
		CUBRIDPreparedStatementProxy pStmt = null;
		CUBRIDResultSetProxy rs = null;
		boolean hasNextPage = true;
		long totalRecord = exportConfig.getTotalCount(tableName);
		long beginIndex = 1;
		int exportedCount = 0;
		String whereCondition = exportConfig.getWhereCondition(tableName);
		boolean isExportedColumnTitles = false;
		List<String> columnTitles = new ArrayList<String>();
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
					int colCount = rsmt.getColumnCount();
					// Init title
					if (!isExportedColumnTitles) {
						for (int j = 1; j < colCount; j++) {

							String columnName = rsmt.getColumnName(j);
							String columnType = rsmt.getColumnTypeName(j);
							int precision = rsmt.getPrecision(j);
							columnType = columnType == null ? "" : columnType;
							setIsHasBigValue(columnType, precision);
							columnTitles.add(columnName);
						}
						isExportedColumnTitles = true;

						if (exportConfig.isFirstRowAsColumnName()) {
							for (int j = 1; j < rsmt.getColumnCount() + 1; j++) {
								fs.write(surround + rsmt.getColumnName(j) + surround);
								if (j != rsmt.getColumnCount()) {
									fs.write(columnSeprator);
								}
							}
							fs.write(rowSeprator);
							fs.flush();
						}
					}

					while (rs.next()) {
						writeNextLine(tableName, fs, rs, rsmt, columnSeprator, rowSeprator,
								surround);
						fs.write(rowSeprator);
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
					LOGGER.error("", e);
					exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(tableName));
				} finally {
					QueryUtil.freeQuery(pStmt, rs);
				}
				if (hasNextPage(beginIndex, totalRecord)) {
					hasNextPage = true;
					fs.write(rowSeprator);
				} else {
					hasNextPage = false;
				}

				System.gc();
			}
		} finally {
			QueryUtil.freeQuery(conn);
			Closer.close(fs);
		}
	}

	public void exportFromCache(String tableName) throws IOException {
		BufferedWriter fs = null;
		int exportedCount = 0;
		ResultSetDataCache resultSetDataCache = exportConfig.getResultSetDataCache();
		try {
			fs = FileUtil.getBufferedWriter(exportConfig.getDataFilePath(tableName),
					exportConfig.getFileCharset());
			try {

				List<ColumnInfo> columnInfos = resultSetDataCache.getColumnInfos();
				int colCount = columnInfos.size();
				for (int j = 0; j < colCount; j++) {
					fs.write(surround + columnInfos.get(j).getName() + surround);
					if (j != colCount - 1) {
						fs.write(columnSeprator);
					}
				}
				fs.write(rowSeprator);
				fs.flush();

				List<ArrayList<Object>> datas = resultSetDataCache.getDatas();
				for (ArrayList<Object> rowData : datas) {
					writeNextLine(tableName, fs, columnInfos, rowData, columnSeprator, rowSeprator,
							surround);
					fs.write(rowSeprator);
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
				LOGGER.error("", e);
				exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(tableName));
			}
			System.gc();
		} finally {
			Closer.close(fs);
		}
	}

	/**
	 * Write a line to a CSV file
	 * 
	 * @param fs BufferedWriter
	 * @param rs CUBRIDResultSetProxy
	 * @param rsmt CUBRIDResultSetMetaDataProxy
	 * @throws SQLException The exception
	 * @throws IOException The exception
	 */
	private void writeNextLine(String tableName, BufferedWriter fs, CUBRIDResultSetProxy rs,
			CUBRIDResultSetMetaDataProxy rsmt, String columnSeprator, String rowSeprator,
			String surround) throws SQLException, IOException { // FIXME move this logic to core module
		int colCount = rsmt.getColumnCount();
		for (int j = 1; j <= colCount; j++) {
			String columnType = rsmt.getColumnTypeName(j);
			int precision = rsmt.getPrecision(j);
			columnType = FieldHandlerUtils.amendDataTypeByResult(rs, j, columnType);
			setIsHasBigValue(columnType, precision);
			Object value = null;
			if (DataType.DATATYPE_BLOB.equals(columnType)) {
				String fileName = exportBlobData(tableName, rs, j);
				if (StringUtil.isNotEmpty(fileName)) {
					value = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName + BLOB_FOLDER_POSTFIX
							+ File.separator + fileName;
				} else {
					value = DataType.NULL_EXPORT_FORMAT;
				}
			} else if (DataType.DATATYPE_CLOB.equals(columnType)) {
				String fileName = exportClobData(tableName, rs, j);
				if (StringUtil.isNotEmpty(fileName)) {
					value = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName + CLOB_FOLDER_POSTFIX
							+ File.separator + fileName;
				} else {
					value = DataType.NULL_EXPORT_FORMAT;
				}
			} else {
				value = FieldHandlerUtils.getRsValueForExport(columnType, rs, j,
						exportConfig.getNULLValueTranslation());
			}

			fs.write(surround);
			fs.write(value.toString().replaceAll("\"", "\"\""));
			fs.write(surround);
			if (j != colCount) {
				fs.write(columnSeprator);
			}
		}
	}

	/**
	 * Write a line to a txt file
	 * 
	 * @param fs BufferedWriter
	 * @param columns List<ColumnInfo>
	 * @param rowValues List<Object>
	 * @throws SQLException The exception
	 * @throws IOException The exception
	 */
	private void writeNextLine(String tableName, BufferedWriter fs, List<ColumnInfo> columns,
			List<Object> rowValues, String columnSeprator, String rowSeprator, String surround) throws SQLException, IOException {
		int colCount = columns.size();
		for (int j = 0; j < colCount; j++) {
			String columnType = columns.get(j).getType();
			int precision = columns.get(j).getPrecision();
			setIsHasBigValue(columnType, precision);
			Object value = null;
			if (DataType.DATATYPE_BLOB.equals(columnType)) {

				String fileName = exportBlobData(tableName, (Blob) rowValues.get(j));
				if (StringUtil.isNotEmpty(fileName)) {
					value = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName + BLOB_FOLDER_POSTFIX
							+ File.separator + fileName;
				} else {
					value = DataType.NULL_EXPORT_FORMAT;
				}
			} else if (DataType.DATATYPE_CLOB.equals(columnType)) {
				String fileName = exportClobData(tableName, (Clob) rowValues.get(j));
				if (StringUtil.isNotEmpty(fileName)) {
					value = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName + CLOB_FOLDER_POSTFIX
							+ File.separator + fileName;
				} else {
					value = DataType.NULL_EXPORT_FORMAT;
				}
			} else {
				Object obj = rowValues.get(j);
				value = (obj == null) ? exportConfig.getNULLValueTranslation() : obj;
			}

			fs.write(surround);
			fs.write(value.toString().replaceAll("\"", "\"\""));
			fs.write(surround);
			if (j != colCount - 1) {
				fs.write(columnSeprator);
			}
		}
	}
}
