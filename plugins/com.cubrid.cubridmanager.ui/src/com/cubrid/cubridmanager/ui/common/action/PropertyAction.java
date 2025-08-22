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
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetHAConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.util.PreferenceUtil;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to show property of CUBRID node
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class PropertyAction extends SelectionAction {
    public static final String ID = PropertyAction.class.getName();
    private boolean isCancel = false;

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public PropertyAction(Shell shell, String text, ImageDescriptor icon) {
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
    public PropertyAction(
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
            if (server == null || !server.isConnected()) {
                return false;
            }
            ServerUserInfo userInfo = server.getServerInfo().getLoginedUserInfo();
            String type = node.getType();
            if (CubridNodeType.SERVER.equals(type)
                    || CubridNodeType.DATABASE_FOLDER.equals(type)
                    || CubridNodeType.DATABASE.equals(type)) {
                return true;
            } else if (CubridNodeType.BROKER_FOLDER.equals(type)
                    || CubridNodeType.BROKER.equals(type)
                    || CubridNodeType.SHARD_FOLDER.equals(type)
                    || CubridNodeType.SHARD.equals(type)) {
                return userInfo != null
                        && (CasAuthType.AUTH_ADMIN == userInfo.getCasAuth()
                                || CasAuthType.AUTH_MONITOR == userInfo.getCasAuth());
            }
        }
        return false;
    }

    /** Open property dialog,view and set property */
    public void run() {
        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            return;
        }
        final ICubridNode node = (ICubridNode) obj[0];
        String type = node.getType();
        if (CubridNodeType.SERVER.equals(type)
                || CubridNodeType.DATABASE_FOLDER.equals(type)
                || CubridNodeType.DATABASE.equals(type)
                || CubridNodeType.BROKER_FOLDER.equals(type)
                || CubridNodeType.BROKER.equals(type)) {
            TaskExecutor taskExcutor = new GetPropertyExecutor(node, getShell());
            ServerInfo serverInfo = node.getServer().getServerInfo();
            GetCubridConfParameterTask getCubridConfParameterTask =
                    new GetCubridConfParameterTask(serverInfo);
            GetBrokerConfParameterTask getBrokerConfParameterTask =
                    new GetBrokerConfParameterTask(serverInfo);
            GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(serverInfo);
            if (CubridNodeType.SERVER.equals(type)) {
                taskExcutor.addTask(getCubridConfParameterTask);
                taskExcutor.addTask(getBrokerConfParameterTask);
                taskExcutor.addTask(getCMConfParameterTask);
                if (CompatibleUtil.isSupportNewHAConfFile(serverInfo)) {
                    GetHAConfParameterTask getHAConfParameterTask =
                            new GetHAConfParameterTask(serverInfo);
                    taskExcutor.addTask(getHAConfParameterTask);
                }
            }
            if (CubridNodeType.DATABASE_FOLDER.equals(type)
                    || CubridNodeType.DATABASE.equals(type)) {
                taskExcutor.addTask(getCubridConfParameterTask);
            }
            if (CubridNodeType.BROKER_FOLDER.equals(type) || CubridNodeType.BROKER.equals(type)) {
                taskExcutor.addTask(getBrokerConfParameterTask);
            }
            new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        }
        if (!isCancel) {
            Dialog dialog = PreferenceUtil.createPropertyDialog(getShell(), node);
            dialog.open();
        }
    }

    /**
     * Get Property task executor
     *
     * @author pangqiren
     * @version 1.0 - 2010-1-6 created by pangqiren
     */
    class GetPropertyExecutor extends TaskExecutor {
        private final ICubridNode node;
        private final Shell shell;

        public GetPropertyExecutor(ICubridNode node, Shell shell) {
            this.node = node;
            this.shell = shell;
        }

        /**
         * Execute to get property
         *
         * @param monitor the IProgressMonitor
         * @return <code>true</code> if successful;<code>false</code> otherwise
         */
        public boolean exec(final IProgressMonitor monitor) {
            isCancel = false;
            if (monitor.isCanceled()) {
                isCancel = true;
                return false;
            }
            monitor.beginTask(Messages.loadConfParaTaskName, IProgressMonitor.UNKNOWN);
            for (ITask task : taskList) {
                task.execute();
                if (openErrorBox(shell, task.getErrorMsg(), monitor) || monitor.isCanceled()) {
                    isCancel = true;
                    return false;
                }
                if (task instanceof GetCubridConfParameterTask) {
                    GetCubridConfParameterTask getCubridConfParameterTask =
                            (GetCubridConfParameterTask) task;
                    Map<String, Map<String, String>> confParas =
                            getCubridConfParameterTask.getConfParameters();
                    node.getServer().getServerInfo().setCubridConfParaMap(confParas);
                }
                if (task instanceof GetBrokerConfParameterTask) {
                    GetBrokerConfParameterTask getBrokerConfParameterTask =
                            (GetBrokerConfParameterTask) task;
                    Map<String, Map<String, String>> confParas =
                            getBrokerConfParameterTask.getConfParameters();
                    node.getServer().getServerInfo().setBrokerConfParaMap(confParas);
                }
                if (task instanceof GetCMConfParameterTask) {
                    GetCMConfParameterTask getCMConfParameterTask = (GetCMConfParameterTask) task;
                    Map<String, String> confParas = getCMConfParameterTask.getConfParameters();
                    node.getServer().getServerInfo().setCmConfParaMap(confParas);
                }
                if (task instanceof GetHAConfParameterTask) {
                    GetHAConfParameterTask getHAConfParameterTask = (GetHAConfParameterTask) task;
                    Map<String, Map<String, String>> confParas =
                            getHAConfParameterTask.getConfParameters();
                    node.getServer().getServerInfo().setHaConfParaMap(confParas);
                }
            }
            return true;
        }
    }
}
