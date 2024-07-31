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
package com.cubrid.common.ui.spi.model.loader;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.serial.task.GetSerialInfoListTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load the children of serial folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-8 created by pangqiren
 */
public class CubridSerialFolderLoader extends CubridNodeLoader {

    public static final String SERIAL_FOLDER_ID = "Serials";

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
            CubridDatabase database = ((ISchemaNode) parent).getDatabase();
            if (!database.isLogined() || database.getRunningType() == DbRunningType.STANDALONE) {
                database.getDatabaseInfo().setSerialInfoList(null);
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            DatabaseInfo databaseInfo = database.getDatabaseInfo();
            final GetSerialInfoListTask task = new GetSerialInfoListTask(databaseInfo);
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
            List<SerialInfo> serialInfoList = task.getSerialInfoList();
            List<SerialInfo> authSerialInfoList = new ArrayList<SerialInfo>();

            if (serialInfoList != null && !serialInfoList.isEmpty()) {
                for (SerialInfo serialInfo : serialInfoList) {
                    authSerialInfoList.add(serialInfo);
                    String id =
                            parent.getId()
                                    + NODE_SEPARATOR
                                    + serialInfo.getOwner()
                                    + "."
                                    + serialInfo.getName();
                    ICubridNode serialNode = createSerialNode(databaseInfo, id, serialInfo);
                    parent.addChild(serialNode);
                }
            }
            databaseInfo.setSerialInfoList(authSerialInfoList);
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
     * Create serial node
     *
     * @param id The node id
     * @param serialInfo The model object
     * @return ICubridNode
     */
    public static ICubridNode createSerialNode(
            DatabaseInfo databaseInfo, String id, SerialInfo serialInfo) {
        ICubridNode serialNode;
        if (databaseInfo.isSupportUserSchema()) {
            serialNode =
                    new DefaultSchemaNode(
                            id,
                            "[" + serialInfo.getOwner() + "] " + serialInfo.getName(),
                            serialInfo.getOwner() + "." + serialInfo.getName(),
                            "icons/navigator/serial_item.png");
        } else {
            serialNode =
                    new DefaultSchemaNode(
                            id,
                            serialInfo.getName(),
                            serialInfo.getName(),
                            "icons/navigator/serial_item.png");
        }
        serialNode.setId(id);
        serialNode.setType(NodeType.SERIAL);
        serialNode.setModelObj(serialInfo);
        serialNode.setContainer(false);
        return serialNode;
    }
}
