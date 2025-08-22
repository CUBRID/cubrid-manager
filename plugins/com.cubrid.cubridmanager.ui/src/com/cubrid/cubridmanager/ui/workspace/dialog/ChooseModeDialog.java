/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.workspace.dialog;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.workspace.Messages;
import org.eclipse.draw2d.GridData;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The choose mode dialog
 *
 * @author Kevin.Wang
 */
public class ChooseModeDialog extends CMTitleAreaDialog {

    private String selectedMode = ApplicationType.CUBRID_MANAGER.getShortName();
    private Button cmButton;
    private Button cqbButton;

    public ChooseModeDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        cmButton = new Button(composite, SWT.RADIO);
        cmButton.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        cmButton.setToolTipText(Messages.tipCMMode);
        cmButton.setImage(CubridManagerUIPlugin.getImage("icons/cubridmanager32.gif"));
        cmButton.setSelection(true);
        cmButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedMode = ApplicationType.CUBRID_MANAGER.getShortName();
                        setMessage(Messages.msgChooseCMMode);
                    }
                });

        Label cmLabel = new Label(composite, SWT.None);
        cmLabel.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        cmLabel.setText(Messages.lblCMModeInfo);

        cqbButton = new Button(composite, SWT.RADIO);
        cqbButton.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        cqbButton.setToolTipText(Messages.tipCQBMode);
        cqbButton.setImage(CubridManagerUIPlugin.getImage("icons/cubridquery32.gif"));
        cqbButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedMode = ApplicationType.CUBRID_QUERY_BROWSER.getShortName();
                        setMessage(Messages.msgChooseCQBMode);
                    }
                });

        Label cqbLabel = new Label(composite, SWT.None);
        cqbLabel.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        cqbLabel.setText(Messages.lblCQBModeInfo);

        Label place = new Label(composite, SWT.None);
        place.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, 10));

        Group infoGroup = new Group(composite, SWT.None);
        infoGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 480, 60));
        infoGroup.setLayout(new FillLayout());
        infoGroup.setText(Messages.grpTipMode);

        Text infoText = new Text(infoGroup, SWT.MULTI | SWT.WRAP);
        infoText.setText(Messages.lblTwoModeInfo);
        infoText.setEnabled(false);
        infoText.setEditable(false);

        return parent;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleChooseMode);
        setMessage(Messages.msgChooseMode);
    }

    /**
     * Create button bar
     *
     * @param parent the parent composite
     * @return the Control object
     */
    protected Control createButtonBar(Composite parent) {
        return super.createButtonBarWithoutSeparator(parent);
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
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    /**
     * When press button in button bar,call this method
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }

    public String getSelectedMode() {
        return selectedMode;
    }
}
