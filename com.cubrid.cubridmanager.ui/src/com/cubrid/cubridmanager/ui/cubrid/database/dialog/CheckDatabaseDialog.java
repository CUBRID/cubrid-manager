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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The Dialog of Check the Database
 *
 * @author robin 2009-3-11
 */
public class CheckDatabaseDialog extends CMTitleAreaDialog implements ITaskExecutorInterceptor {

    private Text dbNameText;
    private Composite parentComp;
    private CubridDatabase database = null;
    private Button repairButton = null;
    private String jobName;

    public CheckDatabaseDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        parentComp = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createdbNameGroup(composite);
        createDescriptionGroup(composite);
        repairButton = new Button(composite, SWT.CHECK);
        repairButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        repairButton.setText(Messages.btnRepair);
        setTitle(Messages.titleCheckDbDialog);
        setMessage(Messages.msgCheckDbDialog);
        initial();
        return parentComp;
    }

    /**
     * Create Description Group
     *
     * @param composite the parent composite
     */
    private void createDescriptionGroup(Composite composite) {
        GridLayout layout = new GridLayout();
        final Group descGroup = new Group(composite, SWT.NONE);
        descGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        descGroup.setLayout(layout);
        descGroup.setText(Messages.grpCheckDescInfo);

        final Label text = new Label(descGroup, SWT.WRAP);
        text.setText(Messages.lblCheckDescInfo);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create Database Name Group
     *
     * @param composite the parent composite
     */
    private void createdbNameGroup(Composite composite) {

        final Group dbnameGroup = new Group(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        final GridData gdDbnameGroup = new GridData(GridData.FILL_HORIZONTAL);
        dbnameGroup.setLayoutData(gdDbnameGroup);
        dbnameGroup.setLayout(layout);

        final Label databaseName = new Label(dbnameGroup, SWT.LEFT | SWT.WRAP);

        databaseName.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        databaseName.setText(Messages.lblCheckDbName);

        dbNameText = new Text(dbnameGroup, SWT.BORDER);
        dbNameText.setEnabled(false);
        final GridData gdDbNameText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dbNameText.setLayoutData(gdDbNameText);
    }

    /** Init the dialog */
    private void initial() {
        dbNameText.setText(database.getName());
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setSize(400, 400);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleCheckDbDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When press button in button bar,call this method
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!verify()) {
                return;
            }
            CommonUpdateTask task =
                    new CommonUpdateTask(
                            CommonTaskName.CHECK_DATABASE_TASK_NAME,
                            database.getServer().getServerInfo(),
                            CommonSendMsg.getCheckDbSendMsg());
            task.setDbName(database.getName());
            task.setRepairDb(repairButton.getSelection() ? YesNoType.Y : YesNoType.N);

            String serverName = database.getServer().getName();
            String dbName = database.getName();
            jobName = Messages.msgCheckDbRearName + " - " + dbName + "@" + serverName;
            TaskJobExecutor taskExec = new CommonTaskJobExec(this);
            taskExec.addTask(task);
            JobFamily jobFamily = new JobFamily();
            jobFamily.setServerName(serverName);
            jobFamily.setDbName(dbName);

            taskExec.schedule(jobName, jobFamily, true, Job.SHORT);
            return;
        }
        super.buttonPressed(buttonId);
    }

    /** @see com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor#completeAll() */
    public void completeAll() {
        CommonUITool.openInformationBox(
                parentComp.getShell(),
                Messages.titleSuccess,
                Messages.bind(Messages.msgCheckSuccess, jobName));
    }

    /**
     * After a task has been executed, do some thing such as refresh.
     *
     * @param task the task
     * @return IStatus if complete refresh false if run into error
     */
    public IStatus postTaskFinished(ITask task) {
        return Status.OK_STATUS;
    }

    /**
     * Verity data
     *
     * @return true
     */
    private boolean verify() {
        setErrorMessage(null);
        return true;
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }
}
