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
package com.cubrid.cubridmanager.core.cubrid.service.model;

import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public class BrokerNodeTest extends TestCase {

    public void testBrokerNode() {
        BrokerNode brokerNode = new BrokerNode();
        BrokerInfoList brokerInfoList = new BrokerInfoList();
        brokerNode.setBrokerInfoList(brokerInfoList);
        Map<String, BrokerStatusInfos> brokerStatusMap = new HashMap<String, BrokerStatusInfos>();
        brokerNode.setBrokerStatusMap(brokerStatusMap);

        assertEquals(brokerNode.getBrokerInfoList(), brokerInfoList);
        assertEquals(brokerNode.getBrokerStatusMap(), brokerStatusMap);

        brokerNode.setBrokerStatusMap(null);
        assertNull(brokerNode.getBrokerStatusMap());

        String port = "30000";
        BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
        brokerNode.addBrokerStatus(port, brokerStatusInfos);
        assertNotNull(brokerNode.getBrokerStatusMap());
        BrokerStatusInfos brokerStatusInfos2 = brokerNode.getBrokerStatusMap().get(port);
        assertEquals(brokerStatusInfos, brokerStatusInfos2);

        brokerNode.setRemoteBrokers(new HashSet<NodeInfo>());
        brokerNode.AddRemoteBroker(new NodeInfo(NodeType.BROKER));
        assertNull(brokerNode.getRemoteBrokers());

        brokerNode.genBrokerInfo();
        assertNotNull(brokerNode.getBrokerInfo());
        assertNotNull(brokerNode.getStatus());

        buildFakeData(brokerNode);

        assertNotNull(brokerNode.getHostSet());
        //		assertNotNull(brokerNode.getDbSet());
        assertNotNull(brokerNode.getAccessedHosts());
        assertNotNull(brokerNode.getAccessedHostDbMap());
        assertNotNull(brokerNode.getAccessedDbInfo());
    }

    public void buildFakeData(BrokerNode brokerNode) {
        DbLocationInfo dbLocalInfo = new DbLocationInfo();
        dbLocalInfo.setDbName("demodb");
        dbLocalInfo.setVolPath("/home1/demo/CUBRID/databases/demodb");
        dbLocalInfo.setLobBasePath("file:/home1/demo/CUBRID/databases/demodb/lob");
        dbLocalInfo.setLogPath("/home1/demo/CUBRID/databases/demodb");
        dbLocalInfo.addDbHost("localhost");
        List<DbLocationInfo> dbLocationInfoList = new ArrayList<DbLocationInfo>();
        dbLocationInfoList.add(dbLocalInfo);
        brokerNode.setDbLocationInfoList(dbLocationInfoList);

        BrokerStatusInfos bStatInfos = new BrokerStatusInfos();
        bStatInfos.setBname("query_editor");
        Map<String, BrokerStatusInfos> brokerStatusMap = new HashMap<String, BrokerStatusInfos>();
        brokerStatusMap.put("query_editor", bStatInfos);
        brokerNode.setBrokerStatusMap(brokerStatusMap);
    }
}
