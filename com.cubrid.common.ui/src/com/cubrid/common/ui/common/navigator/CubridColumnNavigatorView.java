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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.control.AttributeTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.AttributeTableViewerLabelProvider;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class CubridColumnNavigatorView extends ViewPart {
    public static final String ID = "com.cubrid.common.navigator.columns";
    private TableViewer tableColTableViewer;
    private Label lblSchemaName;
    private String schemaName;

    public void createPartControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        {
            GridLayout gl = new GridLayout(1, false);
            comp.setLayout(gl);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            comp.setLayoutData(gd);
        }
        lblSchemaName = new Label(comp, SWT.NONE);
        lblSchemaName.setText(com.cubrid.common.ui.common.Messages.lblQuickViewColInfo);
        {
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            lblSchemaName.setLayoutData(gd);
        }

        tableColTableViewer = new TableViewer(comp, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        {
            GridData gd = new GridData(GridData.FILL_BOTH);
            tableColTableViewer.getTable().setLayoutData(gd);
            tableColTableViewer
                    .getTable()
                    .setToolTipText(
                            com.cubrid.common.ui.common.Messages.miniSchemaCopyColumnTooltip);
            final MenuManager menuManager = new MenuManager();
            menuManager.setRemoveAllWhenShown(true);

            final Menu contextMenu = menuManager.createContextMenu(tableColTableViewer.getTable());
            tableColTableViewer.getTable().setMenu(contextMenu);
            final Menu copyMenu =
                    new Menu(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            SWT.POP_UP);
            tableColTableViewer.getTable().setMenu(copyMenu);
            tableColTableViewer
                    .getTable()
                    .addKeyListener(
                            new org.eclipse.swt.events.KeyAdapter() {
                                public void keyPressed(org.eclipse.swt.events.KeyEvent event) {
                                    if ((event.stateMask & SWT.CTRL) != 0
                                            && (event.stateMask & SWT.SHIFT) == 0
                                            && event.keyCode == 'c') {
                                        copyColumnListToClipboard(",");
                                    }
                                }
                            });

            final MenuItem copyMenuItem = new MenuItem(copyMenu, SWT.PUSH);
            copyMenuItem.setText(
                    com.cubrid.common.ui.common.Messages.miniSchemaCopyColumnWithComma);
            copyMenuItem.setAccelerator(SWT.CTRL + 'C');
            copyMenuItem.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            copyColumnListToClipboard(",");
                        }
                    });

            final MenuItem copyMenuItem2 = new MenuItem(copyMenu, SWT.PUSH);
            copyMenuItem2.setText(
                    com.cubrid.common.ui.common.Messages.miniSchemaCopyColumnWithNewline);
            copyMenuItem2.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            copyColumnListToClipboard(StringUtil.NEWLINE);
                        }
                    });

            new MenuItem(copyMenu, SWT.SEPARATOR);

            final MenuItem copyMenuItem3 = new MenuItem(copyMenu, SWT.PUSH);
            copyMenuItem3.setText(com.cubrid.common.ui.common.Messages.miniSchemaCopySelectQuery);
            copyMenuItem3.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            copySelectSQLToClipboard();
                        }
                    });

            final MenuItem copyMenuItem4 = new MenuItem(copyMenu, SWT.PUSH);
            copyMenuItem4.setText(com.cubrid.common.ui.common.Messages.miniSchemaCopyInsertQuery);
            copyMenuItem4.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            copyInsertSQLToClipboard();
                        }
                    });

            final MenuItem copyMenuItem5 = new MenuItem(copyMenu, SWT.PUSH);
            copyMenuItem5.setText(com.cubrid.common.ui.common.Messages.miniSchemaCopyUpdateQuery);
            copyMenuItem5.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            copyUpdateSQLToClipboard();
                        }
                    });
        }

        Table columnsTable = tableColTableViewer.getTable();
        columnsTable.setLinesVisible(true);
        columnsTable.setHeaderVisible(true);

        final TableColumn pkColumn = new TableColumn(columnsTable, SWT.NONE);
        pkColumn.setAlignment(SWT.CENTER);
        pkColumn.setWidth(30);
        pkColumn.setText(Messages.tblColumnPK);

        final TableColumn nameColumn = new TableColumn(columnsTable, SWT.NONE);
        nameColumn.setWidth(90);
        nameColumn.setText(Messages.tblColumnName);

        final TableColumn dataTypeColumn = new TableColumn(columnsTable, SWT.NONE);
        dataTypeColumn.setWidth(120);
        dataTypeColumn.setText(Messages.tblColumnDataType);

        final TableColumn defaultColumn = new TableColumn(columnsTable, SWT.NONE);
        defaultColumn.setWidth(98);
        defaultColumn.setText(Messages.tblColumnDefault);

        final TableColumn autoIncrTableColumn = new TableColumn(columnsTable, SWT.NONE);
        autoIncrTableColumn.setAlignment(SWT.CENTER);
        autoIncrTableColumn.setWidth(100);
        autoIncrTableColumn.setText(Messages.tblColumnAutoIncr);

        final TableColumn notNullColumn = new TableColumn(columnsTable, SWT.NONE);
        notNullColumn.setWidth(70);
        notNullColumn.setText(Messages.tblColumnNotNull);
        notNullColumn.setAlignment(SWT.CENTER);

        final TableColumn uniqueColumn = new TableColumn(columnsTable, SWT.NONE);
        uniqueColumn.setWidth(70);
        uniqueColumn.setText(Messages.tblColumnUnique);
        uniqueColumn.setAlignment(SWT.CENTER);

        final TableColumn sharedColumn = new TableColumn(columnsTable, SWT.NONE);
        sharedColumn.setWidth(60);
        sharedColumn.setResizable(false);
        sharedColumn.setText(Messages.tblColumnShared);
        sharedColumn.setAlignment(SWT.CENTER);

        final TableColumn inheritColumn = new TableColumn(columnsTable, SWT.NONE);
        inheritColumn.setAlignment(SWT.CENTER);
        inheritColumn.setWidth(90);
        inheritColumn.setResizable(false);
        inheritColumn.setText(Messages.tblColumnInherit);

        final TableColumn classColumn = new TableColumn(columnsTable, SWT.NONE);
        classColumn.setWidth(90);
        classColumn.setResizable(false);
        classColumn.setText(Messages.tblColumnClass);
        classColumn.setAlignment(SWT.CENTER);
    }

    public void setFocus() {
        CubridNavigatorView mainNav = CubridNavigatorView.findNavigationView();
        if (mainNav != null) {
            DatabaseInfo databaseInfo = mainNav.getCurrentDatabaseInfo();
            SchemaInfo schemaInfo = mainNav.getCurrentSchemaInfo();
            updateView(databaseInfo, schemaInfo);
        }
    }

    public void updateView(DatabaseInfo databaseInfo, SchemaInfo schemaInfo) {
        if (databaseInfo == null || schemaInfo == null) {
            cleanView();
        }
        if (schemaInfo != null) {
            this.schemaName = schemaInfo.getUniqueName();
        }
        redrawView(databaseInfo, schemaInfo);
    }

    public void cleanView() {
        this.schemaName = null;
        lblSchemaName.setText(com.cubrid.common.ui.common.Messages.lblQuickViewColInfo);
        try {
            tableColTableViewer.setInput(null);
        } catch (Exception ignored) {
        }
    }

    private void redrawView(DatabaseInfo databaseInfo, SchemaInfo schemaInfo) {
        if (schemaInfo != null && schemaInfo.getUniqueName() != null) {
            String msg =
                    com.cubrid.common.ui.common.Messages.lblQuickViewColInfo
                            + " "
                            + schemaInfo.getUniqueName();
            lblSchemaName.setText(msg);
        }

        AttributeTableViewerContentProvider attrContentProvider =
                new AttributeTableViewerContentProvider();
        attrContentProvider.setShowClassAttribute(true);
        AttributeTableViewerLabelProvider attrLabelProvider =
                new AttributeTableViewerLabelProvider(databaseInfo, schemaInfo);
        try {
            tableColTableViewer.setContentProvider(attrContentProvider);
            tableColTableViewer.setLabelProvider(attrLabelProvider);
            tableColTableViewer.setInput(schemaInfo);
            // Auto set column size, maximum is 300px, minimum is 28px
            CommonUITool.packTable(tableColTableViewer.getTable(), 28, 300);
        } catch (Exception ignored) {
        }
    }

    public static CubridColumnNavigatorView getInstance() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return null;
        }

        IViewReference viewReference = page.findViewReference(ID);
        if (viewReference != null) {
            IViewPart viewPart = viewReference.getView(false);
            return viewPart instanceof CubridColumnNavigatorView
                    ? (CubridColumnNavigatorView) viewPart
                    : null;
        }

        return null;
    }

    private String makeTableItemsToColumnsText(TableItem[] items, String separateChar) {
        if (items == null) {
            return "";
        }

        StringBuilder columns = new StringBuilder();
        for (TableItem item : items) {
            String column = item.getText(1).trim();
            if (columns.length() > 0) {
                columns.append(separateChar);
            }
            columns.append(QuerySyntax.escapeKeyword(column));
        }

        return columns.toString();
    }

    private void copyColumnListToClipboard(String separateChar) {
        TableItem[] items = tableColTableViewer.getTable().getSelection();
        if (items == null || items.length == 0) {
            return;
        }
        String text = makeTableItemsToColumnsText(items, separateChar);
        CommonUITool.copyContentToClipboard(text);
    }

    private void copySelectSQLToClipboard() {
        TableItem[] items = tableColTableViewer.getTable().getSelection();
        if (items == null || items.length == 0) {
            return;
        }

        String tableName = QuerySyntax.escapeKeyword(schemaName);
        String columns = makeTableItemsToColumnsText(items, ", ");

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(columns);
        sql.append(" FROM ").append(QuerySyntax.escapeKeyword(tableName));

        CommonUITool.copyContentToClipboard(sql.toString());
    }

    private void copyInsertSQLToClipboard() {
        TableItem[] items = tableColTableViewer.getTable().getSelection();
        if (items == null || items.length == 0) {
            return;
        }

        String tableName = QuerySyntax.escapeKeyword(schemaName);
        String columns = makeTableItemsToColumnsText(items, ", ");

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName);
        sql.append(" (").append(columns).append(") VALUES (");
        for (int i = 0; i < items.length; i++) {
            if (i == 0) sql.append("?");
            else sql.append(", ?");
        }
        sql.append(");");

        CommonUITool.copyContentToClipboard(sql.toString());
    }

    private void copyUpdateSQLToClipboard() {
        TableItem[] items = tableColTableViewer.getTable().getSelection();
        if (items == null || items.length == 0) {
            return;
        }

        String escapedTableName = QuerySyntax.escapeKeyword(schemaName);

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(escapedTableName).append(" SET ");
        int i = 0;
        for (TableItem item : items) {
            String column = item.getText(1).trim();
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(QuerySyntax.escapeKeyword(column));
            sql.append(" = ?");
            i++;
        }
        sql.append(" WHERE ");

        CommonUITool.copyContentToClipboard(sql.toString());
    }
}
