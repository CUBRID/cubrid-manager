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
package com.cubrid.common.ui.compare.data.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.compare.data.control.DataCompareEditorInput;
import com.cubrid.common.ui.compare.data.control.DataCompareEditorPart;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Comparison dialog
 *
 * @author Isaiah Choe
 * @version 1.0 - 2013-01-23 created by Isaiah Choe
 */
public class DataCompareDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(DataCompareDialog.class);

    private static final int COMPARE_ID = Integer.MAX_VALUE - 2;
    private List<ICubridNode> selections;
    private org.eclipse.swt.widgets.List leftCombo;
    private org.eclipse.swt.widgets.List rightCombo;
    private boolean isCanceled;

    /**
     * The constructor
     *
     * @param parentShell
     * @param queryEditorPart
     */
    public DataCompareDialog(Shell parentShell, List<ICubridNode> selections) {
        super(parentShell);
        this.selections = selections;
    }

    /**
     * Create the dialog area
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (selections == null) {
            return parentComp;
        }

        Label lblLeft = new Label(composite, SWT.NONE);
        lblLeft.setText(Messages.lblSchemaComparisonBase);

        Label lblRight = new Label(composite, SWT.NONE);
        lblRight.setText(Messages.lblSchemaComparisonTarget);

        leftCombo = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.V_SCROLL);
        leftCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        rightCombo = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.V_SCROLL);
        rightCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblDescription = new Label(composite, SWT.NONE);
        lblDescription.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));
        lblDescription.setText(com.cubrid.common.ui.compare.Messages.lblDataCompareDescDialog);

        for (Object obj : selections) {
            CubridDatabase db = (CubridDatabase) obj;
            if (db == null) {
                LOGGER.warn("The cubridDatabase is a null.");
                continue;
            }

            DatabaseInfo dbInfo = db.getDatabaseInfo();
            if (dbInfo == null) {
                LOGGER.warn("The databaseInfo is a null.");
                continue;
            }

            leftCombo.add(db.getLabel() + " - " + dbInfo.getBrokerIP() + "@" + dbInfo.getDbName());
        }

        for (Object obj : selections) {
            CubridDatabase db = (CubridDatabase) obj;
            if (db == null) {
                LOGGER.warn("The cubridDatabase is a null.");
                continue;
            }

            DatabaseInfo dbInfo = db.getDatabaseInfo();
            if (dbInfo == null) {
                LOGGER.warn("The databaseInfo is a null.");
                continue;
            }

            rightCombo.add(db.getLabel() + " - " + dbInfo.getBrokerIP() + "@" + dbInfo.getDbName());
        }

        setMessage(Messages.msgSchemaComparison);

        leftCombo.setSelection(0);
        rightCombo.setSelection(1);

        return parentComp;
    }

    private boolean isSelectedSameDatabases() {
        if (leftCombo.getSelectionIndex() != rightCombo.getSelectionIndex()) {
            return false;
        }

        return true;
    }

    private boolean isSelectedAllSideDatabases() {
        if (leftCombo.getSelectionIndex() >= 0 && rightCombo.getSelectionIndex() >= 0) {
            return true;
        }

        return false;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setSize(600, 400);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleDataComparison);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, COMPARE_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnClose, false);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        isCanceled = false;

        if (buttonId == COMPARE_ID) {
            if (!isSelectedAllSideDatabases()) {
                CommonUITool.openWarningBox(
                        com.cubrid.common.ui.compare.Messages.errNeedSelectCompareDb);
                return;
            }

            if (isSelectedSameDatabases()) {
                CommonUITool.openWarningBox(
                        com.cubrid.common.ui.compare.Messages.errSelectSameCompareDb);
                return;
            }

            ICubridNode sourceNode = selections.get(leftCombo.getSelectionIndex());
            ICubridNode targetNode = selections.get(rightCombo.getSelectionIndex());

            showDataCompareEditor(new DataCompareEditorInput(sourceNode, targetNode));
            super.buttonPressed(IDialogConstants.OK_ID);
        }

        super.buttonPressed(buttonId);
    }

    private void showDataCompareEditor(DataCompareEditorInput input) {
        try {
            PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, DataCompareEditorPart.ID);
        } catch (Exception e) {
            CommonUITool.openErrorBox(
                    Display.getDefault().getActiveShell(),
                    com.cubrid.common.ui.compare.Messages.fetchSchemaErrorFromDB);
            LOGGER.error(e.getMessage(), e);
        }
    }

    public boolean isCanceled() {
        return isCanceled;
    }
}
