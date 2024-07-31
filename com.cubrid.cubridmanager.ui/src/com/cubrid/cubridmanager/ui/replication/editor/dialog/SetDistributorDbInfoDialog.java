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
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
 * Set distributor database information dialog
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SetDistributorDbInfoDialog extends CMTitleAreaDialog implements ModifyListener {
    private Text dbNameText = null;
    private Text dbPathText;
    private Text dbaPasswordText;
    private Text confirmDbaPasswordText;
    private Text agentPortText;
    private Text copyLogText;
    private Text trailLogText;
    private Text errorLogText;
    private Text delayTimeText;
    private Button restartReplButton;
    private DistributorNode distributorNode;
    private HostNode hostNode;
    private boolean isEditable = true;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public SetDistributorDbInfoDialog(Shell parentShell) {
        super(parentShell);
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

        Group distributorInfoGroup = new Group(composite, SWT.NONE);
        distributorInfoGroup.setText(Messages.grpDistdbInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        distributorInfoGroup.setLayoutData(gridData);
        layout = new GridLayout();
        layout.numColumns = 2;
        distributorInfoGroup.setLayout(layout);

        Label dbNameLabel = new Label(distributorInfoGroup, SWT.LEFT);
        dbNameLabel.setText(Messages.lblDbName);
        dbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        dbNameText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER);
        dbNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        if (isEditable) {
            Label dbPathLabel = new Label(distributorInfoGroup, SWT.LEFT);
            dbPathLabel.setText(Messages.lblDbPath);
            dbPathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            dbPathText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER);
            dbPathText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

            Label dbaPasswordLabel = new Label(distributorInfoGroup, SWT.LEFT);
            dbaPasswordLabel.setText(Messages.lblDbaPassword);
            dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            dbaPasswordText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER | SWT.PASSWORD);
            dbaPasswordText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

            Label dbaPasswordConfirmLabel = new Label(distributorInfoGroup, SWT.LEFT);
            dbaPasswordConfirmLabel.setText(Messages.lblConfirmDbaPassword);
            dbaPasswordConfirmLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            confirmDbaPasswordText =
                    new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER | SWT.PASSWORD);
            confirmDbaPasswordText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        }

        Label copyLogLabel = new Label(distributorInfoGroup, SWT.LEFT);
        copyLogLabel.setText(Messages.lblCopyLogPath);
        copyLogLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        copyLogText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER);
        copyLogText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

        Label trailLogLabel = new Label(distributorInfoGroup, SWT.LEFT);
        trailLogLabel.setText(Messages.lblTrailLogPath);
        trailLogLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        trailLogText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER);
        trailLogText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

        Label errorLogLabel = new Label(distributorInfoGroup, SWT.LEFT);
        errorLogLabel.setText(Messages.lblErrorLogPath);
        errorLogLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        errorLogText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER);
        errorLogText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

        Label delayTimeLogLabel = new Label(distributorInfoGroup, SWT.LEFT);
        delayTimeLogLabel.setText(Messages.lblDelayTimeLogSize);
        delayTimeLogLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        delayTimeText = new Text(distributorInfoGroup, SWT.LEFT | SWT.BORDER);
        delayTimeText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

        restartReplButton = new Button(distributorInfoGroup, SWT.CHECK);
        restartReplButton.setText(Messages.btnRestartReplication);
        restartReplButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

        Group agentPortGroup = new Group(composite, SWT.NONE);
        agentPortGroup.setText(Messages.grpReplAgentInfo);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        agentPortGroup.setLayoutData(gridData);
        layout = new GridLayout();
        layout.numColumns = 2;
        agentPortGroup.setLayout(layout);

        Label agentPortLabel = new Label(agentPortGroup, SWT.LEFT);
        agentPortLabel.setText(Messages.lblReplAgentPort);
        agentPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        agentPortText = new Text(agentPortGroup, SWT.LEFT | SWT.BORDER);
        agentPortText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

        initialize();
        setTitle(Messages.titleSetDistdbDialog);
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleSetDistdbDialog);
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
        if (!distributorNode.isValid() || !isEditable) {
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
        if (buttonId == IDialogConstants.OK_ID && distributorNode != null) {
            String dbName = dbNameText.getText();
            String dbPath = dbPathText.getText();
            String dbaPassword = dbaPasswordText.getText();
            String agentPort = agentPortText.getText();
            String copyLogPath = copyLogText.getText();
            String trailLogPath = trailLogText.getText();
            String errorLogPath = errorLogText.getText();
            String delayTimeLogSize = delayTimeText.getText();
            boolean isRestartRepl = restartReplButton.getSelection();
            distributorNode.setDbName(dbName);
            distributorNode.setDbPath(dbPath);
            distributorNode.setDbaPassword(dbaPassword);
            distributorNode.setReplAgentPort(agentPort);
            distributorNode.setCopyLogPath(copyLogPath);
            distributorNode.setTrailLogPath(trailLogPath);
            distributorNode.setErrorLogPath(errorLogPath);
            distributorNode.setDelayTimeLogSize(delayTimeLogSize);
            distributorNode.setRestartWhenError(isRestartRepl);
            distributorNode.setName(dbName);
        }
        super.buttonPressed(buttonId);
    }

    /** initialize some values. */
    public void initialize() {
        if (distributorNode != null && distributorNode.getParent() instanceof HostNode) {
            hostNode = (HostNode) getDistributor().getParent();
        }
        if (distributorNode != null) {
            String dbName = distributorNode.getDbName();
            String dbPath = distributorNode.getDbPath();
            String dbaPassword = distributorNode.getDbaPassword();
            String agentPort = distributorNode.getReplAgentPort();
            String copyLogPath = distributorNode.getCopyLogPath();
            String trailLogPath = distributorNode.getTrailLogPath();
            String errorLogPath = distributorNode.getErrorLogPath();
            String delayTimeLogSize = distributorNode.getDelayTimeLogSize();
            boolean isRestartRepl = distributorNode.isRestartWhenError();
            dbNameText.setText(dbName == null ? "" : dbName);
            if (dbPath == null || dbPath.trim().length() == 0) {
                dbPath = hostNode.getDbPath();
            }
            if (dbPath == null) {
                dbPath = "";
            }
            if (dbPathText != null) {
                dbPathText.setText(dbPath);
            }
            if (dbaPasswordText != null) {
                dbaPasswordText.setText(dbaPassword == null ? "" : dbaPassword);
            }
            if (confirmDbaPasswordText != null) {
                confirmDbaPasswordText.setText(dbaPassword == null ? "" : dbaPassword);
            }
            agentPortText.setText(agentPort == null ? "" : agentPort);
            copyLogText.setText(copyLogPath == null ? dbPath : copyLogPath);
            trailLogText.setText(trailLogPath == null ? dbPath : trailLogPath);
            errorLogText.setText(errorLogPath == null ? dbPath : errorLogPath);
            delayTimeText.setText(delayTimeLogSize == null ? "" : delayTimeLogSize);
            restartReplButton.setSelection(isRestartRepl);
        }
        if (hostNode == null || !hostNode.isValid()) {
            setErrorMessage(Messages.errInvalidHostInfo);
        } else {
            if (isEditable) {
                setMessage(Messages.msgSetDistdbDialog);
            } else {
                setMessage(Messages.msg1SetDistdbDialog);
            }
        }
        if (isEditable) {
            dbNameText.addModifyListener(this);
            if (dbPathText != null) {
                dbPathText.addModifyListener(this);
            }
            if (dbaPasswordText != null) {
                dbaPasswordText.addModifyListener(this);
            }
            if (confirmDbaPasswordText != null) {
                confirmDbaPasswordText.addModifyListener(this);
            }
            agentPortText.addModifyListener(this);
            copyLogText.addModifyListener(this);
            trailLogText.addModifyListener(this);
            errorLogText.addModifyListener(this);
            delayTimeText.addModifyListener(this);
        } else {
            dbNameText.setEnabled(false);
            if (dbPathText != null) {
                dbPathText.setEnabled(false);
            }
            if (dbaPasswordText != null) {
                dbaPasswordText.setEnabled(false);
            }
            if (confirmDbaPasswordText != null) {
                confirmDbaPasswordText.setEnabled(false);
            }
            agentPortText.setEnabled(false);
            copyLogText.setEnabled(false);
            trailLogText.setEnabled(false);
            errorLogText.setEnabled(false);
            delayTimeText.setEnabled(false);
            restartReplButton.setEnabled(false);
        }
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     * @param event ModifyEvent
     */
    public void modifyText(ModifyEvent event) {
        if (event.widget == dbNameText) {
            String separator = FileUtil.getSeparator(hostNode.getOsInfoType());
            String path = hostNode.getDbPath() == null ? "" : hostNode.getDbPath();
            dbPathText.setText(path + separator + dbNameText.getText());
        } else if (event.widget == dbPathText) {
            copyLogText.setText(dbPathText.getText());
            trailLogText.setText(dbPathText.getText());
            errorLogText.setText(dbPathText.getText());
        }
        boolean isValidHost = hostNode != null && hostNode.isValid();
        if (!isValidHost) {
            setErrorMessage(Messages.errInvalidHostInfo);
            setEnabled(false);
            return;
        }

        String dbName = dbNameText.getText();
        String dbPath = dbPathText.getText();
        String dbaPassword = dbaPasswordText.getText();
        String confirmDbaPassword = confirmDbaPasswordText.getText();
        String copyLogPath = copyLogText.getText();
        String trailLogPath = trailLogText.getText();
        String errorLogPath = errorLogText.getText();
        String delayTimeLogSize = delayTimeText.getText();
        String agentPort = agentPortText.getText();

        boolean isValidDbName = ValidateUtil.isValidDBName(dbName);
        if (!isValidDbName) {
            setErrorMessage(Messages.errDatabaseName);
            setEnabled(false);
            return;
        }
        boolean isDatabaseExist = false;
        if (isValidDbName && isValidHost) {
            isDatabaseExist = checkDatabaseExist(dbName);
        }
        if (isDatabaseExist) {
            setErrorMessage(Messages.errDbExist);
            setEnabled(false);
            return;
        }
        boolean isValidDbPathName = ValidateUtil.isValidPathName(dbPath);
        if (!isValidDbPathName) {
            setErrorMessage(Messages.errDbPath);
            setEnabled(false);
            return;
        }
        boolean isValidDbaPassword =
                dbaPassword.trim().length() >= 4 && dbaPassword.indexOf(" ") < 0;
        if (!isValidDbaPassword) {
            setErrorMessage(Messages.errDbaPassword);
            setEnabled(false);
            return;
        }
        boolean isValidConfirmDbaPassword =
                confirmDbaPassword.length() >= 4 && confirmDbaPassword.indexOf(" ") < 0;
        if (!isValidConfirmDbaPassword) {
            setErrorMessage(Messages.errConfirmDbaPassword);
            setEnabled(false);
            return;
        }
        boolean isEqualPassword = dbaPassword.equals(confirmDbaPassword);
        if (!isEqualPassword) {
            setErrorMessage(Messages.errPasswordNotEqual);
            setEnabled(false);
            return;
        }
        boolean isValidCopyLogPath = ValidateUtil.isValidPathName(copyLogPath);
        if (!isValidCopyLogPath) {
            setErrorMessage(Messages.errCopyLogPath);
            setEnabled(false);
            return;
        }
        boolean isValidTrailLogPath = ValidateUtil.isValidPathName(trailLogPath);
        if (!isValidTrailLogPath) {
            setErrorMessage(Messages.errTrailLogPath);
            setEnabled(false);
            return;
        }
        boolean isValidErrorLogPath = ValidateUtil.isValidPathName(errorLogPath);
        if (!isValidErrorLogPath) {
            setErrorMessage(Messages.errErrorLogPath);
            setEnabled(false);
            return;
        }
        boolean isValidDelayTimeLogSize = ValidateUtil.isNumber(delayTimeLogSize);
        if (isValidDelayTimeLogSize) {
            isValidDelayTimeLogSize = 0 <= Integer.parseInt(delayTimeLogSize);
        }
        if (!isValidDelayTimeLogSize) {
            setErrorMessage(Messages.errDelayTimeLogSize);
            setEnabled(false);
            return;
        }
        boolean isValidAgentPort = ValidateUtil.isNumber(agentPort);
        if (isValidAgentPort) {
            isValidAgentPort =
                    1024 <= Integer.parseInt(agentPort) && Integer.parseInt(agentPort) <= 65535;
        }
        if (!isValidAgentPort) {
            setErrorMessage(Messages.errReplAgentPort);
            setEnabled(false);
            return;
        }
        setErrorMessage(null);
        setEnabled(true);
    }

    /**
     * Check whether the database exist
     *
     * @param dbName the database name
     * @return <code>true</code> if already exist;<code>false</code> otherwise
     */
    private boolean checkDatabaseExist(String dbName) {
        boolean isDatabaseExist = false;
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
                        && !dbName.equals(distributorNode.getDbName())) {
                    isDatabaseExist = true;
                    break;
                }
            }
        }
        return isDatabaseExist;
    }

    /**
     * set button to enable
     *
     * @param isEnabled boolean
     */
    private void setEnabled(boolean isEnabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
        }
    }

    public DistributorNode getDistributor() {
        return this.distributorNode;
    }

    public void setDistributor(DistributorNode distributor) {
        this.distributorNode = distributor;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
}
