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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * Shard conf entity
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-11-30
 */
public class Shards extends CubridConfProperties implements IShardModel {

    {
        // initialize
        String[][] shardBrokerParameters = CubridShardConfParaConstants.getShardParameters();
        for (String[] paras : shardBrokerParameters) {
            if (paras[3].equals(CubridShardConfParaConstants.PARAMETER_TYPE_BROKER_GENERAL)) {
                this.setValue(paras[0], paras[2]);
            }
        }
    }

    private static final String copyright;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append("\r\n");
        sb.append(
                        "# Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. ")
                .append("\r\n");
        sb.append("#").append("\r\n");
        sb.append("#   This program is free software; you can redistribute it and/or modify ")
                .append("\r\n");
        sb.append("#   it under the terms of the GNU General Public License as published by ")
                .append("\r\n");
        sb.append("#   the Free Software Foundation; version 2 of the License. ").append("\r\n");
        sb.append("#").append("\r\n");
        sb.append("#  This program is distributed in the hope that it will be useful, ")
                .append("\r\n");
        sb.append("#  but WITHOUT ANY WARRANTY; without even the implied warranty of ")
                .append("\r\n");
        sb.append("#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ")
                .append("\r\n");
        sb.append("#  GNU General Public License for more details. ").append("\r\n");
        sb.append("#").append("\r\n");
        sb.append("#  You should have received a copy of the GNU General Public License ")
                .append("\r\n");
        sb.append("#  along with this program; if not, write to the Free Software ").append("\r\n");
        sb.append("#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA ")
                .append("\r\n");
        sb.append("#").append("\r\n");
        sb.append("\r\n");
        copyright = sb.toString();
    }

    private List<Shard> shardList = new ArrayList<Shard>();

    private transient boolean running;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<Shard> getShardList() {
        return shardList;
    }

    public void addShard(Shard shard) {
        this.shardList.add(shard);
    }

    public void removeShard(Shard shard) {
        this.shardList.remove(shard);
    }

    public void removeShard(String shardName) {
        for (Shard shard : this.shardList) {
            if (shard.getName().equals(shardName)) {
                this.shardList.remove(shard);
                break;
            }
        }
    }

    public Shard getShard(String shardName) {
        for (Shard shard : this.shardList) {
            if (shard.getName().equals(shardName)) {
                // this.shardList.remove(shard);
                return shard;
            }
        }
        return null;
    }

    public String toGeneralString() {
        StringBuilder sb = new StringBuilder();
        sb.append(copyright);
        sb.append("[shard]");
        sb.append("\r\n");
        sb.append(this.getLine(CubridShardConfParaConstants.MASTER_SHM_ID));
        sb.append(this.getLine(CubridShardConfParaConstants.ADMIN_LOG_FILE));
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(copyright);
        sb.append("[shard]");
        sb.append("\r\n");
        sb.append(this.getLine(CubridShardConfParaConstants.MASTER_SHM_ID));
        sb.append(this.getLine(CubridShardConfParaConstants.ADMIN_LOG_FILE));
        sb.append("\r\n");
        for (Shard shard : shardList) {
            sb.append(shard.toString());
            sb.append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Parse response data to Shards
     *
     * @param confData
     */
    public void parse(String[] confData) {
        Shard shard = null;
        boolean tag = true;
        for (int j = 0; j < confData.length; j++) {
            String data = confData[j].trim();
            if (data.length() == 0 || data.indexOf("#") == 0) {
                continue;
            }
            if (data.equals(CubridShardConfParaConstants.SHARD_SECTION)) {
                tag = true;
                continue;
            } else if (data.matches("^\\[%.+\\]$")) {
                String secionName = data.replaceAll("(^\\[%)|(\\]$)", "");
                secionName = secionName.toLowerCase(Locale.getDefault());
                shard = new Shard();
                shard.setName(secionName);
                this.addShard(shard);
                tag = false;
                continue;
            }

            String[] entry = data.split("=");
            if (entry != null && entry.length == 2) {
                if (tag) {
                    this.setValue(entry[0].trim(), entry[1].trim());
                } else if (shard != null) {
                    shard.setValue(entry[0].trim(), entry[1].trim());
                }
            }
        }
    }

    public String getFileName() {
        return "shard.conf";
    }

    /**
     * check for port conflicts
     *
     * @param conrPro current CubridConfProperties entity
     * @param name value's name
     * @param value value
     * @return
     */
    // TODO Updating broker need to call this method
    public boolean checkPortConflicts(CubridConfProperties conrPro, String value) {
        return checkConflicts(conrPro, value, "_PORT");
    }

    /**
     * check for shm id conflicts
     *
     * @param conrPro current CubridConfProperties entity
     * @param name value's name
     * @param value value
     * @return
     */
    // TODO Updating broker need to call this method
    public boolean checkShmIdConflicts(CubridConfProperties conrPro, String value) {
        return checkConflicts(conrPro, value, "_SHM_ID");
    }

    private boolean checkConflicts(CubridConfProperties conrPro, String value, String key) {
        List<CubridConfProperties> confList = new ArrayList<CubridConfProperties>();
        if (conrPro == null) {
            confList.add(this);
            confList.addAll(shardList);
        } else if (conrPro instanceof Shards) {
            confList.addAll(shardList);
        } else if (conrPro instanceof Shard) {
            confList.add(this);
            confList.addAll(shardList);
            confList.remove(conrPro);
        }
        for (CubridConfProperties conf : confList) {
            for (Entry<String, String> entity : conf.getProperties().entrySet()) {
                String keyOther = entity.getKey();
                String valueOther = entity.getValue();
                if (keyOther.contains(key) && valueOther.equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check for shard name conflicts
     *
     * @param shard
     * @param shardName
     * @return
     */
    public boolean checkShardNameConflicts(Shard shard, String shardName) {
        List<Shard> shards = new ArrayList<Shard>();
        shards.addAll(shardList);
        shards.remove(shard);
        for (Shard shard0 : shards) {
            if (shard0.getName().equalsIgnoreCase(shardName)) {
                return true;
            }
        }
        return false;
    }
}
