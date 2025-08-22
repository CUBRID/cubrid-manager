/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.dialog;

import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
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

/**
 * Export data type choosing dialog
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-1-28 created by Yu Guojia
 */
public class ExportERDataDialog extends CMTitleAreaDialog {
    private Button erdButton;
    private Button erwinButton;
    private Button imageButton;
    private Button sqlButton;
    private int selectedType = ERDTYPE;
    public static int ERDTYPE = 1;
    public static int ERWINTYPE = 2;
    public static int IMAGETYPE = 3;
    public static int SQLTYPE = 4;

    public ExportERDataDialog(Shell parentShell) {
        super(parentShell);
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(300, 293);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleChoose);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.okBTN, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.cancelBTN, false);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        /* Type group */
        Group typeOptionGroup = new Group(parentComp, SWT.None);
        typeOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        typeOptionGroup.setLayout(new GridLayout(1, false));

        Composite typeComposite = new Composite(typeOptionGroup, SWT.NONE);
        typeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 2;
        layout.marginWidth = 10;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 5;
        typeComposite.setLayout(layout);

        erdButton = new Button(typeComposite, SWT.RADIO);
        erdButton.setText("CUBRID ERD(.erd)");
        erdButton.setToolTipText("CUBRID ERD(.erd)");
        erdButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        erdButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedType = ERDTYPE;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
        erwinButton = new Button(typeComposite, SWT.RADIO);
        erwinButton.setText("ERWIN(.xml)");
        erwinButton.setToolTipText("ERWIN(.xml)");
        erwinButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        erwinButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedType = ERWINTYPE;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
        imageButton = new Button(typeComposite, SWT.RADIO);
        imageButton.setText("Image(.jpg,...)");
        imageButton.setToolTipText("Image(.jpg,...)");
        imageButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        imageButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedType = IMAGETYPE;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
        sqlButton = new Button(typeComposite, SWT.RADIO);
        sqlButton.setText("sql(.sql)");
        sqlButton.setToolTipText("sql(.sql)");
        sqlButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        sqlButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedType = SQLTYPE;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        setTitle(Messages.titleChoose);
        setMessage(Messages.msgChooseSaveFileType);
        return parent;
    }

    /**
     * erd:1; erwin:2; image:3; sql:4
     *
     * @return
     */
    public int getSelectedType() {
        return selectedType;
    }
}
