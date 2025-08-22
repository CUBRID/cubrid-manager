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
package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.ui.cubrid.table.dialog.CreateViewDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.loader.schema.CubridViewsFolderLoader;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.JDBCGetAllDbUserTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * This action is responsible to add view.
 *
 * @author robin 2009-6-4
 */
public class CreateViewAction extends SelectionAction {
    public static final String ID = CreateViewAction.class.getName();
    private boolean canceledTask = false;

    public CreateViewAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public CreateViewAction(
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
        return true;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(obj, null, false);
    }

    public void run(CubridDatabase database) {
        TaskExecutor taskExcutor = new CommonTaskExec(null);
        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        boolean isSupportUserSchema = databaseInfo.isSupportUserSchema();

        JDBCGetAllDbUserTask task = new JDBCGetAllDbUserTask(databaseInfo);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }

        List<String> dbUserList = task.getDbUserList();

        CreateViewDialog dialog =
                new CreateViewDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        database,
                        true);
        dialog.setDbUserList(dbUserList);

        ISelectionProvider provider = getSelectionProvider();
        if (dialog.open() == IDialogConstants.OK_ID && (provider instanceof TreeViewer)) {
            TreeViewer treeViewer = (TreeViewer) provider;
            ICubridNode folderNode =
                    database.getChild(
                            database.getId()
                                    + ICubridNodeLoader.NODE_SEPARATOR
                                    + CubridViewsFolderLoader.VIEWS_FOLDER_ID);
            if (folderNode == null || !folderNode.getLoader().isLoaded()) {
                return;
            }
            String viewName = dialog.getNewViewName();
            String owner = dialog.getOwner();
            String id =
                    folderNode.getId() + ICubridNodeLoader.NODE_SEPARATOR + owner + "." + viewName;
            ClassInfo newClassInfo =
                    new ClassInfo(
                            dialog.getNewViewName(),
                            owner,
                            ClassType.VIEW,
                            false,
                            false,
                            isSupportUserSchema);
            ICubridNode newNode = CubridViewsFolderLoader.createUserViewNode(id, newClassInfo);
            CommonUITool.addNodeToTree(treeViewer, folderNode, newNode);

            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, folderNode);

            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    newNode, CubridNodeChangedEventType.NODE_ADD));
        } else {
            canceledTask = true;
        }
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        ISchemaNode node = (ISchemaNode) obj[0];
        CubridDatabase database = node.getDatabase();
        run(database);
    }

    public boolean isCanceledTask() {
        return canceledTask;
    }
}
