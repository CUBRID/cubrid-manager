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
package com.cubrid.common.ui.query.dialog;

import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * View and update the column detail
 *
 * @author robin 2009-7-6
 */
public class RowDetailDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(RowDetailDialog.class);

    private final List<ColumnInfo> columnInfoList;
    private final TableItem dataItem;
    private final QueryExecuter qe;
    private StyledText columnValueText;
    private Combo columnCombo;
    private final String columnName;
    private int selComboIndex = -1;
    private Combo fileCharsetCombo;
    private Button setNullBtn;
    private Button importBtn;
    private Button exportBtn;
    private final Map<String, CellValue> dataMap;
    private final String dbCharset;
    private Text columnTypeTxt;

    /**
     * The constructor
     *
     * @param parentShell
     * @param allColumnList
     * @param dataMap
     * @param tableItem
     * @param columnName
     * @param qe
     */
    public RowDetailDialog(
            Shell parentShell,
            List<ColumnInfo> allColumnList,
            Map<String, CellValue> dataMap,
            TableItem tableItem,
            String columnName,
            QueryExecuter qe) {
        super(parentShell);
        this.columnName = columnName;
        this.columnInfoList = allColumnList;
        this.dataItem = tableItem;
        this.qe = qe;
        this.dataMap = dataMap;
        this.dbCharset = qe.getDatabase().getDatabaseInfo().getCharSet();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        final Composite composite = new Composite(parentComp, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            layout.numColumns = 2;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        Label columnNameLabel = new Label(composite, SWT.NONE);
        columnNameLabel.setText(Messages.lblColumnName);

        columnCombo = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY);
        columnCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        columnCombo.setVisibleItemCount(10);
        columnCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        int index = columnCombo.getSelectionIndex();
                        if (selComboIndex >= 0
                                && isValueChange()
                                && CommonUITool.openConfirmBox(
                                        getShell(), Messages.cfmUpdateChangedValue)) {
                            final boolean updateDB =
                                    !QueryExecuter.isNewInsertedRecordItem(dataItem);
                            update(
                                    columnValueText.getText(),
                                    fileCharsetCombo.getText(),
                                    dataItem.getText(1),
                                    updateDB);
                            setUpdateButtonEnable();
                        }
                        selComboIndex = index;

                        ColumnInfo columnInfo = columnInfoList.get(selComboIndex);
                        columnTypeTxt.setText(columnInfo.getComleteType());
                        String columnValue = dataItem.getText(selComboIndex + 1);
                        boolean isNull =
                                DataType.VALUE_NULL.equals(
                                        dataItem.getData("" + (selComboIndex + 1)));
                        if (isNull) {
                            columnValueText.setText(DataType.NULL_EXPORT_FORMAT);
                            setNullBtn.setSelection(true);
                        } else {
                            columnValueText.setText(columnValue);
                            setNullBtn.setSelection(false);
                        }

                        importBtn.setEnabled(false);
                        importBtn.setVisible(false);
                        exportBtn.setEnabled(false);
                        fileCharsetCombo.setEnabled(false);

                        String colType = columnInfo.getType();
                        boolean isMuchValueType =
                                DBAttrTypeFormatter.isMuchValueType(
                                        columnInfo.getComleteType(), -1);
                        boolean isByteType =
                                DataType.DATATYPE_BLOB.equals(colType)
                                        || DataType.DATATYPE_BIT.equals(colType)
                                        || DataType.DATATYPE_BIT_VARYING.equals(colType);

                        boolean isCanExport = false;
                        if ((DataType.DATATYPE_BLOB.equals(colType)
                                || DataType.DATATYPE_CLOB.equals(colType))) {
                            isCanExport = isCanUpdate() && !isNull;
                        } else if (DataType.DATATYPE_BIT.equals(colType)
                                || DataType.DATATYPE_BIT_VARYING.equals(colType)) {
                            isCanExport =
                                    (isCanUpdate()
                                                    || !DataType.BIT_EXPORT_FORMAT.equals(
                                                            columnValue))
                                            && !isNull;
                        } else {
                            isCanExport = !isNull && isMuchValueType;
                        }

                        if (isCanUpdate()) {
                            if (DataType.DATATYPE_OID.equals(colType)
                                    || DataType.DATATYPE_CLASS.equals(colType)) {
                                columnValueText.setEnabled(false);
                                setNullBtn.setEnabled(false);
                            } else {
                                columnValueText.setEnabled(true);
                                setNullBtn.setEnabled(true);
                                importBtn.setEnabled(isMuchValueType);
                                exportBtn.setEnabled(isCanExport);
                                boolean isCharsetEnabled =
                                        (isMuchValueType || isCanExport) && !isNull && !isByteType;
                                fileCharsetCombo.setEnabled(isCharsetEnabled);
                            }
                        } else {
                            setNullBtn.setEnabled(false);
                            importBtn.setEnabled(false);
                            exportBtn.setEnabled(isCanExport);
                            boolean isCharsetEnabled = isCanExport && !isNull && !isByteType;
                            fileCharsetCombo.setEnabled(isCharsetEnabled);
                        }
                        setUpdateButtonEnable();
                    }
                });

        Label columnTypeLabel = new Label(composite, SWT.NONE);
        columnTypeLabel.setText(Messages.lblColumnType);

        columnTypeTxt = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        columnTypeTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createColumnValueGroup(composite);
        createFileGroup(composite);
        initial();
        setTitle(Messages.titleRowDetailDialog);
        setMessage(Messages.msgRowDetailDialog);
        return parentComp;
    }

    /**
     * Create column value group
     *
     * @param parent Composite
     */
    private void createColumnValueGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        {
            group.setText(Messages.grpColumnValue);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.horizontalSpan = 2;
            group.setLayoutData(gridData);

            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            group.setLayout(layout);
        }

        final Label descLabel = new Label(group, SWT.NONE);
        {
            descLabel.setText(Messages.lblColumnValue);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 2;
            descLabel.setLayoutData(gd);
        }

        {
            if (isCanUpdate()) {
                columnValueText =
                        new StyledText(
                                group,
                                SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
                CommonUITool.registerContextMenu(columnValueText, true);
            } else {
                columnValueText =
                        new StyledText(
                                group,
                                SWT.WRAP
                                        | SWT.MULTI
                                        | SWT.BORDER
                                        | SWT.V_SCROLL
                                        | SWT.H_SCROLL
                                        | SWT.READ_ONLY);
                CommonUITool.registerContextMenu(columnValueText, false);
            }
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 2;
            gd.heightHint = 300;
            columnValueText.setLayoutData(gd);
            columnValueText.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            setUpdateButtonEnable();
                        }
                    });
        }

        setNullBtn = new Button(group, SWT.CHECK);
        {
            setNullBtn.setText(com.cubrid.common.ui.cubrid.table.Messages.btnSetNull);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 2;
            setNullBtn.setLayoutData(gd);
            setNullBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            if (setNullBtn.getSelection()) {
                                columnValueText.setText(DataType.NULL_EXPORT_FORMAT);
                            } else {
                                columnValueText.setText(dataItem.getText(selComboIndex + 1));
                            }
                        }
                    });
        }
    }

    /**
     * Create file group
     *
     * @param parent Composite
     */
    private void createFileGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        {
            group.setText(Messages.grpSelectFile);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.horizontalSpan = 2;
            group.setLayoutData(gridData);

            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            group.setLayout(layout);
        }

        final Label fileCharsetLabel = new Label(group, SWT.NONE);
        fileCharsetLabel.setText(com.cubrid.common.ui.cubrid.table.Messages.lblFileCharset);

        fileCharsetCombo = new Combo(group, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            String charset = StringUtil.getDefaultCharset();
            fileCharsetCombo.setText(charset);

            fileCharsetCombo.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            setErrorMessage(null);
                            String charsetName = fileCharsetCombo.getText();
                            try {
                                "".getBytes(charsetName);
                            } catch (UnsupportedEncodingException e) {
                                setErrorMessage(
                                        com.cubrid.common.ui.cubrid.table.Messages
                                                .errUnsupportedCharset);
                                return;
                            }
                        }
                    });
        }

        Composite composite = new Composite(group, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 5;
        composite.setLayout(rowLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.END;
        gridData.horizontalSpan = 2;
        composite.setLayoutData(gridData);

        importBtn = new Button(composite, SWT.PUSH);
        importBtn.setText(Messages.btnImport);
        importBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FileDialog dialog =
                                new FileDialog(getShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
                        String filepath =
                                CommonUIPlugin.getSettingValue(TableUtil.EXPORT_FILE_PATH_KEY);
                        if (filepath != null) {
                            dialog.setFilterPath(filepath);
                        }
                        dialog.setFilterExtensions(new String[] {"*.*"});
                        dialog.setFilterNames(new String[] {"*.*"});
                        String filePath = dialog.open();
                        if (filePath != null) {
                            setNullBtn.setSelection(false);
                            columnValueText.setText(FieldHandlerUtils.FILE_URL_PREFIX + filePath);
                            File file = new File(filePath);
                            CommonUIPlugin.putSettingValue(
                                    TableUtil.EXPORT_FILE_PATH_KEY, file.getParent());
                        }
                    }
                });

        exportBtn = new Button(composite, SWT.PUSH);
        exportBtn.setText(Messages.btnExport);
        exportBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!validate()) {
                            return;
                        }
                        File file =
                                TableUtil.getSavedFile(
                                        getShell(),
                                        new String[] {"*.*"},
                                        new String[] {"*.*"},
                                        null,
                                        null,
                                        null);
                        if (null != file && file.getName().length() > 0) {
                            exportData(file.getPath());
                        }
                    }
                });
    }

    /** initializes some values */
    private void initial() {
        int index = 0;
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            columnCombo.add(columnInfo.getName());

            if (columnInfo.getName().equals(columnName)) {
                index = i;
            }
        }
        columnCombo.select(index);
        boolean isNull = DataType.VALUE_NULL.equals(dataItem.getData("" + (index + 1)));
        if (isNull) {
            columnValueText.setText(DataType.NULL_EXPORT_FORMAT);
        } else {
            columnValueText.setText(dataItem.getText(index + 1));
        }
        selComboIndex = index;
    }

    /**
     * Validate the data validation
     *
     * @return boolean
     */
    private boolean validate() {
        ColumnInfo columnInfo = columnInfoList.get(selComboIndex);
        String value = columnValueText.getText();
        if (DataType.NULL_EXPORT_FORMAT.equals(value)) {
            return true;
        }
        String completedType = columnInfo.getComleteType();
        boolean isMuchValue = DBAttrTypeFormatter.isMuchValueType(completedType, -1);
        boolean isValid = false;
        if (isMuchValue && DBAttrTypeFormatter.isFilePath(value)) {
            isValid = true;
        } else {
            FormatDataResult result =
                    DBAttrTypeFormatter.format(completedType, value, false, dbCharset, true);
            isValid = result.isSuccess();
        }
        if (!isValid) {
            String errMsg =
                    Messages.bind(
                            com.cubrid.common.ui.cubrid.table.Messages.errTextTypeNotMatch,
                            completedType);
            CommonUITool.openErrorBox(getShell(), errMsg);
            return false;
        }
        if (fileCharsetCombo.isEnabled()) {
            String charsetName = fileCharsetCombo.getText();
            try {
                "".getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                CommonUITool.openErrorBox(
                        getShell(),
                        com.cubrid.common.ui.cubrid.table.Messages.errUnsupportedCharset);
                return false;
            }
        }
        return true;
    }

    /**
     * Verify whether value changed
     *
     * @return boolean
     */
    private boolean isValueChange() {
        String newStr = columnValueText.getText();
        String oldStr = dataItem.getText(selComboIndex + 1);
        boolean isNull = DataType.VALUE_NULL.equals(dataItem.getData("" + (selComboIndex + 1)));
        if (isNull) {
            return !newStr.equals(DataType.NULL_EXPORT_FORMAT);
        } else {
            return !oldStr.equals(newStr);
        }
    }

    /**
     * Check whether can update
     *
     * @return <code>true</code> if can update;otherwise,<code>false</code>
     */
    private boolean isCanUpdate() {
        ColumnInfo columnInfo = columnInfoList.get(0);
        if (!DataType.DATATYPE_OID.equals(columnInfo.getType())) {
            return false;
        }
        if (DataType.DATATYPE_OID.equals(columnInfo.getType())
                && "NONE".equals(dataItem.getText(1))) {
            return false;
        }
        return true;
    }

    /** Set the update button to enable */
    private void setUpdateButtonEnable() {
        if (getButton(IDialogConstants.OK_ID) == null) {
            return;
        }
        if (!isCanUpdate()) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            getButton(IDialogConstants.OK_ID).setVisible(false);
            return;
        }
        getButton(IDialogConstants.OK_ID).setEnabled(isValueChange());
    }

    /** Update data */
    private void updateData() {
        if (!validate()) {
            return;
        }
        final String columnValue = columnValueText.getText();
        final String fileCharset = fileCharsetCombo.getText();
        final String oid = dataItem.getText(1);
        final boolean updateDB = !QueryExecuter.isNewInsertedRecordItem(dataItem);

        AbstractUITask task =
                new AbstractUITask() {
                    boolean isSuccess = false;

                    public void execute(IProgressMonitor monitor) {
                        errorMsg = update(columnValue, fileCharset, oid, updateDB);
                        isSuccess = errorMsg == null;
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

        TaskExecutor taskExecutor = new CommonTaskExec(Messages.updateDataTaskName);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
    }

    /**
     * Update data to db and update ui.
     *
     * @param columnValue String
     * @param fileCharset String
     * @param oid String
     * @return error message.
     */
    private String update(String columnValue, String fileCharset, String oid, boolean updateDB) {
        final ColumnInfo columnInfo = columnInfoList.get(selComboIndex);
        final String[] cols = new String[1];
        final String[] vals = new String[1];
        cols[0] = columnInfo.getName();
        vals[0] = columnValue;
        final Object realObj;
        boolean isMuchValue = DBAttrTypeFormatter.isMuchValueType(columnInfo.getComleteType(), -1);
        if (DataType.NULL_EXPORT_FORMAT.equals(vals[0])) {
            realObj = null;
        } else if (isMuchValue) {
            realObj =
                    DBAttrTypeFormatter.formatMuchValue(
                            vals[0],
                            columnInfo.getComleteType(),
                            qe.getQueryEditor().getConnection().checkAndConnectQuietly(),
                            dbCharset,
                            fileCharset,
                            true);
        } else {
            realObj =
                    DBAttrTypeFormatter.format(
                                    columnInfo.getComleteType(), vals[0], false, dbCharset, true)
                            .getFormatedJavaObj();
        }
        if (realObj instanceof Exception) {
            return ((Exception) realObj).getMessage();
        } else {
            try {
                Object[] values = null;
                if (realObj instanceof Object[]) {
                    Object[] objCollection =
                            DataType.getCollectionValues(
                                    columnInfo.getComleteType(), (Object[]) realObj, true);
                    values = new Object[] {objCollection};
                } else {
                    values = new Object[] {realObj};
                }
                if (updateDB) {
                    qe.updateValue(oid, cols, values);
                }
            } catch (SQLException e) {
                LOGGER.error("", e);
                return Messages.bind(
                        com.cubrid.common.ui.common.Messages.errCommonTip,
                        e.getErrorCode(),
                        e.getMessage());
            }
        }
        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                String type = columnInfo.getType();
                                if (DataType.NULL_EXPORT_FORMAT.equals(vals[0])) {
                                    dataItem.setData("" + (selComboIndex + 1), DataType.VALUE_NULL);
                                    dataItem.setText(
                                            selComboIndex + 1, DataType.NULL_EXPORT_FORMAT);
                                    dataMap.put(columnInfo.getIndex(), null);
                                    return;
                                }
                                String showValue = vals[0];
                                if (DataType.DATATYPE_BLOB.equals(type)) {
                                    showValue = DataType.BLOB_EXPORT_FORMAT;
                                } else if (DataType.DATATYPE_CLOB.equals(type)) {
                                    showValue = DataType.CLOB_EXPORT_FORMAT;
                                } else if (DataType.DATATYPE_BIT.equals(type)
                                        || DataType.DATATYPE_BIT_VARYING.equals(type)) {
                                    byte[] bArr = (byte[]) realObj;
                                    if (bArr.length
                                            > FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH) {
                                        showValue = DataType.BIT_EXPORT_FORMAT;
                                    } else {
                                        showValue =
                                                "X'" + DBAttrTypeFormatter.getHexString(bArr) + "'";
                                    }
                                }
                                dataItem.setText(selComboIndex + 1, showValue);
                                dataMap.put(
                                        columnInfo.getIndex(), new CellValue(showValue, showValue));
                                columnValueText.setText(showValue);

                                boolean isNull =
                                        DataType.VALUE_NULL.equals(
                                                dataItem.getData("" + (selComboIndex + 1)));
                                if (isNull) {
                                    dataItem.setData("" + (selComboIndex + 1), "");
                                }
                            }
                        });
        return null;
    }

    /**
     * Export data
     *
     * @param filePath String
     */
    private void exportData(final String filePath) {
        final String oidStr = dataItem.getText(1);
        final String value = columnValueText.getText();
        final String fileCharset = fileCharsetCombo.getText();
        AbstractUITask task =
                new AbstractUITask() {
                    boolean isSuccess = false;

                    public void execute(final IProgressMonitor monitor) {
                        try {
                            export(filePath, oidStr, value, fileCharset);
                            isSuccess = true;
                        } catch (IOException e) {
                            errorMsg = e.getMessage();
                        } catch (SQLException e) {
                            errorMsg = e.getMessage();
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

        TaskExecutor taskExecutor = new CommonTaskExec(Messages.exportDataTaskName);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            CommonUITool.openInformationBox(
                    getShell(),
                    com.cubrid.common.ui.common.Messages.titleSuccess,
                    Messages.msgExportData);
        }
    }

    /**
     * Export data to file
     *
     * @param filePath String
     * @param oidStr String
     * @param value String
     * @param fileCharset String
     * @throws IOException The exception
     * @throws SQLException The exception
     */
    private void export(final String filePath, String oidStr, String value, String fileCharset)
            throws IOException, SQLException { // FIXME move this logic to core module
        OutputStream fs = null;
        Writer writer = null;
        InputStream in = null;
        Reader reader = null;
        ResultSet rs = null;

        try {
            ColumnInfo columnInfo = columnInfoList.get(selComboIndex);
            String type = columnInfo.getType();
            String completedType = columnInfo.getComleteType();
            String colName = columnInfo.getName();

            if (DataType.DATATYPE_BLOB.equals(type) && DataType.BLOB_EXPORT_FORMAT.equals(value)) {
                CUBRIDOIDProxy oidPxory =
                        CUBRIDOIDProxy.getNewInstance(
                                (CUBRIDConnectionProxy)
                                        qe.getQueryEditor().getConnection().checkAndConnect(),
                                oidStr);
                rs = oidPxory.getValues(new String[] {colName});
                rs.next();
                Blob blob = rs.getBlob(colName);
                in = blob.getBinaryStream();
                fs = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] bArr = new byte[512];
                int count = in.read(bArr);
                while (count > 0) {
                    fs.write(bArr, 0, count);
                    count = in.read(bArr);
                }
                fs.flush();
            } else {
                if (DataType.DATATYPE_CLOB.equals(type)
                        && DataType.CLOB_EXPORT_FORMAT.equals(value)) {
                    CUBRIDOIDProxy oidPxory =
                            CUBRIDOIDProxy.getNewInstance(
                                    (CUBRIDConnectionProxy)
                                            qe.getQueryEditor().getConnection().checkAndConnect(),
                                    oidStr);
                    rs = oidPxory.getValues(new String[] {colName});
                    rs.next();
                    Clob clob = rs.getClob(colName);
                    reader = clob.getCharacterStream();
                    writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            new FileOutputStream(filePath), fileCharset));

                    char[] charArr = new char[512];
                    int count = reader.read(charArr);
                    while (count > 0) {
                        writer.write(charArr, 0, count);
                        count = reader.read(charArr);
                    }
                    writer.flush();
                } else if (DataType.DATATYPE_BIT.equals(type)
                        || DataType.DATATYPE_BIT_VARYING.equals(type)) {
                    byte[] bArr = null;
                    if (DataType.BIT_EXPORT_FORMAT.equals(value)) {
                        CUBRIDOIDProxy oidPxory =
                                CUBRIDOIDProxy.getNewInstance(
                                        (CUBRIDConnectionProxy)
                                                qe.getQueryEditor()
                                                        .getConnection()
                                                        .checkAndConnect(),
                                        oidStr);
                        rs = oidPxory.getValues(new String[] {colName});
                        rs.next();
                        bArr = rs.getBytes(colName);
                    } else {
                        FormatDataResult result = new FormatDataResult();
                        DBAttrTypeFormatter.formatBit(completedType, value, result, dbCharset);
                        bArr = (byte[]) result.getFormatedJavaObj();
                    }
                    fs = new BufferedOutputStream(new FileOutputStream(filePath));
                    fs.write(bArr);
                    fs.flush();
                } else {
                    byte[] bArr = value.getBytes(fileCharset);
                    fs = new BufferedOutputStream(new FileOutputStream(filePath));
                    fs.write(bArr);
                    fs.flush();
                }
            }
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                    fs = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                    writer = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        //		createButton(parent, IDialogConstants.OK_ID, Messages.updateBtn, false);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.common.ui.common.Messages.btnClose,
                false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            int index = columnCombo.getSelectionIndex();
            if (selComboIndex >= 0 && isValueChange()) {
                if (CommonUITool.openConfirmBox(getShell(), Messages.cfmUpdateChangedValue)) {
                    updateData();
                } else {
                    columnValueText.setText(dataItem.getText(index + 1));
                }
            } else {
                CommonUITool.openInformationBox(
                        getShell(), Messages.msgValueNoChangedTitle, Messages.msgValueNoChanged);
            }
            setUpdateButtonEnable();
            return;
        }
        super.buttonPressed(buttonId);
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        getShell().setText(Messages.titleRowDetailDialog);
        super.constrainShellSize();
    }
}
