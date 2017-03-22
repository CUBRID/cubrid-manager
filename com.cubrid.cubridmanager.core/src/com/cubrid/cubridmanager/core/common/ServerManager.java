/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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

package com.cubrid.cubridmanager.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;

/**
 * This class is responsible to manage all CUBRID Manager server
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class ServerManager {

	private static final ServerManager instance = new ServerManager();

	private HashMap<String, ServerInfo> serverInfos = new HashMap<String, ServerInfo>();
	
	public static ServerManager getInstance() {
		return instance;
	}
	
	private ServerManager() {
	}
	
	public ServerInfo getServer(String hostAddress, int port, String userName) {
		return serverInfos.get(hostAddress + ":" + port + ":" + userName);
	}

	/**
	 * Return connected status of server
	 * 
	 * @param hostAddress String host address
	 * @param port int host port
	 * @param userName the String
	 * @return boolean
	 */
	public boolean isConnected(String hostAddress, int port, String userName) {
		ServerInfo serverInfo = getServer(hostAddress, port, userName);
		if (serverInfo == null) {
			return false;
		}
		return serverInfo.isConnected();
	}

	/**
	 * Set connected status of server
	 * 
	 * @param hostAddress String host address
	 * @param port int host port
	 * @param userName the String
	 * @param isConnected boolean whether is connected
	 */
	public void setConnected(String hostAddress, int port, String userName, boolean isConnected) {
		synchronized (this) {
			ServerInfo serverInfo = getServer(hostAddress, port, userName);
			if (serverInfo == null) {
				return;
			}
			serverInfo.setConnected(isConnected);
		}
	}
	
	/**
	 * Remove CUBRID Manager server
	 * 
	 * @param hostAddress String host address
	 * @param port int host port
	 * @param userName the String
	 */
	public void removeServer(String hostAddress, int port, String userName) {
		synchronized (this) {
			setConnected(hostAddress, port, userName, false);
			serverInfos.remove(hostAddress + ":" + port + ":" + userName);
		}
	}

	/**
	 * Add CUBRID Manager server information
	 * 
	 * @param hostAddress String host address
	 * @param port int host port
	 * @param value ServerInfo given serverInfo
	 * @param userName the String
	 * @return ServerInfo
	 */
	public ServerInfo addServer(String hostAddress, int port, String userName, ServerInfo value) {
		synchronized (this) {
			return serverInfos.put(hostAddress + ":" + port + ":" + userName, value);
		}
	}

	public void disConnectAllServer() {
		synchronized (this) {
			if (serverInfos != null) {
				List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>();
				serverInfoList.addAll(serverInfos.values());
				Iterator<ServerInfo> it = serverInfoList.iterator();
				while (it.hasNext()) {
					ServerInfo serverInfo = it.next();
					if (serverInfo.isConnected()) {
						setConnected(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
								serverInfo.getLoginedUserInfo().getUserName(), false);
					}
				}
			}
		}
	}

	public HashMap<String, ServerInfo> getAllServerInfos(){
		return serverInfos;
	}
}
