/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.cubridmanager.core.common.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.common.loader.CubridClassLoaderPool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;

/**
 * 
 * This class is responsible to provide JDBC connection service
 * 
 * @author wangsl
 * @version 1.0 - 2009-6-4 created by wangsl
 */
public final class JDBCConnectionManager {
	private static final Logger LOGGER = LogUtil.getLogger(JDBCConnectionManager.class);

	private JDBCConnectionManager() {
	}

	/**
	 * Get new JDBC connection
	 * 
	 * @param dbInfo DatabaseInfo The given info of DatabaseInfo
	 * @param autoCommit boolean Whether is autoCommit
	 * @throws SQLException A SQLException will be throws when exception occur
	 * @return Connection
	 */
	public static Connection getConnection(DatabaseInfo dbInfo,
			boolean autoCommit) throws SQLException {
		if (dbInfo == null || dbInfo.getServerInfo() == null) {
			throw new IllegalArgumentException();
		}

		String dbName = dbInfo.getDbName();
		ServerInfo serverInfo = dbInfo.getServerInfo();
		String brokerIP = dbInfo.getBrokerIP();
		String brokerPort = dbInfo.getBrokerPort();
		DbUserInfo userInfo = dbInfo.getAuthLoginedDbUserInfo();
		String userName = userInfo.getName();
		String password = userInfo.getNoEncryptPassword();

		String charset = dbInfo.getCharSet();
		String driverVersion = serverInfo.getJdbcDriverVersion();

		// advanced jdbc settings
		String jdbcAttrs = dbInfo.getJdbcAttrs();
		boolean isShard = dbInfo.isShard();

		return getConnection(brokerIP, brokerPort, dbName, userName, password,
				charset, jdbcAttrs, driverVersion, autoCommit, isShard);
	}

	/**
	 * 
	 * Get new JDBC connection
	 * 
	 * @param brokerIP
	 * @param brokerPort
	 * @param dbName
	 * @param userName
	 * @param password
	 * @param charset
	 * @param jdbcAttrs
	 * @param driverVersion
	 * @param autoCommit
	 * @param isShard
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String brokerIP, String brokerPort,
			String dbName, String userName, String password, String charset,
			String jdbcAttrs, String driverVersion, boolean autoCommit,
			boolean isShard) throws SQLException {

		String url = "jdbc:cubrid:" + brokerIP + ":" + brokerPort + ":"
				+ dbName + ":::";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("connection url=" + url + ", charset="
					+ (charset == null ? "" : charset));
		}

		Driver cubridDriver = CubridClassLoaderPool.getCubridDriver(driverVersion);
		//can't get connection throw DriverManger
		Properties props = new Properties();
		props.put("user", userName);
		props.put("password", password == null ? "" : password);

		boolean hasCharset = false;
		if (charset != null && charset.length() > 0) {
			props.put("charset", charset);
			hasCharset = true;
		}

		// check SQL Code autocompletion mode
		if (CubridManagerCorePlugin.getDefault().isSQLCodeAutocompletionMode()) {
			url = new StringBuffer(url)
					.append("?")
					.append(CubridManagerCorePlugin.CM_SQL_CODE_AUTOCOMPLETION)
					.append("=")
					.append(String.valueOf(Boolean.TRUE)).toString();
		}

		// advanced jdbc settings
		Properties tmpProps = parseJdbcOptions(jdbcAttrs, hasCharset);
		for (Enumeration<Object> e = tmpProps.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = (String) tmpProps.get(key);
			props.put(key, val);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(key + "=" + val);
			}
		}

		Connection conn = null;
		try {
			conn = cubridDriver.connect(url, props);
			conn.setAutoCommit(autoCommit);
			//conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			// [TOOLS-2425]Support shard broker
			// setLockTimeout, setTransactionisolation methods can't be used on SHARD broker.
//			if (!isShard) {
//				((CUBRIDConnectionProxy) conn).setLockTimeout(1000);
//			}
		} catch (SQLException e) {
			if (e.getErrorCode() == -2013 || e.getErrorCode() == -2003) {
				LOGGER.error(e.getMessage(), e);
				String errorMsg = e.getLocalizedMessage();
				throw new BrokerConnectionException(errorMsg, e);
			} else {
				throw e;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SQLException(e.getLocalizedMessage(), e);
		}

		return conn;
	}

	public static boolean testShardBroker(DatabaseInfo dbInfo, int shardId) {
		String dbName = dbInfo.getDbName();
		ServerInfo serverInfo = dbInfo.getServerInfo();
		String brokerIP = dbInfo.getBrokerIP();

		DbUserInfo userInfo = dbInfo.getAuthLoginedDbUserInfo();

		String url = "jdbc:cubrid:" + brokerIP + ":" + dbInfo.getBrokerPort()
				+ ":" + dbName + ":::";
		Driver cubridDriver = CubridClassLoaderPool.getCubridDriver(serverInfo.getJdbcDriverVersion());
		if (cubridDriver == null) {
			LOGGER.error("The cubridDriver is a null.");
			return false;
		}

		Properties props = new Properties();
		props.put("user", userInfo.getName());
		props.put("password", userInfo.getNoEncryptPassword() == null ? ""
				: userInfo.getNoEncryptPassword());

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = cubridDriver.connect(url, props);
			conn.setAutoCommit(true);
			stmt = conn.createStatement();
			stmt.executeQuery("/*+ shard_id(" + shardId
					+ ") */ SELECT * FROM db_root");
		} catch (Exception e) {
			LOGGER.error("", e);
			return false;
		} finally {
			QueryUtil.freeQuery(conn, stmt);
		}

		return true;
	}

	/**
	 * 
	 * parse key/value pair option string on JDBC URL
	 * 
	 * @param jdbcAttrs
	 * @param skipCharset if you want to not use charset options
	 * @return Properties
	 */
	public static Properties parseJdbcOptions(String jdbcAttrs,
			boolean skipCharset) {
		Properties props = new Properties();
		if (jdbcAttrs == null || jdbcAttrs.length() == 0) {
			return props;
		}

		String[] jdbcAttrArrays = jdbcAttrs.split("&");
		if (jdbcAttrArrays == null) {
			return props;
		}

		for (int i = 0; i < jdbcAttrArrays.length; i++) {
			String attr = jdbcAttrArrays[i];
			if (attr == null || attr.length() == 0) {
				continue;
			}

			String[] attrArray = attr.split("=");
			if (attrArray != null && attrArray.length == 2
					&& attrArray[0] != null && attrArray[1] != null) {
				if (skipCharset
						&& "charset".equalsIgnoreCase(attrArray[0].trim())) {
					continue;
				}
				props.put(attrArray[0].trim(), attrArray[1].trim());
			}
		}

		return props;
	}

	/**
	 * Judge the db is available.
	 * 
	 * @param dbInfo
	 * @return
	 * @throws SQLException
	 */
	public static boolean isConnectable(DatabaseInfo dbInfo) {
		boolean isConnected = true;

		Connection connect = null;

		try {
			connect = getConnection(dbInfo, false);
		} catch (SQLException e) {
			isConnected = false;
		} finally {
			try {
				if (connect == null || connect.isClosed()) {
					isConnected = false;
				}
			} catch (SQLException e) {
			}
			QueryUtil.freeQuery(connect);
		}

		return isConnected;
	}
}
