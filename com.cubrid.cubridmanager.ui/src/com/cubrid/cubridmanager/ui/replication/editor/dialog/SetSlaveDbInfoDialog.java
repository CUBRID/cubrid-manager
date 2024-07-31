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
package com.cubrid.cubridmanager.ui.replication.editor.dialog;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.dialog.IUpdatable;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.control.SetReplicationParamComp;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import java.util.List;
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
 * Set slave database information dialog
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SetSlaveDbInfoDialog extends CMTitleAreaDialog implements ModifyListener, IUpdatable {
    private Text slaveDbNameText;
    private Text dbPathText;
    private Text dbUserText;
    private Text dbPasswordText;
    private Text confirmDbPasswordText;
    private SlaveNode slaveNode;
    private HostNode hostNode;
    private boolean isEditable = true;
    private final SetReplicationParamComp paramEditor;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public SetSlaveDbInfoDialog(Shell parentShell) {
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

        Group slaveDbInfoGroup = new Group(composite, SWT.NONE);
        slaveDbInfoGroup.setText(Messages.grpSdbInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        slaveDbInfoGroup.setLayoutData(gridData);
        layout = new GridLayout();
        layout.numColumns = 2;
        slaveDbInfoGroup.setLayout(layout);

        Label slaveDbNameLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
        slaveDbNameLabel.setText(Messages.lblDbName);
        slaveDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        slaveDbNameText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER);
        slaveDbNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        if (isEditable) {
            Label dbPathLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
            dbPathLabel.setText(Messages.lblDbPath);
            dbPathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            dbPathText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER);
            dbPathText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        }
        Label dbUserLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
        dbUserLabel.setText(Messages.lblDbUser);
        dbUserLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        dbUserText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER);
        dbUserText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        if (isEditable) {
            Label dbaPasswordLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
            dbaPasswordLabel.setText(Messages.lblDbPassword);
            dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            dbPasswordText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER | SWT.PASSWORD);
            dbPasswordText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

            Label dbaPasswordConfirmLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
            dbaPasswordConfirmLabel.setText(Messages.lblConfirmDbPassword);
            dbaPasswordConfirmLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            confirmDbPasswordText =
                    new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER | SWT.PASSWORD);
            confirmDbPasswordText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        }

        paramEditor.createReplicationParamComp(composite);
        initialize();
        setTitle(Messages.titleSetSlaveDbDialog);
        return parentComp;
    }

    /** initialize some values */
    private void initialize() {
        if (slaveNode != null && slaveNode.getParent() instanceof HostNode) {
            hostNode = (HostNode) getSlave().getParent();
        }
        paramEditor.init();
        if (slaveNode != null) {
            slaveDbNameText.setText(slaveNode.getDbName() == null ? "" : slaveNode.getDbName());
            String dbPath = slaveNode.getDbPath();
            if (dbPath == null || dbPath.trim().length() == 0) {
                dbPath = hostNode.getDbPath();
            }
            if (dbPath == null) {
                dbPath = "";
            }
            if (dbPathText != null) {
                dbPathText.setText(dbPath);
            }
            dbUserText.setText(
                    slaveNode.getDbUser() == null ? "replication_user" : slaveNode.getDbUser());
            if (dbPasswordText != null) {
                dbPasswordText.setText(
                        slaveNode.getDbPassword() == null ? "" : slaveNode.getDbPassword());
            }
            if (confirmDbPasswordText != null) {
                confirmDbPasswordText.setText(
                        slaveNode.getDbPassword() == null ? "" : slaveNode.getDbPassword());
            }
            paramEditor.setReplicationParamMap(slaveNode.getParamMap());
        }
        if (hostNode == null || !hostNode.isValid()) {
            setErrorMessage(Messages.errInvalidHostInfo);
        } else {
            if (isEditable) {
                setMessage(Messages.msgSetSlaveDbDialog);
            } else {
                setMessage(Messages.msg1SetSlaveDbDialog);
            }
        }
        if (isEditable) {
            slaveDbNameText.addModifyListener(this);
            if (dbPathText != null) {
                dbPathText.addModifyListener(this);
            }
            dbUserText.addModifyListener(this);
            if (dbPasswordText != null) {
                dbPasswordText.addModifyListener(this);
            }
            if (confirmDbPasswordText != null) {
                confirmDbPasswordText.addModifyListener(this);
            }
            paramEditor.setEditable(true);
        } else {
            slaveDbNameText.setEnabled(false);
            if (dbPathText != null) {
                dbPathText.setEnabled(false);
            }
            dbUserText.setEnabled(false);
            if (dbPasswordText != null) {
                dbPasswordText.setEnabled(false);
            }
            if (confirmDbPasswordText != null) {
                confirmDbPasswordText.setEnabled(false);
            }
            paramEditor.setEditable(false);
        }
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleSetSlaveDbDialog);
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
        if (!slaveNode.isValid() || !isEditable) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
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
        if (buttonId == IDialogConstants.OK_ID && slaveNode != null) {
            String dbName = slaveDbNameText.getText();
            String dbPath = dbPathText.getText();
            String dbUser = dbUserText.getText();
            String dbaPassword = dbPasswordText.getText();
            slaveNode.setDbName(dbName);
            slaveNode.setDbPath(dbPath);
            slaveNode.setDbUser(dbUser);
            slaveNode.setDbPassword(dbaPassword);
            slaveNode.setParamMap(paramEditor.getParamMap());
            slaveNode.setName(dbName);
        }
        super.buttonPressed(buttonId);
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     * @param event ModifyEvent
     */
    public void modifyText(ModifyEvent event) {
        boolean isValid = valid();
        if (event.widget == slaveDbNameText) {
            String separator = FileUtil.getSeparator(hostNode.getOsInfoType());
            String dbPath = hostNode.getDbPath() == null ? "" : hostNode.getDbPath();
            dbPathText.setText(dbPath + separator + slaveDbNameText.getText());
        }

        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(isValid);
        }
    }

    /**
     * validate the input data
     *
     * @return boolean
     */
    public boolean valid() {
        String dbName = slaveDbNameText.getText();
        String dbPath = dbPathText.getText();
        String dbUser = dbUserText.getText();
        String dbaPassword = dbPasswordText.getText();
        String confirmDbaPassword = confirmDbPasswordText.getText();

        boolean isValidHost = hostNode != null && hostNode.isValid();
        if (!isValidHost) {
            setErrorMessage(Messages.errInvalidHostInfo);
            return false;
        }
        boolean isValidDbName = ValidateUtil.isValidDBName(dbName);
        if (!isValidDbName) {
            setErrorMessage(Messages.errDatabaseName);
            return false;
        }
        boolean isDatabaseExist = false;
        if (isValidDbName && isValidHost) {
            List<DatabaseInfo> databaseInfoList = hostNode.getDatabaseInfoList();
            for (int i = 0; databaseInfoList != null && i < databaseInfoList.size(); i++) {
                DatabaseInfo databaseInfo = databaseInfoList.get(i);
                if (dbName.equalsIgnoreCase(databaseInfo.getDbName())) {
                    isDatabaseExist = true;
                    break;
                }
            }
            if (!isDatabaseExist) {
                List<LeafNode> childNodeList = hostNode.getChildNodeList();
                for (int i = 0; childNodeList != null && i < childNodeList.size(); i++) {
                    LeafNode node = childNodeList.get(i);
                    if (dbName.equalsIgnoreCase(node.getName())
                            && !dbName.equals(slaveNode.getDbName())) {
                        isDatabaseExist = true;
                        break;
                    }
                }
            }
        }

        if (isDatabaseExist) {
            setErrorMessage(Messages.errDbExist);
            return false;
        }
        boolean isValidDbPathName = ValidateUtil.isValidPathName(dbPath);
        if (!isValidDbPathName) {
            setErrorMessage(Messages.errDbPath);
            return false;
        }
        boolean isValidDbUser =
                dbUser.trim().length() > 0
                        && dbUser.indexOf(" ") < 0
                        && !dbUser.equalsIgnoreCase("dba")
                        && dbUser.trim().length() < ValidateUtil.MAX_NAME_LENGTH;
        if (!isValidDbUser) {
            setErrorMessage(Messages.errDbUser);
            return false;
        }
        boolean isValidDbPassword =
                dbaPassword.trim().length() >= 4 && dbaPassword.indexOf(" ") < 0;
        if (!isValidDbPassword) {
            setErrorMessage(Messages.errDbPassword);
            return false;
        }
        boolean isValidConfirmDbPassword =
                confirmDbaPassword.length() >= 4 && confirmDbaPassword.indexOf(" ") < 0;
        if (!isValidConfirmDbPassword) {
            setErrorMessage(Messages.errConfirmedDbPassword);
            return false;
        }
        boolean isEqualPassword = dbaPassword.equals(confirmDbaPassword);
        if (!isEqualPassword) {
            setErrorMessage(Messages.errPasswordNotEqual);
            return false;
        }
        setErrorMessage(null);
        return true;
    }

    public SlaveNode getSlave() {
        return this.slaveNode;
    }

    public void setSlave(SlaveNode slave) {
        this.slaveNode = slave;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    /** @see com.cubrid.common.ui.spi.dialog.IUpdatable#updateUI() */
    public void updateUI() {
        String errMsg = paramEditor.getErrorMsg();
        if (errMsg != null) {
            CommonUITool.openErrorBox(errMsg);
        }
    }
}
