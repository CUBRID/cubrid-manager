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
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HistoryFileHelp;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HostSystemMonitorCompositePart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.RecordAction;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.Recordable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
public class HostSystemMonitorViewPart extends CubridViewPart implements Recordable {

    private static final Logger LOGGER = LogUtil.getLogger(HostSystemMonitorViewPart.class);
    public static final String ID = HostSystemMonitorViewPart.class.getName();
    private Composite composite;
    private boolean runflag = true;
    private int startRun = 0;

    private static HostStatData diagOldOneStatusResult = new HostStatData();
    private static HostStatData diagOldTwoStatusResult = new HostStatData();
    private HostStatData diagStatusResult = new HostStatData();
    private long monitorTimes;
    private boolean interruptReq;
    private HostStatDataProxy hostStatDataProxy;
    private HostSystemMonitorCompositePart compositePart;

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
        hostStatDataProxy = new HostStatDataProxy();
        serverInfo = cubridNode.getServer().getServerInfo();
        if (serverInfo != null) {
            String hostAddress = serverInfo.getHostAddress();
            int monPort = serverInfo.getHostMonPort();
            historyFileName =
                    HistoryComposite.HOST_SYSMON_HISTORY_FILE_PREFIX
                            + hostAddress
                            + "_"
                            + monPort
                            + HistoryComposite.HISTORY_SUFFIX;
            IPath histPath = CubridManagerCorePlugin.getDefault().getStateLocation();
            historyPath = histPath.toOSString() + File.separator + historyFileName;
            historyFileHelp = new HistoryFileHelp();
            historyFileHelp.setHistoryPath(historyPath);

            List<String> typeLst = new ArrayList<String>();
            for (HostStatEnum hostEnum : HostStatEnum.values()) {
                typeLst.add(hostEnum.getName());
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

        compositePart = new HostSystemMonitorCompositePart(composite);
        compositePart.setCpuSeriesKey(
                new String[] {HostStatEnum.USER.name(), HostStatEnum.KERNEL.name()});
        compositePart.setIowaitSeriesKey(new String[] {HostStatEnum.IOWAIT.name()});
        compositePart.setMemorySeriesKey(new String[] {HostStatEnum.MEMPHY_PERCENT.name()});

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

        final CommonQueryTask<HostStatData> task =
                new CommonQueryTask<HostStatData>(
                        serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), diagStatusResult);
        task.execute();

        if (startRun == 0) {
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptReq) {
                    hostStatDataProxy.getDiagStatusResultMap();
                } else {
                    showErrorMsg(diagStatusResult);
                }
            }
        } else if (startRun == 1) {
            diagOldOneStatusResult.copyFrom(diagStatusResult);
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(diagStatusResult);
                }
            }
            hostStatDataProxy.compute(diagStatusResult, diagOldOneStatusResult);
        } else {

            diagOldTwoStatusResult.copyFrom(diagOldOneStatusResult);
            diagOldOneStatusResult.copyFrom(diagStatusResult);
            diagStatusResult = task.getResultModel();
            if (!diagStatusResult.getStatus()) {
                if (interruptReq) {
                    return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
                } else {
                    showErrorMsg(diagStatusResult);
                }
            }
            hostStatDataProxy.compute(
                    diagStatusResult, diagOldOneStatusResult, diagOldTwoStatusResult);
        }
        return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
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
        // history
        if (recordFlag) {
            historyFileHelp.buildCountFile(typeNames);
            historyFileHelp.storageData(resultMap, HostStatEnum.values());
        } else {
            historyFileHelp.closeHistroyFile();
        }
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
            historyFileHelp.closeHistroyFile();
            super.dispose();
        }
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

    /** This method is to create actions at tool bar */
    private void makeActions() {
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

        final IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(settingAction);

        RecordAction recordAction = new RecordAction();
        recordAction.setRecorder(this);
        recordAction.setPrepareTooltip(Messages.hostSysMonStartRecordTooltip);
        recordAction.setRecordTooltip(Messages.hostSysMonRecordingTooltip);

        recordAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
        recordAction.setToolTipText(Messages.hostSysMonStartRecordTooltip);
        manager.add(recordAction);
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
