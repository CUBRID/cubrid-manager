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
package com.cubrid.common.ui.common.control;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;

/**
 * This utility class is responsible to get notice url logic
 *
 * @author fulei
 * @version 1.0 - 2012-12-04 created by fulei
 */
public class CubridNoticeUtil {
    private static final Logger LOGGER = LogUtil.getLogger(CubridNoticeUtil.class);
    public static final String CHECK_NOTICE_INFO_URL = "http://ftp.cubrid.org/sites/inf/";
    public static final String IGNORE_NOTICE = ".ignore_notice";

    public String getNoticeURL(String code) {
        String checkFileUrl = CHECK_NOTICE_INFO_URL;
        if ("ko_KR".equals(Platform.getNL())) {
            checkFileUrl += code + "/notice_ko.txt";
        } else {
            checkFileUrl += code + "/notice.txt";
        }

        String contents = getCheckFileContents(checkFileUrl);
        if (StringUtil.isEmpty(contents)) {
            return "";
        }

        return checkNoticeContents(contents) ? contents : "";
    }

    /**
     * Check notice contents whether validate
     *
     * @param contents
     * @return boolean
     */
    @SuppressWarnings("unused")
    private boolean checkNoticeContents(String contents) {
        try {
            String[] content = contents.split("\\|");
            if (content.length < 4) {
                return false;
            }

            String index = content[0];
            String startDateString = content[1];
            String endDateString = content[2];
            String noticeURL = content[3];
            String ignoreArray =
                    PersistUtils.getPreferenceValue(CommonUIPlugin.PLUGIN_ID, IGNORE_NOTICE);

            // check whether the index is in the ignore indexes
            for (String ignoreIndex : ignoreArray.split(",")) {
                if (ignoreIndex.equals(index)) {
                    return false;
                }
            }

            if (!checkDate(startDateString, endDateString)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("parse notice contents error :  " + contents, e);
        }

        return false;
    }

    /**
     * Check whether current date is between check file date
     *
     * @param startDateString
     * @param endDateString
     * @return
     */
    private boolean checkDate(String startDateString, String endDateString) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            long startTime = df.parse(startDateString).getTime();
            long endTime = df.parse(endDateString).getTime();
            long currentTime = System.currentTimeMillis();
            if (startTime < currentTime && currentTime < endTime) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * Get check file contents by url
     *
     * @param checkFileUrl
     * @return
     */
    private String getCheckFileContents(String checkFileUrl) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL postUrl = new URL(checkFileUrl);
            conn = (HttpURLConnection) postUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false); // use cache
            conn.connect();
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }
}
