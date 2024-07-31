/*
 * Copyright (C) 2013 NHN Corporation. All rights reserved by NHN.
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

package com.cubrid.cubridmanager.ui.cubrid.jobauto.action;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.SetQueryPlanListTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.dialog.EditQueryPlanDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridDatabaseLoader;
import com.cubrid.cubridmanager.ui.spi.model.loader.jobauto.CubridJobAutoFolderLoader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This is an action to listen to adding query plan selection event and open an instance of
 * EditQueryPlanDialog class
 *
 * @author lizhiqiang 2009-3-13
 */
public class AddQueryPlanAction extends SelectionAction {
    public static final String ID = AddQueryPlanAction.class.getName();
    private static final String ICON_PATH = "icons/navigator/auto_query_item.png";
    private boolean canceledTask = false;

    public AddQueryPlanAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    protected AddQueryPlanAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        CubridDatabase database = null;
        DefaultSchemaNode selection = null;
        if (obj.length > 0 && obj[0] instanceof DefaultSchemaNode) {
            selection = (DefaultSchemaNode) obj[0];
            database = selection.getDatabase();
        }
        assert (null != selection);
        if (database == null) {
            CommonUITool.openErrorBox(Messages.msgSelectDB);
            return;
        }
        run(database);
    }

    public void run(CubridDatabase database) {
        ICubridNode jobAutoFolderNode =
                database.getChild(
                        database.getId()
                                + ICubridNodeLoader.NODE_SEPARATOR
                                + CubridDatabaseLoader.JOB_AUTO_FOLDER_ID);
        DefaultSchemaNode queryPlanFolderNode =
                (DefaultSchemaNode)
                        jobAutoFolderNode.getChild(
                                jobAutoFolderNode.getId()
                                        + ICubridNodeLoader.NODE_SEPARATOR
                                        + CubridJobAutoFolderLoader.QUERY_PLAN_FOLDER_ID);
        boolean withUser = false;
        if (CompatibleUtil.isSupportQueryPlanWithUser(database.getDatabaseInfo())) {
            withUser = true;
        }

        TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
        EditQueryPlanDialog editQueryPlanDlg = new EditQueryPlanDialog(getShell(), true);
        editQueryPlanDlg.setDatabase(database);
        editQueryPlanDlg.setOperation(AddEditType.ADD);
        editQueryPlanDlg.initPara(queryPlanFolderNode);
        DefaultSchemaNode newNode = null;
        if (editQueryPlanDlg.open() == Dialog.OK) {
            // Sets the data of task
            QueryPlanInfoHelp queryPlanInfoHelp = editQueryPlanDlg.getQueryPlanInfo();
            QueryPlanInfo queryPlanInfo = queryPlanInfoHelp.getQueryPlanInfo();
            String newQueryId = queryPlanInfoHelp.getQuery_id();
            newNode = new DefaultSchemaNode(newQueryId, newQueryId, newQueryId, ICON_PATH);
            newNode.setContainer(false);
            newNode.setType(CubridNodeType.QUERY_PLAN);
            newNode.setModelObj(queryPlanInfo);

            List<ICubridNode> list = queryPlanFolderNode.getChildren();
            List<String> msgList = new ArrayList<String>();
            QueryPlanInfo queryPlan = null;
            QueryPlanInfoHelp qHelp = new QueryPlanInfoHelp();
            for (ICubridNode icn : list) {
                if (!icn.isContainer()) {
                    queryPlan = (QueryPlanInfo) icn.getAdapter(QueryPlanInfo.class);
                    qHelp.setQueryPlanInfo(queryPlan);
                    msgList.add(qHelp.buildMsg(withUser));
                }
            }
            qHelp.setQueryPlanInfo(queryPlanInfo);
            msgList.add(qHelp.buildMsg(withUser));

            ServerInfo serverInfo = database.getServer().getServerInfo();
            SetQueryPlanListTask task = new SetQueryPlanListTask(serverInfo);
            task.setDbname(database.getName());
            task.buildMsg(msgList);
            String taskName =
                    Messages.bind(
                            com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages
                                    .addQueryPlanTaskName,
                            newQueryId);
            TaskExecutor taskExecutor = new CommonTaskExec(taskName);
            taskExecutor.addTask(task);
            new ExecTaskWithProgress(taskExecutor).exec();
            if (!taskExecutor.isSuccess()) {
                return;
            }
        } else {
            canceledTask = true;
        }

        if (null != newNode) {
            queryPlanFolderNode.addChild(newNode);
            treeViewer.add(queryPlanFolderNode, newNode);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    newNode, CubridNodeChangedEventType.NODE_ADD));
        }
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        if (!(obj instanceof ISchemaNode)) {
            return false;
        }
        ISchemaNode node = (ISchemaNode) obj;
        CubridDatabase database = node.getDatabase();
        if (CubridNodeType.QUERY_PLAN_FOLDER.equals(node.getType())
                && database != null
                && database.isLogined()) {
            DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCanceledTask() {
        return canceledTask;
    }
}
