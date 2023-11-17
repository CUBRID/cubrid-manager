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
package com.cubrid.cubridmanager.core.cubrid.user.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations;

/**
 *
 * get user privilege task
 *
 * @author fulei
 * @version 1.0 - 2012-09-11 created by fulei
 */

public class GetUserAuthorizationsTask extends JDBCTask {
	/**
	 * The constructor
	 * 
	 * @param dbInfo
	 * 
	 */
	public GetUserAuthorizationsTask(DatabaseInfo dbInfo) {
		super("GetUserPrivilegeTask", dbInfo);
	}

	public GetUserAuthorizationsTask(DatabaseInfo dbInfo, Connection con) {
		super("GetUserPrivilegeTask", dbInfo, con);
	}

	/**
	 * get privilege information
	 * 
	 * @param userName String
	 * @throws SQLException The exception
	 */
	public Map<String, ClassAuthorizations> getUserAuthorizations(String userName) throws SQLException {
		Map<String, ClassAuthorizations> userAuthMap = new HashMap<String, ClassAuthorizations>();
		if (userName == null) {
			return userAuthMap;
		}

		String sql;
		if (databaseInfo.isSupportUserSchema()) {
			sql = "SELECT a.class_name, a.auth_type, a.is_grantable, a.owner_name"
				+ " FROM db_auth a"
				+ " WHERE a.grantee_name=UPPER('" + userName + "')"
				+ " AND NOT EXISTS (SELECT 1 FROM db_partition p"
				+ " WHERE a.class_name=LOWER(p.partition_class_name)"
				+ " AND a.owner_name=p.owner_name)"
				;
		} else {
			sql = "SELECT a.class_name, a.auth_type, a.is_grantable"
					+ " FROM db_auth a"
					+ " WHERE a.grantee_name=UPPER('" + userName + "')"
					+ " AND NOT EXISTS (SELECT 1 FROM db_partition p"
					+ " WHERE a.class_name=LOWER(p.partition_class_name))";
		}

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String className = rs.getString("class_name");
				String type = rs.getString("auth_type");
				String grantable = rs.getString("is_grantable");
				if (className == null || className.trim().length() == 0) {
					continue;
				}
				String ownerName = "";
				if (databaseInfo.isSupportUserSchema()) {
					ownerName = rs.getString("owner_name");
				}
				
				ClassAuthorizations auth = userAuthMap.get(className);
				if (auth == null) {
					auth = new ClassAuthorizations();
					auth.setClassName(className);
					auth.setOwnerName(ownerName);
					if (!ownerName.isEmpty()) {
						userAuthMap.put(ownerName + "." + className, auth);
					} else {
						userAuthMap.put(className, auth);
					}
				}

				makeAuthorizations(type, grantable, auth);
			}
		} finally {
			finish();
		}

		return userAuthMap;
	}

	/**
	 * 
	 * get view privilege information
	 * @param viewName owner
	 * @param viewName String
	 * @throws SQLException The exception
	 */
	public Map<String, ClassAuthorizations> getViewAuthorizationsByViewName(
			String userName, String viewName) throws SQLException {
		if (viewName == null || userName == null) {
			return null;
		}

		Map<String, ClassAuthorizations> userAuthMap = new HashMap<String, ClassAuthorizations>();
		String sql;
		if (databaseInfo.isSupportUserSchema()) {
			sql = "SELECT a.auth_type, a.grantee_name, a.class_name, a.is_grantable, a.owner_name"
				+ " FROM db_auth a, db_vclass v"
				+ " WHERE a.class_name=v.vclass_name"
				+ " AND a.owner_name=v.owner_name"
				+ " AND a.grantee_name<>UPPER('" + userName+ "')"
				+ " AND a.class_name='" + viewName + "'";
		} else {
			sql = "SELECT a.auth_type, a.grantee_name, a.class_name, a.is_grantable"
					+ " FROM db_auth a, db_vclass v"
					+ " WHERE a.class_name=v.vclass_name"
					+ " AND a.grantee_name<>UPPER('" + userName+ "')"
					+ " AND a.class_name='" + viewName + "'";
		}

		// [TOOLS-2425]Support shard broker
		sql = databaseInfo.wrapShardQuery(sql);

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String type = rs.getString("auth_type");
				String grantable = rs.getString("is_grantable");
				String granteeName = rs.getString("grantee_name");
				String className = rs.getString("class_name");

				if (granteeName == null || granteeName.trim().length() == 0) {
					continue;
				}

				String ownerName = "";
				if (databaseInfo.isSupportUserSchema()) {
					ownerName = rs.getString("owner_name");
				}
				
				ClassAuthorizations auth = userAuthMap.get(granteeName);
				if (auth == null) {
					auth = new ClassAuthorizations();
					auth.setClassName(className);
					auth.setOwnerName(ownerName);
					userAuthMap.put(granteeName, auth);
				}

				makeAuthorizations(type, grantable, auth);
			}
		} finally {
			finish();
		}

		return userAuthMap;
	}

	private void makeAuthorizations(String type, String grantable,
			ClassAuthorizations auth) {
		if (type.equals("SELECT")) {
			auth.setSelectPriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantSelectPriv(true);
			}
		} else if (type.equals("UPDATE")) {
			auth.setUpdatePriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantUpdatePriv(true);
			}
		} else if (type.equals("INSERT")) {
			auth.setInsertPriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantInsertPriv(true);
			}
		} else if (type.equals("DELETE")) {
			auth.setDeletePriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantDeletePriv(true);
			}
		} else if (type.equals("ALTER")) {
			auth.setAlterPriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantAlterPriv(true);
			}
		} else if (type.equals("EXECUTE")) {
			auth.setExecutePriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantExecutePriv(true);
			}
		} else if (type.equals("INDEX")) {
			auth.setIndexPriv(true);
			if (grantable.equals("YES")) {
				auth.setGrantIndexPriv(true);
			}
		}
	}
}
