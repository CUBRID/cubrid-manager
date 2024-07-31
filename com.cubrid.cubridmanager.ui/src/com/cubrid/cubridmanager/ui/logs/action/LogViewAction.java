/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.cubridmanager.ui.logs.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckFileTask;
import com.cubrid.cubridmanager.core.logs.model.LogContentInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.task.GetLogListTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.logs.Messages;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
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
 * This action is responsible to view log.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-10 created by wuyingshi
 */
public class LogViewAction extends SelectionAction {

    private static final Logger LOGGER = LogUtil.getLogger(LogViewAction.class);
    public static final String ID = LogViewAction.class.getName();
    private ICubridNode cubridNode = null;

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public LogViewAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The Constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public LogViewAction(
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
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (CubridNodeType.BROKER_SQL_LOG.equals(node.getType())
                    || CubridNodeType.LOGS_BROKER_ADMIN_LOG.equals(node.getType())
                    || CubridNodeType.LOGS_BROKER_ACCESS_LOG.equals(node.getType())
                    || CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(node.getType())
                    || CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(node.getType())
                    || CubridNodeType.LOGS_COPY_DATABASE_LOG.equals(node.getType())
                    || CubridNodeType.LOGS_APPLY_DATABASE_LOG.equals(node.getType())) {
                return true;
            }
        }
        return false;
    }

    /** Open the log editor and show log content */
    public void run() {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        if (cubridNode == null) {
            Object[] obj = this.getSelectedObj();
            if (!isSupported(obj[0])) {
                setEnabled(false);
                return;
            }
            cubridNode = (ICubridNode) obj[0];
        }

        LogInfo logInfo = (LogInfo) cubridNode.getAdapter(LogInfo.class);
        final String filePath = logInfo.getPath();

        TaskJobExecutor taskJobExecutor =
                new TaskJobExecutor() {
                    public IStatus exec(IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            cubridNode = null;
                            return Status.CANCEL_STATUS;
                        }
                        for (ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (monitor.isCanceled()) {
                                cubridNode = null;
                                return Status.CANCEL_STATUS;
                            }
                            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                                cubridNode = null;
                                return new Status(
                                        IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                            }
                            if (task instanceof CheckFileTask) {
                                CheckFileTask checkFileTask = (CheckFileTask) task;
                                final String[] files = checkFileTask.getExistFiles();
                                if (files == null || files.length == 0) {
                                    return new Status(
                                            IStatus.ERROR,
                                            CubridManagerUIPlugin.PLUGIN_ID,
                                            Messages.bind(Messages.errLogFileNoExist, filePath));
                                }
                            } else if (task instanceof GetLogListTask) {
                                GetLogListTask getLogListTask = (GetLogListTask) task;
                                final LogContentInfo logContentInfo =
                                        (LogContentInfo) getLogListTask.getLogContent();
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
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

        CheckFileTask checkFileTask = new CheckFileTask(cubridNode.getServer().getServerInfo());
        checkFileTask.setFile(new String[] {filePath});
        taskJobExecutor.addTask(checkFileTask);

        GetLogListTask task = new GetLogListTask(cubridNode.getServer().getServerInfo());
        task.setPath(filePath);
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

    public void setCubridNode(ICubridNode cubridNode) {
        this.cubridNode = cubridNode;
    }
}
