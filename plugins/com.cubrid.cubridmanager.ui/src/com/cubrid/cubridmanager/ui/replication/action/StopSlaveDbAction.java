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
package com.cubrid.cubridmanager.ui.replication.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.SlaveInfo;
import com.cubrid.cubridmanager.ui.replication.Messages;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible for stoping slave database in standalone mode
 *
 * @author wuyingshi
 * @version 1.0 - 2009-6-4 created by wuyingshi
 */
public class StopSlaveDbAction extends SelectionAction {

    public static final String ID = StopSlaveDbAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public StopSlaveDbAction(Shell shell, String text, ImageDescriptor icon) {
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
    public StopSlaveDbAction(
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
     * @param obj Object
     * @return boolean(whether to support)
     */
    public boolean isSupported(Object obj) {
        return isSupportedNode(obj);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj Object
     * @return boolean(whether to support)
     */
    public static boolean isSupportedNode(Object obj) {
        if (obj instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) obj;
            if (!database.isLogined()) {
                return false;
            }
            ServerUserInfo serverUserInfo =
                    database.getServer().getServerInfo().getLoginedUserInfo();
            if (serverUserInfo == null || !serverUserInfo.isAdmin()) {
                return false;
            }
            if (!database.isDistributorDb()) {
                return false;
            }
            ReplicationInfo replInfo = (ReplicationInfo) database.getAdapter(ReplicationInfo.class);
            boolean isSupported = false;
            if (replInfo != null && replInfo.getSlaveList() != null) {
                for (int i = 0; i < replInfo.getSlaveList().size(); i++) {
                    DatabaseInfo dbInfo =
                            serverUserInfo.getDatabaseInfo(
                                    replInfo.getSlaveList().get(i).getSlaveDbName());
                    if (dbInfo == null || dbInfo.getRunningType() == DbRunningType.STANDALONE) {
                        isSupported = false;
                        break;
                    } else {
                        isSupported = true;
                    }
                }
            }
            return isSupported;
        }
        return false;
    }

    /** Stop database and refresh navigator */
    public void run() {

        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }

        final ISchemaNode schemaNode = (ISchemaNode) obj[0];
        ReplicationInfo replInfo = (ReplicationInfo) schemaNode.getAdapter(ReplicationInfo.class);
        Set<CubridDatabase> databaseSet = new HashSet<CubridDatabase>();
        for (int j = 0; j < replInfo.getSlaveList().size(); j++) {
            CubridDatabase database =
                    StartSlaveDbAction.getDatabaseByName(
                            schemaNode.getParent(),
                            replInfo.getSlaveList().get(j).getSlaveDbName());
            databaseSet.add(database);
        }
        StringBuffer dbNamesBuffer = new StringBuffer("");
        Iterator<SlaveInfo> it = replInfo.getSlaveList().iterator();
        while (it.hasNext()) {
            dbNamesBuffer.append(it.next().getSlaveDbName()).append(",");
        }
        String dbNames =
                dbNamesBuffer.toString().substring(0, dbNamesBuffer.toString().length() - 1);
        boolean isStop =
                CommonUITool.openConfirmBox(
                        getShell(), Messages.bind(Messages.msgConfirmStopDatabase, dbNames));
        if (!isStop) {
            return;
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
            for (int i = 0; i < dbObjectArr.length; i++) {
                ISchemaNode node = (ISchemaNode) dbObjectArr[i];
                CubridDatabase database = node.getDatabase();
                CommonUpdateTask task =
                        new CommonUpdateTask(
                                CommonTaskName.STOP_DB_TASK_NAME,
                                database.getServer().getServerInfo(),
                                CommonSendMsg.getCommonDatabaseSendMsg());
                task.setDbName(database.getLabel());
                taskExcutor.addTask(task);
            }
            new ExecTaskWithProgress(taskExcutor).exec();
            if (taskExcutor.isSuccess()) {
                for (int i = 0; i < dbObjectArr.length; i++) {
                    CubridDatabase database = (CubridDatabase) dbObjectArr[i];
                    CubridNodeManager.getInstance()
                            .fireCubridNodeChanged(
                                    new CubridNodeChangedEvent(
                                            database, CubridNodeChangedEventType.DATABASE_STOP));
                }
                ActionManager.getInstance().fireSelectionChanged(getSelection());
            }
        }
    }
}
