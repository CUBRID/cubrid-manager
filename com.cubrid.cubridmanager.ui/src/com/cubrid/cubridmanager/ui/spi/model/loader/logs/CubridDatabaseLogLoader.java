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
package com.cubrid.cubridmanager.ui.spi.model.loader.logs;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.logs.model.DbLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.DbLogInfos;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import com.cubrid.cubridmanager.ui.spi.model.CubridDbLogNode;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load all children of CUBRID database log folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridDatabaseLogLoader extends CubridNodeLoader {

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded() || parent == null) {
                return;
            }
            String dbName = parent.getLabel();
            ServerInfo serverInfo = parent.getServer().getServerInfo();
            DbLogInfos dbLogInfos = new DbLogInfos();
            final CommonQueryTask<DbLogInfos> task =
                    new CommonQueryTask<DbLogInfos>(
                            serverInfo, CommonSendMsg.getCommonDatabaseSendMsg(), dbLogInfos);
            task.setDbName(dbName);

            monitorCancel(monitor, new ITask[] {task});
            task.execute();
            final String errorMsg = task.getErrorMsg();
            if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
                parent.removeAllChild();
                openErrorBox(errorMsg);
                setLoaded(true);
                return;
            }
            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }
            parent.removeAllChild();
            dbLogInfos = task.getResultModel();
            DbLogInfoList dbLogInfoList = dbLogInfos == null ? null : dbLogInfos.getDbLogInfoList();
            List<LogInfo> logInfoList =
                    dbLogInfoList == null ? null : dbLogInfoList.getDbLogInfoList();
            if (logInfoList != null) {
                for (LogInfo dbLogInfo : logInfoList) {
                    String id = parent.getId() + NODE_SEPARATOR + dbLogInfo.getName();
                    CubridDbLogNode dbLogInfoNode =
                            new CubridDbLogNode(
                                    id, dbLogInfo.getName(), "icons/navigator/log_item.png");
                    dbLogInfoNode.setType(CubridNodeType.LOGS_SERVER_DATABASE_LOG);
                    dbLogInfoNode.setModelObj(dbLogInfo);
                    dbLogInfoNode.setEditorId(LogEditorPart.ID);
                    dbLogInfoNode.setContainer(false);
                    parent.addChild(dbLogInfoNode);
                }
            }
            if (dbLogInfos != null && serverInfo.getLogInfoManager() != null) {
                serverInfo.getLogInfoManager().addDbLogInfos(dbLogInfos);
            }
            if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
                Collections.sort(parent.getChildren());
            }
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }
}
