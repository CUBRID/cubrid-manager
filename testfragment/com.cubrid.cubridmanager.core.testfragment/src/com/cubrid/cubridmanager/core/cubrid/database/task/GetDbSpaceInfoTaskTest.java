package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.JsonObjectUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;

public class GetDbSpaceInfoTaskTest extends SetupEnvTestCase {

    public void testSendMessage() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        CommonQueryTask<DbSpaceInfoList> task =
                new CommonQueryTask<DbSpaceInfoList>(
                        serverInfo,
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new DbSpaceInfoList());
        task.setDbName(testDbName);
        task.setUsingSpecialDelimiter(false);
        task.execute();

        DbSpaceInfoList bean = task.getResultModel();
        assertEquals(null, task.getErrorMsg());
        System.out.println(task.getErrorMsg());
        assertEquals(testDbName, bean.getDbname());

        // test children
        assertEquals(true, bean.getSpaceinfo().size() > 0);
        System.out.println("------------the result of task:dbspaceinfo in JSON-----------------");
        System.out.println(JsonObjectUtil.object2json(bean));
        System.out.println("-------------------------------------------------------------------");
    }

    public void testExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        CommonQueryTask<DbSpaceInfoList> task =
                new CommonQueryTask<DbSpaceInfoList>(
                        serverInfo,
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new DbSpaceInfoList());
        task.setDbName("demodb");
        task.setUsingSpecialDelimiter(false);
        task.execute();

        assertTrue(task.isSuccess());

        DbSpaceInfoList bean = task.getResultModel();
        assertNull(task.getErrorMsg());

        assertEquals(true, bean.getSpaceinfo().size() > 0);
        assertEquals(
                "{\"dbname\":\"demodb\",\"freespace\":\"15216\",\"pagesize\":\"4096\",\"spaceInfoMap\":{\"\":{\"date\":\"\",\"freepage\":\"0\",\"location\":\"\",\"spacename\":\"\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"0\",\"type\":\"\",\"volumeCount\":\"1\"},\"ACTIVE_LOG\":{\"date\":\"20090703\",\"freepage\":\"0\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"Active_log\",\"volumeCount\":\"1\"},\"ARCHIVE_LOG\":{\"date\":\"20090620\",\"freepage\":\"0\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"4839\",\"type\":\"Archive_log\",\"volumeCount\":\"3\"},\"DATA\":{\"date\":\"20090702\",\"freepage\":\"2020\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"DATA\",\"volumeCount\":\"1\"},\"GENERIC\":{\"date\":\"20090703\",\"freepage\":\"1836\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"GENERIC\",\"volumeCount\":\"1\"}},\"spaceinfo\":[{\"date\":\"20090703\",\"freepage\":\"1836\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"demodb\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"GENERIC\",\"volumeCount\":\"0\"},{\"date\":\"20090702\",\"freepage\":\"2020\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"demodb_data_x001\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"DATA\",\"volumeCount\":\"0\"},{\"date\":\"20090620\",\"freepage\":\"0\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"demodb_lgar001\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2188\",\"type\":\"Archive_log\",\"volumeCount\":\"0\"},{\"date\":\"20090703\",\"freepage\":\"0\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"demodb_lgat\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"Active_log\",\"volumeCount\":\"0\"},{\"date\":\"20090626\",\"freepage\":\"0\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"demodb_lgar002\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"91\",\"type\":\"Archive_log\",\"volumeCount\":\"0\"},{\"date\":\"20090614\",\"freepage\":\"0\",\"location\":\"\\/opt\\/frameworks\\/cubrid2\\/databases\\/demodb\",\"spacename\":\"demodb_lgar000\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"2560\",\"type\":\"Archive_log\",\"volumeCount\":\"0\"},{\"date\":\"\",\"freepage\":\"0\",\"location\":\"\",\"spacename\":\"Total\",\"totalPageStr\":\"\",\"totalSizeStr\":\"\",\"totalpage\":\"0\",\"type\":\"\",\"volumeCount\":\"0\"}],\"taskName\":\"dbspaceinfo\"}",
                JsonObjectUtil.object2json(bean));
    }

    public void testNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        CommonQueryTask<DbSpaceInfoList> task =
                new CommonQueryTask<DbSpaceInfoList>(
                        serverInfo,
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new DbSpaceInfoList());
        task.setDbName("notexistdb");
        task.setUsingSpecialDelimiter(false);
        task.execute();

        assertFalse(task.isSuccess());

        DbSpaceInfoList bean = task.getResultModel();
        assertNotNull(task.getErrorMsg());

        assertEquals(
                "{\"dbname\":\"notexistdb\",\"freespace\":\"0\",\"pagesize\":\"-1\",\"spaceInfoMap\":{},\"spaceinfo\":\"\",\"taskName\":\"dbspaceinfo\"}",
                JsonObjectUtil.object2json(bean));

        // I expect failure result perfectly. But, fragment result attributes are exists. This may
        // be the cmserver bug.

    }
}
