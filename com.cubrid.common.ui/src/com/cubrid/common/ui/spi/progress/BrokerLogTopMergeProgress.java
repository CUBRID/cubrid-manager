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
package com.cubrid.common.ui.spi.progress;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * parse BrokerLogTop and write to excel
 *
 * @author fulei
 * @version 1.0 - 2012-6-15 created by fulei
 */
public class BrokerLogTopMergeProgress implements IRunnableWithProgress {
    /*
    Limitation of Excel
    -------------------
    Version 	max rows 	max cols 	max total cells		max cell characters
    Excel 2003 	65,536   	256   		16,777,216
    Excel 2007 	1,048,576   16,384   	17,179,869,184 		32767
    */
    private static final Logger LOGGER = LogUtil.getLogger(BrokerLogTopMergeProgress.class);
    private boolean success = false;
    private final String qFilePath;
    private final String resFilePath;
    private final String xlsFilePath;
    private int partionLineCount = 3000;
    private int excelCelllength = 32700;
    private String charset = "UTF-8";
    private boolean end = false;

    public BrokerLogTopMergeProgress(
            String qFilePath,
            String resFilePath,
            String xlsFilePath,
            int partionLineCount,
            String charset) {
        this.qFilePath = qFilePath;
        this.resFilePath = resFilePath;
        this.xlsFilePath = xlsFilePath;
        this.partionLineCount = partionLineCount;
        this.charset = charset;
    }

    public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
        try {
            ArrayList<ArrayList<String>> resList = loadResFile();
            processByPartition(resList);
            openSuccessDialog(Messages.brokerLogTopMergeProgressSuccess);
            success = true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            openErrorDialog(e.getMessage());
        }
    }

    /**
     * merge and write excel by line count partion
     *
     * @param resList
     * @throws Exception
     */
    public void processByPartition(ArrayList<ArrayList<String>> resList) throws Exception {
        BufferedReader reader = null;
        try {
            reader =
                    new BufferedReader(
                            new InputStreamReader(new FileInputStream(qFilePath), charset));
            boolean fisrtFile = true;
            while (!end) {
                LinkedHashMap<String, StringBuilder> brokerLogMap = loadBrokerLogFile(reader);
                LinkedHashMap<String, String> indexSQLMap = parseLogToSQL(brokerLogMap);
                ArrayList<ArrayList<String>> partitionList = merge(resList, indexSQLMap);
                String fileName = getExcelFileName(partitionList, end && fisrtFile);
                writeExcelPartitionByfile(partitionList, new File(fileName));
                fisrtFile = false;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * parse excel name
     *
     * @param partitionList
     * @param singleFile
     * @return
     */
    public String getExcelFileName(ArrayList<ArrayList<String>> partitionList, boolean singleFile) {
        String fileName = xlsFilePath;
        if (singleFile) {
            return fileName;
        }
        String[] fileNames = fileName.split("\\.");
        String xlsFilePrefix = fileNames[0];
        String xlsFileSuffix = fileNames[fileNames.length - 1];
        ArrayList<String> beginIndex = partitionList.get(0);
        ArrayList<String> endIndex = partitionList.get(partitionList.size() - 1);

        StringBuilder sb = new StringBuilder();
        sb.append(xlsFilePrefix)
                .append("(")
                .append(beginIndex.get(0))
                .append("_")
                .append(endIndex.get(0))
                .append(")")
                .append(".")
                .append(xlsFileSuffix);

        return sb.toString();
    }

    /**
     * createDatabaseWithProgress return database name
     *
     * @return Catalog
     */
    public boolean merge() {

        Display display = Display.getDefault();
        display.syncExec(
                new Runnable() {
                    public void run() {
                        try {
                            new ProgressMonitorDialog(null)
                                    .run(true, false, BrokerLogTopMergeProgress.this);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                });

        return success;
    }

    /** load .q broker file; */
    private LinkedHashMap<String, StringBuilder> loadBrokerLogFile(BufferedReader reader)
            throws Exception {
        LinkedHashMap<String, StringBuilder> bokerLogMap =
                new LinkedHashMap<String, StringBuilder>();
        String tempString = null;
        String regex = "\\[Q\\d+\\]-+";
        Pattern pattern = Pattern.compile(regex);
        String indexWithSquareBracketsRegex = "\\[Q\\d+\\]";
        Pattern indexSquareBracketsPattern = Pattern.compile(indexWithSquareBracketsRegex);
        String indexStringRegex = "Q\\d+";
        Pattern indexStringPattern = Pattern.compile(indexStringRegex);
        StringBuilder oneLog = null;
        while ((tempString = reader.readLine()) != null) {
            tempString = tempString.trim();
            // if read end a part and readLine bigger than one partion number break
            if (tempString.equals("")) {
                if (bokerLogMap.size() + 1 > partionLineCount) {
                    return bokerLogMap;
                } else {
                    continue;
                }
            }
            Matcher matcher = pattern.matcher(tempString);
            if (matcher.find()) {
                String indexLine = matcher.group(0);
                Matcher indexWithSquareBracketsMatcher =
                        indexSquareBracketsPattern.matcher(indexLine);
                if (indexWithSquareBracketsMatcher.find()) {
                    String indexWithSquareBrackets = indexWithSquareBracketsMatcher.group(0);
                    Matcher indexStringMatcher =
                            indexStringPattern.matcher(indexWithSquareBrackets);
                    String index = indexWithSquareBrackets;
                    if (indexStringMatcher.find()) {
                        index = indexStringMatcher.group(0);
                    }
                    oneLog = bokerLogMap.get(index);
                    if (oneLog == null) {
                        oneLog = new StringBuilder();
                        bokerLogMap.put(index, oneLog);
                    }
                }
            } else {
                oneLog.append(tempString).append(System.getProperty("line.separator"));
            }
        }
        end = true;
        return bokerLogMap;
    }

    /**
     * parse log to sql
     *
     * @param bokerLogMap
     * @return
     */
    private LinkedHashMap<String, String> parseLogToSQL(
            LinkedHashMap<String, StringBuilder> bokerLogMap) throws Exception {
        LinkedHashMap<String, String> indexSQLMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, StringBuilder> entry : bokerLogMap.entrySet()) {
            String sql = CommonUITool.parseBrokerLogToSQL(entry.getValue().toString());
            indexSQLMap.put(entry.getKey(), sql);
        }
        return indexSQLMap;
    }

    /**
     * load res file
     *
     * @param fileName
     * @return
     */
    private ArrayList<ArrayList<String>> loadResFile() throws Exception {
        BufferedReader reader = null;
        ArrayList<ArrayList<String>> resList = new ArrayList<ArrayList<String>>();
        try {
            reader =
                    new BufferedReader(
                            new InputStreamReader(new FileInputStream(resFilePath), charset));
            String tempString = null;
            String maxMinAvgRegex = "\\d+\\.\\d+";
            Pattern maxMinAvgPattern = Pattern.compile(maxMinAvgRegex);
            String indexWithSquareBracketsRegex = "\\[Q\\d+\\]";
            Pattern indexSquareBracketsPattern = Pattern.compile(indexWithSquareBracketsRegex);
            String indexStringRegex = "Q\\d+";
            Pattern indexStringPattern = Pattern.compile(indexStringRegex);
            String cntRegex = "\\d+\\s\\(\\d+\\)";
            Pattern cntPattern = Pattern.compile(cntRegex);
            String cntErrNumberRegex = "\\d+";
            Pattern cntErrNumberPattern = Pattern.compile(cntErrNumberRegex);

            while ((tempString = reader.readLine()) != null) {
                tempString = tempString.trim();
                ArrayList<String> line = new ArrayList<String>();
                Matcher indexSquareBracketsMatcher = indexSquareBracketsPattern.matcher(tempString);
                Matcher maxMinAvgMatcher = maxMinAvgPattern.matcher(tempString);
                Matcher cntMatcher = cntPattern.matcher(tempString);
                if (indexSquareBracketsMatcher.find()) {
                    String indexWithSquareBrackets = indexSquareBracketsMatcher.group(0);
                    Matcher indexStringMatcher =
                            indexStringPattern.matcher(indexWithSquareBrackets);
                    if (indexStringMatcher.find()) {
                        line.add(indexStringMatcher.group(0));
                    }
                } else {
                    continue;
                }
                while (maxMinAvgMatcher.find()) {
                    line.add(maxMinAvgMatcher.group(0));
                }
                if (cntMatcher.find()) {
                    String cntErr = cntMatcher.group(0);
                    Matcher cntErrNumberMatcher = cntErrNumberPattern.matcher(cntErr);
                    while (cntErrNumberMatcher.find()) {
                        line.add(cntErrNumberMatcher.group(0));
                    }
                }
                resList.add(line);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return resList;
    }

    /**
     * writeExcelPartitionByfile
     *
     * @param mergeString
     * @param xlsFile
     * @throws Exception
     */
    public void writeExcelPartitionByfile(ArrayList<ArrayList<String>> mergeString, File xlsFile)
            throws Exception { // FIXME move this logic to core module
        WritableWorkbook wwb = null;
        WritableSheet ws = null;
        String sheetName = "log_top_merge";

        try {
            WritableCellFormat normalCellStyle = getNormalCell();
            WritableCellFormat sqlCellStyle = getSQLCell();
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding(charset);
            wwb = Workbook.createWorkbook(xlsFile, workbookSettings);
            ws = wwb.createSheet(sheetName, 0);
            ws.setColumnView(7, 200);
            ws.addCell(new jxl.write.Label(0, 0, "NUM", normalCellStyle));
            ws.addCell(new jxl.write.Label(1, 0, "ID", normalCellStyle));
            ws.addCell(new jxl.write.Label(2, 0, "MAX (sec)", normalCellStyle));
            ws.addCell(new jxl.write.Label(3, 0, "MIN (sec)", normalCellStyle));
            ws.addCell(new jxl.write.Label(4, 0, "AVG (sec)", normalCellStyle));
            ws.addCell(new jxl.write.Label(5, 0, "Counts", normalCellStyle));
            ws.addCell(new jxl.write.Label(6, 0, "Errors", normalCellStyle));
            ws.addCell(new jxl.write.Label(7, 0, "SQL contents", normalCellStyle));

            jxl.write.Label label = null;
            jxl.write.Number num = null;
            for (int i = 0; i < mergeString.size(); i++) {
                List<String> oneLine = mergeString.get(i);
                int row = i + 1;
                for (int j = 0; j < 8; j++) {
                    if (j == 0) {
                        String numString = oneLine.get(0) == null ? "" : oneLine.get(0);
                        num =
                                new jxl.write.Number(
                                        j,
                                        row,
                                        Integer.valueOf(numString.replaceAll("Q", "")),
                                        normalCellStyle);
                        ws.addCell(num);
                    } else if (j == 1) {
                        String comment = "";
                        String sql = oneLine.get(6) == null ? "" : oneLine.get(6).trim();
                        if (sql.startsWith("/*")) {
                            int endIndexOfComment = sql.indexOf("*/");
                            if (endIndexOfComment != -1) {
                                comment = sql.substring(2, endIndexOfComment).trim();
                            }
                        }
                        label = new jxl.write.Label(j, row, comment, sqlCellStyle);
                        ws.addCell(label);
                    } else if (j > 1 && j < 7) {
                        num =
                                new jxl.write.Number(
                                        j, row, Float.valueOf(oneLine.get(j - 1)), normalCellStyle);
                        ws.addCell(num);
                    } else {
                        String s = oneLine.get(6);
                        if (s.length() > excelCelllength) {
                            s = s.substring(0, excelCelllength - 3);
                            s += "...";
                        }
                        label = new jxl.write.Label(j, row, s, sqlCellStyle);
                        ws.addCell(label);
                    }
                }
            }
            wwb.write();
        } catch (Exception e) {
            LOGGER.error("write excel error", e);
            throw e;
        } finally {
            if (wwb != null) {
                try {
                    wwb.close();
                } catch (Exception ex) {
                    LOGGER.error("close excel stream error", ex);
                }
            }
        }
    }

    /**
     * getNormalCell
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getNormalCell() { // FIXME move this logic to core module
        WritableFont font = new WritableFont(WritableFont.TIMES, 11);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return format;
    }

    /**
     * getSQLCell
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getSQLCell() { // FIXME move this logic to core module
        WritableFont font = new WritableFont(WritableFont.TIMES, 11);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.LEFT);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return format;
    }

    /**
     * merge sql and res list add it to write list ,and delete the wirte list from res list
     *
     * @param resList
     * @param indexSQLMap
     * @return
     */
    public ArrayList<ArrayList<String>> merge(
            ArrayList<ArrayList<String>> resList, LinkedHashMap<String, String> indexSQLMap) {
        ArrayList<ArrayList<String>> writeList = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> oneLine : resList) {
            String index = oneLine.get(0);
            String sql = indexSQLMap.get(index);
            if (sql != null) {
                // if get sql, and it to write list
                oneLine.add(sql);
                writeList.add(oneLine);
            }
        }

        // remove write list from rest lsit
        for (ArrayList<String> deleteFromRestList : writeList) {
            resList.remove(deleteFromRestList);
        }
        return writeList;
    }

    /**
     * show error message to users
     *
     * @param showMess String
     */
    private void openErrorDialog(final String showMess) {
        Display display = Display.getDefault();
        display.asyncExec(
                new Runnable() {
                    public void run() {
                        CommonUITool.openErrorBox(
                                PlatformUI.getWorkbench().getDisplay().getActiveShell(), showMess);
                    }
                });
    }

    /**
     * show success message to users
     *
     * @param showMess String
     */
    private void openSuccessDialog(final String showMess) {
        Display display = Display.getDefault();
        display.asyncExec(
                new Runnable() {
                    public void run() {
                        CommonUITool.openInformationBox(
                                PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                Messages.titleConfirm,
                                showMess);
                    }
                });
    }

    public boolean isSuccess() {
        return success;
    }
}
