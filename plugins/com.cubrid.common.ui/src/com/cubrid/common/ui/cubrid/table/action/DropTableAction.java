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

import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.editor.TableEditorInput;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.DropTableOrViewTask;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * This action is responsible to drop table .
 *
 * @author robin 2009-6-4
 */
public class DropTableAction extends SelectionAction {
    public static final String ID = DropTableAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(DropTableAction.class);

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public DropTableAction(Shell shell, String text, ImageDescriptor icon) {
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
    public DropTableAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
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
        return ActionSupportUtil.isSupportMultiSelection(
                obj,
                new String[] {NodeType.USER_TABLE, NodeType.USER_PARTITIONED_TABLE_FOLDER},
                false);
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        int len = obj.length; // FIXME move this logic to core module
        StringBuilder sb = new StringBuilder();
        ISchemaNode table = (ISchemaNode) obj[0];
        String type = table.getType();
        for (int i = 0; i < len && i < 100; i++) {
            table = (DefaultSchemaNode) obj[i];
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(table.getName());
        }
        if (len > 100) {
            sb.append("...");
        }
        String message = null;

        if (NodeType.USER_TABLE.equals(type)
                || NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) {
            message = Messages.bind(Messages.dropTable, sb.toString());
        }

        boolean ret = CommonUITool.openConfirmBox(message);
        if (!ret) {
            return;
        }
        String taskName = Messages.bind(Messages.dropTableTaskName, sb.toString());
        TaskExecutor taskExecutor = new CommonTaskExec(taskName);
        DropTableOrViewTask task = new DropTableOrViewTask(table.getDatabase().getDatabaseInfo());
        List<String> tableNameList = new ArrayList<String>();

        for (int i = 0; i < len; i++) {
            table = (DefaultSchemaNode) obj[i];
            tableNameList.add(table.getName());
        }
        String[] tableNames = new String[tableNameList.size()];
        tableNames = tableNameList.toArray(tableNames);
        task.setTableName(tableNames);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            // delete table/column descriptions which is dropping table.
            DatabaseInfo dbInfo = table.getDatabase().getDatabaseInfo();
            Connection conn = null;
            try {
                conn = JDBCConnectionManager.getConnection(dbInfo, false);
                IDatabaseSpec dbSpec = table.getDatabase().getDatabaseInfo();
                boolean isSupportTableComment =
                        SchemaCommentHandler.isInstalledMetaTable(dbSpec, conn);
                if (isSupportTableComment) {
                    for (int i = 0; i < len; i++) {
                        table = (DefaultSchemaNode) obj[i];
                        SchemaCommentHandler.deleteDescription(dbInfo, conn, table.getName());
                    }
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                QueryUtil.freeQuery(conn);
            }

            ISelectionProvider provider = this.getSelectionProvider();
            final TreeViewer viewer = (TreeViewer) provider;
            ICubridNode parent = table.getParent();
            table.getDatabase().getDatabaseInfo().removeSchema(table.getName());
            for (int i = 0; i < len; i++) {
                parent.removeChild((ISchemaNode) obj[i]);
                /*Broadcast the view changed*/
                QueryEditorUtil.fireSchemaNodeChanged((ISchemaNode) obj[i]);
            }
            viewer.remove(parent, obj);
            viewer.setSelection(new StructuredSelection(parent), true);

            // refresh user folder count label
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(viewer, parent);

            /*For bug TOOLS-3118: close opened TableEditorPart about dropped table*/
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            for (IEditorReference editorRef :
                    workbenchWindow.getActivePage().getEditorReferences()) {
                IEditorPart editor = editorRef.getEditor(true);
                if (editor.getEditorInput() instanceof TableEditorInput) {
                    TableEditorInput input = (TableEditorInput) editor.getEditorInput();
                    ISchemaNode tableOfEditor = input.getEditedTableNode();
                    for (int i = 0; i < len; i++) {
                        if (tableOfEditor.equals((ISchemaNode) obj[i])) {
                            workbenchWindow.getActivePage().closeEditor(editor, false);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Run
     *
     * @param nodes
     */
    public void run(ICubridNode[] nodes) {
        doRun(nodes);
    }

    /**
     * Do run
     *
     * @param obj
     */
    private void doRun(Object[] obj) {
        int len = obj.length;
        StringBuilder sb = new StringBuilder();
        ISchemaNode table = (ISchemaNode) obj[0];
        String type = table.getType(); // FIXME move this logic to core module
        for (int i = 0; i < len && i < 100; i++) {
            table = (DefaultSchemaNode) obj[i];
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(table.getName());
        }
        if (len > 100) {
            sb.append("...");
        }
        String message = null;

        if (NodeType.USER_TABLE.equals(type)
                || NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) {
            message = Messages.bind(Messages.dropTable, sb.toString());
        }

        boolean ret = CommonUITool.openConfirmBox(message);
        if (!ret) {
            return;
        }
        String taskName = Messages.bind(Messages.dropTableTaskName, sb.toString());
        TaskExecutor taskExecutor = new CommonTaskExec(taskName);
        DropTableOrViewTask task = new DropTableOrViewTask(table.getDatabase().getDatabaseInfo());
        List<String> tableNameList = new ArrayList<String>();

        for (int i = 0; i < len; i++) {
            table = (DefaultSchemaNode) obj[i];
            tableNameList.add(table.getName());
        }
        String[] tableNames = new String[tableNameList.size()];
        tableNames = tableNameList.toArray(tableNames);
        task.setTableName(tableNames);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            // delete table/column descriptions which is dropping table.
            DatabaseInfo dbInfo = table.getDatabase().getDatabaseInfo();
            Connection conn = null;
            try {
                conn = JDBCConnectionManager.getConnection(dbInfo, false);
                IDatabaseSpec dbSpec = table.getDatabase().getDatabaseInfo();
                boolean isSupportTableComment =
                        SchemaCommentHandler.isInstalledMetaTable(dbSpec, conn);
                if (isSupportTableComment) {
                    for (int i = 0; i < len; i++) {
                        table = (DefaultSchemaNode) obj[i];
                        SchemaCommentHandler.deleteDescription(dbInfo, conn, table.getName());
                    }
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                QueryUtil.freeQuery(conn);
            }
            // TODO -KK
            TreeViewer treeViewer = CubridNavigatorView.findNavigationView().getViewer();
            ICubridNode parent = table.getParent();
            table.getDatabase().getDatabaseInfo().removeSchema(table.getName());
            for (int i = 0; i < len; i++) {
                parent.removeChild((ISchemaNode) obj[i]);
                /*Broadcast the view changed*/
                QueryEditorUtil.fireSchemaNodeChanged((ISchemaNode) obj[i]);
            }
            treeViewer.remove(parent, obj);
            treeViewer.setSelection(new StructuredSelection(parent), true);

            // refresh user folder count label
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, parent);

            /*For bug TOOLS-3118: close opened TableEditorPart about dropped table*/
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            for (IEditorReference editorRef :
                    workbenchWindow.getActivePage().getEditorReferences()) {
                IEditorPart editor = editorRef.getEditor(true);
                if (editor.getEditorInput() instanceof TableEditorInput) {
                    TableEditorInput input = (TableEditorInput) editor.getEditorInput();
                    ISchemaNode tableOfEditor = input.getEditedTableNode();
                    for (int i = 0; i < len; i++) {
                        if (tableOfEditor.equals((ISchemaNode) obj[i])) {
                            workbenchWindow.getActivePage().closeEditor(editor, false);
                            break;
                        }
                    }
                }
            }
        }
    }
}
