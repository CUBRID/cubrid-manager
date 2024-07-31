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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Task of get charset of database.
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-5-4 created by Santiago Wang
 */
public class GetDbCollationTask extends JDBCTask {
    private String collation;
    private Map<Integer, String> collMap = new HashMap<Integer, String>();

    /**
     * The constructor
     *
     * @param dbInfo
     */
    public GetDbCollationTask(DatabaseInfo dbInfo) {
        super("GetDbCollation", dbInfo);
        init();
    }

    // init collation map.
    private void init() {
        final String collIsoBin = "iso88591_bin";
        final String collUtfBin = "utf8_bin";
        final String collEuckrBin = "euckr_bin";
        collMap.put(Integer.valueOf(3), collIsoBin);
        collMap.put(Integer.valueOf(4), collEuckrBin);
        collMap.put(Integer.valueOf(5), collUtfBin);
    }

    public void execute() {
        String sql = "SELECT lang,charset FROM db_root";
        if (databaseInfo != null) {
            sql = databaseInfo.wrapShardQuery(sql);
        }

        try {
            if (StringUtil.isNotEmpty(errorMsg)) {
                return;
            }

            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return;
            }

            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int charsetId = rs.getInt("charset");
                setCollation(findCollation(charsetId));
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            finish();
        }
    }

    /**
     * Find collation according to charset ID.
     *
     * @param charsetId
     * @return
     */
    protected String findCollation(Integer charsetId) {
        String collation = collMap.get(charsetId);
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public String getCollation() {
        return collation;
    }
}
