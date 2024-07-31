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

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.task.DelAllRecordsTask;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to delete table data.
 *
 * @author robin 2009-6-4
 */
public class DeleteTableAction extends SelectionAction {
    public static final String ID = DeleteTableAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public DeleteTableAction(Shell shell, String text, ImageDescriptor icon) {
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
    public DeleteTableAction(
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
        return ActionSupportUtil.isSupportMultiSelection(
                obj,
                new String[] {NodeType.USER_TABLE, NodeType.USER_PARTITIONED_TABLE_FOLDER},
                false);
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        doRun(obj);
    }

    /**
     * Run
     *
     * @param obj
     */
    public void run(ICubridNode[] obj) {
        doRun(obj);
    }

    /**
     * Do run
     *
     * @param obj
     */
    private void doRun(Object[] obj) {
        StringBuilder sb = new StringBuilder();
        final List<String> tableList = new ArrayList<String>();
        CubridDatabase database = null;
        for (int i = 0; i < obj.length; i++) {
            DefaultSchemaNode table = (DefaultSchemaNode) obj[i];
            database = table.getDatabase();
            final String tableName = table.getName();
            tableList.add(tableName);
            if (i < 100) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(tableName);
            }
        }
        if (obj.length > 100) {
            sb.append("...");
        }

        String message = Messages.bind(Messages.confirmTableDeleteWarn, sb.toString());

        if (!CommonUITool.openConfirmBox(message)) {
            return;
        }

        final DelAllRecordsTask task = new DelAllRecordsTask(database.getDatabaseInfo());
        TaskJobExecutor taskExec =
                new CommonTaskJobExec(
                        new ITaskExecutorInterceptor() {
                            public void completeAll() {
                                int[] rowCount = task.getDeleteRecordsCount();
                                List<String> rowCountList = new ArrayList<String>();
                                for (int i = 0; i < rowCount.length; i++) {
                                    rowCountList.add(String.valueOf(rowCount[i]));
                                }
                                String message =
                                        Messages.bind(
                                                Messages.resultTableDeleteInformantion,
                                                tableList,
                                                rowCountList);
                                CommonUITool.openInformationBox(Messages.msg_information, message);
                            }

                            public IStatus postTaskFinished(ITask task) {
                                return Status.OK_STATUS;
                            }
                        });

        String[] tableNames = new String[tableList.size()];
        tableNames = tableList.toArray(tableNames);
        task.setTableName(tableNames);
        taskExec.addTask(task);
        JobFamily jobFamily = new JobFamily();

        String serverName = database.getServer().getName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        String jobName =
                Messages.msgDeleteTableDataJobName
                        + " - "
                        + tableList.toString()
                        + "@"
                        + dbName
                        + "@"
                        + serverName;
        taskExec.schedule(jobName, jobFamily, false, Job.SHORT);
    }
}
