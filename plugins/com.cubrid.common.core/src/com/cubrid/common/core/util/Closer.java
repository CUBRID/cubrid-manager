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
package com.cubrid.common.core.util;

import static com.cubrid.common.core.util.NoOp.noOp;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Close the resource utility.
 *
 * @author lcl
 */
public final class Closer {
    private Closer() {
        noOp();
    }

    /**
     * close closeable object silently (will eat off exception, print to error stream)
     *
     * @param co closeable object, can be null.
     */
    public static void close(Closeable co) {
        if (co == null) {
            return;
        }

        try {
            co.close();
        } catch (IOException e) {
            noOp();
        }
    }

    /**
     * close socket silently (will eat off exception, print to error stream)
     *
     * @param sock socket object, can be null.
     */
    public static void close(Socket sock) {
        if (sock == null) {
            return;
        }

        try {
            sock.close();
        } catch (IOException e) {
            noOp();
        }
    }

    /**
     * close server socket silently (will eat off exception, print to error stream)
     *
     * @param sock server socket object, can be null.
     */
    public static void close(ServerSocket sock) {
        if (sock == null) {
            return;
        }

        try {
            sock.close();
        } catch (IOException e) {
            noOp();
        }
    }

    /**
     * close datagram socket silently (will eat off exception, print to error stream)
     *
     * @param sock datagram socket object, can be null.
     */
    public static void close(DatagramSocket sock) {
        if (sock == null) {
            return;
        }

        sock.close();
    }

    /**
     * close selector silently (will eat off exception, print to error stream)
     *
     * @param selector selector object, can be null
     */
    public static void close(Selector selector) {

        if (selector == null) {
            return;
        }

        try {
            selector.close();
        } catch (IOException e) {
            noOp();
        }
    }

    /**
     * close database connection silently (will eat off exception, print to error stream)
     *
     * @param conn db connection
     */
    public static void close(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            conn.close();
        } catch (SQLException e) {
            noOp();
        }
    }
}
