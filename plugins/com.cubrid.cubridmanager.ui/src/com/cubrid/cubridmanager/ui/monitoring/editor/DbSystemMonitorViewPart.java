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
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStat;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HistoryFileHelp;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.DbSystemMonitorCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.RecordAction;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.Recordable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * A editor part is used to view system monitor info which.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-6 created by lizhiqiang
 */
public class DbSystemMonitorViewPart extends CubridViewPart implements Recordable {

    private static final Logger LOGGER = LogUtil.getLogger(DbSystemMonitorViewPart.class);
    public static final String ID = DbSystemMonitorViewPart.class.getName();
    private Composite composite;
    private boolean runflag = true;
    private int startRun = 0;
    private static DbProcStat dbProcOldOneStatusResult = new DbProcStat();
    private static DbProcStat dbProcOldTwoStatusResult = new DbProcStat();
    private DbProcStat dbProcStatusResult = new DbProcStat();
    private DbProcStatProxy dbProcStatProxy;

    private static HostStatData hostOldOneStatusResult = new HostStatData();
    private static HostStatData hostOldTwoStatusResult = new HostStatData();
    private HostStatData hostStatusResult = new HostStatData();
    private HostStatDataProxy hostStatDataProxy;
    private long monitorTimes;
    private boolean interruptReq;
    private String dbName = "";
    private DbSystemMonitorCompositePart compositePart;
    private DbComboContribution dbCombo;
    private static final String ALL_DB_NAME = Messages.msgAllDbNameInCombo;

    private String historyPath;
    private String historyFileName;
    private HistoryFileHelp historyFileHelp;

    private String[] typeNames;
    private ServerInfo serverInfo;
    private boolean recordFlag;

    /**
     * @param site IViewSite the view site
     * @throws PartInitException if this view was not initialized successfully
     * @see com.cubrid.common.ui.spi.part.CubridViewPart#init(org.eclipse.ui.IViewSite)
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        dbProcStatProxy = new DbProcStatProxy();
        hostStatDataProxy = new HostStatDataProxy();

        serverInfo = cubridNode.getServer().getServerInfo();
        if (serverInfo != null) {
            String hostAddress = serverInfo.getHostAddress();
            int monPort = serverInfo.getHostMonPort();
            historyFileName =
                    HistoryComposite.DB_SYSMON_HISTORY_FILE_PREFIX
                            + dbName
                            + "@"
                            + hostAddress
                            + "_"
                            + monPort
                            + HistoryComposite.HISTORY_SUFFIX;
            IPath histPath = CubridManagerCorePlugin.getDefault().getStateLocation();
            historyPath = histPath.toOSString() + File.separator + historyFileName;
            historyFileHelp = new HistoryFileHelp();
            historyFileHelp.setHistoryPath(historyPath);

            List<String> typeLst = new ArrayList<String>();
            for (DbProcStatEnum dbProcEnum : DbProcStatEnum.values()) {
                typeLst.add(dbProcEnum.getName());
            }
            typeNames = typeLst.toArray(new String[typeLst.size()]);
        }
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

        compositePart = new DbSystemMonitorCompositePart(composite);
        compositePart.setCpuSeriesKey(
                new String[] {
                    DbProcStatEnum.USER_PERCENT.name(), DbProcStatEnum.KERNEL_PERCENT.name()
                });
        compositePart.setMemorySeriesKey(new String[] {DbProcStatEnum.MEMPHY_PERCENT.name()});

        // history setting
        compositePart.setHistoryFileName(historyFileName);
        compositePart.setHistoryPath(historyPath);
        compositePart.setHistoryFileHelp(historyFileHelp);

        compositePart.load();

        scrolledComp.setContent(composite);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);
        scrolledComp.setMinHeight(800);
        scrolledComp.setMinWidth(350);

        ServerInfo serverInfo = cubridNode.getServer().getServerInfo();
        ArrayList<String> databaseLst = new ArrayList<String>();
        List<DatabaseInfo> databaseInfoLst = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
        for (DatabaseInfo databaseInfo : databaseInfoLst) {
            DbRunningType dbRunningType = databaseInfo.getRunningType();
            if (dbRunningType == DbRunningType.CS) {
                databaseLst.add(databaseInfo.getDbName());
            }
        }
        dbCombo = new DbComboContribution("database");
        dbCombo.setDatabaseLst(databaseLst);
        if (databaseLst.isEmpty()) {
            runflag = false;
        } else {
            databaseLst.add(0, ALL_DB_NAME);
            dbCombo.setSelectedDb(databaseLst.get(0));
        }
        String selectDb = dbCombo.getSelectedDb();
        if (ALL_DB_NAME.equals(selectDb)) {
            dbName = "";
        } else {
            dbName = selectDb;
        }
        makeActions();
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

        TreeMap<String, String> returnMap = getDbProcTaskValue(serverInfo, startRun);
        TreeMap<String, String> hostMap = getHostProcTaskValue(serverInfo, startRun);
        String hostCpuTotal = hostMap.get(HostStatEnum.CPU_TOTAL.name());
        String memPhyTotal = hostMap.get(HostStatEnum.MEMPHY_TOTAL.name());
        returnMap.put(HostStatEnum.CPU_TOTAL.name(), hostCpuTotal);
        returnMap.put(HostStatEnum.MEMPHY_TOTAL.name(), memPhyTotal);
        return returnMap;
    }

    /**
     * Get the update value from dbprocstat task
     *
     * @param serverInfo the server info
     * @param startRun int
     * @return TreeMap<String, String>
     */
    private TreeMap<String, String> getDbProcTaskValue(ServerInfo serverInfo, int startRun) {
        String selectDb = dbCombo.getSelectedDb();
        if (ALL_DB_NAME.equals(selectDb)) {
            dbName = "";
        } else {
            dbName = selectDb;
        }
        final CommonQueryTask<DbProcStat> dbStatTask =
                new CommonQueryTask<DbProcStat>(
                        serverInfo, CommonSendMsg.getCommonDatabaseSendMsg(), dbProcStatusResult);
        dbStatTask.setDbName(dbName);
        dbStatTask.execute();

        if (startRun == 0) {
            dbProcStatusResult = dbStatTask.getResultModel();
            if (!dbProcStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(dbProcStatusResult);
                }
            }
        } else if (startRun == 1) {
            dbProcOldOneStatusResult.copyFrom(dbProcStatusResult);
            dbProcStatusResult.clearDbstat();
            dbProcStatusResult = dbStatTask.getResultModel();
            if (!dbProcStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(dbProcStatusResult);
                }
            }
            dbProcStatProxy.compute(dbName, dbProcStatusResult, dbProcOldOneStatusResult);
        } else {
            dbProcOldTwoStatusResult.copyFrom(dbProcOldOneStatusResult);
            dbProcOldOneStatusResult.copyFrom(dbProcStatusResult);
            dbProcStatusResult.clearDbstat();
            dbProcStatusResult = dbStatTask.getResultModel();
            if (!dbProcStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(dbProcStatusResult);
                }
            }
            dbProcStatProxy.compute(
                    dbName, dbProcStatusResult, dbProcOldOneStatusResult, dbProcOldTwoStatusResult);
        }
        return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
    }

    /**
     * Get update value from hostStatdata task
     *
     * @param serverInfo the serverInfo
     * @param startRun int
     * @return Map<String, String>
     */
    private TreeMap<String, String> getHostProcTaskValue(
            ServerInfo serverInfo, int startRun) { // FIXME extract

        final CommonQueryTask<HostStatData> hostStattask =
                new CommonQueryTask<HostStatData>(
                        serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), hostStatusResult);
        hostStattask.execute();
        if (startRun == 0) {

            hostStatusResult = hostStattask.getResultModel();
            if (!hostStatusResult.getStatus()) {
                if (interruptReq) {
                    hostStatDataProxy.getDiagStatusResultMap();
                } else {
                    showErrorMsg(hostStatusResult);
                }
            }
        } else if (startRun == 1) {
            hostOldOneStatusResult.copyFrom(hostStatusResult);
            hostStatusResult = hostStattask.getResultModel();
            if (!hostStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(hostStatusResult);
                }
            }
            hostStatDataProxy.compute(hostStatusResult, hostOldOneStatusResult);
        } else {
            hostOldTwoStatusResult.copyFrom(hostOldOneStatusResult);
            hostOldOneStatusResult.copyFrom(hostStatusResult);
            hostStatusResult = hostStattask.getResultModel();
            if (!hostStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(hostStatusResult);
                }
            }
            hostStatDataProxy.compute(
                    hostStatusResult, hostOldOneStatusResult, hostOldTwoStatusResult);
        }
        return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
    }

    /**
     * Show error message When the response' status is not success
     *
     * @param diagStatusResult this instance of BrokerDiagData
     */
    private void showErrorMsg(final DbProcStat diagStatusResult) {
        interruptReq = true;
        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(diagStatusResult.getNote());
                            }
                        });
    }

    /**
     * Show error message When the response' status is not success
     *
     * @param diagStatusResult this instance of BrokerDiagData
     */
    private void showErrorMsg(final HostStatData diagStatusResult) {
        interruptReq = true;
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
        compositePart.updateValueMap(resultMap);
        monitorTimes += 1;
    }

    /**
     * A inner class that update the data of chart in a single thread
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-6-17 created by lizhiqiang
     */
    class DataGenerator extends Thread {
        private TreeMap<String, String> updateMap;

        /** Thread run method */
        @Override
        public void run() {
            while (runflag) {
                updateMap = getUpdateValue(startRun);
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
                    // history
                    if (recordFlag) {
                        historyFileHelp.buildCountFile(typeNames);
                        historyFileHelp.storageData(updateMap, DbProcStatEnum.values());

                    } else {
                        historyFileHelp.closeHistroyFile();
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
            historyFileHelp.closeHistroyFile();
            super.dispose();
        }
    }

    /** This method is to create actions at tool bar */
    private void makeActions() {
        final IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(dbCombo);

        Action settingAction =
                new Action() {
                    public void run() {
                        compositePart.fireChartSetting();
                    }
                };
        settingAction.setText(Messages.chartSettingTxt);
        settingAction.setToolTipText(Messages.chartSettingTxt);
        settingAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/action/setting-small.png"));
        manager.add(settingAction);

        RecordAction recordAction = new RecordAction();
        recordAction.setRecorder(this);
        recordAction.setPrepareTooltip(Messages.dbSysMonStartRecordTooltip);
        recordAction.setRecordTooltip(Messages.dbSysMonRecordingTooltip);

        recordAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
        recordAction.setToolTipText(Messages.dbSysMonStartRecordTooltip);
        manager.add(recordAction);
    }

    /** @param startRun the startRun to set */
    public void setStartRun(int startRun) {
        this.startRun = startRun;
    }

    /**
     * Convert the Map key value
     *
     * @param inputMap the instance of Map<IDiagPara,String>
     * @return the instance of TreeMap<String, String>
     */
    private TreeMap<String, String> convertMapKey(
            Map<IDiagPara, String> inputMap) { // FIXME extract
        TreeMap<String, String> map = new TreeMap<String, String>();
        for (Map.Entry<IDiagPara, String> entry : inputMap.entrySet()) {
            map.put(entry.getKey().getName(), entry.getValue());
        }
        return map;
    }

    /**
     * Update the history path
     *
     * @param selectedDb a string representative of selected database name
     */
    private void updateHistoryPath(String selectedDb) {
        String dbName = "";
        if (serverInfo != null) {
            if (!ALL_DB_NAME.equals(selectedDb)) {
                dbName = selectedDb;
            }
            String hostAddress = serverInfo.getHostAddress();
            int monPort = serverInfo.getHostMonPort();
            historyFileName =
                    HistoryComposite.DB_SYSMON_HISTORY_FILE_PREFIX
                            + dbName
                            + "@"
                            + hostAddress
                            + "_"
                            + monPort
                            + HistoryComposite.HISTORY_SUFFIX;

            int lastSeparatorIndex = historyPath.lastIndexOf(File.separator);
            String prePath = historyPath.substring(0, lastSeparatorIndex);
            historyPath = prePath + File.separator + historyFileName;
            historyFileHelp.setHistoryPath(historyPath);
            historyFileHelp.setChangedHistoryPath(true);
        }
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

    /**
     * An concrete ControlContribution implementation for adding a database combo to a tool bar.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-6-18 created by lizhiqiang
     */
    public class DbComboContribution extends ControlContribution {
        private List<String> databaseLst;
        private int selected;
        private String selectedDb;

        // Constuctor
        public DbComboContribution(String id) {
            super(id);
        }

        /**
         * Creates and returns the control for this contribution item under the given parent
         * composite.
         *
         * @param parent the parent composite
         * @return the control under the given parent composite.
         */
        protected Control createControl(Composite parent) {
            final Combo dbCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            dbCombo.setToolTipText(Messages.dbSelectTip);
            if (databaseLst != null && !databaseLst.isEmpty()) {
                dbCombo.setItems(databaseLst.toArray(new String[databaseLst.size()]));
                dbCombo.select(0);
                selected = dbCombo.getSelectionIndex();
                selectedDb = dbCombo.getItem(selected);
                dbCombo.addSelectionListener(
                        new SelectionAdapter() {

                            /**
                             * Sent when selection occurs in the control.
                             *
                             * @param event an event containing information about the selection
                             */
                            public void widgetSelected(SelectionEvent event) {
                                widgetDefaultSelected(event);
                            }

                            /**
                             * Sent when default selection occurs in the control.
                             *
                             * @param event an event containing information about the default
                             *     selection
                             */
                            public void widgetDefaultSelected(SelectionEvent event) {
                                int newSelected = dbCombo.getSelectionIndex();
                                if (selected == newSelected) {
                                    return;
                                }
                                String newSelectedDb = dbCombo.getItem(newSelected);
                                if (CommonUITool.openConfirmBox(
                                        Messages.bind(Messages.msgChangeDb, newSelectedDb))) {
                                    selected = newSelected;
                                    selectedDb = newSelectedDb;

                                    setStartRun(0);
                                    compositePart.updateChart();
                                    updateHistoryPath(selectedDb);

                                } else {
                                    dbCombo.select(selected);
                                    return;
                                }
                            }
                        });
            }

            return dbCombo;
        }

        /**
         * Get the database list
         *
         * @return the databaseLst
         */
        public List<String> getDatabaseLst() {
            return databaseLst;
        }

        /** @param databaseLst the databaseLst to set */
        public void setDatabaseLst(List<String> databaseLst) {
            this.databaseLst = databaseLst;
        }

        /**
         * Get the selected database.
         *
         * @return the selectedDb
         */
        public String getSelectedDb() {
            return selectedDb;
        }

        /** @param selectedDb the selectedDb to set */
        public void setSelectedDb(String selectedDb) {
            this.selectedDb = selectedDb;
        }
    }
}
