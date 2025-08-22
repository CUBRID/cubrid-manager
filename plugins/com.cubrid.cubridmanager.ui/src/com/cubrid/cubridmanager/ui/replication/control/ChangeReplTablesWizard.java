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
package com.cubrid.cubridmanager.ui.replication.control;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.replication.task.ChangeReplTablesTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.replication.Messages;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Change Replication table wizard is responsible change replicated tables
 *
 * @author pangqiren
 * @version 1.0 - 2009-11-26 created by pangqiren
 */
public class ChangeReplTablesWizard extends Wizard {

    private SetDatabaseInfoPage setDatabaseInfoPage = null;
    private ChangeTablesPage changeTablesPage = null;
    private final ICubridNode replicationNode;
    private WizardDialog dialog = null;

    /** The constructor */
    public ChangeReplTablesWizard(ICubridNode node) {
        setWindowTitle(Messages.titleChangeReplTables);
        replicationNode = node;
    }

    /** Add wizard pages */
    public void addPages() {
        setDatabaseInfoPage = new SetDatabaseInfoPage(replicationNode);
        addPage(setDatabaseInfoPage);
        changeTablesPage = new ChangeTablesPage(replicationNode);
        addPage(changeTablesPage);
        dialog = (WizardDialog) getContainer();
        dialog.addPageChangedListener(changeTablesPage);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * @return boolean
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage() == changeTablesPage;
    }

    /**
     * Called when user clicks Finish
     *
     * @return boolean
     */
    public boolean performFinish() {
        String mdbName = setDatabaseInfoPage.getMasterDbName();
        String distdbName = setDatabaseInfoPage.getDistributorDbName();
        String distdbPassword = setDatabaseInfoPage.getDistdbPassword();
        boolean isReplAllTables = changeTablesPage.isReplAllTables();
        List<String> replTableList = changeTablesPage.getReplTableList();
        TaskJobExecutor taskExcutor =
                new TaskJobExecutor() {
                    public void closeUI() {
                        Display.getDefault()
                                .syncExec(
                                        new Runnable() {
                                            public void run() {
                                                dialog.close();
                                            }
                                        });
                    }

                    public void setVisible(final boolean isVisible) {
                        Display display = Display.getDefault();
                        display.syncExec(
                                new Runnable() {
                                    public void run() {
                                        dialog.getShell().setVisible(isVisible);
                                    }
                                });
                    }

                    public IStatus exec(final IProgressMonitor monitor) {
                        setVisible(false);
                        for (ITask task : taskList) {
                            task.execute();
                            if (monitor.isCanceled()) {
                                closeUI();
                                return Status.CANCEL_STATUS;
                            }
                            final String msg = task.getErrorMsg();
                            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                                return new Status(
                                        IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                            }
                        }
                        closeUI();
                        return Status.OK_STATUS;
                    }
                };
        ServerInfo serverInfo = replicationNode.getServer().getServerInfo();
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setMdbName(mdbName);
        task.setDistdbName(distdbName);
        task.setDistdbPassword(distdbPassword);
        task.setReplAllClasses(isReplAllTables);
        task.setReplicatedClasses(replTableList);
        taskExcutor.addTask(task);

        JobFamily jobFamily = new JobFamily();
        String serverName = replicationNode.getServer().getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(distdbName);

        String jobName =
                Messages.bind(
                        Messages.changeReplicationSchemaJobName,
                        new String[] {distdbName, serverName});
        taskExcutor.schedule(jobName, jobFamily, true, Job.SHORT);
        return false;
    }
}
