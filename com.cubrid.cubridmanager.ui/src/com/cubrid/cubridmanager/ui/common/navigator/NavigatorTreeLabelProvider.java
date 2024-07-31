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
package com.cubrid.cubridmanager.ui.common.navigator;

import com.cubrid.common.ui.decorator.DecoratedImage;
import com.cubrid.common.ui.decorator.DecoratorManager;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridBroker;
import com.cubrid.cubridmanager.ui.spi.model.CubridBrokerFolder;
import com.cubrid.cubridmanager.ui.spi.model.CubridShard;
import com.cubrid.cubridmanager.ui.spi.model.CubridShardFolder;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * CUBIRD manager navigator treeviewer label provider
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class NavigatorTreeLabelProvider extends LabelProvider
        implements IStyledLabelProvider, IColorProvider {

    private static ImageDescriptor imgDecMaster =
            CubridManagerUIPlugin.getImageDescriptor("icons/navigator/master.png");
    private static ImageDescriptor imgDecSlave =
            CubridManagerUIPlugin.getImageDescriptor("icons/navigator/slave.png");
    private static ImageDescriptor imgDecReplica =
            CubridManagerUIPlugin.getImageDescriptor("icons/navigator/replica.gif");
    private static ImageDescriptor imgDecUnknow =
            CubridManagerUIPlugin.getImageDescriptor("icons/navigator/unknow.gif");
    private static DecoratorManager decoratorManager = new DecoratorManager();

    public NavigatorTreeLabelProvider() {}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getImage(Object element) {
        String iconPath = "";
        if (element instanceof CubridServer) {
            CubridServer server = (CubridServer) element;
            if (server.isConnected()) {
                iconPath = server.getConnectedIconPath();
            } else {
                iconPath = server.getDisConnectedIconPath();
            }
            Image serverImage = CubridManagerUIPlugin.getImage(iconPath);
            return decorateServerImgae(serverImage, server);
        } else if (element instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) element;
            if (database.getRunningType() == DbRunningType.STANDALONE && database.isLogined()) {
                iconPath = database.getStopAndLoginIconPath();
            } else if (database.getRunningType() == DbRunningType.STANDALONE
                    && !database.isLogined()) {
                iconPath = database.getStopAndLogoutIconPath();
            } else if (database.getRunningType() == DbRunningType.CS && database.isLogined()) {
                iconPath = database.getStartAndLoginIconPath();
            } else if (database.getRunningType() == DbRunningType.CS && !database.isLogined()) {
                iconPath = database.getStartAndLogoutIconPath();
            }
            Image databaseImage = CubridManagerUIPlugin.getImage(iconPath);
            return decorateDatabaseImgae(databaseImage, database);
        } else if (element instanceof CubridBrokerFolder) {
            CubridBrokerFolder brokerFolder = (CubridBrokerFolder) element;
            if (brokerFolder.isRunning()) {
                iconPath = brokerFolder.getStartedIconPath();
            } else {
                iconPath = brokerFolder.getStopedIconPath();
            }
        } else if (element instanceof CubridBroker) {
            CubridBroker broker = (CubridBroker) element;
            if (broker.isRunning()) {
                iconPath = broker.getStartedIconPath();
            } else {
                iconPath = broker.getStopedIconPath();
            }
        } else if (element instanceof CubridShardFolder) {
            // TODO shard
            CubridShardFolder shardFolder = (CubridShardFolder) element;
            // if (shardFolder.isEnable()) {
            if (shardFolder.isRunning()) {
                iconPath = shardFolder.getStartedIconPath();
            } else {
                iconPath = shardFolder.getStopedIconPath();
            }
            // } else {
            // iconPath = shardFolder.getDisableIconPath();
            // }
        } else if (element instanceof CubridShard) {
            CubridShard shard = (CubridShard) element;
            if (shard.isRunning()) {
                iconPath = shard.getStartedIconPath();
            } else {
                iconPath = shard.getStopedIconPath();
            }
        } else if (element instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) element;
            iconPath = node.getIconPath();
        }
        if (iconPath != null && iconPath.length() > 0) {
            return CubridManagerUIPlugin.getImage(iconPath.trim());
        }

        return super.getImage(element);
    }

    public String getText(Object element) {
        if (element instanceof ICubridNode) {
            if (element instanceof CubridDatabase) {
                CubridDatabase database = (CubridDatabase) element;
                StringBuffer sbLabel = new StringBuffer();
                sbLabel.append(((ICubridNode) element).getLabel());
                DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, true);
                if (editorConfig != null
                        && editorConfig.getDatabaseComment() != null
                        && editorConfig.getDatabaseComment().length() > 0) {
                    sbLabel.append("(").append(editorConfig.getDatabaseComment()).append(")");
                    return sbLabel.toString();
                }
            } else if (element instanceof CubridBroker) {
                CubridBroker broker = (CubridBroker) element;
                BrokerInfo brokerInfo = broker.getBrokerInfo();
                if (brokerInfo == null || brokerInfo.getAccess_mode() == null) {
                    return broker.getName();
                }
                return broker.getName()
                        + " ("
                        + brokerInfo.getPort()
                        + ","
                        + brokerInfo.getAccess_mode()
                        + ")";
            } else if (element instanceof CubridServer) {
                CubridServer server = (CubridServer) element;
                return decorateServerText(server.getLabel(), server);
            }
            return ((ICubridNode) element).getLabel();
        } else if (element instanceof PendingUpdateAdapter) {
            return com.cubrid.common.ui.common.Messages.msgLoading;
        }
        return element == null ? "" : element.toString();
    }

    /**
     * Decorate the server image
     *
     * @param baseImage
     * @param server
     * @return
     */
    private Image decorateServerImgae(Image baseImage, CubridServer server) {
        if (server.isConnected()) {
            HAHostStatusInfo haHostStatusInfo = server.getServerInfo().getHaHostStatusInfo();
            if (haHostStatusInfo != null) {
                if (HostStatusType.REPLICA.equals(haHostStatusInfo.getStatusType())) {
                    DecoratedImage decoratedImage =
                            decoratorManager.decorate(
                                    baseImage,
                                    "ServerConnected",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    imgDecReplica,
                                    "Replic");
                    return decoratedImage.getDecoratedImage();
                }

                if (HostStatusType.UNKNOWN.equals(haHostStatusInfo.getStatusType())) {
                    DecoratedImage decoratedImage =
                            decoratorManager.decorate(
                                    baseImage,
                                    "ServerConnected",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    imgDecUnknow,
                                    "Unknow");
                    return decoratedImage.getDecoratedImage();
                }
            }
        }

        return baseImage;
    }

    /**
     * Decorate the server lable
     *
     * @param label
     * @param server
     * @return
     */
    private String decorateServerText(String label, CubridServer server) {
        StringBuilder sb = new StringBuilder();
        sb.append(label);

        if (server.isConnected()) {
            HAHostStatusInfo haHostStatusInfo = server.getServerInfo().getHaHostStatusInfo();
            if (haHostStatusInfo != null) {
                if (HostStatusType.REPLICA.equals(haHostStatusInfo.getStatusType())) {
                    sb.append(" - [").append(Messages.lblServerReplica).append("]");
                }

                if (HostStatusType.UNKNOWN.equals(haHostStatusInfo.getStatusType())) {
                    sb.append(" - [").append(Messages.lblServerUnknow).append("]");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Decorate the database image
     *
     * @param label
     * @param server
     * @return
     */
    private Image decorateDatabaseImgae(Image baseImage, CubridDatabase database) {
        CubridServer server = database.getServer();

        HAHostStatusInfo haHostStatusInfo = server.getServerInfo().getHaHostStatusInfo();
        HADatabaseStatusInfo haDatabaseStatusInfo =
                HAUtil.getHADatabaseStatusInfo(
                        database.getName(), haHostStatusInfo, server.getServerInfo());

        List<String> haNodeList = HAUtil.getAllHaDBList(server.getServerInfo());
        server.getLoader().setLoaded(true);
        if (haNodeList.contains(database.getName())) {
            DBStatusType statusType = haDatabaseStatusInfo.getStatusType();

            if (DBStatusType.ACTIVE.equals(statusType)) {
                DecoratedImage decoratedImage =
                        decoratorManager.decorate(
                                baseImage,
                                "HADatabase",
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                imgDecMaster,
                                "Active");
                return decoratedImage.getDecoratedImage();
            }

            if (DBStatusType.STANDBY.equals(statusType)) {
                DecoratedImage decoratedImage =
                        decoratorManager.decorate(
                                baseImage,
                                "HADatabase",
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                imgDecSlave,
                                "Standby");
                return decoratedImage.getDecoratedImage();
            }
        }

        return baseImage;
    }

    public void dispose() {
        super.dispose();
        decoratorManager.dispose();
    }

    public StyledString getStyledText(Object element) {
        String text = getText(element);
        StyledString styledString = new StyledString(text);

        return styledString;
    }

    public Color getForeground(Object element) {
        return ResourceManager.getColor(SWT.COLOR_BLACK);
    }

    public Color getBackground(Object element) {
        if (element instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) element;
            if (database != null && database.getServer() != null) {
                DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, true);
                if (editorConfig != null && editorConfig.getBackGround() != null) {
                    RGB rgb = editorConfig.getBackGround();
                    return ResourceManager.getColor(EditorConstance.convertDeepBackground(rgb));
                }
            }
        }
        return null;
    }
}
