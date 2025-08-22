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

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.task.RenameTableColumnTask;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog of Rename Table's column.
 *
 * @author Kevin Cao 2011-3-3
 */
public class RenameColumnDialog extends CMTitleAreaDialog {

    private final String tableName;
    private final String column;
    private Text newText = null;
    private Button okButton;
    private String newName = "";
    private final CubridDatabase database;

    /**
     * The constructor
     *
     * @param parentShell
     * @param table
     * @param column
     * @param database
     */
    public RenameColumnDialog(
            Shell parentShell, String table, String column, CubridDatabase database) {
        super(parentShell);
        this.tableName = table;
        this.column = column;
        this.database = database;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.bind(Messages.renameShellTitle, tableName, column));
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, Messages.renameOKBTN, false);
        okButton.setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.renameCancelBTN, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /** Save the new text. */
    protected void okPressed() {
        newName = newText.getText().trim();
        TaskExecutor taskExec =
                new CommonTaskExec(Messages.bind(Messages.renameShellTitle, tableName, column));
        RenameTableColumnTask task = new RenameTableColumnTask(database.getDatabaseInfo());
        task.setTableName(tableName);
        task.setNewName(newName);
        task.setOldName(column);
        taskExec.addTask(task);
        new ExecTaskWithProgress(taskExec).busyCursorWhile();
        if (taskExec.isSuccess()) {
            super.okPressed();
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label label1 = new Label(composite, SWT.LEFT);
        label1.setText(Messages.bind(Messages.renameNewTableName, Messages.metaAttribute));
        label1.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        newText = new Text(composite, SWT.BORDER);
        newText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
        newText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        newText.setText(column);
        newText.selectAll();
        newText.setFocus();
        newText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        setErrorMessage(null);
                        okButton.setEnabled(false);
                        String newTable = newText.getText().trim();
                        if (!ValidateUtil.isValidIdentifier(newTable)) {
                            setErrorMessage(
                                    Messages.bind(
                                            Messages.renameInvalidTableNameMSG,
                                            Messages.metaAttribute,
                                            newTable));
                            return;
                        } else if (newTable.equalsIgnoreCase(column)) {
                            return;
                        }
                        okButton.setEnabled(true);
                    }
                });

        setTitle(Messages.bind(Messages.renameMSGTitle, Messages.metaAttribute));
        setMessage(Messages.bind(Messages.renameDialogMSG, Messages.metaAttribute));
        return parent;
    }

    /**
     * Retrieve the new Name.
     *
     * @return new name.
     */
    public String getNewName() {
        return newName;
    }
}
