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
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.ShardConnection;
import com.cubrid.cubridmanager.core.shard.model.ShardKeys;
import com.cubrid.cubridmanager.core.shard.model.ShardStatus;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.model.ShardsStatus;
import com.cubrid.cubridmanager.core.shard.task.GetShardConfTask;
import com.cubrid.cubridmanager.core.shard.task.GetShardStatusTask;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.CubridShard;
import com.cubrid.cubridmanager.ui.spi.model.CubridShardFolder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * This class is responsible to load all shards of shard folder
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-11-30
 */
public class CubridShardsFolderLoader extends CubridNodeLoader {
    private static final Logger LOGGER = LogUtil.getLogger(CubridShardsFolderLoader.class);
    private CubridShardFolder cubridShardFolder;

    public CubridShardsFolderLoader(CubridShardFolder cubridShardFolder) {
        this.cubridShardFolder = cubridShardFolder;
    }

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
            if (serverInfo == null) {
                LOGGER.error("The serverInfo is a null.");
                return;
            }
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

            Display display = Display.getDefault();

            boolean statusTag = false;
            boolean confTag = false;

            Shards shards = new Shards();
            GetShardConfTask<Shards> getShardConfTask =
                    new GetShardConfTask<Shards>(serverInfo, shards);
            monitorCancel(monitor, new ITask[] {getShardConfTask});
            getShardConfTask.execute();
            if (!monitor.isCanceled() && !getShardConfTask.isSuccess()) {
                final String errorMsg = getShardConfTask.getErrorMsg();
                confTag = false;
                cubridShardFolder.setEnable(false);
                parent.removeAllChild();
                display.syncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(errorMsg);
                            }
                        });
                setLoaded(true);
                // return;
            } else {
                confTag = true;
                getShardConfTask.loadDataToModel();
                serverInfo.setShards(shards);
            }

            GetShardStatusTask getShardStatusTask = new GetShardStatusTask(serverInfo, null);
            monitorCancel(monitor, new ITask[] {getShardStatusTask});
            getShardStatusTask.execute();
            ShardsStatus shardsStatus = null;
            if (getShardStatusTask.isSuccess()) {
                shardsStatus = getShardStatusTask.getShardsStatus();
                cubridShardFolder.setRunning(true);
                statusTag = true;
            } else {
                final String errorMsg = getShardStatusTask.getErrorMsg();
                if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
                    statusTag = false;
                    if (errorMsg.trim().contains("cubrid shard is not running")) {
                        cubridShardFolder.setRunning(false);
                        parent.removeAllChild();
                        // return;
                    } else {
                        display.syncExec(
                                new Runnable() {
                                    public void run() {
                                        CommonUITool.openErrorBox(errorMsg);
                                    }
                                });
                    }
                }
            }

            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }

            List<ICubridNode> oldNodeList = parent.getChildren();
            parent.removeAllChild();

            Map<String, ShardStatus> tmpCache = new HashMap<String, ShardStatus>();
            if (statusTag) {
                List<ShardStatus> shardStatuss =
                        (shardsStatus == null || shardsStatus.getShardStatuss() == null)
                                ? null
                                : shardsStatus.getShardStatuss();
                for (int i = 0; shardStatuss != null && i < shardStatuss.size(); i++) {
                    ShardStatus shard = shardStatuss.get(i);
                    tmpCache.put(shard.getName(), shard);
                }
            }
            if (confTag) {
                List<Shard> shardList =
                        (shards == null || shards.getShardList() == null)
                                ? null
                                : shards.getShardList();
                for (int i = 0; shardList != null && i < shardList.size(); i++) {
                    Shard shard = shardList.get(i);
                    //
                    ShardConnection shardConnection = new ShardConnection();
                    shard.setShardConnectionFile(shardConnection);
                    GetShardConfTask<ShardConnection> getShardConnectionConfTask =
                            new GetShardConfTask<ShardConnection>(serverInfo, shardConnection);
                    getShardConnectionConfTask.execute();
                    getShardConnectionConfTask.loadDataToModel();

                    ShardKeys shardKeys = new ShardKeys();
                    shard.setShardKeysFile(shardKeys);
                    GetShardConfTask<ShardKeys> getShardKeyConfTask =
                            new GetShardConfTask<ShardKeys>(serverInfo, shardKeys);
                    getShardKeyConfTask.execute();
                    getShardKeyConfTask.loadDataToModel();
                    //
                    String id = parent.getId() + NODE_SEPARATOR + shard.getName();
                    ICubridNode shardNode = isContained(oldNodeList, id);

                    ShardStatus shardStatus = tmpCache.get(shard.getName());
                    String shardLabel =
                            shardStatus != null
                                    ? " ("
                                            + shardStatus.getPort()
                                            + ","
                                            + shardStatus.getAccessMode()
                                            + ")"
                                    : "";

                    if (shardNode == null) {
                        shardNode = new CubridShard(id, shard.getName() + shardLabel);
                        ((CubridShard) shardNode).setName(shard.getName());
                        shardNode.setType(CubridNodeType.SHARD);
                        shardNode.setContainer(true);
                        shardNode.setModelObj(shard);
                        shardNode.setLoader(new CubridShardFolderLoader());
                    } else {
                        shardNode.setModelObj(shard);
                        if (shardNode.getLoader() != null && shardNode.getLoader().isLoaded()) {
                            shardNode.getLoader().setLoaded(false);
                            shardNode.getChildren(monitor);
                        }
                    }
                    ((CubridShard) shardNode).setRunning(shardStatus != null);
                    parent.addChild(shardNode);
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
