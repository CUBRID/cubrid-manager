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
package com.cubrid.cubridmanager.ui.common.control;

import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.task.SetCMConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.preference.PreferencePage;
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
import org.eclipse.swt.widgets.Text;

/**
 * CUBRID Manager property page show cm.conf configuraiton parameter
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-4 created by pangqiren
 */
public class ManagerServerPropertyPage extends PreferencePage implements ModifyListener {

    private final ICubridNode node;
    private boolean isAdmin = false;
    private Text cmPortText;
    private Text monitorIntervalText;
    private Button allowUserMultiConButton;
    private Button executeDiagButton;
    private Text queryTimeText;
    private final Map<String, String> initialValueMap = new HashMap<String, String>();
    private final Map<String, String> defaultValueMap = new HashMap<String, String>();
    private Button autoStartBrokerButton;
    private boolean isChanged = false;
    private boolean isApply = false;

    /**
     * The constructor
     *
     * @param node
     * @param name
     */
    public ManagerServerPropertyPage(ICubridNode node, String name) {
        super(name, null);
        noDefaultAndApplyButton();
        this.node = node;
        if (this.node != null
                && this.node.getServer().getServerInfo().getLoginedUserInfo().isAdmin()) {
            isAdmin = true;
        }
    }

    /**
     * Creates the page content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createGeneralGroup(composite);
        createDiagnosticsGroup(composite);

        initial();
        return composite;
    }

    /**
     * Create the general group composite
     *
     * @param parent the parent composite
     */
    private void createGeneralGroup(Composite parent) {
        Group generalInfoGroup = new Group(parent, SWT.NONE);
        generalInfoGroup.setText(Messages.grpGeneral);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        generalInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        generalInfoGroup.setLayout(layout);

        Label cmPortLabel = new Label(generalInfoGroup, SWT.LEFT);
        cmPortLabel.setText(ConfConstants.CM_PORT + ":");
        cmPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        cmPortText = new Text(generalInfoGroup, SWT.LEFT | SWT.BORDER);
        cmPortText.setTextLimit(5);
        cmPortText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        if (!isAdmin) {
            cmPortText.setEditable(false);
        }

        Label monitorIntervalLabel = new Label(generalInfoGroup, SWT.LEFT);
        monitorIntervalLabel.setText(ConfConstants.MONITOR_INTERVAL + ":");
        monitorIntervalLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        monitorIntervalText = new Text(generalInfoGroup, SWT.LEFT | SWT.BORDER);
        monitorIntervalText.setTextLimit(8);
        monitorIntervalText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        if (!isAdmin) {
            monitorIntervalText.setEditable(false);
        }

        allowUserMultiConButton = new Button(generalInfoGroup, SWT.CHECK | SWT.LEFT);
        allowUserMultiConButton.setText(ConfConstants.ALLOW_USER_MULTI_CONNECTION);
        allowUserMultiConButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        if (!isAdmin) {
            allowUserMultiConButton.setEnabled(false);
        }

        autoStartBrokerButton = new Button(generalInfoGroup, SWT.CHECK | SWT.LEFT);
        autoStartBrokerButton.setText(ConfConstants.AUTO_START_BROKER);
        autoStartBrokerButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        if (!isAdmin) {
            autoStartBrokerButton.setEnabled(false);
        }
    }

    /**
     * Create diagnostics group composite
     *
     * @param parent the parent composite
     */
    private void createDiagnosticsGroup(Composite parent) {
        Group diagInfoGroup = new Group(parent, SWT.NONE);
        diagInfoGroup.setText(Messages.grpDiagnositics);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        diagInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        diagInfoGroup.setLayout(layout);

        executeDiagButton = new Button(diagInfoGroup, SWT.CHECK | SWT.LEFT);
        executeDiagButton.setText(ConfConstants.EXECUTE_DIAG);
        executeDiagButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        if (!isAdmin) {
            executeDiagButton.setEnabled(false);
        }

        Label queryTimeLabel = new Label(diagInfoGroup, SWT.LEFT);
        queryTimeLabel.setText(ConfConstants.SERVER_LONG_QUERY_TIME + ":");
        queryTimeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        queryTimeText = new Text(diagInfoGroup, SWT.LEFT | SWT.BORDER);
        queryTimeText.setTextLimit(8);
        queryTimeText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        if (!isAdmin) {
            queryTimeText.setEditable(false);
        }
    }

    /** initial the page content */
    private void initial() {
        Map<String, String> confParaMap = node.getServer().getServerInfo().getCmConfParaMap();
        if (confParaMap != null) {
            Iterator<Map.Entry<String, String>> it = confParaMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                initialValueMap.put(key, value);
            }
        }
        String cmParameters[][] = ConfConstants.getCmParameters();
        for (int i = 0; i < cmParameters.length; i++) {
            defaultValueMap.put(cmParameters[i][0], cmParameters[i][2]);
        }
        defaultValue();
        cmPortText.addModifyListener(this);
        monitorIntervalText.addModifyListener(this);
        queryTimeText.addModifyListener(this);
    }

    /** Restore the default value */
    private void defaultValue() {
        if (initialValueMap != null) {
            cmPortText.setText(defaultValueMap.get(ConfConstants.CM_PORT));
            String cmPort = initialValueMap.get(ConfConstants.CM_PORT);
            if (cmPort != null) {
                cmPortText.setText(cmPort);
            }

            monitorIntervalText.setText(defaultValueMap.get(ConfConstants.MONITOR_INTERVAL));
            String monitorInterval = initialValueMap.get(ConfConstants.MONITOR_INTERVAL);
            if (monitorInterval != null) {
                monitorIntervalText.setText(monitorInterval);
            }

            allowUserMultiConButton.setSelection(
                    defaultValueMap.get(ConfConstants.ALLOW_USER_MULTI_CONNECTION).equals("YES"));
            String allowMultiConn = initialValueMap.get(ConfConstants.ALLOW_USER_MULTI_CONNECTION);
            if (allowMultiConn != null) {
                allowUserMultiConButton.setSelection("YES".equals(allowMultiConn));
            }

            autoStartBrokerButton.setSelection(
                    "YES".equals(defaultValueMap.get(ConfConstants.AUTO_START_BROKER)));
            String autoStartBroker = initialValueMap.get(ConfConstants.AUTO_START_BROKER);
            if (autoStartBroker != null) {
                autoStartBrokerButton.setSelection("YES".equals(autoStartBroker));
            }

            executeDiagButton.setSelection(
                    defaultValueMap.get(ConfConstants.EXECUTE_DIAG).equals("ON"));
            String executeDiag = initialValueMap.get(ConfConstants.EXECUTE_DIAG);
            if (executeDiag != null) {
                executeDiagButton.setSelection("ON".equals(executeDiag));
            }

            queryTimeText.setText(defaultValueMap.get(ConfConstants.SERVER_LONG_QUERY_TIME));
            String serverLongQueryTime = initialValueMap.get(ConfConstants.SERVER_LONG_QUERY_TIME);
            if (serverLongQueryTime != null) {
                queryTimeText.setText(serverLongQueryTime);
            }
        }
    }

    /**
     * When modify,check the validation
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        String port = cmPortText.getText();
        boolean isValidPort = ValidateUtil.isInteger(port);
        if (isValidPort) {
            int intValue = Integer.parseInt(port);
            if (intValue > 65535 || intValue < 1024) {
                isValidPort = false;
            }
        }
        if (!isValidPort) {
            setErrorMessage(Messages.errCmPort);
            setValid(false);
            return;
        }
        String monitorInterval = monitorIntervalText.getText();
        boolean isValidMonitorInterval = ValidateUtil.isInteger(monitorInterval);
        if (isValidMonitorInterval) {
            int intValue = Integer.parseInt(monitorInterval);
            if (intValue < 1) {
                isValidMonitorInterval = false;
            }
        }
        if (!isValidMonitorInterval) {
            setErrorMessage(Messages.errMonitorInterval);
            setValid(false);
            return;
        }
        String queryTime = queryTimeText.getText();
        boolean isValidQueryTime = ValidateUtil.isInteger(queryTime);
        if (isValidQueryTime) {
            int intValue = Integer.parseInt(queryTime);
            if (intValue < 0) {
                isValidQueryTime = false;
            }
        }
        if (!isValidQueryTime) {
            setErrorMessage(Messages.errServerLongQueryTime);
            setValid(false);
            return;
        }
        boolean isValid = isValidPort && isValidMonitorInterval && isValidQueryTime;
        if (isValid) {
            setErrorMessage(null);
        }
        setValid(isValid);
    }

    /** Restore default values */
    protected void performDefaults() {
        defaultValue();
        if (isApply) {
            perform(initialValueMap);
        }
        isChanged = false;
        isApply = false;
    }

    /**
     * Execute and save the content
     *
     * @return <code>true</code> if it is successful;<code>false</code> otherwise
     */
    public boolean performOk() {
        if (cmPortText == null || cmPortText.isDisposed()) {
            return true;
        }
        if (!isAdmin) {
            return true;
        }
        Map<String, String> confParaMap = node.getServer().getServerInfo().getCmConfParaMap();
        String port = cmPortText.getText();
        if (isChanged(ConfConstants.CM_PORT, port)) {
            confParaMap.put(ConfConstants.CM_PORT, port);
            isChanged = true;
        }
        String monitorInterval = monitorIntervalText.getText();
        if (isChanged(ConfConstants.MONITOR_INTERVAL, monitorInterval)) {
            confParaMap.put(ConfConstants.MONITOR_INTERVAL, monitorInterval);
            isChanged = true;
        }
        boolean isAllowUserMultiConn = allowUserMultiConButton.getSelection();
        if (isChanged(
                ConfConstants.ALLOW_USER_MULTI_CONNECTION, isAllowUserMultiConn ? "YES" : "NO")) {
            confParaMap.put(
                    ConfConstants.ALLOW_USER_MULTI_CONNECTION, isAllowUserMultiConn ? "YES" : "NO");
            isChanged = true;
        }
        boolean isAutoStartBroker = autoStartBrokerButton.getSelection();
        if (isChanged(ConfConstants.AUTO_START_BROKER, isAutoStartBroker ? "YES" : "NO")) {
            confParaMap.put(ConfConstants.AUTO_START_BROKER, isAutoStartBroker ? "YES" : "NO");
            isChanged = true;
        }
        boolean isExecuteDialg = executeDiagButton.getSelection();
        if (isChanged(ConfConstants.EXECUTE_DIAG, isExecuteDialg ? "ON" : "OFF")) {
            confParaMap.put(ConfConstants.EXECUTE_DIAG, isExecuteDialg ? "ON" : "OFF");
            isChanged = true;
        }
        String queryTime = queryTimeText.getText();
        if (isChanged(ConfConstants.SERVER_LONG_QUERY_TIME, queryTime)) {
            confParaMap.put(ConfConstants.SERVER_LONG_QUERY_TIME, queryTime);
            isChanged = true;
        }
        if (!isChanged) {
            return true;
        }
        perform(confParaMap);
        isChanged = false;
        isApply = true;
        return true;
    }

    /**
     * Perform task and set parameter to cm.conf
     *
     * @param confParaMap the conf parameters map
     */
    private void perform(Map<String, String> confParaMap) {
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setCMParameterTaskName);
        SetCMConfParameterTask task = new SetCMConfParameterTask(node.getServer().getServerInfo());
        task.setConfParameters(confParaMap);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(Messages.titleSuccess, Messages.msgChangeCMParaSuccess);
        }
    }

    /**
     * Test whether has modify
     *
     * @param paraName the parameter name
     * @param uiValue the value
     * @return <code>true</code> if it changed;<code>false</code>otherwise
     */
    private boolean isChanged(String paraName, String uiValue) {
        String initialValue = initialValueMap.get(paraName);
        String defaultValue = defaultValueMap.get(paraName);
        if (initialValue == null && !uiValue.equals(defaultValue)) {
            return true;
        }
        if (initialValue != null && !uiValue.equals(initialValue)) {
            return true;
        }
        return false;
    }
}
