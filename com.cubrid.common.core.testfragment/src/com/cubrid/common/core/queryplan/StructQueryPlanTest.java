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
package com.cubrid.common.core.queryplan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cubrid.common.core.queryplan.model.PlanResult;
import com.cubrid.cubridmanager.core.query.plan.PlanParserTest;

/**
 * @author fulei
 *
 * @version 1.0 - 2012-12-25 created by fulei
 */

public class StructQueryPlanTest extends PlanParserTest {
	private Date date = new Date();
	public void testStructQueryPlan () {
		StructQueryPlan plan1 = new StructQueryPlan("select * from test_a1", "plan1" , date);
		StructQueryPlan plan2 = new StructQueryPlan("select * from test_a2", "plan2" , date);
		List<StructQueryPlan> planList = new ArrayList<StructQueryPlan>();
		planList.add(plan1);
		planList.add(plan2);
		
		plan1.countSubPlan();
		assertEquals(plan1.getCreated(), date);
		plan1.getCreatedDateString();
		plan1.getPlanRaw();
		assertEquals(plan1.getSql(), "select * from test_a1");
		plan1.getSubPlan(1);
		plan1.setPlanRaw("xx");
		plan1.setSql("select * from test_a1");
		plan1.toString();
		plan1.toXML();
		String xml = StructQueryPlan.serialize(planList);
		assertEquals(StructQueryPlan.unserialize(xml).size(), planList.size());
		
		
		assertNotNull(plan2.clone());
		
		String planString = loadPlanExample("plan02.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertTrue(bool);
		parser.getPlanTree(1);
		PlanResult planRoot = parser.getPlanTree(0);
		plan2.setPlanRaw(planRoot.getParsedRaw());
		
		assertTrue(plan2.calCost() >= 0);
	}
}
