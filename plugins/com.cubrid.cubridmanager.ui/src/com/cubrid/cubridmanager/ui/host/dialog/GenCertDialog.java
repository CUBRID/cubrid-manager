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
package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CertStatus;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.GenerateCertificateTask;
import com.cubrid.cubridmanager.ui.host.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GenCertDialog extends CMTitleAreaDialog {

    private final String defaultCountry = "KR";
    private final String defaultState = "Kyunggi-do";
    private final String defaultCity = "Bundang-si";
    private final String defaultOrganization = "Search Solution Corporation";
    private final String defaultEmail = "contact@cubrid.org";
    private Color normalColor = null;
    private Color defaultColor = ResourceManager.getColor(SWT.COLOR_GRAY);

    private Text countryText;
    private Text stateText;
    private Text cityText;
    private Text organizationText;
    private Text emailText;
    private Combo dateCombo;
    private Button remButton;
    private ServerInfo serverInfo;

    private int[] validValues = {10000 * 365, 3 * 365, 1 * 365, 30, 7};
    private String[] validMessages = {
        Messages.lblValidNaverExpire,
        Messages.lblValidThreeYear,
        Messages.lblValidOneYear,
        Messages.lblValidOneMonth,
        Messages.lblValidOneWeek
    };

    /**
     * The constructor
     *
     * @param parentShell
     */
    public GenCertDialog(Shell parentShell, ServerInfo serverInfo) {
        super(parentShell);
        this.serverInfo = serverInfo;
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
        layout.numColumns = 1;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Group optionGroup = new Group(composite, SWT.None);
        optionGroup.setText(Messages.grpOption);
        optionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        optionGroup.setLayout(new GridLayout(3, false));

        Label countryLabel = new Label(optionGroup, SWT.None);
        countryLabel.setText(Messages.lblCountry);
        countryLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        countryText = new Text(optionGroup, SWT.BORDER);
        countryText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        normalColor = countryText.getForeground();

        countryText.setText(defaultCountry);
        countryText.setForeground(defaultColor);
        addFocusListener(countryText, defaultCountry);

        Label countryInfoLabel = new Label(optionGroup, SWT.None);
        countryInfoLabel.setText(Messages.lblOption);
        countryInfoLabel.setForeground(ResourceManager.getColor(SWT.COLOR_BLUE));
        countryInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Label stateLabel = new Label(optionGroup, SWT.None);
        stateLabel.setText(Messages.lblState);
        stateLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        stateText = new Text(optionGroup, SWT.BORDER);
        stateText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        stateText.setText(defaultState);
        stateText.setForeground(defaultColor);
        addFocusListener(stateText, defaultState);

        Label stateInfoLabel = new Label(optionGroup, SWT.None);
        stateInfoLabel.setText(Messages.lblOption);
        stateInfoLabel.setForeground(ResourceManager.getColor(SWT.COLOR_BLUE));
        stateInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Label cityLabel = new Label(optionGroup, SWT.None);
        cityLabel.setText(Messages.lblCity);
        cityLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        cityText = new Text(optionGroup, SWT.BORDER);
        cityText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        cityText.setText(defaultCity);
        cityText.setForeground(defaultColor);
        addFocusListener(cityText, defaultCity);

        Label cityInfoLabel = new Label(optionGroup, SWT.None);
        cityInfoLabel.setText(Messages.lblOption);
        cityInfoLabel.setForeground(ResourceManager.getColor(SWT.COLOR_BLUE));
        cityInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Label organizationLabel = new Label(optionGroup, SWT.None);
        organizationLabel.setText(Messages.lblOrganization);
        organizationLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        organizationText = new Text(optionGroup, SWT.BORDER);
        organizationText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        organizationText.setText(defaultOrganization);
        organizationText.setForeground(defaultColor);
        addFocusListener(organizationText, defaultOrganization);

        Label organizationInfoLabel = new Label(optionGroup, SWT.None);
        organizationInfoLabel.setText(Messages.lblOption);
        organizationInfoLabel.setForeground(ResourceManager.getColor(SWT.COLOR_BLUE));
        organizationInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Label emailLabel = new Label(optionGroup, SWT.None);
        emailLabel.setText(Messages.lblEmail);
        emailLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        emailText = new Text(optionGroup, SWT.BORDER);
        emailText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        emailText.setText(defaultEmail);
        emailText.setForeground(defaultColor);
        addFocusListener(emailText, defaultEmail);

        Label emailInfoLabel = new Label(optionGroup, SWT.None);
        emailInfoLabel.setText(Messages.lblOption);
        emailInfoLabel.setForeground(ResourceManager.getColor(SWT.COLOR_BLUE));
        emailInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Label validLabel = new Label(optionGroup, SWT.None);
        validLabel.setText(Messages.lblValid);
        validLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        dateCombo = new Combo(optionGroup, SWT.READ_ONLY);
        dateCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        dateCombo.setItems(validMessages);
        dateCombo.select(0);

        remButton = new Button(composite, SWT.CHECK);
        remButton.setText(Messages.btnRemember);
        remButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        setTitle(Messages.titleGenCertDialog);
        setMessage(Messages.msgGenCertDialog);

        return parentComp;
    }

    private void addFocusListener(final Text text, final String defaultValye) {
        text.addFocusListener(
                new FocusListener() {
                    public void focusLost(FocusEvent e) {
                        if (StringUtil.isEmpty(text.getText())
                                || StringUtil.isEqual(defaultValye, text.getText())) {
                            text.setText(defaultValye);
                            text.setForeground(defaultColor);
                        } else {
                            text.setForeground(normalColor);
                        }
                    }

                    public void focusGained(FocusEvent e) {
                        if (StringUtil.isEqual(defaultValye, text.getText())) {
                            text.setForeground(normalColor);
                            text.selectAll();
                        }
                    }
                });
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.TITLE | SWT.PRIMARY_MODAL;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(500, 430);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleGenCertDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnGenerate, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnNotGenerate, false);
    }

    /**
     * Call this method when button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.OK_ID == buttonId) {
            genCertFile();
        }

        if (remButton.getSelection()) {
            serverInfo.setCheckCertStatus(false);
        }

        super.buttonPressed(buttonId);
    }

    private void genCertFile() {
        GenerateCertificateTask task = new GenerateCertificateTask(serverInfo);
        if (!StringUtil.isEmpty(countryText.getText())) {
            task.setCName(countryText.getText());
        }
        if (!StringUtil.isEmpty(stateText.getText())) {
            task.setStName(stateText.getText());
        }

        if (!StringUtil.isEmpty(cityText.getText())) {
            task.setLonName(cityText.getText());
        }

        if (!StringUtil.isEmpty(organizationText.getText())) {
            task.setOrgname(organizationText.getText());
        }

        if (!StringUtil.isEmpty(emailText.getText())) {
            task.setEmail(emailText.getText());
        }

        int index = dateCombo.getSelectionIndex();
        task.setDays(validValues[index]);

        TaskExecutor taskExecutor = new CommonTaskExec(Messages.msgGenCert);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();

        if (task.isSuccess()) {
            serverInfo.setCertStatus(CertStatus.CUSTOMIZED);
            CommonUITool.openInformationBox(Messages.titleRestartCMS, Messages.msgRestartCMS);
        } else {
            CommonUITool.openErrorBox(task.getErrorMsg());
        }
    }
}
