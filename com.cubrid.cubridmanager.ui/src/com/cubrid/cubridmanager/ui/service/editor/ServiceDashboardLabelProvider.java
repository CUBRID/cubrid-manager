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
package com.cubrid.cubridmanager.ui.service.editor;

import java.text.NumberFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.decorator.DecoratedImage;
import com.cubrid.common.ui.decorator.DecoratorManager;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;

/**
 *
 * CUBIRD manager navigator treeviewer label provider
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class ServiceDashboardLabelProvider extends
		LabelProvider implements
		ITableLabelProvider,
		ITableColorProvider {

	private static ImageDescriptor imgDecMaster = CubridManagerUIPlugin.getImageDescriptor("icons/navigator/master.png");
	private static ImageDescriptor imgDecSlave = CubridManagerUIPlugin.getImageDescriptor("icons/navigator/slave.png");
	private static ImageDescriptor imgDecReplica = CubridManagerUIPlugin.getImageDescriptor("icons/navigator/replica.gif");
	private static ImageDescriptor imgDecUnknow = CubridManagerUIPlugin.getImageDescriptor("icons/navigator/unknow.gif");
	private static DecoratorManager decoratorManager = new DecoratorManager();
	private static NumberFormat formater = NumberFormat.getInstance();

	public ServiceDashboardLabelProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
	 */
	public Color getForeground(Object element, int columnIndex) {
		return ResourceManager.getColor(SWT.COLOR_BLACK);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof CubridDatabase) {
			CubridDatabase database = (CubridDatabase) element;
			if (database != null && database.getServer() != null) {
				DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, true);
				if (editorConfig != null
						&& editorConfig.getBackGround() != null) {
					RGB rgb = editorConfig.getBackGround();
					return ResourceManager.getColor(EditorConstance.convertDeepBackground(rgb));
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		String iconPath = "";

		if (columnIndex != 0 || element == null) {
			return null;
		}

		if (element instanceof ServiceDashboardInfo) {
			CubridServer server = ((ServiceDashboardInfo) element).getServer();
			if (server.isConnected()) {
				iconPath = server.getConnectedIconPath();
			} else {
				iconPath = server.getDisConnectedIconPath();
			}
			Image serverImage = CubridManagerUIPlugin.getImage(iconPath);
			return decorateServerImgae(serverImage, server);
		} else if (element instanceof CubridDatabase) {
			CubridDatabase database = (CubridDatabase) element;
			if (database.getRunningType() == DbRunningType.STANDALONE
					&& database.isLogined()) {
				iconPath = database.getStopAndLoginIconPath();
			} else if (database.getRunningType() == DbRunningType.STANDALONE
					&& !database.isLogined()) {
				iconPath = database.getStopAndLogoutIconPath();
			} else if (database.getRunningType() == DbRunningType.CS
					&& database.isLogined()) {
				iconPath = database.getStartAndLoginIconPath();
			} else if (database.getRunningType() == DbRunningType.CS
					&& !database.isLogined()) {
				iconPath = database.getStartAndLogoutIconPath();
			}
		} else if (element instanceof CubridGroupNode) {
			CubridGroupNode group = (CubridGroupNode) element;
			iconPath = group.getIconPath();
		}
		if (iconPath != null && iconPath.length() > 0) {
			return CubridManagerUIPlugin.getImage(iconPath.trim());
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element != null && (element instanceof ServiceDashboardInfo ||
				element instanceof CubridGroupNode)) {
			if (element instanceof ServiceDashboardInfo) {
				ServiceDashboardInfo sDashInfo = (ServiceDashboardInfo) element;
				CubridServer server = sDashInfo.getServer();

				switch (columnIndex) {
				case 0:
					return decorateServerText(server.getName(), server);
				case 1:
					return server.getHostAddress() == null ? ""
							: server.getHostAddress();
				case 2:
					return server.getMonPort() == null ? ""
							: server.getMonPort();
				case 3:
					return server.getUserName() == null ? ""
							: server.getUserName();
				case 4:
					int freeDataPerc = sDashInfo.getFreeDataPerc();
					String freeDataPercText = sDashInfo.getServer().isConnected() && freeDataPerc >= 0
							? freeDataPerc + "%" : "-";
					return freeDataPercText;
				case 5:
					int freeIndexPerc = sDashInfo.getFreeIndexPerc();
					String freeIndexPercText = sDashInfo.getServer().isConnected() && freeIndexPerc >= 0
							? freeIndexPerc + "%" : "-";
					return freeIndexPercText;
				case 6:
					int freeTempPerc = sDashInfo.getFreeTempPerc();
					String freeTempPercText = sDashInfo.getServer().isConnected() && freeTempPerc >= 0
							? freeTempPerc + "%" : "-";
					return freeTempPercText;
				case 7:
					int freeGenericPerc = sDashInfo.getFreeGenericPerc();
					String freeGenericPercText = sDashInfo.getServer().isConnected() && freeGenericPerc >= 0
							? freeGenericPerc + "%" : "-";
					return freeGenericPercText;
				case 8:
					int tps = sDashInfo.getServerTps();
					return sDashInfo.getServer().isConnected() ? Integer.toString(tps):"-";
				case 9:
					int qps = sDashInfo.getServerQps();
					return sDashInfo.getServer().isConnected() ? Integer.toString(qps):"-";
				case 10:
					int errorQ = sDashInfo.getServerErrorQ();
					return  sDashInfo.getServer().isConnected() ? Integer.toString(errorQ):"-";
				case 11:
					double memUsed = sDashInfo.getMemUsed();
					double memTotal = sDashInfo.getMemTotal();
					return sDashInfo.getServer().isConnected() ? formater.format(StringUtil.convertToG(new Double(
							memUsed).longValue() * 1024))
							+ "GB / " + formater.format(StringUtil.convertToG(new Double(
									memTotal).longValue() * 1024)) + "GB":"- / -";
				case 12:
					long freespaceOnStorage = sDashInfo.getFreespaceOnStorage();
					return sDashInfo.getServer().isConnected() ? getSpaceDesc(freespaceOnStorage):"-";
				case 13:
					double cpuUsed = sDashInfo.getCpuUsed();
					return sDashInfo.getServer().isConnected() ? cpuUsed+"%":"-";
				case 14:
					return sDashInfo.getServer().isConnected() ? "Yes" : "No";
				case 15:
					String serverVersion = sDashInfo.getServerVersion();
					return sDashInfo.getServer().isConnected() ? serverVersion:"-";
				case 16:
					String brokerPort = sDashInfo.getBrokerPort();
					return sDashInfo.getServer().isConnected() ? brokerPort:"-";
				}

			} else if (element instanceof CubridGroupNode) {
				CubridGroupNode group = (CubridGroupNode) element;
				switch (columnIndex) {
				case 0:
					return group.getName();
				}
			}
		}
		return "";
	}

	private String getSpaceDesc(long spaceSize) { // FIXME extract
		StringBuilder sb = new StringBuilder();
		if (spaceSize >= 1024 * 1024 * 1024) {
			sb.append(formater.format(StringUtil.convertToG(spaceSize))).append(
					"GB");
			return sb.toString();
		} else if (spaceSize >= 1024 * 1024) {
			sb.append(formater.format(StringUtil.convertToM(spaceSize))).append(
					"MB");
			return sb.toString();
		} else if (spaceSize >= 1024) {
			sb.append(formater.format((float) spaceSize / 1024f / 1024f)).append(
					"MB");
			return sb.toString();
		}
		sb.append(spaceSize).append("B");
		return sb.toString();
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
			if(haHostStatusInfo != null) {
				if (HostStatusType.MASTER.equals(haHostStatusInfo.getStatusType())) {
					DecoratedImage decoratedImage = decoratorManager.decorate(
							baseImage, "ServerConnected", null, null, null, null,
							null, null, imgDecMaster, "Master");
					return decoratedImage.getDecoratedImage();
				}

				if (HostStatusType.SLAVE.equals(haHostStatusInfo.getStatusType())) {
					DecoratedImage decoratedImage = decoratorManager.decorate(
							baseImage, "ServerConnected", null, null, null, null,
							null, null, imgDecSlave, "Slave");
					return decoratedImage.getDecoratedImage();
				}

				if (HostStatusType.REPLICA.equals(haHostStatusInfo.getStatusType())) {
					DecoratedImage decoratedImage = decoratorManager.decorate(
							baseImage, "ServerConnected", null, null, null, null,
							null, null, imgDecReplica, "Replic");
					return decoratedImage.getDecoratedImage();
				}

				if (HostStatusType.UNKNOWN.equals(haHostStatusInfo.getStatusType())) {
					DecoratedImage decoratedImage = decoratorManager.decorate(
							baseImage, "ServerConnected", null, null, null, null,
							null, null, imgDecUnknow, "Unknow");
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
			if(haHostStatusInfo != null) {
				if (HostStatusType.MASTER.equals(haHostStatusInfo.getStatusType())) {
					sb.append(" - [").append(com.cubrid.cubridmanager.ui.common.Messages.lblServerMaster).append("]");
				}

				if (HostStatusType.SLAVE.equals(haHostStatusInfo.getStatusType())) {
					sb.append(" - [").append(com.cubrid.cubridmanager.ui.common.Messages.lblServerSlave).append("]");
				}

				if (HostStatusType.REPLICA.equals(haHostStatusInfo.getStatusType())) {
					sb.append(" - [").append(com.cubrid.cubridmanager.ui.common.Messages.lblServerReplica).append("]");
				}

				if (HostStatusType.UNKNOWN.equals(haHostStatusInfo.getStatusType())) {
					sb.append(" - [").append(com.cubrid.cubridmanager.ui.common.Messages.lblServerUnknow).append("]");
				}
			}

		}
		return sb.toString();
	}

	public void dispose() {
		super.dispose();
		decoratorManager.dispose();
	}

}
