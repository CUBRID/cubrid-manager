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
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.ui.cubrid.synonym.dialog.CreateSynonymDialog;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.loader.CubridSynonymFolderLoader;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.user.task.JDBCGetAllDbUserTask;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class NewSynonymAction extends SelectionAction {

    public static final String ID = NewSynonymAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public NewSynonymAction(
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
    public NewSynonymAction(
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
        if (!ActionSupportUtil.isSupportMultiSelection(obj, null, false)) {
            return false;
        }

        if (ApplicationType.CUBRID_MANAGER.equals(
                PerspectiveManager.getInstance().getCurrentMode())) {
            return true;
        }

        CubridDatabase database = null;
        if (obj instanceof ISchemaNode) {
            ISchemaNode node = (ISchemaNode) obj;
            database = node.getDatabase();
        } else if (obj instanceof Object[]) {
            Object[] objArr = (Object[]) obj;
            ISchemaNode node = (ISchemaNode) objArr[0];
            database = node.getDatabase();
        }
        if (database != null
                && database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority()) {
            return true;
        }
        return false;
    }

    /** Create Synonym */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISchemaNode Node = (ISchemaNode) obj[0];
        CubridDatabase database = Node.getDatabase();
        run(database);
    }

    /**
     * Create Synonym
     *
     * @param database CubridDatabase
     */
    public void run(CubridDatabase database) {
        TaskExecutor taskExcutor = new CommonTaskExec(null);

        JDBCGetAllDbUserTask task = new JDBCGetAllDbUserTask(database.getDatabaseInfo());
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }

        List<String> dbUserList = task.getDbUserList();

        CreateSynonymDialog dlg = new CreateSynonymDialog(getShell(), database, dbUserList);
        ISelectionProvider provider = getSelectionProvider();
        if (dlg.open() == IDialogConstants.OK_ID && (provider instanceof TreeViewer)) {
            TreeViewer treeViewer = (TreeViewer) provider;
            ICubridNode folderNode =
                    database.getChild(
                            database.getId()
                                    + ICubridNodeLoader.NODE_SEPARATOR
                                    + CubridSynonymFolderLoader.SYNONYM_FOLDER_ID);
            if (folderNode == null || !folderNode.getLoader().isLoaded()) {
                return;
            }
            String synonymName = dlg.getSynonym().getName().toLowerCase(Locale.getDefault());
            String ownerName = dlg.getSynonym().getOwner();
            String id =
                    folderNode.getId()
                            + ICubridNodeLoader.NODE_SEPARATOR
                            + ownerName
                            + "."
                            + synonymName;

            Synonym synonym = dlg.getSynonym();
            ICubridNode newNode =
                    CubridSynonymFolderLoader.createSynonymNode(
                            database.getDatabaseInfo(), id, synonym);
            CommonUITool.addNodeToTree(treeViewer, folderNode, newNode);
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, folderNode);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    newNode, CubridNodeChangedEventType.NODE_ADD));
        }
    }
}
