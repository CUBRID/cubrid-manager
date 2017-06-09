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

import java.sql.SQLException;
import java.util.List;

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * create user task
 * 
 * @author fulei
 * @version 1.0 - 2012-09-12 created by fulei
 */
public class CreateUserTask extends JDBCTask {
	private String userName;
	private String password;
	private List<String> groupList;
	private List<String> memberList;
	private String description;
	
	public CreateUserTask(DatabaseInfo dbInfo,
			String userName,String password, List<String> groupList,
			List<String> memberList, String description) {
		super("CreateUserTask", dbInfo);
		this.userName = userName;
		this.password = password;
		this.groupList = groupList;
		this.memberList = memberList;
		this.description = description;
	}
		
	public void execute() {
		try {
			createUser();
		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
	}
	
	/**
	 * create user 
	 * @param userName
	 * @param password
	 * @param groupList
	 * @param memberList
	 * @throws SQLException
	 */
	public void createUser() throws SQLException{
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE USER ");
		if (userName.trim().length() == 0) {
			sb.append("<USER_NAME>");
		} else {
			sb.append(QuerySyntax.escapeKeyword(userName)).append(" ");
		}
		if (password != null && password.trim().length() > 0) {
			sb.append(" PASSWORD '").append(password).append("'");
		}

		StringBuffer groupBf = new StringBuffer();
		for (String groupName : groupList) {
			groupBf.append(QuerySyntax.escapeKeyword(groupName)).append(",");
		}
		if (groupBf.length() > 0) {
			groupBf = groupBf.deleteCharAt(groupBf.length() - 1);
			sb.append(" GROUPS ").append(groupBf);
		}

		StringBuffer memberBf = new StringBuffer();
		for (String memberName : memberList) {
			memberBf.append(QuerySyntax.escapeKeyword(memberName)).append(",");
		}
		if (memberBf.length() > 0) {
			memberBf = memberBf.deleteCharAt(memberBf.length() - 1);
			sb.append(" MEMBERS ").append(memberBf);
		}

		if (StringUtil.isNotEmpty(description)) {
			description = String.format("'%s'", description);
			sb.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(description)));
		}

		String sql = sb.toString();

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			finish();
		}
	}
}
