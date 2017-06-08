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
package com.cubrid.cubridmanager.core.cubrid.user.model;

import java.util.Locale;
import java.util.Map;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations;

/**
 * This class is responsible to store all database user information
 * 
 * @author pangqiren 2009-3-23
 */
public class DbUserInfo {
	private String dbName = null;
	private String name = null;
	private String password = null;
	private String noEncryptPassword = null;
	private UserGroup groups;
	private boolean isDbaAuthority = false;
	private String description;
	private Map<String, String> authorization;//CM previlege
	private Map<String, ClassAuthorizations> userAuthorizations;//CQB previlege
	/**
	 * The constructor
	 */
	public DbUserInfo() {
		//empty
	}

	/**
	 * @param dbName
	 * @param dbUser
	 * @param dbPassword
	 * @param isDBAGroup
	 */
	public DbUserInfo(String dbName, String name, String password,
			String noEncryptPassword, boolean isDbaAuthority) {
		super();
		this.dbName = dbName;
		this.name = name;
		this.password = password;
		this.noEncryptPassword = noEncryptPassword;
		this.isDbaAuthority = isDbaAuthority;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getName() {
		return name;
	}

	/**
	 * Set name
	 * 
	 * @param name String
	 */
	public void setName(String name) {
		if (name != null) {
			this.name = name.toLowerCase(Locale.getDefault());
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserGroup getGroups() {
		return groups;
	}

	public Map<String, String> getAuthorization() {
		return authorization;
	}

	/**
	 * 
	 * add authorization
	 * 
	 * @param authorization Map<String, String>
	 */
	public void addAuthorization(Map<String, String> authorization) {
		this.authorization = authorization;
	}

	/**
	 * add groups
	 * 
	 * @param groups UserGroup
	 */
	public void addGroups(UserGroup groups) {
		this.groups = groups;
	}

	public boolean isDbaAuthority() {
		return isDbaAuthority;
	}

	public void setDbaAuthority(boolean isDbaAuthority) {
		this.isDbaAuthority = isDbaAuthority;
	}

	public String getNoEncryptPassword() {
		return noEncryptPassword;
	}

	public void setNoEncryptPassword(String noEncryptPassword) {
		this.noEncryptPassword = noEncryptPassword;
	}

	public Map<String, ClassAuthorizations> getUserAuthorizations() {
		return userAuthorizations;
	}

	public void setUserAuthorizations(
			Map<String, ClassAuthorizations> userAuthorizations) {
		this.userAuthorizations = userAuthorizations;
	}
	
	public boolean isPublic() {
		return StringUtil.isEqualIgnoreCase("PUBLIC", name);	
	}

	/*Flat clone*/
	public DbUserInfo clone() throws CloneNotSupportedException{
		DbUserInfo obj = (DbUserInfo) super.clone();
		return obj;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
