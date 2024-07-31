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
package com.cubrid.cubridmanager.ui.host.control;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
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
 * Edit Parameter Dialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-12-03 created by Kevin.Wang
 */
public class EditParameterDialog extends CMTitleAreaDialog {
    private DataModel dataModel;

    private Text keyText;
    private Text valueText;

    // private Text helpText;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public EditParameterDialog(Shell parentShell, DataModel dataModel) {
        super(parentShell);

        this.dataModel = dataModel;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        this.setMessage(Messages.msgSettingParameters);

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

        Label keyLabel = new Label(composite, SWT.None);
        keyLabel.setLayoutData(
                CommonUITool.createGridData(GridData.VERTICAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        keyLabel.setText(Messages.lblKey);

        keyText = new Text(composite, SWT.BORDER);
        keyText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label valueLabel = new Label(composite, SWT.None);
        valueLabel.setLayoutData(
                CommonUITool.createGridData(GridData.VERTICAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        valueLabel.setText(Messages.lblValue);

        valueText = new Text(composite, SWT.BORDER | SWT.MULTI);
        valueText.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 60));

        //		Label helpLabel = new Label(composite, SWT.None);
        //		helpLabel.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH,
        //				1, 1, -1, -1));
        //		helpLabel.setText("Manual :");
        //
        //		helpText = new Text(composite, SWT.BORDER |SWT.MULTI | SWT.READ_ONLY);
        //		helpText.setLayoutData(CommonUITool.createGridData(
        //				GridData.FILL_BOTH, 1, 1, -1, -1));

        init();
        return parentComp;
    }

    private void init() {
        keyText.setText(dataModel.getKey() == null ? "" : dataModel.getKey());
        valueText.setText(dataModel.getValue() == null ? "" : dataModel.getValue());
    }

    protected int getShellStyle() {
        return super.getShellStyle() | SWT.TITLE | SWT.PRIMARY_MODAL;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setSize(400, 300);
        CommonUITool.centerShell(getShell());
        getShell().setText("Edit parameter");
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
     * Call this method when button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (validate()) {
                dataModel.setKey(keyText.getText());
                dataModel.setValue(valueText.getText());
            }
        }

        super.buttonPressed(buttonId);
    }

    private boolean validate() {
        setErrorMessage(null);
        if (StringUtil.isEmpty(keyText.getText())) {
            this.setErrorMessage(Messages.errKeyEmpty);
            return false;
        }
        if (StringUtil.isEmpty(valueText.getText())) {
            this.setErrorMessage(Messages.errValueEmpty);
            return false;
        }
        return true;
    }

    public DataModel getDataModel() {
        return dataModel;
    }
}
