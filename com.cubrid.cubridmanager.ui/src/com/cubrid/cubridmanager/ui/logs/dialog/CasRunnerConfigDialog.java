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
package com.cubrid.cubridmanager.ui.logs.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.ui.logs.Messages;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog is used to Configure exec config.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-18 created by wuyingshi
 */
public class CasRunnerConfigDialog extends CMTitleAreaDialog implements ModifyListener {

    private Group group = null;
    private Combo comboBrokerName = null;
    private Text textUserName = null;
    private Text textPassword = null;
    private Spinner spinnerNumThread = null;
    private Spinner spinnerRepeatCount = null;
    private static String brokerName = "";
    private static String userName = "";
    private static String password = "";
    private static String numThread = "";
    private static String numRepeatCount = "";
    private static String dbname = "";
    private boolean showqueryresult = false;
    private boolean showqueryplan = false;
    private Button checkBoxShowQueryResult = null;
    private Combo comboDBName = null;
    private String logstring = "";

    private boolean execwithFile = false;
    private Button checkBoxShowQueryPlan = null;

    private Composite composite;
    private CubridDatabase database = null;
    private List<String> allDatabaseList = null;
    private BrokerInfos brokerInfos = null;
    private LogInfo logInfo = null;
    private String targetBroker = "";

    /**
     * The constructor
     *
     * @param parentShell
     */
    public CasRunnerConfigDialog(Shell parentShell) {
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

        composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 3;
        composite.setLayout(layout);

        createGroup();
        setTitle(Messages.titleCasRunnerConfigDialog);
        setMessage(Messages.msgCasRunnerConfigDialog);
        init();
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleCasRunnerConfigDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.buttonOk, true);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.buttonCancel, false);
        verify();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!verify()) {
                return;
            }
            setBrokerName(comboBrokerName.getText());
            setUserName(textUserName.getText());
            setPassword(textPassword.getText());
            setNumThread(Integer.toString(spinnerNumThread.getSelection()));
            setNumRepeatCount(Integer.toString(spinnerRepeatCount.getSelection()));
            setShowqueryresult(checkBoxShowQueryResult.getSelection());
            setShowqueryplan(checkBoxShowQueryPlan.getSelection());
            setDbname(comboDBName.getText());
        }
        super.buttonPressed(buttonId);
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
        if (comboDBName.getText() == null || "".equals(comboDBName.getText())) {
            return Messages.msgValidInputDbName;
        }
        if (comboBrokerName.getText() == null || "".equals(comboBrokerName.getText())) {
            return Messages.msgValidInputBrokerName;
        }
        if (textUserName.getText() == null || "".equals(textUserName.getText())) {
            return Messages.msgValidInputUserID;
        }
        return null;
    }

    /** This method initializes group */
    private void createGroup() {
        GridData gridData17 = new GridData(GridData.FILL_HORIZONTAL);
        gridData17.horizontalSpan = 2;
        GridData gridData6 = new GridData(GridData.FILL_HORIZONTAL);
        gridData6.horizontalSpan = 2;

        GridData gridData13 = new GridData(GridData.FILL_HORIZONTAL);
        gridData13.horizontalSpan = 3;
        GridData gridData12 = new GridData(GridData.FILL_HORIZONTAL);
        gridData12.horizontalAlignment = GridData.FILL;
        GridData gridData11 = new GridData(GridData.FILL_HORIZONTAL);
        gridData11.horizontalAlignment = GridData.FILL;
        GridData gridData10 = new GridData(GridData.FILL_HORIZONTAL);
        gridData10.horizontalAlignment = GridData.FILL;
        gridData10.heightHint = 15;
        GridData gridData9 = new GridData(GridData.FILL_HORIZONTAL);
        gridData9.horizontalAlignment = GridData.FILL;
        gridData9.heightHint = 15;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        group = new Group(composite, SWT.FILL);
        Label labelDBName = new Label(group, SWT.NONE);
        createComboDBName();
        group.setLayoutData(gridData13);
        group.setLayout(gridLayout);
        Label labelBrokerName = new Label(group, SWT.NONE);
        labelBrokerName.setText(Messages.labelBrokerName);
        createComboBrokerName();
        Label labelUserName = new Label(group, SWT.NONE);
        labelUserName.setText(Messages.labelUserId);
        textUserName = new Text(group, SWT.BORDER);
        textUserName.setLayoutData(gridData9);
        Label labelPassword = new Label(group, SWT.NONE);
        labelPassword.setText(Messages.labelPassword);
        textPassword = new Text(group, SWT.BORDER | SWT.PASSWORD);
        textPassword.setLayoutData(gridData10);
        Label labelNumThread = new Label(group, SWT.NONE);
        labelNumThread.setText(Messages.labelNumThread);
        spinnerNumThread = new Spinner(group, SWT.BORDER);
        spinnerNumThread.setMaximum(20);
        spinnerNumThread.setLayoutData(gridData11);
        spinnerNumThread.setMinimum(1);
        Label labelRepeatCount = new Label(group, SWT.NONE);
        labelRepeatCount.setText(Messages.labelNumRepeatCount);
        spinnerRepeatCount = new Spinner(group, SWT.BORDER);
        spinnerRepeatCount.setMaximum(1000);
        spinnerRepeatCount.setMinimum(1);
        spinnerRepeatCount.setLayoutData(gridData12);
        spinnerRepeatCount.setDigits(0);
        checkBoxShowQueryResult = new Button(group, SWT.CHECK);
        checkBoxShowQueryResult.setText(Messages.labelViewCasRunnerQueryResult);
        checkBoxShowQueryResult.setLayoutData(gridData6);
        checkBoxShowQueryResult.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        checkBoxShowQueryPlan.setEnabled(checkBoxShowQueryResult.getSelection());
                    }
                });
        checkBoxShowQueryPlan = new Button(group, SWT.CHECK);
        checkBoxShowQueryPlan.setEnabled(false);
        checkBoxShowQueryPlan.setLayoutData(gridData17);
        checkBoxShowQueryPlan.setText(Messages.labelViewCasRunnerQueryPlan);
        labelDBName.setText(Messages.labelDatabase);
    }

    /** This method initializes comboBrokerName */
    private void createComboBrokerName() {
        GridData gridData8 = new GridData(GridData.FILL_BOTH);
        gridData8.horizontalAlignment = GridData.FILL;
        comboBrokerName = new Combo(group, SWT.NONE);
        comboBrokerName.setLayoutData(gridData8);
    }

    /** This method initializes comboDBName */
    private void createComboDBName() {
        GridData gridData7 = new GridData(GridData.FILL_BOTH);
        gridData7.horizontalAlignment = GridData.FILL;
        comboDBName = new Combo(group, SWT.NONE);
        comboDBName.setLayoutData(gridData7);
    }

    /**
     * get the database.
     *
     * @return database
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * set the database.
     *
     * @param database CubridDatabase
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /** Initials some values. */
    private void init() {

        if (execwithFile) {
            checkBoxShowQueryResult.setSelection(false);
            checkBoxShowQueryResult.setEnabled(false);
        }

        for (int i = 0, n = allDatabaseList.size(); i < n; i++) {
            String dbname = (String) allDatabaseList.get(i);
            comboDBName.add(dbname);
        }

        if ((getDbname().length() == 0) && (getUserName().length() == 0)) {
            if (logstring.length() > 0) {
                initWithLogString();
            }
        } else {
            comboDBName.setText(getDbname());
            textUserName.setText(getUserName());
        }

        int numbroker = brokerInfos.getBorkerInfoList().getBrokerInfoList().size();

        for (int i = 0; i < numbroker; i++) {
            BrokerInfo item =
                    (BrokerInfo) brokerInfos.getBorkerInfoList().getBrokerInfoList().get(i);
            comboBrokerName.add(item.getName());
        }
        if (getBrokerName().length() == 0) {
            comboBrokerName.setText(targetBroker);
        } else {
            comboBrokerName.setText(getBrokerName());
        }

        if (getPassword().length() != 0) {
            textPassword.setText(getPassword());
        }
        if (getNumThread().length() != 0) {
            try {
                spinnerNumThread.setSelection(Integer.parseInt(getNumThread()));
            } catch (NumberFormatException ee) {
                spinnerNumThread.setSelection(1);
            }
        }
        {
            try {
                spinnerRepeatCount.setSelection(Integer.parseInt(getNumRepeatCount()));
            } catch (NumberFormatException ee) {
                spinnerRepeatCount.setSelection(1);
            }

            if (!execwithFile) {
                checkBoxShowQueryResult.setEnabled(true);
                checkBoxShowQueryResult.setSelection(isShowqueryresult());
                checkBoxShowQueryPlan.setEnabled(isShowqueryresult());
                checkBoxShowQueryPlan.setSelection(isShowqueryplan());
            }
        }
        comboDBName.addModifyListener(this);
        comboBrokerName.addModifyListener(this);
        textUserName.addModifyListener(this);
        textPassword.addModifyListener(this);
    }

    /** init dialog with log string info. */
    private void initWithLogString() {
        int dbnameindex = logstring.indexOf("connect");
        if (dbnameindex > 0) {
            dbnameindex += 8;
            int iDbuserStart, iDbuserEnd;
            iDbuserStart = logstring.indexOf(" ", dbnameindex);
            iDbuserEnd = logstring.indexOf("\n", iDbuserStart);
            if ((iDbuserStart > 0) && (iDbuserEnd > iDbuserStart)) {
                String dbname = logstring.substring(dbnameindex, iDbuserStart);
                String username = logstring.substring(iDbuserStart + 1, iDbuserEnd - 1);
                for (int i = 0, n = comboDBName.getItemCount(); i < n; i++) {
                    if (comboDBName.getItem(i).equals(dbname)) {
                        comboDBName.setText(dbname);
                        break;
                    }
                }

                textUserName.setText(username);
            }
        }
    }

    /**
     * get the allDatabaseList.
     *
     * @return allDatabaseList
     */
    public List<String> getAllDatabaseList() {
        return allDatabaseList;
    }

    /**
     * set the allDatabaseList.
     *
     * @param allDatabaseList List<String>
     */
    public void setAllDatabaseList(List<String> allDatabaseList) {
        this.allDatabaseList = allDatabaseList;
    }

    /**
     * get the brokerInfos.
     *
     * @return brokerInfos
     */
    public BrokerInfos getBrokerInfos() {
        return brokerInfos;
    }

    /**
     * set the brokerInfos.
     *
     * @param brokerInfos BrokerInfos
     */
    public void setBrokerInfos(BrokerInfos brokerInfos) {
        this.brokerInfos = brokerInfos;
    }

    /**
     * get the log information.
     *
     * @return logInfo
     */
    public LogInfo getLogInfo() {
        return logInfo;
    }

    /**
     * set the log information.
     *
     * @param logInfo LogInfo
     */
    public void setLogInfo(LogInfo logInfo) {
        this.logInfo = logInfo;
    }

    /**
     * get the target broker.
     *
     * @return targetBroker
     */
    public String getTargetBroker() {
        return targetBroker;
    }

    /**
     * set the target broker.
     *
     * @param targetBroker String
     */
    public void setTargetBroker(String targetBroker) {
        this.targetBroker = targetBroker;
    }

    /**
     * set the broker name.
     *
     * @param brokerName String
     */
    public static void setBrokerName(String brokerName) {
        CasRunnerConfigDialog.brokerName = brokerName;
    }

    /**
     * get the broker name
     *
     * @return brokerName
     */
    public static String getBrokerName() {
        return brokerName;
    }

    /**
     * set the user name.
     *
     * @param userName String
     */
    public static void setUserName(String userName) {
        CasRunnerConfigDialog.userName = userName;
    }

    /**
     * get the user name.
     *
     * @return userName
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * set the password.
     *
     * @param password String
     */
    public static void setPassword(String password) {
        CasRunnerConfigDialog.password = password;
    }

    /**
     * get the password.
     *
     * @return password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * set the numThread.
     *
     * @param numThread String
     */
    public static void setNumThread(String numThread) {
        CasRunnerConfigDialog.numThread = numThread;
    }

    /**
     * get the numThread.
     *
     * @return numThread
     */
    public static String getNumThread() {
        return numThread;
    }

    /**
     * set the numRepeatCount.
     *
     * @param numRepeatCount String
     */
    public static void setNumRepeatCount(String numRepeatCount) {
        CasRunnerConfigDialog.numRepeatCount = numRepeatCount;
    }

    /**
     * get the numRepeatCount.
     *
     * @return numRepeatCount
     */
    public String getNumRepeatCount() {
        return numRepeatCount;
    }

    /**
     * get the dbname.
     *
     * @param dbname String
     */
    public static void setDbname(String dbname) {
        CasRunnerConfigDialog.dbname = dbname;
    }

    /**
     * set the dbname.
     *
     * @return dbname
     */
    public static String getDbname() {
        return dbname;
    }

    /**
     * set the showqueryresult status.
     *
     * @param showqueryresult boolean
     */
    public void setShowqueryresult(boolean showqueryresult) {
        this.showqueryresult = showqueryresult;
    }

    /**
     * get the showqueryresult status.
     *
     * @return showqueryresult
     */
    public boolean isShowqueryresult() {
        return showqueryresult;
    }

    /**
     * set the showqueryplan status.
     *
     * @param showqueryplan boolean
     */
    public void setShowqueryplan(boolean showqueryplan) {
        this.showqueryplan = showqueryplan;
    }

    /**
     * get the showqueryplan status.
     *
     * @return showqueryplan
     */
    public boolean isShowqueryplan() {
        return showqueryplan;
    }

    /**
     * get the execwithFile.
     *
     * @return execwithFile
     */
    public boolean isExecwithFile() {
        return execwithFile;
    }

    /**
     * set the execwithFile.
     *
     * @param execwithFile boolean
     */
    public void setExecwithFile(boolean execwithFile) {
        this.execwithFile = execwithFile;
    }

    /**
     * get the logstring.
     *
     * @return logstring
     */
    public String getLogstring() {
        return logstring;
    }

    /**
     * set the logstring.
     *
     * @param logstring String
     */
    public void setLogstring(String logstring) {
        this.logstring = logstring;
    }
}
