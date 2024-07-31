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
package com.cubrid.cubridmanager.ui.logs.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.task.DelAllLogTask;
import com.cubrid.cubridmanager.ui.logs.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This action is responsible to RemoveLogAction
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-10 created by wuyingshi
 */
public class RemoveLogAction extends SelectionAction {

    public static final String ID = RemoveLogAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public RemoveLogAction(Shell shell, String text, ImageDescriptor icon) {
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
    public RemoveLogAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return false
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj Object
     * @return boolean(whether to support)
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof Object[]) {
            return true;
        }
        if (!(obj instanceof ICubridNode)) {
            return false;
        }
        ICubridNode node = (ICubridNode) obj;
        if (node.getServer() == null) {
            return false;
        }
        ServerUserInfo serverUserInfo = node.getServer().getServerInfo().getLoginedUserInfo();
        String type = node.getType();
        if (CubridNodeType.BROKER_SQL_LOG.equals(type)) {
            if (serverUserInfo == null || serverUserInfo.getCasAuth() != CasAuthType.AUTH_ADMIN) {
                return false;
            }
            return true;
        } else if (CubridNodeType.LOGS_BROKER_ACCESS_LOG.equals(type)
                || CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(type)
                || CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(type)) {
            if (serverUserInfo == null || !serverUserInfo.isAdmin()) {
                return false;
            }
            if (CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(type) && isLastDbServerLog(node)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Return whether it is the last database server log path
     *
     * @param node ICubridNode
     * @return boolean
     */
    private boolean isLastDbServerLog(ICubridNode node) {
        LogInfo logInfo = (LogInfo) node.getAdapter(LogInfo.class);
        String logPath = logInfo.getPath();
        String lastDBLog = "";
        if (CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(node.getType())) {
            String[] path = new String[node.getParent().getChildren().size()];
            for (int j = 0, len = path.length; j < len; j++) {
                LogInfo currLogFile =
                        ((LogInfo)
                                (node.getParent().getChildren().get(j).getAdapter(LogInfo.class)));
                if (lastDBLog.trim().length() == 0) {
                    lastDBLog = currLogFile.getPath();
                    continue;
                }
                if (lastDBLog.compareTo(currLogFile.getPath()) < 0) {
                    lastDBLog = currLogFile.getPath();
                }
            }
        }

        return lastDBLog.equals(logPath);
    }

    /** Delete log file */
    public void run() {
        if (!CommonUITool.openConfirmBox(Messages.warningRemoveLog)) {
            return;
        }
        Object[] selected = this.getSelectedObj();
        if (selected == null || selected.length == 0) {
            return;
        }
        List<String> logPathList = new ArrayList<String>();
        for (int i = 0; i < selected.length; i++) {
            ICubridNode node = (ICubridNode) selected[i];
            if (isLastDbServerLog(node)) {
                continue;
            }
            LogInfo logInfo = (LogInfo) node.getAdapter(LogInfo.class);
            if (logInfo != null
                    && logInfo.getPath() != null
                    && logInfo.getPath().trim().length() > 0) {
                logPathList.add(logInfo.getPath());
            }
        }
        DelAllLogTask delLogTask =
                new DelAllLogTask(((DefaultCubridNode) selected[0]).getServer().getServerInfo());
        String[] paths = new String[logPathList.size()];
        delLogTask.setPath(logPathList.toArray(paths));
        TaskExecutor taskExecutor = new CommonTaskExec(Messages.removeLogTaskName);
        taskExecutor.addTask(delLogTask);
        new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
        if (taskExecutor.isSuccess()) {
            TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
            for (int i = 0; i < selected.length; i++) {
                ICubridNode delNode = (ICubridNode) selected[i];
                if (isLastDbServerLog(delNode)) {
                    continue;
                }
                ICubridNode parentNode = delNode.getParent();
                parentNode.removeChild(delNode);
                treeViewer.remove(delNode);

                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPage activePage = window.getActivePage();
                IEditorPart editor = activePage.findEditor(delNode);
                if (null != editor) {
                    activePage.closeEditor(editor, true);
                }
            }
        }
    }
}
