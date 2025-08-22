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

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.cubrid.table.dialog.CreateViewDialog;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetViewAllColumnsTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.JDBCGetAllDbUserTask;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * This action is responsible to edit view.
 *
 * @author robin 2009-6-4
 */
public class EditViewAction extends SelectionAction {

    public static final String ID = EditViewAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public EditViewAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
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
    public EditViewAction(
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
        return ActionSupportUtil.isSupportSingleSelection(obj, new String[] {NodeType.USER_VIEW});
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISchemaNode node = (ISchemaNode) obj[0];
        CubridDatabase database = node.getDatabase();
        run(database, node);
    }

    /**
     * run edit view
     *
     * @param ISchemaNode viewNode
     * @param database CubridDatabase
     */
    public int run(CubridDatabase database, ISchemaNode node) {
        CreateViewDialog dialog =
                new CreateViewDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        database,
                        false);

        GetAllClassListTask getAllClassListTask =
                new GetAllClassListTask(database.getDatabaseInfo());
        getAllClassListTask.setTableName(node.getName());

        GetViewAllColumnsTask getAllDBVclassTask =
                new GetViewAllColumnsTask(database.getDatabaseInfo());
        getAllDBVclassTask.setClassName(node.getName());

        GetAllAttrTask getAllAttrTask = new GetAllAttrTask(database.getDatabaseInfo());
        getAllAttrTask.setClassName(node.getName());

        JDBCGetAllDbUserTask getAllDbUserTask =
                new JDBCGetAllDbUserTask(database.getDatabaseInfo());

        dialog.execTask(
                -1,
                new ITask[] {
                    getAllClassListTask, getAllDBVclassTask, getAllAttrTask, getAllDbUserTask
                },
                true,
                getShell());
        if (getAllClassListTask.getErrorMsg() != null
                || getAllDBVclassTask.getErrorMsg() != null
                || getAllAttrTask.getErrorMsg() != null
                || getAllDbUserTask.getErrorMsg() != null
                || getAllClassListTask.isCancel()
                || getAllDBVclassTask.isCancel()
                || getAllAttrTask.isCancel()
                || getAllDbUserTask.isCancel()) {

            return IDialogConstants.CANCEL_ID;
        }
        ClassInfo classInfo = getAllClassListTask.getClassInfo();
        List<String> vclassList = getAllDBVclassTask.getAllVclassList();
        List<DBAttribute> attrList = getAllAttrTask.getAllAttrList();
        List<String> dbUserList = getAllDbUserTask.getDbUserList();

        dialog.setAttrList(attrList);
        dialog.setClassInfo(classInfo);
        dialog.setVclassList(vclassList);
        dialog.setDbUserList(dbUserList);

        if (dialog.open() == IDialogConstants.OK_ID) {
            String viewName = node.getName();
            String newViewName = dialog.getNewViewName();
            String owner = dialog.getOwner();
            node.getDatabase().getDatabaseInfo().removeSchema(viewName);
            ISelectionProvider provider = getSelectionProvider();
            TreeViewer treeViewer = (TreeViewer) provider;
            if (!viewName.equals(newViewName)) {

                ClassInfo newClassInfo = (ClassInfo) node.getAdapter(ClassInfo.class);
                newClassInfo.setClassName(newViewName);
                newClassInfo.setOwnerName(owner);
                newClassInfo.setSupportUserSchema(database.getDatabaseInfo().isSupportUserSchema());
                node.setId(
                        node.getParent().getId() + ICubridNodeLoader.NODE_SEPARATOR + newViewName);
                node.setLabel(newViewName);
                treeViewer.refresh(node, true);
            }
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, node.getParent());
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    node, CubridNodeChangedEventType.NODE_REFRESH));
            ActionManager.getInstance().fireSelectionChanged(getSelection());

            /*Broadcast the view changed*/
            QueryEditorUtil.fireSchemaNodeChanged(node);
            return IDialogConstants.OK_ID;
        }
        return IDialogConstants.CANCEL_ID;
    }
}
