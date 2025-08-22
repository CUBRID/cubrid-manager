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
package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.ui.host.Messages;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UserPasswordInputDialog extends CMTitleAreaDialog {

    public static void main(String[] args) {
        Display display = Display.getDefault();
        UserPasswordInputDialog dialog = new UserPasswordInputDialog(display.getActiveShell());
        dialog.open();
    }

    private Text userName;
    private Text userPassword;
    private Combo jdbcDriver;

    public UserPasswordInputDialog(Shell parentShell) {
        super(parentShell);
    }

    public UserPasswordInputDialog(Shell parentShell, String title) {
        this(parentShell);
        if (title != null) setTitle(title);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        Composite newComposite = new Composite(composite, SWT.NONE);
        newComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        newComposite.setLayout(layout);

        setTitle(Messages.titleUserPassword);
        setMessage(Messages.msgInputNamePassword, IMessageProvider.INFORMATION);

        Label lblUserName = new Label(newComposite, SWT.NONE);
        lblUserName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblUserName.setText(Messages.lblUserName);

        userName = new Text(newComposite, SWT.BORDER);
        userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        userName.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        validateInput();
                    }
                });
        Label lblPassword = new Label(newComposite, SWT.NONE);
        lblPassword.setText(Messages.lblPassword);
        lblPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        userPassword = new Text(newComposite, SWT.BORDER | SWT.PASSWORD);
        userPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        userPassword.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        validateInput();
                    }
                });

        Label lblJdbcDriver = new Label(newComposite, SWT.NONE);
        lblJdbcDriver.setText(Messages.lblJdbcVersion);
        lblJdbcDriver.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        jdbcDriver = new Combo(newComposite, SWT.BORDER);
        jdbcDriver.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        jdbcDriver.add("Auto Detect", 0);
        initJdbcDriver();
        jdbcDriver.select(0);

        applyDialogFont(newComposite);
        return composite;
    }

    private void initJdbcDriver() {
        Map<String, String> driverMap = CubridJdbcManager.getInstance().getLoadedJdbc();
        if (driverMap.size() == 0) {
            jdbcDriver.removeAll();
            setErrorMessage(Messages.msgInvalidJdbcDriver);
            return;
        }

        for (String key : CubridJdbcManager.getInstance().getLoadedJdbc().keySet()) {
            jdbcDriver.add(key);
        }
    }

    private void setEnabled(boolean b) {
        getButton(IDialogConstants.OK_ID).setEnabled(b);
    }

    protected void validateInput() {
        String name = userName.getText();
        boolean isValidUserName =
                name.indexOf(" ") < 0
                        && name.trim().length() >= 4
                        && name.trim().length() <= ValidateUtil.MAX_NAME_LENGTH;
        if (!isValidUserName) {
            setErrorMessage(Messages.errUserName);
            setEnabled(false);
            return;
        }

        String password = userPassword.getText();
        if (password.trim().length() == 0) {
            setErrorMessage(Messages.errUserPassword);
            setEnabled(false);
            return;
        }

        setErrorMessage(null);
        setEnabled(true);
    }

    private String userNameVal;
    private String userPasswordVal;
    private String jdbcVersion;

    protected void buttonPressed(int buttonId) {
        this.userNameVal = userName.getText();
        this.userPasswordVal = userPassword.getText();
        this.jdbcVersion = jdbcDriver.getText();
        Map<String, String> jdbcVersionMap = CubridJdbcManager.getInstance().getLoadedJdbc();
        if (jdbcVersionMap == null
                || (!jdbcVersion.equals("Auto Detect")
                        && jdbcVersionMap.get(jdbcVersion) == null)) {
            setErrorMessage(Messages.msgInvalidJdbcDriver);
            return;
        }

        super.buttonPressed(buttonId);
    }

    public String getUserName() {
        return this.userNameVal;
    }

    public String getUserPassword() {
        return this.userPasswordVal;
    }

    public String getJdbcVersion() {
        return this.jdbcVersion;
    }
}
