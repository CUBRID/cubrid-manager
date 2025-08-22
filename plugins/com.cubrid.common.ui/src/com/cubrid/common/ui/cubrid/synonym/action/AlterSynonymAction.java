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
package com.cubrid.common.ui.cubrid.synonym.action;

import com.cubrid.common.core.common.model.Synonym;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.cubrid.synonym.Messages;
import com.cubrid.common.ui.cubrid.synonym.dialog.CreateSynonymDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.cubrid.synonym.JDBCGetSynonymInfoTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.JDBCGetAllDbUserTask;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public class AlterSynonymAction extends SelectionAction {

    public static final String ID = AlterSynonymAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public AlterSynonymAction(Shell shell, String text, ImageDescriptor icon) {
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
    public AlterSynonymAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
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
        return ActionSupportUtil.isSupportSinSelCheckDbUser(obj, NodeType.SYNONYM);
    }

    /** Open alter synonym dialog */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (!isSupported(objArr)) {
            this.setEnabled(false);
            return;
        }
        final ISchemaNode synonymNode = (ISchemaNode) objArr[0];
        CubridDatabase database = synonymNode.getDatabase();
        run(database, synonymNode);
    }

    /**
     * edit synonym
     *
     * @param database
     * @param node
     * @return
     */
    public int run(
            final CubridDatabase database,
            final ISchemaNode node) { // FIXME move this logic to core module
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
                            Synonym synonym = null;
                            JDBCGetSynonymInfoTask getSynonymInfoTask =
                                    (JDBCGetSynonymInfoTask) task;
                            synonym = getSynonymInfoTask.getSynonymInfo(node.getName());

                            if (synonym == null) {
                                openErrorBox(shell, Messages.errNameNoExist, monitor);
                                return false;
                            }

                            node.setModelObj(synonym);
                        }
                        return true;
                    }
                };
        ITask task = null;

        task = new JDBCGetSynonymInfoTask(database.getDatabaseInfo());
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return IDialogConstants.CANCEL_ID;
        }

        TaskExecutor userTaskExcutor = new CommonTaskExec(null);

        JDBCGetAllDbUserTask userTask = new JDBCGetAllDbUserTask(database.getDatabaseInfo());
        userTaskExcutor.addTask(userTask);
        new ExecTaskWithProgress(userTaskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return IDialogConstants.CANCEL_ID;
        }

        List<String> dbUserList = userTask.getDbUserList();

        CreateSynonymDialog dialog =
                new CreateSynonymDialog(
                        getShell(),
                        node.getDatabase(),
                        (Synonym) node.getAdapter(Synonym.class),
                        dbUserList);

        if (dialog.open() != IDialogConstants.CANCEL_ID) {
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    node, CubridNodeChangedEventType.NODE_REFRESH));
            ActionManager.getInstance().fireSelectionChanged(getSelection());
            return IDialogConstants.OK_ID;
        }
        return IDialogConstants.CANCEL_ID;
    }
}
