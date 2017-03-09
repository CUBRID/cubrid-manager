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
package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * Test DbSpaceInfoList
 * 
 * @author lizhiqiang
 * @version 1.0 - 2009-12-30 created by lizhiqiang
 */
public class DbSpaceInfoListTest extends
		TestCase {
	DbSpaceInfoList bean;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		bean = new DbSpaceInfoList();
		bean.setDbname("dbname");
		bean.setPagesize(8);
		bean.setLogpagesize(8);
		bean.setFreespace(9);
		
		DbSpaceInfo dbSpaceInfo = new DbSpaceInfo();
		dbSpaceInfo.setType("GENERIC");
		dbSpaceInfo.setFreepage(256);
		dbSpaceInfo.setLocation("d:\\data");
		dbSpaceInfo.setSpacename("generic1");
		dbSpaceInfo.setTotalpage(1024);
		dbSpaceInfo.setTotalPageStr("1M");
		dbSpaceInfo.setTotalSizeStr("256M");
		
		bean.addSpaceinfo(dbSpaceInfo);
		
		dbSpaceInfo = new DbSpaceInfo();
		dbSpaceInfo.setType("GENERIC");
		dbSpaceInfo.setFreepage(256);
		dbSpaceInfo.setLocation("d:\\data");
		dbSpaceInfo.setSpacename("generic1");
		dbSpaceInfo.setTotalpage(1024);
		dbSpaceInfo.setTotalPageStr("1M");
		dbSpaceInfo.setTotalSizeStr("256M");
		bean.addSpaceinfo(dbSpaceInfo);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getTaskName()}
	 * .
	 */
	public void testGetTaskName() {
		assertEquals(bean.getTaskName(), "dbspaceinfo");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getDbname()}
	 * .
	 */
	public void testGetDbname() {
		assertEquals(bean.getDbname(), "dbname");
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getPagesize()}
	 * .
	 */
	public void testGetPagesize() {
		assertEquals(bean.getPagesize(), 8);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getLogpagesize()}
	 * .
	 */
	public void testGetLogpagesize() {
		assertEquals(bean.getLogpagesize(), 8);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getSpaceinfo()}
	 * .
	 */
	public void testGetSpaceinfo() {
		bean.setSpaceinfo(null);
		assertEquals(bean.getSpaceinfo(), null);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getSpaceInfoMap()}
	 * .
	 */
	public void testGetSpaceInfoMap() {
		bean.getSpaceInfoMap();
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#addSpaceinfo(com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo)}
	 * .
	 */
	@SuppressWarnings("unchecked")
	public void testAddSpaceinfo() {
		bean.addSpaceinfo(new DbSpaceInfo());
		assertEquals(bean.getSpaceinfo() instanceof List, true);
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#removeSpaceinfo(com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo)}
	 * .
	 */
	public void testRemoveSpaceinfo() {
		bean.removeSpaceinfo(new DbSpaceInfo());
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList#getFreespace()}
	 * .
	 */
	public void testGetFreespace() {
		assertEquals(bean.getFreespace(), 9);
	}

}
