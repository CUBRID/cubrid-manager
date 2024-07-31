/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.common.persist;

import com.cubrid.common.configuration.jdbc.IJDBCConnecInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** The ConnectionInfo Description : ConnectionInfo Author : Kevin.Wang Create date : 2014-2-27 */
public class ConnectionInfo implements IJDBCConnecInfo { // FIXME logic code move to core module
    private String conName;
    private int port;
    private String host;
    private String dbName;
    private String conUser;
    private String conPassword;
    private String driverFileName;
    private String charset;
    private String version;
    private boolean isAutoCommit;
    private String jdbcAttrs;
    private Map<String, Object> parameterMap = new HashMap<String, Object>();

    public ConnectionInfo(
            String conName,
            String host,
            int port,
            String dbName,
            String conUser,
            String conPassword,
            String driverFileName,
            String charset) {
        this.conName = conName;
        this.port = port;
        this.host = host;
        this.dbName = dbName;
        this.conUser = conUser;
        this.conPassword = conPassword;
        this.driverFileName = driverFileName;
        this.charset = charset;
    }

    public ConnectionInfo(
            String conName,
            String host,
            int port,
            String dbName,
            String conUser,
            String conPassword,
            String driverFileName,
            String charset,
            boolean isAutoCommit,
            String version,
            String jdbcAttrs) {
        this(conName, host, port, dbName, conUser, conPassword, driverFileName, charset);
        this.isAutoCommit = isAutoCommit;
        if (StringUtil.isNotEmpty(version)) {
            this.version = version;
        } else if (StringUtil.isNotEmpty(driverFileName)) {
            try {
                this.version = JdbcClassLoaderFactory.getJdbcJarVersion(driverFileName);
            } catch (IOException e) {
                // ignore
            }
        }
        this.jdbcAttrs = jdbcAttrs;
    }

    /** @return the conName */
    public String getConName() {
        return conName;
    }

    /** @return the port */
    public int getPort() {
        return port;
    }

    /** @return the host */
    public String getHost() {
        return host;
    }

    /** @return the dbName */
    public String getDbName() {
        return dbName;
    }

    /** @return the conUser */
    public String getConUser() {
        return conUser;
    }

    /** @return the conPassword */
    public String getConPassword() {
        return conPassword;
    }

    /** @return the driverFileName */
    public String getDriverFileName() {
        return driverFileName;
    }

    public int getDbType() {
        return 1;
    }

    public String getCharset() {
        return charset;
    }

    public String getJDBCAttrs() {
        return this.jdbcAttrs;
    }

    public boolean isAutoCommit() {
        return isAutoCommit;
    }

    public void setParameter(String key, Object value) {
        parameterMap.put(key, value);
    }

    public Object getParameter(String key) {
        return parameterMap.get(key);
    }

    public String getVersion() {
        return version;
    }

    /** @return the jdbcAttrs */
    public String getJdbcAttrs() {
        return jdbcAttrs;
    }

    /** @param jdbcAttrs the jdbcAttrs to set */
    public void setJdbcAttrs(String jdbcAttrs) {
        this.jdbcAttrs = jdbcAttrs;
    }

    /** @return the parameterMap */
    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    /** @param parameterMap the parameterMap to set */
    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    /** @param conName the conName to set */
    public void setConName(String conName) {
        this.conName = conName;
    }

    /** @param port the port to set */
    public void setPort(int port) {
        this.port = port;
    }

    /** @param host the host to set */
    public void setHost(String host) {
        this.host = host;
    }

    /** @param dbName the dbName to set */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /** @param conUser the conUser to set */
    public void setConUser(String conUser) {
        this.conUser = conUser;
    }

    /** @param conPassword the conPassword to set */
    public void setConPassword(String conPassword) {
        this.conPassword = conPassword;
    }

    /** @param driverFileName the driverFileName to set */
    public void setDriverFileName(String driverFileName) {
        this.driverFileName = driverFileName;
    }

    /** @param charset the charset to set */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /** @param version the version to set */
    public void setVersion(String version) {
        this.version = version;
    }

    /** @param isAutoCommit the isAutoCommit to set */
    public void setAutoCommit(boolean isAutoCommit) {
        this.isAutoCommit = isAutoCommit;
    }

    public String getSchema() {
        return null;
    }
}
