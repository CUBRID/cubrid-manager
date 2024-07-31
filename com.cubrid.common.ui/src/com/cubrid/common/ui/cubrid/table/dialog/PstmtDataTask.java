/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.newreader.TxtReader;
import com.cubrid.common.core.reader.CSVReader;
import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.control.XlsxReaderHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileConstants;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSXImportFileHandler;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.EmptyCell;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import org.apache.poi.ss.usermodel.DateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

/**
 * Do with table data task by prepared statement
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-28 created by pangqiren
 */
public class PstmtDataTask extends AbstractUITask {

    private static final Logger LOGGER = LogUtil.getLogger(PstmtDataTask.class);

    public static final int PROGRESS_ROW = 5;
    public static final int PROGRESS_COMMIT = 20;
    private final String fileName;
    private CUBRIDPreparedStatementProxy pStmt;
    private final List<PstmtParameter> parameterList;
    private final CubridDatabase database;
    private final String sql;
    private final int startRow;
    private final int rowCount;
    private final int commitLineCountOnce;
    private final String fileCharset;
    private final String dbCharset;
    private final File parentFile;

    private Connection conn;
    private int commitedCount = 0;
    private final boolean isFirstRowAsColumn;
    private boolean isCancel = false;
    private int totalProgress = 10;
    private int workedProgress = 0;
    private final boolean isIgnoreError;

    private final List<String> errorMsgList = new ArrayList<String>();
    private int totalErrorCount = 0;
    private final String errLogFilePath;
    private BufferedWriter errorLogWriter = null;
    private int jobIndex = -1;
    private final ImportFileHandler importFileHandler;
    private String separator;

    /**
     * The constructor
     *
     * @param sql
     * @param database
     * @param parameterList
     * @param importFileHandler
     */
    public PstmtDataTask(
            String sql,
            CubridDatabase database,
            List<PstmtParameter> parameterList,
            ImportFileHandler importFileHandler) {
        this.sql = sql;
        this.database = database;
        this.parameterList = parameterList;
        this.fileName = null;
        this.startRow = 0;
        this.rowCount = 0;
        this.commitLineCountOnce = 0;
        this.fileCharset = null;
        this.dbCharset = database.getDatabaseInfo().getCharSet();
        this.isFirstRowAsColumn = false;
        this.parentFile = null;
        this.isIgnoreError = false;
        this.errLogFilePath = null;
        this.importFileHandler = importFileHandler;
    }

    /**
     * The constructor
     *
     * @param sql
     * @param database
     * @param parameterList
     * @param importFileHandler
     * @param jobIndex
     */
    public PstmtDataTask(
            String sql,
            CubridDatabase database,
            List<PstmtParameter> parameterList,
            ImportFileHandler importFileHandler,
            int jobIndex) {
        this.sql = sql;
        this.database = database;
        this.parameterList = parameterList;
        this.fileName = null;
        this.startRow = 0;
        this.rowCount = 0;
        this.commitLineCountOnce = 0;
        this.fileCharset = null;
        this.dbCharset = database.getDatabaseInfo().getCharSet();
        this.isFirstRowAsColumn = false;
        this.parentFile = null;
        this.isIgnoreError = false;
        this.errLogFilePath = null;
        this.importFileHandler = importFileHandler;
        this.jobIndex = jobIndex;
    }

    /**
     * The constructor
     *
     * @param sql
     * @param database
     * @param fileName
     * @param parameterList
     * @param startRow
     * @param rowCount
     * @param commitLineCountOnce
     * @param charset
     * @param isFirstRowAsColumn
     * @param errorIgnore
     * @param errorLogDir
     * @param importFileHandler
     */
    public PstmtDataTask(
            String sql,
            CubridDatabase database,
            String fileName,
            List<PstmtParameter> parameterList,
            int startRow,
            int rowCount,
            int commitLineCountOnce,
            String charset,
            boolean isFirstRowAsColumn,
            boolean errorIgnore,
            String errorLogDir,
            ImportFileHandler importFileHandler) {
        this.sql = sql;
        this.database = database;
        this.fileName = fileName;
        this.parameterList = parameterList;
        this.startRow = startRow;
        this.rowCount = rowCount;
        this.commitLineCountOnce = commitLineCountOnce;
        this.fileCharset = charset;
        this.dbCharset = database.getDatabaseInfo().getCharSet();
        this.isFirstRowAsColumn = isFirstRowAsColumn;
        File file = new File(this.fileName);
        if (file.exists()) {
            parentFile = file.getParentFile();
        } else {
            parentFile = null;
        }
        this.isIgnoreError = errorIgnore;
        errLogFilePath =
                errorLogDir == null ? null : errorLogDir + File.separator + startRow + ".csv";
        if (errLogFilePath != null) {
            try {
                errorLogWriter =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(errLogFilePath), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                errorLogWriter = null;
            } catch (FileNotFoundException e) {
                errorLogWriter = null;
            }
        }
        this.importFileHandler = importFileHandler;
    }

    /**
     * The constructor
     *
     * @param sql
     * @param database
     * @param fileName
     * @param parameterList
     * @param startRow
     * @param rowCount
     * @param commitLineCountOnce
     * @param charset
     * @param isFirstRowAsColumn
     * @param errorIgnore
     * @param errorLogDir
     * @param importFileHandler
     * @param separator
     */
    public PstmtDataTask(
            String sql,
            CubridDatabase database,
            String fileName,
            List<PstmtParameter> parameterList,
            int startRow,
            int rowCount,
            int commitLineCountOnce,
            String charset,
            boolean isFirstRowAsColumn,
            boolean errorIgnore,
            String errorLogDir,
            ImportFileHandler importFileHandler,
            String separator) {
        this(
                sql,
                database,
                fileName,
                parameterList,
                startRow,
                rowCount,
                commitLineCountOnce,
                charset,
                isFirstRowAsColumn,
                errorIgnore,
                errorLogDir,
                importFileHandler);
        this.separator = separator;
    }

    /**
     * Execute to import data
     *
     * @param monitor IProgressMonitor
     */
    public void execute(IProgressMonitor monitor) {
        String msg =
                Messages.bind(
                        Messages.msgExeSqlTaskName,
                        new String[] {
                            String.valueOf(startRow + 1), String.valueOf(startRow + rowCount)
                        });
        monitor.beginTask(msg, totalProgress);
        try {
            errorMsg = null;
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            pStmt = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            if (fileName == null) {
                executeFromInput(monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".xlsx")) {
                executeFromXlsx(monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".xls")) {
                executeFromXls(monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".csv")) {
                executeFromCSV(monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".txt")) {
                executeFromTxt(monitor);
            }
        } catch (Exception e) {
            rollback(monitor, e);
        } finally {
            finish();
            isDone = true;
        }
    }

    /** @see AbstractTask#finish() */
    public void finish() {
        QueryUtil.freeQuery(conn, pStmt);
        if (errorLogWriter != null) {
            try {
                errorLogWriter.close();
            } catch (IOException e) {
                errorLogWriter = null;
            }
            errorLogWriter = null;
        }
    }

    /**
     * Write the error log
     *
     * @param errorLogs String[]
     */
    private void writeErrorLog(String[] errorLogs) { // FIXME move this logic to core module
        if (errorLogWriter == null) {
            return;
        }
        try {
            for (int i = 0; i < errorLogs.length; i++) {
                String errorLog = errorLogs[i];
                errorLogWriter.write(errorLog);
                if (i != errorLogs.length - 1) {
                    errorLogWriter.write(',');
                }
            }
            errorLogWriter.write('\n');
            errorLogWriter.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Return whether task is done
     *
     * @return boolean
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * @see AbstractTask#isCancel()
     * @return boolean
     */
    public boolean isCancel() {
        return isCancel;
    }

    /**
     * @see AbstractTask#isSuccess()
     * @return boolean
     */
    public boolean isSuccess() {
        return errorMsg == null && commitedCount == rowCount;
    }

    /** @see AbstractTask#cancel() */
    public void cancel() {
        if (isCancel) {
            return;
        }
        isCancel = true;
        if (!isDone) {
            try {
                if (pStmt != null) {
                    pStmt.cancel();
                }
            } catch (Exception ignored) {
                LOGGER.error("", ignored);
            }
            finish();
        }
        isDone = true;
    }

    /**
     * Get commit count
     *
     * @return int
     */
    public int getCommitedCount() {
        return commitedCount;
    }

    /**
     * Do with data from input
     *
     * @param monitor IProgressMonitor
     * @throws SQLException The exception
     */
    private void executeFromInput(IProgressMonitor monitor) throws SQLException {
        for (PstmtParameter pstmtParameter : parameterList) {
            FieldHandlerUtils.setPreparedStatementValue(pstmtParameter, pStmt, dbCharset);
        }
        workedProgress = totalProgress / 2;
        monitor.worked(workedProgress);
        if (pStmt != null) {
            pStmt.executeUpdate();
        }
        if (conn != null) {
            conn.commit();
        }
        monitor.worked(totalProgress - workedProgress);
    }

    /**
     * Error handling function.
     *
     * @param errorMsg error message
     * @throws DataFormatException The exception
     */
    private void dataTypeErrorHandling(String errorMsg) throws DataFormatException {
        if (this.isIgnoreError) {
            addErrorMsg(errorMsg);
            writeErrorLog(new String[] {errorMsg});
            totalErrorCount++;
        } else {
            this.errorMsg = errorMsg;
            throw new DataFormatException(errorMsg);
        }
    }

    public int getTotalErrorCount() {
        return totalErrorCount;
    }

    /**
     * Do with data from excel file
     *
     * @param monitor IProgressMonitor
     * @throws Exception the Exception
     */
    private void executeFromXlsx(final IProgressMonitor monitor)
            throws Exception { // FIXME move this logic to core module
        XlsxReaderHandler xlsxReader =
                new XlsxReaderHandler((XLSXImportFileHandler) importFileHandler) {

                    private SimpleDateFormat datetimeSdf;
                    private SimpleDateFormat timestampSdf;
                    private SimpleDateFormat dateSdf;
                    private SimpleDateFormat timeSdf;

                    @Override
                    public void operateRows(int sheetIndex, List<String> rowlist)
                            throws SQLException, DataFormatException {
                        if (currentRow == getTitleRow()) {
                            return;
                        }

                        XLSXImportFileHandler fileHandler =
                                (XLSXImportFileHandler) importFileHandler;
                        List<Integer> itemsNumberOfSheets = null;
                        try {
                            itemsNumberOfSheets =
                                    fileHandler.getSourceFileInfo().getItemsNumberOfSheets();
                        } catch (Exception ex) {
                            LOGGER.error(ex.getMessage());
                            return;
                        }
                        int rowFromStart = 0;
                        for (int i = 0; i < sheetIndex; i++) {
                            rowFromStart += itemsNumberOfSheets.get(i);
                        }
                        rowFromStart += currentRow;
                        int absoluteStartRow = getAbsoluteRowNum(startRow, itemsNumberOfSheets);
                        if (absoluteStartRow > rowFromStart) {
                            return;
                        }

                        int absoluteEndRow =
                                getAbsoluteRowNum(startRow + rowCount, itemsNumberOfSheets);
                        int rowCountIncludingTitle = absoluteEndRow - absoluteStartRow;

                        int relativeRow = rowFromStart - absoluteStartRow;
                        if (relativeRow > rowCountIncludingTitle - 1) {
                            return;
                        }

                        for (int i = 0; i < parameterList.size(); i++) {
                            PstmtParameter pstmtParameter = parameterList.get(i);
                            int column = Integer.parseInt(pstmtParameter.getStringParamValue());
                            String dataType = DataType.getRealType(pstmtParameter.getDataType());
                            String cellContent = rowlist.get(column);
                            int cellType = FieldHandlerUtils.getCellType(dataType, cellContent);
                            boolean isHaveError = false;
                            try {
                                double value;
                                Date dateCon;
                                switch (cellType) {
                                    case -1:
                                        cellContent = DataType.NULL_EXPORT_FORMAT;
                                        break;
                                    case 0:
                                        if (cellContent.contains(".")) {
                                            cellContent =
                                                    cellContent.substring(
                                                            0, cellContent.indexOf('.'));
                                        }
                                        break;
                                    case 2:
                                        value = Double.parseDouble(cellContent.trim());
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = datetimeSdf.format(dateCon);
                                        break;
                                    case 3:
                                        value = Double.parseDouble(cellContent.trim());
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = timestampSdf.format(dateCon);
                                        break;
                                    case 4:
                                        value = Double.parseDouble(cellContent.trim());
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = dateSdf.format(dateCon);
                                        break;
                                    case 5:
                                        value = Double.parseDouble(cellContent.trim());
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = timeSdf.format(dateCon);
                                        break;
                                    default:
                                        break;
                                }
                            } catch (NumberFormatException e) {
                                isHaveError = true;
                            }

                            String content =
                                    FieldHandlerUtils.getRealValueForImport(
                                            dataType, cellContent, parentFile);
                            FormatDataResult formatDataResult =
                                    DBAttrTypeFormatter.format(
                                            dataType, content, false, dbCharset, true);
                            if (formatDataResult.isSuccess()) {
                                PstmtParameter parameter =
                                        new PstmtParameter(
                                                pstmtParameter.getParamName(),
                                                pstmtParameter.getParamIndex(),
                                                pstmtParameter.getDataType(),
                                                content);
                                parameter.setCharSet(fileCharset);
                                FieldHandlerUtils.setPreparedStatementValue(
                                        parameter, pStmt, dbCharset);
                            } else {
                                isHaveError = true;
                                PstmtParameter parameter =
                                        new PstmtParameter(
                                                pstmtParameter.getParamName(),
                                                pstmtParameter.getParamIndex(),
                                                pstmtParameter.getDataType(),
                                                null);
                                parameter.setCharSet(fileCharset);
                                FieldHandlerUtils.setPreparedStatementValue(
                                        parameter, pStmt, dbCharset);
                            }
                            if (isHaveError) {
                                dataTypeErrorHandling(getErrorMsg(i, column, dataType));
                            }
                        }
                        if (pStmt != null) {
                            pStmt.addBatch();
                            monitor.worked(PROGRESS_ROW);
                            workedProgress += PROGRESS_ROW;
                        }
                        int importedRow = relativeRow + 1;
                        if (importedRow > 0 && importedRow % commitLineCountOnce == 0) {
                            commit(monitor, importedRow);
                        } else {
                            if (importedRow == rowCount && importedRow % commitLineCountOnce != 0) {
                                commit(monitor, importedRow);
                            }
                        }
                        if (isCancel) {
                            return;
                        }
                    }

                    public void startDocument() {
                        if (isFirstRowAsColumn) {
                            setTitleRow(0);
                        }
                        datetimeSdf =
                                new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                        timestampSdf =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        timeSdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    }

                    private int getAbsoluteRowNum(int value, List<Integer> itemsNumberOfSheets) {
                        if (itemsNumberOfSheets == null || itemsNumberOfSheets.isEmpty()) {
                            return value;
                        }
                        if (getTitleRow() == -1) {
                            return value;
                        }
                        int upLimit = 0;
                        int downLimit = 0;
                        int absoluteVal = value;
                        for (int i = 0; i < itemsNumberOfSheets.size(); i++) {
                            int absoluteValIncldingTitle = value + i + 1;
                            upLimit += itemsNumberOfSheets.get(i);
                            if (i == 0) {
                                if (absoluteValIncldingTitle <= upLimit) {
                                    absoluteVal = absoluteValIncldingTitle;
                                    break;
                                }
                            } else {
                                downLimit += itemsNumberOfSheets.get(i - 1);
                                if (absoluteValIncldingTitle > downLimit
                                        && absoluteValIncldingTitle <= upLimit) {
                                    absoluteVal = absoluteValIncldingTitle;
                                    break;
                                }
                            }
                        }
                        return absoluteVal;
                    }
                };
        xlsxReader.process(fileName);
    }

    /**
     * Get the error message about error value.
     *
     * @param row of error location
     * @param column of error location
     * @param dataType data type
     * @return error message.
     */
    private String getErrorMsg(int row, int column, String dataType) {
        return Messages.bind(
                Messages.errDataTypeNoMatch,
                new String[] {String.valueOf(startRow + row), String.valueOf(column), dataType});
    }

    /**
     * Do with data from excel file
     *
     * @param monitor IProgressMonitor
     */
    private void executeFromXls(IProgressMonitor monitor) { // FIXME move this logic to core module
        try {
            XLSImportFileHandler fileHandler = (XLSImportFileHandler) importFileHandler;
            Sheet[] sheets = fileHandler.getSheets();
            ImportFileDescription fileDesc = fileHandler.getSourceFileInfo();
            int rowNum = 0;
            int currentRow = 0;
            for (int sheetNum = 0; sheetNum < sheets.length; sheetNum++) {
                int start = 0;
                int lastRowNum = rowNum;
                int rows = fileDesc.getItemsNumberOfSheets().get(sheetNum);
                if (isFirstRowAsColumn) {
                    rowNum += rows - 1;
                } else {
                    rowNum += rows;
                }
                if (startRow > rowNum) {
                    continue;
                }

                if (lastRowNum >= startRow) {
                    start = isFirstRowAsColumn ? 1 : 0;
                } else {
                    start = startRow - lastRowNum + (isFirstRowAsColumn ? 1 : 0);
                }
                Sheet sheet = sheets[sheetNum];
                String content = null;
                String pattern = null;
                for (int i = start; i < rows && currentRow < rowCount; i++) {
                    for (int j = 0; j < parameterList.size(); j++) {
                        PstmtParameter pstmtParameter = parameterList.get(j);
                        int column = Integer.parseInt(pstmtParameter.getStringParamValue());
                        Cell cell = sheet.getCell(column, i);
                        content = null;
                        pattern = null;
                        if (cell == null) {
                            content = null;
                        } else if (cell instanceof EmptyCell) {
                            content = null;
                        } else {
                            content = cell.getContents();
                            CellFormat format = cell.getCellFormat();
                            if (format != null && format.getFormat() != null) {
                                pattern = format.getFormat().getFormatString();
                            }
                        }
                        String dataType = DataType.getRealType(pstmtParameter.getDataType());
                        content =
                                FieldHandlerUtils.getRealValueForImport(
                                        dataType, content, parentFile);
                        FormatDataResult formatDataResult = null;
                        if (StringUtil.isEmpty(pattern)) {
                            formatDataResult =
                                    DBAttrTypeFormatter.format(
                                            dataType, content, false, dbCharset, true);
                        } else {
                            formatDataResult =
                                    DBAttrTypeFormatter.format(
                                            dataType, content, pattern, false, dbCharset, true);
                        }

                        if (formatDataResult.isSuccess()) {
                            PstmtParameter parameter =
                                    new PstmtParameter(
                                            pstmtParameter.getParamName(),
                                            pstmtParameter.getParamIndex(),
                                            pstmtParameter.getDataType(),
                                            content);
                            parameter.setCharSet(fileCharset);
                            FieldHandlerUtils.setPreparedStatementValue(
                                    parameter, pStmt, dbCharset);
                        } else {
                            dataTypeErrorHandling(getErrorMsg(i, column, dataType));
                            PstmtParameter parameter =
                                    new PstmtParameter(
                                            pstmtParameter.getParamName(),
                                            pstmtParameter.getParamIndex(),
                                            pstmtParameter.getDataType(),
                                            null);
                            parameter.setCharSet(fileCharset);
                            FieldHandlerUtils.setPreparedStatementValue(
                                    parameter, pStmt, dbCharset);
                        }
                    }
                    if (pStmt != null) {
                        pStmt.addBatch();
                        monitor.worked(PROGRESS_ROW);
                        workedProgress += PROGRESS_ROW;
                    }
                    currentRow++;
                    if (currentRow > 0 && currentRow % commitLineCountOnce == 0) {
                        commit(monitor, currentRow);
                    }
                    if (isCancel) {
                        return;
                    }
                }
            }
            if (currentRow > 0 && currentRow % commitLineCountOnce > 0) {
                commit(monitor, currentRow);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (BiffException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * Do with data from CSV file
     *
     * @param monitor IProgressMonitor
     */
    private void executeFromCSV(IProgressMonitor monitor) { // FIXME move this logic to core module
        File file = new File(fileName);
        CSVReader csvReader = null;
        try {
            if (fileCharset == null || fileCharset.trim().length() == 0) {
                csvReader = new CSVReader(new FileReader(file));
            } else {
                csvReader =
                        new CSVReader(
                                new InputStreamReader(new FileInputStream(file), fileCharset));
            }
            if (isFirstRowAsColumn) {
                csvReader.readNext();
            }
            int currentRow = 0;
            int rowNum = 0;
            String[] cvsRow;
            while ((cvsRow = csvReader.readNext()) != null && currentRow < rowCount) {
                rowNum++;
                if (startRow >= rowNum) {
                    continue;
                }

                for (int j = 0; j < parameterList.size(); j++) {
                    PstmtParameter pstmtParameter = parameterList.get(j);
                    int column = Integer.parseInt(pstmtParameter.getStringParamValue());
                    String content = null;
                    if (cvsRow.length > column) {
                        content = cvsRow[column];
                    }
                    String dataType = DataType.getRealType(pstmtParameter.getDataType());
                    content =
                            FieldHandlerUtils.getRealValueForImport(dataType, content, parentFile);
                    FormatDataResult formatDataResult =
                            DBAttrTypeFormatter.format(dataType, content, false, dbCharset, true);
                    if (formatDataResult.isSuccess()) {
                        PstmtParameter parameter =
                                new PstmtParameter(
                                        pstmtParameter.getParamName(),
                                        pstmtParameter.getParamIndex(),
                                        pstmtParameter.getDataType(),
                                        content);
                        parameter.setCharSet(fileCharset);
                        FieldHandlerUtils.setPreparedStatementValue(parameter, pStmt, dbCharset);
                    } else {
                        int row = isFirstRowAsColumn ? currentRow + 1 : currentRow;
                        dataTypeErrorHandling(getErrorMsg(row, column, dataType));
                        PstmtParameter parameter =
                                new PstmtParameter(
                                        pstmtParameter.getParamName(),
                                        pstmtParameter.getParamIndex(),
                                        pstmtParameter.getDataType(),
                                        null);
                        parameter.setCharSet(fileCharset);
                        FieldHandlerUtils.setPreparedStatementValue(parameter, pStmt, dbCharset);
                    }
                }
                if (pStmt != null) {
                    pStmt.addBatch();
                    monitor.worked(PROGRESS_ROW);
                    workedProgress += PROGRESS_ROW;
                }
                currentRow++;
                if (currentRow > 0 && currentRow % commitLineCountOnce == 0) {
                    commit(monitor, currentRow);
                }
                if (isCancel) {
                    return;
                }
            }
            if (currentRow > 0 && currentRow % commitLineCountOnce > 0) {
                commit(monitor, currentRow);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                    csvReader = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    /**
     * Do with data from Txt file
     *
     * @param monitor IProgressMonitor
     */
    private void executeFromTxt(IProgressMonitor monitor) { // FIXME move this logic to core module
        File file = new File(fileName);
        TxtReader txtReader = null;
        try {
            if (fileCharset == null || fileCharset.trim().length() == 0) {
                txtReader = new TxtReader(new FileReader(file), separator);
            } else {
                txtReader =
                        new TxtReader(
                                new InputStreamReader(new FileInputStream(file), fileCharset),
                                separator);
            }
            if (isFirstRowAsColumn) {
                txtReader.readNext();
            }
            int currentRow = 0;
            int rowNum = 0;
            String[] cvsRow;
            while ((cvsRow = txtReader.readNext()) != null && currentRow < rowCount) {
                rowNum++;
                if (startRow >= rowNum) {
                    continue;
                }

                for (int j = 0; j < parameterList.size(); j++) {
                    PstmtParameter pstmtParameter = parameterList.get(j);
                    int column = Integer.parseInt(pstmtParameter.getStringParamValue());
                    String content = null;
                    if (cvsRow.length > column) {
                        content = cvsRow[column];
                    }
                    String dataType = DataType.getRealType(pstmtParameter.getDataType());
                    content =
                            FieldHandlerUtils.getRealValueForImport(dataType, content, parentFile);
                    FormatDataResult formatDataResult =
                            DBAttrTypeFormatter.format(dataType, content, false, dbCharset, true);
                    if (formatDataResult.isSuccess()) {
                        PstmtParameter parameter =
                                new PstmtParameter(
                                        pstmtParameter.getParamName(),
                                        pstmtParameter.getParamIndex(),
                                        pstmtParameter.getDataType(),
                                        content);
                        parameter.setCharSet(fileCharset);
                        FieldHandlerUtils.setPreparedStatementValue(parameter, pStmt, dbCharset);
                    } else {
                        int row = isFirstRowAsColumn ? currentRow + 1 : currentRow;
                        dataTypeErrorHandling(getErrorMsg(row, column, dataType));
                        PstmtParameter parameter =
                                new PstmtParameter(
                                        pstmtParameter.getParamName(),
                                        pstmtParameter.getParamIndex(),
                                        pstmtParameter.getDataType(),
                                        null);
                        parameter.setCharSet(fileCharset);
                        FieldHandlerUtils.setPreparedStatementValue(parameter, pStmt, dbCharset);
                    }
                }
                if (pStmt != null) {
                    pStmt.addBatch();
                    monitor.worked(PROGRESS_ROW);
                    workedProgress += PROGRESS_ROW;
                }
                currentRow++;
                if (currentRow > 0 && currentRow % commitLineCountOnce == 0) {
                    commit(monitor, currentRow);
                }
                if (isCancel) {
                    return;
                }
            }
            if (currentRow > 0 && currentRow % commitLineCountOnce > 0) {
                commit(monitor, currentRow);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                    txtReader = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    /**
     * Commit the data
     *
     * @param monitor IProgressMonitor
     * @param currentRow int
     * @throws SQLException The exception
     */
    private void commit(IProgressMonitor monitor, int currentRow) throws SQLException {
        if (pStmt == null) {
            return;
        }
        int[] counts = pStmt.executeBatch();
        int insertCount = 0;
        for (int i = 0; counts != null && i < counts.length; i++) {
            if (counts[i] > 0) {
                insertCount += counts[i];
            }
        }
        if (conn != null) {
            conn.commit();
        }
        commitedCount += insertCount;
        pStmt.clearBatch();
        monitor.worked(PROGRESS_COMMIT);
        workedProgress += PROGRESS_COMMIT;
        monitor.subTask(Messages.bind(Messages.msgRowsFinish, currentRow));
    }

    /**
     * Roll back data
     *
     * @param monitor IProgressMonitor
     * @param ex Exception
     */
    private void rollback(IProgressMonitor monitor, Exception ex) {

        try {
            if (isCancel && conn.isClosed()) {
                this.errorMsg = null;
            } else {
                String errorMsg = null;
                if (ex instanceof RuntimeException && ex.getCause() != null) {
                    if (ex.getCause() instanceof OutOfMemoryError) {
                        errorMsg = Messages.errNoAvailableMemory;
                    } else {
                        errorMsg = ex.getCause().getMessage();
                    }
                } else {
                    errorMsg = ex.getMessage();
                }
                errorMsg =
                        errorMsg == null || errorMsg.trim().length() == 0
                                ? "Unknown error."
                                : errorMsg;
                if (this.isIgnoreError) {
                    addErrorMsg(errorMsg);
                    writeErrorLog(new String[] {errorMsg});
                    totalErrorCount++;
                }
                this.errorMsg = errorMsg;
                if (jobIndex != -1) {
                    String detialMsg =
                            System.getProperty("line.separator")
                                    + "Error line : "
                                    + String.valueOf(jobIndex);
                    if (jobIndex > 1) {
                        detialMsg += " (Before lines had been commited successfully)";
                    }
                    this.errorMsg += detialMsg;
                }
                monitor.worked(totalProgress - workedProgress);
                monitor.subTask(Messages.errOccur);
                LOGGER.error("", ex);
            }
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Get the parameter of PSTMT list
     *
     * @return List<List<PstmtParameter>>
     */
    public List<List<PstmtParameter>> getRowParameterList() {
        if (fileName.toLowerCase(Locale.getDefault()).endsWith(".xlsx")) {
            return getRowParameterListFromXlsx();
        } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".xls")) {
            return getRowParameterListFromExcel();
        } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".csv")) {
            return getRowParameterListFromCSV();
        }
        return null;
    }

    /**
     * Get the row parameter list from excel 2007
     *
     * @return List<List<PstmtParameter>>
     */
    private List<List<PstmtParameter>>
            getRowParameterListFromXlsx() { // FIXME move this logic to core module
        final List<List<PstmtParameter>> rowParaList = new ArrayList<List<PstmtParameter>>();
        XlsxReaderHandler xlsxReader =
                new XlsxReaderHandler((XLSXImportFileHandler) importFileHandler) {
                    private SimpleDateFormat datetimeSdf;
                    private SimpleDateFormat timestampSdf;
                    private SimpleDateFormat dateSdf;
                    private SimpleDateFormat timeSdf;

                    @Override
                    public void operateRows(int sheetIndex, List<String> rowlist)
                            throws SQLException, DataFormatException {
                        if (currentRow == getTitleRow()) {
                            return;
                        }

                        int rowFromStart =
                                sheetIndex
                                                * (ImportFileConstants.XLSX_ROW_LIMIT
                                                        + 1
                                                        - getTitleRow())
                                        + currentRow;
                        if (startRow > rowFromStart) {
                            return;
                        }

                        int lastRow = startRow + rowCount + getTitleRow() + 1;
                        if (lastRow <= rowFromStart) {
                            return;
                        }
                        List<PstmtParameter> paraList = new ArrayList<PstmtParameter>();
                        for (int i = 0; i < parameterList.size(); i++) {
                            PstmtParameter pstmtParameter = parameterList.get(i);

                            PstmtParameter newParam =
                                    new PstmtParameter(
                                            pstmtParameter.getParamName(),
                                            pstmtParameter.getParamIndex(),
                                            pstmtParameter.getDataType(),
                                            null);

                            int column = Integer.parseInt(pstmtParameter.getStringParamValue());

                            String dataType = DataType.getRealType(pstmtParameter.getDataType());
                            String cellContent = rowlist.get(column).trim();
                            int cellType = FieldHandlerUtils.getCellType(dataType, cellContent);
                            double value;
                            Date dateCon;
                            switch (cellType) {
                                case -1:
                                    cellContent = DataType.NULL_EXPORT_FORMAT;
                                    break;
                                case 2:
                                    try {
                                        value = Double.parseDouble(cellContent);
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = datetimeSdf.format(dateCon);
                                    } catch (NumberFormatException e) {
                                        cellContent = DataType.NULL_EXPORT_FORMAT;
                                    }
                                    break;
                                case 3:
                                    try {
                                        value = Double.parseDouble(cellContent);
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = timestampSdf.format(dateCon);
                                    } catch (Exception e) {
                                        cellContent = DataType.NULL_EXPORT_FORMAT;
                                    }
                                    break;
                                case 4:
                                    try {
                                        value = Double.parseDouble(cellContent);
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = dateSdf.format(dateCon);
                                    } catch (Exception e) {
                                        cellContent = DataType.NULL_EXPORT_FORMAT;
                                    }
                                    break;
                                case 5:
                                    try {
                                        value = Double.parseDouble(cellContent);
                                        dateCon = DateUtil.getJavaDate(value);
                                        cellContent = timeSdf.format(dateCon);
                                    } catch (Exception e) {
                                        cellContent = DataType.NULL_EXPORT_FORMAT;
                                    }
                                    break;
                                default:
                                    break;
                            }

                            String content =
                                    FieldHandlerUtils.getRealValueForImport(
                                            dataType, cellContent, parentFile);
                            FormatDataResult formatDataResult =
                                    DBAttrTypeFormatter.format(
                                            dataType, content, false, dbCharset, true);

                            if (formatDataResult.isSuccess()) {
                                newParam.setCharSet(fileCharset);
                                newParam.setParamValue(content);
                            } else {
                                dataTypeErrorHandling(getErrorMsg(i, column, dataType));
                                newParam.setCharSet(fileCharset);
                                newParam.setParamValue(null);
                            }
                            paraList.add(newParam);
                        }
                        rowParaList.add(paraList);
                    }

                    public void startDocument() {
                        if (isFirstRowAsColumn) {
                            setTitleRow(0);
                        }
                        datetimeSdf =
                                new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                        timestampSdf =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        timeSdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    }
                };
        try {
            xlsxReader.process(fileName);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return rowParaList;
    }

    /**
     * Get the parameter of PSTMT list from excel file
     *
     * @return List<List<PstmtParameter>>
     */
    private List<List<PstmtParameter>>
            getRowParameterListFromExcel() { // FIXME move this logic to core module

        File file = new File(fileName);
        Workbook workbook = null;
        List<List<PstmtParameter>> rowParaList = new ArrayList<List<PstmtParameter>>();
        try {
            if (fileCharset == null) {
                workbook = Workbook.getWorkbook(file);
            } else {
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setEncoding(fileCharset);
                workbook = Workbook.getWorkbook(file, workbookSettings);
            }
            Sheet[] sheets = workbook.getSheets();
            for (int sheetNum = 0; sheetNum < sheets.length; sheetNum++) {
                Sheet sheet = sheets[sheetNum];
                int rows = sheet.getRows();
                int start = 0;
                if (isFirstRowAsColumn) {
                    start = 1;
                }
                for (int i = start; i < rows; i++) {
                    List<PstmtParameter> paraList = new ArrayList<PstmtParameter>();
                    for (int j = 0; j < parameterList.size(); j++) {
                        PstmtParameter pstmtParameter = parameterList.get(j);

                        PstmtParameter newParam =
                                new PstmtParameter(
                                        pstmtParameter.getParamName(),
                                        pstmtParameter.getParamIndex(),
                                        pstmtParameter.getDataType(),
                                        null);

                        int column = Integer.parseInt(pstmtParameter.getStringParamValue());
                        Cell cell = sheet.getCell(column, i);
                        String content = null;
                        String pattern = null;
                        if (cell == null) {
                            content = null;
                        } else if (cell instanceof EmptyCell) {
                            content = null;
                        } else {
                            content = cell.getContents();
                            CellFormat format = cell.getCellFormat();
                            if (format != null && format.getFormat() != null) {
                                pattern = format.getFormat().getFormatString();
                            }
                        }
                        String dataType = DataType.getRealType(pstmtParameter.getDataType());
                        content =
                                FieldHandlerUtils.getRealValueForImport(
                                        dataType, content, parentFile);
                        FormatDataResult formatDataResult = null;
                        if (StringUtil.isEmpty(pattern)) {
                            formatDataResult =
                                    DBAttrTypeFormatter.format(
                                            dataType, content, false, dbCharset, true);
                        } else {
                            formatDataResult =
                                    DBAttrTypeFormatter.format(
                                            dataType, content, pattern, false, dbCharset, true);
                        }

                        if (formatDataResult.isSuccess()) {
                            newParam.setCharSet(fileCharset);
                            newParam.setParamValue(content);
                        } else {
                            dataTypeErrorHandling(getErrorMsg(i, column, dataType));
                            newParam.setCharSet(fileCharset);
                            newParam.setParamValue(null);
                        }
                        paraList.add(newParam);
                    }
                    rowParaList.add(paraList);
                }
            }
        } catch (BiffException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return rowParaList;
    }

    /**
     * Get the parameter of PSTMT list from CSV file
     *
     * @return List<List<PstmtParameter>>
     */
    private List<List<PstmtParameter>>
            getRowParameterListFromCSV() { // FIXME move this logic to core module

        List<List<PstmtParameter>> rowParaList = new ArrayList<List<PstmtParameter>>();
        File file = new File(fileName);
        CSVReader csvReader = null;
        try {
            if (fileCharset == null || fileCharset.trim().length() == 0) {
                csvReader = new CSVReader(new FileReader(file));
            } else {
                csvReader =
                        new CSVReader(
                                new InputStreamReader(new FileInputStream(file), fileCharset));
            }
            int currentRow = 0;
            if (isFirstRowAsColumn) {
                csvReader.readNext();
                currentRow++;
            }
            String[] cvsRow;
            while ((cvsRow = csvReader.readNext()) != null) {
                List<PstmtParameter> paraList = new ArrayList<PstmtParameter>();
                for (int j = 0; j < parameterList.size(); j++) {
                    PstmtParameter pstmtParameter = parameterList.get(j);

                    PstmtParameter newParam =
                            new PstmtParameter(
                                    pstmtParameter.getParamName(),
                                    pstmtParameter.getParamIndex(),
                                    pstmtParameter.getDataType(),
                                    null);

                    int column = Integer.parseInt(pstmtParameter.getStringParamValue());
                    String content = null;
                    if (cvsRow.length > column) {
                        content = cvsRow[column];
                    }
                    String dataType = DataType.getRealType(pstmtParameter.getDataType());
                    content =
                            FieldHandlerUtils.getRealValueForImport(dataType, content, parentFile);
                    FormatDataResult formatDataResult =
                            DBAttrTypeFormatter.format(
                                    DataType.getRealType(pstmtParameter.getDataType()),
                                    content,
                                    false,
                                    dbCharset,
                                    true);
                    if (formatDataResult.isSuccess()) {
                        newParam.setParamValue(content);
                        newParam.setCharSet(fileCharset);
                    } else {
                        dataTypeErrorHandling(getErrorMsg(currentRow, column, dataType));
                        newParam.setParamValue(null);
                        newParam.setCharSet(fileCharset);
                    }
                    paraList.add(newParam);
                }
                rowParaList.add(paraList);
                currentRow++;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return rowParaList;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
    }

    /**
     * Add error message to list.
     *
     * @param errorMsg error message.
     */
    private void addErrorMsg(String errorMsg) {
        if (errorMsgList.size() < 100) {
            errorMsgList.add(errorMsg);
        }
    }

    /**
     * Get the error messages.
     *
     * @return the error message list.
     */
    public List<String> getErrorMsgList() {
        List<String> result = new ArrayList<String>();
        result.addAll(errorMsgList);
        return result;
    }

    public String getErrLogFilePath() {
        return errLogFilePath;
    }

    /**
     * Make the error log directory
     *
     * @return String
     */
    public static String makeErrorLogDir() { // FIXME move this logic to core module
        String errorLogDir =
                CommonUIPlugin.getDefault()
                        .getStateLocation()
                        .append(String.valueOf(System.currentTimeMillis()))
                        .toOSString();
        File file = new File(errorLogDir);
        deleteAllFile(file);
        return file.mkdirs() ? errorLogDir : null;
    }

    /**
     * Delete all files
     *
     * @param file File
     * @return boolean
     */
    public static boolean deleteAllFile(File file) { // FIXME move this logic to core module
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                deleteAllFile(files[i]);
            }
            return file.delete();
        }
        return true;
    }
}
