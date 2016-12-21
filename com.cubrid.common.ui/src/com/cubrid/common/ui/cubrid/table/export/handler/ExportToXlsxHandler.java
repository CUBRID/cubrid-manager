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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.control.XlsxWriterHelper;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.export.ResultSetDataCache;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileConstants;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.Export;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * ResultSetToXlsxHandler Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class ExportToXlsxHandler extends
		AbsExportDataHandler {

	private static final Logger LOGGER = LogUtil.getLogger(ExportToXlsxHandler.class);
	private volatile boolean isConfirmColumnLimit = false;

	public ExportToXlsxHandler(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler) {
		super(dbInfo, exportConfig, exportDataEventHandler);
	}

	public void handle(String tableName) throws IOException, SQLException {
		if (StringUtil.isEmpty(tableName)) {
			return;
		}
		
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

		int rowLimit = ImportFileConstants.XLSX_ROW_LIMIT; // 1048576: limit xlsx row number.
		int columnLimit = ImportFileConstants.XLSX_COLUMN_LIMIT; // 16384: limit xlsx column number.
		int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;

		boolean hasNextPage = true;
		long beginIndex = 1;
		long totalRecord = exportConfig.getTotalCount(tableName);
		if (totalRecord == 0) {
			return;
		}
		
		String whereCondition = exportConfig.getWhereCondition(tableName);

		XlsxWriterHelper xlsxWriterhelper = new XlsxWriterHelper();
		//create memory workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		Calendar cal = Calendar.getInstance();
		int datetimeStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get(
				"datetime")).getIndex();
		int timestampStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get(
				"timestamp")).getIndex();
		int dateStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("date")).getIndex();
		int timeStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("time")).getIndex();
		int sheetNum = 0;
		int xssfRowNum = 0;

		File file = new File(exportConfig.getDataFilePath(tableName));
		Map<String, File> fileMap = new HashMap<String, File>();
		XlsxWriterHelper.SpreadsheetWriter sheetWriter = null;

		Connection conn = null;
		CUBRIDPreparedStatementProxy pStmt = null;
		CUBRIDResultSetProxy rs = null;

		boolean isInitedColumnTitle = false;
		List<String> columnTitles = new ArrayList<String>();

		try {
			conn = getConnection();
			String sql = getSelectSQL(tableName);
			isPaginating = isPagination(whereCondition, sql, whereCondition);
			int exportedCount = 0;
			while (hasNextPage) {
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
				if (colCount >= columnLimit && !isConfirmColumnLimit) {
					isConfirmColumnLimit = true;
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (!CommonUITool.openConfirmBox(Messages.exportColumnCountOverWarnInfo)) {
								isExit = true;
							}
						}
					});
					if (isExit) {
						return;
					}
					colCount = columnLimit;
				}

				if (!isInitedColumnTitle) {
					columnTitles = getColumnTitleList(rsmt);
					isInitedColumnTitle = true;
					if (isExit) {
						return;
					}

					if (sheetWriter != null) {
						try {
							XlsxWriterHelper.writeSheetWriter(sheetWriter);
						} catch (IOException e) {
							sheetWriter = null;
							throw e;
						}
					}
					sheetWriter = createSheetWriter(workbook, xlsxWriterhelper, sheetNum++,
							fileMap, columnTitles, xssfRowNum);
					if (exportConfig.isFirstRowAsColumnName()) {
						xssfRowNum++;
					}
				}

				try {
					while (rs.next()) {
						sheetWriter.insertRow(xssfRowNum);
						for (int k = 1; k <= colCount; k++) {
							String colType = rsmt.getColumnTypeName(k);
							colType = FieldHandlerUtils.amendDataTypeByResult(rs, k, colType);
							int precision = rsmt.getPrecision(k);
							setIsHasBigValue(colType, precision);
							Object cellValue = FieldHandlerUtils.getRsValueForExport(colType, rs,
									k, exportConfig.getNULLValueTranslation());
							// We need judge the CLOB/BLOD data by column type
							if (DataType.DATATYPE_BLOB.equals(colType)
									|| DataType.DATATYPE_CLOB.equals(colType)) {
								if (DataType.DATATYPE_BLOB.equals(colType)) {
									String fileName = exportBlobData(tableName, rs, k);
									String dataCellValue = DataType.NULL_EXPORT_FORMAT;
									if (StringUtil.isNotEmpty(fileName)) {
										dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX
												+ tableName + BLOB_FOLDER_POSTFIX + File.separator
												+ fileName;
									}
									sheetWriter.createCell(k - 1, dataCellValue);
								} else {
									String fileName = exportClobData(tableName, rs, k);
									String dataCellValue = DataType.NULL_EXPORT_FORMAT;
									if (StringUtil.isNotEmpty(fileName)) {
										dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX
												+ tableName + CLOB_FOLDER_POSTFIX + File.separator
												+ fileName;
									}
									sheetWriter.createCell(k - 1, dataCellValue);
								}
							} else if (cellValue instanceof Long) {
								sheetWriter.createCell(k - 1, ((Long) cellValue).longValue());
							} else if (cellValue instanceof Double) {
								sheetWriter.createCell(k - 1, ((Double) cellValue).doubleValue());
							} else if (cellValue instanceof Date) {
								cal.setTime((Date) cellValue);
								if (DataType.DATATYPE_DATETIME.equals(colType)) {
									sheetWriter.createCell(k - 1, cal, datetimeStyleIndex);
								} else if (DataType.DATATYPE_DATE.equals(colType)) {
									sheetWriter.createCell(k - 1, cal, dateStyleIndex);
								} else if (DataType.DATATYPE_TIME.equals(colType)) {
									sheetWriter.createCell(k - 1, cal, timeStyleIndex);
								} else {
									sheetWriter.createCell(k - 1, cal, timestampStyleIndex);
								}
							} else {
								String cellStr = cellValue.toString().length() > cellCharacterLimit ? cellValue.toString().substring(
										0, cellCharacterLimit)
										: cellValue.toString();

								sheetWriter.createCell(k - 1, Export.covertXMLString(cellStr));
							}
						}

						sheetWriter.endRow();
						xssfRowNum++;
						exportedCount++;
						if ((xssfRowNum + 1) % rowLimit == 0) {
							xssfRowNum = 0;
							if (sheetWriter != null) {
								try {
									XlsxWriterHelper.writeSheetWriter(sheetWriter);
								} catch (IOException e) {
									sheetWriter = null;
									throw e;
								}
							}
							sheetWriter = createSheetWriter(workbook, xlsxWriterhelper, sheetNum,
									fileMap, columnTitles, xssfRowNum);
							sheetNum++;
							if (exportConfig.isFirstRowAsColumnName()) {
								xssfRowNum++;
							}
						}

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
		} finally {
			QueryUtil.freeQuery(conn);
			try {
				if (sheetWriter != null) {
					XlsxWriterHelper.writeSheetWriter(sheetWriter);
				}
			} catch (IOException e) {
				sheetWriter = null;
				throw e;
			} finally {
				XlsxWriterHelper.writeWorkbook(workbook, xlsxWriterhelper, fileMap, file);
			}
		}
	}

	public void exportFromCache(String tableName) throws IOException {
		if (StringUtil.isEmpty(tableName)) {
			return;
		}

		int rowLimit = ImportFileConstants.XLSX_ROW_LIMIT; // 1048576: limit xlsx row number.
		int columnLimit = ImportFileConstants.XLSX_COLUMN_LIMIT; // 16384: limit xlsx column number.
		int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;

		XlsxWriterHelper xlsxWriterhelper = new XlsxWriterHelper();
		//create memory workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		Calendar cal = Calendar.getInstance();
		int datetimeStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get(
				"datetime")).getIndex();
		int timestampStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get(
				"timestamp")).getIndex();
		int dateStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("date")).getIndex();
		int timeStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("time")).getIndex();
		int sheetNum = 0;
		int xssfRowNum = 0;

		File file = new File(exportConfig.getDataFilePath(tableName));
		Map<String, File> fileMap = new HashMap<String, File>();
		XlsxWriterHelper.SpreadsheetWriter sheetWriter = null;

		boolean isInitedColumnTitle = false;
		List<String> columnTitles = new ArrayList<String>();

		try {
			int exportedCount = 0;
			ResultSetDataCache resultSetDataCache = exportConfig.getResultSetDataCache();
			List<ColumnInfo> columnInfos = resultSetDataCache.getColumnInfos();
			List<ArrayList<Object>> datas = resultSetDataCache.getDatas();
			int colCount = columnInfos.size();
			if (colCount >= columnLimit && !isConfirmColumnLimit) {
				isConfirmColumnLimit = true;
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						if (!CommonUITool.openConfirmBox(Messages.exportColumnCountOverWarnInfo)) {
							isExit = true;
						}
					}
				});
				if (isExit) {
					return;
				}
				colCount = columnLimit;
			}

			if (!isInitedColumnTitle) {
				for (ColumnInfo column : columnInfos) {
					columnTitles.add(column.getName());
				}
				isInitedColumnTitle = true;
				if (isExit) {
					return;
				}

				sheetWriter = createSheetWriter(workbook, xlsxWriterhelper, sheetNum++, fileMap,
						columnTitles, xssfRowNum);
				if (exportConfig.isFirstRowAsColumnName()) {
					xssfRowNum++;
				}
			}

			try {
				for (ArrayList<Object> rowData : datas) {
					sheetWriter.insertRow(xssfRowNum);
					for (int k = 1; k <= colCount; k++) {
						String colType = columnInfos.get(k - 1).getType();
						int precision = columnInfos.get(k - 1).getPrecision();
						setIsHasBigValue(colType, precision);
						Object cellValue = rowData.get(k - 1);
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
								sheetWriter.createCell(k - 1, dataCellValue);
							} else {
								String fileName = exportClobData(tableName, (Clob) cellValue);
								String dataCellValue = DataType.NULL_EXPORT_FORMAT;
								if (StringUtil.isNotEmpty(fileName)) {
									dataCellValue = DBAttrTypeFormatter.FILE_URL_PREFIX + tableName
											+ CLOB_FOLDER_POSTFIX + File.separator + fileName;
								}
								sheetWriter.createCell(k - 1, dataCellValue);
							}
						} else if (cellValue instanceof Long) {
							sheetWriter.createCell(k - 1, ((Long) cellValue).longValue());
						} else if (cellValue instanceof Double) {
							sheetWriter.createCell(k - 1, ((Double) cellValue).doubleValue());
						} else if (cellValue instanceof Date) {
							cal.setTime((Date) cellValue);
							if (DataType.DATATYPE_DATETIME.equals(colType)) {
								sheetWriter.createCell(k - 1, cal, datetimeStyleIndex);
							} else if (DataType.DATATYPE_DATE.equals(colType)) {
								sheetWriter.createCell(k - 1, cal, dateStyleIndex);
							} else if (DataType.DATATYPE_TIME.equals(colType)) {
								sheetWriter.createCell(k - 1, cal, timeStyleIndex);
							} else {
								sheetWriter.createCell(k - 1, cal, timestampStyleIndex);
							}
						} else {
							String cellStr = cellValue.toString().length() > cellCharacterLimit ? cellValue.toString().substring(
									0, cellCharacterLimit)
									: cellValue.toString();

							sheetWriter.createCell(k - 1, Export.covertXMLString(cellStr));
						}
					}

					sheetWriter.endRow();
					xssfRowNum++;
					exportedCount++;
					if ((xssfRowNum + 1) % rowLimit == 0) {
						xssfRowNum = 0;
						if (sheetWriter != null) {
							try {
								XlsxWriterHelper.writeSheetWriter(sheetWriter);
							} catch (IOException e) {
								sheetWriter = null;
								throw e;
							}
						}
						sheetWriter = createSheetWriter(workbook, xlsxWriterhelper, sheetNum,
								fileMap, columnTitles, xssfRowNum);
						sheetNum++;
						if (exportConfig.isFirstRowAsColumnName()) {
							xssfRowNum++;
						}
					}

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
		} finally {
			try {
				if (sheetWriter != null) {
					XlsxWriterHelper.writeSheetWriter(sheetWriter);
				}
			} catch (IOException e) {
				sheetWriter = null;
				throw e;
			} finally {
				XlsxWriterHelper.writeWorkbook(workbook, xlsxWriterhelper, fileMap, file);
			}
		}
	}

	private List<String> getColumnTitleList(CUBRIDResultSetMetaDataProxy rsmt) throws SQLException { // FIXME move this logic to core module
		int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;
		int colCount = rsmt.getColumnCount();
		List<String> titleList = new ArrayList<String>(colCount);
		for (int j = 1; j <= colCount; j++) {
			String columnName = rsmt.getColumnName(j);
			String columnType = rsmt.getColumnTypeName(j);
			int precision = rsmt.getPrecision(j);
			columnType = columnType == null ? "" : columnType;
			setIsHasBigValue(columnType, precision);
			// the data length > XLS column character limit
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
					return titleList;
				}
			}
			if (columnName.length() > cellCharacterLimit) {
				columnName = columnName.substring(0, cellCharacterLimit);
			}
			titleList.add(columnName);
		}

		return titleList;
	}

	/**
	 * Create the instance of SpreadsheetWriter and based upon the given
	 * condition writing the header of a sheet
	 *
	 * @param workbook the instance of Workbook
	 * @param xlsxWriterhelper the instance of XlsxWriterHelper
	 * @param sheetNum the number of a sheet
	 * @param fileMap a map includes the temporary file and its name
	 * @param columnTitles the column title
	 * @param xssfRowNum the number of row
	 * @throws IOException the exception
	 * @return the instance of XlsxWriterHelper.SpreadsheetWriter
	 */
	private XlsxWriterHelper.SpreadsheetWriter createSheetWriter(XSSFWorkbook workbook,
			XlsxWriterHelper xlsxWriterhelper, int sheetNum, Map<String, File> fileMap,
			List<String> columnTitles, int xssfRowNum) throws IOException { // FIXME move this logic to core module
		XSSFSheet sheet = workbook.createSheet("sheet" + sheetNum);
		String sheetRef = sheet.getPackagePart().getPartName().getName().substring(1);

		File tmp = File.createTempFile("sheet" + sheetNum, ".xml");
		fileMap.put(sheetRef, tmp);

		String charset = null;
		if (StringUtil.isEmpty(exportConfig.getFileCharset())) {
			charset = "UTF-8";
		} else {
			charset = exportConfig.getFileCharset();
		}
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmp), charset);
		XlsxWriterHelper.SpreadsheetWriter sheetWriter = new XlsxWriterHelper.SpreadsheetWriter(
				writer);
		sheetWriter.setCharset(charset);
		sheetWriter.beginSheet();
		if (exportConfig.isFirstRowAsColumnName() && columnTitles != null) {
			sheetWriter.insertRow(xssfRowNum);
			int styleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("header")).getIndex();
			for (int index = 0; index < columnTitles.size(); index++) {
				sheetWriter.createCell(index, columnTitles.get(index), styleIndex);
			}
			sheetWriter.endRow();
		}
		return sheetWriter;
	}
}
