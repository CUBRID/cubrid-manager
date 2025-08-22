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
package com.cubrid.jdbc.proxy;

import com.cubrid.jdbc.proxy.manage.CubridManagerJdbcProxyPlugin;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.TestCase;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

/**
 * Set up test env
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-15 created by pangqiren
 */
public abstract class SetupEnv extends TestCase {
    protected static Collection<ConnectionInfo> connInfoList = null;

    protected void setUp() throws Exception {
        if (connInfoList == null) {
            connInfoList = SystemParameter.getConnInfoMap().values();
            File libDir = new File(getFilePathInPlugin("/lib/"));
            Iterator<ConnectionInfo> it = connInfoList.iterator();
            JdbcClassLoaderFactory.clearClassLoader();
            while (it.hasNext()) {
                ConnectionInfo connInfo = it.next();
                String serverVersion = connInfo.getServerVersion();
                if (libDir != null && libDir.isDirectory()) {
                    File[] libFiles = libDir.listFiles();
                    for (File libFile : libFiles) {
                        String libFilePath = libFile.getAbsolutePath();
                        String version = JdbcClassLoaderFactory.validateJdbcFile(libFilePath);
                        if (version != null
                                && version.indexOf(serverVersion) > 0
                                && !JdbcClassLoaderFactory.isContainedClassLoader(libFilePath)) {
                            JdbcClassLoaderFactory.registerClassLoader(libFilePath);
                            connInfo.setServerVersion(version);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is to find the file's path in a fragment or a plugin.
     *
     * @param filepath the file path in the fragment or a plugin
     * @return the absolute file path
     */
    public String getFilePathInPlugin(String filepath) {
        URL fileUrl = null;
        if (CubridManagerJdbcProxyPlugin.getDefault() == null) {
            fileUrl = this.getClass().getResource(filepath);
        } else {
            Bundle bundle = CubridManagerJdbcProxyPlugin.getDefault().getBundle();
            URL url = bundle.getResource(filepath);
            try {
                fileUrl = FileLocator.toFileURL(url);
            } catch (IOException e) {
                return null;
            }
        }
        return fileUrl == null ? null : fileUrl.getPath();
    }

    public boolean executeDDL(Connection conn, String sql) {
        boolean success = false;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            boolean isSuccess = stmt.execute(sql);
            assert (isSuccess == false);
            success = true;
            conn.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            stmt = null;
        }
        return success;
    }
}
