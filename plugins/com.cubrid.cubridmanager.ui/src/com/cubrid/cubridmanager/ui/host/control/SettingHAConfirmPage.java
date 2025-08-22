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
package com.cubrid.cubridmanager.ui.host.control;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMWizardPage;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
import java.util.Map;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Setting HA Confirm Page
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-12-11 created by Kevin.Wang
 */
public class SettingHAConfirmPage extends CMWizardPage {
    private static final String PAGE_NAME = SettingHAConfirmPage.class.getName();

    private Text text;

    protected SettingHAConfirmPage() {
        super(PAGE_NAME);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());
        setControl(container);
        setDescription(Messages.descSettingConfirmPage);

        Composite composite = new Composite(container, SWT.None);
        composite.setLayout(new GridLayout(1, false));

        text = new Text(composite, SWT.MULTI | SWT.BORDER);
        text.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        text.setEditable(false);
    }

    private void initData() {
        text.setText(getConfirmInfo());
    }

    private String getConfirmInfo() {
        StringBuilder sb = new StringBuilder();

        HAModel haModel = getConfigHAWizard().getHaModel();
        sb.append(
                        Messages.bind(
                                Messages.txtServerConfirm,
                                haModel.getMasterServer().getServer().getServerName()
                                        + "@"
                                        + haModel.getMasterServer().getHostName(),
                                haModel.getSlaveServer().getServer().getServerName()
                                        + "@"
                                        + haModel.getSlaveServer().getHostName()))
                .append(StringUtil.NEWLINE);

        String masterCubrid = getCubridInfo(haModel.getMasterServer());
        if (masterCubrid.length() > 0) {
            sb.append(
                            Messages.bind(
                                    Messages.txtModifyCubridConf,
                                    haModel.getMasterServer().getServer().getServerName()))
                    .append(StringUtil.NEWLINE);
            sb.append(masterCubrid).append(StringUtil.NEWLINE);
        }

        String slaveCubrid = getCubridInfo(haModel.getSlaveServer());
        if (slaveCubrid.length() > 0) {
            sb.append(
                            Messages.bind(
                                    Messages.txtModifyCubridConf,
                                    haModel.getSlaveServer().getServer().getServerName()))
                    .append(StringUtil.NEWLINE);
            sb.append(slaveCubrid).append(StringUtil.NEWLINE);
        }

        String masterCubridHA = getCubridHAInfo(haModel.getMasterServer());
        if (masterCubridHA.length() > 0) {
            sb.append(
                            Messages.bind(
                                    Messages.txtModifyCubridHAConf,
                                    haModel.getMasterServer().getServer().getServerName()))
                    .append(StringUtil.NEWLINE);
            sb.append(masterCubridHA).append(StringUtil.NEWLINE);
        }

        String slaveCubridHA = getCubridHAInfo(haModel.getSlaveServer());
        if (slaveCubridHA.length() > 0) {
            sb.append(
                            Messages.bind(
                                    Messages.txtModifyCubridHAConf,
                                    haModel.getSlaveServer().getServer().getServerName()))
                    .append(StringUtil.NEWLINE);
            sb.append(slaveCubridHA).append(StringUtil.NEWLINE);
        }

        return sb.toString();
    }

    private String getCubridInfo(HAServer haServer) {
        StringBuilder sb = new StringBuilder();

        StringBuilder add = new StringBuilder();
        for (String key : haServer.getCubridParameters().keySet()) {
            Map<String, String> dataMap = haServer.getCubridParameters().get(key);
            Map<String, String> originMap = haServer.getOriginCubridParameters().get(key);
            for (String p : dataMap.keySet()) {
                String value = dataMap.get(p);

                if (originMap == null || originMap.get(p) == null) {
                    appendParameterInfo(key, p, null, value, add);
                }
            }
        }

        if (add.length() > 0) {
            sb.append(Messages.txtAddParameters).append(StringUtil.NEWLINE);
            sb.append(add.toString());
        }

        StringBuilder modify = new StringBuilder();
        for (String key : haServer.getCubridParameters().keySet()) {
            Map<String, String> dataMap = haServer.getCubridParameters().get(key);
            Map<String, String> originMap = haServer.getOriginCubridParameters().get(key);
            for (String p : dataMap.keySet()) {
                if (originMap != null
                        && originMap.get(p) != null
                        && !originMap.get(p).equals(dataMap.get(p))) {
                    appendParameterInfo(key, p, originMap.get(p), dataMap.get(p), modify);
                }
            }
        }

        if (modify.length() > 0) {
            sb.append(Messages.txtModifyParameters).append(StringUtil.NEWLINE);
            sb.append(modify.toString());
        }

        StringBuilder delete = new StringBuilder();
        for (String key : haServer.getOriginCubridParameters().keySet()) {
            Map<String, String> dataMap = haServer.getCubridParameters().get(key);
            Map<String, String> originMap = haServer.getOriginCubridParameters().get(key);
            for (String p : originMap.keySet()) {
                if (dataMap == null || dataMap.get(p) == null) {
                    appendParameterInfo(key, p, originMap.get(p), null, delete);
                }
            }
        }

        if (delete.length() > 0) {
            sb.append(Messages.txtDeleteParameters).append(StringUtil.NEWLINE);
            sb.append(delete.toString());
        }

        return sb.toString();
    }

    private void appendParameterInfo(
            String sectionName,
            String key,
            String originValue,
            String newValue,
            StringBuilder buffer) {
        if (originValue == null && newValue != null) {
            buffer.append("\t")
                    .append(sectionName)
                    .append("\t")
                    .append(key)
                    .append("\t")
                    .append(newValue)
                    .append(StringUtil.NEWLINE);
        } else if (originValue != null && newValue != null) {
            buffer.append("\t")
                    .append(sectionName)
                    .append("\t")
                    .append(key)
                    .append("\t")
                    .append(originValue)
                    .append(" -----> ")
                    .append(newValue)
                    .append(StringUtil.NEWLINE);
        } else if (originValue != null && newValue == null) {
            buffer.append("\t")
                    .append(sectionName)
                    .append("\t")
                    .append(key)
                    .append("\t")
                    .append(originValue)
                    .append(StringUtil.NEWLINE);
        }
    }

    private String getCubridHAInfo(HAServer haServer) {
        StringBuilder sb = new StringBuilder();

        StringBuilder add = new StringBuilder();
        for (String key : haServer.getCubridHAParameters().keySet()) {
            Map<String, String> dataMap = haServer.getCubridHAParameters().get(key);
            Map<String, String> originMap = haServer.getOrigincCubridHAParameters().get(key);
            for (String p : dataMap.keySet()) {
                String value = dataMap.get(p);

                if (originMap == null || originMap.get(p) == null) {
                    appendParameterInfo(key, p, null, value, add);
                }
            }
        }

        if (add.length() > 0) {
            sb.append(Messages.txtAddParameters).append(StringUtil.NEWLINE);
            sb.append(add.toString());
        }

        StringBuilder modify = new StringBuilder();
        for (String key : haServer.getCubridHAParameters().keySet()) {
            Map<String, String> dataMap = haServer.getCubridHAParameters().get(key);
            Map<String, String> originMap = haServer.getOrigincCubridHAParameters().get(key);
            for (String p : dataMap.keySet()) {
                if (originMap != null
                        && originMap.get(p) != null
                        && !originMap.get(p).equals(dataMap.get(p))) {
                    appendParameterInfo(key, p, originMap.get(p), dataMap.get(p), modify);
                }
            }
        }

        if (modify.length() > 0) {
            sb.append(Messages.txtModifyParameters).append(StringUtil.NEWLINE);
            sb.append(modify.toString());
        }

        StringBuilder delete = new StringBuilder();
        for (String key : haServer.getOrigincCubridHAParameters().keySet()) {
            Map<String, String> dataMap = haServer.getCubridHAParameters().get(key);
            Map<String, String> originMap = haServer.getOrigincCubridHAParameters().get(key);
            for (String p : originMap.keySet()) {
                if (dataMap == null || dataMap.get(p) == null) {
                    appendParameterInfo(key, p, originMap.get(p), null, delete);
                }
            }
        }

        if (delete.length() > 0) {
            sb.append(Messages.txtDeleteParameters).append(StringUtil.NEWLINE);
            sb.append(delete.toString());
        }

        return sb.toString();
    }

    private ConfigHAWizard getConfigHAWizard() {
        return (ConfigHAWizard) getWizard();
    }

    protected void handlePageLeaving(PageChangingEvent event) {
        super.handlePageLeaving(event);
    }

    protected void handlePageShowing(PageChangingEvent event) {
        initData();
        setTitle(Messages.haStep4);
    }
}
