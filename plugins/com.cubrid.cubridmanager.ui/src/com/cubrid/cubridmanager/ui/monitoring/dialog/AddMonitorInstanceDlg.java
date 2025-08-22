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
package com.cubrid.cubridmanager.ui.monitoring.dialog;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.BrokerStatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusDumpMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartSettingDlg;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.CubridStatusMonitorInstance;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.HistoryComposite;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.MonitorType;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSetting;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ShowSettingMatching;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.StatusMonInstanceData;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This type provides a dialog for the action of addMonitorInstanceAction in order to set some
 * properties for monitor instance.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-31 created by lizhiqiang
 */
public class AddMonitorInstanceDlg extends CMTitleAreaDialog {

    private final String[] monitorTypes =
            new String[] {MonitorType.BROKER.toString(), MonitorType.DATABASE.toString()};
    private final String[] defaultMonitorTtl =
            new String[] {Messages.brokerMonitorChartTtl, Messages.dbMonitorChartTtl};
    private ChartSettingDlg chartSettingDlg;
    private Combo typeCombo;
    private Text nodeTxt;
    private ICubridNode selection;
    private boolean isOkenable[];
    private StatusMonInstanceData monData;
    private Button saveBtn;
    private String hostAddress;
    private int monPort;
    private boolean isNewBrokerDiag;

    /**
     * Constructor
     *
     * @param parentShell
     */
    public AddMonitorInstanceDlg(Shell parentShell) {
        super(parentShell);
        isOkenable = new boolean[1];
        isOkenable[0] = false;
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above the button bar).
     *
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {

        initial();

        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite comp = new Composite(parentComp, SWT.NO_FOCUS);
        GridLayout layout = new GridLayout();
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite typeComp = new Composite(comp, SWT.NONE);
        typeComp.setLayout(new GridLayout(4, false));
        typeComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label typeLbl = new Label(typeComp, SWT.NONE);
        typeLbl.setText(Messages.addMonInsDlgTypeLbl);

        typeCombo = new Combo(typeComp, SWT.READ_ONLY);
        final GridData gdContentTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        typeCombo.setLayoutData(gdContentTxt);
        typeCombo.setItems(monitorTypes);

        Label nodeLbl = new Label(typeComp, SWT.NONE);
        nodeLbl.setText(Messages.addMonInsDlgNodeName);

        nodeTxt = new Text(typeComp, SWT.BORDER);
        nodeTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (monData == null) {
            typeCombo.select(0);
        } else {
            MonitorType monitorType = monData.getMonitorType();
            typeCombo.setText(monitorType.toString());
            nodeTxt.setEnabled(false);
            String noteLabel = selection.getLabel();
            nodeTxt.setText(noteLabel);
        }
        nodeTxt.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent ex) {
                        if (ValidateUtil.isValidDBName(nodeTxt.getText())) { // using db name rule
                            isOkenable[0] = true;
                        } else {
                            isOkenable[0] = false;
                        }
                        enableOk();
                    }
                });
        typeCombo.addSelectionListener(new TypeSelectionAdapter());

        final CTabFolder folder = new CTabFolder(comp, SWT.BORDER);
        folder.setLayout(new GridLayout());
        GridData gdTabFolder = new GridData(SWT.FILL, SWT.FILL, true, true);
        folder.setLayoutData(gdTabFolder);
        folder.setSimple(false);

        TreeMap<String, ShowSetting> settingMap = new TreeMap<String, ShowSetting>();
        chartSettingDlg = new ChartSettingDlg(null);
        chartSettingDlg.setFolder(folder);
        chartSettingDlg.setShowTitlteContent(true);

        String historyFileName = hostAddress + "_" + monPort + HistoryComposite.HISTORY_SUFFIX;
        if (typeCombo.getSelectionIndex() == 0) {
            historyFileName = HistoryComposite.BROKER_HISTORY_FILE_PREFIX + historyFileName;
        } else if (typeCombo.getSelectionIndex() == 1) {
            historyFileName = HistoryComposite.DB_HISTORY_FILE_PREFIX + historyFileName;
        }
        chartSettingDlg.setHistoryFileName(historyFileName);
        if (monData == null) {
            BrokerDiagData brokerDiagData = new BrokerDiagData();
            TreeMap<String, String> map = convertMapKey(brokerDiagData.getDiagStatusResultMap());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                ShowSetting showSetting = new ShowSetting();
                ShowSettingMatching.match(key, showSetting, MonitorType.BROKER, isNewBrokerDiag);
                settingMap.put(key, showSetting);
            }
            IPath historyPath = CubridManagerCorePlugin.getDefault().getStateLocation();
            chartSettingDlg.setTitleName(defaultMonitorTtl[0]);
            String sHistoryPath = historyPath.toOSString() + File.separator + historyFileName;
            chartSettingDlg.setHistoryPath(sHistoryPath);
        } else {
            // title
            String titleName = monData.getTitleName();
            String titleBgColor = monData.getTitleBgColor();
            String titleFontName = monData.getTitleFontName();
            int titleFontSize = monData.getTitleFontSize();
            String titleFontColor = monData.getTitleFontColor();

            chartSettingDlg.setTitleName(titleName);
            chartSettingDlg.setTtlBgColor(titleBgColor);
            chartSettingDlg.setTtlFontName(titleFontName);
            chartSettingDlg.setTtlFontSize(titleFontSize);
            chartSettingDlg.setTtlFontColor(titleFontColor);

            // plot
            String plotBgColor = monData.getPlotBgColor();
            String plotDateAxisColor = monData.getPlotDateAxisColor();
            String plotDomainGridColor = monData.getPlotDomainGridColor();
            String plotNumberAxisColor = monData.getPlotNumberAxisColor();
            String plotRangGridColor = monData.getPlotRangGridColor();

            chartSettingDlg.setPlotBgColor(plotBgColor);
            chartSettingDlg.setPlotDateAxisColor(plotDateAxisColor);
            chartSettingDlg.setPlotDomainGridColor(plotDomainGridColor);
            chartSettingDlg.setPlotNumberAxisColor(plotNumberAxisColor);
            chartSettingDlg.setPlotRangGridColor(plotRangGridColor);

            // series
            settingMap = monData.getSettingMap();

            // history path
            String historyPath = monData.getHistoryPath();
            chartSettingDlg.setHistoryPath(historyPath);
        }
        chartSettingDlg.setSettingMap(settingMap);

        chartSettingDlg.createTtlTab();
        chartSettingDlg.createPlotItem();
        chartSettingDlg.createSeriesItemByDefault();
        chartSettingDlg.createHistoryPathItem();

        folder.setSelection(0);

        saveBtn = new Button(comp, SWT.CHECK);
        saveBtn.setText(Messages.btnSaveMonitorSetting);

        setTitle(Messages.addMonInsDlgTtl);
        setMessage(Messages.addMonInsDlgMsg);

        return comp;
    }

    /** Initialize some fields before opening this dialog */
    private void initial() {
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        hostAddress = serverInfo.getHostAddress();
        monPort = serverInfo.getHostMonPort();
        isNewBrokerDiag = CompatibleUtil.isNewBrokerDiag(serverInfo);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.addMonInsDlgShellTxt);
        if (monData != null) {
            enableOk();
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /** When press "ok" button, call it. */
    public void okPressed() {
        chartSettingDlg.performGetData();
        StatusMonInstanceData data = new StatusMonInstanceData();
        // type
        String type = typeCombo.getText().trim();
        if (type.equals(MonitorType.BROKER.toString())) {
            data.setMonitorType(MonitorType.BROKER);
        } else if (type.equals(MonitorType.DATABASE.toString())) {
            data.setMonitorType(MonitorType.DATABASE);
        }

        // title
        data.setTitleName(chartSettingDlg.getTitleName());
        data.setTitleBgColor(chartSettingDlg.getTtlBgColor());
        data.setTitleFontName(chartSettingDlg.getTtlFontName());
        data.setTitleFontSize(chartSettingDlg.getTtlFontSize());
        data.setTitleFontColor(chartSettingDlg.getTtlFontColor());

        // plot
        data.setPlotBgColor(chartSettingDlg.getPlotBgColor());
        data.setPlotDomainGridColor(chartSettingDlg.getPlotDomainGridColor());
        data.setPlotRangGridColor(chartSettingDlg.getPlotRangGridColor());
        data.setPlotDateAxisColor(chartSettingDlg.getPlotDateAxisColor());
        data.setPlotNumberAxisColor(chartSettingDlg.getPlotNumberAxisColor());
        // series
        data.setSettingMap(chartSettingDlg.getSettingMap());
        // history path
        data.setHistoryPath(chartSettingDlg.getHistoryPath());

        // store the data
        CubridStatusMonitorInstance instance = CubridStatusMonitorInstance.getInstance();
        String nodeName = nodeTxt.getText().trim();
        String statusId = selection.getId();

        ServerInfo serverInfo = selection.getServer().getServerInfo();
        String prefix = QueryOptions.getPrefix(serverInfo);
        String selectionKey = prefix + QueryOptions.MONITOR_FOLDER_NAME + nodeName;
        if (statusId.endsWith(nodeName)) {
            instance.updateData(selectionKey, data);
            selection.setModelObj(data);
            String viewId = "";
            switch (data.getMonitorType()) {
                case BROKER:
                    viewId = BrokerStatusMonitorViewPart.ID;
                    break;
                case DATABASE:
                    viewId = DbStatusDumpMonitorViewPart.ID;
                    break;
                default:
            }
            selection.setViewId(viewId);
        } else {
            instance.addData(selectionKey, data);
        }
        // save button
        if (saveBtn.getSelection()) {
            instance.saveSetting(selectionKey);
        }
        super.okPressed();
    }

    /** Enable the "OK" button */
    private void enableOk() {
        boolean is = true;
        for (int i = 0; i < isOkenable.length; i++) {
            is = is && isOkenable[i];
        }
        if (is) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            setErrorMessage(null);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        if (!isOkenable[0]) {
            setErrorMessage(Messages.errorNodeNameMsg);
            return;
        }
    }

    /**
     * Sets the value of selection
     *
     * @param selection ICubridNode
     */
    public void setSelection(ICubridNode selection) {
        this.selection = selection;
    }

    /**
     * This type extends the SelectionAdapter and responses the selection of typeCombo.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-4-7 created by lizhiqiang
     */
    private final class TypeSelectionAdapter extends SelectionAdapter {
        /**
         * Sent when selection occurs in the control.
         *
         * @param ex an event containing information about the selection
         */
        public void widgetSelected(SelectionEvent ex) {
            TreeMap<String, ShowSetting> settingMap = new TreeMap<String, ShowSetting>();
            String historyFileName = "";
            if (typeCombo.getSelectionIndex() == 0) {
                chartSettingDlg.setTitleName(defaultMonitorTtl[0]);
                BrokerDiagData brokerDiagData = new BrokerDiagData();
                TreeMap<String, String> map = new TreeMap<String, String>();
                for (Map.Entry<IDiagPara, String> entry :
                        brokerDiagData.getDiagStatusResultMap().entrySet()) {
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

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    ShowSetting showSetting = new ShowSetting();
                    ShowSettingMatching.match(
                            key, showSetting, MonitorType.BROKER, isNewBrokerDiag);
                    settingMap.put(key, showSetting);
                }
                chartSettingDlg.setSettingMap(settingMap);
                historyFileName =
                        HistoryComposite.BROKER_HISTORY_FILE_PREFIX
                                + hostAddress
                                + "_"
                                + monPort
                                + HistoryComposite.HISTORY_SUFFIX;
            } else if (typeCombo.getSelectionIndex() == 1) {
                chartSettingDlg.setTitleName(defaultMonitorTtl[1]);
                DbStatDumpData dbStatDumpData = new DbStatDumpData();
                TreeMap<String, String> map = new TreeMap<String, String>();
                for (Map.Entry<IDiagPara, String> entry :
                        dbStatDumpData.getDiagStatusResultMap().entrySet()) {
                    map.put(entry.getKey().getName(), entry.getValue());
                }
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    ShowSetting showSetting = new ShowSetting();
                    ShowSettingMatching.match(key, showSetting, MonitorType.DATABASE);
                    settingMap.put(key, showSetting);
                }
                chartSettingDlg.setSettingMap(settingMap);

                historyFileName =
                        HistoryComposite.DB_HISTORY_FILE_PREFIX
                                + hostAddress
                                + "_"
                                + monPort
                                + HistoryComposite.HISTORY_SUFFIX;
            }
            String historyPath = chartSettingDlg.getHistoryPath().trim();
            String newHistoryPath =
                    historyPath.substring(0, historyPath.lastIndexOf(File.separator));
            newHistoryPath += File.separator + historyFileName;
            chartSettingDlg.setHistoryFileName(historyFileName);
            chartSettingDlg.setHistoryPath(newHistoryPath);
        }
    }

    /** @param monData the monData to set */
    public void setMonData(StatusMonInstanceData monData) {
        this.monData = monData;
        isOkenable[0] = monData == null ? false : true;
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
}
