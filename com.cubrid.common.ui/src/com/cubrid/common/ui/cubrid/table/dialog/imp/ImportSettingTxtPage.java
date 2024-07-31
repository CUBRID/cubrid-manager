/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * The Import Setting Page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 30, 2012 created by Kevin.Wang
 */
public class ImportSettingTxtPage extends AbsImportSettingPage
        implements ISelectionChangedListener {
    public static final String PAGE_NAME = ImportSettingTxtPage.class.getName();
    private CubridDatabase database;
    private FileToTableMappingComposite mappingComposite;
    private Button nullOneButton;
    private Button nullTwoButton;
    private Button nullThreeButton;
    private Button otherButton;
    private Text otherText;

    private Button breakButton;
    private Button ignoreButton;
    private Combo dbCharsetCombo;
    private Combo fileCharsetCombo;
    private Combo rowDelimiterCombo;
    private Combo columnDelimiterCombo;
    private Text lineText;
    private Spinner threadCountSpinner;
    private Spinner commitCountSpinner;

    private Button importClobButton;
    private Button importBlobButton;

    private String[] columnDelimeter = {",", "\t", "'"};
    private String[] columnDelimeterName = {
        Messages.lblNameComma, Messages.lblNameTab, Messages.lblNameQuote
    };

    private String[] rowDelimeter = {",", "\t", "'", StringUtil.CR, StringUtil.LF, StringUtil.CRLF};
    private String[] rowDelimeterName = {
        Messages.lblNameComma,
        Messages.lblNameTab,
        Messages.lblNameQuote,
        Messages.lblNameCR,
        Messages.lblNameLF,
        Messages.lblNameCRLF
    };

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    protected ImportSettingTxtPage(CubridDatabase database) {
        super(PAGE_NAME, Messages.titleImportSettingPage, null);
        setTitle(Messages.titleImportStep2);
        setMessage(Messages.msgImportSettingPage);

        this.database = database;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        setControl(container);

        mappingComposite = new FileToTableMappingComposite(container, SWT.NONE, database, this);
        mappingComposite.addSelectionChangedListener(this);
        mappingComposite.setLayout(new GridLayout());
        FormData leftData = new FormData();
        leftData.top = new FormAttachment(0, 5);
        leftData.bottom = new FormAttachment(100, 0);
        leftData.left = new FormAttachment(0, 5);
        leftData.right = new FormAttachment(55, 0);
        mappingComposite.setLayoutData(leftData);

        Composite rightComposite = new Composite(container, SWT.NONE);
        FormData rightData = new FormData();
        rightData.top = new FormAttachment(0, 5);
        rightData.bottom = new FormAttachment(100, 0);
        rightData.left = new FormAttachment(55, 0);
        rightData.right = new FormAttachment(100, -5);
        rightComposite.setLayoutData(rightData);
        GridLayout rightCompositeLayout = new GridLayout();
        rightCompositeLayout.verticalSpacing = 10;
        rightComposite.setLayout(rightCompositeLayout);

        Group dataOptionGroup = new Group(rightComposite, SWT.None);
        dataOptionGroup.setText(Messages.grpDataOptions);
        dataOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        dataOptionGroup.setLayout(new GridLayout(2, false));

        Group nullValueGroup = new Group(dataOptionGroup, SWT.None);
        nullValueGroup.setText(Messages.grpNulls);
        nullValueGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        nullValueGroup.setLayout(new GridLayout(3, false));

        nullOneButton = new Button(nullValueGroup, SWT.CHECK);
        nullOneButton.setText("'NULL'");
        nullOneButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        nullTwoButton = new Button(nullValueGroup, SWT.CHECK);
        nullTwoButton.setText("'\\N'");
        nullTwoButton.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));

        nullThreeButton = new Button(nullValueGroup, SWT.CHECK);
        nullThreeButton.setText("'(NULL)'");
        nullThreeButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        otherButton = new Button(nullValueGroup, SWT.CHECK);
        otherButton.setText(Messages.btnOther);
        otherButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        otherButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        otherText.setEnabled(otherButton.getSelection());
                    }
                });

        otherText = new Text(nullValueGroup, SWT.BORDER);
        otherText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        otherText.setTextLimit(64);
        otherText.setEnabled(false);

        Group delimiterOptionGroup = new Group(dataOptionGroup, SWT.None);
        delimiterOptionGroup.setText(Messages.grpDelimiter);
        delimiterOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        delimiterOptionGroup.setLayout(new GridLayout(2, false));

        Label columnLabel = new Label(delimiterOptionGroup, SWT.None);
        columnLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        columnLabel.setText(Messages.lblCol);

        columnDelimiterCombo = new Combo(delimiterOptionGroup, SWT.BORDER);
        columnDelimiterCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        columnDelimiterCombo.setTextLimit(32);
        columnDelimiterCombo.setItems(columnDelimeterName);
        columnDelimiterCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        if (columnDelimiterCombo.getText().length() == 0) {
                            setErrorMessage(Messages.msgErrorSeparatorEmpty);
                            return;
                        }
                        if (columnDelimiterCombo.getText().indexOf("\"") >= 0
                                || columnDelimiterCombo.getText().indexOf("\n") >= 0) {
                            setErrorMessage(Messages.errorSeparatorInvalid);
                            return;
                        }

                        ImportConfig importConfig = getImportDataWizard().getImportConfig();
                        if (!getColumnDelimeter().equals(importConfig.getColumnDelimiter())) {
                            mappingComposite.resetMapResult();
                            validate();
                        }

                        getImportDataWizard()
                                .getImportConfig()
                                .setColumnDelimiter(getColumnDelimeter());
                        setErrorMessage(null);
                    }
                });

        Label rowLabel = new Label(delimiterOptionGroup, SWT.None);
        rowLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        rowLabel.setText(Messages.lblRow);

        rowDelimiterCombo = new Combo(delimiterOptionGroup, SWT.BORDER);
        rowDelimiterCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        rowDelimiterCombo.setTextLimit(32);
        rowDelimiterCombo.setItems(rowDelimeterName);
        rowDelimiterCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        if (rowDelimiterCombo.getText().length() == 0) {
                            setErrorMessage(Messages.msgErrorSeparatorEmpty);
                            return;
                        }
                        if (rowDelimiterCombo.getText().indexOf("\"") >= 0) {
                            setErrorMessage(Messages.errorSeparatorInvalid);
                            return;
                        }

                        ImportConfig importConfig = getImportDataWizard().getImportConfig();
                        if (!getRowdelimeter().equals(importConfig.getRowDelimiter())) {
                            mappingComposite.resetMapResult();
                            validate();
                        }

                        getImportDataWizard().getImportConfig().setRowDelimiter(getRowdelimeter());
                        setErrorMessage(null);
                    }
                });

        Composite errorComposite = new Composite(dataOptionGroup, SWT.None);
        errorComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        errorComposite.setLayout(new GridLayout(3, false));

        Label errorLabel = new Label(errorComposite, SWT.None);
        errorLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        errorLabel.setText(Messages.lblErrorHandle);

        ignoreButton = new Button(errorComposite, SWT.RADIO);
        ignoreButton.setText(Messages.btnIgnoreSetToNull);
        ignoreButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        ignoreButton.setSelection(true);

        breakButton = new Button(errorComposite, SWT.RADIO);
        breakButton.setText(Messages.btnBreakImport);
        breakButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Group enCodingOptionGroup = new Group(rightComposite, SWT.None);
        enCodingOptionGroup.setText(Messages.grpEncodingOption);
        enCodingOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        enCodingOptionGroup.setLayout(new GridLayout(4, false));

        Label dbCharsetLabel = new Label(enCodingOptionGroup, SWT.None);
        dbCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        dbCharsetLabel.setText(Messages.lblDBCharset);

        dbCharsetCombo = new Combo(enCodingOptionGroup, SWT.BORDER);
        dbCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 50, 21));
        dbCharsetCombo.setEnabled(false);

        Label fileCharsetLabel = new Label(enCodingOptionGroup, SWT.None);
        fileCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(enCodingOptionGroup, SWT.BORDER);
        fileCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 50, 21));
        fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
        fileCharsetCombo.select(0);
        fileCharsetCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        getImportDataWizard()
                                .getImportConfig()
                                .setFilesCharset(fileCharsetCombo.getText());
                    }
                });

        Group importOptionGroup = new Group(rightComposite, SWT.None);
        importOptionGroup.setText(Messages.grpImportOptions);
        importOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        importOptionGroup.setLayout(new GridLayout(2, false));

        Label lineLabel = new Label(importOptionGroup, SWT.None);
        lineLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        lineLabel.setText(Messages.lblTotalLine);

        lineText = new Text(importOptionGroup, SWT.BORDER);
        lineText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        lineText.setText("0");
        lineText.setEditable(false);

        Label threadCountLabel = new Label(importOptionGroup, SWT.None);
        threadCountLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        threadCountLabel.setText(Messages.lblThreadNum);

        threadCountSpinner = new Spinner(importOptionGroup, SWT.BORDER);
        threadCountSpinner.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        threadCountSpinner.setMaximum(ImportConfig.MAX_IMPORT_THREAD_COUNT);
        threadCountSpinner.setMinimum(ImportConfig.MIN_IMPORT_THREAD_COUNT);
        threadCountSpinner.setSelection(ImportConfig.DEFAULT_IMPORT_THREAD_COUNT);

        Label commitCountLabel = new Label(importOptionGroup, SWT.None);
        commitCountLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        commitCountLabel.setText(Messages.lblCommitCount);

        commitCountSpinner = new Spinner(importOptionGroup, SWT.BORDER);
        commitCountSpinner.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        commitCountSpinner.setMaximum(ImportConfig.MAX_IMPORT_COMMIT_COUNT);
        commitCountSpinner.setMinimum(ImportConfig.MIN_IMPORT_COMMIT_COUNT);
        commitCountSpinner.setIncrement(ImportConfig.IMPORT_COMMIT_STEP);
        commitCountSpinner.setSelection(ImportConfig.DEFAULT_IMPORT_COMMIT_COUNT);

        Group lobOptionGroup = new Group(rightComposite, SWT.None);
        lobOptionGroup.setText(Messages.grpLobOptions);
        lobOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        lobOptionGroup.setLayout(new GridLayout(2, false));

        importClobButton = new Button(lobOptionGroup, SWT.CHECK);
        importClobButton.setText(Messages.btnImportClob);
        importClobButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        importClobButton.setSelection(true);

        importBlobButton = new Button(lobOptionGroup, SWT.CHECK);
        importBlobButton.setText(Messages.btnImportBlob);
        importBlobButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        importBlobButton.setSelection(true);
    }

    private String getColumnDelimeter() {
        if (columnDelimiterCombo.getSelectionIndex() < 0) {
            return columnDelimiterCombo.getText();
        } else {
            int index = columnDelimiterCombo.getSelectionIndex();
            return columnDelimeter[index];
        }
    }

    private String getRowdelimeter() {
        if (rowDelimiterCombo.getSelectionIndex() < 0) {
            return rowDelimiterCombo.getText();
        } else {
            int index = rowDelimiterCombo.getSelectionIndex();
            return rowDelimeter[index];
        }
    }

    /**
     * Set the column delimeter
     *
     * @param delimeter
     */
    private void setColumnDelimeter(String delimeter) {

        if (delimeter == null || delimeter.length() == 0) {
            columnDelimiterCombo.deselectAll();
            return;
        }

        boolean isFound = false;
        for (int i = 0; i < columnDelimeter.length; i++) {
            if (columnDelimeter[i].equals(delimeter)) {
                columnDelimiterCombo.select(i);
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            columnDelimiterCombo.setText(delimeter);
        }
    }

    /**
     * Set the row delimeter
     *
     * @param delimeter
     */
    private void setRowDelimeter(String delimeter) {

        if (delimeter == null || delimeter.length() == 0) {
            rowDelimiterCombo.deselectAll();
            return;
        }

        boolean isFound = false;
        for (int i = 0; i < rowDelimeter.length; i++) {
            if (rowDelimeter[i].equals(delimeter)) {
                rowDelimiterCombo.select(i);
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            rowDelimiterCombo.setText(delimeter);
        }
    }

    /** Init the data */
    protected void init() {
        ImportConfig importConfig = getImportDataWizard().getImportConfig();

        if (importConfig.getNullValueList().contains("NULL")) {
            nullOneButton.setSelection(true);
            importConfig.getNullValueList().remove("NULL");
        } else {
            nullOneButton.setSelection(false);
        }

        if (importConfig.getNullValueList().contains("\\N")) {
            nullTwoButton.setSelection(true);
            importConfig.getNullValueList().remove("\\N");
        } else {
            nullTwoButton.setSelection(false);
        }

        if (importConfig.getNullValueList().contains("(NULL)")) {
            nullThreeButton.setSelection(true);
            importConfig.getNullValueList().remove("(NULL)");
        } else {
            nullThreeButton.setSelection(false);
        }

        if (importConfig.getNullValueList().size() > 0) {
            StringBuilder otherSB = new StringBuilder();
            for (int i = 0; i < importConfig.getNullValueList().size(); i++) {
                otherSB.append(importConfig.getNullValueList().get(i));
                if (i + 1 < importConfig.getNullValueList().size()) {
                    otherSB.append(",");
                }
            }
            otherButton.setSelection(true);
            otherText.setText(otherSB.toString());
            otherText.setEnabled(true);
        } else {
            otherButton.setSelection(false);
            otherText.setText("");
            otherText.setEnabled(false);
        }

        dbCharsetCombo.setText(database.getDatabaseInfo().getCharSet());
        fileCharsetCombo.setText(importConfig.getFilesCharset());
        lineText.setText("0");
        threadCountSpinner.setSelection(importConfig.getThreadCount());
        commitCountSpinner.setSelection(importConfig.getCommitLine());
        setColumnDelimeter(importConfig.getColumnDelimiter());
        setRowDelimeter(importConfig.getRowDelimiter());

        /*Error handle*/
        if (ImportConfig.ERROR_HANDLE_BREAK == importConfig.getErrorHandle()) {
            breakButton.setSelection(true);
        } else {
            breakButton.setSelection(false);
        }
        if (ImportConfig.ERROR_HANDLE_IGNORE == importConfig.getErrorHandle()) {
            ignoreButton.setSelection(true);
        } else {
            ignoreButton.setSelection(false);
        }

        importClobButton.setSelection(importConfig.isImportCLobData());
        importBlobButton.setSelection(importConfig.isImportBLobData());

        mappingComposite.init();
    }

    public boolean validate() {
        setErrorMessage(null);
        setPageComplete(false);

        ImportConfig importConfig = getImportDataWizard().getImportConfig();
        List<DefaultSchemaNode> result = mappingComposite.getSelectedTableNode();

        if (result.size() == 0) {
            setErrorMessage(Messages.errNoSelectedTable);
            return false;
        }

        for (DefaultSchemaNode node : result) {
            Object value = node.getData(ImportObjectLabelProvider.IS_MAPPED);
            if (value == null || !Boolean.parseBoolean(value.toString())) {
                setErrorMessage(Messages.bind(Messages.errTableSetting, node.getName()));
                return false;
            }
            TableConfig tableConfig = importConfig.getTableConfig(node.getName());
            if (tableConfig != null) {
                String filePath = tableConfig.getFilePath();
                if (!StringUtil.isEmpty(filePath)) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        setErrorMessage(Messages.bind(Messages.errFileNotExist, filePath));

                        return false;
                    }
                }
            }
        }

        setPageComplete(true);
        return true;
    }

    protected void handlePageLeaving(PageChangingEvent event) {
        if ((event.getTargetPage() instanceof ImportTypePage)) {
            if (!CommonUITool.openConfirmBox(Messages.importWizardBackConfirmMsg)) {
                event.doit = false;
            } else {
                clearOptions();
            }
            return;
        }

        ImportConfig importConfig = getImportDataWizard().getImportConfig();
        /*NULL setting*/
        importConfig.getNullValueList().clear();
        if (nullOneButton.getSelection()) {
            importConfig.getNullValueList().add("NULL");
        }
        if (nullTwoButton.getSelection()) {
            importConfig.getNullValueList().add("\\N");
        }
        if (nullThreeButton.getSelection()) {
            importConfig.getNullValueList().add("(NULL)");
        }
        if (otherButton.getSelection() && otherText.getText().length() > 0) {
            String[] others = otherText.getText().split(",");
            for (String value : others) {
                if (importConfig.getNullValueList().indexOf(value) < 0) {
                    importConfig.getNullValueList().add(value);
                }
            }
        }

        /*Error handle*/
        if (breakButton.getSelection()) {
            importConfig.setErrorHandle(ImportConfig.ERROR_HANDLE_BREAK);
        }
        if (ignoreButton.getSelection()) {
            importConfig.setErrorHandle(ImportConfig.ERROR_HANDLE_IGNORE);
        }

        importConfig.setFilesCharset(fileCharsetCombo.getText());
        importConfig.setRowDelimiter(getRowdelimeter());
        importConfig.setColumnDelimiter(getColumnDelimeter());
        importConfig.setThreadCount(threadCountSpinner.getSelection());
        importConfig.setCommitLine(commitCountSpinner.getSelection());

        importConfig.setImportCLobData(importClobButton.getSelection());
        importConfig.setImportBLobData(importBlobButton.getSelection());

        /*Update the selected table*/
        List<DefaultSchemaNode> selectedNodeList = mappingComposite.getSelectedTableNode();
        List<String> removedList = new ArrayList<String>();
        for (String tableName : importConfig.getSelectedMap().keySet()) {
            boolean isChecked = false;
            for (DefaultSchemaNode node : selectedNodeList) {
                if (tableName.equals(node.getName())) {
                    isChecked = true;
                    break;
                }
            }
            if (!isChecked) {
                removedList.add(tableName);
            }
        }
        for (String tableName : removedList) {
            importConfig.deleteTableConfig(tableName);
        }
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.cubrid.table.dialog.imp.ISelectionChangedListener#selectionChanged()
     */
    public void selectionChanged() {
        lineText.setText(String.valueOf(mappingComposite.getLineCount()));
        if (!validate()) {
            setPageComplete(false);
        } else {
            setPageComplete(true);
        }
    }

    /** clear all options */
    public void clearOptions() {
        this.setInited(false);
    }
}
