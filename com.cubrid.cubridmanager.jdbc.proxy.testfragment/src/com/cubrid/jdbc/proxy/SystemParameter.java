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
package com.cubrid.jdbc.proxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Read sysParam.properties file configuration information
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class SystemParameter {

    private static Map<String, ConnectionInfo> connInfoMap = new HashMap<String, ConnectionInfo>();

    static {
        readProperties("sysParam.properties");
    }

    public static Map<String, ConnectionInfo> getConnInfoMap() {
        return connInfoMap;
    }

    public static void readProperties(String filePath) {
        InputStream in = null;
        try {
            in = SystemParameter.class.getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            ConnectionInfo connInfo = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0 || line.matches("\\s*#")) {
                    continue;
                }
                if (line.matches("^\\[.+\\]$")) {
                    String serverName = line.replaceAll("\\[", "");
                    serverName = serverName.replaceAll("\\]", "");
                    connInfo = new ConnectionInfo();
                    connInfoMap.put(serverName, connInfo);
                } else {
                    String[] properties = line.split("=");
                    if (properties == null || properties.length != 2) {
                        continue;
                    }
                    String key = properties[0].trim();
                    String value = properties[1].trim();
                    if (key.equals("hostIp")) {
                        connInfo.setHostIp(value);
                    } else if (key.equals("hostVersion")) {
                        connInfo.setServerVersion(value);
                    } else if (key.equals("dbname")) {
                        connInfo.setDbName(value);
                    } else if (key.equals("charset")) {
                        connInfo.setCharset(value);
                    } else if (key.equals("port")) {
                        connInfo.setPort(value);
                    } else if (key.equals("dbUser")) {
                        connInfo.setDbUser(value);
                    } else if (key.equals("userPass")) {
                        connInfo.setDbUserPass(value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
