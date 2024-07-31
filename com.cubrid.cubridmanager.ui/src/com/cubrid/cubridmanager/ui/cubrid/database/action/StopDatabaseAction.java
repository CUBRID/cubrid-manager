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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to start database in standalone mode
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class StopDatabaseAction extends SelectionAction {
    public static final String ID = StopDatabaseAction.class.getName();

    public StopDatabaseAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public StopDatabaseAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return isSupportedNode(obj);
    }

    public static boolean isSupportedNode(Object obj) {
        if (obj instanceof ISchemaNode) {
            return ActionSupportUtil.hasAdminPermissionOnRunningState(obj);
        } else if (obj instanceof Object[]) {
            return true;
        } else {
            return false;
        }
    }

    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0) {
            setEnabled(false);
            return;
        }
        Set<CubridDatabase> databaseSet = new HashSet<CubridDatabase>();
        for (int i = 0; objArr != null && i < objArr.length; i++) {
            if (!isSupported(objArr[i])) {
                setEnabled(false);
                return;
            }
            ISchemaNode schemaNode = (ISchemaNode) objArr[i];
            CubridDatabase database = schemaNode.getDatabase();
            databaseSet.add(database);
        }
        StringBuffer dbNames = new StringBuffer();
        Iterator<CubridDatabase> it = databaseSet.iterator();
        while (it.hasNext()) {
            CubridDatabase database = it.next();
            dbNames.append(database.getLabel()).append(",");
        }
        dbNames = new StringBuffer(dbNames.substring(0, dbNames.length() - 1));
        boolean isStop =
                CommonUITool.openConfirmBox(
                        getShell(),
                        Messages.bind(Messages.msgConfirmStopDatabase, dbNames.toString()));
        if (!isStop) {
            return;
        }
        Iterator<CubridDatabase> iter = databaseSet.iterator();
        while (iter.hasNext()) {
            CubridDatabase database = iter.next();
            final JobFamily jobFamily = new JobFamily();
            String serverName = database.getServer().getName();
            String dbName = database.getName();
            jobFamily.setServerName(serverName);
            jobFamily.setDbName(dbName);
            Job[] jobs = Job.getJobManager().find(jobFamily);
            if (jobs.length > 0) {
                CommonUITool.openWarningBox(Messages.bind(Messages.msgStopDbWithJob, dbName));
                databaseSet.remove(database);
                continue;
            }
        }

        final Object[] dbObjectArr = new Object[databaseSet.size()];
        databaseSet.toArray(dbObjectArr);
        ISelectionProvider provider = getSelectionProvider();
        final Shell shell = getShell();
        if (provider instanceof TreeViewer && dbObjectArr.length > 0) {
            final TreeViewer viewer = (TreeViewer) provider;
            TaskExecutor taskExcutor =
                    new TaskExecutor() {
                        public boolean exec(final IProgressMonitor monitor) {
                            Display display = Display.getDefault();
                            if (monitor.isCanceled()) {
                                return false;
                            }
                            for (int i = 0; i < taskList.size(); i++) {
                                ISchemaNode node = (ISchemaNode) dbObjectArr[i];
                                final CubridDatabase database = node.getDatabase();
                                if (!isSupported(database)) {
                                    continue;
                                }
                                monitor.subTask(
                                        Messages.bind(Messages.stopDbTaskName, database.getName()));
                                ITask task = taskList.get(i);
                                task.execute();
                                final String msg = task.getErrorMsg();
                                if (openErrorBox(shell, msg, monitor)) {
                                    return false;
                                }
                                if (monitor.isCanceled()) {
                                    return false;
                                }
                                database.removeAllChild();
                                if (database.getLoader() != null) {
                                    database.getLoader().setLoaded(false);
                                }
                                database.setRunningType(DbRunningType.STANDALONE);
                                display.syncExec(
                                        new Runnable() {
                                            public void run() {
                                                viewer.refresh(database, true);
                                            }
                                        });
                                if (monitor.isCanceled()) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    };
            boolean isContinue = true;
            for (int i = 0; i < dbObjectArr.length; i++) {
                ISchemaNode node = (ISchemaNode) dbObjectArr[i];
                CubridDatabase database = node.getDatabase();
                if (!isSupported(database)) {
                    setEnabled(false);
                    return;
                }
                if (!LayoutUtil.checkAllQueryEditor(database)) {
                    isContinue = false;
                    break;
                }
                CommonUpdateTask task =
                        new CommonUpdateTask(
                                CommonTaskName.STOP_DB_TASK_NAME,
                                database.getServer().getServerInfo(),
                                CommonSendMsg.getCommonDatabaseSendMsg());
                task.setDbName(database.getLabel());
                taskExcutor.addTask(task);
            }
            if (!isContinue) {
                return;
            }
            new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
            ActionManager.getInstance().fireSelectionChanged(getSelection());
            for (int i = 0; i < dbObjectArr.length; i++) {
                CubridDatabase database = (CubridDatabase) dbObjectArr[i];
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_STOP));
            }
        }
    }
}
