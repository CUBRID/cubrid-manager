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
package com.cubrid.common.ui.cubrid.table.importhandler.handler;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * XLS Import File Handler, it will handle with the XLS format data import for multi-thread access.
 *
 * <p>Now use jxl API parse the excel XLS format file, jxl API have the below problem: It will load
 * the all data to memory when first call Sheet.getCell() or Sheet.getColumns().
 *
 * <p>When use multi-thread to import the XLS data, for sharing the data must preload the data
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-22 created by Kevin Cao
 */
public class XLSImportFileHandler implements ImportFileHandler {

    private static final Logger LOGGER = LogUtil.getLogger(XLSImportFileHandler.class);

    private final String fileName;
    private final String fileCharset;
    private Workbook workbook = null;
    private ImportFileDescription importFileDescription = null;
    private Sheet[] sheets = null;

    /**
     * The constructor
     *
     * @param fileName
     * @param fileCharset
     */
    public XLSImportFileHandler(String fileName, String fileCharset) {
        this.fileName = fileName;
        this.fileCharset = fileCharset;
    }

    /**
     * Get the source file information
     *
     * @return ImportFileDescription
     * @throws Exception in process.
     */
    public ImportFileDescription getSourceFileInfo()
            throws Exception { // FIXME move this logic to core module
        synchronized (this) {
            if (importFileDescription == null) {
                final List<String> colsLst = new ArrayList<String>();
                final List<Integer> itemsNumberOfSheets = new ArrayList<Integer>();
                importFileDescription = new ImportFileDescription(0, 0, colsLst);

                IRunnableWithProgress runnable =
                        new IRunnableWithProgress() {
                            public void run(final IProgressMonitor monitor) {
                                monitor.beginTask("", IProgressMonitor.UNKNOWN);
                                Workbook workbook = null;
                                int totalRowCount = 0;
                                int sheetNum = 0;
                                try {
                                    if (fileCharset == null) {
                                        workbook = Workbook.getWorkbook(new File(fileName));
                                    } else {
                                        WorkbookSettings workbookSettings = new WorkbookSettings();
                                        workbookSettings.setEncoding(fileCharset);
                                        workbook =
                                                Workbook.getWorkbook(
                                                        new File(fileName), workbookSettings);
                                    }

                                    // get column count and total row count
                                    sheetNum = workbook.getNumberOfSheets();
                                    if (sheetNum > 0) {
                                        int columnCount = workbook.getSheet(0).getColumns();
                                        for (int j = 0;
                                                !monitor.isCanceled() && j < columnCount;
                                                j++) {
                                            Cell cell = workbook.getSheet(0).getCell(j, 0);
                                            colsLst.add(
                                                    cell == null
                                                            ? ""
                                                            : cell.getContents()); // $NON-NLS-1$
                                        }
                                    }
                                    for (int i = 0; !monitor.isCanceled() && i < sheetNum; i++) {
                                        int rowsInSheet = workbook.getSheet(i).getRows();
                                        itemsNumberOfSheets.add(Integer.valueOf(rowsInSheet));
                                        totalRowCount += rowsInSheet;
                                    }
                                    if (monitor.isCanceled()) {
                                        throw new InterruptedException();
                                    }
                                } catch (Exception e) {
                                    LOGGER.error(e.getMessage(), e);
                                    throw new RuntimeException(e);
                                } finally {
                                    importFileDescription.setSheetNum(sheetNum);
                                    importFileDescription.setTotalCount(totalRowCount);
                                    importFileDescription.setFirstRowCols(colsLst);
                                    importFileDescription.setItemsNumberOfSheets(
                                            itemsNumberOfSheets);
                                    if (workbook != null) {
                                        workbook.close();
                                    }
                                    monitor.done();
                                }
                            }
                        };

                PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
            }
            return importFileDescription;
        }
    }

    /**
     * Get the excel workbook
     *
     * @return the Workbook
     * @throws BiffException The exception
     * @throws IOException The exception
     */
    public Workbook getWorkbook()
            throws BiffException, IOException { // FIXME move this logic to core module
        synchronized (this) {
            if (workbook == null) {
                File file = new File(fileName);
                if (fileCharset == null || fileCharset.trim().length() == 0) {
                    workbook = Workbook.getWorkbook(file);
                } else {
                    WorkbookSettings workbookSettings = new WorkbookSettings();
                    workbookSettings.setEncoding(fileCharset);
                    workbook = Workbook.getWorkbook(file, workbookSettings);
                }
            }
            return workbook;
        }
    }

    /**
     * Get sheets
     *
     * @return Sheet[]
     * @throws IOException The exception
     * @throws BiffException The exception
     */
    public Sheet[] getSheets()
            throws BiffException, IOException { // FIXME move this logic to core module
        synchronized (this) {
            if (sheets == null) {
                try {
                    if (workbook == null) {
                        getWorkbook();
                    }
                    sheets = workbook.getSheets();
                    // this sheets is used by multi-thread, hence preload the data
                    for (int i = 0; sheets != null && i < sheets.length; i++) {
                        sheets[i].getColumns(); // it will load all data
                    }
                } catch (OutOfMemoryError error) {
                    sheets = null;
                    throw new RuntimeException(error);
                }
            }
            return sheets;
        }
    }

    /** Close the workbook */
    public void dispose() {
        synchronized (this) { // FIXME move this logic to core module
            if (workbook != null) {
                workbook.close();
            }
            workbook = null;
            sheets = null;
            importFileDescription = null;
        }
    }
}
