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
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import java.io.UnsupportedEncodingException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Set PrearedStatment parameter value dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-23 created by pangqiren
 */
public class SetPstmtValueDialog extends CMTitleAreaDialog {

    private Button inputTextBtn;
    private StyledText paraValueText;
    private Button selectFileBtn;
    private Text filePathText;
    private Button browseBtn;
    private Combo fileCharsetCombo;
    private final TableItem item;

    public static final String FILE_CHARSET = "fileCharset";
    private final int editedColumn;
    private final CubridDatabase database;
    private Button setNullBtn;
    private String paraType = null;

    /**
     * The constructor
     *
     * @param parentShell
     * @param item
     * @param database
     * @param column
     */
    public SetPstmtValueDialog(
            Shell parentShell, final TableItem item, CubridDatabase database, int column) {
        super(parentShell);
        this.item = item;
        this.database = database;
        editedColumn = column;
    }

    /**
     * The constructor
     *
     * @param parentShell
     * @param item
     * @param database
     * @param column
     * @param paraType
     */
    public SetPstmtValueDialog(
            Shell parentShell,
            final TableItem item,
            CubridDatabase database,
            int column,
            String paraType) {
        super(parentShell);
        this.item = item;
        this.database = database;
        this.editedColumn = column;
        this.paraType = paraType;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }
        createInputText(composite);
        createFileComposite(composite);

        initial();
        setTitle(Messages.titleSetPstmtValueDialog);
        setMessage(Messages.msgSetPstmtValueDialog);
        return parentComp;
    }

    /**
     * Create input text group
     *
     * @param parentComp Composite
     */
    private void createInputText(Composite parentComp) {
        Group group = new Group(parentComp, SWT.NONE);
        {
            group.setText(Messages.btnSetParaValue);
            group.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            group.setLayout(layout);
        }

        inputTextBtn = new Button(group, SWT.RADIO | SWT.LEFT);
        inputTextBtn.setText(Messages.btnSetParaValue);
        inputTextBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        inputTextBtn.setSelection(true);
        inputTextBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (inputTextBtn.getSelection()) {
                            paraValueText.setEnabled(true);
                            setNullBtn.setEnabled(true);

                            filePathText.setText("");
                            browseBtn.setEnabled(false);
                            fileCharsetCombo.setEnabled(false);
                            selectFileBtn.setSelection(false);
                        }
                        validate();
                    }
                });

        paraValueText = new StyledText(group, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        paraValueText.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
        paraValueText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validate();
                    }
                });
        CommonUITool.registerContextMenu(paraValueText, true);

        setNullBtn = new Button(group, SWT.CHECK);
        setNullBtn.setText(Messages.btnSetNull);
        setNullBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        setNullBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (setNullBtn.getSelection()) {
                            paraValueText.setText(DataType.NULL_EXPORT_FORMAT);
                            paraValueText.setEnabled(false);
                        } else {
                            paraValueText.setEnabled(true);
                        }
                    }
                });
    }

    /**
     * Create file group
     *
     * @param parentComp Composite
     */
    private void createFileComposite(Composite parentComp) {
        Group group = new Group(parentComp, SWT.NONE);
        {
            group.setText(Messages.btnSelectFile);
            group.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            group.setLayout(layout);
        }

        selectFileBtn = new Button(group, SWT.RADIO | SWT.LEFT);
        selectFileBtn.setText(Messages.btnSelectFile);
        selectFileBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        selectFileBtn.setSelection(false);
        selectFileBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (selectFileBtn.getSelection()) {
                            browseBtn.setEnabled(true);
                            fileCharsetCombo.setEnabled(true);

                            inputTextBtn.setSelection(false);
                            paraValueText.setEnabled(false);
                            paraValueText.setText("");
                            setNullBtn.setSelection(false);
                            setNullBtn.setEnabled(false);
                        }
                        validate();
                    }
                });

        Label fileNameLbl = new Label(group, SWT.NONE);
        fileNameLbl.setText(Messages.importFileNameLBL);

        filePathText = new Text(group, SWT.BORDER);
        filePathText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        filePathText.setEnabled(false);

        browseBtn = new Button(group, SWT.NONE);
        browseBtn.setText(Messages.btnBrowse);
        browseBtn.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
        browseBtn.setEnabled(false);
        browseBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                        dlg.setFilterPath(filePathText.getText());
                        String file = dlg.open();
                        if (file != null) {
                            filePathText.setText(file);
                        }
                        validate();
                    }
                });

        final Label fileCharsetLabel = new Label(group, SWT.NONE);
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(group, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.horizontalSpan = 2;
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            String charset = StringUtil.getDefaultCharset();
            fileCharsetCombo.setText(charset);

            fileCharsetCombo.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }
    }

    /** Initial the data */
    private void initial() {
        String str = item.getText(editedColumn);
        boolean isFile = DBAttrTypeFormatter.isFilePath(str);
        boolean isNull = DataType.NULL_EXPORT_FORMAT.equals(str);
        if (isFile) {
            inputTextBtn.setSelection(false);
            paraValueText.setText("");
            paraValueText.setEnabled(false);
            setNullBtn.setSelection(false);
            setNullBtn.setEnabled(false);

            selectFileBtn.setSelection(true);
            browseBtn.setEnabled(true);
            fileCharsetCombo.setEnabled(true);
            filePathText.setText(
                    item.getText(editedColumn).replaceFirst(FieldHandlerUtils.FILE_URL_PREFIX, ""));
            String charSet = (String) item.getData(FILE_CHARSET);
            if (charSet == null) {
                fileCharsetCombo.select(0);
            } else {
                fileCharsetCombo.setText(charSet);
            }
            selectFileBtn.setFocus();
        } else {
            inputTextBtn.setSelection(true);
            if (isNull) {
                setNullBtn.setSelection(true);
                paraValueText.setText("");
                paraValueText.setEnabled(false);
            } else {
                setNullBtn.setSelection(false);
                paraValueText.setText(item.getText(editedColumn));
                paraValueText.setEnabled(true);
            }
            selectFileBtn.setSelection(false);
            browseBtn.setEnabled(false);
            fileCharsetCombo.setEnabled(false);
            paraValueText.setFocus();
        }
    }

    /**
     * Validate the data
     *
     * @return boolean
     */
    private boolean validate() {
        setErrorMessage(null);
        if (inputTextBtn.getSelection()) {
            if (paraType == null) {
                paraType = item.getText(1);
            }
            String data = paraValueText.getText();
            if (!setNullBtn.getSelection() && data.length() > 0) {
                FormatDataResult formatDataResult =
                        DBAttrTypeFormatter.format(
                                DataType.getRealType(paraType),
                                data,
                                false,
                                database.getDatabaseInfo().getCharSet(),
                                true);
                if (!formatDataResult.isSuccess()) {
                    setErrorMessage(Messages.bind(Messages.errTextTypeNotMatch, paraType));
                    return false;
                }
            }
        } else {
            String filePath = filePathText.getText();
            if (filePath.trim().length() == 0) {
                setErrorMessage(Messages.msgSelectFile);
                return false;
            }
            String charsetName = fileCharsetCombo.getText();
            try {
                "".getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                setErrorMessage(Messages.errUnsupportedCharset);
                return false;
            }
        }
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        return true;
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
     * Call this method when press button
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (validate()) {
                if (inputTextBtn.getSelection()) {
                    item.setText(editedColumn, paraValueText.getText());
                    item.setData(FILE_CHARSET, null);
                } else {
                    String filePath = filePathText.getText();
                    item.setText(editedColumn, "file:" + filePath);
                    String charSet = fileCharsetCombo.getText();
                    item.setData(FILE_CHARSET, charSet);
                }
                super.buttonPressed(buttonId);
            }
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleSetPstmtValueDialog);
    }
}
