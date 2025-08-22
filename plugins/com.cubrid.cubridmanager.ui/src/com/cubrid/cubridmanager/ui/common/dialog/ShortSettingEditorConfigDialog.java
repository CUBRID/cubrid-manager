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
package com.cubrid.cubridmanager.ui.common.dialog;

import com.cubrid.common.ui.common.control.SelectColorCombo;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.common.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The ShortSettingEditorConfigDialog class
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-3-21 created by Kevin.Wang
 */
public class ShortSettingEditorConfigDialog extends CMTitleAreaDialog {
    private DatabaseEditorConfig editorConfig;
    private SelectColorCombo colorCombo;

    /** @param parentShell */
    public ShortSettingEditorConfigDialog(Shell parentShell, DatabaseEditorConfig editorConfig) {
        super(parentShell);
        this.editorConfig = editorConfig;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.RIGHT);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        new Composite(composite, SWT.NONE).setLayoutData(CommonUITool.createGridData(1, 1, 20, 0));

        Label backgroundLabel = new Label(composite, SWT.None);
        backgroundLabel.setText(Messages.labBackground);
        backgroundLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        colorCombo =
                new SelectColorCombo(composite, SWT.BORDER, EditorConstance.getDefaultBackground());
        colorCombo.setLayoutData(CommonUITool.createGridData(1, 1, 110, 20));

        return composite;
    }

    protected void okPressed() {
        RGB selectedColor = colorCombo.getSelectedColor();

        if (editorConfig == null) {
            editorConfig = new DatabaseEditorConfig();
        }
        editorConfig.setBackGround(selectedColor);

        super.okPressed();
    }

    public DatabaseEditorConfig getEditorConfig() {
        return editorConfig;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(380, 220);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleSetEditorConfig);
        setMessage(Messages.msgSetEditorConfig);
        /*Expand the color combo*/
        this.getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                }
                                colorCombo.expandMenu();
                            }
                        });
    }

    protected int getShellStyle() {
        return SWT.CLOSE;
    }
}
