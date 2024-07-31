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
package com.cubrid.common.ui.spi.model.loader.schema;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.cubrid.table.control.SchemaInfoEditorPart;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load the children of CUBRID views folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-19 created by pangqiren
 */
public class CubridViewsFolderLoader extends CubridNodeLoader {

    private static final String SYSTEM_VIEW_FOLDER_NAME = Messages.msgSystemViewFolderName;
    public static final String SYSTEM_VIEW_FOLDER_ID = "#System views";
    public static final String VIEWS_FOLDER_ID = "Views";

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
                database.getDatabaseInfo().setUserViewInfoList(null);
                database.getDatabaseInfo().setSysViewInfoList(null);
                database.getDatabaseInfo().clearSchemas();
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            DatabaseInfo databaseInfo = database.getDatabaseInfo();
            final GetAllClassListTask task = new GetAllClassListTask(databaseInfo);
            monitorCancel(monitor, new ITask[] {task});
            List<ClassInfo> allClassInfoList = task.getSchema(true, false);
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
            // add system view folder
            String systemViewFolderId = parent.getId() + NODE_SEPARATOR + SYSTEM_VIEW_FOLDER_ID;
            ICubridNode systemViewFolder = parent.getChild(systemViewFolderId);
            parent.removeAllChild();
            if (systemViewFolder == null) {
                systemViewFolder =
                        new DefaultSchemaNode(
                                systemViewFolderId,
                                SYSTEM_VIEW_FOLDER_NAME,
                                SYSTEM_VIEW_FOLDER_NAME,
                                "icons/navigator/folder_sys.png");
                systemViewFolder.setType(NodeType.SYSTEM_VIEW_FOLDER);
                systemViewFolder.setContainer(true);
                ICubridNodeLoader loader = new CubridSystemViewFolderLoader();
                loader.setLevel(getLevel());
                systemViewFolder.setLoader(loader);
                parent.addChild(systemViewFolder);
                if (getLevel() == DEFINITE_LEVEL) {
                    systemViewFolder.getChildren(monitor);
                }
            } else {
                parent.addChild(systemViewFolder);
                if (systemViewFolder.getLoader() != null
                        && systemViewFolder.getLoader().isLoaded()) {
                    systemViewFolder.getLoader().setLoaded(false);
                    systemViewFolder.getChildren(monitor);
                }
            }
            if (allClassInfoList != null) {
                for (ClassInfo classInfo : allClassInfoList) {
                    String id = parent.getId() + NODE_SEPARATOR + classInfo.getUniqueName();
                    ICubridNode classNode = createUserViewNode(id, classInfo);
                    parent.addChild(classNode);
                }
            }
            database.getDatabaseInfo().setUserViewInfoList(allClassInfoList);
            database.getDatabaseInfo().clearSchemas();
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Create user view node
     *
     * @param id The node id
     * @param classInfo The model object
     * @return ICubridNode
     */
    public static ICubridNode createUserViewNode(String id, ClassInfo classInfo) {
        String viewName = classInfo.getUniqueName();
        String viewClassName = classInfo.getClassName();
        ICubridNode classNode;
        if (classInfo.isSupportUserSchema()) {
            classNode =
                    new DefaultSchemaNode(
                            id,
                            "[" + classInfo.getOwnerName() + "] " + viewClassName,
                            viewName,
                            "icons/navigator/schema_view_item.png");
        } else {
            classNode =
                    new DefaultSchemaNode(
                            id, viewClassName, viewName, "icons/navigator/schema_view_item.png");
        }
        classNode.setType(NodeType.USER_VIEW);
        classNode.setEditorId(SchemaInfoEditorPart.ID);
        classNode.setContainer(false);
        classNode.setModelObj(classInfo);
        return classNode;
    }
}
