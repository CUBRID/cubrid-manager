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

import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HistoryFileHelp;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * This type is responsible for the type of HostSystemMonitorViewPart with UI
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-13 created by lizhiqiang
 */
public class HostSystemMonitorCompositePart {
    private String[] cpuSeriesKey;
    private String[] iowaitSeriesKey;
    private String[] memorySeriesKey;
    private final TreeMap<String, String> cpuValueMap;
    private final TreeMap<String, String> iowaitValueMap;
    private final TreeMap<String, String> memoryValueMap;
    private final Composite composite;
    private CombinedBarTimeSeriesChart cpuChart;
    private CombinedBarTimeSeriesChart iowaitChart;
    private CombinedBarTimeSeriesChart memoryChart;
    private Label usercpuValue;
    private Label kernelcpuValue;
    private Label phymemTtlValue;
    private Label phymemUsedValue;
    private Label iowaitValue;
    private Label swapmemTtlValue;
    private Label swapmemUsedValue;
    private Composite chartComp;

    private String historyPath;
    private String historyFileName;
    private HistoryFileHelp historyFileHelp;
    private Label totalcpuValue;
    private Composite sysInfoComp;

    public HostSystemMonitorCompositePart(Composite composite) {
        this.composite = composite;
        cpuValueMap = new TreeMap<String, String>();
        iowaitValueMap = new TreeMap<String, String>();
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
        loadIowaitChart(chartComp);

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
        cpuChart.setBarGroupName(Messages.hostSysCpuChartBarGrpName);
        cpuChart.setSeriesGroupName(Messages.hostSysCpuChartSeriesGrpName);
        cpuChart.load(parent);
    }

    /**
     * * Load an instance of CombinedBarTimeSeriesChart stand for IO wait
     *
     * @param parent an instance of Composite
     */
    private void loadIowaitChart(Composite parent) {
        iowaitChart = new CombinedBarTimeSeriesChart();
        iowaitChart.setValueMap(iowaitValueMap);
        iowaitChart.setBarGroupName(Messages.hostSysIowaitChartBartGrpName);
        iowaitChart.setSeriesGroupName(Messages.hostSysIowaitChartSeriesGrpName);
        iowaitChart.load(parent);
    }

    /**
     * Load an instance of CombinedBarTimeSeriesChart stand for physical
     *
     * @param parent an instance of Composite
     */
    private void loadMemoryChart(Composite parent) {
        memoryChart = new CombinedBarTimeSeriesChart();
        memoryChart.setValueMap(memoryValueMap);
        memoryChart.setBarGroupName(Messages.hostSysPhysicalChartBarGrpName);
        memoryChart.setSeriesGroupName(Messages.hostSysPhysicalChartSeriesGrpName);
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
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));

        createCpuInfoGroup(comp);
        createPhyMemGroup(comp);
        createIowaitGroup(comp);
        createSwapMemGroup(comp);
    }

    /**
     * Create a group including cpu info
     *
     * @param comp an instance of Composite
     */
    private void createCpuInfoGroup(Composite comp) {
        Group cpuGrp = new Group(comp, SWT.UP);
        cpuGrp.setText(Messages.hostSysCppuInfoGrpName);
        cpuGrp.setLayout(new GridLayout(2, false));
        cpuGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label usercpuLbl = new Label(cpuGrp, SWT.NULL);
        usercpuLbl.setText(Messages.hostSysCpuUserInfoLbl);

        usercpuValue = new Label(cpuGrp, SWT.RIGHT);
        usercpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label kernelcpuLbl = new Label(cpuGrp, SWT.NULL);
        kernelcpuLbl.setText(Messages.hostSysCpuKernelInfoLbl);

        kernelcpuValue = new Label(cpuGrp, SWT.RIGHT);
        kernelcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label totalcpuLbl = new Label(cpuGrp, SWT.NULL);
        totalcpuLbl.setText(Messages.hostSysCpuTotalInfoLbl);

        totalcpuValue = new Label(cpuGrp, SWT.RIGHT);
        totalcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create a group including physical memory
     *
     * @param comp an instance of Composite
     */
    private void createPhyMemGroup(Composite comp) {
        Group phymemGrp = new Group(comp, SWT.NULL);
        phymemGrp.setText(Messages.hostSysMemPhyInfoGrpName);
        phymemGrp.setLayout(new GridLayout(2, false));
        phymemGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label phymemTtlLbl = new Label(phymemGrp, SWT.NULL);
        phymemTtlLbl.setText(Messages.hostSysMemPhyInfoTtlLbl);

        phymemTtlValue = new Label(phymemGrp, SWT.RIGHT);
        phymemTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label phymemFreeLbl = new Label(phymemGrp, SWT.NULL);
        phymemFreeLbl.setText(Messages.hostSysMemPhyInfoUsedLbl);

        phymemUsedValue = new Label(phymemGrp, SWT.RIGHT);
        phymemUsedValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * create a Group including IO wait info.
     *
     * @param comp the given instance of Composite
     */
    private void createIowaitGroup(Composite comp) {
        Group iowaitGrp = new Group(comp, SWT.NULL);
        iowaitGrp.setText(Messages.hostSysIowaitInfoGrpName);
        iowaitGrp.setLayout(new GridLayout(2, false));
        iowaitGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label iowaitLbl = new Label(iowaitGrp, SWT.NULL);
        iowaitLbl.setText(Messages.hostSysIowaitUsedLbl);

        iowaitValue = new Label(iowaitGrp, SWT.RIGHT);
        iowaitValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(iowaitGrp, SWT.NONE);
    }

    /**
     * Create a group including the swap memory info.
     *
     * @param comp the given instance of Composite
     */
    private void createSwapMemGroup(Composite comp) {
        Group swapmemGrp = new Group(comp, SWT.NULL);
        swapmemGrp.setText(Messages.hostSysMemSwapInfoGrpName);
        swapmemGrp.setLayout(new GridLayout(2, false));
        swapmemGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label swapmemTtlLbl = new Label(swapmemGrp, SWT.NULL);
        swapmemTtlLbl.setText(Messages.hostSysMemSwapInfoTtlLbl);

        swapmemTtlValue = new Label(swapmemGrp, SWT.RIGHT);
        swapmemTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label swapmemFreeLbl = new Label(swapmemGrp, SWT.NULL);
        swapmemFreeLbl.setText(Messages.hostSysMemSwapInfoUsedLbl);

        swapmemUsedValue = new Label(swapmemGrp, SWT.RIGHT);
        swapmemUsedValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * update value in the chart and group unit
     *
     * @param resultMap the given instance of TreeMap<String,String>
     */
    public void updateValueMap(TreeMap<String, String> resultMap) {
        // update series
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            for (String key : cpuSeriesKey) {
                if (key.equals(entry.getKey())) {
                    cpuValueMap.put(key, entry.getValue());
                }
            }
            for (String key : iowaitSeriesKey) {
                if (key.equals(entry.getKey())) {
                    iowaitValueMap.put(key, entry.getValue());
                }
            }
            for (String key : memorySeriesKey) {
                if (key.equals(entry.getKey())) {
                    memoryValueMap.put(key, entry.getValue());
                }
            }
        }
        cpuChart.updateValueMap(cpuValueMap);
        iowaitChart.updateValueMap(iowaitValueMap);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setGroupingUsed(true);

        String memPhyUsed = resultMap.get(HostStatEnum.MEMPHY_USED.name());
        double memPhyUsedMb = 0;
        if (null != memPhyUsed) {
            memPhyUsedMb = Long.parseLong(memPhyUsed) / 1024.0;
            memoryChart.updateValueMap(memoryValueMap, numberFormat.format(memPhyUsedMb) + "MB");
            phymemUsedValue.setText(numberFormat.format(memPhyUsedMb));
        }

        // update info
        int totalCpuPercent = 0;
        String userPersent = resultMap.get(HostStatEnum.USER.name());
        if (null != userPersent) {
            usercpuValue.setText(userPersent + "%");
            totalCpuPercent = Integer.parseInt(userPersent);
        }
        String kernelPercent = resultMap.get(HostStatEnum.KERNEL.name());
        if (null != kernelPercent) {
            kernelcpuValue.setText(kernelPercent + "%");
            totalCpuPercent += Integer.parseInt(kernelPercent);
        }
        totalcpuValue.setText(totalCpuPercent + "%");

        iowaitValue.setText(resultMap.get(HostStatEnum.IOWAIT.name()) + "%");

        String memPhyTtl = resultMap.get(HostStatEnum.MEMPHY_TOTAL.name());
        if (null != memPhyTtl) {
            double memPhyTtlMb = Long.parseLong(memPhyTtl) / 1024.0;
            phymemTtlValue.setText(numberFormat.format(memPhyTtlMb));

            Integer hMemPhyPercent = (int) (memPhyUsedMb / memPhyTtlMb * 10000 + 0.5);
            resultMap.put(HostStatEnum.MEMPHY_PERCENT.name(), Integer.toString(hMemPhyPercent));
            resultMap.put(HostStatEnum.MEMPHY_USED.name(), Integer.toString((int) memPhyUsedMb));
        }

        String memSwapTtl = resultMap.get(HostStatEnum.MEMSWAP_TOTAL.name());
        if (null != memSwapTtl) {
            double memSwapTtlMb = Long.parseLong(memSwapTtl) / 1024.0;
            swapmemTtlValue.setText(numberFormat.format(memSwapTtlMb));
        }

        String memSwapUsed = resultMap.get(HostStatEnum.MEMSWAP_USED.name());
        if (null != memSwapUsed) {
            double memSwapUsedMb = Long.parseLong(memSwapUsed) / 1024.0;
            swapmemUsedValue.setText(numberFormat.format(memSwapUsedMb));
        }
    }

    /** @param cpuSeriesKey the cpuSeriesKey to set */
    public void setCpuSeriesKey(String[] cpuSeriesKey) {
        this.cpuSeriesKey = cpuSeriesKey.clone();
        for (String key : cpuSeriesKey) {
            cpuValueMap.put(key, "0");
        }
    }

    /** @param iowaitSeriesKey the iowaitSeriesKey to set */
    public void setIowaitSeriesKey(String[] iowaitSeriesKey) {
        this.iowaitSeriesKey = iowaitSeriesKey.clone();
        for (String key : iowaitSeriesKey) {
            iowaitValueMap.put(key, "0");
        }
    }

    /** @param memorySeriesKey the memorySeriesKey to set */
    public void setMemorySeriesKey(String[] memorySeriesKey) {
        this.memorySeriesKey = memorySeriesKey.clone();
        for (String key : memorySeriesKey) {
            memoryValueMap.put(key, "0");
        }
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
        XYPlot cpuSeriesPlot = cpuChart.getSeriesChart().getXYPlot();
        String plotBgColor = trimPaintColor(cpuSeriesPlot.getBackgroundPaint().toString());
        String plotDomainGridColor =
                trimPaintColor(cpuSeriesPlot.getDomainGridlinePaint().toString());
        String plotRangGridColor = trimPaintColor(cpuSeriesPlot.getRangeGridlinePaint().toString());

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

            JFreeChart memoryBarChart = memoryChart.getBarChart();
            Plot memoryBarPlot = memoryBarChart.getPlot();
            XYPlot memorySeriesPlot = (XYPlot) memoryChart.getSeriesChart().getPlot();

            JFreeChart iowaitBarChart = iowaitChart.getBarChart();
            Plot iowaitBarPlot = iowaitBarChart.getPlot();
            XYPlot iowaitSeriesPlot = (XYPlot) iowaitChart.getSeriesChart().getPlot();

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

            // cpu chart
            cpuBarChart.setBackgroundPaint(bgColor);
            cpuBarPlot.setBackgroundPaint(bgColor);
            cpuSeriesPlot.setBackgroundPaint(bgColor);
            // memoryChart
            memoryBarPlot.setBackgroundPaint(bgColor);
            memoryBarChart.setBackgroundPaint(bgColor);
            memorySeriesPlot.setBackgroundPaint(bgColor);
            // iowaitChart
            iowaitBarPlot.setBackgroundPaint(bgColor);
            iowaitBarChart.setBackgroundPaint(bgColor);
            iowaitSeriesPlot.setBackgroundPaint(bgColor);

            // DomainGridColor
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
            // iowaitChart
            iowaitSeriesPlot.setRangeGridlinePaint(rangeGridColor);

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
        sysInfoShowingProp.setName(Messages.hostSelectedSysInfo);
        sysInfoShowingProp.setShowing(isSysInfoVisible);
        chartSelectionLst.add(sysInfoShowingProp);

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
        if (list.get(3).isShowing()) {
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

        chartComp.layout();
        sysInfoComp.layout();
        composite.layout();
    }

    /**
     * Get the historyPath
     *
     * @return the historyPath
     */
    public String getHistoryPath() {
        return historyPath;
    }

    /** @param historyPath the historyPath to set */
    public void setHistoryPath(String historyPath) {
        this.historyPath = historyPath;
    }

    /**
     * Get the historyFileName
     *
     * @return the historyFileName
     */
    public String getHistoryFileName() {
        return historyFileName;
    }

    /** @param historyFileName the historyFileName to set */
    public void setHistoryFileName(String historyFileName) {
        this.historyFileName = historyFileName;
    }

    /** @param historyFileHelp the historyFileHelp to set */
    public void setHistoryFileHelp(HistoryFileHelp historyFileHelp) {
        this.historyFileHelp = historyFileHelp;
    }
}
