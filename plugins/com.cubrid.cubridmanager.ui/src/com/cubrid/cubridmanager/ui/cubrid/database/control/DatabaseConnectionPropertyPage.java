/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * query options page for database node
 *
 * @author wangsl 2009-5-11
 */
public class DatabaseConnectionPropertyPage extends PreferencePage
        implements IWorkbenchPreferencePage {
    private Combo brokerPortCombo;
    private final CubridDatabase database;
    private Combo charsetCombo;
    private String oldBrokerPort = "";
    private String oldCharSet = "";
    private String oldBrokerIp = "";
    private Text brokerIpText = null;

    public DatabaseConnectionPropertyPage(CubridDatabase database, String name) {
        super(name);
        noDefaultAndApplyButton();
        this.database = database;
    }

    public boolean performOk() {
        if (charsetCombo == null || charsetCombo.isDisposed()) {
            return true;
        }
        if (!checkValid()) {
            return false;
        }
        boolean isChanged = false;
        if (!brokerIpText.getText().equals(oldBrokerIp)) {
            isChanged = true;
        }
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
                brokerPort = text;
            }
            if (brokerPort != null && !brokerPort.equals(this.oldBrokerPort)) {
                isChanged = true;
            }
        }
        String charset = charsetCombo.getText();
        if (!charset.equals(this.oldCharSet)) {
            isChanged = true;
        }
        if (!isChanged) {
            return true;
        }
        // check the query editor in this database
        if (!LayoutUtil.checkAllQueryEditor(database)) {
            return false;
        }

        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        QueryOptions.setCharset(databaseInfo, charset);

        if (brokerPort != null) {
            QueryOptions.setBrokerPort(databaseInfo, brokerPort);
        }
        QueryOptions.setBrokerIp(databaseInfo, brokerIpText.getText());
        QueryOptions.savePref();

        CubridNodeManager.getInstance()
                .fireCubridNodeChanged(
                        new CubridNodeChangedEvent(
                                database, CubridNodeChangedEventType.NODE_REFRESH));

        return super.performOk();
    }

    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Group group = new Group(top, SWT.NONE);
        group.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        group.setLayout(layout);

        final Label labelBrokerIp = new Label(group, SWT.NONE);
        labelBrokerIp.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        labelBrokerIp.setText(Messages.brokerIP);

        brokerIpText = new Text(group, SWT.BORDER);
        brokerIpText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        final Label labelBrokerPort = new Label(group, SWT.NONE);
        labelBrokerPort.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        labelBrokerPort.setText(Messages.brokerPort);

        brokerPortCombo = new Combo(group, SWT.NONE);
        brokerPortCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
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

        Label charSetLbl = new Label(group, SWT.CHECK);
        charSetLbl.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        charSetLbl.setText(Messages.lblCharSet);

        charsetCombo = new Combo(group, SWT.BORDER);
        charsetCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        loadPreference();

        return top;
    }

    /** load the preference data. */
    private void loadPreference() {
        if (database == null) {
            return;
        }

        String brokerIp = QueryOptions.getBrokerIp(database.getDatabaseInfo());
        if (brokerIp == null || brokerIp.trim().length() == 0) {
            brokerIp = database.getServer().getHostAddress();
        }
        this.oldBrokerIp = brokerIp;
        brokerIpText.setText(brokerIp);

        String brokerPort = QueryOptions.getBrokerPort(database.getDatabaseInfo());
        this.oldBrokerPort = brokerPort;
        String text = (String) brokerPortCombo.getData(brokerPort);
        if (text != null) {
            brokerPortCombo.setText(text);
        }
        if (text == null && brokerPort != null) {
            brokerPortCombo.setText(brokerPort);
        }

        ServerUserInfo userInfo = database.getServer().getServerInfo().getLoginedUserInfo();
        if (!userInfo.isAdmin()) {
            brokerPortCombo.setEnabled(false);
            brokerIpText.setEnabled(false);
        }

        String charset = QueryOptions.getCharset(database.getDatabaseInfo());
        this.oldCharSet = charset;
        charsetCombo.setItems(QueryOptions.getAllCharset(charset));
        if (charset != null && charset.trim().length() > 0) {
            charsetCombo.setText(charset);
        } else {
            charsetCombo.select(0);
        }
    }

    /**
     * validate the data
     *
     * @return boolean
     */
    public boolean checkValid() {
        String brokerIp = brokerIpText.getText();
        if (brokerIp == null || brokerIp.trim().length() <= 0) {
            CommonUITool.openErrorBox(Messages.errInvalidBrokerIp);
            return false;
        }
        String borkerPort = brokerPortCombo.getText();
        if (borkerPort == null || borkerPort.trim().length() <= 0) {
            CommonUITool.openErrorBox(Messages.errInvalidBrokerPort);
            return false;
        }
        String charset = charsetCombo.getText();
        if (charset == null || charset.trim().length() <= 0) {
            CommonUITool.openErrorBox(Messages.errInvalidCharSet);
            return false;
        }
        return true;
    }

    public void init(IWorkbench workbench) {}
}
