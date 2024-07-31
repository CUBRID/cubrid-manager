/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.monitoring.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo;
import com.cubrid.cubridmanager.core.monitoring.task.UpdateStatusTemplateTask;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.dialog.DiagStatusMonitorTemplateDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is an action in order to listen to selection and open status monitor dialog
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-27 created by lizhiqiang
 */
public class EditStatusMonitorTemplateAction extends SelectionAction {

    public static final String ID = EditStatusMonitorTemplateAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public EditStatusMonitorTemplateAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The Constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    protected EditStatusMonitorTemplateAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** Override the run method in order to open an instance of status monitor dialog */
    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode selection = (ICubridNode) obj[0];
        ServerInfo serverInfo = selection.getServer().getServerInfo();

        boolean execDiagChecked = false;
        final GetCMConfParameterTask task = new GetCMConfParameterTask(serverInfo);
        TaskExecutor taskExec = new CommonTaskExec(null);
        taskExec.addTask(task);
        new ExecTaskWithProgress(taskExec).exec();
        if (!taskExec.isSuccess()) {
            return;
        }
        Map<String, String> confParas = task.getConfParameters();
        if (confParas == null) {
            execDiagChecked = false;
        } else {
            if (confParas.get(ConfConstants.EXECUTE_DIAG) == null) {
                execDiagChecked = false;
            } else {
                execDiagChecked =
                        confParas.get(ConfConstants.EXECUTE_DIAG).equals(OnOffType.ON.getText())
                                ? true
                                : false;
            }
        }
        DiagStatusMonitorTemplateDialog dialog = new DiagStatusMonitorTemplateDialog(getShell());
        dialog.setOperation(AddEditType.EDIT);
        dialog.setSelection(selection);
        dialog.setExecDiagChecked(execDiagChecked);
        if (dialog.open() == Dialog.OK) {
            StatusTemplateInfo statusTemplateInfo = dialog.getStatusTemplateInfo();
            UpdateStatusTemplateTask updateTask = new UpdateStatusTemplateTask(serverInfo);
            updateTask.setStatusTemplateInfo(statusTemplateInfo);
            updateTask.buildMsg();

            String taskName =
                    Messages.bind(Messages.editTemplateTaskName, statusTemplateInfo.getName());
            TaskExecutor taskExecutor = new CommonTaskExec(taskName);
            taskExecutor.addTask(updateTask);
            new ExecTaskWithProgress(taskExecutor).exec();

            if (taskExecutor.isSuccess()) {
                TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
                CommonUITool.refreshNavigatorTree(treeViewer, selection.getParent());
            }
        }
    }

    /**
     * Makes this action not support to select multi object
     *
     * @return boolean true if allowed ,false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj Object
     * @return boolean true if supported, false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (!CubridNodeType.STATUS_MONITOR_TEMPLATE.equals(node.getType())) {
                return false;
            }
            ServerUserInfo userInfo = node.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo == null
                    || StatusMonitorAuthType.AUTH_ADMIN != userInfo.getStatusMonitorAuth()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
