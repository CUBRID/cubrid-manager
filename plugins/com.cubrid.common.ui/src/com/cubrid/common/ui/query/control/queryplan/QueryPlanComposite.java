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
package com.cubrid.common.ui.query.control.queryplan;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.queryplan.model.PlanCost;
import com.cubrid.common.core.queryplan.model.PlanNode;
import com.cubrid.common.core.queryplan.model.PlanResult;
import com.cubrid.common.core.queryplan.model.PlanTerm;
import com.cubrid.common.core.queryplan.model.PlanTermItem;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.Map;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

/**
 * CommonQueryPlanComposite Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-16 created by Kevin.Wang
 */
public class QueryPlanComposite extends Composite {
    private static final int[] TABLE_COLS_WIDTH_DEF =
            new int[] {200, 100, 100, 250, 70, 75, 70, 500};

    private StructQueryPlan queryPlan;
    private final Tree planTree;
    private StyledText planSql;
    private final StyledText planRaw;
    private final GraphViewer planGraphic;
    private final SashForm planSashForm;
    private final SashForm treePlanSashForm;
    private DatabaseInfo databaseInfo;

    private static final int SASH_WIDTH = 3;
    private static final Color BG_SSCAN = ResourceManager.getColor(new RGB(255, 215, 215));
    private static final Color BG_ISCAN = ResourceManager.getColor(new RGB(210, 255, 200));
    private static final Color FG_TABLE = ResourceManager.getColor(new RGB(36, 36, 255));
    private static final Color FG_INDEX = ResourceManager.getColor(new RGB(255, 0, 0));

    /**
     * The constructor
     *
     * @param parent
     * @param style
     * @param queryPlan
     */
    public QueryPlanComposite(
            Composite parent, int style, StructQueryPlan queryPlan, DatabaseInfo databaseInfo) {
        this(parent, style, queryPlan, databaseInfo, false);
    }

    /**
     * The constructor
     *
     * @param parent
     * @param style
     * @param queryPlan
     * @param isShowSqlPlan
     */
    public QueryPlanComposite(
            Composite parent,
            int style,
            StructQueryPlan queryPlan,
            final DatabaseInfo databaseInfo,
            final boolean isShowSqlPlan) {
        super(parent, style);
        this.queryPlan = queryPlan;
        this.databaseInfo = databaseInfo;

        setLayout(new FillLayout());

        planSashForm = new SashForm(this, SWT.VERTICAL);
        planSashForm.SASH_WIDTH = 0;
        planSashForm.setBackground(CombinedQueryEditorComposite.BACK_COLOR);
        planSashForm.setLayout(new FillLayout());

        treePlanSashForm = new SashForm(planSashForm, SWT.VERTICAL);
        treePlanSashForm.SASH_WIDTH = SASH_WIDTH;
        treePlanSashForm.setBackground(CombinedQueryEditorComposite.BACK_COLOR);
        treePlanSashForm.setLayout(new FillLayout());
        // Plan Tree
        planTree = new Tree(treePlanSashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        planTree.setHeaderVisible(true);
        planTree.setLinesVisible(true);
        planTree.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TreeItem item = ((Tree) event.getSource()).getSelection()[0];
                        if (item != null) {
                            if (item.getData() != null) {
                                if (planSql != null && !planSql.isDisposed()) {
                                    planSql.setText((String) item.getData());
                                    updateStyledText(planSql);
                                }
                            }

                            String tableName = trimAliasOnTableName(item.getText(1));
                            CubridNavigatorView mainNav = CubridNavigatorView.findNavigationView();
                            if (mainNav != null) {
                                mainNav.showQuickView(databaseInfo, tableName, true);
                            }
                        }
                    }
                });

        TreeColumn[] cols = new TreeColumn[TABLE_COLS_WIDTH_DEF.length];
        {
            int index = 0;

            // Type
            cols[index] = new TreeColumn(planTree, SWT.LEFT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col1);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col1);

            // Table
            cols[++index] = new TreeColumn(planTree, SWT.LEFT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col2);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col2);

            // Index
            cols[++index] = new TreeColumn(planTree, SWT.LEFT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col6);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col6);

            // Terms
            cols[++index] = new TreeColumn(planTree, SWT.LEFT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col7);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col7);

            // Cost
            cols[++index] = new TreeColumn(planTree, SWT.RIGHT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col3);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col3_dtl);

            // Card
            cols[++index] = new TreeColumn(planTree, SWT.RIGHT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col8);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col8_dtl);

            // Row/Page
            cols[++index] = new TreeColumn(planTree, SWT.RIGHT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col5);
            cols[index].setToolTipText(Messages.qedit_plan_tree_simple_col5_dtl);

            // Extra informations
            cols[++index] = new TreeColumn(planTree, SWT.LEFT);
            cols[index].setText(Messages.qedit_plan_tree_simple_col9);

            for (int columIndex = 0; columIndex < cols.length; columIndex++) {
                cols[columIndex].setMoveable(columIndex > 0);
            }

            for (int i = 0, len = TABLE_COLS_WIDTH_DEF.length; i < len; i++) {
                int tColMaxWidth = TABLE_COLS_WIDTH_DEF[i];
                cols[i].pack();
                if (cols[i].getWidth() < tColMaxWidth) {
                    cols[i].setWidth(tColMaxWidth);
                }
            }
        }
        if (isShowSqlPlan) {
            // Parsed SQL
            planSql =
                    new StyledText(
                            treePlanSashForm,
                            SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
            planSql.setLayout(new FillLayout());
            planSql.setToolTipText(Messages.tooltipHowToExpandLogPane);
            planSql.addFocusListener(
                    new FocusListener() {
                        public void focusLost(FocusEvent e) {
                            treePlanSashForm.setWeights(new int[] {70, 30});
                        }

                        public void focusGained(FocusEvent e) {
                            treePlanSashForm.setWeights(new int[] {30, 70});
                        }
                    });
            CommonUITool.registerCopyPasteContextMenu(planSql, false);
            treePlanSashForm.setWeights(new int[] {70, 30});
        }

        // Raw Plan
        planRaw =
                new StyledText(
                        planSashForm,
                        SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        // planRaw.setLayout(new FillLayout());
        planRaw.setVisible(false);

        // Graphic Plan
        planGraphic = new GraphViewer(planSashForm, SWT.NONE);
        // Graphic Plan
        UpdateManager updateManager =
                planGraphic.getGraphControl().getLightweightSystem().getUpdateManager();

        planGraphic.setContentProvider(new GraphPlanContentProvider<Object, Object>());
        planGraphic.setLabelProvider(new GraphPlanStyleProvider());
        updateManager.addUpdateListener(
                new UpdateListener() {
                    public void notifyValidating() {}

                    public void notifyPainting(
                            Rectangle damage, @SuppressWarnings("rawtypes") Map dirtyRegions) {
                        // re-layout when Graph resized
                        planGraphic.getGraphControl().applyLayout();
                    }
                });

        planGraphic.setLayoutAlgorithm(
                new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), false);
        planGraphic.setUseHashlookup(true);
        planGraphic
                .getGraphControl()
                .getLightweightSystem()
                .setEventDispatcher(
                        new SWTEventDispatcher() {
                            public void dispatchMouseMoved(org.eclipse.swt.events.MouseEvent me) {
                                // keep empty to remove the DnD function
                            }
                        });
        planGraphic.getGraphControl().setHorizontalScrollBarVisibility(FigureCanvas.ALWAYS);
        planGraphic.getGraphControl().setVerticalScrollBarVisibility(FigureCanvas.ALWAYS);

        planSashForm.setWeights(new int[] {100, 0, 0});

        displayPlan();
    }

    /**
     * print the plan
     *
     * @param tabItem PlanTabItem
     * @param sq StructQueryPlan
     */
    public void displayPlan() {
        printTextPlan();
        printTreePlan();
        printGraphicPlan();
    }

    /**
     * Print the raw text query execution plan on the tab
     *
     * @param tabItem
     * @param sq
     */
    private void printTextPlan() {
        if (queryPlan == null || queryPlan.getPlanRaw() == null) {
            planRaw.setText("");
        } else {
            planRaw.setText(queryPlan.getPlanRaw());
        }
    }

    /**
     * Print the tree style of the query execution plan on the tab
     *
     * @param tabItem
     * @param sq
     */
    private void printTreePlan() {
        // clear tab item contents
        while (planTree.getItemCount() > 0) {
            planTree.getItem(0).dispose();
        }

        if (queryPlan != null) {
            PlanNode planRoot = makeRootPlanNode(queryPlan, false);
            TreeItem treeItem = printSubPlan(null, planRoot, "");

            for (int i = 0, len = queryPlan.countSubPlan(); i < len; i++) {
                PlanResult plan = queryPlan.getSubPlan(i);
                if (plan == null) {
                    continue;
                }

                // print a raw plan
                if (i == 0) {
                    String log =
                            "Query:"
                                    + StringUtil.NEWLINE
                                    + plan.getSql()
                                    + StringUtil.NEWLINE
                                    + StringUtil.NEWLINE
                                    + "Execution Plan:"
                                    + StringUtil.NEWLINE
                                    + plan.getParsedRaw();
                    if (planSql != null && !planSql.isDisposed()) {
                        planSql.setText(log);
                    }

                    decorateSqlText();
                }

                PlanNode node = plan.getPlanNode();
                if (node != null) {
                    String log =
                            "Query:"
                                    + StringUtil.NEWLINE
                                    + plan.getSql()
                                    + StringUtil.NEWLINE
                                    + StringUtil.NEWLINE
                                    + "Execution Plan:"
                                    + StringUtil.NEWLINE
                                    + plan.getParsedRaw();
                    printSubPlan(treeItem, node, log);
                }
            }
        }

        packPlanTree();
    }

    /**
     * Print the graphic style of the query execution plan on the tab
     *
     * @param tabItem
     * @param sq
     */
    private void printGraphicPlan() {
        if (queryPlan != null) {
            PlanNode planRoot = makeRootPlanNode(queryPlan, true);

            planGraphic.setInput(planRoot);
        } else {
            planGraphic.setInput(null);
        }
    }

    public void decorateSqlText() {
        if (planSql != null && !planSql.isDisposed()) {
            updateStyledText(planSql);
        }
        updateStyledText(planRaw);
    }

    private void updateStyledText(StyledText styledText) {
        final String[] titleString = {
            "Join graph segments (f indicates final):",
            "Join graph nodes:",
            "Join graph equivalence classes:",
            "Join graph edges:",
            "Join graph terms:",
            "Query plan:",
            "Query stmt:",
            "Query:",
            "Execution Plan:"
        };

        for (String find : titleString) {
            int sp = -1;
            int ep = 0;

            for (; ; ) {
                sp = styledText.getText().indexOf(find, ep);
                if (sp == -1) {
                    break;
                }

                StyleRange eachStyle = new StyleRange();
                eachStyle.start = sp;
                eachStyle.length = find.length();
                eachStyle.fontStyle = SWT.BOLD;
                eachStyle.foreground = getDisplay().getSystemColor(SWT.COLOR_RED);
                styledText.setStyleRange(eachStyle);

                ep = sp + 1;
            }
        }
    }

    /**
     * Make the root node if there have many root nodes.
     *
     * @param sq
     * @param makeNestedNodes
     * @return
     */
    private PlanNode makeRootPlanNode(StructQueryPlan sq, boolean makeNestedNodes) {
        PlanNode planRoot = new PlanNode();
        planRoot.setMethod(Messages.lblPlanQuery);
        planRoot.setDepth(0);
        planRoot.setCost(new PlanCost());
        int costVal = 0;
        for (int i = 0, len = sq.countSubPlan(); i < len; i++) {
            PlanResult result = sq.getSubPlan(i);
            if (result == null) {
                continue;
            }

            PlanNode node = result.getPlanNode();
            if (node != null) {
                if (makeNestedNodes) {
                    planRoot.addChild(node);
                }
                costVal += node.getCost().getTotal();
            }
        }
        planRoot.getCost().setTotal(costVal);
        planRoot.getCost().setCard(-1);

        return planRoot;
    }

    /**
     * print the sub plan.
     *
     * @param tabItem PlanTabItem
     * @param treeItem TreeItem
     * @param node PlanNode
     * @param sql String
     */
    private TreeItem printSubPlan(TreeItem treeItem, PlanNode node, String sql) {
        boolean isRoot = treeItem == null;
        boolean existChildren = node.getChildren() != null && node.getChildren().size() > 0;

        TreeItem item = null;
        if (isRoot) {
            item = new TreeItem(planTree, SWT.NONE);
            item.setData(sql);
        } else {
            item = new TreeItem(treeItem, SWT.NONE);
            item.setData(sql);
        }

        String icon = null;
        boolean isIndex = node.getIndex() != null;
        boolean isTable = node.getTable() != null && node.getTable().getPartitions() != null;
        if ("idx-join".equals(node.getMethod())) {
            icon = "icons/queryeditor/qe_explain_index_join.png";
        } else if (existChildren) {
            icon = "icons/queryeditor/qe_explain_folder.png";
        } else if (isIndex) {
            icon = "icons/queryeditor/qe_explain_index.png";
        } else if (isTable) {
            icon = "icons/queryeditor/qe_explain_partition.png";
        } else {
            icon = "icons/queryeditor/qe_explain_table.png";
        }

        // Type
        int i = 0;
        item.setImage(i, CommonUIPlugin.getImage(icon));
        if (node.getMethod() != null) {
            item.setText(i++, node.getMethodTitle());
            if (node.getMethod().startsWith("temp") || node.getMethod().startsWith("sscan")) {
                item.setBackground(BG_SSCAN);
            } else if (node.getMethod().startsWith("iscan")) {
                item.setBackground(BG_ISCAN);
            }
        }
        //		if (node.getOrder() == null) {
        //			item.setText(i++, node.getMethod());
        //		} else {
        //			item.setText(i++, node.getMethod() + "(order:"
        //					+ node.getOrder().toLowerCase() + ")");
        //		}

        // Table
        if (node.getTable() == null) {
            item.setText(i++, "");
        } else {
            item.setForeground(i, FG_TABLE);
            item.setText(i++, node.getTable().getName());
        }

        // Index
        if (node.getIndex() == null) {
            item.setText(i++, "");
        } else {
            item.setForeground(i, FG_INDEX);
            PlanTerm index = node.getIndex();
            item.setText(i++, index.getName());
        }

        // Terms
        if (node.getTable() == null || node.getTable().getPartitions() == null) {
            item.setText(i++, "");
        } else {
            item.setText(i++, node.getTable().getTextPartitions());
        }

        // Cost
        if (node.getCost() == null) {
            item.setText(i++, "");
            item.setText(i++, "");
        } else {
            PlanCost cost = node.getCost();
            item.setText(i++, String.valueOf(cost.getTotal()));
            if (node.getCost().getCard() < 0) {
                item.setText(i++, "-");
            } else {
                item.setText(i++, String.valueOf(cost.getCard()));
            }
        }

        // Row/Page
        if (node.getTable() == null) {
            item.setText(i++, "");
        } else {
            item.setText(i++, node.getTable().getCard() + "/" + node.getTable().getPage());
        }

        // Extra information
        if (node.getSort() == null) {
            item.setText(i++, "");
        } else {
            item.setText(i++, "(sort " + node.getSort() + ")");
        }

        boolean isOddRow = false;

        if (node.getIndex() != null) {
            PlanTerm subTerm = node.getIndex();
            // It do not make a child node if it is iscan and it has only 1 child.
            if ("iscan".equals(node.getMethod()) && subTerm.hasSingleTerm()) {
                overwritePlanTreeItem(item, subTerm);
            } else {
                printSubPlanTerm(item, subTerm, subTerm.getTypeString(), (isOddRow = !isOddRow));
            }
        }

        if (node.getEdge() != null) {
            PlanTerm subTerm = node.getEdge();
            // It do not make a child node if it is iscan and it has only 1 child.
            if (("follow".equals(node.getMethod()) || node.getMethod().startsWith("nl-join"))
                    && subTerm.hasSingleTerm()
            /*&& "join".equals(subTerm.getName())*/ ) {
                overwritePlanTreeItem(item, subTerm);
            } else {
                printSubPlanTerm(item, subTerm, subTerm.getTypeString(), (isOddRow = !isOddRow));
            }
        }

        if (node.getSargs() != null) {
            PlanTerm subTerm = node.getSargs();
            // It do not make a child node if it is iscan and it has only 1 child.
            if ("sscan".equals(node.getMethod()) && subTerm.hasSingleTerm()) {
                overwritePlanTreeItem(item, subTerm);
            } else {
                printSubPlanTerm(item, subTerm, subTerm.getTypeString(), (isOddRow = !isOddRow));
            }
        }

        if (node.getFilter() != null) {
            printSubPlanTerm(
                    item,
                    node.getFilter(),
                    node.getFilter().getTypeString(),
                    (isOddRow = !isOddRow));
        }

        item.setExpanded(true);

        if (!isRoot) {
            treeItem.setExpanded(true);
        }

        if (existChildren) {
            for (PlanNode childNode : node.getChildren()) {
                printSubPlan(item, childNode, null);
            }
        }

        return item;
    }

    /**
     * Resize plan tree
     *
     * @param tabItem
     */
    private void packPlanTree() {
        for (int i = 0; i < TABLE_COLS_WIDTH_DEF.length; i++) {
            int tColMaxWidth = TABLE_COLS_WIDTH_DEF[i];
            planTree.getColumn(i).pack();
            if (planTree.getColumn(i).getWidth() > tColMaxWidth) {
                planTree.getColumn(i).setWidth(tColMaxWidth);
            }
        }
    }

    private void overwritePlanTreeItem(TreeItem item, PlanTerm indexTerm) {
        item.setText(3, indexTerm.getTermString());
        item.setText(7, indexTerm.getTermItems()[0].getAttribute());
    }

    /**
     * print the sub plan term.
     *
     * @param treeItem TreeItem
     * @param term PlanTerm
     * @param typeName String
     * @param isOddRow boolean
     */
    private void printSubPlanTerm(
            TreeItem treeItem, PlanTerm term, String typeName, boolean isOddRow) {
        PlanTermItem[] termItems = term.getTermItems();
        if (termItems == null) {
            return;
        }

        String icon = "icons/queryeditor/qe_explain_table.png";
        int len = termItems.length;
        if (len == 1) {
            TreeItem termTreeItem = new TreeItem(treeItem, SWT.NONE);
            termTreeItem.setText(0, typeName);
            termTreeItem.setImage(0, CommonUIPlugin.getImage(icon));
            termTreeItem.setText(3, term.getTermString());
            if (termItems[0] != null) {
                termTreeItem.setText(7, termItems[0].getAttribute());
            }
        } else {
            PlanTermItem planTermItem = termItems[0];
            if (planTermItem == null || planTermItem.getCondition() == null) {
                return;
            }

            for (int j = 0; j < len; j++) {
                planTermItem = termItems[j];
                if (planTermItem == null || planTermItem.getCondition() == null) {
                    continue;
                }

                TreeItem item = new TreeItem(treeItem, SWT.NONE);

                // Type
                item.setText(0, typeName + " " + (j + 1));
                item.setImage(0, CommonUIPlugin.getImage(icon));

                int i = 1;

                // Table
                item.setText(i++, "");

                // Index
                item.setText(i++, "");

                // Terms
                item.setText(i++, planTermItem.getCondition());

                // Cost
                item.setText(i++, "");

                // Card
                item.setText(i++, "");

                // Row/Page
                item.setText(i++, "");

                // Extra informations
                item.setText(i++, planTermItem.getAttribute());

                item.setExpanded(true);
            }
        }
    }

    /**
     * use treemode
     *
     * @param isTreeMode boolean
     */
    public void useDisplayMode(int mode) {
        if (mode == PLAN_DISPLAY_MODE.TREE.getInt()) {
            planTree.setVisible(true);
            if (planSql != null && !planSql.isDisposed()) {
                planSql.setVisible(true);
            }
            planRaw.setVisible(false);
            planGraphic.getGraphControl().setVisible(false);

            planSashForm.setWeights(new int[] {100, 0, 0});
        } else if (mode == PLAN_DISPLAY_MODE.TEXT.getInt()) {
            planTree.setVisible(false);
            if (planSql != null && !planSql.isDisposed()) {
                planSql.setVisible(false);
            }
            planRaw.setVisible(true);
            planGraphic.getGraphControl().setVisible(false);

            planSashForm.setWeights(new int[] {0, 100, 0});
        } else if (mode == PLAN_DISPLAY_MODE.GRAPH.getInt()) {
            planTree.setVisible(false);
            if (planSql != null && !planSql.isDisposed()) {
                planSql.setVisible(false);
            }
            planRaw.setVisible(false);
            planGraphic.getGraphControl().setVisible(true);

            planSashForm.setWeights(new int[] {0, 0, 100});
        }
    }

    public void setQueryRecord(StructQueryPlan queryPlan) {
        this.queryPlan = queryPlan;
        displayPlan();
    }

    public StructQueryPlan getQueryRecord() {
        return queryPlan;
    }

    public Tree getPlanTree() {
        return planTree;
    }

    /**
     * Get plan graphic
     *
     * @return the planGraphic
     */
    public GraphViewer getPlanGraphic() {
        return planGraphic;
    }

    /**
     * Get plan raw text
     *
     * @return the planRaw
     */
    public StyledText getPlanRaw() {
        return planRaw;
    }

    /**
     * Return selected table on the query plan. If it have no table on this node, it will return
     * null.
     */
    public String getSelectedTable() { // FIXME move this logic to core module
        TreeItem[] items = planTree.getSelection();
        if (items == null || items.length == 0) {
            return null;
        }
        return trimAliasOnTableName(items[0].getText(1));
    }

    /**
     * Return selected index on the query plan. If it have no index on this node, it will return
     * null.
     */
    public String getSelectedIndex() { // FIXME move this logic to core module
        TreeItem[] items = planTree.getSelection();
        if (items == null || items.length == 0) {
            return null;
        }
        return items[0].getText(2);
    }

    public StructQueryPlan getQueryPlan() {
        return queryPlan;
    }

    /**
     * Remove an alias name if the table has it.
     *
     * @param tableName
     * @return
     */
    private String trimAliasOnTableName(String tableName) { // FIXME move this logic to core module
        String realTableName = tableName;
        if (StringUtil.isNotEmpty(tableName)) {
            String[] tableNames = tableName.split(" ");
            if (tableNames != null && tableNames[0] != null) {
                realTableName = tableNames[0].trim();
            }
        }
        return realTableName;
    }
}
