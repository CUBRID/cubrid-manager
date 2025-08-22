/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import static com.cubrid.common.ui.spi.util.CommonUITool.getColorElem;
import static com.cubrid.common.ui.spi.util.CommonUITool.trimPaintColor;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartSettingDlg;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartShowingProp;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.CombinedBarTimeSeriesChart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.MonitorType;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSetting;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSettingMatching;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.slf4j.Logger;

/**
 * A editor part is used to view host dashboard history monitor.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-7-27 created by lizhiqiang
 */
public class HostDashboardHistoryViewPart extends ViewPart {

    private static final Logger LOGGER = LogUtil.getLogger(HostDashboardHistoryViewPart.class);

    public static final String ID = HostDashboardHistoryViewPart.class.getName();

    private CombinedBarTimeSeriesChart cpuChart;

    private CombinedBarTimeSeriesChart iowaitChart;

    private CombinedBarTimeSeriesChart memoryChart;

    private ChartCompositePart brokerChartPart;

    private TreeMap<String, String> cpuValueMap;

    private TreeMap<String, String> memoryValueMap;

    private TreeMap<String, String> iowaitValueMap;

    private HistoryFileHelp historyFileHelp;

    private Composite composite;

    private String historyPath;
    private String historyFileName;
    private boolean isChangedHistoryPath;

    private Composite brokerComp;

    private Composite chartComp;

    private boolean isNewBrokerDiag;

    /**
     * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite)
     * @param site The IViewSite
     * @exception PartInitException if this view was not initialized successfully
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        cpuValueMap = new TreeMap<String, String>();
        iowaitValueMap = new TreeMap<String, String>();
        memoryValueMap = new TreeMap<String, String>();
        setCpuSeriesKey(new String[] {HostStatEnum.USER.name(), HostStatEnum.KERNEL.name()});
        setIowaitSeriesKey(new String[] {HostStatEnum.IOWAIT.name()});
        setMemorySeriesKey(new String[] {HostStatEnum.MEMPHY_USED.name()});
    }

    /**
     * Initializes
     *
     * @param hostNode The HostNode
     */
    public void init(HostNode hostNode) {
        String hostAddress = hostNode.getIp();
        String monPort = hostNode.getPort();
        historyFileName =
                HistoryComposite.HOSTDASHBOARD_HISTORY_FILE_PREFIX
                        + hostAddress
                        + "_"
                        + monPort
                        + HistoryComposite.HISTORY_SUFFIX;

        IPath path = CubridManagerCorePlugin.getDefault().getStateLocation();
        historyPath = path.toOSString() + File.separator + historyFileName;
        historyFileHelp = new HistoryFileHelp();
        historyFileHelp.setHistoryPath(historyPath);
        brokerChartPart.addChartMouseListener();
        ServerInfo serverInfo = hostNode.getServerInfo();
        if (serverInfo != null) {
            isNewBrokerDiag = CompatibleUtil.isNewBrokerDiag(serverInfo);
        }
    }

    /**
     * Creates the SWT controls for this workbench part.
     *
     * @param parent the parent control
     * @see IWorkbenchPart
     */
    public void createPartControl(Composite parent) {
        final ScrolledComposite scrolledComp =
                new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        composite = new Composite(scrolledComp, SWT.RESIZE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final HistoryComposite historyComposite = new HistoryComposite();

        historyComposite.loadTimeSelection(composite);

        Label sepWithResult = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT);
        sepWithResult.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        chartComp = new Composite(composite, SWT.RESIZE);

        GridLayout chartCompLayout = new GridLayout(1, true);
        chartComp.setLayout(chartCompLayout);
        chartComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        loadCpuChart(chartComp);
        loadMemoryChart(chartComp);
        loadIowaitChart(chartComp);
        loadBrokerChart(chartComp);

        scrolledComp.setContent(composite);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);
        scrolledComp.setMinHeight(800);
        scrolledComp.setMinWidth(350);
        historyComposite
                .getQueryBtn()
                .addSelectionListener(new HistoryBtnSelectionListener(historyComposite));

        makeActions();
    }

    /**
     * Load an instance of CombinedBarTimeSeriesChart stand for CPU
     *
     * @param parent an instance of Composite
     */
    private void loadCpuChart(Composite parent) {
        cpuChart = new CombinedBarTimeSeriesChart();
        cpuChart.setHasBarChart(false);
        cpuChart.setShowSeriesAxis(true);
        cpuChart.setAreaRender(true);
        cpuChart.setValueMap(cpuValueMap);
        cpuChart.setSeriesGroupName(Messages.hostHistoryCpuChartGrpName);
        cpuChart.load(parent);
    }

    /**
     * * Load an instance of CombinedBarTimeSeriesChart stand for IO wait
     *
     * @param parent an instance of Composite
     */
    private void loadIowaitChart(Composite parent) {
        iowaitChart = new CombinedBarTimeSeriesChart();
        iowaitChart.setHasBarChart(false);
        iowaitChart.setShowSeriesAxis(true);
        iowaitChart.setValueMap(iowaitValueMap);
        iowaitChart.setSeriesGroupName(Messages.hostHistoryIowaitChartGrpName);
        iowaitChart.load(parent);
    }

    /**
     * Load an instance of CombinedBarTimeSeriesChart stand for physical
     *
     * @param parent an instance of Composite
     */
    private void loadMemoryChart(Composite parent) {
        memoryChart = new CombinedBarTimeSeriesChart();
        memoryChart.setHasBarChart(false);
        memoryChart.setShowSeriesAxis(true);
        memoryChart.setValueMap(memoryValueMap);
        memoryChart.setSeriesGroupName(Messages.hostHistoryPhysicalChartGrpName);
        memoryChart.load(parent);
    }

    /**
     * Load an instance of ChartCompositePart stand for broker monitor info
     *
     * @param parent the instance of Composite
     */
    private void loadBrokerChart(Composite parent) {
        brokerComp = new Composite(parent, SWT.NULL);
        brokerComp.setLayout(new GridLayout());
        brokerComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        Group brokerGrp = new Group(brokerComp, SWT.NONE);
        brokerGrp.setText(Messages.hostBrokerSeriesGroupName);
        GridLayout layoutGrp = new GridLayout();
        layoutGrp.verticalSpacing = 0;
        layoutGrp.horizontalSpacing = 0;
        layoutGrp.marginLeft = 0;
        layoutGrp.marginRight = 0;
        layoutGrp.marginTop = 0;
        layoutGrp.marginBottom = 0;
        brokerGrp.setLayout(layoutGrp);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        brokerGrp.setLayoutData(gridData);

        BrokerDiagData brokerDiagData = new BrokerDiagData();
        TreeMap<String, String> map = convertMapKey(brokerDiagData.getDiagStatusResultMap());
        brokerChartPart = new ChartCompositePart(brokerGrp, map);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            ShowSetting showSetting = brokerChartPart.getSettingMap().get(key);
            ShowSettingMatching.match(key, showSetting, MonitorType.BROKER);
        }
        brokerChartPart.loadContent();
        JFreeChart chart = (JFreeChart) brokerChartPart.getChart();
        chart.setBorderVisible(false);

        XYPlot xyplot = (XYPlot) brokerChartPart.getChart().getPlot();
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setFixedAutoRange(300000d);
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        renderer.setURLGenerator(null);
        renderer.setBaseToolTipGenerator(null);
    }

    /**
     * send when CUBRID node object
     *
     * @see com.cubrid.common.ui.spi.event.ICubridNodeChangedListener#nodeChanged
     *     (com.cubrid.common.ui.spi.event.CubridNodeChangedEvent)
     * @param event the CubridNodeChangedEvent object
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        // Do nothing
    }

    /** This method is to create actions at tool bar */
    private void makeActions() {
        Action settingAction =
                new Action() {
                    public void run() {
                        fireChartSetting();
                    }
                };
        settingAction.setText(Messages.chartSettingTxt);
        settingAction.setToolTipText(Messages.chartSettingTxt);
        settingAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/action/setting-small.png"));

        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(settingAction);
    }

    /**
     * Convert the Map key value
     *
     * @param inputMap the instance of Map<IDiagPara,String>
     * @return the instance of TreeMap<String, String>
     */
    private TreeMap<String, String> convertMapKey(Map<IDiagPara, String> inputMap) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        for (Map.Entry<IDiagPara, String> entry : inputMap.entrySet()) {
            if (isNewBrokerDiag) {
                if (entry.getKey() == BrokerDiagEnum.ACTIVE_SESSION) {
                    continue;
                }
            } else {
                if (entry.getKey() == BrokerDiagEnum.ACTIVE
                        || entry.getKey() == BrokerDiagEnum.SESSION) {
                    continue;
                }
            }
            map.put(entry.getKey().getName(), entry.getValue());
        }
        return map;
    }

    /** @param cpuSeriesKey the cpuSeriesKey to set */
    private void setCpuSeriesKey(String[] cpuSeriesKey) {
        for (String key : cpuSeriesKey) {
            cpuValueMap.put(key, "0");
        }
    }

    /** @param iowaitSeriesKey the iowaitSeriesKey to set */
    private void setIowaitSeriesKey(String[] iowaitSeriesKey) {
        for (String key : iowaitSeriesKey) {
            iowaitValueMap.put(key, "0");
        }
    }

    /** @param memorySeriesKey the memorySeriesKey to set */
    private void setMemorySeriesKey(String[] memorySeriesKey) {
        for (String key : memorySeriesKey) {
            memoryValueMap.put(key, "0");
        }
    }

    /** Disposes this view when it closed */
    public void dispose() {
        synchronized (this) {
            super.dispose();
        }
    }

    /** Call this method when this editor is focus */
    public void setFocus() {
        LayoutManager.getInstance()
                .getTitleLineContrItem()
                .changeTitleForViewOrEditPart(null, this);
        LayoutManager.getInstance()
                .getStatusLineContrItem()
                .changeStuatusLineForViewOrEditPart(null, this);
    }

    /**
     * This method is responsible for preparing data for ChartSettingDlg and dealing with the
     * results of chartSettingDlg
     */
    private void fireChartSetting() {

        ChartSettingDlg chartSettingDlg = new ChartSettingDlg(composite.getShell());
        chartSettingDlg.setHasTitlSetting(false);
        chartSettingDlg.setHasHistoryPath(true);
        chartSettingDlg.setHasAxisSetting(false);
        chartSettingDlg.setHasChartSelection(true);

        // plot appearance
        XYPlot cpuSeriesPlot = cpuChart.getSeriesChart().getXYPlot();
        String plotBgColor = trimPaintColor(cpuSeriesPlot.getBackgroundPaint().toString());
        String plotDomainGridColor =
                trimPaintColor(cpuSeriesPlot.getDomainGridlinePaint().toString());
        String plotRangGridColor = trimPaintColor(cpuSeriesPlot.getRangeGridlinePaint().toString());

        chartSettingDlg.setPlotBgColor(plotBgColor);
        chartSettingDlg.setPlotDomainGridColor(plotDomainGridColor);
        chartSettingDlg.setPlotRangGridColor(plotRangGridColor);

        // series
        if (brokerChartPart == null) {
            chartSettingDlg.setHasSeriesItemSetting(false);
        } else {
            chartSettingDlg.setHasSeriesItemSetting(true);
            chartSettingDlg.setSettingMap(brokerChartPart.getSettingMap());
        }
        // history path
        chartSettingDlg.setHistoryPath(historyPath);
        chartSettingDlg.setHistoryFileName(historyFileName);

        // chart selection
        chartSettingDlg.setChartSelectionLst(getSelectedCharts());

        if (chartSettingDlg.open() == Dialog.OK) {
            // plot appearance
            plotBgColor = chartSettingDlg.getPlotBgColor();
            plotDomainGridColor = chartSettingDlg.getPlotDomainGridColor();
            plotRangGridColor = chartSettingDlg.getPlotRangGridColor();

            XYPlot memorySeriesPlot = (XYPlot) memoryChart.getSeriesChart().getPlot();
            XYPlot iowaitSeriesPlot = (XYPlot) iowaitChart.getSeriesChart().getPlot();

            // history path
            String newHistoryPath = chartSettingDlg.getHistoryPath();
            isChangedHistoryPath = historyPath.equals(newHistoryPath) ? false : true;
            if (isChangedHistoryPath) {
                historyPath = newHistoryPath;
                historyFileHelp.setHistoryPath(historyPath);
            }

            int red = 0;
            int green = 0;
            int blue = 0;
            // background
            red = getColorElem(plotBgColor, 0);
            green = getColorElem(plotBgColor, 1);
            blue = getColorElem(plotBgColor, 2);
            Color bgColor = new Color(red, green, blue);

            // cpu chart
            cpuSeriesPlot.setBackgroundPaint(bgColor);
            // memoryChart
            memorySeriesPlot.setBackgroundPaint(bgColor);
            // iowaitChart
            iowaitSeriesPlot.setBackgroundPaint(bgColor);

            // DomainGridColor
            // broker Chart
            red = getColorElem(plotDomainGridColor, 0);
            green = getColorElem(plotDomainGridColor, 1);
            blue = getColorElem(plotDomainGridColor, 2);
            Color domainGridlineColor = new Color(red, green, blue);
            // cpu chart
            cpuSeriesPlot.setDomainGridlinePaint(domainGridlineColor);
            // memoryChart
            memorySeriesPlot.setDomainGridlinePaint(domainGridlineColor);
            // delayChart
            iowaitSeriesPlot.setDomainGridlinePaint(domainGridlineColor);

            // RangeGridColor
            red = getColorElem(plotRangGridColor, 0);
            green = getColorElem(plotRangGridColor, 1);
            blue = getColorElem(plotRangGridColor, 2);
            Color rangeGridColor = new Color(red, green, blue);

            // cpu chart
            cpuSeriesPlot.setRangeGridlinePaint(rangeGridColor);
            // memoryChart
            memorySeriesPlot.setRangeGridlinePaint(rangeGridColor);
            // delayChart
            iowaitSeriesPlot.setRangeGridlinePaint(rangeGridColor);
            // broker Chart;
            if (brokerChartPart != null) {
                XYPlot brokerPlot = brokerChartPart.getChart().getXYPlot();
                brokerPlot.setBackgroundPaint(bgColor);
                brokerPlot.setDomainGridlinePaint(domainGridlineColor);
                brokerPlot.setRangeGridlinePaint(rangeGridColor);

                brokerChartPart.setSettingMap(chartSettingDlg.getSettingMap());
                brokerChartPart.updateSettingSeries();
            }
            // chart Selection
            fireChartSelection(chartSettingDlg.getChartSelectionLst());
        }
    }

    /**
     * Get an instance of List<ChartShowingProp> which include the info of displayed chart
     *
     * @return List<ChartShowingProp>
     */
    private List<ChartShowingProp> getSelectedCharts() {
        List<ChartShowingProp> chartSelectionLst = new ArrayList<ChartShowingProp>();
        boolean isCpuVisible = cpuChart.getBasicComposite().isVisible();
        ChartShowingProp cpuShowingProp = new ChartShowingProp();
        cpuShowingProp.setName(Messages.hostSelectedChartCpu);
        cpuShowingProp.setShowing(isCpuVisible);
        chartSelectionLst.add(cpuShowingProp);

        boolean isMemoryVisible = memoryChart.getBasicComposite().isVisible();
        ChartShowingProp memoryShowingProp = new ChartShowingProp();
        memoryShowingProp.setName(Messages.hostSelectedChartMemory);
        memoryShowingProp.setShowing(isMemoryVisible);
        chartSelectionLst.add(memoryShowingProp);

        boolean isIowaitVisible = iowaitChart.getBasicComposite().isVisible();
        ChartShowingProp iowaitShowingProp = new ChartShowingProp();
        iowaitShowingProp.setName(Messages.hostSelectedChartIowait);
        iowaitShowingProp.setShowing(isIowaitVisible);
        chartSelectionLst.add(iowaitShowingProp);

        boolean isBrokerVisible = brokerComp.isVisible();
        ChartShowingProp brokerShowingProp = new ChartShowingProp();
        brokerShowingProp.setName(Messages.hostSelectedChartBroker);
        brokerShowingProp.setShowing(isBrokerVisible);
        chartSelectionLst.add(brokerShowingProp);
        return chartSelectionLst;
    }

    /**
     * Make a certain chart showing or no showing based on the given list. The order of the instance
     * of ChartShowingProp in the list depended upon the added order in the method of
     * getSelectedCharts.
     *
     * @param list the instance of List<ChartShowingProp>
     */
    private void fireChartSelection(List<ChartShowingProp> list) {

        if (list.get(0).isShowing()) {
            final GridData gridDataChart = new GridData(GridData.FILL_BOTH);
            cpuChart.getBasicComposite().setLayoutData(gridDataChart);
            cpuChart.getBasicComposite().setVisible(true);

        } else {
            final GridData gridDataChart = new GridData();
            gridDataChart.heightHint = 0;
            gridDataChart.widthHint = 0;
            cpuChart.getBasicComposite().setLayoutData(gridDataChart);
            cpuChart.getBasicComposite().setVisible(false);
        }
        if (list.get(1).isShowing()) {
            final GridData gridData = new GridData(GridData.FILL_BOTH);
            memoryChart.getBasicComposite().setLayoutData(gridData);
            memoryChart.getBasicComposite().setVisible(true);
        } else {
            final GridData gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            memoryChart.getBasicComposite().setLayoutData(gridData);
            memoryChart.getBasicComposite().setVisible(false);
        }
        if (list.get(2).isShowing()) {
            final GridData gridData = new GridData(GridData.FILL_BOTH);
            iowaitChart.getBasicComposite().setLayoutData(gridData);
            iowaitChart.getBasicComposite().setVisible(true);
        } else {
            final GridData gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            iowaitChart.getBasicComposite().setLayoutData(gridData);
            iowaitChart.getBasicComposite().setVisible(false);
        }
        if (list.get(3).isShowing()) {
            final GridData gridData = new GridData(GridData.FILL_BOTH);
            brokerComp.setLayoutData(gridData);
            brokerComp.setVisible(true);
        } else {
            final GridData gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            brokerComp.setLayoutData(gridData);
            brokerComp.setVisible(false);
        }
        chartComp.layout();
        composite.layout();
    }

    /**
     * This type is response to the button of history query
     *
     * @author lizhiqiang 2010-8-4
     */
    private final class HistoryBtnSelectionListener implements SelectionListener {
        private final HistoryComposite historyComposite;

        public HistoryBtnSelectionListener(HistoryComposite historyComposite) {
            this.historyComposite = historyComposite;
        }

        /**
         * default response to selection button
         *
         * @param ex the instance of SelectionEvent
         */
        public void widgetDefaultSelected(SelectionEvent ex) {
            String date = historyComposite.getDate();
            String fromTime = historyComposite.getFromTime();
            String toTime = historyComposite.getToTime();
            // check date/fromTime/toTime
            boolean timeOrder = historyComposite.checkTime(date, fromTime, toTime);
            if (!timeOrder) {
                CommonUITool.openErrorBox(Messages.errHostDashboardHistorySettingTime);
                return;
            }
            String[] ymd = date.split("-");
            int year = Integer.valueOf(ymd[0]);
            int month = Integer.valueOf(ymd[1]);
            int day = Integer.valueOf(ymd[2]);
            String[] fromHms = fromTime.split(":");
            int fromHour = Integer.valueOf(fromHms[0]);
            int fromMinute = Integer.valueOf(fromHms[1]);
            int fromSecond = Integer.valueOf(fromHms[2]);
            Calendar calFrom = Calendar.getInstance();
            calFrom.set(year, month, day, fromHour, fromMinute, fromSecond);
            final long millisFrom = calFrom.getTimeInMillis();
            String[] toHms = toTime.split(":");
            int toHour = Integer.valueOf(toHms[0]);
            int toMinute = Integer.valueOf(toHms[1]);
            int toSecond = Integer.valueOf(toHms[2]);
            Calendar calTo = Calendar.getInstance();
            calTo.set(year, month, day, toHour, toMinute, toSecond);
            final long millisTo = calTo.getTimeInMillis();

            final CounterFile countFile = historyFileHelp.openHistoryFile();

            if (countFile == null) {
                return;
            }
            // cpu chart
            XYPlot cpuPlot = (XYPlot) cpuChart.getSeriesChart().getPlot();
            cpuPlot.getDomainAxis().setRange(millisFrom, millisTo);

            final List<String> cpuTypes = new ArrayList<String>();
            String typeUser = HostStatEnum.USER.name();
            cpuTypes.add(typeUser);

            String typeKernel = HostStatEnum.KERNEL.name();
            cpuTypes.add(typeKernel);

            cpuChart.executeQueryWithBusyCursor(countFile, cpuTypes, millisFrom, millisTo);
            // memory chart
            XYPlot memoryPlot = (XYPlot) memoryChart.getSeriesChart().getPlot();
            memoryPlot.getDomainAxis().setRange(millisFrom, millisTo);

            final List<String> memoryTypes = new ArrayList<String>();
            String typeMemory = HostStatEnum.MEMPHY_USED.name();
            memoryTypes.add(typeMemory);

            String maxPercentType = HostStatEnum.MEMPHY_PERCENT.name();
            memoryChart.executeQueryWithBusyCursor(
                    countFile, memoryTypes, millisFrom, millisTo, maxPercentType);
            // iowait chart
            XYPlot iowaitPlot = (XYPlot) iowaitChart.getSeriesChart().getPlot();
            iowaitPlot.getDomainAxis().setRange(millisFrom, millisTo);

            final List<String> iowaitTypes = new ArrayList<String>();
            String typeIowait = HostStatEnum.IOWAIT.name();
            iowaitTypes.add(typeIowait);

            iowaitChart.executeQueryWithBusyCursor(countFile, iowaitTypes, millisFrom, millisTo);

            // broker chart
            XYPlot brokerPlot = (XYPlot) brokerChartPart.getChart().getPlot();
            brokerPlot.getDomainAxis().setRange(millisFrom, millisTo);

            final List<String> brokerTypes = new ArrayList<String>();
            for (BrokerDiagEnum enumeration : BrokerDiagEnum.values()) {
                String type = enumeration.getName();
                brokerTypes.add(type);
            }
            brokerChartPart.executeQueryWithBusyCursor(
                    countFile, brokerTypes, millisFrom, millisTo);
            try {
                countFile.close();
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage());
            }
        }

        /**
         * response to selection button
         *
         * @param ex the instance of SelectionEvent
         */
        public void widgetSelected(SelectionEvent ex) {
            widgetDefaultSelected(ex);
        }
    }
}
