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
package com.cubrid.cubridmanager.core.broker.model;

import junit.framework.TestCase;

/**
 * TODO: how to write comments The purpose of the class Known bugs The
 * development/maintenance history of the class Document applicable invariants
 * The concurrency strategy
 * 
 * ApplyServerInfoTest Description
 * 
 * @author lizhiqiang
 * @version 1.0 - 2009-12-30 created by lizhiqiang
 */
public class ApplyServerInfoTest extends
		TestCase {
	ApplyServerInfo asi;

	public void setUp() {
		asi = new ApplyServerInfo();
		asi.setAsId("as_id");
		asi.setAsC("as_c");
		asi.setAsCpu("as_cpu");
		asi.setAsCtime("2000/12/12");
		asi.setAsCur("as_cur");
		asi.setAsDbHost("as_dbhost");
		asi.setAsDbName("as_dbname");
		asi.setAsErrorQuery("as_error_query");
		asi.setAsLat("as_lat");
		asi.setAsLongQuery("as_long_query");
		asi.setAsLct("as_lct");
		asi.setAsLongTran("as_long_tran");
		asi.setAsNumQuery("as_num_query");
		asi.setAsPid("as_pid");
		asi.setAsPsize("as_psize");
		asi.setAsStatus("as_status");
		asi.setAsNumTran("as_num_tran");
		asi.setAsPort("asPort");
		asi.setAsClientIp("asClientIp");
	}

	public void testGet() {
		String temp = asi.getAsId();
		assertEquals(temp, "as_id");
		temp = asi.getAsC();
		assertEquals(temp, "as_c");
		temp = asi.getAsCpu();
		assertEquals(temp, "as_cpu");
		temp = asi.getAsCtime();
		assertEquals(temp, "2000/12/12");
		temp = asi.getAsCur();
		assertEquals(temp, "as_cur");
		temp = asi.getAsDbHost();
		assertEquals(temp, "as_dbhost");
		temp = asi.getAsDbName();
		assertEquals(temp, "as_dbname");
		temp = asi.getAsErrorQuery();
		assertEquals(temp, "as_error_query");
		temp = asi.getAsLat();
		assertEquals(temp, "as_lat");
		temp = asi.getAsLongQuery();
		assertEquals(temp, "as_long_query");
		temp = asi.getAsLct();
		assertEquals(temp, "as_lct");
		temp = asi.getAsLongTran();
		assertEquals(temp, "as_long_tran");
		temp = asi.getAsNumQuery();
		assertEquals(temp, "as_num_query");
		temp = asi.getAsPid();
		assertEquals(temp, "as_pid");
		temp = asi.getAsPsize();
		assertEquals(temp, "as_psize");
		temp = asi.getAsStatus();
		assertEquals(temp, "as_status");
		temp = asi.getAsNumTran();
		assertEquals(temp, "as_num_tran");
		temp = asi.getAsPort();
		assertEquals("asPort", temp);
		temp = asi.getAsClientIp();
		assertEquals("asClientIp", temp);
	}

	public void testClone() {
		ApplyServerInfo newAs = asi.clone();
		assertNotSame(asi, newAs);
	}
	
	public void testClear() {
		asi.clear();
		assertEquals("", asi.getAsClientIp());
	}
}
