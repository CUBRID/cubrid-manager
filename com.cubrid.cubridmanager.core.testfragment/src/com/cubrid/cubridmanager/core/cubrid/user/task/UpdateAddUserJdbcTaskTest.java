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
package com.cubrid.cubridmanager.core.cubrid.user.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fulei
 * @version 1.0 - 2012-11-30 created by fulei
 */
public class UpdateAddUserJdbcTaskTest extends SetupJDBCTestCase {

    public void testUpdateUserAuthorizations() {
        Map<String, ClassAuthorizations> authorizationsMap =
                new HashMap<String, ClassAuthorizations>();
        authorizationsMap.put("test_a1", new ClassAuthorizations());
        Map<String, Object> authMap = new HashMap<String, Object>();
        authMap.put("0", "test_a1");
        authMap.put("1", true);
        authMap.put("8", false);
        authMap.put("2", true);
        authMap.put("9", false);
        authMap.put("3", true);
        authMap.put("10", false);
        authMap.put("4", true);
        authMap.put("11", false);
        authMap.put("5", true);
        authMap.put("12", false);
        authMap.put("6", true);
        authMap.put("13", false);
        authMap.put("7", true);
        authMap.put("14", false);
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(authMap);
        UpdateAddUserJdbcTask task =
                new UpdateAddUserJdbcTask(
                        databaseInfo, "testuser", authorizationsMap, list, false, false);
        task.setDba(false);
        task.isDba();
        task.execute();

        authMap = new HashMap<String, Object>();
        authMap.put("0", "test_a1");
        authMap.put("1", false);
        authMap.put("8", true);
        authMap.put("2", false);
        authMap.put("9", true);
        authMap.put("3", false);
        authMap.put("10", true);
        authMap.put("4", false);
        authMap.put("11", true);
        authMap.put("5", false);
        authMap.put("12", true);
        authMap.put("6", false);
        authMap.put("13", true);
        authMap.put("7", false);
        authMap.put("14", true);
        list.clear();
        list.add(authMap);
        task =
                new UpdateAddUserJdbcTask(
                        databaseInfo, "testuser", authorizationsMap, list, false, false);
        task.execute();

        authMap = new HashMap<String, Object>();
        authMap.put("0", "test_a1");
        authMap.put("1", false);
        authMap.put("2", false);
        authMap.put("3", false);
        authMap.put("4", false);
        authMap.put("5", false);
        authMap.put("6", false);
        authMap.put("7", false);
        authMap.put("8", false);
        authMap.put("9", false);
        authMap.put("10", false);
        authMap.put("11", false);
        authMap.put("12", false);
        authMap.put("13", false);
        authMap.put("14", false);

        Map<String, Object> grantMap = new HashMap<String, Object>();
        grantMap.put("0", "test_a1");
        grantMap.put("1", true);
        grantMap.put("2", true);
        grantMap.put("3", true);
        grantMap.put("4", true);
        grantMap.put("5", true);
        grantMap.put("6", true);
        grantMap.put("7", true);
        grantMap.put("8", true);
        grantMap.put("9", true);
        grantMap.put("10", true);
        grantMap.put("11", true);
        grantMap.put("12", true);
        grantMap.put("13", true);
        grantMap.put("14", true);

        list.clear();
        list.add(authMap);

        ArrayList<Map<String, Object>> listOld = new ArrayList<Map<String, Object>>();
        listOld.add(grantMap);
        task =
                new UpdateAddUserJdbcTask(
                        databaseInfo, "testuser", authorizationsMap, list, listOld, false, false);
        task.execute();
    }
}
