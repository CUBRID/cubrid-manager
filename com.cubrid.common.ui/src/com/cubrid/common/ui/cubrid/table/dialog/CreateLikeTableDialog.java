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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.CreateLikeTableTask;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * In MySQL compatible mode,create table by like statement dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-4-19 created by pangqiren
 */
public class CreateLikeTableDialog extends CMTitleAreaDialog implements ModifyListener {
    private Text likeTableOwnerText = null;
    private Combo newTableOwnerCombo = null;
    private Text likeTableNameText = null;
    private Text newTableNameText = null;
    private CubridDatabase database = null;
    private String likeTableOwnerName = null;
    private String likeTableName = null;
    private String newTableName = null;
    private String newOwnerName = null;
    private List<String> dbUserList = null;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public CreateLikeTableDialog(
            Shell parentShell, CubridDatabase database, List<String> dbUserList) {
        super(parentShell);
        this.database = database;
        this.dbUserList = dbUserList;
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
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        DatabaseInfo dbInfo = database.getDatabaseInfo();
        layout.numColumns = 5;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label tableNameLabel = new Label(composite, SWT.LEFT);
        tableNameLabel.setText(Messages.lblLikeTableName);
        tableNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        likeTableOwnerText = new Text(composite, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        likeTableOwnerText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 30, -1));

        likeTableNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        int horSpan = 3;
        likeTableNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, horSpan, 1, 100, -1));

        Label newTableNameLabel = new Label(composite, SWT.LEFT);
        newTableNameLabel.setText(Messages.lblNewTableName);
        newTableNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        newTableOwnerCombo = new Combo(composite, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        newTableOwnerCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 30, -1));
        newTableOwnerCombo.addSelectionListener(
                new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        modifyText(null);
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        newTableNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        newTableNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
        newTableNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, horSpan, 1, 100, -1));
        newTableNameText.addModifyListener(this);

        if (likeTableName == null) {
            likeTableNameText.addModifyListener(this);
            likeTableNameText.setFocus();
        } else {
            likeTableNameText.setText(likeTableName);
            likeTableNameText.setEnabled(false);
            newTableNameText.setFocus();
        }

        if (likeTableOwnerName != null) {
            likeTableOwnerText.setText(likeTableOwnerName);
            likeTableOwnerText.setEnabled(false);
        }

        if (dbInfo != null) {
            String ownerName;
            for (int i = 0; i < dbUserList.size(); i++) {
                ownerName = dbUserList.get(i);
                newTableOwnerCombo.add(ownerName);
                boolean isSame =
                        StringUtil.isEqualIgnoreCase(
                                ownerName,
                                database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName());
                if (isSame) {
                    newTableOwnerCombo.select(i);
                }
            }
            if (!database.getDatabaseInfo().isSupportUserSchema()) {
                newTableOwnerCombo.setEnabled(false);
            }
        }

        setTitle(Messages.titleCreateLikeTableDialog);
        setMessage(Messages.msgCreateLikeTableDialog);
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(450, 240);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleCreateLikeTableDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, true);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.common.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            createTable();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /** Execute task to create table */
    private void createTable() {
        newOwnerName = newTableOwnerCombo.getText();
        newTableName = newTableNameText.getText();
        String taskName = Messages.bind(Messages.createLikeTableTaskName, getNewTableName());
        TaskExecutor executor = new CommonTaskExec(taskName);
        CreateLikeTableTask task = new CreateLikeTableTask(getDatabase().getDatabaseInfo());
        task.setTableName(newTableName);
        task.setOwnerName(newOwnerName);
        task.setLikeTableName(getLikeTableName());
        task.setLikeOwnerName(getLikeTableOwnerName());
        executor.addTask(task);
        new ExecTaskWithProgress(executor).exec();
        if (task.isSuccess()) {
            super.buttonPressed(IDialogConstants.OK_ID);
        }
    }

    /**
     * Listen to the modify event
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        String newTableName = newTableNameText.getText();
        String newOwnerName = newTableOwnerCombo.getText();
        String likeTableName = likeTableNameText.getText();
        boolean isValid = true;
        if ("".equals(newTableName) || "".equals(likeTableName)) {
            setErrorMessage(Messages.errNoTableName);
            isValid = false;
        } else {
            if (!ValidateUtil.isValidIdentifier(newTableName)) {
                setErrorMessage(
                        Messages.bind(Messages.renameInvalidTableNameMSG, "table", newTableName));
                isValid = false;
                newTableNameText.selectAll();
                newTableNameText.setFocus();
            } else if (database.getDatabaseInfo().isSupportUserSchema()
                    && newTableName.equalsIgnoreCase(getLikeTableName())
                    && newOwnerName.equalsIgnoreCase(getLikeTableOwnerName())) {
                setErrorMessage(
                        Messages.bind(Messages.errExistTable, newOwnerName + "." + newTableName));
                isValid = false;
                newTableNameText.setFocus();
            } else if (!database.getDatabaseInfo().isSupportUserSchema()
                    && newTableName.equalsIgnoreCase(getLikeTableName())) {
                setErrorMessage(
                        Messages.bind(
                                Messages.errExistTable, likeTableOwnerName + "." + newTableName));
                isValid = false;
                newTableNameText.setFocus();
            } else if (!ValidateUtil.isASCII(newTableName)
                    && !ValidateUtil.isSupportMultiByte(database)) {
                setErrorMessage(Messages.errMultiBytes);
                isValid = false;
                newTableNameText.selectAll();
                newTableNameText.setFocus();
            }
        }
        if (isValid) {
            setErrorMessage(null);
        }
        getButton(IDialogConstants.OK_ID).setEnabled(isValid);
    }

    /**
     * Get CUBRID Database
     *
     * @return the CubridDatabase object
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * Set CUBRID Database
     *
     * @param database the CubridDatabase object
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    public String getLikeTableName() {
        return likeTableName;
    }

    public void setLikeTableName(String likeTableName) {
        this.likeTableName = likeTableName;
    }

    public String getLikeTableOwnerName() {
        return likeTableOwnerName;
    }

    public void setLikeTableOwnerName(String ownerName) {
        likeTableOwnerName = ownerName;
    }

    /**
     * Return the new table name
     *
     * @return The string
     */
    public String getNewTableName() {
        return newTableName;
    }

    public String getNewOwnerName() {
        return newOwnerName;
    }

    public String getNewUniqueTable() {
        return newOwnerName + "." + newTableName;
    }

    public String getLikeUniqueTable() {
        return likeTableOwnerName + "." + likeTableName;
    }

    public void setDbUserList(List<String> dbUserList) {
        this.dbUserList = dbUserList;
    }
}
