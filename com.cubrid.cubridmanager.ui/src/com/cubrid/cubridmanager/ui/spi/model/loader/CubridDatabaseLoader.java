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
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.model.loader.CubridSerialFolderLoader;
import com.cubrid.common.ui.spi.model.loader.CubridSynonymFolderLoader;
import com.cubrid.common.ui.spi.model.loader.CubridTriggerFolderLoader;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import com.cubrid.common.ui.spi.model.loader.schema.CubridViewsFolderLoader;
import com.cubrid.common.ui.spi.model.loader.sp.CubridSPFolderLoader;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.ParamDumpInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.ParamDumpTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetDbCollationTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.replication.model.MasterInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamInfo;
import com.cubrid.cubridmanager.core.replication.model.SlaveInfo;
import com.cubrid.cubridmanager.core.replication.task.CheckDistributorDbTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplAgentStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplicatedTablesTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplicationInfoTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplicationParamTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseStatusEditor;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.loader.jobauto.CubridJobAutoFolderLoader;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

/**
 * This class is responsible to load the children of CUBRID database,these children include
 * Users,Job automation,Database space,Schema,Stored procedure,Trigger folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridDatabaseLoader extends CubridNodeLoader {

    private static final String USERS_FOLDER_NAME = Messages.msgUserFolderName;
    private static final String JOB_AUTO_FOLDER_NAME = Messages.msgJobAutoFolderName;
    private static final String DB_SPACE_FOLDER_NAME = Messages.msgDbSpaceFolderName;
    private static final String TABLES_FOLDER_NAME = Messages.msgTablesFolderName;
    private static final String VIEWS_FOLDER_NAME = Messages.msgViewsFolderName;
    private static final String SP_FOLDER_NAME = Messages.msgSpFolderName;
    private static final String TRIGGER_FOLDER_NAME = Messages.msgTriggerFolderName;
    private static final String SYNONYM_FOLDER_NAME = Messages.msgSynonymFolderName;
    private static final String SERIAL_FOLDER_NAME = Messages.msgSerialFolderName;

    public static final String JOB_AUTO_FOLDER_ID = "Job automation";
    public static final String DB_SPACE_FOLDER_ID = "Database space";

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(final ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            CubridDatabase database = (CubridDatabase) parent;
            database.getDatabaseInfo().clear();
            if (!database.isLogined()) {
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            // when refresh,firstly check whether this database exist
            ServerInfo serverInfo = parent.getServer().getServerInfo();
            final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);
            ParamDumpTask paramDumpTask = null;
            if (CompatibleUtil.isSupportGetParamDump(serverInfo)) {
                paramDumpTask = new ParamDumpTask(serverInfo);
                paramDumpTask.setDbName(database.getLabel());
                paramDumpTask.setBoth(YesNoType.N);
            }
            monitorCancel(monitor, new ITask[] {getDatabaseListTask, paramDumpTask});
            getDatabaseListTask.execute();
            if (!checkResult(getDatabaseListTask, monitor)) {
                parent.removeAllChild();
                return;
            }
            List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();

            ParamDumpInfo paraDumpInfo = null;
            if (paramDumpTask != null) {
                paramDumpTask.execute();
                if (!checkResult(paramDumpTask, monitor)) {
                    parent.removeAllChild();
                    return;
                }
                paraDumpInfo = paramDumpTask.getContent();
            }
            String databaseName = database.getLabel();
            boolean isExist = false;
            for (int i = 0; databaseInfoList != null && i < databaseInfoList.size(); i++) {
                DatabaseInfo dbInfo = databaseInfoList.get(i);
                if (dbInfo.getDbName().equalsIgnoreCase(databaseName)) {
                    database.setRunningType(dbInfo.getRunningType());
                    isExist = true;
                }
            }
            if (!isExist) {
                Display display = Display.getDefault();
                display.syncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(Messages.errDatabaseNoExist);
                                CubridNavigatorView navigatorView =
                                        CubridNavigatorView.getNavigatorView(
                                                CubridHostNavigatorView.ID);
                                TreeViewer treeViewer =
                                        navigatorView == null ? null : navigatorView.getViewer();
                                if (treeViewer != null) {
                                    CommonUITool.refreshNavigatorTree(
                                            treeViewer, parent.getParent());
                                }
                            }
                        });
                setLoaded(true);
                return;
            }
            if (!loadReplication(parent, monitor)) {
                setLoaded(true);
                return;
            }
            // add tables folder
            addTableFolder(monitor, database);
            // add views folder
            addViewFolder(monitor, database);
            // add serials folder
            addSerialFolder(monitor, database);
            // add user folder
            addUserFolder(monitor, database);
            // add triggers folder
            addTriggerFolder(monitor, database);
            // add synonyms folder
            if (database.getDatabaseInfo().isSupportSynonym()) {
                addSynonymFolder(monitor, database);
            }
            // add stored procedure folder
            addProcedureFolder(monitor, database);
            // add job automation folder
            addJobAutoFolder(monitor, database);
            // add database space folder
            addSpaceFolder(monitor, database);
            // set parameter dump information
            database.getDatabaseInfo().setParaDumpInfo(paraDumpInfo);
            // set charset info
            setDbCollation(database);
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Add database space folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addSpaceFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String databaseSpaceFolderId = database.getId() + NODE_SEPARATOR + DB_SPACE_FOLDER_ID;
        ICubridNode databaseSpaceFolder = database.getChild(databaseSpaceFolderId);
        if (databaseSpaceFolder == null) {
            databaseSpaceFolder =
                    new DefaultSchemaNode(
                            databaseSpaceFolderId,
                            DB_SPACE_FOLDER_NAME,
                            "icons/navigator/volume_group.png");
            databaseSpaceFolder.setType(CubridNodeType.DBSPACE_FOLDER);
            databaseSpaceFolder.setContainer(true);
            databaseSpaceFolder.setEditorId(DatabaseStatusEditor.ID);
            ICubridNodeLoader loader = new CubridDbSpaceFolderLoader();
            loader.setLevel(getLevel());
            databaseSpaceFolder.setLoader(loader);
            database.addChild(databaseSpaceFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                databaseSpaceFolder.getChildren(monitor);
            }
        } else {
            if (databaseSpaceFolder.getLoader() != null
                    && databaseSpaceFolder.getLoader().isLoaded()) {
                databaseSpaceFolder.getLoader().setLoaded(false);
                databaseSpaceFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add job auto folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addJobAutoFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String jobAutoFolderId = database.getId() + NODE_SEPARATOR + JOB_AUTO_FOLDER_ID;
        ICubridNode jobAutoFolder = database.getChild(jobAutoFolderId);
        if (jobAutoFolder == null) {
            jobAutoFolder =
                    new DefaultSchemaNode(
                            jobAutoFolderId,
                            JOB_AUTO_FOLDER_NAME,
                            "icons/navigator/auto_group.png");
            jobAutoFolder.setType(CubridNodeType.JOB_FOLDER);
            jobAutoFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridJobAutoFolderLoader();
            loader.setLevel(getLevel());
            jobAutoFolder.setLoader(loader);
            database.addChild(jobAutoFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                jobAutoFolder.getChildren(monitor);
            }
        } else {
            if (jobAutoFolder.getLoader() != null && jobAutoFolder.getLoader().isLoaded()) {
                jobAutoFolder.getLoader().setLoaded(false);
                jobAutoFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add procedure folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addProcedureFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String spFolderId = database.getId() + NODE_SEPARATOR + CubridSPFolderLoader.SP_FOLDER_ID;
        ICubridNode spFolder = database.getChild(spFolderId);
        if (spFolder == null) {
            spFolder =
                    new DefaultSchemaNode(
                            spFolderId, SP_FOLDER_NAME, "icons/navigator/procedure_group.png");
            spFolder.setType(NodeType.STORED_PROCEDURE_FOLDER);
            spFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridSPFolderLoader();
            loader.setLevel(getLevel());
            spFolder.setLoader(loader);
            database.addChild(spFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                spFolder.getChildren(monitor);
            }
        } else {
            if (spFolder.getLoader() != null && spFolder.getLoader().isLoaded()) {
                spFolder.getLoader().setLoaded(false);
                spFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add serial folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addSerialFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String serialFolderId =
                database.getId() + NODE_SEPARATOR + CubridSerialFolderLoader.SERIAL_FOLDER_ID;
        ICubridNode serialFolder = database.getChild(serialFolderId);
        if (serialFolder == null) {
            serialFolder =
                    new DefaultSchemaNode(
                            serialFolderId, SERIAL_FOLDER_NAME, "icons/navigator/serial_group.png");
            serialFolder.setType(NodeType.SERIAL_FOLDER);
            serialFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridSerialFolderLoader();
            loader.setLevel(getLevel());
            serialFolder.setLoader(loader);
            database.addChild(serialFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                serialFolder.getChildren(monitor);
            }
        } else {
            if (serialFolder.getLoader() != null && serialFolder.getLoader().isLoaded()) {
                serialFolder.getLoader().setLoaded(false);
                serialFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add trigger folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addTriggerFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String tiggerFolderId =
                database.getId() + NODE_SEPARATOR + CubridTriggerFolderLoader.TRIGGER_FOLDER_ID;
        ICubridNode tiggerFolder = database.getChild(tiggerFolderId);
        // IsDBAUserTask
        if (tiggerFolder == null) {
            tiggerFolder =
                    new DefaultSchemaNode(
                            tiggerFolderId,
                            TRIGGER_FOLDER_NAME,
                            "icons/navigator/trigger_group.png");
            tiggerFolder.setType(NodeType.TRIGGER_FOLDER);
            tiggerFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridTriggerFolderLoader();
            loader.setLevel(getLevel());
            tiggerFolder.setLoader(loader);
            database.addChild(tiggerFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                tiggerFolder.getChildren(monitor);
            }
        } else {
            if (tiggerFolder.getLoader() != null && tiggerFolder.getLoader().isLoaded()) {
                tiggerFolder.getLoader().setLoaded(false);
                tiggerFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add Synonym folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addSynonymFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String synonymFolderId =
                database.getId() + NODE_SEPARATOR + CubridSynonymFolderLoader.SYNONYM_FOLDER_ID;
        ICubridNode synonymFolder = database.getChild(synonymFolderId);
        if (synonymFolder == null) {
            synonymFolder =
                    new DefaultSchemaNode(
                            synonymFolderId,
                            SYNONYM_FOLDER_NAME,
                            "icons/navigator/synonym_group.png");
            synonymFolder.setType(NodeType.SYNONYM_FOLDER);
            synonymFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridSynonymFolderLoader();
            loader.setLevel(getLevel());
            synonymFolder.setLoader(loader);
            database.addChild(synonymFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                synonymFolder.getChildren(monitor);
            }
        } else {
            if (synonymFolder.getLoader() != null && synonymFolder.getLoader().isLoaded()) {
                synonymFolder.getLoader().setLoaded(false);
                synonymFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add view folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addViewFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String viewsFolderId =
                database.getId() + NODE_SEPARATOR + CubridViewsFolderLoader.VIEWS_FOLDER_ID;
        ICubridNode viewsFolder = database.getChild(viewsFolderId);
        if (viewsFolder == null) {
            viewsFolder =
                    new DefaultSchemaNode(
                            viewsFolderId, VIEWS_FOLDER_NAME, "icons/navigator/schema_view.png");
            viewsFolder.setType(NodeType.VIEW_FOLDER);
            viewsFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridViewsFolderLoader();
            loader.setLevel(getLevel());
            viewsFolder.setLoader(loader);
            database.addChild(viewsFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                viewsFolder.getChildren(monitor);
            }
        } else {
            if (viewsFolder.getLoader() != null && viewsFolder.getLoader().isLoaded()) {
                viewsFolder.getLoader().setLoaded(false);
                viewsFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add table folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addTableFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String tablesFolderId =
                database.getId() + NODE_SEPARATOR + CubridTablesFolderLoader.TABLES_FOLDER_ID;
        ICubridNode tablesFolder = database.getChild(tablesFolderId);
        if (tablesFolder == null) {
            tablesFolder =
                    new DefaultSchemaNode(
                            tablesFolderId, TABLES_FOLDER_NAME, "icons/navigator/schema_table.png");
            tablesFolder.setType(NodeType.TABLE_FOLDER);
            tablesFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridTablesFolderLoader();
            loader.setLevel(getLevel());
            tablesFolder.setLoader(loader);
            database.addChild(tablesFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                tablesFolder.getChildren(monitor);
            }
        } else {
            if (tablesFolder.getLoader() != null && tablesFolder.getLoader().isLoaded()) {
                tablesFolder.getLoader().setLoaded(false);
                tablesFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Add user folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addUserFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String userFolderId = database.getId() + NODE_SEPARATOR + USERS_FOLDER_ID;
        ICubridNode userFolder = database.getChild(userFolderId);
        if (userFolder == null) {
            userFolder =
                    new DefaultSchemaNode(
                            userFolderId, USERS_FOLDER_NAME, "icons/navigator/user_group.png");
            userFolder.setType(CubridNodeType.USER_FOLDER);
            userFolder.setContainer(true);
            ICubridNodeLoader loader = new CubridDbUsersFolderLoader();
            loader.setLevel(getLevel());
            userFolder.setLoader(loader);
            database.addChild(userFolder);
            if (getLevel() == DEFINITE_LEVEL) {
                userFolder.getChildren(monitor);
            }
        } else {
            if (userFolder.getLoader() != null && userFolder.getLoader().isLoaded()) {
                userFolder.getLoader().setLoaded(false);
                userFolder.getChildren(monitor);
            }
        }
    }

    /**
     * Load replication function
     *
     * @param parent the parent node
     * @param monitor the monitor object
     * @return <code>true</code> it can be loaded;<code>false</code> otherwise
     */
    private boolean loadReplication(final ICubridNode parent, final IProgressMonitor monitor) {
        CubridDatabase database = (CubridDatabase) parent;
        ServerInfo serverInfo = parent.getServer().getServerInfo();
        ServerUserInfo serverUserInfo = serverInfo.getLoginedUserInfo();
        if (serverUserInfo == null || !serverUserInfo.isAdmin()) {
            return true;
        }
        DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
        if (dbUserInfo == null
                || dbUserInfo.getName() == null
                || !dbUserInfo.getName().trim().equalsIgnoreCase("DBA")) {
            return true;
        }
        String dbPassword = dbUserInfo.getNoEncryptPassword();
        // add for replication support,only can be supported after version 8.2.2 and in linux OS
        if (serverInfo.isSupportReplication() == 0) {
            final CheckDistributorDbTask checkDistributorDbTask =
                    new CheckDistributorDbTask(serverInfo);
            checkDistributorDbTask.setDistDbName(database.getLabel());
            checkDistributorDbTask.setDbaPassword(dbPassword);
            checkDistributorDbTask.setRunningMode(database.getRunningType() == DbRunningType.CS);

            final GetReplicationInfoTask getReplicationInfoTask =
                    new GetReplicationInfoTask(serverInfo);
            getReplicationInfoTask.setDistDbName(database.getLabel());
            getReplicationInfoTask.setDbaPassword(dbPassword);
            getReplicationInfoTask.setRunningMode(database.getRunningType() == DbRunningType.CS);

            final GetReplicatedTablesTask getReplicatedTablesTask =
                    new GetReplicatedTablesTask(serverInfo);
            getReplicatedTablesTask.setDistdbName(database.getLabel());
            getReplicatedTablesTask.setDistdbPassword(dbPassword);
            getReplicatedTablesTask.setRunningMode(database.getRunningType() == DbRunningType.CS);

            final GetReplicationParamTask getReplicationParamTask =
                    new GetReplicationParamTask(serverInfo);
            getReplicationParamTask.setDistDbName(database.getLabel());
            getReplicationParamTask.setDistDbDbaPasswd(dbPassword);
            getReplicationParamTask.setRunningMode(database.getRunningType() == DbRunningType.CS);

            final GetReplAgentStatusTask getReplAgentStatusTask =
                    new GetReplAgentStatusTask(serverInfo);
            getReplAgentStatusTask.setDbName(database.getLabel());

            monitorCancel(
                    monitor,
                    new ITask[] {
                        checkDistributorDbTask,
                        getReplicationInfoTask,
                        getReplicatedTablesTask,
                        getReplicationParamTask,
                        getReplAgentStatusTask
                    });

            checkDistributorDbTask.execute();
            if (!checkResult(checkDistributorDbTask, monitor)) {
                return false;
            }
            boolean isDistdb = checkDistributorDbTask.isDistributorDb();
            database.getDatabaseInfo().setDistributorDb(isDistdb);
            if (isDistdb) {
                getReplicationInfoTask.execute();
                if (!checkResult(getReplicationInfoTask, monitor)) {
                    return false;
                }
                ReplicationInfo replInfo = getReplicationInfoTask.getReplicationInfo();
                database.getDatabaseInfo().setReplInfo(replInfo);
                if (replInfo != null && replInfo.getDistInfo() != null) {
                    replInfo.getDistInfo().setDistDbPath(database.getDatabaseInfo().getDbDir());
                }
                if (replInfo != null
                        && replInfo.getMasterList() != null
                        && replInfo.getMasterList().size() > 0
                        && replInfo.getSlaveList() != null
                        && replInfo.getSlaveList().size() > 0) {
                    MasterInfo masterInfo = replInfo.getMasterList().get(0);
                    SlaveInfo slaveInfo = replInfo.getSlaveList().get(0);
                    getReplicatedTablesTask.setMasterdbName(masterInfo.getMasterDbName());
                    getReplicatedTablesTask.setSlavedbName(slaveInfo.getSlaveDbName());
                    getReplicatedTablesTask.execute();
                    if (!checkResult(getReplicatedTablesTask, monitor)) {
                        return false;
                    }
                    String[] classNames = getReplicatedTablesTask.getReplicatedTables();
                    boolean isReplAll = getReplicatedTablesTask.isReplicateAll();
                    if (classNames != null && classNames.length > 0) {
                        masterInfo.setReplTableList(Arrays.asList(classNames));
                    }
                    masterInfo.setReplAllTable(isReplAll);

                    getReplicationParamTask.setMasterDbName(masterInfo.getMasterDbName());
                    getReplicationParamTask.setSlaveDbName(
                            replInfo.getSlaveList().get(0).getSlaveDbName());
                    getReplicationParamTask.execute();
                    if (!checkResult(getReplicationParamTask, monitor)) {
                        return false;
                    }
                    ReplicationParamInfo paramInfo = getReplicationParamTask.getReplicationParams();
                    slaveInfo.setParamInfo(paramInfo);
                }

                getReplAgentStatusTask.execute();
                if (!checkResult(getReplAgentStatusTask, monitor)) {
                    return false;
                }
                boolean isActive = getReplAgentStatusTask.isActive();
                replInfo.getDistInfo().setAgentActive(isActive);
            }
        }
        return true;
    }

    /**
     * Check the task result
     *
     * @param task the task
     * @param monitor the IProgressMonitor object
     * @return <code>true</code>if this task is valid;<code>false</code> otherwise
     */
    private boolean checkResult(SocketTask task, final IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            setLoaded(true);
            return false;
        }
        final String msg = task.getErrorMsg();
        if (!monitor.isCanceled() && msg != null && msg.trim().length() > 0) {
            openErrorBox(msg);
            setLoaded(true);
            return false;
        }
        return true;
    }

    private void setDbCollation(CubridDatabase database) {
        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);
        if (!supportCharset || !databaseInfo.getServerInfo().isConnected()) {
            return;
        }
        GetDbCollationTask getDbCollationTask = new GetDbCollationTask(databaseInfo);
        getDbCollationTask.execute();
        if (getDbCollationTask.isSuccess()) {
            databaseInfo.setCollation(getDbCollationTask.getCollation());
        }
    }
}
