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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.RenameTableDialog;
import com.cubrid.common.ui.cubrid.table.editor.TableEditorInput;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetTablesTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.RenameTableOrViewTask;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * This action is responsible to rename table data.
 *
 * @author robin 2009-6-4
 */
public class RenameTableAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(RenameTableAction.class);
    public static final String ID = RenameTableAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public RenameTableAction(Shell shell, String text, ImageDescriptor icon) {
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
    public RenameTableAction(
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
                obj,
                new String[] {
                    NodeType.USER_TABLE, NodeType.USER_PARTITIONED_TABLE_FOLDER, NodeType.USER_VIEW
                });
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        ISchemaNode table = (ISchemaNode) obj[0];

        doRun(table.getDatabase(), table);
    }

    public void run(CubridDatabase cubridDatabase, ISchemaNode table) {

        doRun(cubridDatabase, table);
    }

    /**
     * Perform rename Table
     *
     * @param cubridDatabase
     * @param table
     */
    private void doRun(CubridDatabase cubridDatabase, ISchemaNode table) {
        boolean isTable = false;
        String type = table.getType();
        if (NodeType.USER_TABLE.equals(type)
                || NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) {
            isTable = true;
        } else if (NodeType.USER_VIEW.equals(type)) {
            isTable = false;
        }

        String tableName = table.getName();
        CubridDatabase db = table.getDatabase();
        DatabaseInfo dbInfo = db.getDatabaseInfo();
        boolean isSupportUserSchema = dbInfo.isSupportUserSchema();
        GetTablesTask getTableTask = new GetTablesTask(dbInfo);
        List<String> tableList = getTableTask.getAllTableAndViews();

        RenameTableDialog dlg =
                new RenameTableDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        tableName,
                        isTable,
                        tableList,
                        true,
                        isSupportUserSchema);
        int ret = dlg.open();
        if (ret == IDialogConstants.OK_ID) {
            String newClassName = dlg.getNewName();
            String newTableName;
            if (isSupportUserSchema) {
                newTableName = dlg.getOwnerName() + "." + newClassName;
            } else {
                newTableName = newClassName;
            }
            RenameTableOrViewTask task = new RenameTableOrViewTask(dbInfo);
            task.setOldClassName(tableName);
            task.setNewClassName(newTableName);
            task.setTable(isTable);
            String taskName =
                    Messages.bind(
                            com.cubrid.common.ui.cubrid.table.Messages.renameTableTaskName,
                            new String[] {tableName, newTableName});
            TaskExecutor taskExecutor = new CommonTaskExec(taskName);
            taskExecutor.addTask(task);
            new ExecTaskWithProgress(taskExecutor).exec();

            if (taskExecutor.isSuccess()) {
                ISelectionProvider provider = this.getSelectionProvider();
                final TreeViewer viewer = (TreeViewer) provider;
                // remove the old table schema information
                table.getDatabase().getDatabaseInfo().removeSchema(tableName);

                DefaultSchemaNode cloneTable = null;
                try {
                    cloneTable = ((DefaultSchemaNode) table).clone();
                    CubridNodeManager.getInstance()
                            .fireCubridNodeChanged(
                                    new CubridNodeChangedEvent(
                                            cloneTable, CubridNodeChangedEventType.NODE_REMOVE));
                } catch (CloneNotSupportedException ex) {
                    LOGGER.error(ex.getMessage());
                }

                ClassInfo classInfo = (ClassInfo) table.getAdapter(ClassInfo.class);
                classInfo.setClassName(newClassName);
                classInfo.setSupportUserSchema(dbInfo.isSupportUserSchema());
                table.setId(
                        table.getParent().getId()
                                + ICubridNodeLoader.NODE_SEPARATOR
                                + newTableName);
                if (isSupportUserSchema) {
                    String label = "[" + dlg.getOwnerName() + "] " + newClassName;
                    table.setLabel(label);
                } else {
                    String label = "[" + dlg.getOwnerName() + "] " + newClassName;
                    table.setLabel(newTableName);
                }
                table.setUniqueName(newTableName);
                viewer.refresh(table, true);
                LayoutManager.getInstance().getWorkbenchContrItem().reopenEditorOrView(table);

                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        table, CubridNodeChangedEventType.NODE_ADD));
                ActionManager.getInstance().fireSelectionChanged(getSelection());
                /*Broadcast the view changed*/
                QueryEditorUtil.fireSchemaNodeChanged(table);

                /*For bug TOOLS-3118: close opened TableEditorPart about dropped table*/
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                for (IEditorReference editorRef :
                        workbenchWindow.getActivePage().getEditorReferences()) {
                    IEditorPart editor = editorRef.getEditor(true);
                    if (editor.getEditorInput() instanceof TableEditorInput) {
                        TableEditorInput input = (TableEditorInput) editor.getEditorInput();
                        ISchemaNode tableOfEditor = input.getEditedTableNode();
                        if (tableOfEditor.equals(table)) {
                            workbenchWindow.getActivePage().closeEditor(editor, false);
                            break;
                        }
                    }
                }
            }
        }
    }
}
