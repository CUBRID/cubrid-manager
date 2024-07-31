package com.cubrid.cubridmanager.core.utils;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import java.util.ArrayList;
import java.util.List;

public class CoreUtilsTest extends SetupEnvTestCase {
    public void testChangeHAModeFromCubridConf() {
        List<String> lst;

        try {
            lst = CoreUtils.changeHAModeFromCubridConf(null, null, null);
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            lst = CoreUtils.changeHAModeFromCubridConf(null, null, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        GetCubridConfParameterTask task = null;

        try {
            task = new GetCubridConfParameterTask(null);
            lst = CoreUtils.changeHAModeFromCubridConf(task, null, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<String> clist = new ArrayList<String>();
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<String> clist = new ArrayList<String>();
            lst = CoreUtils.changeHAModeFromCubridConf(null, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(null, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            serverInfo.setConnected(false);
            task = new GetCubridConfParameterTask(serverInfo);
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            serverInfo.setAllDatabaseList(null);
            task = new GetCubridConfParameterTask(serverInfo);
            TreeNode response = new TreeNode();
            response.add("confdata", "ha_mode=ON");
            //			TreeNode child = new TreeNode();
            //			child.add("open", "conflist");
            //			child.add("confdata", "1");
            //			child.add("confdata", "2");
            //			response.addChild(child);

            TreeNode child2 = new TreeNode();
            child2.add("open", "conflist");
            child2.add("confdata", "[service]");
            child2.add("confdata", "service");
            child2.add("confdata", "common=1");
            child2.add("confdata", "[common]=2");
            child2.add("confdata", "ha_mode=ON");
            child2.add("confdata", ConfConstants.COMMON_SECTION);
            response.addChild(child2);

            task.setResponse(response);
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "YES");
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "REPLICA");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=on");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "YES");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=off");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "YES");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=on");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "REPLICA");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=on");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=off");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=off");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            serverInfo.setConnected(false);
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            serverInfo.setAllDatabaseList(null);
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "YES");
            List<String> clist = new ArrayList<String>();
            clist.add("check1");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "REPLICA");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=on");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "YES");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=off");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "YES");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=on");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "REPLICA");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=on");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.HA_MODE + "=off");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add("# The createdb creates a log volume file of 'log_volume_size' size\n");
            clist.add("# if don't have any options about size.\n");
            clist.add("");
            clist.add("ha_mode=on");
            clist.add("");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add("# The createdb creates a log volume file of 'log_volume_size' size\n");
            clist.add("# if don't have any options about size.\n");
            clist.add("");
            clist.add("ha_mode=off");
            clist.add("");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add("# The createdb creates a log volume file of 'log_volume_size' size\n");
            clist.add("# if don't have any options about size.\n");
            clist.add("");
            clist.add("ha_mode=yes");
            clist.add("");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            task = new GetCubridConfParameterTask(serverInfo);
            task.getConfParameters().get(ConfConstants.COMMON_SECTION_NAME).get("[@demodb]");
            task.getConfParameters()
                    .get(ConfConstants.COMMON_SECTION_NAME)
                    .put(ConfConstants.HA_MODE, "ON");
            List<String> clist = new ArrayList<String>();
            clist.add("# The createdb creates a log volume file of 'log_volume_size' size\n");
            clist.add("# if don't have any options about size.\n");
            clist.add("");
            clist.add("ha_mode=replica");
            clist.add("");
            lst = CoreUtils.changeHAModeFromCubridConf(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testAddDatabaseToServiceServer() {
        List<String> lst = null;
        try {
            lst = CoreUtils.addDatabaseToServiceServer(null, null, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            lst = CoreUtils.addDatabaseToServiceServer(task, null, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("service=server,broker,manager\n");
            clist.add(ConfConstants.SERVICE_SECTION);
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,,manager\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=,\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=,bar\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,testdb,quick\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,testdb,\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,,quick\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=,testdb,quick\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,testdb,quick\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("# server=,,\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("# server=\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        } catch (Exception e) {
            assertTrue(true);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("# server=\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("# server=\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("# server=\n");
            lst = CoreUtils.addDatabaseToServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        }
    }

    public void testDeleteDatabaseFromServiceServer() {
        List<String> lst = null;
        try {
            lst = CoreUtils.deleteDatabaseFromServiceServer(null, null, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,testdb,quick\n");
            lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        }

        try {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("#server=demodb,testdb,\n");
            lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
            assertTrue(lst == null);
        } catch (Exception e) {
            assertTrue(true);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,,quick\n");
            lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("# server=,testdb,quick\n");
            lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
            assertNull(lst);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=demodb,testdb,quick\n");
            lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
            assertNotNull(lst);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=,,\n");
            lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
            assertNull(lst);
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=\n");
            try {
                lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=\n");
            try {
                lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=\n");
            try {
                lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }

        {
            GetCubridConfParameterTask task = new GetCubridConfParameterTask(null);
            List<String> clist = new ArrayList<String>();
            clist.add(ConfConstants.SERVICE_SECTION);
            clist.add("[service]\n");
            clist.add(
                    "# The list of processes to be started automatically by 'cubrid service start' command\n");
            clist.add(
                    "# Any combinations are available with server, broker, manager and heartbeat.\n");
            clist.add("service=server,broker,manager\n");
            clist.add("# The list of database servers in all by 'cubrid service start' command.\n");
            clist.add(
                    "# This property is effective only when the above 'service' property contains 'server' keyword.\n");
            clist.add("server=\n");
            try {
                lst = CoreUtils.deleteDatabaseFromServiceServer(task, clist, "demodb");
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }
    }
}
