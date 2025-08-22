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

package com.cubrid.common.ui.cubrid.procedure.action;

import com.cubrid.common.ui.cubrid.procedure.Messages;
import com.cubrid.common.ui.cubrid.procedure.dialog.EditFunctionDialog;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo;
import com.cubrid.cubridmanager.core.cubrid.sp.task.GetSPInfoListTask;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to edit function
 *
 * @author robin 2009-3-18
 */
public class EditFunctionAction extends SelectionAction {

    public static final String ID = EditFunctionAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public EditFunctionAction(Shell shell, String text, ImageDescriptor icon) {
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
    public EditFunctionAction(
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
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSinSelCheckDbUser(
                obj, NodeType.STORED_PROCEDURE_FUNCTION);
    }

    /** Open the EditFunctionDialog and edit function */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (!isSupported(objArr)) {
            this.setEnabled(false);
            return;
        }
        Shell shell = getShell();
        CubridDatabase database = null;
        ISchemaNode node = null;
        if (objArr[0] instanceof ISchemaNode
                && NodeType.STORED_PROCEDURE_FUNCTION.equals(((ISchemaNode) objArr[0]).getType())) {
            node = (ISchemaNode) objArr[0];
            database = node.getDatabase();
        }
        if (database == null || node == null) {
            CommonUITool.openErrorBox(shell, Messages.errSelectProcedure);
            return;
        }

        final GetSPInfoListTask task = new GetSPInfoListTask(database.getDatabaseInfo());
        task.setSpName(node.getName());

        TaskExecutor taskExcutor = new CommonTaskExec(getText());
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }

        List<SPInfo> list = task.getSPInfoList();
        if (list.size() > 1) {
            CommonUITool.openErrorBox(shell, Messages.errDuplicateName);
            return;
        }
        if (list.isEmpty()) {
            CommonUITool.openErrorBox(shell, Messages.errNotExistName);
            return;
        }

        EditFunctionDialog dlg = new EditFunctionDialog(shell);
        dlg.setDatabase(database);
        dlg.setNewFlag(false);
        dlg.setSpInfo(list.get(0));
        if (dlg.open() == IDialogConstants.OK_ID) {
            ActionManager.getInstance().fireSelectionChanged(getSelection());
        }
    }
}
