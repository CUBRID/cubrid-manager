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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLog;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLogList;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.TransactionInfoDialog;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;

/**
 * Back up error log dialog
 *
 * @author robin 2009-3-11
 */
public class BackupErrLogDialog extends CMTitleAreaDialog {

    private static final Logger LOGGER = LogUtil.getLogger(TransactionInfoDialog.class);

    private Table errorLogsTable;
    private DbLotEntry dbLotEntry;
    private BackUpErrorLogList errorLogList = new BackUpErrorLogList();
    private boolean isRunning = false;
    private CubridDatabase database;
    private TableViewer tableViewer = null;
    private List<Map<String, Object>> errorLogsInfoTableList;

    private static final String ERROR_DESCRIPTION = Messages.backupLogDesc;
    private static final String ERROR_TIME = Messages.backupLogTime;
    private static final String BACKUP_ID = Messages.backupLogBackid;
    public static final String DATABASE = Messages.backupLogDb;
    private static final String LOCK_HOLDERS_GROUP_NAME = Messages.backupLogLockGroupName;

    /** @param parentShell */
    public BackupErrLogDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create the dialog area
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createLockHoldersGroup(composite);

        setTitle(Messages.backupLogTitle);
        setMessage(Messages.backupLogMessage);
        initial();
        return parentComp;
    }

    /**
     * create the LockHoldersGroup
     *
     * @param composite Composite
     */
    private void createLockHoldersGroup(Composite composite) {
        final Group lockHoldersGroup = new Group(composite, SWT.NONE);
        lockHoldersGroup.setText(LOCK_HOLDERS_GROUP_NAME);
        lockHoldersGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(1, true);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        lockHoldersGroup.setLayout(layout);

        final String[] columnNameArr =
                new String[] {DATABASE, BACKUP_ID, ERROR_TIME, ERROR_DESCRIPTION};
        tableViewer =
                CommonUITool.createCommonTableViewer(
                        lockHoldersGroup,
                        new TableViewerSorter(),
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));
        errorLogsTable = tableViewer.getTable();
        initialTableModel();
        tableViewer.setInput(errorLogsInfoTableList);
        for (int i = 0; i < errorLogsTable.getColumnCount(); i++) {
            errorLogsTable.getColumn(i).pack();
        }
        /* TOOLS-3216 Display the error log with red color */
        for (int i = 0; i < errorLogsTable.getItemCount(); i++) {
            String dbName = (String) errorLogsInfoTableList.get(i).get("0");
            String logDesc = (String) errorLogsInfoTableList.get(i).get("3");
            String startDesc = "backupdb(" + dbName + "): auto job start";
            String sucDesc = "backupdb(" + dbName + "): success";
            if (!startDesc.equals(logDesc) && !sucDesc.equals(logDesc)) {
                errorLogsTable
                        .getItem(i)
                        .setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            }
        }
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setMinimumSize(640, 500);
        getShell().setText(Messages.backupShellTitle);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.RETRY_ID, Messages.btnRefresh, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.RETRY_ID) {
            errorLogList.clear();
            if (loadData(getShell())) {
                initial();
                initialTableModel();
                tableViewer.setInput(errorLogsInfoTableList);
                for (int i = 0; i < errorLogsTable.getColumnCount(); i++) {
                    errorLogsTable.getColumn(i).pack();
                }
                /* TOOLS-3216 Display the error log with red color */
                for (int i = 0; i < errorLogsTable.getItemCount(); i++) {
                    String dbName = (String) errorLogsInfoTableList.get(i).get("0");
                    String logDesc = (String) errorLogsInfoTableList.get(i).get("3");
                    String startDesc = "backupdb(" + dbName + "): auto job start";
                    String sucDesc = "backupdb(" + dbName + "): success";
                    if (!startDesc.equals(logDesc) && !sucDesc.equals(logDesc)) {
                        errorLogsTable
                                .getItem(i)
                                .setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                    }
                }
            }

            return;
        }
        super.buttonPressed(buttonId);
    }

    /** Initial data */
    private void initial() {
        //
    }

    /** initializes table model */
    private void initialTableModel() {
        errorLogsInfoTableList = new ArrayList<Map<String, Object>>();

        for (int i = 0;
                errorLogList != null
                        && errorLogList.getErrorLogList() != null
                        && i < errorLogList.getErrorLogList().size();
                i++) {
            BackUpErrorLog bean = errorLogList.getErrorLogList().get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("0", bean.getDbname());
            map.put("1", bean.getBackupid());
            map.put("2", bean.getError_time());
            map.put("3", bean.getError_desc());
            errorLogsInfoTableList.add(map);
        }
    }

    /**
     * Check the data validation
     *
     * @return boolean
     */
    public boolean valid() {
        return true;
    }

    public DbLotEntry getDbLotEntry() {
        return dbLotEntry;
    }

    public void setDbLotEntry(DbLotEntry dbLotEntry) {
        this.dbLotEntry = dbLotEntry;
    }

    /**
     * load the data
     *
     * @param shell Shell
     * @return boolean
     */
    public boolean loadData(Shell shell) {
        CommonQueryTask<BackUpErrorLogList> task =
                new CommonQueryTask<BackUpErrorLogList>(
                        database.getServer().getServerInfo(),
                        CommonSendMsg.getCommonSimpleSendMsg(),
                        new BackUpErrorLogList());
        connect(-1, new SocketTask[] {task}, true, shell);
        if (task.getErrorMsg() != null) {
            return false;
        }
        List<BackUpErrorLog> allErrLogList = task.getResultModel().getErrorLogList();
        if (allErrLogList != null) {
            for (BackUpErrorLog backUpErrorLog : allErrLogList) {
                if (backUpErrorLog.getDbname().equals(database.getName())) {
                    errorLogList.addError(backUpErrorLog);
                }
            }
        }

        return true;
    }

    /**
     * connect
     *
     * @param buttonId int
     * @param tasks SocketTask[]
     * @param cancelable boolean
     * @param shell Shell
     */
    public void connect(
            final int buttonId, final SocketTask[] tasks, boolean cancelable, Shell shell) {
        final Display display = shell.getDisplay();
        isRunning = false;
        try {
            new ProgressMonitorDialog(getShell())
                    .run(
                            true,
                            cancelable,
                            new IRunnableWithProgress() {
                                public void run(final IProgressMonitor monitor)
                                        throws InvocationTargetException, InterruptedException {
                                    monitor.beginTask(
                                            com.cubrid.common.ui.spi.Messages.msgRunning,
                                            IProgressMonitor.UNKNOWN);

                                    if (monitor.isCanceled()) {
                                        return;
                                    }

                                    isRunning = true;
                                    Thread thread =
                                            new Thread() {
                                                public void run() {
                                                    while (!monitor.isCanceled() && isRunning) {
                                                        try {
                                                            sleep(1);
                                                        } catch (InterruptedException e) {
                                                        }
                                                    }
                                                    if (monitor.isCanceled()) {
                                                        for (SocketTask t : tasks) {
                                                            if (t != null) {
                                                                t.cancel();
                                                            }
                                                        }
                                                    }
                                                }
                                            };
                                    thread.start();
                                    if (monitor.isCanceled()) {
                                        isRunning = false;
                                        return;
                                    }
                                    for (SocketTask task : tasks) {
                                        if (task != null) {
                                            task.execute();
                                            final String msg = task.getErrorMsg();
                                            if (monitor.isCanceled()) {
                                                isRunning = false;
                                                return;
                                            }
                                            if (msg != null
                                                    && msg.length() > 0
                                                    && !monitor.isCanceled()) {
                                                display.syncExec(
                                                        new Runnable() {
                                                            public void run() {
                                                                CommonUITool.openErrorBox(msg);
                                                            }
                                                        });
                                                isRunning = false;
                                                return;
                                            }
                                        }
                                        if (monitor.isCanceled()) {
                                            isRunning = false;
                                            return;
                                        }
                                    }
                                    if (monitor.isCanceled()) {
                                        isRunning = false;
                                        return;
                                    }
                                    if (!monitor.isCanceled()) {
                                        display.syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        if (buttonId > 0) {
                                                            setReturnCode(buttonId);
                                                            close();
                                                        }
                                                    }
                                                });
                                    }
                                    isRunning = false;
                                    monitor.done();
                                }
                            });
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public BackUpErrorLogList getErrorLogList() {
        return errorLogList;
    }

    public void setErrorLogList(BackUpErrorLogList errorLogList) {
        this.errorLogList = errorLogList;
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }
}
