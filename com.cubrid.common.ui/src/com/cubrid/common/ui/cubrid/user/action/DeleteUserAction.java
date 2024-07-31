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

package com.cubrid.common.ui.cubrid.user.action;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.cubrid.user.Messages;
import com.cubrid.common.ui.cubrid.user.dialog.EditUserDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.DropUserTask;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Delete the user
 *
 * @author robin 2009-3-18
 */
public class DeleteUserAction extends SelectionAction {

    public static final String ID = DeleteUserAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     */
    public DeleteUserAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     */
    public DeleteUserAction(
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
        if (!(obj instanceof ISchemaNode)) {
            return false;
        }
        ISchemaNode node = (ISchemaNode) obj;
        CubridDatabase database = node.getDatabase();
        if (database != null
                && database.getRunningType() == DbRunningType.CS
                && database.isLogined()) {
            DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            if (dbUserInfo == null) {
                return false;
            }
            if (EditUserDialog.DB_DEFAULT_USERNAME.equalsIgnoreCase(node.getName())
                    || EditUserDialog.DB_DBA_USERNAME.equalsIgnoreCase(node.getName())) {
                return false;
            }
            if (dbUserInfo.getName() != null
                    && dbUserInfo.getName().equalsIgnoreCase(node.getName())) {
                return false;
            }
            if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
                return true;
            }
        }
        return false;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (obj.length == 1 && obj[0] instanceof DefaultCubridNode) {
            ISchemaNode schemaNode = (ISchemaNode) obj[0];
            String msg = Messages.bind(Messages.msgDoYouWantToDeleteUser, schemaNode.getName());
            if (!CommonUITool.openConfirmBox(getShell(), msg)) {
                return;
            }
            doRun(schemaNode);
        }
    }

    public void doRun(ISchemaNode node) { // FIXME move this logic to core module
        CubridDatabase database = node.getDatabase();
        if (database == null || node == null) {
            CommonUITool.openErrorBox(getShell(), Messages.msgSelectDB);
            return;
        }

        String childId =
                database.getId()
                        + ICubridNodeLoader.NODE_SEPARATOR
                        + CubridNodeLoader.USERS_FOLDER_ID;
        ICubridNode folderNode = database.getChild(childId);
        if (folderNode == null || !folderNode.getLoader().isLoaded()) {
            return;
        }
        //		if (database.getDatabaseInfo().isHAMode()) {
        //
        //	CommonUITool.openErrorBox(com.cubrid.cubridmanager.ui.common.Messages.errNoSupportInHA);
        //			return;
        //		}

        TaskExecutor taskExecutor = new CommonTaskExec(CommonTaskName.DELETE_USER_TASK_NAME);
        DropUserTask task = new DropUserTask(database.getDatabaseInfo(), node.getName());
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).busyCursorWhile();

        if (taskExecutor.isSuccess()) {
            CubridNavigatorView navigatorView = CubridNavigatorView.findNavigationView();
            if (navigatorView != null) {
                TreeViewer treeViewer = navigatorView.getViewer();
                if (treeViewer != null) {
                    // refresh user folder count label
                    CubridNodeChangedEvent event =
                            new CubridNodeChangedEvent(
                                    folderNode, CubridNodeChangedEventType.NODE_REFRESH);
                    CubridNodeManager.getInstance().fireCubridNodeChanged(event);
                    CommonUITool.updateFolderNodeLabelIncludingChildrenCount(
                            treeViewer, node.getParent());
                    CommonUITool.openInformationBox(
                            com.cubrid.common.ui.common.Messages.titleSuccess,
                            Messages.msgDeleteUserSuccess);
                }
            }
        }
    }
}
