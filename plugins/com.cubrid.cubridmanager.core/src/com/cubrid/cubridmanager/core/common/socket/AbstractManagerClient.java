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
package com.cubrid.cubridmanager.core.common.socket;

/**
 * To provide connection service for upper layer, send message, receive message and parse message
 * for upper layer to use.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-10-17
 */
public abstract class AbstractManagerClient implements IManagerClient {

    /** return result */
    protected TreeNode response;

    protected String responsedMsg;
    protected String errorMsg;
    protected String warningMsg;
    protected int statusCode;

    protected String hostAddress;
    protected int port;
    protected String userName;

    protected String requestCharsetName = "UTF-8";
    protected String responseCharsetName = "UTF-8";

    protected boolean canConnect = true;

    /**
     * Return parsed response node
     *
     * @return TreeNode
     */
    public TreeNode getResponse() {
        return response;
    }

    /**
     * Return the original response message
     *
     * @return String
     */
    public String getResponsedMsg() {
        return responsedMsg;
    }

    /**
     * Get error message
     *
     * @return String The error message
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * Get warning message
     *
     * @return String The warning message
     */
    public String getWarningMsg() {
        return warningMsg;
    }

    /**
     * Get the host address of this socket
     *
     * @return String The host address
     */
    public String getHostAddress() {
        return hostAddress;
    }

    /**
     * Set the host address of this socket
     *
     * @param hostAddress String The host address
     */
    public void setHostAddress(String hostAddress) {
        tearDownConnection();
        this.hostAddress = hostAddress;
    }

    /**
     * Set the port of this socket
     *
     * @param port int The prot of this socket
     */
    public void setPort(int port) {
        tearDownConnection();
        this.port = port;
    }

    /**
     * Get the port of this socket
     *
     * @return int The port of this socket
     */
    public int getPort() {
        return port;
    }

    public void stopRead() {
        // do thing
    }

    public void setHeartbeat(int time) {
        // do thing
    }

    public void setUsingSpecialDelimiter(boolean usingSpecialDelimiter) {
        // do thing
    }

    public Thread getHeartbeatThread() {
        // do thing
        return null;
    }

    public void stopHeartbeatThread() {
        // do thing
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean canConnect() {
        return canConnect;
    }
}
