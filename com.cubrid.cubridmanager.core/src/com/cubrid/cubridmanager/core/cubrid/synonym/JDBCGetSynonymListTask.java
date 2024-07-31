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

package com.cubrid.cubridmanager.core.cubrid.synonym;

import com.cubrid.common.core.common.model.Synonym;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class JDBCGetSynonymListTask extends JDBCTask {
    private static final Logger LOGGER = LogUtil.getLogger(JDBCGetSynonymListTask.class);
    private final List<Synonym> synonyms = new ArrayList<Synonym>();

    public JDBCGetSynonymListTask(DatabaseInfo dbInfo) {
        super("GetSynonymList", dbInfo);
    }

    /** Execute select sql. */
    public void execute() {
        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return;
            }
            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return;
            }
            stmt = connection.createStatement();
            String sql = "select * from db_synonym";

            // [TOOLS-2425]Support shard broker
            sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Synonym synonym = new Synonym();
                synonym.setName(rs.getString("synonym_name"));
                synonym.setOwner(rs.getString("synonym_owner_name"));
                synonym.setTargetName(rs.getString("target_name"));
                synonym.setTargetOwner(rs.getString("target_owner_name"));
                synonym.setComment(rs.getString("comment"));
                synonyms.add(synonym);
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
            if (errorMsg.indexOf("Select is not authorized on db_trigger") >= 0) {
                errorMsg = "";
            }

            LOGGER.error(e.getMessage(), e);
        } finally {
            finish();
        }
    }

    /**
     * Return whether adding synonym task is executed well
     *
     * @return List<Synonym>
     */
    public List<Synonym> getSynonymInfoList() {
        return synonyms;
    }
}
