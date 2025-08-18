/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.common.ui.spi.table.celleditor;

import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.table.FileDialogUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Table cell content edited dialog for string with large data
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-6-26 created by Kevin.Wang
 */
public class LongTextCellPopupDialog extends CMTitleAreaDialog implements ICellPopupEditor {

    private Combo fileCharsetCombo;
    private StyledText columnValueText;
    private CellValue newValue;
    private Object currValue;

    protected ColumnInfo columnInfo;
    protected CellValue value;
    protected Button setNullBtn;
    protected Button importBtn;
    protected Button exportBtn;
    protected boolean isEditable = true;
    protected String defaultCharset;
    /**
     * The Constructor
     *
     * @param parent Shell
     * @param cellType CellType
     */
    public LongTextCellPopupDialog(
            Shell parent,
            ColumnInfo columnInfo,
            String defaultCharset,
            CellValue value,
            boolean isEditable) {
        super(parent);
        this.columnInfo = columnInfo;
        this.defaultCharset = defaultCharset;
        this.value = value;
        this.isEditable = isEditable;
    }

    /**
     * Create dialog area
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Composite btnComposite = new Composite(composite, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 5;
        btnComposite.setLayout(rowLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.horizontalSpan = 2;
        btnComposite.setLayoutData(gridData);

        setNullBtn = new Button(btnComposite, SWT.CHECK);
        {
            setNullBtn.setText(Messages.btnSetNull);
            setNullBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            changeBtnStatus();
                        }
                    });
        }

        importBtn = new Button(btnComposite, SWT.PUSH);
        importBtn.setText(Messages.btnImport);
        importBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        File file =
                                FileDialogUtils.getImportedFile(
                                        getShell(), new String[] {"*.txt", "*.sql", "*.csv"});
                        if (null != file && file.getName().length() > 0) {
                            final String charsetName = fileCharsetCombo.getText();
                            try {
                                "".getBytes(charsetName);
                            } catch (UnsupportedEncodingException e) {
                                CommonUITool.openErrorBox(Messages.errCharset);
                                return;
                            }
                            try {
                                String textValue = FileUtil.readData(file.getPath(), charsetName);
                                columnValueText.setText(textValue);
                                currValue = textValue;
                            } catch (IOException ex) {
                                CommonUITool.openErrorBox(ex.getMessage());
                            }
                        }
                    }
                });

        exportBtn = new Button(btnComposite, SWT.PUSH);
        exportBtn.setText(Messages.btnExport);
        exportBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (columnValueText.getText().length() == 0) {
                            CommonUITool.openWarningBox(Messages.noDataExport);
                            return;
                        }
                        String charsetName = fileCharsetCombo.getText();
                        try {
                            "".getBytes(charsetName);
                        } catch (UnsupportedEncodingException e) {
                            CommonUITool.openErrorBox(Messages.errCharset);
                            return;
                        }

                        File file =
                                FileDialogUtils.getDataExportedFile(
                                        getShell(),
                                        new String[] {"*.*"},
                                        new String[] {"*.*"},
                                        null);
                        if (null != file && file.getName().length() > 0) {
                            exportData(file.getPath());
                        }
                    }
                });

        fileCharsetCombo = new Combo(btnComposite, SWT.NONE);
        {
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            String charset = StringUtil.getDefaultCharset();
            fileCharsetCombo.setText(charset);
        }

        columnValueText =
                new StyledText(
                        composite, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        if (value.hasLoadAll()) {
            CommonUITool.registerContextMenu(columnValueText, isEditable);
        }

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 280;
        gd.widthHint = 500;
        columnValueText.setLayoutData(gd);

        initValue();

        return composite;
    }

    protected void updateButtonStatus() {
        if (!isEditable || !value.hasLoadAll()) {
            if (null != setNullBtn) {
                setNullBtn.setEnabled(false);
            }
            if (null != importBtn) {
                importBtn.setEnabled(false);
            }
            if (getButton(IDialogConstants.OK_ID) != null) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }
        }
        if (!value.hasLoadAll()) {
            if (null != exportBtn) {
                exportBtn.setEnabled(false);
            }
        }
    }
    /** Initial the value */
    private void initValue() {
        if (value == null || value.getValue() == null || NULL_VALUE.equals(value.getValue())) {
            setNullBtn.setSelection(true);
            changeBtnStatus();
        } else {
            Object object = value.getValue();
            if (isEditable && !value.hasLoadAll()) {
                setMessage(Messages.msgLoadIncomplete);
            }
            currValue = object;
            if (object instanceof String) {
                columnValueText.setText((String) object);
            } else if (object instanceof File) {
                File file = (File) object;
                try {
                    String textValue =
                            FileUtil.readData(file.getAbsolutePath(), fileCharsetCombo.getText());
                    columnValueText.setText(textValue);
                } catch (IOException ex) {
                    CommonUITool.openErrorBox(ex.getMessage());
                }
            }
        }
        ModifyListener listener =
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        currValue = columnValueText.getText();
                    }
                };
        columnValueText.addModifyListener(listener);
    }

    /** Change button status */
    private void changeBtnStatus() {
        if (setNullBtn.getSelection()) {
            importBtn.setEnabled(false);
            columnValueText.setEnabled(false);
        } else {
            importBtn.setEnabled(true);
            columnValueText.setEnabled(true);
        }
    }

    /**
     * Export data to file
     *
     * @param filePath String
     */
    private void exportData(final String filePath) {
        final String charsetName = fileCharsetCombo.getText();
        final String content = columnValueText.getText();
        AbstractUITask task =
                new AbstractUITask() {
                    boolean isSuccess = false;

                    public void execute(final IProgressMonitor monitor) {
                        BufferedWriter writer = null;
                        try {
                            writer =
                                    new BufferedWriter(
                                            new OutputStreamWriter(
                                                    new FileOutputStream(filePath), charsetName));
                            writer.write(content);
                            writer.flush();
                            isSuccess = true;
                        } catch (IOException e) {
                            errorMsg = e.getMessage();
                        } finally {
                            if (writer != null) {
                                try {
                                    writer.close();
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                        }
                    }

                    public void cancel() {
                        // empty
                    }

                    public void finish() {
                        // empty
                    }

                    public boolean isCancel() {
                        return false;
                    }

                    public boolean isSuccess() {
                        return isSuccess;
                    }
                };

        TaskExecutor taskExecutor = new CommonTaskExec(Messages.msgExportFieldData);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            CommonUITool.openInformationBox(
                    getShell(), Messages.titleSuccess, Messages.msgExportSuccess);
        }
    }

    /**
     * Get the sell style
     *
     * @return int
     */
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.SHELL_TRIM;
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
        updateButtonStatus();
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        getShell().setMinimumSize(640, 480);
        super.constrainShellSize();
        if (isEditable) {
            getShell().setText(Messages.titleEditData);
            this.setMessage(Messages.msgEditData);
        } else {
            getShell().setText(Messages.titleViewData);
            this.setMessage(Messages.msgViewData);
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            Object obj = null;
            String shownStr = "";
            if (setNullBtn.getSelection()) {
                shownStr = NULL_VALUE;
                obj = null;
            } else {
                obj = currValue;
                if (DataType.isClobDataType(columnInfo.getType()) && currValue instanceof File) {
                    shownStr = CLOB_VALUE;
                } else if (currValue instanceof String) {
                    shownStr = (String) currValue;
                    if (!StringUtil.isEmpty(shownStr)
                            && shownStr.length() > FieldHandlerUtils.MAX_DISPLAY_CLOB_LENGTH) {
                        shownStr =
                                shownStr.substring(0, FieldHandlerUtils.MAX_DISPLAY_CLOB_LENGTH)
                                        + "...";
                    }
                }
            }
            if (CellViewer.isCellValueEqual(value, obj)) {
                super.buttonPressed(IDialogConstants.CANCEL_ID);
                return;
            } else {
                boolean isContinue =
                        CommonUITool.openConfirmBox(getShell(), Messages.confirmDataChanged);
                if (!isContinue) {
                    return;
                }
            }

            newValue = new CellValue(obj);
            if (obj instanceof File) {
                newValue.setFileCharset(fileCharsetCombo.getText());
            }
            newValue.setShowValue(shownStr);
        }
        super.buttonPressed(buttonId);
    }

    public CellValue getValue() {
        return newValue;
    }

    public int show() {
        return this.open();
    }
}
