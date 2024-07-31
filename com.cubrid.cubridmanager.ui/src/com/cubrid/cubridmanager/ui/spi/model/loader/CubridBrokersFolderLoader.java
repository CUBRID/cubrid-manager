/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.spi.model.loader;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.task.GetShardConfTask;
import com.cubrid.cubridmanager.core.shard.task.GetShardStatusTask;
import com.cubrid.cubridmanager.ui.broker.editor.BrokerStatusView;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridBroker;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.CubridShardFolder;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * This class is responsible to load all broker of Borkers folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridBrokersFolderLoader extends CubridNodeLoader {

    private static final String SHARD_FOLDER_NAME = Messages.msgShardsFolderName;
    public static final String SHARD_FOLDER_ID = "Shards";

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            ServerInfo serverInfo = parent.getServer().getServerInfo();
            ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
            if (userInfo == null || CasAuthType.AUTH_NONE == userInfo.getCasAuth()) {
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            BrokerInfos brokerInfos = new BrokerInfos();
            final CommonQueryTask<BrokerInfos> task =
                    new CommonQueryTask<BrokerInfos>(
                            serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), brokerInfos);
            monitorCancel(monitor, new ITask[] {task});
            task.execute();
            final String errorMsg = task.getErrorMsg();
            if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
                parent.removeAllChild();
                Display display = Display.getDefault();
                display.syncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(errorMsg);
                            }
                        });
                setLoaded(true);
                return;
            }
            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }
            brokerInfos = task.getResultModel();
            List<ICubridNode> oldNodeList = parent.getChildren();
            parent.removeAllChild();
            List<BrokerInfo> brokerInfoList =
                    (brokerInfos == null || brokerInfos.getBorkerInfoList() == null)
                            ? null
                            : brokerInfos.getBorkerInfoList().getBrokerInfoList();
            for (int i = 0; brokerInfoList != null && i < brokerInfoList.size(); i++) {
                BrokerInfo brokerInfo = brokerInfoList.get(i);
                String id = parent.getId() + NODE_SEPARATOR + brokerInfo.getName();
                ICubridNode brokerInfoNode = isContained(oldNodeList, id);
                if (brokerInfoNode == null) {
                    brokerInfoNode =
                            new CubridBroker(
                                    id, brokerInfo.getName(), "icons/navigator/broker.png");
                    ((CubridBroker) brokerInfoNode)
                            .setStartedIconPath("icons/navigator/broker_started.png");
                    brokerInfoNode.setType(CubridNodeType.BROKER);
                    brokerInfoNode.setContainer(true);
                    brokerInfoNode.setModelObj(brokerInfo);
                    brokerInfoNode.setViewId(BrokerStatusView.ID);
                    brokerInfoNode.setLoader(new CubridBrokerFolderLoader());
                } else {
                    brokerInfoNode.setModelObj(brokerInfo);
                    if (brokerInfoNode.getLoader() != null
                            && brokerInfoNode.getLoader().isLoaded()) {
                        brokerInfoNode.getLoader().setLoaded(false);
                        brokerInfoNode.getChildren(monitor);
                    }
                }
                parent.addChild(brokerInfoNode);
            }

            serverInfo.setBrokerInfos(brokerInfos);

            // For the Shard Broker Folder
            if (CompatibleUtil.isSupportShard(serverInfo)) {
                Shards shards = new Shards();
                GetShardConfTask<Shards> getShardConfTask =
                        new GetShardConfTask<Shards>(serverInfo, shards);
                getShardConfTask.execute();
                // final String errorMsg = getShardConfTask.getErrorMsg();
                // "File(?) open error" means didn't support this API.
                // if (errorMsg == null ||
                // !"File(?) open error".equals(errorMsg.trim())) {
                if (getShardConfTask.isSuccess()) {
                    GetShardStatusTask getShardStatusTask =
                            new GetShardStatusTask(serverInfo, null);
                    getShardStatusTask.execute();
                    boolean runningShard = getShardStatusTask.isSuccess();
                    serverInfo.setShards(shards);
                    shards.setRunning(runningShard);

                    String shardFolderId = parent.getId() + NODE_SEPARATOR + SHARD_FOLDER_ID;
                    ICubridNode shardFolder = parent.getChild(shardFolderId);
                    if (shardFolder == null) {
                        shardFolder = new CubridShardFolder(shardFolderId, SHARD_FOLDER_NAME);
                        ICubridNodeLoader loader =
                                new CubridShardsFolderLoader((CubridShardFolder) shardFolder);
                        loader.setLevel(getLevel());
                        shardFolder.setLoader(loader);
                        parent.addChild(shardFolder);
                        // shardFolder.getChildren(monitor);
                    } else {
                        if (shardFolder.getLoader() != null && shardFolder.getLoader().isLoaded()) {
                            shardFolder.getLoader().setLoaded(false);
                            shardFolder.getChildren(monitor);
                        }
                    }
                }
            }

            Collections.sort(parent.getChildren());
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Return whether contained the node from node id
     *
     * @param nodeList the all node
     * @param id the node id
     * @return the ICubridNode object
     */
    private ICubridNode isContained(List<ICubridNode> nodeList, String id) {
        for (int i = 0; nodeList != null && i < nodeList.size(); i++) {
            ICubridNode node = nodeList.get(i);
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }
}
