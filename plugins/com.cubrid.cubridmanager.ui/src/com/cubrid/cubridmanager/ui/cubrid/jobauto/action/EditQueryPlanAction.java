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
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This is an action to listen to editing query plan selection event and open an instance of
 * EditQueryPlanDialog class
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-12 created by lizhiqiang
 */
public class EditQueryPlanAction extends SelectionAction {

    public static final String ID = EditQueryPlanAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public EditQueryPlanAction(Shell shell, String text, ImageDescriptor icon) {
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
    protected EditQueryPlanAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** get edit query plan node */
    public void run() {
        Object[] obj = this.getSelectedObj();
        CubridDatabase database = null;
        DefaultSchemaNode selection = null;
        if (obj.length > 0 && obj[0] instanceof DefaultSchemaNode) {
            selection = (DefaultSchemaNode) obj[0];
            database = selection.getDatabase();
        }
        if (database == null) {
            CommonUITool.openErrorBox(Messages.msgSelectDB);
            return;
        }
        run(database, selection);
    }

    /** Creates a Dialog which is the instance of EditBackupPlanDialog to edit a query plan */
    public int run(CubridDatabase database, DefaultSchemaNode schemaNode) {
        if (database == null) {
            CommonUITool.openErrorBox(Messages.msgSelectDB);
            return IDialogConstants.CANCEL_ID;
        }

        boolean withUser = CompatibleUtil.isSupportQueryPlanWithUser(database.getDatabaseInfo());

        boolean isEditAble = false;
        if (CubridNodeType.QUERY_PLAN.equals(schemaNode.getType())
                && database != null
                && database.isLogined()) {
            DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
                isEditAble = true;
            }
        }

        EditQueryPlanDialog editQueryPlanDlg = new EditQueryPlanDialog(getShell(), isEditAble);
        editQueryPlanDlg.setOperation(AddEditType.EDIT);
        editQueryPlanDlg.initPara(schemaNode);

        if (editQueryPlanDlg.open() == Dialog.OK) {
            // Sets the data of task
            List<ICubridNode> list = schemaNode.getParent().getChildren();
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

            ServerInfo serverInfo = database.getServer().getServerInfo();
            SetQueryPlanListTask task = new SetQueryPlanListTask(serverInfo);
            task.setDbname(database.getName());
            task.buildMsg(msgList);
            String taskName =
                    Messages.bind(
                            com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages
                                    .editQueryPlanTaskName,
                            schemaNode.getName());
            TaskExecutor taskExecutor = new CommonTaskExec(taskName);
            taskExecutor.addTask(task);
            new ExecTaskWithProgress(taskExecutor).exec();
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    schemaNode, CubridNodeChangedEventType.NODE_REFRESH));
            ActionManager.getInstance().fireSelectionChanged(getSelection());
            return IDialogConstants.OK_ID;
        }
        return IDialogConstants.CANCEL_ID;
    }

    /**
     * Sets this action support to select multi-object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        if (!(obj instanceof ISchemaNode)) {
            return false;
        }

        return true;
    }
}
