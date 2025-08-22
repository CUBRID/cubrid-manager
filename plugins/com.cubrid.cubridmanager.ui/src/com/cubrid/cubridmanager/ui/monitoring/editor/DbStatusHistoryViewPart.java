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
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.MonitorType;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSetting;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSettingMatching;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.jfree.chart.plot.XYPlot;
import org.slf4j.Logger;

/**
 * A editor part is used to view broker status history monitor.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-27 created by lizhiqiang
 */
public class DbStatusHistoryViewPart extends CubridViewPart {

    private static final Logger LOGGER = LogUtil.getLogger(DbStatusHistoryViewPart.class);
    public static final String ID = DbStatusHistoryViewPart.class.getName();
    private ChartCompositePart chartPart;
    private DbComboContribution dbCombo;
    private ServerInfo serverInfo;

    /**
     * Creates the SWT controls for this workbench part.
     *
     * @param parent the parent control
     * @see IWorkbenchPart
     */
    public void createPartControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.RESIZE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final HistoryComposite historyComposite = new HistoryComposite();
        historyComposite.loadTimeSelection(composite);
        serverInfo = cubridNode.getServer().getServerInfo();

        Label sepWithResult = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT);
        sepWithResult.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        DbStatDumpData dbStatDumpData = new DbStatDumpData();
        TreeMap<String, String> map = new TreeMap<String, String>();
        for (Map.Entry<IDiagPara, String> entry :
                dbStatDumpData.getDiagStatusResultMap().entrySet()) {
            map.put(entry.getKey().getName(), entry.getValue());
        }
        chartPart = new ChartCompositePart(composite, map);
        dbCombo = new DbComboContribution("database");
        List<String> databaseLst = new ArrayList<String>();
        List<DatabaseInfo> databaseInfoLst = serverInfo.getLoginedUserInfo().getDatabaseInfoList();

        if (null != databaseInfoLst && !databaseInfoLst.isEmpty()) {
            for (DatabaseInfo databaseInfo : databaseInfoLst) {
                databaseLst.add(databaseInfo.getDbName());
            }

            dbCombo.setDatabaseLst(databaseLst);
            if (!databaseLst.isEmpty()) {
                dbCombo.setSelectedDb(databaseLst.get(0));
            }
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            ShowSetting showSetting = chartPart.getSettingMap().get(key);
            ShowSettingMatching.match(key, showSetting, MonitorType.DATABASE);
        }
        chartPart.setChartTitle(Messages.databaseHistoryChartTtl);

        String dbName = dbCombo.getSelectedDb();
        String hostAddress = serverInfo.getHostAddress();
        int monPort = serverInfo.getHostMonPort();
        String historyFileName =
                HistoryComposite.DB_HISTORY_FILE_PREFIX
                        + dbName
                        + "@"
                        + hostAddress
                        + "_"
                        + monPort
                        + HistoryComposite.HISTORY_SUFFIX;
        chartPart.setHistoryFileName(historyFileName);
        IPath historyPath = CubridManagerCorePlugin.getDefault().getStateLocation();
        String sHistoryPath = historyPath.toOSString() + File.separator + historyFileName;
        chartPart.setHistoryPath(sHistoryPath);

        chartPart.loadContent();
        chartPart.addChartMouseListener();
        makeActions();

        historyComposite
                .getQueryBtn()
                .addSelectionListener(
                        new SelectionListener() {

                            public void widgetDefaultSelected(SelectionEvent ex) {
                                String date = historyComposite.getDate();
                                String fromTime = historyComposite.getFromTime();
                                String toTime = historyComposite.getToTime();
                                // check date/fromTime/toTime
                                boolean timeOrder =
                                        historyComposite.checkTime(date, fromTime, toTime);
                                if (!timeOrder) {
                                    CommonUITool.openErrorBox(Messages.errDbHistorySettingTime);
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
                                long millisFrom = calFrom.getTimeInMillis();
                                String[] toHms = toTime.split(":");
                                int toHour = Integer.valueOf(toHms[0]);
                                int toMinute = Integer.valueOf(toHms[1]);
                                int toSecond = Integer.valueOf(toHms[2]);
                                Calendar calTo = Calendar.getInstance();
                                calTo.set(year, month, day, toHour, toMinute, toSecond);
                                long millisTo = calTo.getTimeInMillis();

                                XYPlot plot = (XYPlot) chartPart.getChart().getPlot();
                                plot.getDomainAxis().setRange(millisFrom, millisTo);

                                CounterFile countFile = chartPart.openHistoryFile();
                                if (countFile == null) {
                                    return;
                                }

                                List<String> types = new ArrayList<String>();
                                for (DbStatDumpEnum diagName : DbStatDumpEnum.values()) {
                                    String type = diagName.getName();
                                    types.add(type);
                                }
                                chartPart.executeQueryWithBusyCursor(
                                        countFile, types, millisFrom, millisTo);
                                try {
                                    countFile.close();
                                } catch (IOException e1) {
                                    LOGGER.error(e1.getMessage());
                                }
                            }

                            public void widgetSelected(SelectionEvent ex) {
                                widgetDefaultSelected(ex);
                            }
                        });
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
                        chartPart.fireChartSetting();
                    }
                };
        settingAction.setText(Messages.chartSettingTxt);
        settingAction.setToolTipText(Messages.chartSettingTxt);
        settingAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/action/setting-small.png"));

        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(dbCombo);
        manager.add(new Separator());
        manager.add(settingAction);
    }

    /** Disposes this view when it closed */
    public void dispose() {
        synchronized (this) {
            chartPart.closeHistroyFile();
            super.dispose();
        }
    }

    /**
     * An concrete ControlContribution implementation for adding a database combo to a tool bar.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-8-10 created by lizhiqiang
     */
    private class DbComboContribution extends ControlContribution {
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
         * @return the control under th e given parent composite.
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
