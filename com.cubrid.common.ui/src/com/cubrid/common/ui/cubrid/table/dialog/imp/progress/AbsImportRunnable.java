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
package com.cubrid.common.ui.cubrid.table.dialog.imp.progress;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.DataFormatException;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.TableConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataTableFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportColumnData;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportRowData;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportStatus;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandler;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.paramSetter.ParamSetException;
import com.cubrid.common.ui.spi.util.paramSetter.ParamSetter;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;

/**
 *
 * The AbsImportDataThread Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public abstract class AbsImportRunnable implements
		Runnable {
	private static final Logger LOGGER = LogUtil.getLogger(AbsImportRunnable.class);

	public static final int PROGRESS_COMMIT = 100;

	protected ImportConfig importConfig;
	protected CubridDatabase database;
	protected TableConfig tableConfig;
	private ImportDataEventHandler importDataEventHandler;
	private IImportDataProcessManager progressManager;
	protected ImportFileDescription importFileDescription;
	protected String dbCharset;
	protected String tableName;

	protected Connection conn;
	protected CUBRIDPreparedStatementProxy pStmt;
	protected Statement stmt;

	protected String errorMsg;
	private BufferedWriter errorLogWriter = null;
	private int totalErrorCount = 0;
	protected File errLogFolder;

	protected volatile boolean isFinished = false;
	protected volatile boolean isCanceled = false;
	private ParamSetter paramSetter = new ParamSetter();

	/**
	 * The constructor
	 *
	 * @param database
	 * @param tableName
	 * @param importConfig
	 * @param progressManager
	 */
	protected AbsImportRunnable(CubridDatabase database, String tableName,
			ImportConfig importConfig, ImportDataProgressManager progressManager,
			ImportDataEventHandler importDataEventHandler) {
		this.database = database;
		this.tableName = tableName;
		this.importConfig = importConfig;
		this.progressManager = progressManager;
		this.importDataEventHandler = importDataEventHandler;
		this.tableConfig = importConfig.getSelectedMap().get(tableName);
		this.dbCharset = database.getDatabaseInfo().getCharSet();
		if (!StringUtil.isEmpty(importConfig.getErrorLogFolderPath())) {
			errLogFolder = new File(importConfig.getErrorLogFolderPath());
		}
	}

	private void initCsvErrorWriter() {
		initErrorWriter(true);
	}

	/**
	 * Init the csv writer
	 *
	 */
	private void initErrorWriter(boolean isCsvErrorLog) { // FIXME move this logic to core module
		if (errLogFolder == null) {
			return;
		}
		/*Create error log folder*/
		errLogFolder.mkdir();
		String errLogDir = errLogFolder.getAbsolutePath();
		String errLogFilePath = null;
		if (errLogDir != null) {
			errLogFilePath = errLogDir + File.separator + tableName + "_err";
			if (isCsvErrorLog) {
				errLogFilePath += ".csv";
			} else {
				errLogFilePath += ".sql";
			}
		}

		if (errLogFilePath != null) {
			try {
				File logFile = new File(errLogFilePath);
				logFile.createNewFile();
				errorLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						errLogFilePath), "UTF-8"));
				// TODO: need to use the charset which is used during import/export
			} catch (UnsupportedEncodingException e) {
				errorLogWriter = null;
			} catch (FileNotFoundException e) {
				errorLogWriter = null;
			} catch (IOException e) {
				errorLogWriter = null;
				LOGGER.error("Create error log file error:" + errLogFilePath + StringUtil.NEWLINE
						+ e.getMessage());
			}
		}
	}

	public void run() {
		handleEvent(new ImportDataBeginOneTableEvent(tableName));

		try {
			performCreateTable(tableName);
		} catch (Exception e) {
			writeErrorLog("Create table failed:" + e.getMessage());
			LOGGER.error("", e);
		}

		DataType.setNULLValuesForImport(importConfig.getNullValueList().toArray(
				new String[importConfig.getNullValueList().size()]));

		try {
			initConnection();

			doRun();
		} catch (final Exception e) {
			handleEvent(new ImportDataTableFailedEvent(tableName));
			LOGGER.error("", e);

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					String msg = Messages.errorOpenFile + StringUtil.NEWLINE
							+ tableConfig.getFilePath() + StringUtil.NEWLINE + StringUtil.NEWLINE
							+ e.getMessage();
					CommonUITool.openErrorBox(msg);
				}
			});
		} finally {
			finish();
		}
		handleEvent(new ImportDataFinishOneTableEvent(tableName));
		if (progressManager != null) {
			progressManager.taskFinished(this);
		}
	}

	/**
	 * If need,create new table
	 *
	 * @param tableName
	 * @throws SQLException
	 */
	protected void performCreateTable(String tableName) throws SQLException {
		TableConfig tableConfig = importConfig.getTableConfig(tableName);
		if (tableConfig != null && !StringUtil.isEmpty(tableConfig.getCreateDDL())) {
			createTable(tableConfig.getCreateDDL());
		}
	}

	protected abstract void doRun() throws Exception;

	/**
	 * Init the connection
	 *
	 */
	protected void initConnection() throws SQLException{
		errorMsg = null;
		try {
			conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
		} catch (SQLException e) {
			LOGGER.error("Init connection failed :" + e.getMessage(), e);
			throw e;
		}

		try {
			if (StringUtil.isNotEmpty(tableConfig.getInsertDML())) {
				pStmt = (CUBRIDPreparedStatementProxy) conn.prepareStatement(tableConfig.getInsertDML());
			}
		} catch (SQLException e) {
			LOGGER.error("Create CUBRIDPreparedStatementProxy failed :" + e.getMessage(), e);
			throw e;
		}

		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.error("Create CUBRIDStatementProxy failed :" + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 *
	 * Commit the data
	 *
	 *
	 * @param monitor IProgressMonitor
	 * @param currentRow int
	 * @throws SQLException The exception
	 */
	protected void commit(List<ImportRowData> rowList) throws SQLException {
		if (pStmt == null) {
			return;
		}

		try {
			pStmt.executeBatch();
			QueryUtil.commit(conn);
			handleEvent(new ImportDataSuccessEvent(tableName, rowList.size()));
			LOGGER.debug("Committed : " + rowList.size());
		} catch (SQLException ex) {
			QueryUtil.rollback(conn);
			ImportDataFailedEvent failedEvt = new ImportDataFailedEvent(tableName, rowList.size(),
					"", ex.getMessage());
			handleEvent(failedEvt);
			LOGGER.debug("Failed : " + rowList.size());
			writeErrorLog(rowList);
		} finally {
			rowList.clear();
			try {
				pStmt.clearBatch();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage());
				processSQLException(e);
			}
		}
	}

	protected void handleEvent(ImportDataEvent event) {
		if (importDataEventHandler != null) {
			importDataEventHandler.handleEvent(event);
		}
	}

	protected void processSQLException(SQLException e) throws SQLException{
		if (isConnectionCutDown(e)) {
			/*Free the connection and get connection again*/
			QueryUtil.freeQuery(conn, pStmt);
			QueryUtil.freeQuery(stmt);

			initConnection();
		}
	}

	/**
	 * If database connect is closed by server, it needs retry 5 times
	 *
	 * @param ex the exception raised.
	 * @return true:need retry.
	 */
	private boolean isConnectionCutDown(Exception ex) {
		String message = ex.getMessage();
		return message.indexOf("Connection or Statement might be closed") >= 0
				|| message.indexOf("Cannot communicate with the broker") >= 0;
	}

	/**
	 * Write error Log
	 *
	 */
	protected void writeErrorLog(List<ImportRowData> rowList) { // FIXME move this logic to core module
		if (errLogFolder == null) {
			return;
		}

		boolean isNeedHeader = false;

		if (rowList == null || rowList.size() == 0) {
			return;
		}

		boolean csvErrorLog = true;
		if (StringUtil.isNotEmpty(rowList.get(0).getSql())) {
			csvErrorLog = false;
		}

		if (errorLogWriter == null) {
			isNeedHeader = true;
			initErrorWriter(csvErrorLog);
		}

		try {
			/* If need writer header */
			if (isNeedHeader && csvErrorLog) {
				int columnSize = 1;
				if (rowList.get(0).getColumnList().size() > 1) {
					columnSize = rowList.get(0).getColumnList().size();
				}
				errorLogWriter.write(getCSVHeader(columnSize));
				errorLogWriter.write(StringUtil.NEWLINE);
			}

			/* Writer error log */
			for (int i = 0; i < rowList.size(); i++) {
				ImportRowData row = rowList.get(i);
				if (row == null) {
					LOGGER.error("Index:" + i + " ImportRowData is a null.");
					continue;
				}
				if (csvErrorLog) {
					errorLogWriter.write(getCSVErrorLog(row));
				} else {
					errorLogWriter.write(row.getSql());
				}
				errorLogWriter.write(StringUtil.NEWLINE);
			}
			errorLogWriter.flush();

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	protected void writeErrorLog(ImportRowData rowData) { // FIXME move this logic to core module
		boolean isNeedHeader = false;

		if (rowData == null) {
			return;
		}

		boolean csvErrorLog = true;
		if (StringUtil.isNotEmpty(rowData.getSql())) {
			csvErrorLog = false;
		}

		if (errorLogWriter == null) {
			isNeedHeader = true;
			initErrorWriter(csvErrorLog);
		}

		try {
			/* If need writer header */
			if (isNeedHeader && csvErrorLog) {
				int columnSize = 1;
				if (rowData.getColumnList().size() > 1) {
					columnSize = rowData.getColumnList().size();
				}
				errorLogWriter.write(getCSVHeader(columnSize));
				errorLogWriter.write(StringUtil.NEWLINE);
			}

			/* Writer error log */
			if (csvErrorLog) {
				errorLogWriter.write(getCSVErrorLog(rowData));
			} else {
				errorLogWriter.write(rowData.getSql());
			}
			errorLogWriter.write(StringUtil.NEWLINE);
			errorLogWriter.flush();

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * Get csv header
	 *
	 * @param columnSize
	 * @return
	 */
	private String getCSVHeader(int columnSize) { // FIXME move this logic to core module
		StringBuilder sb = new StringBuilder();
		sb.append("Line").append(",");

		for (int i = 0; i < columnSize; i++) {
			sb.append("Column-").append(String.valueOf(i)).append(",");
		}
		return sb.toString();
	}

	/**
	 * Get csv log
	 *
	 * @param rowData
	 * @return
	 */
	private String getCSVErrorLog(ImportRowData rowData) { // FIXME move this logic to core module
		StringBuilder sb = new StringBuilder();
		if (rowData != null) {
			sb.append(rowData.getRowIndex()).append(",");
			if (rowData.getColumnList().size() > 0) {
				for (int i = 0; i < rowData.getColumnList().size(); i++) {
					sb.append(rowData.getColumnList().get(i).toString()).append(",");
				}
			} else {
				sb.append(rowData.getSql()).append(",");
			}
		}

		return sb.toString();
	}

	protected ImportRowData processRowData(String[] columnArray, String[] columnPattern,
			int currentRow, File parentFile) throws StopPerformException { // FIXME move this logic to core module
		ImportRowData rowData = new ImportRowData(currentRow);
		ImportColumnData columnData = null;
		boolean isSuccess = false;
		try {
			for (int j = 0; j < tableConfig.getPstmList().size(); j++) {
				PstmtParameter pstmtParameter = tableConfig.getPstmList().get(j);
				int column = Integer.parseInt(pstmtParameter.getStringParamValue());
				String content = null;
				String pattern = null;
				if (columnArray.length > column) {
					content = currentRow != 0 ? columnArray[column] :
						StringUtil.removeBOM(columnArray[column], importConfig.getFilesCharset());
				}

				if (columnPattern != null && columnPattern.length > column) {
					pattern = columnPattern[column];
				}

				/*Recored the origin data*/
				columnData = new ImportColumnData(content);
				rowData.getColumnList().add(columnData);

				String dataType = DataType.getRealType(pstmtParameter.getDataType());
				Object value = getRealValueForImport(dataType, content, parentFile);
				try {
					PstmtParameter parameter = new PstmtParameter(pstmtParameter.getParamName(),
							pstmtParameter.getParamIndex(), pstmtParameter.getDataType(), value);
					parameter.setCharSet(importConfig.getFilesCharset());
					if (StringUtil.isNotEmpty(pattern)) {
						parameter.setDatePattern(pattern);
					}
					if (value != null && value instanceof File) {
						parameter.setFileValue(true);
					}
					setPreparedStatementValue(pStmt, parameter, dbCharset);
					columnData.setStatus(ImportStatus.STATUS_FORMAT_SUCCESS);
					isSuccess = true;
				} catch (ParamSetException ex) {
					isSuccess = false;
					LOGGER.debug(ex.getMessage());
				} catch (SQLException ex) {
					isSuccess = false;
					LOGGER.debug(ex.getMessage());
				} finally {
					if (!isSuccess) {
						columnData.setStatus(ImportStatus.STATUS_FORMAT_FAILED);
						dataTypeErrorHandling(getErrorMsg(currentRow, column, dataType));
						PstmtParameter parameter = new PstmtParameter(
								pstmtParameter.getParamName(), pstmtParameter.getParamIndex(),
								pstmtParameter.getDataType(), null);
						parameter.setCharSet(importConfig.getFilesCharset());
						try {
							setPreparedStatementNULL(pStmt, parameter);
						} catch (SQLException e) {
							LOGGER.debug(e.getMessage());
						}
					}
				}
			}
		} catch (OutOfMemoryError error) {
			throw new RuntimeException(error);
		}
		return rowData;
	}

	/**
	 * Get the error message about error value.
	 *
	 * @param row of error location
	 * @param column of error location
	 * @param dataType data type
	 * @return error message.
	 */
	protected String getErrorMsg(int row, int column, String dataType) {
		return Messages.bind(Messages.errDataTypeNoMatch,
				new String[]{String.valueOf(row), String.valueOf(column), dataType });
	}

	/**
	 * Execute the sql
	 *
	 * @param sql
	 * @throws SQLException
	 */
	protected void createTable(String ddl) throws SQLException { // FIXME move this logic to core module
		Connection connection = null;
		Statement statement = null;
		try {
			connection = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);
			statement = connection.createStatement();
			statement.execute(ddl);
		} catch (SQLException e) {
			LOGGER.error("Init connection failed :" + e.getMessage());
			e.printStackTrace();
		} finally {
			QueryUtil.freeQuery(connection, statement);
		}

	}

	/**
	 * Error handling function.
	 *
	 * @param errorMsg error message
	 * @throws DataFormatException The exception
	 */
	protected void dataTypeErrorHandling(String errorMsg) throws StopPerformException {
		if (importConfig.getErrorHandle() == ImportConfig.ERROR_HANDLE_IGNORE) {
			writeErrorLog(new String[]{errorMsg });
			totalErrorCount++;
		} else {
			this.errorMsg = errorMsg;
			throw new StopPerformException(errorMsg);
		}
	}

	protected ImportFileDescription getFileDescription(final ImportFileHandler importFileHandler) {
		importFileDescription = null;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					importFileDescription = importFileHandler.getSourceFileInfo();
				} catch (Exception e) {
					LOGGER.error("Open file error:" + e.getMessage());
					CommonUITool.openErrorBox(Messages.errorOpenFile);
				}
			}
		});
		return importFileDescription;
	}

	/**
	 *
	 * Write the error log
	 *
	 * @param errorLogs String[]
	 */
	private void writeErrorLog(String[] errorLogs) { // FIXME move this logic to core module
		if (errorLogWriter == null) {
			initCsvErrorWriter();
		}
		try {
			for (int i = 0; i < errorLogs.length; i++) {
				String errorLog = errorLogs[i];
				errorLogWriter.write(errorLog);
				if (i != errorLogs.length - 1) {
					errorLogWriter.write(',');
				}
			}
			errorLogWriter.write(StringUtil.NEWLINE);
			errorLogWriter.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 *
	 * Write the error log
	 *
	 * @param errorLogs String[]
	 */
	private void writeErrorLog(String errorLog) { // FIXME move this logic to core module
		if (errorLogWriter == null) {
			initCsvErrorWriter();
		}
		try {
			errorLogWriter.write(errorLog);
			errorLogWriter.write(StringUtil.NEWLINE);
			errorLogWriter.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	protected void setPreparedStatementValue(PreparedStatement pstmt, PstmtParameter parameter,
			String dbCharset) throws ParamSetException, SQLException {
		paramSetter.handle(pstmt, parameter, importConfig.isImportCLobData(),
				importConfig.isImportBLobData());
	}

	protected void setPreparedStatementNULL(PreparedStatement pstmt, PstmtParameter parameter) throws SQLException {
		paramSetter.setNull(pstmt, parameter.getParamIndex());
	}

	/**
	 * @see AbstractTask#finish()
	 */
	public void finish() {
		QueryUtil.freeQuery(conn, pStmt);
		QueryUtil.freeQuery(stmt);
		if (errorLogWriter != null) {
			try {
				errorLogWriter.close();
			} catch (IOException e) {
				errorLogWriter = null;
			}
			errorLogWriter = null;
		}

		isFinished = true;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public synchronized boolean isCancel() {
		return isCanceled;
	}

	public synchronized void setCancel(boolean isCancel) {
		this.isCanceled = isCancel;
	}

	/**
	 *
	 * Get formated real value from imported content
	 *
	 * @param dataType String
	 * @param content String
	 * @param parent File
	 * @return String
	 */
	protected Object getRealValueForImport(String dataType, String content, File parent) { // FIXME move this logic to core module
		if (content == null) {
			return null;
		}
		String upperType = dataType.trim().toUpperCase();
		if (DataType.isNullValueForImport(dataType, content)) {
			return null;
		}

		boolean isMuchValue = DBAttrTypeFormatter.isMuchValueType(dataType);
		if (isMuchValue && content.startsWith(FieldHandlerUtils.FILE_URL_PREFIX)) {
			String str = content;
			File file = new File(str.replaceFirst(FieldHandlerUtils.FILE_URL_PREFIX, ""));
			if (!file.exists()) {
				file = new File(parent, str.replaceFirst(FieldHandlerUtils.FILE_URL_PREFIX, ""));
			}
			if (file.exists()) {
				return file;
			}
			return content;
		}

		// trim the content
		String[] trimedTypes = {DataType.DATATYPE_INTEGER, DataType.DATATYPE_BIGINT,
				DataType.DATATYPE_SMALLINT, DataType.DATATYPE_FLOAT, DataType.DATATYPE_DOUBLE,
				DataType.DATATYPE_NUMERIC, DataType.DATATYPE_SHORT, DataType.DATATYPE_DECIMAL,
				DataType.DATATYPE_REAL, DataType.DATATYPE_DATETIME, DataType.DATATYPE_TIMESTAMP,
				DataType.DATATYPE_TIME, DataType.DATATYPE_DATE, DataType.DATATYPE_BIT_VARYING,
				DataType.DATATYPE_BIT, DataType.DATATYPE_SEQUENCE, DataType.DATATYPE_MULTISET,
				DataType.DATATYPE_SET, DataType.DATATYPE_MONETARY, DataType.DATATYPE_MONETARY,
				DataType.DATATYPE_INT, DataType.DATATYPE_TINYINT, DataType.DATATYPE_CURRENCY,
				DataType.DATATYPE_OID };
		for (String trimedType : trimedTypes) {
			if (upperType.startsWith(trimedType)) {
				return content.trim();
			}
		}
		return content;
	}
}
