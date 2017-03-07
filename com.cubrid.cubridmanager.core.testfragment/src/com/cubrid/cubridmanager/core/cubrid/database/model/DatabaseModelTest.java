/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import java.util.ArrayList;
import java.util.HashMap;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;

public class DatabaseModelTest extends
		SetupEnvTestCase {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testModelDatabaseInfo() {
		DatabaseInfo bean = new DatabaseInfo(testDbName, serverInfo);
		bean.setDbName("dbName");
		assertEquals(bean.getDbName(), "dbName");
		bean.setLogined(true);
		assertTrue(bean.isLogined());
		bean.setDbDir("dbDir");
		assertEquals(bean.getDbDir(), "dbDir");
		bean.setRunningType(DbRunningType.CS);
		assertEquals(bean.getRunningType(), DbRunningType.CS);
		DbUserInfoList dbUserInfoList = new DbUserInfoList();
		bean.setDbUserInfoList(dbUserInfoList);
		assertTrue(bean.getDbUserInfoList() != null);

		DbUserInfo dbUserInfo = new DbUserInfo();
		bean.addDbUserInfo(dbUserInfo);
		bean.removeDbUserInfo(dbUserInfo);

		bean.setAuthLoginedDbUserInfo(dbUserInfo);
		assertEquals(bean.getAuthLoginedDbUserInfo(), dbUserInfo);

		bean.setBrokerPort("brokerPort");
		assertEquals(bean.getBrokerPort(), "brokerPort");
		bean.setTriggerList(new ArrayList());
		assertEquals(bean.getTriggerList() != null, true);
		bean.setUserTableInfoList(new ArrayList());
		assertEquals(bean.getUserTableInfoList() != null, true);
		bean.setUserViewInfoList(new ArrayList());
		assertEquals(bean.getUserViewInfoList() != null, true);
		bean.setSysTableInfoList(new ArrayList());
		assertEquals(bean.getSysTableInfoList() != null, true);
		bean.setSysViewInfoList(new ArrayList());
		assertEquals(bean.getSysViewInfoList() != null, true);
		bean.setPartitionedTableMap(new HashMap());
		assertEquals(bean.getPartitionedTableMap() != null, true);
		bean.setBackupPlanInfoList(new ArrayList());
		assertEquals(bean.getBackupPlanInfoList() != null, true);
		bean.setQueryPlanInfoList(new ArrayList());
		assertEquals(bean.getQueryPlanInfoList() != null, true);
		bean.setDbSpaceInfoList(new DbSpaceInfoList());
		assertEquals(bean.getDbSpaceInfoList() != null, true);
		bean.setDbUserInfoList(new DbUserInfoList());
		assertEquals(bean.getDbUserInfoList() != null, true);
		bean.setSpProcedureInfoList(new ArrayList());
		assertEquals(bean.getSpProcedureInfoList() != null, true);
		bean.setSpFunctionInfoList(new ArrayList());
		assertEquals(bean.getSpFunctionInfoList() != null, true);
		bean.setSerialInfoList(new ArrayList());
		assertEquals(bean.getSerialInfoList() != null, true);
		bean.setServerInfo(serverInfo);
		assertEquals(bean.getServerInfo() != null, true);
		bean.clear();
		bean.setLogined(true);
		assertEquals(bean.isLogined(), true);
		bean.addDbUserInfo(new DbUserInfo());
		bean.removeDbUserInfo(new DbUserInfo());
		bean.getClassInfoList();
		bean.addPartitionedTableList("dbname", null);
		bean.addBackupPlanInfo(new BackupPlanInfo());
		bean.removeBackupPlanInfo(new BackupPlanInfo());
		bean.removeAllBackupPlanInfo();
		bean.addQueryPlanInfo(new QueryPlanInfo());
		bean.removeQueryPlanInfo(new QueryPlanInfo());
		bean.removeAllQueryPlanInfo();
		bean.addSpaceInfo(new DbSpaceInfoList.DbSpaceInfo());
		bean.removeSpaceInfo(new DbSpaceInfoList.DbSpaceInfo());
		bean.getSpInfoList();
		bean.getSchemaInfo("tableName");
		bean.putSchemaInfo(new SchemaInfo());
		bean.clearSchemas();
		bean.getErrorMessage();
		bean.setDbUserInfoList(null);
		assertEquals(bean.getDbUserInfoList(), null);
		bean.addDbUserInfo(new DbUserInfo());
		bean.removeDbUserInfo(new DbUserInfo());
	}

}
