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

package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.control.XlsxWriterHelper;
import com.cubrid.common.ui.cubrid.table.dialog.ExportTableDataTask;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileConstants;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;

/**
 * Export data to xls or csv format file
 *
 * @author wangsl
 * @version 1.0 - 2009-6-4 created by wangsl
 */
public class Export {

    private static final Logger LOGGER = LogUtil.getLogger(Export.class);

    private final File file;
    private final List<List<ColumnInfo>> resultColsList;
    private final List<List<Map<String, String>>> resultDataList;
    private boolean isCancel;
    private WritableWorkbook workbook;
    private BufferedWriter csvWriter;
    private final String fileCharset;
    private long exportedCount = 0;

    /**
     * The constructor
     *
     * @param file
     * @param fileCharset
     * @param tbl
     * @param hasOid
     */
    public Export(File file, String fileCharset, Table tbl, TableItem[] items, boolean hasOid) {
        resultColsList = new ArrayList<List<ColumnInfo>>();
        resultDataList = new ArrayList<List<Map<String, String>>>();
        this.file = file;
        this.fileCharset = fileCharset;
        int colCount = tbl.getColumnCount();
        int itemCount = items.length;

        int start = 1;
        if (hasOid) {
            start++;
        }
        List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        for (int j = start; j < colCount; j++) {
            ColumnInfo columnInfo = (ColumnInfo) tbl.getColumns()[j].getData();
            columnList.add(columnInfo);
        }
        for (int i = 0; i < itemCount; i++) {
            Map<String, String> value = new HashMap<String, String>();
            for (int j = start; j < colCount; j++) {
                ColumnInfo columnInfo = (ColumnInfo) tbl.getColumns()[j].getData();
                if (DataType.VALUE_NULL.equals(items[i].getData(j + ""))) {
                    value.put(columnInfo.getIndex(), null);
                } else {
                    value.put(columnInfo.getIndex(), items[i].getText(j));
                }
            }
            dataList.add(value);
        }
        resultColsList.add(columnList);
        resultDataList.add(dataList);
    }

    /**
     * The constructor
     *
     * @param file
     * @param fileCharset
     * @param columnInfoList
     * @param exportedDataList
     * @param hasOid
     */
    public Export(
            File file,
            String fileCharset,
            List<ColumnInfo> columnInfoList,
            List<Map<String, CellValue>> exportedDataList,
            boolean hasOid) {
        resultColsList = new ArrayList<List<ColumnInfo>>();
        resultDataList = new ArrayList<List<Map<String, String>>>();
        this.file = file;
        this.fileCharset = fileCharset;
        init(columnInfoList, exportedDataList, hasOid);
    }

    /**
     * Initial the exported column information and data information
     *
     * @param columnInfoList List<ColumnInfo>
     * @param exportedDataList List<Map<String, Object>>
     * @param hasOid boolean
     */
    private void init(
            List<ColumnInfo> columnInfoList,
            List<Map<String, CellValue>> exportedDataList,
            boolean hasOid) {
        if (columnInfoList == null || columnInfoList.isEmpty()) {
            return;
        }
        int colCount = columnInfoList.size();
        int itemCount = exportedDataList.size();
        int start = 0;
        if (hasOid) {
            start++;
        }
        List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        for (int j = start; j < colCount; j++) {
            ColumnInfo columnInfo = columnInfoList.get(j);
            columnList.add(columnInfo);
        }
        for (int i = 0; i < itemCount; i++) {
            Map<String, String> value = new HashMap<String, String>();
            for (int j = start; j < colCount; j++) {
                ColumnInfo columnInfo = columnInfoList.get(j);
                String type = columnInfo.getType();
                CellValue dataValue = exportedDataList.get(i).get((hasOid ? j : j + 1) + "");
                value.put(
                        columnInfo.getIndex(),
                        FieldHandlerUtils.getValueForExport(type, dataValue.getShowValue()));
            }
            dataList.add(value);
        }
        resultColsList.add(columnList);
        resultDataList.add(dataList);
    }

    /**
     * The constructor
     *
     * @param file
     * @param fileCharset
     * @param queryResultVector
     */
    public Export(File file, String fileCharset, Vector<QueryExecuter> queryResultVector) {
        resultColsList = new ArrayList<List<ColumnInfo>>();
        resultDataList = new ArrayList<List<Map<String, String>>>();
        this.file = file;
        this.fileCharset = fileCharset;
        for (QueryExecuter qe : queryResultVector) {
            init(qe.getAllColumnList(), qe.getAllDataList(), false);
        }
    }

    /**
     * Export data as specified file type
     *
     * @param monitor IProgressMonitor
     * @throws FileNotFoundException if failed
     * @throws UnsupportedEncodingException if failed
     * @throws IOException if failed
     * @throws RowsExceededException if failed
     * @throws NumberFormatException if failed
     * @throws WriteException if failed
     */
    public void export(final IProgressMonitor monitor)
            throws FileNotFoundException, UnsupportedEncodingException, IOException,
                    RowsExceededException, NumberFormatException, WriteException {
        if (file == null) {
            cancel();
            return;
        }
        try {
            if (file.getName().toLowerCase(Locale.getDefault()).endsWith(".xlsx")) {
                exportXlsx(monitor);
            } else if (file.getName().toLowerCase(Locale.getDefault()).endsWith(".xls")) {
                exportXls(monitor);
            } else {
                exportCsv(monitor);
            }
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * export all data in Query Editor result table cache as xlsx
     *
     * @param monitor IProgressMonitor
     * @throws IOException if failed
     */
    private void exportXlsx(final IProgressMonitor monitor)
            throws IOException { // FIXME move this logic to core module

        final int rowLimit =
                ImportFileConstants.XLSX_ROW_LIMIT
                        - 1; // 1048576: limit xlsx row number except for column row.
        final int columnLimit =
                ImportFileConstants.XLSX_COLUMN_LIMIT; // 16384: limit xlsx column number.
        final int cellCharacterLimit = ImportFileConstants.XLSX_CELL_CHAR_LIMIT;

        //		//create dateformat
        //		SimpleDateFormat datetimeSdf = new SimpleDateFormat(
        //				"yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        //		SimpleDateFormat timestampSdf = new SimpleDateFormat(
        //				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd",
        //				Locale.getDefault());
        //		SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss",
        //				Locale.getDefault());
        //		Date date = null;

        // create memory workbook
        XlsxWriterHelper xlsxWriterhelper = new XlsxWriterHelper();
        XSSFWorkbook workbook = new XSSFWorkbook();

        //		Calendar cal = Calendar.getInstance();
        //		int datetimeStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(
        //				workbook).get("datetime")).getIndex();
        //		int timestampStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(
        //				workbook).get("timestamp")).getIndex();
        //		int dateStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(
        //				workbook).get("date")).getIndex();
        //		int timeStyleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(
        //				workbook).get("time")).getIndex();

        Map<String, File> fileMap = new HashMap<String, File>();
        try {
            for (int k = 0; k < resultDataList.size(); k++) {
                List<ColumnInfo> columnList = resultColsList.get(k);
                List<Map<String, String>> dataList = resultDataList.get(k);

                int colCount = columnList.size();
                int itemCount = dataList.size();
                if (colCount > columnLimit) {
                    if (!CommonUITool.openConfirmBox(Messages.columnCountOver)) {
                        return;
                    }
                    colCount = columnLimit;
                }
                XlsxWriterHelper.SpreadsheetWriter sheetWriter = null;
                try {
                    sheetWriter = createSheetWriter(workbook, "Sheet " + (k + 1), fileMap);

                    // export columns
                    exportColumnsForXLSX(sheetWriter, k, columnLimit);

                    int sheetNum = 0;
                    for (int i = 0, xssfRowNum = 1; i < itemCount; i++) {
                        sheetWriter.insertRow(xssfRowNum);
                        for (int j = 0; j < colCount; j++) {
                            String colType = columnList.get(j).getType();
                            String colIndex = columnList.get(j).getIndex();
                            String cellValue = dataList.get(i).get(colIndex);
                            int cellType = FieldHandlerUtils.getCellType(colType, cellValue);
                            switch (cellType) {
                                case -1:
                                    sheetWriter.createCell(j, DataType.NULL_EXPORT_FORMAT);
                                    break;
                                case 0:
                                    sheetWriter.createCell(j, Long.parseLong(cellValue));
                                    break;
                                case 1:
                                    sheetWriter.createCell(j, Double.parseDouble(cellValue));
                                    break;
                                    // TOOLS-954
                                    // if using datetime type with miliseconds, xlsx is not
                                    // supported datetime type.
                                    // therefore, we should be treated it as text.
                                    //							case 2:
                                    //								try {
                                    //									date = datetimeSdf.parse(cellValue);
                                    //								} catch (ParseException ex) {
                                    //									LOGGER.error(ex.getMessage());
                                    //								}
                                    //								cal.setTime(date);
                                    //								sheetWriter.createCell(j, cal,
                                    //										datetimeStyleIndex);
                                    //								break;
                                    //							case 3:
                                    //								try {
                                    //									date = timestampSdf.parse(cellValue);
                                    //								} catch (ParseException ex) {
                                    //									LOGGER.error(ex.getMessage());
                                    //								}
                                    //								cal.setTime(date);
                                    //								sheetWriter.createCell(j, cal,
                                    //										timestampStyleIndex);
                                    //								break;
                                    //							case 4:
                                    //								try {
                                    //									date = dateSdf.parse(cellValue);
                                    //								} catch (ParseException ex) {
                                    //									LOGGER.error(ex.getMessage());
                                    //								}
                                    //								cal.setTime(date);
                                    //								sheetWriter.createCell(j, cal, dateStyleIndex);
                                    //								break;
                                    //							case 5:
                                    //								try {
                                    //									date = timeSdf.parse(cellValue);
                                    //								} catch (ParseException ex) {
                                    //									LOGGER.error(ex.getMessage());
                                    //								}
                                    //								cal.setTime(date);
                                    //								sheetWriter.createCell(j, cal, timeStyleIndex);
                                    //								break;
                                case 2:
                                default:
                                    String cellStr =
                                            cellValue.toString().length() > cellCharacterLimit
                                                    ? cellValue
                                                            .toString()
                                                            .substring(0, cellCharacterLimit)
                                                    : cellValue.toString();
                                    sheetWriter.createCell(j, covertXMLString(cellStr));
                                    break;
                            }
                        }
                        sheetWriter.endRow();
                        xssfRowNum++;
                        if (((i + 1) % rowLimit) == 0 && (i + 1) < itemCount) {
                            sheetNum++;
                            try {
                                XlsxWriterHelper.writeSheetWriter(sheetWriter);
                            } catch (IOException e) {
                                sheetWriter = null;
                                throw e;
                            }
                            sheetWriter =
                                    createSheetWriter(
                                            workbook, "Sheet " + (k + 1) + "_" + sheetNum, fileMap);
                            exportColumnsForXLSX(sheetWriter, k, columnLimit);
                            xssfRowNum = 1;
                        }
                        exportedCount++;
                        monitor.subTask(
                                Messages.bind(
                                        com.cubrid.common.ui.cubrid.table.Messages.msgExportDataRow,
                                        exportedCount));
                    }
                } finally {
                    try {
                        XlsxWriterHelper.writeSheetWriter(sheetWriter);
                    } catch (IOException e) {
                        sheetWriter = null;
                        throw e;
                    }
                }
            }
        } finally {
            XlsxWriterHelper.writeWorkbook(workbook, xlsxWriterhelper, fileMap, file);
        }
    }

    /**
     * Convert special xml string(<,>,&,',");
     *
     * @param str String
     * @return String
     */
    public static String covertXMLString(String str) { // FIXME move this logic to core module
        if (str == null) {
            return null;
        }
        return str.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("'", "&apos;")
                .replaceAll("\"", "&quot;");
    }

    /**
     * Export the column name for xlsx format
     *
     * @param sheetWriter SpreadsheetWriter
     * @param sheetNum int
     * @param columnLimit int
     * @throws IOException The exception
     */
    private void exportColumnsForXLSX(
            XlsxWriterHelper.SpreadsheetWriter sheetWriter, int sheetNum, int columnLimit)
            throws IOException { // FIXME move this logic to core module
        if (resultColsList == null) {
            return;
        }
        sheetWriter.insertRow(0);
        List<ColumnInfo> columnList = resultColsList.get(sheetNum);
        for (int i = 0; columnList != null && i < columnList.size() && i < columnLimit; i++) {
            String columnName = columnList.get(i).getName();
            sheetWriter.createCell(i, columnName);
        }
        sheetWriter.endRow();
    }

    /**
     * Create the instance of SpreadsheetWriter and based upon the given condition writing the
     * header of a sheet
     *
     * @param workbook the instance of Workbook
     * @param sheetName the name of a sheet
     * @param fileMap a map includes the temporary file and its name
     * @return the instance of XlsxWriterHelper.SpreadsheetWriter
     */
    private XlsxWriterHelper.SpreadsheetWriter createSheetWriter(
            XSSFWorkbook workbook,
            String sheetName,
            Map<String, File> fileMap) { // FIXME move this logic to core module
        XSSFSheet sheet = workbook.createSheet(sheetName);
        String sheetRef = sheet.getPackagePart().getPartName().getName().substring(1);
        File tmp = null;
        XlsxWriterHelper.SpreadsheetWriter sheetWriter = null;
        try {
            tmp = File.createTempFile(sheetName, ".xml");
            fileMap.put(sheetRef, tmp);
            String charset = null;
            if (fileCharset == null || fileCharset.trim().length() == 0) {
                charset = "UTF-8";
            } else {
                charset = fileCharset;
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmp), charset);
            sheetWriter = new XlsxWriterHelper.SpreadsheetWriter(writer);
            sheetWriter.setCharset(charset);
            sheetWriter.beginSheet();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
        return sheetWriter;
    }

    /**
     * export all data in Query Editor result table cache as xls
     *
     * @param monitor IProgressMonitor
     * @throws IOException if failed
     * @throws FileNotFoundException if failed
     * @throws UnsupportedEncodingException if failed
     * @throws RowsExceededException if failed
     * @throws NumberFormatException if failed
     * @throws WriteException if failed
     */
    private void exportXls(final IProgressMonitor monitor)
            throws IOException, FileNotFoundException, UnsupportedEncodingException,
                    RowsExceededException, NumberFormatException,
                    WriteException { // FIXME move this logic to core module
        workbook = null;
        try {
            if (fileCharset == null || fileCharset.trim().length() == 0) {
                workbook = Workbook.createWorkbook(file);
            } else {
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setEncoding(fileCharset);
                workbook = Workbook.createWorkbook(file, workbookSettings);
            }

            int totalSheetNum = 0;
            final int rowLimit =
                    ImportFileConstants.XLS_ROW_LIMIT
                            - 1; // 65536: limit xls row number except the column row
            final int columnLimit =
                    ImportFileConstants.XLS_COLUMN_LIMIT; // 256: limit xls column number..

            for (int k = 0; k < resultDataList.size(); k++) {
                List<ColumnInfo> columnList = resultColsList.get(k);
                List<Map<String, String>> dataList = resultDataList.get(k);
                WritableSheet sheet = workbook.createSheet("Sheet " + (k + 1), totalSheetNum++);

                int colCount = columnList.size();
                int itemCount = dataList.size();
                if (colCount > columnLimit) {
                    if (!CommonUITool.openConfirmBox(Messages.columnCountOver)) {
                        return;
                    }
                    colCount = columnLimit;
                }
                // export columns
                exportColumnsForXls(sheet, k, columnLimit);

                int sheetNum = 0;
                for (int i = 0, xlsRecordNum = 1; i < itemCount; i++) {
                    if (!CommonUITool.isAvailableMemory(
                            ExportTableDataTask.REMAINING_MEMORY_SIZE)) {
                        throw new OutOfMemoryError();
                    }
                    int start = 0;
                    for (int j = start; j < colCount; j++) {
                        String colType = columnList.get(j).getType();
                        String colIndex = columnList.get(j).getIndex();
                        String value = dataList.get(i).get(colIndex);
                        int colNumber = j - start;
                        FieldHandlerUtils.setValue2XlsCell(
                                sheet, colNumber, xlsRecordNum, colType, value);
                    }

                    xlsRecordNum++;
                    if (((i + 1) % rowLimit) == 0 && (i + 1) < itemCount) {
                        sheetNum++;
                        sheet =
                                workbook.createSheet(
                                        "Sheet " + (k + 1) + "_" + sheetNum, totalSheetNum++);
                        exportColumnsForXls(sheet, k, columnLimit);
                        xlsRecordNum = 1;
                    }
                    exportedCount++;
                    monitor.subTask(
                            Messages.bind(
                                    com.cubrid.common.ui.cubrid.table.Messages.msgExportDataRow,
                                    exportedCount));
                }
            }
        } finally {
            try {
                if (workbook != null) {
                    workbook.write();
                }
            } finally {
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
        }
    }

    /**
     * Export the column name for xls format
     *
     * @param sheet WritableSheet
     * @param sheetNum int
     * @param columnLimit int
     * @throws WriteException The exception
     * @throws RowsExceededException The exception
     */
    private void exportColumnsForXls(WritableSheet sheet, int sheetNum, int columnLimit)
            throws RowsExceededException, WriteException { // FIXME move this logic to core module
        if (resultColsList == null) {
            return;
        }
        List<ColumnInfo> columnList = resultColsList.get(sheetNum);
        for (int i = 0; columnList != null && i < columnList.size() && i < columnLimit; i++) {
            String columnName = columnList.get(i).getName();
            sheet.addCell(new Label(i, 0, columnName));
        }
    }

    /**
     * Export all data in Query Editor result table cache as csv
     *
     * @param monitor IProgressMonitor
     * @throws IOException if failed
     */
    private void exportCsv(final IProgressMonitor monitor)
            throws IOException { // FIXME move this logic to core module
        csvWriter = null;
        try {
            if (fileCharset != null && fileCharset.trim().length() > 0) {
                csvWriter =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(file), fileCharset.trim()));
            } else {
                csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            }
            for (int k = 0; k < resultDataList.size(); k++) {
                // export column name row
                exportColumnsForCsv(csvWriter, k);

                List<ColumnInfo> columnList = resultColsList.get(k);
                List<Map<String, String>> dataList = resultDataList.get(k);
                for (int i = 0; i < dataList.size(); i++) {
                    for (int j = 0; j < columnList.size(); j++) {
                        String colType = columnList.get(j).getType();
                        String colIndex = columnList.get(j).getIndex();
                        String data = dataList.get(i).get(colIndex);
                        csvWriter.write(FieldHandlerUtils.getData2WriteCSV(colType, data));
                        if (j != columnList.size() - 1) {
                            csvWriter.write(',');
                        }
                    }
                    csvWriter.write('\n');
                    if ((i + 1) % ExportTableDataTask.COMMIT_LINES == 0) {
                        csvWriter.flush();
                    }
                    exportedCount++;
                    monitor.subTask(
                            Messages.bind(
                                    com.cubrid.common.ui.cubrid.table.Messages.msgExportDataRow,
                                    exportedCount));
                }
            }
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Export the column name for csv format
     *
     * @param csvWriter BufferedWriter
     * @param sheetNum int
     * @throws IOException The exception
     */
    private void exportColumnsForCsv(BufferedWriter csvWriter, int sheetNum)
            throws IOException { // FIXME move this logic to core module
        if (resultColsList == null) {
            return;
        }
        List<ColumnInfo> columnList = resultColsList.get(sheetNum);
        for (int i = 0; columnList != null && i < columnList.size(); i++) {
            String columnName = columnList.get(i).getName();
            csvWriter.write(columnName);
            if (i != columnList.size() - 1) {
                csvWriter.write(',');
            }
        }
        csvWriter.write('\n');
    }

    /**
     * cancel action
     *
     * @throws WriteException if failed
     * @throws IOException if failed
     */
    public void cancel() throws WriteException, IOException {
        isCancel = true;
        if (workbook != null) {
            workbook.close();
        }
        if (csvWriter != null) {
            csvWriter.close();
        }
        if (file != null) {
            boolean isSucc = file.delete();
            if (!isSucc) {
                throw new IOException();
            }
        }
    }

    /**
     * Whether is canceled
     *
     * @return boolean
     */
    public boolean isCanceled() {
        return isCancel;
    }

    /**
     * Get the result messsage
     *
     * @param ex Exception
     * @return String
     */
    public String getResultMsg(Exception ex) {
        if (ex == null) {
            return Messages.bind(Messages.exportOk, exportedCount);
        }
        String resultMsg = "";
        if (ex instanceof RuntimeException && ex.getCause() instanceof OutOfMemoryError) {
            resultMsg = com.cubrid.common.ui.cubrid.table.Messages.errNoMemory;
        } else {
            resultMsg = ex.getMessage();
        }
        return resultMsg == null || resultMsg.trim().length() == 0 ? "Unknown error." : resultMsg;
    }
}
