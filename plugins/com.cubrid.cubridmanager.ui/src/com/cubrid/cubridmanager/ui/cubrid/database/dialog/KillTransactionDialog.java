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

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.KillTransactionList;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.Transaction;
import com.cubrid.cubridmanager.core.utils.ModelUtil.KillTranType;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * Lock information of detailed
 *
 * @author robin 2009-3-11
 */
public class KillTransactionDialog extends CMTitleAreaDialog {

    private static final Logger LOGGER = LogUtil.getLogger(TransactionInfoDialog.class);
    private Composite parentComp = null;
    private CubridDatabase database = null;
    private Transaction transationInfo;
    private boolean isRunning = false;
    private boolean isSuccess = false;
    private KillTransactionList killTransactionList;
    public static final int KILL_ONE_TRANSACTION_ID = 2001;
    public static final int KILL_USER_TRANSACTION_ID = 2002;
    public static final int KILL_CLIENT_TRANSACTION_ID = 2003;
    public static final int KILL_PROGRAM_TRANSACTION_ID = 2004;
    private org.eclipse.swt.widgets.List killCombo;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public KillTransactionDialog(Shell parentShell) {
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
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.numColumns = 2;
        composite.setLayout(layout);

        final Group tranGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.numColumns = 4;
        final GridData gdDbnameGroup = new GridData(GridData.FILL_BOTH);
        gdDbnameGroup.horizontalSpan = 2;
        tranGroup.setLayoutData(gdDbnameGroup);
        tranGroup.setLayout(layout);
        tranGroup.setText(Messages.grpTransactionInfo);
        {
            final Label parameterNameLabel = new Label(tranGroup, SWT.LEFT | SWT.WRAP);
            parameterNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            parameterNameLabel.setText(Messages.lblTransactionUserName);
            final Text l2 = new Text(tranGroup, SWT.WRAP | SWT.BORDER);
            l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            l2.setText(transationInfo.getUser());
            l2.setEnabled(false);
        }

        {
            final Label parameterNameLabel = new Label(tranGroup, SWT.LEFT | SWT.WRAP);
            parameterNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            parameterNameLabel.setText(Messages.lblTransactionHostName);
            final Text l2 = new Text(tranGroup, SWT.WRAP | SWT.BORDER);
            l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            l2.setText(transationInfo.getHost());
            l2.setEnabled(false);
        }
        {
            final Label parameterNameLabel = new Label(tranGroup, SWT.LEFT | SWT.WRAP);
            parameterNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            parameterNameLabel.setText(Messages.lblTransactionProcessId);
            final Text l2 = new Text(tranGroup, SWT.WRAP | SWT.BORDER);
            l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            l2.setText(transationInfo.getPid());
            l2.setEnabled(false);
        }
        {
            final Label parameterNameLabel = new Label(tranGroup, SWT.LEFT | SWT.WRAP);
            parameterNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            parameterNameLabel.setText(Messages.lblTransactionProgramName);
            final Text l2 = new Text(tranGroup, SWT.WRAP | SWT.BORDER);
            l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            l2.setText(transationInfo.getProgram());
            l2.setEnabled(false);
        }

        final Label label = new Label(composite, SWT.LEFT | SWT.WRAP);
        label.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        label.setText(Messages.lblTransactionKillType);

        killCombo = new org.eclipse.swt.widgets.List(composite, SWT.READ_ONLY | SWT.BORDER);

        killCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        killCombo.add(Messages.itemKillOnly);
        // For CUBRID "killtran", only support kill transaction by os user id, not user from
        // transaction info
        // killCombo.add(Messages.itemKillSameName);
        killCombo.add(Messages.itemKillSameHost);
        killCombo.add(Messages.itemKillSameProgram);
        killCombo.select(0);
        setTitle(Messages.titleKillTransactionDialog);
        setMessage(Messages.msgKillTransactionDialog);

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleKillTransactionDialog);
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
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When press button from button bar,handle with this event
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {

        if (buttonId == IDialogConstants.OK_ID) {
            int index = killCombo.getSelectionIndex();
            switch (index) {
                case 0:
                    if (CommonUITool.openConfirmBox(
                            parentComp.getShell(),
                            Messages.bind(Messages.msgKillOnlyConfirm, transationInfo.getPid()))) {
                        if (CompatibleUtil.isSupportNewKillTranTask(database.getDatabaseInfo())) {
                            // New CMS only support number as parameter when kill transaction by
                            // index
                            String idx = transationInfo.getTranindex();
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(idx);
                            while (matcher.find()) {
                                idx = matcher.group();
                                break;
                            }
                            killTransaction(KillTranType.INDEX, idx);
                        } else {
                            killTransaction(KillTranType.T, transationInfo.getTranindex());
                        }
                        setReturnCode(KILL_ONE_TRANSACTION_ID);
                        close();
                    } else {
                        return;
                    }
                    break;
                    /* For CUBRID "killtran", only support kill transaction by os user id, not user from transaction info
                    * case 1:
                    if (CommonUITool.openConfirmBox(parentComp.getShell(),
                    		Messages.bind(Messages.msgKillSameUserConfirm,
                    				transationInfo.getUser()))) {

                    	killTransaction(KillTranType.U, transationInfo.getUser());
                    	setReturnCode(KILL_USER_TRANSACTION_ID);
                    	close();
                    } else {
                    	return;
                    }
                    break;*/
                case 1:
                    if (CommonUITool.openConfirmBox(
                            parentComp.getShell(),
                            Messages.bind(
                                    Messages.msgKillSameHostConfirm, transationInfo.getHost()))) {
                        killTransaction(KillTranType.H, transationInfo.getHost());
                        setReturnCode(KILL_CLIENT_TRANSACTION_ID);
                        close();
                    } else {
                        return;
                    }
                    break;
                case 2:
                    if (CommonUITool.openConfirmBox(
                            parentComp.getShell(),
                            Messages.bind(
                                    Messages.msgKillSameHostConfirm,
                                    transationInfo.getProgram()))) {
                        if (CompatibleUtil.isSupportNewKillTranTask(database.getDatabaseInfo())) {
                            killTransaction(KillTranType.PROGRAM, transationInfo.getProgram());
                        } else {
                            killTransaction(KillTranType.PG, transationInfo.getProgram());
                        }
                        setReturnCode(KILL_PROGRAM_TRANSACTION_ID);
                        close();
                    } else {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }

        super.buttonPressed(buttonId);
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
        executeKillTrans(-1, new SocketTask[] {task}, true, getShell());
        if (task.getErrorMsg() != null) {
            isSuccess = false;
            return;
        }
        isSuccess = true;
        CommonUITool.openInformationBox(
                parentComp.getShell(), Messages.titleSuccess, Messages.msgKillSuccess);
        killTransactionList = task.getResultModel();
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

    public Transaction getTransationInfo() {
        return transationInfo;
    }

    public void setTransationInfo(Transaction transationInfo) {
        this.transationInfo = transationInfo;
    }

    /**
     * Execute to kill transaction
     *
     * @param buttonId the button id
     * @param tasks the task array
     * @param cancelable whether it can be cancelable
     * @param shell the shell
     */
    public void executeKillTrans(
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

    public KillTransactionList getKillTransactionList() {
        return killTransactionList;
    }

    public void setKillTransactionList(KillTransactionList killTransactionList) {
        this.killTransactionList = killTransactionList;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
