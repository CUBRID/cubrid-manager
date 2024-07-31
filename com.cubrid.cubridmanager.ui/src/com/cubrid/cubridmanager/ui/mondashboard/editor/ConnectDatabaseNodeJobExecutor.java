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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.mondashboard.task.VerifyDbUserPasswordTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.ConnectDatabaseNodeDialog;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

/**
 * Login the database job executor
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-25 created by pangqiren
 */
public class ConnectDatabaseNodeJobExecutor extends TaskJobExecutor {

    private boolean isContinue;
    private final DatabaseNode dbNode;
    private String dbUserName;
    private String dbPassword;
    private final ServerInfo serverInfo;

    /**
     * The constructor
     *
     * @param dbNode
     * @param serverInfo
     */
    public ConnectDatabaseNodeJobExecutor(DatabaseNode dbNode, ServerInfo serverInfo) {
        this.dbNode = dbNode;
        this.serverInfo = serverInfo;
        dbUserName = dbNode.getDbUser();
        dbPassword = dbNode.getDbPassword();
        if (!dbNode.isConnected()) {
            VerifyDbUserPasswordTask verifyDbUserTask = new VerifyDbUserPasswordTask(serverInfo);
            verifyDbUserTask.setDbName(dbNode.getDbName());
            verifyDbUserTask.setDbUser(dbUserName);
            verifyDbUserTask.setDbPassword(dbPassword);
            addTask(verifyDbUserTask);
        }
    }

    /**
     * Execute to connect database
     *
     * @param monitor the IProgressMonitor
     * @return <code>true</code> if successful;<code>false</code>otherwise;
     */
    public IStatus exec(final IProgressMonitor monitor) {
        monitor.subTask(
                Messages.bind(
                        com.cubrid.cubridmanager.ui.mondashboard.Messages.jobConnectDatabase,
                        dbNode.getDbName()));
        // Check this database whether exist
        List<String> realDbNameList = serverInfo.getAllDatabaseList();
        if (!realDbNameList.contains(dbNode.getDbName())) {
            return Status.CANCEL_STATUS;
        }
        for (ITask task : taskList) {
            if (task instanceof VerifyDbUserPasswordTask) {
                IStatus status = connectDatabase((VerifyDbUserPasswordTask) task);
                if (status == Status.CANCEL_STATUS) {
                    return Status.CANCEL_STATUS;
                }
            } else {
                task.execute();
            }
            final String msg = task.getErrorMsg();
            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                return new Status(IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
            }
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
        }
        return Status.OK_STATUS;
    }

    /**
     * Connect database
     *
     * @param task The MonitoringTask
     * @return IStatus
     */
    private IStatus connectDatabase(final VerifyDbUserPasswordTask task) {
        isContinue = true;
        boolean isConnected = false;
        while (!isConnected && isContinue) {
            task.execute();
            if (task.isValidPassword()) {
                isConnected = true;
                return Status.OK_STATUS;
            } else {
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        String msg =
                                                dbNode.getDbName()
                                                        + "@"
                                                        + dbNode.getParent().getIp()
                                                        + ":"
                                                        + dbNode.getParent().getPort();
                                        ConnectDatabaseNodeDialog dialog =
                                                new ConnectDatabaseNodeDialog(null, dbUserName);
                                        dialog.setDescription(
                                                Messages.bind(Messages.msgVerifyPwdDialog, msg));
                                        if (dialog.open() == IDialogConstants.OK_ID) {
                                            dbUserName = dialog.getUserName();
                                            dbPassword = dialog.getPassword();
                                            task.setDbUser(dbUserName);
                                            task.setDbPassword(dbPassword);
                                        } else {
                                            isContinue = false;
                                        }
                                    }
                                });
            }
        }
        if (!isContinue) {
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    /**
     * Notification that the create replication job has completed execution
     *
     * @param event the event details
     */
    public void done(final IJobChangeEvent event) {
        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                done(event.getResult());
                            }
                        });
    }

    /**
     * Finish the task executor async
     *
     * @param status IStatus
     */
    public void finish(final IStatus status) {
        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                done(status);
                            }
                        });
    }

    /**
     * Finish the task executor
     *
     * @param status IStatus
     */
    private void done(final IStatus status) {
        if (status == Status.OK_STATUS) {
            dbNode.setDbUser(dbUserName);
            dbNode.setDbPassword(dbPassword);
            dbNode.setConnected(true);
        } else {
            dbNode.setConnected(false);
        }
        if (status.getSeverity() == IStatus.ERROR && status.getMessage() != null) {
            dbNode.setErrorMsg(status.getMessage());
        }
        dbNode.setConnecting(false);
    }
}
