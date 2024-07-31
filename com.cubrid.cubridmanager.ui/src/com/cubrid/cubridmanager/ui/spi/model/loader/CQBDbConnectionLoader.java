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

import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.Messages;
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
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load the children of CUBRID database connection,these children
 * include Users,Job automation,Database space,Schema,Stored procedure,Trigger folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CQBDbConnectionLoader extends CubridNodeLoader {

    private static final String TABLES_FOLDER_NAME = Messages.msgTablesFolderName;
    private static final String VIEWS_FOLDER_NAME = Messages.msgViewsFolderName;
    private static final String SP_FOLDER_NAME = Messages.msgSpFolderName;
    private static final String TRIGGER_FOLDER_NAME = Messages.msgTriggerFolderName;
    private static final String SYNONYM_FOLDER_NAME = Messages.msgSynonymFolderName;
    private static final String SERIAL_FOLDER_NAME = Messages.msgSerialFolderName;
    private static final String USERS_FOLDER_NAME = Messages.msgUserFolderName;

    public static final String USERS_FOLDER_ID = "Users";
    public static final String VIEWS_FOLDER_ID = "Views";
    public static final String SP_FOLDER_ID = "Stored procedure";
    public static final String TRIGGER_FOLDER_ID = "Triggers";
    public static final String SYNONYM_FOLDER_ID = "Synonym";
    public static final String SERIAL_FOLDER_ID = "Serials";

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
            // add tables folder
            addTableFolder(monitor, database);
            // add views folder
            addViewFolder(monitor, database);
            // add serials folder
            addSerialFolder(monitor, database);
            // add users folder
            addUserFolder(monitor, database);
            // add triggers folder
            addTriggerFolder(monitor, database);
            // add synonyms folder
            if (database.getDatabaseInfo().isSupportSynonym()) {
                addSynonymFolder(monitor, database);
            }
            // add stored procedure folder
            addProcedureFolder(monitor, database);

            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Add procedure folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addProcedureFolder(final IProgressMonitor monitor, CubridDatabase database) {
        String spFolderId = database.getId() + NODE_SEPARATOR + SP_FOLDER_ID;
        ICubridNode spFolder = database.getChild(spFolderId);
        if (spFolder == null) {
            spFolder =
                    new DefaultSchemaNode(
                            spFolderId,
                            SP_FOLDER_NAME,
                            SP_FOLDER_NAME,
                            "icons/navigator/procedure_group.png");
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
        String serialFolderId = database.getId() + NODE_SEPARATOR + SERIAL_FOLDER_ID;
        ICubridNode serialFolder = database.getChild(serialFolderId);
        if (serialFolder == null) {
            serialFolder =
                    new DefaultSchemaNode(
                            serialFolderId,
                            SERIAL_FOLDER_NAME,
                            SP_FOLDER_NAME,
                            "icons/navigator/serial_group.png");
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
        if (!database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority()) {
            return;
        }
        String tiggerFolderId = database.getId() + NODE_SEPARATOR + TRIGGER_FOLDER_ID;
        ICubridNode tiggerFolder = database.getChild(tiggerFolderId);
        if (tiggerFolder == null) {
            tiggerFolder =
                    new DefaultSchemaNode(
                            tiggerFolderId,
                            TRIGGER_FOLDER_NAME,
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
     * Add synonym folder
     *
     * @param monitor the IProgressMonitor
     * @param database the CubridDatabase
     */
    private void addSynonymFolder(final IProgressMonitor monitor, CubridDatabase database) {
        if (!database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority()) {
            return;
        }
        String synoymFolderId = database.getId() + NODE_SEPARATOR + SYNONYM_FOLDER_ID;
        ICubridNode synonymFolder = database.getChild(synoymFolderId);
        if (synonymFolder == null) {
            synonymFolder =
                    new DefaultSchemaNode(
                            synoymFolderId,
                            SYNONYM_FOLDER_NAME,
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
        String viewsFolderId = database.getId() + NODE_SEPARATOR + VIEWS_FOLDER_ID;
        ICubridNode viewsFolder = database.getChild(viewsFolderId);
        if (viewsFolder == null) {
            viewsFolder =
                    new DefaultSchemaNode(
                            viewsFolderId,
                            VIEWS_FOLDER_NAME,
                            VIEWS_FOLDER_NAME,
                            "icons/navigator/schema_view.png");
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
                            userFolderId,
                            USERS_FOLDER_NAME,
                            USERS_FOLDER_NAME,
                            "icons/navigator/user_group.png");
            userFolder.setType(NodeType.USER_FOLDER);
            userFolder.setContainer(true);
            ICubridNodeLoader loader = new CQBDbUsersFolderLoader();
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
                            tablesFolderId,
                            TABLES_FOLDER_NAME,
                            TABLES_FOLDER_NAME,
                            "icons/navigator/schema_table.png");
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
}
