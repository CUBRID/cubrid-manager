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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * get auto increment information for schema
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-10 created by moulinwang
 */
public class GetAutoIncrementTask extends JDBCTask {
    private String tableName;
    private ArrayList<SerialInfo> serialInfoList;

    /**
     * The constructor
     *
     * @param dbInfo
     */
    public GetAutoIncrementTask(DatabaseInfo dbInfo) {
        super("GetSerialInfo", dbInfo);
    }

    /** Send request to Server */
    public void execute() {
        serialInfoList = new ArrayList<SerialInfo>();
        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return;
            }

            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return;
            }

            // databaseInfo.getServerInfo().compareVersionKey("8.2.2") >= 0;
            boolean isSupportCache = CompatibleUtil.isSupportCache(databaseInfo);
            String sql = "SELECT owner.name, db_serial.* FROM db_serial WHERE class_name=?";

            // [TOOLS-2425]Support shard broker
            sql = databaseInfo.wrapShardQuery(sql);

            stmt = connection.prepareStatement(sql);
            ((PreparedStatement) stmt).setString(1, tableName);
            rs = ((PreparedStatement) stmt).executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String owner = rs.getString("owner.name");
                String currentVal = rs.getString("current_val");
                String incrementVal = rs.getString("increment_val");
                String maxVal = rs.getString("max_val");
                String minVal = rs.getString("min_val");
                String cyclic = rs.getString("cyclic");
                String startVal = rs.getString("started");
                String className = rs.getString("class_name");
                String attName = rs.getString("att_name");
                String cacheCount = null;
                if (isSupportCache) {
                    cacheCount = rs.getString("cached_num");
                }
                boolean isCycle = "1".equals(cyclic) ? true : false;
                SerialInfo serialInfo =
                        new SerialInfo(
                                name,
                                owner,
                                currentVal,
                                incrementVal,
                                maxVal,
                                minVal,
                                isCycle,
                                startVal,
                                cacheCount,
                                className,
                                attName);
                serialInfoList.add(serialInfo);
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            finish();
        }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName.toLowerCase(Locale.getDefault());
    }

    public ArrayList<SerialInfo> getSerialInfoList() {
        return serialInfoList;
    }
}
