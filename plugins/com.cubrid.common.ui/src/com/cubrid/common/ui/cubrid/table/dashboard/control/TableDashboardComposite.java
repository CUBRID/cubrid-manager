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

package com.cubrid.common.ui.cubrid.table.dashboard.control;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.table.button.ITableButtonSupportEvent;
import com.cubrid.common.ui.spi.table.button.InputTextDialog;
import com.cubrid.common.ui.spi.table.button.TableEditButtonSupport;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

public class TableDashboardComposite extends Composite implements ITableButtonSupportEvent {
    private final CTabFolder tabFolder;
    private TableViewer columnTableView;
    private TableDashboardDetailLabelProvider labelProvider;
    private DatabaseInfo databaseInfo;
    private CTabItem tabItem;
    private static final int WIDTH_UNIQUECOLUMN = 70;
    private static final int WIDTH_NOTNULLCOLUMN = 70;
    private static final int WIDTH_DATATYPECOLUMN = 120;
    private static final int WIDTH_NAMECOLUMN = 160;
    private static final int WIDTH_PKCOLUMN = 30;
    private static final int WIDTH_SHAREDCOLUMN = 70;
    public static final Color BACK_COLOR = ResourceManager.getColor(136, 161, 227);

    public TableDashboardComposite(CTabFolder parent, int style) {
        super(parent, style);
        this.tabFolder = parent;
        GridLayout tLayout = new GridLayout(1, true);
        tLayout.verticalSpacing = 0;
        tLayout.horizontalSpacing = 0;
        tLayout.marginWidth = 0;
        tLayout.marginHeight = 0;
        setLayout(tLayout);
    }

    /** Create the SQL history composite */
    public void initialize() {
        SashForm bottomSash = new SashForm(tabFolder, SWT.VERTICAL);
        bottomSash.SASH_WIDTH = 2;
        bottomSash.setBackground(BACK_COLOR);

        SashForm tailSash = new SashForm(bottomSash, SWT.HORIZONTAL);
        tailSash.SASH_WIDTH = 2;
        tailSash.setBackground(BACK_COLOR);

        Composite tableComp = new Composite(tailSash, SWT.NONE);
        tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        tableComp.setLayout(new GridLayout());
        createColumnsTable(tableComp);

        tabItem = new TablesDetailInfoCTabItem(tabFolder, SWT.NONE, this);
        tabItem.setControl(bottomSash);
        tabItem.setShowClose(true);
        tabFolder.setSelection(tabItem);
    }

    /** Get the data that displaying */
    public SchemaInfo getData() {
        if (columnTableView != null && !columnTableView.getTable().isDisposed()) {
            Object ob = columnTableView.getInput();
            SchemaInfo schema = (SchemaInfo) ob;
            return schema;
        }
        return null;
    }

    public void setInput(
            SchemaInfo schema, DatabaseInfo database, boolean isSchemaCommentInstalled) {
        this.databaseInfo = database;
        labelProvider.setSchema(schema);
        labelProvider.setDatabase(database);
        tabItem.setText(schema == null ? "" : schema.getUniqueName());
        tabItem.setData(schema);
        columnTableView.setInput(schema);
        columnTableView.refresh();
        try {
            columnTableView.getTable().setSelection(0);
        } catch (Exception ignored) {
        }
        if (isSchemaCommentInstalled) {
            new TableEditButtonSupport(columnTableView, this, 2);
        }
    }

    /** Create the column information table */
    private void createColumnsTable(Composite topComposite) {
        columnTableView =
                new TableViewer(topComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        Table columnsTable = columnTableView.getTable();

        columnsTable.setLinesVisible(true);
        columnsTable.setHeaderVisible(true);

        final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdColumnsTable.heightHint = 189;
        columnsTable.setLayoutData(gdColumnsTable);

        // 0 PK
        TableColumn tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setAlignment(SWT.CENTER);
        tblCol.setWidth(WIDTH_PKCOLUMN);
        tblCol.setText(Messages.tblColumnPK);

        // 1 NAME
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(WIDTH_NAMECOLUMN);
        tblCol.setText(Messages.tblColumnName);

        // 2 MEMO
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(WIDTH_NAMECOLUMN);
        tblCol.setText(Messages.tblColumnMemo);

        // 3 DATATYPE
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(WIDTH_DATATYPECOLUMN);
        tblCol.setText(Messages.tblColumnDataType);

        // 4 DEFAULT
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(98);
        tblCol.setText(Messages.tblColumnDefault);

        // 5 AUTO INCREMENT
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setAlignment(SWT.CENTER);
        tblCol.setWidth(100);
        tblCol.setText(Messages.tblColumnAutoIncr);

        // 6 NOT NULL
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(WIDTH_NOTNULLCOLUMN);
        tblCol.setText(Messages.tblColumnNotNull);
        tblCol.setAlignment(SWT.CENTER);

        // 7 UK
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(WIDTH_UNIQUECOLUMN);
        tblCol.setText(Messages.tblColumnUnique);
        tblCol.setAlignment(SWT.CENTER);

        // 8 SHARED
        tblCol = new TableColumn(columnsTable, SWT.NONE);
        tblCol.setWidth(WIDTH_SHAREDCOLUMN);
        tblCol.setResizable(true);
        tblCol.setText(Messages.tblColumnShared);
        tblCol.setAlignment(SWT.CENTER);

        labelProvider = new TableDashboardDetailLabelProvider(null, null);
        columnTableView.setContentProvider(new TableDashboardDetailContentProvider());
        columnTableView.setLabelProvider(labelProvider);
        columnTableView
                .getTable()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyPressed(KeyEvent event) {
                                if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'c') {
                                    copyTablesDetailToClipboard();
                                } else if (event.keyCode == SWT.CR) {
                                    showEditDialog(
                                            columnTableView.getTable(),
                                            columnTableView.getTable().getSelectionIndex());
                                }
                            }
                        });

        registerContextMenu();
    }

    public void copyTablesDetailToClipboard() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final Clipboard cb = new Clipboard(shell.getDisplay());
        StringBuilder sb = new StringBuilder();
        List<Integer> selectIndex = new ArrayList<Integer>();
        SchemaInfo schema = (SchemaInfo) columnTableView.getInput();
        List<DBAttribute> list = schema.getAttributes();
        for (int i = 0; i < columnTableView.getTable().getSelectionIndices().length; i++) {
            selectIndex.add(columnTableView.getTable().getSelectionIndices()[i]);
        }
        for (int i = 0; i < selectIndex.size(); i++) {
            if (i != 0) {
                sb.append(StringUtil.NEWLINE);
            }
            DBAttribute attr = list.get(i);
            sb.append(attr.getName());
        }
        TextTransfer textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[] {textTransfer};
        Object[] data = new Object[] {sb.toString()};
        cb.setContents(data, transfers);
        cb.dispose();
    }

    /** register context menu */
    private void registerContextMenu() {
        columnTableView
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(columnTableView.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(columnTableView.getTable());
        columnTableView.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);
        final MenuItem itemShowMuchValue = new MenuItem(menu, SWT.PUSH);
        itemShowMuchValue.setText(
                com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartMenuCopy);
        itemShowMuchValue.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        copyTablesDetailToClipboard();
                    }
                });

        columnTableView.getTable().setMenu(menu);
    }

    /** A self CTabItem class ,can store TablesDetailInfoTableInfoComposite */
    public class TablesDetailInfoCTabItem extends CTabItem {
        private TableDashboardComposite tableInfoComposite;

        public TablesDetailInfoCTabItem(
                CTabFolder parent, int style, TableDashboardComposite tableInfoComposite) {
            super(parent, style);
            this.tableInfoComposite = tableInfoComposite;
        }

        public TableDashboardComposite getTableInfoComposite() {
            return tableInfoComposite;
        }

        public void setTableInfoComposite(TableDashboardComposite tableInfoComposite) {
            this.tableInfoComposite = tableInfoComposite;
        }
    }

    public void showEditDialog(Table table, int index) {
        SchemaInfo info = getData();
        if (info == null) {
            return;
        }
        List<DBAttribute> attrs = info.getAttributes();
        if (attrs == null) {
            return;
        }
        if (index >= attrs.size()) {
            return;
        }
        DBAttribute attr = attrs.get(index);
        if (attr == null) {
            return;
        }

        InputTextDialog dialog =
                new InputTextDialog(
                        Display.getCurrent().getActiveShell(),
                        com.cubrid.common.ui.cubrid.table.Messages.titleColumnDescEditor,
                        com.cubrid.common.ui.cubrid.table.Messages.msgColumnDescEditor,
                        com.cubrid.common.ui.cubrid.table.Messages.labelColumnDescEditor,
                        attr.getDescription());
        if (dialog.open() == IDialogConstants.OK_ID) { // FIXME move this logic to core module
            String tableName = info.getUniqueName();
            String columnName = attr.getName();
            String description = dialog.getResult();
            Connection conn = null;
            try {
                conn = JDBCConnectionManager.getConnection(databaseInfo, true);
                SchemaCommentHandler.updateDescription(
                        databaseInfo, conn, tableName, columnName, description);
                attr.setDescription(description);
                columnTableView.setInput(info);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                QueryUtil.commit(conn);
                QueryUtil.freeQuery(conn);
            }
        }
    }
}
