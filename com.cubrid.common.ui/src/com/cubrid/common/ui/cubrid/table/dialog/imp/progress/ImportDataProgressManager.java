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

package com.cubrid.common.ui.cubrid.table.dialog.imp.progress;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.TableConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandlerImpl;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.slf4j.Logger;

/**
 * The ImportDataProgressManager Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public class ImportDataProgressManager implements IImportDataProcessManager {

    private static final Logger LOGGER = LogUtil.getLogger(ImportDataProgressManager.class);

    private final ImportDataEventHandler importDataEventHandler;
    private final CubridDatabase database;
    private final ImportConfig importConfig;

    private ThreadPoolExecutor executor = null;

    private ProgressIndicator progressIndicator;
    private int pmLength = 0;
    private boolean initSuccess = false;

    private volatile int totalDataTaskCount = 0;
    private volatile int finishedDataTaskCount = 0;
    private volatile int totalTaskCount = 0;
    private volatile int finishedTaskCount = 0;
    private long totalRecordCount = 0;

    private List<AbsImportRunnable> importDataRunnableList = new ArrayList<AbsImportRunnable>();
    private ImportDDLRunnable importDDLRunnable = null;
    private volatile boolean isPerformedIndexFile = false;

    public ImportDataProgressManager(
            IImportDataMonitor importDataMonitor,
            CubridDatabase database,
            ProgressIndicator progressIndicator,
            ImportConfig importConfig) {
        this.database = database;
        this.progressIndicator = progressIndicator;
        this.importConfig = importConfig;
        importDataEventHandler = new ImportDataEventHandlerImpl(importDataMonitor);
        executor =
                new ThreadPoolExecutor(
                        importConfig.getThreadCount(),
                        importConfig.getThreadCount(),
                        1,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>());
        computeTaskCount();
        initHistoryConfig();
    }

    public void startProcess() {
        // If need perform schema sql
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            try {
                performSchemaFile();
            } catch (Exception ex) {
                CommonUITool.openErrorBox("Create schema failed:" + ex.getMessage());
                LOGGER.error(ex.getMessage());
                return;
            }
        }
        // If need perform index file before import data
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL
                && importConfig.isHaMode()) {
            try {
                performIndexFile();
            } catch (Exception ex) {
                CommonUITool.openErrorBox("Create index failed:" + ex.getMessage());
                LOGGER.error(ex.getMessage());
                return;
            }
        }
        // Perform data import
        LinkedHashMap<String, TableConfig> map =
                importConfig.getTableConfigByType(TableConfig.TYPE_DATA);
        for (String tableName : map.keySet()) {
            AbsImportRunnable importDataRunnable = getImportRunnable(importConfig, tableName);
            executor.execute(importDataRunnable);
            importDataRunnableList.add(importDataRunnable);
        }
        // If only index file and not ha mode
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL
                && !importConfig.isHaMode()
                && importConfig.getTableConfigByType(TableConfig.TYPE_INDEX).size()
                        == importConfig.getSelectedMap().size()) {
            try {
                performIndexFile();
            } catch (Exception ex) {
                CommonUITool.openErrorBox("Create index failed:" + ex.getMessage());
                LOGGER.error(ex.getMessage());
                return;
            }
        }
    }

    /**
     * Perform schema sql file, This is synchronized
     *
     * @throws InterruptedException
     */
    private synchronized void performSchemaFile() throws InterruptedException {
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            LinkedHashMap<String, TableConfig> map =
                    importConfig.getTableConfigByType(TableConfig.TYPE_SCHEMA);
            for (String key : map.keySet()) {
                importDDLRunnable =
                        new ImportDDLRunnable(
                                database, key, importConfig, importDataEventHandler, null);
                Thread thread = new Thread(importDDLRunnable);
                thread.start();
                thread.join();
                taskFinished(importDDLRunnable);
                importDDLRunnable = null;
            }
        }
    }

    /**
     * Perform index sql file, This is synchronized
     *
     * @throws InterruptedException
     */
    private synchronized void performIndexFile() throws InterruptedException {
        if (isPerformedIndexFile) {
            return;
        }

        isPerformedIndexFile = true;
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            LinkedHashMap<String, TableConfig> map =
                    importConfig.getTableConfigByType(TableConfig.TYPE_INDEX);
            for (String key : map.keySet()) {
                importDDLRunnable =
                        new ImportDDLRunnable(
                                database, key, importConfig, importDataEventHandler, null);
                Thread thread = new Thread(importDDLRunnable);
                thread.start();
                thread.join();
                taskFinished(importDDLRunnable);
                importDDLRunnable = null;
            }
        }
    }

    public void stopProcess() {
        /*Stop the import thread first*/
        for (AbsImportRunnable importDataRunnable : importDataRunnableList) {
            if (importDataRunnable != null) {
                importDataRunnable.setCancel(true);
            }
        }
        executor.shutdownNow();

        if (importDDLRunnable != null) {
            importDDLRunnable.setCancel(true);
        }
    }

    private AbsImportRunnable getImportRunnable(ImportConfig importConfig, String tableName) {
        int importType = importConfig.getImportType();

        switch (importType) {
            case ImportConfig.IMPORT_FROM_TXT:
                {
                    return new ImportFromTxtRunnable(
                            database, tableName, importConfig, importDataEventHandler, this);
                }
            case ImportConfig.IMPORT_FROM_EXCEL:
                {
                    TableConfig tableConfig = importConfig.getSelectedMap().get(tableName);
                    String fileName = tableConfig.getFilePath();

                    String lowerCase = fileName.toLowerCase(Locale.getDefault());
                    if (lowerCase.endsWith(".xlsx")) {
                        return new ImportFromXlsxRunnable(
                                database, tableName, importConfig, importDataEventHandler, this);
                    } else if (lowerCase.endsWith(".xls")) {
                        return new ImportFromXlsRunnable(
                                database, tableName, importConfig, importDataEventHandler, this);
                    } else if (lowerCase.endsWith(".csv")) {
                        return new ImportFromCsvRunnable(
                                database, tableName, importConfig, importDataEventHandler, this);
                    }
                }
            case ImportConfig.IMPORT_FROM_SQL:
                {
                    return new ImportFromSQLRunnable(
                            database, tableName, importConfig, importDataEventHandler, this);
                }
        }
        LOGGER.error("Get import thread failed.");
        return null;
    }

    public synchronized void taskFinished(AbsImportRunnable task) {
        // Count finished task count
        finishedTaskCount++;
        if (importDataRunnableList.contains(task)) {
            importDataRunnableList.remove(task);
            finishedDataTaskCount++;
        }

        // If all the taks finished
        if (finishedTaskCount == totalTaskCount) {
            importDataEventHandler.handleEvent(new ImportDataFinishAllTableEvent());
            executor.shutdown();
            importDataEventHandler.dispose();
        } else if (finishedDataTaskCount == totalDataTaskCount) { // If import sql data finished
            // If need create index
            if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL
                    && !isPerformedIndexFile) {
                try {
                    performIndexFile();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        }
    }

    /**
     * parse get total data number
     *
     * @return
     */
    public void computeTaskCount() { // FIXME move this logic to core module
        // Import SQL file
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            for (TableConfig config : importConfig.getSelectedMap().values()) {
                File file = new File(config.getFilePath());
                if (file.exists()) {
                    totalRecordCount += file.length();
                }
                if (TableConfig.TYPE_DATA.equals(config.getFileType())) {
                    totalDataTaskCount++;
                }
                totalTaskCount++;
            }
        } else {
            for (TableConfig config : importConfig.getSelectedMap().values()) {
                int count = 0;
                if (config != null) {
                    count = config.getLineCount();
                }
                totalRecordCount += count;
                totalDataTaskCount++;
                totalTaskCount++;
            }
        }
        pmLength = (int) (totalRecordCount);
        progressIndicator.beginTask(pmLength);
        setInitSuccess(true);
    }

    private void initHistoryConfig() { // FIXME move this logic to core module
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String path = Platform.getInstanceLocation().getURL().getPath();
        String folderName = "[Import data]" + dateFormat.format(new Date());
        File errLogFolder = new File(path + File.separator + folderName);
        while (errLogFolder.exists()) {
            folderName = "[Import data]" + dateFormat.format(new Date());
            errLogFolder = new File(path + File.separator + folderName);
        }

        importConfig.setName(errLogFolder.getName());
        importConfig.setErrorLogFolderPath(errLogFolder.getAbsolutePath());
    }

    public boolean isInitSuccess() {
        return initSuccess;
    }

    public void setInitSuccess(boolean initSuccess) {
        this.initSuccess = initSuccess;
    }
}
