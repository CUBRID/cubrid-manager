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
package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbUnloadInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * This task is responsible to get database unload information list
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class GetDbUnloadInfoTask extends SocketTask {

    private static final String[] SEMD_MSG_ITEMS = new String[] {"task", "token"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetDbUnloadInfoTask(ServerInfo serverInfo) {
        super("unloadinfo", serverInfo, SEMD_MSG_ITEMS);
    }

    /**
     * Get database unload information list
     *
     * @return List<DbUnloadInfo>
     */
    public List<DbUnloadInfo> getDbUnloadInfoList() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        List<DbUnloadInfo> dbUnloadInfoList = new ArrayList<DbUnloadInfo>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode treeNode = response.getChildren().get(i);
            if (treeNode != null
                    && treeNode.getValue("open") != null
                    && treeNode.getValue("open").equals("database")) {
                DbUnloadInfo dbUnloadInfo = new DbUnloadInfo();
                String dbName = treeNode.getValue("dbname");
                dbUnloadInfo.setDbName(dbName);
                String[] schemaInfoArr = treeNode.getValues("schema");
                addSchemaDate(dbUnloadInfo, schemaInfoArr);
                String[] objectInfoArr = treeNode.getValues("object");
                addObjectDate(dbUnloadInfo, objectInfoArr);
                String[] indexInfoArr = treeNode.getValues("index");
                addIndexDate(dbUnloadInfo, indexInfoArr);
                String[] triggerInfoArr = treeNode.getValues("trigger");
                addTriggerDate(dbUnloadInfo, triggerInfoArr);
                dbUnloadInfoList.add(dbUnloadInfo);
            }
        }
        return dbUnloadInfoList;
    }

    /**
     * add triggerInfoArr
     *
     * @param dbUnloadInfo DbUnloadInfo
     * @param triggerInfoArr String[]
     */
    private void addTriggerDate(DbUnloadInfo dbUnloadInfo, String[] triggerInfoArr) {
        if (triggerInfoArr != null) {
            for (int j = 0; j < triggerInfoArr.length; j++) {
                String triggerInfo = triggerInfoArr[j];
                String[] arr = triggerInfo.split(";");
                if (arr != null && arr.length == 2) {
                    dbUnloadInfo.getTriggerPathList().add(arr[0]);
                    dbUnloadInfo.getTriggerDateList().add(arr[1]);
                }
            }
        }
    }

    /**
     * add indexInfoArr
     *
     * @param dbUnloadInfo DbUnloadInfo
     * @param indexInfoArr String[]
     */
    private void addIndexDate(DbUnloadInfo dbUnloadInfo, String[] indexInfoArr) {
        if (indexInfoArr != null) {
            for (int j = 0; j < indexInfoArr.length; j++) {
                String indexInfo = indexInfoArr[j];
                String[] arr = indexInfo.split(";");
                if (arr != null && arr.length == 2) {
                    dbUnloadInfo.getIndexPathList().add(arr[0]);
                    dbUnloadInfo.getIndexDateList().add(arr[1]);
                }
            }
        }
    }

    /**
     * add objectInfoArr
     *
     * @param dbUnloadInfo DbUnloadInfo
     * @param objectInfoArr String[]
     */
    private void addObjectDate(DbUnloadInfo dbUnloadInfo, String[] objectInfoArr) {
        if (objectInfoArr != null) {
            for (int j = 0; j < objectInfoArr.length; j++) {
                String objectInfo = objectInfoArr[j];
                String[] arr = objectInfo.split(";");
                if (arr != null && arr.length == 2) {
                    dbUnloadInfo.getObjectPathList().add(arr[0]);
                    dbUnloadInfo.getObjectDateList().add(arr[1]);
                }
            }
        }
    }

    /**
     * add schemaInfoArr
     *
     * @param dbUnloadInfo DbUnloadInfo
     * @param schemaInfoArr String[]
     */
    private void addSchemaDate(DbUnloadInfo dbUnloadInfo, String[] schemaInfoArr) {
        if (schemaInfoArr != null) {
            for (int j = 0; j < schemaInfoArr.length; j++) {
                String schemaInfo = schemaInfoArr[j];
                String[] arr = schemaInfo.split(";");
                if (arr != null && arr.length == 2) {
                    dbUnloadInfo.getSchemaPathList().add(arr[0]);
                    dbUnloadInfo.getSchemaDateList().add(arr[1]);
                }
            }
        }
    }
}
