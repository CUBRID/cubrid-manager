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
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to start database in C/S mode
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class StartDatabaseAction extends SelectionAction {
    public static final String ID = StartDatabaseAction.class.getName();

    public StartDatabaseAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public StartDatabaseAction(
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
            return ActionSupportUtil.hasAdminPermissionOnStopState(obj);
        } else if (obj instanceof Object[]) {
            return true;
        } else {
            return false;
        }
    }

    public void run() {
        Object[] objArr = getSelectedObj();
        List<ISchemaNode> schemaList = new ArrayList<ISchemaNode>();
        for (Object obj : objArr) {
            if (obj instanceof ISchemaNode) {
                schemaList.add((ISchemaNode) obj);
            }
        }

        if (schemaList.size() == 0) {
            setEnabled(false);
            return;
        }

        ISchemaNode[] schemaArray = new ISchemaNode[schemaList.size()];
        doRun(schemaList.toArray(schemaArray));
    }

    public void doRun(ISchemaNode[] schemaArray) {
        if (schemaArray == null || schemaArray.length == 0) {
            return;
        }

        List<ISchemaNode> startList = new ArrayList<ISchemaNode>();
        /*Judge start job is running*/
        for (ISchemaNode node : schemaArray) {
            if (!isSupported(node)) {
                setEnabled(false);
                return;
            }
            CubridDatabase database = node.getDatabase();

            final JobFamily jobFamily = new JobFamily();
            String serverName = database.getServer().getName();
            String dbName = database.getName();
            jobFamily.setServerName(serverName);
            jobFamily.setDbName(dbName);
            Job[] jobs = Job.getJobManager().find(jobFamily);
            if (jobs.length > 0) {
                CommonUITool.openWarningBox(Messages.bind(Messages.msgStartDbWithJob, dbName));
                continue;
            }
            startList.add(database);
        }

        CubridNavigatorView navigationView = CubridNavigatorView.findNavigationView();
        if (navigationView != null && startList.size() > 0) {
            final TreeViewer treeViewer = navigationView.getViewer();
            TaskExecutor taskExcutor =
                    new TaskExecutor() {
                        public boolean exec(final IProgressMonitor monitor) {
                            Display display = Display.getDefault();
                            if (monitor.isCanceled()) {
                                return false;
                            }
                            for (int i = 0; i < taskList.size(); i++) {
                                ITask task = taskList.get(i);
                                final CubridDatabase database =
                                        (CubridDatabase) task.getData("dbName");
                                if (!isSupported(database)) {
                                    continue;
                                }
                                monitor.subTask(
                                        Messages.bind(
                                                Messages.startDbTaskName, database.getName()));
                                task.execute();
                                if (openErrorBox(shell, task.getErrorMsg(), monitor)
                                        || monitor.isCanceled()) {
                                    return false;
                                }
                                openWarningBox(shell, task.getWarningMsg(), monitor);
                                if (monitor.isCanceled()) {
                                    return false;
                                }
                                database.removeAllChild();
                                if (database.getLoader() != null) {
                                    database.getLoader().setLoaded(false);
                                }
                                database.setRunningType(DbRunningType.CS);
                                display.syncExec(
                                        new Runnable() {
                                            public void run() {
                                                treeViewer.refresh(database, true);
                                            }
                                        });
                                if (monitor.isCanceled()) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    };

            for (ISchemaNode schemaNode : startList) {
                CubridDatabase database = schemaNode.getDatabase();
                if (!isSupported(database)) {
                    setEnabled(false);
                    return;
                }
                ServerInfo serverInfo = database.getServer().getServerInfo();
                CommonUpdateTask task =
                        new CommonUpdateTask(
                                CommonTaskName.START_DB_TASK_NAME,
                                serverInfo,
                                CommonSendMsg.getCommonDatabaseSendMsg());
                task.setDbName(database.getLabel());
                task.putData("dbName", database);
                taskExcutor.addTask(task);
            }
            new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
            ActionManager.getInstance().fireSelectionChanged(getSelection());
        }
    }
}
