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

package com.cubrid.common.ui.cubrid.table.progress;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.exp.ExportMonitor;
import com.cubrid.common.ui.cubrid.table.event.ExportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * Export Query Result Data ViewPart
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-31 created by Kevin.Wang
 */
public class ExportQueryResultDataViewPart extends EditorPart
        implements ISaveablePart2, IExportDataMonitor {

    public static final String ID = ExportQueryResultDataViewPart.class.getName();
    private ProgressIndicator progressIndicator;
    private TableViewer tvProgress;
    private Button btnStop;
    private boolean stop = false;

    private IExportDataProcessManager manager;
    private ExportConfig exportConfig = null;
    private IEditorInput input;
    private CubridDatabase database = null;
    private List<ExportMonitor> tableList = new ArrayList<ExportMonitor>();

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
        progressPanel.setLayout(new GridLayout(2, false));
        progressPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        progressIndicator = new ProgressIndicator(progressPanel, SWT.NONE);
        progressIndicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        btnStop = new Button(progressPanel, SWT.NONE);
        btnStop.setText(com.cubrid.common.ui.common.Messages.btnStop);
        btnStop.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run_stop.png"));
        btnStop.addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent event) {
                        processStop();
                    }
                });
        tvProgress = new TableViewer(backPanel, SWT.BORDER | SWT.FULL_SELECTION);
        tvProgress.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        tvProgress.getTable().setLinesVisible(true);
        tvProgress.getTable().setHeaderVisible(true);

        final TableViewerColumn tableName = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn totalCount = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn parseCount = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn status = new TableViewerColumn(tvProgress, SWT.NONE);
        final TableViewerColumn elapsedTime = new TableViewerColumn(tvProgress, SWT.NONE);

        tableName.getColumn().setWidth(250);
        tableName.getColumn().setText(Messages.exportMonitorPartColumnFileName);

        totalCount.getColumn().setWidth(100);
        totalCount.getColumn().setText(Messages.exportMonitorPartColumnTotalcount);

        parseCount.getColumn().setWidth(100);
        parseCount.getColumn().setText(Messages.exportMonitorPartColumnParsecount);

        status.getColumn().setWidth(100);
        status.getColumn().setText(com.cubrid.common.ui.common.Messages.columnStatus);

        elapsedTime.getColumn().setWidth(130);
        elapsedTime.getColumn().setText(com.cubrid.common.ui.common.Messages.columnElapsedTime);

        tvProgress.setContentProvider(new TableContentProvider());
        tvProgress.setLabelProvider(new TableLabelProvider());

        init();
    }

    public void dispose() {
        super.dispose();
        if (!stop) {
            processStop();
        }
    }

    /** dispose */
    public void close() {
        super.dispose();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        ExportQueryResultDataViewPart editor =
                (ExportQueryResultDataViewPart) page.findEditor(input);
        if (editor != null) {
            page.activate(editor);
            page.closeEditor(editor, false);
        }
    }

    /** process stop logic */
    public void processStop() {
        if (manager != null) {
            if (!btnStop.isDisposed()) {
                btnStop.setEnabled(false);
                progressIndicator.done();
                for (ExportMonitor po : tableList) {
                    if (po.getStatus() == ExportMonitor.STATUS_RUNNING) {
                        po.setStatus(ExportMonitor.STATUS_STOPED);
                    }
                }
                tvProgress.refresh(tableList);
            }
            stop = true;
            manager.stopProcess();
        }
    }

    /**
     * Init the editor part
     *
     * @param site IEditorSite
     * @param input editorInput
     * @throws PartInitException when error
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        ExportDataEditorInput exportDataEditorInput = (ExportDataEditorInput) input;
        this.database = exportDataEditorInput.getDatabase();
        this.exportConfig = exportDataEditorInput.getExportConfig();
        this.input = input;
        setSite(site);
        setInput(input);
        setTitleToolTip(input.getName());
        setTitleImage(CommonUIPlugin.getImage("icons/action/table_data_export.png"));
        setPartName(input.getName());
    }

    /**
     * initManager
     *
     * @return load file result
     */
    public void init() {
        manager =
                new CommonExportDataProcessManager(
                        database.getDatabaseInfo(), exportConfig, this, progressIndicator);
        if (manager.isInitSuccess()) {
            initTableView();
            manager.startProcess();
        }
    }

    /** intiTableView */
    private void initTableView() {
        if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_FILE) {
            if (exportConfig.isExportSchema()) {
                ExportMonitor po = new ExportMonitor(ExportConfig.TASK_NAME_SCHEMA);
                po.setTotalCount(1);
                tableList.add(po);

                if (exportConfig.isExportIndex()) {
                    ExportMonitor indexPo = new ExportMonitor(ExportConfig.TASK_NAME_INDEX);
                    indexPo.setTotalCount(1);
                    tableList.add(indexPo);
                }
                if (exportConfig.isExportSerial()) {
                    ExportMonitor serialPo = new ExportMonitor(ExportConfig.TASK_NAME_SERIAL);
                    serialPo.setTotalCount(1);
                    tableList.add(serialPo);
                }
                if (exportConfig.isExportView()) {
                    ExportMonitor viewPo = new ExportMonitor(ExportConfig.TASK_NAME_VIEW);
                    viewPo.setTotalCount(1);
                    tableList.add(viewPo);
                }
            }
            if (exportConfig.isExportData()) {
                for (String table : exportConfig.getTableNameList()) {
                    ExportMonitor po = new ExportMonitor(table);
                    po.setTotalCount(exportConfig.getTotalCount(table));
                    tableList.add(po);
                }

                for (String sqlName : exportConfig.getSQLNameList()) {
                    ExportMonitor po = new ExportMonitor(sqlName);
                    po.setTotalCount(exportConfig.getTotalCount(sqlName));
                    tableList.add(po);
                }
            }
        }
        //
        //		else if (exportConfig.getExportType() == IExportConfig.EXPORT_TO_LOADDB) {
        //			if (exportConfig.isExportIndex()) {
        //				ExportMonitor indexPo = new ExportMonitor(
        //						exportConfig.getDataFilePath(IExportConfig.LOADDB_INDEXFILEKEY));
        //				indexPo.setTotalCount(1);
        //				tableList.add(indexPo);
        //			}
        //			if (exportConfig.isExportSchema()) {
        //				ExportMonitor schemaPo = new ExportMonitor(
        //						exportConfig.getDataFilePath(IExportConfig.LOADDB_SCHEMAFILEKEY));
        //				schemaPo.setTotalCount(1);
        //				tableList.add(schemaPo);
        //			}
        //
        //			if (exportConfig.isExportData()) {
        //				ExportMonitor po = new ExportMonitor(
        //						exportConfig.getDataFilePath(IExportConfig.LOADDB_DATAFILEKEY));
        //				po.setTotalCount(exportConfig.getTotalCount(IExportConfig.LOADDB_DATAFILEKEY));
        //				tableList.add(po);
        //			}
        //		}

        tvProgress.setInput(tableList);
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

    public void finished() {
        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                progressIndicator.done();
                            }
                        });
    }

    public void addEvent(ExportDataEvent event) {
        if (stop) {
            return;
        }
        flushUI(event);
    }

    /** Write events to UI */
    public void flushUI(final ExportDataEvent event) {

        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                if (stop) {
                                    return;
                                }
                                int pbvalue = 0;
                                if (event instanceof ExportDataBeginOneTableEvent) {
                                    updateOneTableData((ExportDataBeginOneTableEvent) event);
                                } else if (event instanceof ExportDataSuccessEvent) {
                                    updateTableData(event);
                                    pbvalue += ((ExportDataSuccessEvent) event).getSuccessCount();
                                } else if (event instanceof ExportDataFinishOneTableEvent) {
                                    updateOneTableData((ExportDataFinishOneTableEvent) event);
                                } else if (event instanceof ExportDataFailedOneTableEvent) {
                                    updateOneTableData((ExportDataFailedOneTableEvent) event);
                                }
                                if (event instanceof ExportDataFinishAllTableEvent) {
                                    btnStop.setEnabled(false);
                                    progressIndicator.done();
                                    return;
                                }
                                if (pbvalue > 0) {
                                    progressIndicator.worked(pbvalue);
                                }
                            }
                        });
    }

    /**
     * when one file begin, update table
     *
     * @param evt BeginOneFileEvent
     */
    private void updateOneTableData(ExportDataBeginOneTableEvent evt) {
        if (stop) {
            return;
        }
        String tableName = evt.getTableName();
        for (int i = 0; i < tableList.size(); i++) {
            ExportMonitor po = tableList.get(i);
            if (po.getTableName().equals(tableName)) {
                po.setBeginTime(evt.getEventTime());
                po.setStatus(ExportMonitor.STATUS_RUNNING);
                tvProgress.refresh(tableList);
                break;
            }
        }
    }

    /**
     * when one file begin, update table
     *
     * @param evt BeginOneFileEvent
     */
    private void updateOneTableData(ExportDataFailedOneTableEvent evt) {
        if (stop) {
            return;
        }
        String tableName = evt.getTableName();
        for (int i = 0; i < tableList.size(); i++) {
            ExportMonitor po = tableList.get(i);
            if (po.getTableName().equals(tableName)) {
                po.setBeginTime(evt.getEventTime());
                po.setStatus(ExportMonitor.STATUS_FAILED);
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
    private void updateOneTableData(ExportDataFinishOneTableEvent evt) {
        if (stop) {
            return;
        }
        String tableName = evt.getTableName();
        for (int i = 0; i < tableList.size(); i++) {
            ExportMonitor po = tableList.get(i);
            if (po.getTableName().equals(tableName)) {
                po.setStatus(ExportMonitor.STATUS_FINISHED);
                po.setElapsedTime(evt.getEventTime() - po.getBeginTime());
                tvProgress.refresh(tableList);
                break;
            }
        }
    }

    private void updateTableData(ExportDataEvent evt) {
        if (stop) {
            return;
        }

        String tableName = "";
        int successCount = 1;
        if (evt instanceof ExportDataSuccessEvent) {
            ExportDataSuccessEvent event = (ExportDataSuccessEvent) evt;
            tableName = event.getTableName();
            successCount = event.getSuccessCount();
        }
        for (ExportMonitor po : tableList) {
            if (po.getTableName().equals(tableName)) {
                long oldParseCount = po.getParseCount();

                po.setElapsedTime(evt.getEventTime() - po.getBeginTime());
                po.setParseCount(oldParseCount + successCount);
                po.setStatus(ExportMonitor.STATUS_RUNNING);
                tvProgress.refresh(tableList);
                break;
            }
        }
    }
}

class TableLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {
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
        ExportMonitor monitor = (ExportMonitor) element;
        switch (columnIndex) {
            case 0:
                return monitor.getTableName();
            case 1:
                if (monitor.getTotalCount() >= 0) {
                    return Long.toString(monitor.getTotalCount());
                }
                return "-";
            case 2:
                if (monitor.getParseCount() >= 0) {
                    return Long.toString(monitor.getParseCount());
                }
                return "-";
            case 3:
                if (monitor.getStatus() == ExportMonitor.STATUS_FINISHED) {
                    return com.cubrid.common.ui.common.Messages.runSQLStatusFinished;
                } else if (monitor.getStatus() == ExportMonitor.STATUS_RUNNING) {
                    return com.cubrid.common.ui.common.Messages.runSQLStatusRunning;
                } else if (monitor.getStatus() == ExportMonitor.STATUS_STOPED) {
                    return com.cubrid.common.ui.common.Messages.runSQLStatusStopped;
                } else if (monitor.getStatus() == ExportMonitor.STATUS_FAILED) {
                    return com.cubrid.common.ui.common.Messages.runSQLStatusFailed;
                }
                return com.cubrid.common.ui.common.Messages.runSQLStatusWaiting;
            case 4:
                return getElapsedTimeString(monitor.getElapsedTime());
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

    public Color getBackground(Object element) {
        ExportMonitor monitor = (ExportMonitor) element;
        if (monitor.getStatus() == ExportMonitor.STATUS_FINISHED) {
            return ResourceManager.getColor(SWT.COLOR_GREEN);
        }
        if (monitor.getStatus() == ExportMonitor.STATUS_FAILED) {
            return ResourceManager.getColor(SWT.COLOR_RED);
        }

        return null;
    }

    public Color getForeground(Object element) {
        return null;
    }
}

class TableContentProvider implements IStructuredContentProvider {
    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @SuppressWarnings("all")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            List<ExportMonitor> list = (List<ExportMonitor>) inputElement;
            ExportMonitor[] nodeArr = new ExportMonitor[list.size()];
            return list.toArray(nodeArr);
        }

        return new Object[] {};
    }
}
