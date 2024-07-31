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
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.replication.task.ChangeMasterDbTask;
import com.cubrid.cubridmanager.ui.replication.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This dialog is responsible for changing master database.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-9-8 created by wuyingshi
 */
public class ChangeMasterDbDialog extends CMTitleAreaDialog implements ModifyListener {
    private CubridDatabase database = null;
    private Text oldMasterDbNameText = null;
    private Text oldMasterDbHostIpText = null;
    private Text newMasterDbNameText = null;
    private Text newMasterDbHostIpText = null;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public ChangeMasterDbDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create dialog area
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

        Group oldMasterDbInfoGroup = new Group(composite, SWT.NONE);
        oldMasterDbInfoGroup.setText(Messages.chmsdb0grpOldMasterDbInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        oldMasterDbInfoGroup.setLayoutData(gridData);
        layout = new GridLayout();
        layout.numColumns = 3;
        oldMasterDbInfoGroup.setLayout(layout);

        Label oldMasterDbHostIpLabel = new Label(oldMasterDbInfoGroup, SWT.LEFT);
        oldMasterDbHostIpLabel.setText(Messages.chmsdb0lblOldMasterDbHostIp);
        oldMasterDbHostIpLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        oldMasterDbHostIpText = new Text(oldMasterDbInfoGroup, SWT.LEFT | SWT.BORDER);
        oldMasterDbHostIpText.setEnabled(false);
        oldMasterDbHostIpText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

        Label oldMasterDbNameLabel = new Label(oldMasterDbInfoGroup, SWT.LEFT);
        oldMasterDbNameLabel.setText(Messages.chmsdb0lblOldMasterDbName);
        oldMasterDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        oldMasterDbNameText = new Text(oldMasterDbInfoGroup, SWT.LEFT | SWT.BORDER);
        oldMasterDbNameText.setEnabled(false);
        oldMasterDbNameText.setText("wings_mdb");
        oldMasterDbNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

        Group newMasterDbInfoGroup = new Group(composite, SWT.NONE);
        newMasterDbInfoGroup.setText(Messages.chmsdb0grpNewMasterDbInfo);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        newMasterDbInfoGroup.setLayoutData(gridData);
        layout = new GridLayout();
        layout.numColumns = 3;
        newMasterDbInfoGroup.setLayout(layout);

        Label newMasterDbHostIpLabel = new Label(newMasterDbInfoGroup, SWT.LEFT);
        newMasterDbHostIpLabel.setText(Messages.chmsdb0lblNewMasterDbHostIp);
        newMasterDbHostIpLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        newMasterDbHostIpText = new Text(newMasterDbInfoGroup, SWT.LEFT | SWT.BORDER);
        newMasterDbHostIpText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
        newMasterDbHostIpText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

        Label newMasterDbNameLabel = new Label(newMasterDbInfoGroup, SWT.LEFT);
        newMasterDbNameLabel.setText(Messages.chmsdb0lblNewMasterDbName);
        newMasterDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        newMasterDbNameText = new Text(newMasterDbInfoGroup, SWT.LEFT | SWT.BORDER);
        newMasterDbNameText.setTextLimit(ValidateUtil.MAX_DB_NAME_LENGTH);
        newMasterDbNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

        setTitle(Messages.chmsdb0titleChangeMasterDbDialog);
        setMessage(Messages.chmsdb0msgChangeMasterDbDialog);
        init();
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setSize(400, 300);
        getShell().setText(Messages.chmsdb0titleChangeMasterDbDialog);
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
        verify();
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID && !verify()) {
            return;
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     * @param event ModifyEvent
     */
    public void modifyText(ModifyEvent event) {
        verify();
    }

    /**
     * verify the input content.
     *
     * @return boolean
     */
    private boolean verify() {
        String msg = validInput();
        if (msg != null && !"".equals(msg)) {
            setErrorMessage(msg);
            return false;
        }
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    /**
     * input content of to be verified.
     *
     * @return error message
     */
    private String validInput() {
        if (newMasterDbHostIpText.getText() == null
                || "".equals(newMasterDbHostIpText.getText())
                || !ValidateUtil.isIP(newMasterDbHostIpText.getText())) {
            return Messages.chmsdb0errInvalidNewMasterDbHostIp;
        }

        if (newMasterDbNameText.getText() == null
                || "".equals(newMasterDbNameText.getText())
                || !ValidateUtil.isValidDBName(newMasterDbNameText.getText())) {
            return Messages.chmsdb0errInvalidNewMasterDbName;
        }

        return null;
    }

    /** initialize some values */
    private void init() {
        oldMasterDbNameText.addModifyListener(this);
        oldMasterDbHostIpText.addModifyListener(this);
        newMasterDbNameText.addModifyListener(this);
        newMasterDbHostIpText.addModifyListener(this);
    }

    /**
     * Execute task and Change master database
     *
     * @param buttonId int
     */
    @SuppressWarnings("unused")
    private void changeMasterDb(final int buttonId) {
        ChangeMasterDbTask task = new ChangeMasterDbTask(database.getServer().getServerInfo());

        task.setOldMasterDbName(oldMasterDbNameText.getText());
        task.setOldMasterHostIp(oldMasterDbHostIpText.getText());

        task.setNewMasterDbName(newMasterDbNameText.getText());
        task.setNewMasterHostIp(newMasterDbHostIpText.getText());

        task.setDistHostIp(database.getServer().getLabel());
        task.setDistDbName(database.getLabel());
        task.setDistDbDbaPasswd(database.getPassword());

        CommonTaskExec taskExcutor = new CommonTaskExec(null);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
        if (taskExcutor.isSuccess()) {
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
}
