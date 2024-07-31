/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.control.SelectColorCombo;
import com.cubrid.common.ui.common.dialog.JdbcOptionDialog;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ConnectDatabaseExecutor;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMDBNodePersistManager;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * The dialog is used to login database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class LoginDatabaseDialog extends CMTitleAreaDialog implements ModifyListener {
    // private static final Logger LOGGER = LogUtil.getLogger(LoginDatabaseDialog.class);
    public static final int SAVE_ID = -1;
    private Text userNameText;
    private Text passwordText;
    private Text commentText;
    private Text attrText;
    private CubridDatabase database;
    // private CubridDatabase oldDatabase;
    private boolean isSavePassword;
    private SelectColorCombo selectColorCombo;
    private boolean fireLogoutEvent = false;
    private String oldLoginUserName;
    private boolean oldDatabaseIsLogin = false;
    private Combo brokerPortCombo;
    private Text brokerIpText;
    private Combo charsetCombo;
    private final String USER_DBA = "dba";

    public LoginDatabaseDialog(Shell parentShell, CubridDatabase database) {
        super(parentShell);
        this.database = database;
        //		try {
        //			this.oldDatabase = database.clone();
        //		} catch (CloneNotSupportedException e) {
        //		}
        oldLoginUserName = database.getUserName();
        oldDatabaseIsLogin = database.isLogined();
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        final Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 5;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label userNameLabel = new Label(composite, SWT.LEFT);
        userNameLabel.setText(Messages.lblDbUserName);
        userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        userNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        if (database != null && database.getUserName() != null) {
            userNameText.setText(database.getUserName());
        }
        userNameText.addModifyListener(this);
        userNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, 100, -1));
        userNameText.setFocus();

        Label passwordLabel = new Label(composite, SWT.LEFT);
        passwordLabel.setText(Messages.lblDbPassword);
        passwordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        passwordText = new Text(composite, SWT.LEFT | SWT.PASSWORD | SWT.BORDER);
        passwordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
        passwordText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, 100, -1));

        if (database != null && database.getPassword() != null && database.isAutoSavePassword()) {
            passwordText.setText(database.getPassword());
        }

        if (database != null && database.getUserName() != null) {
            passwordText.selectAll();
            passwordText.setFocus();
        }

        new Composite(composite, SWT.NONE).setLayoutData(CommonUITool.createGridData(1, 1, 0, 0));

        Button btnSavePassword = new Button(composite, SWT.CHECK);
        btnSavePassword.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_FILL, 1, 1, -1, -1));
        btnSavePassword.setText(Messages.btnSavePassword);
        if (database == null) {
            btnSavePassword.setSelection(true);
            isSavePassword = true;
        } else {
            btnSavePassword.setSelection(database.isAutoSavePassword());
            isSavePassword = database.isAutoSavePassword();
        }

        btnSavePassword.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        isSavePassword = !isSavePassword;
                    }
                });

        Label backgroundLabel = new Label(composite, SWT.None);
        backgroundLabel.setText(Messages.lblBackground);
        backgroundLabel.setLayoutData(
                CommonUITool.createGridData(
                        GridData.HORIZONTAL_ALIGN_END | GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, true);
        RGB selectedColor = null;
        if (editorConfig != null) {
            selectedColor = editorConfig.getBackGround();
        } else {
            selectedColor = EditorConstance.getDefaultBackground();
        }
        selectColorCombo = new SelectColorCombo(composite, SWT.BORDER, selectedColor);
        selectColorCombo.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 2, 1, 110, -1));

        Label commentLabel = new Label(composite, SWT.None);
        commentLabel.setText(Messages.lblComment);
        commentLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        commentText = new Text(composite, SWT.BORDER);
        commentText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, -1, -1));
        commentText.setTextLimit(64);
        if (editorConfig != null) {
            commentText.setText(StringUtil.nvl(editorConfig.getDatabaseComment()));
        }

        new Label(composite, SWT.None);

        Label commentDescLabel = new Label(composite, SWT.None);
        commentDescLabel.setText(Messages.lblDescComment);
        commentDescLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 4, 1, -1, -1));

        final Group brokerGroup = new Group(composite, SWT.NONE);
        {
            brokerGroup.setText(com.cubrid.common.ui.query.Messages.brokerGrp);
            brokerGroup.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 5, 1, -1, -1));
            GridLayout brokerLayout = new GridLayout();
            brokerLayout.numColumns = 3;
            brokerLayout.marginHeight =
                    convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            brokerLayout.marginWidth =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            brokerLayout.verticalSpacing =
                    convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            brokerLayout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            brokerGroup.setLayout(brokerLayout);

            final Label labelBrokerIp = new Label(brokerGroup, SWT.NONE);
            labelBrokerIp.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            labelBrokerIp.setText(com.cubrid.common.ui.query.Messages.brokerIP);

            brokerIpText = new Text(brokerGroup, SWT.BORDER);
            brokerIpText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

            final Label labelBrokerPort = new Label(brokerGroup, SWT.NONE);
            labelBrokerPort.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            labelBrokerPort.setText(com.cubrid.common.ui.query.Messages.brokerPort);

            brokerPortCombo = new Combo(brokerGroup, SWT.NONE);
            brokerPortCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
            BrokerInfos brokerInfos = database.getServer().getServerInfo().getBrokerInfos();
            BrokerInfoList bis = brokerInfos == null ? null : brokerInfos.getBorkerInfoList();
            if (bis != null) {
                List<BrokerInfo> brokerInfoList = bis.getBrokerInfoList();
                for (BrokerInfo brokerInfo : brokerInfoList) {
                    if (StringUtil.isEmpty(brokerInfo.getPort())) {
                        continue;
                    }

                    String status = "";
                    if (!StringUtil.isEqualIgnoreCase(brokerInfos.getBrokerstatus(), "ON")) {
                        status = "OFF";
                    } else {
                        status =
                                !StringUtil.isEqualIgnoreCase(brokerInfo.getState(), "ON")
                                        ? "OFF"
                                        : "ON";
                    }

                    String text =
                            brokerInfo.getName() + "[" + brokerInfo.getPort() + "/" + status + "]";
                    brokerPortCombo.add(text);
                    brokerPortCombo.setData(brokerInfo.getPort(), text);
                    brokerPortCombo.setData(text, brokerInfo);
                }
            }

            Label charSetLbl = new Label(brokerGroup, SWT.CHECK);
            charSetLbl.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            charSetLbl.setText(com.cubrid.common.ui.query.Messages.lblCharSet);

            charsetCombo = new Combo(brokerGroup, SWT.BORDER);
            charsetCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

            Button testConnectionButton = new Button(brokerGroup, SWT.None);
            testConnectionButton.setText(com.cubrid.common.ui.query.Messages.btnTestConnection);
            testConnectionButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            testConnectionButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            widgetDefaultSelected(e);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {
                            final String dbUser = userNameText.getText();
                            final String dbPassword = passwordText.getText();

                            final DatabaseInfo dbInfo = database.getDatabaseInfo();

                            final String brokerIP = brokerIpText.getText();
                            final String brokerPort = getBrokerPort();

                            final String dbName = dbInfo.getDbName();
                            final ServerInfo serverInfo = dbInfo.getServerInfo();

                            final String charset = dbInfo.getCharSet();
                            final String driverVersion = serverInfo.getJdbcDriverVersion();

                            // advanced jdbc settings
                            final String jdbcAttrs = attrText.getText();
                            final boolean isShard = dbInfo.isShard();

                            TaskExecutor taskExcutor =
                                    new ConnectDatabaseExecutor(
                                            brokerIP,
                                            brokerPort,
                                            dbName,
                                            dbUser,
                                            dbPassword,
                                            charset,
                                            jdbcAttrs,
                                            driverVersion,
                                            false,
                                            isShard);
                            new ExecTaskWithProgress(taskExcutor).exec();

                            if (taskExcutor.isSuccess()) {
                                CommonUITool.openInformationBox(
                                        Messages.titleSuccess, Messages.msgTestConnSuccess);
                            }
                        }
                    });
        }

        Group advancedOptionGroup = new Group(composite, SWT.NONE);
        {
            advancedOptionGroup.setText(Messages.grpAdvancedJDBC);
            advancedOptionGroup.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 5, 1, -1, -1));
            GridLayout brokerInfoGroupLayout = new GridLayout();
            brokerInfoGroupLayout.numColumns = 3;
            advancedOptionGroup.setLayout(brokerInfoGroupLayout);

            // JDBC attributes
            Label attrLabel = new Label(advancedOptionGroup, SWT.LEFT);
            attrLabel.setText(com.cubrid.common.ui.common.Messages.lblJdbcAttr);
            attrLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

            attrText = new Text(advancedOptionGroup, SWT.LEFT | SWT.BORDER);
            attrText.setEditable(false);
            attrText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

            Button btnAttr = new Button(advancedOptionGroup, SWT.NONE);
            {
                btnAttr.setText(com.cubrid.common.ui.common.Messages.btnJdbcAttr);
                btnAttr.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
                btnAttr.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                JdbcOptionDialog dialog =
                                        new JdbcOptionDialog(getShell(), attrText.getText());
                                if (IDialogConstants.OK_ID == dialog.open()) {
                                    String jdbcOptions = dialog.getJdbcOptions();
                                    attrText.setText(StringUtil.nvl(jdbcOptions));
                                }
                            }
                        });
            }
        }

        if (database != null && database.getDatabaseInfo() != null) {
            String jdbcAttrs = database.getDatabaseInfo().getJdbcAttrs();
            attrText.setText(StringUtil.nvl(jdbcAttrs));

            String brokerIp = QueryOptions.getBrokerIp(database.getDatabaseInfo());
            if (StringUtil.isEmpty(brokerIp)) {
                brokerIp = database.getServer().getHostAddress();
            }
            if (brokerIp != null) {
                brokerIpText.setText(brokerIp);
            }

            String brokerPort = QueryOptions.getBrokerPort(database.getDatabaseInfo());
            brokerPort = (String) brokerPortCombo.getData(brokerPort);
            if (brokerPort != null) {
                brokerPortCombo.setText(brokerPort);
            }

            String charset = QueryOptions.getCharset(database.getDatabaseInfo());
            charsetCombo.setItems(QueryOptions.getAllCharset(charset));
            if (charset != null && charset.trim().length() > 0) {
                charsetCombo.setText(charset);
            } else {
                charsetCombo.select(0);
            }
        }

        setTitle(Messages.titleLoginDbDialog);
        setMessage(Messages.msgLoginDbDialog);

        return parentComp;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleLoginDbDialog);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        /* Save button */
        createButton(parent, SAVE_ID, Messages.btnConnectSave, false);
        /* Connect button */
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        if (database == null || StringUtil.isEmpty(database.getUserName())) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        /* Cancel button */
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            boolean isConnected = connect();
            if (!isConnected) {
                return;
            }

            fireLogoutEvent = save();
            // open database dashboard
            if (GeneralPreference.isUseDatabaseDashboard()) {
                openEditorOrView((ICubridNode) database);
            }
        } else if (buttonId == SAVE_ID) {
            save();
        }
        setReturnCode(buttonId);
        close();
    }

    /**
     * Open and reopen the editor part of this CUBRID node
     *
     * @param cubridNode the ICubridNode object
     */
    public void openEditorOrView(ICubridNode cubridNode) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        // close the editor part that has been open
        String editorId = cubridNode.getEditorId();
        if (editorId != null && editorId.trim().length() > 0) {
            IEditorPart editorPart = LayoutUtil.getEditorPart(cubridNode, editorId);
            if (editorPart != null) {
                window.getActivePage().closeEditor(editorPart, false);
            }
        }

        LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(cubridNode);
    }

    /**
     * Get the broker port
     *
     * @return
     */
    private String getBrokerPort() {
        String text = brokerPortCombo.getText();

        if (brokerPortCombo.getData(text) != null) {
            Object obj = brokerPortCombo.getData(text);
            if (obj != null && obj instanceof BrokerInfo) {
                BrokerInfo brokerInfo = (BrokerInfo) obj;
                return brokerInfo.getPort();
            }
        }

        int leftBracketIndex = text.indexOf("[");
        if (leftBracketIndex >= 0 && leftBracketIndex + 1 < text.length()) {
            String subString = text.substring(leftBracketIndex + 1);
            int lineIndex = subString.indexOf("/");
            if (lineIndex >= 1) {
                return subString.substring(0, lineIndex);
            }

            int rightBracketIndex = subString.indexOf("]");
            if (rightBracketIndex >= 1) {
                return subString.substring(0, rightBracketIndex);
            }

            return subString;
        }
        return text;
    }
    /** Execute task and login database */
    private boolean connect() {
        final String dbUser = userNameText.getText();
        final String dbPassword = passwordText.getText();

        database.getDatabaseInfo().setJdbcAttrs(attrText.getText());
        final DatabaseInfo dbInfo = database.getDatabaseInfo();
        database.setAutoSavePassword(isSavePassword);

        TaskExecutor taskExcutor =
                new LoginDatabaseTaskExecutor(
                        getShell(),
                        dbInfo.getServerInfo(),
                        dbInfo.getDbName(),
                        dbUser,
                        dbPassword,
                        true);
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
        if (!taskExcutor.isSuccess() && passwordText != null && !passwordText.isDisposed()) {
            passwordText.selectAll();
            passwordText.setFocus();
        }

        if (taskExcutor.isSuccess()) {
            if (CompatibleUtil.isAfter112(dbInfo.getServerInfo())) {
                dbInfo.setSupportUserSchema(true);
            }
            saveBrokerInfo();
        }

        /*For [TOOLS-3516]*/
        if (dbInfo.getAuthLoginedDbUserInfo() != null
                && CompatibleUtil.isNeedCheckDbaAuthorityByJDBC(dbInfo)) {
            IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
            checkTask.execute();
            if (checkTask.isSuccess()) {
                dbInfo.getAuthLoginedDbUserInfo().setDbaAuthority(checkTask.isDBAUser());
            } else {
                dbInfo.getAuthLoginedDbUserInfo()
                        .setDbaAuthority(USER_DBA.equals(dbUser.toLowerCase()));
            }
        }

        return taskExcutor.isSuccess();
    }

    private boolean save() {
        if (database == null) {
            return false;
        }

        database.setAutoSavePassword(isSavePassword);

        final String dbUser = userNameText.getText();
        final String dbPassword = passwordText.getText();

        if (database.getDatabaseInfo() != null) {
            database.getDatabaseInfo().setJdbcAttrs(attrText.getText());

            DbUserInfo info = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            if (info != null) {
                info.setName(dbUser);
                info.setNoEncryptPassword(dbPassword);
            }

            CMDBNodePersistManager.getInstance()
                    .updateDbPassword(
                            database.getServer().getHostAddress(),
                            database.getServer().getMonPort(),
                            database.getDatabaseInfo().getDbName(),
                            dbUser,
                            dbPassword,
                            isSavePassword);
            CMDBNodePersistManager.getInstance().setJdbcAttrs(database, attrText.getText());

            DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, true);
            if (editorConfig == null) {
                editorConfig = new DatabaseEditorConfig();
            }
            RGB selectedColor = selectColorCombo.getSelectedColor();
            editorConfig.setBackGround(selectedColor);
            editorConfig.setDatabaseComment(commentText.getText());

            QueryOptions.putEditorConfig(database, editorConfig, true);
            /* TOOLS-1222Edit database dialog can't save password */
            CMDBNodePersistManager.getInstance().addDatabase(database, editorConfig);

            saveBrokerInfo();

            boolean userChanged = !dbUser.equals(oldLoginUserName);
            return oldDatabaseIsLogin && userChanged;
        }

        return false;
    }

    private void saveBrokerInfo() {
        DatabaseInfo databaseInfo = database.getDatabaseInfo();

        String charset = charsetCombo.getText();
        QueryOptions.setCharset(databaseInfo, charset);

        String brokerPort = "";
        if (database != null) {
            String text = brokerPortCombo.getText();
            BrokerInfo brokerInfo = null;
            if (brokerPortCombo.getData(text) instanceof BrokerInfo) {
                brokerInfo = (BrokerInfo) brokerPortCombo.getData(text);
            }
            if (brokerInfo != null) {
                brokerPort = brokerInfo.getPort();
            } else {
                brokerPort = getBrokerPort();
            }
        }
        if (brokerPort != null) {
            QueryOptions.setBrokerPort(databaseInfo, brokerPort);
        }
        QueryOptions.setBrokerIp(databaseInfo, brokerIpText.getText());
        QueryOptions.savePref();
    }

    public void modifyText(ModifyEvent event) {
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        /* Check the user name is valid */
        String userName = userNameText.getText();
        boolean isValidUserName = userName.trim().length() > 0 && userName.indexOf(" ") < 0;
        if (!isValidUserName) {
            setErrorMessage(Messages.errUserName);
            if (userNameText != null && !userNameText.isDisposed()) {
                userNameText.selectAll();
                userNameText.setFocus();
            }

            return;
        }
        /*For bug TOOLS-2983*/
        //		/* Check the use is authorized */
        //		boolean isAuthorizedUser = false;
        //		ServerUserInfo userInfo = database.getServer().getServerInfo().getLoginedUserInfo();
        //		if (userInfo.isAdmin()) {
        //			isAuthorizedUser = true;
        //		} else if (userInfo != null && isValidUserName) {
        //			isAuthorizedUser = userName.equalsIgnoreCase(database.getUserName());
        //		}
        //		if (!isAuthorizedUser) {
        //			setErrorMessage(Messages.errUnauthorizedUser);
        //			return;
        //		}

        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public boolean isFireLogoutEvent() {
        return fireLogoutEvent;
    }

    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    public RGB getSelectedColor() {
        return selectColorCombo.getSelectedColor();
    }
}
