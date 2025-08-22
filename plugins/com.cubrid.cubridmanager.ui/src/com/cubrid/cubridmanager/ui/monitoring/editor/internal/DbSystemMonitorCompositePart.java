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
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import static com.cubrid.common.ui.spi.util.CommonUITool.getColorElem;
import static com.cubrid.common.ui.spi.util.CommonUITool.trimPaintColor;

import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HistoryFileHelp;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

/**
 * This type is responsible for the type of DbStatusDumpMonitorViewPart with UI
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-18 created by lizhiqiang
 */
public class DbSystemMonitorCompositePart {
    private String[] cpuSeriesKey;
    private String[] memorySeriesKey;
    private final Composite composite;
    private final TreeMap<String, String> cpuValueMap;
    private final TreeMap<String, String> memoryValueMap;
    private CombinedBarTimeSeriesChart cpuChart;
    private CombinedBarTimeSeriesChart memoryChart;
    private Label usercpuValue;
    private Label kernelcpuValue;
    private Label phymemValue;
    private Label virmemValue;
    private Composite chartComp;

    private String historyPath;
    private String historyFileName;
    private HistoryFileHelp historyFileHelp;
    private Label totalcpuValue;
    private Composite sysInfoComp;

    public DbSystemMonitorCompositePart(Composite composite) {
        this.composite = composite;
        cpuValueMap = new TreeMap<String, String>();
        memoryValueMap = new TreeMap<String, String>();
    }

    /** Load all the necessary unit on the composite */
    public void load() {
        chartComp = new Composite(composite, SWT.RESIZE);
        GridLayout chartCompLayout = new GridLayout(1, true);
        chartComp.setLayout(chartCompLayout);
        chartComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        loadCpuChart(chartComp);
        loadMemoryChart(chartComp);

        sysInfoComp = new Composite(composite, SWT.RESIZE);
        GridLayout sysInfoCompLayout = new GridLayout();
        sysInfoComp.setLayout(sysInfoCompLayout);
        sysInfoComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        loadSystemInfoComposite(sysInfoComp);
    }

    /**
     * Load an instance of CombinedBarTimeSeriesChart stand for CPU
     *
     * @param parent an instance of Composite
     */
    private void loadCpuChart(Composite parent) {
        cpuChart = new CombinedBarTimeSeriesChart();
        cpuChart.setAreaRender(true);
        cpuChart.setValueMap(cpuValueMap);
        cpuChart.setBarGroupName(Messages.dbSysCpuChartBarGroupName);
        cpuChart.setSeriesGroupName(Messages.dbSysCpuChartSeriesGroupName);
        cpuChart.load(parent);
    }

    /**
     * Load an instance of CombinedBarTimeSeriesChart stand for physical
     *
     * @param parent an instance of Composite
     */
    private void loadMemoryChart(Composite parent) {
        memoryChart = new CombinedBarTimeSeriesChart();
        memoryChart.setValueMap(memoryValueMap);
        memoryChart.setBarGroupName(Messages.dbSysPhysicalChartBarGroupName);
        memoryChart.setSeriesGroupName(Messages.dbSysPhysicalChartSeriesGroupName);
        memoryChart.load(parent);
    }

    /**
     * Load groups including system info
     *
     * @param parent an instance of Composite
     */
    private void loadSystemInfoComposite(Composite parent) {
        Composite comp = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, true);
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createCpuInfoGroup(comp);
        createMemGroup(comp);
    }

    /**
     * Create a group including database CPU info
     *
     * @param parent an instance of Composite
     */
    private void createCpuInfoGroup(Composite parent) {
        Group cpuGrp = new Group(parent, SWT.NULL);
        cpuGrp.setText(Messages.dbSysCpuInfoGroupName);
        cpuGrp.setLayout(new GridLayout(2, false));
        cpuGrp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label usercpuLbl = new Label(cpuGrp, SWT.NULL);
        usercpuLbl.setText(Messages.dbSysCpuUserInfoLbl);

        usercpuValue = new Label(cpuGrp, SWT.RIGHT);
        usercpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label kernelcpuLbl = new Label(cpuGrp, SWT.NULL);
        kernelcpuLbl.setText(Messages.dbSysCpuKernelInfoLbl);

        kernelcpuValue = new Label(cpuGrp, SWT.RIGHT);
        kernelcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label totalcpuLbl = new Label(cpuGrp, SWT.NULL);
        totalcpuLbl.setText(Messages.dbSysCpuTotalInfoLbl);

        totalcpuValue = new Label(cpuGrp, SWT.RIGHT);
        totalcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create a group including memory info
     *
     * @param parent an instance of Composite
     */
    private void createMemGroup(Composite parent) {
        Group memGrp = new Group(parent, SWT.NULL);
        memGrp.setText(Messages.dbSysMemInfoGroupName);
        memGrp.setLayout(new GridLayout(2, false));
        memGrp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label phymemLbl = new Label(memGrp, SWT.NULL);
        phymemLbl.setText(Messages.dbSysMemPhysicalLbl);

        phymemValue = new Label(memGrp, SWT.RIGHT);
        phymemValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label virmemLbl = new Label(memGrp, SWT.NULL);
        virmemLbl.setText(Messages.dbSysMemVirtualLbl);

        virmemValue = new Label(memGrp, SWT.RIGHT);
        virmemValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * update value in the chart and group unit
     *
     * @param resultMap the given instance of TreeMap<String,String>
     */
    public void updateValueMap(TreeMap<String, String> resultMap) {
        String deltaCpuUser = resultMap.get(DbProcStatEnum.DELTA_USER.name());
        String deltaCpuKernel = resultMap.get(DbProcStatEnum.DELTA_KERNEL.name());
        String hostCpuTotal = resultMap.get(HostStatEnum.CPU_TOTAL.name());
        String hostMemTotal = resultMap.get(HostStatEnum.MEMPHY_TOTAL.name());
        if (hostCpuTotal == null || hostMemTotal == null) {
            return;
        }
        double hostCpuTotalDouble = Double.parseDouble(hostCpuTotal);
        double hostMemTotalDouble = Double.parseDouble(hostMemTotal);
        Long deltaCpuUserLong = Long.parseLong(deltaCpuUser == null ? "0" : deltaCpuUser);
        Long dletaCpuKernelLong = Long.parseLong(deltaCpuKernel == null ? "0" : deltaCpuKernel);
        int userPercent = 0;
        int kernelPercent = 0;
        if (!"0".equals(hostCpuTotal)) {
            userPercent = (int) (deltaCpuUserLong / hostCpuTotalDouble * 100 + 0.5);
            kernelPercent = (int) (dletaCpuKernelLong / hostCpuTotalDouble * 100 + 0.5);
            resultMap.put(DbProcStatEnum.USER_PERCENT.name(), Integer.toString(userPercent));
            resultMap.put(DbProcStatEnum.KERNEL_PERCENT.name(), Integer.toString(kernelPercent));
        }

        String memPhyUsed = resultMap.get(DbProcStatEnum.MEM_PHYSICAL.name());
        memPhyUsed = memPhyUsed == null ? "0" : memPhyUsed;
        double memPhyUsedLong = Long.parseLong(memPhyUsed);

        int memPhyPercent = 0;
        if (!"0".equals(hostMemTotal)) {
            Integer hMemPhyPercent = (int) (memPhyUsedLong / hostMemTotalDouble * 10000 + 0.5);
            resultMap.put(DbProcStatEnum.MEMPHY_PERCENT.name(), Integer.toString(hMemPhyPercent));
            memPhyPercent = (int) (memPhyUsedLong / hostMemTotalDouble * 100 + 0.5);
        }

        // update series
        for (String key : cpuSeriesKey) {
            if (key.equals(DbProcStatEnum.USER_PERCENT.name())) {
                cpuValueMap.put(key, Integer.toString(userPercent));
            } else if (key.equals(DbProcStatEnum.KERNEL_PERCENT.name())) {
                cpuValueMap.put(key, Integer.toString(kernelPercent));
            }
        }
        for (String key : memorySeriesKey) {
            if (key.equals(DbProcStatEnum.MEMPHY_PERCENT.name())) {
                memoryValueMap.put(key, Integer.toString(memPhyPercent));
            }
        }
        cpuChart.updateValueMap(cpuValueMap);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setGroupingUsed(true);

        String memPhy = resultMap.get(DbProcStatEnum.MEM_PHYSICAL.name());
        if (null != memPhy) {
            double memPhyMb = Long.parseLong(memPhy) / 1024.0;
            memoryChart.updateValueMap(memoryValueMap, numberFormat.format(memPhyMb) + "MB");
            resultMap.put(
                    DbProcStatEnum.MEM_PHYSICAL.name(), Integer.toString((int) (memPhyMb + 0.5)));
            // update physical memory value
            phymemValue.setText(numberFormat.format(memPhyMb));
        }

        // update info
        usercpuValue.setText(userPercent + "%");
        kernelcpuValue.setText(kernelPercent + "%");
        int totalCpuPercent = userPercent + kernelPercent;
        totalcpuValue.setText(totalCpuPercent + "%");

        String memVirtual = resultMap.get(DbProcStatEnum.MEM_VIRTUAL.name());
        if (null != memVirtual) {
            double memVirMb = Long.parseLong(memVirtual) / 1024.0;
            virmemValue.setText(numberFormat.format(memVirMb));
        }
    }

    /** @param cpuSeriesKey the cpuSeriesKey to set */
    public void setCpuSeriesKey(String[] cpuSeriesKey) {
        this.cpuSeriesKey = cpuSeriesKey.clone();
        for (String key : cpuSeriesKey) {
            cpuValueMap.put(key, "0");
        }
    }

    /** @param memorySeriesKey the memorySeriesKey to set */
    public void setMemorySeriesKey(String[] memorySeriesKey) {
        this.memorySeriesKey = memorySeriesKey.clone();
        for (String key : memorySeriesKey) {
            memoryValueMap.put(key, "0");
        }
    }

    /** Update the plot in chart from scratch. */
    public void updateChart() {
        cpuChart.updateFromScratch();
        memoryChart.updateFromScratch();
    }

    /**
     * This method is responsible for preparing data for ChartSettingDlg and dealing with the
     * results of chartSettingDlg
     */
    public void fireChartSetting() {

        ChartSettingDlg chartSettingDlg = new ChartSettingDlg(composite.getShell());
        chartSettingDlg.setHasTitlSetting(false);
        chartSettingDlg.setHasHistoryPath(true);
        chartSettingDlg.setHasAxisSetting(false);
        chartSettingDlg.setHasSeriesItemSetting(false);
        chartSettingDlg.setHasChartSelection(true);

        // plot appearance
        XYPlot dbStatplot = cpuChart.getSeriesChart().getXYPlot();
        String plotBgColor = trimPaintColor(dbStatplot.getBackgroundPaint().toString());
        String plotDomainGridColor = trimPaintColor(dbStatplot.getDomainGridlinePaint().toString());
        String plotRangGridColor = trimPaintColor(dbStatplot.getRangeGridlinePaint().toString());

        chartSettingDlg.setPlotBgColor(plotBgColor);
        chartSettingDlg.setPlotDomainGridColor(plotDomainGridColor);
        chartSettingDlg.setPlotRangGridColor(plotRangGridColor);

        // history setting
        chartSettingDlg.setHistoryFileName(historyFileName);
        chartSettingDlg.setHistoryPath(historyPath);

        // chart selection
        chartSettingDlg.setChartSelectionLst(getSelectedCharts());

        if (chartSettingDlg.open() == Dialog.OK) {
            // plot appearance
            plotBgColor = chartSettingDlg.getPlotBgColor();
            plotDomainGridColor = chartSettingDlg.getPlotDomainGridColor();
            plotRangGridColor = chartSettingDlg.getPlotRangGridColor();

            JFreeChart cpuBarChart = cpuChart.getBarChart();
            Plot cpuBarPlot = cpuBarChart.getPlot();
            XYPlot cpuSeriesPlot = (XYPlot) cpuChart.getSeriesChart().getPlot();

            JFreeChart memoryBarChart = memoryChart.getBarChart();
            Plot memoryBarPlot = memoryBarChart.getPlot();
            XYPlot memorySeriesPlot = (XYPlot) memoryChart.getSeriesChart().getPlot();

            // history path
            String newHistoryPath = chartSettingDlg.getHistoryPath();
            boolean isChangedHistoryPath = historyPath.equals(newHistoryPath) ? false : true;
            if (isChangedHistoryPath) {
                historyPath = newHistoryPath;
                historyFileHelp.setHistoryPath(historyPath);
                historyFileHelp.setChangedHistoryPath(true);
            }

            int red = 0;
            int green = 0;
            int blue = 0;
            // background
            red = getColorElem(plotBgColor, 0);
            green = getColorElem(plotBgColor, 1);
            blue = getColorElem(plotBgColor, 2);
            Color bgColor = new Color(red, green, blue);
            dbStatplot.setBackgroundPaint(bgColor);

            // cpu chart
            cpuBarChart.setBackgroundPaint(bgColor);
            cpuBarPlot.setBackgroundPaint(bgColor);
            cpuSeriesPlot.setBackgroundPaint(bgColor);
            // memoryChart
            memoryBarPlot.setBackgroundPaint(bgColor);
            memoryBarChart.setBackgroundPaint(bgColor);
            memorySeriesPlot.setBackgroundPaint(bgColor);

            // DomainGridColor
            // db Chart
            red = getColorElem(plotDomainGridColor, 0);
            green = getColorElem(plotDomainGridColor, 1);
            blue = getColorElem(plotDomainGridColor, 2);
            Color domainGridlineColor = new Color(red, green, blue);
            dbStatplot.setDomainGridlinePaint(domainGridlineColor);
            // cpu chart
            cpuSeriesPlot.setDomainGridlinePaint(domainGridlineColor);
            // memoryChart
            memorySeriesPlot.setDomainGridlinePaint(domainGridlineColor);

            // RangeGridColor
            red = getColorElem(plotRangGridColor, 0);
            green = getColorElem(plotRangGridColor, 1);
            blue = getColorElem(plotRangGridColor, 2);
            Color rangeGridColor = new Color(red, green, blue);
            dbStatplot.setRangeGridlinePaint(rangeGridColor);
            // cpu chart
            cpuSeriesPlot.setRangeGridlinePaint(rangeGridColor);
            // memoryChart
            memorySeriesPlot.setRangeGridlinePaint(rangeGridColor);

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

        boolean isSysInfoVisible = sysInfoComp.isVisible();
        ChartShowingProp sysInfoShowingProp = new ChartShowingProp();
        sysInfoShowingProp.setName(Messages.dbSelectedSysInfo);
        sysInfoShowingProp.setShowing(isSysInfoVisible);
        chartSelectionLst.add(sysInfoShowingProp);

        boolean isCpuVisible = cpuChart.getBasicComposite().isVisible();
        ChartShowingProp cpuShowingProp = new ChartShowingProp();
        cpuShowingProp.setName(Messages.dbSelectedChartCpu);
        cpuShowingProp.setShowing(isCpuVisible);
        chartSelectionLst.add(cpuShowingProp);

        boolean isMemoryVisible = memoryChart.getBasicComposite().isVisible();
        ChartShowingProp memoryShowingProp = new ChartShowingProp();
        memoryShowingProp.setName(Messages.dbSelectedChartMemory);
        memoryShowingProp.setShowing(isMemoryVisible);
        chartSelectionLst.add(memoryShowingProp);
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
        int showingCount = 0;
        for (ChartShowingProp prop : list) {
            if (prop.isShowing()) {
                showingCount++;
            }
        }
        if (list.get(0).isShowing()) {
            final GridData gridDatatSys = new GridData(GridData.FILL_HORIZONTAL);
            sysInfoComp.setLayoutData(gridDatatSys);
            sysInfoComp.setVisible(true);
            if (showingCount == 1) {
                final GridData gridDataChart = new GridData();
                gridDataChart.heightHint = 0;
                gridDataChart.widthHint = 0;
                chartComp.setLayoutData(gridDataChart);
                chartComp.setVisible(false);
            } else {
                final GridData gridDataChart = new GridData(GridData.FILL_BOTH);
                chartComp.setLayoutData(gridDataChart);
                chartComp.setVisible(true);
            }

        } else {
            final GridData gridDataSys = new GridData();
            gridDataSys.heightHint = 0;
            gridDataSys.widthHint = 0;
            sysInfoComp.setLayoutData(gridDataSys);
            sysInfoComp.setVisible(false);
            final GridData gridDataChart = new GridData(GridData.FILL_BOTH);
            chartComp.setLayoutData(gridDataChart);
            chartComp.setVisible(true);
        }
        if (list.get(1).isShowing()) {
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
        if (list.get(2).isShowing()) {
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

        chartComp.layout();
        sysInfoComp.layout();
        composite.layout();
    }

    /** @param historyPath the historyPath to set */
    public void setHistoryPath(String historyPath) {
        this.historyPath = historyPath;
    }

    /** @param historyFileHelp the historyFileHelp to set */
    public void setHistoryFileHelp(HistoryFileHelp historyFileHelp) {
        this.historyFileHelp = historyFileHelp;
    }

    /** @param historyFileName the historyFileName to set */
    public void setHistoryFileName(String historyFileName) {
        this.historyFileName = historyFileName;
    }
}
