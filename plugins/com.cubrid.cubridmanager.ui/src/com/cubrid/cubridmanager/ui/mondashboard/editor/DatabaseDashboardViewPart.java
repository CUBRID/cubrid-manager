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

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.DbProcessStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpEnum;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.core.monitoring.model.StandbyServerStatEnum;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataChangedEvent;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGenerator;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.MondashDataResult;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
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
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
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
 * @version 1.0 - 2010-6-6 created by lizhiqiang
 */
public class DatabaseDashboardViewPart extends ViewPart implements DataUpdateListener, Recordable {

    public static final String ID = DatabaseDashboardViewPart.class.getName();

    private String[] cpuSeriesKey;
    private String[] memorySeriesKey;
    private String[] countSeriesKey;
    private String[] delaySeriesKey;
    private TreeMap<String, String> cpuValueMap;
    private TreeMap<String, String> countValueMap;
    private TreeMap<String, String> memoryValueMap;
    private TreeMap<String, String> delayValueMap;

    private CombinedBarTimeSeriesChart cpuChart;
    private CombinedBarTimeSeriesChart memoryChart;
    private CombinedBarTimeSeriesChart delayChart;
    private CombinedBarTimeSeriesChart countChart;

    private Label usercpuValue;
    private Label kernelcpuValue;
    private Label phymemValue;
    private Label virmemValue;
    private Label delayValue;
    private Label failedTtlValue;
    private Label commitTtlValue;
    private Label insertTtlValue;
    private Label updateTtlValue;
    private Label deleteTtlValue;

    private Composite composite;
    private ChartCompositePart dbStatChartPart;

    private DataGenerator generator;
    private DatabaseNode dbNode;

    private Label applyLogValue;
    private Label copyLogValue;
    private Label dbServerStatusValue;

    private boolean recordFlag;
    private String historyPath;
    private String historyFileName;
    private boolean isChangedHistoryPath;

    private HistoryFileHelp historyFileHelp;

    private String[] typeNames;

    private Composite dbStatComp;

    private Composite chartComp;

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
        countValueMap = new TreeMap<String, String>();
        memoryValueMap = new TreeMap<String, String>();
        delayValueMap = new TreeMap<String, String>();
        setCpuSeriesKey(
                new String[] {
                    DbProcStatEnum.USER_PERCENT.name(), DbProcStatEnum.KERNEL_PERCENT.name()
                });
        setMemorySeriesKey(new String[] {DbProcStatEnum.MEMPHY_PERCENT.name()});
        setCountSeriesKey(new String[] {StandbyServerStatEnum.COMMIT_COUNTER.name()});
        setDelaySeriesKey(new String[] {StandbyServerStatEnum.DELAY_TIME.name()});
    }

    /**
     * Initializes
     *
     * @param dbNode The DatabaseNode
     */
    public void init(DatabaseNode dbNode) {
        this.dbNode = dbNode;
        HostNode hostNode = dbNode.getParent();
        String partName = getPartName();
        String suffix =
                " - " + dbNode.getDbName() + "@" + hostNode.getIp() + ":" + hostNode.getPort();
        if (!partName.endsWith(suffix)) {
            setPartName(partName + suffix);
        }

        ServerInfo serverInfo = hostNode.getServerInfo();
        if (serverInfo != null) {
            String hostAddress = serverInfo.getHostAddress();
            int monPort = serverInfo.getHostMonPort();
            historyFileName =
                    HistoryComposite.DBDASHBOARD_HISTORY_FILE_PREFIX
                            + dbNode.getDbName()
                            + "@"
                            + hostAddress
                            + "_"
                            + monPort
                            + HistoryComposite.HISTORY_SUFFIX;
            IPath histPath = CubridManagerCorePlugin.getDefault().getStateLocation();
            historyPath = histPath.toOSString() + File.separator + historyFileName;
            historyFileHelp = new HistoryFileHelp();
            historyFileHelp.setHistoryPath(historyPath);
        }
        List<String> typeLst = new ArrayList<String>();
        for (DbProcStatEnum dbProcEnum : DbProcStatEnum.values()) {
            typeLst.add(dbProcEnum.getName());
        }
        for (StandbyServerStatEnum standbyEnum : StandbyServerStatEnum.values()) {
            typeLst.add(standbyEnum.getName());
        }
        for (DbStatDumpEnum dbDumpEnum : DbStatDumpEnum.values()) {
            typeLst.add(dbDumpEnum.getName());
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

        composite = new Composite(scrolledComp, SWT.RESIZE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        chartComp = new Composite(composite, SWT.RESIZE);
        GridLayout chartCompLayout = new GridLayout(1, true);
        chartComp.setLayout(chartCompLayout);
        chartComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        loadCpuChart(chartComp);
        loadMemoryChart(chartComp);
        loadCountChart(chartComp);
        loadDelayChart(chartComp);
        loadDatabaseChart(chartComp);

        sysInfoComp = new Composite(composite, SWT.RESIZE);
        GridLayout sysInfoCompLayout = new GridLayout();
        sysInfoComp.setLayout(sysInfoCompLayout);
        sysInfoComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        loadSystemInfoComposite(sysInfoComp);

        scrolledComp.setContent(composite);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);
        scrolledComp.setMinHeight(800);
        scrolledComp.setMinWidth(545);

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
        cpuChart.setBarGroupName(Messages.dbCpuChartBarGroupName);
        cpuChart.setSeriesGroupName(Messages.dbCpuChartSeriesGroupName);
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
        memoryChart.setBarGroupName(Messages.dbPhysicalChartBarGroupName);
        memoryChart.setSeriesGroupName(Messages.dbPhysicalChartSeriesGroupName);
        memoryChart.load(parent);
    }

    /**
     * Load an instance of CombinedBarTimeSeriesChart stand for IO wait
     *
     * @param parent an instance of Composite
     */
    private void loadCountChart(Composite parent) {
        countChart = new CombinedBarTimeSeriesChart();
        countChart.setValueMap(countValueMap);
        countChart.setBarGroupName(Messages.dbCountChartBarGroupName);
        countChart.setSeriesGroupName(Messages.dbCountChartSeriesGroupName);
        countChart.load(parent);
    }

    /**
     * * Load an instance of CombinedBarTimeSeriesChart stand for IO wait
     *
     * @param parent an instance of Composite
     */
    private void loadDelayChart(Composite parent) {
        delayChart = new CombinedBarTimeSeriesChart();
        delayChart.setValueMap(delayValueMap);
        delayChart.setBarGroupName(Messages.dbDelayChartBarGroupName);
        delayChart.setSeriesGroupName(Messages.dbDelayChartSeriesGroupName);
        delayChart.load(parent);
    }

    /**
     * Load an instance of ChartCompositePart stand for database monitor info
     *
     * @param parent an instance of Composite
     */
    private void loadDatabaseChart(Composite parent) {
        dbStatComp = new Composite(parent, SWT.NULL);
        dbStatComp.setLayout(new GridLayout());
        dbStatComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        Group dbGrp = new Group(dbStatComp, SWT.NONE);
        dbGrp.setText(Messages.dbDatabaseStatusSeriesGroupName);
        GridLayout layoutGrp = new GridLayout();
        layoutGrp.verticalSpacing = 0;
        layoutGrp.horizontalSpacing = 0;
        layoutGrp.marginLeft = 0;
        layoutGrp.marginRight = 0;
        layoutGrp.marginTop = 0;
        layoutGrp.marginBottom = 0;
        dbGrp.setLayout(layoutGrp);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        dbGrp.setLayoutData(gridData);

        DbStatDumpData dbStatDumpData = new DbStatDumpData();
        TreeMap<String, String> map = convertMapKey(dbStatDumpData.getDiagStatusResultMap());
        dbStatChartPart = new ChartCompositePart(dbGrp, map);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            ShowSetting showSetting = dbStatChartPart.getSettingMap().get(key);
            ShowSettingMatching.match(key, showSetting, MonitorType.DATABASE);
        }
        dbStatChartPart.loadContent();
        JFreeChart chart = (JFreeChart) dbStatChartPart.getChart();
        chart.setBorderVisible(false);

        XYPlot xyplot = (XYPlot) dbStatChartPart.getChart().getPlot();
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
        Composite comp = new Composite(parent, SWT.TOP);
        GridLayout layout = new GridLayout(2, true);
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite subCompOne = new Composite(comp, SWT.NULL);
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        subCompOne.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        subCompOne.setLayoutData(gridData);

        createCpuInfoGroup(subCompOne);
        createMemGroup(subCompOne);
        createDelayGroup(subCompOne);

        Composite subCompTwo = new Composite(comp, SWT.TOP);
        subCompTwo.setLayout(new GridLayout());
        subCompTwo.setLayoutData(new GridData(GridData.FILL_BOTH));

        crateCountGroup(subCompTwo);
        createLogStateGroup(subCompTwo);
    }

    /**
     * Create a group including cpu info
     *
     * @param comp an instance of Composite
     */
    private void createCpuInfoGroup(Composite comp) {
        Group cpuGrp = new Group(comp, SWT.UP);
        cpuGrp.setText(Messages.dbCpuInfoGroupName);
        cpuGrp.setLayout(new GridLayout(2, false));
        cpuGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label usercpuLbl = new Label(cpuGrp, SWT.NULL);
        usercpuLbl.setText(Messages.dbUserCpuInfoLbl);

        usercpuValue = new Label(cpuGrp, SWT.RIGHT);
        usercpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label kernelcpuLbl = new Label(cpuGrp, SWT.NULL);
        kernelcpuLbl.setText(Messages.dbKernelCpuInfoLbl);

        kernelcpuValue = new Label(cpuGrp, SWT.RIGHT);
        kernelcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label totalcpuLbl = new Label(cpuGrp, SWT.NULL);
        totalcpuLbl.setText(Messages.dbTotalSysCpuInfoLbl);

        totalcpuValue = new Label(cpuGrp, SWT.RIGHT);
        totalcpuValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create a group including physical memory
     *
     * @param comp an instance of Composite
     */
    private void createMemGroup(Composite comp) {
        Group memGrp = new Group(comp, SWT.NULL);
        memGrp.setText(Messages.dbMemInfoGroupName);
        memGrp.setLayout(new GridLayout(2, false));
        memGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label phymemLbl = new Label(memGrp, SWT.NULL);
        phymemLbl.setText(Messages.dbMemPhyInfoLbl);

        phymemValue = new Label(memGrp, SWT.RIGHT);
        phymemValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label virmemLbl = new Label(memGrp, SWT.NULL);
        virmemLbl.setText(Messages.dbMemVirtualInfoLbl);

        virmemValue = new Label(memGrp, SWT.RIGHT);
        virmemValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * create a Group including IO wait info.
     *
     * @param comp an instance of Composite
     */
    private void createDelayGroup(Composite comp) {
        Group delayGrp = new Group(comp, SWT.NULL);
        delayGrp.setText(Messages.dbDelayInfoGroupName);
        delayGrp.setLayout(new GridLayout(2, false));
        delayGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label delayLbl = new Label(delayGrp, SWT.NULL);
        delayLbl.setText(Messages.dbDelayInfoLbl);

        delayValue = new Label(delayGrp, SWT.RIGHT);
        delayValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create a group including the swap memory info.
     *
     * @param comp an instance of Composite
     */
    private void crateCountGroup(Composite comp) {
        Group countGrp = new Group(comp, SWT.TOP);
        countGrp.setText(Messages.dbCountInfoGroupName);
        countGrp.setLayout(new GridLayout(2, false));
        countGrp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label failedTtlLbl = new Label(countGrp, SWT.NULL);
        failedTtlLbl.setText(Messages.dbFailedInfoLbl);

        failedTtlValue = new Label(countGrp, SWT.RIGHT);
        failedTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label commitTtlLbl = new Label(countGrp, SWT.NULL);
        commitTtlLbl.setText(Messages.dbCommintInfoLbl);

        commitTtlValue = new Label(countGrp, SWT.RIGHT);
        commitTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label insertTtlLbl = new Label(countGrp, SWT.NULL);
        insertTtlLbl.setText(Messages.dbInsertInfoLbl);

        insertTtlValue = new Label(countGrp, SWT.RIGHT);
        insertTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label updateTtlLbl = new Label(countGrp, SWT.NULL);
        updateTtlLbl.setText(Messages.dbUpdateInfoLbl);

        updateTtlValue = new Label(countGrp, SWT.RIGHT);
        updateTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label deleteTtlLbl = new Label(countGrp, SWT.NULL);
        deleteTtlLbl.setText(Messages.dbDeleteInfoLbl);

        deleteTtlValue = new Label(countGrp, SWT.RIGHT);
        deleteTtlValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create log state group
     *
     * @param comp an instance of Composite
     */
    private void createLogStateGroup(Composite comp) {
        Group logStateGrp = new Group(comp, SWT.TOP);
        logStateGrp.setText(Messages.dbProcessStateInfoGroupName);
        logStateGrp.setLayout(new GridLayout(2, false));
        logStateGrp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label dbServerLbl = new Label(logStateGrp, SWT.NULL);
        dbServerLbl.setText(Messages.dbServerProcessLbl);

        dbServerStatusValue = new Label(logStateGrp, SWT.RIGHT);
        dbServerStatusValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label applyLogLbl = new Label(logStateGrp, SWT.NULL);
        applyLogLbl.setText(Messages.dbApplyLogDbInfoLbl);

        applyLogValue = new Label(logStateGrp, SWT.RIGHT);
        applyLogValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label copyLogLbl = new Label(logStateGrp, SWT.NULL);
        copyLogLbl.setText(Messages.dbCopyLogDbInfoLbl);

        copyLogValue = new Label(logStateGrp, SWT.RIGHT);
        copyLogValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
            generator.removeDataUpdateListener(this);
            historyFileHelp.closeHistroyFile();
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

    /** @param countSeriesKey the countSeriesKey to set */
    public void setCountSeriesKey(String[] countSeriesKey) {
        this.countSeriesKey = countSeriesKey.clone();
        for (String key : countSeriesKey) {
            countValueMap.put(key, "0");
        }
    }

    /** @param delaySeriesKey the delaySeriesKey to set */
    public void setDelaySeriesKey(String[] delaySeriesKey) {
        this.delaySeriesKey = delaySeriesKey.clone();
        for (String key : delaySeriesKey) {
            delayValueMap.put(key, "0");
        }
    }

    /**
     * Perform updating the dynamic data in this view part
     *
     * @param map an instance of Map
     * @param hostCpuTotal String
     * @param hostMemTotal String
     */
    private void performUpdate(
            Map<IDiagPara, String> map, String hostCpuTotal, String hostMemTotal) {
        if (map == null) {
            resetZero();
            return;
        }
        Map<String, String> dbProcStatMap = new HashMap<String, String>();
        Map<String, String> standbyServerStatMap = new HashMap<String, String>();
        Map<String, String> dbStatDumpMap = new HashMap<String, String>();
        for (Map.Entry<IDiagPara, String> entry : map.entrySet()) {
            if (entry.getKey() instanceof DbProcStatEnum) {
                dbProcStatMap.put(entry.getKey().getName(), entry.getValue());
            }
            if (entry.getKey() instanceof StandbyServerStatEnum) {
                standbyServerStatMap.put(entry.getKey().getName(), entry.getValue());
            }
            if (entry.getKey() instanceof DbStatDumpEnum) {
                dbStatDumpMap.put(entry.getKey().getName(), entry.getValue());
            }
        }
        if (dbProcStatMap.isEmpty()) {
            resetZeroForProc();
        } else {
            updateDbProcValue(dbProcStatMap, hostCpuTotal, hostMemTotal);
        }

        if (standbyServerStatMap.isEmpty()) {
            resetZeroForCount();
        } else {
            updateStandbyStat(standbyServerStatMap);
        }

        // update broker
        dbStatChartPart.updateValueMap(dbStatDumpMap);
        // update process state
        updateProcessStat();
        // history
        if (recordFlag) {
            historyFileHelp.buildCountFile(typeNames);
            historyFileHelp.storageData(dbProcStatMap, DbProcStatEnum.values());
            historyFileHelp.storageData(standbyServerStatMap, StandbyServerStatEnum.values());
            historyFileHelp.storageData(dbStatDumpMap, DbStatDumpEnum.values());

        } else {
            historyFileHelp.closeHistroyFile();
        }
    }

    /** Update process statistic value */
    private void updateProcessStat() {
        if (dbNode.getHaDatabaseStatus() == null
                || dbNode.getHaDatabaseStatus().getDbServerProcessStatus() == null) {
            dbServerStatusValue.setText("");
        } else {
            String str =
                    dbNode.getHaDatabaseStatus()
                            .getDbServerProcessStatus()
                            .getProcessStatus()
                            .getText();
            dbServerStatusValue.setText(str);
        }

        DbProcessStatusInfo copyLogDbProcessStatusInfo =
                HAUtil.getActiveCopyLogDbProcessStatusInfo(dbNode);
        if (copyLogDbProcessStatusInfo == null) {
            copyLogValue.setText("");
        } else {
            copyLogValue.setText(copyLogDbProcessStatusInfo.getProcessStatus().getText());
        }

        DbProcessStatusInfo applyLogDbProcessStatusInfo =
                HAUtil.getActiveApplyLogDbProcessStatusInfo(dbNode);
        if (applyLogDbProcessStatusInfo == null) {
            applyLogValue.setText("");
        } else {
            applyLogValue.setText(applyLogDbProcessStatusInfo.getProcessStatus().getText());
        }
    }

    /**
     * Updating the data of standby statistic
     *
     * @param resultMap an instance of Map
     */
    private void updateStandbyStat(Map<String, String> resultMap) {
        if (resultMap == null) {
            resetZero();
            return;
        }
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            for (String key : countSeriesKey) {
                if (key.equals(entry.getKey())) {
                    countValueMap.put(key, entry.getValue());
                }
            }

            for (String key : delaySeriesKey) {
                if (key.equals(entry.getKey())) {
                    delayValueMap.put(key, entry.getValue());
                }
            }
        }

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(3);
        String delayTime = resultMap.get(StandbyServerStatEnum.DELAY_TIME.name());
        if (delayTime != null) {
            String delayTimeFormat = numberFormat.format(Long.parseLong(delayTime));
            delayValue.setText(delayTimeFormat);
            delayChart.updateValueMap(delayValueMap, delayTimeFormat);
        }

        String failCounter = resultMap.get(StandbyServerStatEnum.FAIL_COUNTER.name());
        if (failCounter != null) {
            failedTtlValue.setText(numberFormat.format(Long.parseLong(failCounter)));
        }
        String commitCounter = resultMap.get(StandbyServerStatEnum.COMMIT_COUNTER.name());
        if (commitCounter != null) {
            String commitCounterFormat = numberFormat.format(Long.parseLong(commitCounter));
            commitTtlValue.setText(commitCounterFormat);
            countChart.updateValueMap(countValueMap, commitCounterFormat);
        }
        String insertCounter = resultMap.get(StandbyServerStatEnum.INSERT_COUNTER.name());
        if (insertCounter != null) {
            insertTtlValue.setText(numberFormat.format(Long.parseLong(insertCounter)));
        }
        String updateCounter = resultMap.get(StandbyServerStatEnum.UPDATE_COUNTER.name());
        if (updateCounter != null) {
            updateTtlValue.setText(numberFormat.format(Long.parseLong(updateCounter)));
        }
        String deleteCounter = resultMap.get(StandbyServerStatEnum.DELETE_COUNTER.name());
        if (deleteCounter != null) {
            deleteTtlValue.setText(numberFormat.format(Long.parseLong(deleteCounter)));
        }
    }

    /**
     * Updating the data of dbProc
     *
     * @param dbProcStatMap an instance of Map
     * @param hostCpuTotalStr String
     * @param hostMemTotalStr String
     */
    private void updateDbProcValue(
            Map<String, String> dbProcStatMap, String hostCpuTotalStr, String hostMemTotalStr) {
        String deltaCpuUser = dbProcStatMap.get(DbProcStatEnum.DELTA_USER.name());
        String deltaCpuKernel = dbProcStatMap.get(DbProcStatEnum.DELTA_KERNEL.name());
        Long deltaCpuUserLong = Long.parseLong(deltaCpuUser == null ? "0" : deltaCpuUser);
        Long dletaCpuKernelLong = Long.parseLong(deltaCpuKernel == null ? "0" : deltaCpuKernel);
        double hostCpuTotal = Long.parseLong(hostCpuTotalStr);
        int userPercent = 0;
        int kernelPercent = 0;
        if (!"0".equals(hostCpuTotalStr)) {
            userPercent = (int) (deltaCpuUserLong / hostCpuTotal * 100 + 0.5);
            kernelPercent = (int) (dletaCpuKernelLong / hostCpuTotal * 100 + 0.5);
            dbProcStatMap.put(DbProcStatEnum.USER_PERCENT.name(), Integer.toString(userPercent));
            dbProcStatMap.put(
                    DbProcStatEnum.KERNEL_PERCENT.name(), Integer.toString(kernelPercent));
        }

        String memPhyUsed = dbProcStatMap.get(DbProcStatEnum.MEM_PHYSICAL.name());
        memPhyUsed = memPhyUsed == null ? "0" : memPhyUsed;
        double memPhyUsedLong = Long.parseLong(memPhyUsed);
        double hostMemTotal = Long.parseLong(hostMemTotalStr);

        int memPhyPercent = 0;
        if (!"0".equals(hostMemTotalStr)) {
            Integer hMemPhyPercent = (int) (memPhyUsedLong / hostMemTotal * 10000 + 0.5);
            dbProcStatMap.put(
                    DbProcStatEnum.MEMPHY_PERCENT.name(), Integer.toString(hMemPhyPercent));
            memPhyPercent = (int) (memPhyUsedLong / hostMemTotal * 100 + 0.5);
        }

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

        double memPhyUsedMb = Long.parseLong(memPhyUsed) / 1024.0;
        // reset memory physical in dbProcStatMap
        dbProcStatMap.put(
                DbProcStatEnum.MEM_PHYSICAL.name(), Integer.toString((int) (memPhyUsedMb + 0.5)));
        memoryChart.updateValueMap(memoryValueMap, numberFormat.format(memPhyUsedMb) + "MB");
        phymemValue.setText(numberFormat.format(memPhyUsedMb));
        // update info
        usercpuValue.setText(userPercent + "%");
        kernelcpuValue.setText(kernelPercent + "%");
        int totalCpuPercent = userPercent + kernelPercent;
        totalcpuValue.setText(totalCpuPercent + "%");

        String memVirtual = dbProcStatMap.get(DbProcStatEnum.MEM_VIRTUAL.name());
        if (null != memVirtual) {
            double memVirUsedMb = Long.parseLong(memPhyUsed) / 1024.0;
            virmemValue.setText(numberFormat.format(memVirUsedMb));
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
        if (dbNode.getParent().isConnected()) {
            HADatabaseStatusInfo haDbStatusInfo =
                    HAUtil.getDatabaseStatusInfo(
                            dataChangedEvent.getHaHostStatusInfoList(),
                            dbNode.getParent().getIp(),
                            dbNode.getDbName());
            if (haDbStatusInfo == null) {
                haDbStatusInfo =
                        HAUtil.getDatabaseStatusInfo(
                                dataChangedEvent.getDbStatusInfoList(), dbNode.getDbName());
                if (null == haDbStatusInfo) {
                    haDbStatusInfo = new HADatabaseStatusInfo();
                    haDbStatusInfo.setDbName(dbNode.getDbName());
                }
            }
            dbNode.setHaDatabaseStatus(haDbStatusInfo);
        } else {
            HADatabaseStatusInfo haDatabaseStatus = new HADatabaseStatusInfo();
            haDatabaseStatus.setDbName(dbNode.getDbName());
            dbNode.setHaDatabaseStatus(haDatabaseStatus);
        }

        Set<MondashDataResult> set = dataChangedEvent.getResultSet();
        Map<IDiagPara, String> updateMap = null;
        String hostCpuTotal = "0";
        String hostMemTotal = "0";
        for (MondashDataResult result : set) {
            if (dbNode.getDbName().equals(result.getName())) {
                updateMap = result.getUpdateMap();
            } else if (generator.getName().equals(result.getName())) {
                Map<IDiagPara, String> hostMap = result.getUpdateMap();
                if (hostMap != null) {
                    hostCpuTotal = hostMap.get(HostStatEnum.CPU_TOTAL);
                    hostCpuTotal = hostCpuTotal == null ? "0" : hostCpuTotal;
                    hostMemTotal = hostMap.get(HostStatEnum.MEMPHY_TOTAL);
                    hostMemTotal = hostMemTotal == null ? "0" : hostMemTotal;
                }
            }
        }
        performUpdate(updateMap, hostCpuTotal, hostMemTotal);
    }

    public HANode getModel() {
        return dbNode;
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
        recordAction.setPrepareTooltip(Messages.dbDashboardMonitorStartRecordTooltip);
        recordAction.setRecordTooltip(Messages.dbDashboardRecordingTooltip);

        recordAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
        recordAction.setToolTipText(Messages.dbDashboardMonitorStartRecordTooltip);
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
        XYPlot dbStatplot = dbStatChartPart.getChart().getXYPlot();
        String plotBgColor = trimPaintColor(dbStatplot.getBackgroundPaint().toString());
        String plotDomainGridColor = trimPaintColor(dbStatplot.getDomainGridlinePaint().toString());
        String plotRangGridColor = trimPaintColor(dbStatplot.getRangeGridlinePaint().toString());

        chartSettingDlg.setPlotBgColor(plotBgColor);
        chartSettingDlg.setPlotDomainGridColor(plotDomainGridColor);
        chartSettingDlg.setPlotRangGridColor(plotRangGridColor);

        // series
        chartSettingDlg.setSettingMap(dbStatChartPart.getSettingMap());

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
            XYPlot cpuSeriesPlot = (XYPlot) cpuChart.getSeriesChart().getPlot();

            JFreeChart memoryBarChart = memoryChart.getBarChart();
            Plot memoryBarPlot = memoryBarChart.getPlot();
            XYPlot memorySeriesPlot = (XYPlot) memoryChart.getSeriesChart().getPlot();

            JFreeChart delayBarChart = delayChart.getBarChart();
            Plot delayBarPlot = delayBarChart.getPlot();
            XYPlot delaySeriesPlot = (XYPlot) delayChart.getSeriesChart().getPlot();

            JFreeChart countBarChart = countChart.getBarChart();
            Plot countBarPlot = countBarChart.getPlot();
            XYPlot countSeriesPlot = (XYPlot) countChart.getSeriesChart().getPlot();

            // history path
            String newHistoryPath = chartSettingDlg.getHistoryPath();
            isChangedHistoryPath = historyPath.equals(newHistoryPath) ? false : true;
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
            // delayChart
            delayBarPlot.setBackgroundPaint(bgColor);
            delayBarChart.setBackgroundPaint(bgColor);
            delaySeriesPlot.setBackgroundPaint(bgColor);
            // countChart;
            countBarPlot.setBackgroundPaint(bgColor);
            countBarChart.setBackgroundPaint(bgColor);
            countSeriesPlot.setBackgroundPaint(bgColor);

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
            // delayChart
            delaySeriesPlot.setDomainGridlinePaint(domainGridlineColor);
            // countChart;
            countSeriesPlot.setDomainGridlinePaint(domainGridlineColor);
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
            // delayChart
            delaySeriesPlot.setRangeGridlinePaint(rangeGridColor);
            // countChart;
            countSeriesPlot.setRangeGridlinePaint(rangeGridColor);

            dbStatChartPart.setSettingMap(chartSettingDlg.getSettingMap());
            dbStatChartPart.updateSettingSeries();

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

        boolean isDelayVisible = delayChart.getBasicComposite().isVisible();
        ChartShowingProp delayShowingProp = new ChartShowingProp();
        delayShowingProp.setName(Messages.dbSelectedChartDelay);
        delayShowingProp.setShowing(isDelayVisible);
        chartSelectionLst.add(delayShowingProp);

        boolean isCountVisible = countChart.getBasicComposite().isVisible();
        ChartShowingProp countShowingProp = new ChartShowingProp();
        countShowingProp.setName(Messages.dbSelectedChartCount);
        countShowingProp.setShowing(isCountVisible);
        chartSelectionLst.add(countShowingProp);

        boolean isDbstatVisible = dbStatComp.isVisible();
        ChartShowingProp dbStatShowingProp = new ChartShowingProp();
        dbStatShowingProp.setName(Messages.dbSelectedChartBroker);
        dbStatShowingProp.setShowing(isDbstatVisible);
        chartSelectionLst.add(dbStatShowingProp);
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
            delayChart.getBasicComposite().setLayoutData(gridData);
            delayChart.getBasicComposite().setVisible(true);
        } else {
            final GridData gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            delayChart.getBasicComposite().setLayoutData(gridData);
            delayChart.getBasicComposite().setVisible(false);
        }
        if (list.get(4).isShowing()) {
            final GridData gridData = new GridData(GridData.FILL_BOTH);
            countChart.getBasicComposite().setLayoutData(gridData);
            countChart.getBasicComposite().setVisible(true);
        } else {
            final GridData gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            countChart.getBasicComposite().setLayoutData(gridData);
            countChart.getBasicComposite().setVisible(false);
        }
        if (list.get(5).isShowing()) {
            final GridData gridData = new GridData(GridData.FILL_BOTH);
            dbStatComp.setLayoutData(gridData);
            dbStatComp.setVisible(true);
        } else {
            final GridData gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            dbStatComp.setLayoutData(gridData);
            dbStatComp.setVisible(false);
        }
        chartComp.layout();
        sysInfoComp.layout();
        composite.layout();
    }

    /** Set the zero */
    private void resetZero() {
        resetZeroForProc();
        resetZeroForCount();
    }

    /** reset zero for proc */
    private void resetZeroForProc() {
        for (String key : cpuSeriesKey) {
            cpuValueMap.put(key, "0");
        }
        for (String key : memorySeriesKey) {
            memoryValueMap.put(key, "0");
        }

        cpuChart.updateValueMap(cpuValueMap);
        memoryChart.updateValueMap(memoryValueMap);
        memoryChart.updateValueMap(memoryValueMap, 0 + "KB");

        usercpuValue.setText("0");
        kernelcpuValue.setText("0");
        totalcpuValue.setText("0");
        phymemValue.setText("0");
        virmemValue.setText("0");
    }

    /** reset zero for count */
    private void resetZeroForCount() {
        for (String key : countSeriesKey) {
            countValueMap.put(key, "0");
        }
        for (String key : delaySeriesKey) {
            delayValueMap.put(key, "0");
        }
        delayChart.updateValueMap(delayValueMap);
        countChart.updateValueMap(countValueMap);
        delayValue.setText("0");
        failedTtlValue.setText("0");
        commitTtlValue.setText("0");
        insertTtlValue.setText("0");
        updateTtlValue.setText("0");
        deleteTtlValue.setText("0");
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
