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
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataChangedEvent;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGenerator;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.MondashDataResult;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartSettingDlg;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartShowingProp;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.CombinedBarTimeSeriesChart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.MonitorType;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.RecordAction;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.Recordable;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSetting;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSettingMatching;
import java.awt.Color;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * A editor part is used to view system monitor info which.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-17 created by lizhiqiang
 */
public class HostDashboardViewPart extends ViewPart implements DataUpdateListener, Recordable {

    public static final String ID = HostDashboardViewPart.class.getName();

    private String[] cpuSeriesKey;
    private String[] iowaitSeriesKey;
    private String[] memorySeriesKey;
    private TreeMap<String, String> cpuValueMap;
    private TreeMap<String, String> iowaitValueMap;
    private TreeMap<String, String> memoryValueMap;
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
    private DataGenerator generator;
    private ChartCompositePart brokerChartPart;
    private HostNode hostNode;
    private Composite composite;
    private Composite chartComp;

    private Composite brokerComp;

    private boolean recordFlag;
    private String historyPath;
    private String historyFileName;
    private boolean isChangedHistoryPath;
    private HistoryFileHelp historyFileHelp;

    private String[] typeNames;
    private boolean isNewBrokerDiag;

    private Label totalcpuValue;

    private Composite sysInfoComp;

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
        setMemorySeriesKey(new String[] {HostStatEnum.MEMPHY_PERCENT.name()});
    }

    /**
     * Initializes
     *
     * @param hostNode The HostNode
     */
    public void init(HostNode hostNode) {
        this.hostNode = hostNode;
        String partName = getPartName();
        String postfix = " - " + hostNode.getIp() + ":" + hostNode.getPort();
        if (!partName.endsWith(postfix)) {
            setPartName(partName + postfix);
        }
        ServerInfo serverInfo = hostNode.getServerInfo();
        ServerType serverType = null;
        if (serverInfo != null) {
            serverType = serverInfo.getServerType();
        }
        if (serverType != ServerType.DATABASE && brokerChartPart == null) {
            loadBrokerChart(chartComp);
            chartComp.layout();
            composite.layout();
        }

        if (serverInfo != null) {
            String hostAddress = serverInfo.getHostAddress();
            int monPort = serverInfo.getHostMonPort();
            historyFileName =
                    HistoryComposite.HOSTDASHBOARD_HISTORY_FILE_PREFIX
                            + hostAddress
                            + "_"
                            + monPort
                            + HistoryComposite.HISTORY_SUFFIX;
            IPath histPath = CubridManagerCorePlugin.getDefault().getStateLocation();
            historyPath = histPath.toOSString() + File.separator + historyFileName;
            historyFileHelp = new HistoryFileHelp();
            historyFileHelp.setHistoryPath(historyPath);
            isNewBrokerDiag = CompatibleUtil.isNewBrokerDiag(serverInfo);
        }

        List<String> typeLst = new ArrayList<String>();
        for (HostStatEnum hostEnum : HostStatEnum.values()) {
            typeLst.add(hostEnum.getName());
        }
        for (BrokerDiagEnum brokerEnum : BrokerDiagEnum.values()) {
            typeLst.add(brokerEnum.getName());
        }
        typeNames = typeLst.toArray(new String[typeLst.size()]);

        DataGeneratorPool pool = DataGeneratorPool.getInstance();
        String generatorName =
                hostNode.getUserName() + "@" + hostNode.getIp() + ":" + hostNode.getPort();
        generator = pool.getDataGenerator(generatorName, new DataProvider());
        generator.addDataUpdateListener(this);
    }

    /**
     * Creates the SWT controls for this workbench part.
     *
     * @param parent the parent control
     */
    public void createPartControl(Composite parent) {
        final ScrolledComposite scrolledComp =
                new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

        composite = new Composite(scrolledComp, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

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
        GridData sysGridData = new GridData(GridData.FILL_HORIZONTAL);
        sysInfoComp.setLayoutData(sysGridData);
        loadSystemInfoComposite(sysInfoComp);

        scrolledComp.setContent(composite);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);
        scrolledComp.setMinHeight(800);
        scrolledComp.setMinWidth(350);

        makeActions();
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
        cpuChart.setBarGroupName(Messages.hostCpuChartBarGroupName);
        cpuChart.setSeriesGroupName(Messages.hostCpuChartSeriesGroupName);
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
        iowaitChart.setBarGroupName(Messages.hostIowaitChartBartGroupName);
        iowaitChart.setSeriesGroupName(Messages.hostIowaitChartSeriesGroupName);
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
        memoryChart.setBarGroupName(Messages.hostPhysicalChartBarGroupName);
        memoryChart.setSeriesGroupName(Messages.hostPhysicalChartSeriesGroupName);
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
        dateaxis.setVisible(false);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        renderer.setURLGenerator(null);
        renderer.setBaseToolTipGenerator(null);
    }

    /**
     * Load groups including system info
     *
     * @param parent an instance of Composite
     */
    private void loadSystemInfoComposite(Composite parent) {
        final Composite infoComp = new Composite(parent, SWT.NULL);
        GridLayout cpuLayout = new GridLayout(2, false);
        infoComp.setLayout(cpuLayout);
        infoComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        createCpuInfoGroup(infoComp);
        createPhyMemGroup(infoComp);
        createIowaitGroup(infoComp);
        createSwapMemGroup(infoComp);
    }

    /**
     * Create a group including cpu info
     *
     * @param comp an instance of Composite
     */
    private void createCpuInfoGroup(Composite comp) {
        final Group cpuGrp = new Group(comp, SWT.UP);
        cpuGrp.setText(Messages.hostCpuInfoGroupName);
        cpuGrp.setLayout(new GridLayout(2, false));
        cpuGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label usercpuLbl = new Label(cpuGrp, SWT.NULL);
        usercpuLbl.setText(Messages.hostUserCpuInfoLbl);

        usercpuValue = new Label(cpuGrp, SWT.RIGHT);
        usercpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label kernelcpuLbl = new Label(cpuGrp, SWT.NULL);
        kernelcpuLbl.setText(Messages.hostKernelCpuInfoLbl);

        kernelcpuValue = new Label(cpuGrp, SWT.RIGHT);
        kernelcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label totalcpuLbl = new Label(cpuGrp, SWT.NULL);
        totalcpuLbl.setText(Messages.hostTotalCpuInfoLbl);

        totalcpuValue = new Label(cpuGrp, SWT.RIGHT);
        totalcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create a group including physical memory
     *
     * @param comp an instance of Composite
     */
    private void createPhyMemGroup(Composite comp) {
        final Group phymemGrp = new Group(comp, SWT.NULL);
        phymemGrp.setText(Messages.hostPhyInfoGroupName);
        phymemGrp.setLayout(new GridLayout(2, false));
        phymemGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label phymemTtlLbl = new Label(phymemGrp, SWT.NULL);
        phymemTtlLbl.setText(Messages.hostPhyTotalInfoLbl);

        phymemTtlValue = new Label(phymemGrp, SWT.RIGHT);
        phymemTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label phymemFreeLbl = new Label(phymemGrp, SWT.NULL);
        phymemFreeLbl.setText(Messages.hostPhyUsedInfoLbl);

        phymemUsedValue = new Label(phymemGrp, SWT.RIGHT);
        phymemUsedValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * create a Group including IO wait info.
     *
     * @param comp an instance of Composite
     */
    private void createIowaitGroup(Composite comp) {
        final Group iowaitGrp = new Group(comp, SWT.NULL);
        iowaitGrp.setText(Messages.hostIowaitInfoGroupName);
        iowaitGrp.setLayout(new GridLayout(2, false));
        iowaitGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label iowaitLbl = new Label(iowaitGrp, SWT.NULL);
        iowaitLbl.setText(Messages.hostIowaitInfoLbl);

        iowaitValue = new Label(iowaitGrp, SWT.RIGHT);
        iowaitValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(iowaitGrp, SWT.NONE);
    }

    /**
     * Create a group including the swap memory info.
     *
     * @param comp an instance of Composite
     */
    private void createSwapMemGroup(Composite comp) {
        final Group swapmemGrp = new Group(comp, SWT.NULL);
        swapmemGrp.setText(Messages.hostSwapmemInfoGroupName);
        swapmemGrp.setLayout(new GridLayout(2, false));
        swapmemGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label swapmemTtlLbl = new Label(swapmemGrp, SWT.NULL);
        swapmemTtlLbl.setText(Messages.hostSwapmemTotalLbl);

        swapmemTtlValue = new Label(swapmemGrp, SWT.RIGHT);
        swapmemTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label swapmemFreeLbl = new Label(swapmemGrp, SWT.NULL);
        swapmemFreeLbl.setText(Messages.hostSwapmemUsedLbl);

        swapmemUsedValue = new Label(swapmemGrp, SWT.RIGHT);
        swapmemUsedValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * send when CUBRID node object
     *
     * @param event the CubridNodeChangedEvent object
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        // empty
    }

    /** Disposes this view when it closed */
    public void dispose() {
        synchronized (this) {
            if (generator != null) {
                generator.removeDataUpdateListener(this);
            }

            if (historyFileHelp != null) {
                historyFileHelp.closeHistroyFile();
            }
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
     * @see com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener
     * @param map which includes all the changed value
     */
    public void performUpdate(Map<IDiagPara, String> map) {
        if (map == null) {
            resetZero();
            return;
        }
        Map<String, String> hostStatMap = new HashMap<String, String>();
        Map<String, String> brokerDiagMap = new HashMap<String, String>();
        for (Map.Entry<IDiagPara, String> entry : map.entrySet()) {
            if (entry.getKey() instanceof HostStatEnum) {
                hostStatMap.put(entry.getKey().getName(), entry.getValue());
            }
            if (entry.getKey() instanceof BrokerDiagEnum) {
                brokerDiagMap.put(entry.getKey().getName(), entry.getValue());
            }
        }
        updateHostStatValue(hostStatMap);
        // update broker
        if (brokerChartPart != null) {
            brokerChartPart.updateValueMap(brokerDiagMap);
        }
        // history
        if (recordFlag) {
            historyFileHelp.buildCountFile(typeNames);
            historyFileHelp.storageData(hostStatMap, HostStatEnum.values());
            if (brokerChartPart != null) {
                historyFileHelp.storageData(brokerDiagMap, BrokerDiagEnum.values());
            }
        } else {
            historyFileHelp.closeHistroyFile();
        }
    }

    /**
     * Updating the data relating host statistic
     *
     * @param resultMap an instance of Map
     */
    public void updateHostStatValue(Map<String, String> resultMap) {
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
            memPhyUsedMb = ((double) Long.parseLong(memPhyUsed)) / 1024.0;
            memoryChart.updateValueMap(memoryValueMap, numberFormat.format(memPhyUsedMb) + "MB");
            phymemUsedValue.setText(numberFormat.format(memPhyUsedMb));
        }
        // update info
        int totalCpuPercent = 0;
        String userVal = resultMap.get(HostStatEnum.USER.name());
        if (null != userVal) {
            usercpuValue.setText(userVal + "%");
            totalCpuPercent = Integer.parseInt(userVal);
        }
        String kernelVal = resultMap.get(HostStatEnum.KERNEL.name());
        if (null != kernelVal) {
            kernelcpuValue.setText(kernelVal + "%");
            totalCpuPercent += Integer.parseInt(kernelVal);
        }
        totalcpuValue.setText(totalCpuPercent + "%");

        String iowaitVal = resultMap.get(HostStatEnum.IOWAIT.name());
        if (null != iowaitVal) {
            iowaitValue.setText(iowaitVal + "%");
        }
        String memPhyTtl = resultMap.get(HostStatEnum.MEMPHY_TOTAL.name());
        if (null != memPhyTtl) {
            double memPhyTtlMb = ((double) Long.parseLong(memPhyTtl)) / 1024.0;
            phymemTtlValue.setText(numberFormat.format(memPhyTtlMb));

            Integer hMemPhyPercent = (int) (memPhyUsedMb / memPhyTtlMb * 10000 + 0.5);
            resultMap.put(HostStatEnum.MEMPHY_PERCENT.name(), Integer.toString(hMemPhyPercent));
            resultMap.put(HostStatEnum.MEMPHY_USED.name(), Integer.toString((int) memPhyUsedMb));
        }

        String memSwapTtl = resultMap.get(HostStatEnum.MEMSWAP_TOTAL.name());
        if (null != memSwapTtl) {
            double memSwapTtlMb = ((double) Long.parseLong(memSwapTtl)) / 1024.0;
            swapmemTtlValue.setText(numberFormat.format(memSwapTtlMb));
        }
        String memSwapUsed = resultMap.get(HostStatEnum.MEMSWAP_USED.name());
        if (null != memSwapUsed) {
            double memSwapUsedMb = ((double) Long.parseLong(memSwapUsed)) / 1024.0;
            swapmemUsedValue.setText(numberFormat.format(memSwapUsedMb));
        }
    }

    /** @param cpuSeriesKey the cpuSeriesKey to set */
    private void setCpuSeriesKey(String[] cpuSeriesKey) {
        this.cpuSeriesKey = cpuSeriesKey.clone();
        for (String key : cpuSeriesKey) {
            cpuValueMap.put(key, "0");
        }
    }

    /** @param iowaitSeriesKey the iowaitSeriesKey to set */
    private void setIowaitSeriesKey(String[] iowaitSeriesKey) {
        this.iowaitSeriesKey = iowaitSeriesKey.clone();
        for (String key : iowaitSeriesKey) {
            iowaitValueMap.put(key, "0");
        }
    }

    /** @param memorySeriesKey the memorySeriesKey to set */
    private void setMemorySeriesKey(String[] memorySeriesKey) {
        this.memorySeriesKey = memorySeriesKey.clone();
        for (String key : memorySeriesKey) {
            memoryValueMap.put(key, "0");
        }
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

    /**
     * Perform the update data
     *
     * @param dataChangedEvent the given event including newest data
     */
    public void performUpdate(DataChangedEvent dataChangedEvent) {
        if (composite == null || composite.isDisposed()) {
            return;
        }
        Set<MondashDataResult> set = dataChangedEvent.getResultSet();
        if (set == null || set.isEmpty()) {
            resetZero();
            return;
        }
        for (MondashDataResult result : set) {
            if (generator.getName().equals(result.getName())) {
                performUpdate(result.getUpdateMap());
            }
        }
    }

    public HANode getModel() {
        return hostNode;
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

        final IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(settingAction);

        RecordAction recordAction = new RecordAction();
        recordAction.setRecorder(this);
        recordAction.setPrepareTooltip(Messages.hostDashboardMonitorStartRecordTooltip);
        recordAction.setRecordTooltip(Messages.hostDashboardRecordingTooltip);

        recordAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
        recordAction.setToolTipText(Messages.hostDashboardMonitorStartRecordTooltip);
        manager.add(recordAction);
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
            isChangedHistoryPath = historyPath.equals(newHistoryPath) ? false : true;
            if (isChangedHistoryPath) {
                historyPath = newHistoryPath;
                historyFileHelp.setChangedHistoryPath(true);
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

        if (brokerComp != null) {
            boolean isBrokerVisible = brokerComp.isVisible();
            ChartShowingProp brokerShowingProp = new ChartShowingProp();
            brokerShowingProp.setName(Messages.hostSelectedChartBroker);
            brokerShowingProp.setShowing(isBrokerVisible);
            chartSelectionLst.add(brokerShowingProp);
        }
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
        if (list.get(4) != null) {
            if (list.get(4).isShowing()) {
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
        }
        chartComp.layout();
        sysInfoComp.layout();
        composite.layout();
    }

    /** Set the zero */
    private void resetZero() {
        for (String key : cpuSeriesKey) {
            cpuValueMap.put(key, "0");
        }
        for (String key : iowaitSeriesKey) {
            iowaitValueMap.put(key, "0");
        }
        for (String key : memorySeriesKey) {
            memoryValueMap.put(key, "0");
        }
        cpuChart.updateValueMap(cpuValueMap);
        memoryChart.updateValueMap(memoryValueMap);
        memoryChart.updateValueMap(memoryValueMap, 0 + "GB");
        iowaitChart.updateValueMap(iowaitValueMap);

        usercpuValue.setText("0");
        kernelcpuValue.setText("0");
        totalcpuValue.setText("0");
        phymemTtlValue.setText("0");
        phymemUsedValue.setText("0");
        iowaitValue.setText("0");
        swapmemTtlValue.setText("0");
        swapmemUsedValue.setText("0");
    }

    /**
     * Get the record flag.
     *
     * @return boolean
     */
    public boolean getRecordFlag() {
        return recordFlag;
    }

    /**
     * Set the record flag
     *
     * @param recordFlag the record state
     */
    public void setRecordFlag(boolean recordFlag) {
        this.recordFlag = recordFlag;
    }
}
