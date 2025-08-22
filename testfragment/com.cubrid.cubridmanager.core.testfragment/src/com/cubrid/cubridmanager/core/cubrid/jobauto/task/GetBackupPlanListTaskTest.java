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
package com.cubrid.cubridmanager.core.cubrid.jobauto.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * Test GetBackupPlanListTask class
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class GetBackupPlanListTaskTest extends SetupEnvTestCase {

    public void testRealEnv() {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        boolean isSucce = this.addBackupPlan("eee");
        isSucce = isSucce && this.addBackupPlan("fff");
        if (isSucce) {
            GetBackupPlanListTask task = new GetBackupPlanListTask(serverInfo);
            task.setDbName(testDbName);
            task.execute();
            assertTrue(task.getBackupPlanInfoList().size() >= 2);
            this.deleteBackupPlan("eee");
            this.deleteBackupPlan("fff");
            task.setResponse(new TreeNode());
            task.getBackupPlanInfoList();
            task.setErrorMsg("err");
            task.getBackupPlanInfoList();
        }
    }

    public void testNullDbName() {
        GetBackupPlanListTask task = new GetBackupPlanListTask(serverInfo);
        task.setDbName(null);
        task.execute();

        assertFalse(task.isSuccess());
        assertTrue(task.getErrorMsg() != null && task.getErrorMsg().trim().length() > 0);

        assertNull(task.getBackupPlanInfoList());
    }
}
