/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.table.button;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputTextDialog extends CMTitleAreaDialog {
    private Text txtInput;
    private String labelName;
    private String title;
    private String message;
    private String result;

    public InputTextDialog(
            Shell parentShell, String title, String message, String labelName, String initialData) {
        super(parentShell);
        this.labelName = labelName;
        this.title = title;
        this.message = message;
        this.result = initialData;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(title);
        setMessage(message);

        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            layout.marginHeight = 5;
            layout.marginWidth = 5;
            layout.verticalSpacing = 5;
            layout.horizontalSpacing = 5;
            composite.setLayout(layout);
        }

        Label lbl = new Label(composite, SWT.NONE);
        lbl.setText(labelName);
        lbl.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        txtInput = new Text(composite, SWT.BORDER);
        txtInput.setText(StringUtil.nvl(result));
        txtInput.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        txtInput.setSelection(0, txtInput.getText().length());

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        this.getShell().setMinimumSize(450, 200);
        CommonUITool.centerShell(getShell());
        getShell().setText(title);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            result = txtInput.getText();
        }
        super.buttonPressed(buttonId);
    }

    public String getResult() {
        return result;
    }
}
