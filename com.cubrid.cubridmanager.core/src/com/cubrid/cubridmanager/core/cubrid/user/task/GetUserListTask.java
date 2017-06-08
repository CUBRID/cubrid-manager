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
package com.cubrid.cubridmanager.core.cubrid.user.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.cubrid.user.model.UserGroup;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * 
 * get user task
 * 
 * @author fulei
 * @version 1.0 - 2012-09-10 created by fulei
 */
public class GetUserListTask extends JDBCTask {
	private boolean isCommentSupport = false;
	/**
	 * The constructor
	 * 
	 * @param dbInfo
	 * 
	 */
	public GetUserListTask(DatabaseInfo dbInfo) {
		super("GetAllUserList", dbInfo);
		isCommentSupport = CompatibleUtil.isCommentSupports(dbInfo);
	}

	public GetUserListTask(DatabaseInfo dbInfo, Connection connection) {
		super("GetAllUserList", dbInfo, connection);
		isCommentSupport = CompatibleUtil.isCommentSupports(dbInfo);
	}
	
	/**
	 * get DbUserInfoList
	 * @return DbUserInfoList
	 */
	public DbUserInfoList getResultModel(){
		DbUserInfoList dbUserInfoList = new DbUserInfoList();
		dbUserInfoList.setDbname(databaseInfo.getDbName());
		buildUserInfo(dbUserInfoList);
		finish();
		return dbUserInfoList;
	}

	/**
	 * 
	 * Build the database user information
	 * 
	 * @param dbUserInfoList DbUserInfoList
	 * @throws SQLException The exception
	 */
	private void buildUserInfo(DbUserInfoList dbUserInfoList) {
		Map<String, String> oidMap = new HashMap<String, String>();
		Map<String, List<String>> groupMap = new HashMap<String, List<String>>();

		String sql = isCommentSupport ?
				"SELECT db_user, name, groups, comment FROM db_user"
				: "SELECT db_user, name, groups FROM db_user";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String oidStr = rs.getString(1);
				String name = rs.getString(2);
				CUBRIDResultSetProxy cubridRS = (CUBRIDResultSetProxy) rs;
				Object[] objs = (Object[]) cubridRS.getCollection(3);
				List<String> groups = new ArrayList<String>();
				for (Object obj : objs) {
					if (obj != null
							&& obj.getClass() == CUBRIDOIDProxy.getCUBRIDOIDClass(cubridRS.getJdbcVersion())) {
						groups.add((new CUBRIDOIDProxy(obj)).getOidString());
					}
				}
				if (name != null) {
					name = name.toLowerCase(Locale.getDefault());
				}
				oidMap.put(oidStr, name);
				groupMap.put(name, groups);

				DbUserInfo dbUserInfo = new DbUserInfo();
				dbUserInfo.setDbName(databaseInfo.getDbName());
				dbUserInfo.setName(name);
				if (isCommentSupport) {
					String description = rs.getString(4);
					dbUserInfo.setDescription(description);
				}
				dbUserInfo.addAuthorization(new HashMap<String,String>());
				dbUserInfoList.addUser(dbUserInfo);
			}
		} catch(SQLException ex) {
			errorMsg = ex.getMessage();
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		// get the group user
		if (dbUserInfoList.getUserList() != null) {
			for (DbUserInfo userInfo : dbUserInfoList.getUserList()) {
				UserGroup userGroup = userInfo.getGroups();
				if (userGroup == null) {
					userGroup = new UserGroup();
					userInfo.addGroups(userGroup);
				}
				List<String> groupOIDList = groupMap.get(userInfo.getName());
				for (String groupOID : groupOIDList) {
					String groupUser = oidMap.get(groupOID);
					if (groupUser != null) {
						userInfo.getGroups().addGroup(groupUser);
					}
				}
			}
		}
		
		//get the member user
//		for (DbUserInfo userInfo : dbUserInfoList.getUserList()) {
//			List<String> groupNameList = userInfo.getGroupList();
//			for (String groupName : groupNameList) {
//				User groupUser = catalog.getUserByName(groupName);
//				if (groupUser != null) {
//					((CUBRIDUser) groupUser).addMember(user.getName());
//				}
//			}
//		}
	}

	/**
	 * Get the all database users
	 * 
	 * @param conn Connection
	 * @return List<String>
	 * @throws The exception
	 */
	public List<String> getAllDbUserList() throws SQLException {
		List<String> dbUserList = new ArrayList<String>();

		String sql = "SELECT name FROM db_user";

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				dbUserList.add(name);
			}
		} finally {
			finish();
		}

		return dbUserList;
	}
}
