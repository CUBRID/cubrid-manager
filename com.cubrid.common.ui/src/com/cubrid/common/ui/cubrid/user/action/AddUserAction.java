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

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.cubrid.user.dialog.EditUserDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllPartitionClassTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetUserAuthorizationsTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetUserListTask;
import java.sql.Connection;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * This action is responsible to add the user
 *
 * @author robin 2009-3-18
 */
public class AddUserAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(AddUserAction.class);
    public static final String ID = AddUserAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public AddUserAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public AddUserAction(
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
            if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
                return true;
            }
        }
        return false;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ISchemaNode node = (ISchemaNode) obj[0];

        doRun(node);
    }

    /**
     * Perform do run
     *
     * @param node
     */
    public void doRun(ISchemaNode node) { // FIXME move this logic to core module
        CubridDatabase database = node.getDatabase();
        //		if (database.getDatabaseInfo().isHAMode()) {
        //			CommonUITool.openErrorBox(Messages.errNoSupportInHA);
        //			return;
        //		}
        String childId =
                database.getId()
                        + ICubridNodeLoader.NODE_SEPARATOR
                        + CubridNodeLoader.USERS_FOLDER_ID;
        ICubridNode folderNode = database.getChild(childId);
        if (folderNode == null) {
            return;
        }

        EditUserDialog dlg = new EditUserDialog(getShell());
        Connection con = null;
        try {
            con = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);

            final GetAllClassListTask classInfoTask =
                    new GetAllClassListTask(database.getDatabaseInfo(), con);
            final GetAllPartitionClassTask partitionTask =
                    new GetAllPartitionClassTask(database.getDatabaseInfo(), con);
            GetUserListTask task = new GetUserListTask(database.getDatabaseInfo(), con);
            DbUserInfoList userListInfo = null;
            try {
                userListInfo = task.getResultModel();
            } catch (Exception e) {
                LOGGER.error("load user failed", e);
                return;
            }
            boolean isSuccess =
                    dlg.execTask(-1, new ITask[] {classInfoTask, partitionTask}, getShell());
            if (!isSuccess) {
                return;
            }

            dlg.setUserListInfo(userListInfo);
            dlg.setDatabase(database);
            dlg.setUserName(node.getName());
            dlg.setPartitionClassMap(partitionTask.getPartitionClassMap());
            dlg.setNewFlag(true);

            GetUserAuthorizationsTask privilegeTask =
                    new GetUserAuthorizationsTask(database.getDatabaseInfo(), con);
            try {
                for (DbUserInfo userInfo : userListInfo.getUserList()) {
                    if (userInfo.getName().equals(node.getName())) {
                        userInfo.setUserAuthorizations(
                                privilegeTask.getUserAuthorizations(userInfo.getName()));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("get user failed", e);
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            QueryUtil.freeQuery(con);
        }

        if (dlg.open() == IDialogConstants.OK_ID) {
            // Don't count the child
            if (!folderNode.getLoader().isLoaded()) {
                return;
            }
            CubridNavigatorView navigatorView = CubridNavigatorView.findNavigationView();
            if (navigatorView != null) {
                TreeViewer treeViewer = navigatorView.getViewer();
                if (treeViewer != null) {
                    CubridNodeChangedEvent event =
                            new CubridNodeChangedEvent(
                                    folderNode, CubridNodeChangedEventType.NODE_REFRESH);
                    CubridNodeManager.getInstance().fireCubridNodeChanged(event);
                    CommonUITool.updateFolderNodeLabelIncludingChildrenCount(
                            treeViewer, folderNode);
                } else {
                    CommonUITool.refreshNavigatorTree(treeViewer, node.getParent());
                    setEnabled(false);
                }
            }
            ActionManager.getInstance().fireSelectionChanged(getSelection());
        }
    }
}
