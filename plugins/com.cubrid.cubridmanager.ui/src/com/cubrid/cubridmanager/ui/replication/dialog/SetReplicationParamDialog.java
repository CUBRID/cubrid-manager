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
package com.cubrid.cubridmanager.ui.replication.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.dialog.IUpdatable;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamInfo;
import com.cubrid.cubridmanager.core.replication.task.GetReplicationParamTask;
import com.cubrid.cubridmanager.core.replication.task.SetReplicationParamTask;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.control.SetReplicationParamComp;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This dialog is responsible for setting replication parameter
 *
 * @author wuyingshi
 * @version 1.0 - 2009-9-6 created by wuyingshi
 */
public class SetReplicationParamDialog extends CMTitleAreaDialog implements IUpdatable {

    private CubridDatabase database = null;
    private Combo slaveDbNameCombo = null;
    private ReplicationInfo replInfo = null;
    private final SetReplicationParamComp paramEditor;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public SetReplicationParamDialog(Shell parentShell) {
        super(parentShell);
        paramEditor = new SetReplicationParamComp(this);
    }

    /**
     * Create the dialog area
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        createBasicGroup(composite);
        paramEditor.createReplicationParamComp(composite);
        initial();

        setTitle(Messages.repparm0titleReplicationParamDbDialog);
        setMessage(Messages.repparm0msgReplicationParamDbDialog);
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.repparm0titleReplicationParamDbDialog);
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
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            perform(buttonId);
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /**
     * Creates basic group
     *
     * @param parent Composite
     */
    private void createBasicGroup(Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.repparm0grpReplicationInfo);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 5;
        group.setLayout(gridLayout);

        Label slaveDbNameLabel = new Label(group, SWT.NONE);
        slaveDbNameLabel.setText(Messages.repparm0lblSlaveDbName);
        slaveDbNameLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        slaveDbNameCombo = new Combo(group, SWT.NONE | SWT.READ_ONLY);
        slaveDbNameCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Button connectButton = new Button(group, SWT.RIGHT);
        connectButton.setText(Messages.repparm0btnConnect);
        connectButton.setLayoutData(new GridData());
        connectButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {

                        GetReplicationParamTask task =
                                new GetReplicationParamTask(database.getServer().getServerInfo());
                        task.setMasterDbName(replInfo.getMasterList().get(0).getMasterDbName());
                        task.setSlaveDbName(slaveDbNameCombo.getText());
                        task.setDistDbName(database.getLabel());
                        task.setDistDbDbaPasswd(database.getPassword());
                        task.setRunningMode(database.getRunningType() == DbRunningType.CS);

                        TaskExecutor taskExecutor = new CommonTaskExec(null);
                        taskExecutor.addTask(task);
                        new ExecTaskWithProgress(taskExecutor).exec();
                        if (taskExecutor.isSuccess()) {
                            ReplicationParamInfo paramInfo = task.getReplicationParams();
                            Map<String, String> paramMap = paramInfo.getParamMap();
                            paramEditor.setReplicationParamMap(paramMap);
                        }
                    }
                });
    }

    /** initial the page content */
    private void initial() {
        setReplInfo((ReplicationInfo) database.getAdapter(ReplicationInfo.class));
        for (int j = 0; j < replInfo.getSlaveList().size(); j++) {
            slaveDbNameCombo.add(replInfo.getSlaveList().get(j).getSlaveDbName());
        }
        slaveDbNameCombo.setText(replInfo.getSlaveList().get(0).getSlaveDbName());

        GetReplicationParamTask task =
                new GetReplicationParamTask(database.getServer().getServerInfo());
        task.setMasterDbName(replInfo.getMasterList().get(0).getMasterDbName());
        task.setSlaveDbName(slaveDbNameCombo.getText());
        task.setDistDbName(database.getLabel());
        task.setDistDbDbaPasswd(database.getPassword());
        task.setRunningMode(database.getRunningType() == DbRunningType.CS);

        TaskExecutor taskExecutor = new CommonTaskExec(null);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            ReplicationParamInfo paramInfo = task.getReplicationParams();
            Map<String, String> paramMap = paramInfo.getParamMap();
            paramEditor.setReplicationParamMap(paramMap);
        }
    }

    /**
     * Perform the task and set parameters
     *
     * @param buttonId int
     */
    private void perform(int buttonId) {
        SetReplicationParamTask task =
                new SetReplicationParamTask(database.getServer().getServerInfo());

        task.setMasterDbName(replInfo.getMasterList().get(0).getMasterDbName());
        task.setSlaveDbName(slaveDbNameCombo.getText());
        task.setDistDbName(database.getLabel());
        task.setDistDbDbaPasswd(database.getPassword());
        task.setParameterMap(paramEditor.getParamMap());
        task.setRunningMode(database.getRunningType() == DbRunningType.CS);
        TaskExecutor taskExecutor = new CommonTaskExec(null);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            CommonUITool.openInformationBox(
                    Messages.repparm0titleSuccess, Messages.repparm0msgChangeParamSuccess);
            super.buttonPressed(buttonId);
        }
    }

    /** @return the database */
    public CubridDatabase getDatabase() {
        return database;
    }

    /** @param database the database to set */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /** @return the replInfo */
    public ReplicationInfo getReplInfo() {
        return replInfo;
    }

    /** @param replInfo the replInfo to set */
    public void setReplInfo(ReplicationInfo replInfo) {
        this.replInfo = replInfo;
    }

    /** @see com.cubrid.common.ui.spi.dialog.IUpdatable#updateUI() */
    public void updateUI() {
        String errorMsg = paramEditor.getErrorMsg();
        if (errorMsg == null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            CommonUITool.openErrorBox(errorMsg);
        }
    }
}
