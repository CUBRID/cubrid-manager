/*
 * Copyright (C) 2009 NHN Corporation. All rights reserved by NHN.
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
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.SetQueryPlanListTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This is an action to listen to deleting query plan selection event and execute the operation of
 * deleting query plan.
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-12 created by lizhiqiang
 */
public class DeleteQueryPlanAction extends SelectionAction {

    public static final String ID = DeleteQueryPlanAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public DeleteQueryPlanAction(Shell shell, String text, ImageDescriptor icon) {
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
    protected DeleteQueryPlanAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** Delete the selected serials */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || !isSupported(objArr)) {
            setEnabled(false);
            return;
        }
        ISchemaNode nodeArray[] = new ISchemaNode[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            nodeArray[i] = (ISchemaNode) objArr[i];
        }
        run(nodeArray);
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run(ISchemaNode nodeArray[]) {
        List<String> nodeNames = new ArrayList<String>();
        CubridDatabase database = null;
        for (Object obj : nodeArray) {
            if (obj instanceof DefaultSchemaNode) {
                database = ((DefaultSchemaNode) obj).getDatabase();
            }
            ICubridNode selection = (ICubridNode) obj;
            nodeNames.add(selection.getLabel());
        }
        if (!CommonUITool.openConfirmBox(
                Messages.bind(Messages.delQueryPlanConfirmContent, nodeNames))) {
            return;
        }
        if (database == null) {
            CommonUITool.openErrorBox(Messages.msgSelectDB);
            return;
        }

        ICubridNode parentSelection = ((DefaultSchemaNode) nodeArray[0]).getParent();
        boolean withUser = CompatibleUtil.isSupportQueryPlanWithUser(database.getDatabaseInfo());
        List<ICubridNode> list = parentSelection.getChildren();
        List<String> msgList = new ArrayList<String>();
        QueryPlanInfo queryPlan = null;
        QueryPlanInfoHelp qHelp = new QueryPlanInfoHelp();
        for (ICubridNode icn : list) {
            if (!icn.isContainer()) {
                boolean isRemove = false;
                for (String nodeName : nodeNames) {
                    if (icn.getLabel().equals(nodeName)) {
                        isRemove = true;
                        break;
                    }
                }
                if (isRemove) {
                    continue;
                }
                queryPlan = (QueryPlanInfo) icn.getAdapter(QueryPlanInfo.class);
                qHelp.setQueryPlanInfo(queryPlan);
                msgList.add(qHelp.buildMsg(withUser));
            }
        }

        ServerInfo serverInfo = database.getServer().getServerInfo();
        SetQueryPlanListTask task = new SetQueryPlanListTask(serverInfo);
        task.setDbname(database.getName());
        task.buildMsg(msgList);

        String taskName = Messages.bind(Messages.delQueryPlanTaskName, nodeNames);
        TaskExecutor taskExecutor = new CommonTaskExec(taskName);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();

        if (!taskExecutor.isSuccess()) {
            return;
        }

        for (Object obj : nodeArray) {
            TreeViewer treeViewer = (TreeViewer) getSelectionProvider();
            DefaultSchemaNode delNode = ((DefaultSchemaNode) obj);
            ICubridNode parentNode = delNode.getParent();
            parentNode.removeChild(delNode);
            treeViewer.remove(delNode);
        }
    }

    /**
     * Sets this action support to select multi-object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ISchemaNode) {
            ISchemaNode node = (ISchemaNode) obj;
            CubridDatabase database = node.getDatabase();
            if (CubridNodeType.QUERY_PLAN.equals(node.getType())
                    && database != null
                    && database.isLogined()) {
                DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
                if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
                    return true;
                }
            }
        } else if (obj instanceof Object[]) {
            Object[] objArr = (Object[]) obj;
            CubridDatabase database = null;
            for (Object object : objArr) {
                DefaultSchemaNode node = (DefaultSchemaNode) object;
                CubridDatabase db = node.getDatabase();
                if (database == null) {
                    database = db;
                } else if (!database.getId().equals(db.getId())) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
