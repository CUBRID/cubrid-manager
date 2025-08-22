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
package com.cubrid.common.ui.spi.model.loader.sp;

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
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPType;
import com.cubrid.cubridmanager.core.cubrid.sp.task.GetSPInfoListTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load the children of Stored procedure function folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-26 created by pangqiren
 */
public class CubridFunctionFolderLoader extends CubridNodeLoader {

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
                database.getDatabaseInfo().setSpFunctionInfoList(null);
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            DatabaseInfo databaseInfo = database.getDatabaseInfo();
            final GetSPInfoListTask task = new GetSPInfoListTask(databaseInfo);
            task.setSpType(SPType.FUNCTION);
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
            List<SPInfo> spInfoList = task.getSPInfoList();
            DbUserInfo userInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            boolean isDBA = false;
            String loginUserName = "";
            if (userInfo != null && userInfo.isDbaAuthority()) {
                isDBA = true;
            }
            if (userInfo != null) {
                loginUserName = userInfo.getName();
            }
            List<SPInfo> authSpInfoList = new ArrayList<SPInfo>();
            if (spInfoList != null && !spInfoList.isEmpty()) {
                for (SPInfo spInfo : spInfoList) {
                    if (!isDBA && !loginUserName.equalsIgnoreCase(spInfo.getOwner())) {
                        continue;
                    }
                    authSpInfoList.add(spInfo);
                    String id = parent.getId() + NODE_SEPARATOR + spInfo.getSpName();
                    ICubridNode spNode = createFunctionNode(id, spInfo);
                    parent.addChild(spNode);
                }
            }
            databaseInfo.setSpFunctionInfoList(authSpInfoList);
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
     * Create function node
     *
     * @param id The node id
     * @param spInfo The model object
     * @return ICubridNode
     */
    public static ICubridNode createFunctionNode(String id, SPInfo spInfo) {
        ICubridNode spNode =
                new DefaultSchemaNode(
                        id, spInfo.getSpName(), "icons/navigator/procedure_func_item.png");
        spNode.setType(NodeType.STORED_PROCEDURE_FUNCTION);
        spNode.setModelObj(spInfo);
        spNode.setContainer(false);
        return spNode;
    }
}
