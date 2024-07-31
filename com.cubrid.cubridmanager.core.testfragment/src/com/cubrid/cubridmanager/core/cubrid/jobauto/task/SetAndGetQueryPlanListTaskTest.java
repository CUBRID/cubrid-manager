package com.cubrid.cubridmanager.core.cubrid.jobauto.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests Type GetQueryPlanListTask and SetQueryPlanListTask
 *
 * @author lizhiqiang Apr 3, 2009
 */
public class SetAndGetQueryPlanListTaskTest extends SetupEnvTestCase {
    /** Tests getBackupPlanListTask method */
    public void test() {
        QueryPlanInfo queryPlan = new QueryPlanInfo();
        queryPlan.setDbname(testDbName);
        queryPlan.setQuery_id("test_set");
        queryPlan.setPeriod("MONTH");
        queryPlan.setDetail("1 02:01");
        List<String> msgList = new ArrayList<String>();
        QueryPlanInfoHelp qHelp = new QueryPlanInfoHelp();
        qHelp.setQueryPlanInfo(queryPlan);
        msgList.add(qHelp.buildMsg(true));
        // test set query plans
        SetQueryPlanListTask taskSet = new SetQueryPlanListTask(serverInfo);
        taskSet.setDbname(testDbName);
        taskSet.buildMsg(msgList);
        taskSet.execute();
        String errorSet = taskSet.getErrorMsg();
        assertNull(errorSet);
        // get query plan list
        GetQueryPlanListTask taskGet = new GetQueryPlanListTask(serverInfo);
        taskGet.setDbName(testDbName);
        taskGet.execute();
        String errorGet = taskGet.getErrorMsg();
        assertNull(errorGet);
        List<QueryPlanInfo> queryPlanInfoList = taskGet.getQueryPlanInfoList();
        assertNotNull(queryPlanInfoList);

        if (queryPlanInfoList.size() > 0) {
            QueryPlanInfo info = queryPlanInfoList.get(0);
            assertNotNull(info);
        }
        // delete query plan
        taskSet = new SetQueryPlanListTask(serverInfo);
        taskSet.setDbname(testDbName);
        taskSet.buildMsg(new ArrayList<String>());
        taskSet.execute();
        assertTrue(taskSet.isSuccess());

        taskGet.setErrorMsg("err");
        taskGet.getQueryPlanInfoList();
    }
}
