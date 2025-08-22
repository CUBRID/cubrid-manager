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
package com.cubrid.cubridmanager.ui.monstatistic.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This type provides a dialog for user to configuration a monitor statistic item in monitor
 * statistic page
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-08-20 created by Santiago Wang
 */
public class EditMonitorIntervalDialog extends CMTitleAreaDialog {

    private Combo intervalCombo;
    private Button btnEditCheck;
    private final String[] itemsOfIntervalCombo;

    private String interval;
    private boolean isEditable = false;
    private boolean isEditEnable = false;
    private boolean isOkEnable = true;

    /**
     * Constructor
     *
     * @param parentShell
     */
    public EditMonitorIntervalDialog(Shell parentShell) {
        super(parentShell);
        itemsOfIntervalCombo = new String[15];
        for (int i = 0; i < itemsOfIntervalCombo.length; i++) {
            if (i < 6) {
                itemsOfIntervalCombo[i] = Integer.toString(10 * (i + 1));
            } else if (i >= 6 && i < 10) {
                itemsOfIntervalCombo[i] = Integer.toString(60 * (i - 4));
            } else if (i >= 10) {
                itemsOfIntervalCombo[i] = Integer.toString(600 * (i - 9));
            }
        }
        interval = itemsOfIntervalCombo[5];
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above the button bar).
     *
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        int columePerLine = isEditable ? 3 : 2;
        Composite intervalComp = new Composite(parentComp, SWT.RESIZE);
        GridLayout layoutIntervalComp = new GridLayout(columePerLine, false);
        intervalComp.setLayout(layoutIntervalComp);
        intervalComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblInterval = new Label(intervalComp, SWT.NONE);
        final GridData gdLblInterval = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdLblInterval.widthHint = 80;
        lblInterval.setLayoutData(gdLblInterval);
        lblInterval.setText(Messages.lblInterval);

        intervalCombo = new Combo(intervalComp, SWT.BORDER);
        final GridData gdIntervalCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdIntervalCombo.widthHint = 160;
        intervalCombo.setLayoutData(gdIntervalCombo);
        intervalCombo.setItems(itemsOfIntervalCombo);
        intervalCombo.setText(interval);
        intervalCombo.setEnabled(false);

        if (isEditable) {
            intervalCombo.addVerifyListener(
                    new VerifyListener() {
                        public void verifyText(VerifyEvent event) {
                            String text = event.text;
                            if (StringUtil.isEmpty(text)) {
                                return;
                            }
                            if (text.matches("^\\d*$")) {
                                event.doit = true;
                            } else {
                                event.doit = false;
                            }
                        }
                    });
            intervalCombo.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            String str = intervalCombo.getText();
                            if (StringUtil.isEmpty(str)) {
                                isOkEnable = false;
                                enableOk();
                                return;
                            }
                            int interval = Integer.parseInt(str);
                            if (interval >= 10 && interval < 3600) {
                                isOkEnable = true;
                            } else {
                                isOkEnable = false;
                            }
                            enableOk();
                        }
                    });

            btnEditCheck = new Button(intervalComp, SWT.CHECK);
            btnEditCheck.setText(Messages.btnEditInterval);
            btnEditCheck.addSelectionListener(
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            isEditEnable = btnEditCheck.getSelection();
                            intervalCombo.setEnabled(isEditEnable);
                        }
                    });
            btnEditCheck.setSelection(false);
        }

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.checkMonIntervalTitle);
        this.setTitle(Messages.checkMonIntervalTitle);
        this.setMessage(Messages.checkMonIntervalMsg);
        CommonUITool.centerShell(getShell());
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /** Enable the "OK" button */
    private void enableOk() {
        String errMsg = null;
        if (!isOkEnable) {
            errMsg = Messages.errInvalidIntervalMsg;
        }
        getButton(IDialogConstants.OK_ID).setEnabled(isOkEnable);
        setErrorMessage(errMsg);
    }

    /** When press "ok" button, call it. */
    public void okPressed() {
        if (isEditEnable && CommonUITool.openConfirmBox(Messages.confirmChangeMonInterval)) {
            interval = intervalCombo.getText().trim();
        } else {
            isEditEnable = false;
        }
        super.okPressed();
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isEditEnable() {
        return isEditEnable;
    }
}
