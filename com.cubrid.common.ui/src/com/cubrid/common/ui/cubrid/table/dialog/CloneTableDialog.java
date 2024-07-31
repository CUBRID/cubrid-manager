/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Clone Table Dialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-07-31 created by Kevin.Wang
 */
public class CloneTableDialog extends CMTitleAreaDialog {

    private List<String> existNameList;
    private Text targetNameText;
    private String targetName;
    private String originTableName;

    public CloneTableDialog(Shell parentShell, List<String> nameList, String originTableName) {
        super(parentShell);
        this.existNameList = nameList;
        this.originTableName = originTableName;
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label label1 = new Label(composite, SWT.LEFT);
        label1.setText(Messages.lblTargetName);
        label1.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        targetNameText = new Text(composite, SWT.BORDER);
        targetNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        if (originTableName != null) {
            targetNameText.setText(originTableName);
        }
        targetNameText.setFocus();
        targetNameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        setErrorMessage(null);
                        getButton(IDialogConstants.OK_ID).setEnabled(false);
                        String newTable = targetNameText.getText();
                        if (!ValidateUtil.isValidIdentifier(newTable)) {
                            setErrorMessage(
                                    Messages.bind(
                                            Messages.errInvalidName, targetNameText.getText()));
                            return;
                        }
                        if (existNameList.indexOf(newTable.toLowerCase(Locale.getDefault()))
                                != -1) {
                            setErrorMessage(Messages.bind(Messages.errExistTable, newTable));
                            return;
                        }
                        getButton(IDialogConstants.OK_ID).setEnabled(true);
                    }
                });
        setTitle(Messages.titleCloneTable);
        setMessage(Messages.msgCloneTable);
        return parent;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleCloneTable);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.renameOKBTN, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.renameCancelBTN, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            targetName = targetNameText.getText();
        }
        super.buttonPressed(buttonId);
    }

    public String getTargetName() {
        return targetName;
    }
}
