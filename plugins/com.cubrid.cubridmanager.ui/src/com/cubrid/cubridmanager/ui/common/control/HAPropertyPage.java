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
import com.cubrid.cubridmanager.core.common.task.SetHAConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * HA property page
 *
 * @author pangqiren
 * @version 1.0 - 2011-3-30 created by pangqiren
 */
public class HAPropertyPage extends PreferencePage implements ModifyListener {

    private final ICubridNode node;
    private boolean isAdmin = false;
    private Combo haModeCombo;
    private Text haNodeListText;
    private Text haPortText;
    private Text haReplicaListText;
    private Text haDbListText;
    private Text haApplyMaxMemText;
    private Text haCopySyncModeText;

    private Text hostNameText;
    private Text userNameText;
    private Text userHaCopySyncModeText;

    private final Map<String, Map<String, String>> initialValueMap =
            new HashMap<String, Map<String, String>>();
    private final Map<String, String> defaultValueMap = new HashMap<String, String>();

    private boolean isChanged = false;
    private boolean isApply = false;
    private Table hostUserTable;
    private Button delBtn;
    private Text haPingHostText;
    private Text haCopyLogBaseText;

    /**
     * The constructor
     *
     * @param node
     * @param name
     */
    public HAPropertyPage(ICubridNode node, String name) {
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

        createCommonGroup(composite);
        createHostUserGroup(composite);

        initial();
        return composite;
    }

    /**
     * Create the general group composite
     *
     * @param parent the parent composite
     */
    private void createCommonGroup(Composite parent) {
        Group commonInfoGroup = new Group(parent, SWT.NONE);
        commonInfoGroup.setText(Messages.grpHACommon);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        commonInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        commonInfoGroup.setLayout(layout);

        Label haModeLabel = new Label(commonInfoGroup, SWT.LEFT);
        haModeLabel.setText(ConfConstants.HA_MODE + ":");
        haModeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haModeCombo = new Combo(commonInfoGroup, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        haModeCombo.setEnabled(isAdmin);
        haModeCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haPortLabel = new Label(commonInfoGroup, SWT.LEFT);
        haPortLabel.setText(ConfConstants.HA_PORT_ID + ":");
        haPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haPortText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haPortText.setEnabled(isAdmin);
        haPortText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haNodeListLabel = new Label(commonInfoGroup, SWT.LEFT);
        haNodeListLabel.setText(ConfConstants.HA_NODE_LIST + ":");
        haNodeListLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haNodeListText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haNodeListText.setEnabled(isAdmin);
        haNodeListText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haReplicaListLabel = new Label(commonInfoGroup, SWT.LEFT);
        haReplicaListLabel.setText(ConfConstants.HA_REPLICA_LIST + ":");
        haReplicaListLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haReplicaListText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haReplicaListText.setEnabled(isAdmin);
        haReplicaListText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haDbListLabel = new Label(commonInfoGroup, SWT.LEFT);
        haDbListLabel.setText(ConfConstants.HA_DB_LIST + ":");
        haDbListLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haDbListText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haDbListText.setEnabled(isAdmin);
        haDbListText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haApplyMaxMemLabel = new Label(commonInfoGroup, SWT.LEFT);
        haApplyMaxMemLabel.setText(ConfConstants.HA_APPLY_MAX_MEM_SIZE + ":");
        haApplyMaxMemLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haApplyMaxMemText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haApplyMaxMemText.setEnabled(isAdmin);
        haApplyMaxMemText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haCopySyncModeLabel = new Label(commonInfoGroup, SWT.LEFT);
        haCopySyncModeLabel.setText(ConfConstants.HA_COPY_SYNC_MODE + ":");
        haCopySyncModeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haCopySyncModeText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haCopySyncModeText.setEnabled(isAdmin);
        haCopySyncModeText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haPingHostsLabel = new Label(commonInfoGroup, SWT.LEFT);
        haPingHostsLabel.setText(ConfConstants.HA_PING_HOSTS + ":");
        haPingHostsLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haPingHostText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haPingHostText.setEnabled(isAdmin);
        haPingHostText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haCopyLogBaseLabel = new Label(commonInfoGroup, SWT.LEFT);
        haCopyLogBaseLabel.setText(ConfConstants.HA_COPY_LOG_BASE + ":");
        haCopyLogBaseLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        haCopyLogBaseText = new Text(commonInfoGroup, SWT.LEFT | SWT.BORDER);
        haCopyLogBaseText.setEnabled(isAdmin);
        haCopyLogBaseText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
    }

    /**
     * Create diagnostics group composite
     *
     * @param parent the parent composite
     */
    private void createHostUserGroup(Composite parent) {
        Group hostUserGroup = new Group(parent, SWT.NONE);
        hostUserGroup.setText(Messages.grpHAHostUser);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        hostUserGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        hostUserGroup.setLayout(layout);

        Label hostNameLabel = new Label(hostUserGroup, SWT.LEFT);
        hostNameLabel.setText(Messages.lblHostName);
        hostNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        hostNameText = new Text(hostUserGroup, SWT.LEFT | SWT.BORDER);
        hostNameText.setEnabled(isAdmin);
        hostNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label userNameLabel = new Label(hostUserGroup, SWT.LEFT);
        userNameLabel.setText(Messages.lblUserName);
        userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        userNameText = new Text(hostUserGroup, SWT.LEFT | SWT.BORDER);
        userNameText.setToolTipText(Messages.tipUserName);
        userNameText.setEnabled(isAdmin);
        userNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label haCopySyncModeLabel = new Label(hostUserGroup, SWT.LEFT);
        haCopySyncModeLabel.setText(Messages.lblHACopySyncMode);
        haCopySyncModeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        userHaCopySyncModeText = new Text(hostUserGroup, SWT.LEFT | SWT.BORDER);
        userHaCopySyncModeText.setToolTipText(Messages.tipSyncMode);
        userHaCopySyncModeText.setEnabled(isAdmin);
        userHaCopySyncModeText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Composite btnComposite = createBtnComposite(hostUserGroup);
        Button addBtn = new Button(btnComposite, SWT.NONE);
        RowData rowData = new RowData();
        rowData.width = 60;
        addBtn.setLayoutData(rowData);
        addBtn.setText(Messages.btnAdd);
        addBtn.setEnabled(isAdmin);
        addBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String hostName = hostNameText.getText();
                        String userName = userNameText.getText();
                        String syncMode = userHaCopySyncModeText.getText();
                        if (hostName.trim().length() == 0) {
                            CommonUITool.openErrorBox(Messages.errHostName);
                            hostNameText.setFocus();
                            return;
                        }
                        if (userName.trim().length() == 0) {
                            CommonUITool.openErrorBox(Messages.errUserName);
                            userNameText.setFocus();
                            return;
                        }
                        if (syncMode.trim().length() == 0) {
                            CommonUITool.openErrorBox(Messages.errSyncMode);
                            userHaCopySyncModeText.setFocus();
                            return;
                        }
                        TableItem[] items = hostUserTable.getItems();
                        TableItem editItem = null;
                        for (int i = 0; i < items.length; i++) {
                            TableItem item = items[i];
                            if (hostName.equals(item.getText(0))
                                    && userName.equals(item.getText(1))) {
                                editItem = item;
                                break;
                            }
                        }
                        if (editItem == null) {
                            editItem = new TableItem(hostUserTable, SWT.NONE);
                        }
                        editItem.setText(0, hostName);
                        editItem.setText(1, userName);
                        editItem.setText(2, syncMode);
                    }
                });

        hostUserTable =
                new Table(
                        hostUserGroup,
                        SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        {
            hostUserTable.setHeaderVisible(true);
            GridData gdColumnTable = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
            gdColumnTable.heightHint = 160;
            hostUserTable.setLayoutData(gdColumnTable);
            hostUserTable.setLinesVisible(true);
        }
        CommonUITool.hackForYosemite(hostUserTable);

        hostUserTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableItem[] items = hostUserTable.getSelection();
                        if (items == null || items.length == 0) {
                            delBtn.setEnabled(false);
                        } else {
                            delBtn.setEnabled(isAdmin);
                        }
                        if (isAdmin && items != null && items.length > 0) {
                            String hostName = items[0].getText(0);
                            String userName = items[0].getText(1);
                            String syncMode = items[0].getText(2);
                            hostNameText.setText(hostName);
                            userNameText.setText(userName);
                            userHaCopySyncModeText.setText(syncMode);
                        }
                    }
                });

        TableColumn tblCol = new TableColumn(hostUserTable, SWT.LEFT);
        tblCol.setWidth(83);
        tblCol.setText(Messages.colHostName);

        tblCol = new TableColumn(hostUserTable, SWT.LEFT);
        tblCol.setWidth(123);
        tblCol.setText(Messages.colUserName);

        tblCol = new TableColumn(hostUserTable, SWT.LEFT);
        tblCol.setWidth(196);
        tblCol.setText(Messages.colHACopySyncMode);

        btnComposite = createBtnComposite(hostUserGroup);
        delBtn = new Button(btnComposite, SWT.NONE);
        rowData = new RowData();
        rowData.width = 60;
        delBtn.setLayoutData(rowData);
        delBtn.setText(Messages.btnDelete);
        delBtn.setEnabled(isAdmin);
        delBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        int[] indices = hostUserTable.getSelectionIndices();
                        if (indices == null || indices.length == 0) {
                            return;
                        }
                        hostUserTable.remove(indices);
                    }
                });
    }

    /**
     * Create the button composite
     *
     * @param parent Composite
     * @return Composite
     */
    private Composite createBtnComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 5;
        composite.setLayout(rowLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.END;
        composite.setLayoutData(gridData);
        return composite;
    }

    /** initial the page content */
    private void initial() {
        Map<String, Map<String, String>> confParaMap =
                node.getServer().getServerInfo().getHaConfParaMap();
        if (confParaMap != null) {
            Iterator<Map.Entry<String, Map<String, String>>> confParaMapIt =
                    confParaMap.entrySet().iterator();
            while (confParaMapIt.hasNext()) {
                Map.Entry<String, Map<String, String>> entry = confParaMapIt.next();
                String key = entry.getKey();
                Map<String, String> map = entry.getValue();
                if (map != null) {
                    Map<String, String> sectionMap = new HashMap<String, String>();
                    Iterator<Map.Entry<String, String>> mapIt = map.entrySet().iterator();
                    while (mapIt.hasNext()) {
                        Map.Entry<String, String> mapEntry = mapIt.next();
                        String mapKey = mapEntry.getKey();
                        String mapValue = mapEntry.getValue();
                        sectionMap.put(mapKey, mapValue);
                    }
                    initialValueMap.put(key, sectionMap);
                }
            }
        }

        String haConfParameters[][] = ConfConstants.getHAConfParameters();
        for (int i = 0; i < haConfParameters.length; i++) {
            String key = haConfParameters[i][0];
            String value = haConfParameters[i][2];
            defaultValueMap.put(key, value);
        }
        defaultValue();

        haPortText.addModifyListener(this);
        haNodeListText.addModifyListener(this);
        haReplicaListText.addModifyListener(this);
        haDbListText.addModifyListener(this);
        haApplyMaxMemText.addModifyListener(this);
        haCopySyncModeText.addModifyListener(this);
        haPingHostText.addModifyListener(this);
        haCopyLogBaseText.addModifyListener(this);
    }

    /** Restore the default value */
    private void defaultValue() {
        haModeCombo.setItems(new String[] {"on", "off", "replica"});
        // initial the general parameter value from default value
        initValueFromConf(defaultValueMap);

        // Initial the general parameter value from common section([common] in
        // cubrid_ha.conf)
        Map<String, String> comonParaMap = initialValueMap.get(ConfConstants.COMMON_SECTION);
        initValueFromConf(comonParaMap);

        Iterator<Entry<String, Map<String, String>>> it = initialValueMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Map<String, String>> entry = it.next();
            String key = entry.getKey();
            if (key.equals(ConfConstants.COMMON_SECTION)) {
                continue;
            }
            if (!key.matches("^\\[%.+\\]$")) {
                continue;
            }
            key = key.replaceAll("\\[%", "");
            key = key.replaceAll("\\]", "");
            String[] hostUsers = key.split("\\|");
            if (hostUsers == null || hostUsers.length != 2) {
                continue;
            }
            String hostName = hostUsers[0] == null ? "" : hostUsers[0];
            String userName = hostUsers[1] == null ? "" : hostUsers[1];
            Map<String, String> map = entry.getValue();
            String syncMode = map.get(ConfConstants.HA_COPY_SYNC_MODE);

            TableItem item = new TableItem(hostUserTable, SWT.NONE);
            item.setText(0, hostName);
            item.setText(1, userName);
            item.setText(2, syncMode == null ? "" : syncMode);
        }
    }

    /**
     * Initial the general parameter value from common section([common] in cubrid.conf)
     *
     * @param paraMap the pamateter map
     */
    private void initValueFromConf(Map<String, String> paraMap) {
        if (paraMap == null) {
            return;
        }

        String str = paraMap.get(ConfConstants.HA_MODE);
        if (str != null && str.trim().length() > 0) {
            haModeCombo.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_PORT_ID);
        if (str != null && str.trim().length() > 0) {
            haPortText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_NODE_LIST);
        if (str != null && str.trim().length() > 0) {
            haNodeListText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_REPLICA_LIST);
        if (str != null && str.trim().length() > 0) {
            haReplicaListText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_DB_LIST);
        if (str != null && str.trim().length() > 0) {
            haDbListText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_APPLY_MAX_MEM_SIZE);
        if (str != null && str.trim().length() > 0) {
            haApplyMaxMemText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_COPY_SYNC_MODE);
        if (str != null && str.trim().length() > 0) {
            haCopySyncModeText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_PING_HOSTS);
        if (str != null && str.trim().length() > 0) {
            haPingHostText.setText(str);
        }

        str = paraMap.get(ConfConstants.HA_COPY_LOG_BASE);
        if (str != null && str.trim().length() > 0) {
            haCopyLogBaseText.setText(str);
        }
    }

    /**
     * When modify,check the validation
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        String port = haPortText.getText();
        boolean isValidPort = ValidateUtil.isInteger(port);
        if (isValidPort) {
            int intValue = Integer.parseInt(port);
            if (intValue > 65535 || intValue < 1024) {
                isValidPort = false;
            }
        }
        if (!isValidPort) {
            setErrorMessage(Messages.errHaPortId);
            setValid(false);
            return;
        }
        String maxMem = haApplyMaxMemText.getText();
        boolean isValidMem = ValidateUtil.isInteger(maxMem);
        if (isValidMem) {
            int intValue = Integer.parseInt(maxMem);
            if (intValue < 1) {
                isValidMem = false;
            }
        }
        if (!isValidMem) {
            setErrorMessage(Messages.errHAMemSize);
            setValid(false);
            return;
        }
        boolean isValid = isValidPort && isValidMem;
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
        if (haModeCombo == null || haModeCombo.isDisposed()) {
            return true;
        }
        if (!isAdmin) {
            return true;
        }

        Map<String, Map<String, String>> confParaMap = new HashMap<String, Map<String, String>>();
        Map<String, String> commonMap = initialValueMap.get(ConfConstants.COMMON_SECTION);
        if (commonMap == null) {
            commonMap = new HashMap<String, String>();
        }
        confParaMap.put(ConfConstants.COMMON_SECTION, commonMap);

        String haMode = haModeCombo.getText();
        if (isChanged(ConfConstants.HA_MODE, haMode)) {
            commonMap.put(ConfConstants.HA_MODE, haMode);
            isChanged = true;
        }

        String haPort = haPortText.getText();
        if (isChanged(ConfConstants.HA_PORT_ID, haPort)) {
            commonMap.put(ConfConstants.HA_PORT_ID, haPort);
            isChanged = true;
        }

        String haNodeList = haNodeListText.getText();
        if (isChanged(ConfConstants.HA_NODE_LIST, haNodeList)) {
            commonMap.put(ConfConstants.HA_NODE_LIST, haNodeList);
            isChanged = true;
        }

        String haReplicaList = haReplicaListText.getText();
        if (isChanged(ConfConstants.HA_REPLICA_LIST, haReplicaList)) {
            commonMap.put(ConfConstants.HA_REPLICA_LIST, haReplicaList);
            isChanged = true;
        }

        String haDbList = haDbListText.getText();
        if (isChanged(ConfConstants.HA_DB_LIST, haDbList)) {
            commonMap.put(ConfConstants.HA_DB_LIST, haDbList);
            isChanged = true;
        }

        String haApplyMaxMem = haApplyMaxMemText.getText();
        if (isChanged(ConfConstants.HA_APPLY_MAX_MEM_SIZE, haApplyMaxMem)) {
            commonMap.put(ConfConstants.HA_APPLY_MAX_MEM_SIZE, haApplyMaxMem);
            isChanged = true;
        }

        String haCopySyncMode = haCopySyncModeText.getText();
        if (isChanged(ConfConstants.HA_COPY_SYNC_MODE, haCopySyncMode)) {
            commonMap.put(ConfConstants.HA_COPY_SYNC_MODE, haCopySyncMode);
            isChanged = true;
        }

        String haPingHost = haPingHostText.getText();
        if (isChanged(ConfConstants.HA_PING_HOSTS, haPingHost)) {
            commonMap.put(ConfConstants.HA_PING_HOSTS, haPingHost);
            isChanged = true;
        }

        String haCopyLogBase = haCopyLogBaseText.getText();
        if (isChanged(ConfConstants.HA_COPY_LOG_BASE, haCopyLogBase)) {
            commonMap.put(ConfConstants.HA_COPY_LOG_BASE, haCopyLogBase);
            isChanged = true;
        }

        int count = hostUserTable.getItemCount();
        int initialCount =
                initialValueMap.size()
                        - (initialValueMap.get(ConfConstants.COMMON_SECTION) == null ? 0 : 1);
        if (count != initialCount) {
            isChanged = true;
        }
        for (int i = 0; i < count; i++) {
            TableItem item = hostUserTable.getItem(i);
            String hostName = item.getText(0);
            String userName = item.getText(1);
            String syncMode = item.getText(2);
            Map<String, String> hostUserMap = new HashMap<String, String>();
            String key = "[%" + hostName + "|" + userName + "]";
            hostUserMap.put(ConfConstants.HA_COPY_SYNC_MODE, syncMode);
            confParaMap.put(key, hostUserMap);

            if (initialValueMap.get(key) == null
                    || !syncMode.equals(
                            initialValueMap.get(key).get(ConfConstants.HA_COPY_SYNC_MODE))) {
                isChanged = true;
            }
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
    private void perform(Map<String, Map<String, String>> confParaMap) {
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setHAConfTaskName);
        SetHAConfParameterTask task = new SetHAConfParameterTask(node.getServer().getServerInfo());
        task.setConfParameters(confParaMap);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(Messages.titleSuccess, Messages.msgSetHAConfSuccess);
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
        Map<String, String> map = initialValueMap.get(ConfConstants.COMMON_SECTION);
        String initialValue = map == null ? null : map.get(paraName);
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
