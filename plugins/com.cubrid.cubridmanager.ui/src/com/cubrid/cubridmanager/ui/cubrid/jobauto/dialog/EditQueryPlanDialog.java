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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.dialog;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.control.SqlParser;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.control.PeriodGroup;
import com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
 * A dialog that show up when a user click the query plan context menu.
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-12 created by lizhiqiang
 */
public class EditQueryPlanDialog extends CMTitleAreaDialog implements Observer {
    private static final String PWDASTERISK = "***************";
    private Text statementText;
    private Text idText;

    private AddEditType operation;
    private PeriodGroup periodGroup;
    private QueryPlanInfoHelp queryPlanInfo;

    private List<String> childrenLabel;
    private CubridDatabase database;
    private boolean isEnabledOkButton[];
    private boolean withUser;
    private Text userText;
    private Text pwdTxt;
    private Button changePassButton;
    public static final int CHECK_ID = 101;

    private boolean isEditable;

    /**
     * Constructor
     *
     * @param parentShell
     */
    public EditQueryPlanDialog(Shell parentShell, boolean isEditAble) {
        super(parentShell);
        this.isEditable = isEditAble;
        isEnabledOkButton = new boolean[] {false, false, true, true, true, true, true};
    }

    /**
     * Create the dialog area
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        final Composite composite = new Composite(parentComp, SWT.RESIZE);
        final GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdComposite.widthHint = 500;
        composite.setLayoutData(gdComposite);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        gridLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        gridLayout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(gridLayout);

        final Group queryGroup = new Group(composite, SWT.NONE);
        final GridData gdQueryGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        queryGroup.setLayoutData(gdQueryGroup);
        queryGroup.setLayout(new GridLayout());
        queryGroup.setText(Messages.msgQryBasicGroupName);

        createBasicGroup(queryGroup);

        periodGroup = new PeriodGroup(this, isEditable);
        periodGroup.addObserver(this);
        boolean isSupportPeriodic =
                CompatibleUtil.isSupportPeriodicAutoJob(database.getServer().getServerInfo());
        periodGroup.setSupportPeriodic(isSupportPeriodic);
        periodGroup.setMsgPeriodGroup(Messages.msgQryPeriodGroup);
        periodGroup.setMsgPeriodTimeLbl(Messages.msgQryPeriodTimeLbl);
        periodGroup.setTimeSplitByColon(true);

        if (operation == AddEditType.EDIT) {
            // Sets the edit title and message
            setMessage(Messages.editQryPlanMsg);
            setTitle(Messages.editQryPlanTitle);
            getShell().setText(Messages.editQryPlanTitle);
            periodGroup.setTypeValue((queryPlanInfo.getPeriod()));
            periodGroup.setDetailValue(queryPlanInfo.getDetail());
            if (isSupportPeriodic && queryPlanInfo.isPeriodic()) {
                periodGroup.setIntervalValue(queryPlanInfo.getInterval());
            } else {
                periodGroup.setTimeValue(queryPlanInfo.getTime());
            }

            isEnabledOkButton[0] = true;
            isEnabledOkButton[1] = true;
        } else {
            setMessage(Messages.addQryPlanMsg);
            setTitle(Messages.addQryPlanTitle);
            getShell().setText(Messages.addQryPlanTitle);
        }
        periodGroup.createPeriodGroup(composite);
        createStatementGroup(composite);
        if (operation == AddEditType.EDIT) {
            statementText.setFocus();
        }

        if (!isEditable) {
            statementText.setEditable(false);
            idText.setEditable(false);
            userText.setEditable(false);
            pwdTxt.setEditable(false);
        }
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CHECK_ID, Messages.btnCheckQuery, true);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        if (operation == AddEditType.ADD) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        if (!isEditable) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            getButton(CHECK_ID).setEnabled(false);
        }
    }

    /**
     * Creates basic group
     *
     * @param composite Composite
     */
    private void createBasicGroup(Composite composite) {
        final Composite basicComposite = new Composite(composite, SWT.NONE);
        final GridData gdIdComposite = new GridData(GridData.FILL_HORIZONTAL);
        basicComposite.setLayoutData(gdIdComposite);
        basicComposite.setLayout(new GridLayout(2, false));

        final Label queryExecPlanLabel = new Label(basicComposite, SWT.NONE);
        final GridData gdQueryExecPlanLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        queryExecPlanLabel.setLayoutData(gdQueryExecPlanLabel);
        queryExecPlanLabel.setText(Messages.msgQryIdLbl);

        idText = new Text(basicComposite, SWT.BORDER);
        idText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
        final GridData gdIdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdIdText.widthHint = 80;
        idText.setLayoutData(gdIdText);

        if (withUser) {
            final Label userLbl = new Label(basicComposite, SWT.NONE);
            final GridData gdUserLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
            userLbl.setLayoutData(gdUserLbl);
            userLbl.setText(Messages.msgQryUserNameLbl);

            userText = new Text(basicComposite, SWT.BORDER);
            final GridData gdUserTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
            userText.setLayoutData(gdUserTxt);

            final Label pwdLbl = new Label(basicComposite, SWT.NONE);
            final GridData gdPwdLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
            pwdLbl.setLayoutData(gdPwdLbl);
            pwdLbl.setText(Messages.msgQryUserPwdLbl);

            pwdTxt = new Text(basicComposite, SWT.LEFT | SWT.PASSWORD | SWT.BORDER);
            final GridData gdPwdTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
            pwdTxt.setLayoutData(gdPwdTxt);
            // If CMS support change password
            if (CompatibleUtil.isAfter920(database.getServer().getServerInfo())
                    && operation == AddEditType.EDIT) {
                changePassButton = new Button(basicComposite, SWT.CHECK);
                changePassButton.setText(Messages.btnChangePassword);
                changePassButton.setLayoutData(
                        CommonUITool.createGridData(
                                GridData.HORIZONTAL_ALIGN_BEGINNING, 2, 1, -1, -1));
                changePassButton.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent e) {
                                if (changePassButton.getSelection()) {
                                    pwdTxt.setEnabled(true);
                                    pwdTxt.setText("");
                                } else {
                                    pwdTxt.setEnabled(false);
                                    if (!isUserNameChanged()) {
                                        pwdTxt.setText(PWDASTERISK);
                                    }
                                }
                            }
                        });
                if (!isEditable) {
                    changePassButton.setEnabled(false);
                }
            }
        }

        // sets the initial value
        if (operation == AddEditType.EDIT) {
            idText.setText(queryPlanInfo.getQuery_id());
            idText.setEditable(false);
            if (userText != null) {
                String userName = queryPlanInfo.getUserName();
                userText.setText(userName);
                isEnabledOkButton[6] = "".equals(userName) ? false : true;
                pwdTxt.setText(PWDASTERISK);
                pwdTxt.setEnabled(false);
            }
        } else {
            idText.setEditable(true);
            if (userText == null) {
                isEnabledOkButton[6] = true;
            } else {
                isEnabledOkButton[6] = false;
                pwdTxt.setEnabled(true);
            }
        }
        idText.addModifyListener(new IdTextModifyListener());
        if (userText != null) {
            userText.addModifyListener(new UserTextModifyListener());
        }
    }

    /**
     * Creates statement group
     *
     * @param composite Composite
     */
    private void createStatementGroup(Composite composite) {
        final Group statementGroup = new Group(composite, SWT.NONE);
        statementGroup.setText(Messages.msgQryStateLbl);
        final GridData gdStatementGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
        statementGroup.setLayoutData(gdStatementGroup);
        statementGroup.setLayout(new GridLayout());

        statementText = new Text(statementGroup, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
        final GridData gdText = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdText.widthHint = 470;
        gdText.heightHint = 100;
        statementText.setLayoutData(gdText);
        // sets the initial value
        if (operation == AddEditType.EDIT) {
            String sql = queryPlanInfo.getQuery_string();
            SqlFormattingStrategy formator = new SqlFormattingStrategy();
            sql = formator.format(sql);
            statementText.setText(sql);
        }
        statementText.addModifyListener(new StatementModifyListener());
    }

    /**
     * When button press,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == CHECK_ID) {
            String query = statementText.getText().trim();
            String queryError = checkQuery(query);
            if (queryError == null) {
                CommonUITool.openInformationBox(Messages.btnCheckQuery, Messages.msgQueryCorrect);
            } else {
                if (queryError.indexOf("null\r\n") == 0) {
                    queryError = queryError.replaceAll("null\r\n", "");
                }
                CommonUITool.openErrorBox(queryError);
            }
            return;
        } else if (buttonId == IDialogConstants.OK_ID) {
            // Updates the fields of backupPlanInfo
            queryPlanInfo.setDbname(database.getName());
            String newQueryid = idText.getText().trim();
            queryPlanInfo.setQuery_id(newQueryid);
            if (userText != null) {
                String newUserName = userText.getText();
                queryPlanInfo.setUserName(newUserName);
                String newUserPwd = pwdTxt.getText();
                // If CMS support change password
                if (CompatibleUtil.isAfter920(database.getServer().getServerInfo())) {
                    if (changePassButton != null
                            && !changePassButton.getSelection()
                            && !isUserNameChanged()
                            && PWDASTERISK.equals(newUserPwd)) {
                        newUserPwd = "unknown"; // $NON-NLS-1$
                    }
                } else {
                    if (PWDASTERISK.equals(newUserPwd)) {
                        newUserPwd = "unknown"; // $NON-NLS-1$
                    } else if ("".equals(newUserPwd)) {
                        newUserPwd = "none"; // $NON-NLS-1$
                    }
                }

                queryPlanInfo.setUserPwd(newUserPwd);
            }
            String newPeriodType = periodGroup.getTextOfTypeCombo();
            queryPlanInfo.setPeriod(newPeriodType);
            queryPlanInfo.setPeriodic(periodGroup.isPeriodicEnabled());
            if (!periodGroup.isPeriodicEnabled()) {
                queryPlanInfo.setTime(periodGroup.getTime());
            } else {
                queryPlanInfo.setInterval(periodGroup.getInterval());
            }

            String detail = periodGroup.getDetailValue();
            queryPlanInfo.setDetail(detail);
            String query = statementText.getText().trim();
            query = SqlParser.convertSql(query);
            query = query.endsWith(";") ? query : query + ";";
            queryPlanInfo.setQuery_string(query);
            super.okPressed();
            return;
        }
        super.buttonPressed(buttonId);
    }

    /**
     * Judge the user name is changed
     *
     * @return
     */
    private boolean isUserNameChanged() {
        if (userText == null) {
            return false;
        }
        String newUserName = userText.getText();
        return (!StringUtil.isEqual(queryPlanInfo.getUserName(), newUserName));
    }

    /** A class that response the change of idText */
    private class IdTextModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String id = idText.getText().trim();
            if (id.length() <= 0) {
                isEnabledOkButton[0] = false;
            } else {
                if (ValidateUtil.isValidDBName(id)) {
                    if (childrenLabel.contains(id)) {
                        isEnabledOkButton[4] = false;
                    } else if (id.length() > Integer.valueOf(Messages.queryplanIdMaxLen)) {
                        isEnabledOkButton[5] = false;
                    } else {
                        isEnabledOkButton[0] = true;
                        isEnabledOkButton[3] = true;
                        isEnabledOkButton[4] = true;
                        isEnabledOkButton[5] = true;
                    }
                } else {
                    isEnabledOkButton[3] = false;
                }
            }
            enableOk();
        }
    }

    /** A class that response the change of userText */
    private class UserTextModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String userName = userText.getText();
            if (operation == AddEditType.EDIT && !isUserNameChanged()) {
                pwdTxt.setEnabled(false);
                pwdTxt.setText(PWDASTERISK);
                if (changePassButton != null) {
                    changePassButton.setSelection(false);
                }
            } else {
                pwdTxt.setEnabled(true);
                String pwdString = pwdTxt.getText();
                if (PWDASTERISK.equals(pwdString)) {
                    pwdTxt.setText("");
                }
            }
            if ("".equals(userName)) {
                isEnabledOkButton[6] = false;
            } else {
                isEnabledOkButton[6] = true;
            }
            enableOk();
        }
    }

    /** A class that response the change of statementText */
    public class StatementModifyListener implements ModifyListener {

        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String query = statementText.getText().trim();
            if (query.length() > 0) {
                isEnabledOkButton[1] = true;
            } else {
                isEnabledOkButton[1] = false;
            }
            enableOk();
        }
    }

    /**
     * Sets the queryPlanInfo and selection which is a folder
     *
     * @param selection the selection to set
     */
    public void initPara(DefaultSchemaNode selection) {
        childrenLabel = new ArrayList<String>();
        ICubridNode[] childrenNode = null;
        QueryPlanInfo qpi = null;
        if (operation == AddEditType.EDIT) {
            qpi = (QueryPlanInfo) selection.getAdapter(QueryPlanInfo.class);
            childrenNode = selection.getParent().getChildren(new NullProgressMonitor());
        } else {
            qpi = new QueryPlanInfo();
            childrenNode = selection.getChildren(new NullProgressMonitor());
        }
        queryPlanInfo = new QueryPlanInfoHelp();
        queryPlanInfo.setQueryPlanInfo(qpi);
        database = selection.getDatabase();
        for (ICubridNode childNode : childrenNode) {
            childrenLabel.add(childNode.getLabel());
        }
        withUser = CompatibleUtil.isSupportQueryPlanWithUser(database.getDatabaseInfo());
    }

    /** @param operation the operation to set */
    public void setOperation(AddEditType operation) {
        this.operation = operation;
    }

    /** @param childrenLabel the childrenLabel to set */
    public void setChildrenLabel(List<String> childrenLabel) {
        this.childrenLabel = childrenLabel;
    }

    /** @param database the database to set */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /** Enable the "OK" button */
    private void enableOk() {
        boolean is = true;
        for (int i = 0; i < isEnabledOkButton.length; i++) {
            is = is && isEnabledOkButton[i];
        }
        if (is) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        if (!isEnabledOkButton[0]) {
            setErrorMessage(Messages.errQueryplanIdEmpty);
            return;
        }
        if (!isEnabledOkButton[6]) {
            setErrorMessage(Messages.errQueryPlanUser);
            return;
        }
        if (!isEnabledOkButton[3]) {
            setErrorMessage(Messages.errIdTextMsg);
            return;
        }
        if (!isEnabledOkButton[5]) {
            setErrorMessage(Messages.errQueryplanIdLen);
            return;
        }
        if (!isEnabledOkButton[4]) {
            setErrorMessage(Messages.errQueryPlanIdRepeatMsg);
            return;
        }
        if (!isEnabledOkButton[2]) {
            periodGroup.enableOk();
            return;
        }
        if (!isEnabledOkButton[1]) {
            setErrorMessage(Messages.errQueryplanStmtEmpty);
            return;
        }

        setErrorMessage(null);
    }

    /**
     * Gets the instance of QueryPlanInfoHelp
     *
     * @return queryPlanInfo
     */
    public QueryPlanInfoHelp getQueryPlanInfo() {
        return queryPlanInfo;
    }

    /**
     * Observer the change of instance of the type Period
     *
     * @param ob the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code> method.
     */
    public void update(Observable ob, Object arg) {
        boolean isAllow = (Boolean) arg;
        isEnabledOkButton[2] = isAllow;
        enableOk();
    }

    /**
     * check the query
     *
     * @param queries String
     * @return error message String
     */
    private String checkQuery(String queries) {
        if (queries.trim().length() == 0) {
            return Messages.errQueryplanStmtEmpty;
        }
        Vector<String> qVector = QueryUtil.queriesToQuery(queries);
        CUBRIDStatementProxy statement = null;
        Connection queryConn = null;

        try {
            queryConn = getConnection();
            statement = (CUBRIDStatementProxy) queryConn.createStatement();

            int len = qVector.size();
            for (int i = 0; i < len; i++) {
                String sql = qVector.get(i);
                statement.getQueryplan(sql);
            }
        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            QueryUtil.freeQuery(queryConn, statement);
        }
        return null;
    }

    private Connection getConnection() throws SQLException {
        // Can Check UserInfo
        if (canCheckConnection()) {
            String dbName = database.getDatabaseInfo().getDbName();
            ServerInfo serverInfo = database.getDatabaseInfo().getServerInfo();
            String brokerIP = database.getDatabaseInfo().getBrokerIP();
            String brokerPort = database.getDatabaseInfo().getBrokerPort();
            String charset = database.getDatabaseInfo().getCharSet();
            String driverVersion = serverInfo.getJdbcDriverVersion();
            String jdbcAttrs = database.getDatabaseInfo().getJdbcAttrs();
            boolean isShard = database.getDatabaseInfo().isShard();

            String userName = userText.getText();
            String password = pwdTxt.getText();

            return JDBCConnectionManager.getConnection(
                    brokerIP,
                    brokerPort,
                    dbName,
                    userName,
                    password,
                    charset,
                    jdbcAttrs,
                    driverVersion,
                    false,
                    isShard);
        } else {
            return JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
        }
    }

    private boolean canCheckConnection() {
        if (CompatibleUtil.isAfter920(database.getServer().getServerInfo())) {
            if (AddEditType.ADD.equals(this.operation)) {
                return true;
            } else if (changePassButton.getSelection() || isUserNameChanged()) {
                return true;
            }
        }

        return false;
    }
}
