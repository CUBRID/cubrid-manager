/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.cubridmanager.core.cubrid.sp.model;

import junit.framework.TestCase;

/**
 * Test SPArgsInfo model
 * 
 * @author wuyingshi
 * @version 1.0 - 2009-12-30 created by wuyingshi
 */
public class SPArgsInfoTest extends
		TestCase {

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.sp.model.SPArgsInfo#SPArgsInfo()}
	 * .
	 */
	public void testSPArgsInfo() {
		String spName = "spName";
		int index = 1;
		String argName = "argName";
		String dataType = "String";
		SPArgsType spArgsType = SPArgsType.IN;
		//test constructor
		SPArgsInfo spArgsInfoNoArg = new SPArgsInfo();
		assertNotNull(spArgsInfoNoArg);
		SPArgsInfo spArgsInfo = new SPArgsInfo(spName, argName, index,
				dataType, spArgsType, null);
		assertNotNull(spArgsInfo);

		//test 	getters and setters	
		spArgsInfo.setSpName(spName);
		spArgsInfo.setIndex(index);
		spArgsInfo.setArgName(argName);
		spArgsInfo.setDataType(dataType);
		spArgsInfo.setSpArgsType(spArgsType);

		assertEquals(spArgsInfo.getSpName(), spName);
		assertEquals(spArgsInfo.getIndex(), index);
		assertEquals(spArgsInfo.getArgName(), argName);
		assertEquals(spArgsInfo.getDataType(), dataType);
		assertEquals(spArgsInfo.getSpArgsType(), spArgsType);
	}

}
