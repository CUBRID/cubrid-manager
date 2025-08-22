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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDbBackupInfoTask;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.BackupDatabaseDialog;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to backup database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class BackupDatabaseAction extends SelectionAction {
    public static final String ID = BackupDatabaseAction.class.getName();

    public BackupDatabaseAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public BackupDatabaseAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.hasAdminPermission(obj);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ISchemaNode node = (ISchemaNode) obj[0];
        final CubridDatabase database = node.getDatabase();
        final BackupDatabaseDialog dialog = new BackupDatabaseDialog(getShell());
        dialog.setDatabase(database);
        final Shell shell = getShell();
        TaskExecutor taskExcutor =
                new TaskExecutor() {
                    public boolean exec(final IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return false;
                        }
                        monitor.beginTask(
                                Messages.loadDbBackupInfoTaskName, IProgressMonitor.UNKNOWN);
                        for (ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (openErrorBox(shell, msg, monitor)) {
                                return false;
                            }
                            if (monitor.isCanceled()) {
                                return false;
                            }
                            if (task instanceof GetDbBackupInfoTask) {
                                GetDbBackupInfoTask getDbBackupInfoTask =
                                        (GetDbBackupInfoTask) task;
                                dialog.setDbBackupInfo(getDbBackupInfoTask.getDbBackupInfo());
                            } else if (task instanceof GetCubridConfParameterTask) {
                                GetCubridConfParameterTask getCubridConfParameterTask =
                                        (GetCubridConfParameterTask) task;
                                Map<String, Map<String, String>> confParas =
                                        getCubridConfParameterTask.getConfParameters();
                                Map<String, String> commonParas =
                                        confParas.get(ConfConstants.COMMON_SECTION_NAME);
                                Map<String, String> dbParas =
                                        confParas.get("[@" + database.getLabel() + "]");
                                String str =
                                        dbParas == null
                                                ? null
                                                : dbParas.get(ConfConstants.REPLICATION);
                                boolean isReplication = "yes".equals(str);
                                if (!isReplication) {
                                    str =
                                            commonParas == null
                                                    ? null
                                                    : commonParas.get(ConfConstants.REPLICATION);
                                    isReplication = "yes".equals(str);
                                }
                                dialog.setReplication(isReplication);
                            }
                        }
                        return true;
                    }
                };
        ServerInfo serverInfo = database.getServer().getServerInfo();
        GetCubridConfParameterTask getCubridConfParameterTask =
                new GetCubridConfParameterTask(serverInfo);
        taskExcutor.addTask(getCubridConfParameterTask);
        GetDbBackupInfoTask getDbBackupInfoTask = new GetDbBackupInfoTask(serverInfo);
        getDbBackupInfoTask.setDbName(database.getLabel());
        taskExcutor.addTask(getDbBackupInfoTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            dialog.open();
        }
    }
}
