/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the common log4j interface to be convenient to Get Logger.
 *
 * @author pangqiren
 * @version 1.0 - 2009-06-04 created by pangqiren
 * @version 1.1 - 2009-09-06 updated by Isaiah Choe
 */
public final class LogUtil {
    private LogUtil() {}

    /**
     * re-initialize logger configurations
     *
     * @param level Level
     * @param workspace String
     */
    public static void configLogger(Level level, String workspace) {
        Properties configPro = new Properties();
        InputStream in = null;
        try {
            in = new LogUtil().getClass().getResourceAsStream("/log4j.properties");
            configPro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            configPro = null;
        } finally {
            FileUtil.close(in);
        }

        // If log4j.properties can't be found.
        if (configPro == null) {
            return;
        }

        if (Level.ERROR.equals(level)) {
            configPro.put("log4j.rootLogger", "ERROR,stdout,logfile");
        } else if (Level.DEBUG.equals(level)) {
            configPro.put("log4j.rootLogger", "DEBUG,stdout,logfile");
        }

        String logPath = workspace;
        if (workspace == null) {
            logPath = System.getProperty("user.home");
        }
        if (logPath != null) {
            logPath =
                    logPath
                            + File.separator
                            + "logs"
                            + File.separator
                            + "cubrid.log"; // TODO: have to rename another name.
            configPro.put("log4j.appender.logfile.file", logPath);
        }

        PropertyConfigurator.configure(configPro);
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
