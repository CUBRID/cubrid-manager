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
package com.cubrid.cubridmanager.ui.broker.control;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.broker.task.SetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.utils.CubridBrokerUtils;
import com.cubrid.cubridmanager.ui.broker.Messages;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSetting;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * A property page which is responsible for editing or just showing a certain broker property
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-27 created by lizhiqiang
 */
public class BrokerParameterPropertyPage extends PreferencePage {

    private final ICubridNode node;
    private Button refreshBtn;
    private Text intervalTxt;
    private Table paraTable;
    private Map<String, String> brokerMap;
    private Map<String, Map<String, String>> confParaMap;
    private String[] columnNameArrs;
    private TableViewer paraTableViewer;
    private String brokerName;
    private BrokerIntervalSetting setting;

    private final ServerUserInfo userInfo;
    private final Map<String, Map<String, String>> oldConfParaMap;
    private final boolean isQueryOrTransTimeUseMs;
    private final ServerInfo serverInfo;

    /**
     * The constructor
     *
     * @param node the ICubridNode object
     * @param title the title
     */
    public BrokerParameterPropertyPage(ICubridNode node, String title) {
        super(title, null);
        noDefaultAndApplyButton();
        this.node = node;
        serverInfo = node.getServer().getServerInfo();
        isQueryOrTransTimeUseMs = CompatibleUtil.isQueryOrTransTimeUseMs(serverInfo);
        userInfo = serverInfo.getLoginedUserInfo();
        oldConfParaMap = node.getParent().getServer().getServerInfo().getBrokerConfParaMap();
    }

    /**
     * Create the page content
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createContents(Composite parent) {
        parent.setLayout(initGridLayout(new GridLayout(), true));
        TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        tabFolder.setLayout(new GridLayout());

        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.parameterNameOfTap);
        item.setControl(createParamComp(tabFolder));

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.refreshNameOfTap);
        item.setControl(createRefreshComp(tabFolder));
        initialize();

        return parent;
    }

    /**
     * Creates the parameter table
     *
     * @param parent the parent composite
     * @return the composite
     */
    private Control createParamComp(Composite parent) {

        columnNameArrs =
                new String[] {Messages.tblParameter, Messages.tblValueType, Messages.tblParamValue};
        paraTableViewer =
                CommonUITool.createCommonTableViewer(
                        parent,
                        null,
                        columnNameArrs,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));
        paraTable = paraTableViewer.getTable();

        return paraTable;
    }

    /**
     * Creates the refresh Composite
     *
     * @param parent the parent composite
     * @return the composite
     */
    private Control createRefreshComp(Composite parent) {
        Composite refreshComp = new Composite(parent, SWT.None);
        refreshComp.setLayout(new GridLayout());
        final GridData gdRefreshComp = new GridData(SWT.FILL, SWT.TOP, true, false);
        refreshComp.setLayoutData(gdRefreshComp);

        final Label tipLbl = new Label(refreshComp, SWT.NONE);
        final GridData gdTipLbl = new GridData(SWT.LEFT, SWT.TOP, true, false);
        tipLbl.setText(Messages.refreshTitle);
        tipLbl.setLayoutData(gdTipLbl);

        final Composite radioComp = new Composite(refreshComp, SWT.None);
        final GridData gdRadioComp = new GridData(SWT.FILL, SWT.TOP, true, false);
        radioComp.setLayoutData(gdRadioComp);
        radioComp.setLayout(new GridLayout(3, false));

        refreshBtn = new Button(radioComp, SWT.CHECK);
        refreshBtn.setText(Messages.refreshOnLbl);
        refreshBtn.setSelection(false);

        intervalTxt = new Text(radioComp, SWT.BORDER | SWT.RIGHT);
        final GridData gdIntervalTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        intervalTxt.setLayoutData(gdIntervalTxt);
        intervalTxt.setText("1");
        intervalTxt.setEnabled(false);

        final Label secLbl = new Label(radioComp, SWT.NONE);
        final GridData gdSecLbl = new GridData(SWT.LEFT, SWT.TOP, true, false);
        secLbl.setText(Messages.refreshUnitLbl);
        tipLbl.setLayoutData(gdSecLbl);

        refreshBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (refreshBtn.getSelection()) {
                            intervalTxt.setEnabled(true);
                        } else {
                            intervalTxt.setEnabled(false);
                        }
                    }
                });

        intervalTxt.addVerifyListener(
                new VerifyListener() {
                    public void verifyText(VerifyEvent event) {
                        if (!"".equals(event.text) && !ValidateUtil.isNumber(event.text)) {
                            event.doit = false;
                            return;
                        }
                    }
                });

        return refreshComp;
    }

    /** Initialize the value of all controls */
    private void initialize() {
        String serverName = node.getServer().getLabel();
        brokerName = node.getLabel().toLowerCase(); // if need
        // initialize table
        confParaMap = node.getParent().getServer().getServerInfo().getBrokerConfParaMap();
        brokerMap = confParaMap.get(brokerName);

        List<Map<String, String>> parameterList = getParameterList();
        paraTableViewer.setInput(parameterList);
        for (int k = 0; k < paraTable.getItemCount(); k++) {
            if (parameterList.get(k).get("bgColor").equalsIgnoreCase("grey")) {
                paraTable.getItem(k).setBackground(ResourceManager.getColor(200, 200, 200));
            }
        }
        for (int i = 0; i < paraTable.getColumnCount(); i++) {
            paraTable.getColumn(i).pack();
        }

        if (userInfo.getCasAuth() == CasAuthType.AUTH_ADMIN) {
            linkEditorForTable();
        }
        // initialize refresh
        BrokerIntervalSettingManager manager = BrokerIntervalSettingManager.getInstance();

        setting = manager.getBrokerIntervalSetting(serverName, brokerName);
        boolean state = setting.isOn();
        String interval = setting.getInterval();
        if (state) {
            refreshBtn.setSelection(true);
            intervalTxt.setEnabled(true);
            intervalTxt.setText(interval);
        } else {
            refreshBtn.setSelection(false);
            intervalTxt.setEnabled(false);
        }
    }

    /**
     * getParameterList
     *
     * @return Parameter List
     */
    private List<Map<String, String>> getParameterList() {
        List<Map<String, String>> parameterList = new ArrayList<Map<String, String>>();
        String brokerParameters[][] = ConfConstants.getBrokerParameters(serverInfo);
        for (int i = 2; i < brokerParameters.length; i++) {
            Map<String, String> dataMap = new HashMap<String, String>();
            String para = brokerParameters[i][0];
            String type = brokerParameters[i][1];
            String defaultValue = brokerParameters[i][2];
            String paramType = brokerParameters[i][3];
            String bgColor = "grey";
            if ("general".equals(paramType)) {
                continue;
            } else if ("common".equals(paramType)) {
                bgColor = "white";
            }
            if (para.equals(ConfConstants.APPL_SERVER_PORT)) {
                defaultValue = CubridBrokerUtils.getBrokerPort(serverInfo, dataMap);
                if (null == defaultValue) {
                    continue;
                }
            }
            if (brokerMap != null && brokerMap.get(para) != null) {
                defaultValue = brokerMap.get(para);
                bgColor = "white";
            }
            if (para.equals(ConfConstants.LONG_QUERY_TIME)
                    || para.equals(ConfConstants.LONG_TRANSACTION_TIME)) {
                if (isQueryOrTransTimeUseMs) {
                    type += "(msec)";
                    double dVal = Double.parseDouble(defaultValue) * 1000;
                    defaultValue = Integer.toString((int) (dVal + 0.5));
                } else {
                    type += "(sec)";
                }
            }

            dataMap.put("0", para);
            dataMap.put("1", type);
            dataMap.put("2", defaultValue);
            dataMap.put("bgColor", bgColor);
            parameterList.add(dataMap);
        }
        return parameterList;
    }

    /** Links the editable column of table */
    private void linkEditorForTable() {
        paraTableViewer.setColumnProperties(columnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = null;
        editors[1] = null;
        editors[2] = new TextCellEditor(paraTable);
        paraTableViewer.setCellEditors(editors);
        paraTableViewer.setCellModifier(
                new ICellModifier() {

                    public boolean canModify(Object element, String property) {
                        if (property.equals(columnNameArrs[2])) {
                            return true;
                        }
                        return false;
                    }

                    @SuppressWarnings("unchecked")
                    public Object getValue(Object element, String property) {
                        Map<String, String> map = (Map<String, String>) element;
                        if (property.equals(columnNameArrs[2])) {
                            return map.get("2");
                        }
                        return null;
                    }

                    @SuppressWarnings("unchecked")
                    public void modify(Object element, String property, Object value) {
                        Object obj = null;
                        if (element instanceof Item) {
                            obj = ((Item) element).getData();
                        }
                        if (obj == null) {
                            return;
                        }
                        Map<String, String> map = (Map<String, String>) obj;
                        String paramName = map.get("0");
                        String type = map.get("1");
                        boolean isValid = true;
                        if (type.indexOf("int") >= 0) {
                            boolean isInt = ValidateUtil.isInteger(value.toString());
                            if (!isInt) {
                                isValid = false;
                            }
                        } else if (type.startsWith("string")) {
                            String valueStr = value.toString().trim();
                            int start = type.indexOf("(");
                            int end = type.indexOf(")");
                            if (start > 0) {
                                String valueStrs = type.substring(start + 1, end);
                                String[] values = valueStrs.split("\\|");
                                boolean isExist = false;
                                for (String val : values) {
                                    if (valueStr.equals(val)) {
                                        isExist = true;
                                    }
                                }
                                if (!isExist) {
                                    isValid = false;
                                }
                            }
                        }
                        if (!isValid) {
                            CommonUITool.openErrorBox(
                                    Messages.bind(
                                            Messages.errParameterValue, new Object[] {paramName}));
                        }
                        if (type.indexOf("int") >= 0 && isValid) {
                            int intValue = Integer.parseInt(value.toString());
                            if (paramName.equalsIgnoreCase(ConfConstants.MAX_STRING_LENGTH)) {
                                if (intValue == 0 || intValue < -1) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errMaxStringLengthValue,
                                                    new Object[] {paramName}));
                                }
                            } else {
                                if (intValue <= 0) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errPositiveValue,
                                                    new Object[] {paramName}));
                                }
                                List<Map<String, String>> parameterList =
                                        (List<Map<String, String>>) paraTableViewer.getInput();
                                if (paramName.equalsIgnoreCase(ConfConstants.MIN_NUM_APPL_SERVER)) {
                                    Map<String, String> dataMap = parameterList.get(3);
                                    String maxNumApplServer = dataMap.get("2");
                                    if (maxNumApplServer.trim().length() > 0
                                            && intValue
                                                    > Integer.parseInt(maxNumApplServer.trim())) {
                                        isValid = false;
                                        CommonUITool.openErrorBox(
                                                Messages.bind(
                                                        Messages.errMinNumApplServerValue,
                                                        new Object[] {paramName}));
                                    }
                                }
                                if (paramName.equalsIgnoreCase(ConfConstants.MAX_NUM_APPL_SERVER)) {
                                    Map<String, String> dataMap = parameterList.get(2);
                                    String minNumApplServer = dataMap.get("2");
                                    if (minNumApplServer.trim().length() > 0
                                            && intValue
                                                    < Integer.parseInt(minNumApplServer.trim())) {
                                        isValid = false;
                                        CommonUITool.openErrorBox(
                                                Messages.bind(
                                                        Messages.errMaxNumApplServeValue,
                                                        new Object[] {paramName}));
                                    }
                                }
                            }
                        }
                        if (paramName.equalsIgnoreCase(ConfConstants.BROKER_PORT) && isValid) {
                            int port = Integer.parseInt(value.toString());
                            if (port < 1024 || port > 65535) {
                                isValid = false;
                                CommonUITool.openErrorBox(
                                        Messages.bind(Messages.errBrokerPortAndShmId, paramName));
                            }
                            if (isValid) {
                                String paramValue = value.toString().trim();
                                for (Map.Entry<String, Map<String, String>> entry :
                                        oldConfParaMap.entrySet()) {
                                    if (entry.getKey().equals(node.getLabel())) {
                                        isValid = true;
                                        continue;
                                    } else {
                                        String otherPort =
                                                entry.getValue().get(ConfConstants.BROKER_PORT);
                                        if (paramValue.equalsIgnoreCase(otherPort)) {
                                            isValid = false;
                                            CommonUITool.openErrorBox(Messages.errReduplicatePort);
                                            break;
                                        } else {
                                            isValid = true;
                                        }
                                    }
                                }
                                Map<String, String> brokerSectionMap =
                                        oldConfParaMap.get(ConfConstants.BROKER_SECTION_NAME);
                                String masterShmId =
                                        brokerSectionMap.get(ConfConstants.MASTER_SHM_ID);
                                if (paramValue.equals(masterShmId)) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(Messages.errUseMasterShmId, paramValue));
                                }
                            }
                            if (isValid) {
                                String paramValue = value.toString().trim();
                                int intServerPortValue = Integer.parseInt(paramValue) + 1;
                                List<Map<String, String>> parameterList =
                                        (List<Map<String, String>>) paraTableViewer.getInput();
                                for (Map<String, String> dataMap : parameterList) {
                                    if (dataMap.get("0")
                                            .equalsIgnoreCase(ConfConstants.APPL_SERVER_PORT)) {
                                        String serverPortValue =
                                                Integer.toString(intServerPortValue);
                                        dataMap.put("2", serverPortValue);
                                    }
                                }
                            }
                        }
                        if (paramName.equalsIgnoreCase(ConfConstants.APPL_SERVER_SHM_ID)
                                && isValid) {
                            int port = Integer.parseInt(value.toString());
                            if (port < 1024 || port > 65535) {
                                isValid = false;
                                CommonUITool.openErrorBox(
                                        Messages.bind(Messages.errBrokerPortAndShmId, paramName));
                            }
                            if (isValid) {
                                String paramValue = value.toString().trim();
                                for (Map.Entry<String, Map<String, String>> entry :
                                        oldConfParaMap.entrySet()) {
                                    if (entry.getKey().equals(node.getLabel())) {
                                        isValid = true;
                                        continue;
                                    } else {
                                        String otherPort =
                                                entry.getValue()
                                                        .get(ConfConstants.APPL_SERVER_SHM_ID);
                                        if (paramValue.equalsIgnoreCase(otherPort)) {
                                            isValid = false;
                                            CommonUITool.openErrorBox(Messages.errReduplicateShmId);
                                            break;
                                        } else {
                                            isValid = true;
                                        }
                                    }
                                }
                                Map<String, String> brokerSectionMap =
                                        oldConfParaMap.get(ConfConstants.BROKER_SECTION_NAME);
                                String masterShmId =
                                        brokerSectionMap.get(ConfConstants.MASTER_SHM_ID);
                                if (paramValue.equals(masterShmId)) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(Messages.errUseMasterShmId, paramValue));
                                }
                            }
                        }
                        if (isValid) {
                            map.put("2", value.toString());
                        }
                        paraTableViewer.refresh();
                    }
                });
    }

    /**
     * Save the page content
     *
     * @return <code>true</code> if it saved successfully;<code>false</code> otherwise
     */
    public boolean performOk() {
        // execute set parameter task
        String brokerParameters[][] = ConfConstants.getBrokerParameters(serverInfo);
        if (isTableChange()) {
            Map<String, String> paraMap = confParaMap.get(node.getLabel());
            // compare the default value, if equal default value and delete it from map
            for (String[] brokerPara : brokerParameters) {
                if (brokerPara[0].equals(ConfConstants.APPL_SERVER_PORT)) {
                    String serverPort = paraMap.get(ConfConstants.APPL_SERVER_PORT);
                    if (null != serverPort) {
                        int serverPortValue =
                                Integer.parseInt(paraMap.get(ConfConstants.BROKER_PORT)) + 1;
                        if (serverPort.equalsIgnoreCase(Integer.toString(serverPortValue))) {
                            paraMap.remove(ConfConstants.APPL_SERVER_PORT);
                        }
                    }
                } else if (brokerPara[0].equals(ConfConstants.LONG_QUERY_TIME)) {
                    if (isQueryOrTransTimeUseMs) {
                        int intValue = Integer.parseInt(brokerPara[2]) * 1000;
                        if (Integer.toString(intValue)
                                .equals(paraMap.get(ConfConstants.LONG_QUERY_TIME))) {
                            paraMap.remove(ConfConstants.LONG_QUERY_TIME);
                        } else {
                            String sVal = paraMap.get(ConfConstants.LONG_QUERY_TIME);
                            double dVal = Double.parseDouble(sVal) / 1000;
                            paraMap.put(ConfConstants.LONG_QUERY_TIME, String.valueOf(dVal));
                        }
                    } else {
                        if (brokerPara[2].equals(paraMap.get(ConfConstants.LONG_QUERY_TIME))) {
                            paraMap.remove(ConfConstants.LONG_QUERY_TIME);
                        }
                    }
                } else if (brokerPara[0].equals(ConfConstants.LONG_TRANSACTION_TIME)) {
                    if (isQueryOrTransTimeUseMs) {
                        int intValue = Integer.parseInt(brokerPara[2]) * 1000;
                        if (Integer.toString(intValue)
                                .equals(paraMap.get(ConfConstants.LONG_TRANSACTION_TIME))) {
                            paraMap.remove(ConfConstants.LONG_TRANSACTION_TIME);
                        } else {
                            String sVal = paraMap.get(ConfConstants.LONG_TRANSACTION_TIME);
                            double dVal = Double.parseDouble(sVal) / 1000;
                            paraMap.put(ConfConstants.LONG_TRANSACTION_TIME, String.valueOf(dVal));
                        }
                    } else {
                        if (brokerPara[2].equals(
                                paraMap.get(ConfConstants.LONG_TRANSACTION_TIME))) {
                            paraMap.remove(ConfConstants.LONG_TRANSACTION_TIME);
                        }
                    }
                } else if (brokerPara[2].equals(paraMap.get(brokerPara[0]))) {
                    paraMap.remove(brokerPara[0]);
                }
            }

            SetBrokerConfParameterTask setBrokerConfParameterTask =
                    new SetBrokerConfParameterTask(serverInfo);
            setBrokerConfParameterTask.setConfParameters(confParaMap);

            String taskName =
                    Messages.bind(Messages.setBrokerConfParameterTaskName, this.node.getName());
            CommonTaskExec taskExec = new CommonTaskExec(taskName);
            taskExec.addTask(setBrokerConfParameterTask);
            new ExecTaskWithProgress(taskExec).exec();
            if (taskExec.isSuccess()) {
                CommonUITool.openInformationBox(
                        com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
                        Messages.restartBrokerMsg);
            } else {
                return true;
            }
        }
        // refresh tap
        if (isSettingChange()) {
            boolean isOn = refreshBtn.getSelection();
            String interval = intervalTxt.getText();
            String serverName = node.getServer().getLabel();
            String nodeName = node.getLabel();
            BrokerIntervalSetting newSetting =
                    new BrokerIntervalSetting(serverName, nodeName, interval, isOn);
            BrokerIntervalSettingManager manager = BrokerIntervalSettingManager.getInstance();
            manager.removeBrokerIntervalSetting(serverName, brokerName);
            manager.setBrokerInterval(newSetting);
        }
        return true;
    }

    /**
     * Initiates a grid layout
     *
     * @param layout an instance of GridLayout
     * @param margins if true,sets the marginWidth and marginHeight,or else,using default
     *     value @return
     * @return the layout
     */
    private GridLayout initGridLayout(GridLayout layout, boolean margins) {
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        if (margins) {
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        } else {
            layout.marginWidth = 0;
            layout.marginHeight = 0;
        }
        return layout;
    }

    /**
     * Judge if there is change on table
     *
     * @return <code>true</code> if changed;<code>false</code> otherwise
     */
    @SuppressWarnings("unchecked")
    private boolean isTableChange() {

        List<Map<String, String>> paramList =
                (List<Map<String, String>>) paraTableViewer.getInput();
        Map<String, String> paraMap = new HashMap<String, String>();
        for (Map<String, String> map : paramList) {
            paraMap.put(map.get("0"), map.get("2"));
        }
        confParaMap.put(node.getLabel(), paraMap);

        // set the default value to brokerMap
        String brokerParameters[][] = ConfConstants.getBrokerParameters(serverInfo);
        for (String[] brokerParameter : brokerParameters) {
            if (brokerParameter[0].equalsIgnoreCase(ConfConstants.MASTER_SHM_ID)
                    || brokerParameter[0].equalsIgnoreCase(ConfConstants.ADMIN_LOG_FILE)) {
                continue;
            }
            if (brokerParameter[0].equalsIgnoreCase(ConfConstants.APPL_SERVER_PORT)
                    && brokerMap.get(brokerParameter[0]) == null) {
                int serverPortValue =
                        Integer.parseInt(brokerMap.get(ConfConstants.BROKER_PORT)) + 1;
                brokerMap.put(brokerParameter[0], Integer.toString(serverPortValue));
                continue;
            }
            if (brokerMap.get(brokerParameter[0]) == null) {
                brokerMap.put(brokerParameter[0], brokerParameter[2]);
            }
        }

        for (Map.Entry<String, String> entry : brokerMap.entrySet()) {
            String key = entry.getKey();
            String paraValue = paraMap.get(key);
            if (null != paraValue) {
                if (key.equalsIgnoreCase(ConfConstants.LONG_QUERY_TIME)
                        || key.equalsIgnoreCase(ConfConstants.LONG_TRANSACTION_TIME)) {
                    if (isQueryOrTransTimeUseMs) {
                        double dVal = Double.parseDouble(paraValue) / 1000;
                        double dEntry = Double.parseDouble(entry.getValue());
                        if (Math.abs(dVal - dEntry) >= .0000001) {
                            return true;
                        }
                    } else {
                        if (!paraValue.equals(entry.getValue())) {
                            return true;
                        }
                    }
                } else {
                    if (!paraValue.equals(entry.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Judge if there is change on interval setting
     *
     * @return <code>true</code> if changed;<code>false</code> otherwise
     */
    private boolean isSettingChange() {
        boolean state = setting.isOn();
        String interval = setting.getInterval();
        if (refreshBtn.getSelection() == state) {
            if (!intervalTxt.getText().trim().equals(interval)) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
}
