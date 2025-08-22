/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.core.reader.CSVReader;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataTableFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportRowData;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportStatus;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 * The ImportFormTxtThread Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public class ImportFromCsvRunnable extends AbsImportRunnable {
    private static final Logger LOGGER = LogUtil.getLogger(ImportFromCsvRunnable.class);

    /**
     * The constructor
     *
     * @param database
     * @param parameterList
     * @param tableName
     * @param importConfig
     * @param importDataEventHandler
     */
    public ImportFromCsvRunnable(
            CubridDatabase database,
            String tableName,
            ImportConfig importConfig,
            ImportDataEventHandler importDataEventHandler,
            ImportDataProgressManager progressManager) {
        super(database, tableName, importConfig, progressManager, importDataEventHandler);
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.cubrid.table.dialog.imp.progress.AbsImportDataThread#doRun()
     */
    protected void doRun() throws Exception { // FIXME move this logic to core module
        if (pStmt == null) {
            handleEvent(
                    new ImportDataFailedEvent(
                            tableName,
                            tableConfig.getLineCount(),
                            tableConfig.getInsertDML(),
                            "Invalid parameters."));
            return;
        }
        File file = new File(tableConfig.getFilePath());
        boolean isFirstRowAsColumn = tableConfig.isFirstRowAsColumn();
        String fileCharset = importConfig.getFilesCharset();
        File parentFile;
        if (file.exists()) {
            parentFile = file.getParentFile();
        } else {
            parentFile = null;
        }

        CSVReader csvReader = null;
        int currentRow = 0;
        List<ImportRowData> rowList = new ArrayList<ImportRowData>();
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
                currentRow++;
            }

            String[] cvsRow;
            ImportRowData rowData = null;
            while ((cvsRow = csvReader.readNext()) != null) {
                boolean isSuccess = true;
                try {
                    /*Process the row data*/
                    rowData = processRowData(cvsRow, null, currentRow, parentFile);
                    rowList.add(rowData);
                    pStmt.addBatch();
                    currentRow++;

                    /*Process commit*/
                    if (rowList.size() >= importConfig.getCommitLine()) {
                        commit(rowList);
                    }

                    if (isCanceled) {
                        return;
                    }

                } catch (SQLException ex) {
                    isSuccess = false;
                    LOGGER.debug(ex.getMessage());
                } catch (StopPerformException ex) {
                    isSuccess = false;
                    handleEvent(new ImportDataTableFailedEvent(tableName));
                    break;
                } finally {
                    if (!isSuccess) {
                        rowData.setStatus(ImportStatus.STATUS_COMMIT_FAILED);
                        writeErrorLog(rowData);
                    }
                }
            }
            if (rowList.size() > 0) {
                commit(rowList);
                rowList = null;
            }

        } catch (IOException ex) {
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
}
