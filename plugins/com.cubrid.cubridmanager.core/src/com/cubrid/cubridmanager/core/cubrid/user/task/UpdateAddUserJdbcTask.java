/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.cubrid.user.task;

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * update user Authorizations
 *
 * @author fulei
 * @version 1.0 - 2012-09-11 created by fulei
 */
public class UpdateAddUserJdbcTask extends JDBCTask {
    private String userName;
    //	private Map<String, ClassAuthorizations> authorizationsMap;
    private List<Map<String, Object>> authListData;
    private List<Map<String, Object>> authListDataOld;
    private boolean isDba = false;
    private boolean noNeedRevoke = false;

    public UpdateAddUserJdbcTask(
            DatabaseInfo dbInfo,
            String userName,
            Map<String, ClassAuthorizations> authorizationsMap,
            List<Map<String, Object>> authListData,
            List<Map<String, Object>> authListDataOld,
            boolean isDba,
            boolean noNeedRevoke) {
        super("UpdateAddUserJdbcTask", dbInfo);
        this.userName = userName;
        //		this.authorizationsMap = authorizationsMap;
        this.authListData = authListData;
        this.authListDataOld = authListDataOld;
        this.isDba = isDba;
        this.noNeedRevoke = noNeedRevoke;
    }

    public UpdateAddUserJdbcTask(
            DatabaseInfo dbInfo,
            String userName,
            Map<String, ClassAuthorizations> authorizationsMap,
            List<Map<String, Object>> authListData,
            boolean isDba,
            boolean noNeedRevoke) {
        super("UpdateAddUserJdbcTask", dbInfo);
        this.userName = userName;
        //		this.authorizationsMap = authorizationsMap;
        this.authListData = authListData;
        this.isDba = isDba;
        this.noNeedRevoke = noNeedRevoke;
    }

    public void execute() {
        if (isDba) {
            return;
        }

        errorMsg = updateUserAuthorizations();
        finish();
    }

    public String updateUserAuthorizations() {
        List<String> sqlList = new ArrayList<String>();

        // Remove no change permissions
        if (!noNeedRevoke && authListDataOld != null) {
            for (Map<String, Object> revokeMap : authListDataOld) {
                String revokeClassName = (String) revokeMap.get("0");
                for (Map<String, Object> grantMap : authListData) {
                    String grantClassName = (String) grantMap.get("0");
                    if (revokeClassName.equals(grantClassName)) {
                        for (int i = 1; i < 8; i++) {
                            if (revokeMap.get("" + i).equals(grantMap.get("" + i))
                                    && revokeMap
                                            .get("" + (i + 7))
                                            .equals(grantMap.get("" + (i + 7)))) {
                                revokeMap.put("" + i, false);
                                revokeMap.put("" + (i + 7), false);
                                grantMap.put("" + i, false);
                                grantMap.put("" + (i + 7), false);
                            }
                        }
                    }
                }
            }
        }

        // revoke old privilege
        if (!noNeedRevoke && authListDataOld != null) {
            // REVOKE privilege
            for (Map<String, Object> revokeMap : authListDataOld) {
                String className = (String) revokeMap.get("0");
                String escapedTableName = QuerySyntax.escapeKeyword(className);
                String escapedUserName = QuerySyntax.escapeKeyword(userName);

                if ((Boolean) revokeMap.get("1")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE SELECT ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }

                if ((Boolean) revokeMap.get("2")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE INSERT ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }

                if ((Boolean) revokeMap.get("3")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE UPDATE ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }

                if ((Boolean) revokeMap.get("4")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE DELETE ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }

                if ((Boolean) revokeMap.get("5")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE ALTER ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }

                if ((Boolean) revokeMap.get("6")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE INDEX ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }

                if ((Boolean) revokeMap.get("7")) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder
                            .append("REVOKE EXECUTE ON ")
                            .append(escapedTableName)
                            .append(" FROM ")
                            .append(escapedUserName);
                    sqlList.add(sqlBuilder.toString());
                }
            }
        }

        // GRANT privilege
        for (Map<String, Object> revokeMap : authListData) {
            String className = (String) revokeMap.get("0");
            String escapedTableName = QuerySyntax.escapeKeyword(className);
            String escapedUserName = QuerySyntax.escapeKeyword(userName);

            if ((Boolean) revokeMap.get("1") && !(Boolean) revokeMap.get("8")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT SELECT ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("8")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT SELECT ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }

            if ((Boolean) revokeMap.get("2") && !(Boolean) revokeMap.get("9")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT INSERT ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("9")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT INSERT ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }

            if ((Boolean) revokeMap.get("3") && !(Boolean) revokeMap.get("10")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT UPDATE ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("10")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT UPDATE ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }

            if ((Boolean) revokeMap.get("4") && !(Boolean) revokeMap.get("11")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT DELETE ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("11")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT DELETE ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }

            if ((Boolean) revokeMap.get("5") && !(Boolean) revokeMap.get("12")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT ALTER ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("12")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT ALTER ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }

            if ((Boolean) revokeMap.get("6") && !(Boolean) revokeMap.get("13")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT INDEX ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("13")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT INDEX ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }

            if ((Boolean) revokeMap.get("7") && !(Boolean) revokeMap.get("14")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT EXECUTE ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName);
                sqlList.add(sqlBuilder.toString());
            } else if ((Boolean) revokeMap.get("14")) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder
                        .append("GRANT EXECUTE ON ")
                        .append(escapedTableName)
                        .append(" TO ")
                        .append(escapedUserName)
                        .append(" ")
                        .append("WITH GRANT OPTION");
                sqlList.add(sqlBuilder.toString());
            }
        }

        StringBuilder errors = new StringBuilder();
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            errors.append(e.getMessage()).append("\n");
            return errors.toString();
        }

        for (String sql : sqlList) {
            try {
                // [TOOLS-2425]Support shard broker
                sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                errors.append(e.getMessage()).append("\n");
            }
        }

        finish();

        return errors.length() > 0 ? errors.toString() : null;
    }

    public boolean isDba() {
        return isDba;
    }

    public void setDba(boolean isDba) {
        this.isDba = isDba;
    }
}
