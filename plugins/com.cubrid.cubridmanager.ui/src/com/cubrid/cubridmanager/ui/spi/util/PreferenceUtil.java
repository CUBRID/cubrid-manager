/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.spi.util;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.common.preference.NullCategoryPreferencePage;
import com.cubrid.common.ui.query.preference.QueryOptionPreferencePage;
import com.cubrid.common.ui.spi.dialog.CMPreferenceDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.control.BrokerParameterPropertyPage;
import com.cubrid.cubridmanager.ui.broker.control.BrokersParameterPropertyPage;
import com.cubrid.cubridmanager.ui.common.control.DatabaseConfigPropertyPage;
import com.cubrid.cubridmanager.ui.common.control.HAPropertyPage;
import com.cubrid.cubridmanager.ui.common.control.ManagerServerPropertyPage;
import com.cubrid.cubridmanager.ui.common.control.ServerConfigPropertyPage;
import com.cubrid.cubridmanager.ui.common.control.ServicePropertyPage;
import com.cubrid.cubridmanager.ui.cubrid.database.control.DatabaseConnectionPropertyPage;
import com.cubrid.cubridmanager.ui.shard.control.ShardParameterPropertyPage;
import com.cubrid.cubridmanager.ui.shard.control.ShardsParameterPropertyPage;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is responsible to create the common dialog with perference
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class PreferenceUtil {
    private PreferenceUtil() {}

    /**
     * Create the category preference node
     *
     * @param title String preference node name and title
     * @param id String preference node id
     * @param msg String detail message
     * @return PreferenceNode
     */
    private static PreferenceNode createCategoryNode(String title, String id, String msg) {
        NullCategoryPreferencePage categoryPrefPage = new NullCategoryPreferencePage(title, msg);
        PreferenceNode categoryNode = new PreferenceNode(id);
        categoryNode.setPage(categoryPrefPage);
        return categoryNode;
    }

    /**
     * Create property dialog related with CUBRID node
     *
     * @param parentShell the parent shell
     * @param node the ICubridNode object
     * @return the Dialog object
     */
    public static Dialog createPropertyDialog(Shell parentShell, ICubridNode node) {
        PreferenceManager mgr = new PreferenceManager();
        String type = node.getType();
        ServerType serverType = node.getServer().getServerInfo().getServerType();
        if (CubridNodeType.SERVER.equals(type)) {
            // cubrid manager server property
            ServerConfigPropertyPage cmServerPropertyPage =
                    new ServerConfigPropertyPage(node, Messages.msgCmServerPropertyPageName);
            PreferenceNode cmServerNode = new PreferenceNode(Messages.msgCmServerPropertyPageName);
            cmServerNode.setPage(cmServerPropertyPage);
            mgr.addToRoot(cmServerNode);

            PreferenceNode categoryNode =
                    createCategoryNode(
                            Messages.msgConfigureParameterPageName,
                            Messages.msgConfigureParameterPageName,
                            Messages.msgDetailConfigureParameter);
            mgr.addToRoot(categoryNode);

            if (serverType == ServerType.BOTH || serverType == ServerType.DATABASE) {
                // service node
                ServicePropertyPage servicePorpertyPage =
                        new ServicePropertyPage(node, Messages.msgServicePropertyPageName);
                PreferenceNode serviceNode =
                        new PreferenceNode(Messages.msgServicePropertyPageName);
                serviceNode.setPage(servicePorpertyPage);
                categoryNode.add(serviceNode);

                // database server node
                DatabaseConfigPropertyPage databaseServerPorpertyPage =
                        new DatabaseConfigPropertyPage(
                                node, Messages.msgDatabaseServerCommonPropertyPageName, true);
                PreferenceNode databaseServerNode =
                        new PreferenceNode(Messages.msgDatabaseServerCommonPropertyPageName);
                databaseServerNode.setPage(databaseServerPorpertyPage);
                categoryNode.add(databaseServerNode);

                // HA configuraiton
                if (CompatibleUtil.isSupportNewHAConfFile(node.getServer().getServerInfo())) {
                    HAPropertyPage haPropertyPage =
                            new HAPropertyPage(node, Messages.msgHAPropertyPageName);
                    PreferenceNode haNode = new PreferenceNode(Messages.msgHAPropertyPageName);
                    haNode.setPage(haPropertyPage);
                    categoryNode.add(haNode);
                }
            }

            if (serverType == ServerType.BOTH || serverType == ServerType.BROKER) {
                // brokers node
                BrokersParameterPropertyPage brokersParameterPorpertyPage =
                        new BrokersParameterPropertyPage(node, Messages.msgBrokerPropertyPageName);
                PreferenceNode brokersParameterNode =
                        new PreferenceNode(Messages.msgBrokerPropertyPageName);
                brokersParameterNode.setPage(brokersParameterPorpertyPage);
                categoryNode.add(brokersParameterNode);
            }

            // mananger node
            ManagerServerPropertyPage managerPorpertyPage =
                    new ManagerServerPropertyPage(node, Messages.msgManagerPropertyPageName);
            PreferenceNode managerNode = new PreferenceNode(Messages.msgManagerPropertyPageName);
            managerNode.setPage(managerPorpertyPage);
            categoryNode.add(managerNode);

            // query editor node
            if (serverType == ServerType.BOTH || serverType == ServerType.DATABASE) {
                CubridServer server = node.getServer();
                QueryOptionPreferencePage queryEditorPage = new QueryOptionPreferencePage(server);
                PreferenceNode queryEditorNode =
                        new PreferenceNode(Messages.msgQueryPropertyPageName);
                queryEditorNode.setPage(queryEditorPage);
                mgr.addToRoot(queryEditorNode);
            }
        } else if (CubridNodeType.DATABASE_FOLDER.equals(type)) {
            // database server node
            DatabaseConfigPropertyPage databaseServerPorpertyPage =
                    new DatabaseConfigPropertyPage(
                            node, Messages.msgDatabaseServerCommonPropertyPageName, true);
            PreferenceNode databaseServerNode =
                    new PreferenceNode(Messages.msgDatabaseServerCommonPropertyPageName);
            databaseServerNode.setPage(databaseServerPorpertyPage);
            mgr.addToRoot(databaseServerNode);
        } else if (CubridNodeType.DATABASE.equals(type)) {
            // database parameter
            DatabaseConfigPropertyPage databaseParameterPorpertyPage =
                    new DatabaseConfigPropertyPage(
                            node, Messages.msgDatabaseServerPropertyPageName, false);
            PreferenceNode databaseParameterNode =
                    new PreferenceNode(Messages.msgDatabaseServerPropertyPageName);
            databaseParameterNode.setPage(databaseParameterPorpertyPage);
            // database query
            CubridDatabase database = (CubridDatabase) node;
            DatabaseConnectionPropertyPage page =
                    new DatabaseConnectionPropertyPage(database, Messages.msgQueryPropertyPageName);
            PreferenceNode queryNode = new PreferenceNode(Messages.msgQueryPropertyPageName);
            queryNode.setPage(page);
            mgr.addToRoot(queryNode);
            mgr.addToRoot(databaseParameterNode);
        } else if (CubridNodeType.BROKER_FOLDER.equals(type)) {
            // brokers node
            BrokersParameterPropertyPage brokersParameterPorpertyPage =
                    new BrokersParameterPropertyPage(node, Messages.msgBrokerPropertyPageName);
            PreferenceNode brokersParameterNode =
                    new PreferenceNode(Messages.msgBrokerPropertyPageName);
            brokersParameterNode.setPage(brokersParameterPorpertyPage);
            mgr.addToRoot(brokersParameterNode);
        }

        if (CubridNodeType.BROKER.equals(type)) {
            BrokerParameterPropertyPage brokerParameterPorpertyPage =
                    new BrokerParameterPropertyPage(node, node.getLabel());
            PreferenceNode brokerParameterNode = new PreferenceNode(node.getLabel());
            brokerParameterNode.setPage(brokerParameterPorpertyPage);
            mgr.addToRoot(brokerParameterNode);
        } else if (CubridNodeType.SHARD_FOLDER.equals(type)) {
            ShardsParameterPropertyPage shardsParameterPorpertyPage =
                    new ShardsParameterPropertyPage(node, node.getName());
            PreferenceNode shardsParameterNode = new PreferenceNode(Messages.msgShardsFolderName);
            shardsParameterNode.setPage(shardsParameterPorpertyPage);
            mgr.addToRoot(shardsParameterNode);
        } else if (CubridNodeType.SHARD.equals(type)) {
            ShardParameterPropertyPage shardParameterPorpertyPage =
                    new ShardParameterPropertyPage(node, node.getName());
            PreferenceNode shardParameterNode = new PreferenceNode(node.getName());
            shardParameterNode.setPage(shardParameterPorpertyPage);
            mgr.addToRoot(shardParameterNode);
        }

        CMPreferenceDialog dlg =
                new CMPreferenceDialog(parentShell, mgr, Messages.titlePropertiesDialog);
        dlg.setPreferenceStore(CubridManagerUIPlugin.getDefault().getPreferenceStore());
        return dlg;
    }
}
