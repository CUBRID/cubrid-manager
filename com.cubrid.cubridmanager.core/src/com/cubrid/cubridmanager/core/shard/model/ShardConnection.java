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
package com.cubrid.cubridmanager.core.shard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Shard connection entity.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-4
 */
public class ShardConnection implements IShardModel {
    private Shard shard;
    private String fieName;
    private List<String> connections = new ArrayList<String>();

    public String getFieName() {
        return fieName == null ? "" : fieName;
    }

    public void setFieName(String fieName) {
        this.fieName = fieName;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public void addConnection(String connection) {
        this.connections.add(connection);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# shard-id  real-db-name  connection-info");
        sb.append("\r\n");

        for (String con : connections) {
            sb.append(con.replaceAll(",", "        "));
            sb.append("\r\n");
        }

        return sb.toString();
    }

    /**
     * Parse response data to ShardConnection
     *
     * @param confData
     */
    public void parse(String[] confData) {
        for (int j = 0; j < confData.length; j++) {
            String data = confData[j].trim();
            if (data.length() == 0 || data.indexOf("#") == 0) {
                continue;
            }
            this.addConnection(data.replaceAll(" +", ","));
        }
    }

    public String getFileName() {
        return this.shard.getName() + "_connection.txt";
    }

    public void setShard(Shard shard) {
        this.shard = shard;
    }
}
