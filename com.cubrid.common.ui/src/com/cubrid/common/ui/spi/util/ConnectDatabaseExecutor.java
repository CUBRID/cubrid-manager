/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.common.ui.spi.util;

import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;

/**
 * ConnectDatabaseExecutor Description
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-15 created by Kevin.Wang
 */
public class ConnectDatabaseExecutor extends
		TaskExecutor {
	private static final Logger LOGGER = LogUtil.getLogger(ConnectDatabaseExecutor.class);

	private String brokerIP;
	private String brokerPort;
	private String dbName;
	private String userName;
	private String password;
	private String charset;
	private String jdbcAttrs;
	private String driverVersion;
	private boolean autoCommit;
	private boolean isShard;
	private boolean isConnectSuccess;

	/**
	 * Test connection
	 * 
	 * @param dbInfo
	 * @return
	 */
	public ConnectDatabaseExecutor(DatabaseInfo dbInfo) {
		this.dbName = dbInfo.getDbName();
		ServerInfo serverInfo = dbInfo.getServerInfo();
		this.brokerIP = dbInfo.getBrokerIP();
		this.brokerPort = dbInfo.getBrokerPort();
		DbUserInfo userInfo = dbInfo.getAuthLoginedDbUserInfo();
		this.userName = userInfo.getName();
		this.password = userInfo.getNoEncryptPassword();
		this.charset = dbInfo.getCharSet();
		this.driverVersion = serverInfo.getJdbcDriverVersion();
		this.jdbcAttrs = dbInfo.getJdbcAttrs();
		this.isShard = dbInfo.isShard();

	}
	
	public ConnectDatabaseExecutor(String brokerIP, String brokerPort,
			String dbName, String userName, String password, String charset,
			String jdbcAttrs, String driverVersion, boolean autoCommit,
			boolean isShard) {
		this.brokerIP = brokerIP;
		this.brokerPort = brokerPort;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.charset = charset;
		this.jdbcAttrs = jdbcAttrs;
		this.driverVersion = driverVersion;
		this.autoCommit = autoCommit;
		this.isShard = isShard;

	}

	public boolean exec(IProgressMonitor monitor) {
		monitor.beginTask("", IProgressMonitor.UNKNOWN);
		isConnectSuccess = internalConnect(brokerIP, brokerPort, dbName,
				userName, password, charset, jdbcAttrs, driverVersion,
				autoCommit, isShard);
		monitor.done();
		
		return isConnectSuccess;
	}

	private boolean internalConnect(String brokerIP, String brokerPort,
			String dbName, String userName, String password, String charset,
			String jdbcAttrs, String driverVersion, boolean autoCommit,
			boolean isShard) {
		Display display = Display.getDefault();
		CUBRIDConnectionProxy connection = null;
		try {
			connection = (CUBRIDConnectionProxy) JDBCConnectionManager.getConnection(
					brokerIP, brokerPort, dbName, userName, password, charset,
					jdbcAttrs, driverVersion, autoCommit, isShard);
			if (connection == null) {
				display.syncExec(new Runnable() {
					public void run() {
						CommonUITool.openErrorBox(Messages.msgConnectBrokerFailure);
					}
				});
				return false;
			}
			String jdbcVersion = connection.getJdbcVersion();
			jdbcVersion = jdbcVersion == null ? "" : jdbcVersion.replaceAll(
					"CUBRID-JDBC-", "");
			if (jdbcVersion.lastIndexOf(".") > 0) {
				jdbcVersion = jdbcVersion.substring(0,
						jdbcVersion.lastIndexOf("."));
			}
			String dbVersion = connection.getMetaData().getDatabaseProductVersion();
			dbVersion = dbVersion == null ? "" : dbVersion;
			String minorDbVersion = dbVersion;
			if (minorDbVersion.lastIndexOf(".") > 0) {
				minorDbVersion = minorDbVersion.substring(0,
						minorDbVersion.lastIndexOf("."));
			}

			if (isNewVersioningCheck(minorDbVersion)) {
				if (isAfterJdbc111(jdbcVersion)) {
					return true;
				} else {
					final String finalDbVersion = dbVersion;
					display.syncExec(new Runnable() {
						public void run() {
							isConnectSuccess = CommonUITool.openConfirmBox(Messages.bind(
									Messages.tipRecommendDriver,
									finalDbVersion));
						}
					});
					return isConnectSuccess;
				}
			} else {
				if (CompatibleUtil.compareVersion(jdbcVersion, minorDbVersion) == 0) {
					return true;
				} else {
					final String finalDbVersion = dbVersion;
					display.syncExec(new Runnable() {
						public void run() {
							isConnectSuccess = CommonUITool.openConfirmBox(Messages.bind(
									Messages.tipNoSupportJdbcVersion,
									finalDbVersion));
						}
					});
					return isConnectSuccess;
				}
			}
		} catch (final SQLException e) {
			LOGGER.error(e.getMessage(), e);
			display.syncExec(new Runnable() {
				public void run() {
					CommonUITool.openErrorBox(Messages.bind(
							Messages.errCommonTip, e.getErrorCode(),
							e.getMessage()));
				}
			});
			return false;
		} catch (final Exception e) {
			display.syncExec(new Runnable() {
				public void run() {
					CommonUITool.openErrorBox(e.getMessage());
				}
			});
			return false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * From 11.2 version, the engine and jdbc versioning  are different.
	 *
	 * @param void
	 * @return true:new versionning(Server version 11.2 or higher)
	 */
	private boolean isNewVersioningCheck(String DbVersion) {
		if (CompatibleUtil.compareVersion("11.2.0", DbVersion) <= 0) {
			return true;
		}
		return false;
	}

	private boolean isAfterJdbc111(String jdbcVersion) {
		String[] tempString;
		int tempIntVersion = 0;

		tempString = jdbcVersion.replaceAll("[^0-9.]","").split("\\.");

		tempIntVersion += Integer.valueOf(tempString[0]).intValue() * 10;
		tempIntVersion += Integer.valueOf(tempString[1]).intValue();

		if (tempIntVersion >= 111) {
			return true;
		}

		return false;
	}
}
