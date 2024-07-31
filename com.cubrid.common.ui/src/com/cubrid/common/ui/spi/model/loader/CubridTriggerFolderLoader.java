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

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.ui.perspective.PerspectiveManager;
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
import com.cubrid.cubridmanager.core.cubrid.trigger.task.GetTriggerListTask;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerListTask;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load all trigger information in some database,these children include
 * all trigger informaiton
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridTriggerFolderLoader extends CubridNodeLoader {

    public static final String TRIGGER_FOLDER_ID = "Triggers";

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
                database.getDatabaseInfo().setTriggerList(null);
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            DatabaseInfo databaseInfo = database.getDatabaseInfo();
            ITask task = null;
            if (ApplicationType.CUBRID_MANAGER.equals(
                    PerspectiveManager.getInstance().getCurrentMode())) {
                task = new GetTriggerListTask(parent.getServer().getServerInfo());
                ((GetTriggerListTask) task).setDbName(database.getLabel());
            } else {
                task = new JDBCGetTriggerListTask(databaseInfo);
            }

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
            List<Trigger> triggerList = null;
            if (task instanceof GetTriggerListTask) {
                triggerList = ((GetTriggerListTask) task).getTriggerInfoList();
            } else if (task instanceof JDBCGetTriggerListTask) {
                triggerList = ((JDBCGetTriggerListTask) task).getTriggerInfoList();
            }
            if (triggerList != null && !triggerList.isEmpty()) {
                for (Trigger trigger : triggerList) {
                    if (databaseInfo.isSupportUserSchema()) {
                        int idx = trigger.getName().indexOf(".");
                        if (idx > 0) {
                            String triggerName = trigger.getName();
                            trigger.setOwner(triggerName.substring(0, idx));
                            trigger.setName(triggerName.substring(idx + 1));
                        }
                    }
                    String id;
                    if (trigger.getOwner() != null && !trigger.getOwner().isEmpty()) {
                        id =
                                parent.getId()
                                        + NODE_SEPARATOR
                                        + trigger.getOwner()
                                        + "."
                                        + trigger.getName();
                    } else {
                        id = parent.getId() + NODE_SEPARATOR + trigger.getName();
                    }
                    ICubridNode triggerNode = createTriggerNode(databaseInfo, id, trigger);
                    parent.addChild(triggerNode);
                }
            }
            databaseInfo.setTriggerList(triggerList);
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
     * Create trigger node
     *
     * @param id The node id
     * @param trigger The model object
     * @return ICubridNode
     */
    public static ICubridNode createTriggerNode(
            DatabaseInfo databaseInfo, String id, Trigger trigger) {
        ICubridNode triggerNode;
        if (databaseInfo.isSupportUserSchema()) {
            triggerNode =
                    new DefaultSchemaNode(
                            id,
                            "["
                                    + trigger.getOwner().toUpperCase(Locale.getDefault())
                                    + "] "
                                    + trigger.getName(),
                            trigger.getOwner() + "." + trigger.getName(),
                            "icons/navigator/trigger_item.png");
        } else {
            triggerNode =
                    new DefaultSchemaNode(
                            id,
                            trigger.getName(),
                            trigger.getName(),
                            "icons/navigator/trigger_item.png");
        }
        triggerNode.setType(NodeType.TRIGGER);
        triggerNode.setModelObj(trigger);
        triggerNode.setContainer(false);
        return triggerNode;
    }
}
