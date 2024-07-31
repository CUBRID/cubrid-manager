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
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Service Property page for cubrid.conf configuration file service section
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class ServicePropertyPage extends PreferencePage {

    private final ICubridNode node;
    private boolean isAdmin = false;
    private Button serverButton;
    private Button managerButton;
    private Button brokerButton;
    private Table databaseTable;
    private boolean isChanged = false;
    private boolean isApply = false;
    private final Map<String, Map<String, String>> initialValueMap =
            new HashMap<String, Map<String, String>>();

    /**
     * The constructor
     *
     * @param node
     * @param name
     */
    public ServicePropertyPage(ICubridNode node, String name) {
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

        createServiceGroup(composite);
        createDatabaseGroup(composite);
        initial();
        return composite;
    }

    /**
     * Create service group composite
     *
     * @param parent the parent composite
     */
    private void createServiceGroup(Composite parent) {
        Group serviceInfoGroup = new Group(parent, SWT.NONE);
        serviceInfoGroup.setText(Messages.grpService);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        serviceInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        serviceInfoGroup.setLayout(layout);

        serverButton = new Button(serviceInfoGroup, SWT.CHECK | SWT.LEFT);
        serverButton.setText("server");
        if (isAdmin) {
            serverButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            enableDatabaseTable();
                            if (!serverButton.getSelection()) {
                                for (int i = 0; i < databaseTable.getItemCount(); i++) {
                                    databaseTable.getItem(i).setChecked(false);
                                }
                            }
                        }
                    });
        } else {
            serverButton.setEnabled(false);
        }

        managerButton = new Button(serviceInfoGroup, SWT.CHECK | SWT.LEFT);
        managerButton.setText("manager");
        managerButton.setEnabled(false);

        brokerButton = new Button(serviceInfoGroup, SWT.CHECK | SWT.LEFT);
        brokerButton.setText("broker");
        if (!isAdmin) {
            brokerButton.setEnabled(false);
        }
    }

    /**
     * Create database group composite
     *
     * @param parent the parent composite
     */
    private void createDatabaseGroup(Composite parent) {
        Group databaseInfoGroup = new Group(parent, SWT.NONE);
        databaseInfoGroup.setText(Messages.grpAutoDatabase);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        databaseInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        databaseInfoGroup.setLayout(layout);

        databaseTable =
                new Table(
                        databaseInfoGroup,
                        SWT.CHECK
                                | SWT.V_SCROLL
                                | SWT.MULTI
                                | SWT.BORDER
                                | SWT.H_SCROLL
                                | SWT.FULL_SELECTION);
        databaseTable.setLinesVisible(false);
        databaseTable.setHeaderVisible(false);
        databaseTable.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 100));
    }

    /** initial the page content */
    private void initial() {
        Map<String, Map<String, String>> confParaMap =
                node.getServer().getServerInfo().getCubridConfParaMap();
        Map<String, String> map = null;
        if (confParaMap != null) {
            map = confParaMap.get(ConfConstants.SERVICE_SECTION_NAME);
        }
        if (map != null) {
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            Map<String, String> defaultMap = new HashMap<String, String>();
            initialValueMap.put(ConfConstants.SERVICE_SECTION_NAME, defaultMap);
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                defaultMap.put(key, entry.getValue());
            }
        }
        defaultValue();
        enableDatabaseTable();
    }

    /** Enable or disable database table according to serverButton checked status */
    private void enableDatabaseTable() {
        if (serverButton.getSelection()) {
            if (isAdmin) {
                databaseTable.setEnabled(true);
            } else {
                databaseTable.setEnabled(false);
            }
        } else {
            databaseTable.setEnabled(false);
        }
    }

    /** Restore the default value */
    private void defaultValue() {
        if (initialValueMap != null) {
            Map<String, String> map = initialValueMap.get(ConfConstants.SERVICE_SECTION_NAME);
            if (map == null) {
                return;
            }
            String service = map.get(ConfConstants.COMMON_SERVICE);
            if (service.indexOf("server") >= 0) {
                serverButton.setSelection(true);
            }
            if (service.indexOf("manager") >= 0) {
                managerButton.setSelection(true);
            }
            if (service.indexOf("broker") >= 0) {
                brokerButton.setSelection(true);
            }
            String databases = map.get(ConfConstants.COMMON_SERVER);
            String[] databasesList = null;
            if (databases != null) {
                databasesList = databases.split(",");
            }
            if (databaseTable != null) {
                databaseTable.removeAll();
            }
            if (node != null) {
                List<String> databaseList = node.getServer().getServerInfo().getAllDatabaseList();
                if (databaseList != null) {
                    for (int i = 0; i < databaseList.size(); i++) {
                        String databaseName = databaseList.get(i);
                        TableItem item = new TableItem(databaseTable, SWT.NONE);
                        item.setText(databaseName);
                        item.setChecked(isContainDatabase(databasesList, databaseName));
                    }
                }
            }
        }
    }

    /**
     * Check whether the database in database list
     *
     * @param databasesList the database array
     * @param databaseName the database name
     * @return <code>true</code> if it is contained;<code>false</code> otherwise
     */
    private boolean isContainDatabase(String[] databasesList, String databaseName) {
        if (databasesList == null || databasesList.length == 0) {
            return false;
        }
        for (String db : databasesList) {
            if (db.equals(databaseName)) {
                return true;
            }
        }
        return false;
    }

    /** Perform the default values */
    protected void performDefaults() {
        defaultValue();
        if (isApply) {
            perform(initialValueMap);
        }
        isApply = false;
        isChanged = false;
    }

    /**
     * Execute and save
     *
     * @return <code>true</code> if it is successful;<code>false</code> otherwise
     */
    public boolean performOk() {
        if (serverButton == null || serverButton.isDisposed()) {
            return true;
        }
        if (!isAdmin) {
            return true;
        }
        Map<String, Map<String, String>> confParaMap =
                node.getServer().getServerInfo().getCubridConfParaMap();
        Map<String, String> map = null;
        if (confParaMap != null) {
            map = confParaMap.get(ConfConstants.SERVICE_SECTION_NAME);
        }
        if (map == null) {
            map = new HashMap<String, String>();
            confParaMap.put(ConfConstants.SERVICE_SECTION_NAME, map);
        }
        String service = map.get(ConfConstants.COMMON_SERVICE);
        if (service == null) {
            service = "";
        }
        String databaseServers = map.get(ConfConstants.COMMON_SERVER);
        if (databaseServers == null) {
            databaseServers = "";
        }
        String serviceValue = "";
        if (serverButton.getSelection()) {
            serviceValue = "server";
        }
        if (managerButton.getSelection()) {
            if (serverButton.getSelection()) {
                serviceValue += ",";
            }
            serviceValue += "manager";
        }
        if (brokerButton.getSelection()) {
            if (serverButton.getSelection() || managerButton.getSelection()) {
                serviceValue += ",";
            }
            serviceValue += "broker";
        }
        int itemCount = databaseTable.getItemCount();
        StringBuffer databases = new StringBuffer();
        for (int i = 0; i < itemCount; i++) {
            TableItem item = databaseTable.getItem(i);
            if (item.getChecked()) {
                databases.append(item.getText()).append(",");
            }
        }
        if (databases.length() > 0) {
            databases = new StringBuffer(databases.substring(0, databases.lastIndexOf(",")));
        }
        isChanged = !compare(service, serviceValue);
        if (!isChanged) {
            isChanged = !compare(databaseServers, databases.toString());
        }
        if (!isChanged) {
            return true;
        }

        map.put(ConfConstants.COMMON_SERVICE, serviceValue);
        map.put(ConfConstants.COMMON_SERVER, databases.toString());
        perform(confParaMap);
        isApply = true;
        isChanged = false;
        return true;
    }

    /**
     * Perform the task and set cubrid.conf configuration file service section parameter
     *
     * @param confParaMap the conf parameter map
     */
    private void perform(Map<String, Map<String, String>> confParaMap) {
        SetCubridConfParameterTask task =
                new SetCubridConfParameterTask(node.getServer().getServerInfo());
        task.setConfParameters(confParaMap);
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setCubridParameterTaskName);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(
                    Messages.titleSuccess, Messages.msgChangeServiceParaSuccess);
        }
    }

    /**
     * Compare str
     *
     * @param str1 the str
     * @param str2 the str
     * @return <code>true</code> if
     */
    private boolean compare(String str1, String str2) { // FIXME extract to utility class
        String[] strArr1 = str1.split(",");
        String[] strArr2 = str2.split(",");
        if (strArr1.length != strArr2.length) {
            return false;
        }
        if (strArr1.length == 1 && strArr2.length == 1 && strArr1[0].equals(strArr2[0])) {
            return true;
        }
        for (int i = 0; i < strArr1.length; i++) {
            String value = strArr1[i];
            boolean isExistValue = false;
            for (int j = 0; j < strArr2.length; j++) {
                if (value.trim().equals(strArr2[j])) {
                    isExistValue = true;
                    break;
                }
            }
            if (!isExistValue) {
                return false;
            }
        }
        return true;
    }
}
