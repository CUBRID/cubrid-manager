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
package com.cubrid.common.ui.common.control;

import static com.cubrid.common.ui.query.Messages.autoCommitLabel;
import static com.cubrid.common.ui.query.Messages.errBrokerPort;
import static com.cubrid.common.ui.query.Messages.shardBrokerAlert;
import static com.cubrid.common.ui.query.Messages.shardIdLabel;
import static com.cubrid.common.ui.query.Messages.shardValLabel;
import static com.cubrid.common.ui.spi.model.CubridDatabase.hasValidDatabaseInfo;
import static com.cubrid.common.ui.spi.util.CommonUITool.createGridData;
import static com.cubrid.common.ui.spi.util.CommonUITool.createGridLayout;
import static com.cubrid.common.ui.spi.util.CommonUITool.openWarningBox;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.swt.layout.GridData.BEGINNING;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.dialog.JdbcManageDialog;
import com.cubrid.common.ui.common.dialog.JdbcOptionDialog;
import com.cubrid.common.ui.common.dialog.ShardIdSelectionDialog;
import com.cubrid.common.ui.spi.Constants;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.FormValidateUtil;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * Connection composite
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-27 created by pangqiren
 */
public class ConnectionComposite extends Composite {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(ConnectionComposite.class);

    private final boolean isIncludingSavePwd;
    private Text userNameText;
    private Text passwordText;
    private Text brokerPortText;
    private Text databaseText;
    private Text attrText;
    private Text brokerIpText;
    private Combo jdbcCombo;
    private Combo charsetCombo;
    private Combo databaseCombo;
    private Combo brokerPortCombo;
    private Button btnSavePassword;
    private Button btnAutoCommit;
    private Button btnShard;
    private Button btnShardId;
    private String errorMsg;
    private int curShardId;
    private int curShardVal;
    private int shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_ID;
    private DatabaseInfo dbInfo;
    private boolean isMultiBroker;

    public ConnectionComposite(
            Composite parent, boolean isIncludingSavePwd, boolean isMultiBroker) {
        super(parent, SWT.NONE);
        this.isIncludingSavePwd = isIncludingSavePwd;
        this.isMultiBroker = isMultiBroker;
        setLayoutData(new GridData(GridData.FILL_BOTH));
        setLayout(new GridLayout());
        createBrokerInfoGroup(this);
        createDbInfoGroup(this);
    }

    private void createDbInfoGroup(Composite composite) {
        Group dbInfoGroup = new Group(composite, SWT.NONE);
        dbInfoGroup.setText(Messages.grpDbInfo);
        dbInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dbInfoGroup.setLayout(createGridLayout(3));

        Label databaseLabel = new Label(dbInfoGroup, SWT.LEFT);
        databaseLabel.setText(Messages.lblLoginDatabaseName);
        databaseLabel.setLayoutData(createGridData(1, 1, -1, -1));

        if (isMultiBroker) {
            databaseCombo = new Combo(dbInfoGroup, SWT.LEFT | SWT.BORDER);
            databaseCombo.setLayoutData(createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        } else {
            databaseText = new Text(dbInfoGroup, SWT.LEFT | SWT.BORDER);
            databaseText.setLayoutData(createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        }

        Label userNameLabel = new Label(dbInfoGroup, SWT.LEFT);
        userNameLabel.setText(Messages.lblDbUserName);
        userNameLabel.setLayoutData(createGridData(1, 1, -1, -1));
        userNameText = new Text(dbInfoGroup, SWT.LEFT | SWT.BORDER);
        userNameText.setLayoutData(createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

        Label passwordLabel = new Label(dbInfoGroup, SWT.LEFT);
        passwordLabel.setText(Messages.lblDbPassword);
        passwordLabel.setLayoutData(createGridData(1, 1, -1, -1));
        passwordText = new Text(dbInfoGroup, SWT.LEFT | SWT.PASSWORD | SWT.BORDER);
        passwordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
        passwordText.setLayoutData(createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        passwordText.addFocusListener(
                new FocusAdapter() {
                    public void focusGained(FocusEvent event) {
                        passwordText.selectAll();
                        passwordText.setFocus();
                    }
                });

        new Label(dbInfoGroup, SWT.NONE).setLayoutData(createGridData(1, 1, 0, 0));

        int span = 2;
        if (isIncludingSavePwd) {
            btnSavePassword = new Button(dbInfoGroup, SWT.CHECK);
            btnSavePassword.setLayoutData(createGridData(GridData.BEGINNING, 1, 1, -1, -1));
            btnSavePassword.setText(Messages.btnSavePassword);
            span = 1;
        }

        btnAutoCommit = new Button(dbInfoGroup, SWT.CHECK);
        btnAutoCommit.setLayoutData(createGridData(GridData.BEGINNING, span, 1, -1, -1));
        btnAutoCommit.setText(autoCommitLabel);
    }

    private void createBrokerInfoGroup(Composite composite) {
        Group brokerInfoGroup = new Group(composite, SWT.NONE);
        brokerInfoGroup.setText(Messages.grpBrokerInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        brokerInfoGroup.setLayoutData(gridData);
        brokerInfoGroup.setLayout(createGridLayout(3));

        Label brokerIpLabel = new Label(brokerInfoGroup, SWT.LEFT);
        brokerIpLabel.setText(Messages.lblLoginServerName);
        brokerIpLabel.setLayoutData(createGridData(1, 1, -1, -1));
        brokerIpText = new Text(brokerInfoGroup, SWT.LEFT | SWT.BORDER);
        brokerIpText.setLayoutData(createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        brokerIpText.addFocusListener(
                new FocusAdapter() {
                    public void focusGained(FocusEvent event) {
                        brokerIpText.selectAll();
                    }
                });

        Label brokerPortLabel = new Label(brokerInfoGroup, SWT.LEFT);
        brokerPortLabel.setText(Messages.lblLoginBrokerPort);
        brokerPortLabel.setLayoutData(createGridData(1, 1, -1, -1));
        VerifyListener verifyListener =
                new VerifyListener() {
                    public void verifyText(VerifyEvent event) {
                        Pattern pattern = Pattern.compile("[0-9]*");
                        Matcher matcher = pattern.matcher(event.text);
                        if (matcher.matches()) {
                            event.doit = true;
                        } else if (event.text.length() > 0) {
                            event.doit = false;
                        } else {
                            event.doit = true;
                        }
                    }
                };

        Composite portAndShardComp = new Composite(brokerInfoGroup, SWT.NONE);
        portAndShardComp.setLayout(createGridLayout(3, 0, 0));
        portAndShardComp.setLayoutData(createGridData(FILL_HORIZONTAL, 2, 1, -1, -1));

        if (isMultiBroker) {
            brokerPortCombo = new Combo(portAndShardComp, SWT.LEFT | SWT.BORDER);
            brokerPortCombo.setLayoutData(createGridData(GridData.BEGINNING, 1, 1, 100, -1));
            brokerPortCombo.addVerifyListener(verifyListener);
        } else {
            brokerPortText = new Text(portAndShardComp, SWT.LEFT | SWT.BORDER);
            brokerPortText.setLayoutData(createGridData(BEGINNING, 1, 1, 100, -1));
            brokerPortText.addVerifyListener(verifyListener);
        }

        btnShard = new Button(portAndShardComp, SWT.CHECK);
        btnShard.setLayoutData(createGridData(BEGINNING, 1, 1, -1, -1));
        btnShard.setText(com.cubrid.common.ui.query.Messages.shardBrokerLabel);
        btnShard.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if (btnShard.getSelection()) {
                            openWarningBox(shardBrokerAlert);
                        }
                        btnShardId.setEnabled(btnShard.getSelection());
                    }
                });

        btnShardId = new Button(portAndShardComp, SWT.PUSH);
        btnShardId.setLayoutData(createGridData(HORIZONTAL_ALIGN_END, 1, 1, -1, -1));
        btnShardId.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        ShardIdSelectionDialog dialog = new ShardIdSelectionDialog(getShell());
                        dialog.setDatabaseInfo(dbInfo);
                        dialog.setShardId(curShardId);
                        dialog.setShardVal(curShardVal);
                        dialog.setShardQueryType(shardQueryType);
                        if (dialog.open() == OK_ID) {
                            curShardId = dialog.getShardId();
                            curShardVal = dialog.getShardVal();
                            shardQueryType = dialog.getShardQueryType();
                            if (dbInfo != null) {
                                dbInfo.setCurrentShardId(curShardId);
                                dbInfo.setCurrentShardVal(curShardVal);
                                dbInfo.setShardQueryType(shardQueryType);
                            }
                            updateShardIdButtonText();
                        }
                    }
                });

        updateShardIdButtonText();

        Label charsetLabel = new Label(brokerInfoGroup, SWT.LEFT);
        charsetLabel.setText(com.cubrid.common.ui.query.Messages.lblCharSet);
        charsetLabel.setLayoutData(createGridData(1, 1, -1, -1));
        charsetCombo = new Combo(brokerInfoGroup, SWT.LEFT | SWT.BORDER);
        charsetCombo.setLayoutData(createGridData(FILL_HORIZONTAL, 2, 1, 100, -1));

        Label jdbcLabel = new Label(brokerInfoGroup, SWT.LEFT);
        jdbcLabel.setText(Messages.lblDbJdbcVersion);
        jdbcLabel.setLayoutData(createGridData(1, 1, -1, -1));
        jdbcCombo = new Combo(brokerInfoGroup, SWT.LEFT | SWT.READ_ONLY | SWT.BORDER);
        jdbcCombo.setLayoutData(createGridData(FILL_HORIZONTAL, 1, 1, 100, -1));

        Button btnOpen = new Button(brokerInfoGroup, SWT.NONE);
        btnOpen.setText(Messages.btnBrowse);
        btnOpen.setLayoutData(createGridData(1, 1, 80, -1));
        btnOpen.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        JdbcManageDialog dialog = new JdbcManageDialog(getShell());
                        if (dialog.open() == OK_ID) {
                            String jdbcVersion = dialog.getSelectedJdbcVersion();
                            if (isBlank(jdbcVersion)) {
                                jdbcVersion = jdbcCombo.getText();
                            }
                            resetJdbcCombo(jdbcVersion);
                        }
                    }
                });

        // JDBC attributes
        Label attrLabel = new Label(brokerInfoGroup, SWT.LEFT);
        attrLabel.setText(Messages.lblJdbcAttr);
        attrLabel.setLayoutData(createGridData(1, 1, -1, -1));

        attrText = new Text(brokerInfoGroup, SWT.LEFT | SWT.BORDER);
        attrText.setEditable(false);
        attrText.setLayoutData(createGridData(FILL_HORIZONTAL, 1, 1, 100, -1));

        Button btnAttr = new Button(brokerInfoGroup, SWT.NONE);
        btnAttr.setText(Messages.btnJdbcAttr);
        btnAttr.setLayoutData(createGridData(1, 1, 80, -1));
        btnAttr.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        JdbcOptionDialog dialog =
                                new JdbcOptionDialog(getShell(), attrText.getText());
                        if (dialog.open() == OK_ID) {
                            attrText.setText(dialog.getJdbcOptions());
                        }
                    }
                });
    }

    public void updateShardIdButtonText() {
        String buttonMessage;
        if (shardQueryType == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
            buttonMessage = Messages.bind(shardIdLabel, curShardId);
        } else {
            buttonMessage = Messages.bind(shardValLabel, curShardVal);
        }
        btnShardId.setText(buttonMessage);
        btnShardId.pack();
    }

    public void init(CubridDatabase database, ModifyListener listener) {
        resetJdbcCombo(null);
        changeData(database);

        if (hasValidDatabaseInfo(database)) {
            this.dbInfo = database.getDatabaseInfo();
        } else {
            this.dbInfo = null;
        }

        if (hasValidDatabaseInfo(database)) {
            curShardId = database.getDatabaseInfo().getCurrentShardId();
            curShardVal = database.getDatabaseInfo().getCurrentShardVal();
            shardQueryType = database.getDatabaseInfo().getShardQueryType();
        }
        updateShardIdButtonText();
        btnShardId.setEnabled(btnShard.getSelection());

        if (listener != null) {
            if (databaseText != null) {
                databaseText.addModifyListener(listener);
            }
            if (databaseCombo != null) {
                databaseCombo.addModifyListener(listener);
            }
            userNameText.addModifyListener(listener);
            passwordText.addModifyListener(listener);
            brokerIpText.addModifyListener(listener);
            if (brokerPortText != null) {
                brokerPortText.addModifyListener(listener);
            }
            if (brokerPortCombo != null) {
                brokerPortCombo.addModifyListener(listener);
            }
            jdbcCombo.addModifyListener(listener);
        }
    }

    public void changeData(CubridDatabase database) {
        charsetCombo.removeAll();

        if (database == null) {
            showDefaultForm();
            return;
        }

        dbInfo = database.getDatabaseInfo();
        if (dbInfo == null) {
            showDefaultForm();
            return;
        }

        ServerInfo serverInfo = dbInfo.getServerInfo();
        if (serverInfo == null) {
            showDefaultForm();
            return;
        }

        String databaseName = dbInfo.getDbName();
        String brokerIp = dbInfo.getBrokerIP();
        String brokerPort = dbInfo.getBrokerPort();
        String userName = dbInfo.getAuthLoginedDbUserInfo().getName();
        String userPassword = dbInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
        String charset = dbInfo.getCharSet();
        String jdbcAttrs = dbInfo.getJdbcAttrs();
        String jdbcVersion = serverInfo.getJdbcDriverVersion();

        if (databaseText != null) {
            databaseText.setText(databaseName);
        }

        userNameText.setText(userName);
        btnAutoCommit.setSelection(QueryOptions.getAutoCommit(serverInfo));

        if (database.isAutoSavePassword()) {
            passwordText.setText(StringUtil.nvl(userPassword));
            if (isIncludingSavePwd) {
                btnSavePassword.setSelection(true);
            }
        } else {
            passwordText.setText("");
            if (isIncludingSavePwd) {
                btnSavePassword.setSelection(false);
            }
        }

        brokerIpText.setText(brokerIp);
        if (brokerPortText != null) {
            brokerPortText.setText(brokerPort);
        }

        charsetCombo.setItems(QueryOptions.getAllCharset(charset));
        if (charset == null) {
            charsetCombo.select(0);
        } else {
            charsetCombo.setText(charset);
        }

        jdbcCombo.setText(jdbcVersion);
        attrText.setText(StringUtil.nvl(jdbcAttrs));

        passwordText.selectAll();
        passwordText.setFocus();

        if (hasValidDatabaseInfo(database)) {
            boolean isShard = database.getDatabaseInfo().isShard();
            btnShard.setSelection(isShard);
            btnShardId.setEnabled(isShard);
        }
    }

    private void showDefaultForm() {
        if (databaseText != null) {
            databaseText.setText(Constants.DEFAULT_DBNAME);
        }
        if (databaseCombo != null) {
            databaseCombo.removeAll();
        }
        userNameText.setText(Constants.DEFAULT_DBUSER);
        btnAutoCommit.setSelection(QueryOptions.getAutoCommit(null));
        brokerIpText.setText(Constants.BROKER_DEFAULT_HOST);
        if (brokerPortText != null) {
            brokerPortText.setText(Constants.BROKER_DEFAULT_PORT);
        }
        if (brokerPortCombo != null) {
            brokerPortCombo.removeAll();
            brokerPortCombo.add(Constants.BROKER_DEFAULT_PORT);
            brokerPortCombo.select(0);
        }
        passwordText.setText("");
        charsetCombo.setItems(QueryOptions.getAllCharset(null));
        charsetCombo.select(0);
        if (jdbcCombo.getItemCount() > 0) {
            jdbcCombo.select(0);
        }
    }

    public boolean isAutoSavePassword() {
        if (isIncludingSavePwd) {
            return btnSavePassword.getSelection();
        } else {
            return false;
        }
    }

    private void resetJdbcCombo(String jdbcVersion) {
        jdbcCombo.removeAll();
        Map<String, String> jdbcMap = CubridJdbcManager.getInstance().getLoadedJdbc();
        for (Map.Entry<String, String> entry : jdbcMap.entrySet()) {
            jdbcCombo.add(entry.getKey());
        }
        if (jdbcCombo.getItemCount() > 0) {
            jdbcCombo.select(0);
        }
        if (isNotBlank(jdbcVersion)) {
            jdbcCombo.setText(jdbcVersion);
        }
    }

    public boolean valid() {
        errorMsg = null;

        String dbName = FormValidateUtil.getString(databaseText);
        if (dbName == null) {
            dbName = FormValidateUtil.getString(databaseCombo);
        }

        if (isBlank(dbName)
                || FormValidateUtil.isEmpty(userNameText)
                || FormValidateUtil.isEmpty(brokerIpText)) {
            return false;
        }

        String brokerPort = FormValidateUtil.getString(brokerPortText);
        if (brokerPort == null) {
            brokerPort = FormValidateUtil.getString(brokerPortCombo);
        }

        if (!ValidateUtil.betweenValues(brokerPort, 1024, 65535)) {
            errorMsg = errBrokerPort;
            return false;
        }

        if (FormValidateUtil.isEmpty(charsetCombo) || FormValidateUtil.isEmpty(jdbcCombo)) {
            return false;
        }

        return true;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Text getPasswordText() {
        return passwordText;
    }

    public Text getBrokerIpText() {
        return brokerIpText;
    }

    public Text getUserNameText() {
        return userNameText;
    }

    public Text getBrokerPortText() {
        return brokerPortText;
    }

    public Text getDatabaseText() {
        return databaseText;
    }

    public Combo getDatabaseCombo() {
        return databaseCombo;
    }

    public Combo getJdbcCombo() {
        return jdbcCombo;
    }

    public Combo getCharsetCombo() {
        return charsetCombo;
    }

    public Button getBtnShard() {
        return btnShard;
    }

    public Combo getBrokerPortCombo() {
        return brokerPortCombo;
    }

    public boolean isAutoCommit() {
        return btnAutoCommit.getSelection();
    }

    public Text getAttrText() {
        return attrText;
    }

    public void setAttrText(Text attrText) {
        this.attrText = attrText;
    }

    public int getCurShardId() {
        return curShardId;
    }

    public int getCurShardVal() {
        return curShardVal;
    }

    public int getShardQueryType() {
        return shardQueryType;
    }
}
