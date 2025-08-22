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
package com.cubrid.cubridmanager.ui.common.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.GetCMUserListTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.common.dialog.UserManagementDialog;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to manage user information
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class UserManagementAction extends SelectionAction {

    public static final String ID = UserManagementAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public UserManagementAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public UserManagementAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            CubridServer server = node.getServer();
            if (server != null
                    && server.isConnected()
                    && server.getServerInfo() != null
                    && server.getServerInfo().getLoginedUserInfo() != null
                    && server.getServerInfo().getLoginedUserInfo().isAdmin()) {
                return true;
            }
        }
        return false;
    }

    /** Open the user management dialog and manage CUBRID Manager user */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (obj == null || obj.length <= 0 || !isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ICubridNode node = (ICubridNode) obj[0];
        final CubridServer server = node.getServer();
        final UserManagementDialog dialog = new UserManagementDialog(getShell());
        dialog.setServer(server);
        TaskExecutor taskExcutor = new GetUserInfoTaskExecutor(dialog, getShell());
        GetCMUserListTask getCMUserListTask = new GetCMUserListTask(server.getServerInfo());
        taskExcutor.addTask(getCMUserListTask);
        new ExecTaskWithProgress(taskExcutor).exec();
        if (taskExcutor.isSuccess()) {
            dialog.open();
        }
    }

    /**
     * Get CM user information task executor
     *
     * @author pangqiren
     * @version 1.0 - 2010-1-6 created by pangqiren
     */
    static class GetUserInfoTaskExecutor extends TaskExecutor {

        private final UserManagementDialog dialog;
        private final Shell shell;

        public GetUserInfoTaskExecutor(UserManagementDialog dialog, Shell shell) {
            this.dialog = dialog;
            this.shell = shell;
        }

        /**
         * Execute to get CM user information
         *
         * @param monitor the IProgressMonitor
         * @return <code>true</code> if successful;<code>false</code> otherwise
         */
        public boolean exec(final IProgressMonitor monitor) {
            if (monitor.isCanceled()) {
                return false;
            }
            monitor.beginTask(Messages.loadUserInfoTaskName, IProgressMonitor.UNKNOWN);
            for (ITask task : taskList) {
                task.execute();
                if (openErrorBox(shell, task.getErrorMsg(), monitor) || monitor.isCanceled()) {
                    return false;
                }
                if (task instanceof GetCMUserListTask) {
                    GetCMUserListTask getCMUserListTask = (GetCMUserListTask) task;
                    List<ServerUserInfo> userInfoList = getCMUserListTask.getServerUserInfoList();
                    dialog.setServerUserInfoList(userInfoList);
                }
            }
            return true;
        }
    }
}
