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
package com.cubrid.common.ui.query.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
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
 * MultiShardQueryDialog
 *
 * @author Isaiah Choe 2012-05-18
 */
public class MultiShardQueryDialog extends CMTitleAreaDialog {
    private static int max = 0;
    private static int min = 0;
    private Label lblShard;
    private Text txtShard;
    private Text txtMaxShard;
    private int minShardId;
    private int maxShardId;

    /** @param parentShell */
    public MultiShardQueryDialog(Shell parentShell) {
        super(parentShell);
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
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 4;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        setTitle(Messages.shardMultiQueryTitle);
        setMessage(Messages.shardMultiQueryMessage);

        lblShard = new Label(composite, SWT.NONE);
        lblShard.setText(Messages.shardMultiQueryStartLabel);
        txtShard = new Text(composite, SWT.BORDER);
        {
            GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
            txtShard.setLayoutData(gd);
        }
        Label lbl = null;
        {
            lbl = new Label(composite, SWT.NONE);
            lbl.setText(Messages.shardMultiQueryEndLabel);
        }
        txtMaxShard = new Text(composite, SWT.BORDER);
        {
            GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
            txtMaxShard.setLayoutData(gd);
        }

        txtShard.setText(String.valueOf(min));
        txtMaxShard.setText(String.valueOf(max));
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(430, 210);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.shardMultiQueryDialogTitle);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.shardMultiQueryRunButton, true);
        createButton(
                parent, IDialogConstants.CANCEL_ID, Messages.shardMultiQueryCloseButton, false);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!ValidateUtil.isNumber(txtShard.getText())) {
                CommonUITool.openErrorBox(Messages.shardMultiQueryStartShardIdInputErrorMsg);
                return;
            }
            int minShardId = StringUtil.intValue(txtShard.getText());
            if (minShardId < 0) {
                CommonUITool.openErrorBox(Messages.shardMultiQueryStartShardIdInputErrorMsg);
                return;
            }

            if (!ValidateUtil.isNumber(txtMaxShard.getText())) {
                CommonUITool.openErrorBox(Messages.shardMultiQueryEndShardIdInputErrorMsg);
                return;
            }
            int maxShardId = StringUtil.intValue(txtMaxShard.getText());
            if (maxShardId < 0) {
                CommonUITool.openErrorBox(Messages.shardMultiQueryEndShardIdInputErrorMsg);
                return;
            }

            this.minShardId = minShardId;
            this.maxShardId = maxShardId;

            max = maxShardId;
            min = minShardId;
        }

        super.buttonPressed(buttonId);
    }

    public int getMinShardId() {
        return minShardId;
    }

    public int getMaxShardId() {
        return maxShardId;
    }
}
