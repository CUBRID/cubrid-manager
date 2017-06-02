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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * test SPInfo model
 * 
 * @author wuyingshi
 * @version 1.0 - 2009-12-30 created by wuyingshi
 */
public class SPInfoTest extends
		TestCase {

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo#SPInfo(java.lang.String)}
	 * .
	 */
	public void testSPInfoString() {
		String spName = "spName";
		SPType spType = SPType.FUNCTION;
		String returnType = "returnType";
		String language = "language";
		String owner = "owner";
		String target = "target";
		List<SPArgsInfo> argsInfoList = new ArrayList<SPArgsInfo>();
		SPArgsInfo sPArgsInfo = new SPArgsInfo();
		//test constructor
		SPInfo sPInfo1 = new SPInfo(spName);
		assertNotNull(sPInfo1);
		SPInfo sPInfo = new SPInfo(spName, spType, returnType, language, owner,
				target, null);
		assertNotNull(sPInfo);

		//test 	getters and setters	
		sPInfo.setSpName(spName);
		sPInfo.setSpType(spType);
		sPInfo.setReturnType(returnType);
		sPInfo.setLanguage(language);
		sPInfo.setOwner(owner);
		sPInfo.setTarget(target);
		sPInfo.setArgsInfoList(null);

		assertEquals(sPInfo.getSpName(), spName);
		assertEquals(sPInfo.getSpType(), spType);
		assertEquals(sPInfo.getReturnType(), returnType);
		assertEquals(sPInfo.getLanguage(), language);
		assertEquals(sPInfo.getOwner(), owner);
		assertEquals(sPInfo.getTarget(), target);
		assertEquals(sPInfo.getArgsInfoList(), null);

		//test 	public void removeSPArgsInfo(SPArgsInfo spArgsInfo)
		sPInfo.removeSPArgsInfo(sPArgsInfo);

		//test 	public void addSPArgsInfo(SPArgsInfo spArgsInfo) 
		assertNotNull(argsInfoList);
		sPInfo.addSPArgsInfo(sPArgsInfo);
		argsInfoList = null;
		sPInfo.addSPArgsInfo(sPArgsInfo);
	}
}
