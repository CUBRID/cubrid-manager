package com.cubrid.cubridmanager.core.cubrid.database.model;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class DbUnloadInfoTest extends TestCase {

    public void testDbUnloadInfo() {
        DbUnloadInfo dbUnloadInfo = new DbUnloadInfo();
        dbUnloadInfo.setDbName("demodb");
        dbUnloadInfo.getIndexDateList().add("2009/09/08");
        dbUnloadInfo.getIndexPathList().add("/home/daniel/index");
        dbUnloadInfo.getObjectDateList().add("2009/09/07");
        dbUnloadInfo.getObjectPathList().add("/home/daniel/object");
        dbUnloadInfo.getSchemaDateList().add("2009/09/06");
        dbUnloadInfo.getSchemaPathList().add("/home/daniel/schema");
        dbUnloadInfo.getTriggerDateList().add("2009/09/05");
        dbUnloadInfo.getTriggerPathList().add("/home/daniel/trigger");
        assertEquals(dbUnloadInfo.getDbName(), "demodb");
        assertEquals(dbUnloadInfo.getIndexDateList().get(0), "2009/09/08");
        assertEquals(dbUnloadInfo.getIndexPathList().get(0), "/home/daniel/index");

        assertEquals(dbUnloadInfo.getObjectDateList().get(0), "2009/09/07");
        assertEquals(dbUnloadInfo.getObjectPathList().get(0), "/home/daniel/object");

        assertEquals(dbUnloadInfo.getSchemaDateList().get(0), "2009/09/06");
        assertEquals(dbUnloadInfo.getSchemaPathList().get(0), "/home/daniel/schema");

        assertEquals(dbUnloadInfo.getTriggerDateList().get(0), "2009/09/05");
        assertEquals(dbUnloadInfo.getTriggerPathList().get(0), "/home/daniel/trigger");
    }

    public void testSetList() {
        DbUnloadInfo dbUnloadInfo = new DbUnloadInfo();
        dbUnloadInfo.setDbName("demodb");
        List<String> schemaPathList = new ArrayList<String>();
        schemaPathList.add("schemaPathList");
        dbUnloadInfo.setSchemaPathList(schemaPathList);
        assertEquals("schemaPathList", dbUnloadInfo.getSchemaPathList().get(0));

        List<String> schemaDateList = new ArrayList<String>();
        schemaDateList.add("schemaDateList");
        dbUnloadInfo.setSchemaDateList(schemaDateList);
        assertEquals("schemaDateList", dbUnloadInfo.getSchemaDateList().get(0));

        List<String> objectPathList = new ArrayList<String>();
        objectPathList.add("objectPathList");
        dbUnloadInfo.setObjectPathList(objectPathList);
        assertEquals("objectPathList", dbUnloadInfo.getObjectPathList().get(0));

        List<String> objectDateList = new ArrayList<String>();
        objectDateList.add("objectDateList");
        dbUnloadInfo.setObjectDateList(objectDateList);
        assertEquals("objectDateList", dbUnloadInfo.getObjectDateList().get(0));

        List<String> indexPathList = new ArrayList<String>();
        indexPathList.add("indexPathList");
        dbUnloadInfo.setIndexPathList(indexPathList);
        assertEquals("indexPathList", dbUnloadInfo.getIndexPathList().get(0));

        List<String> indexDateList = new ArrayList<String>();
        indexDateList.add("indexDateList");
        dbUnloadInfo.setIndexDateList(indexDateList);
        assertEquals("indexDateList", dbUnloadInfo.getIndexDateList().get(0));

        List<String> triggerPathList = new ArrayList<String>();
        triggerPathList.add("triggerPathList");
        dbUnloadInfo.setTriggerPathList(triggerPathList);
        assertEquals("triggerPathList", dbUnloadInfo.getTriggerPathList().get(0));

        List<String> triggerDateList = new ArrayList<String>();
        triggerDateList.add("triggerDateList");
        dbUnloadInfo.setTriggerDateList(triggerDateList);
        assertEquals("triggerDateList", dbUnloadInfo.getTriggerDateList().get(0));
    }
}
