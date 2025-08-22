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
package com.cubrid.jdbc.proxy.manage;

import java.sql.SQLException;
import junit.framework.TestCase;

/**
 * Test CUBRIDProxyException
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-19 created by pangqiren
 */
public class CUBRIDProxySQLExceptionTest extends TestCase {

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.manage.CUBRIDProxySQLException#CUBRIDProxyException(java.lang.String,
     * int)} .
     */
    public void testCUBRIDProxyExceptionStringInt() {
        CUBRIDProxySQLException e = new CUBRIDProxySQLException("proxy exception", -2003);
        assertEquals(e.getMessage(), "proxy exception");
        assertEquals(e.getErrorCode(), -2003);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.manage.CUBRIDProxySQLException#CUBRIDProxyException(java.sql.SQLException)}
     * .
     */
    public void testCUBRIDProxyExceptionSQLException() {
        SQLException sqlException = new SQLException("exception", "sqlState", -3003);
        CUBRIDProxySQLException e = new CUBRIDProxySQLException(sqlException);
        assertEquals(e.getMessage(), "exception");
        assertEquals(e.getSQLState(), "sqlState");
        assertEquals(e.getErrorCode(), -3003);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.manage.CUBRIDProxySQLException#CUBRIDProxyException(java.lang.Throwable,
     * int)} .
     */
    public void testCUBRIDProxyExceptionThrowableInt() {
        Exception exception = new Exception("exception");
        CUBRIDProxySQLException e = new CUBRIDProxySQLException(exception, -3003);
        assertEquals(e.getMessage(), "exception");
        assertEquals(e.getErrorCode(), -3003);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.manage.CUBRIDProxySQLException#CUBRIDProxyException(java.lang.Throwable,
     * java.sql.SQLException)} .
     */
    public void testCUBRIDProxyExceptionThrowableSQLException() {
        Exception exception = new Exception("exception");
        SQLException sqlException = new SQLException("sqlexception", "sqlState", -3003);
        CUBRIDProxySQLException e = new CUBRIDProxySQLException(exception, sqlException);
        assertEquals(e.getMessage(), "exception\r\nsqlexception");
        assertEquals(e.getSQLState(), "sqlState");
        assertEquals(e.getErrorCode(), -3003);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.manage.CUBRIDProxySQLException#CUBRIDProxyException(java.lang.Throwable,
     * java.lang.Throwable, int)} .
     */
    public void testCUBRIDProxyExceptionThrowableThrowableInt() {
        Exception exception = new Exception("exception");
        SQLException sqlException = new SQLException("sqlexception", "sqlState", -3003);
        CUBRIDProxySQLException e = new CUBRIDProxySQLException(exception, sqlException, -4003);
        assertEquals(e.getMessage(), "exception\r\nsqlexception");
        assertEquals(e.getErrorCode(), -4003);
    }
}
