/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi;

import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckFileTask;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hand shake time for cubrid host token by sending info to CMS at regular time.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-11-11 created by Yu Guojia
 */
public class ConnectionKeepAliveHandler implements ICubridNodeChangedListener {

    private final Map<String, ServerInfo> connectedServers =
            new ConcurrentHashMap<String, ServerInfo>();
    private static final ConnectionKeepAliveHandler INSTANCE = new ConnectionKeepAliveHandler();

    public static ConnectionKeepAliveHandler getInstance() {
        return INSTANCE;
    }

    private ConnectionKeepAliveHandler() {
        sendHeartBeat();
    }

    private void sendHeartBeat() {
        Timer timer = new Timer();
        TimerTask timerTask =
                new TimerTask() {
                    public void run() {
                        Iterator<String> it = connectedServers.keySet().iterator();
                        while (it.hasNext()) {
                            ServerInfo serverInfo = connectedServers.get(it.next());
                            runHeartBeatTask(serverInfo);
                            sleep();
                        }
                    }
                };
        timer.scheduleAtFixedRate(timerTask, new Date(), 600000); // 10 minute
    }

    private void runHeartBeatTask(ServerInfo serverInfo) {
        CheckFileTask checkFileTask = new CheckFileTask(serverInfo);
        checkFileTask.execute();
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void nodeChanged(CubridNodeChangedEvent event) {
        ICubridNode cubridNode = event.getCubridNode();
        CubridNodeChangedEventType eventType = event.getType();
        if (cubridNode == null || eventType == null) {
            return;
        }

        if (!(cubridNode instanceof CubridServer)) {
            return;
        }

        CubridServer server = (CubridServer) cubridNode;
        ServerInfo serverInfo = server.getServerInfo();
        if (CubridNodeChangedEventType.SERVER_CONNECTED.equals(eventType)) {
            addServer(serverInfo);
        } else if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(eventType)) {
            delServer(serverInfo);
        }
    }

    private void addServer(ServerInfo serverInfo) {
        String uid = generateServerId(serverInfo);
        if (!connectedServers.containsKey(uid)) {
            connectedServers.put(uid, serverInfo);
        }
    }

    private void delServer(ServerInfo serverInfo) {
        String uid = generateServerId(serverInfo);
        connectedServers.remove(uid);
    }

    private String generateServerId(ServerInfo serverInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(serverInfo.getHostAddress());
        sb.append(",");
        sb.append(serverInfo.getHostMonPort());
        sb.append(",");
        sb.append(serverInfo.getHostJSPort());
        return sb.toString();
    }
}
