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

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.cubrid.table.editor.TableEditorInput;
import com.cubrid.common.ui.cubrid.table.editor.TableEditorPart;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.NodeUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetCollations;
import com.cubrid.cubridmanager.core.cubrid.user.task.JDBCGetAllDbUserTask;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This action is responsible to edit table.
 *
 * @author robin 2009-6-4
 */
public class EditTableAction extends SelectionAction {
    public static final String ID = EditTableAction.class.getName();
    public static final int MODE_TABLE_EDIT = 1;
    public static final int MODE_INDEX_EDIT = 2;
    public static final int MODE_FK_EDIT = 3;

    public EditTableAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public EditTableAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSingleSelection(
                obj, new String[] {NodeType.USER_TABLE, NodeType.USER_PARTITIONED_TABLE_FOLDER});
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISelectionProvider provider = getSelectionProvider();
        if (!(provider instanceof TreeViewer)) {
            return;
        }
        ISchemaNode tableNode = (ISchemaNode) obj[0];
        doRun(tableNode, MODE_TABLE_EDIT);
    }

    public void run(ISchemaNode tableNode) {
        doRun(tableNode, MODE_TABLE_EDIT);
    }

    public void editIndexMode(ISchemaNode tableNode) {
        doRun(tableNode, MODE_INDEX_EDIT);
    }

    private void doRun(ISchemaNode table, int type) {
        final DatabaseInfo databaseInfo = NodeUtil.findDatabaseInfo(table);
        if (databaseInfo == null) {
            return;
        }

        final String tableName = table.getName();
        TaskExecutor taskExcutor =
                new TaskExecutor() {
                    public boolean exec(final IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return false;
                        }

                        for (ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (openErrorBox(shell, msg, monitor)) {
                                return false;
                            }
                            if (monitor.isCanceled()) {
                                return false;
                            }
                        }
                        databaseInfo.removeSchema(tableName);

                        SchemaInfo schemaInfo = databaseInfo.getSchemaInfo(tableName);
                        if (schemaInfo == null) {
                            openErrorBox(shell, databaseInfo.getErrorMessage(), monitor);
                            return false;
                        }

                        return true;
                    }
                };

        boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);

        JDBCGetAllDbUserTask allUserTask = new JDBCGetAllDbUserTask(databaseInfo);
        taskExcutor.addTask(allUserTask);

        GetCollations collationTask = null;
        if (supportCharset) {
            collationTask = new GetCollations(databaseInfo);
            taskExcutor.addTask(collationTask);
        }

        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }

        List<String> dbUserList = allUserTask.getDbUserList();
        SchemaInfo schemaInfo = databaseInfo.getSchemaInfo(tableName);

        TableEditorInput input =
                new TableEditorInput(table.getDatabase(), false, schemaInfo, table, type);
        input.setDbUserList(dbUserList);

        if (supportCharset) {
            List<Collation> collations = collationTask.getCollations();
            input.setCollationList(collations);
        }

        try {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

            // find whether selected table is being edited.
            // If true, active opened editor of it and return, else open new editor.
            for (IEditorReference editorRef :
                    workbenchWindow.getActivePage().getEditorReferences()) {
                IEditorPart oldEditor = editorRef.getEditor(true);
                if (oldEditor.getEditorInput() instanceof TableEditorInput) {
                    TableEditorInput oldInput = (TableEditorInput) oldEditor.getEditorInput();
                    ISchemaNode oldTable = oldInput.getEditedTableNode();
                    if (oldTable != null && oldTable.equals(table)) {
                        workbenchWindow.getActivePage().activate(oldEditor);
                        return;
                    }
                }
            }

            workbenchWindow.getActivePage().openEditor(input, TableEditorPart.ID);
        } catch (Exception ignore) {
        }
    }
}
