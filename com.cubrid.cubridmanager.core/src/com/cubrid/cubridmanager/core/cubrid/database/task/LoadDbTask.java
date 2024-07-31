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

/**
 * This task is responsible to load database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class LoadDbTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dbname",
                "checkoption",
                "period",
                "user",
                "estimated",
                "oiduse",
                "statisticsuse",
                "nolog",
                "schema",
                "object",
                "index",
                "errorcontrolfile",
                "ignoreclassfile"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public LoadDbTask(ServerInfo serverInfo) {
        super("loaddb", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set database name
     *
     * @param dbName String the database name
     */
    public void setDbName(String dbName) {
        this.setMsgItem("dbname", dbName);
    }

    /**
     * Set load option,schema or data or both
     *
     * @param checkOption String the check option info
     */
    public void setCheckOption(String checkOption) {
        if (checkOption == null
                || (!checkOption.equals("both")
                        && !checkOption.equals("load")
                        && !checkOption.equals("syntax"))) {
            return;
        }
        this.setMsgItem("checkoption", checkOption);
    }

    /**
     * Set period
     *
     * @param isUsed boolean whether is used
     * @param period String the period of using
     */
    public void setUsedPeriod(boolean isUsed, String period) {
        if (isUsed) {
            this.setMsgItem("period", period);
        } else {
            this.setMsgItem("period", "none");
        }
    }

    /**
     * Set database user
     *
     * @param userName String the user name
     */
    public void setDbUser(String userName) {
        this.setMsgItem("user", userName);
    }

    /**
     * Set estimated size
     *
     * @param isUsed boolean whether is used
     * @param size String
     */
    public void setUsedEstimatedSize(boolean isUsed, String size) {
        if (isUsed) {
            this.setMsgItem("estimated", size);
        } else {
            this.setMsgItem("estimated", "none");
        }
    }

    /**
     * Set whether oid is used
     *
     * @param isNoUsed boolean whether is no used
     */
    public void setNoUsedOid(boolean isNoUsed) {
        if (isNoUsed) {
            this.setMsgItem("oiduse", "no");
        } else {
            this.setMsgItem("oiduse", "yes");
        }
    }

    /**
     * Set whether statistics is used
     *
     * @param isNoUsed boolean whether is no used
     */
    public void setNoUsedStatistics(boolean isNoUsed) {
        if (isNoUsed) {
            this.setMsgItem("statisticsuse", "no");
        } else {
            this.setMsgItem("statisticsuse", "yes");
        }
    }

    /**
     * Set whether log is used
     *
     * @param isNoUsed boolean whetrh is no used
     */
    public void setNoUsedLog(boolean isNoUsed) {
        if (isNoUsed) {
            this.setMsgItem("nolog", "yes");
        } else {
            this.setMsgItem("nolog", "no");
        }
    }

    /**
     * Set loaded schema path
     *
     * @param schemaPath String the schema path
     */
    public void setSchemaPath(String schemaPath) {
        this.setMsgItem("schema", schemaPath);
    }

    /**
     * Set loaded object path
     *
     * @param objectPath String the object path
     */
    public void setObjectPath(String objectPath) {
        this.setMsgItem("object", objectPath);
    }

    /**
     * Set loaded index path
     *
     * @param indexPath String the index path
     */
    public void setIndexPath(String indexPath) {
        this.setMsgItem("index", indexPath);
    }

    /**
     * Set error control file
     *
     * @param isUsed boolean Whether is used
     * @param errorControlFilePath String the error control file path
     */
    public void setUsedErrorContorlFile(boolean isUsed, String errorControlFilePath) {
        if (isUsed) {
            this.setMsgItem("errorcontrolfile", errorControlFilePath);
        } else {
            this.setMsgItem("errorcontrolfile", "none");
        }
    }

    /**
     * Set ignored class file
     *
     * @param isUsed boolean whether is used
     * @param ignoredClassFilePath String the ignored class file path
     */
    public void setUsedIgnoredClassFile(boolean isUsed, String ignoredClassFilePath) {
        if (isUsed) {
            this.setMsgItem("ignoreclassfile", ignoredClassFilePath);
        } else {
            this.setMsgItem("ignoreclassfile", "none");
        }
    }

    /**
     * Get load database result
     *
     * @return String[]
     */
    public String[] getLoadResult() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        return response.getValues("line");
    }
}
