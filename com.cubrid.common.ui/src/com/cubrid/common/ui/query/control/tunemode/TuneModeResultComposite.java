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
package com.cubrid.common.ui.query.control.tunemode;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.schema.control.TextCompareInput;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.queryplan.PLAN_DISPLAY_MODE;
import com.cubrid.common.ui.query.control.queryplan.QueryPlanComposite;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.ArrayList;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.draw2d.GridData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ToolTip;
import org.slf4j.Logger;

/**
 * QueryTunerDialog Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-9 created by Kevin.Wang
 */
public class TuneModeResultComposite extends Composite {
    private static final Logger LOGGER = LogUtil.getLogger(TuneModeResultComposite.class);
    private static final Color SASH_COLOR = ResourceManager.getColor(128, 128, 128);
    private static final int DISPLAY_SQL = 1;
    private static final int DISPLAY_TEXT = 2;
    private static final int DISPLAY_TREE = 4;
    private static final int DISPLAY_GRAPH = 8;

    private TableViewer compareLeftTableViewer;
    private TableViewer compareRightTableViewer;
    private TextMergeViewer textMergeViewer;
    private CompareConfiguration textMergeConfig;
    private QueryPlanComposite leftComparePlanComposite;
    private QueryPlanComposite rightComparePlanComposite;
    private SashForm queryPlanCompareSashForm;
    private ToolItem sqlModeItem;
    private ToolItem textModelItem;
    private ToolItem treeModelItem;
    private ToolItem graphModelItem;
    private TuneModeModel leftRecord;
    private TuneModeModel rightRecord;
    private Button useLeft;
    private Button useRight;
    private ToolTip tooltip;
    private Composite container;
    private DatabaseInfo databaseInfo;

    public TuneModeResultComposite(Composite parent, int style) {
        super(parent, style);
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public void showToolTip(Control control, String title, String message) {
        if (tooltip == null) {
            tooltip = new ToolTip(container.getShell(), SWT.None);
            tooltip.setAutoHide(true);
        }

        Point pt = control.toDisplay(container.getLocation());
        pt.x += 10;
        pt.y -= 5;
        tooltip.setText(title);
        tooltip.setMessage(message);
        tooltip.setLocation(pt);
        tooltip.setVisible(true);
    }

    public void initialize() {
        container = new Composite(this, SWT.None);
        container.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        {
            GridLayout glayout = new GridLayout();
            glayout.marginWidth = 0;
            glayout.marginHeight = 0;
            glayout.marginTop = 0;
            glayout.marginBottom = 0;
            glayout.marginLeft = 0;
            glayout.marginRight = 0;
            container.setLayout(glayout);
        }
        container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        createQueryCompareTab(container);
    }

    public void showResult(TuneModeModel tuneModeModel) {
        setFocus();
        if (useLeft.getSelection()) {
            this.leftRecord = tuneModeModel;
            showToolTip(useLeft, Messages.ttAlertLeftShowTitle, Messages.ttAlertLeftShow);
        } else {
            this.rightRecord = tuneModeModel;
            showToolTip(useRight, Messages.ttAlertRightShowTitle, Messages.ttAlertRightShow);
        }
        displayComparePlan(leftRecord, rightRecord);
    }

    private void createQueryCompareTab(Composite comp) {
        Composite composite = new Composite(comp, SWT.None);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        Composite topComposite = new Composite(composite, SWT.None);
        topComposite.setLayout(new FormLayout());
        topComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 25));

        Composite leftTopComposite = new Composite(topComposite, SWT.None);
        FormData leftTopData = new FormData();
        leftTopData.top = new FormAttachment(0, 0);
        leftTopData.bottom = new FormAttachment(100, 0);
        leftTopData.left = new FormAttachment(0, 0);
        leftTopData.right = new FormAttachment(50, 0);
        leftTopComposite.setLayoutData(leftTopData);
        leftTopComposite.setLayout(new GridLayout(1, false));

        Composite rightTopComposite = new Composite(topComposite, SWT.None);
        FormData rightTopData = new FormData();
        rightTopData.top = new FormAttachment(0, 0);
        rightTopData.bottom = new FormAttachment(100, 0);
        rightTopData.left = new FormAttachment(50, 0);
        rightTopData.right = new FormAttachment(100, 0);
        rightTopComposite.setLayoutData(rightTopData);
        rightTopComposite.setLayout(new GridLayout(4, false));

        useLeft = new Button(leftTopComposite, SWT.CHECK);
        useLeft.setText(Messages.lblTuneModeOrgPlan);
        useLeft.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        useLeft.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if (useLeft.getSelection()) {
                            useRight.setSelection(false);
                        } else {
                            useRight.setSelection(true);
                        }
                    }
                });
        useLeft.setSelection(true);

        useRight = new Button(rightTopComposite, SWT.CHECK);
        useRight.setText(Messages.lblTuneModeNewPlan);
        useRight.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        useRight.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if (useRight.getSelection()) {
                            useLeft.setSelection(false);
                        } else {
                            useLeft.setSelection(true);
                        }
                    }
                });
        useRight.setSelection(false);

        ToolBar leftToolBar = new ToolBar(rightTopComposite, SWT.None);
        leftToolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        Composite folderPlaceComposite = new Composite(rightTopComposite, SWT.None);
        folderPlaceComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        ToolBar rightToolBar = new ToolBar(rightTopComposite, SWT.None);
        rightToolBar.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

        sqlModeItem = new ToolItem(rightToolBar, SWT.CHECK);
        sqlModeItem.setImage(CommonUIPlugin.getImage("icons/queryplan/sql_compare.gif"));
        sqlModeItem.setToolTipText(Messages.itemTooltipQuery);
        // sqlModeItem.setText(Messages.lblTuneCompareQueryBtn);
        sqlModeItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_SQL);
                    }
                });

        textModelItem = new ToolItem(rightToolBar, SWT.CHECK);
        textModelItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_raw.png"));
        textModelItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);
        // textModelItem.setText(Messages.lblPlanRawBtn);
        textModelItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_TEXT);
                    }
                });

        treeModelItem = new ToolItem(rightToolBar, SWT.CHECK);
        treeModelItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_tree.png"));
        treeModelItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);
        // treeModelItem.setText(Messages.lblPlanTreeBtn);
        treeModelItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_TREE);
                    }
                });

        graphModelItem = new ToolItem(rightToolBar, SWT.CHECK);
        graphModelItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_graph.png"));
        graphModelItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);
        // graphModelItem.setText(Messages.lblPlanGraph);
        graphModelItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_GRAPH);
                    }
                });

        Composite tablesComposite = new Composite(composite, SWT.None);
        tablesComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 60));
        tablesComposite.setLayout(new FormLayout());

        /*Left composite*/
        Composite leftTableComposite = new Composite(tablesComposite, SWT.BORDER);
        leftTableComposite.setLayout(new GridLayout());
        FormData leftTableData = new FormData();
        leftTableData.top = new FormAttachment(0, 0);
        leftTableData.bottom = new FormAttachment(100, 0);
        leftTableData.left = new FormAttachment(0, 0);
        leftTableData.right = new FormAttachment(50, -2);
        leftTableComposite.setLayoutData(leftTableData);

        /*Right composite*/
        Composite rightTableComposite = new Composite(tablesComposite, SWT.BORDER);
        rightTableComposite.setLayout(new GridLayout());
        FormData rightTabelData = new FormData();
        rightTabelData.top = new FormAttachment(0, 0);
        rightTabelData.bottom = new FormAttachment(100, 0);
        rightTabelData.left = new FormAttachment(50, 2);
        rightTabelData.right = new FormAttachment(100, 0);
        rightTableComposite.setLayoutData(rightTabelData);

        compareLeftTableViewer = new TableViewer(leftTableComposite, SWT.FULL_SELECTION);
        compareLeftTableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        compareLeftTableViewer.getTable().setHeaderVisible(true);
        compareLeftTableViewer.getTable().setLinesVisible(false);

        compareLeftTableViewer.setContentProvider(new TuneModeResultContentProvider());
        compareLeftTableViewer.setLabelProvider(
                new TuneModeResultLabelProvider(compareLeftTableViewer, false));

        TableColumn fetchColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        fetchColumnLeft.setText(Messages.columnFetches);
        fetchColumnLeft.setWidth(60);

        TableColumn dirtyColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        dirtyColumnLeft.setText(Messages.columnDirties);
        dirtyColumnLeft.setWidth(60);

        TableColumn ioReadColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        ioReadColumnLeft.setText(Messages.columnIORead);
        ioReadColumnLeft.setWidth(80);

        TableColumn ioWriteColumnLeft =
                new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        ioWriteColumnLeft.setText(Messages.columnIOWrite);
        ioWriteColumnLeft.setWidth(80);

        TableColumn costColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        costColumnLeft.setText(Messages.columnCost);
        costColumnLeft.setWidth(60);

        compareRightTableViewer = new TableViewer(rightTableComposite, SWT.FULL_SELECTION);
        compareRightTableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        compareRightTableViewer.getTable().setHeaderVisible(true);
        compareRightTableViewer.getTable().setLinesVisible(false);

        compareRightTableViewer.setContentProvider(new TuneModeResultContentProvider());
        compareRightTableViewer.setLabelProvider(
                new TuneModeResultLabelProvider(compareRightTableViewer, false));

        TableColumn fetchColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        fetchColumnRight.setText(Messages.columnFetches);
        fetchColumnRight.setWidth(60);

        TableColumn dirtyColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        dirtyColumnRight.setText(Messages.columnDirties);
        dirtyColumnRight.setWidth(60);

        TableColumn ioReadColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        ioReadColumnRight.setText(Messages.columnIORead);
        ioReadColumnRight.setWidth(80);

        TableColumn ioWriteColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        ioWriteColumnRight.setText(Messages.columnIOWrite);
        ioWriteColumnRight.setWidth(80);

        TableColumn costColumnRight = new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        costColumnRight.setText(Messages.columnCost);
        costColumnRight.setWidth(60);

        queryPlanCompareSashForm = new SashForm(composite, SWT.VERTICAL);
        queryPlanCompareSashForm.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        queryPlanCompareSashForm.setBackground(SASH_COLOR);
        queryPlanCompareSashForm.setLayout(new GridLayout());
        queryPlanCompareSashForm.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        queryPlanCompareSashForm.setSashWidth(0);

        Composite textMergeComposite = new Composite(queryPlanCompareSashForm, SWT.None);
        textMergeComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        textMergeComposite.setLayout(new FillLayout());

        textMergeConfig = new CompareConfiguration();
        textMergeConfig.setProperty(CompareConfiguration.SHOW_PSEUDO_CONFLICTS, Boolean.FALSE);
        textMergeConfig.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.TRUE);
        textMergeConfig.setLeftEditable(false);
        textMergeConfig.setRightEditable(false);
        textMergeViewer = new TextMergeViewer(textMergeComposite, SWT.BORDER, textMergeConfig);
        DiffNode queryDiffNode =
                new DiffNode(
                        null,
                        Differencer.CHANGE,
                        null,
                        new TextCompareInput(""),
                        new TextCompareInput(""));
        textMergeViewer.setInput(queryDiffNode);

        Composite compareQueryPlanComposite = new Composite(queryPlanCompareSashForm, SWT.None);
        compareQueryPlanComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        compareQueryPlanComposite.setLayout(new FormLayout());

        leftComparePlanComposite =
                new QueryPlanComposite(compareQueryPlanComposite, SWT.BORDER, null, databaseInfo);
        FormData leftData = new FormData();
        leftData.top = new FormAttachment(0, 0);
        leftData.bottom = new FormAttachment(100, 0);
        leftData.left = new FormAttachment(0, 0);
        leftData.right = new FormAttachment(50, 0);
        leftComparePlanComposite.setLayoutData(leftData);

        rightComparePlanComposite =
                new QueryPlanComposite(compareQueryPlanComposite, SWT.BORDER, null, databaseInfo);
        FormData rightData = new FormData();
        rightData.top = new FormAttachment(0, 0);
        rightData.bottom = new FormAttachment(100, 0);
        rightData.left = new FormAttachment(50, 0);
        rightData.right = new FormAttachment(100, 0);
        rightComparePlanComposite.setLayoutData(rightData);

        queryPlanCompareSashForm.setWeights(new int[] {100, 0});
        updateCompareDisplayModel(DISPLAY_TREE);
    }

    /**
     * Update compare display model
     *
     * @param model
     */
    private void updateCompareDisplayModel(int model) {
        if (model == DISPLAY_TEXT) {
            // compareDisplayModel = DISPLAY_TEXT;
            queryPlanCompareSashForm.setWeights(new int[] {0, 100});

            leftComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());
            rightComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());

            sqlModeItem.setSelection(false);
            textModelItem.setSelection(true);
            treeModelItem.setSelection(false);
            graphModelItem.setSelection(false);
        }

        if (model == DISPLAY_TREE) {
            // compareDisplayModel = DISPLAY_TREE;
            queryPlanCompareSashForm.setWeights(new int[] {0, 100});

            leftComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());
            rightComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());

            sqlModeItem.setSelection(false);
            textModelItem.setSelection(false);
            treeModelItem.setSelection(true);
            graphModelItem.setSelection(false);
        }

        if (model == DISPLAY_GRAPH) {
            // compareDisplayModel = DISPLAY_GRAPH;
            queryPlanCompareSashForm.setWeights(new int[] {0, 100});

            leftComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());
            rightComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());

            sqlModeItem.setSelection(false);
            textModelItem.setSelection(false);
            treeModelItem.setSelection(false);
            graphModelItem.setSelection(true);
        }

        if (model == DISPLAY_SQL) {
            // compareDisplayModel = DISPLAY_SQL;
            queryPlanCompareSashForm.setWeights(new int[] {100, 0});

            sqlModeItem.setSelection(true);
            textModelItem.setSelection(false);
            treeModelItem.setSelection(false);
            graphModelItem.setSelection(false);
        }
    }

    /**
     * Display compare query plan
     *
     * @param leftModel
     * @param rightModel
     */
    private void displayComparePlan(TuneModeModel leftModel, TuneModeModel rightModel) {
        String leftQuery = "";
        String rightQuery = "";

        textMergeConfig.setLeftLabel(Messages.lblTuneModeOrgSql);
        if (leftModel != null) {
            leftQuery = leftModel.getQuery();
            textMergeConfig.setLeftLabel(Messages.lblTuneModeOrgSql);
            compareLeftTableViewer.setInput(leftModel);
            compareLeftTableViewer.refresh();
            leftComparePlanComposite.setQueryRecord(leftModel.getQueryPlan());
        } else {
            compareLeftTableViewer.setInput(new ArrayList<TuneModeModel>());
            compareLeftTableViewer.refresh();
            leftComparePlanComposite.setQueryRecord(null);
        }

        textMergeConfig.setRightLabel(Messages.lblTuneModeNewSql);
        if (rightModel != null) {
            rightQuery = rightModel.getQuery();
            compareRightTableViewer.setInput(rightModel);
            compareRightTableViewer.refresh();
            rightComparePlanComposite.setQueryRecord(rightModel.getQueryPlan());
        } else {
            compareRightTableViewer.setInput(new ArrayList<TuneModeModel>());
            compareRightTableViewer.refresh();
            rightComparePlanComposite.setQueryRecord(null);
        }

        DiffNode queryDiffNode =
                new DiffNode(
                        null,
                        Differencer.CHANGE,
                        null,
                        new TextCompareInput(leftQuery),
                        new TextCompareInput(rightQuery));
        textMergeViewer.setInput(queryDiffNode);
    }

    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.MODELESS | SWT.MAX | SWT.MIN | SWT.RESIZE;
    }

    protected Control createButtonBar(Composite parent) {
        return parent;
    }
}
