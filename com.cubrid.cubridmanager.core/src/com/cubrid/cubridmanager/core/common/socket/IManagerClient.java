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
public interface IManagerClient {

    /**
     * Send request to CUBRID Manager server
     *
     * @param message String
     */
    void sendRequest(String message);

    /** Stop reading response message. */
    @Deprecated
    void stopRead();

    /**
     * Set the heart beat time in milliseconds, this method takes effect only at the first time.
     * this method is to monitor whether the socket connection status is OK.
     *
     * @param time The heart time in milliseconds
     */
    @Deprecated
    void setHeartbeat(final int time);

    /** Tear down the socket connection */
    void tearDownConnection();

    /**
     * Return parsed response node
     *
     * @return TreeNode
     */
    TreeNode getResponse();

    /**
     * Return the original response message
     *
     * @return String
     */
    String getResponsedMsg();

    /**
     * Set whether using special delimiter.
     *
     * @param usingSpecialDelimiter Whether using the special delimiter
     */
    @Deprecated
    void setUsingSpecialDelimiter(boolean usingSpecialDelimiter);

    /**
     * Get error message
     *
     * @return The error message
     */
    String getErrorMsg();

    /**
     * Get warning message
     *
     * @return The warning message
     */
    String getWarningMsg();

    /**
     * Get the host address of this socket
     *
     * @return The host address
     */
    String getHostAddress();

    /**
     * Set the host address of this socket
     *
     * @param hostAddress The host address
     */
    void setHostAddress(String hostAddress);

    /**
     * Get the port of this socket
     *
     * @return The port of this socket
     */
    int getPort();

    /**
     * Set the port of this socket
     *
     * @param port The prot of this socket
     */
    void setPort(int port);

    /**
     * Get heart beat thread.
     *
     * @return The heart beat thread
     */
    @Deprecated
    Thread getHeartbeatThread();

    /** Stop heart beat thread. */
    @Deprecated
    void stopHeartbeatThread();

    /**
     * Set time out
     *
     * @param timeout int
     */
    void setTimeout(int timeout);

    /**
     * get the response status code
     *
     * @return response status code
     */
    int getStatusCode();

    /**
     * get whether the host:port can be connected.
     *
     * @return
     */
    boolean canConnect();
}
