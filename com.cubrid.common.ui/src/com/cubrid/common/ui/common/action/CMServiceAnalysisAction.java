/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.action;

import com.cubrid.cubridmanager.core.cubrid.service.model.NodeInfo;
import com.cubrid.cubridmanager.core.cubrid.service.task.HaShardDemo;
import com.cubrid.cubridmanager.core.cubrid.service.task.HaShardManager;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Launch CUBRID Migration Toolkit
 *
 * @author PCraft
 * @version 1.0 - 2013-01-22 created by PCraft
 * @deprecated
 */
public class CMServiceAnalysisAction extends Action {
    public static final String ID = CMServiceAnalysisAction.class.getName();

    public CMServiceAnalysisAction(String text, ImageDescriptor image) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
    }

    public void run() {
        HaShardManager haShardManager = HaShardManager.getInstance();

        /*HaShardDemo.registerServiceAndConnect("192.168.1.60", 8001, "admin",
        "123456", "HA Master");*/
        // HA node list
        HaShardDemo demo = new HaShardDemo();
        demo.registerServiceAndBuildInfo(
                "192.168.1.119",
                8001,
                "admin",
                "1111",
                "www.cubrid.org",
                "Broker Server 1",
                "dev-cub-ha-005.ncl");
        //		demo.registerServiceAndBuildInfo("192.168.1.250", 8001, "admin",
        //				"1111", "www.cubrid.org", "HA Master", "dev-cub-ha-001.ncl");
        demo.registerServiceAndBuildInfo(
                "192.168.1.247",
                8001,
                "admin",
                "1111",
                "www.cubrid.org",
                "HA Slave",
                "dev-cub-ha-002.ncl");
        demo.registerServiceAndBuildInfo(
                "192.168.1.82",
                8001,
                "admin",
                "1111",
                "www.cubrid.org",
                "HA Replica 1",
                "dev-cub-ha-003.ncl");
        demo.registerServiceAndBuildInfo(
                "192.168.1.247",
                8001,
                "admin",
                "1111",
                "www.cubrid.org",
                "HA Replica 2",
                "dev-cub-ha-004.ncl");

        // Shard node list
        demo.registerServiceAndBuildInfo(
                "192.168.1.226",
                8001,
                "admin",
                "1111",
                "drive.cubrid.org",
                "Shard 1",
                "dev-cub-shard-001.ncl");
        demo.registerServiceAndBuildInfo(
                "192.168.1.223",
                8001,
                "admin",
                "1111",
                "drive.cubrid.org",
                "Shard 2",
                "dev-cub-shard-002.ncl");
        demo.registerServiceAndBuildInfo(
                "192.168.1.22",
                8001,
                "admin",
                "1111",
                "drive.cubrid.org",
                "Shard 3",
                "dev-cub-shard-003.ncl");

        System.out.println(
                "-------------------------------------------------------------------------");
        System.out.println(
                "Service Name  - Database(s) -    Node Name    -     "
                        + "IP     -   Broker Status   - Connect Name - Node Status");
        System.out.println(
                "-------------------------------------------------------------------------");

        haShardManager.sort();
        haShardManager.registerBrokerHostInHaShard();

        List<NodeInfo> haNodes = haShardManager.getHaNodes();
        System.out.println("[HA Services]");
        for (NodeInfo node : haNodes) {
            System.out.println(node);
        }

        List<NodeInfo> shardNodes = haShardManager.getShardNodes();
        System.out.println("[Shard Services]");
        for (NodeInfo node : shardNodes) {
            System.out.println(node);
        }

        System.out.println(
                "-------------------------------------------------------------------------");
        System.out.println(
                "Broker  -  Service Name  -  "
                        + "Broker Status  -  Connect Name  -   DB(Accessed)  -  Host(Accessed)");
        System.out.println(
                "-------------------------------------------------------------------------");
        List<NodeInfo> brokerNodes = haShardManager.getBrokerNodes();
        System.out.println("[Brokers]");
        for (NodeInfo node : brokerNodes) {
            System.out.println(node);
        }
    }
}
