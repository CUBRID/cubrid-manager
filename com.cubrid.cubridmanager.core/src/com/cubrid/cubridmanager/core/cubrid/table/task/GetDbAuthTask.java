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

import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Get database authority in the view db_auth
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-09-13 created by lizhiqiang
 */
public class GetDbAuthTask extends JDBCTask {
    public GetDbAuthTask(DatabaseInfo dbInfo) {
        super("GetDbAuthTask", dbInfo);
    }

    /**
     * Get the tables
     *
     * @param className String
     * @return List<String>
     */
    public List<DbAuth> getDbAuths(String className) {
        List<DbAuth> dbAuthList = new ArrayList<DbAuth>();
        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return dbAuthList;
            }
            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return dbAuthList;
            }

            String sql = "SELECT * FROM db_auth WHERE class_name=\'" + className + "\'";

            // [TOOLS-2425]Support shard broker
            sql = databaseInfo.wrapShardQuery(sql);

            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DbAuth dbAuth = new DbAuth();
                dbAuth.setGrantorName(rs.getString("grantor_name"));
                dbAuth.setGranteeName(rs.getString("grantee_name"));
                dbAuth.setClassName(rs.getString("class_name"));
                dbAuth.setAuthType(rs.getString("auth_type"));
                dbAuth.setGrantable(rs.getString("is_grantable"));
                dbAuthList.add(dbAuth);
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            finish();
        }
        return dbAuthList;
    }
}
