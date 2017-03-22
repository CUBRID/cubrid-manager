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
package com.cubrid.cubridmanager.core.common.task;

import java.util.List;

import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo.InterfaceVersion;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * This task is responsible to communite with CUBRID Manager server by
 * monitoring port.because every server only permit to have a monitroing
 * socket,hence this task is responsible to handle with all requests which use
 * this monitoring port.
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class MonitoringTask extends SocketTask {
	private final String[] connectServerMsgItems = new String[]{"id", "@id",
			"password", CIPHER_CHARACTER + "password", "clientver" };
	private boolean isConnectServerRunning = false;
	private List<InterfaceVersion> interfaceVersions = null;
	{
		interfaceVersions = (List<InterfaceVersion>) ServerInfo.InterfaceVersion.ALL_INTERFACE_VERSION.clone();
		interfaceVersions.remove(serverInfo.getInterfaceVersion());
	}

	/**
	 * The constructor
	 * 
	 * @param serverInfo
	 */
	public MonitoringTask(ServerInfo serverInfo) {
		super("login", serverInfo, null);
		setUsingMonPort(true);
		setNeedMultiSend(true);
	}

	/**
	 * Connect CUBRID Manager server
	 * 
	 * @param clientVersion String The given client version
	 * @param heartBeatTimeout a milisecond of a HA heartbeat timeout (1000ms ~ 3600000ms)
	 * @return ServerInfo The instance of ServerInfo
	 */
	public ServerInfo connectServer(String clientVersion, int heartBeatTimeout) {
		if (ServerManager.isConnected(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
				serverInfo.getUserName())) {
			return serverInfo;
		}
		isConnectServerRunning = true;

		//Get CUBRID Manager server version
		GetCmsEnvTask getCmsEnvTask = new GetCmsEnvTask(serverInfo);
		getCmsEnvTask.setTimeout(serverInfo.getSoTimeOut());
		getCmsEnvTask.execute();

		// try other protocol
		while (!getCmsEnvTask.canConnect() && !interfaceVersions.isEmpty()) {
			serverInfo.setInterfaceVersion(interfaceVersions.remove(0));
			getCmsEnvTask.setTimeout(serverInfo.getSoTimeOut());
			getCmsEnvTask.execute();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Try other protocol. " + this.serverInfo.getInterfaceVersion());
			}
			break;
		}

		String version = getCmsEnvTask.getVersion();
		if (getCmsEnvTask.isSuccess()) {
			serverInfo.setServerVersion(version);
			serverInfo.setCertStatus(getCmsEnvTask.getCertStatus());
		}else{
			errorMsg = getCmsEnvTask.getErrorMsg();
			warningMsg = getCmsEnvTask.getWarningMsg();

			ServerManager.setConnected(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
		}

		clearMsgItems();
		setOrders(connectServerMsgItems);
		if (CompatibleUtil.isSupportCipher(version)) {
			setMsgItem(CIPHER_CHARACTER + "id",
					CipherUtils.encrypt(serverInfo.getUserName()));
			setMsgItem(CIPHER_CHARACTER + "password",
					CipherUtils.encrypt(serverInfo.getUserPassword()));
		} else {
			setMsgItem("id", serverInfo.getUserName());
			setMsgItem("password", serverInfo.getUserPassword());
		}
		setMsgItem("clientver", clientVersion);
		setNeedServerConnected(false);
		this.setTimeout(serverInfo.getSoTimeOut());
		this.execute();
		if (!isSuccess()) {
			ServerManager.setConnected(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
			finish();
			return serverInfo;
		}
		TreeNode node = getResponse();
		String token = node.getValue("token");
		if (token == null || token.trim().length() == 0) {
			errorMsg = Messages.error_invalidToken;
			finish();
			return serverInfo;
		}
		ServerManager.setConnected(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
				serverInfo.getUserName(), true);
		serverInfo.setHostToken(token);
		getClientService().setHeartbeat(heartBeatTimeout);
		isConnectServerRunning = false;
		return serverInfo;
	}

	/**
	 * Cancel this task
	 */
	public void cancel() {
		logoutServer();
		if (isConnectServerRunning) {
			super.cancel();
			getClientService().stopHeartbeatThread();
			ServerManager.setConnected(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
		} else {
			getClientService().stopRead();
		}
	}

	/**
	 * 
	 * Stop monitoring
	 * 
	 */
	public void stopMonitor() {
		logoutServer();
		getClientService().stopRead();
		getClientService().tearDownConnection();
		getClientService().stopHeartbeatThread();
	}

	/**
	 * logout cms
	 */
	private void logoutServer() {
		setMsgItem("task", "logout");
		execute();
	}
}
