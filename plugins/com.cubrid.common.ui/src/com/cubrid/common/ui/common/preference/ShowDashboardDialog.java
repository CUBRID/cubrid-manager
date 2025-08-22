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
package com.cubrid.common.ui.common.preference;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * ShowDashboardDialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-15 created by Kevin.Wang
 */
public class ShowDashboardDialog extends Dialog {
    public static final int TYPE_HOST = 1;
    public static final int TYPE_DB = 2;
    private Button showLaterButton;
    private int dashboardType;
    // auto show dashboard whenever it is connected
    private boolean useAutoShow;
    // auto refresh
    private int autoRefreshSecond = 1;
    private int minTime = 1;
    private int maxTime = 180;
    private Spinner timePinner;

    public ShowDashboardDialog(
            Shell parentShell, int dashboardType, boolean useAutoShow, int autoRefreshSecond) {
        super(parentShell);
        this.dashboardType = dashboardType;
        this.useAutoShow = useAutoShow;
        this.autoRefreshSecond = autoRefreshSecond;
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

        Label lblShowDashboard = new Label(composite, SWT.NONE);
        lblShowDashboard.setText(Messages.lblShowDashboard);
        showLaterButton = new Button(composite, SWT.CHECK | SWT.WRAP);
        showLaterButton.setText(Messages.btnShowDashboard);
        showLaterButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        showLaterButton.setSelection(useAutoShow);

        if (dashboardType == TYPE_DB) {
            createAutoRefreshTime(composite);
        }

        init();
        return composite;
    }

    private void init() {
        if (TYPE_HOST == dashboardType) {
            showLaterButton.setSelection(GeneralPreference.isUseHostDashboard());
        }

        if (TYPE_DB == dashboardType) {
            showLaterButton.setSelection(GeneralPreference.isUseDatabaseDashboard());
        }
    }

    private void createAutoRefreshTime(Composite comp) {
        Label lblShowDashboard = new Label(comp, SWT.NONE);
        lblShowDashboard.setText(Messages.lblAutoRefreshSecond);

        timePinner = new Spinner(comp, SWT.BORDER | SWT.RIGHT);
        {
            final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
            timePinner.setLayoutData(gd);
        }
        timePinner.setValues(autoRefreshSecond, minTime, maxTime, 0, 1, 1);
        timePinner.addKeyListener(
                new org.eclipse.swt.events.KeyAdapter() {
                    public void keyPressed(org.eclipse.swt.events.KeyEvent event) {
                        if (timePinner.getText().trim().length() == 0) {
                            return;
                        }
                        int selectTimeValue = Integer.parseInt(timePinner.getText());
                        if (selectTimeValue < minTime || selectTimeValue > maxTime) {
                            String msg =
                                    Messages.bind(
                                            Messages.databaseDashboardAutoRefreshConfErrMsg,
                                            Integer.toString(minTime),
                                            Integer.toString(maxTime));
                            CommonUITool.openErrorBox(msg);
                            return;
                        }
                    }
                });

        Label lblUnit = new Label(comp, SWT.NONE);
        lblUnit.setText(Messages.lblAutoRefreshSecondUnit);
    }

    protected void okPressed() {
        useAutoShow = showLaterButton.getSelection();
        if (TYPE_DB == dashboardType) {
            autoRefreshSecond = Integer.parseInt(timePinner.getText());
        }
        super.okPressed();
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(380, 150);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleShowDashboard);
    }

    protected int getShellStyle() {
        return SWT.CLOSE;
    }

    public int getAutoRefreshSecond() {
        return autoRefreshSecond;
    }

    public boolean isUseAutoShow() {
        return useAutoShow;
    }
}
