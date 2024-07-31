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

import com.cubrid.common.ui.cubrid.table.Messages;
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
import com.cubrid.cubridmanager.core.cubrid.table.task.DropTableOrViewTask;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to drop table .
 *
 * @author robin 2009-6-4
 */
public class DropViewAction extends SelectionAction {
    public static final String ID = DropViewAction.class.getName();
    private boolean canceledTask = false;

    public DropViewAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public DropViewAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(
                obj, new String[] {NodeType.USER_VIEW}, false);
    }

    public void run(ISchemaNode[] nodeArray) {
        if (nodeArray == null || nodeArray.length == 0) {
            return;
        }

        int selectedCount = nodeArray.length;
        ISchemaNode table = nodeArray[0];
        String type = table.getType();
        String message = null;

        if (NodeType.USER_VIEW.equals(type)) {
            message = Messages.bind(Messages.dropView, selectedCount);
        }

        boolean ret = CommonUITool.openConfirmBox(message);
        if (!ret) {
            canceledTask = true;
            return;
        }

        String taskName = Messages.bind(Messages.dropTableTaskName, selectedCount);
        TaskExecutor taskExecutor = new CommonTaskExec(taskName);
        DropTableOrViewTask task = new DropTableOrViewTask(table.getDatabase().getDatabaseInfo());
        List<String> viewNameList = new ArrayList<String>();
        for (int i = 0; i < selectedCount; i++) {
            table = (DefaultSchemaNode) nodeArray[i];
            type = table.getType();
            if (NodeType.USER_VIEW.equals(type)) {
                viewNameList.add(table.getName());
            }
        }

        String[] viewNames = new String[viewNameList.size()];
        viewNames = viewNameList.toArray(viewNames);
        task.setViewName(viewNames);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();

        if (taskExecutor.isSuccess()) {
            ISelectionProvider provider = this.getSelectionProvider();
            final TreeViewer viewer = (TreeViewer) provider;
            ICubridNode parent = table.getParent();
            table.getDatabase().getDatabaseInfo().removeSchema(table.getName());
            for (int i = 0; i < selectedCount; i++) {
                parent.removeChild(nodeArray[i]);
                /*Broadcast the view changed*/
                QueryEditorUtil.fireSchemaNodeChanged(nodeArray[i]);
            }
            viewer.remove(parent, nodeArray);
            viewer.setSelection(new StructuredSelection(parent), true);
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(viewer, parent);
        }
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        ISchemaNode nodeArray[] = new ISchemaNode[obj.length];
        for (int i = 0; i < obj.length; i++) {
            nodeArray[i] = (ISchemaNode) obj[i];
        }

        run(nodeArray);
    }

    public boolean isCanceledTask() {
        return canceledTask;
    }
}
