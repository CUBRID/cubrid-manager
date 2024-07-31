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
package com.cubrid.cubridmanager.core.cubrid.user.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserDetailInfo Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-2 created by Kevin.Wang
 */
public class UserDetailInfo {
    private String userName;
    private Map<String, DBAuth> classAuthMap = new HashMap<String, DBAuth>();

    /**
     * Get the user name
     *
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /** @param userName the userName to set */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addAuth(String className, AuthType authType) {
        DBAuth dbAuth = classAuthMap.get(className);
        if (dbAuth == null) {
            dbAuth = new DBAuth(className, authType);
            classAuthMap.put(className, dbAuth);
        } else {
            dbAuth.setAuthType(AuthType.mergeAuth(dbAuth.getAuthType(), authType));
        }
    }

    public void addAuth(DBAuth dbAuth) {
        if (dbAuth == null) {
            return;
        }
        classAuthMap.put(dbAuth.getClassName(), dbAuth);
    }

    public DBAuth getAuth(String className) {
        return classAuthMap.get(className);
    }

    public List<DBAuth> getAllDBAuth() {
        List<DBAuth> list = new ArrayList<DBAuth>();
        list.addAll(classAuthMap.values());
        return list;
    }
}
