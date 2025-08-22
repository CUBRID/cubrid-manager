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
package com.cubrid.cubridmanager.ui.monstatistic.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorDashboardPersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorStatisticPersistManager;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This type provides a dialog for user to configuration a monitor statistic item in monitor
 * statistic page
 *
 * @author Santiago Wang
 * @version 1.0 - 2010-3-31 created by Santiago Wang
 */
public class AddStatisticPageDialog extends CMTitleAreaDialog {

    private String name;
    private Text nameText;
    private String cubridServerId;
    // isOkEnable[0]: check whether user input the invalid name;
    // isOkEnable[1]: check whether user input the duplicate name;
    private final boolean[] isOkEnable;

    /**
     * Constructor
     *
     * @param parentShell
     */
    public AddStatisticPageDialog(Shell parentShell) {
        super(parentShell);
        isOkEnable = new boolean[2];
        for (int i = 0; i < isOkEnable.length; i++) {
            isOkEnable[i] = true;
        }
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above the button bar).
     *
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite nameComp = new Composite(parentComp, SWT.RESIZE);
        GridLayout layoutNameComp = new GridLayout(2, false);
        nameComp.setLayout(layoutNameComp);
        nameComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblName = new Label(nameComp, SWT.NONE);
        final GridData gdLblName = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdLblName.widthHint = 80;
        lblName.setLayoutData(gdLblName);
        lblName.setText(Messages.lblPageName);

        nameText = new Text(nameComp, SWT.BORDER);
        final GridData gdNameText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdNameText.widthHint = 220;
        nameText.setLayoutData(gdNameText);
        nameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        String name = nameText.getText().trim();
                        if (StringUtil.isEmpty(name)) {
                            isOkEnable[0] = false;
                            enableOk();
                            return;
                        } else {
                            isOkEnable[0] = true;
                        }
                        // check format
                        String regex = "^[0-9a-zA-Z_]{1,32}$";
                        if (!Pattern.matches(regex, name)) {
                            isOkEnable[0] = false;
                            enableOk();
                            return;
                        } else {
                            isOkEnable[0] = true;
                        }
                        // check duplicate name
                        MonitorStatisticPersistManager persistManager =
                                MonitorStatisticPersistManager.getInstance();
                        String checkedId = name;
                        if (cubridServerId != null) {
                            checkedId += "@" + cubridServerId;
                        }
                        isOkEnable[1] = !persistManager.isContainedById(checkedId, cubridServerId);
                        /*TOOLS-3672 Avoid dashboard node and monitor statistic node have the same name*/
                        if (cubridServerId == null) {
                            List<ICubridNode> dashboardList =
                                    MonitorDashboardPersistManager.getInstance()
                                            .getAllMonitorDashboards();
                            for (int i = 0; i < dashboardList.size(); i++) {
                                ICubridNode node = dashboardList.get(i);
                                if (name.equals(node.getLabel())) {
                                    isOkEnable[1] = false;
                                }
                            }
                        }

                        enableOk();
                    }
                });

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.addMonitorStatisticPageTitle);
        this.setTitle(Messages.addMonitorStatisticPageTitle);
        this.setMessage(Messages.addMonitorStatisticMsg);
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
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /** Enable the "OK" button */
    public void enableOk() {
        String errMsg = null;
        if (!isOkEnable[0]) {
            errMsg = Messages.errInvalidPageNameMsg;
        } else if (!isOkEnable[1]) {
            errMsg = Messages.errDuplicatePageNameMsg;
        }
        if (errMsg == null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        setErrorMessage(errMsg);
    }

    /** When press "ok" button, call it. */
    public void okPressed() {
        name = nameText.getText().trim();
        super.okPressed();
    }

    public String getName() {
        return name;
    }

    public String getCubridServerId() {
        return cubridServerId;
    }

    public void setCubridServerId(String cubridServerId) {
        this.cubridServerId = cubridServerId;
    }
}
