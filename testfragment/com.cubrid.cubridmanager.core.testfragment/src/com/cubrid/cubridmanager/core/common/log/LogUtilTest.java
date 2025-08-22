package com.cubrid.cubridmanager.core.common.log;

import com.cubrid.common.core.util.LogUtil;
import junit.framework.TestCase;
import org.apache.log4j.Level;

public class LogUtilTest extends TestCase {
    public void testConfigLogger() {
        LogUtil.configLogger(Level.ALL, "../../../../../../../");
        LogUtil.getLogger(LogUtilTest.class).debug("logtest");

        LogUtil.configLogger(Level.DEBUG, "../../../../../../../");
        LogUtil.getLogger(LogUtilTest.class).debug("logtest");

        LogUtil.configLogger(Level.ERROR, "../../../../../../../");
        LogUtil.getLogger(LogUtilTest.class).debug("logtest");

        LogUtil.configLogger(Level.FATAL, "../../../../../../../");
        LogUtil.getLogger(LogUtilTest.class).debug("logtest");

        LogUtil.configLogger(Level.INFO, "../../../../../../../");
        LogUtil.getLogger(LogUtilTest.class).debug("logtest");

        LogUtil.configLogger(Level.WARN, "../../../../../../../");
        LogUtil.getLogger(LogUtilTest.class).debug("logtest");

        LogUtil.configLogger(null, null);
    }
}
