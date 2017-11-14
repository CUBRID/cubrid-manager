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
package com.cubrid.cubridmanager.core.cubrid.database.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo;
import com.cubrid.cubridmanager.core.cubrid.table.SchemaProvider;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;

/**
 * 
 * This class is responsible to cache CUBRID database information
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class DatabaseInfo implements IDatabaseSpec {
	private String dbName;
	private String dbDir;
	private DbRunningType runningType = DbRunningType.STANDALONE;
	private boolean isLogined = false;
	//database connection information
	private DbUserInfo authLoginedDbUserInfo = null;
	private String brokerPort = null;
	private String brokerIP = null;
	private String charSet = null;
	private String jdbcAttrs = null;
	// all trigger
	private List<Trigger> triggerList = null;
	// all classes
	private List<ClassInfo> userTableInfoList = null;
	private List<ClassInfo> userViewInfoList = null;
	private List<ClassInfo> sysTableInfoList = null;
	private List<ClassInfo> sysViewInfoList = null;
	private Map<String, List<ClassInfo>> partitionedTableMap = null;
	// all backup plan list
	private List<BackupPlanInfo> backupPlanInfoList = null;
	// all query plan list
	private List<QueryPlanInfo> queryPlanInfoList = null;
	// all database space
	// information(Generic,data,index,temp,archive_log,active_log)
	private DbSpaceInfoList dbSpaceInfoList = null;
	// all database user information
	private DbUserInfoList dbUserInfoList = null;
	//all stored procedured
	private List<SPInfo> spProcedureInfoList = null;
	private List<SPInfo> spFunctionInfoList = null;
	//all serial
	private List<SerialInfo> serialInfoList = null;
	private ServerInfo serverInfo = null;
	// information of all schemas, key=${tablename}
	private Map<String, SchemaInfo> schemaMap = null;
	private String errorMessage = null;
	// whether it is distributor database
	private boolean isDistributorDb = false;
	private ReplicationInfo replInfo = null;
	private ParamDumpInfo paraDumpInfo = null;
	private String version = null;
	private boolean isSupportTableComment;
	private boolean isShard = false;

	public static final int SHARD_QUERY_TYPE_VAL = 0;
	public static final int SHARD_QUERY_TYPE_ID = 1;
	private int shardQueryType = SHARD_QUERY_TYPE_VAL;
	private int currentShardId = 0;
	private int currentShardVal = 0;
	private String collation = null;

	/**
	 * Set version synchronized
	 * 
	 * @param version String
	 */
	private void setVersion(String version) {
		synchronized (this) {
			this.version = version;
		}
	}

	/**
	 * Retrieves the version of jdbc driver currently used.
	 * 
	 * @return version of jdbc driver
	 */
	public String getVersion() {
		if (null == version) {
			Connection con = null;
			
			try {
				con = JDBCConnectionManager.getConnection(this, true);
				setVersion(con.getMetaData().getDatabaseProductVersion());
			} catch (Exception e) {
				if (null != serverInfo) {
					return serverInfo.getServerVersionKey();
				}
				throw new RuntimeException(e);
			} finally {
				if (con != null) {
					try {
						con.close();
						con = null;
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}
		return version;
	}
	
	public String getJdbcAttrs() {
		return jdbcAttrs;
	}

	public void setJdbcAttrs(String jdbcAttrs) {
		this.jdbcAttrs = jdbcAttrs;
	}

	/**
	 * The constructor
	 * 
	 * @param dbName
	 * @param serverInfo
	 */
	public DatabaseInfo(String dbName, ServerInfo serverInfo) {
		this.dbName = dbName;
		this.serverInfo = serverInfo;
	}

	/**
	 * 
	 * Clear database cached information
	 * 
	 */
	public void clear() {
		synchronized (this) {
			dbUserInfoList = null;
			userTableInfoList = null;
			userViewInfoList = null;
			sysViewInfoList = null;
			sysTableInfoList = null;
			partitionedTableMap = null;
			dbSpaceInfoList = null;
			triggerList = null;
			backupPlanInfoList = null;
			queryPlanInfoList = null;
			spProcedureInfoList = null;
			spFunctionInfoList = null;
			serialInfoList = null;
			schemaMap = null;
			isDistributorDb = false;
			replInfo = null;
			paraDumpInfo = null;
			setCollation(null);
		}
	}

	/**
	 * 
	 * Get database name
	 * 
	 * @return String The database name
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * 
	 * Set database name
	 * 
	 * @param dbName String The given database name
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * 
	 * Get database dir
	 * 
	 * @return String The database directory
	 */
	public String getDbDir() {
		return dbDir;
	}

	/**
	 * 
	 * Set database dir
	 * 
	 * @param dbDir String The database directory
	 */
	public void setDbDir(String dbDir) {
		this.dbDir = dbDir;
		setVersion(null);
	}

	/**
	 * 
	 * Get database running type
	 * 
	 * @return DbRunningType The database running type
	 */
	public DbRunningType getRunningType() {
		return runningType;
	}

	/**
	 * 
	 * Set database running type
	 * 
	 * @param runningType DbRunningType The given database running type
	 */
	public void setRunningType(DbRunningType runningType) {
		this.runningType = runningType;
		clear();
	}

	/**
	 * 
	 * Return whether database is logined
	 * 
	 * @return boolean Whether is login
	 */
	public boolean isLogined() {
		return isLogined;
	}

	/**
	 * 
	 * Set whether database is logined
	 * 
	 * @param isLogined String Whether is login
	 */
	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
		clear();
	}

	/**
	 * 
	 * Get auth logined database user information
	 * 
	 * @return DbUserInfo The instance of DbUserInfo
	 */
	public DbUserInfo getAuthLoginedDbUserInfo() {
		return authLoginedDbUserInfo;
	}

	/**
	 * 
	 * Set auth logined database user information
	 * 
	 * @param authDbUserInfo DbUserInfo The instance of DbUserInfo
	 */
	public void setAuthLoginedDbUserInfo(DbUserInfo authDbUserInfo) {
		this.authLoginedDbUserInfo = authDbUserInfo;
	}

	/**
	 * 
	 * Get all database user list
	 * 
	 * @return DbUserInfoList The list includes the instances of DbUserInfo
	 */
	public DbUserInfoList getDbUserInfoList() {
		synchronized (this) {
			return dbUserInfoList;
		}
	}

	/**
	 * 
	 * Set database user list
	 * 
	 * @param dbUserInfoList DbUserInfoList The list includes the instances of
	 *        DbUserInfo
	 */
	public void setDbUserInfoList(DbUserInfoList dbUserInfoList) {
		synchronized (this) {
			this.dbUserInfoList = dbUserInfoList;
		}
	}

	/**
	 * 
	 * Add database user information
	 * 
	 * @param dbUserInfo DbUserInfo
	 */
	public void addDbUserInfo(DbUserInfo dbUserInfo) {
		synchronized (this) {
			if (dbUserInfoList == null) {
				dbUserInfoList = new DbUserInfoList();
				dbUserInfoList.setDbname(this.dbName);
			}
		}
	}

	/**
	 * 
	 * Remove database user information
	 * 
	 * @param dbUserInfo DbUserInfo The given instance of DbUserInfo
	 */
	public void removeDbUserInfo(DbUserInfo dbUserInfo) {
		synchronized (this) {
			if (dbUserInfoList != null) {
				dbUserInfoList.removeUser(dbUserInfo);
			}
		}
	}

	/**
	 * 
	 * Get all triggers list
	 * 
	 * @return List<Trigger>
	 */
	public List<Trigger> getTriggerList() {
		synchronized (this) {
			return triggerList;
		}
	}

	/**
	 * 
	 * Set triggers list
	 * 
	 * @param triggerList List<Trigger> The given list that includes the
	 *        triggers of Trigger
	 */
	public void setTriggerList(List<Trigger> triggerList) {
		synchronized (this) {
			this.triggerList = triggerList;
		}
	}

	/**
	 * 
	 * Get class info list
	 * 
	 * @return List<ClassInfo> The list includes some instance of ClassInfo
	 */
	public List<ClassInfo> getClassInfoList() {
		List<ClassInfo> classInfoList = new ArrayList<ClassInfo>();
		if (userTableInfoList != null) {
			classInfoList.addAll(userTableInfoList);
		}
		if (userViewInfoList != null) {
			classInfoList.addAll(userViewInfoList);
		}
		if (sysTableInfoList != null) {
			classInfoList.addAll(sysTableInfoList);
		}
		if (sysViewInfoList != null) {
			classInfoList.addAll(sysViewInfoList);
		}
		return classInfoList;
	}

	/**
	 * 
	 * Get user table information list
	 * 
	 * @return List<ClassInfo> The list that includes some instance of ClassInfo
	 */
	public List<ClassInfo> getUserTableInfoList() {
		return userTableInfoList;
	}

	/**
	 * 
	 * Set user table information list
	 * 
	 * @param userTableInfoList List<ClassInfo> The given list that includes
	 *        some instance of ClassInfo
	 */
	public void setUserTableInfoList(List<ClassInfo> userTableInfoList) {
		this.userTableInfoList = userTableInfoList;
	}

	/**
	 * 
	 * Get user view information list
	 * 
	 * @return List<ClassInfo> The list that includes some instance of ClassInfo
	 */
	public List<ClassInfo> getUserViewInfoList() {
		return userViewInfoList;
	}

	/**
	 * 
	 * Set user view information list
	 * 
	 * @param userViewInfoList List<ClassInfo> The given list that include some
	 *        instance of ClassInfo
	 */
	public void setUserViewInfoList(List<ClassInfo> userViewInfoList) {
		this.userViewInfoList = userViewInfoList;
	}

	/**
	 * 
	 * Get system table information list
	 * 
	 * @return List<ClassInfo> The list that includes some instance of ClassInfo
	 */
	public List<ClassInfo> getSysTableInfoList() {
		return sysTableInfoList;
	}

	/**
	 * 
	 * Set system table information list
	 * 
	 * @param sysTableInfoList List<ClassInfo> The given list that include some
	 *        instance of ClassInfo
	 */
	public void setSysTableInfoList(List<ClassInfo> sysTableInfoList) {
		this.sysTableInfoList = sysTableInfoList;
	}

	/**
	 * 
	 * Get system view information list
	 * 
	 * @return List<ClassInfo> The list that includes some instance of ClassInfo
	 */
	public List<ClassInfo> getSysViewInfoList() {
		return sysViewInfoList;
	}

	/**
	 * 
	 * Set system view information list
	 * 
	 * @param sysViewInfoList List<ClassInfo> The list that includes some
	 *        instance of ClassInfo
	 */
	public void setSysViewInfoList(List<ClassInfo> sysViewInfoList) {
		this.sysViewInfoList = sysViewInfoList;
	}

	/**
	 * 
	 * Get partitioned table map,key is partitioned table,value is the children
	 * partitioned table of key
	 * 
	 * @return Map<String, List<ClassInfo>> This map includes some lists
	 *         includes some instance of ClassInfo
	 */
	public Map<String, List<ClassInfo>> getPartitionedTableMap() {
		return partitionedTableMap;
	}

	/**
	 * 
	 * Set partitioned table map,key is partitioned table,value is the children
	 * partitioned table of key
	 * 
	 * @param partitionedTableMap Map<String, List<ClassInfo>> This map includes
	 *        some lists includes some instance of ClassInfo
	 */
	public void setPartitionedTableMap(
			Map<String, List<ClassInfo>> partitionedTableMap) {
		this.partitionedTableMap = partitionedTableMap;
	}

	/**
	 * 
	 * Add the children partitioned table of partitioned table
	 * 
	 * @param tableName String The table name
	 * @param classInfoList he list that includes some instance of ClassInfo
	 */
	public void addPartitionedTableList(String tableName,
			List<ClassInfo> classInfoList) {
		if (this.partitionedTableMap == null) {
			this.partitionedTableMap = new HashMap<String, List<ClassInfo>>();
		}
		this.partitionedTableMap.put(tableName, classInfoList);
	}

	/**
	 * 
	 * Get backup plan information list
	 * 
	 * @return List<BackupPlanInfo> The list that includes some instances of
	 *         BackupPlanInfo
	 */
	public List<BackupPlanInfo> getBackupPlanInfoList() {
		synchronized (this) {
			return backupPlanInfoList;
		}
	}

	/**
	 * 
	 * Set backup plan information list
	 * 
	 * @param backupPlanInfoList List<BackupPlanInfo> The list that includes
	 *        some instances of BackupPlanInfo
	 */
	public void setBackupPlanInfoList(List<BackupPlanInfo> backupPlanInfoList) {
		synchronized (this) {
			this.backupPlanInfoList = backupPlanInfoList;
		}
	}

	/**
	 * 
	 * Add backup plan information
	 * 
	 * @param backupPlanInfo BackupPlanInfo The instance of BackupPlanInfo
	 */
	public void addBackupPlanInfo(BackupPlanInfo backupPlanInfo) {
		synchronized (this) {
			if (backupPlanInfoList == null) {
				backupPlanInfoList = new ArrayList<BackupPlanInfo>();
			}
			if (!backupPlanInfoList.contains(backupPlanInfo)) {
				backupPlanInfoList.add(backupPlanInfo);
			}
		}
	}

	/**
	 * 
	 * Remove backup plan information
	 * 
	 * @param backupPlanInfo BackupPlanInfo The instance of BackupPlanInfo
	 */
	public void removeBackupPlanInfo(BackupPlanInfo backupPlanInfo) {
		synchronized (this) {
			if (backupPlanInfoList != null) {
				backupPlanInfoList.remove(backupPlanInfo);
			}
		}
	}

	/**
	 * 
	 * Remove all backup plan information
	 * 
	 */
	public void removeAllBackupPlanInfo() {
		synchronized (this) {
			if (backupPlanInfoList != null) {
				backupPlanInfoList.clear();
			}
		}
	}

	/**
	 * 
	 * Get query plan information list
	 * 
	 * @return List<QueryPlanInfo> The list includes some instances of
	 *         QueryPlanInfo
	 */
	public List<QueryPlanInfo> getQueryPlanInfoList() {
		synchronized (this) {
			return queryPlanInfoList;
		}
	}

	/**
	 * 
	 * Set query plan information list
	 * 
	 * @param queryPlanInfoList List<QueryPlanInfo> The list includes some
	 *        instances of QueryPlanInfo
	 */
	public void setQueryPlanInfoList(List<QueryPlanInfo> queryPlanInfoList) {
		synchronized (this) {
			this.queryPlanInfoList = queryPlanInfoList;
		}
	}

	/**
	 * 
	 * Add query plan information
	 * 
	 * @param queryPlanInfo QueryPlanInfo The instance of QueryPlanInfo
	 */
	public void addQueryPlanInfo(QueryPlanInfo queryPlanInfo) {
		synchronized (this) {
			if (queryPlanInfoList == null) {
				queryPlanInfoList = new ArrayList<QueryPlanInfo>();
			}
			if (!queryPlanInfoList.contains(queryPlanInfo)) {
				queryPlanInfoList.add(queryPlanInfo);
			}
		}
	}

	/**
	 * 
	 * Remove query plan information
	 * 
	 * @param queryPlanInfo QueryPlanInfo The instance of QueryPlanInfo
	 */
	public void removeQueryPlanInfo(QueryPlanInfo queryPlanInfo) {
		synchronized (this) {
			if (queryPlanInfoList != null) {
				queryPlanInfoList.remove(queryPlanInfo);
			}
		}
	}

	/**
	 * 
	 * Remove all query plan information
	 * 
	 */
	public void removeAllQueryPlanInfo() {
		synchronized (this) {
			if (queryPlanInfoList != null) {
				queryPlanInfoList.clear();
			}
		}
	}

	/**
	 * 
	 * Get database space information list
	 * 
	 * @return DbSpaceInfoList The instance of DbSpaceInfoList
	 */
	public DbSpaceInfoList getDbSpaceInfoList() {
		return dbSpaceInfoList;
	}

	/**
	 * 
	 * Set database space information list
	 * 
	 * @param dbSpaceInfoList DbSpaceInfoList The instance of DbSpaceInfoList
	 */
	public void setDbSpaceInfoList(DbSpaceInfoList dbSpaceInfoList) {
		this.dbSpaceInfoList = dbSpaceInfoList;
	}

	/**
	 * 
	 * Add database space information
	 * 
	 * @param spaceInfo DbSpaceInfo The instance of DbSpaceInfo
	 */
	public void addSpaceInfo(DbSpaceInfo spaceInfo) {
		if (dbSpaceInfoList == null) {
			dbSpaceInfoList = new DbSpaceInfoList();
			dbSpaceInfoList.setDbname(dbName);
		}
		dbSpaceInfoList.addSpaceinfo(spaceInfo);
	}

	/**
	 * 
	 * Remove database space information
	 * 
	 * @param spaceInfo DbSpaceInfo The instance of DbSpaceInfo
	 */
	public void removeSpaceInfo(DbSpaceInfo spaceInfo) {
		if (dbSpaceInfoList != null) {
			dbSpaceInfoList.removeSpaceinfo(spaceInfo);
		}
	}

	/**
	 * 
	 * Get database stored procedure information list
	 * 
	 * @return List<SPInfo> The list includes some instance of SPInfo
	 */
	public List<SPInfo> getSpInfoList() {
		synchronized (this) {
			List<SPInfo> spInfoList = new ArrayList<SPInfo>();
			if (spFunctionInfoList != null) {
				spInfoList.addAll(spFunctionInfoList);
			}
			if (spProcedureInfoList != null) {
				spInfoList.addAll(spProcedureInfoList);
			}
			return spInfoList;
		}
	}

	/**
	 * 
	 * Get database stored procedure of procedure type information list
	 * 
	 * @return List<SPInfo> The list includes some instance of SPInfo
	 */
	public List<SPInfo> getSpProcedureInfoList() {
		synchronized (this) {
			return spProcedureInfoList;
		}
	}

	/**
	 * 
	 * Set database stored procedure of procedure type information list
	 * 
	 * @param spProcedureInfoList List<SPInfo> The list includes some instance
	 *        of SPInfo
	 */
	public void setSpProcedureInfoList(List<SPInfo> spProcedureInfoList) {
		synchronized (this) {
			this.spProcedureInfoList = spProcedureInfoList;
		}
	}

	/**
	 * 
	 * Get database stored procedure of function type information list
	 * 
	 * @return List<SPInfo> The list includes some instance of SPInfo
	 */
	public List<SPInfo> getSpFunctionInfoList() {
		synchronized (this) {
			return spFunctionInfoList;
		}
	}

	/**
	 * 
	 * Set database stored procedure of function type information list
	 * 
	 * @param spFunctionInfoList List<SPInfo> The list includes some instance of
	 *        SPInfo
	 */
	public void setSpFunctionInfoList(List<SPInfo> spFunctionInfoList) {
		synchronized (this) {
			this.spFunctionInfoList = spFunctionInfoList;
		}
	}

	/**
	 * 
	 * Get serial information list
	 * 
	 * @return List<SerialInfo> The list includes some instance of SerialInfo
	 */
	public List<SerialInfo> getSerialInfoList() {
		return serialInfoList;
	}

	/**
	 * 
	 * Set serial information list
	 * 
	 * @param serialInfoList List<SerialInfo> The list includes some instance of
	 *        SerialInfo
	 */
	public void setSerialInfoList(List<SerialInfo> serialInfoList) {
		this.serialInfoList = serialInfoList;
	}

	/**
	 * 
	 * Get broker port
	 * 
	 * @return String The broker port
	 */
	public String getBrokerPort() {
		return brokerPort;
	}

	/**
	 * 
	 * Set broker port
	 * 
	 * @param brokerPort String The broker port
	 */
	public void setBrokerPort(String brokerPort) {
		this.brokerPort = brokerPort;
	}

	/**
	 * get a schema object via table name
	 * 
	 * Note: using delay loading method for large amount number of tables
	 * 
	 * @param tableName String The table name
	 * @return SchemaInfo The instance of SchemaInfo
	 */
	public SchemaInfo getSchemaInfo(String tableName) {
		if (StringUtil.isEmpty(tableName)) {
			return null;
		}

		if (null == schemaMap) {
			schemaMap = new HashMap<String, SchemaInfo>();
		}

		SchemaInfo schemaInfo = schemaMap.get(tableName);
		if (null == schemaInfo) {
			SchemaProvider schemaProvider = new SchemaProvider(this, tableName);
			schemaInfo = schemaProvider.getSchema();
			if (schemaInfo == null && StringUtil.isNotEmpty(schemaProvider.getErrorMessage())) {
				errorMessage = schemaProvider.getErrorMessage();
				return null;
			} else {
				putSchemaInfo(schemaInfo);
			}
		}

		return schemaInfo;
	}

	/**
	 * get a schema object via table name
	 * 
	 * Note: using delay loading method for large amount number of tables
	 * 
	 * @param connection
	 * @param tableName String The table name
	 * @return SchemaInfo The instance of SchemaInfo
	 */
	public SchemaInfo getSchemaInfo(Connection connection, String tableName) {
		if (StringUtil.isEmpty(tableName)) {
			return null;
		}

		if (schemaMap == null) {
			schemaMap = new HashMap<String, SchemaInfo>();
		}

		SchemaInfo schemaInfo = schemaMap.get(tableName);
		if (schemaInfo == null) {
			SchemaProvider schemaProvider = new SchemaProvider(this, tableName);
			schemaInfo = schemaProvider.getSchema(connection);
			if (schemaInfo == null && StringUtil.isNotEmpty(schemaProvider.getErrorMessage())) {
				errorMessage = schemaProvider.getErrorMessage();
				return null;
			} else {
				putSchemaInfo(schemaInfo);
			}
		}

		return schemaInfo;
	}

	/**
	 * add a schema object to the map. design reason: class name is unique in a
	 * given database so key=${tablename}
	 * 
	 * @param schema SchemaInfo The instance of SchemaInfo
	 */
	public void putSchemaInfo(SchemaInfo schema) {
		if (schema == null) {
			return;
		}
		if (null == schemaMap) {
			schemaMap = new HashMap<String, SchemaInfo>();
		}
		String key = schema.getClassname();
		schemaMap.put(key, schema);
	}

	/**
	 * 
	 * Clear schemas
	 * 
	 */
	public void clearSchemas() {
		if (null == schemaMap) {
			return;
		} else {
			schemaMap.clear();
		}
	}

	/**
	 * 
	 * Remove the schema in cache
	 * 
	 * @param schemaName The string
	 */
	public void removeSchema(String schemaName) {
		if (null == schemaMap) {
			return;
		} else {
			schemaMap.remove(schemaName);
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getBrokerIP() {
		return brokerIP;
	}

	public void setBrokerIP(String brokerIP) {
		this.brokerIP = brokerIP;
	}

	/**
	 * Get the char set
	 * 
	 * @return String The char set
	 */
	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public boolean isDistributorDb() {
		return isDistributorDb;
	}

	public void setDistributorDb(boolean isDistributorDb) {
		this.isDistributorDb = isDistributorDb;
	}

	public ReplicationInfo getReplInfo() {
		return replInfo;
	}

	public void setReplInfo(ReplicationInfo replInfo) {
		this.replInfo = replInfo;
	}

	public ParamDumpInfo getParaDumpInfo() {
		return paraDumpInfo;
	}

	public void setParaDumpInfo(ParamDumpInfo paraDumpInfo) {
		this.paraDumpInfo = paraDumpInfo;
	}

	/**
	 * 
	 * Get configuration parameter value in running process
	 * 
	 * @param paraName The parameter name
	 * @return String The parameter value
	 */
	public String getRunningConfParameter(String paraName) {
		if (paraDumpInfo == null || paraDumpInfo.getServerData() == null) {
			return null;
		}
		return paraDumpInfo.getServerData().get(paraName);
	}

	/**
	 * 
	 * Check whether this database is HA mode
	 * 
	 * @return <code>true</code> if it is;otherwise <code>false</code>
	 */
	public boolean isHAMode() {
		String haMode = getRunningConfParameter(ConfConstants.HA_MODE);
		if (haMode != null) {
			haMode = haMode.replaceAll("\"", "");
		}
		return "on".equalsIgnoreCase(haMode) || "yes".equalsIgnoreCase(haMode)
				|| "replica".equalsIgnoreCase(haMode);
	}

	/**
	 * return whether it has the table comment function or not
	 * @return
	 */
	public boolean isSupportTableComment() {
		return isSupportTableComment;
	}

	/**
	 * set the table comment function support
	 * @param isSupportTableComment
	 */
	public void setSupportTableComment(boolean isSupportTableComment) {
		this.isSupportTableComment = isSupportTableComment;
	}

	public boolean isShard() {
		// [TOOLS-2425]Support shard broker
		return isShard;
	}

	public void setShard(boolean isShard) {
		// [TOOLS-2425]Support shard broker
		this.isShard = isShard;
	}

	public int getCurrentShardId() {
		// [TOOLS-2425]Support shard broker
		return currentShardId;
	}

	public void setCurrentShardId(int currentShardId) {
		// [TOOLS-2425]Support shard broker
		this.currentShardId = currentShardId;
	}

	public int getCurrentShardVal() {
		// [TOOLS-2425]Support shard broker
		return currentShardVal;
	}

	public void setCurrentShardVal(int currentShardVal) {
		// [TOOLS-2425]Support shard broker
		this.currentShardVal = currentShardVal;
	}

	public int getShardQueryType() {
		// [TOOLS-2425]Support shard broker
		return shardQueryType;
	}

	public void setShardQueryType(int shardQueryType) {
		// [TOOLS-2425]Support shard broker
		this.shardQueryType = shardQueryType;
	}

	public String wrapShardQuery(String sql) {
		// [TOOLS-2425]Support shard broker
		if (!isShard) {
			return sql;
		}

		if (shardQueryType == SHARD_QUERY_TYPE_ID) {
			return QueryUtil.wrapShardQueryWithId(sql, currentShardId);
		}

		return QueryUtil.wrapShardQueryWithVal(sql, currentShardVal);
	}

	public static String wrapShardQuery(DatabaseInfo dbInfo, String sql) {
		// [TOOLS-2425]Support shard broker
		if (dbInfo == null) {
			return sql;
		}

		return dbInfo.wrapShardQuery(sql);
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public String getCollation() {
		return collation;
	}

	/**
	 * Get server OS info
	 * 
	 * @return OsInfoType The OS info type
	 */
	public FileUtil.OsInfoType getServerOsInfo() {
		if (serverInfo == null) {
			return null;
		}
		return serverInfo.getServerOsInfo();
	}
	
	/**
	 * Flat clone
	 */
	public DatabaseInfo clone() throws CloneNotSupportedException {
		DatabaseInfo obj = (DatabaseInfo) super.clone();
		DbUserInfo userInfo = null;
		if (this.authLoginedDbUserInfo != null) {
			userInfo = authLoginedDbUserInfo.clone();
		}
		obj.setAuthLoginedDbUserInfo(userInfo);
		return obj;
	}

	/**
	 * Get the broker name about current connected database
	 * @return
	 */
	public String getBrokerName() {
		String brokerName = "";
		List<BrokerInfo> brokers = serverInfo.getBrokerInfos()
				.getBorkerInfoList().getBrokerInfoList();
		for (BrokerInfo broker: brokers) {
			if (brokerPort.equals(broker.getPort().trim())) {
				brokerName = broker.getName();
			}
		}
		return brokerName;
	}
}
