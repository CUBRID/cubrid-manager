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
package com.cubrid.common.ui.cubrid.serial.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.serial.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.serial.task.DeleteSerialTask;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * This action is responsible to delete serial
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-8 created by pangqiren
 */
public class DeleteSerialAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(DeleteSerialAction.class);
    public static final String ID = DeleteSerialAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public DeleteSerialAction(Shell shell, String text, ImageDescriptor icon) {
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
    public DeleteSerialAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return true
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelCheckDbUser(obj, NodeType.SERIAL);
    }

    /** Delete the selected serials */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || !isSupported(objArr)) {
            setEnabled(false);
            return;
        }
        ISchemaNode nodeArray[] = new ISchemaNode[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            nodeArray[i] = (ISchemaNode) objArr[i];
        }
        run(nodeArray);
    }

    /** Delete the selected serials */
    public void run(ISchemaNode[] nodeArray) {
        if (nodeArray == null) {
            LOGGER.error("The nodeArray parameter is a null.");
            return;
        }

        final List<String> serialNameList = new ArrayList<String>();
        final StringBuffer serialNames = new StringBuffer();
        for (int i = 0; nodeArray != null && i < nodeArray.length; i++) {
            if (!isSupported(nodeArray[i])) {
                setEnabled(false);
                return;
            }
            ISchemaNode schemaNode = (ISchemaNode) nodeArray[i];
            if (i == 0) {
                serialNames.append(schemaNode.getName());
            }
            serialNameList.add(schemaNode.getName());
        }
        if (nodeArray.length > 1) {
            serialNames.append(", ...");
        }
        String cfmMsg =
                Messages.bind(
                        Messages.msgConfirmDelSerial, serialNames.toString(), nodeArray.length);
        boolean isDelete = CommonUITool.openConfirmBox(getShell(), cfmMsg);
        if (!isDelete) {
            return;
        }

        final Shell shell = getShell();
        TaskExecutor taskExcutor =
                new TaskExecutor() {
                    public boolean exec(final IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return false;
                        }

                        String taskName =
                                Messages.bind(Messages.delSerialTaskName, serialNames.toString());
                        monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
                        for (ITask task : taskList) {
                            if (task instanceof DeleteSerialTask) {
                                DeleteSerialTask deleteSerialTask = (DeleteSerialTask) task;
                                String[] serialNames = new String[serialNameList.size()];
                                deleteSerialTask.deleteSerial(serialNameList.toArray(serialNames));
                            }

                            final String msg = task.getErrorMsg();
                            if (openErrorBox(shell, msg, monitor)) {
                                return false;
                            }

                            if (monitor.isCanceled()) {
                                return false;
                            }
                        }
                        return true;
                    }
                };
        ISchemaNode schemaNode = (ISchemaNode) nodeArray[0];
        CubridDatabase database = schemaNode.getDatabase();
        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        DeleteSerialTask deleteSerialTask = new DeleteSerialTask(databaseInfo);
        taskExcutor.addTask(deleteSerialTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return;
        }
        ISelectionProvider provider = this.getSelectionProvider();
        ICubridNode parent = schemaNode.getParent();
        if (provider instanceof TreeViewer) {
            TreeViewer viewer = (TreeViewer) provider;
            for (int i = 0; nodeArray != null && i < nodeArray.length; i++) {
                parent.removeChild((ICubridNode) nodeArray[i]);
            }
            viewer.remove(parent, nodeArray);
            viewer.setSelection(new StructuredSelection(parent), true);
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(viewer, parent);
        }
    }
}
