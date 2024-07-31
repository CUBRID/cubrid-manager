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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.query.control.SqlParser;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * The dialog for preparedstatement
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-30 created by pangqiren
 */
public abstract class PstmtDataDialog extends CMTitleAreaDialog {
    protected final CubridDatabase database;
    protected String tableName;
    protected SchemaInfo schemaInfo;
    protected boolean isInsert = false;
    protected StyledText sqlTxt = null;
    protected Button analyzeButton;
    protected Table parameterTable = null;
    protected TableEditor tableEditor = null;
    private int newIndex = -1;
    protected boolean isChanging = false;

    public PstmtDataDialog(Shell parentShell, CubridDatabase database) {
        this(parentShell, database, null, false);
    }

    public PstmtDataDialog(
            Shell parentShell, CubridDatabase database, String tableName, boolean isInsert) {
        super(parentShell);
        this.tableName = tableName;
        this.database = database;
        this.schemaInfo =
                this.database == null || this.tableName == null
                        ? null
                        : this.database.getDatabaseInfo().getSchemaInfo(tableName);
        this.isInsert = isInsert;
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

        SashForm sashForm = new SashForm(composite, SWT.NONE);
        {
            sashForm.setOrientation(SWT.VERTICAL);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.widthHint = 600;
            gridData.heightHint = 500;
            sashForm.setLayoutData(gridData);
            GridLayout layout = new GridLayout();
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            sashForm.setLayout(layout);

            createSqlTextComposite(sashForm);
            createBottomComposite(sashForm);

            sashForm.setWeights(new int[] {25, 75});
        }
        initial();
        return parentComp;
    }

    protected void initial() {
        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            TableItem item = new TableItem(parameterTable, SWT.NONE);
            item.setText(0, da.getName());
            item.setText(1, DataType.getShownType(da.getType()));
        }
        packTable();

        String sql = "";
        if (tableName != null && isInsert) {
            sql = createInsertPstmtSQL();
        } else if (tableName != null) {
            sql = createSelectPstmtSQL();
        }
        sqlTxt.setText(sql);
    }

    protected abstract Composite createBottomComposite(Composite parent);

    protected void createParameterTable(Composite parent, boolean isMulti) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            composite.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            composite.setLayoutData(gridData);
        }

        parameterTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.heightHint = 100;
            parameterTable.setLayoutData(gridData);
            parameterTable.setHeaderVisible(true);
            parameterTable.setLinesVisible(true);

            TableColumn[] tblColumns = new TableColumn[3];
            tblColumns[0] = new TableColumn(parameterTable, SWT.NONE);
            tblColumns[0].setText(Messages.colParaName);
            tblColumns[0].setWidth(130);
            tblColumns[1] = new TableColumn(parameterTable, SWT.NONE);
            tblColumns[1].setText(Messages.colParaType);
            tblColumns[1].setWidth(110);
            tblColumns[2] = new TableColumn(parameterTable, SWT.NONE);
            tblColumns[2].setWidth(130);
            if (isMulti) {
                tblColumns[2].setText(Messages.colFileColumn);
            } else {
                tblColumns[2].setText(Messages.colParaValue);
            }

            tableEditor = new TableEditor(parameterTable);
            tableEditor.horizontalAlignment = SWT.LEFT;
            tableEditor.grabHorizontal = true;

            parameterTable.addListener(
                    SWT.MouseUp,
                    new Listener() {
                        public void handleEvent(Event event) {
                            if (event.button != 1) {
                                return;
                            }
                            handle(event);
                        }
                    });
        }

        Button clearButton = new Button(composite, SWT.NONE);
        {
            GridData gridData = new GridData();
            gridData.verticalAlignment = SWT.TOP;
            clearButton.setLayoutData(gridData);
            if (isMulti) {
                clearButton.setText(Messages.btnClearColumn);
            } else {
                clearButton.setText(Messages.btnClearValue);
            }
            clearButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            for (int i = 0; i < parameterTable.getItemCount(); i++) {
                                parameterTable.getItem(i).setText(2, "");
                            }
                            getButton(IDialogConstants.OK_ID).setEnabled(false);
                        }
                    });
        }
    }

    protected void packTable() {
        for (int i = 0; i < parameterTable.getColumnCount(); i++) {
            if (parameterTable.getColumns()[i].getWidth() > 200) {
                parameterTable.getColumns()[i].setWidth(200);
            }
        }
    }

    protected void handle(final Event event) {

        if (isChanging) {
            return;
        }
        Point pt = new Point(event.x, event.y);
        int topIndex = parameterTable.getTopIndex();
        int curIndex = newIndex;
        newIndex = parameterTable.getSelectionIndex();
        if (curIndex < 0 || newIndex < 0 || topIndex > newIndex || curIndex != newIndex) {
            return;
        }
        final TableItem item = parameterTable.getItem(newIndex);
        if (item == null) {
            return;
        }
        for (int i = 1; i < parameterTable.getColumnCount(); i++) {
            Rectangle rect = item.getBounds(i);
            if (rect.contains(pt)) {
                if (i == 1) {
                    handleType(item);
                } else if (i == 2) {
                    handleValue(item);
                }
            }
        }
    }

    protected void handleType(final TableItem item) {
        String[][] typeMapping = DataType.getTypeMapping(database.getDatabaseInfo(), true, true);
        List<String> typeList = new ArrayList<String>();
        for (int i = 0; i < typeMapping.length; i++) {
            String type = typeMapping[i][0];
            if (!type.startsWith(DataType.DATATYPE_OBJECT)) {
                typeList.add(type);
            }
        }
        String[] items = typeList.toArray(new String[] {});
        final int editColumn = 1;

        final Combo typeCombo = new Combo(parameterTable, SWT.BORDER | SWT.FULL_SELECTION);
        typeCombo.setItems(items);
        typeCombo.setVisibleItemCount(20);

        final String paraName = item.getText(0);
        typeCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validateType(paraName, typeCombo.getText());
                    }
                });

        typeCombo.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent event) {
                        if (isChanging) {
                            return;
                        }
                        isChanging = true;
                        if (validateType(paraName, typeCombo.getText())) {
                            item.setText(editColumn, typeCombo.getText());
                        }
                        typeCombo.dispose();
                        isChanging = false;
                        validate();
                    }
                });

        // add listener for key pressed
        typeCombo.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent event) {
                        if (event.detail == SWT.TRAVERSE_RETURN) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            if (validateType(paraName, typeCombo.getText())) {
                                item.setText(editColumn, typeCombo.getText());
                            }
                            typeCombo.dispose();
                            isChanging = false;
                            validate();
                            event.doit = true;
                            handleValue(item);
                        } else if (event.detail == SWT.TRAVERSE_ESCAPE) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            typeCombo.dispose();
                            event.doit = false;
                            isChanging = false;
                        }
                    }
                });

        tableEditor.setEditor(typeCombo, item, editColumn);
        typeCombo.setText(item.getText(editColumn));
        typeCombo.setFocus();
    }

    protected abstract void handleValue(final TableItem item);

    protected boolean validateType(String paraName, String type) {
        setErrorMessage(null);
        if (type == null || type.trim().length() == 0) {
            setErrorMessage(Messages.bind(Messages.msgParaType, paraName));
            return false;
        }

        return true;
    }

    private void createSqlTextComposite(Composite parent) {
        Group sqlGroup = new Group(parent, SWT.NONE);
        {
            sqlGroup.setText(Messages.grpSql);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.heightHint = 160;
            sqlGroup.setLayoutData(gridData);
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            sqlGroup.setLayout(layout);
        }

        sqlTxt = new StyledText(sqlGroup, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        {
            GridData gridData = new GridData(GridData.FILL_BOTH);
            sqlTxt.setLayoutData(gridData);
            sqlTxt.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            if (validSql()) {
                                analyzeButton.setEnabled(true);
                            } else {
                                analyzeButton.setEnabled(false);
                            }
                        }
                    });
            CommonUITool.registerContextMenu(sqlTxt, true);
        }

        Composite composite = new Composite(sqlGroup, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            composite.setLayout(gridLayout);
            GridData gridData = new GridData();
            gridData.verticalAlignment = SWT.TOP;
            composite.setLayoutData(gridData);
        }

        Button clearButton = new Button(composite, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            clearButton.setLayoutData(gridData);
            clearButton.setText(Messages.btnClear);
            clearButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            sqlTxt.setText("");
                            validate();
                        }
                    });
        }

        analyzeButton = new Button(composite, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            analyzeButton.setLayoutData(gridData);
            analyzeButton.setText(Messages.btnAnalyze);
            analyzeButton.setEnabled(false);
            analyzeButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            analyzeSql();
                            validate();
                        }
                    });
        }
    }

    protected void analyzeSql() {
        if (!validSql()) {
            return;
        }
        String sql = sqlTxt.getText();
        int count = SqlParser.getStrCount(sql, "?");

        parameterTable.removeAll();
        ParameterMetaData metaData = TableUtil.getParameterMetaData(database, sqlTxt.getText());
        for (int i = 0; i < count; i++) {
            TableItem item = new TableItem(parameterTable, SWT.NONE);
            item.setText(0, String.valueOf(i + 1));
            if (metaData != null) {
                try {
                    String type = metaData.getParameterTypeName(i + 1);
                    if (type == null || type.trim().length() == 0) {
                        continue;
                    }
                    int precision = metaData.getPrecision(i + 1);
                    int scale = metaData.getScale(i + 1);
                    type = FieldHandlerUtils.getComleteType(type, null, precision, scale);
                    item.setText(1, type);
                } catch (SQLException e) {
                    continue;
                }
            }
        }
        validate();
        packTable();
    }

    protected abstract boolean validate();

    protected boolean validSql() {
        String sql = sqlTxt.getText();
        if (sql.trim().length() == 0) {
            return false;
        }
        if (sql.indexOf("?") == -1) {
            return false;
        }
        return true;
    }

    /**
     * Create inserted prepared statement SQL
     *
     * @return String
     */
    protected String createInsertPstmtSQL() { // FIXME move this logic to core module
        StringBuffer columns = new StringBuffer("");
        StringBuffer values = new StringBuffer("");

        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            if (values.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(QuerySyntax.escapeKeyword(da.getName()));
            values.append("?");
        }

        StringBuffer sql = new StringBuffer("");
        if (columns.length() > 0) {
            sql.append("INSERT INTO ");
            sql.append(QuerySyntax.escapeKeyword(tableName));
            sql.append(" (");
            sql.append(columns);
            sql.append(") VALUES (");
            sql.append(values);
            sql.append(");");
        }
        return sql.toString();
    }

    /**
     * Create selected prepared statement SQL
     *
     * @return String
     */
    protected String createSelectPstmtSQL() { // FIXME move this logic to core module
        StringBuffer columns = new StringBuffer("");
        StringBuffer wheres = new StringBuffer("");

        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            if (columns.length() > 0) {
                columns.append(", ");
                wheres.append(" AND ");
            }
            columns.append(QuerySyntax.escapeKeyword(da.getName()));
            wheres.append(QuerySyntax.escapeKeyword(da.getName()) + " = ?");
        }
        StringBuffer sql = new StringBuffer("");
        if (columns.length() > 0) {
            sql.append("SELECT ");
            sql.append(columns);
            sql.append(" FROM ");
            sql.append(QuerySyntax.escapeKeyword(tableName));
            sql.append(" WHERE ");
            sql.append(wheres + ";");
        }
        return sql.toString();
    }

    /**
     * Set the executed SQL
     *
     * @param sql The String
     */
    public void setSql(String sql) { // FIXME move this logic to core module
        sqlTxt.setText(sql);
        analyzeSql();
    }

    @Override
    protected int getShellStyle() {
        return SWT.MODELESS | SWT.RESIZE | SWT.TITLE | SWT.MAX | SWT.MIN;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titlePstmtDataDialog);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnExecute, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.closeButtonName, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
     * Calculate the spend time
     *
     * @param beginTimestamp long
     * @param endTimestamp long
     * @return String
     */
    public static String calcSpendTime(
            long beginTimestamp, long endTimestamp) { // FIXME move this logic to core module
        double elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        String elapsedTimeStr = nf.format(elapsedTime);
        if (elapsedTime < 0.001) {
            elapsedTimeStr = "0.000";
        }
        return elapsedTimeStr;
    }

    /**
     * Get comments SQL value
     *
     * @param parameterList List<PstmtParameter>
     * @return String
     */
    protected String getCommentSqlValue(
            List<PstmtParameter> parameterList) { // FIXME move this logic to core module
        StringBuffer valueComments = new StringBuffer("--");
        for (PstmtParameter parameter : parameterList) {
            if (parameter.getStringParamValue() == null) {
                valueComments.append("NULL");
                valueComments.append(",");
            } else {
                valueComments.append(parameter.getStringParamValue());
                valueComments.append(",");
            }
        }
        valueComments = valueComments.deleteCharAt(valueComments.length() - 1);
        valueComments.append(StringUtil.NEWLINE);
        return valueComments.toString();
    }
}
