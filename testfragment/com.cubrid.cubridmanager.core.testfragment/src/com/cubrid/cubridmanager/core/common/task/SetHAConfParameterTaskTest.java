package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetHAConfParameterTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/sethaconfpara_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        SetHAConfParameterTask task = new SetHAConfParameterTask(serverInfo);
        Map<String, Map<String, String>> map = serverInfo.getHaConfParaMap();
        if (map != null) {
            task.setConfParameters(map);
            // compare
            assertEquals(msg, task.getRequest());
        }
    }

    public void testReceive() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/sethaconfpara_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);

        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void testSetContent() throws Exception {
        SetHAConfParameterTask task = new SetHAConfParameterTask(serverInfo);
        List<String> list = new ArrayList<String>();
        list.add("abc");
        list.add("def");
        task.setConfContents(list);
    }

    public void testSetConfParameters() {
        SetHAConfParameterTask task = new SetHAConfParameterTask(serverInfo);
        Map<String, Map<String, String>> confParameters =
                new HashMap<String, Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("ha_mode", "yes");
        map.put("ha_port_id", "yangming@tooldev01:dbms3");
        map.put("ha_replica_list", "yangming@dbms1");
        map.put("ha_db_list", "db_ha");
        map.put("ha_apply_max_mem_size", "300");
        map.put("ha_copy_sync_mode", "sync:sync");
        confParameters.put(ConfConstants.COMMON_SECTION, map);
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("ha_copy_sync_mode", "yes");
        confParameters.put("sdfd", map2);
        confParameters.put("df", null);
        confParameters.put("sd", new HashMap<String, String>());
        task.setConfParameters(confParameters);
    }
}
