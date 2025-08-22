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
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.logs.model.LogContentInfo;
import com.cubrid.cubridmanager.core.logs.task.GetLogListTask;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.logs.Messages;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * This action is responsible for viewing log.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-8-28 created by wuyingshi
 */
public class ViewReplicationErrorLogAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(ViewReplicationErrorLogAction.class);
    public static final String ID = ViewReplicationErrorLogAction.class.getName();
    private ICubridNode cubridNode = null;

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ViewReplicationErrorLogAction(Shell shell, String text, ImageDescriptor icon) {
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
    public ViewReplicationErrorLogAction(
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
        return true;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj Object
     * @return boolean(whether to support)
     */
    public boolean isSupported(Object obj) {
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
            return database.isDistributorDb();
        }
        return false;
    }

    /** view replication error log */
    public void run() {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }

        final ISchemaNode schemaNode = (ISchemaNode) obj[0];
        cubridNode = schemaNode;
        ReplicationInfo replInfo = (ReplicationInfo) schemaNode.getAdapter(ReplicationInfo.class);
        final GetLogListTask task = new GetLogListTask(schemaNode.getServer().getServerInfo());

        TaskJobExecutor taskJobExecutor =
                new TaskJobExecutor() {
                    public IStatus exec(IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            cubridNode = null;
                            return Status.CANCEL_STATUS;
                        }
                        for (ITask t : taskList) {
                            t.execute();
                            final String msg = t.getErrorMsg();

                            if (monitor.isCanceled()) {
                                cubridNode = null;
                                return Status.CANCEL_STATUS;
                            }
                            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                                cubridNode = null;
                                return new Status(
                                        IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                            } else {
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        LogContentInfo logContentInfo =
                                                                (LogContentInfo)
                                                                        task.getLogContent();
                                                        IEditorPart editorPart =
                                                                LayoutUtil.getEditorPart(
                                                                        cubridNode,
                                                                        LogEditorPart.ID);
                                                        if (editorPart != null) {
                                                            window.getActivePage()
                                                                    .closeEditor(editorPart, false);
                                                        }
                                                        try {
                                                            IEditorPart editor =
                                                                    window.getActivePage()
                                                                            .openEditor(
                                                                                    cubridNode,
                                                                                    LogEditorPart
                                                                                            .ID);
                                                            ((LogEditorPart) editor)
                                                                    .setTableInfo(
                                                                            logContentInfo, true);
                                                        } catch (PartInitException e) {
                                                            LOGGER.error(e.getMessage(), e);
                                                        }
                                                    }
                                                });
                            }
                            if (monitor.isCanceled()) {
                                cubridNode = null;
                                return Status.CANCEL_STATUS;
                            }
                        }
                        cubridNode = null;
                        return Status.OK_STATUS;
                    }
                };
        String errPath =
                replInfo.getDistInfo()
                                .getCopyLogPath()
                                .substring(
                                        0,
                                        replInfo.getDistInfo()
                                                .getCopyLogPath()
                                                .indexOf(replInfo.getDistInfo().getDistDbName()))
                        + replInfo.getDistInfo().getDistDbName()
                        + schemaNode.getServer().getServerInfo().getPathSeparator()
                        + replInfo.getDistInfo().getDistDbName()
                        + ".err";
        task.setPath(errPath);
        task.setStart("1");
        task.setEnd("100");
        taskJobExecutor.addTask(task);
        String jobName =
                Messages.viewLogJobName
                        + " - "
                        + cubridNode.getName()
                        + "@"
                        + cubridNode.getServer().getName();
        taskJobExecutor.schedule(jobName, null, false, Job.SHORT);
    }
}
