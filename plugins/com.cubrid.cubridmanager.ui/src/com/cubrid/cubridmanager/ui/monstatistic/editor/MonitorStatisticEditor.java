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
package com.cubrid.cubridmanager.ui.monstatistic.editor;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticData;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.dialog.EditMultiHostStatisticItemDialog;
import com.cubrid.cubridmanager.ui.monstatistic.dialog.EditSingleHostStatisticItemDialog;
import com.cubrid.cubridmanager.ui.monstatistic.editor.internal.MonitorStatisticChart;
import com.cubrid.cubridmanager.ui.monstatistic.editor.internal.WhiteChart;
import com.cubrid.cubridmanager.ui.monstatistic.progress.LoadMonitorStatisticDataProgress;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorStatisticPersistManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * A editor part is used for Monitor Statistic Page.
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-7-3 created by Santiago Wang
 */
public class MonitorStatisticEditor extends CubridEditorPart implements ITaskExecutorInterceptor {

    private static final Logger LOGGER = LogUtil.getLogger(MonitorStatisticEditor.class);
    public static final String ID = MonitorStatisticEditor.class.getName();

    private final MonitorStatisticPersistManager monitorStatisticPersistManager =
            MonitorStatisticPersistManager.getInstance();
    private final String MESSAGE_EDIT_MODE = Messages.btnBackToViewMode;
    private final String MESSAGE_View_MODE = Messages.btnGoToEditMode;
    private final String MESSAGE_SELECT_ALL = Messages.btnSelectAll;
    private final String MESSAGE_DESELECT_ALL = Messages.btnDeselectAll;
    private final Color COLOR_CHART_AREA_BACKGROUND = ResourceManager.getColor(255, 255, 255);
    private MonitorStatistic pageNode;
    private ServerInfo serverInfo;
    private List<StatisticChartItem> statisticItemList;
    private Map<StatisticChartItem, List<StatisticData>> statisticDataMap;
    // TODO: should remove reference before object dispose
    private List<Composite> compList = new ArrayList<Composite>();
    private List<MonitorStatisticChart> chartList = new ArrayList<MonitorStatisticChart>();
    private boolean isMultiHost = false;
    private boolean isEditMode = false;
    private int addItemCompIndex;
    private ScrolledComposite scrolledComp;
    private Composite chartAreaRootComp;
    private Composite toolbarComp;
    private ToolItem changeModeItem;
    private Button selectBtn;
    private ToolBar toolbar;

    //	private Label refreshTimeLabel;
    private final int SIZE_WIDTH = 200;
    private final int SIZE_HEIGHT = 300;
    private final int CHART_PER_LINE = 3;
    private final int CHART_AREA_WIDTH = 1200;

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        this.pageNode = (MonitorStatistic) input;
        this.setPartName(this.pageNode.getId());
        this.isMultiHost = this.pageNode.isMultiHost();
        if (!this.isMultiHost) {
            this.serverInfo = this.pageNode.getServer().getServerInfo();
        }
        this.statisticItemList = this.pageNode.getStatisticItemList();
    }

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        createToolBar(parent);

        scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scrolledComp.setLayout(new FillLayout());
        scrolledComp.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        scrolledComp.getVerticalBar().setIncrement(10);
        scrolledComp.addListener(
                SWT.Activate,
                new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        scrolledComp.setFocus();
                    }
                });

        chartAreaRootComp = new Composite(scrolledComp, SWT.NONE);
        {
            final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            chartAreaRootComp.setLayoutData(gd);
            GridLayout layout = new GridLayout(3, false);
            chartAreaRootComp.setLayout(layout);
            chartAreaRootComp.setBackground(COLOR_CHART_AREA_BACKGROUND);
        }

        statisticDataMap = refreshData(statisticItemList);
        createDataChart(chartAreaRootComp);
        createAddChart(chartAreaRootComp);
        createHiddenItems();

        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);
        scrolledComp.setContent(chartAreaRootComp);
        scrolledComp.setMinWidth(CHART_AREA_WIDTH);
        updateScrolledCompositeHeight();
    }

    private void createToolBar(Composite parent) {
        toolbarComp = new Composite(parent, SWT.NONE);
        {
            final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            toolbarComp.setLayoutData(gd);
            GridLayout layout = new GridLayout(2, false);
            toolbarComp.setLayout(layout);
        }

        toolbar = new ToolBar(toolbarComp, SWT.RIGHT);
        toolbar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        changeModeItem = new ToolItem(toolbar, SWT.PUSH);
        changeModeItem.setText(MESSAGE_View_MODE);
        changeModeItem.setImage(
                CubridManagerUIPlugin.getImage("icons/action/auto_backup_edit.png"));
        changeModeItem.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        isEditMode = !isEditMode;
                        updateToolbarAndCharts(isEditMode);
                        updateScrolledCompositeHeight();
                        scrolledComp.layout();
                    }
                });

        // initial
        refreshToolbar(isEditMode);
    }

    private void updateToolbarAndCharts(boolean isEditMode) {
        refreshToolbar(isEditMode);
        for (int i = 0; i < addItemCompIndex; i++) {
            chartList.get(i).updateToolbar(isEditMode);
        }
        Composite compAddItem = compList.get(addItemCompIndex);
        compAddItem.setVisible(isEditMode);
    }

    private void refreshToolbar(boolean isEditMode) {
        if (isEditMode) {
            changeModeItem.setText(MESSAGE_EDIT_MODE);
            changeModeItem.setImage(
                    CubridManagerUIPlugin.getImage("icons/navigator/status_item.png"));
        } else {
            changeModeItem.setText(MESSAGE_View_MODE);
            changeModeItem.setImage(
                    CubridManagerUIPlugin.getImage("icons/action/auto_backup_edit.png"));
        }

        ToolItem[] items = toolbar.getItems();
        for (int i = 1; i < items.length; i++) {
            items[i].dispose();
        }
        if (isEditMode) {
            ToolItem addItem = new ToolItem(toolbar, SWT.PUSH);
            addItem.setText(Messages.btnAdd);
            addItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_add.png"));
            addItem.addSelectionListener(
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(final SelectionEvent event) {
                            openEditStatisticItemDialog();
                        }
                    });

            ToolItem deleteItem = new ToolItem(toolbar, SWT.PUSH);
            deleteItem.setText(Messages.btnDelete);
            deleteItem.setImage(
                    CubridManagerUIPlugin.getImage("icons/action/auto_backup_delete.png"));
            deleteItem.addSelectionListener(
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(final SelectionEvent event) {
                            if (!CommonUITool.openConfirmBox(
                                    Messages.confirmMultiStatisticChartRemoveWarn)) {
                                return;
                            }
                            List<StatisticChartItem> chartItemList =
                                    new ArrayList<StatisticChartItem>();
                            for (MonitorStatisticChart chart : chartList) {
                                if (chart.getSelection()) {
                                    chartItemList.add(chart.getStatisticChartItem());
                                }
                            }
                            removeStatisticItem(chartItemList);
                        }
                    });

            ToolItem refreshItem = new ToolItem(toolbar, SWT.PUSH);
            refreshItem.setText(Messages.btnRefresh);
            refreshItem.setToolTipText(Messages.RefreshTooltip);
            refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
            refreshItem.addSelectionListener(
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(final SelectionEvent event) {
                            statisticDataMap = refreshData(statisticItemList);
                            for (MonitorStatisticChart chart : chartList) {
                                chart.refreshChart(
                                        statisticDataMap.get(chart.getStatisticChartItem()));
                            }
                        }
                    });

            selectBtn = new Button(toolbarComp, SWT.CHECK);
            final GridData gdSelectBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false);
            selectBtn.setLayoutData(gdSelectBtn);
            selectBtn.setText(MESSAGE_SELECT_ALL);
            selectBtn.addSelectionListener(
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(final SelectionEvent event) {
                            Button btn = (Button) event.widget;
                            for (MonitorStatisticChart item : chartList) {
                                item.setSelection(btn.getSelection());
                            }
                            if (btn.getSelection()) {
                                btn.setText(MESSAGE_DESELECT_ALL);
                            } else {
                                btn.setText(MESSAGE_SELECT_ALL);
                            }
                            btn.getParent().layout();
                        }
                    });
        } else {
            ToolItem refreshItem = new ToolItem(toolbar, SWT.PUSH);
            refreshItem.setText(Messages.btnRefresh);
            refreshItem.setToolTipText(Messages.RefreshTooltip);
            refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
            refreshItem.addSelectionListener(
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(final SelectionEvent event) {
                            statisticDataMap = refreshData(statisticItemList);
                            for (MonitorStatisticChart chart : chartList) {
                                chart.refreshChart(
                                        statisticDataMap.get(chart.getStatisticChartItem()));
                            }
                        }
                    });

            if (selectBtn != null) {
                selectBtn.dispose();
            }
        }

        toolbarComp.layout();
    }

    private void createDataChart(Composite parent) {
        if (statisticItemList == null) {
            return;
        }

        for (StatisticChartItem item : statisticItemList) {
            final Composite comp = new Composite(parent, SWT.NONE);
            {
                comp.setLayout(new GridLayout(1, false));
                GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
                gridData.widthHint = SIZE_WIDTH;
                gridData.heightHint = SIZE_HEIGHT;
                comp.setLayoutData(gridData);
                comp.setBackground(COLOR_CHART_AREA_BACKGROUND);
            }
            if (item == null) {
                // TODO: deal with no chart item
                continue;
            }
            MonitorStatisticChart chart = new MonitorStatisticChart(comp, false, isMultiHost);
            chart.setEditor(this);
            chart.setStatisticChartItem(item);
            chart.setGroupName(item.getName());
            chart.setStatisticDataList(statisticDataMap.get(item));
            chart.setEditMode(false);
            chart.loadChart();

            chartList.add(chart);
            compList.add(comp);
        }
    }

    private void createAddChart(Composite parent) {
        Composite addItemComp = new Composite(parent, SWT.NONE);
        addItemComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        {
            addItemComp.setLayout(new GridLayout(1, false));
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.widthHint = SIZE_WIDTH;
            gridData.heightHint = SIZE_HEIGHT;
            addItemComp.setLayoutData(gridData);
            addItemComp.setBackground(COLOR_CHART_AREA_BACKGROUND);
        }
        addItemCompIndex = compList.size();

        WhiteChart addItemChart = new WhiteChart(addItemComp, this);
        addItemChart.setAddItemChart(true);
        addItemChart.loadChart();
        addItemComp.setVisible(false);
        compList.add(addItemComp);
    }

    /**
     * Check whether has enough items to keep this editor display correctly, if not, create hidden
     * items to fill editor.
     *
     * @param parent
     */
    private void createHiddenItems() {
        if (compList.size() >= CHART_PER_LINE) {
            return;
        }
        int count = CHART_PER_LINE - compList.size();
        for (int i = 0; i < count; i++) {
            Composite comp = new Composite(chartAreaRootComp, SWT.RESIZE);
            {
                comp.setLayout(new GridLayout(1, false));
                GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
                gridData.widthHint = SIZE_WIDTH;
                gridData.heightHint = SIZE_HEIGHT;
                comp.setLayoutData(gridData);
                comp.setBackground(COLOR_CHART_AREA_BACKGROUND);
            }
            WhiteChart whiteChart = new WhiteChart(comp, this);
            whiteChart.loadChart();
            for (Control ctrl : comp.getChildren()) {
                ctrl.setVisible(false);
            }
            compList.add(comp);
        }
    }

    private Map<StatisticChartItem, List<StatisticData>> refreshData(StatisticChartItem item) {
        if (item == null) {
            return null;
        }
        List<StatisticChartItem> itemList = new ArrayList<StatisticChartItem>();
        itemList.add(item);
        return refreshData(itemList);
    }

    private Map<StatisticChartItem, List<StatisticData>> refreshData(
            List<StatisticChartItem> itemList) {
        if (itemList == null) {
            return null;
        }
        LoadMonitorStatisticDataProgress progress;
        if (isMultiHost) {
            progress = new LoadMonitorStatisticDataProgress();
        } else {
            progress = new LoadMonitorStatisticDataProgress(serverInfo);
        }
        progress.setStatisticItemList(itemList);
        progress.loadMonitorStatisticData();
        /*[TOOLS-3742] when invalid token or connect server failure, give out error message*/
        if (!isMultiHost && progress.getErrorMsg() != null) {
            CommonUITool.openErrorBox(progress.getErrorMsg());
        }
        Map<StatisticChartItem, List<StatisticData>> dataMap = progress.getStatisticDataMap();

        return dataMap;
    }

    private void addNewDataChart(StatisticChartItem item) {
        if (item == null) {
            return;
        }
        statisticItemList.add(item);
        Composite compNewChart;
        Composite compAddItem = compList.get(addItemCompIndex);
        if (addItemCompIndex < CHART_PER_LINE - 1) {
            compNewChart = compList.get(addItemCompIndex + 1);
            for (Control ctrl : compNewChart.getChildren()) {
                ctrl.dispose();
            }
        } else {
            compNewChart = new Composite(chartAreaRootComp, SWT.NONE);
            compList.add(compNewChart);
        }
        // initial layout data of composite
        {
            compNewChart.setLayout(new GridLayout(1, false));
            GridData gdCompNewChart = new GridData(GridData.FILL_HORIZONTAL);
            gdCompNewChart.widthHint = SIZE_WIDTH;
            gdCompNewChart.heightHint = SIZE_HEIGHT;
            compNewChart.setLayoutData(gdCompNewChart);
            compNewChart.setBackground(COLOR_CHART_AREA_BACKGROUND);
        }
        // change the position of add item
        compList.set(addItemCompIndex, compNewChart);
        compList.set(addItemCompIndex + 1, compAddItem);
        compNewChart.moveAbove(compAddItem);
        addItemCompIndex++;

        // draw the chart
        Map<StatisticChartItem, List<StatisticData>> dataMap = refreshData(item);

        MonitorStatisticChart chart = new MonitorStatisticChart(compNewChart, false, isMultiHost);
        chart.setEditor(this);
        chart.setGroupName(item.getName());
        chart.setStatisticDataList(dataMap.get(item));
        chart.setEditMode(true);
        chart.loadChart();
        chart.setStatisticChartItem(item);
        chartList.add(chart);

        // update the scroll bar size
        updateScrolledCompositeHeight();

        compAddItem.layout();
        compNewChart.layout();
        chartAreaRootComp.layout();
        scrolledComp.layout();
    }

    private void updateScrolledCompositeHeight() {
        final int compSize = compList.size();
        boolean isMoreThan2Row = false;
        if (isEditMode) {
            isMoreThan2Row = compSize < (2 * CHART_PER_LINE + 1);
        } else {
            isMoreThan2Row = (compSize - 1) < (2 * CHART_PER_LINE + 1);
        }

        int scrolledCompHeight;
        if (isMoreThan2Row) {
            scrolledCompHeight = 2 * SIZE_HEIGHT + 50;
        } else {
            scrolledCompHeight =
                    (compSize / CHART_PER_LINE + (compSize % CHART_PER_LINE == 0 ? 0 : 1))
                                    * SIZE_HEIGHT
                            + 50;
        }
        scrolledComp.setMinHeight(scrolledCompHeight);
    }

    class BtnAddSeletionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            openEditStatisticItemDialog();
        }
    }

    public void addStatisticItem(StatisticChartItem item) {
        if (item == null) {
            return;
        }

        item.setSeries(statisticItemList.size());
        statisticItemList.add(item);
        monitorStatisticPersistManager.saveStatistic();
    }

    public void updateStatisticItem(StatisticChartItem item) {
        if (item == null || item.getSeries() == -1) {
            return;
        }

        StatisticChartItem oldItem = statisticItemList.get(item.getSeries());
        MonitorStatisticChart chart = chartList.get(oldItem.getSeries());

        statisticDataMap.remove(oldItem);
        chartList.remove(oldItem);
        statisticItemList.set(item.getSeries(), item);
        chartList.set(oldItem.getSeries(), chart);
        Map<StatisticChartItem, List<StatisticData>> dataMap = refreshData(item);
        chart.refreshChart(dataMap.get(item));
        chart.updateGroupName(item.getName());
        statisticDataMap.put(item, dataMap.get(item));

        monitorStatisticPersistManager.saveStatistic();
    }

    public void removeStatisticItem(StatisticChartItem item) {
        if (item == null) {
            return;
        }
        statisticItemList.remove(item);
        statisticDataMap.remove(item);
        chartList.remove(item.getSeries());
        Composite comp = compList.get(item.getSeries());
        compList.remove(item.getSeries());
        comp.dispose();

        for (int i = 0; i < statisticItemList.size(); i++) {
            StatisticChartItem chartItem = statisticItemList.get(i);
            chartItem.setSeries(i);
        }

        createHiddenItems();
        addItemCompIndex--;
        chartAreaRootComp.layout();

        monitorStatisticPersistManager.saveStatistic();
        updateScrolledCompositeHeight();
        scrolledComp.layout();
    }

    public void removeStatisticItem(List<StatisticChartItem> chartItemList) {
        if (chartItemList == null) {
            return;
        }
        List<Integer> indexList = new ArrayList<Integer>();
        for (StatisticChartItem chartItem : chartItemList) {
            indexList.add(statisticItemList.indexOf(chartItem));
        }
        for (StatisticChartItem chartItem : chartItemList) {
            statisticItemList.remove(chartItem);
            statisticDataMap.remove(chartItem);
            addItemCompIndex--;
        }
        Collections.sort(indexList);
        for (int i = indexList.size() - 1; i >= 0; i--) {
            int index = indexList.get(i);
            chartList.remove(index);
            Composite comp = compList.get(index);
            compList.remove(index);
            comp.dispose();
        }
        for (int i = 0; i < statisticItemList.size(); i++) {
            StatisticChartItem chartItem = statisticItemList.get(i);
            chartItem.setSeries(i);
        }
        createHiddenItems();
        chartAreaRootComp.layout();

        monitorStatisticPersistManager.saveStatistic();
        updateScrolledCompositeHeight();
        scrolledComp.layout();
    }

    public void openDetailView(
            StatisticChartItem statisticChartItem, List<StatisticData> statisticDataList) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return;
        }
        StringBuilder secondaryIdSb = new StringBuilder(MonitorStatisticDetailViewPart.ID);
        secondaryIdSb.append("@").append(this.pageNode.getId());
        IViewReference viewReference =
                page.findViewReference(MonitorStatisticDetailViewPart.ID, secondaryIdSb.toString());
        if (viewReference == null) {
            try {
                MonitorStatisticDetailViewPart viewPart =
                        (MonitorStatisticDetailViewPart)
                                page.showView(
                                        MonitorStatisticDetailViewPart.ID,
                                        secondaryIdSb.toString(),
                                        IWorkbenchPage.VIEW_ACTIVATE);
                viewPart.setMultiHost(isMultiHost);
                viewPart.init(this.pageNode, this);
                viewPart.setStatisticChartItem(statisticChartItem);
                viewPart.setStatisticDataList(statisticDataList);
                viewPart.reloadChart();
            } catch (PartInitException ex) {
                viewReference = null;
            }
        } else {
            MonitorStatisticDetailViewPart viewPart =
                    (MonitorStatisticDetailViewPart) viewReference.getView(false);
            window.getActivePage().bringToTop(viewPart);
            viewPart.setStatisticChartItem(statisticChartItem);
            viewPart.setStatisticDataList(statisticDataList);
            viewPart.reloadChart();
        }
    }

    public void openEditStatisticItemDialog() {
        if (isMultiHost) {
            EditMultiHostStatisticItemDialog dialog =
                    new EditMultiHostStatisticItemDialog(getSite().getShell(), this);
            if (dialog.open() == Dialog.OK) {
                StatisticChartItem item = dialog.getStatisticChartItem();
                item.setSeries(statisticItemList.size());
                addNewDataChart(item);
                monitorStatisticPersistManager.saveStatistic();
            }
        } else {
            EditSingleHostStatisticItemDialog dialog =
                    new EditSingleHostStatisticItemDialog(getSite().getShell(), this, serverInfo);
            if (dialog.open() == Dialog.OK) {
                StatisticChartItem item = dialog.getStatisticChartItem();
                item.setSeries(statisticItemList.size());
                addNewDataChart(item);
                monitorStatisticPersistManager.saveStatistic();
            }
        }
    }

    public void refreshChartBySeries(int series) {
        StatisticChartItem chartItem = statisticItemList.get(series);
        MonitorStatisticChart chart = chartList.get(series);
        if (chartItem == null || chart == null) {
            return;
        }

        Map<StatisticChartItem, List<StatisticData>> dataMap = refreshData(chartItem);
        chart.refreshChart(dataMap.get(chartItem));
        statisticDataMap.put(chartItem, dataMap.get(chartItem));
    }

    public String getNodeId() {
        return pageNode.getId();
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        noOp();
    }

    public IStatus postTaskFinished(ITask task) {
        return null;
    }

    public void completeAll() {
        noOp();
    }

    public void doSave(IProgressMonitor monitor) {
        noOp();
    }

    public void doSaveAs() {
        noOp();
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
