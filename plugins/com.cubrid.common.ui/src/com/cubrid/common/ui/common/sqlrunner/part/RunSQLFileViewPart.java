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
package com.cubrid.common.ui.common.sqlrunner.part;

import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.sqlrunner.RunSQLFileTableContentProvider;
import com.cubrid.common.ui.common.sqlrunner.RunSQLFileTableLabelProvider;
import com.cubrid.common.ui.common.sqlrunner.dialog.ViewSQLLogDialog;
import com.cubrid.common.ui.common.sqlrunner.event.BeginOneFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FailedEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FinishAllFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.FinishOneFileEvent;
import com.cubrid.common.ui.common.sqlrunner.event.RunSQLEvent;
import com.cubrid.common.ui.common.sqlrunner.event.SuccessEvent;
import com.cubrid.common.ui.common.sqlrunner.event.monitor.IRunSQLMonitor;
import com.cubrid.common.ui.common.sqlrunner.event.monitor.RunSQLProcessManager;
import com.cubrid.common.ui.common.sqlrunner.model.SqlRunnerFailed;
import com.cubrid.common.ui.common.sqlrunner.model.SqlRunnerProgress;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * run SQL part
 *
 * @author fulei
 */
public class RunSQLFileViewPart extends EditorPart implements ISaveablePart2 {
    public static final String ID = RunSQLFileViewPart.class.getName();

    private final int FAILSQLMARKCOUNT = 1000;
    private ProgressBar pbTotal;
    private TableViewer tvProgress;
    private Button btnStop;
    private List<String> filesList;
    private CubridDatabase database = null;
    private boolean stop = false;
    private RunSQLFileEditorInput input;
    private Button openLogFolderButton;
    private List<SqlRunnerProgress> tableList = new ArrayList<SqlRunnerProgress>();
    private RunSQLProcessManager manager = null;

    /**
     * Create part controls
     *
     * @param parent of the controls
     */
    public void createPartControl(Composite parent) {
        Composite backPanel = new Composite(parent, SWT.NONE);
        backPanel.setLayout(new GridLayout(1, false));
        backPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite progressPanel = new Composite(backPanel, SWT.NONE);
        progressPanel.setLayout(new GridLayout(3, false));
        progressPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        pbTotal = new ProgressBar(progressPanel, SWT.NONE);
        pbTotal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        btnStop = new Button(progressPanel, SWT.NONE);
        btnStop.setText(Messages.btnStop);
        btnStop.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run_stop.png"));
        btnStop.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        processStop();
                    }
                });

        openLogFolderButton = new Button(progressPanel, SWT.NONE);
        openLogFolderButton.setText(Messages.runSQLOpenBtn);
        openLogFolderButton.setEnabled(false);
        openLogFolderButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            java.awt.Desktop.getDesktop().open(new File(input.getLogFolderPath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Composite textComp = new Composite(backPanel, SWT.NONE);
        GridData textCompGd = new GridData(GridData.FILL_HORIZONTAL);
        textCompGd.heightHint = 50;
        textCompGd.exclude = true;
        textComp.setVisible(false);
        textComp.setLayoutData(textCompGd);
        textComp.setLayout(new GridLayout(1, false));

        Text errorText = new Text(textComp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        errorText.setLayoutData(new GridData(GridData.FILL_BOTH));
        errorText.setEditable(false);

        tvProgress = new TableViewer(backPanel, SWT.BORDER | SWT.FULL_SELECTION);
        tvProgress.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        tvProgress.getTable().setLinesVisible(true);
        tvProgress.getTable().setHeaderVisible(true);

        final TableViewerColumn fileName = new TableViewerColumn(tvProgress, SWT.NONE);

        final TableViewerColumn successCount = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn failCount = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn status = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn elapsedTime = new TableViewerColumn(tvProgress, SWT.NONE);
        fileName.getColumn().setWidth(250);
        fileName.getColumn().setText(Messages.fileName);

        successCount.getColumn().setWidth(100);
        successCount.getColumn().setText(Messages.successCount);
        failCount.getColumn().setWidth(100);
        failCount.getColumn().setText(Messages.failCount);
        status.getColumn().setWidth(100);
        status.getColumn().setText(Messages.columnStatus);
        elapsedTime.getColumn().setWidth(130);
        elapsedTime.getColumn().setText(Messages.columnElapsedTime);

        tvProgress.setContentProvider(new RunSQLFileTableContentProvider());
        tvProgress.setLabelProvider(new RunSQLFileTableLabelProvider());
        tvProgress.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) event.getSelection();
                        SqlRunnerProgress po = (SqlRunnerProgress) selection.getFirstElement();

                        new ViewSQLLogDialog(
                                        Display.getDefault().getActiveShell(),
                                        po.getFileName(),
                                        po.getFailList())
                                .open();
                    }
                });

        String errorMsg = testConnection();
        if (errorMsg != null) {
            pbTotal.setEnabled(false);
            btnStop.setEnabled(false);
            textCompGd.exclude = false;
            textComp.setVisible(true);
            errorText.setText("Can't get connection : " + errorMsg);
            textComp.getParent().layout();
            return;
        }

        initManager();
        intiTableView();
    }

    /** process stop logic */
    public void processStop() {
        if (manager != null) {
            if (!btnStop.isDisposed()) {
                btnStop.setEnabled(false);
                pbTotal.setSelection(pbTotal.getMaximum());

                for (SqlRunnerProgress po : tableList) {
                    if (po.getStatus() == 1) {
                        po.setStatus(3);
                    }
                }

                tvProgress.refresh(tableList);
            }
            manager.stopProcess();
            stop = true;
        }
    }

    /**
     * initManager
     *
     * @return load file result
     */
    public void initManager() {
        manager =
                new RunSQLProcessManager(
                        filesList,
                        new RunSQLMonitor(),
                        database,
                        pbTotal,
                        input.getCharset(),
                        input.getCommitCount(),
                        input.getLogFolderPath(),
                        input.getMaxThreadSize());
        manager.startProcess();
    }

    /**
     * test connection before run SQL
     *
     * @return
     */
    public String testConnection() {
        Connection conn = null;
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                return Messages.errGetConnectionFailed;
            }
            return e.getMessage();
        } finally {
            QueryUtil.freeQuery(conn);
        }

        return null;
    }

    /** intiTableView */
    private void intiTableView() {
        for (String filePath : filesList) {
            SqlRunnerProgress po = new SqlRunnerProgress(new File(filePath).getName());
            tableList.add(po);
        }
        tvProgress.setInput(tableList);
    }

    /** @return cann't be close in prograss */
    public int promptToSaveOnClose() {
        return manager == null ? ISaveablePart2.CANCEL : ISaveablePart2.YES;
    }

    /**
     * Init the editor part
     *
     * @param site IEditorSite
     * @param input MigrationCfgEditorInput
     * @throws PartInitException when error
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.input = (RunSQLFileEditorInput) input;
        this.database = this.input.getDatabase();
        this.filesList = this.input.getFileList();

        setSite(site);
        setInput(input);
        setTitleToolTip(input.getName());
        setTitleImage(CommonUIPlugin.getImage("icons/navigator/sql.png"));
        setPartName(input.getName());
    }

    /**
     * Do nothing
     *
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {}

    /** Do nothing */
    public void doSaveAs() {}

    /**
     * Default return false
     *
     * @return true if is running
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * Default return false
     *
     * @return false
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /** Set focus */
    public void setFocus() {
        btnStop.setFocus();
    }

    public void dispose() {
        super.dispose();
        if (!stop) {
            processStop();
        }
    }

    public class RunSQLMonitor implements IRunSQLMonitor {
        private List<RunSQLEvent> eventCache = new ArrayList<RunSQLEvent>();

        public void finished() {
            Display.getDefault()
                    .asyncExec(
                            new Runnable() {
                                public void run() {
                                    btnStop.setEnabled(false);
                                    pbTotal.setSelection(pbTotal.getMaximum());
                                    if (manager.hasErrData()) {
                                        openLogFolderButton.setEnabled(true);
                                    }
                                }
                            });
        }

        /**
         * Event found
         *
         * @param event MigrationEvent
         */
        public void addEvent(final RunSQLEvent event) {
            if (stop) {
                return;
            }

            // Add a cache to make the UI refresh per second.
            eventCache.add(event);
            flushUI();
        }

        /** Write events to UI */
        public void flushUI() {
            final List<RunSQLEvent> ec = new ArrayList<RunSQLEvent>(eventCache);
            eventCache.clear();
            Thread thread =
                    new Thread() {
                        public void run() {
                            for (RunSQLEvent evt : ec) {
                                if (stop) {
                                    return;
                                }
                                int pbvalue = 0;
                                if (evt instanceof BeginOneFileEvent) {
                                    updateOneTableData((BeginOneFileEvent) evt);
                                } else if (evt instanceof SuccessEvent) {
                                    updateTableData(evt);
                                    pbvalue += ((SuccessEvent) evt).getWorkSize();
                                } else if (evt instanceof FailedEvent) {
                                    updateTableData(evt);
                                    pbvalue += ((FailedEvent) evt).getWorkSize();
                                } else if (evt instanceof FinishOneFileEvent) {
                                    updateOneTableData((FinishOneFileEvent) evt);
                                }
                                if (evt instanceof FinishAllFileEvent) {
                                    btnStop.setEnabled(false);
                                    pbTotal.setSelection(pbTotal.getMaximum());
                                    if (manager.hasErrData()) {
                                        openLogFolderButton.setEnabled(true);
                                    }
                                    stop = true;
                                    return;
                                }
                                // Sleep a moment or the progress bar will not change
                                // the status.
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException ex) {

                                }
                                if (pbvalue > 0) {
                                    pbTotal.setSelection(pbTotal.getSelection() + pbvalue);
                                }
                            }
                        }
                    };
            Display.getDefault().asyncExec(thread);
        }

        /**
         * when one file begin ,update table
         *
         * @param evt BeginOneFileEvent
         */
        private void updateOneTableData(BeginOneFileEvent evt) {
            if (stop) {
                return;
            }

            String fileName = evt.getFileName();
            for (int i = 0; i < tableList.size(); i++) {
                SqlRunnerProgress po = tableList.get(i);
                if (po.getFileName().equals(fileName)) {
                    po.setBeginTime(evt.getEventTime());
                    po.setStatus(1);
                    tvProgress.refresh(tableList);
                    break;
                }
            }
        }

        /**
         * when one file finish ,update table
         *
         * @param evt FinishOneFileEvent
         */
        private void updateOneTableData(FinishOneFileEvent evt) {
            if (stop) {
                return;
            }

            String fileName = evt.getFileName();
            for (int i = 0; i < tableList.size(); i++) {
                SqlRunnerProgress po = tableList.get(i);
                if (!po.getFileName().equals(fileName)) {
                    continue;
                }

                if (!po.isError()) {
                    Color color = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
                    Table table = tvProgress.getTable();
                    table.getItems()[i].setBackground(color);
                    table.redraw();
                }
                po.setStatus(2);
                po.setElapsedTime(evt.getEventTime() - po.getBeginTime());
                tvProgress.refresh(tableList);
                break;
            }
        }

        /**
         * @param evt parse event
         * @return sql byte
         */
        private void updateTableData(RunSQLEvent evt) {
            if (stop) {
                return;
            }

            int index = 0;
            int sqlCount = 0;
            String fileName = "";
            boolean isSuccess = true;

            FailedEvent failedEvent = null;
            if (evt instanceof SuccessEvent) {
                SuccessEvent event = (SuccessEvent) evt;
                fileName = event.getFileName();
                isSuccess = true;
                sqlCount = event.getSqlCount();
            } else if (evt instanceof FailedEvent) {
                failedEvent = (FailedEvent) evt;
                fileName = failedEvent.getFileName();
                isSuccess = false;
            }

            Color color = Display.getDefault().getSystemColor(SWT.COLOR_RED);
            for (SqlRunnerProgress po : tableList) {
                if (po.getFileName().equals(fileName)) {
                    long oldSuccessCount = po.getSuccessCount();
                    long oldFailCount = po.getFailCount();

                    po.setElapsedTime(evt.getEventTime() - po.getBeginTime());
                    if (isSuccess) {
                        po.setSuccessCount(oldSuccessCount + sqlCount);
                    } else {
                        po.setFailCount(++oldFailCount);
                        if (po.getFailList().size() < FAILSQLMARKCOUNT) {
                            // add error sql and line number
                            SqlRunnerFailed failedPO =
                                    new SqlRunnerFailed(
                                            failedEvent.getIndex(),
                                            failedEvent.getSql(),
                                            failedEvent.getErrorMessage());
                            po.addFailToList(failedPO);
                        }

                        Table table = tvProgress.getTable();
                        table.getItems()[index].setBackground(color);
                        table.redraw();
                        po.setError(true);
                    }
                    po.setStatus(1);
                    tvProgress.refresh(tableList);
                    break;
                }
                index++;
            }
        }
    }
}
