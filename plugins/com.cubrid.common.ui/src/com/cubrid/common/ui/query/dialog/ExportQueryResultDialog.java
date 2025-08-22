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
package com.cubrid.common.ui.query.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.cubrid.table.progress.ExportDataEditorInput;
import com.cubrid.common.ui.cubrid.table.progress.ExportQueryResultDataViewPart;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.table.FileDialogUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.UIQueryUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Export Query Result
 *
 * @author Kevin.Wang
 */
public class ExportQueryResultDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(ExportQueryResultDialog.class);
    private CubridDatabase database;
    private static final String DEFAULT_FILE_CHARSET = "UTF-8";
    private Button sqlButton;
    private Button csvButton;
    private Button xlsButton;
    private Button xlsxButton;
    private Button txtButton;
    private Text filePathText;
    private Button exportFromCacheBtn = null;
    private Spinner threadCountSpinner;
    private Combo dbCharsetCombo;
    private Combo fileCharsetCombo;
    private Button useFirstAsColumnBtn = null;
    private Combo rowDelimiterCombo;
    private Combo columnDelimiterCombo;
    private Button nullOneButton;
    private Button nullTwoButton;
    private Button nullThreeButton;
    private Button otherButton;
    private Text otherText;
    private static final String[] NULL_VALUES = {"NULL", "\\N", "(NULL)"};
    private String[] columnDelimeter = {",", "\t", "'"};
    private String[] columnDelimeterName = {
        Messages.lblNameComma, Messages.lblNameTab, Messages.lblNameQuote
    };
    private String[] rowDelimeter = {",", StringUtil.CRLF, "\t", "'"};
    private String[] rowDelimeterName = {
        Messages.lblNameComma, Messages.lblNameCRLF, Messages.lblNameTab, Messages.lblNameQuote
    };

    private List<QueryExecuter> queryExecuterList;
    /**
     * The constructor
     *
     * @param parentShell
     * @param database
     * @param queryExecuterList
     */
    public ExportQueryResultDialog(Shell parentShell, List<QueryExecuter> queryExecuterList) {
        super(parentShell);
        this.queryExecuterList = queryExecuterList;
        for (QueryExecuter queryExecuter : queryExecuterList) {
            if (database == null) {
                this.database = queryExecuter.getDatabase();
            }
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
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        createComposite(composite);

        setTitle(Messages.exportDataMsgTitle);
        if (queryExecuterList.size() == 1) {
            setMessage(Messages.msgExportAllResults);
        } else {
            setMessage(Messages.msgExportAllQueryResults);
        }

        return parent;
    }

    /**
     * Create the composite
     *
     * @param parent Composite
     */
    private void createComposite(Composite parent) {
        final boolean isOneResult = queryExecuterList.size() == 1;

        String tableName = null;
        try {
            tableName =
                    UIQueryUtil.getTableNameFromQuery(
                            queryExecuterList.get(0).getConnection().checkAndConnectQuietly(),
                            queryExecuterList.get(0).getOrignQuery());
        } finally {
            if (queryExecuterList.get(0).getConnection().isAutoClosable()) {
                queryExecuterList.get(0).getConnection().close();
            }
        }

        boolean isTable = isOneResult && !StringUtil.isEmpty(tableName);

        Composite composite = new Composite(parent, SWT.NONE);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        // where to export group
        Group whereOptionGroup = new Group(composite, SWT.None);
        whereOptionGroup.setText(Messages.grpWhereExport);
        whereOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        whereOptionGroup.setLayout(new GridLayout(2, false));

        Label fileTypeLabel = new Label(whereOptionGroup, SWT.None);
        fileTypeLabel.setText(Messages.lblFileType);
        fileTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Composite fileTypeComposite = new Composite(whereOptionGroup, SWT.None);
        fileTypeComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        int layoutCount = 0;
        if (isTable) {
            sqlButton = new Button(fileTypeComposite, SWT.RADIO);
            sqlButton.setText("SQL");
            sqlButton.setLayoutData(CommonUITool.createGridData(1, 1, 50, -1));
            sqlButton.setSelection(true);
            sqlButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            widgetDefaultSelected(e);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {
                            useFirstAsColumnBtn.setEnabled(false);
                            setNullWidgetStatus(false);
                            setDelimiterWidgetStatus(false);
                            fileCharsetCombo.setText(DEFAULT_FILE_CHARSET);
                            changeFileSuffix();
                            verify();
                        }
                    });
            layoutCount++;
        }

        csvButton = new Button(fileTypeComposite, SWT.RADIO);
        csvButton.setText("CSV");
        csvButton.setSelection(isTable ? false : true);
        csvButton.setLayoutData(CommonUITool.createGridData(1, 1, 50, -1));
        csvButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        useFirstAsColumnBtn.setEnabled(true);
                        setNullWidgetStatus(true);
                        setDelimiterWidgetStatus(false);
                        fileCharsetCombo.setText(DEFAULT_FILE_CHARSET);
                        changeFileSuffix();
                        verify();
                    }
                });
        layoutCount++;

        xlsButton = new Button(fileTypeComposite, SWT.RADIO);
        xlsButton.setText("XLS");
        xlsButton.setLayoutData(CommonUITool.createGridData(1, 1, 50, -1));
        xlsButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        useFirstAsColumnBtn.setEnabled(true);
                        setNullWidgetStatus(true);
                        setDelimiterWidgetStatus(false);
                        String charset = StringUtil.getDefaultCharset();
                        if (charset != null) {
                            fileCharsetCombo.setText(charset);
                        }
                        changeFileSuffix();
                        verify();
                    }
                });
        layoutCount++;

        xlsxButton = new Button(fileTypeComposite, SWT.RADIO);
        xlsxButton.setText("XLSX");
        xlsxButton.setLayoutData(CommonUITool.createGridData(1, 1, 57, -1));
        xlsxButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        useFirstAsColumnBtn.setEnabled(true);
                        setNullWidgetStatus(true);
                        setDelimiterWidgetStatus(false);
                        fileCharsetCombo.setText(DEFAULT_FILE_CHARSET);
                        changeFileSuffix();
                        verify();
                    }
                });
        layoutCount++;

        txtButton = new Button(fileTypeComposite, SWT.RADIO);
        txtButton.setText("TXT");
        txtButton.setLayoutData(CommonUITool.createGridData(1, 1, 50, -1));
        txtButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        useFirstAsColumnBtn.setEnabled(true);
                        setNullWidgetStatus(true);
                        setDelimiterWidgetStatus(true);
                        fileCharsetCombo.setText(DEFAULT_FILE_CHARSET);
                        changeFileSuffix();
                        verify();
                    }
                });
        layoutCount++;
        fileTypeComposite.setLayout(new GridLayout(layoutCount, false));

        Label pathLabel = new Label(whereOptionGroup, SWT.None);
        pathLabel.setText(Messages.lblFilePath);
        pathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Composite fileComposite = new Composite(whereOptionGroup, SWT.None);
        fileComposite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        fileComposite.setLayout(new GridLayout(2, false));

        filePathText = new Text(fileComposite, SWT.BORDER);
        filePathText.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        Button browseButton = new Button(fileComposite, SWT.None);
        browseButton.setText(Messages.btnBrowse);
        browseButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        browseButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        File file =
                                FileDialogUtils.getDataExportedFile(
                                        getShell(),
                                        new String[] {"*.*"},
                                        new String[] {"*.*"},
                                        null);
                        if (file == null) {
                            return;
                        }
                        filePathText.setText(
                                file.getAbsolutePath() + getFileExtName(getFileType()));
                        verify();
                    }
                });

        Group parsingGroup = new Group(composite, SWT.None);
        parsingGroup.setText(Messages.grpParsingOption);
        parsingGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        parsingGroup.setLayout(new GridLayout(4, false));

        Label threadCountLabel = new Label(parsingGroup, SWT.None);
        threadCountLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, 100, -1));
        threadCountLabel.setText(Messages.lblThreadCount);

        threadCountSpinner = new Spinner(parsingGroup, SWT.BORDER | SWT.LEFT);
        threadCountSpinner.setValues(
                queryExecuterList.size(), 1, queryExecuterList.size(), 0, 1, 1);
        threadCountSpinner.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, 100, -1));

        exportFromCacheBtn = new Button(parsingGroup, SWT.CHECK);
        {
            exportFromCacheBtn.setText(Messages.lblExportFromCache);
            exportFromCacheBtn.setLayoutData(
                    CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 2, 1, -1, -1));
            exportFromCacheBtn.setSelection(false);
            exportFromCacheBtn.setToolTipText(Messages.tipExportFromCache);
        }

        Label dbCharsetLabel = new Label(parsingGroup, SWT.None);
        dbCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, 110, -1));
        dbCharsetLabel.setText(Messages.lblJDBCCharset);

        dbCharsetCombo = new Combo(parsingGroup, SWT.BORDER);
        dbCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 100, 21));
        String dbCharset = database.getDatabaseInfo().getCharSet();
        dbCharsetCombo.setItems(QueryOptions.getAllCharset(null));
        if (dbCharset != null && dbCharset.length() > 0) {
            dbCharsetCombo.setText(dbCharset);
        }
        dbCharsetCombo.setEnabled(false);

        Label fileCharsetLabel = new Label(parsingGroup, SWT.None);
        fileCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(parsingGroup, SWT.BORDER);
        fileCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 100, 21));
        fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
        fileCharsetCombo.setText(DEFAULT_FILE_CHARSET);
        fileCharsetCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        verify();
                    }
                });

        Group dataOptionGroup = new Group(composite, SWT.None);
        dataOptionGroup.setText(Messages.grpDataOptions);
        dataOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        dataOptionGroup.setLayout(new GridLayout(2, false));

        useFirstAsColumnBtn = new Button(dataOptionGroup, SWT.CHECK);
        {
            useFirstAsColumnBtn.setText(Messages.btnFirstLineAsCol);
            GridData gridData = new GridData();
            gridData.widthHint = 400;
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalIndent = 0;
            gridData.horizontalSpan = 2;
            useFirstAsColumnBtn.setLayoutData(gridData);
            useFirstAsColumnBtn.setSelection(true);
        }

        Group delimiterOptionGroup = new Group(dataOptionGroup, SWT.None);
        delimiterOptionGroup.setText(Messages.grpDelimiter);
        delimiterOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        delimiterOptionGroup.setLayout(new GridLayout(2, false));

        Label columnLabel = new Label(delimiterOptionGroup, SWT.None);
        columnLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        columnLabel.setText(Messages.lblCol);

        columnDelimiterCombo = new Combo(delimiterOptionGroup, SWT.BORDER);
        columnDelimiterCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        columnDelimiterCombo.setTextLimit(32);
        columnDelimiterCombo.setItems(columnDelimeterName);
        columnDelimiterCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        verify();
                    }
                });

        Label rowLabel = new Label(delimiterOptionGroup, SWT.None);
        rowLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        rowLabel.setText(Messages.lblRow);

        rowDelimiterCombo = new Combo(delimiterOptionGroup, SWT.BORDER);
        rowDelimiterCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        rowDelimiterCombo.setTextLimit(32);
        rowDelimiterCombo.setItems(rowDelimeterName);
        rowDelimiterCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        verify();
                    }
                });

        Group nullValueGroup = new Group(dataOptionGroup, SWT.None);
        nullValueGroup.setText(Messages.grpNullOption);
        nullValueGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        nullValueGroup.setLayout(new GridLayout(3, false));

        nullOneButton = new Button(nullValueGroup, SWT.RADIO);
        nullOneButton.setText(NULL_VALUES[0]);
        nullOneButton.setLayoutData(CommonUITool.createGridData(1, 1, 70, -1));
        nullOneButton.setSelection(true);

        nullTwoButton = new Button(nullValueGroup, SWT.RADIO);
        nullTwoButton.setText(NULL_VALUES[1]);
        nullTwoButton.setLayoutData(CommonUITool.createGridData(2, 1, 70, -1));

        nullThreeButton = new Button(nullValueGroup, SWT.RADIO);
        nullThreeButton.setText(NULL_VALUES[2]);
        nullThreeButton.setLayoutData(CommonUITool.createGridData(1, 1, 70, -1));

        otherButton = new Button(nullValueGroup, SWT.RADIO);
        otherButton.setText(Messages.btnOther);
        otherButton.setLayoutData(CommonUITool.createGridData(1, 1, 70, -1));
        otherButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        verify();
                    }
                });

        otherText = new Text(nullValueGroup, SWT.BORDER);
        otherText.setLayoutData(CommonUITool.createGridData(1, 1, 60, 14));
        otherText.setTextLimit(64);
        otherText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        verify();
                    }
                });

        if (sqlButton != null && sqlButton.getSelection()) {
            useFirstAsColumnBtn.setEnabled(false);
            setDelimiterWidgetStatus(false);
            setNullWidgetStatus(false);
        } else {
            useFirstAsColumnBtn.setEnabled(true);
            setDelimiterWidgetStatus(false);
            setNullWidgetStatus(true);
        }
    }

    private void changeFileSuffix() {
        String fullName = filePathText.getText();
        if (StringUtil.isEmpty(fullName)) {
            return;
        }
        int suffixStart = fullName.lastIndexOf(".");
        if (suffixStart > 0) {
            fullName = fullName.substring(0, suffixStart);
        }
        filePathText.setText(fullName + getFileExtName(getFileType()));
    }

    /**
     * Set null value widget status
     *
     * @param isEnable boolean
     */
    private void setNullWidgetStatus(boolean isEnable) {
        nullOneButton.setEnabled(isEnable);
        nullTwoButton.setEnabled(isEnable);
        nullThreeButton.setEnabled(isEnable);
        otherButton.setEnabled(isEnable);
        otherText.setEnabled(isEnable);
    }

    /**
     * Set Delimiter widget status
     *
     * @param isEnable boolean
     */
    private void setDelimiterWidgetStatus(boolean isEnable) {
        rowDelimiterCombo.setEnabled(isEnable);
        columnDelimiterCombo.setEnabled(isEnable);
        if (isEnable) {
            rowDelimiterCombo.select(1);
            columnDelimiterCombo.select(0);
        } else {
            rowDelimiterCombo.setText("");
            columnDelimiterCombo.setText("");
        }
    }

    /** Verify */
    public void verify() {
        if (getButton(IDialogConstants.OK_ID) == null) {
            return;
        }
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        if (filePathText.getText() == null || "".equals(filePathText.getText().trim())) {
            setErrorMessage(Messages.exportSelectFileERRORMSG);
            return;
        }

        String charsetName = fileCharsetCombo.getText();
        try {
            "".getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            setErrorMessage(Messages.errUnsupportedCharset);
            return;
        }
        if (rowDelimiterCombo.isEnabled()) {
            if (rowDelimiterCombo.getText() == null
                    || "".equals(rowDelimiterCombo.getText().trim())) {
                return;
            }
        }

        if (columnDelimiterCombo.isEnabled()) {
            if (columnDelimiterCombo.getText() == null
                    || "".equals(columnDelimiterCombo.getText().trim())) {
                return;
            }
        }

        if (otherButton.isEnabled() && otherButton.getSelection()) {
            if (otherText.getText() == null || "".equals(otherText.getText().trim())) {
                return;
            }
        }

        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.exportShellTitle);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.exportButtonName, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnClose, false);
        verify();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            ExportConfig exportConfig = new ExportConfig();
            exportConfig.setUsePagination(false);
            exportConfig.setExportData(true);
            exportConfig.setExportFileType(getFileType());
            exportConfig.setExportType(ExportConfig.EXPORT_TO_FILE);

            exportConfig.setExportFromCache(exportFromCacheBtn.getSelection());
            exportConfig.setThreadCount(threadCountSpinner.getSelection());
            exportConfig.setFileCharset(fileCharsetCombo.getText());
            exportConfig.setFirstRowAsColumnName(useFirstAsColumnBtn.getSelection());
            exportConfig.setRowDelimeter(getRowDelimeter());
            exportConfig.setColumnDelimeter(getColumnDelimeter());

            String fileFullName = filePathText.getText();
            int start = fileFullName.lastIndexOf(File.separator);
            String fileName = fileFullName.substring(start + 1, fileFullName.length());
            fileName = fileName.replace(getFileExtName(getFileType()), "");
            String dir = fileFullName.substring(0, start);
            exportConfig.setDataFileFolder(dir);

            String nullValue = "";
            if (nullOneButton.getSelection()) {
                nullValue = NULL_VALUES[0];
            } else if (nullTwoButton.getSelection()) {
                nullValue = NULL_VALUES[1];
            } else if (nullThreeButton.getSelection()) {
                nullValue = NULL_VALUES[2];
            } else if (otherButton.getSelection()) {
                nullValue = otherText.getText();
            }
            exportConfig.setNULLValueTranslation(nullValue);
            CubridDatabase database = null;
            for (QueryExecuter queryExecuter : queryExecuterList) {
                exportConfig.setSQL(fileName, queryExecuter.getOrignQuery());
                // TODO Need to refactoring without ResultSetDataCache
                //				exportConfig.setResultSetDataCache(queryExecuter.getTableDataCache());
                exportConfig.setDataFilePath(fileName, fileFullName);
                if (database == null) {
                    database = queryExecuter.getDatabase();
                }
            }

            ExportDataEditorInput input = new ExportDataEditorInput();
            input.setDatabase(database);
            input.setExportConfig(exportConfig);
            try {
                PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .openEditor(input, ExportQueryResultDataViewPart.ID);
            } catch (Exception e) {
                CommonUITool.openErrorBox(getShell(), e.getMessage());
                LOGGER.error("", e);
            }
        }
        super.buttonPressed(buttonId);
    }

    /**
     * Get the file type
     *
     * @return int
     */
    private int getFileType() {
        int fileType = ExportConfig.FILE_TYPE_SQL;
        if (sqlButton != null && sqlButton.getSelection()) {
            fileType = ExportConfig.FILE_TYPE_SQL;
        } else if (csvButton.getSelection()) {
            fileType = ExportConfig.FILE_TYPE_CSV;
        } else if (xlsButton.getSelection()) {
            fileType = ExportConfig.FILE_TYPE_XLS;
        } else if (xlsxButton.getSelection()) {
            fileType = ExportConfig.FILE_TYPE_XLSX;
        } else if (txtButton.getSelection()) {
            fileType = ExportConfig.FILE_TYPE_TXT;
        }
        return fileType;
    }

    /**
     * Get the column delimiter
     *
     * @return String
     */
    private String getColumnDelimeter() {
        if (columnDelimiterCombo.getSelectionIndex() < 0) {
            return columnDelimiterCombo.getText();
        } else {
            int index = columnDelimiterCombo.getSelectionIndex();
            return columnDelimeter[index];
        }
    }

    /**
     * Get the row delimiter
     *
     * @return String
     */
    private String getRowDelimeter() {
        if (rowDelimiterCombo.getSelectionIndex() < 0) {
            return rowDelimiterCombo.getText();
        } else {
            int index = rowDelimiterCombo.getSelectionIndex();
            return rowDelimeter[index];
        }
    }

    /**
     * Get export file extension name
     *
     * @return String
     */
    private String getFileExtName(int fileType) {
        if (fileType == 1) {
            return ".sql";
        } else if (fileType == 2) {
            return ".csv";
        } else if (fileType == 3) {
            return ".xls";
        } else if (fileType == 4) {
            return ".xlsx";
        } else if (fileType == 5) {
            return ".obs";
        } else {
            return ".txt";
        }
    }
}
