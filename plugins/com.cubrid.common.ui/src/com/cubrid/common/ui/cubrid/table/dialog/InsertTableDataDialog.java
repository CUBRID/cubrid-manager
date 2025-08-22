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

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * The dialog of insert table data
 *
 * @author pangqiren 2009-6-4
 */
public class InsertTableDataDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(InsertTableDataDialog.class);
    private static final String NEW_LINE = System.getProperty("line.separator");
    private SashForm sashForm = null;
    private Table attrTable = null;
    private StyledText sqlHistoryTxt = null;
    private TableEditor tableEditor = null;
    private final String tableName;
    private final SchemaInfo schemaInfo;
    private Label lblTotalInsertedCount = null;
    private int cntTotalInsertedRecord = 0;
    private CubridDatabase database = null;
    private static final int BTN_INSERT_ID = 100;
    private static final int BTN_CLEAR_ID = 200;
    private static final int BTN_COMMIT_ID = 300;
    private static final int EDIT_COLUMN = 3;
    private boolean isChanging = false;
    private final List<String> sqlList = new ArrayList<String>();

    public InsertTableDataDialog(Shell parentShell, ISchemaNode table) {
        super(parentShell);
        this.tableName = table.getName();
        this.database = table.getDatabase();
        this.schemaInfo = table.getDatabase().getDatabaseInfo().getSchemaInfo(tableName);
    }

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

        sashForm = new SashForm(composite, SWT.NONE);
        {
            sashForm.setOrientation(SWT.VERTICAL);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.widthHint = 620;
            gridData.heightHint = 500;
            sashForm.setLayoutData(gridData);

            createAttrTable();

            sqlHistoryTxt =
                    new StyledText(sashForm, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
            sqlHistoryTxt.setLayoutData(new GridData(GridData.FILL_BOTH));
            CommonUITool.registerContextMenu(sqlHistoryTxt, false);

            sashForm.setWeights(new int[] {60, 40});
        }

        lblTotalInsertedCount = new Label(composite, SWT.NONE);
        {
            lblTotalInsertedCount.setText("");
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            lblTotalInsertedCount.setLayoutData(gridData);
        }

        setTitle(Messages.insertInstanceMsgTitle);
        setMessage(Messages.insertInstanceMsg);
        return parentComp;
    }

    /** Create the table attribute table */
    private void createAttrTable() {

        attrTable = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
        attrTable.setLayoutData(new GridData(GridData.FILL_BOTH));
        attrTable.setHeaderVisible(true);
        attrTable.setLinesVisible(true);

        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(20, 120));
        layout.addColumnData(new ColumnWeightData(20, 120));
        layout.addColumnData(new ColumnWeightData(20, 120));
        layout.addColumnData(new ColumnWeightData(40, 160));
        attrTable.setLayout(layout);

        TableColumn[] tblColumns = new TableColumn[4];
        tblColumns[0] = new TableColumn(attrTable, SWT.NONE);
        tblColumns[0].setText(Messages.metaAttribute);
        tblColumns[1] = new TableColumn(attrTable, SWT.NONE);
        tblColumns[1].setText(Messages.metaDomain);
        tblColumns[2] = new TableColumn(attrTable, SWT.NONE);
        tblColumns[2].setText(Messages.metaConstaints);
        tblColumns[3] = new TableColumn(attrTable, SWT.NONE);
        tblColumns[3].setText(Messages.metaValue);

        TableItem item;
        for (int i = 0, n = schemaInfo.getAttributes().size(); i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            String type = DataType.getShownType(da.getType());
            if (type.toUpperCase(Locale.getDefault()).startsWith(DataType.DATATYPE_BLOB)
                    || type.toUpperCase(Locale.getDefault()).startsWith(DataType.DATATYPE_CLOB)) {
                continue;
            }
            item = new TableItem(attrTable, SWT.NONE);
            item.setText(0, da.getName());
            item.setText(1, type);
            item.setText(2, getConstaintString(da));
        }
        packTable();

        tableEditor = new TableEditor(attrTable);
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.grabHorizontal = true;

        attrTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        // Identify the selected row
                        TableItem item = (TableItem) event.item;
                        if (item == null) {
                            return;
                        }
                        handleValue(item);
                    }
                });
    }

    private void packTable() {
        for (int i = 0; i < attrTable.getColumnCount(); i++) {
            if (attrTable.getColumns()[i].getWidth() > 200) {
                attrTable.getColumns()[i].setWidth(200);
            }
        }
    }

    private void handleValue(final TableItem item) {
        // Clean up any previous editor control
        Control oldEditor = tableEditor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }

        final Text newEditor = new Text(attrTable, SWT.MULTI | SWT.WRAP);

        newEditor.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validate(newEditor.getText(), item);
                    }
                });

        newEditor.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent event) {
                        if (isChanging) {
                            return;
                        }
                        isChanging = true;
                        if (validate(newEditor.getText(), item)) {
                            item.setText(EDIT_COLUMN, newEditor.getText());
                        }
                        newEditor.dispose();
                        validate();
                        isChanging = false;
                    }
                });

        // add listener for key pressed
        newEditor.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent event) {
                        if (event.detail == SWT.TRAVERSE_RETURN) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            if (validate(newEditor.getText(), item)) {
                                item.setText(EDIT_COLUMN, newEditor.getText());
                            }
                            newEditor.dispose();
                            validate();
                            isChanging = false;
                            event.doit = true;
                            int selItem =
                                    (attrTable.getSelectionIndex() + 1) % attrTable.getItemCount();
                            if (selItem == 0) {
                                getButton(BTN_INSERT_ID).setFocus();
                            } else {
                                attrTable.setSelection(selItem);
                                handleValue(attrTable.getItem(selItem));
                            }
                        } else if (event.detail == SWT.TRAVERSE_ESCAPE) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            newEditor.dispose();
                            event.doit = false;
                            isChanging = false;
                        }
                    }
                });

        tableEditor.setEditor(newEditor, item, EDIT_COLUMN);
        newEditor.setText(item.getText(EDIT_COLUMN));
        newEditor.selectAll();
        newEditor.setFocus();
    }

    private boolean validate() {
        setErrorMessage(null);
        for (int i = 0; i < attrTable.getItemCount(); i++) {
            TableItem item = attrTable.getItem(i);
            String value = item.getText(EDIT_COLUMN);
            if (!validate(value, item)) {
                return false;
            }
        }
        return true;
    }

    private boolean validate(String data, TableItem item) { // FIXME move this logic to core module
        setErrorMessage(null);
        String type = DataType.getRealType(item.getText(1));
        if (data.length() > 0) {
            boolean result = DBAttrTypeFormatter.validateAttributeValue(type, data, false);
            if (!result) {
                String msg = Messages.bind(Messages.insertDataTypeErrorMsg, item.getText(1));
                setErrorMessage(msg);
                return false;
            }
        } else {
            String constaint = item.getText(EDIT_COLUMN - 1);
            if (-1 != constaint.indexOf("Not Null") && -1 == constaint.indexOf("Default:")) {
                String msg = Messages.bind(Messages.insertNotNullErrorMsg, item.getText(0));
                setErrorMessage(msg);
                return false;
            }
        }
        return true;
    }

    private String getConstaintString(DBAttribute dbattr) { // FIXME move this logic to core module
        StringBuffer bf = new StringBuffer();
        // add auto increment support
        if (dbattr.getAutoIncrement() == null) {
            if (dbattr.isNotNull()) {
                bf.append(",Not Null");
            }
        } else {
            bf.append(",Auto Increment");
        }
        if (dbattr.isShared()) {
            bf.append(",Shared");
        }
        if (dbattr.isUnique()) {
            bf.append(",Unique");
        } else {
            String defaultValue = dbattr.getDefault();
            if (defaultValue != null) {
                if (defaultValue.trim().length() == 0) {
                    defaultValue = "'" + defaultValue + "'";
                }
                bf.append(",Default:" + defaultValue);
            }
        }
        if (bf.length() > 1) {
            return bf.substring(1);
        }
        return bf.toString();
    }

    /** clear the insert contents. */
    private void clearInsert() {
        for (int i = 0; i < attrTable.getItemCount(); i++) {
            attrTable.getItem(i).setText(EDIT_COLUMN, "");
        }
    }

    /** clear history */
    private void clearHistory() {
        sqlHistoryTxt.setText("");
    }

    /**
     * create insert SQL
     *
     * @return string
     */
    private String createInsertSQL() { // FIXME move this logic to core module
        StringBuffer sql = new StringBuffer("INSERT INTO ");
        sql.append(QuerySyntax.escapeKeyword(tableName));
        sql.append(" (");
        StringBuffer columns = new StringBuffer("");
        StringBuffer values = new StringBuffer("");

        for (int i = 0; i < attrTable.getItemCount(); i++) {
            String type = DataType.getRealType(attrTable.getItem(i).getText(1));
            String value = attrTable.getItem(i).getText(3);

            if (value.length() > 0) {
                if (values.length() > 0) {
                    columns.append(", ");
                    values.append(", ");
                }
                columns.append(QuerySyntax.escapeKeyword(attrTable.getItem(i).getText(0)));
                try {
                    value = getInsertedValue(type, value);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
                values.append(value);
            }
        }
        if (columns.length() > 0 && values.length() > 0) {
            sql.append(columns);
            sql.append(") VALUES (");
            sql.append(values);
            sql.append(")");
        } else {
            sql = new StringBuffer("");
        }
        return sql.toString();
    }

    /**
     * Get inserted value
     *
     * @param type String
     * @param value String
     * @return String
     */
    private String getInsertedValue(
            String type, String value) { // FIXME move this logic to core module
        String insertedValue = DBAttrTypeFormatter.formatValueForInput(type, value, false);

        String upperType = type.toUpperCase(Locale.getDefault());
        if (!upperType.equals(DataType.DATATYPE_DATETIME)
                && !upperType.equals(DataType.DATATYPE_TIMESTAMP)) {
            return insertedValue;
        }

        if (Locale.getDefault().equals(Locale.US)) {
            return insertedValue;
        }

        // Other locale do not support AM PM
        String upperValue = value == null ? "" : value.toUpperCase(Locale.getDefault());
        if (upperValue.indexOf("PM") > 0 || upperValue.indexOf("AM") > 0) {
            return upperType + "'" + value + "'";
        }

        return insertedValue;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        String msg = Messages.bind(Messages.insertInstanceWindowTitle, this.tableName);
        getShell().setText(msg);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, BTN_INSERT_ID, Messages.insertButtonName, false);
        createButton(parent, BTN_COMMIT_ID, Messages.btnCommit, false);
        createButton(parent, BTN_CLEAR_ID, Messages.clearButtonName, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.closeButtonName, false);
        getButton(BTN_COMMIT_ID).setEnabled(false);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == BTN_INSERT_ID) {
            if (!validate()) {
                return;
            }
            String insertSql = createInsertSQL();
            if (insertSql.trim().length() > 0) {
                sqlHistoryTxt.append(insertSql + ";");
                sqlHistoryTxt.append(NEW_LINE);
                sqlList.add(insertSql);
                getButton(BTN_COMMIT_ID).setEnabled(true);
            }
        } else if (buttonId == BTN_COMMIT_ID) {
            if (!sqlList.isEmpty()) {
                String countMsg = "";
                String resultMsg = "";
                try {
                    int count = TableUtil.insertRecord(database, sqlList);

                    getButton(BTN_COMMIT_ID).setEnabled(false);

                    cntTotalInsertedRecord += count;
                    countMsg =
                            Messages.bind(Messages.totalInsertedCountMsg, cntTotalInsertedRecord);
                    resultMsg = Messages.bind(Messages.insertedCountMsg, count);
                } catch (SQLException e) {
                    resultMsg =
                            Messages.bind(Messages.insertFailed, e.getErrorCode(), e.getMessage());
                    getButton(BTN_COMMIT_ID).setEnabled(false);
                } finally {
                    sqlList.clear();
                }

                lblTotalInsertedCount.setText(countMsg);

                sqlHistoryTxt.append("// ");
                sqlHistoryTxt.append(resultMsg);
                sqlHistoryTxt.append(NEW_LINE);
                sqlHistoryTxt.append(NEW_LINE);
            }
        } else if (buttonId == BTN_CLEAR_ID) {
            clearInsert();
            clearHistory();
            sqlList.clear();
            getButton(BTN_COMMIT_ID).setEnabled(false);
            lblTotalInsertedCount.setText("");
            if (tableEditor != null
                    && tableEditor.getEditor() != null
                    && !tableEditor.getEditor().isDisposed()) {
                tableEditor.getEditor().dispose();
            }
        } else if (buttonId == IDialogConstants.CANCEL_ID) {
            super.buttonPressed(buttonId);
        }
    }
}
