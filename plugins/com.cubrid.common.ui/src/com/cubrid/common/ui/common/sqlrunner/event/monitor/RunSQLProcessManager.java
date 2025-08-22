/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.sqlrunner.event.monitor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.sqlrunner.event.BeginOneFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FailedEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FinishAllFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FinishOneFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.SuccessEvent;
import com.cubrid.common.ui.common.sqlrunner.event.handler.IRunSQLEventHandler;
import com.cubrid.common.ui.common.sqlrunner.event.handler.RunSQLEventHandler;
import com.cubrid.common.ui.common.sqlrunner.part.RunSQLFileViewPart.RunSQLMonitor;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.slf4j.Logger;

/**
 * this class is core class of run SQL function which is process the SQL running
 *
 * @author fulei
 */
public class RunSQLProcessManager implements IRunSQLProcessManager {
    private static final Logger LOGGER = LogUtil.getLogger(RunSQLProcessManager.class);
    private static final int FILE_FETCH_COUNT = 1000;
    private static final int DEFAULT_COMMIT_COUNT = 1000;

    private final IRunSQLEventHandler eventsHandler;
    private List<String> filesList;
    private final CubridDatabase database;
    private int totalSQLNumber = 0;
    private ThreadPoolExecutor executor = null;

    private int factor = 1;
    private ProgressBar pbTotal;
    private int pmLength = 0;

    /*The default value should be true*/
    private int maxThreadSize = 1;
    private int commitCount = 1000;
    private String charset = "UTF-8";

    private WritableWorkbook wwb = null;
    private HashMap<String, WritableSheet> excelSheetMap = new HashMap<String, WritableSheet>();
    private boolean isStop = false;

    public RunSQLProcessManager(
            List<String> filesList,
            RunSQLMonitor runSQLMonitor,
            CubridDatabase database,
            ProgressBar pbTotal,
            String charset,
            int commitCount,
            String logFolderPath,
            int maxThreadCount) {

        this.filesList = filesList;
        this.database = database;
        this.pbTotal = pbTotal;
        this.charset = charset;
        this.commitCount = commitCount;
        this.maxThreadSize = maxThreadCount;

        String filePath = logFolderPath + File.separator + "error_" + database.getName() + "@";
        DatabaseInfo dbInfo = database.getDatabaseInfo();
        if (dbInfo == null) {
            LOGGER.error("The database.getDatabaseInfo() is a null.");
            filePath += ".xls";
        } else {
            filePath += dbInfo.getBrokerIP() + ".xls";
        }

        File excelFile = new File(filePath);
        wwb = createExcel(excelFile);
        eventsHandler = new RunSQLEventHandler(runSQLMonitor, excelFile, wwb, excelSheetMap);

        executor =
                new ThreadPoolExecutor(
                        maxThreadSize,
                        maxThreadSize,
                        1,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>());
    }

    public void startProcess() {
        parsePmLength();

        for (int i = 0; i < filesList.size(); i++) {
            File file = new File(filesList.get(i));
            if (file.exists()) {
                if (wwb != null) {
                    excelSheetMap.put(file.getName(), createaWritableSheet(wwb, file.getName(), i));
                }
                executor.execute(new RunSQLFileThread(file, database.getDatabaseInfo()));
            }
        }
    }

    /**
     * get all file size and count pm length and return factor
     *
     * @return long
     */
    public long parsePmLength() {
        long totalSize = 0;

        for (int i = 0; i < filesList.size(); i++) {
            File file = new File(filesList.get(i));
            if (file.exists()) {
                totalSize += file.length();
            }
        }

        while ((totalSize / factor) > Integer.MAX_VALUE) {
            factor = factor * 1024;
        }

        pmLength = (int) (totalSize / factor);
        pbTotal.setMaximum(pmLength);

        return factor;
    }

    private class RunSQLFileThread extends Thread {
        private String fileName;
        private DatabaseInfo databaseInfo;
        private File file;
        private boolean end = false;
        boolean fisrtFile = true;
        private int lineNumber = 1;

        public RunSQLFileThread(File file, DatabaseInfo databaseInfo) {
            super();
            this.file = file;
            this.fileName = file.getName();
            this.databaseInfo = databaseInfo;
        }

        public void run() { // FIXME logic code move to core module
            BufferedReader reader = null;
            Connection conn = null;
            Statement stmt = null;
            long currentRunCount = 0;

            try {
                conn = JDBCConnectionManager.getConnection(databaseInfo, false);
                stmt = conn.createStatement();
                reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file.getAbsoluteFile()), charset));
                while (!end) {
                    if (isStop) {
                        end = true;
                        break;
                    }

                    if (fisrtFile) {
                        eventsHandler.handleEvent(new BeginOneFileEvent(fileName));
                    }

                    List<String[]> sqlList = loadSQL(reader);
                    if (sqlList.size() == 0) {
                        continue;
                    }

                    currentRunCount = executeSQL(conn, stmt, sqlList, commitCount, currentRunCount);
                    fisrtFile = false;
                }
                eventsHandler.handleEvent(new FinishOneFileEvent(fileName));
                // -1 is beacuse of except this thread count of itself
                if (executor.getCompletedTaskCount() == filesList.size() - 1) {
                    eventsHandler.handleEvent(new FinishAllFileEvent());
                    executor.shutdown();
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            } finally {
                LOGGER.debug(
                        "Committed : currentRunCount={}, commitCount={}",
                        currentRunCount,
                        commitCount);
                QueryUtil.commit(conn);
                QueryUtil.freeQuery(conn, stmt);
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }

        /**
         * load SQL file by bufferedReader
         *
         * @param reader
         * @return
         */
        public List<String[]> loadSQL(
                BufferedReader reader) { // FIXME logic code move to core module
            List<String[]> sqlList = new ArrayList<String[]>();
            try {
                String tempString = null;
                int seprateSQLLineNumber =
                        lineNumber; // the first line which sql doesnt'in one line
                String preString = "";
                boolean parseCommentFlag = false; // parse /**/ comment
                int sizeCount = 0;

                while ((tempString = reader.readLine()) != null) {
                    sizeCount += tempString.getBytes().length;
                    tempString = tempString.trim();

                    if (parseCommentFlag && tempString.endsWith("*/")) {
                        parseCommentFlag = false;
                        lineNumber++;
                        continue;
                    } else if (parseCommentFlag) {
                        lineNumber++;
                        continue;
                    }

                    if (tempString.trim().startsWith("/*")) {
                        if (tempString.trim().endsWith("*/")) {
                            lineNumber++;
                            continue;
                        } else {
                            parseCommentFlag = true;
                            lineNumber++;
                            continue;
                        }
                    }

                    if (StringUtil.isEmpty(tempString)
                            || tempString.trim().startsWith("--")
                            || tempString.trim().startsWith("//")) {
                        lineNumber++;
                        continue;
                    }

                    if (StringUtil.isNotEmpty(preString)) {
                        tempString = preString + tempString;
                    }

                    List<String[]> qList = StringUtil.extractQueries(tempString);
                    if (qList.isEmpty()) {
                        if (StringUtil.isEmpty(preString)) {
                            preString = preString + tempString + " ";
                            seprateSQLLineNumber = lineNumber;
                        } else {
                            preString = tempString + " ";
                        }
                    } else {
                        for (int i = 0; i < qList.size(); i++) {
                            if (StringUtil.isEmpty(preString)) {
                                seprateSQLLineNumber = lineNumber;
                            }

                            String[] query = qList.get(i);
                            String sql = query[0].toString();
                            if (sql.endsWith(";")) {
                                String[] sqlAndLineNumber = new String[3];
                                sqlAndLineNumber[0] = Integer.toString(seprateSQLLineNumber);
                                sqlAndLineNumber[1] = sql;
                                // whole String take the size ,each sql size separate by list size
                                sqlAndLineNumber[2] =
                                        Integer.toString((int) (sizeCount / factor) / qList.size());
                                sqlList.add(sqlAndLineNumber);
                                totalSQLNumber++;
                                if (i == qList.size() - 1
                                        && (Integer.valueOf(query[1]) + 1 < tempString.length())) {
                                    preString =
                                            tempString.substring(
                                                    Integer.valueOf(query[1]), tempString.length());
                                } else {
                                    preString = "";
                                }
                            } else {
                                preString =
                                        preString.substring(
                                                Integer.valueOf(query[1]), tempString.length());
                                break;
                            }
                        }
                        sizeCount = 0;
                        if (sqlList.size() >= FILE_FETCH_COUNT) {
                            lineNumber++;
                            return sqlList;
                        }
                    }

                    lineNumber++;
                }
            } catch (Exception e) {
                LOGGER.error("loadSQL()", e);
            }

            end = true;
            LOGGER.debug("totalSQLNumber={}", totalSQLNumber);

            return sqlList;
        }

        /**
         * execute SQL
         *
         * @param Connection con
         * @param conn
         * @param sqlList
         */
        public long executeSQL(
                Connection conn,
                Statement stmt,
                List<String[]> sqlList,
                int commitCount,
                long currentRunCount) {
            if (commitCount <= 0) { // FIXME logic code move to core module
                commitCount = DEFAULT_COMMIT_COUNT;
            }

            int workSize = 0;
            int sqlCount = 0;

            try {
                for (int i = 0; i < sqlList.size(); i++) {
                    if (isStop) {
                        break;
                    }

                    String sql = sqlList.get(i)[1];
                    String lineNumber = sqlList.get(i)[0];
                    String wokrSize = sqlList.get(i)[2];

                    try {
                        stmt.execute(sql);
                        workSize += Integer.valueOf(wokrSize);
                        sqlCount++;
                    } catch (Exception e) {
                        eventsHandler.handleEvent(new SuccessEvent(fileName, sqlCount, workSize));
                        workSize = 0;
                        sqlCount = 0;
                        eventsHandler.handleEvent(
                                new FailedEvent(
                                        fileName,
                                        sql,
                                        Long.valueOf(lineNumber),
                                        e.getMessage(),
                                        Integer.valueOf(wokrSize)));
                        LOGGER.debug(
                                "Execute SQL from SQL file sql : {}, error message: {}", sql, e);
                    } finally {
                        if (++currentRunCount % commitCount == 0) {
                            QueryUtil.commit(conn);
                            LOGGER.debug(
                                    "Committed : currentRunCount={}, commitCount={}",
                                    currentRunCount,
                                    commitCount);
                        }
                    }
                }

                if (sqlCount > 0) {
                    eventsHandler.handleEvent(new SuccessEvent(fileName, sqlCount, workSize));
                }
            } catch (Exception e) {
                LOGGER.error("commit error", e);
            }

            return currentRunCount;
        }
    }

    /**
     * create excel workbook
     *
     * @param excelFile
     * @return
     */
    public WritableWorkbook createExcel(File excelFile) {
        WritableWorkbook wwb = null;
        try {
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding(charset);
            wwb = Workbook.createWorkbook(excelFile, workbookSettings);
        } catch (Exception e) {
            LOGGER.error("create excel error", e);
        }

        return wwb;
    }

    /**
     * create excel sheet
     *
     * @param wwb
     * @param fileName
     * @param index
     * @return
     */
    public WritableSheet createaWritableSheet(
            WritableWorkbook wwb,
            String fileName,
            int index) { // FIXME logic code move to core module
        WritableSheet ws = null;
        try {
            ws = wwb.createSheet(fileName, index);
            WritableCellFormat normalCellStyle = getNormalCell();
            jxl.write.Label header =
                    new jxl.write.Label(0, 0, Messages.failedSQLlineNumber, normalCellStyle);
            ws.addCell(header);
            jxl.write.Label header1 =
                    new jxl.write.Label(1, 0, Messages.failedSQL, normalCellStyle);
            ws.addCell(header1);
            jxl.write.Label header2 =
                    new jxl.write.Label(2, 0, Messages.failedErrorMessage, normalCellStyle);
            ws.addCell(header2);
            ws.setColumnView(1, 100);
            ws.setColumnView(2, 80);
        } catch (Exception e) {
            ws = null;
            LOGGER.error("create WritableSheet error", e);
        }

        return ws;
    }

    /**
     * getNormalCell
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getNormalCell() { // FIXME logic code move to core module
        WritableFont font = new WritableFont(WritableFont.TIMES, 12);
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

    public void stopProcess() {
        isStop = true;
        eventsHandler.setStop(true);
        eventsHandler.writeExcel();
        executor.shutdownNow();
    }

    public int getPmLength() {
        return pmLength;
    }

    public boolean hasErrData() {
        return eventsHandler.hasErrData();
    }
}
