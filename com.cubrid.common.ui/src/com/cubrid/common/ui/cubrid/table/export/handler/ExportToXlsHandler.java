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

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.export.ResultSetDataCache;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileConstants;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * ExportToXlsHandler Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class ExportToXlsHandler extends
		AbsExportDataHandler {
	private static final Logger LOGGER = LogUtil.getLogger(ExportToXlsxHandler.class);

	//private volatile boolean isConfirmColumnLimit = false;

	public ExportToXlsHandler(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler) {
		super(dbInfo, exportConfig, exportDataEventHandler);
	}

	public void handle(String tableName) throws IOException, SQLException {
		if(exportConfig.isExportFromCache()){
			exportFromCache(tableName);
		}else{
			exportByQuerying(tableName);
		}
	}
	
	public void exportByQuerying(String tableName) throws IOException, SQLException {
		if (StringUtil.isEmpty(tableName)) { // FIXME move this logic to core module
			return;
		}

		Connection conn = null;
		CUBRIDPreparedStatementProxy pStmt = null;
		CUBRIDResultSetProxy rs = null;
		WritableWorkbook workbook = null;
		int workbookNum = 0;
		String whereCondition = exportConfig.getWhereCondition(tableName);
		boolean hasNextPage = true;
		long beginIndex = 1;
		long totalRecord = exportConfig.getTotalCount(tableName);
		if (totalRecord == 0) {
			return;
		}
		
		int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;
		int rowLimit = ImportFileConstants.XLS_ROW_LIMIT; // 65536: limit xls row number.
		boolean isInitedColumnTitles = false;
		List<String> columnTitles = new ArrayList<String>();
		try {
			conn = getConnection();
			int sheetNum = 0;
			int xlsRecordNum = 0;
			workbook = createWorkbook(exportConfig.getDataFilePath(tableName), workbookNum++);
			WritableSheet sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
			sheetNum++;
			int exportedCount = 0;
			String sql = getSelectSQL(tableName);
			isPaginating = isPagination(whereCondition, sql, whereCondition);
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

					if (!isInitedColumnTitles) {
						isInitedColumnTitles = true;
						columnTitles = getColumnTitleList(rsmt);
						if (isExit) {
							return;
						}
						// first line add column name
						if (exportConfig.isFirstRowAsColumnName()) {
							writeHeader(sheet, columnTitles);
							xlsRecordNum++;
						}
					}

					while (rs.next()) {
						//Check memory
						if (!CommonUITool.isAvailableMemory(REMAINING_MEMORY_SIZE)) {
							closeWorkbook(workbook);
							workbook = null;
							System.gc();
							workbook = createWorkbook(exportConfig.getDataFilePath(tableName),
									workbookNum++);
							sheetNum = 0;
							sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
							sheetNum++;
							xlsRecordNum = 0;
							// first line add column name
							if (exportConfig.isFirstRowAsColumnName()) {
								writeHeader(sheet, columnTitles);
								xlsRecordNum++;
							}
						}
						for (int j = 1; j <= colCount; j++) {
							String colType = rsmt.getColumnTypeName(j);
							colType = FieldHandlerUtils.amendDataTypeByResult(rs, j, colType);
							int precision = rsmt.getPrecision(j);
							setIsHasBigValue(colType, precision);
							Object cellValue = FieldHandlerUtils.getRsValueForExport(colType, rs,
									j, exportConfig.getNULLValueTranslation());
							// We need judge the CLOB/BLOD data by column type
							if (DataType.DATATYPE_BLOB.equals(colType)
									|| DataType.DATATYPE_CLOB.equals(colType)) {
								if (DataType.DATATYPE_BLOB.equals(colType)) {
									String fileName = exportBlobData(tableName, rs, j);
									String dataCellValue = DataType.NULL_EXPORT_FORMAT;
									if (StringUtil.isNotEmpty(fileName)) {
										dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX
												+ tableName + BLOB_FOLDER_POSTFIX + File.separator
												+ fileName;
									}
									sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
								} else {
									String fileName = exportClobData(tableName, rs, j);
									String dataCellValue = DataType.NULL_EXPORT_FORMAT;
									if (StringUtil.isNotEmpty(fileName)) {
										dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX
												+ tableName + CLOB_FOLDER_POSTFIX + File.separator
												+ fileName;
									}
									sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
								}
							} else if (cellValue instanceof Long) {
								sheet.addCell(new Number(j - 1, xlsRecordNum, (Long) cellValue));
							} else if (cellValue instanceof Double) {
								sheet.addCell(new Number(j - 1, xlsRecordNum, (Double) cellValue));
							} else if (cellValue instanceof Timestamp) {
								String dataCellValue = FieldHandlerUtils.formatDateTime((Timestamp) cellValue);
								sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
							} else if (cellValue instanceof java.sql.Time) {
								String dataCellValue = DateUtil.getDatetimeString(
										((java.sql.Time) cellValue).getTime(),
										FieldHandlerUtils.FORMAT_TIME);
								sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
							} else if (cellValue instanceof java.sql.Date) {
								String dataCellValue = DateUtil.getDatetimeString(
										((java.sql.Date) cellValue).getTime(),
										FieldHandlerUtils.FORMAT_DATE);
								sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
							} else {
								sheet.addCell(new Label(
										j - 1,
										xlsRecordNum,
										cellValue.toString().length() > cellCharacterLimit ? cellValue.toString().substring(
												0, cellCharacterLimit)
												: cellValue.toString()));
							}
						}

						xlsRecordNum++;
						if ((xlsRecordNum + 1) % rowLimit == 0) {
							xlsRecordNum = 0;
							sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
							sheetNum++;
							// first line add column name
							if (exportConfig.isFirstRowAsColumnName()) {
								writeHeader(sheet, columnTitles);
								xlsRecordNum++;
							}
						}
						exportedCount++;
						if (exportedCount >= COMMIT_LINES) {
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
				} else {
					hasNextPage = false;
				}

				System.gc();
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn);
			closeWorkbook(workbook);
		}
	}

	public void exportFromCache(String tableName) throws IOException {
		if (StringUtil.isEmpty(tableName)) { // FIXME move this logic to core module
			return;
		}

		WritableWorkbook workbook = null;
		int workbookNum = 0;
		int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;
		int rowLimit = ImportFileConstants.XLS_ROW_LIMIT; // 65536: limit xls row number.
		boolean isInitedColumnTitles = false;
		List<String> columnTitles = new ArrayList<String>();
		try {
			int sheetNum = 0;
			int xlsRecordNum = 0;
			workbook = createWorkbook(exportConfig.getDataFilePath(tableName), workbookNum++);
			WritableSheet sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
			sheetNum++;
			int exportedCount = 0;
			try {
				ResultSetDataCache resultSetDataCache = exportConfig.getResultSetDataCache();
				List<ArrayList<Object>> datas = resultSetDataCache.getDatas();
				List<ColumnInfo> columnInfos = resultSetDataCache.getColumnInfos();
				int colCount = columnInfos.size();

				if (!isInitedColumnTitles) {
					isInitedColumnTitles = true;
					for (ColumnInfo column : columnInfos) {
						columnTitles.add(column.getName());
					}
					if (isExit) {
						return;
					}
					// first line add column name
					if (exportConfig.isFirstRowAsColumnName()) {
						writeHeader(sheet, columnTitles);
						xlsRecordNum++;
					}
				}

				for (ArrayList<Object> rowData : datas) {
					//Check memory
					if (!CommonUITool.isAvailableMemory(REMAINING_MEMORY_SIZE)) {
						closeWorkbook(workbook);
						workbook = null;
						System.gc();
						workbook = createWorkbook(exportConfig.getDataFilePath(tableName),
								workbookNum++);
						sheetNum = 0;
						sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
						sheetNum++;
						xlsRecordNum = 0;
						// first line add column name
						if (exportConfig.isFirstRowAsColumnName()) {
							writeHeader(sheet, columnTitles);
							xlsRecordNum++;
						}
					}
					for (int j = 1; j <= colCount; j++) {
						String colType = columnInfos.get(j - 1).getType();
						int precision = columnInfos.get(j - 1).getPrecision();
						setIsHasBigValue(colType, precision);
						Object cellValue = rowData.get(j - 1);
						// We need judge the CLOB/BLOD data by column type
						if (DataType.DATATYPE_BLOB.equals(colType)
								|| DataType.DATATYPE_CLOB.equals(colType)) {
							if (DataType.DATATYPE_BLOB.equals(colType)) {
								String fileName = exportBlobData(tableName, (Blob) cellValue);
								String dataCellValue = DataType.NULL_EXPORT_FORMAT;
								if (StringUtil.isNotEmpty(fileName)) {
									dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName
											+ BLOB_FOLDER_POSTFIX + File.separator + fileName;
								}
								sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
							} else {
								String fileName = exportClobData(tableName, (Clob) cellValue);
								String dataCellValue = DataType.NULL_EXPORT_FORMAT;
								if (StringUtil.isNotEmpty(fileName)) {
									dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName
											+ CLOB_FOLDER_POSTFIX + File.separator + fileName;
								}
								sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
							}
						} else if (cellValue instanceof Long) {
							sheet.addCell(new Number(j - 1, xlsRecordNum, (Long) cellValue));
						} else if (cellValue instanceof Double) {
							sheet.addCell(new Number(j - 1, xlsRecordNum, (Double) cellValue));
						} else if (cellValue instanceof Timestamp) {
							String dataCellValue = FieldHandlerUtils.formatDateTime((Timestamp) cellValue);
							sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
						} else if (cellValue instanceof java.sql.Time) {
							String dataCellValue = DateUtil.getDatetimeString(
									((java.sql.Time) cellValue).getTime(),
									FieldHandlerUtils.FORMAT_TIME);
							sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
						} else if (cellValue instanceof java.sql.Date) {
							String dataCellValue = DateUtil.getDatetimeString(
									((java.sql.Date) cellValue).getTime(),
									FieldHandlerUtils.FORMAT_DATE);
							sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
						} else {
							sheet.addCell(new Label(
									j - 1,
									xlsRecordNum,
									cellValue.toString().length() > cellCharacterLimit ? cellValue.toString().substring(
											0, cellCharacterLimit)
											: cellValue.toString()));
						}
					}

					xlsRecordNum++;
					if ((xlsRecordNum + 1) % rowLimit == 0) {
						xlsRecordNum = 0;
						sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
						sheetNum++;
						// first line add column name
						if (exportConfig.isFirstRowAsColumnName()) {
							writeHeader(sheet, columnTitles);
							xlsRecordNum++;
						}
					}
					exportedCount++;
					if (exportedCount >= COMMIT_LINES) {
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
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			closeWorkbook(workbook);
		}
	}
	
	private List<String> getColumnTitleList(CUBRIDResultSetMetaDataProxy rsmt) throws SQLException { // FIXME move this logic to core module
		// it set 257. Because Tbl's first column is oid value that doesn't export
		int columnLimit = ImportFileConstants.XLS_COLUMN_LIMIT + 1; // 256: limit xls column number.
		int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;

		int colCount = rsmt.getColumnCount();
		List<String> columnTitleList = new ArrayList<String>(colCount);
		for (int j = 1; j <= colCount; j++) {
			String columnName = rsmt.getColumnName(j);
			String columnType = rsmt.getColumnTypeName(j);
			int precision = rsmt.getPrecision(j);
			columnType = columnType == null ? "" : columnType;
			setIsHasBigValue(columnType, precision);
			//the data length > XLS column character limit
			if ((precision > cellCharacterLimit) && !hasConfirmBigValue) {
				final String confirmMSG = Messages.bind(
						Messages.exportCharacterCountExceedWarnInfo, columnName);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						hasConfirmBigValue = true;
						if (!CommonUITool.openConfirmBox(confirmMSG)) {
							isExit = true;
						}
					}
				});
				if (isExit) {
					return columnTitleList;
				}
			}
			if (columnName.length() > cellCharacterLimit) {
				columnTitleList.add(columnName.substring(0, cellCharacterLimit));
			} else {
				columnTitleList.add(columnName);
			}
			if (colCount > columnLimit) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						if (!CommonUITool.openConfirmBox(Messages.exportColumnCountOverWarnInfo)) {
							isExit = true;
						}
					}
				});
				if (isExit) {
					return columnTitleList;
				}
				colCount = columnLimit;
				break;
			}
		}
		return columnTitleList;
	}

	private WritableWorkbook createWorkbook(String filePath, int workbookNum) throws IOException { // FIXME move this logic to core module
		File file = new File(getFixFileName(filePath, workbookNum));
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		if (StringUtil.isEmpty(exportConfig.getFileCharset())) {
			workbook = Workbook.createWorkbook(file);
		} else {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding(exportConfig.getFileCharset());
			workbook = Workbook.createWorkbook(file, workbookSettings);
		}

		return workbook;
	}

	private void closeWorkbook(WritableWorkbook workbook) { // FIXME move this logic to core module
		try {
			if (workbook != null) {
				workbook.write();
			}
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		try {
			if (workbook != null) {
				workbook.close();
			}
		} catch (WriteException e) {
			LOGGER.error("", e);
		} catch (IOException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * Write the sheet header
	 *
	 * @param sheet
	 * @param columnNameList
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void writeHeader(WritableSheet sheet, List<String> columnNameList) throws RowsExceededException,
			WriteException { // FIXME move this logic to core module
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
		WritableCellFormat wcf = new WritableCellFormat(wf);
		for (int columnIndex = 0; columnIndex < columnNameList.size(); columnIndex++) {
			String columnName = columnNameList.get(columnIndex);
			Label label = new Label(columnIndex, 0, columnName, wcf);
			sheet.addCell(label);
			label = null;
		}
		wcf = null;
		wf = null;
	}
}
