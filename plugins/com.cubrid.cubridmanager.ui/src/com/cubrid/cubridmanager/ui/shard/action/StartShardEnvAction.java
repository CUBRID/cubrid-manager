/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.shard.action;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.IMessageHandler;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.shard.task.StartShardTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.shard.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridShardFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Start shard environment
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-1-6
 */
public class StartShardEnvAction extends SelectionAction {
    public static final String ID = StartShardEnvAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     */
    public StartShardEnvAction(Shell shell) {
        this(
                shell,
                null,
                Messages.startShardEnvActionName,
                CubridManagerUIPlugin.getImageDescriptor("icons/action/broker_group_start.png"));
    }

    /**
     * The Constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    protected StartShardEnvAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** Override the run method in order to complete starting broker environment */
    public void run() {
        final Object[] obj = this.getSelectedObj();
        CubridShardFolder selection = (CubridShardFolder) obj[0];
        if (null == selection) {
            return;
        }
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        StartShardTask task = new StartShardTask(serverInfo, null);

        IMessageHandler messageHandler =
                new IMessageHandler() {
                    public String translate(String message) {
                        if (message == null) {
                            return "";
                        }
                        if (message.indexOf("failed to metadata validate check") != -1) {
                            return Messages.errStartShardNotConfigOrFailed
                                    + StringUtil.NEWLINE
                                    + StringUtil.NEWLINE
                                    + Messages.msgShardGuide;
                        }
                        return "";
                    }
                };

        TaskExecutor taskExecutor =
                new CommonTaskExec(Messages.startShardEnvActionName, messageHandler);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (!taskExecutor.isSuccess()) {
            return;
        }
        if (!task.isSuccess()) {
            String msg = Messages.bind(Messages.errCanNotStartShardBroker, Messages.msgShardGuide);
            CommonUITool.openErrorBox(msg);
            return;
        }
        selection.setRunning(true);
        TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
        CommonUITool.refreshNavigatorTree(treeViewer, selection);
        ActionManager.getInstance().fireSelectionChanged(getSelection());
    }

    /**
     * Makes this action not support to select multi object
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the object
     * @return <code>true</code> if supported;<code>false</code>
     */
    public boolean isSupported(Object obj) {
        return isSupportedNode(obj);
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the object
     * @return <code>true</code> if supported;<code>false</code>
     */
    public static boolean isSupportedNode(Object obj) {
        if (obj instanceof CubridShardFolder) {
            CubridShardFolder selection = ((CubridShardFolder) obj);
            if (!selection.isEnable()) {
                return false;
            }
            ServerUserInfo userInfo = selection.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo == null || CasAuthType.AUTH_ADMIN != userInfo.getCasAuth()) {
                return false;
            }
            return !selection.isRunning();
        }
        return false;
    }
}
