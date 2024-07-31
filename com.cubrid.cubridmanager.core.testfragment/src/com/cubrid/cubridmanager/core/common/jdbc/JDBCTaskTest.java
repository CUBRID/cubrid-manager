package com.cubrid.cubridmanager.core.common.jdbc;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;

/**
 * Test JDBCTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-14 created by pangqiren
 */
public class JDBCTaskTest extends SetupJDBCTestCase {

    /** test jdbc task */
    public void testJDBCTask() {
        // normal case
        JDBCTask jdbcTask = new JDBCTask("taskname", databaseInfo);
        assertTrue(jdbcTask.getConnection() != null);
        assertFalse(jdbcTask.isCancel());
        assertTrue(jdbcTask.isSuccess());
        jdbcTask.execute();
        jdbcTask.cancel();
        assertTrue(jdbcTask.isCancel());
        jdbcTask.setCancel(false);
        assertFalse(jdbcTask.isCancel());
        jdbcTask.finish();
        // exception case
        jdbcTask = new JDBCTask("taskname", null);
        assertTrue(jdbcTask.isSuccess());
        assertFalse(jdbcTask.getErrorMsg() != null);
    }
}
