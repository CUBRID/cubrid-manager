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
package com.cubrid.cubridmanager.core;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Read sysParam.properties file configuration information
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class SystemParameter {

    static Map<String, String> properties = new HashMap<String, String>();

    static {
        readProperties("sysParam.properties");
    }

    public static String getParameterValue(String key) {
        return properties.get(key);
    }

    public static int getParameterIntValue(String key) {
        String val = properties.get(key);

        return Integer.valueOf(val);
    }

    /** @return */
    public Properties getSysHomeDirFromProperties(String path) {
        Properties initProps = new Properties();
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream(path);
            initProps.load(in);
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
        return initProps;
    }

    @SuppressWarnings("rawtypes")
    public static void readProperties(String filePath) {
        Properties props = new Properties();
        SystemParameter initProperty = new SystemParameter();
        try {

            props = initProperty.getSysHomeDirFromProperties(filePath);
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                if (!properties.containsKey(key)) {
                    properties.put(key, property);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
