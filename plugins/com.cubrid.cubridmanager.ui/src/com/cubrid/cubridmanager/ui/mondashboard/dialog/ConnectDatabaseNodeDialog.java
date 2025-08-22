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
package com.cubrid.cubridmanager.ui.mondashboard.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Connect database node dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-25 created by pangqiren
 */
public class ConnectDatabaseNodeDialog extends CMTitleAreaDialog {
    private Text userNameText = null;
    private Text passwordText = null;
    private String userName;
    private String password;
    private String description;

    /**
     * The constructor
     *
     * @param parentShell
     * @param userName
     */
    public ConnectDatabaseNodeDialog(Shell parentShell, String userName) {
        super(parentShell);
        this.userName = userName;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label userNameLabel = new Label(composite, SWT.LEFT);
        userNameLabel.setText(Messages.lblDbUserName);
        userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        userNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        userNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        if (this.userName != null) {
            userNameText.setText(this.userName);
            userNameText.setEnabled(false);
        }
        Label passwordLabel = new Label(composite, SWT.LEFT);
        passwordLabel.setText(Messages.lblDbPassword);
        passwordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        passwordText = new Text(composite, SWT.LEFT | SWT.PASSWORD | SWT.BORDER);
        passwordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
        passwordText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        passwordText.setFocus();

        setTitle(Messages.titleLoginDbDialog);
        if (description == null) {
            setMessage(Messages.msgLoginDbDialog);
        } else {
            setMessage(description);
        }
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(400, 240);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleLoginDbDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            userName = userNameText.getText();
            password = passwordText.getText();
        }
        super.buttonPressed(buttonId);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
