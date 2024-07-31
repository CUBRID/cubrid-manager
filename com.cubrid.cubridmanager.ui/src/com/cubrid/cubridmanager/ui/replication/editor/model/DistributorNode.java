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
package com.cubrid.cubridmanager.ui.replication.editor.model;

/**
 * The distributor model object,it store distributor database information
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class DistributorNode extends LeafNode {
    private String dbName;
    private String dbPath;
    private String dbaPassword;
    private String replAgentPort;
    private String copyLogPath;
    private String trailLogPath;
    private String errorLogPath;
    private String delayTimeLogSize;
    private boolean isRestartWhenError;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getDbaPassword() {
        return dbaPassword;
    }

    public void setDbaPassword(String dbaPassword) {
        this.dbaPassword = dbaPassword;
    }

    public String getReplAgentPort() {
        return replAgentPort;
    }

    public void setReplAgentPort(String replAgentPort) {
        this.replAgentPort = replAgentPort;
    }

    public String getCopyLogPath() {
        return copyLogPath;
    }

    public void setCopyLogPath(String copyLogPath) {
        this.copyLogPath = copyLogPath;
    }

    public String getTrailLogPath() {
        return trailLogPath;
    }

    public void setTrailLogPath(String trailLogPath) {
        this.trailLogPath = trailLogPath;
    }

    public String getErrorLogPath() {
        return errorLogPath;
    }

    public void setErrorLogPath(String errorLogPath) {
        this.errorLogPath = errorLogPath;
    }

    public String getDelayTimeLogSize() {
        return delayTimeLogSize;
    }

    public void setDelayTimeLogSize(String delayTimeLogSize) {
        this.delayTimeLogSize = delayTimeLogSize;
    }

    public boolean isRestartWhenError() {
        return isRestartWhenError;
    }

    public void setRestartWhenError(boolean isRestartWhenError) {
        this.isRestartWhenError = isRestartWhenError;
    }

    /**
     * @see com.cubrid.cubridmanager.ui.replication.editor.model.Node#isValid()
     * @return boolean
     */
    public boolean isValid() { // FIXME more simple
        if (dbName == null || dbName.trim().length() == 0) {
            return false;
        }
        if (dbPath == null || dbPath.trim().length() == 0) {
            return false;
        }
        if (dbaPassword == null || dbaPassword.trim().length() == 0) {
            return false;
        }
        if (replAgentPort == null || replAgentPort.trim().length() == 0) {
            return false;
        }
        if (copyLogPath == null || copyLogPath.trim().length() == 0) {
            return false;
        }
        if (trailLogPath == null || trailLogPath.trim().length() == 0) {
            return false;
        }
        if (errorLogPath == null || errorLogPath.trim().length() == 0) {
            return false;
        }
        if (delayTimeLogSize == null || delayTimeLogSize.trim().length() == 0) {
            return false;
        }
        return true;
    }
}
