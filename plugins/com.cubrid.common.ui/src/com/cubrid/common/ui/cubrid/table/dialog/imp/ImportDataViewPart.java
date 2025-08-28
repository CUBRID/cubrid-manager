/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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

package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.sqlrunner.dialog.ViewSQLLogDialog;
import com.cubrid.common.ui.common.sqlrunner.model.SqlRunnerFailed;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataTableFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportMonitor;
import com.cubrid.common.ui.cubrid.table.dialog.imp.progress.IImportDataMonitor;
import com.cubrid.common.ui.cubrid.table.dialog.imp.progress.ImportDataProgressManager;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * The Import Data ViewPart
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public class ImportDataViewPart extends EditorPart implements ISaveablePart2 {

    public static final String ID = ImportDataViewPart.class.getName();
    private final int FAILSQLMARKCOUNT = 1000;
    /*The button column index*/
    static final int buttonColumnIndex = 6;

    private ProgressIndicator progressIndicator;
    private TableViewer progressTableViewer;
    private Button btnStop;
    private boolean isStoped = false;
    private Label historyLabel;
    private Text historyText;
    private Button saveButton;
    private Button closeButton;
    private Button openLogFolderButton;

    private IEditorInput input;

    private ImportDataProgressManager manager;
    private IImportDataMonitor importDataMonitor;
    private ImportConfig importConfig = null;
    private CubridDatabase database = null;
    private List<ImportMonitor> tableList = new ArrayList<ImportMonitor>();

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

        progressIndicator = new ProgressIndicator(progressPanel, SWT.NONE);
        progressIndicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

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
                            java.awt.Desktop.getDesktop()
                                    .open(new File(importConfig.getErrorLogFolderPath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        progressTableViewer = new TableViewer(backPanel, SWT.BORDER | SWT.FULL_SELECTION);
        progressTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        progressTableViewer.getTable().setLinesVisible(true);
        progressTableViewer.getTable().setHeaderVisible(true);

        final TableViewerColumn tableName = new TableViewerColumn(progressTableViewer, SWT.NONE);
        final TableViewerColumn totalCount = new TableViewerColumn(progressTableViewer, SWT.NONE);
        final TableViewerColumn parseCount = new TableViewerColumn(progressTableViewer, SWT.NONE);
        final TableViewerColumn failedCount = new TableViewerColumn(progressTableViewer, SWT.NONE);
        final TableViewerColumn status = new TableViewerColumn(progressTableViewer, SWT.NONE);
        final TableViewerColumn elapsedTime = new TableViewerColumn(progressTableViewer, SWT.NONE);
        final TableViewerColumn buttonColumn = new TableViewerColumn(progressTableViewer, SWT.NONE);

        tableName.getColumn().setWidth(250);
        tableName.getColumn().setText(Messages.columnName);

        totalCount.getColumn().setWidth(100);
        totalCount.getColumn().setText(Messages.columnCount);

        parseCount.getColumn().setWidth(100);
        parseCount.getColumn().setText(Messages.columnFinished);

        failedCount.getColumn().setWidth(100);
        failedCount.getColumn().setText(Messages.columnFailed);

        status.getColumn().setWidth(100);
        status.getColumn().setText(Messages.columnStatus);

        elapsedTime.getColumn().setWidth(130);
        elapsedTime.getColumn().setText(Messages.columnTime);

        buttonColumn.getColumn().setWidth(90);

        progressTableViewer.setContentProvider(new TableContentProvider());
        progressTableViewer.setLabelProvider(new TableLabelProvider());
        progressTableViewer.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) event.getSelection();
                        ImportMonitor monitor = (ImportMonitor) selection.getFirstElement();
                        openViewDialog(monitor);
                    }
                });

        Composite historyComposite = new Composite(backPanel, SWT.NONE);
        historyComposite.setLayout(new GridLayout(4, false));
        historyComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        historyLabel = new Label(historyComposite, SWT.NONE);
        historyLabel.setText(Messages.lblHistory);
        historyText = new Text(historyComposite, SWT.BORDER);
        historyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        saveButton = new Button(historyComposite, SWT.NONE);
        saveButton.setText(Messages.btnSaveAndClose);
        saveButton.addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent event) {
                        if (historyText.getText().trim().length() == 0) {
                            CommonUITool.openErrorBox(Messages.errHistoryEmpty);
                            return;
                        }
                        if (ImportConfigManager.getInstance().getConfig(historyText.getText())
                                != null) {
                            CommonUITool.openErrorBox(Messages.errHistoryExist);
                            return;
                        }
                        importConfig.setName(historyText.getText().trim());
                        ImportConfigManager.getInstance().addConfig(importConfig);
                        close();
                    }
                });

        closeButton = new Button(historyComposite, SWT.NONE);
        closeButton.setText(Messages.btnClose);
        closeButton.addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent event) {
                        close();
                    }
                });

        setHistroyWidgetStatus(false);

        init();
    }

    /**
     * Init the editor part
     *
     * @param site IEditorSite
     * @param input editorInput
     * @throws PartInitException when error
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);
        this.importConfig = (ImportConfig) input.getAdapter(ImportConfig.class);
        this.input = input;
        setSite(site);
        setInput(input);
        setTitleToolTip(input.getName());
        setPartName(input.getName());
    }

    /**
     * initManager
     *
     * @return load file result
     */
    public void init() {
        importDataMonitor = new ImportDataMonitor();
        manager =
                new ImportDataProgressManager(
                        importDataMonitor, database, progressIndicator, importConfig);
        if (manager.isInitSuccess()) {
            initTableView();
            manager.startProcess();
        }
    }

    /** intiTableView */
    private void initTableView() {
        /*Init the data*/
        for (TableConfig tableConfig : importConfig.getSelectedMap().values()) {
            ImportMonitor monitor = new ImportMonitor(tableConfig.getName());
            monitor.setTotalCount(tableConfig.getLineCount());
            tableList.add(monitor);
        }
        progressTableViewer.setInput(tableList);
    }

    /**
     * Open view log dialog
     *
     * @param monitor
     */
    private static void openViewDialog(ImportMonitor monitor) {
        new ViewSQLLogDialog(
                        Display.getCurrent().getActiveShell(),
                        monitor.getTableName(),
                        monitor.getFailList())
                .open();
    }
    /** process stop logic */
    public void processStop() {
        if (manager != null) {
            if (!btnStop.isDisposed()) {
                btnStop.setEnabled(false);
                progressIndicator.done();
                for (ImportMonitor monitor : tableList) {
                    if (monitor.getStatus() == 1) {
                        monitor.setStatus(ImportMonitor.STATUS_STOPED);
                    }
                }
                progressTableViewer.refresh(tableList);
            }
            isStoped = true;
            manager.stopProcess();
        }

        if (importDataMonitor.hasError() && !openLogFolderButton.isDisposed()) {
            openLogFolderButton.setEnabled(true);
        }
        setHistroyWidgetStatus(true);
        saveButton.forceFocus();
    }

    public void dispose() {
        if (!isStoped) {
            isStoped = true;
            manager.stopProcess();
        }
        super.dispose();
    }

    /** dispose */
    public void close() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        ImportDataViewPart editor = (ImportDataViewPart) page.findEditor(input);
        if (editor != null) {
            page.activate(editor);
            page.closeEditor(editor, false);
        }
    }

    private void setHistroyWidgetStatus(boolean isEnable) {
        historyLabel.setEnabled(isEnable);
        historyText.setEnabled(isEnable);
        saveButton.setEnabled(isEnable);
        closeButton.setEnabled(isEnable);
        if (isEnable) {
            String dateStringNow = DateUtil.getDatetimeStringOnNow("MM/dd HH:mm");
            String databaseName = database.getDatabaseInfo().getDbName();
            String historyName =
                    Messages.bind(Messages.defaultImportHistoryName, databaseName, dateStringNow);
            historyText.setText(historyName);
            saveButton.forceFocus();
        }
    }

    public int promptToSaveOnClose() {
        return 0;
    }

    public void doSave(IProgressMonitor monitor) {}

    public void doSaveAs() {}

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void setFocus() {}

    private class ImportDataMonitor implements IImportDataMonitor {
        private volatile List<ImportDataEvent> eventCache = new ArrayList<ImportDataEvent>();
        private volatile boolean isHasError = false;;
        private volatile Map<ImportMonitor, Button> monitorToButtonMap =
                new HashMap<ImportMonitor, Button>();
        /**
         * Event found
         *
         * @param event MigrationEvent
         */
        public void addEvent(final ImportDataEvent event) {
            if (isStoped) {
                return;
            }
            // Add a cache to make the UI refresh per second.
            eventCache.add(event);
            flushUI();
        }

        /** Write events to UI */
        public void flushUI() {
            final List<ImportDataEvent> ec = new ArrayList<ImportDataEvent>(eventCache);
            eventCache.clear();
            Thread t =
                    new Thread() {
                        public void run() {
                            for (ImportDataEvent evt : ec) {
                                if (isStoped) {
                                    return;
                                }
                                int pbvalue = 0;
                                if (evt instanceof ImportDataBeginOneTableEvent) {
                                    updateOneTableData((ImportDataBeginOneTableEvent) evt);
                                } else if (evt instanceof ImportDataSuccessEvent) {
                                    ImportDataSuccessEvent successEvent =
                                            (ImportDataSuccessEvent) evt;
                                    if (importConfig.getImportType()
                                            == ImportConfig.IMPORT_FROM_SQL) {
                                        pbvalue += successEvent.getWorkedSize();
                                    } else {
                                        pbvalue += successEvent.getWorkedCount();
                                    }
                                    updateTableData(successEvent);
                                } else if (evt instanceof ImportDataFailedEvent) {
                                    ImportDataFailedEvent failedEvent = (ImportDataFailedEvent) evt;
                                    if (importConfig.getImportType()
                                            == ImportConfig.IMPORT_FROM_SQL) {
                                        pbvalue += failedEvent.getWorkedSize();
                                    } else {
                                        pbvalue += failedEvent.getFailedCount();
                                    }
                                    isHasError = true;
                                    updateTableData(failedEvent);
                                } else if (evt instanceof ImportDataTableFailedEvent) {
                                    ImportDataTableFailedEvent failedEvent =
                                            (ImportDataTableFailedEvent) evt;
                                    updateOneTableData(failedEvent);
                                } else if (evt instanceof ImportDataFinishOneTableEvent) {
                                    updateOneTableData((ImportDataFinishOneTableEvent) evt);
                                }
                                if (evt instanceof ImportDataFinishAllTableEvent) {
                                    btnStop.setEnabled(false);
                                    progressIndicator.done();
                                    if (isHasError) {
                                        openLogFolderButton.setEnabled(true);
                                    }

                                    setHistroyWidgetStatus(true);
                                    saveButton.forceFocus();
                                    return;
                                }
                                // Sleep a moment or the progress bar will not change
                                // the status.
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException ex) {

                                }
                                if (pbvalue > 0) {
                                    progressIndicator.worked(pbvalue);
                                }
                            }
                        }
                    };
            Display.getDefault().asyncExec(t);
        }

        /**
         * when one file begin ,update table
         *
         * @param evt BeginOneFileEvent
         */
        private void updateOneTableData(ImportDataBeginOneTableEvent evt) {
            if (isStoped) {
                return;
            }
            String tableName = evt.getTableName();
            for (int i = 0; i < tableList.size(); i++) {
                ImportMonitor monitor = tableList.get(i);
                if (monitor.getTableName().equals(tableName)) {
                    monitor.setBeginTime(evt.getEventTime());
                    monitor.setStatus(ImportMonitor.STATUS_RUNNING);
                    progressTableViewer.refresh(tableList);
                    break;
                }
            }
        }

        /**
         * when one file finish ,update table
         *
         * @param evt FinishOneFileEvent
         */
        private void updateOneTableData(ImportDataFinishOneTableEvent evt) {
            if (isStoped) {
                return;
            }
            String tableName = evt.getTableName();
            for (int i = 0; i < tableList.size(); i++) {
                ImportMonitor monitor = tableList.get(i);
                if (monitor.getTableName().equals(tableName)) {
                    monitor.setStatus(ImportMonitor.STATUS_FINISHED);
                    monitor.setElapsedTime(evt.getEventTime() - monitor.getBeginTime());
                    progressTableViewer.refresh(tableList);
                    break;
                }
            }
        }

        /**
         * when one file finish ,update table
         *
         * @param evt ImportDataTableFailedEvent
         */
        private void updateOneTableData(ImportDataTableFailedEvent evt) {
            if (isStoped) {
                return;
            }
            String tableName = evt.getTableName();
            for (ImportMonitor monitor : tableList) {
                if (monitor.getTableName().equals(tableName)) {
                    long finishedCount = monitor.getParseCount();
                    long totalCount = monitor.getTotalCount();
                    if (totalCount > finishedCount) {
                        monitor.setFailedCount(totalCount - finishedCount);
                    }
                    monitor.setElapsedTime(evt.getEventTime() - monitor.getBeginTime());

                    monitor.setStatus(ImportMonitor.STATUS_RUNNING);
                    progressTableViewer.refresh(tableList);
                    break;
                }
            }
        }

        /**
         * @param evt parse event
         * @return sql byte
         */
        private void updateTableData(ImportDataEvent evt) {
            if (isStoped) {
                return;
            }

            String tableName = "";
            int workedCount = 1;
            int failedCount = 1;
            if (evt instanceof ImportDataSuccessEvent) {
                ImportDataSuccessEvent event = (ImportDataSuccessEvent) evt;
                tableName = event.getTableName();
                workedCount = event.getWorkedCount();
                for (ImportMonitor monitor : tableList) {
                    if (monitor.getTableName().equals(tableName)) {
                        monitor.setElapsedTime(evt.getEventTime() - monitor.getBeginTime());
                        monitor.setParseCount(monitor.getParseCount() + workedCount);
                        monitor.setStatus(ImportMonitor.STATUS_RUNNING);
                        progressTableViewer.refresh(tableList);
                        break;
                    }
                }
            }

            if (evt instanceof ImportDataFailedEvent) {
                ImportDataFailedEvent event = (ImportDataFailedEvent) evt;
                tableName = event.getTableName();
                failedCount = event.getFailedCount();
                for (ImportMonitor monitor : tableList) {
                    if (monitor.getTableName().equals(tableName)) {
                        monitor.setElapsedTime(evt.getEventTime() - monitor.getBeginTime());
                        monitor.setFailedCount(monitor.getFailedCount() + failedCount);
                        monitor.setStatus(ImportMonitor.STATUS_RUNNING);
                        progressTableViewer.refresh(tableList);

                        ImportDataFailedEvent failedEvent = (ImportDataFailedEvent) evt;
                        if (monitor.getFailList().size() < FAILSQLMARKCOUNT) {
                            // add error sql and line number
                            SqlRunnerFailed failedPO =
                                    new SqlRunnerFailed(
                                            monitor.getFailList().size() + 1,
                                            failedEvent.getSql(),
                                            failedEvent.getErrorMsg());
                            monitor.addFailToList(failedPO);
                        }

                        Button openViewButton = monitorToButtonMap.get(monitor);
                        if (openViewButton == null) {
                            createViewButton(progressTableViewer.getTable(), monitor);
                        }
                        break;
                    }
                }
            }
        }

        private void createViewButton(Table table, final ImportMonitor monitor) {
            TableItem tableItem = null;
            for (TableItem item : table.getItems()) {
                if (monitor.equals(item.getData())) {
                    tableItem = item;
                    break;
                }
            }

            if (tableItem != null) {
                Button button = new Button(table, SWT.None);
                button.setText(Messages.btnViewErrorLog);
                button.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent e) {
                                openViewDialog(monitor);
                            }
                        });
                monitorToButtonMap.put(monitor, button);
                TableEditor editor = new TableEditor(table);
                editor.grabHorizontal = true;
                editor.grabVertical = true;
                editor.setEditor(button, tableItem, buttonColumnIndex);
                editor.layout();
            }
        }

        public void finished() {
            Display.getDefault()
                    .asyncExec(
                            new Runnable() {
                                public void run() {
                                    if (!btnStop.isDisposed()) {
                                        btnStop.setEnabled(false);
                                        progressIndicator.done();
                                    }
                                }
                            });
        }

        public boolean hasError() {
            return isHasError;
        }
    }
}

/**
 * table label provider
 *
 * @author Kevin.Wang
 */
class TableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
    private SimpleDateFormat formater = new SimpleDateFormat("mm:ss.SSS");

    /**
     * Default return null
     *
     * @param element to be display.
     * @param columnIndex is the index of column. Begin with 0.
     * @return null
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /**
     * Retrieves the column's text by column index
     *
     * @param element to be displayed.
     * @param columnIndex is the index of column. Begin with 0.
     * @return String to be filled in the column.
     */
    public String getColumnText(Object element, int columnIndex) {
        ImportMonitor p = (ImportMonitor) element;
        switch (columnIndex) {
            case 0:
                return p.getTableName();
            case 1:
                if (p.getTotalCount() == -1) {
                    return "-";
                }
                return Long.toString(p.getTotalCount());
            case 2:
                return Long.toString(p.getParseCount());
            case 3:
                return Long.toString(p.getFailedCount());
            case 4:
                if (p.getStatus() == ImportMonitor.STATUS_FINISHED) {
                    return Messages.runSQLStatusFinished;
                } else if (p.getStatus() == ImportMonitor.STATUS_RUNNING) {
                    return Messages.runSQLStatusRunning;
                } else if (p.getStatus() == ImportMonitor.STATUS_STOPED) {
                    return Messages.runSQLStatusStopped;
                }
                return Messages.runSQLStatusWaiting;
            case 5:
                return getElapsedTimeString(p.getElapsedTime());
            default:
                return null;
        }
    }

    /**
     * get elapseTime "hh:mm:ss.SSS"
     *
     * @param elapsedTime
     * @return
     */
    public String getElapsedTimeString(Long elapsedTime) {
        String hourString = "";
        long hour = elapsedTime / (3600 * 1000);
        if (hour == 0) {
            hourString = "00";
        } else if (hour < 10) {
            hourString = "0" + Long.valueOf(hour);
        } else {
            hourString = Long.toString(hour);
        }
        return hourString + ":" + formater.format(elapsedTime);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang
     * .Object, int)
     */
    public Color getForeground(Object element, int columnIndex) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang
     * .Object, int)
     */
    public Color getBackground(Object element, int columnIndex) {
        ImportMonitor monitor = (ImportMonitor) element;
        if (columnIndex >= ImportDataViewPart.buttonColumnIndex) {
            return null;
        }

        if (monitor.getStatus() == ImportMonitor.STATUS_FINISHED) {
            if (monitor.getFailedCount() == 0) {
                return ResourceManager.getColor(SWT.COLOR_GREEN);
            } else {
                return ResourceManager.getColor(SWT.COLOR_RED);
            }
        }
        return null;
    }
}

class TableContentProvider implements IStructuredContentProvider {
    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            List<ImportMonitor> list = (List<ImportMonitor>) inputElement;
            ImportMonitor[] nodeArr = new ImportMonitor[list.size()];
            return list.toArray(nodeArr);
        }

        return new Object[] {};
    }
}
