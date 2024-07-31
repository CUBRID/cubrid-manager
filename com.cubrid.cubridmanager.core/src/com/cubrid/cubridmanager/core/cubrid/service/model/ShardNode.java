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

import com.cubrid.cubridmanager.core.shard.model.ShardStatus;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.model.ShardsStatus;
import java.util.HashMap;
import java.util.Map;

public class ShardNode extends NodeInfo { // FIXME description
    private Shards shards;
    private Map<String, String> dbStatusMap;
    private String severStatus;
    private ShardsStatus shardsStatus;

    public ShardNode() {
        super(NodeType.SHARD);
    }

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    public Map<String, String> getDbStatusMap() {
        return dbStatusMap;
    }

    public void setDbStatusMap(Map<String, String> dbStatusMap) {
        this.dbStatusMap = dbStatusMap;
    }

    public void setDbStatus(String dbName, String status) {
        if (dbStatusMap == null) {
            dbStatusMap = new HashMap<String, String>();
        }
        dbStatusMap.put(dbName, status);
    }

    public String getSeverStatus() {
        return severStatus;
    }

    public void setSeverStatus(String severStatus) {
        this.severStatus = severStatus;
    }

    public String getStatus() {
        String result = super.getStatus();
        if (result == null) {
            genStatus();
            result = super.getStatus();
        }
        return result;
    }

    public void genStatus() {
        StringBuilder sb = new StringBuilder(getSeverStatus());
        if (getDatabases() == null || dbStatusMap == null) {
            setStatus(sb.toString());
            return;
        } else if (getDatabases().size() <= 0) {
            setStatus(sb.toString());
            return;
        }
        sb.append("(");
        for (String dbName : getDatabases()) {
            sb.append(dbName).append(":");
            sb.append(dbStatusMap.get(dbName)).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        setStatus(sb.toString());
    }

    public ShardsStatus getShardsStatus() {
        return shardsStatus;
    }

    public void setShardsStatus(ShardsStatus shardsStatus) {
        this.shardsStatus = shardsStatus;
    }

    public String getBrokerInfo() {
        String result = super.getBrokerInfo();
        if (result == null) {
            genBrokerInfo();
            result = super.getBrokerInfo();
        }
        return result;
    }

    public void genBrokerInfo() {
        StringBuilder sb = new StringBuilder();
        if (shardsStatus != null) {
            int count = 0;
            for (ShardStatus shardStatus : shardsStatus.getShardStatuss()) {
                if (shardStatus != null) {
                    // sb.append(brokerInfo.getName()).append("[");
                    sb.append(shardStatus.getPort()).append(":");
                    sb.append(shardStatus.getStatus()).append(", ");
                    count++;
                }
            }
            if (count > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }
        super.setBrokerInfo(sb.toString());
    }

    public String toString() {
        genBrokerInfo();
        return super.toString();
    }
}
