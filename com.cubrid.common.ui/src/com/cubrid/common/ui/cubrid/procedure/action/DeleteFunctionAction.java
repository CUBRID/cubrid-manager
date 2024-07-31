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
package com.cubrid.common.ui.cubrid.procedure.action;

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.cubrid.procedure.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.task.CommonSQLExcuterTask;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to delete function
 *
 * @author robin 2009-3-18
 */
public class DeleteFunctionAction extends SelectionAction {
    public static final String ID = DeleteFunctionAction.class.getName();

    public DeleteFunctionAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public DeleteFunctionAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelCheckDbUser(
                obj, NodeType.STORED_PROCEDURE_FUNCTION);
    }

    public void run() { // FIXME logic code move to core module
        Object[] objects = this.getSelectedObj();
        if (objects == null || !isSupported(objects)) {
            setEnabled(false);
            return;
        }

        Shell shell = getShell();
        CubridDatabase database = null;
        ISchemaNode node = null;
        if (objects[0] instanceof ISchemaNode
                && NodeType.STORED_PROCEDURE_FUNCTION.equals(
                        ((ISchemaNode) objects[0]).getType())) {
            node = (ISchemaNode) objects[0];
            database = node.getDatabase();
        }
        if (database == null || node == null) {
            CommonUITool.openErrorBox(shell, Messages.errSelectFunction);
            return;
        }
        if (!CommonUITool.openConfirmBox(shell, Messages.msgSureDropFunction)) {
            return;
        }

        CommonSQLExcuterTask task = new CommonSQLExcuterTask(database.getDatabaseInfo());
        for (Object object : objects) {
            node = (ISchemaNode) object;
            String sql = "DROP FUNCTION " + QuerySyntax.escapeKeyword(node.getName());
            task.addSqls(sql);
        }

        TaskExecutor taskExcutor = new CommonTaskExec(getText());
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }

        ISelectionProvider provider = this.getSelectionProvider();
        ICubridNode parent = node.getParent();
        if (provider instanceof TreeViewer) {
            TreeViewer viewer = (TreeViewer) provider;
            for (int i = 0; objects != null && i < objects.length; i++) {
                parent.removeChild((ISchemaNode) objects[i]);
            }
            viewer.remove(parent, objects);
            viewer.setSelection(new StructuredSelection(parent), true);
        }
    }
}
