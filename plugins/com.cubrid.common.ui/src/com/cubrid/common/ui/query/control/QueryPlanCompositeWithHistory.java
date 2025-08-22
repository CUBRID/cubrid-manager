/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.queryplan.model.PlanCost;
import com.cubrid.common.core.queryplan.model.PlanNode;
import com.cubrid.common.core.queryplan.model.PlanResult;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.action.EditTableAction;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.queryplan.PLAN_DISPLAY_MODE;
import com.cubrid.common.ui.query.control.queryplan.QueryPlanComposite;
import com.cubrid.common.ui.query.control.tunemode.TuneModeModel;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TabContextMenuManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * The Query execution plan tab in the Query Editor
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class QueryPlanCompositeWithHistory extends Composite
        implements ISubTabSelection { // TOOD rename to QueryPlanBaseComposite
    private static final Logger LOGGER = LogUtil.getLogger(QueryPlanCompositeWithHistory.class);
    private final QueryEditorPart editor;
    private final CTabFolder resultTabFolder;
    private CTabFolder plansTabFolder = null;
    private SashForm plansHistorySash = null;
    private Table planHistoryTable = null;
    private List<StructQueryPlan> planHistoryList = new ArrayList<StructQueryPlan>();
    private ToolItem historySwitchItem = null;
    private ToolItem historyShowHideItem = null;
    private ToolItem delHistory = null;
    private ToolItem dispModeTextItem = null;
    private ToolItem dispModeTreeItem = null;
    private ToolItem dispModeGraphItem = null;
    private ToolItem editTableItem = null;
    private ToolItem editIndexItem = null;
    private int[] sashPlanWeight = null;
    private final CTabItem explainTabItem;
    private boolean collectToHistoryFlag = false;
    private TableColumn[] planHistoryTblCols = new TableColumn[4];
    private TableColumn currentSortTableColumn = null;
    private HashMap<TableColumn, Boolean> sortStyle =
            new HashMap<TableColumn, Boolean>(); // descent or ascend
    private HashMap<TableColumn, String> sortType =
            new HashMap<TableColumn, String>(); // number or string
    private Collator comparator = Collator.getInstance(Locale.getDefault());
    private int lastQueryPlanTabNum = 1; // The global uid for query plan

    public QueryPlanCompositeWithHistory(
            CTabFolder parent, int style, QueryEditorPart queryEditorPart) {
        super(parent, style);
        this.editor = queryEditorPart;
        this.resultTabFolder = parent;

        GridLayout tLayout = new GridLayout(1, false);
        tLayout.verticalSpacing = 0;
        tLayout.horizontalSpacing = 0;
        tLayout.marginWidth = 0;
        tLayout.marginHeight = 0;
        setLayout(tLayout);

        explainTabItem = new CTabItem(parent, SWT.NONE);
        explainTabItem.setText(Messages.qedit_plan_folder);
        explainTabItem.setControl(this);

        initialize();
    }

    /** Initializing a Plan Tab */
    private void initialize() {
        createPlanToolbar();

        // create the plan tab folder and history sash
        plansHistorySash = new SashForm(this, SWT.HORIZONTAL);
        plansHistorySash.setLayout(new GridLayout(2, true));
        plansHistorySash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create left plan tab folder
        plansTabFolder = new CTabFolder(plansHistorySash, SWT.BOTTOM | SWT.CLOSE);
        plansTabFolder.setSimple(false);
        plansTabFolder.setUnselectedImageVisible(true);
        plansTabFolder.setUnselectedCloseVisible(true);
        plansTabFolder.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
        plansTabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        plansTabFolder.setLayout(new GridLayout(1, true));
        plansTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        TabContextMenuManager tabContextMenuManager = new TabContextMenuManager(plansTabFolder);
        tabContextMenuManager.createContextMenu();

        // create the right plan history table
        planHistoryTable = new Table(plansHistorySash, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        planHistoryTable.setLayout(new GridLayout(1, true));
        planHistoryTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        planHistoryTable.setHeaderVisible(true);
        planHistoryTable.setLinesVisible(true);

        planHistoryTable.addMouseListener(
                new MouseListener() {
                    public void mouseDoubleClick(MouseEvent event) {
                        int selectionIndex = planHistoryTable.getSelectionIndex();
                        if (selectionIndex < 0) {
                            return;
                        }

                        TableItem tableItem =
                                planHistoryTable.getItem(planHistoryTable.getSelectionIndex());
                        if (tableItem == null) {
                            return;
                        }

                        int uid = Integer.valueOf(tableItem.getText(0));
                        StructQueryPlan sq = planHistoryList.get(uid - 1);
                        PlanTabItem tabItem = findPlanTab(uid);
                        if (tabItem == null) {
                            tabItem = newPlanTab(uid);
                        }

                        plansTabFolder.setSelection(tabItem);
                        printPlan(tabItem, sq);
                    }

                    public void mouseDown(MouseEvent event) {}

                    public void mouseUp(MouseEvent event) {}
                });

        int i = 0;
        planHistoryTblCols[i] = new TableColumn(planHistoryTable, SWT.RIGHT);
        planHistoryTblCols[i].setText(Messages.qedit_plan_history_col1); // No
        planHistoryTblCols[i].setMoveable(true);
        planHistoryTblCols[i].setWidth(20);
        addNumberSorter(planHistoryTable, planHistoryTblCols[i]);
        sortType.put(planHistoryTblCols[i], "NUMBER");

        planHistoryTblCols[++i] = new TableColumn(planHistoryTable, SWT.LEFT);
        planHistoryTblCols[i].setText(Messages.qedit_plan_history_col2); // Date
        planHistoryTblCols[i].setMoveable(true);
        planHistoryTblCols[i].setWidth(100);
        addStringSorter(planHistoryTable, planHistoryTblCols[i]);
        sortType.put(planHistoryTblCols[i], "STRING");

        planHistoryTblCols[++i] = new TableColumn(planHistoryTable, SWT.RIGHT);
        planHistoryTblCols[i].setText(Messages.qedit_plan_history_col4); // Cost
        planHistoryTblCols[i].setMoveable(true);
        planHistoryTblCols[i].setWidth(90);
        addNumberSorter(planHistoryTable, planHistoryTblCols[i]);
        sortType.put(planHistoryTblCols[i], "NUMBER");

        planHistoryTblCols[++i] = new TableColumn(planHistoryTable, SWT.LEFT);
        planHistoryTblCols[i].setText(Messages.qedit_plan_history_col3); // Cost
        planHistoryTblCols[i].setMoveable(false);
        planHistoryTblCols[i].setWidth(90);

        addStringSorter(planHistoryTable, planHistoryTblCols[i]);
        sortType.put(planHistoryTblCols[i], "STRING");
        sashPlanWeight = new int[] {80, 20};
        plansHistorySash.setWeights(sashPlanWeight);

        newPlanTab(1);
        plansTabFolder.setSelection(0);

        hideHistoryPane();
        refreshToolbarStatus(QueryOptions.getQueryPlanDisplayType());
    }

    private void refreshToolbarStatus(int displayMode) {
        dispModeTextItem.setSelection(PLAN_DISPLAY_MODE.TEXT.getInt() == displayMode);
        dispModeTreeItem.setSelection(PLAN_DISPLAY_MODE.TREE.getInt() == displayMode);
        dispModeGraphItem.setSelection(PLAN_DISPLAY_MODE.GRAPH.getInt() == displayMode);
    }

    private QueryPlanComposite getSelectedQueryPlanComposite() {
        if (plansTabFolder == null || plansTabFolder.getSelection() == null) {
            return null;
        }
        return (QueryPlanComposite) plansTabFolder.getSelection().getControl();
    }

    /**
     * column String sorter
     *
     * @param table
     * @param column
     */
    private void addStringSorter(final Table table, final TableColumn column) {
        column.addListener(
                SWT.Selection,
                new Listener() {
                    boolean isAscend = false;

                    public void handleEvent(Event e) {
                        int columnIndex = getColumnIndex(table, column);
                        TableItem[] items = table.getItems();
                        sortString(items, columnIndex, isAscend);
                        table.setSortColumn(column);
                        table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
                        isAscend = !isAscend;
                        currentSortTableColumn = column;
                        sortStyle.put(currentSortTableColumn, isAscend);
                    }
                });
    }

    /**
     * addNumberSorter
     *
     * @param table
     * @param column
     */
    private void addNumberSorter(final Table table, final TableColumn column) {
        column.addListener(
                SWT.Selection,
                new Listener() {
                    boolean isAscend = false;

                    public void handleEvent(Event e) {
                        int columnIndex = getColumnIndex(table, column);
                        TableItem[] items = table.getItems();
                        // sort
                        sortNumber(items, columnIndex, isAscend);
                        table.setSortColumn(column);
                        table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
                        isAscend = !isAscend;
                        currentSortTableColumn = column;
                        sortStyle.put(currentSortTableColumn, isAscend);
                    }
                });
    }

    /**
     * getTableItemText
     *
     * @param table
     * @param item
     * @return
     */
    private String[] getTableItemText(Table table, TableItem item) {
        int count = table.getColumnCount();
        String[] strs = new String[count];
        for (int i = 0; i < count; i++) {
            strs[i] = item.getText(i);
        }
        return strs;
    }

    /**
     * getColumnIndex
     *
     * @param table
     * @param column
     * @return
     */
    private int getColumnIndex(Table table, TableColumn column) {
        TableColumn[] columns = table.getColumns();
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(column)) return i;
        }
        return -1;
    }

    /** refreshTableByCurrentSort */
    private void refreshTableByCurrentSort() {
        boolean isAscend = false;
        if (currentSortTableColumn == null || sortStyle.get(currentSortTableColumn) == null) {
            currentSortTableColumn = planHistoryTblCols[0];
        } else {
            isAscend = !sortStyle.get(currentSortTableColumn);
        }
        int columnIndex = getColumnIndex(planHistoryTable, currentSortTableColumn);
        TableItem[] items = planHistoryTable.getItems();
        if (sortType.get(currentSortTableColumn).equals("STRING")) {
            sortString(items, columnIndex, isAscend);
        } else {
            sortNumber(items, columnIndex, isAscend);
        }
    }

    /**
     * sort by compare String
     *
     * @param items
     * @param columnIndex
     * @param isAscend
     */
    private void sortString(TableItem[] items, int columnIndex, boolean isAscend) {
        for (int i = 1; i < items.length; i++) {
            String str2value = items[i].getText(columnIndex);
            if (str2value.equalsIgnoreCase("")) {
                break;
            }
            for (int j = 0; j < i; j++) {
                String str1value = items[j].getText(columnIndex);
                boolean isLessThan = comparator.compare(str2value, str1value) < 0;
                if ((isAscend && isLessThan) || (!isAscend && !isLessThan)) {
                    String[] values = getTableItemText(planHistoryTable, items[i]);
                    Object obj = items[i].getData();
                    items[i].dispose();
                    TableItem item = new TableItem(planHistoryTable, SWT.NONE, j);
                    item.setText(values);
                    item.setData(obj);
                    items = planHistoryTable.getItems();
                    break;
                }
            }
        }
    }

    /**
     * sort by compare String
     *
     * @param items
     * @param columnIndex
     * @param isAscend
     */
    private void sortNumber(TableItem[] items, int columnIndex, boolean isAscend) {
        for (int i = 1; i < items.length; i++) {
            String strvalue2 = items[i].getText(columnIndex);
            if (strvalue2.equalsIgnoreCase("")) {
                break;
            }
            for (int j = 0; j < i; j++) {
                String strvalue1 = items[j].getText(columnIndex);
                float numbervalue1 = Float.valueOf(strvalue1);
                float numbervalue2 = Float.valueOf(strvalue2);
                boolean isLessThan = false;
                if (numbervalue2 < numbervalue1) {
                    isLessThan = true;
                }
                if ((isAscend && isLessThan) || (!isAscend && !isLessThan)) {
                    String[] values = getTableItemText(planHistoryTable, items[i]);
                    Object obj = items[i].getData();
                    items[i].dispose();
                    TableItem item = new TableItem(planHistoryTable, SWT.NONE, j);
                    item.setText(values);
                    item.setData(obj);
                    items = planHistoryTable.getItems();
                    break;
                }
            }
        }
    }

    /**
     * delete history only delete from tableItem in planHistoryTable and Preference don't delete it
     * from planHistoryList because planHistoryList keep the uid and index relationship
     */
    private void deleteHistory() {
        MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messageBox.setText(Messages.tooltip_qedit_explain_history_delete);
        messageBox.setMessage(Messages.explain_history_delete_message);
        // remove data, both view and model
        int buttonID = messageBox.open();
        if (buttonID == SWT.YES) {
            List<Integer> deleteIndex = new ArrayList<Integer>();
            for (int i = 0; i < planHistoryTable.getSelectionIndices().length; i++) {
                deleteIndex.add(planHistoryTable.getSelectionIndices()[i]);
            }
            List<StructQueryPlan> deleteList = new ArrayList<StructQueryPlan>();
            for (int i = 0; i < deleteIndex.size(); i++) {
                int seletectIndex = deleteIndex.get(i);
                int newIndex = seletectIndex - i;
                TableItem tableItem = planHistoryTable.getItem(newIndex);
                if (tableItem == null) {
                    return;
                }
                int uid = Integer.valueOf(tableItem.getText(0));
                PlanTabItem tabItem = findPlanTab(uid);
                if (tabItem != null) {
                    if (!tabItem.isDisposed()) {
                        tabItem.dispose();
                    }
                }

                if (!tableItem.isDisposed()) {
                    tableItem.dispose();
                }
                StructQueryPlan sq = planHistoryList.get(uid - 1);
                deleteList.add(sq);
            }
            PlanHistoryManager.deleteStructQuerysFromPreference(
                    editor.getSelectedDatabase(), deleteList);
        }
    }

    private void createPlanToolbar() {
        Composite toolBarComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 10;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        toolBarComposite.setLayout(gridLayout);
        toolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));

        ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);

        dispModeTextItem = new ToolItem(toolBar, SWT.CHECK);
        dispModeTextItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_raw.png"));
        dispModeTextItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode + "(F11)");
        dispModeTextItem.setText(Messages.lblPlanRawBtn);
        dispModeTextItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        showSelectedPlan(PLAN_DISPLAY_MODE.TEXT.getInt());
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        dispModeTreeItem = new ToolItem(toolBar, SWT.CHECK);
        dispModeTreeItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_tree.png"));
        dispModeTreeItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode + "(F11)");
        dispModeTreeItem.setText(Messages.lblPlanTreeBtn);
        dispModeTreeItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        showSelectedPlan(PLAN_DISPLAY_MODE.TREE.getInt());
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        dispModeGraphItem = new ToolItem(toolBar, SWT.CHECK);
        dispModeGraphItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_graph.png"));
        dispModeGraphItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode + "(F11)");
        dispModeGraphItem.setText(Messages.lblPlanGraph);
        dispModeGraphItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        showSelectedPlan(PLAN_DISPLAY_MODE.GRAPH.getInt());
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        editTableItem = new ToolItem(toolBar, SWT.None);
        editTableItem.setImage(CommonUIPlugin.getImage("icons/navigator/schema_table_item.png"));
        editTableItem.setText(Messages.lblPlanEditTable);
        editTableItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        QueryPlanComposite comp = getSelectedQueryPlanComposite();
                        String tableName = comp.getSelectedTable();
                        if (StringUtil.isEmpty(tableName)) {
                            CommonUITool.openErrorBox(Messages.msgPlanEditTable);
                            return;
                        }
                        ISchemaNode node =
                                new DefaultSchemaNode(tableName, tableName, tableName, null);
                        node.setDatabase(editor.getSelectedDatabase());
                        EditTableAction action =
                                (EditTableAction)
                                        ActionManager.getInstance().getAction(EditTableAction.ID);
                        if (action == null) {
                            CommonUITool.openErrorBox(Messages.errPlanEditNoAction);
                            return;
                        }
                        action.run((ISchemaNode) node);
                    }
                });

        editIndexItem = new ToolItem(toolBar, SWT.None);
        editIndexItem.setImage(CommonUIPlugin.getImage("icons/navigator/table_index_item.png"));
        editIndexItem.setText(Messages.lblPlanEditIndex);
        editIndexItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        QueryPlanComposite comp = getSelectedQueryPlanComposite();
                        String tableName = comp.getSelectedTable();
                        //				String indexName = comp.getSelectedIndex();
                        if (StringUtil.isEmpty(tableName)) {
                            CommonUITool.openErrorBox(Messages.msgPlanEditIndex);
                            return;
                        }
                        ISchemaNode node =
                                new DefaultSchemaNode(tableName, tableName, tableName, null);
                        node.setDatabase(editor.getSelectedDatabase());
                        EditTableAction action =
                                (EditTableAction)
                                        ActionManager.getInstance().getAction(EditTableAction.ID);
                        if (action == null) {
                            CommonUITool.openErrorBox(Messages.errPlanEditNoAction);
                            return;
                        }
                        action.editIndexMode((ISchemaNode) node);
                    }
                });

        ToolItem comparePlanItem = new ToolItem(toolBar, SWT.None);
        comparePlanItem.setImage(
                CommonUIPlugin.getImage("icons/queryplan/use_compare_queryplan.png"));
        comparePlanItem.setText(Messages.lblComparePlan);
        comparePlanItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        QueryPlanComposite comp = getSelectedQueryPlanComposite();
                        StructQueryPlan sq = comp.getQueryPlan();
                        TuneModeModel tuneModeModel = new TuneModeModel(sq, null);
                        editor.displayTuneModeResult(tuneModeModel);
                    }
                });

        ToolBar delHistoryToolBar = new ToolBar(toolBarComposite, SWT.FLAT);
        delHistoryToolBar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, true));

        // Show/hide of the history pane
        historyShowHideItem = new ToolItem(delHistoryToolBar, SWT.PUSH);
        historyShowHideItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_history_hide.png"));
        historyShowHideItem.setToolTipText(Messages.tooltip_qedit_explain_history_show_hide);
        historyShowHideItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        boolean isShow = !planHistoryTable.getVisible();
                        if (isShow) {
                            showHistoryPane();
                        } else {
                            hideHistoryPane();
                        }
                    }
                });

        new ToolItem(delHistoryToolBar, SWT.SEPARATOR);

        // Collecting histories switch
        historySwitchItem = new ToolItem(delHistoryToolBar, SWT.CHECK);
        historySwitchItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_history_switch.png"));
        historySwitchItem.setToolTipText(Messages.tooltip_qedit_explain_history_switch);
        historySwitchItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        collectToHistoryFlag = !historySwitchItem.getSelection();
                    }
                });
        historySwitchItem.setSelection(true);

        new ToolItem(delHistoryToolBar, SWT.SEPARATOR);

        delHistory = new ToolItem(delHistoryToolBar, SWT.PUSH);
        delHistory.setImage(CommonUIPlugin.getImage("icons/action/table_record_delete.png"));
        delHistory.setDisabledImage(
                CommonUIPlugin.getImage("icons/action/table_record_delete_disabled.png"));
        delHistory.setToolTipText(Messages.tooltip_qedit_explain_history_delete);
        delHistory.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (planHistoryTable.getSelectionIndices().length == 0) {
                            MessageDialog.openError(
                                    PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                    Messages.error,
                                    Messages.explain_history_delete_error);
                            return;
                        }
                        deleteHistory();
                    }
                });
    }

    private void showHistoryPane() {
        planHistoryTable.setVisible(true);
        historyShowHideItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_history_hide.png"));
        plansHistorySash.setWeights(sashPlanWeight);
        delHistory.setEnabled(true);
    }

    private void hideHistoryPane() {
        planHistoryTable.setVisible(false);
        historyShowHideItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_history_show.png"));
        sashPlanWeight = plansHistorySash.getWeights();
        plansHistorySash.setWeights(new int[] {100, 0});
        delHistory.setEnabled(false);
    }

    public void rotateQueryPlanDisplayMode() {
        if (dispModeTextItem == null || dispModeTreeItem == null || dispModeGraphItem == null) {
            return;
        }
        if (dispModeTextItem.isDisposed()
                || dispModeTreeItem.isDisposed()
                || dispModeGraphItem.isDisposed()) {
            return;
        }
        int rotatingMenuIndex = 0;
        if (dispModeTextItem.getSelection()) {
            rotatingMenuIndex = PLAN_DISPLAY_MODE.TREE.getInt();
        } else if (dispModeTreeItem.getSelection()) {
            rotatingMenuIndex = PLAN_DISPLAY_MODE.GRAPH.getInt();
        } else {
            rotatingMenuIndex = PLAN_DISPLAY_MODE.TEXT.getInt();
        }
        showSelectedPlan(rotatingMenuIndex);
    }

    private void showSelectedPlan(int displayMode) {
        refreshToolbarStatus(displayMode);
        QueryOptions.setQueryPlanDisplayType(displayMode);

        CTabItem[] items = plansTabFolder.getItems();
        for (int i = 0, len = items.length; i < len; i++) {
            if (items[i] instanceof PlanTabItem) {
                PlanTabItem item = (PlanTabItem) items[i];
                item.getQueryPlanComposite().useDisplayMode(displayMode);
            }
        }
    }

    private void clearAll() {
        planHistoryList.clear();
        for (int i = planHistoryTable.getItemCount() - 1; i >= 0; i--) {
            planHistoryTable.getItem(0).dispose();
        }

        for (int i = plansTabFolder.getItemCount() - 1; i >= 0; i--) {
            PlanTabItem tabItem = (PlanTabItem) plansTabFolder.getItem(0);
            tabItem.dispose();
        }
    }

    /** Dispose all plan tabs in plan panel */
    public void clearPlanTabs() {
        if (plansTabFolder == null || plansTabFolder.isDisposed()) {
            return;
        }

        while (plansTabFolder.getItemCount() > 0) {
            if (!plansTabFolder.getItem(0).getControl().isDisposed()) {
                plansTabFolder.getItem(0).getControl().dispose();
            }
            plansTabFolder.getItem(0).dispose();
        }
    }

    /**
     * make & init plan tab for execution plans
     *
     * @param sq StructQueryPlan
     */
    private void addPlanTab(StructQueryPlan sq) {
        if (collectToHistoryFlag) {
            PlanHistoryManager.addStructQueryPlanListToPreference(editor.getSelectedDatabase(), sq);
            planHistoryList.add(sq);
        }

        int uid = lastQueryPlanTabNum++;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<addPlanTab-uid>" + uid + "</addPlanTab-uid>");
        }

        PlanTabItem tabItem = findPlanTab(uid);
        if (tabItem == null) {
            tabItem = newPlanTab(uid);
        }

        plansTabFolder.setSelection(tabItem);

        printPlan(tabItem, sq);

        if (collectToHistoryFlag) {
            printHistory(uid, sq);
            refreshTableByCurrentSort();
        }
    }

    public void fillPlanHistory() {
        clearAll();
        planHistoryList = new ArrayList<StructQueryPlan>();
        List<StructQueryPlan> StructQueryPlanList =
                PlanHistoryManager.getStructQueryPlanListFromPreference(
                        editor.getSelectedDatabase());
        if (StructQueryPlanList != null) {
            planHistoryList.addAll(StructQueryPlanList);
        }
        if (planHistoryList.size() == 0) {
            PlanTabItem tabItem = newPlanTab(1);
            plansTabFolder.setSelection(tabItem);
        }
        int index = 1;
        for (int i = 0, len = planHistoryList.size(); i < len; i++) {
            StructQueryPlan sq = planHistoryList.get(i);
            PlanResult planRoot = sq.getSubPlan(0);
            if (planRoot == null) {
                continue;
            }
            printHistory(index, sq);
            index++;
        }
        refreshTableByCurrentSort();
    }

    /**
     * find a existing tab item object with a uid
     *
     * @param uid int
     * @return planTabItem
     */
    private PlanTabItem findPlanTab(int uid) {
        if (plansTabFolder == null || plansTabFolder.getItemCount() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("<findPlanTab-return>null</findPlanTab-return>");
            }
            return null;
        }

        String findTabName = uid > 0 ? Messages.qedit_plan + uid : Messages.qedit_plan;
        for (int i = 0, len = plansTabFolder.getItemCount(); i < len; i++) {
            String tabName = plansTabFolder.getItem(i).getText();
            if (findTabName.equals(tabName)) {
                return (PlanTabItem) plansTabFolder.getItem(i);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<findPlanTab-return>null</findPlanTab-return>");
        }

        return null;
    }

    /**
     * create a new plan tab
     *
     * @param uid int
     * @return planTabItem
     */
    private PlanTabItem newPlanTab(int uid) {
        final PlanTabItem planTabItem = new PlanTabItem(plansTabFolder, SWT.NONE);
        planTabItem.setText(uid > 0 ? Messages.qedit_plan + uid : Messages.qedit_plan);

        DatabaseInfo databaseInfo =
                (editor == null || editor.getSelectedDatabase() == null)
                        ? null
                        : editor.getSelectedDatabase().getDatabaseInfo();
        QueryPlanComposite queryPlanComposite =
                new QueryPlanComposite(plansTabFolder, SWT.None, null, databaseInfo, true);
        setupContextMenu(queryPlanComposite.getPlanTree());
        CommonUITool.registerCopyPasteContextMenu(queryPlanComposite.getPlanRaw(), false);

        planTabItem.setQueryPlanComposite(queryPlanComposite);
        planTabItem.setControl(queryPlanComposite);
        return planTabItem;
    }

    private void setupContextMenu(final Tree tree) {
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        //				IAction showSchemaAction =
                        // ActionManager.getInstance().getAction(ShowSchemaAction.ID);
                        //				if (showSchemaAction != null) {
                        //					manager.add(showSchemaAction);
                        //
                        //					showSchemaAction.setEnabled(false);
                        //					if
                        // (DatabaseNavigatorMenu.SELF_DATABASE_ID.equals(editor.getSelectedDatabase().getId())) {
                        //						return;
                        //					}
                        //
                        //					if (editor.getSelectedDatabase().getDatabaseInfo() == null) {
                        //						return;
                        //					}
                        //
                        //					if (tree.getSelection() != null && tree.getSelection().length > 0) {
                        //						String tname = tree.getSelection()[0].getText(1);
                        //						if (tname == null || tname.length() == 0) {
                        //							return;
                        //						}
                        //
                        //						int ep = tname.indexOf(' ');
                        //						if (ep != -1) {
                        //							tname = tname.substring(0, ep);
                        //						}
                        //						editor.setCurrentSchemaName(tname);
                        //						if (tname != null) {
                        //							showSchemaAction.setEnabled(true);
                        //						}
                        //					}
                        //				}
                    }
                });

        Menu contextMenu = menuManager.createContextMenu(tree);
        tree.setMenu(contextMenu);
    }

    /**
     * print the plan
     *
     * @param tabItem PlanTabItem
     * @param sq StructQueryPlan
     */
    private void printPlan(final PlanTabItem tabItem, StructQueryPlan sq) {
        if (tabItem == null || sq == null) {
            return;
        }
        tabItem.getQueryPlanComposite().setQueryRecord(sq);

        switch (QueryOptions.getQueryPlanDisplayType()) {
            case QueryOptions.QUERY_PLAN_DISPLAY_MODE_TEXT:
                tabItem.getQueryPlanComposite().useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());
                break;
            case QueryOptions.QUERY_PLAN_DISPLAY_MODE_TREE:
                tabItem.getQueryPlanComposite().useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());
                break;
            case QueryOptions.QUERY_PLAN_DISPLAY_MODE_GRAPH:
                tabItem.getQueryPlanComposite().useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());
                break;
        }
    }

    /**
     * print a plan history
     *
     * @param uid int
     * @param sq StructQueryPlan
     */
    private void printHistory(int uid, StructQueryPlan sq) {
        String created = sq.getCreatedDateString();

        float costValue = 0.0f;

        PlanResult planRoot = sq.getSubPlan(0);
        if (planRoot == null) {
            return;
        }

        for (int i = 0, len = sq.countSubPlan(); i < len; i++) {
            planRoot = sq.getSubPlan(i);
            PlanNode node = planRoot.getPlanNode();

            if (node != null && node.getCost() != null) {
                PlanCost cost = node.getCost();
                costValue += cost.getTotal();
            }
        }

        TableItem item = new TableItem(planHistoryTable, SWT.LEFT);

        item.setText(0, String.valueOf(uid));
        item.setText(1, created);
        item.setText(2, String.valueOf(costValue));
        String sql = planRoot.getPlainSql();
        if (sql.length() > 100) {
            sql = sql.substring(0, 96);
            sql += "...";
        }
        item.setText(3, sql);
        for (int i = 0, len = planHistoryTblCols.length; i < len; i++) {
            if (planHistoryTblCols[i] != null && !planHistoryTblCols[i].isDisposed()) {
                planHistoryTblCols[i].pack();
            }
        }
    }

    /** @see org.eclipse.swt.widgets.Widget#dispose() */
    public void dispose() {
        clearPlanTabs();

        if (!planHistoryTable.isDisposed()) {
            planHistoryTable.dispose();
        }

        for (int i = 0, len = planHistoryTblCols.length; i < len; i++) {
            if (planHistoryTblCols[i] != null && !planHistoryTblCols[i].isDisposed()) {
                planHistoryTblCols[i].dispose();
            }
        }

        super.dispose();
    }

    /**
     * Make the plan
     *
     * @param sq StructQueryPlan
     * @param tabIdx int
     */
    public void makePlan(StructQueryPlan sq, int tabIdx) {
        addPlanTab(sq);
        resultTabFolder.setSelection(explainTabItem);
    }

    /**
     * The Query Execution Plan Sub Tab Item in the Query Editor
     *
     * @author pcraft 2009-4-28
     */
    static class PlanTabItem extends CTabItem {
        /** a list index no of StructQueryPlan ArrayList in plan history pane */
        private int uid = 0;

        private QueryPlanComposite queryPlanComposite;

        public PlanTabItem(CTabFolder parent, int style, int index) {
            super(parent, style, index);
        }

        public PlanTabItem(CTabFolder parent, int style) {
            super(parent, style);
        }

        public int getUid() {

            return uid;
        }

        public void setUid(int uid) {

            this.uid = uid;
        }

        /** @return the queryPlanComposite */
        public QueryPlanComposite getQueryPlanComposite() {
            return queryPlanComposite;
        }

        /** @param queryPlanComposite the queryPlanComposite to set */
        public void setQueryPlanComposite(QueryPlanComposite queryPlanComposite) {
            this.queryPlanComposite = queryPlanComposite;
        }
    }

    /** Set selection and bring to top */
    public void setSelection() {
        plansTabFolder.setSelection(0);
    }

    /**
     * Select the sub tab by the tab index
     *
     * @param subTabIndex
     */
    public void select(int subTabIndex) {
        if (plansTabFolder == null) {
            return;
        }

        int total = plansTabFolder.getItemCount();
        if (subTabIndex >= total) {
            return;
        }

        plansTabFolder.setSelection(subTabIndex);
    }
}
