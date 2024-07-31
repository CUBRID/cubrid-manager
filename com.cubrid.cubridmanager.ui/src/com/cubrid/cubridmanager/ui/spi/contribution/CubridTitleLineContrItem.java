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
package com.cubrid.cubridmanager.ui.spi.contribution;

import com.cubrid.common.ui.spi.contribution.TitleLineContrItem;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.List;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * CUBRID Manager title line contribution item
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-9 created by pangqiren
 */
public class CubridTitleLineContrItem extends TitleLineContrItem {

    /**
     * Get title of CUBRID manager application for navigator
     *
     * @param cubridNode the ICubridNode object
     * @return the title
     */
    protected String getTitleForNavigator(ICubridNode cubridNode) {
        if (cubridNode == null) {
            return "";
        }
        String title = cubridNode.getLabel();

        CubridServer server = cubridNode.getServer();
        String serverTitle = server == null ? null : server.getLabel();
        if (server != null && server.isConnected()) {
            ServerInfo serverInfo = server.getServerInfo();
            ServerUserInfo userInfo = serverInfo == null ? null : serverInfo.getLoginedUserInfo();
            if (userInfo != null
                    && userInfo.getUserName() != null
                    && userInfo.getUserName().trim().length() > 0) {
                serverTitle = userInfo.getUserName() + "@" + serverTitle;
            }
            String monPort = server.getMonPort();
            if (monPort != null && monPort.trim().length() > 0) {
                serverTitle = serverTitle + ":" + monPort;
            }
        }

        StringBuffer dbTitleBuffer = new StringBuffer("");
        CubridDatabase database =
                cubridNode instanceof ISchemaNode ? ((ISchemaNode) cubridNode).getDatabase() : null;
        if (database != null && database.getDatabaseInfo() != null) {
            DatabaseInfo dbInfo = database.getDatabaseInfo();
            DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
            String userName = dbUserInfo == null ? null : dbUserInfo.getName();
            dbTitleBuffer =
                    (userName != null && userName.trim().length() > 0)
                            ? dbTitleBuffer.append(userName).append("@").append(dbInfo.getDbName())
                            : dbTitleBuffer;
            String brokerPort = QueryOptions.getBrokerPort(dbInfo);
            BrokerInfos brokerInfos = database.getServer().getServerInfo().getBrokerInfos();
            List<BrokerInfo> brokerInfoList =
                    brokerInfos == null || brokerInfos.getBorkerInfoList() == null
                            ? null
                            : brokerInfos.getBorkerInfoList().getBrokerInfoList();

            boolean isExist = false;
            for (int i = 0; brokerInfoList != null && i < brokerInfoList.size(); i++) {
                BrokerInfo brokerInfo = brokerInfoList.get(i);
                if (brokerPort != null && brokerPort.equals(brokerInfo.getPort())) {
                    isExist = true;
                    String status =
                            brokerInfo.getState() == null
                                            || brokerInfo.getState().trim().equalsIgnoreCase("OFF")
                                    ? "OFF"
                                    : "ON";
                    String text =
                            brokerInfo.getName() + "[" + brokerInfo.getPort() + "/" + status + "]";
                    dbTitleBuffer.append(":").append(text);
                    break;
                }
            }
            if (!isExist && brokerPort != null && brokerPort.trim().length() > 0) {
                dbTitleBuffer.append(":").append(brokerPort);
            }
            String charset = database.getDatabaseInfo().getCharSet();
            if (charset != null && charset.trim().length() > 0) {
                dbTitleBuffer.append(":charset=").append(charset);
            }
        }

        if (serverTitle != null && serverTitle.trim().length() > 0) {
            title = serverTitle;
        }
        if (dbTitleBuffer != null && dbTitleBuffer.toString().trim().length() > 0) {
            title = serverTitle + " / " + dbTitleBuffer.toString();
        }
        return title;
    }

    /**
     * Get title of application for query editor
     *
     * @param cubridNode ICubridNode
     * @return String
     */
    protected String getTitleForQueryEditor(ICubridNode cubridNode) {
        return getTitleForNavigator(cubridNode);
    }

    /**
     * Get the title of view or editor(not including query editor)
     *
     * @param cubridNode the ICubridNode object
     * @param workbenchPart the IWorkbenchPart object
     * @return the title
     */
    protected String getTitleForViewOrEdit(ICubridNode cubridNode, IWorkbenchPart workbenchPart) {
        String serverTitle = "";
        if (cubridNode != null
                && cubridNode.getServer() != null
                && cubridNode.getServer().isConnected()) {
            CubridServer server = cubridNode.getServer();
            serverTitle = server.getLabel();
            ServerUserInfo userInfo = cubridNode.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo != null
                    && userInfo.getUserName() != null
                    && userInfo.getUserName().trim().length() > 0) {
                serverTitle = userInfo.getUserName() + "@" + serverTitle;
            }
            String monPort = cubridNode.getServer().getMonPort();
            if (monPort != null && monPort.trim().length() > 0) {
                serverTitle = serverTitle + ":" + monPort;
            }
        }
        String partTitle = "";
        if (cubridNode != null && workbenchPart == null) {
            if (null != cubridNode.getViewId()) {
                IViewPart viewPart = LayoutUtil.getViewPart(cubridNode, cubridNode.getViewId());
                if (viewPart != null) {
                    partTitle = viewPart.getTitle();
                }
            }
            if (null != cubridNode.getEditorId()) {
                IEditorPart editorPart =
                        LayoutUtil.getEditorPart(cubridNode, cubridNode.getEditorId());
                if (editorPart != null) {
                    partTitle = editorPart.getTitle();
                }
            }
        } else if (workbenchPart != null) {
            partTitle = workbenchPart.getTitle();
        }

        String title = "";
        if (serverTitle != null && serverTitle.trim().length() > 0) {
            title = serverTitle;
        }
        if (partTitle != null && partTitle.trim().length() > 0) {
            if (title != null && title.trim().length() > 0) {
                title += " / ";
            }
            title += partTitle;
        }
        return title;
    }
}
