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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;
import org.slf4j.Logger;

/**
 * Check the subClass task
 *
 * @author Kevin.Wang
 */
public class CheckSubClassTask extends JDBCTask {
    private static final Logger LOGGER = LogUtil.getLogger(CheckSubClassTask.class);

    public CheckSubClassTask(DatabaseInfo dbInfo) {
        super("CheckSubClass", dbInfo);
    }

    /**
     * Check the class is has subClass
     *
     * @param className
     * @return
     */
    public boolean checkSubClass(String owner, String className) {
        boolean isHasSubClass = false;

        String sql = "";
        if (databaseInfo.isSupportUserSchema()) {
            if (owner.isEmpty() || className.isEmpty()) {
                errorMsg = Messages.msg_error;
                return isHasSubClass;
            }
            sql =
                    "SELECT c.sub_classes FROM _db_class as c, db_user as u "
                            + "WHERE u.name='"
                            + owner
                            + "' AND c.class_name='"
                            + className
                            + "' group by u.name";
        } else {
            sql = "SELECT sub_classes FROM _db_class WHERE class_name='" + className + "'";
        }

        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return isHasSubClass;
            }

            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return isHasSubClass;
            }

            sql = databaseInfo.wrapShardQuery(sql);
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Object obj = rs.getObject("sub_classes");
                if (obj != null) {
                    isHasSubClass = true;
                    break;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            errorMsg = e.getMessage();
        } finally {
            finish();
        }

        return isHasSubClass;
    }
}
