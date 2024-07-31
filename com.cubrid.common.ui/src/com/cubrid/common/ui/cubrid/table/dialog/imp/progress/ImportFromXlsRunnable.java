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
package com.cubrid.common.ui.cubrid.table.dialog.imp.progress;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataTableFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportRowData;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportStatus;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandlerFactory;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSImportFileHandler;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.biff.EmptyCell;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import org.slf4j.Logger;

/**
 * The ImportFormTxtThread Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public class ImportFromXlsRunnable extends AbsImportRunnable {
    private static final Logger LOGGER = LogUtil.getLogger(ImportFromXlsRunnable.class);

    /**
     * The constructor
     *
     * @param database
     * @param parameterList
     * @param tableName
     * @param importConfig
     * @param importDataEventHandler
     */
    public ImportFromXlsRunnable(
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
    @Override
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
        String fileName = tableConfig.getFilePath();
        boolean isFirstRowAsColumn = tableConfig.isFirstRowAsColumn();
        File parentFile;
        File file = new File(fileName);
        if (file.exists()) {
            parentFile = file.getParentFile();
        } else {
            parentFile = null;
        }

        int start = 0;
        if (isFirstRowAsColumn) {
            start = 1;
        }

        try {
            XLSImportFileHandler fileHandler =
                    (XLSImportFileHandler)
                            ImportFileHandlerFactory.getHandler(fileName, importConfig);
            Sheet[] sheets = fileHandler.getSheets();
            ImportFileDescription fileDesc = getFileDescription(fileHandler);
            int currentRow = 0;
            List<ImportRowData> rowList = new ArrayList<ImportRowData>();
            for (int sheetNum = 0; sheetNum < sheets.length; sheetNum++) {
                int rows = fileDesc.getItemsNumberOfSheets().get(sheetNum);

                Sheet sheet = sheets[sheetNum];
                String[] rowContent = null;
                String[] patterns = null;
                ImportRowData rowData = null;
                String content = null;
                String pattern = null;
                for (int i = start; i < rows; i++) {
                    boolean isSuccess = true;
                    try {
                        int columns = sheet.getColumns();
                        for (int j = 0; j < columns; j++) {
                            rowContent = new String[columns];
                            patterns = new String[columns];
                            Cell[] cells = sheet.getRow(i);
                            for (int k = 0; k < cells.length; k++) {
                                Cell cell = cells[k];
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
                                rowContent[k] = content;
                                patterns[k] = pattern;
                            }
                        }
                        /*Process the row data*/
                        rowData = processRowData(rowContent, patterns, currentRow, parentFile);
                        pStmt.addBatch();
                        rowList.add(rowData);
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
                        LOGGER.debug("Stop import by user setting.");
                        break;
                    } catch (OutOfMemoryError error) {
                        throw new RuntimeException(error);
                    } finally {
                        if (!isSuccess) {
                            rowData.setStatus(ImportStatus.STATUS_COMMIT_FAILED);
                            writeErrorLog(rowData);
                        }
                    }
                }
            }
            if (rowList.size() > 0) {
                commit(rowList);
            }
        } catch (BiffException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        }
    }
}
