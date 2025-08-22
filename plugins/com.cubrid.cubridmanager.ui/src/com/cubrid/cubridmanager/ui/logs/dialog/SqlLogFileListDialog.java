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
package com.cubrid.cubridmanager.ui.logs.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.logs.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * The dialog is used to select broker log files to analyze.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-18 created by wuyingshi
 */
public class SqlLogFileListDialog extends CMTitleAreaDialog {

    private static Group groupTargetFile = null;
    private Button checkTransactionBasedLogTop = null;
    private Table tableTargetFile = null;
    private List<String> selectedStringList = null;
    private boolean option = false;
    private Composite composite;
    private CubridDatabase database = null;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public SqlLogFileListDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 3;
        composite.setLayout(layout);

        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
        gridData1.horizontalSpan = 3;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData1.grabExcessHorizontalSpace = false;

        createGroupTargetFile();
        checkTransactionBasedLogTop = new Button(composite, SWT.CHECK);
        checkTransactionBasedLogTop.setText(Messages.chkAnalizeOptionT);
        checkTransactionBasedLogTop.setLayoutData(gridData1);

        setTitle(Messages.titleSqlLogFileListDialog);
        setMessage(Messages.msgSqlLogFileListDialog);
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleSqlLogFileListDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.buttonOk, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.buttonCancel, false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {

        if (buttonId == IDialogConstants.OK_ID) {
            if (!verify()) {
                return;
            }
            int selectioncount = tableTargetFile.getSelectionCount();
            selectedStringList = new ArrayList<String>();
            for (int j = 0; j < selectioncount; j++) {
                String filepath = tableTargetFile.getSelection()[j].getText(0);
                selectedStringList.add(filepath);
            }
            option = checkTransactionBasedLogTop.getSelection();
        }
        super.buttonPressed(buttonId);
    }

    /**
     * verify the input content.
     *
     * @return boolean
     */
    private boolean verify() {
        String msg = validInput();
        if (msg != null && !"".equals(msg)) {
            setErrorMessage(msg);
            return false;
        }
        setErrorMessage(null);
        return true;
    }

    /**
     * input content of to be verified.
     *
     * @return error message
     */
    private String validInput() {
        int selectioncount = tableTargetFile.getSelectionCount();
        if (selectioncount == 0) {
            return Messages.msgSelectTargeFile;
        }
        return null;
    }

    /** This method initializes groupTargetFile */
    private void createGroupTargetFile() {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 3;
        groupTargetFile = new Group(composite, SWT.FILL);
        groupTargetFile.setText(Messages.labelTargetFileList);
        groupTargetFile.setLayout(new GridLayout());
        groupTargetFile.setLayoutData(gridData);
        createTableTargetFile();
    }

    /** This method initializes tableTargetFile */
    private void createTableTargetFile() {
        GridData gridData3 = new GridData(GridData.FILL_BOTH);
        gridData3.heightHint = 220;
        gridData3.widthHint = 300;
        tableTargetFile = new Table(groupTargetFile, SWT.MULTI | SWT.FULL_SELECTION);
        tableTargetFile.setHeaderVisible(true);
        tableTargetFile.setLayoutData(gridData3);
        tableTargetFile.setLinesVisible(true);
        TableLayout tlayout = new TableLayout();
        tlayout.addColumnData(new ColumnWeightData(20, 100, true));
        TableColumn logFile = new TableColumn(tableTargetFile, SWT.LEFT);
        logFile.setText(Messages.labelLogFile);
        logFile.setWidth(470);
    }

    /**
     * get the database.
     *
     * @return database
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * set the databae.
     *
     * @param database CubridDatabase
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * set targetStringList into table.
     *
     * @param targetStringList List<String>
     */
    public void setInfo(List<String> targetStringList) {
        TableItem item;
        for (String string : targetStringList) {
            item = new TableItem(tableTargetFile, SWT.NONE);
            item.setText(0, string);
        }
        if (targetStringList.size() == 1) {
            tableTargetFile.setSelection(0);
        }
    }

    /**
     * Return whether is option
     *
     * @return option
     */
    public boolean isOption() {
        return option;
    }

    /**
     * Set whether is option
     *
     * @param option boolean
     */
    public void setOption(boolean option) {
        this.option = option;
    }

    /**
     * Get selected string list
     *
     * @return selectedStringList
     */
    public List<String> getSelectedStringList() {
        return selectedStringList;
    }
}
