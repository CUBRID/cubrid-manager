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
package com.cubrid.cubridmanager.core.common.model;

import java.util.StringTokenizer;

/**
 * 
 * This entity class is responsible to cache CUBRID Server environment
 * information
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class EnvInfo {

	private String rootDir;
	private String databaseDir;
	private String cmServerDir;
	private ServerVersion serverVersion = null;
	private String brokerVersion;
	private String[] hostMonTabStatus;
	// os(NT,LINUX,UNIX)
	private String osInfo;
	/**
	 * 
	 * Get CUBRID database root dir
	 * 
	 * @return String
	 */
	public String getRootDir() {
		return rootDir;
	}

	/**
	 * 
	 * Set CUBRID database root dir
	 * 
	 * @param rootDir String The root path
	 */
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * 
	 * Get CUBRID database dir
	 * 
	 * @return String
	 */
	public String getDatabaseDir() {
		return databaseDir;
	}

	/**
	 * Set CUBRID database directory
	 * 
	 * @param databaseDir String The database path
	 */
	public void setDatabaseDir(String databaseDir) {
		this.databaseDir = databaseDir;
	}

	/**
	 * 
	 * Get CUBRID server version
	 * 
	 * @return String
	 */
	public String getServerVersion() {
		return serverVersion.getServerVersion();
	}

	/**
	 * Set CUBRID server version
	 * 
	 * @param serverVersion String The server version
	 */
	public void setServerVersion(String version) {
		if (serverVersion == null){
			serverVersion = new ServerVersion();
		}
		serverVersion.setVersion(version);
	}
	public ServerVersion getServerDetails(){
		return serverVersion;
	}

	/**
	 * 
	 * Get broker version
	 * 
	 * @return String
	 */
	public String getBrokerVersion() {
		return brokerVersion;
	}

	/**
	 * 
	 * Set broker version
	 * 
	 * @param brokerVersion String The broker version
	 */
	public void setBrokerVersion(String brokerVersion) {
		this.brokerVersion = brokerVersion;
	}

	/**
	 * 
	 * Get host monitor status
	 * 
	 * @return String[] The host monitor status
	 */
	public String[] getHostMonTabStatus() {
		return hostMonTabStatus == null ? null
				: (String[]) hostMonTabStatus.clone();
	}

	/**
	 * 
	 * Set host monitor status
	 * 
	 * @param hostMonTabStatus String[]
	 */
	public void setHostMonTabStatus(String[] hostMonTabStatus) {
		this.hostMonTabStatus = hostMonTabStatus == null ? null
				: (String[]) (hostMonTabStatus.clone());
	}

	/**
	 * 
	 * Get CUBRID Server OS information
	 * 
	 * @return String The info of OS
	 */
	public String getOsInfo() {
		return osInfo;
	}

	/**
	 * 
	 * Set CUBRID Server OS information
	 * 
	 * @param osInfo String The info of OS
	 */
	public void setOsInfo(String osInfo) {
		this.osInfo = osInfo;
	}

	/**
	 * 
	 * Get CUBRID Manager server directory
	 * 
	 * @return String
	 */
	public String getCmServerDir() {
		return cmServerDir;
	}

	/**
	 * 
	 * Set CUBRID Manager server directory
	 * 
	 * @param cmServerDir String
	 */
	public void setCmServerDir(String cmServerDir) {
		this.cmServerDir = cmServerDir;
	}

}
