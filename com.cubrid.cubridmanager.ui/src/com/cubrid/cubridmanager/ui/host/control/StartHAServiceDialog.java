/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Start HA Service Dialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-12-11 created by Kevin.Wang
 */
public class StartHAServiceDialog extends CMTitleAreaDialog {

    private HAModel haModel;

    private Text step1Text;
    private Button step1Button;

    private Text step2Text;
    private Button step2Button;

    private Text setp3Text;

    public StartHAServiceDialog(Shell parentShell, HAModel haModel) {
        super(parentShell);
        this.haModel = haModel;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        setTitle(Messages.haStep5);
        setMessage(Messages.msgStartHAService);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(15);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Group step1Group = new Group(composite, SWT.None);
        step1Group.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 60));
        step1Group.setText(
                Messages.bind(Messages.grpStep1, haModel.getMasterServer().getHostName()));
        step1Group.setLayout(new GridLayout());

        step1Text = new Text(step1Group, SWT.BORDER);
        step1Text.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        step1Text.setEditable(false);

        step1Button = new Button(step1Group, SWT.CHECK);
        step1Button.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        step1Button.setText(Messages.btnHaveDone);
        step1Button.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        validate();
                    }
                });

        Group step2Group = new Group(composite, SWT.None);
        step2Group.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 60));
        step2Group.setText(
                Messages.bind(Messages.grpStep2, haModel.getSlaveServer().getHostName()));
        step2Group.setLayout(new GridLayout());

        step2Text = new Text(step2Group, SWT.BORDER);
        step2Text.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        step2Text.setEditable(false);

        step2Button = new Button(step2Group, SWT.CHECK);
        step2Button.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        step2Button.setText(Messages.btnHaveDone);
        step2Button.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        validate();
                    }
                });

        Group step3Group = new Group(composite, SWT.None);
        step3Group.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        step3Group.setText(Messages.grpStep3);
        step3Group.setLayout(new GridLayout());

        setp3Text = new Text(step3Group, SWT.MULTI | SWT.BORDER);
        setp3Text.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        setp3Text.setEditable(false);

        init();
        return parentComp;
    }

    private void init() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(haModel.getSlaveServer().getServer().getServerInfo().getHostAddress())
                .append("\t")
                .append(haModel.getSlaveServer().getHostName());
        step1Text.setText(sb1.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append(haModel.getMasterServer().getServer().getServerInfo().getHostAddress())
                .append("\t")
                .append(haModel.getMasterServer().getHostName());
        step2Text.setText(sb2.toString());

        StringBuilder sb3 = new StringBuilder();
        sb3.append(Messages.txtStopService).append(StringUtil.NEWLINE);
        sb3.append(Messages.txtStartCMSService).append(StringUtil.NEWLINE);
        sb3.append(Messages.txtStartHAService).append(StringUtil.NEWLINE);
        sb3.append(Messages.txtHelp);

        setp3Text.setText(sb3.toString());
    }

    protected int getShellStyle() {
        return SWT.TITLE | SWT.PRIMARY_MODAL;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setSize(700, 500);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleStartHAService);
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
    }

    /**
     * Call this method when button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validate()) {
                return;
            }
        }

        super.buttonPressed(buttonId);
    }

    private boolean validate() {
        setErrorMessage(null);

        if (!step1Button.getSelection()) {
            this.setErrorMessage(Messages.errDoStep1);
            return false;
        }
        if (!step2Button.getSelection()) {
            this.setErrorMessage(Messages.errDoStep2);
            return false;
        }

        return true;
    }
}
