/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.cubridmanager.core.common.model.CubridShardConfParaConstants;

/**
 * Shard broker entity
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-11-30
 */
public class Shard extends CubridConfProperties {

    {
        // initialize
        String[][] shardBrokerParameters = CubridShardConfParaConstants.getShardParameters();
        for (String[] paras : shardBrokerParameters) {
            if (paras[3].equals(CubridShardConfParaConstants.PARAMETER_TYPE_BROKER_COMMON)) {
                this.setValue(paras[0], paras[2]);
            }
        }
    }

    private String name;

    private ShardConnection shardConnectionFile;

    private ShardKeys shardKeysFile;

    private transient boolean running;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShardConnection getShardConnectionFile() {
        return shardConnectionFile;
    }

    public void setShardConnectionFile(ShardConnection shardConnectionFile) {
        this.shardConnectionFile = shardConnectionFile;
        this.shardConnectionFile.setFieName(this.getName() + "_connection.txt");
        this.shardConnectionFile.setShard(this);
    }

    public ShardKeys getShardKeysFile() {
        return shardKeysFile;
    }

    public void setShardKeysFile(ShardKeys shardKeysFile) {
        this.shardKeysFile = shardKeysFile;
        this.shardKeysFile.setFieName(this.getName() + "_key.txt");
        this.shardKeysFile.setShard(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[%").append(name).append("]");
        sb.append("\r\n");
        sb.append(this.getLine(CubridShardConfParaConstants.SERVICE));
        sb.append(this.getLine(CubridShardConfParaConstants.BROKER_PORT));
        sb.append(this.getLine(CubridShardConfParaConstants.SHARD_DB_NAME));
        sb.append(this.getLine(CubridShardConfParaConstants.SHARD_DB_USER));
        sb.append(this.getLine(CubridShardConfParaConstants.SHARD_DB_PASSWORD));
        sb.append(this.getLine(CubridShardConfParaConstants.SHARD_CONNECTION_FILE));
        sb.append(this.getLine(CubridShardConfParaConstants.SHARD_KEY_FILE));
        sb.append(this.getLine(CubridShardConfParaConstants.APPL_SERVER_SHM_ID));
        sb.append(this.getLine(CubridShardConfParaConstants.METADATA_SHM_ID));
        sb.append("\r\n");
        sb.append(this.getLine(CubridShardConfParaConstants.MIN_NUM_APPL_SERVER));
        sb.append(this.getLine(CubridShardConfParaConstants.MAX_NUM_APPL_SERVER));
        sb.append(this.getLine(CubridShardConfParaConstants.LOG_DIR));
        sb.append(this.getLine(CubridShardConfParaConstants.ERROR_LOG_DIR));
        sb.append(this.getLine(CubridShardConfParaConstants.SQL_LOG));
        sb.append(this.getLine(CubridShardConfParaConstants.TIME_TO_KILL));
        sb.append(this.getLine(CubridShardConfParaConstants.SESSION_TIMEOUT));
        sb.append(this.getLine(CubridShardConfParaConstants.KEEP_CONNECTION));
        sb.append(this.getLine(CubridShardConfParaConstants.MAX_PREPARED_STMT_COUNT));
        sb.append(this.getLine(CubridShardConfParaConstants.NUM_PROXY_MIN));
        sb.append(this.getLine(CubridShardConfParaConstants.NUM_PROXY_MAX));
        sb.append(this.getLine(CubridShardConfParaConstants.PROXY_LOG_FILE));
        sb.append(this.getLine(CubridShardConfParaConstants.PROXY_LOG));
        sb.append(this.getLine(CubridShardConfParaConstants.MAX_CLIENT));

        return sb.toString();
    }
}
