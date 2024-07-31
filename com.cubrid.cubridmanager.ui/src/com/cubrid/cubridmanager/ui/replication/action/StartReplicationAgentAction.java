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
package com.cubrid.cubridmanager.ui.replication.action;

import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.task.StartReplicationAgentTask;
import com.cubrid.cubridmanager.ui.replication.Messages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible for starting replication Agent database in C/S mode
 *
 * @author wuyingshi
 * @version 1.0 - 2009-9-1 created by wuyingshi
 */
public class StartReplicationAgentAction extends SelectionAction {

    public static final String ID = StartReplicationAgentAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public StartReplicationAgentAction(Shell shell, String text, ImageDescriptor icon) {
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
    public StartReplicationAgentAction(
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
        return false;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj Object
     * @return boolean(whether to support)
     */
    public boolean isSupported(Object obj) {
        return isSupportedNode(obj);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj Object
     * @return boolean(whether to support)
     */
    public static boolean isSupportedNode(Object obj) {
        if (obj instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) obj;
            if (!database.isLogined() || database.getRunningType() == DbRunningType.STANDALONE) {
                return false;
            }
            ServerUserInfo serverUserInfo =
                    database.getServer().getServerInfo().getLoginedUserInfo();
            if (serverUserInfo == null || !serverUserInfo.isAdmin()) {
                return false;
            }
            if (!database.isDistributorDb()) {
                return false;
            }
            ReplicationInfo replInfo = (ReplicationInfo) database.getAdapter(ReplicationInfo.class);
            for (int i = 0;
                    replInfo != null
                            && replInfo.getSlaveList() != null
                            && i < replInfo.getSlaveList().size();
                    i++) {
                DatabaseInfo dbInfo =
                        serverUserInfo.getDatabaseInfo(
                                replInfo.getSlaveList().get(i).getSlaveDbName());
                if (dbInfo == null || dbInfo.getRunningType() == DbRunningType.STANDALONE) {
                    return false;
                }
            }
            if (replInfo != null && replInfo.getDistInfo() != null) {
                return !replInfo.getDistInfo().isAgentActive();
            }
            return false;
        }
        return false;
    }

    /** Start replication agent and refresh navigator */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (obj == null || obj.length == 0 || !isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ISchemaNode schemaNode = (ISchemaNode) obj[0];
        CubridDatabase database = schemaNode.getDatabase();
        String dbNames = database.getName();
        String dbaPassword = database.getPassword();
        ReplicationInfo replInfo = (ReplicationInfo) database.getAdapter(ReplicationInfo.class);
        if (!CommonUITool.openConfirmBox(
                Messages.bind(
                        Messages.msgConfirmStartMasterDbAndSlaveDb,
                        replInfo.getMasterList().get(0).getMasterDbName(),
                        replInfo.getSlaveList().get(0).getSlaveDbName()))) {
            return;
        }
        StartReplicationAgentTask task =
                new StartReplicationAgentTask(database.getServer().getServerInfo());
        task.setDbName(dbNames);
        task.setDbaPasswd(dbaPassword);
        TaskExecutor taskExecutor = new CommonTaskExec(null);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            CommonUITool.openInformationBox(
                    Messages.msgSuccess, Messages.bind(Messages.msgStartAgent, dbNames));
            if (replInfo != null && replInfo.getDistInfo() != null) {
                replInfo.getDistInfo().setAgentActive(true);
            }
            ActionManager.getInstance().fireSelectionChanged(getSelection());
        }
    }
}
