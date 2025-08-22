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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.DbTransactionList;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.KillTransactionList;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.Transaction;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.utils.ModelUtil.KillTranType;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;

/**
 * Show the transaction info
 *
 * @author robin 2009-3-11
 */
public class TransactionInfoDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(TransactionInfoDialog.class);

    private Table transactionTable;
    private final List<Map<String, String>> transactionListData =
            new ArrayList<Map<String, String>>();
    private TableViewer transactionTableViewer;
    private Composite parentComp = null;
    private CubridDatabase database = null;
    private Label objectIdLabel;
    public static final int KILL_TRANSACTION_ID = 100;
    public static final int REFRESH_ID = 101;
    private boolean isRunning = false;

    private DbTransactionList dbTransactionList;

    private final String USER_DBA = "dba";

    /** @param parentShell */
    public TransactionInfoDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createTitleInfo(composite);
        createTransactionGroup(composite);

        setTitle(Messages.titleTransactionDialog);
        setMessage(Messages.msgTransactionDialog);
        initial();
        return parentComp;
    }

    /**
     * Create Title Info
     *
     * @param composite the parent composite
     */
    private void createTitleInfo(Composite composite) {
        objectIdLabel = new Label(composite, SWT.NONE);
    }

    /**
     * Create transaction group
     *
     * @param composite the parent composite
     */
    private void createTransactionGroup(Composite composite) {
        final Group transactionGroup = new Group(composite, SWT.NONE);
        transactionGroup.setText(Messages.grpTransaction);
        transactionGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(1, true);
        transactionGroup.setLayout(layout);
        final String[] columnNameArr =
                new String[] {
                    Messages.tblColTranInfoTranIndex,
                    Messages.tblColTranInfoUserName,
                    Messages.tblColTranInfoHost,
                    Messages.tblColTranInfoProcessId,
                    Messages.tblColTranInfoProgramName
                };
        transactionTableViewer =
                CommonUITool.createCommonTableViewer(
                        transactionGroup,
                        new TableViewerSorter(),
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));

        transactionTableViewer.setInput(transactionListData);
        transactionTable = transactionTableViewer.getTable();

        Menu menu = new Menu(getShell(), SWT.POP_UP);
        final MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
        itemCopy.setText(Messages.menuKillTransaction);
        itemCopy.addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent event) {
                        int index = transactionTable.getSelectionIndex();
                        transactionTable.getItem(index).getText(3);
                        if (CommonUITool.openConfirmBox(
                                parentComp.getShell(),
                                Messages.bind(
                                        Messages.msgKillOnlyConfirm,
                                        transactionTable.getItem(index).getText(3)))) {
                            killTransaction(
                                    KillTranType.T, transactionTable.getItem(index).getText(0));
                            initial();
                        } else {
                            return;
                        }
                    }
                });
        transactionTable.setMenu(menu);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        // getShell().setSize(550, 530);
        getShell().setText(Messages.titleTransactionDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent, REFRESH_ID, com.cubrid.cubridmanager.ui.common.Messages.btnRefresh, true);

        if (database.getDatabaseInfo() != null
                && database.getDatabaseInfo().getAuthLoginedDbUserInfo() != null
                && database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName() != null
                && USER_DBA.equals(
                        database.getDatabaseInfo()
                                .getAuthLoginedDbUserInfo()
                                .getName()
                                .toLowerCase())) {
            createButton(parent, KILL_TRANSACTION_ID, Messages.killTransactionName, false);
        }

        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When button press,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == KILL_TRANSACTION_ID) {
            int i = transactionTable.getSelectionIndex();
            String pid = transactionTable.getItem(i).getText(3);
            if (i >= 0
                    && dbTransactionList != null
                    && dbTransactionList.getTransationInfo() != null
                    && dbTransactionList.getTransationInfo().getTransactionList() != null
                    && dbTransactionList.getTransationInfo().getTransactionList().size() > i) {
                KillTransactionDialog dlg = new KillTransactionDialog(parentComp.getShell());
                Transaction bean = null;
                for (Transaction t : dbTransactionList.getTransationInfo().getTransactionList()) {
                    if (pid.equals(t.getPid())) {
                        bean = t;
                    }
                }
                dlg.setTransationInfo(bean);
                dlg.setDatabase(database);
                if (dlg.open() == IDialogConstants.CANCEL_ID) {
                    return;
                }
                // if (dlg.open() != IDialogConstants.CANCEL_ID) {
                if (dlg.isSuccess()) { // only when kill successfully refresh the data
                    this.dbTransactionList
                            .getTransationInfo()
                            .setTransactionList(
                                    dlg.getKillTransactionList()
                                            .getTransationInfo()
                                            .getTransactionList());
                }
                initial();
                // }
            }

        } else if (buttonId == REFRESH_ID) {
            loadData(getShell());
            initial();
            return;
        }
        super.buttonPressed(buttonId);
    }

    /** Initial data */
    private void initial() {

        transactionListData.clear();
        if (this.dbTransactionList != null
                && dbTransactionList.getTransationInfo() != null
                && dbTransactionList.getTransationInfo().getTransactionList() != null) {
            for (Transaction bean : dbTransactionList.getTransationInfo().getTransactionList()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("0", bean.getTranindex());
                map.put("1", bean.getUser());
                map.put("2", bean.getHost());
                map.put("3", bean.getPid());
                map.put("4", bean.getProgram());
                transactionListData.add(map);
            }
        }
        transactionTableViewer.refresh();
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            transactionTable.getColumn(i).pack();
        }
        if (database != null) {
            objectIdLabel.setText(Messages.lblActiveTransaction + database.getName());
        }
    }

    /**
     * Get added CubridDatabase
     *
     * @return the CubridDatabase object
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * Set edited CubridDatabase
     *
     * @param database the CubridDatabase object
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * load the init data from the server
     *
     * @param shell the shell
     * @return <code>true</code> if it is valid;<code>false</code>otherwise
     */
    public boolean loadData(Shell shell) {
        CommonQueryTask<DbTransactionList> task =
                new CommonQueryTask<DbTransactionList>(
                        database.getServer().getServerInfo(),
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new DbTransactionList());
        task.setDbName(database.getName());
        DbUserInfo userInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
        if (userInfo != null) {
            task.setDbUser(userInfo.getName() == null ? "" : userInfo.getName());
            task.setDbpasswd(
                    userInfo.getNoEncryptPassword() == null ? "" : userInfo.getNoEncryptPassword());
        }

        execTask(-1, new SocketTask[] {task}, true, shell);
        if (task.getErrorMsg() != null) {
            return false;
        }
        setDbTransactionList(task.getResultModel());
        return true;
    }

    /**
     * Kill the transaction
     *
     * @param type the transaction type
     * @param parameter the parameter
     */
    private void killTransaction(KillTranType type, String parameter) {

        CommonQueryTask<KillTransactionList> task =
                new CommonQueryTask<KillTransactionList>(
                        database.getServer().getServerInfo(),
                        CommonSendMsg.getKillTransactionMSGItems(),
                        new KillTransactionList());
        task.setDbName(database.getName());
        task.setKillTranType(type);
        task.setKillTranParameter(parameter);
        execTask(-1, new SocketTask[] {task}, true, getShell());
        if (task.getErrorMsg() != null) {
            return;
        }
        CommonUITool.openInformationBox(
                parentComp.getShell(), Messages.titleSuccess, Messages.msgKillSuccess);
        KillTransactionList killTransactionList = task.getResultModel();
        DbTransactionList dbDbTransactionList = new DbTransactionList();
        dbDbTransactionList.setTransationInfo(killTransactionList.getTransationInfo());
        setDbTransactionList(dbDbTransactionList);
    }

    /**
     * Execute tasks
     *
     * @param buttonId the button id
     * @param tasks the tasks array
     * @param cancelable whether it is cancelable
     * @param shell the shell
     */
    public void execTask(
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
                                        throws InvocationTargetException,
                                                InterruptedException { // FIXME more simple
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
                                                                CommonUITool.openErrorBox(
                                                                        getShell(), msg);
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

    public DbTransactionList getDbTransactionList() {
        return dbTransactionList;
    }

    public void setDbTransactionList(DbTransactionList dbTransactionList) {
        this.dbTransactionList = dbTransactionList;
    }
}
