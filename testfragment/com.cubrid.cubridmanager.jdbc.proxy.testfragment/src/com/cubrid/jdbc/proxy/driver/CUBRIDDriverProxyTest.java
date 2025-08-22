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
package com.cubrid.jdbc.proxy.driver;

import com.cubrid.jdbc.proxy.ConnectionInfo;
import com.cubrid.jdbc.proxy.SetupEnv;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test CUBRIDDriverProxy
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
public class CUBRIDDriverProxyTest extends SetupEnv {

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#CUBRIDDriverProxy(java.lang.String)} .
     */
    public void testCUBRIDDriverProxyString() {
        for (ConnectionInfo connInfo : connInfoList) {
            try {
                new CUBRIDDriverProxy(connInfo.getServerVersion());
                assertTrue(true);
            } catch (Exception e) {
                assertFalse(true);
            }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#CUBRIDDriverProxy(java.sql.Driver)} .
     */
    @SuppressWarnings("unchecked")
    public void testCUBRIDDriverProxyDriver() {
        for (ConnectionInfo connInfo : connInfoList) {
            try {
                ClassLoader loader =
                        JdbcClassLoaderFactory.getClassLoader(connInfo.getServerVersion());
                Class<Driver> driverClazz =
                        (Class<Driver>) loader.loadClass("cubrid.jdbc.driver.CUBRIDDriver");
                Driver driver = driverClazz.newInstance();
                new CUBRIDDriverProxy(driver);
                assertTrue(true);
            } catch (Exception e) {
                assertFalse(true);
            }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#connect(java.lang.String,
     * java.util.Properties)} .
     */
    public void testConnectStringProperties() {
        for (ConnectionInfo connInfo : connInfoList) {
            Connection conn = null;
            try {
                CUBRIDDriverProxy driver = new CUBRIDDriverProxy(connInfo.getServerVersion());
                Properties props = new Properties();
                props.put("user", connInfo.getDbUser());
                props.put(
                        "password",
                        connInfo.getDbUserPass() == null ? "" : connInfo.getDbUserPass());
                conn = driver.connect(connInfo.getConnectionUrl(), props);
                assertTrue(conn != null);
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#connect(java.lang.String,
     * java.util.Properties, java.lang.String)} .
     */
    public void testConnectStringPropertiesString() {
        for (ConnectionInfo connInfo : connInfoList) {
            Connection conn = null;
            try {
                CUBRIDDriverProxy driver = new CUBRIDDriverProxy(connInfo.getServerVersion());
                Properties props = new Properties();
                props.put("user", connInfo.getDbUser());
                props.put(
                        "password",
                        connInfo.getDbUserPass() == null ? "" : connInfo.getDbUserPass());
                methodGetPropertyInfo(connInfo, driver, props);
                /*
                 * Test for
                 * {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#connect(java.lang.String, java.util.Properties, java.lang.String)}
                 * .
                 */
                conn =
                        driver.connect(
                                connInfo.getConnectionUrl(), props, connInfo.getServerVersion());
                assertTrue(conn != null);
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /** Test other method */
    public void test() {
        System.out.println("test CUBRIDDriverProxy:");
        for (ConnectionInfo connInfo : connInfoList) {
            try {
                CUBRIDDriverProxy driver = new CUBRIDDriverProxy(connInfo.getServerVersion());
                methodAcceptsURL(connInfo, driver);
                methodGetMajorVersion(driver);
                methodGetMinorVersion(driver);
                methodJdbcCompliant(driver);
                methodGetPatchVersion(driver);
                methodGetJdbcVersion(connInfo, driver);
                methodSetJdbcVersion(driver);
            } catch (Exception e) {
                assertFalse(true);
            }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#defaultConnection()} .
     */
    public void testDefaultConnection() {
        for (ConnectionInfo connInfo : connInfoList) {
            Connection conn = null;
            try {
                CUBRIDDriverProxy driver = new CUBRIDDriverProxy(connInfo.getServerVersion());
                conn = driver.defaultConnection();
                assertTrue(conn != null);
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Test for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#acceptsURL(java.lang.String)}
     *
     * @throws SQLException
     */
    public void methodAcceptsURL(ConnectionInfo connInfo, CUBRIDDriverProxy driver)
            throws SQLException {
        assertTrue(driver.acceptsURL(connInfo.getConnectionUrl()));
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#getMajorVersion()}. */
    public void methodGetMajorVersion(CUBRIDDriverProxy driver) {
        System.out.println("driver major version:" + driver.getMajorVersion());
        assertTrue(driver.getMajorVersion() > 0);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#getMinorVersion()}. */
    public void methodGetMinorVersion(CUBRIDDriverProxy driver) {
        System.out.println("driver minor version:" + driver.getMinorVersion());
        assertTrue(driver.getMinorVersion() > 0);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#getPropertyInfo(java.lang.String,
     * java.util.Properties)} .
     *
     * @throws SQLException
     */
    public void methodGetPropertyInfo(
            ConnectionInfo connInfo, CUBRIDDriverProxy driver, Properties props)
            throws SQLException {
        DriverPropertyInfo[] driverPropertyInfo =
                driver.getPropertyInfo(connInfo.getConnectionUrl(), props);
        assertTrue(driverPropertyInfo != null);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#jdbcCompliant()}. */
    public void methodJdbcCompliant(CUBRIDDriverProxy driver) {
        System.out.println(driver.jdbcCompliant());
        assertTrue(driver.jdbcCompliant());
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#getPatchVersion()}.
     *
     * @throws SQLException
     */
    public void methodGetPatchVersion(CUBRIDDriverProxy driver) {
        try {
            System.out.println("driver patch version:" + driver.getPatchVersion());
            assertTrue(driver.getPatchVersion() > 0);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#getJdbcVersion()}. */
    public void methodGetJdbcVersion(ConnectionInfo connInfo, CUBRIDDriverProxy driver) {
        System.out.println("driver jdbc version:" + driver.getJdbcVersion());
        assertTrue(driver.getJdbcVersion().equals(connInfo.getServerVersion()));
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDDriverProxy#setJdbcVersion(java.lang.String)} .
     */
    public void methodSetJdbcVersion(CUBRIDDriverProxy driver) {
        driver.setJdbcVersion(driver.getJdbcVersion());
    }
}
