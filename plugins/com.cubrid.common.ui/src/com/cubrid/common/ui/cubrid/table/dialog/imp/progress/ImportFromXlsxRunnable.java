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
import com.cubrid.common.ui.cubrid.table.control.XlsxReaderHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataTableFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportRowData;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportStatus;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandlerFactory;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSXImportFileHandler;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.io.File;
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
public class ImportFromXlsxRunnable extends AbsImportRunnable {
    private static final Logger LOGGER = LogUtil.getLogger(ImportFromXlsxRunnable.class);

    //	private SimpleDateFormat datetimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
    //			Locale.getDefault());
    //	private SimpleDateFormat timestampSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
    //			Locale.getDefault());
    //	private SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    //	private SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static int importedRow = 0;

    /**
     * The constructor
     *
     * @param database
     * @param parameterList
     * @param tableName
     * @param importConfig
     * @param importDataEventHandler
     */
    public ImportFromXlsxRunnable(
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
        final File parentFile;
        File file = new File(fileName);
        if (file.exists()) {
            parentFile = file.getParentFile();
        } else {
            parentFile = null;
        }

        final XLSXImportFileHandler importFileHandler =
                (XLSXImportFileHandler) ImportFileHandlerFactory.getHandler(fileName, importConfig);
        final List<ImportRowData> rowList = new ArrayList<ImportRowData>();
        XlsxReaderHandler xlsxReader =
                new XlsxReaderHandler((XLSXImportFileHandler) importFileHandler) {
                    boolean isFirstRowAsColumn = tableConfig.isFirstRowAsColumn();

                    private String[] rowContentArray;
                    private ImportRowData rowData = null;
                    private boolean isFailed = false;

                    public void operateRows(int sheetIndex, List<String> rowContentlist) {
                        if (isFailed) {
                            return;
                        }

                        if (currentRow == getTitleRow()) {
                            return;
                        }

                        if (rowContentlist == null) {
                            return;
                        }

                        rowContentArray = new String[rowContentlist.size()];
                        rowContentlist.toArray(rowContentArray);
                        boolean isSuccess = true;
                        try {
                            /*Process the row data*/
                            rowData = processRowData(rowContentArray, null, currentRow, parentFile);
                            rowList.add(rowData);
                            pStmt.addBatch();
                            importedRow++;

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
                            LOGGER.debug("Stoped by user setting.");
                            isFailed = true;
                        } catch (OutOfMemoryError error) {
                            throw new RuntimeException(error);
                        } finally {
                            if (!isSuccess) {
                                rowData.setStatus(ImportStatus.STATUS_COMMIT_FAILED);
                                writeErrorLog(rowData);
                            }
                        }
                    }

                    public void startDocument() {
                        if (isFirstRowAsColumn) {
                            setTitleRow(0);
                        }
                    }
                };
        xlsxReader.process(fileName);

        if (rowList.size() > 0) {
            commit(rowList);
        }
    }

    //	protected ImportRowData processRowData(String[] columnArray, String[] columnPattern,
    //			List<ImportRowData> rowDataList, int currentRow, File parentFile) {
    //		ImportRowData rowData = new ImportRowData(currentRow);
    //		ImportColumnData columnData = null;
    //		try {
    //			for (int i = 0; i < tableConfig.getPstmList().size(); i++) {
    //				PstmtParameter pstmtParameter = tableConfig.getPstmList().get(i);
    //				int column = Integer.parseInt(pstmtParameter.getStringParamValue());
    //				String dataType = DataType.getType(pstmtParameter.getDataType());
    //				String content = null;
    //				String pattern = null;
    //				if (columnArray.length > column) {
    //					content = columnArray[column];
    //				}
    //				if (columnPattern != null && columnPattern.length > column) {
    //					pattern = columnPattern[column];
    //				}
    //				int cellType = FieldHandlerUtils.getCellType(dataType,
    //						content);
    //				boolean isSuccess = false;
    //				try {
    //					double value;
    //					Date dateCon;
    //					switch (cellType) {
    //					case -1:
    //						content = DataType.NULL_EXPORT_FORMAT;
    //						break;
    //					case 0:
    //						if (content.contains(".")) {
    //							content = content.substring(0,
    //									content.indexOf('.'));
    //						}
    //						break;
    //					case 2:
    //						value = Double.parseDouble(content.trim());
    //						dateCon = DateUtil.getJavaDate(value);
    //						content = datetimeSdf.format(dateCon);
    //						break;
    //					case 3:
    //						value = Double.parseDouble(content.trim());
    //						dateCon = DateUtil.getJavaDate(value);
    //						content = timestampSdf.format(dateCon);
    //						break;
    //					case 4:
    //						value = Double.parseDouble(content.trim());
    //						dateCon = DateUtil.getJavaDate(value);
    //						content = dateSdf.format(dateCon);
    //						break;
    //					case 5:
    //						value = Double.parseDouble(content.trim());
    //						dateCon = DateUtil.getJavaDate(value);
    //						content = timeSdf.format(dateCon);
    //						break;
    //					default:
    //						break;
    //					}
    //				} catch (NumberFormatException e) {
    //					isSuccess = false;
    //				}
    //				columnData = new ImportColumnData(content);
    //				rowData.getColumnList().add(columnData);
    //				Object value = getRealValueForImport(dataType, content, parentFile);
    //				try {
    //					PstmtParameter parameter = new PstmtParameter(pstmtParameter.getParamName(),
    //							pstmtParameter.getParamIndex(), pstmtParameter.getDataType(), value);
    //					parameter.setCharSet(importConfig.getFilesCharset());
    //					if (StringUtil.isNotEmpty(pattern)) {
    //						parameter.setDatePattern(pattern);
    //					}
    //					if (value != null && value instanceof File) {
    //						parameter.setFileValue(true);
    //					}
    //					setPreparedStatementValue(pStmt, parameter, dbCharset);
    //					columnData.setStatus(ImportStatus.STATUS_FORMAT_SUCCESS);
    //					isSuccess = true;
    //				} catch (ParamSetException ex) {
    //					isSuccess = false;
    //					LOGGER.debug(ex.getMessage());
    //				} catch (SQLException ex) {
    //					isSuccess = false;
    //					LOGGER.debug(ex.getMessage());
    //				} finally {
    //					if (!isSuccess) {
    //						dataTypeErrorHandling(getErrorMsg(currentRow, column, dataType));
    //						PstmtParameter parameter = new PstmtParameter(
    //								pstmtParameter.getParamName(), pstmtParameter.getParamIndex(),
    //								pstmtParameter.getDataType(), null);
    //						parameter.setCharSet(importConfig.getFilesCharset());
    //						try {
    //							setPreparedStatementNULL(pStmt, parameter);
    //						} catch (SQLException e) {
    //							LOGGER.debug(e.getMessage());
    //						}
    //						columnData.setStatus(ImportStatus.STATUS_FORMAT_FAILED);
    //					}
    //				}
    //			}
    //		} catch (OutOfMemoryError error) {
    //			throw new RuntimeException(error);
    //		} catch (DataFormatException e) {
    //			columnData.setStatus(ImportStatus.STATUS_FORMAT_FAILED);
    //			throw new RuntimeException(e);
    //		} finally {
    //			rowDataList.add(rowData);
    //		}
    //		return rowData;
    //	}
}
