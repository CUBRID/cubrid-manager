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
package com.cubrid.cubridmanager.core.cubrid.database.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;

/**
 * 
 * Test DatabaseInfo
 * 
 * @author Administrator
 * @version 1.0 - 2010-1-11 created by Administrator
 */
public class DatabaseInfoTest extends
		SetupJDBCTestCase {
	private DatabaseInfo bean;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		bean = new DatabaseInfo("dbName", new ServerInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getDbName()}
	 * .
	 */
	public void testGetDbName() {
		bean.setDbName("dbName");
		assertEquals(bean.getDbName(), "dbName");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getDbDir()}
	 * .
	 */
	public void testGetDbDir() {
		bean.setDbDir("dbDir");
		assertEquals(bean.getDbDir(), "dbDir");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getRunningType()}
	 * .
	 */
	public void testGetRunningType() {
		bean.setRunningType(DbRunningType.CS);
		assertEquals(bean.getRunningType(), DbRunningType.CS);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#isLogined()}
	 * .
	 */
	public void testIsLogined() {
		bean.setLogined(true);
		assertEquals(bean.isLogined(), true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getAuthLoginedDbUserInfo()}
	 * .
	 */
	public void testGetAuthLoginedDbUserInfo() {
		DbUserInfo authDbUserInfo = new DbUserInfo();
		bean.setAuthLoginedDbUserInfo(authDbUserInfo);
		assertNotNull(bean.getAuthLoginedDbUserInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getDbUserInfoList()}
	 * .
	 */
	public void testGetDbUserInfoList() {
		DbUserInfoList dbUserInfoList = new DbUserInfoList();
		bean.setDbUserInfoList(dbUserInfoList);
		assertTrue(bean.getDbUserInfoList() != null);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#addDbUserInfo(com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo)}
	 * .
	 */
	public void testAddDbUserInfo() {
		bean.addDbUserInfo(new DbUserInfo());
		bean.removeDbUserInfo(new DbUserInfo());
		bean.setDbUserInfoList(null);
		bean.removeDbUserInfo(new DbUserInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getTriggerList()}
	 * .
	 */
	public void testGetTriggerList() {
		bean.setTriggerList(new ArrayList<Trigger>());
		assertEquals(bean.getTriggerList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getTrigger(java.lang.String)}
	 * .
	 */
	public void testGetTrigger() {
		Trigger trigger = new Trigger();
		trigger.setName("trigger1");
		bean.setTriggerList(null);
		bean.setTriggerList(new ArrayList<Trigger>());

	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getClassInfoList()}
	 * .
	 */
	public void testGetClassInfoList() {
		bean.getClassInfoList();
		List<ClassInfo> userTableInfoList = new ArrayList<ClassInfo>();
		List<ClassInfo> userViewInfoList = new ArrayList<ClassInfo>();
		List<ClassInfo> sysTableInfoList = new ArrayList<ClassInfo>();
		List<ClassInfo> sysViewInfoList = new ArrayList<ClassInfo>();
		userTableInfoList.add(new ClassInfo("className"));
		userViewInfoList.add(new ClassInfo("className"));
		sysTableInfoList.add(new ClassInfo("className"));
		sysViewInfoList.add(new ClassInfo("className"));
		bean.getClassInfoList();
		bean.setUserTableInfoList(userTableInfoList);
		bean.setUserViewInfoList(userViewInfoList);
		bean.setSysTableInfoList(sysTableInfoList);
		bean.setSysViewInfoList(sysViewInfoList);
		bean.getClassInfoList();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getUserTableInfoList()}
	 * .
	 */
	public void testGetUserTableInfoList() {
		bean.setUserTableInfoList(new ArrayList<ClassInfo>());
		assertEquals(bean.getUserTableInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getUserViewInfoList()}
	 * .
	 */
	public void testGetUserViewInfoList() {
		bean.setUserViewInfoList(new ArrayList<ClassInfo>());
		assertEquals(bean.getUserViewInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getSysTableInfoList()}
	 * .
	 */
	public void testGetSysTableInfoList() {
		bean.setSysTableInfoList(new ArrayList<ClassInfo>());
		assertEquals(bean.getSysTableInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getSysViewInfoList()}
	 * .
	 */
	public void testGetSysViewInfoList() {
		bean.setSysViewInfoList(new ArrayList<ClassInfo>());
		assertTrue(bean.getSysViewInfoList().isEmpty());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getPartitionedTableMap()}
	 * .
	 */
	public void testGetPartitionedTableMap() {
		bean.setPartitionedTableMap(new HashMap<String, List<ClassInfo>>());
		assertEquals(bean.getPartitionedTableMap() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#addPartitionedTableList(java.lang.String, java.util.List)}
	 * .
	 */
	public void testAddPartitionedTableList() {
		bean.addPartitionedTableList("dbname", null);
		bean.addPartitionedTableList("dbname", null);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getBackupPlanInfoList()}
	 * .
	 */
	public void testGetBackupPlanInfoList() {
		bean.setBackupPlanInfoList(new ArrayList<BackupPlanInfo>());
		assertEquals(bean.getBackupPlanInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#addBackupPlanInfo(com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo)}
	 * .
	 */
	public void testAddBackupPlanInfo() {
		bean.addBackupPlanInfo(new BackupPlanInfo());
		bean.setBackupPlanInfoList(null);
		bean.addBackupPlanInfo(new BackupPlanInfo());
		bean.addBackupPlanInfo(new BackupPlanInfo());
		BackupPlanInfo a = new BackupPlanInfo();
		a.setDbname("dbname");
		bean.addBackupPlanInfo(a);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#removeAllBackupPlanInfo()}
	 * .
	 */
	public void testRemoveAllBackupPlanInfo() {
		bean.removeAllBackupPlanInfo();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getQueryPlanInfoList()}
	 * .
	 */
	public void testGetQueryPlanInfoList() {
		bean.setQueryPlanInfoList(new ArrayList<QueryPlanInfo>());
		assertEquals(bean.getQueryPlanInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#addQueryPlanInfo(com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo)}
	 * .
	 */
	public void testAddQueryPlanInfo() {
		bean.addQueryPlanInfo(new QueryPlanInfo());
		bean.setQueryPlanInfoList(null);
		bean.addQueryPlanInfo(new QueryPlanInfo());
		bean.removeQueryPlanInfo(new QueryPlanInfo());
		bean.addQueryPlanInfo(new QueryPlanInfo());
		bean.addQueryPlanInfo(new QueryPlanInfo());
		QueryPlanInfo a = new QueryPlanInfo();
		a.setDbname("dbname");
		bean.addQueryPlanInfo(a);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#removeQueryPlanInfo(com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo)}
	 * .
	 */
	public void testRemoveQueryPlanInfo() {
		QueryPlanInfo queryPlanInfo = new QueryPlanInfo();
		bean.removeQueryPlanInfo(queryPlanInfo);

		bean.setQueryPlanInfoList(new ArrayList<QueryPlanInfo>());
		bean.addQueryPlanInfo(queryPlanInfo);
		bean.removeQueryPlanInfo(queryPlanInfo);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#removeAllQueryPlanInfo()}
	 * .
	 */
	public void testRemoveAllQueryPlanInfo() {
		bean.removeAllQueryPlanInfo();
		bean.setQueryPlanInfoList(new ArrayList<QueryPlanInfo>());
		bean.removeAllQueryPlanInfo();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getDbSpaceInfoList()}
	 * .
	 */
	public void testGetDbSpaceInfoList() {
		bean.setDbSpaceInfoList(new DbSpaceInfoList());
		assertEquals(bean.getDbSpaceInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#addSpaceInfo(com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo)}
	 * .
	 */
	public void testAddSpaceInfo() {
		bean.addSpaceInfo(new DbSpaceInfoList.DbSpaceInfo());
		bean.addSpaceInfo(new DbSpaceInfoList.DbSpaceInfo());
		bean.removeSpaceInfo(new DbSpaceInfoList.DbSpaceInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#removeSpaceInfo(com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo)}
	 * .
	 */
	public void testRemoveSpaceInfo() {
		bean.removeSpaceInfo(new DbSpaceInfoList.DbSpaceInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getSpInfoList()}
	 * .
	 */
	public void testGetSpInfoList() {
		bean.getSpInfoList();
		List<SPInfo> spProcedureInfoList = new ArrayList<SPInfo>();
		spProcedureInfoList.add(new SPInfo("spName"));
		bean.setSpProcedureInfoList(spProcedureInfoList);
		bean.getSpInfoList();
		List<SPInfo> spFunctionInfoList = new ArrayList<SPInfo>();
		spFunctionInfoList.add(new SPInfo("spName"));
		bean.setSpFunctionInfoList(spFunctionInfoList);
		bean.getSpInfoList();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getSpProcedureInfoList()}
	 * .
	 */
	public void testGetSpProcedureInfoList() {
		bean.setSpProcedureInfoList(new ArrayList<SPInfo>());
		assertEquals(bean.getSpProcedureInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getSpFunctionInfoList()}
	 * .
	 */
	public void testGetSpFunctionInfoList() {
		bean.setSpFunctionInfoList(new ArrayList<SPInfo>());
		assertEquals(bean.getSpFunctionInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getSerialInfoList()}
	 * .
	 */
	public void testGetSerialInfoList() {
		bean.setSerialInfoList(new ArrayList<SerialInfo>());
		assertEquals(bean.getSerialInfoList() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getBrokerPort()}
	 * .
	 */
	public void testGetBrokerPort() {
		bean.setBrokerPort("brokerPort");
		assertEquals(bean.getBrokerPort(), "brokerPort");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#putSchemaInfo(com.cubrid.common.core.common.model.SchemaInfo)}
	 * .
	 */
	public void testPutSchemaInfo() {
		bean.putSchemaInfo(new SchemaInfo());
		bean.clearSchemas();
		bean.putSchemaInfo(new SchemaInfo());
		bean.putSchemaInfo(new SchemaInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#clearSchemas()}
	 * .
	 */
	public void testClearSchemas() {
		bean.clearSchemas();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getErrorMessage()}
	 * .
	 */
	public void testGetErrorMessage() {
		bean.getErrorMessage();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getServerInfo()}
	 * .
	 */
	public void testGetServerInfo() {
		bean.setServerInfo(new ServerInfo());
		assertEquals(bean.getServerInfo() != null, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getBrokerIP()}
	 * .
	 */
	public void testGetBrokerIP() {
		bean.setBrokerIP("192.168.0.1");
		assertEquals(bean.getBrokerIP(), "192.168.0.1");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getCharSet()}
	 * .
	 */
	public void testGetCharSet() {
		bean.setCharSet("charset");
		assertEquals(bean.getCharSet(), "charset");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#isDistributorDb()}
	 * .
	 */
	public void testIsDistributorDb() {
		bean.setDistributorDb(true);
		assertTrue(bean.isDistributorDb());
		bean.setDistributorDb(false);
		assertFalse(bean.isDistributorDb());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo#getReplInfo()}
	 * .
	 */
	public void testGetReplInfo() {
		bean.setReplInfo(new ReplicationInfo());
		assertNotNull(bean.getReplInfo());

	}

	/**
	 * Test Data
	 */
	public void testData() throws Exception {
		String createSuperSQL1;
		String sql;
		//bean.getSchemaInfo(null);
		bean.getSchemaInfo("table");
		bean.getSchemaInfo(testDbName);
		bean.setDbName(testDbName);
		bean.setAuthLoginedDbUserInfo(databaseInfo.getAuthLoginedDbUserInfo());
		bean.setServerInfo(serverInfo);
		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.message/sup1.txt");
		String msg = Tool.getFileContent(filepath);
		createSuperSQL1 = msg;
		String[] strs = msg.split(";");
		if (createSuperSQL1 != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					executeDDL(str);
				}
			}
		}

		bean.getSchemaInfo("sup1");
		bean.putSchemaInfo(bean.getSchemaInfo("sup1"));
		bean.getSchemaInfo("sup1");
		sql = "drop table sup1";
		executeDDL(sql);
	}

	/**
	 * execute DDL statement like "create table","drop table"
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public boolean executeDDL(String sql) {
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = JDBCConnectionManager.getConnection(databaseInfo, false);
			conn.setAutoCommit(true);
			stmt = conn.createStatement();
			boolean isMultiResult = stmt.execute(sql);
			assert (isMultiResult == false);
			success = true;
			conn.commit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			QueryUtil.freeQuery(conn, stmt);
		}
		return success;
	}

	public void testIsHAMode() {
		assertFalse(databaseInfo.isHAMode());
	}

	public void testRemoveSchema() {
		bean.removeSchema("Error");
	}

	public void testRemoveBackupPlanInfo() {
		bean.removeBackupPlanInfo(new BackupPlanInfo());

		bean.setBackupPlanInfoList(new ArrayList<BackupPlanInfo>());
		bean.removeBackupPlanInfo(new BackupPlanInfo());
		bean.removeAllBackupPlanInfo();
	}

	public void testGetRunningConfParameter() {
		bean.setParaDumpInfo(new ParamDumpInfo());
		assertNull(bean.getRunningConfParameter("param"));
	}
}
