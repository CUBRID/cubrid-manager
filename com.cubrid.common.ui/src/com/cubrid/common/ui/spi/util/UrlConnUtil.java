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
package com.cubrid.common.ui.spi.util;

import static com.cubrid.common.core.util.NoOp.noOp;
import static com.cubrid.common.ui.spi.util.CommonUITool.getVersionDateMill;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;

/**
 * This util class is responsible to connect some urls
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-23 created by pangqiren
 */
public final class UrlConnUtil {
	private static final Logger LOGGER = LogUtil.getLogger(UrlConnUtil.class);
	private static final int TIME_OUT_MILL = 3000;
	public static final String CHECK_NEW_INFO_URL_KO = "http://www.cubrid.com/news.htm";
	public static final String CHECK_NEW_INFO_URL_EN = "http://www.cubrid.org/news.php";
	public static final String CHECK_NEW_VERSION_URL_EN = "http://www.cubrid.org/check_version.cub";
	public static final String CHECK_NEW_VERSION_URL_KO = "http://www.cubrid.com/check_version.cub";
	public static final String CHECK_NOTICE_INFO_URL_KO = "http://ftp.cubrid.org/sites/inf/cqb/notice_ko.txt";
	public static final String CHECK_NOTICE_INFO_URL_EN = "http://ftp.cubrid.org/sites/inf/cqb/notice.txt";
	public static final String REPORT_BUG_URL_KO = "http://jira.cubrid.org";
	public static final String REPORT_BUG_URL_EN = "http://jira.cubrid.org";

	private UrlConnUtil() {
		noOp();
	}

	/**
	 * Return whether the url can be connected
	 *
	 * @param url the url str
	 * @return <code>true</code> if the url exist;<code>false</code>otherwise
	 */
	public static boolean isUrlExist(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(TIME_OUT_MILL);
			conn.setReadTimeout(TIME_OUT_MILL);
			if (conn.getResponseCode() == HTTP_OK) {
				return true;
			}
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return false;
	}

	/**
	 * Get Url Content
	 *
	 * @param urlStr the url string
	 * @param userAgent String
	 * @return string the page content
	 */
	public static String getContent(String urlStr, String userAgent) {
		HttpURLConnection conn = null;
		BufferedReader in = null;

		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Http-User-Agent", userAgent);
			conn.setConnectTimeout(TIME_OUT_MILL);
			conn.setReadTimeout(TIME_OUT_MILL);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append("\n");
			}

			return sb.toString();
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return "";
	}

	/**
	 *
	 * Return whether CUBRID new version exist
	 *
	 * @param localVersion String
	 * @param userAgent String
	 * @return <code>true</code> if new cubrid version exist;<code>false</code>
	 *         otherwise
	 */
	public static boolean isExistNewCubridVersion(String localVersion, String userAgent) {
		String url = Platform.getNL().equals("ko_KR") ? CHECK_NEW_VERSION_URL_KO
				: CHECK_NEW_VERSION_URL_EN;
		if (!isUrlExist(url)) {
			return false;
		}

		String content = getContent(url, userAgent);
		if (isBlank(content)) {
			return false;
		}
		content = content.toUpperCase(Locale.getDefault());
		if (content.indexOf("<HTML") >= 0) {
			content = content.substring(content.indexOf("<HTML"));
		}

		try {
			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			IXMLMemento memento = XMLMemento.loadMemento(in);
			if (memento == null) {
				return false;
			}
			IXMLMemento[] children = memento.getChildren("BODY");
			if (children != null && children.length == 1) {
				content = children[0].getTextData();
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}

		return compareVersion(content, localVersion);
	}

	/**
	 * Compare the version
	 *
	 * @param serverVersion the server version
	 * @param localVersion the local version
	 * @return <code>true</code> if new cubrid version exist;<code>false</code>
	 *         otherwise
	 */
	private static boolean compareVersion(String serverVersion, String localVersion) {
		if (serverVersion == null || serverVersion.trim().length() == 0
				|| !serverVersion.trim().matches("^(\\d+\\.){3}\\d+$")) {
			return false;
		}
		if (localVersion == null || localVersion.trim().length() == 0
				|| !localVersion.trim().matches("^(\\d+\\.){3}\\d+$")) {
			return false;
		}
		String[] latestBuildIdArr = serverVersion.trim().split("\\.");
		String[] localBuildIdArr = localVersion.split("\\.");
		if (latestBuildIdArr == null || localBuildIdArr == null) {
			return false;
		}

		for (int i = 0; i < localBuildIdArr.length && i < latestBuildIdArr.length; i++) {
			try {
				long localBuildId = getVersionDateMill(localBuildIdArr[i]);
				long latestBuildId = getVersionDateMill(latestBuildIdArr[i]);
				if (latestBuildId > localBuildId) {
					return true;
				} else if (latestBuildId < localBuildId) {
					return false;
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return false;
			}
		}

		return false;
	}
}
