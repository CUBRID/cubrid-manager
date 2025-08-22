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

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
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
 * * A dialog is used to set if shows the column that represent the brokers status
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-3 created by lizhiqiang
 */
public class BrokerEnvStatusSettingDlg extends CMTitleAreaDialog {
    private Button[] columnBtns;
    private Button saveBtn;

    public BrokerEnvStatusSettingDlg(Shell parentShell) {
        super(parentShell);
        columnBtns = new Button[BrokerEnvStatusColumn.values().length];
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
        Group group = new Group(composite, SWT.SHADOW_ETCHED_OUT);
        group.setText(Messages.txtGrpColumnSetting);
        GridLayout layout = new GridLayout(4, true);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        for (BrokerEnvStatusColumn column : BrokerEnvStatusColumn.values()) {
            columnBtns[column.ordinal()] = new Button(group, SWT.CHECK);
            columnBtns[column.ordinal()].setText(column.toString());
            if (column.getValue() != -1) {
                columnBtns[column.ordinal()].setSelection(true);
            }
            switch (column) {
                case NAME:
                case STATUS:
                    columnBtns[column.ordinal()].setEnabled(false);
                    break;
                default:
            }
            columnBtns[column.ordinal()].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
        saveBtn = new Button(composite, SWT.CHECK);
        saveBtn.setText(Messages.btnSaveBrokerEnvColumnSetting);
        return parent;
    }

    /** Set the title of dialog */
    private void makeTitle() {
        setMessage(Messages.msgBrokerEnvColumnSetting);
        setTitle(Messages.ttlBrokerEnvColumnSetting);
        getShell().setText(Messages.shellBrokerEnvColumnSetting);
    }

    /** Notifies that the ok button of this dialog has been pressed. */
    protected void okPressed() {
        int count = -1;
        for (Button button : columnBtns) {
            BrokerEnvStatusColumn column = BrokerEnvStatusColumn.valueOf(button.getText());
            if (button.getSelection()) {
                count++;
                column.setValue(count);
            } else {
                column.setValue(-1);
            }
        }
        if (saveBtn.getSelection()) {
            BrokerTblColumnSetHelp bcsh = BrokerTblColumnSetHelp.getInstance();
            bcsh.saveSetting(
                    BrokerTblColumnSetHelp.StatusColumn.BrokerEnvStatusColumn,
                    BrokerEnvStatusColumn.values());
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
}
