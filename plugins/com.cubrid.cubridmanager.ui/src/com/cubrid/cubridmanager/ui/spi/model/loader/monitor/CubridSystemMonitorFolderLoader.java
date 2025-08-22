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
package com.cubrid.cubridmanager.ui.spi.model.loader.monitor;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbSystemMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.HostSystemMonitorViewPart;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible for loading all system monitor template
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-10 created by lizhiqiang
 * @deprecated
 */
public class CubridSystemMonitorFolderLoader extends CubridNodeLoader {

    private static final String DB_SYSTEM_ID = "dbSystem";
    private static final String HOST_SYSTEM_ID = "hostSystem";

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
            if (userInfo == null
                    || StatusMonitorAuthType.AUTH_NONE == userInfo.getStatusMonitorAuth()) {
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }

            String hostSystemId = parent.getId() + NODE_SEPARATOR + HOST_SYSTEM_ID;
            ICubridNode hostSystemNode =
                    new DefaultCubridNode(
                            hostSystemId,
                            Messages.msgHostSystemMonitorName,
                            "icons/navigator/status_item.png");
            hostSystemNode.setType(CubridNodeType.SYSTEM_MONITOR_TEMPLATE);
            hostSystemNode.setViewId(HostSystemMonitorViewPart.ID);
            hostSystemNode.setContainer(false);
            parent.addChild(hostSystemNode);

            if (CompatibleUtil.isSupportDBSystemMonitor(serverInfo)) {
                String dbSystemId = parent.getId() + NODE_SEPARATOR + DB_SYSTEM_ID;
                ICubridNode dbSystemNode =
                        new DefaultCubridNode(
                                dbSystemId,
                                Messages.msgDbSystemMonitorName,
                                "icons/navigator/status_item.png");
                dbSystemNode.setType(CubridNodeType.SYSTEM_MONITOR_TEMPLATE);
                dbSystemNode.setViewId(DbSystemMonitorViewPart.ID);
                dbSystemNode.setContainer(false);
                parent.addChild(dbSystemNode);
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
