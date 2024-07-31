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

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.MonitorType;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.RecordAction;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.Recordable;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSetting;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSettingMatching;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.StatusMonInstanceData;
import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * A editor part is used to view broker status monitor.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-21 created by lizhiqiang
 */
public class BrokerStatusMonitorViewPart extends CubridViewPart implements Recordable {

    private static final Logger LOGGER = LogUtil.getLogger(BrokerStatusMonitorViewPart.class);
    public static final String ID = BrokerStatusMonitorViewPart.class.getName();
    private Composite composite;
    private boolean runflag = true;
    private int startRun = 0;

    private static BrokerDiagData diagOldOneStatusResult = new BrokerDiagData();
    private static BrokerDiagData diagOldTwoStatusResult = new BrokerDiagData();
    private BrokerDiagData diagStatusResult = new BrokerDiagData();
    private Calendar lastSec;
    private Calendar nowSec;
    private long monitorTimes;
    private ChartCompositePart chartPart;
    private boolean interruptReq;
    private StatusMonInstanceData monInstaceData;
    private boolean recordFlag;
    private boolean isNewBrokerDiag;

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
     */
    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.RESIZE);
        composite.setLayout(new FillLayout());

        ICubridNode selection = getCubridNode();
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        isNewBrokerDiag = CompatibleUtil.isNewBrokerDiag(serverInfo);

        BrokerDiagData brokerDiagData = new BrokerDiagData();
        TreeMap<String, String> map = convertMapKey(brokerDiagData.getDiagStatusResultMap());
        chartPart = new ChartCompositePart(composite, map);
        String hostAddress = serverInfo.getHostAddress();
        int monPort = serverInfo.getHostMonPort();
        String historyFileName =
                HistoryComposite.BROKER_HISTORY_FILE_PREFIX
                        + hostAddress
                        + "_"
                        + monPort
                        + HistoryComposite.HISTORY_SUFFIX;
        chartPart.setHistoryFileName(historyFileName);

        if (null == monInstaceData) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                ShowSetting showSetting = chartPart.getSettingMap().get(key);
                ShowSettingMatching.match(key, showSetting, MonitorType.BROKER, isNewBrokerDiag);
            }
            chartPart.setChartTitle(Messages.brokerMonitorChartTtl);
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
        ServerInfo site = cubridNode.getServer().getServerInfo();

        final CommonQueryTask<BrokerDiagData> task =
                new CommonQueryTask<BrokerDiagData>(
                        site, CommonSendMsg.getGetBrokerStatusItems(), diagStatusResult);
        task.execute();

        TreeMap<String, String> resultMap = null;
        float inter = 0.0f;

        if (startRun == 0) {
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(diagStatusResult.getDiagStatusResultMap());
                } else {
                    showErrorMsg(diagStatusResult);
                }
            }
            return convertMapKey(diagStatusResult.getDiagStatusResultMap());
        } else if (startRun == 1) {
            lastSec = Calendar.getInstance();
            diagOldOneStatusResult.copyFrom(diagStatusResult);
            BrokerDiagData brokerDiagDataDelta = new BrokerDiagData();
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(diagStatusResult.getDiagStatusResultMap());
                } else {
                    showErrorMsg(diagStatusResult);
                }
            }
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

            diagOldTwoStatusResult.copyFrom(diagOldOneStatusResult);
            diagOldOneStatusResult.copyFrom(diagStatusResult);
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(diagStatusResult.getDiagStatusResultMap());
                } else {
                    showErrorMsg(diagStatusResult);
                }
            }
            BrokerDiagData diagStatusResultDelta = new BrokerDiagData();
            diagStatusResultDelta.getDelta(
                    diagStatusResult, diagOldOneStatusResult, diagOldTwoStatusResult, inter);
            resultMap = convertMapKey(diagStatusResultDelta.getDiagStatusResultMap());
        }
        return resultMap;
    }

    /**
     * Show error message When the response' status is not success
     *
     * @param diagStatusResult this instance of BrokerDiagData
     */
    private void showErrorMsg(final BrokerDiagData diagStatusResult) {
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
            while (getRunflag()) {
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
                    if (recordFlag) {
                        chartPart.storageData(updateMap, BrokerDiagEnum.values(), null);
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

    /**
     * Gets the value of runflag
     *
     * @return boolean
     */
    public boolean getRunflag() {
        synchronized (this) {
            return runflag;
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
        manager.add(settingAction);

        RecordAction recordAction = new RecordAction();
        recordAction.setRecorder(this);
        recordAction.setPrepareTooltip(Messages.brokerMonitorStartRecordTooltip);
        recordAction.setRecordTooltip(Messages.brokerMonitorRecordingTooltip);

        recordAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
        recordAction.setToolTipText(Messages.brokerMonitorStartRecordTooltip);
        manager.add(recordAction);
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
