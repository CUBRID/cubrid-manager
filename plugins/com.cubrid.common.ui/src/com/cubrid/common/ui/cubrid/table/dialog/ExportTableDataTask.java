/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.control.XlsxWriterHelper;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileConstants;
import com.cubrid.common.ui.query.control.Export;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * Export Table Data Utility
 *
 * @author pangqiren 2009-6-4
 */
public class ExportTableDataTask extends AbstractUITask {
    private static final Logger LOGGER = LogUtil.getLogger(ExportTableDataTask.class);

    // when export XLS data, will cause the memory not enough, need remaining memory size
    public static final long REMAINING_MEMORY_SIZE = 100 * 1024 * 1024;

    private final String fileName;
    private final File file;
    private String sql;
    private String countSql;
    private final String exportedTableName;
    private final CubridDatabase database;
    private boolean isExit = false;
    private final boolean isFirstRowAsColumnName;
    private boolean isHasBigValue = false;
    private final String fileCharset;
    public static final int COMMIT_LINES = 100;

    private Connection conn = null;
    private Statement stmt = null;
    private CUBRIDResultSetProxy rs = null;

    private boolean isCancel = false;
    private int exportedCount = 0;
    private int totalLineCount = 0;
    private final String tableName;
    private final String nullValue;
    private final String seprator;

    /**
     * The constructor
     *
     * @param tableName
     * @param database
     * @param isFirstRowAsColumnName
     * @param file
     * @param fileCharset
     */
    public ExportTableDataTask(
            String tableName,
            CubridDatabase database,
            boolean isFirstRowAsColumnName,
            File file,
            String fileCharset,
            String nullValue,
            String seprator,
            List<String> columnNames,
            Object whereCondition) {
        this.database = database;
        this.isFirstRowAsColumnName = isFirstRowAsColumnName;
        this.file = file;
        this.nullValue = nullValue;
        StringBuffer columns = new StringBuffer();
        int size = columnNames.size();
        for (int i = 0; i < columnNames.size(); i++) {
            columns.append(QuerySyntax.escapeKeyword(columnNames.get(i)));
            if (i != size - 1) {
                columns.append(',');
            }
        }
        this.tableName = tableName;
        sql = "SELECT " + columns + " FROM " + QuerySyntax.escapeKeyword(tableName) + " ";
        countSql = "SELECT COUNT(*) FROM " + QuerySyntax.escapeKeyword(tableName) + " ";
        if (whereCondition != null) {
            String sqlFilterPart = whereCondition.toString().trim();
            if (StringUtil.isNotEmpty(sqlFilterPart)) {
                // append "where" if necessary
                if (!sqlFilterPart.startsWith("where") && !sqlFilterPart.startsWith("WHERE")) {
                    sqlFilterPart = " WHERE " + sqlFilterPart;
                }
                sql += sqlFilterPart;
                countSql += sqlFilterPart;
            }
        }
        exportedTableName = tableName;
        this.fileName = file.getName();
        this.fileCharset = fileCharset;
        this.seprator = seprator;
    }

    /**
     * Get total line
     *
     * @return int
     */
    public int getTotalLineCount() {
        if (totalLineCount > 0) {
            return totalLineCount;
        }

        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            stmt =
                    conn.createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY,
                            ResultSet.HOLD_CURSORS_OVER_COMMIT);

            rs = (CUBRIDResultSetProxy) stmt.executeQuery(countSql);
            while (rs.next()) {
                totalLineCount = rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            finish();
        }

        return totalLineCount;
    }

    /**
     * Export table data
     *
     * @param monitor IProgressMonitor
     */
    public void execute(IProgressMonitor monitor) {
        monitor.beginTask(
                Messages.bind(Messages.exportMonitorMsg, tableName, totalLineCount),
                totalLineCount * 10);
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            stmt =
                    conn.createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY,
                            ResultSet.HOLD_CURSORS_OVER_COMMIT);

            rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql);
            if (fileName.toLowerCase(Locale.getDefault()).endsWith(".xlsx")) {
                exportXlsx(rs, monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".xls")) {
                exportXls(rs, monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".csv")) {
                exportTxt(rs, monitor, ",", "\"");
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".sql")) {
                exportSql(rs, exportedTableName, monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".obs")) {
                exportLoad(rs, exportedTableName, monitor);
            } else if (fileName.toLowerCase(Locale.getDefault()).endsWith(".txt")) {
                exportTxt(rs, monitor, seprator, "\"");
            } else {
                exportTxt(rs, monitor, ",", "\"");
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            LOGGER.error("", e);
        } catch (OutOfMemoryError e) {
            errorMsg = Messages.errNoMemory;
            LOGGER.error("", e);
        } finally {
            finish();
        }
        isDone = true;
    }

    /**
     * Export data as XLSX file format
     *
     * @param rs CUBRIDResultSetProxy
     * @param monitor IProgressMonitor
     * @throws IOException The exception
     * @throws SQLException The exception
     */
    private void exportXlsx(CUBRIDResultSetProxy rs, final IProgressMonitor monitor)
            throws IOException, SQLException { // FIXME move this logic to core module
        int rowLimit = ImportFileConstants.XLSX_ROW_LIMIT; // 1048576: limit xlsx row number.
        int columnLimit = ImportFileConstants.XLSX_COLUMN_LIMIT; // 16384: limit xlsx column number.
        int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;

        CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
        int colCount = rsmt.getColumnCount();
        if (colCount >= columnLimit) {
            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    if (!CommonUITool.openConfirmBox(
                                            Messages.exportColumnCountOverWarnInfo)) {
                                        isExit = true;
                                    }
                                }
                            });
            if (isExit) {
                return;
            }
            colCount = columnLimit;
        }

        List<String> columnTitles = new ArrayList<String>();
        for (int j = 1; j <= colCount; j++) {
            String columnName = rsmt.getColumnName(j);
            String columnType = rsmt.getColumnTypeName(j);
            int precision = rsmt.getPrecision(j);
            columnType = columnType == null ? "" : columnType;
            setIsHasBigValue(columnType, precision);
            // the data length > XLS column character limit
            if (precision > cellCharacterLimit) {
                final String confirmMSG =
                        Messages.bind(Messages.exportCharacterCountExceedWarnInfo, columnName);

                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        if (!CommonUITool.openConfirmBox(confirmMSG)) {
                                            isExit = true;
                                        }
                                    }
                                });
                if (isExit) {
                    return;
                }
            }
            if (columnName.length() > cellCharacterLimit) {
                columnName = columnName.substring(0, cellCharacterLimit);
            }
            columnTitles.add(columnName);
        }

        XlsxWriterHelper xlsxWriterhelper = new XlsxWriterHelper();
        // create memory workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        Calendar cal = Calendar.getInstance();
        int datetimeStyleIndex =
                ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("datetime")).getIndex();
        int timestampStyleIndex =
                ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("timestamp")).getIndex();
        int dateStyleIndex =
                ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("date")).getIndex();
        int timeStyleIndex =
                ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("time")).getIndex();
        int sheetNum = 1;
        int xssfRowNum = 0;

        Map<String, File> fileMap = new HashMap<String, File>();
        XlsxWriterHelper.SpreadsheetWriter sheetWriter = null;
        try {
            sheetWriter =
                    createSheetWriter(
                            workbook,
                            xlsxWriterhelper,
                            sheetNum,
                            fileMap,
                            columnTitles,
                            xssfRowNum);

            if (isFirstRowAsColumnName && columnTitles != null) {
                xssfRowNum++;
            }

            while (rs.next()) {
                sheetWriter.insertRow(xssfRowNum);
                for (int k = 1; k <= colCount; k++) {
                    String colType = rsmt.getColumnTypeName(k);
                    colType = FieldHandlerUtils.amendDataTypeByResult(rs, k, colType);
                    int precision = rsmt.getPrecision(k);
                    setIsHasBigValue(colType, precision);
                    Object cellValue =
                            FieldHandlerUtils.getRsValueForExport(colType, rs, k, nullValue);

                    if (cellValue instanceof Long) {
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
                        String cellStr =
                                cellValue.toString().length() > cellCharacterLimit
                                        ? cellValue.toString().substring(0, cellCharacterLimit)
                                        : cellValue.toString();
                        sheetWriter.createCell(k - 1, Export.covertXMLString(cellStr));
                    }
                }
                sheetWriter.endRow();
                xssfRowNum++;
                if (((exportedCount + 1) % rowLimit) == 0) {
                    sheetNum++;
                    xssfRowNum -= rowLimit;
                    try {
                        XlsxWriterHelper.writeSheetWriter(sheetWriter);
                    } catch (IOException e) {
                        sheetWriter = null;
                        throw e;
                    }
                    sheetWriter =
                            createSheetWriter(
                                    workbook,
                                    xlsxWriterhelper,
                                    sheetNum,
                                    fileMap,
                                    columnTitles,
                                    xssfRowNum);
                }
                exportedCount++;
                monitor.worked(10);
                monitor.subTask(Messages.bind(Messages.msgExportDataRow, exportedCount));
            }
        } finally {
            try {
                XlsxWriterHelper.writeSheetWriter(sheetWriter);
            } catch (IOException e) {
                sheetWriter = null;
                throw e;
            } finally {
                XlsxWriterHelper.writeWorkbook(workbook, xlsxWriterhelper, fileMap, file);
            }
        }
    }

    /**
     * Create the instance of SpreadsheetWriter and based upon the given condition writing the
     * header of a sheet
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
    private XlsxWriterHelper.SpreadsheetWriter createSheetWriter(
            XSSFWorkbook workbook,
            XlsxWriterHelper xlsxWriterhelper,
            int sheetNum,
            Map<String, File> fileMap,
            List<String> columnTitles,
            int xssfRowNum)
            throws IOException { // FIXME move this logic to core module
        XSSFSheet sheet = workbook.createSheet("sheet" + sheetNum);
        String sheetRef = sheet.getPackagePart().getPartName().getName().substring(1);

        File tmp = File.createTempFile("sheet" + sheetNum, ".xml");
        fileMap.put(sheetRef, tmp);

        String charset = null;
        if (fileCharset == null || fileCharset.trim().length() == 0) {
            charset = "UTF-8";
        } else {
            charset = fileCharset;
        }
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmp), charset);
        XlsxWriterHelper.SpreadsheetWriter sheetWriter =
                new XlsxWriterHelper.SpreadsheetWriter(writer);
        sheetWriter.setCharset(charset);
        sheetWriter.beginSheet();
        if (isFirstRowAsColumnName && columnTitles != null) {
            sheetWriter.insertRow(xssfRowNum);
            int styleIndex =
                    ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("header")).getIndex();
            for (int index = 0; index < columnTitles.size(); index++) {
                sheetWriter.createCell(index, columnTitles.get(index), styleIndex);
            }
            sheetWriter.endRow();
        }

        return sheetWriter;
    }

    /**
     * Export data as XLS file format
     *
     * @param rs CUBRIDResultSetProxy
     * @param monitor IProgressMonitor
     */
    private void exportXls(
            CUBRIDResultSetProxy rs,
            final IProgressMonitor monitor) { // FIXME move this logic to core module
        WritableWorkbook workbook = null;
        try {
            int sheetNum = 0;
            workbook = Workbook.createWorkbook(file);

            if (fileCharset == null || fileCharset.trim().length() == 0) {
                workbook = Workbook.createWorkbook(file);
            } else {
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setEncoding(fileCharset);
                workbook = Workbook.createWorkbook(file, workbookSettings);
            }

            WritableSheet sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
            int rowLimit = ImportFileConstants.XLS_ROW_LIMIT; // 65536: limit xls row number.
            // it set 257. Because Tbl's first column is oid value that doesn't export
            int columnLimit =
                    ImportFileConstants.XLS_COLUMN_LIMIT + 1; // 256: limit xls column number.
            int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;

            CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
            int colCount = rsmt.getColumnCount() + 1;
            isExit = false;
            for (int j = 1; j < colCount; j++) {
                String columnName = rsmt.getColumnName(j);
                String columnType = rsmt.getColumnTypeName(j);
                int precision = rsmt.getPrecision(j);
                columnType = columnType == null ? "" : columnType;
                setIsHasBigValue(columnType, precision);
                // the data length > XLS column character limit
                if (precision > cellCharacterLimit) {
                    final String confirmMSG =
                            Messages.bind(Messages.exportCharacterCountExceedWarnInfo, columnName);

                    Display.getDefault()
                            .syncExec(
                                    new Runnable() {
                                        public void run() {
                                            if (!CommonUITool.openConfirmBox(confirmMSG)) {
                                                isExit = true;
                                            }
                                        }
                                    });
                    if (isExit) {
                        return;
                    }
                }
                // first line add column name
                if (isFirstRowAsColumnName) {
                    WritableFont wf =
                            new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
                    WritableCellFormat wcf = new WritableCellFormat(wf);
                    if (columnName.length() > cellCharacterLimit) {
                        Label label =
                                new Label(j - 1, 0, columnName.substring(0, cellCharacterLimit));
                        sheet.addCell(label);
                        label = null;
                    } else {
                        Label label = new Label(j - 1, 0, columnName.toString(), wcf);
                        sheet.addCell(label);
                        label = null;
                    }
                    wcf = null;
                    wf = null;
                }
            }

            if (colCount > columnLimit) {
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        if (!CommonUITool.openConfirmBox(
                                                Messages.exportColumnCountOverWarnInfo)) {
                                            isExit = true;
                                        }
                                    }
                                });
                if (isExit) {
                    return;
                }
                colCount = columnLimit;
            }
            int xlsRecordNum = 0;
            if (isFirstRowAsColumnName) {
                xlsRecordNum = 1;
            }
            while (rs.next()) {
                if (!CommonUITool.isAvailableMemory(REMAINING_MEMORY_SIZE)) {
                    throw new OutOfMemoryError();
                }
                for (int j = 1; j < colCount; j++) {
                    String colType = rsmt.getColumnTypeName(j);
                    colType = FieldHandlerUtils.amendDataTypeByResult(rs, j, colType);
                    int precision = rsmt.getPrecision(j);
                    setIsHasBigValue(colType, precision);
                    Object cellValue =
                            FieldHandlerUtils.getRsValueForExport(colType, rs, j, nullValue);
                    if (cellValue instanceof Long) {
                        sheet.addCell(new Number(j - 1, xlsRecordNum, (Long) cellValue));
                    } else if (cellValue instanceof Double) {
                        sheet.addCell(new Number(j - 1, xlsRecordNum, (Double) cellValue));
                    } else if (cellValue instanceof Timestamp) {
                        String dataCellValue =
                                FieldHandlerUtils.formatDateTime((Timestamp) cellValue);
                        sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
                    } else if (cellValue instanceof java.sql.Time) {
                        String dataCellValue =
                                DateUtil.getDatetimeString(
                                        ((java.sql.Time) cellValue).getTime(),
                                        FieldHandlerUtils.FORMAT_TIME);
                        sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
                    } else if (cellValue instanceof java.sql.Date) {
                        String dataCellValue =
                                DateUtil.getDatetimeString(
                                        ((java.sql.Date) cellValue).getTime(),
                                        FieldHandlerUtils.FORMAT_DATE);
                        sheet.addCell(new Label(j - 1, xlsRecordNum, dataCellValue));
                    } else {
                        sheet.addCell(
                                new Label(
                                        j - 1,
                                        xlsRecordNum,
                                        cellValue.toString().length() > cellCharacterLimit
                                                ? cellValue
                                                        .toString()
                                                        .substring(0, cellCharacterLimit)
                                                : cellValue.toString()));
                    }
                }
                xlsRecordNum++;
                if (((exportedCount + 1) % rowLimit) == 0) {
                    sheetNum++;
                    xlsRecordNum -= rowLimit;
                    sheet = workbook.createSheet("Sheet " + sheetNum, sheetNum);
                }
                exportedCount++;
                monitor.worked(10);
                monitor.subTask(Messages.bind(Messages.msgExportDataRow, exportedCount));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.write();
                }
            } catch (Exception ignored) {
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Get Buffered Writer
     *
     * @return BufferedWriter
     * @throws UnsupportedEncodingException if failed
     * @throws FileNotFoundException if failed
     */
    private BufferedWriter getBufferedWriter()
            throws UnsupportedEncodingException,
                    FileNotFoundException { // FIXME move this logic to core module
        BufferedWriter fs = null;
        if (fileCharset != null && fileCharset.trim().length() > 0) {
            fs =
                    new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(file), fileCharset.trim()));
        } else {
            fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        }
        return fs;
    }

    /**
     * Export data as TXT file format
     *
     * @param rs CUBRIDResultSetProxy
     * @param monitor IProgressMonitor
     * @throws SQLException The exception
     * @throws IOException The exception
     */
    private void exportTxt(
            CUBRIDResultSetProxy rs,
            final IProgressMonitor monitor,
            String seprator,
            String surround)
            throws SQLException, IOException { // FIXME move this logic to core module
        BufferedWriter fs = null;
        try {
            fs = getBufferedWriter();
            CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
            // first line add column name
            if (isFirstRowAsColumnName) {
                for (int j = 1; j < rsmt.getColumnCount() + 1; j++) {
                    fs.write(surround + rsmt.getColumnName(j) + surround);
                    if (j != rsmt.getColumnCount()) {
                        fs.write(seprator);
                    }
                }
                fs.write('\n');
                fs.flush();
            }
            while (rs.next()) {
                writeNextLine(fs, rs, rsmt, seprator, surround);
                exportedCount++;
                if (exportedCount % COMMIT_LINES == 0) {
                    fs.flush();
                }
                monitor.worked(10);
                monitor.subTask(Messages.bind(Messages.msgExportDataRow, exportedCount));
            }
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                LOGGER.error("", e);
            }
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
    private void writeNextLine(
            BufferedWriter fs,
            CUBRIDResultSetProxy rs,
            CUBRIDResultSetMetaDataProxy rsmt,
            String seprator,
            String surround)
            throws SQLException, IOException { // FIXME move this logic to core module
        int length = rsmt.getColumnCount() + 1;
        for (int j = 1; j < length; j++) {
            String columnType = rsmt.getColumnTypeName(j);
            int precision = rsmt.getPrecision(j);
            columnType = FieldHandlerUtils.amendDataTypeByResult(rs, j, columnType);
            setIsHasBigValue(columnType, precision);
            Object value = FieldHandlerUtils.getRsValueForExport(columnType, rs, j, nullValue);
            fs.write(surround);
            fs.write(value.toString().replaceAll("\"", "\"\""));
            fs.write(surround);
            if (j != length - 1) {
                fs.write(seprator);
            }
        }
        fs.write('\n');
        fs.flush();
    }

    /**
     * Export data as SQL
     *
     * @param rs CUBRIDResultSetProxy
     * @param tableName String
     * @param monitor IProgressMonitor
     * @throws NumberFormatException The exception
     * @throws ParseException The exception
     * @throws SQLException The exception
     * @throws IOException The exception
     */
    private void exportSql(
            CUBRIDResultSetProxy rs, String tableName, final IProgressMonitor monitor)
            throws NumberFormatException, ParseException, SQLException,
                    IOException { // FIXME move this logic to core module
        BufferedWriter fs = null;
        try {
            fs = getBufferedWriter();
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
                    values.append(
                            FieldHandlerUtils.getRsValueForExportSQL(columnType, rs, j).toString());
                }
                values.append(");\n");
                fs.write(insert.toString());
                fs.write(values.toString());
                exportedCount++;
                if (exportedCount % COMMIT_LINES == 0) {
                    fs.flush();
                }
                monitor.worked(10);
                monitor.subTask(Messages.bind(Messages.msgExportDataRow, exportedCount));
            }
        } finally {
            FileUtil.close(fs);
        }
    }

    /**
     * set IsHasBigValue
     *
     * @param columnType String
     * @param precision int
     */
    private void setIsHasBigValue(String columnType, int precision) {
        if (!isHasBigValue) {
            isHasBigValue = FieldHandlerUtils.isBitValue(columnType, precision);
        }
    }

    /**
     * Export data as CUBRID load format
     *
     * @param rs CUBRIDResultSetProxy
     * @param tableName String
     * @param monitor IProgressMonitor
     * @throws SQLException The exception
     * @throws IOException The exception
     */
    private void exportLoad(
            CUBRIDResultSetProxy rs, String tableName, final IProgressMonitor monitor)
            throws SQLException, IOException {
        // FIXME move this logic to core module
        BufferedWriter fs = null;
        try {
            fs = getBufferedWriter();
            CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();

            // loaddb header
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

            // loaddb body
            while (rs.next()) {
                StringBuffer values = new StringBuffer();
                values.append(++exportedCount);
                values.append(":");
                for (int j = 1; j < rsmt.getColumnCount() + 1; j++) {
                    String columnType = rsmt.getColumnTypeName(j);
                    int precision = rsmt.getPrecision(j);
                    columnType = FieldHandlerUtils.amendDataTypeByResult(rs, j, columnType);
                    setIsHasBigValue(columnType, precision);
                    values.append(
                            FieldHandlerUtils.getRsValueForExportOBS(columnType, rs, j).toString());
                }
                values.append("\n");

                fs.write(values.toString());

                if (exportedCount % COMMIT_LINES == 0) {
                    fs.flush();
                }
                monitor.worked(10);
                monitor.subTask(Messages.bind(Messages.msgExportDataRow, exportedCount));
            }
        } finally {
            FileUtil.close(fs);
        }
    }

    public void cancel() {
        isCancel = true;
        try {
            if (stmt != null) {
                stmt.cancel();
            }
        } catch (Exception ignored) {
        } finally {
            finish();
        }
    }

    public void finish() {
        QueryUtil.freeQuery(conn, stmt, rs);
    }

    public boolean isCancel() {
        return isCancel;
    }

    public boolean isSuccess() {
        return errorMsg == null && isDone;
    }

    /**
     * Get result message
     *
     * @return String
     */
    public String getResultMsg() {
        String resultMsg = "";
        if (errorMsg != null) {
            resultMsg = Messages.bind(Messages.errExportTableData, exportedTableName);
            resultMsg += " " + errorMsg;
            return resultMsg;
        }

        resultMsg += Messages.bind(Messages.msgExportTableData, exportedCount, exportedTableName);
        String attachedMsg = "";
        if (isSuccess() && isHasBigValue) {
            String tipMsgBigValue =
                    Messages.bind(
                            Messages.msgTipExportBigValue,
                            FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH);
            attachedMsg += tipMsgBigValue;
        }

        return resultMsg + attachedMsg;
    }
}
