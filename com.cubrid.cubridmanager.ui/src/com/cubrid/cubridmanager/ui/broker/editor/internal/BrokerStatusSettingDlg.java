/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.broker.editor.internal;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.broker.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * * A dialog is used to set if shows the columns that represent the broker apply server and broker
 * job queue
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-3 created by lizhiqiang
 */
public class BrokerStatusSettingDlg extends CMTitleAreaDialog {

    private Button[] basicColBtns, asColBtns, jqColBtns;
    private ServerInfo serverInfo;
    private Button saveBtn;
    private boolean isSupportNewBrokerParamPropery;
    private boolean isAppendDiag;

    public BrokerStatusSettingDlg(Shell parentShell) {
        super(parentShell);
        basicColBtns = new Button[BrokerStatusBasicColumn.values().length];
        asColBtns = new Button[BrokerStatusAsColumn.values().length];
        jqColBtns = new Button[BrokerStatusJqColumn.values().length];
    }

    /**
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        makeTitle();
        Composite parentComp = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parentComp, SWT.RESIZE);
        GridLayout compLayout = new GridLayout();
        compLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        compLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        compLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        compLayout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(compLayout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // TODO
        //		getHelpSystem().setHelp(parentComp,
        //				CubridManagerHelpContextIDs.DATABASE_JOBAUTO);
        if (isSupportNewBrokerParamPropery) {
            createBasicGroup(composite);
        }
        createAsGroup(composite);
        createJqGroup(composite);
        saveBtn = new Button(composite, SWT.CHECK);
        saveBtn.setText(Messages.btnSaveBrokerColumnSetting);
        return parent;
    }

    /** Set the title of dialog */
    private void makeTitle() {
        setMessage(Messages.msgBrokerColumnSetting);
        setTitle(Messages.ttlBrokerColumnSetting);
        getShell().setText(Messages.shellBrokerColumnSetting);
    }

    /**
     * create a group that represent the info of apply server
     *
     * @param composite the parent composite
     */
    private void createBasicGroup(Composite composite) {

        Group basicGroup = new Group(composite, SWT.SHADOW_ETCHED_OUT);
        basicGroup.setText(Messages.txtBasicGrpColumnSetting);
        GridLayout basicLayout = new GridLayout(4, true);
        basicGroup.setLayout(basicLayout);
        basicGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        for (BrokerStatusBasicColumn column : BrokerStatusBasicColumn.values()) {
            if (!isAppendDiag
                    && (column == BrokerStatusBasicColumn.ACCESS_SESSION
                            || column == BrokerStatusBasicColumn.SESSION
                            || column == BrokerStatusBasicColumn.TPS)) {
                continue;
            }
            basicColBtns[column.ordinal()] = new Button(basicGroup, SWT.CHECK);
            basicColBtns[column.ordinal()].setText(column.toString());

            if (column.getValue() != -1) {
                basicColBtns[column.ordinal()].setSelection(true);
            }
            switch (column) {
                case PID:
                case PORT:
                    basicColBtns[column.ordinal()].setEnabled(false);
                    break;
                default:
            }
            basicColBtns[column.ordinal()].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
    }

    /**
     * create a group that represent the basic info
     *
     * @param composite the parent composite
     */
    private void createAsGroup(final Composite composite) {
        Group asGroup = new Group(composite, SWT.SHADOW_ETCHED_OUT);
        asGroup.setText(Messages.txtAsGrpColumnSetting);
        GridLayout asLayout = new GridLayout(4, true);
        asGroup.setLayout(asLayout);
        asGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        for (BrokerStatusAsColumn column : BrokerStatusAsColumn.values()) {
            if (!CompatibleUtil.isSupportBrokerPort(serverInfo)
                    && column == BrokerStatusAsColumn.PORT) {
                continue;
            }
            asColBtns[column.ordinal()] = new Button(asGroup, SWT.CHECK);
            asColBtns[column.ordinal()].setText(column.toString());

            if (column.getValue() != -1) {
                asColBtns[column.ordinal()].setSelection(true);
            }
            switch (column) {
                case ID:
                case PID:
                    asColBtns[column.ordinal()].setEnabled(false);
                    break;
                default:
            }
            asColBtns[column.ordinal()].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
    }

    /**
     * create a group that represent the info of Job Queue
     *
     * @param composite the parent composite
     */
    private void createJqGroup(Composite composite) {
        Group jqGroup = new Group(composite, SWT.SHADOW_ETCHED_OUT);
        jqGroup.setText(Messages.txtJqGrpColumnSetting);
        GridLayout jqLayout = new GridLayout(4, true);
        jqGroup.setLayout(jqLayout);
        jqGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        for (BrokerStatusJqColumn column : BrokerStatusJqColumn.values()) {
            jqColBtns[column.ordinal()] = new Button(jqGroup, SWT.CHECK);
            jqColBtns[column.ordinal()].setText(column.toString());
            if (column.getValue() != -1) {
                jqColBtns[column.ordinal()].setSelection(true);
            }
            switch (column) {
                case ID:
                case PRIORITY:
                    jqColBtns[column.ordinal()].setEnabled(false);
                    break;
                default:
            }
            jqColBtns[column.ordinal()].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
    }

    /** Notifies that the ok button of this dialog has been pressed. */
    protected void okPressed() {
        // basic
        if (isSupportNewBrokerParamPropery) {
            int countBsc = -1;
            for (Button button : basicColBtns) {
                if (button == null) {
                    continue;
                }
                BrokerStatusBasicColumn column = BrokerStatusBasicColumn.valueOf(button.getText());
                if (button.getSelection()) {
                    column.setValue(++countBsc);
                } else {
                    column.setValue(-1);
                }
            }
        }
        // apply server
        int countAs = -1;
        for (Button button : asColBtns) {
            if (button == null) {
                continue;
            }
            BrokerStatusAsColumn column = BrokerStatusAsColumn.valueOf(button.getText());
            if (button.getSelection()) {
                column.setValue(++countAs);
            } else {
                column.setValue(-1);
            }
        }
        // job queue
        int countJq = -1;
        for (Button button : jqColBtns) {
            BrokerStatusJqColumn column = BrokerStatusJqColumn.valueOf(button.getText());
            if (button.getSelection()) {
                column.setValue(++countJq);
            } else {
                column.setValue(-1);
            }
        }
        // save button
        if (saveBtn.getSelection()) {
            BrokerTblColumnSetHelp bcsh = BrokerTblColumnSetHelp.getInstance();
            if (isSupportNewBrokerParamPropery) {
                bcsh.saveSetting(
                        BrokerTblColumnSetHelp.StatusColumn.BrokerStatusBasicColumn,
                        BrokerStatusBasicColumn.values());
            }
            bcsh.saveSetting(
                    BrokerTblColumnSetHelp.StatusColumn.BrokerStatusAsColumn,
                    BrokerStatusAsColumn.values());
            bcsh.saveSetting(
                    BrokerTblColumnSetHelp.StatusColumn.BrokerStatusJqColumn,
                    BrokerStatusJqColumn.values());
        }
        setReturnCode(OK);
        close();
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /** @param serverInfo the serverInfo to set */
    public void setServerInfo(ServerInfo serverInfo) {
        // serverInfo.compareVersionKey("8.2.2");
        isSupportNewBrokerParamPropery = CompatibleUtil.isSupportNewBrokerParamPropery1(serverInfo);
        this.serverInfo = serverInfo;
    }

    /**
     * Get the isAppendDiag
     *
     * @return the isAppendDiag
     */
    public boolean isAppendDiag() {
        return isAppendDiag;
    }

    /** @param isAppendDiag the isAppendDiag to set */
    public void setAppendDiag(boolean isAppendDiag) {
        this.isAppendDiag = isAppendDiag;
    }
}
