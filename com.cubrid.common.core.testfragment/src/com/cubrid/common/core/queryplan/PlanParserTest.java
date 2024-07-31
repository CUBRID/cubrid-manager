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

import com.cubrid.common.core.queryplan.model.PlanResult;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import java.util.Date;

/**
 * Query plan Parser Test
 *
 * @author isaiah
 * @version 1.0 - 2012-12-25 created by isaiah
 */
public class PlanParserTest extends SetupEnvTestCase {
    private Date date = new Date();

    public void testPlan1() throws Exception {
        String filepath = this.getFilePathInPlugin("/com/cubrid/common/core/queryplan/plan1.txt");
        String rawPlan = Tool.getFileContent(filepath);

        StructQueryPlan plan1 = new StructQueryPlan("select * from db_auth", rawPlan, date);
        for (int i = 0; i < plan1.countSubPlan(); i++) {
            PlanResult planResult = plan1.getSubPlan(i);
            System.err.println(planResult.getParsedRaw());
            System.err.println(planResult.getPlanNode());
        }

        assertEquals(plan1.countSubPlan(), 4);
    }

    @Override
    protected boolean isSetupDatabase() {
        return false;
    }
}
