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
package com.cubrid.cubridquery.ui.common.dialog;

import com.cubrid.common.ui.common.control.ConnectionComposite;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ConnectDatabaseExecutor;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask;
import com.cubrid.cubridmanager.ui.spi.model.loader.CQBDbConnectionLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridquery.ui.common.Messages;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Login Query editor dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-10 created by pangqiren
 */
public class LoginQueryEditorDialog extends CMTitleAreaDialog implements ModifyListener {

    public static final int TEST_CONNECT_ID = 3;

    private ConnectionComposite connectionComp;
    private Combo allConnCombo;
    private String selectedConnName;
    private final Map<String, CubridDatabase> dbMap = new HashMap<String, CubridDatabase>();

    /**
     * The constructor
     *
     * @param parentShell
     */
    public LoginQueryEditorDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        Group connInfoGroup = new Group(composite, SWT.NONE);
        {
            connInfoGroup.setText(Messages.grpRegisteredConnInfo);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            connInfoGroup.setLayoutData(gridData);
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            connInfoGroup.setLayout(layout);

            Label allConnLabel = new Label(connInfoGroup, SWT.LEFT);
            allConnLabel.setText(Messages.lblConnName);
            allConnLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

            allConnCombo = new Combo(connInfoGroup, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
            allConnCombo.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            selectedConnName = allConnCombo.getText();
                            changeSelection();
                            valid();
                        }
                    });
            allConnCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
        }

        connectionComp = new ConnectionComposite(composite, false, false);

        setTitle(Messages.titleNewQueryDialog);
        setMessage(Messages.msgNewQueryDialog);
        initial();
        return parentComp;
    }

    /** initialize some values */
    private void initial() {
        List<CubridDatabase> databaseList = CQBDBNodePersistManager.getInstance().getAllDatabase();
        for (CubridDatabase database : databaseList) {
            dbMap.put(database.getLabel(), database);
            allConnCombo.add(database.getLabel());
        }
        dbMap.put(
                DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL,
                DatabaseNavigatorMenu.SELF_DATABASE);
        allConnCombo.add(DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL);
        allConnCombo.setText(selectedConnName);

        connectionComp.init(dbMap.get(selectedConnName), this);
    }

    /** Change the selection */
    private void changeSelection() {
        connectionComp.changeData(null);

        CubridDatabase database = dbMap.get(selectedConnName);
        if (database != null && database.getDatabaseInfo() != null) {
            connectionComp.changeData(database);
        }
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleNewQueryDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                TEST_CONNECT_ID,
                com.cubrid.cubridquery.ui.connection.Messages.btnTestConn,
                false);
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridquery.ui.connection.Messages.btnConnect,
                true);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.common.ui.common.Messages.btnCancel,
                false);
        valid();
    }

    /** validate the data */
    private void valid() {
        setErrorMessage(null);
        boolean isValid = connectionComp.valid();
        if (!isValid) {
            setButtonStatus(false);
            String errorMsg = connectionComp.getErrorMsg();
            if (errorMsg != null) {
                setErrorMessage(errorMsg);
            }
            return;
        }
        setButtonStatus(true);
    }

    /**
     * Set button status
     *
     * @param isEnabled boolean
     */
    private void setButtonStatus(boolean isEnabled) {
        if (getButton(TEST_CONNECT_ID) != null) {
            getButton(TEST_CONNECT_ID).setEnabled(isEnabled);
        }
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == TEST_CONNECT_ID || buttonId == IDialogConstants.OK_ID) {
            String dbName = connectionComp.getDatabaseText().getText();
            String brokerIp = connectionComp.getBrokerIpText().getText();
            String brokerPort = connectionComp.getBrokerPortText().getText();
            String userName = connectionComp.getUserNameText().getText();
            String password = connectionComp.getPasswordText().getText();
            String charset = connectionComp.getCharsetCombo().getText();
            String jdbcDriver = connectionComp.getJdbcCombo().getText();
            String jdbcAttrs = connectionComp.getAttrText().getText().trim();
            boolean isShard = connectionComp.getBtnShard().getSelection();
            int currentShardId = connectionComp.getCurShardId();

            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setServerName(DatabaseNavigatorMenu.SELF_DATABASE_ID);
            serverInfo.setHostAddress(brokerIp);
            serverInfo.setHostMonPort(Integer.parseInt(brokerPort));
            serverInfo.setHostJSPort(Integer.parseInt(brokerPort) + 1);
            serverInfo.setJdbcDriverVersion(jdbcDriver);

            DatabaseInfo dbInfo = new DatabaseInfo(dbName, serverInfo);
            dbInfo.setBrokerIP(brokerIp);
            dbInfo.setBrokerPort(brokerPort);
            dbInfo.setCharSet(charset);
            dbInfo.setJdbcAttrs(jdbcAttrs);
            dbInfo.setShard(isShard);
            dbInfo.setCurrentShardId(currentShardId);

            DbUserInfo userInfo = new DbUserInfo();
            userInfo.setDbName(dbName);
            userInfo.setName(userName);
            userInfo.setNoEncryptPassword(password);
            dbInfo.setAuthLoginedDbUserInfo(userInfo);

            TaskExecutor taskExcutor = new ConnectDatabaseExecutor(dbInfo);
            new ExecTaskWithProgress(taskExcutor).exec();

            if (!taskExcutor.isSuccess()) {
                return;
            }

            if (buttonId == TEST_CONNECT_ID) {
                CommonUITool.openInformationBox(
                        com.cubrid.cubridquery.ui.connection.Messages.titleSuccess,
                        com.cubrid.cubridquery.ui.connection.Messages.msgTestConnSuccess);
                return;
            }
            // check whether dba authorization
            IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
            checkTask.execute();
            userInfo.setDbaAuthority(checkTask.isDBAUser());

            dbInfo.setLogined(true);
            dbInfo.setRunningType(DbRunningType.CS);
            dbInfo.getServerInfo().setConnected(true);
            dbInfo.setLogined(true);

            CubridServer server =
                    new CubridServer(
                            DatabaseNavigatorMenu.SELF_DATABASE_ID,
                            DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL,
                            null,
                            null);
            server.setServerInfo(serverInfo);
            server.setType(NodeType.SERVER);

            DatabaseNavigatorMenu.SELF_DATABASE.setDatabaseInfo(dbInfo);
            DatabaseNavigatorMenu.SELF_DATABASE.setServer(server);
            DatabaseNavigatorMenu.SELF_DATABASE.setStartAndLoginIconPath(
                    "icons/navigator/database_start_connected.png");
            DatabaseNavigatorMenu.SELF_DATABASE.setStartAndLogoutIconPath(
                    "icons/navigator/database_start_disconnected.png");
            DatabaseNavigatorMenu.SELF_DATABASE.setLoader(new CQBDbConnectionLoader());
        }
        super.buttonPressed(buttonId);
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     * @param event an event containing information about the modify
     */
    public void modifyText(ModifyEvent event) {
        valid();
    }

    public String getSelectedConnName() {
        return selectedConnName;
    }

    public void setSelectedConnName(String selectedConnName) {
        this.selectedConnName = selectedConnName;
    }
}
