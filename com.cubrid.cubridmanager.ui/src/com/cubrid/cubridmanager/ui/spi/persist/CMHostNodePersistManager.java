/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.spi.persist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceStore;
import org.slf4j.Logger;

import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridServerLoader;


/**
 *
 * Host node persist manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-1 created by pangqiren
 */
public final class CMHostNodePersistManager {

	private static final Logger LOGGER = LogUtil.getLogger(CMHostNodePersistManager.class);

	private final static String SERVER_XML_CONTENT = "CUBRID_SERVERS";
	private List<CubridServer> serverList = null;
	private HashMap<String, ServerInfo> serverInfos = new HashMap<String, ServerInfo>();
	private static CMHostNodePersistManager instance;

	private CMHostNodePersistManager() {
		init();
		ServerManager.setServerInfos(serverInfos);
	}

	/**
	 * Return the only HostNodePersistManager
	 *
	 * @return HostNodePersistManager
	 */
	public static CMHostNodePersistManager getInstance() {
		synchronized (CMHostNodePersistManager.class) {
			if (instance == null) {
				instance = new CMHostNodePersistManager();
			}
		}
		return instance;
	}

	/**
	 *
	 * Initial the persist manager
	 *
	 */
	protected void init() {
		synchronized (this) {
			serverList = new ArrayList<CubridServer>();
			loadSevers();
		}
	}

	/**
	 *
	 * Load added host from plugin preference
	 *
	 */
	protected void loadSevers() {
		synchronized (this) {
//			serverList.clear();
			boolean isHasLocalHost = false;
			boolean alreadyAddedLocalhostDefault = false;
			IXMLMemento memento = PersistUtils.getXMLMemento(
					ApplicationUtil.CM_UI_PLUGIN_ID, SERVER_XML_CONTENT);
			// For compatible for the version before 8.4.0
			URL url = Platform.getInstanceLocation().getURL();
			File file = new File(url.getFile());
			String optionsPath = file.getAbsolutePath() + File.separator
					+ ".metadata" + File.separator + ".plugins"
					+ File.separator + "org.eclipse.core.runtime"
					+ File.separator + ".settings" + File.separator;

			//Load global preference setting
			QueryOptions.load(optionsPath, null);

			if(memento != null) {
				alreadyAddedLocalhostDefault = memento.getBoolean("alreadyAddedLocalhostDefault");
			}

			isHasLocalHost = loadServers(memento, true, optionsPath);
			if (!isHasLocalHost && !alreadyAddedLocalhostDefault) {
				String id = "localhost";
				String name = "localhost";
				int port = 8001;
				String userName = "admin";
				ServerInfo serverInfo = new ServerInfo();
				serverInfo.setServerName(name);
				serverInfo.setHostAddress(name);
				serverInfo.setHostMonPort(port);
				serverInfo.setHostJSPort(port + 1);
				serverInfo.setUserName(userName);
				serverInfo.setUserPassword("");
				CubridServer server = new CubridServer(id, name,
						"icons/navigator/host.png",
						"icons/navigator/host_connected.png");
				server.setServerInfo(serverInfo);
				server.setType(NodeType.SERVER);
				server.setLoader(new CubridServerLoader());
				serverList.add(0, server);
				serverInfos.put(serverInfo.getHostAddress()+":"+serverInfo.getHostMonPort()+":"+serverInfo.getUserName(), serverInfo);
				/*Save the server list*/
				saveServers();
			}
		}
	}

	/**
	 *
	 * Load added host from preference file path
	 *
	 * @param workspacePath String
	 * @return boolean whether import
	 *
	 */
	public boolean loadSevers(String workspacePath) {
		synchronized (this) {

			String settingPath = workspacePath + File.separator + ".metadata"
					+ File.separator + ".plugins" + File.separator
					+ "org.eclipse.core.runtime" + File.separator + ".settings"
					+ File.separator;

			//Load global preference setting
			QueryOptions.load(settingPath, null);

			String serverPath = settingPath + File.separator
					+ "com.cubrid.cubridmanager.ui.prefs";
			PreferenceStore preference = new PreferenceStore(serverPath);
			int size = serverList.size();
			try {
				preference.load();
				String xmlString = preference.getString(SERVER_XML_CONTENT);
				if (xmlString == null || xmlString.trim().length() == 0) {
					return false;
				}
				ByteArrayInputStream in = new ByteArrayInputStream(
						xmlString.getBytes("UTF-8"));
				IXMLMemento memento = XMLMemento.loadMemento(in);
				loadServers(memento, true, settingPath);
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			boolean isImported = size != serverList.size();
			if (isImported) {
				saveServers();
			}
			return isImported;
		}
	}

	private boolean loadServers(List<CubridServer> servers,
			IXMLMemento memento, boolean isLoadOptions, String optionPath,
			boolean allowDuplicate) {
		boolean isHasLocalHost = false;
		IXMLMemento[] children = memento == null ? null
				: memento.getChildren("host");
		for (int i = 0; children != null && i < children.length; i++) {
			String id = children[i].getString("id");
			String name = children[i].getString("name");
			if ("localhost".equals(name)) {
				isHasLocalHost = true;
			}
			String address = children[i].getString("address");
			String port = children[i].getString("port");
			String user = children[i].getString("user");
			String userPasword = children[i].getString("password");
			String jdbcDriver = children[i].getString("jdbcDriver");
			boolean savePassword = children[i].getBoolean("savePassword");
			String strSoTimeOut = children[i].getString("soTimeOut");
			int soTimeOut = StringUtil.intValue(strSoTimeOut, SocketTask.SOCKET_IO_TIMEOUT_MSEC);
			// duplicate entry check: memento -- current workspace
			if (!allowDuplicate) {
				if (getServer(id) != null
						&& isContainedByHostAddress(address, port, null)) {
					continue;
				}
			}
			String strCheckCert = children[i].getString("isCheckCert");

			ServerInfo serverInfo = new ServerInfo();
			serverInfo.setServerName(name);
			serverInfo.setHostAddress(address);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(user);
			serverInfo.setUserPassword(CipherUtils.decrypt(userPasword));
			serverInfo.setJdbcDriverVersion(jdbcDriver);
			serverInfo.setSoTimeOut(soTimeOut);
			if (!StringUtil.isEmpty(strCheckCert)) {
				serverInfo.setCheckCertStatus(StringUtil.booleanValue(strCheckCert));
			}
			
			CubridServer server = new CubridServer(id, name,
					"icons/navigator/host.png",
					"icons/navigator/host_connected.png");
			server.setServerInfo(serverInfo);
			server.setType(NodeType.SERVER);
			server.setLoader(new CubridServerLoader());
			server.setAutoSavePassword(savePassword);
			if (isLoadOptions) {
				QueryOptions.load(optionPath, serverInfo);
			}
			servers.add(server);
			serverInfos.put(serverInfo.getHostAddress()+":"+serverInfo.getHostMonPort()+":"+serverInfo.getUserName(), serverInfo);
		}
		return isHasLocalHost;
	}

	/**
	 *
	 * Load servers from xml memento
	 *
	 * @param memento IXMLMemento
	 * @param isLoadOptions boolean whether load related options
	 * @param optionPath String
	 * @return boolean
	 */
	private boolean loadServers(IXMLMemento memento, boolean isLoadOptions,
			String optionPath) {
		return loadServers(serverList, memento, isLoadOptions, optionPath,
				false);
	}

	public boolean loadServers(IXMLMemento memento, boolean isLoadOptions,
			String optionPath, List<CubridServer> list) {
		return loadServers(list, memento, isLoadOptions, optionPath, true);
	}

	/**
	 *
	 * Save added server to plug-in preference
	 *
	 */
	public void saveServers() {
		synchronized (this) {
			XMLMemento memento = XMLMemento.createWriteRoot("hosts");
			memento.putBoolean("alreadyAddedLocalhostDefault", true);
			Iterator<CubridServer> iterator = serverList.iterator();
			while (iterator.hasNext()) {
				CubridServer server = (CubridServer) iterator.next();
				IXMLMemento child = memento.createChild("host");
				child.putString("id", server.getId());
				child.putString("name", server.getLabel());
				child.putString("port", String.valueOf(server.getMonPort()));
				child.putString("address", server.getHostAddress());
				child.putString("user", server.getUserName());
				String pwd = server.isAutoSavePassword() ? CipherUtils.encrypt(server.getPassword()) : "";
				child.putString("password", pwd);
				child.putBoolean("savePassword", server.isAutoSavePassword());
				child.putString("jdbcDriver", server.getJdbcDriverVersion());
				if (server.getServerInfo() != null) {
					child.putInteger("soTimeOut", server.getServerInfo().getSoTimeOut());
					child.putString("isCheckCert",String.valueOf(server.getServerInfo().isCheckCertStatus()));
				}
			}
			PersistUtils.saveXMLMemento(ApplicationUtil.CM_UI_PLUGIN_ID,
					SERVER_XML_CONTENT, memento);
		}
	}

	public void saveServer(List<CubridServer> servers, String f) {
		synchronized (this) {
			XMLMemento memento = XMLMemento.createWriteRoot("hosts");
			Iterator<CubridServer> iterator = servers.iterator();
			while (iterator.hasNext()) {
				CubridServer server = (CubridServer) iterator.next();
				IXMLMemento child = memento.createChild("host");
				child.putString("id", server.getId());
				child.putString("name", server.getLabel());
				child.putString("port", String.valueOf(server.getMonPort()));
				child.putString("address", server.getHostAddress());
				child.putString("user", server.getUserName());
//				String pwd = server.isAutoSavePassword() ? CipherUtils.encrypt(server.getPassword())
//						: "";
//				child.putString("password", pwd);
//				child.putBoolean("savePassword", false);
				child.putString("jdbcDriver", server.getJdbcDriverVersion());
				if (server.getServerInfo() != null) {
					child.putInteger("soTimeOut", server.getServerInfo().getSoTimeOut());
				}
			}
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(f);
				fout.write(memento.getContents());
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 *
	 * Add server
	 *
	 * @param server the CubridServer object
	 */
	public void addServer(CubridServer server) {
		synchronized (this) {
			if (server != null) {
				serverList.add(server);
				serverInfos.put(server.getServerInfo().getHostAddress() + ":"
						+ server.getServerInfo().getHostMonPort() + ":"
						+ server.getServerInfo().getUserName(),
						server.getServerInfo());
				saveServers();
				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent(server,
								CubridNodeChangedEventType.NODE_ADD));
			}
		}
	}
	
	public void addServerInHashMap(String hostAddress, int port, String userName, ServerInfo info){
		/*
		 * Used only when connecting or when testing connection of host.
		 * With current implementation, connect and test don't work unless the host is in the hashmap
		 * If the testing host is added using addServer, some visual bugs appear.
		 * If the testing host isn't added at all, it doesn't work.
		 */
		serverInfos.put(hostAddress+":"+port+":"+userName, info);
	}

	/**
	 *
	 * Remove server
	 *
	 * @param server the CubridServer object
	 */
	public void removeServer(CubridServer server) {
		synchronized (this) {
			if (server != null) {
				serverInfos.remove(server.getServerInfo().getHostAddress()+":"+
								server.getServerInfo().getHostMonPort()+":"+
								server.getServerInfo().getUserName());
				serverList.remove(server);
				CMDBNodePersistManager.getInstance().deleteParameter(server);
				saveServers();
				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent(server,
								CubridNodeChangedEventType.NODE_REMOVE));
			}
		}
	}
	
	/**
	 *
	 * Remove all servers
	 *
	 */
	public void removeAllServer() {
		synchronized (this) {
			for (int i = 0; i < serverList.size(); i++) {
				CubridServer server = serverList.get(i);
				serverInfos.remove(server.getHostAddress() + ":" +
						server.getServerInfo().getHostMonPort() + ":" +
						server.getServerInfo().getUserName());
				serverList.remove(server);
				CMDBNodePersistManager.getInstance().deleteParameter(server);
				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent(server,
								CubridNodeChangedEventType.NODE_REMOVE));
			}
			saveServers();
		}
	}

	/**
	 *
	 * Get server by id
	 *
	 * @param id the server id
	 * @return the CubridServer object
	 */
	public CubridServer getServer(String id) { // FIXME extract
		for (int i = 0; i < serverList.size(); i++) {
			CubridServer server = serverList.get(i);
			if (server.getId().equals(id)) {
				return server;
			}
		}
		return null;
	}
	
	public CubridServer getServer(String hostAddress, int port, String userName){
		for(CubridServer server : serverList){
			if(server.getServerInfo().getHostAddress().compareTo(hostAddress) == 0 &&
					server.getServerInfo().getHostMonPort() == port &&
					server.getServerInfo().getUserName().compareTo(userName) == 0){
				return server;
			}
		}
		return null;
	}

	/**
	 *
	 * Return whether this server has been existed and exclude this server
	 *
	 * @param serverName the server name
	 * @param server the CubridServer object
	 * @return <code>true</code> if contain this server;<code>false</code>
	 *         otherwise
	 */
	public boolean isContainedByName(String serverName, CubridServer server) { // FIXME extract
		for (int i = 0; i < serverList.size(); i++) {
			CubridServer serv = serverList.get(i);
			if (server != null && server.getId().equals(serv.getId())) {
				continue;
			}
			if (serv.getLabel().equals(serverName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * Return whether this server has been existed and exclude this server
	 *
	 * @param address the ip address
	 * @param port the port
	 * @param server the CubridServer object
	 * @return <code>true</code> if contain this server;<code>false</code>
	 *         otherwise
	 */
	public boolean isContainedByHostAddress(String address, String port,
			CubridServer server) { // FIXME extract
		for (int i = 0; i < serverList.size(); i++) {
			CubridServer serv = serverList.get(i);
			if (server != null && server.getId().equals(serv.getId())) {
				continue;
			}
			ServerInfo serverInfo = serv.getServerInfo();
			if (serverInfo.getHostAddress().equals(address)
					&& serverInfo.getHostMonPort() == Integer.parseInt(port)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isContainedByUserName(String userName){
		for(CubridServer cs : serverList){
			if(cs.getServerInfo().getUserName().compareTo(userName) == 0){
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * Get connection count in some server
	 *
	 * @param ip The String
	 * @param port The String
	 * @param userName The String
	 * @return int
	 */
	public int getConnectionCount(String ip, String port, String userName) { // FIXME extract
		int count = 0;
		if (serverList != null) {
			for (CubridServer server : serverList) {
				if (server.isConnected() && ip.equals(server.getHostAddress())
						&& port.equals(server.getMonPort())
						&& userName.equals(server.getUserName())) {
					count++;
				}
			}
		}
		return count;
	}

	public List<CubridServer> reloadServers() {
		loadSevers();
		return serverList;
	}
	
	public ServerInfo getServerInfo(String hostAddress, int port, String userName) {
		return serverInfos.get(hostAddress+":"+port+":"+userName);
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
			Iterator<CubridServer> servers = serverList.iterator();
			while(servers.hasNext()){
				CubridServer server = (CubridServer)servers.next();
				if(server.getServerInfo().getServerName().compareTo(hostAddress) == 0 &&
						server.getServerInfo().getHostMonPort() == port &&
						server.getServerInfo().getUserName().compareTo(userName) == 0){
					server.getServerInfo().setConnected(false);
					servers.remove();
				}
			}
			serverInfos.remove(hostAddress+":"+port+":"+userName);
		}
	}
	
	public void removeServerFromHashMap(String hostAddress, int port, String userName){
		serverInfos.remove(hostAddress+":"+port+":"+userName);
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
	public void addServer(String hostAddress, int port, String userName, ServerInfo value) {
		synchronized (this) {
			CubridServer server = getServer(hostAddress, port, userName);
			if(server == null){
				CubridServer newServer = new CubridServer(value.getServerName(),
						value.getServerName(), "com.cubrid.cubridmananger.ui/icons/navigator/host.png",
						"com.cubrid.cubridmananger.ui/icons/navigator/host_connected.png");
				newServer.setServerInfo(value);
				newServer.setLoader(new CubridServerLoader());
				addServer(newServer);
			}else{
				server.setServerInfo(value);
			}
			serverInfos.put(hostAddress+":"+port+":"+userName, value);
		}
	}

	public void disConnectAllServer() {
		synchronized (this) {
			Iterator<CubridServer> servers = serverList.iterator();
			while (servers.hasNext()) {
				CubridServer cubridServer = servers.next();
				ServerInfo serverInfo = cubridServer.getServerInfo();
				if (serverInfo.isConnected()) {
					cubridServer.getServerInfo().setConnected(false);
				}
			}
		}
	}
	
	/**
	 *
	 * Get All servers
	 *
	 * @return all CubridServer objects
	 */
	
	public List<CubridServer> getAllServers(){
		return serverList;
	}

}
