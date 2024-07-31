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
package com.cubrid.common.ui.compare.data.control;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.data.model.CompareViewData;
import com.cubrid.common.ui.compare.data.model.DataCompare;
import com.cubrid.common.ui.compare.data.model.HashedCompareData;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllSchemaTask;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;

public class DataCompareEditorPart extends EditorPart {
    public static final String ID = DataCompareEditorPart.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(DataCompareEditorPart.class);

    public static final int RUNNING_SATE_STANDBY = 0;
    public static final int RUNNING_SATE_ONLINE = 1;
    public static final int RUNNING_SATE_PENDING_STOP = 2;
    private int runningState = RUNNING_SATE_STANDBY;

    public static final int LOG_NO_MATCHES_DATA = 1;
    public static final int LOG_NO_TARGET_DATA = 2;

    private TableViewer compareTableViewer;
    private StyledText txtProgress;
    private ProgressBar progressBar;
    private Button startBtn;
    private Button countRecordBtn;
    private Button continueBtn;
    private List<DataCompare> compareList;
    private boolean enabledInteractive = true;
    private long totalRecords = 0;
    private long completedRecords = 0;
    private Button btnRge;
    private Button btnAll;
    private Button exportBtn;
    private Text txtRgeBegin;
    private Text txtRgeLimit;
    private boolean refreshedRecordCounts;
    private String logFileBasePath = null;
    private String logFileBaseName = null;
    private String charset = null;
    private DataCompareSchemaListViewSorter sorter = null;
    private boolean isRunButtonState = true;
    private boolean canBeContinued = false;
    private boolean isSelectAll = true;
    private boolean useHashMode = false;

    private final String COL_TABLE_NAME = "COL_TABLE_NAME";
    private final String COL_RECORDS_SOURCE = "COL_RECORDS_SOURCE";
    private final String COL_RECORDS_TARGET = "COL_RECORDS_TARGET";
    private final String COL_PROGRESS_COUNT = "COL_PROGRESS_COUNT";
    private final String COL_MATCHES = "COL_MATCHES";
    private final String COL_NOT_MATCHES = "COL_NOT_MATCHES";
    private final String COL_MEMO = "COL_MEMO";

    private String[] columnNames =
            new String[] {
                COL_TABLE_NAME,
                COL_RECORDS_SOURCE,
                COL_RECORDS_TARGET,
                COL_PROGRESS_COUNT,
                COL_MATCHES,
                COL_NOT_MATCHES,
                COL_MEMO
            };

    public void createPartControl(Composite parent) {
        Composite container = createContainer(parent);
        createTopPanel(container);
        createContent(container);
        createProgress(container);

        checkServerCompatibles();

        logFileBasePath = CommonUITool.getWorkspacePath();
        charset = getSourceDB().getCharSet();

        refreshedRecordCounts = false;
        doRefresh(false);
    }

    private void checkServerCompatibles() {
        boolean hasMD5OnServer1 = CompatibleUtil.isAfter840(getSourceDB());
        boolean hasMD5OnServer2 = CompatibleUtil.isAfter840(getTargetDB());
        useHashMode = hasMD5OnServer1 && hasMD5OnServer2;
    }

    private Composite createContainer(Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return container;
    }

    private void createTopPanel(Composite parent) {
        final Group container = new Group(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        CommonUITool.createGridLayout(container, 2, 0, 5, 0, 5, 0, 0, 0, 0);

        // Left area
        final Composite left = new Composite(container, SWT.NONE);
        left.setLayoutData(
                CommonUITool.createGridData(
                        GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL,
                        1,
                        1,
                        -1,
                        -1));
        CommonUITool.createGridLayout(left, 1, 0, 0, 0, 0, 0, 0, 0, 5);

        {
            final Composite left1 = new Composite(left, SWT.NONE);
            left1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
            CommonUITool.createGridLayout(left1, 6, 0, 0, 2, 0, 0, 0, 3, 0);

            String msg =
                    "["
                            + Messages.lblSource
                            + "] "
                            + getSourceDB().getDbName()
                            + "@"
                            + getSourceDB().getBrokerIP()
                            + "   -->   ["
                            + Messages.lblTarget
                            + "] "
                            + getTargetDB().getDbName()
                            + "@"
                            + getTargetDB().getBrokerIP();

            Label lbl = new Label(left1, SWT.NONE);
            lbl.setText(msg);
        }

        {
            final Composite left2 = new Composite(left, SWT.NONE);
            left2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
            CommonUITool.createGridLayout(left2, 6, 2, 0, 2, 0, 0, 0, 3, 3);

            btnAll = new Button(left2, SWT.RADIO);
            btnAll.setText(Messages.lblBtnAll);
            btnAll.setSelection(true);
            btnAll.setLayoutData(
                    CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 6, 1, -1, -1));

            btnRge = new Button(left2, SWT.RADIO);
            btnRge.setText(Messages.lblBtnRange);

            CommonUITool.createLabel(left2, Messages.lblStartPos);

            txtRgeBegin = new Text(left2, SWT.BORDER | SWT.RIGHT);
            txtRgeBegin.setLayoutData(
                    CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, 70, -1));
            txtRgeBegin.setText("1");
            txtRgeBegin.setEnabled(false);

            CommonUITool.createLabel(left2, Messages.lblRowLimit);

            txtRgeLimit = new Text(left2, SWT.BORDER | SWT.RIGHT);
            txtRgeLimit.setLayoutData(
                    CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, 60, -1));
            txtRgeLimit.setText("1000");
            txtRgeLimit.setEnabled(false);

            CommonUITool.createLabel(left2, ")");
        }

        // Right area
        final Composite right = new Composite(container, SWT.NONE);
        right.setLayoutData(
                CommonUITool.createGridData(
                        GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END,
                        1,
                        1,
                        -1,
                        -1));
        CommonUITool.createGridLayout(right, 3, 0, 0, 0, 0, 0, 0, 0, 3);

        countRecordBtn = new Button(right, SWT.PUSH);
        countRecordBtn.setText(Messages.btnDataCompareRefresh);
        countRecordBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 50));

        startBtn = new Button(right, SWT.PUSH);
        startBtn.setText(Messages.btnDataCompareStart);
        startBtn.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 90, 50));

        final Composite right2 = new Composite(right, SWT.NONE);
        right2.setLayoutData(
                CommonUITool.createGridData(
                        GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END,
                        1,
                        1,
                        -1,
                        -1));
        CommonUITool.createGridLayout(right2, 1, 0, 0, 0, 0, 0, 0, 0, 0);

        continueBtn = new Button(right2, SWT.PUSH);
        continueBtn.setText(Messages.btnDataCompareContinue);
        continueBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 80, -1));
        continueBtn.setEnabled(false);

        exportBtn = new Button(right2, SWT.PUSH);
        exportBtn.setText(Messages.btnExportReport);
        exportBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 80, -1));
        exportBtn.setEnabled(refreshedRecordCounts);

        // Define events
        btnAll.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        txtRgeBegin.setEnabled(false);
                        txtRgeLimit.setEnabled(false);
                        continueBtn.setEnabled(canBeContinued);
                    }
                });

        btnRge.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        txtRgeBegin.setEnabled(true);
                        txtRgeLimit.setEnabled(true);
                        continueBtn.setEnabled(false);
                    }
                });

        startBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if (isRunButtonState) {
                            startCompare(true);
                        } else {
                            stopCompare();
                        }
                    }
                });

        continueBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        startCompare(false);
                    }
                });

        countRecordBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        refreshTableAndRecordCounts();
                    }
                });

        exportBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if (!CommonUITool.openConfirmBox(Messages.msgDataCompareExport)) {
                            return;
                        }

                        DirectoryDialog pathDialog =
                                new DirectoryDialog(
                                        Display.getDefault().getActiveShell(), SWT.NONE);
                        pathDialog.setFilterPath("/");
                        final String resultPath = pathDialog.open();
                        if (resultPath == null) {
                            CommonUITool.openErrorBox(Messages.errNeedSelectLogPathSimple);
                            return;
                        }

                        exportDiffLog(resultPath);
                    }
                });
    }

    private void exportDiffLog(String path) {
        String reportFileName =
                "report_"
                        + getSourceDB().getBrokerIP()
                        + "_"
                        + getTargetDB().getBrokerIP()
                        + ".xls";
        exportReport(path, reportFileName);
        exportData(path);
    }

    private boolean exportData(final String basePath) {
        if (compareList == null || compareList.size() == 0) {
            CommonUITool.openErrorBox(Messages.msgNotExistsToExportData);
            return false;
        }

        printToConsole("\n", false);
        printToConsole(Messages.msgBeginToCompareData + "\n", true);

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IRunnableWithProgress progress =
                new IRunnableWithProgress() {
                    public void run(final IProgressMonitor monitor) {
                        int total = compareList.size();
                        monitor.beginTask(Messages.msgLoadingData, total);
                        Display.getDefault()
                                .syncExec(
                                        new Runnable() {
                                            public void run() {
                                                for (DataCompare comp : compareList) {
                                                    int counts =
                                                            exportDataToFile(
                                                                    basePath, comp.getTableName());
                                                    if (counts == -1) {
                                                        if (comp.getRecordsTarget() == -1) {
                                                            String msg =
                                                                    Messages.bind(
                                                                                    Messages
                                                                                            .msgDataCompareNoTable,
                                                                                    comp
                                                                                            .getTableName())
                                                                            + "\n";
                                                            printToConsoleOnWorkThread(msg, true);
                                                        } else {
                                                            String msg =
                                                                    Messages.bind(
                                                                                    Messages
                                                                                            .msgDataCompareNoData,
                                                                                    comp
                                                                                            .getTableName())
                                                                            + "\n";
                                                            printToConsoleOnWorkThread(msg, true);
                                                        }
                                                    } else {
                                                        String msg =
                                                                Messages.bind(
                                                                                Messages
                                                                                        .msgDataCompareResultStatus,
                                                                                comp.getTableName(),
                                                                                counts)
                                                                        + "\n";
                                                        printToConsoleOnWorkThread(msg, true);
                                                    }
                                                    monitor.worked(1);
                                                }
                                                monitor.done();
                                            }
                                        });
                    }
                };

        try {
            new ProgressMonitorDialog(shell).run(true, false, progress);
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

        String msg = Messages.bind(Messages.msgEndToCompareData, basePath) + "\n";
        printToConsole(msg, true);
        return true;
    }

    private int exportDataToFile(
            String basePath, String tableName) { // FIXME logic code move to core module
        String hashedTableName = StringUtil.md5(tableName);
        String filepath =
                logFileBasePath
                        + File.separatorChar
                        + logFileBaseName
                        + "_"
                        + hashedTableName
                        + ".compared";
        String targetPath = basePath + File.separatorChar + tableName + ".sql";

        if (!new File(filepath).exists()) {
            return -1;
        }

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            String charset = getSourceDB().getCharSet();
            boolean preparedColumnMeta = false;

            int rowCount = 0;

            List<String> pkColumns = new ArrayList<String>();
            List<String> pkTypes = new ArrayList<String>();

            synchronized (this) {
                in =
                        new BufferedReader(
                                new InputStreamReader(new FileInputStream(filepath), charset));
                out =
                        new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(targetPath), charset));

                String row = null;
                while ((row = in.readLine()) != null) {
                    String trimmedRow = row.trim();
                    if (trimmedRow.length() == 0) {
                        continue;
                    }

                    String[] values = row.split("\t");
                    List<String> pkValues = new ArrayList<String>();
                    for (int i = 0; i < values.length; i += 3) {
                        if (!preparedColumnMeta) {
                            String columnName = values[0];
                            pkColumns.add(columnName);

                            String columnType = values[1];
                            pkTypes.add(columnType);
                        }

                        String columnValue = values[2];
                        pkValues.add(columnValue);
                    }

                    preparedColumnMeta = true;

                    CompareViewData src = new CompareViewData();
                    src.setIndex(rowCount);
                    src.setSource(true);
                    src.setPkColumnValues(pkValues);

                    String sql = makeSelectQuery(src, tableName, pkColumns, pkTypes);
                    out.write(sql);
                    rowCount++;
                }
            }

            return rowCount;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            FileUtil.close(in);
            FileUtil.close(out);
        }

        return 0;
    }

    private String makeSelectQuery(
            CompareViewData obj,
            String tableName,
            List<String> pkColumns,
            List<String> pkTypes) { // FIXME logic code move to core module
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(QuerySyntax.escapeKeyword(tableName));
        sql.append(" WHERE ");

        for (int i = 0; i < pkColumns.size(); i++) {
            String columnName = pkColumns.get(i);
            String columnType = pkTypes.get(i);
            String columnValue = obj.getPkColumnValues().get(i);

            sql.append(QuerySyntax.escapeKeyword(columnName));
            sql.append(" = ");
            if (HashedCompareData.NUMBER_TYPE.equals(columnType)) {
                sql.append(columnValue);
            } else {
                sql.append("'").append(columnValue).append("'");
            }
        }

        sql.append(";\n");
        return sql.toString();
    }

    private boolean exportReport(
            String basePath, String reportFileName) { // FIXME logic code move to core module
        if (compareList == null || compareList.size() == 0) {
            CommonUITool.openErrorBox(Messages.msgNotExistsToExportData);
            return false;
        }

        printToConsole("\n", false);
        printToConsole(Messages.msgBeginDataCompareExcel + "\n", true);

        String reportFilePath = basePath + File.separatorChar + reportFileName;
        String charset = getSourceDB().getCharSet();
        File file = new File(reportFilePath);

        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setEncoding(charset);
        WritableWorkbook workbook = null;

        try {
            workbook = Workbook.createWorkbook(file, workbookSettings);
            String sheetName = getSourceDB().getDbName() + "-->" + getTargetDB().getDbName();
            WritableSheet sheet = workbook.createSheet(sheetName, 0);

            int index = 0;
            int total = 0;
            sheet.addCell(new jxl.write.Label(index++, total, Messages.lblDataCompareTable));
            sheet.addCell(new jxl.write.Label(index++, total, Messages.lblDataCompareRecordSource));
            sheet.addCell(new jxl.write.Label(index++, total, Messages.lblDataCompareRecordTarget));
            sheet.addCell(
                    new jxl.write.Label(index++, total, Messages.lblDataCompareRecordProgress));
            sheet.addCell(new jxl.write.Label(index++, total, Messages.lblDataCompareRecordMatch));
            sheet.addCell(
                    new jxl.write.Label(index++, total, Messages.lblDataCompareRecordNoMatch));
            sheet.addCell(
                    new jxl.write.Label(index++, total, Messages.lblDataCompareRecordNotExist));

            for (DataCompare comp : compareList) {
                index = 0;
                total++;

                sheet.addCell(new jxl.write.Label(index++, total, comp.getTableName()));
                sheet.addCell(
                        new jxl.write.Label(
                                index++, total, String.valueOf(comp.getRecordsSource())));
                String recordsTarget =
                        comp.getRecordsTarget() == -1
                                ? Messages.msgTableNotFound
                                : String.valueOf(comp.getRecordsTarget());
                sheet.addCell(new jxl.write.Label(index++, total, recordsTarget));
                sheet.addCell(
                        new jxl.write.Label(
                                index++, total, String.valueOf(comp.getProgressPosition())));
                sheet.addCell(
                        new jxl.write.Label(index++, total, String.valueOf(comp.getMatches())));
                sheet.addCell(
                        new jxl.write.Label(index++, total, String.valueOf(comp.getNotMatches())));
                sheet.addCell(
                        new jxl.write.Label(index++, total, String.valueOf(comp.getNotExists())));
            }

            workbook.write();
        } catch (Exception e) {
            CommonUITool.openErrorBox(Messages.msgToExportExcelError);
            printToConsole(e.getMessage(), true);
            LOGGER.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                noOp();
            }
        }

        String msg = Messages.bind(Messages.msgEndDataCompareExcel, reportFilePath) + "\n";
        printToConsole(msg, true);

        return true;
    }

    private void createContent(Composite parent) {
        compareTableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        compareTableViewer.setUseHashlookup(true);
        compareTableViewer.setColumnProperties(columnNames);
        CommonUITool.createGridLayout(compareTableViewer.getTable(), 1, 0, 10, 0, 10, 0, 0, 0, 0);
        compareTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        compareTableViewer.getTable().setLinesVisible(true);
        compareTableViewer.getTable().setHeaderVisible(true);

        sorter = new DataCompareSchemaListViewSorter();
        sorter.setSortType(DataCompareSchemaListViewSorter.SORT_TABLE);
        compareTableViewer.setSorter(sorter);

        makeContextMenu(compareTableViewer);

        final TableViewerColumn firstCol = new TableViewerColumn(compareTableViewer, SWT.NONE);
        firstCol.getColumn().setWidth(30);
        firstCol.getColumn().setResizable(false);
        firstCol.getColumn().setAlignment(SWT.CENTER);
        firstCol.getColumn().setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
        firstCol.getColumn()
                .addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent e) {
                                TableItem[] items = compareTableViewer.getTable().getItems();
                                if (items == null) {
                                    return;
                                }
                                for (TableItem item : items) {
                                    DataCompare dataCompare = (DataCompare) item.getData();
                                    dataCompare.setUse(isSelectAll);
                                }
                                compareTableViewer.refresh();

                                Image image = null;
                                if (isSelectAll) {
                                    image = CommonUIPlugin.getImage("icons/checked.gif");
                                } else {
                                    image = CommonUIPlugin.getImage("icons/unchecked.gif");
                                }
                                firstCol.getColumn().setImage(image);
                                isSelectAll = !isSelectAll;
                            }
                        });

        TableViewerColumn col = new TableViewerColumn(compareTableViewer, SWT.NONE);
        col.getColumn().setWidth(200);
        col.getColumn().setText(Messages.lblDataCompareTable);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_TABLE);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(100);
        col.getColumn().setText(Messages.lblDataCompareRecordSource);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_SOURCE_RECORDS);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(100);
        col.getColumn().setText(Messages.lblDataCompareRecordTarget);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_TARGET_RECORDS);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(100);
        col.getColumn().setText(Messages.lblDataCompareRecordProgress);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_PROGRESS);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(100);
        col.getColumn().setText(Messages.lblDataCompareRecordMatch);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_MATCHES);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(100);
        col.getColumn().setText(Messages.lblDataCompareRecordNoMatch);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_NOT_MATCHES);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(100);
        col.getColumn().setText(Messages.lblDataCompareRecordNotExist);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_NOT_EXISTS);

        col = new TableViewerColumn(compareTableViewer, SWT.LEFT);
        col.getColumn().setWidth(300);
        col.getColumn().setText(Messages.lblDataCompareRecordError);
        makeSortTableColumn(col, DataCompareSchemaListViewSorter.SORT_ERROR);

        CellEditor[] cellEditor = new CellEditor[1];
        cellEditor[0] = new CheckboxCellEditor(compareTableViewer.getTable());
        compareTableViewer.setCellEditors(cellEditor);
        compareTableViewer.setCellModifier(new DataCompareCellModifier(this));
        compareTableViewer.setContentProvider(new DataCompareSchemaListContentProvider());
        compareTableViewer.setLabelProvider(new DataCompareSchemaListLabelProvider());

        compareList = new ArrayList<DataCompare>();
        compareTableViewer.setInput(compareList);

        txtProgress = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        txtProgress.setEditable(false);
        txtProgress.setWordWrap(true);
        txtProgress.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 100));
        txtProgress.setBackground(txtProgress.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        txtProgress.setForeground(txtProgress.getDisplay().getSystemColor(SWT.COLOR_GREEN));

        compareTableViewer.getTable().setToolTipText(Messages.msgYouCanSeeDetailDblclick);
        compareTableViewer
                .getTable()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyReleased(KeyEvent e) {
                                if (e.character == ' ') {
                                    TableItem[] items =
                                            compareTableViewer.getTable().getSelection();
                                    if (items == null) {
                                        return;
                                    }
                                    for (TableItem item : items) {
                                        DataCompare dataCompare = (DataCompare) item.getData();
                                        dataCompare.setUse(!dataCompare.isUse());
                                    }
                                    compareTableViewer.refresh();
                                }
                            }
                        });
        compareTableViewer.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        if (!refreshedRecordCounts) {
                            CommonUITool.openWarningBox(Messages.msgClickRefreshToEsimateDiff);
                            return;
                        }

                        IStructuredSelection selection =
                                (IStructuredSelection) event.getSelection();
                        DataCompare model = (DataCompare) selection.getFirstElement();
                        if (!model.isSameSchema()) {
                            CommonUITool.openWarningBox(Messages.msgTheSchemaDiff);
                            return;
                        }
                        if (model.getRecordsTarget() == -1) {
                            CommonUITool.openWarningBox(Messages.msgTargetNotFound);
                            return;
                        }

                        if (model.getRecordsSource() == 0 && model.getRecordsTarget() > 0) {
                            CommonUITool.openWarningBox(Messages.msgTargetNoData);
                            return;
                        }

                        if (model.getRecordsSource() == 0) {
                            CommonUITool.openWarningBox(Messages.msgNoDataToCompare);
                            return;
                        }

                        if (model.getProgressPosition() == 0) {
                            CommonUITool.openWarningBox(Messages.msgNotYetCompared);
                            return;
                        }

                        if (model.getNotMatches() == 0 && model.getNotExists() == 0) {
                            CommonUITool.openWarningBox(Messages.msgSameData);
                            return;
                        }

                        showDataCompareDetailEditor(model.getSchemaInfo());
                    }
                });
    }

    private void makeContextMenu(final TableViewer tableViewer) {
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        Menu contextMenu = menuManager.createContextMenu(tableViewer.getTable());
        tableViewer.getTable().setMenu(contextMenu);
        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem useItem = new MenuItem(menu, SWT.PUSH);
        useItem.setImage(CommonUIPlugin.getImage("icons/checked.gif"));
        useItem.setText(Messages.lblCheckSelectedItems);
        useItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        checkUncheckItems(tableViewer, true);
                    }
                });

        final MenuItem noUseItem = new MenuItem(menu, SWT.PUSH);
        noUseItem.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
        noUseItem.setText(Messages.lblUncheckSelectedItems);
        noUseItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        checkUncheckItems(tableViewer, false);
                    }
                });

        new MenuItem(menu, SWT.SEPARATOR);

        final MenuItem useAllItem = new MenuItem(menu, SWT.PUSH);
        useAllItem.setImage(CommonUIPlugin.getImage("icons/checked.gif"));
        useAllItem.setText(Messages.lblCheckAllItems);
        useAllItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        checkUncheckAllItems(tableViewer, true);
                    }
                });

        final MenuItem noUseAllItem = new MenuItem(menu, SWT.PUSH);
        noUseAllItem.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
        noUseAllItem.setText(Messages.lblUncheckAllItems);
        noUseAllItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        checkUncheckAllItems(tableViewer, false);
                    }
                });

        compareTableViewer.getTable().setMenu(menu);
    }

    private void checkUncheckAllItems(TableViewer tableViewer, boolean check) {
        Table table = tableViewer.getTable();
        TableItem[] items = table.getItems();
        for (TableItem item : items) {
            DataCompare dataCompare = (DataCompare) item.getData();
            dataCompare.setUse(check);
        }
        tableViewer.refresh();
    }

    private void checkUncheckItems(TableViewer tableViewer, boolean check) {
        Table table = tableViewer.getTable();
        int selectionCount = table.getSelectionCount();
        if (selectionCount > 1) {
            TableItem[] items = table.getSelection();
            for (TableItem item : items) {
                DataCompare dataCompare = (DataCompare) item.getData();
                dataCompare.setUse(check);
            }
        }
        tableViewer.refresh();
    }

    private void makeSortTableColumn(TableViewerColumn col, final int sortType) {
        col.getColumn()
                .addSelectionListener(
                        new SelectionAdapter() {
                            boolean asc = true;

                            public void widgetSelected(SelectionEvent e) {
                                sorter.setSortType(sortType);
                                sorter.setSortAsc(asc);
                                compareTableViewer.setSorter(sorter);
                                compareTableViewer
                                        .getTable()
                                        .setSortColumn((TableColumn) e.getSource());
                                compareTableViewer
                                        .getTable()
                                        .setSortDirection(asc ? SWT.UP : SWT.DOWN);
                                compareTableViewer.refresh();
                                asc = !asc;
                            }
                        });
    }

    private void createProgress(Composite parent) {
        progressBar = new ProgressBar(parent, SWT.SMOOTH);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        progressBar.setSelection(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setToolTipText("");
    }

    private void refreshTableAndRecordCounts() {
        if (!CommonUITool.openConfirmBox(Messages.confirmDataCompareRefreshAlert)) {
            return;
        }

        changeButtonStateForRefresh();
        doRefresh(true);
        changeButtonStateForRefreshFinished();
    }

    private void startCompare(boolean initialize) {
        if (!refreshedRecordCounts) {
            CommonUITool.openErrorBox(Messages.errDataCompareNeedRefresh);
            return;
        }

        int totalCompareTables = 0;
        for (DataCompare dataCompare : compareList) {
            if (dataCompare.isUse()) {
                totalCompareTables++;
            }
        }

        if (totalCompareTables == 0) {
            if (!CommonUITool.openConfirmBox(Messages.confirmDataCompareUseAllTables)) {
                return;
            }

            for (DataCompare dataCompare : compareList) {
                dataCompare.setUse(true);
            }

            compareTableViewer.refresh();
        }

        long totalRecordsForCompare = 0;
        for (DataCompare dataCompare : compareList) {
            if (dataCompare.isUse()) {
                totalRecordsForCompare += dataCompare.getRecordsSource();
            }
        }

        if (totalRecordsForCompare == 0) {
            CommonUITool.openErrorBox(Messages.errDataCompareNeedRefresh2);
            return;
        }

        String msg =
                initialize ? Messages.confirmDataCompareStart : Messages.confirmDataCompareContinue;
        if (totalCompareTables != 0 && !CommonUITool.openConfirmBox(msg)) {
            return;
        }

        if (initialize) {
            completedRecords = 0;
            doRefresh(false);
        }

        isRunButtonState = false;

        changeButtonStateForRunningCompare();
        doCompare();
    }

    private void stopCompare() {
        if (!CommonUITool.openConfirmBox(Messages.confirmDataCompareStop)) {
            return;
        }

        runningState = RUNNING_SATE_PENDING_STOP;
        isRunButtonState = true;
        changeButtonStateForRefresh();
    }

    private String wrapNvl(String columnName) {
        return "NVL(" + columnName + ", '{NULL}')";
    }

    private String wrapClobToChar(String columnName) {
        return "CLOB_TO_CHAR(" + columnName + ")";
    }

    private String wrapBlobToBit(String columnName) {
        return "BLOB_TO_BIT(" + columnName + ")";
    }

    private String wrapToChar(String columnName, String format) {
        return "TO_CHAR(" + columnName + ",'" + format + "')";
    }

    private String wrapToChar(String columnName) {
        return "TO_CHAR(" + columnName + ")";
    }

    private List<HashedCompareData> fetchHashedCompareData(
            Connection conn,
            SchemaInfo schemaInfo,
            long start,
            int rows) { // FIXME logic code move to core module
        Statement stmt = null;
        ResultSet rs = null;

        StringBuilder pkColumns = new StringBuilder();
        Constraint constraint = schemaInfo.getPK();
        if (constraint != null) {
            for (String column : constraint.getAttributes()) {
                if (pkColumns.length() > 0) {
                    pkColumns.append(",");
                }
                pkColumns.append(QuerySyntax.escapeKeyword(column));
            }
        }

        StringBuilder extraColumns = new StringBuilder();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ").append(pkColumns).append(", ");
        sql.append(" MD5(CONCAT(");

        StringBuilder cols = new StringBuilder();
        List<DBAttribute> attrs = schemaInfo.getAttributes();
        // TODO npe
        // TODO Collections.sort
        for (int i = 0, len = attrs.size(); i < len; i++) {
            DBAttribute attr = attrs.get(i);

            if (cols.length() > 0) {
                cols.append(",");
            }

            makeColumnsClause(extraColumns, cols, attr);
        }

        sql.append(cols);
        sql.append(")) AS _record_hash_ ");

        if (extraColumns.length() > 0) {
            sql.append(",");
            sql.append(extraColumns);
        }

        String escapedTableName = QuerySyntax.escapeKeyword(schemaInfo.getUniqueName());
        sql.append(" FROM ").append(escapedTableName);
        sql.append(" ORDER BY ").append(pkColumns);
        sql.append(" FOR ORDERBY_NUM() BETWEEN ").append(start + 1);
        sql.append(" AND ").append(start + rows);

        List<HashedCompareData> result = new ArrayList<HashedCompareData>();

        try {
            StringBuilder extra = new StringBuilder();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                String hash = null;
                boolean isPk = true;
                extra.delete(0, extra.length());
                List<String> pkColumnList = new ArrayList<String>();
                List<String> pkValueList = new ArrayList<String>();
                List<String> pkTypeList = new ArrayList<String>();
                for (int j = 1, len = meta.getColumnCount(); j <= len; j++) {
                    String columnName = meta.getColumnName(j);
                    String columnType =
                            DataType.isNumberType(meta.getColumnTypeName(j))
                                    ? HashedCompareData.NUMBER_TYPE
                                    : HashedCompareData.STRING_TYPE;

                    if (columnName.equalsIgnoreCase("_record_hash_")) {
                        isPk = false;
                        hash = rs.getString(j);
                    } else if (isPk) {
                        pkColumnList.add(columnName);
                        pkValueList.add(rs.getString(j));
                        pkTypeList.add(columnType);
                    } else {
                        String val = null;
                        if (DataType.isSetDataType(meta.getColumnTypeName(j))) {
                            String colType =
                                    FieldHandlerUtils.amendDataTypeByResult(
                                            rs, j, meta.getColumnTypeName(j));
                            val =
                                    (String)
                                            FieldHandlerUtils.getRsValueForExport(
                                                    colType,
                                                    (CUBRIDResultSetProxy) rs,
                                                    j,
                                                    "{NULL}");
                        } else {
                            val = rs.getString(j);
                        }
                        if (val == null) {
                            val = "{NULL}";
                        }
                        extra.append(val);
                    }
                }

                HashedCompareData data =
                        new HashedCompareData(pkColumnList, pkValueList, pkTypeList, hash);
                result.add(data);
            }
        } catch (Exception e) {
            printToConsoleOnWorkThread(StringUtil.NEWLINE, false);
            printToConsoleOnWorkThread(e.getMessage(), true);
            LOGGER.error(sql.toString(), e);
        } finally {
            QueryUtil.freeQuery(stmt, rs);
        }

        return result;
    }

    private void compareHashedCompareData(
            Connection conn,
            DataCompare dataCompare,
            List<HashedCompareData> dataList) { // FIXME logic code move to core module
        if (dataList == null || dataList.size() == 0) {
            // TODO error
            return;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        SchemaInfo schemaInfo = dataCompare.getSchemaInfo();

        StringBuilder extraColumns = new StringBuilder();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MD5(");

        StringBuilder cols = new StringBuilder();
        List<DBAttribute> attrs = schemaInfo.getAttributes();
        // TODO npe
        // TODO Collections.sort
        for (int i = 0, len = attrs.size(); i < len; i++) {
            DBAttribute attr = attrs.get(i);

            if (cols.length() > 0) {
                cols.append(" || ");
            }

            makeColumnsClause(extraColumns, cols, attr);
        }

        sql.append(cols);
        sql.append(") AS _record_hash_ ");

        if (extraColumns.length() > 0) {
            sql.append(",");
            sql.append(extraColumns);
        }

        String escapedTableName = QuerySyntax.escapeKeyword(schemaInfo.getUniqueName());
        sql.append(" FROM ").append(escapedTableName);
        sql.append(" WHERE ");
        {
            HashedCompareData data = dataList.get(0);
            for (int i = 0, len = data.getPkColumns().size(); i < len; i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                String columnName = data.getPkColumns().get(i);
                sql.append(QuerySyntax.escapeKeyword(columnName)).append("=?");
            }
        }

        for (HashedCompareData data : dataList) {
            if (runningState != RUNNING_SATE_ONLINE) {
                return;
            }

            boolean exists = false;
            String hash = null;

            try {
                pstmt = conn.prepareStatement(sql.toString());
                for (int i = 0, len = data.getPkColumns().size(); i < len; i++) {
                    String columnValue = data.getPkValues().get(i);
                    pstmt.setString(i + 1, columnValue);
                }
                rs = pstmt.executeQuery();
                if (!rs.next()) {
                    exists = false;
                } else {
                    exists = true;
                    hash = rs.getString("_record_hash_");
                }
            } catch (SQLException e) {
                exists = false;
                printToConsoleOnWorkThread(StringUtil.NEWLINE, false);
                printToConsoleOnWorkThread(e.getMessage(), true);
                LOGGER.error(sql.toString(), e);
            } finally {
                QueryUtil.freeQuery(pstmt, rs);
            }

            dataCompare.increaseProgress();

            if (!exists) {
                dataCompare.increaseNotExists();
                addLog(schemaInfo.getUniqueName(), data, charset);
            } else {
                if (data.getHash().equals(hash)) {
                    dataCompare.increaseMatches();
                } else {
                    dataCompare.increaseNotMatches();
                    addLog(schemaInfo.getUniqueName(), data, charset);
                }
            }

            completedRecords++;
            refreshProgressBar();

            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    compareTableViewer.refresh();
                                }
                            });
        }
    }

    private List<HashedCompareData> fetchHashedCompareDataCompatible(
            Connection conn,
            SchemaInfo schemaInfo,
            long start,
            int rows) { // FIXME logic code move to core module
        Statement stmt = null;
        ResultSet rs = null;

        StringBuilder pkColumns = new StringBuilder();
        Constraint constraint = schemaInfo.getPK();
        if (constraint != null) {
            for (String column : constraint.getAttributes()) {
                if (pkColumns.length() > 0) {
                    pkColumns.append(",");
                }
                pkColumns.append(QuerySyntax.escapeKeyword(column));
            }
        }

        String escapedTableName = QuerySyntax.escapeKeyword(schemaInfo.getUniqueName());
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ").append(escapedTableName);
        sql.append(" ORDER BY ").append(pkColumns);
        sql.append(" FOR ORDERBY_NUM() BETWEEN ").append(start + 1);
        sql.append(" AND ").append(start + rows);

        List<HashedCompareData> result = new ArrayList<HashedCompareData>();

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                StringBuilder hash = new StringBuilder();
                boolean isPk = true;
                List<String> pkColumnList = new ArrayList<String>();
                List<String> pkValueList = new ArrayList<String>();
                List<String> pkTypeList = new ArrayList<String>();
                // TODO : sort
                for (int j = 1, len = meta.getColumnCount(); j <= len; j++) {
                    String columnName = meta.getColumnName(j);
                    String columnType =
                            DataType.isNumberType(meta.getColumnTypeName(j))
                                    ? HashedCompareData.NUMBER_TYPE
                                    : HashedCompareData.STRING_TYPE;

                    isPk = constraint.getAttributes().contains(columnName);
                    if (isPk) {
                        pkColumnList.add(columnName);
                        pkValueList.add(rs.getString(j));
                        pkTypeList.add(columnType);
                    } else {
                        String val = null;
                        if (DataType.isSetDataType(meta.getColumnTypeName(j))) {
                            String colType =
                                    FieldHandlerUtils.amendDataTypeByResult(
                                            rs, j, meta.getColumnTypeName(j));
                            val =
                                    (String)
                                            FieldHandlerUtils.getRsValueForExport(
                                                    colType,
                                                    (CUBRIDResultSetProxy) rs,
                                                    j,
                                                    "{NULL}");
                        } else {
                            val = rs.getString(j);
                        }
                        if (val == null) {
                            val = "{NULL}";
                        }
                        hash.append(StringUtil.md5(val));
                    }
                }

                HashedCompareData data =
                        new HashedCompareData(
                                pkColumnList,
                                pkValueList,
                                pkTypeList,
                                StringUtil.md5(hash.toString()));
                result.add(data);
            }
        } catch (Exception e) {
            printToConsoleOnWorkThread(StringUtil.NEWLINE, false);
            printToConsoleOnWorkThread(e.getMessage(), true);
            LOGGER.error(sql.toString(), e);
        } finally {
            QueryUtil.freeQuery(stmt, rs);
        }

        return result;
    }

    private void compareHashedCompareDataCompatible(
            Connection conn,
            DataCompare dataCompare,
            List<HashedCompareData> dataList) { // FIXME logic code move to core module
        if (dataList == null || dataList.size() == 0) {
            // TODO error
            return;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        SchemaInfo schemaInfo = dataCompare.getSchemaInfo();
        String escapedTableName = QuerySyntax.escapeKeyword(schemaInfo.getUniqueName());

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ").append(escapedTableName);
        sql.append(" WHERE ");
        {
            HashedCompareData data = dataList.get(0);
            for (int i = 0, len = data.getPkColumns().size(); i < len; i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                String columnName = data.getPkColumns().get(i);
                sql.append(QuerySyntax.escapeKeyword(columnName)).append(" = ?");
            }
        }

        for (HashedCompareData data : dataList) {
            if (runningState != RUNNING_SATE_ONLINE) {
                return;
            }

            boolean exists = false;
            StringBuilder hash = new StringBuilder();

            try {
                pstmt = conn.prepareStatement(sql.toString());
                for (int i = 0, len = data.getPkColumns().size(); i < len; i++) {
                    String columnValue = data.getPkValues().get(i);
                    pstmt.setString(i + 1, columnValue);
                }
                rs = pstmt.executeQuery();
                if (!rs.next()) {
                    exists = false;
                } else {
                    exists = true;

                    ResultSetMetaData meta = rs.getMetaData();
                    for (int j = 1, len = meta.getColumnCount(); j <= len; j++) {
                        String columnName = meta.getColumnName(j);

                        boolean isPk = data.getPkColumns().contains(columnName);
                        if (isPk) {
                            continue;
                        }

                        String val = null;
                        if (DataType.isSetDataType(meta.getColumnTypeName(j))) {
                            String colType =
                                    FieldHandlerUtils.amendDataTypeByResult(
                                            rs, j, meta.getColumnTypeName(j));
                            val =
                                    (String)
                                            FieldHandlerUtils.getRsValueForExport(
                                                    colType,
                                                    (CUBRIDResultSetProxy) rs,
                                                    j,
                                                    "{NULL}");
                        } else {
                            val = rs.getString(j);
                        }
                        if (val == null) {
                            val = "{NULL}";
                        }
                        hash.append(StringUtil.md5(val));
                    }
                }
            } catch (SQLException e) {
                exists = false;
                printToConsoleOnWorkThread(StringUtil.NEWLINE, false);
                printToConsoleOnWorkThread(e.getMessage(), true);
                LOGGER.error(sql.toString(), e);
            } finally {
                QueryUtil.freeQuery(pstmt, rs);
            }

            dataCompare.increaseProgress();

            if (!exists) {
                dataCompare.increaseNotExists();
                addLog(schemaInfo.getUniqueName(), data, charset);
            } else {
                if (data.getHash().equals(StringUtil.md5(hash.toString()))) {
                    dataCompare.increaseMatches();
                } else {
                    dataCompare.increaseNotMatches();
                    addLog(schemaInfo.getUniqueName(), data, charset);
                }
            }

            completedRecords++;
            refreshProgressBar();

            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    compareTableViewer.refresh();
                                }
                            });
        }
    }

    private void makeColumnsClause(
            StringBuilder extraColumns,
            StringBuilder sql,
            DBAttribute attr) { // FIXME logic code move to core module
        String attrName = QuerySyntax.escapeKeyword(attr.getName());
        String type = DataType.getTypePart(attr.getType());
        if (DataType.DATATYPE_DATE.equalsIgnoreCase(type)) {
            sql.append(wrapNvl(wrapToChar(attrName, "YYYYMMDD")));
        } else if (DataType.DATATYPE_TIME.equalsIgnoreCase(type)) {
            sql.append(wrapNvl(wrapToChar(attrName, "HH24MISS")));
        } else if (DataType.DATATYPE_DATETIME.equalsIgnoreCase(type)) {
            sql.append(wrapNvl(wrapToChar(attrName, "YYYYMMDDHH24MISSFF")));
        } else if (DataType.DATATYPE_TIMESTAMP.equalsIgnoreCase(type)) {
            sql.append(wrapNvl(wrapToChar(attrName, "YYYYMMDDHH24MISS")));
        } else if (DataType.DATATYPE_BLOB.equalsIgnoreCase(type)) {
            sql.append(wrapNvl(wrapBlobToBit(attrName)));
        } else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(type)) {
            sql.append(wrapNvl(wrapClobToChar(attrName)));
        } else if (DataType.DATATYPE_SET.equalsIgnoreCase(type)
                || "set_of".equalsIgnoreCase(type)
                || "sequence_of".equalsIgnoreCase(type)
                || "multiset_of".equalsIgnoreCase(type)
                || DataType.DATATYPE_SEQUENCE.equalsIgnoreCase(type)
                || DataType.DATATYPE_MULTISET.equalsIgnoreCase(type)) {
            if (extraColumns.length() > 0) {
                extraColumns.append(",");
            }
            extraColumns.append(attrName);
        } else if (DataType.DATATYPE_OBJECT.equalsIgnoreCase(type)) {
            sql.append("'{OBJECT}'");
        } else {
            sql.append(wrapNvl(wrapToChar(attrName)));
        }
    }

    private void doCompare() { // FIXME logic code move to core module
        runningState = RUNNING_SATE_ONLINE;
        enabledInteractive = false;

        final boolean fullscan = btnAll.getSelection();
        long startTmp = StringUtil.longValue(txtRgeBegin.getText(), 1) - 1;
        if (startTmp < 0) {
            startTmp = 0;
        }
        final long start = startTmp;
        final int limit = StringUtil.intValue(txtRgeLimit.getText(), 1000);

        Thread thread =
                new Thread(
                        new Runnable() {
                            public void run() {
                                Connection conn = null;

                                try {
                                    totalRecords = 0;
                                    for (DataCompare dataCompare : compareList) {
                                        if (!dataCompare.isUse()) {
                                            continue;
                                        }

                                        if (!dataCompare.isSameSchema()) {
                                            continue;
                                        }

                                        long total = dataCompare.getRecordsSource();
                                        long totalTarget = dataCompare.getRecordsTarget();

                                        if (totalTarget <= 0) {
                                            continue;
                                        }

                                        if (!fullscan) {
                                            if (start >= total) {
                                                continue;
                                            }

                                            totalRecords += Math.min(total, limit);
                                        } else {
                                            totalRecords += total;
                                        }
                                    }

                                    for (DataCompare dataCompare : compareList) {
                                        if (runningState != RUNNING_SATE_ONLINE) {
                                            break;
                                        }

                                        if (!dataCompare.isSameSchema()) {
                                            continue;
                                        }

                                        if (!dataCompare.isUse()) {
                                            continue;
                                        }

                                        int fetchLimit = 1000;
                                        if (!fullscan) {
                                            // fetchLimit = limit;
                                        }

                                        long total = dataCompare.getRecordsSource();
                                        long totalTarget = dataCompare.getRecordsTarget();

                                        // Skip, if the target have no data
                                        if (totalTarget <= 0) {
                                            dataCompare.setProgressPosition(0);
                                            dataCompare.setNotExists(total);
                                            Display.getDefault()
                                                    .syncExec(
                                                            new Runnable() {
                                                                public void run() {
                                                                    compareTableViewer.refresh();
                                                                }
                                                            });
                                            continue;
                                        }

                                        if (total <= 0) {
                                            dataCompare.setProgressPosition(0);
                                            dataCompare.setNotExists(0);
                                            Display.getDefault()
                                                    .syncExec(
                                                            new Runnable() {
                                                                public void run() {
                                                                    compareTableViewer.refresh();
                                                                }
                                                            });
                                            continue;
                                        }

                                        long current = dataCompare.getProgressPosition();
                                        if (!fullscan) {
                                            if (start >= total) {
                                                continue;
                                            }
                                            current = 0;
                                            total = current + limit;
                                            dataCompare.setProgressPosition(current);
                                        }

                                        for (; current < total; current += fetchLimit) {
                                            if (runningState != RUNNING_SATE_ONLINE) {
                                                break;
                                            }

                                            String msg =
                                                    Messages.bind(
                                                            Messages.lblDataCompareLogSrcLoading,
                                                            dataCompare.getTableName(),
                                                            (current + 1));
                                            printToConsoleOnWorkThread(msg, true);
                                            conn =
                                                    JDBCConnectionManager.getConnection(
                                                            getSourceDB(), true);
                                            List<HashedCompareData> dataList = null;

                                            if (useHashMode) {
                                                dataList =
                                                        fetchHashedCompareData(
                                                                conn,
                                                                dataCompare.getSchemaInfo(),
                                                                current,
                                                                fetchLimit);
                                            } else {
                                                dataList =
                                                        fetchHashedCompareDataCompatible(
                                                                conn,
                                                                dataCompare.getSchemaInfo(),
                                                                current,
                                                                fetchLimit);
                                            }

                                            QueryUtil.freeQuery(conn);
                                            printToConsoleOnWorkThread("\n", false);

                                            msg =
                                                    Messages.bind(
                                                            Messages.lblDataCompareLogTargetDiff
                                                                    + "\n",
                                                            dataCompare.getTableName(),
                                                            (current + 1));
                                            printToConsoleOnWorkThread(msg, true);
                                            conn =
                                                    JDBCConnectionManager.getConnection(
                                                            getTargetDB(), true);

                                            if (useHashMode) {
                                                compareHashedCompareData(
                                                        conn, dataCompare, dataList);
                                            } else {
                                                compareHashedCompareDataCompatible(
                                                        conn, dataCompare, dataList);
                                            }

                                            QueryUtil.freeQuery(conn);
                                            printToConsoleOnWorkThread("\n", false);
                                        }
                                    }

                                    if (runningState != RUNNING_SATE_ONLINE) {
                                        runningState = RUNNING_SATE_STANDBY;
                                        Display.getDefault()
                                                .asyncExec(
                                                        new Runnable() {
                                                            public void run() {
                                                                changeButtonStateForFinished();
                                                                printToConsole(
                                                                        Messages
                                                                                        .msgDataCompareStopped
                                                                                + "\n",
                                                                        true);
                                                            }
                                                        });
                                        return;
                                    }
                                } catch (SQLException e) {
                                    LOGGER.error(e.getMessage(), e);
                                    final String error = e.getMessage();
                                    runningState = RUNNING_SATE_STANDBY;
                                    Display.getDefault()
                                            .asyncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            changeButtonStateForFinished();
                                                            printToConsole(
                                                                    Messages
                                                                                    .msgDataCompareStoppedError
                                                                            + "\n",
                                                                    true);
                                                            printToConsole(error, true);
                                                        }
                                                    });
                                    return;
                                } finally {
                                    QueryUtil.freeQuery(conn);
                                }

                                runningState = RUNNING_SATE_STANDBY;
                                Display.getDefault()
                                        .asyncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        changeButtonStateForFinished();
                                                        printToConsole(
                                                                Messages.msgDataCompareCompleted
                                                                        + "\n",
                                                                true);
                                                    }
                                                });
                            }
                        });
        thread.start();
    }

    private void doRefresh(boolean collectRecordCount) { // FIXME logic code move to core module
        Map<String, DataCompare> dataCompareMap = new HashMap<String, DataCompare>();
        if (compareList != null) {
            for (DataCompare dataCompare : compareList) {
                dataCompareMap.put(dataCompare.getTableName(), dataCompare);
            }
        }

        DatabaseInfo sourceDB = ((DataCompareEditorInput) getEditorInput()).getSourceDB();
        DatabaseInfo targetDB = ((DataCompareEditorInput) getEditorInput()).getTargetDB();
        if (logFileBaseName != null) {
            FileUtil.delete(logFileBasePath + File.separatorChar + logFileBaseName);
            logFileBaseName = null;
        }

        logFileBaseName = sourceDB.getDbName() + "_" + System.currentTimeMillis();

        GetAllSchemaTask sourceSchemaTask = new GetAllSchemaTask(sourceDB);
        GetAllSchemaTask targetSchemaTask = new GetAllSchemaTask(targetDB);
        TaskExecutor taskExecutor = new CommonTaskExec(Messages.loadEntireSchemaComparison);
        taskExecutor.addTask(sourceSchemaTask);
        taskExecutor.addTask(targetSchemaTask);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            synchronized (compareList) {
                compareList.clear();

                Set<String> partitions = new HashSet<String>();
                List<SchemaInfo> sourceList = sourceSchemaTask.getSchemaList();
                List<SchemaInfo> targetList = targetSchemaTask.getSchemaList();
                for (SchemaInfo schemaInfo : sourceList) {
                    if (schemaInfo.getPartitionList() != null) {
                        for (PartitionInfo partition : schemaInfo.getPartitionList()) {
                            String partClassName = partition.getPartitionClassName();
                            partitions.add(partClassName);
                        }
                    }
                }
                for (SchemaInfo schemaInfo : sourceList) {
                    DataCompare dataCompare = dataCompareMap.get(schemaInfo.getUniqueName());
                    if (dataCompare == null) {
                        dataCompare = new DataCompare();
                        dataCompare.setTableName(schemaInfo.getUniqueName());
                        dataCompare.setSchemaInfo(schemaInfo);
                        dataCompare.setRefreshed(false);
                    } else {
                        dataCompare.setMatches(0);
                        dataCompare.setNotExists(0);
                        dataCompare.setNotMatches(0);
                        dataCompare.setProgressPosition(0);
                    }

                    if (schemaInfo.hasPK() && !partitions.contains(schemaInfo.getUniqueName())) {
                        SchemaInfo targetSchemeInfo =
                                getSchemeInfoByName(schemaInfo.getUniqueName(), targetList);
                        boolean isSameSchema = canCompareData(schemaInfo, targetSchemeInfo);
                        dataCompare.setSameSchema(isSameSchema);
                        compareList.add(dataCompare);
                    }
                }

                Collections.sort(
                        compareList,
                        new Comparator<DataCompare>() {
                            public int compare(DataCompare o1, DataCompare o2) {
                                if (o1 == null || o1.getTableName() == null) {
                                    return -1;
                                } else if (o2 == null || o2.getTableName() == null) {
                                    return 1;
                                }

                                return o1.getTableName().compareToIgnoreCase(o2.getTableName());
                            }
                        });
            }

            compareTableViewer.refresh();
        }

        if (!collectRecordCount) {
            return;
        }

        totalRecords = 0;
        completedRecords = 0;

        refreshRecordCount();
        refreshedRecordCounts = true;
    }

    private SchemaInfo getSchemeInfoByName(String name, List<SchemaInfo> list) {
        for (SchemaInfo s : list) {
            if (StringUtil.isEqual(name, s.getUniqueName())) {
                return s;
            }
        }
        return null;
    }

    private boolean canCompareData(SchemaInfo source, SchemaInfo target) {
        if (target == null) {
            return false;
        }
        for (DBAttribute attr : source.getAttributes()) {
            if (target.getDBAttributeByName(attr.getName(), attr.isClassAttribute()) == null) {
                return false;
            }
        }
        return true;
    }

    private void refreshRecordCount() { // FIXME logic code move to core module
        enabledInteractive = false;

        Thread thread =
                new Thread(
                        new Runnable() {
                            public void run() {
                                Connection conn = null;

                                try {
                                    printToConsoleOnWorkThread(
                                            Messages.msgDataCompareRefreshingSourceRecord, true);

                                    final DatabaseInfo sourceDB = getSourceDB();
                                    conn = JDBCConnectionManager.getConnection(sourceDB, true);
                                    for (DataCompare dataCompare : compareList) {
                                        dataCompare.setRefreshed(true);
                                        long counts =
                                                QueryUtil.countRecords(
                                                        conn, dataCompare.getTableName());
                                        dataCompare.setRecordsSource(counts);
                                    }
                                    QueryUtil.freeQuery(conn);

                                    Display.getDefault()
                                            .asyncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            compareTableViewer.refresh();
                                                        }
                                                    });

                                    printToConsoleOnWorkThread(
                                            " "
                                                    + Messages.msgDataCompareRefreshingCompleted
                                                    + ".\n",
                                            false);
                                    printToConsoleOnWorkThread(
                                            Messages.msgDataCompareRefreshingTargetRecord, true);

                                    final DatabaseInfo targetDB = getTargetDB();
                                    conn = JDBCConnectionManager.getConnection(targetDB, true);
                                    for (DataCompare dataCompare : compareList) {
                                        long counts =
                                                QueryUtil.countRecords(
                                                        conn, dataCompare.getTableName());
                                        dataCompare.setRecordsTarget(counts);
                                    }

                                    Display.getDefault()
                                            .asyncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            compareTableViewer.refresh();
                                                        }
                                                    });

                                    printToConsoleOnWorkThread(
                                            " "
                                                    + Messages.msgDataCompareRefreshingCompleted
                                                    + ".\n\n",
                                            false);

                                    totalRecords = 0;
                                    for (DataCompare dataCompare : compareList) {
                                        totalRecords += dataCompare.getRecordsSource();
                                    }

                                    refreshProgressBar();
                                } catch (SQLException e) {
                                    LOGGER.error(e.getMessage(), e);
                                } finally {
                                    QueryUtil.freeQuery(conn);
                                }

                                enabledInteractive = true;
                            }
                        });
        thread.start();
    }

    private int lastPct = 0;

    private void refreshProgressBar() {
        final int pct = (int) Math.round((double) completedRecords / (double) totalRecords * 100.0);
        if (pct == lastPct) {
            return;
        }

        lastPct = pct;

        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                progressBar.setSelection(pct);
                                String msg = Messages.bind(Messages.msgDataCompareProgressMsg, pct);
                                progressBar.setToolTipText(msg);
                            }
                        });
    }

    private void printToConsoleOnWorkThread(final String message, final boolean showDateTime) {
        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                printToConsole(message, showDateTime);
                            }
                        });
    }

    public void printToConsole(String message, boolean showDateTime) {
        if (showDateTime) {
            txtProgress.append(
                    "["
                            + DateUtil.getDatetimeString(new Date(), Messages.msgLogDatePattern)
                            + "] ");
        }
        txtProgress.append(message);
        txtProgress.setTopIndex(txtProgress.getLineCount() - 1);
    }

    public void addLog(
            String tableName,
            HashedCompareData hashedCompareData,
            String charset) { // FIXME logic code move to core module
        String hashedTableName = StringUtil.md5(tableName);
        List<String> columns = hashedCompareData.getPkColumns();
        List<String> values = hashedCompareData.getPkValues();
        List<String> types = hashedCompareData.getPkTypes();

        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = columns.size(); i < len; i++) {
            String column = StringUtil.urlencode(columns.get(i), charset);
            String type = types.get(i);
            String value = StringUtil.urlencode(values.get(i), charset);
            if (i > 0) {
                sb.append("\t");
            }
            sb.append(column);
            sb.append("\t");
            sb.append(type);
            sb.append("\t");
            sb.append(value);
        }
        sb.append("\n");

        if (logFileBaseName != null) {
            String path =
                    logFileBasePath
                            + File.separatorChar
                            + logFileBaseName
                            + "_"
                            + hashedTableName
                            + ".compared";
            synchronized (this) {
                FileUtil.writeToFile(path, sb.toString(), charset, true);
            }
        }
    }

    private void changeButtonStateForRefresh() {
        enabledInteractive = false;
        isRunButtonState = true;
        canBeContinued = false;
        txtProgress.setSelection(0);
        startBtn.setEnabled(true);
        startBtn.setText(Messages.btnDataCompareStop);
        countRecordBtn.setEnabled(false);
        continueBtn.setEnabled(false);

        exportBtn.setEnabled(false);
        btnAll.setEnabled(false);
        btnRge.setEnabled(false);
        txtRgeBegin.setEnabled(false);
        txtRgeLimit.setEnabled(false);
    }

    private void changeButtonStateForRefreshFinished() {
        enabledInteractive = true;
        isRunButtonState = true;
        canBeContinued = false;
        txtProgress.setSelection(100);
        startBtn.setEnabled(true);
        startBtn.setText(Messages.btnDataCompareStart);
        countRecordBtn.setEnabled(true);
        continueBtn.setEnabled(canBeContinued);

        exportBtn.setEnabled(refreshedRecordCounts);
        btnAll.setEnabled(true);
        btnRge.setEnabled(true);
        txtRgeBegin.setEnabled(btnRge.getSelection());
        txtRgeLimit.setEnabled(btnRge.getSelection());
    }

    private void changeButtonStateForRunningCompare() {
        enabledInteractive = false;
        isRunButtonState = false;
        canBeContinued = false;
        txtProgress.setSelection(0);
        startBtn.setEnabled(true);
        startBtn.setText(Messages.btnDataCompareStop);
        countRecordBtn.setEnabled(false);
        continueBtn.setEnabled(false);

        exportBtn.setEnabled(false);
        btnAll.setEnabled(false);
        btnRge.setEnabled(false);
        txtRgeBegin.setEnabled(false);
        txtRgeLimit.setEnabled(false);
    }

    private void changeButtonStateForFinished() {
        enabledInteractive = true;
        isRunButtonState = true;
        canBeContinued = btnAll.getSelection();
        txtProgress.setSelection(100);
        startBtn.setEnabled(true);
        startBtn.setText(Messages.btnDataCompareStart);
        countRecordBtn.setEnabled(true);
        continueBtn.setEnabled(canBeContinued);

        exportBtn.setEnabled(refreshedRecordCounts);
        btnAll.setEnabled(true);
        btnRge.setEnabled(true);
        txtRgeBegin.setEnabled(btnRge.getSelection());
        txtRgeLimit.setEnabled(btnRge.getSelection());
    }

    private DatabaseInfo getSourceDB() {
        DataCompareEditorInput input = (DataCompareEditorInput) getEditorInput();
        return input.getSourceDB();
    }

    private DatabaseInfo getTargetDB() {
        DataCompareEditorInput input = (DataCompareEditorInput) getEditorInput();
        return input.getTargetDB();
    }

    public List<String> getColumnNames() {
        return Arrays.asList(columnNames);
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public TableViewer getCompareTableViewer() {
        return compareTableViewer;
    }

    public void reloadInput() {
        compareTableViewer.setInput(compareList);
    }

    public boolean isOffline() {
        return enabledInteractive;
    }

    public void setOffline(boolean offline) {
        this.enabledInteractive = offline;
    }

    public void showDataCompareDetailEditor(SchemaInfo schemaInfo) {
        try {
            String hashedTableName = StringUtil.md5(schemaInfo.getUniqueName());
            String path =
                    logFileBasePath
                            + File.separatorChar
                            + logFileBaseName
                            + "_"
                            + hashedTableName
                            + ".compared";
            DataCompareDetailEditorInput input =
                    new DataCompareDetailEditorInput(
                            getSourceDB(), getTargetDB(), path, schemaInfo);
            PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, DataCompareDetailEditorPart.ID);
        } catch (Exception e) {
            // TODO fix the following message
            CommonUITool.openErrorBox(
                    Display.getDefault().getActiveShell(),
                    com.cubrid.common.ui.compare.Messages.fetchSchemaErrorFromDB);
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void setFocus() {}

    public void doSave(IProgressMonitor monitor) {}

    public void doSaveAs() {}

    public void dispose() {
        runningState = RUNNING_SATE_PENDING_STOP;
        super.dispose();
    }
}
