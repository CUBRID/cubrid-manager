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
package com.cubrid.cubridmanager.ui.mondashboard.dialog;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.logs.model.DbLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.DbLogInfos;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.ui.logs.action.LogViewAction;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * Database log list dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-18 created by pangqiren
 */
public class DatabaseLogListDialog extends CMTitleAreaDialog implements ITaskExecutorInterceptor {

    private TableViewer dbLogTableViewer;
    private Table dbLogTable;
    private final String dbName;
    private final ServerInfo serverInfo;
    private final List<Map<String, Object>> dbLogList = new ArrayList<Map<String, Object>>();

    /**
     * The constructor
     *
     * @param parentShell
     * @param dbName
     * @param serverInfo
     */
    public DatabaseLogListDialog(Shell parentShell, String dbName, ServerInfo serverInfo) {
        super(parentShell);
        this.dbName = dbName;
        this.serverInfo = serverInfo;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        {
            GridLayout compLayout = new GridLayout();
            compLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            compLayout.marginWidth =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            compLayout.verticalSpacing =
                    convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            compLayout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(compLayout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        createTable(composite);

        setTitle(Messages.titleDbLogListDialog);
        setMessage(Messages.msgDbLogListDialog);

        initial();

        return parentComp;
    }

    /**
     * Create table area
     *
     * @param parent the parent composite
     */
    private void createTable(Composite parent) {

        Label tipLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        tipLabel.setText(Messages.lblDbLogListInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        tipLabel.setLayoutData(gridData);

        final String[] columnNameArr = new String[] {Messages.colDbLog};

        dbLogTableViewer =
                CommonUITool.createCommonTableViewer(
                        parent,
                        new LogTableViewerSorter(),
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));
        dbLogTable = dbLogTableViewer.getTable();
        for (int i = 0; i < dbLogTable.getColumnCount(); i++) {
            dbLogTable.getColumn(i).pack();
        }

        dbLogTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        getButton(IDialogConstants.OK_ID)
                                .setEnabled(dbLogTable.getSelectionCount() > 0);
                    }
                });
        dbLogTableViewer.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        openLogEditor();
                    }
                });
        dbLogTableViewer.setInput(dbLogList);
    }

    /** Initial the value of dialog field */
    private void initial() {
        DbLogInfos dbLogInfos = new DbLogInfos();
        final CommonQueryTask<DbLogInfos> task =
                new CommonQueryTask<DbLogInfos>(
                        serverInfo, CommonSendMsg.getCommonDatabaseSendMsg(), dbLogInfos);
        task.setDbName(dbName);
        CommonTaskJobExec jobExecutor =
                new CommonTaskJobExec(this) {
                    /** Close the dialog */
                    protected void closeDialog() {
                        // empty
                    }

                    /**
                     * Set the dialog visible or invisible.
                     *
                     * @param flag whether it is visible
                     */
                    protected void setDialogVisible(boolean flag) {
                        // empty
                    }
                };
        jobExecutor.addTask(task);

        JobFamily jobFamily = new JobFamily();
        String serverName = serverInfo.getServerName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        jobExecutor.schedule(Messages.jobGetLogList, jobFamily, false, Job.SHORT);
    }

    /**
     * Call this method when button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            openLogEditor();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /** Open the log editor and show the selected log */
    @SuppressWarnings("unchecked")
    protected void openLogEditor() {
        StructuredSelection selection = (StructuredSelection) dbLogTableViewer.getSelection();
        if (selection == null || selection.isEmpty()) {
            return;
        }
        Map<String, Object> map = (Map<String, Object>) selection.getFirstElement();
        LogInfo logInfo = (LogInfo) map.get("1");

        CubridServer server =
                new CubridServer(
                        serverInfo.getHostAddress(), serverInfo.getHostAddress(), null, null);
        server.setServerInfo(serverInfo);

        DefaultCubridNode dbLogInfoNode =
                new DefaultCubridNode(
                        dbName + ICubridNodeLoader.NODE_SEPARATOR + "database_log",
                        logInfo.getName(),
                        "icons/navigator/log_item.png");
        dbLogInfoNode.setType(CubridNodeType.LOGS_SERVER_DATABASE_LOG);
        dbLogInfoNode.setModelObj(logInfo);
        dbLogInfoNode.setEditorId(LogEditorPart.ID);
        dbLogInfoNode.setContainer(false);
        dbLogInfoNode.setServer(server);

        LogViewAction action =
                (LogViewAction) ActionManager.getInstance().getAction(LogViewAction.ID);
        action.setCubridNode(dbLogInfoNode);
        action.run();
        super.buttonPressed(IDialogConstants.OK_ID);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText("Select Database Log");
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * After a task has been executed, do some thing such as refresh.
     *
     * @param task the task
     * @return IStatus if complete refresh false if run into error
     */
    @SuppressWarnings("unchecked")
    public IStatus postTaskFinished(ITask task) {
        if (task instanceof CommonQueryTask) {
            CommonQueryTask<DbLogInfos> getLogListTask = (CommonQueryTask<DbLogInfos>) task;
            DbLogInfos dbLogInfos = getLogListTask.getResultModel();
            DbLogInfoList dbLogInfoList = dbLogInfos == null ? null : dbLogInfos.getDbLogInfoList();
            List<LogInfo> logInfoList =
                    dbLogInfoList == null ? null : dbLogInfoList.getDbLogInfoList();
            for (int i = 0; logInfoList != null && i < logInfoList.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                LogInfo logInfo = logInfoList.get(i);
                map.put("0", logInfo.getName());
                map.put("1", logInfo);
                dbLogList.add(map);
            }
            if (dbLogTable != null && !dbLogTable.isDisposed()) {
                for (int i = 0; i < dbLogTable.getColumnCount(); i++) {
                    dbLogTable.getColumn(i).pack();
                }
                dbLogTableViewer.refresh();
            }
        }
        return Status.OK_STATUS;
    }

    /** Do something at the end of TaskJobExec */
    public void completeAll() {
        // empty
    }

    /**
     * Logs sorter
     *
     * @author pangqiren
     * @version 1.0 - 2009-6-4 created by pangqiren
     */
    static class LogTableViewerSorter extends TableViewerSorter {
        /**
         * Compare the object
         *
         * @param viewer the viewer
         * @param e1 the object1
         * @param e2 the object2
         * @return <code>1<code> e1>e2;<code>0</code> e1==e2;<code>-1</code>
         *         e1<e2
         */
        @SuppressWarnings("rawtypes")
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (!(e1 instanceof Map) || !(e2 instanceof Map)) {
                return 0;
            }
            int rc = 0;
            Map map1 = (Map) e1;
            Map map2 = (Map) e2;
            String logName1 = (String) map1.get("0");
            String logName2 = (String) map2.get("0");

            String[] dateArr1 = logName1.split("\\.")[0].split("_");
            String[] dateArr2 = logName2.split("\\.")[0].split("_");
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd hhmm", Locale.getDefault());
            int length = dateArr1.length;
            if (length > 2 && dateArr2.length == length) {
                String str1 = dateArr1[length - 2] + " " + dateArr1[length - 1];
                String str2 = dateArr2[length - 2] + " " + dateArr2[length - 1];
                try {
                    Date date1 = dateFormat.parse(str1);
                    Date date2 = dateFormat.parse(str2);
                    rc = date1.compareTo(date2);
                } catch (ParseException e) {
                    rc = 0;
                }
            }
            // If descending order, flip the direction
            if (direction == DESCENDING) {
                rc = -rc;
            }
            return rc;
        }
    }
}
