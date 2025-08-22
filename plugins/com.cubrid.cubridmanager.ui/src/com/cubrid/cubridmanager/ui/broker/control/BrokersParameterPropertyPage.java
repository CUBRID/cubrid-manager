/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.broker.task.DeleteBrokerTask;
import com.cubrid.cubridmanager.core.broker.task.SetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.ui.broker.dialog.BrokerParameterDialog;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSetting;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.common.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * CUBRID Broker property page
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-5-4 created by lizhiqiang
 */
public class BrokersParameterPropertyPage extends PreferencePage {

    private static final String DELETE_BTN_NAME = Messages.deleteBtnName;
    private static final String EDIT_BTN_NAME = Messages.editBtnName;
    private static final String ADD_BTN_NAME = Messages.addBtnName;
    private final String refreshUnit = Messages.refreshUnit;
    private final String refreshOnLbl = Messages.refreshEnvOnLbl;
    private final String refreshEnvTitle = Messages.refreshEnvTitle;
    private final String portOfBrokerLst = Messages.portOfBrokerLst;
    private final String nameOfBrokerLst = Messages.nameOfBrokerLst;
    private static final String BROKER_LIST = Messages.brokerLstGroupName;
    private static final String GENERAL_INFO = Messages.generalInfoGroupName;
    private static final String REFRESHENVOFTAP = Messages.refreshEnvOfTap;
    private static final String BROKERLSTOFTAP = Messages.brokerLstOfTap;
    private static final String RESTART_BROKER_MSG = Messages.restartBrokerMsg;
    private final String editActionTxt = Messages.editActionTxt;
    private final String addActionTxt = Messages.addActionTxt;
    private final String delActionTxt = Messages.delActionTxt;
    private Text masterShmIdTxt;
    private Text adminlogTxt;
    private final ICubridNode node;

    private final Map<String, Map<String, String>> defaultValueMap =
            new HashMap<String, Map<String, String>>();
    private Button refreshBtn;
    private Text intervalTxt;
    private TableViewer brokersTableViewer;
    private List<Map<String, String>> brokerList;
    private Map<String, Map<String, String>> oldConfParaMap;
    private Map<String, Map<String, String>> newConfParaMap;
    private Map<String, BrokerIntervalSetting> oldIntervalSettingMap;
    private Map<String, BrokerIntervalSetting> newIntervalSettingMap;
    private final List<String> deletedBrokerLst = new ArrayList<String>();

    private static final String MASTER_SHMID_LBL_NAME = "MASTER_SHM_ID:";
    private static final String ADMIN_LOG_LBL_NAME = "ADMIN_LOG_FILE:";
    private static final String ENABLE_ACCESS_CONTROL = "ACCESS_CONTROL:";
    private static final String ACCESS_CONTROL_FILE = "ACCESS_CONTROL_FILE:";
    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;
    private String serverName;
    private String brokerEnvName;
    private final ServerUserInfo userInfo;
    private final ServerInfo serverInfo;
    private Text accessControlFileTxt;
    private boolean isSupportEnableAccessControl;
    private Combo enableAccessControlCombo;

    public BrokersParameterPropertyPage(ICubridNode node, String name) {
        super(name, null);
        this.node = node;
        noDefaultAndApplyButton();
        serverInfo = node.getServer().getServerInfo();
        userInfo = serverInfo.getLoginedUserInfo();
    }

    /**
     * Creates the page content
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        layout = new GridLayout();
        tabFolder.setLayout(layout);

        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText(BROKERLSTOFTAP);
        item.setControl(createBrokerLstComp(tabFolder));

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText(REFRESHENVOFTAP);
        item.setControl(createRefreshComp(tabFolder));
        initial();
        setAuthority();

        masterShmIdTxt.addModifyListener(new MasterShmIdModifyListener());
        masterShmIdTxt.addVerifyListener(new NumberVerifyListener());
        return composite;
    }

    /** Sets the authority */
    private void setAuthority() {
        assert (null != userInfo);
        switch (userInfo.getCasAuth()) {
            case AUTH_ADMIN:
                break;
            case AUTH_MONITOR:
                masterShmIdTxt.setEnabled(false);
                adminlogTxt.setEnabled(false);
                addBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                break;
            default:
        }
    }

    /**
     * Creates basic group
     *
     * @param parent the parent composite
     */
    private void createBasicGroup(Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(GENERAL_INFO);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        group.setLayout(gridLayout);

        final Label masterShmIdLbl = new Label(group, SWT.NONE);
        final GridData gdMasterShmIdLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        masterShmIdLbl.setLayoutData(gdMasterShmIdLbl);
        masterShmIdLbl.setText(MASTER_SHMID_LBL_NAME);

        masterShmIdTxt = new Text(group, SWT.BORDER);
        final GridData gdMasterShmIdTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        masterShmIdTxt.setLayoutData(gdMasterShmIdTxt);

        final Label adminlogLbl = new Label(group, SWT.NONE);
        final GridData gdAdminlogLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        adminlogLbl.setLayoutData(gdAdminlogLbl);
        adminlogLbl.setText(ADMIN_LOG_LBL_NAME);

        adminlogTxt = new Text(group, SWT.BORDER);
        final GridData gdAdminlogTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        adminlogTxt.setLayoutData(gdAdminlogTxt);

        isSupportEnableAccessControl = CompatibleUtil.isSupportEnableAccessControl(serverInfo);
        if (!isSupportEnableAccessControl) {
            return;
        }
        final Label enableAccessControlLbl = new Label(group, SWT.NONE);
        final GridData gdEnableAccessControlLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        enableAccessControlLbl.setLayoutData(gdEnableAccessControlLbl);
        enableAccessControlLbl.setText(ENABLE_ACCESS_CONTROL);

        enableAccessControlCombo = new Combo(group, SWT.READ_ONLY);
        final GridData gdEnableAccessControlCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
        enableAccessControlCombo.setLayoutData(gdEnableAccessControlCombo);
        enableAccessControlCombo.setItems(
                new String[] {OnOffType.ON.getText(), OnOffType.OFF.getText()});

        final Label accessControlFileLbl = new Label(group, SWT.NONE);
        final GridData gdAccessControlFileLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        accessControlFileLbl.setLayoutData(gdAccessControlFileLbl);
        accessControlFileLbl.setText(ACCESS_CONTROL_FILE);

        accessControlFileTxt = new Text(group, SWT.BORDER);
        final GridData gdAccessControlFile = new GridData(SWT.FILL, SWT.CENTER, true, false);
        accessControlFileTxt.setLayoutData(gdAccessControlFile);
    }

    /**
     * Creates brokers list Composite
     *
     * @param parent the parent composite
     * @return the composite
     */
    private Control createBrokerLstComp(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createBasicGroup(composite);

        Group brokerLstGroup = new Group(composite, SWT.NONE);
        brokerLstGroup.setText(BROKER_LIST);
        brokerLstGroup.setLayout(new GridLayout(2, false));
        brokerLstGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        String[] columnNameArrs = new String[] {nameOfBrokerLst, portOfBrokerLst};
        brokersTableViewer =
                CommonUITool.createCommonTableViewer(
                        brokerLstGroup,
                        null,
                        columnNameArrs,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));
        Table brokersTable = brokersTableViewer.getTable();
        TableLayout tlayout = new TableLayout();
        tlayout.addColumnData(new ColumnWeightData(120, 100, true));
        tlayout.addColumnData(new ColumnWeightData(120, 100, true));
        brokersTable.setLayout(tlayout);

        createDealButton(brokerLstGroup);
        return composite;
    }

    /**
     * Creates the refresh composite
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
        tipLbl.setText(refreshEnvTitle);
        tipLbl.setLayoutData(gdTipLbl);

        final Composite radioComp = new Composite(refreshComp, SWT.None);
        final GridData gdRadioComp = new GridData(SWT.FILL, SWT.TOP, true, false);
        radioComp.setLayoutData(gdRadioComp);
        radioComp.setLayout(new GridLayout(3, false));

        refreshBtn = new Button(radioComp, SWT.CHECK);
        refreshBtn.setText(refreshOnLbl);
        refreshBtn.setSelection(false);

        intervalTxt = new Text(radioComp, SWT.BORDER | SWT.RIGHT);
        final GridData gdIntervalTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        intervalTxt.setLayoutData(gdIntervalTxt);
        intervalTxt.setText("1");
        intervalTxt.setEnabled(false);

        final Label secLbl = new Label(radioComp, SWT.NONE);
        final GridData gdSecLbl = new GridData(SWT.LEFT, SWT.TOP, true, false);
        secLbl.setText(refreshUnit);
        tipLbl.setLayoutData(gdSecLbl);

        return refreshComp;
    }

    /** Initializes the parameters of this dialog */
    private void initial() {
        oldConfParaMap = node.getServer().getServerInfo().getBrokerConfParaMap();
        Map<String, String> map = null;
        if (oldConfParaMap != null) {
            map = oldConfParaMap.get(ConfConstants.BROKER_SECTION_NAME);
        }
        if (map != null) {
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            Map<String, String> defaultMap = new HashMap<String, String>();
            defaultValueMap.put(ConfConstants.BROKER_SECTION_NAME, defaultMap);
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                defaultMap.put(entry.getKey(), entry.getValue());
            }
            if (null == defaultMap.get(ConfConstants.MASTER_SHM_ID)) {
                masterShmIdTxt.setText("");
            } else {
                masterShmIdTxt.setText(defaultMap.get(ConfConstants.MASTER_SHM_ID));
            }
            if (null == defaultMap.get(ConfConstants.ADMIN_LOG_FILE)) {
                adminlogTxt.setText("");
            } else {
                adminlogTxt.setText(defaultMap.get(ConfConstants.ADMIN_LOG_FILE));
            }
            if (isSupportEnableAccessControl) {
                if (null == defaultMap.get(ConfConstants.ENABLE_ACCESS_CONTROL)) {
                    enableAccessControlCombo.select(1);
                } else {
                    enableAccessControlCombo.setText(
                            defaultMap.get(ConfConstants.ENABLE_ACCESS_CONTROL));
                }
                if (null == defaultMap.get(ConfConstants.ACCESS_CONTROL_FILE)) {
                    accessControlFileTxt.setText("");
                } else {
                    accessControlFileTxt.setText(defaultMap.get(ConfConstants.ACCESS_CONTROL_FILE));
                }
            }
        }

        brokerList = new ArrayList<Map<String, String>>();
        if (oldConfParaMap == null) {
            return;
        }

        // set the broker list
        String[][] supportedBrokerParams = ConfConstants.getBrokerParameters(serverInfo);
        for (Map.Entry<String, Map<String, String>> brokerPara : oldConfParaMap.entrySet()) {
            if (brokerPara.getKey().equals(ConfConstants.BROKER_SECTION_NAME)) {
                continue;
            }
            String name = brokerPara.getKey();
            Map<String, String> pair = brokerPara.getValue();
            String port = pair.get(ConfConstants.BROKER_PORT);

            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("0", name);
            dataMap.put("1", port);
            for (int i = 0; i < supportedBrokerParams.length; i++) {
                String paramKey = supportedBrokerParams[i][0];
                String value = pair.get(paramKey);
                dataMap.put(paramKey, value);
            }
            brokerList.add(dataMap);
        }
        brokersTableViewer.setInput(brokerList);

        // initialize refresh
        BrokerIntervalSettingManager manager = BrokerIntervalSettingManager.getInstance();
        serverName = node.getServer().getLabel();
        brokerEnvName = node.getLabel();
        BrokerIntervalSetting setting = manager.getBrokerIntervalSetting(serverName, brokerEnvName);
        oldIntervalSettingMap = new HashMap<String, BrokerIntervalSetting>();

        newIntervalSettingMap = new HashMap<String, BrokerIntervalSetting>();
        if (null != setting) {
            boolean isOn = setting.isOn();
            refreshBtn.setSelection(isOn);
            intervalTxt.setText(setting.getInterval());
            intervalTxt.setEnabled(isOn);
            oldIntervalSettingMap.put(serverName + "_" + brokerEnvName, setting);
        }

        for (Map<String, String> dataMap : brokerList) {
            String brokername = dataMap.get("0");
            setting = manager.getBrokerIntervalSetting(serverName, brokername);
            if (null != setting) {
                oldIntervalSettingMap.put(serverName + "_" + brokername, setting);
            }
        }

        for (Map.Entry<String, BrokerIntervalSetting> entry : oldIntervalSettingMap.entrySet()) {
            String aSettingName = entry.getKey();
            BrokerIntervalSetting aSetting = new BrokerIntervalSetting();
            String aBrokerName = entry.getValue().getBrokerName();
            String aServerName = entry.getValue().getServerName();
            String aInterval = entry.getValue().getInterval();
            boolean aOn = entry.getValue().isOn();
            aSetting.setBrokerName(aBrokerName);
            aSetting.setServerName(aServerName);
            aSetting.setInterval(aInterval);
            aSetting.setOn(aOn);
            newIntervalSettingMap.put(aSettingName, aSetting);
        }

        // add Listener
        refreshBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        BrokerIntervalSetting brokerEnvSetting =
                                newIntervalSettingMap.get(serverName + "_" + brokerEnvName);
                        if (refreshBtn.getSelection()) {
                            intervalTxt.setEnabled(true);
                            brokerEnvSetting.setOn(true);
                        } else {
                            intervalTxt.setEnabled(false);
                            brokerEnvSetting.setOn(false);
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

        intervalTxt.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        String newInterval = intervalTxt.getText().trim();
                        BrokerIntervalSetting brokerEnvSetting =
                                newIntervalSettingMap.get(serverName + "_" + brokerEnvName);
                        brokerEnvSetting.setInterval(newInterval);
                    }
                });
    }

    /** Restore the default value */
    protected void performDefaults() {
        initial();
        super.performDefaults();
    }

    /**
     * Save the page content
     *
     * @return <code>true</code> if it saved successfully;<code>false</code> otherwise
     */
    public boolean performOk() {
        if (adminlogTxt == null || adminlogTxt.isDisposed()) {
            return true;
        }
        // execute delete broker task
        if (isTableChange()) {
            CommonTaskExec taskExec = new CommonTaskExec(Messages.setBrokerConfParametersTaskName);
            for (String bname : deletedBrokerLst) {
                DeleteBrokerTask task = new DeleteBrokerTask(serverInfo);
                task.setBrokerName(bname);
                taskExec.addTask(task);
            }
            // remove the default value
            String brokerParameters[][] = ConfConstants.getBrokerParameters(serverInfo);
            for (Map.Entry<String, Map<String, String>> entry : newConfParaMap.entrySet()) {
                Map<String, String> paraMap = entry.getValue();
                for (String[] brokerPara : brokerParameters) {
                    if (brokerPara[0].equals(ConfConstants.APPL_SERVER_PORT)) {
                        String port = paraMap.get(ConfConstants.BROKER_PORT);
                        if (null != port) {
                            int serverPortValue =
                                    Integer.parseInt(paraMap.get(ConfConstants.BROKER_PORT)) + 1;
                            String serverPort = paraMap.get(ConfConstants.APPL_SERVER_PORT);
                            if (serverPort.equalsIgnoreCase(Integer.toString(serverPortValue))) {
                                paraMap.remove(ConfConstants.APPL_SERVER_PORT);
                            }
                        }
                    } else if (ConfConstants.isDefaultBrokerParameter(brokerPara[0])
                            && brokerPara[2].equals(paraMap.get(brokerPara[0]))) {
                        paraMap.remove(brokerPara[0]);
                    }
                }
            }

            // execute set parameter task
            SetBrokerConfParameterTask setBrokerConfParameterTask =
                    new SetBrokerConfParameterTask(serverInfo);
            setBrokerConfParameterTask.setConfParameters(newConfParaMap);
            taskExec.addTask(setBrokerConfParameterTask);
            new ExecTaskWithProgress(taskExec).exec();
            if (taskExec.isSuccess()) {
                CommonUITool.openInformationBox(Messages.titleSuccess, RESTART_BROKER_MSG);
            }
        }
        // refresh tap
        if (isSettingChange()) {
            boolean isOn = refreshBtn.getSelection();
            String interval = intervalTxt.getText().trim();
            String serverName = node.getServer().getLabel();
            String nodeName = node.getLabel();
            BrokerIntervalSetting setting =
                    new BrokerIntervalSetting(serverName, nodeName, interval, isOn);
            newIntervalSettingMap.put(serverName + "_" + brokerEnvName, setting);

            BrokerIntervalSettingManager manager = BrokerIntervalSettingManager.getInstance();
            for (Map.Entry<String, BrokerIntervalSetting> entry :
                    oldIntervalSettingMap.entrySet()) {
                String brokerName = entry.getValue().getBrokerName();
                manager.removeBrokerIntervalSetting(serverName, brokerName);
            }
            for (Map.Entry<String, BrokerIntervalSetting> entry :
                    newIntervalSettingMap.entrySet()) {
                BrokerIntervalSetting newSetting = entry.getValue();
                manager.setBrokerInterval(newSetting);
            }
        }
        return true;
    }

    /**
     * Judge if there is change on table
     *
     * @return <code>true</code> if changed;<code>false</code> otherwise
     */
    private boolean isTableChange() {
        newConfParaMap = new HashMap<String, Map<String, String>>();
        Map<String, String> basicMap = new HashMap<String, String>();
        basicMap.put(ConfConstants.MASTER_SHM_ID, masterShmIdTxt.getText());
        basicMap.put(ConfConstants.ADMIN_LOG_FILE, adminlogTxt.getText());
        if (isSupportEnableAccessControl) {
            basicMap.put(ConfConstants.ENABLE_ACCESS_CONTROL, enableAccessControlCombo.getText());
            basicMap.put(ConfConstants.ACCESS_CONTROL_FILE, accessControlFileTxt.getText());
        }

        newConfParaMap.put(ConfConstants.BROKER_SECTION_NAME, basicMap);
        String brokerParameters[][] = ConfConstants.getBrokerParameters(serverInfo);
        for (Map<String, String> map : brokerList) {
            Map<String, String> paraMap = new HashMap<String, String>();
            for (String[] brokerParameter : brokerParameters) {
                if (brokerParameter[0].equalsIgnoreCase(ConfConstants.MASTER_SHM_ID)
                        || brokerParameter[0].equalsIgnoreCase(ConfConstants.ADMIN_LOG_FILE)
                        || brokerParameter[0].equals(ConfConstants.ENABLE_ACCESS_CONTROL)
                        || brokerParameter[0].equals(ConfConstants.ACCESS_CONTROL_FILE)) {
                    continue;
                }
                if (brokerParameter[0].equalsIgnoreCase(ConfConstants.APPL_SERVER_PORT)
                        && map.get(brokerParameter[0]) == null) {
                    int serverPortValue = Integer.parseInt(map.get(ConfConstants.BROKER_PORT)) + 1;
                    paraMap.put(brokerParameter[0], Integer.toString(serverPortValue));
                    continue;
                }
                if (map.get(brokerParameter[0]) == null) {
                    paraMap.put(brokerParameter[0], brokerParameter[2]);
                } else {
                    paraMap.put(brokerParameter[0], map.get(brokerParameter[0]));
                }
            }
            newConfParaMap.put(map.get("0"), paraMap);
        }

        if (oldConfParaMap.size() != newConfParaMap.size()) {
            return true;
        }
        for (Map.Entry<String, Map<String, String>> oldEntry : oldConfParaMap.entrySet()) {
            String oldKey = oldEntry.getKey();
            Map<String, String> oldPropMap = oldEntry.getValue();
            if (!"broker".equals(oldKey)) {
                for (String[] brokerParameter : brokerParameters) {
                    if (brokerParameter[0].equalsIgnoreCase(ConfConstants.MASTER_SHM_ID)
                            || brokerParameter[0].equalsIgnoreCase(ConfConstants.ADMIN_LOG_FILE)
                            || brokerParameter[0].equals(ConfConstants.ENABLE_ACCESS_CONTROL)
                            || brokerParameter[0].equals(ConfConstants.ACCESS_CONTROL_FILE)) {
                        continue;
                    }
                    if (brokerParameter[0].equalsIgnoreCase(ConfConstants.APPL_SERVER_PORT)
                            && oldPropMap.get(brokerParameter[0]) == null
                            && null != oldPropMap.get(ConfConstants.BROKER_PORT)
                            && !oldPropMap.get(ConfConstants.BROKER_PORT).equals("")) {
                        int serverPortValue =
                                Integer.parseInt(oldPropMap.get(ConfConstants.BROKER_PORT)) + 1;
                        oldPropMap.put(brokerParameter[0], Integer.toString(serverPortValue));
                        continue;
                    }

                    if (oldPropMap.get(brokerParameter[0]) == null) {
                        oldPropMap.put(brokerParameter[0], brokerParameter[2]);
                    }
                }
            }
            boolean isExist = false;
            for (Map.Entry<String, Map<String, String>> newEntry : newConfParaMap.entrySet()) {
                if (newEntry.getKey().equals(oldKey)) {
                    isExist = true;
                    Map<String, String> newPropMap = newEntry.getValue();

                    for (Map.Entry<String, String> oldProp : oldPropMap.entrySet()) {
                        String propName = oldProp.getKey();
                        String oldPropValue = oldProp.getValue();
                        String newPropValue = newPropMap.get(propName);
                        if (oldPropValue != null && !oldPropValue.equals(newPropValue)) {
                            return true;
                        }
                    }
                    for (Map.Entry<String, String> newProp : newPropMap.entrySet()) {
                        String propName = newProp.getKey();
                        String newPropValue = newProp.getValue();
                        String oldPropValue = oldPropMap.get(propName);
                        if (newPropValue != null && !newPropValue.equals(oldPropValue)) {
                            return true;
                        }
                    }
                }
            }
            if (!isExist) {
                return true;
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
        if (oldIntervalSettingMap.size() != newIntervalSettingMap.size()) {
            return true;
        }
        for (Map.Entry<String, BrokerIntervalSetting> oldEntry : oldIntervalSettingMap.entrySet()) {
            String oldKey = oldEntry.getKey();
            BrokerIntervalSetting oldSetting = oldEntry.getValue();

            BrokerIntervalSetting newSetting = newIntervalSettingMap.get(oldKey);
            if (null == newSetting) {
                return true;
            } else {
                if (oldSetting.isOn() != newSetting.isOn()) {
                    return true;
                }
                if (!oldSetting.getInterval().equals(newSetting.getInterval())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates the button of add, edit, delete
     *
     * @param parent the parent composite
     */
    private void createDealButton(Composite parent) {
        Composite btnComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        btnComposite.setLayout(layout);
        btnComposite.setLayoutData(new GridData());
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER);

        addBtn = new Button(btnComposite, SWT.PUSH);
        addBtn.setText(ADD_BTN_NAME);
        Point minButtonSize = addBtn.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minButtonSize.x);
        addBtn.setLayoutData(data);
        addBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        AddAction addAction = new AddAction();
                        addAction.run();
                    }
                });
        new Label(btnComposite, SWT.NONE);
        editBtn = new Button(btnComposite, SWT.PUSH);
        editBtn.setText(EDIT_BTN_NAME);
        editBtn.setLayoutData(data);
        IStructuredSelection selection = (IStructuredSelection) brokersTableViewer.getSelection();
        if (0 == selection.size()) {
            editBtn.setEnabled(false);
        }
        editBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        EditAction editAction = new EditAction();
                        editAction.run();
                    }
                });
        new Label(btnComposite, SWT.NONE);
        deleteBtn = new Button(btnComposite, SWT.PUSH);
        deleteBtn.setText(DELETE_BTN_NAME);
        deleteBtn.setLayoutData(data);
        if (0 == selection.size()) {
            deleteBtn.setEnabled(false);
        }
        deleteBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        DeleteAction deleteAction = new DeleteAction();
                        deleteAction.run();
                    }
                });
        brokersTableViewer.addPostSelectionChangedListener(
                new ISelectionChangedListener() {

                    public void selectionChanged(SelectionChangedEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) (event.getSelection());
                        if (selection.size() > 0) {
                            editBtn.setEnabled(true);
                            if (userInfo.getCasAuth() == CasAuthType.AUTH_ADMIN) {
                                deleteBtn.setEnabled(true);
                            } else {
                                deleteBtn.setEnabled(false);
                            }
                        } else {
                            editBtn.setEnabled(false);
                            deleteBtn.setEnabled(false);
                        }
                    }
                });
    }

    /**
     * An action that is an inner class in order to execute deleting the parameter of a broker
     *
     * @author lizhiqiang
     * @version 1.0 - 2009-5-23 created by lizhiqiang
     */
    private class DeleteAction extends Action {

        private final Map<String, String> brokerMap;

        @SuppressWarnings("unchecked")
        public DeleteAction() {
            setText(delActionTxt);
            IStructuredSelection selection =
                    (IStructuredSelection) brokersTableViewer.getSelection();
            brokerMap = (Map<String, String>) (selection.getFirstElement());
            String serverName = node.getServer().getLabel();
            String brokerName = brokerMap.get("0");
            newIntervalSettingMap.remove(serverName + "_" + brokerMap.get("0"));
            deletedBrokerLst.add(brokerName);
        }

        /** Delete the borker */
        public void run() {
            brokerList.remove(brokerMap);
            brokersTableViewer.remove(brokerMap);
        }

        /**
         * Return enabled status
         *
         * @return <code>true</code> if enabled;<code>false</code> otherwise
         */
        public boolean isEnabled() {
            if (null == brokerMap) {
                return false;
            }
            return true;
        }
    }

    /**
     * An action that is an inner class in order to execute editing the parameter of a broker
     *
     * @author lizhiqiang
     * @version 1.0 - 2009-5-23 created by lizhiqiang
     */
    private class EditAction extends Action {
        private final Map<String, String> brokerMap;
        private final BrokerIntervalSetting brokerIntervalSetting;

        @SuppressWarnings("unchecked")
        public EditAction() {
            setText(editActionTxt);
            IStructuredSelection selection =
                    (IStructuredSelection) brokersTableViewer.getSelection();
            brokerMap = (Map<String, String>) (selection.getFirstElement());
            String serverName = node.getServer().getLabel();
            brokerIntervalSetting =
                    newIntervalSettingMap.get(serverName + "_" + brokerMap.get("0"));
        }

        /** Edit the broker */
        @SuppressWarnings("unchecked")
        public void run() {
            String sMasterShmId = masterShmIdTxt.getText().trim();
            List<Map<String, String>> brokerLst2Dialog =
                    (List<Map<String, String>>) brokersTableViewer.getInput();
            BrokerParameterDialog brokerParameterDialog =
                    new BrokerParameterDialog(
                            getShell(),
                            AddEditType.EDIT,
                            node,
                            brokerLst2Dialog,
                            sMasterShmId,
                            brokerMap,
                            brokerIntervalSetting);
            if (brokerParameterDialog.open() == Dialog.OK) {
                BrokerIntervalSetting brokerIntervalSetting =
                        brokerParameterDialog.getBrokerIntervalSetting();
                String serverName = node.getServer().getLabel();
                brokerIntervalSetting.setServerName(serverName);
                newIntervalSettingMap.put(
                        serverName + "_" + brokerIntervalSetting.getBrokerName(),
                        brokerIntervalSetting);

                brokersTableViewer.refresh(brokerMap);
            }
        }

        /**
         * Return enabled status
         *
         * @return <code>true</code> if enabled;<code>false</code> otherwise
         */
        public boolean isEnabled() {
            if (null == brokerMap) {
                return false;
            }
            return true;
        }
    }

    /**
     * An action that is an inner class in order to execute adding the parameter of a broker
     *
     * @author lizhiqiang
     * @version 1.0 - 2009-5-23 created by lizhiqiang
     */
    private class AddAction extends Action {

        public AddAction() {
            setText(addActionTxt);
        }

        /** Add broker */
        @SuppressWarnings("unchecked")
        public void run() {
            String sMasterShmId = masterShmIdTxt.getText().trim();
            List<Map<String, String>> brokerLst2Dialog =
                    (List<Map<String, String>>) brokersTableViewer.getInput();
            BrokerParameterDialog brokerParameterDialog =
                    new BrokerParameterDialog(
                            getShell(), AddEditType.ADD, node, brokerLst2Dialog, sMasterShmId);
            if (brokerParameterDialog.open() == Dialog.OK) {
                Map<String, String> brokerMap = brokerParameterDialog.getBrokerMap();
                BrokerIntervalSetting brokerIntervalSetting =
                        brokerParameterDialog.getBrokerIntervalSetting();
                String serverName = node.getServer().getLabel();
                brokerIntervalSetting.setServerName(serverName);
                newIntervalSettingMap.put(
                        serverName + "_" + brokerIntervalSetting.getBrokerName(),
                        brokerIntervalSetting);
                brokerList.add(brokerMap);
                brokersTableViewer.add(brokerMap);
            }
        }
    }

    /**
     * MasterShmIdModifyListener Response to the modification of Master_shm_id
     *
     * @author cn12978
     * @version 1.0 - 2009-6-1 created by cn12978
     */
    private class MasterShmIdModifyListener implements ModifyListener {

        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event the modify event
         */
        public void modifyText(ModifyEvent event) {
            String sMasterShmId = masterShmIdTxt.getText().trim();
            if (sMasterShmId.length() <= 0) {
                setErrorMessage(Messages.errMasterShmId);
                setValid(false);
                return;
            }
            if (sMasterShmId.length() > 6) {
                setErrorMessage(Messages.errMasterShmId);
                setValid(false);
                return;
            }
            int port = Integer.parseInt(sMasterShmId);
            if (port < 1024 || port > 65535) {
                setErrorMessage(Messages.errMasterShmId);
                setValid(false);
                return;
            }
            for (Map<String, String> map : brokerList) {
                String appServerShmId = map.get(ConfConstants.APPL_SERVER_SHM_ID);
                if (appServerShmId.equals(sMasterShmId)) {
                    setErrorMessage(Messages.errMasterShmIdSamePort);
                    setValid(false);
                    return;
                }
            }
            setErrorMessage(null);
            setValid(true);
        }
    }

    /** A class that verify the entering of volumeText */
    private static class NumberVerifyListener implements VerifyListener {

        /**
         * Verify the text content
         *
         * @param event the verify event
         */
        public void verifyText(VerifyEvent event) {
            if ("".equals(event.text)) {
                return;
            }
            if (ValidateUtil.isNumber(event.text)) {
                event.doit = true;
            } else {
                event.doit = false;
            }
        }
    }
}
