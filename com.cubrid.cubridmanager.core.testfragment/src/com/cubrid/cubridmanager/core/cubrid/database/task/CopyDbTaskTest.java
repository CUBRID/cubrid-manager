package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

public class CopyDbTaskTest extends SetupEnvTestCase {

    public void testCopyExistDB() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.copydb.001.req.txt>");

        CopyDbTask task = new CopyDbTask(serverInfo);
        task.setSrcdbname("demodb");
        task.setDestdbname("newdb");
        task.setDestdbpath("/opt/frameworks/cubrid/databases/newdb");
        task.setExvolpath("/opt/frameworks/cubrid/databases/newdb");
        task.setLogpath("/opt/frameworks/cubrid/databases/newdb");
        task.setOverwrite(YesNoType.N);
        task.setMove(YesNoType.N);
        task.setAdvanced(OnOffType.OFF);

        task.execute();
        assertNull(task.getErrorMsg());
    }

    public void testCopyTargetExistDB() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.copydb.002.req.txt>");

        CopyDbTask task = new CopyDbTask(serverInfo);
        task.setSrcdbname("existdb");
        task.setDestdbname("newdb");
        task.setDestdbpath("/opt/frameworks/cubrid/databases/newdb");
        task.setExvolpath("/opt/frameworks/cubrid/databases/newdb");
        task.setLogpath("/opt/frameworks/cubrid/databases/newdb");
        task.setOverwrite(YesNoType.Y);
        task.setMove(YesNoType.N);
        task.setAdvanced(OnOffType.OFF);

        task.execute();
        assertNull(task.getErrorMsg());
    }

    public void testCopyDbEmptyDestPath() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.copydb.003.req.txt>");

        CopyDbTask task = new CopyDbTask(serverInfo);
        task.setSrcdbname("demodb");
        task.setDestdbname("newdb");
        task.setDestdbpath(null);
        task.setExvolpath("/opt/frameworks/cubrid/databases/newdb");
        task.setLogpath("/opt/frameworks/cubrid/databases/newdb");
        task.setOverwrite(YesNoType.N);
        task.setMove(YesNoType.N);
        task.setAdvanced(OnOffType.OFF);

        task.execute();
        assertNotNull(task.getErrorMsg());
    }

    public void testCopyDbEmptyExvolPath() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.copydb.004.req.txt>");

        CopyDbTask task = new CopyDbTask(serverInfo);
        task.setSrcdbname("demodb");
        task.setDestdbname("newdb");
        task.setDestdbpath("/opt/frameworks/cubrid/databases/newdb");
        task.setExvolpath(null);
        task.setLogpath("/opt/frameworks/cubrid/databases/newdb");
        task.setOverwrite(YesNoType.N);
        task.setMove(YesNoType.N);
        task.setAdvanced(OnOffType.OFF);

        task.execute();
        assertNotNull(task.getErrorMsg());
    }

    public void testCopyDbEmptyLogPath() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.copydb.005.req.txt>");

        CopyDbTask task = new CopyDbTask(serverInfo);
        task.setSrcdbname("demodb");
        task.setDestdbname("newdb");
        task.setDestdbpath("/opt/frameworks/cubrid/databases/newdb");
        task.setExvolpath("/opt/frameworks/cubrid/databases/newdb");
        task.setLogpath(null);
        task.setOverwrite(YesNoType.N);
        task.setMove(YesNoType.N);
        task.setAdvanced(OnOffType.OFF);

        task.execute();
        assertNotNull(task.getErrorMsg());
    }

    public void testCopyNotExistDB() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.copydb.006.req.txt>");

        CopyDbTask task = new CopyDbTask(serverInfo);
        task.setSrcdbname("notexistdb");
        task.setDestdbname("newdb");
        task.setDestdbpath("/opt/frameworks/cubrid/databases/newdb");
        task.setExvolpath("/opt/frameworks/cubrid/databases/newdb");
        task.setLogpath("/opt/frameworks/cubrid/databases/newdb");
        task.setOverwrite(YesNoType.N);
        task.setMove(YesNoType.N);
        task.setAdvanced(OnOffType.OFF);

        task.execute();
        assertNotNull(task.getErrorMsg());
    }
}
