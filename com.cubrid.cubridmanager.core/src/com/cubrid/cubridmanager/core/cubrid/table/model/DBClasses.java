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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.cubridmanager.core.common.model.IModel;

/**
 * This class is the model to store all classes in a database, these classes are
 * grouped to 2 types: system class and user class
 * 
 * @author moulinwang 2009-3-26
 */
public class DBClasses implements
		IModel {
	private String dbName;
	private ClassList systemClassList = null;
	private ClassList userClassList = null;

	public DBClasses() {
		systemClassList = new ClassList();
		userClassList = new ClassList();
	}

	/* (non-Javadoc)
	 * @see com.cubrid.cubridmanager.core.common.model.IModel#getTaskName()
	 */
	public String getTaskName() {
		return "classinfo";
	}

	/**
	 * Set the key "dbName" in request message
	 * 
	 * @param dbName String the database name
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Return all system defined classes
	 * 
	 * @return ClassList a list includes the object of SystemClass
	 */
	public ClassList getSystemClassList() {
		if (null == systemClassList) {
			systemClassList = new ClassList();
		}
		return systemClassList;

	}

	/**
	 * Return all user defined classes
	 * 
	 * @return ClassList the list includes the object of ClassList
	 */
	public ClassList getUserClassList() {
		if (null == userClassList) {
			userClassList = new ClassList();
		}
		return userClassList;
	}

	/**
	 * add a class item to system class list
	 * 
	 * @param classList ClassList The given list includes the object of
	 *        ClassList
	 */
	public void addSystemClass(ClassList classList) {
		this.systemClassList = classList;
	}

	/**
	 * add a class item to user class list
	 * 
	 * @param classList ClassList The given list includes the object of
	 *        ClassList
	 */
	public void addUserClass(ClassList classList) {
		synchronized (this) {
			this.userClassList = classList;
		}
	}

	public String getDbName() {
		return dbName;
	}

}
