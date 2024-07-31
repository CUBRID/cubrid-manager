/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.cubridmanager.core.replication.model;

/**
 * The distributor database information POJO
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-29 created by pangqiren
 */
public class DistributorInfo {

    private String distDbName;
    private String distDbPath;
    private String agentPort;
    private String trailLogPath;
    private String errorLogPath;
    private String copyLogPath;
    private String delayTimeLogSize;
    private boolean isRestartReplWhenError;
    private boolean isAgentActive = false;

    public String getDistDbName() {
        return distDbName;
    }

    public void setDistDbName(String distDbName) {
        this.distDbName = distDbName;
    }

    public String getDistDbPath() {
        return distDbPath;
    }

    public void setDistDbPath(String distDbPath) {
        this.distDbPath = distDbPath;
    }

    public String getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(String agentPort) {
        this.agentPort = agentPort;
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

    public String getCopyLogPath() {
        return copyLogPath;
    }

    public void setCopyLogPath(String copyLogPath) {
        this.copyLogPath = copyLogPath;
    }

    public String getDelayTimeLogSize() {
        return delayTimeLogSize;
    }

    public void setDelayTimeLogSize(String delayTimeLogSize) {
        this.delayTimeLogSize = delayTimeLogSize;
    }

    public boolean isRestartReplWhenError() {
        return isRestartReplWhenError;
    }

    public void setRestartReplWhenError(boolean isRestartReplWhenError) {
        this.isRestartReplWhenError = isRestartReplWhenError;
    }

    public boolean isAgentActive() {
        return isAgentActive;
    }

    public void setAgentActive(boolean isAgentActive) {
        this.isAgentActive = isAgentActive;
    }
}
