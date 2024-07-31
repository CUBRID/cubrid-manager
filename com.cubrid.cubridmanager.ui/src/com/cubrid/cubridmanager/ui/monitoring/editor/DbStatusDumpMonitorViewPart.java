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
package com.cubrid.cubridmanager.ui.monitoring.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.DbComboContribution;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.MonitorType;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.RecordAction;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.Recordable;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSetting;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSettingMatching;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.StatusMonInstanceData;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * A editor part is used to view database status dump monitor.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-23 created by lizhiqiang
 */
public class DbStatusDumpMonitorViewPart extends CubridViewPart implements Recordable {
    private static final Logger LOGGER = LogUtil.getLogger(DbStatusDumpMonitorViewPart.class);
    public static final String ID = DbStatusDumpMonitorViewPart.class.getName();
    private Composite composite;
    private boolean runflag = true;
    private int startRun = 0;

    private static DbStatDumpData diagOldOneStatusResult = new DbStatDumpData();
    private static DbStatDumpData diagOldTwoStatusResult = new DbStatDumpData();
    private DbStatDumpData diagStatusResult = new DbStatDumpData();
    private Calendar lastSec;
    private Calendar nowSec;
    private long monitorTimes;
    private ChartCompositePart chartPart;
    private String dbName;
    private boolean interruptSameDbReq;
    private DbComboContribution dbCombo;
    private StatusMonInstanceData monInstaceData;
    private boolean recordFlag;
    private ServerInfo serverInfo;

    /**
     * @param site IViewSite the view site
     * @throws PartInitException if this view was not initialized successfully
     * @see com.cubrid.common.ui.spi.part.CubridViewPart#init(org.eclipse.ui.IViewSite)
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        initSection();
    }

    /** Initializes the page */
    private void initSection() {
        ICubridNode selection = getCubridNode();
        monInstaceData = (StatusMonInstanceData) selection.getAdapter(StatusMonInstanceData.class);
    }

    /**
     * Creates the SWT controls for this workbench part.
     *
     * @param parent the parent control
     * @see IWorkbenchPart
     */
    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.RESIZE);
        composite.setLayout(new FillLayout());

        serverInfo = cubridNode.getServer().getServerInfo();
        List<String> databaseLst = new ArrayList<String>();
        List<DatabaseInfo> databaseInfoLst = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
        for (DatabaseInfo databaseInfo : databaseInfoLst) {
            DbRunningType dbRunningType = databaseInfo.getRunningType();
            if (dbRunningType == DbRunningType.CS) {
                databaseLst.add(databaseInfo.getDbName());
            }
        }
        DbStatDumpData dbStatDumpData = new DbStatDumpData();
        TreeMap<String, String> map = convertMapKey(dbStatDumpData.getDiagStatusResultMap());

        chartPart = new ChartCompositePart(composite, map);
        dbCombo = new DbComboContribution("database", this);
        dbCombo.setDatabaseLst(databaseLst);
        if (databaseLst.isEmpty()) {
            runflag = false;
        } else {
            dbCombo.setSelectedDb(databaseLst.get(0));
        }
        String hostAddress = serverInfo.getHostAddress();
        int monPort = serverInfo.getHostMonPort();
        String dbName = dbCombo.getSelectedDb();
        String historyFileName =
                HistoryComposite.DB_HISTORY_FILE_PREFIX
                        + dbName
                        + "@"
                        + hostAddress
                        + "_"
                        + monPort
                        + HistoryComposite.HISTORY_SUFFIX;
        chartPart.setHistoryFileName(historyFileName);
        if (null == monInstaceData) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                ShowSetting showSetting = chartPart.getSettingMap().get(key);
                ShowSettingMatching.match(key, showSetting, MonitorType.DATABASE);
            }
            chartPart.setChartTitle(Messages.dbMonitorChartTtl);
            IPath historyPath = CubridManagerCorePlugin.getDefault().getStateLocation();
            String sHistoryPath = historyPath.toOSString() + File.separator + historyFileName;
            chartPart.setHistoryPath(sHistoryPath);
        } else {
            String titleName = monInstaceData.getTitleName();
            chartPart.setChartTitle(titleName);
            chartPart.setSettingData(monInstaceData);
        }

        chartPart.loadContent();
        makeActions();
        this.dbName = dbCombo.getSelectedDb();
        new DataGenerator().start();
    }

    /**
     * send when CUBRID node object
     *
     * @param event the CubridNodeChangedEvent object
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        // empty
    }

    /**
     * Get update value
     *
     * @param startRun int
     * @return Map<String, String>
     */
    private TreeMap<String, String> getUpdateValue(int startRun) {
        ServerInfo serverInfo = cubridNode.getServer().getServerInfo();
        String dbName = dbCombo.getSelectedDb();

        final CommonQueryTask<DbStatDumpData> task =
                new CommonQueryTask<DbStatDumpData>(
                        serverInfo, CommonSendMsg.getCommonDatabaseSendMsg(), diagStatusResult);
        task.setDbName(dbName);
        task.execute();

        TreeMap<String, String> resultMap = null;
        float inter = 0.0f;

        if (startRun == 0) {
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptSameDbReq) {
                    if (this.dbName == null || this.dbName.equals(dbName)) {
                        return convertMapKey(diagStatusResult.getDiagStatusResultMap());
                    } else {
                        interruptSameDbReq = false;
                    }
                } else {
                    showErrorMsg(diagStatusResult, dbName);
                }
            }
            return convertMapKey(diagStatusResult.getDiagStatusResultMap());
        } else if (startRun == 1) {
            lastSec = Calendar.getInstance();
            diagOldOneStatusResult.copy_from(diagStatusResult);
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptSameDbReq) {
                    if (this.dbName == null || this.dbName.equals(dbName)) {
                        return convertMapKey(diagStatusResult.getDiagStatusResultMap());
                    } else {
                        interruptSameDbReq = false;
                    }
                } else {
                    showErrorMsg(diagStatusResult, dbName);
                }
            }
            DbStatDumpData brokerDiagDataDelta = new DbStatDumpData();
            brokerDiagDataDelta.getDelta(diagStatusResult, diagOldOneStatusResult);

            return convertMapKey(brokerDiagDataDelta.getDiagStatusResultMap());
        } else {
            nowSec = Calendar.getInstance();
            double interval =
                    (double) (nowSec.getTimeInMillis() - lastSec.getTimeInMillis()) / 1000;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
            inter = Float.parseFloat(nf.format(interval));

            lastSec = nowSec;

            diagOldTwoStatusResult.copy_from(diagOldOneStatusResult);
            diagOldOneStatusResult.copy_from(diagStatusResult);
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptSameDbReq) {
                    if (this.dbName == null || this.dbName.equals(dbName)) {
                        return convertMapKey(diagStatusResult.getDiagStatusResultMap());
                    } else {
                        interruptSameDbReq = false;
                    }
                } else {
                    showErrorMsg(diagStatusResult, dbName);
                }
            }
            DbStatDumpData diagStatusResultDelta = new DbStatDumpData();
            diagStatusResultDelta.getDelta(
                    diagStatusResult, diagOldOneStatusResult, diagOldTwoStatusResult, inter);

            resultMap = convertMapKey(diagStatusResultDelta.getDiagStatusResultMap());
        }
        return resultMap;
    }

    /**
     * Update the history path
     *
     * @param selectedDb a string representative of selected database name
     */
    public void updateHistoryPath(String selectedDb) {

        if (serverInfo != null) {
            String hostAddress = serverInfo.getHostAddress();
            int monPort = serverInfo.getHostMonPort();
            String historyFileName =
                    HistoryComposite.DB_HISTORY_FILE_PREFIX
                            + selectedDb
                            + "@"
                            + hostAddress
                            + "_"
                            + monPort
                            + HistoryComposite.HISTORY_SUFFIX;
            chartPart.setHistoryFileName(historyFileName);
            String historyPath = chartPart.getHistoryPath();
            int lastSeparatorIndex = historyPath.lastIndexOf(File.separator);
            String prePath = historyPath.substring(0, lastSeparatorIndex);
            historyPath = prePath + File.separator + historyFileName;
            chartPart.setHistoryPath(historyPath);
            chartPart.setChangedHistoryPath(true);
        }
    }

    /**
     * Show error message When the response' status is not success
     *
     * @param diagStatusResult the instance of DbStatDumpData
     * @param dbName the database name
     */
    private void showErrorMsg(final DbStatDumpData diagStatusResult, String dbName) {
        interruptSameDbReq = true;
        this.dbName = dbName;
        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(diagStatusResult.getNote());
                            }
                        });
    }

    /**
     * Update the data of chart
     *
     * @param resultMap Map<String, String>
     */
    private void update(TreeMap<String, String> resultMap) {
        chartPart.updateValueMap(resultMap);
        monitorTimes += 1;
    }

    /**
     * A inner class that update the data of chart in a single thread
     *
     * @author lizhiqiang
     * @version 1.0 - 2009-6-4 created by lizhiqiang
     */
    class DataGenerator extends Thread {
        private TreeMap<String, String> updateMap;

        /** Thread run method */
        @Override
        public void run() {
            while (runflag) {
                updateMap = getUpdateValue(startRun);
                if (interruptSameDbReq) {
                    continue;
                }
                if (startRun <= 1) {
                    startRun++;
                } else {
                    Display.getDefault()
                            .syncExec(
                                    new Runnable() {
                                        public void run() {
                                            if (composite != null && !composite.isDisposed()) {
                                                update(updateMap);
                                            }
                                        }
                                    });
                    if (recordFlag) {
                        chartPart.storageData(updateMap, DbStatDumpEnum.values(), dbName);
                    } else {
                        chartPart.closeHistroyFile();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /** Disposes this view when it closed */
    public void dispose() {
        synchronized (this) {
            runflag = false;
            chartPart.closeHistroyFile();
            super.dispose();
        }
    }

    /** @param startRun the startRun to set */
    public void setStartRun(int startRun) {
        this.startRun = startRun;
    }

    /** @param dbName the dbName to set */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /** This method is to create actions at tool bar */
    private void makeActions() {
        Action settingAction =
                new Action() {
                    public void run() {
                        chartPart.fireChartSetting();
                    }
                };
        settingAction.setText(Messages.chartSettingTxt);
        settingAction.setToolTipText(Messages.chartSettingTxt);
        settingAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/action/setting-small.png"));

        final IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(dbCombo);
        manager.add(new Separator());
        manager.add(settingAction);

        RecordAction recordAction = new RecordAction();
        recordAction.setRecorder(this);
        recordAction.setPrepareTooltip(Messages.dbMonitorStartRecordTooltip);
        recordAction.setRecordTooltip(Messages.dbMonitorRecordingTooltip);

        recordAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
        recordAction.setToolTipText(Messages.dbMonitorStartRecordTooltip);
        manager.add(recordAction);
    }

    /**
     * Get the chart part.
     *
     * @return the chartPart
     */
    public ChartCompositePart getChartPart() {
        return chartPart;
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
