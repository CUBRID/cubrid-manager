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
package com.cubrid.cubridmanager.ui.shard.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.core.util.ThreadUtil;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CubridShardConfParaConstants;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.ShardStatus;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.model.ShardsStatus;
import com.cubrid.cubridmanager.core.shard.task.GetShardConfTask;
import com.cubrid.cubridmanager.core.shard.task.GetShardStatusTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerEnvStatusColumn;
import com.cubrid.cubridmanager.ui.shard.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.CubridShardFolder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * A editor part which is responsible for showing the status of all the shards
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-1-6
 */
public class ShardEnvStatusView extends CubridViewPart {
    private static final Logger LOGGER = LogUtil.getLogger(ShardEnvStatusView.class);
    public static final String ID = "com.cubrid.cubridmanager.ui.shard.editor.ShardEnvStatusView";

    private TableViewer tableViewer;
    private Composite composite;

    private boolean runflag = false;
    private boolean isRunning = true;

    /**
     * Initializes this view with the given view site.
     *
     * @param site the view site
     * @exception PartInitException if this view was not initialized successfully
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        if (null != cubridNode && CubridNodeType.SHARD_FOLDER.equals(cubridNode.getType())) {
            String serverName = cubridNode.getServer().getLabel();
            String port = cubridNode.getServer().getMonPort();
            // Shard Broker Status - All@{0}-{1}
            setPartName(Messages.bind(Messages.envHeadTitel, serverName, port));
            CubridShardFolder shardFolderNode = (CubridShardFolder) cubridNode;
            if (shardFolderNode != null && shardFolderNode.isRunning()) {
                this.setTitleImage(
                        CubridManagerUIPlugin.getImage(
                                "icons/navigator/broker_service_started.png"));
                runflag = true;
            } else {
                this.setTitleImage(
                        CubridManagerUIPlugin.getImage("icons/navigator/broker_group.png"));
            }
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
        // makeActions();
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

        // ShardStatusSettingHelp sssh = ShardStatusSettingHelp.getInstance();
        // Map<String, Boolean> data = sssh.loadSetting(ShardsStatus.class);

        TableLayout tlayout = new TableLayout();
        // for (Entry<String, Boolean> column : data.entrySet()) {
        // tlayout.addColumnData(new ColumnWeightData(0, 0, column.getValue()));
        // }
        for (int i = 0; i < 7; i++) {
            tlayout.addColumnData(new ColumnWeightData(0, 0, true));
        }

        makeTableColumn();

        tableViewer.setContentProvider(new SimpleContentProvider<ShardsStatus>());
        ShardEnvStatusLabelProvider shardEnvStatusLabelProvider = new ShardEnvStatusLabelProvider();
        tableViewer.setLabelProvider(shardEnvStatusLabelProvider);

        tableViewer.getTable().setLayout(tlayout);
    }

    /** Create the column that shows on the shard environment table */
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
        tblColumn.setText(Messages.tblRequest);
        tblColumn.setToolTipText(Messages.tblRequest);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblTps);
        tblColumn.setToolTipText(Messages.tblTps);

        tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblColumn.setText(Messages.tblQps);
        tblColumn.setToolTipText(Messages.tblQps);
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
        if (!CubridNodeType.SHARD_FOLDER.equals(type) && !CubridNodeType.SERVER.equals(type)) {
            return;
        }
        synchronized (this) {
            if (StringUtil.isEqual(type, NodeType.SERVER)) {
                String id = eventNode.getId();
                CubridShardFolder currentNode = (CubridShardFolder) eventNode.getChild(id);
                this.cubridNode = currentNode;
            } else {
                this.cubridNode = eventNode;
            }
            if (this.cubridNode == null || !((CubridShardFolder) eventNode).isRunning()) {
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
            refresh();
        }
    }

    /**
     * Refreshes this view
     *
     * @param isUpdateTable whether update table
     * @param isRefreshChanged whether refresh changed
     */
    public void refresh() {
        ServerInfo site = cubridNode.getServer().getServerInfo();

        Shards shards = new Shards();
        GetShardConfTask<Shards> getShardConfTask = new GetShardConfTask<Shards>(site, shards);
        getShardConfTask.execute();
        if (!getShardConfTask.isSuccess()) {
            CommonUITool.openErrorBox(getShardConfTask.getErrorMsg());
            return;
        }
        getShardConfTask.loadDataToModel();

        GetShardStatusTask getShardStatusTask = new GetShardStatusTask(site, null);
        getShardStatusTask.execute();
        if (!getShardStatusTask.isSuccess()) {
            CommonUITool.openErrorBox(getShardStatusTask.getErrorMsg());
            return;
        }
        ShardsStatus shardsStatus = getShardStatusTask.getShardsStatus();

        Map<String, ShardStatus> tmpCache = new HashMap<String, ShardStatus>();

        List<ShardStatus> newShardInfoList = shardsStatus.getShardStatuss();
        if (newShardInfoList == null) {
            LOGGER.error("The newShardInfoList is a null.");
            return;
        }

        for (int i = 0; newShardInfoList != null && i < newShardInfoList.size(); i++) {
            ShardStatus shard = newShardInfoList.get(i);
            tmpCache.put(shard.getName(), shard);
        }
        List<Shard> shardList = shards.getShardList();

        for (int i = 0; shardList != null && i < shardList.size(); i++) {
            Shard shard = shardList.get(i);
            ShardStatus newShardInfo = tmpCache.get(shard.getName());
            if (newShardInfo == null) {
                newShardInfo = new ShardStatus();
                newShardInfo.setName(shard.getName());
                newShardInfo.setStatus(OnOffType.OFF.getText());
                newShardInfo.setPort(shard.getValue(CubridShardConfParaConstants.BROKER_PORT));
                newShardInfoList.add(newShardInfo);
            } else {
                newShardInfo.setStatus(OnOffType.ON.getText());
            }
        }

        tableViewer.setInput(newShardInfoList);
        tableViewer.refresh();
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
            while (isRunning) {
                if (getRunflag()) {
                    Display.getDefault()
                            .asyncExec(
                                    new Runnable() {
                                        public void run() {
                                            if (composite != null && !composite.isDisposed()) {
                                                refresh();
                                            }
                                        }
                                    });
                }
                ThreadUtil.sleep(60000);
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
