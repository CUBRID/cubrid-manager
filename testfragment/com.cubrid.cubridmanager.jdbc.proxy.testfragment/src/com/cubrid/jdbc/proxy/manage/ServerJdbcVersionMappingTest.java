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

import java.io.IOException;
import junit.framework.TestCase;

/**
 * Test ServerJdbcVersionMapping
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
public class ServerJdbcVersionMappingTest extends TestCase {

    public void test() {
        String[] values = ServerJdbcVersionMapping.getSupportedJdbcVersions("8.2.2");
        assertTrue(values != null && values[0].equals("CUBRID-JDBC-8.2.[0-2].[0-9]{4}"));
        values = ServerJdbcVersionMapping.getSupportedJdbcVersions("8.2.1");
        assertTrue(values != null && values[0].equals("CUBRID-JDBC-8.2.[0-1].[0-9]{4}"));
    }

    public void testGetJdbcJarVersion() throws IOException {
        String res = null;

        res = JdbcClassLoaderFactory.getJdbcJarVersion("resource/lib/CUBRID-8.4.1_jdbc.jar");
        assertNotNull(res);

        res = JdbcClassLoaderFactory.getJdbcJarVersion("resource/lib/JDBC-8.4.1.7007-cubrid.jar");
        assertNotNull(res);

        res = JdbcClassLoaderFactory.getJdbcJarVersion("resource/lib/JDBC-8.4.3.0150-cubrid.jar");
        assertNotNull(res);

        res = JdbcClassLoaderFactory.getJdbcJarVersion("resource/lib/JDBC-9.1.0.0212-cubrid.jar");
        assertNotNull(res);
    }
}
