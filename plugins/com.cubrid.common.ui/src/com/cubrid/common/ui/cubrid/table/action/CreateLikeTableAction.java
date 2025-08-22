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
package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.cubrid.table.dialog.CreateLikeTableDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.JDBCGetAllDbUserTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * In MySQL compatible mode, create table by like statement action
 *
 * @author pangqiren
 * @version 1.0 - 2010-4-19 created by pangqiren
 */
public class CreateLikeTableAction extends SelectionAction {

    private boolean isSupportUserSchema = false;

    public static final String ID = CreateLikeTableAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public CreateLikeTableAction(Shell shell, String text, ImageDescriptor icon) {
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
    public CreateLikeTableAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
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
        return ActionSupportUtil.isSupportSingleSelection(
                obj, new String[] {NodeType.USER_TABLE, NodeType.TABLE_FOLDER});
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || !isSupported(objArr)) {
            setEnabled(false);
            return;
        }

        ISchemaNode node = (ISchemaNode) objArr[0];
        if (node == null) {
            return;
        }

        doRun(node);
    }

    /**
     * Run
     *
     * @param node
     */
    public void run(ISchemaNode node) {
        doRun(node);
    }

    /**
     * Do run
     *
     * @param node
     */
    private void doRun(ISchemaNode node) {

        TaskExecutor taskExcutor = new CommonTaskExec(null);

        JDBCGetAllDbUserTask task = new JDBCGetAllDbUserTask(node.getDatabase().getDatabaseInfo());
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }

        List<String> dbUserList = task.getDbUserList();

        CreateLikeTableDialog dialog =
                new CreateLikeTableDialog(getShell(), node.getDatabase(), dbUserList);
        isSupportUserSchema = node.getDatabase().getDatabaseInfo().isSupportUserSchema();
        if (NodeType.USER_TABLE.equals(node.getType())) {
            ClassInfo classInfo = (ClassInfo) node.getAdapter(ClassInfo.class);
            if (classInfo != null) {
                dialog.setLikeTableName(classInfo.getClassName());
                dialog.setLikeTableOwnerName(classInfo.getOwnerName());
            }
        }
        if (IDialogConstants.OK_ID == dialog.open()) { // FIXME
            TreeViewer treeViewer = CubridNavigatorView.findNavigationView().getViewer();
            String className = dialog.getNewTableName();
            String ownerName = dialog.getNewOwnerName();
            String uniqueName = dialog.getNewUniqueTable();
            ICubridNode newNode = null;
            if (NodeType.USER_TABLE.equals(node.getType())) {
                ClassInfo classInfo = (ClassInfo) node.getAdapter(ClassInfo.class);
                String id =
                        node.getParent().getId() + ICubridNodeLoader.NODE_SEPARATOR + uniqueName;
                ClassInfo newClassInfo =
                        new ClassInfo(
                                className,
                                ownerName,
                                ClassType.NORMAL,
                                classInfo.isSystemClass(),
                                classInfo.isPartitionedClass(),
                                classInfo.isSupportUserSchema());
                newNode =
                        CubridTablesFolderLoader.createUserTableNode(
                                node.getParent(),
                                id,
                                newClassInfo,
                                node.getParent().getLoader().getLevel(),
                                new NullProgressMonitor());
                if (node.getDatabase().getDatabaseInfo().getUserTableInfoList() != null) {
                    node.getDatabase().getDatabaseInfo().getUserTableInfoList().add(newClassInfo);
                }
                CommonUITool.addNodeToTree(treeViewer, node.getParent(), newNode);
                CommonUITool.updateFolderNodeLabelIncludingChildrenCount(
                        treeViewer, node.getParent());
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        newNode, CubridNodeChangedEventType.NODE_ADD));
            } else {
                if (node == null || !node.getLoader().isLoaded()) {
                    return;
                }
                String id = node.getId() + ICubridNodeLoader.NODE_SEPARATOR + className;
                ClassInfo newClassInfo =
                        new ClassInfo(className, null, ClassType.NORMAL, false, false, false);
                newNode =
                        CubridTablesFolderLoader.createUserTableNode(
                                node,
                                id,
                                newClassInfo,
                                node.getLoader().getLevel(),
                                new NullProgressMonitor());
                CommonUITool.addNodeToTree(treeViewer, node, newNode);
            }
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    newNode, CubridNodeChangedEventType.NODE_ADD));
        }
    }

    private String getClassName(String tableName) {
        if (isSupportUserSchema) {
            int idx = tableName.indexOf(".");
            if (idx > 0) {
                return tableName.substring(idx + 1);
            }
        }
        return tableName;
    }

    private String getOwnerName(String tableName) {
        if (isSupportUserSchema) {
            int idx = tableName.indexOf(".");
            if (idx > 0) {
                return tableName.substring(0, idx);
            }
        }
        return tableName;
    }
}
