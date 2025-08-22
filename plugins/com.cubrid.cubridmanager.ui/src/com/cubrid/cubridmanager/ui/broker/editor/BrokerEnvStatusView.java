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
package com.cubrid.cubridmanager.ui.broker.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.Messages;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerEnvStatusColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerEnvStatusSettingDlg;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSetting;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerTblColumnSetHelp;
import com.cubrid.cubridmanager.ui.spi.model.CubridBrokerFolder;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * A editor part which is responsible for showing the status of all the brokers
 *
 * <p>BlocksStatusEditor Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-5-18 created by lizhiqiang
 */
public class BrokerEnvStatusView extends CubridViewPart {

    private static final Logger LOGGER = LogUtil.getLogger(BrokerEnvStatusView.class);
    public static final String ID = "com.cubrid.cubridmanager.ui.broker.editor.BrokerEnvStatusView";

    private TableViewer tableViewer;
    private Composite composite;

    private boolean runflag = false;
    private boolean isRunning = true;
    private List<BrokerInfo> oldBrokerInfoList;
    private boolean isFirstLoaded = true;

    /**
     * Initializes this view with the given view site.
     *
     * @param site the view site
     * @exception PartInitException if this view was not initialized successfully
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        if (null != cubridNode && CubridNodeType.BROKER_FOLDER.equals(cubridNode.getType())) {
            String serverName = cubridNode.getServer().getLabel();
            String port = cubridNode.getServer().getMonPort();
            setPartName(Messages.bind(Messages.envHeadTitel, serverName, port));
            CubridBrokerFolder brokerFolderNode = (CubridBrokerFolder) cubridNode;
            if (brokerFolderNode != null && brokerFolderNode.isRunning()) {
                this.setTitleImage(
                        CubridManagerUIPlugin.getImage(
                                "icons/navigator/broker_service_started.png"));
            } else {
                this.setTitleImage(
                        CubridManagerUIPlugin.getImage("icons/navigator/broker_group.png"));
            }
            runflag = brokerFolderNode == null ? false : brokerFolderNode.isRunning();
        }
    }

    /**
     * Create the page content
     *
     * @param parent the parent composite
     */
    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        composite.setLayout(new FillLayout());

        createTable();
        makeActions();
        composite.addControlListener(
                new ControlAdapter() {
                    public void controlResized(ControlEvent event) {
                        updateTableLayout();
                    }
                });
        new StatusUpdate().start();
    }

    /** This method initializes table */
    private void createTable() {
        tableViewer = new TableViewer(composite, SWT.FULL_SELECTION);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(true);

        BrokerTblColumnSetHelp bcsh = BrokerTblColumnSetHelp.getInstance();
        bcsh.loadSetting(
                BrokerTblColumnSetHelp.StatusColumn.BrokerEnvStatusColumn,
                BrokerEnvStatusColumn.values());

        TableLayout tlayout = new TableLayout();
        for (BrokerEnvStatusColumn column : BrokerEnvStatusColumn.values()) {
            if (column.getValue() == -1) {
                tlayout.addColumnData(new ColumnWeightData(0, 0, false));
            } else {
                tlayout.addColumnData(new ColumnWeightData(10, 40, true));
            }
        }

        tableViewer.getTable().setLayout(tlayout);
        tableViewer
                .getTable()
                .addMouseListener(
                        new MouseAdapter() {
                            public void mouseDoubleClick(MouseEvent event) {
                                int index = -1;
                                if ((index = tableViewer.getTable().getSelectionIndex()) >= 0) {
                                    TableItem tableItem = tableViewer.getTable().getItem(index);
                                    String brokename = tableItem.getText(0).trim();
                                    ICubridNode input = null;
                                    for (ICubridNode node : cubridNode.getChildren()) {
                                        if (node.getLabel().equalsIgnoreCase(brokename)) {
                                            input = node;
                                            break;
                                        }
                                    }
                                    LayoutManager.getInstance().setCurrentSelectedNode(input);
                                    IWorkbenchWindow window =
                                            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                                    if (null == window) {
                                        return;
                                    }
                                    IWorkbenchPage activePage = window.getActivePage();
                                    IViewPart viewPart =
                                            window.getActivePage().findView(BrokerStatusView.ID);
                                    if (null != viewPart) {
                                        activePage.hideView(viewPart);
                                    }
                                    try {
                                        activePage.showView(BrokerStatusView.ID);
                                    } catch (PartInitException e1) {
                                        LOGGER.error(e1.getMessage(), e1);
                                    }
                                }
                            }
                        });
        makeTableColumn();

        tableViewer.setContentProvider(new BrokersStatusContentProvider());
        ServerInfo serverInfo = cubridNode.getServer().getServerInfo();
        BrokersStatusLabelProvider brokersStatusLabelProvider = new BrokersStatusLabelProvider();
        brokersStatusLabelProvider.setServerInfo(serverInfo);
        tableViewer.setLabelProvider(brokersStatusLabelProvider);
    }

    /** Create the column that shows on the broker environment table */
    private void makeTableColumn() {
        TableColumn tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblBrokerName);
        tblColumn.setToolTipText(Messages.tblBrokerName);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblBrokerStatus);
        tblColumn.setToolTipText(Messages.tblBrokerStatus);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblBrokerProcess);
        tblColumn.setToolTipText(Messages.tblBrokerProcess);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblPort);
        tblColumn.setToolTipText(Messages.tblPort);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblServer);
        tblColumn.setToolTipText(Messages.tblServer);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblQueue);
        tblColumn.setToolTipText(Messages.tblQueue);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblRequest);
        tblColumn.setToolTipText(Messages.tblRequest);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblTps);
        tblColumn.setToolTipText(Messages.tblTps);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblQps);
        tblColumn.setToolTipText(Messages.tblQps);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblLongTran);
        tblColumn.setToolTipText(Messages.tblLongTran);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblLongQuery);
        tblColumn.setToolTipText(Messages.tblLongQuery);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblErrQuery);
        tblColumn.setToolTipText(Messages.tblErrQuery);
    }

    /** This method is to create actions at tool bar */
    private void makeActions() {
        Action columnAction =
                new Action() {
                    public void run() {
                        BrokerEnvStatusSettingDlg settingDlg = new BrokerEnvStatusSettingDlg(null);
                        if (settingDlg.open() == Dialog.OK) {

                            TableLayout tlayout = new TableLayout();

                            for (BrokerEnvStatusColumn column : BrokerEnvStatusColumn.values()) {
                                if (column.getValue() == -1) {
                                    tlayout.addColumnData(new ColumnWeightData(0, 0, false));
                                } else {
                                    tlayout.addColumnData(new ColumnWeightData(10, 40, true));
                                }
                            }
                            tableViewer.getTable().setLayout(tlayout);
                            tableViewer.getTable().layout();
                        }
                    }
                };
        columnAction.setText(Messages.envColumnSettingTxt);
        columnAction.setImageDescriptor(
                CubridManagerUIPlugin.getImageDescriptor("icons/action/setting-small.png"));

        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(columnAction);
    }

    /**
     * Response to cubrid node changes
     *
     * @param event the event
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        ICubridNode eventNode = event.getCubridNode();
        if (eventNode == null || this.cubridNode == null) {
            return;
        }
        // if it is not in the same host,return
        if (!eventNode.getServer().getId().equals(this.cubridNode.getServer().getId())) {
            return;
        }
        // if changed node is not broker folder or server,return
        String type = eventNode.getType();
        if (!CubridNodeType.BROKER_FOLDER.equals(type) && !CubridNodeType.SERVER.equals(type)) {
            return;
        }
        synchronized (this) {
            if (type == NodeType.SERVER) {
                String id = eventNode.getId();
                CubridBrokerFolder currentNode = (CubridBrokerFolder) eventNode.getChild(id);
                this.cubridNode = currentNode;
            } else {
                this.cubridNode = eventNode;
            }
            if (this.cubridNode == null || !((CubridBrokerFolder) eventNode).isRunning()) {
                setRunflag(false);
                this.setTitleImage(
                        CubridManagerUIPlugin.getImage("icons/navigator/broker_group.png"));
                return;
            } else {
                setRunflag(true);
                this.setTitleImage(
                        CubridManagerUIPlugin.getImage(
                                "icons/navigator/broker_service_started.png"));
            }
            refresh(true, false);
        }
    }

    /**
     * Refreshes this view
     *
     * @param isUpdateTable whether update table
     * @param isRefreshChanged whether refresh changed
     */
    public void refresh(boolean isUpdateTable, boolean isRefreshChanged) {
        ServerInfo site = cubridNode.getServer().getServerInfo();
        BrokerInfos brokerInfos = new BrokerInfos();
        final CommonQueryTask<BrokerInfos> task =
                new CommonQueryTask<BrokerInfos>(
                        site, CommonSendMsg.getCommonSimpleSendMsg(), brokerInfos);
        task.execute();
        brokerInfos = task.getResultModel();
        List<BrokerInfo> newBrokerInfoList = null;
        if (null != brokerInfos) {
            BrokerInfoList list = brokerInfos.getBorkerInfoList();
            if (list != null && list.getBrokerInfoList() != null) {
                newBrokerInfoList = list.getBrokerInfoList();
            }
        }
        List<BrokerInfo> changedBrokerInfoList = new ArrayList<BrokerInfo>();
        for (int i = 0; newBrokerInfoList != null && i < newBrokerInfoList.size(); i++) {
            BrokerInfo newBrokerInfo = newBrokerInfoList.get(i);
            BrokerInfo changedBrokerInfo = newBrokerInfo.clone();
            for (int j = 0; oldBrokerInfoList != null && j < oldBrokerInfoList.size(); j++) {
                BrokerInfo oldBrokerInfo = oldBrokerInfoList.get(j);
                if (newBrokerInfo
                        .getName()
                        .equalsIgnoreCase(oldBrokerInfo.getName())) { // FIXME more simple
                    long newTran = StringUtil.intValue(newBrokerInfo.getTran());
                    long newQuery = StringUtil.intValue(newBrokerInfo.getQuery());
                    long newLongTran = StringUtil.longValue(newBrokerInfo.getLong_tran());
                    long newLongQuery = StringUtil.longValue(newBrokerInfo.getLong_query());
                    long newErrQuery = StringUtil.intValue(newBrokerInfo.getError_query());

                    long oldTran = StringUtil.intValue(oldBrokerInfo.getTran());
                    long oldQuery = StringUtil.intValue(oldBrokerInfo.getQuery());
                    long oldLongTran = StringUtil.longValue(oldBrokerInfo.getLong_tran());
                    long oldLongQuery = StringUtil.longValue(oldBrokerInfo.getLong_query());
                    long oldErrQuery = StringUtil.intValue(oldBrokerInfo.getError_query());

                    long changedTran = newTran - oldTran;
                    long changedQuery = newQuery - oldQuery;
                    long changedLongTran = newLongTran - oldLongTran;
                    long changedLongQuery = newLongQuery - oldLongQuery;
                    long changedErrQuery = newErrQuery - oldErrQuery;

                    changedBrokerInfo.setTran(String.valueOf(changedTran > 0 ? changedTran : 0));
                    changedBrokerInfo.setQuery(String.valueOf(changedQuery > 0 ? changedQuery : 0));
                    changedBrokerInfo.setLong_tran(
                            String.valueOf(changedLongTran > 0 ? changedLongTran : 0));
                    changedBrokerInfo.setLong_query(
                            String.valueOf(changedLongQuery > 0 ? changedLongQuery : 0));
                    changedBrokerInfo.setError_query(
                            String.valueOf(changedErrQuery > 0 ? changedErrQuery : 0));
                    break;
                }
            }
            changedBrokerInfoList.add(changedBrokerInfo);
        }
        oldBrokerInfoList = newBrokerInfoList;
        if (isUpdateTable) {
            if (isRefreshChanged) {
                tableViewer.setInput(changedBrokerInfoList);
            } else {
                tableViewer.setInput(oldBrokerInfoList);
            }
            tableViewer.refresh();
        }
    }

    /**
     * A inner class which extends the Thread and calls the refresh method
     *
     * @author lizhiqiang
     * @version 1.0 - 2009-5-30 created by lizhiqiang
     */
    private class StatusUpdate extends Thread {
        /** Update the status */
        public void run() {
            int count = 0;
            while (isRunning) {
                String serverName = cubridNode.getServer().getLabel();
                BrokerIntervalSetting brokerIntervalSetting =
                        BrokerIntervalSettingManager.getInstance()
                                .getBrokerIntervalSetting(serverName, cubridNode.getLabel());
                final int term = Integer.parseInt(brokerIntervalSetting.getInterval());
                final int timeCount = count;
                if (getRunflag() && brokerIntervalSetting.isOn() && term > 0) {
                    isFirstLoaded = false;
                    Display.getDefault()
                            .asyncExec(
                                    new Runnable() {
                                        public void run() {
                                            if (composite != null && !composite.isDisposed()) {
                                                refresh(timeCount % term == 0, true);
                                            }
                                        }
                                    });

                    try {
                        if (count % term == 0) {
                            count = 0;
                        }
                        count++;
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    if (isFirstLoaded) {
                        isFirstLoaded = false;
                        Display.getDefault()
                                .asyncExec(
                                        new Runnable() {
                                            public void run() {
                                                if (composite != null && !composite.isDisposed()) {
                                                    refresh(true, false);
                                                }
                                            }
                                        });
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Gets the value of runflag
     *
     * @return <code> true</code> if it is running;<code>false</code> otherwise
     */
    private boolean getRunflag() {
        synchronized (this) {
            return runflag;
        }
    }

    /**
     * Sets the value of runflag
     *
     * @param runflag whether running
     */
    private void setRunflag(boolean runflag) {
        synchronized (this) {
            this.runflag = runflag;
        }
    }

    /** Dispose the resource */
    public void dispose() {
        runflag = false;
        isRunning = false;
        tableViewer = null;
        super.dispose();
    }

    /** Update table layout */
    private void updateTableLayout() {
        TableLayout tlayout = new TableLayout();
        for (BrokerEnvStatusColumn column : BrokerEnvStatusColumn.values()) {
            if (column.getValue() == -1) {
                tlayout.addColumnData(new ColumnWeightData(0, 0, false));
            } else {
                tlayout.addColumnData(new ColumnWeightData(10, 40, true));
            }
        }
        tableViewer.getTable().setLayout(tlayout);
        tableViewer.getTable().layout(true);
    }
}
