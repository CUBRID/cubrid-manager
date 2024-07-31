/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.AuthType;
import com.cubrid.cubridmanager.core.cubrid.user.model.DBAuth;
import com.cubrid.cubridmanager.core.cubrid.user.model.UserDetailInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Get All User Authorizations Task
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-4 created by Kevin.Wang
 */
public class GetAllUserAuthorizationsTask extends JDBCTask {
    /*Key : userName,Value : UserDetailInfo*/
    private Map<String, UserDetailInfo> allAuthMap = new HashMap<String, UserDetailInfo>();

    /**
     * The constructor
     *
     * @param dbInfo
     */
    public GetAllUserAuthorizationsTask(DatabaseInfo dbInfo) {
        super("GetAllUserAuthorizationsTask", dbInfo);
    }

    public GetAllUserAuthorizationsTask(DatabaseInfo dbInfo, Connection con) {
        super("GetAllUserAuthorizationsTask", dbInfo, con);
    }

    public void execute() {
        String sql =
                "SELECT grantor_name, grantee_name, class_name, auth_type, is_grantable FROM db_auth";
        sql = databaseInfo.wrapShardQuery(sql);

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                // String grantorName = rs.getString("grantor_name");
                String granteeName = rs.getString("grantee_name");
                String className = rs.getString("class_name");
                String authType = rs.getString("auth_type");
                boolean isGrantable = StringUtil.booleanValueWithYN(rs.getString("is_grantable"));

                //				if (grantorName != null) {
                //					grantorName = grantorName.toLowerCase(Locale.getDefault());
                //				}
                if (StringUtil.isEmpty(className)) {
                    continue;
                }
                if (granteeName != null) {
                    granteeName = granteeName.toLowerCase(Locale.getDefault());
                }
                UserDetailInfo userInfo = allAuthMap.get(granteeName);
                if (userInfo == null) {
                    userInfo = new UserDetailInfo();
                    userInfo.setUserName(granteeName);
                    allAuthMap.put(granteeName, userInfo);
                }

                AuthType type = AuthType.getAuthType(authType, isGrantable);
                userInfo.addAuth(className, type);
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            QueryUtil.freeQuery(stmt, rs);
        }

        loadDetailInfo();

        finish();
    }

    private void loadDetailInfo() {
        String sql =
                "SELECT class_name, owner_name, class_type "
                        + "FROM db_class "
                        + "WHERE is_system_class = 'NO' AND partitioned = 'NO' ";
        sql = databaseInfo.wrapShardQuery(sql);
        Statement stmt = null;

        /*Add dba user first*/
        UserDetailInfo dbaUserInfo = allAuthMap.get("dba");
        if (dbaUserInfo == null) {
            dbaUserInfo = new UserDetailInfo();
            dbaUserInfo.setUserName("dba");
        }

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String className = rs.getString("class_name");
                String ownerName = rs.getString("owner_name");
                String classType = rs.getString("class_type");
                boolean isTable = "CLASS".equals(classType);

                if (ConstantsUtil.isExtensionalSystemTable(className)) {
                    continue;
                }
                if (ownerName != null) {
                    ownerName = ownerName.toLowerCase(Locale.getDefault());
                }

                UserDetailInfo userDetailInfo = allAuthMap.get(ownerName);
                if (!allAuthMap.containsKey(ownerName)) {
                    userDetailInfo = new UserDetailInfo();
                    userDetailInfo.setUserName(ownerName);
                    allAuthMap.put(ownerName, userDetailInfo);
                }

                /*Add auth to dba*/
                DBAuth dbaAuth = dbaUserInfo.getAuth(className);
                if (dbaAuth == null) {
                    dbaAuth = new DBAuth(className, AuthType.ALL);
                    dbaAuth.setOwner(ownerName);
                    dbaAuth.setTable(isTable);
                    dbaUserInfo.addAuth(dbaAuth);
                } else {
                    dbaAuth.setOwner(ownerName);
                    dbaAuth.setTable(isTable);
                    dbaUserInfo.addAuth(className, AuthType.ALL);
                }

                /*add auth to public user*/
                for (UserDetailInfo userInfo : allAuthMap.values()) {
                    DBAuth dbAuth = userInfo.getAuth(className);
                    if (dbAuth == null) {
                        if (StringUtil.isEqual(ownerName, userInfo.getUserName())) {
                            dbAuth = new DBAuth(className, AuthType.ALL);
                            dbAuth.setOwner(ownerName);
                            dbAuth.setTable(isTable);

                            userInfo.addAuth(dbAuth);
                        } else {
                            continue;
                        }
                    } else {
                        dbAuth.setOwner(ownerName);
                        dbAuth.setTable(isTable);
                    }
                }
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            QueryUtil.freeQuery(stmt, rs);
        }

        allAuthMap.put(dbaUserInfo.getUserName(), dbaUserInfo);
    }

    /** @return the allAuthMap */
    public Map<String, UserDetailInfo> getAllAuthMap() {
        return allAuthMap;
    }
}
