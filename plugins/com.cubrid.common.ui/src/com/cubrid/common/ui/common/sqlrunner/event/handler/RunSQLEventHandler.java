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
package com.cubrid.common.ui.common.sqlrunner.event.handler;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.sqlrunner.event.BeginOneFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FailedEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FinishAllFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FinishOneFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.RunSQLEvent;
import com.cubrid.common.ui.common.sqlrunner.event.SuccessEvent;
import com.cubrid.common.ui.common.sqlrunner.event.monitor.IRunSQLMonitor;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.slf4j.Logger;

/**
 * Run SQL Event handler
 *
 * @author fulei
 */
public class RunSQLEventHandler implements IRunSQLEventHandler {
    private static final Logger LOGGER = LogUtil.getLogger(RunSQLEventHandler.class);

    private final IRunSQLMonitor migrationMonitor;
    private final ExecutorService handlerExecutor = Executors.newFixedThreadPool(1);
    private final HashMap<String, Integer> excelSheetIndexMap = new HashMap<String, Integer>();
    private final WritableCellFormat normalCellStyle = getNormalCell();
    private FinishAllFileEvent mfe;
    private HashMap<String, WritableSheet> excelSheetMap;
    private WritableWorkbook wwb;
    private File excelFile;
    private boolean hasErrData = false;
    private boolean isStop = false;

    public RunSQLEventHandler(
            IRunSQLMonitor migrationMonitor,
            File excelFile,
            WritableWorkbook wwb,
            HashMap<String, WritableSheet> excelSheetMap) {
        this.migrationMonitor = migrationMonitor;
        this.excelSheetMap = excelSheetMap;
        this.wwb = wwb;
        this.excelFile = excelFile;
    }

    public void handleEvent(RunSQLEvent event) {
        handlerExecutor.execute(new EventHandlerRunnable(event));
    }

    public void dispose() {
        migrationMonitor.finished();
        handlerExecutor.shutdown();
    }

    /**
     * Event Handler Runner class.
     *
     * @author fulei
     */
    protected class EventHandlerRunnable implements Runnable {
        private final RunSQLEvent event;

        public EventHandlerRunnable(RunSQLEvent event) {
            this.event = event;
        }

        public void run() {
            try {
                if (isStop()) {
                    return;
                }

                // After finished event, new event will not be accepted.
                if (mfe != null) {
                    return;
                }

                if (event instanceof FinishAllFileEvent) {
                    // Only receives the first MigrationFinishedEvent.
                    mfe = (FinishAllFileEvent) event;
                    migrationMonitor.addEvent(event);
                    writeExcel();
                    dispose();
                } else if (event instanceof SuccessEvent
                        || event instanceof BeginOneFileEvent
                        || event instanceof FinishOneFileEvent) {
                    migrationMonitor.addEvent(event);
                } else if (event instanceof FailedEvent) {
                    migrationMonitor.addEvent(event);
                    hasErrData = true;
                    if (wwb != null) {
                        writeFailedInfoToExcel((FailedEvent) event);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Write failed data to excel.
     *
     * @param event
     */
    public void writeFailedInfoToExcel(FailedEvent event) { // FIXME logic code move to core module
        WritableSheet sheet = excelSheetMap.get(event.getFileName());
        Integer row = excelSheetIndexMap.get(event.getFileName());
        if (row == null) {
            row = 1;
        }

        try {
            jxl.write.Number lineNmuber =
                    new jxl.write.Number(0, row, event.getIndex(), normalCellStyle);
            sheet.addCell(lineNmuber);
            jxl.write.Label sql = new jxl.write.Label(1, row, event.getSql(), normalCellStyle);
            sheet.addCell(sql);
            jxl.write.Label errMessage =
                    new jxl.write.Label(2, row, event.getErrorMessage(), normalCellStyle);
            sheet.addCell(errMessage);
            excelSheetIndexMap.put(event.getFileName(), ++row);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /** Write excel and close it */
    public void writeExcel() {
        if (wwb == null) {
            return;
        }

        try {
            wwb.write();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (wwb != null) {
                    wwb.close();
                    if (!hasErrData && excelFile != null) {
                        excelFile.delete();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Return the normal cell format.
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getNormalCell() { // FIXME logic code move to core module
        WritableFont font = new WritableFont(WritableFont.TIMES, 12);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.LEFT);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return format;
    }

    public HashMap<String, WritableSheet> getExcelSheetMap() {
        return excelSheetMap;
    }

    public void setExcelSheetMap(HashMap<String, WritableSheet> excelSheetMap) {
        this.excelSheetMap = excelSheetMap;
    }

    public WritableWorkbook getWwb() {
        return wwb;
    }

    public void setWwb(WritableWorkbook wwb) {
        this.wwb = wwb;
    }

    public boolean hasErrData() {
        return hasErrData;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }
}
