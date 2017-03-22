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
package com.cubrid.common.ui.spi.persist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * 
 * Provide some static method to get query options
 * 
 * @author wangsl 2009-3-31
 */
public final class QueryOptions {

	private static final Logger LOGGER = LogUtil.getLogger(QueryOptions.class);

	public static final String AUTO_COMMIT = ".auto_commit";
	public static final String ENABLE_UNIT_INSTANCES = ".enable_unit_instances";
	public static final String UNIT_INSTANCES_COUNT = ".instances_count";
	public static final String PAGE_INSTANCES_COUNT = ".page_count";
	public static final String DISPLAY_TYPE_QUERY_PLAN = ".display_type_query_plan";
	public static final String ENABLE_GET_OID = ".enable_get_oid";
	public static final String BROKER_PORT = ".broker_port";
	public static final String CHAR_SET = ".char_set";
	public static final String FONT_STRING = ".font_string";
	public static final String FONT_RGB_RED = ".font_red";
	public static final String FONT_RGB_GREEN = ".font_green";
	public static final String FONT_RGB_BLUE = ".font_blue";
	public static final String USE_DEFAULT_DRIVER = ".use_custom_driver";
	public static final String DRIVER_PATH = ".driver_path";
	public static final String BROKER_IP = ".broker_ip";
	public static final String SHOW_STYLE = ".show_style";
	public static final String KEYWORD_LOWERCASE = ".keyword_lowercase";
	public static final String KEYWORD_NO_AUTO_UPPERCASE = ".keyword_no_autouppercase";
	public static final String WITHOUT_PROMPT_SAVE = ".without_prompt_save";
	public static final String MULTI_PAGE_CONFIRM = ".multi_page_confirm";
	public static final String USE_SCIENTIFIC_NOTATION = ".use_scienfitic_notation";
	public static final String LOB_LOAD_SIZE = ".lob_load_size";

	public static final int DEFAULT_MAX_RECORD_LIMIT = 5000;
	public static final int DEFAULT_MAX_PAGE_LIMIT = 100;

	public static final int FONT_COLOR_RED = 0;
	public static final int FONT_COLOR_GREEN = 0;
	public static final int FONT_COLOR_BLUE = 0;

	public static final int DEFAULT_QUERY_PLAN_DISPLAY_MODEL = 1;
	public static final int QUERY_PLAN_DISPLAY_MODE_TEXT = 0;
	public static final int QUERY_PLAN_DISPLAY_MODE_TREE = 1;
	public static final int QUERY_PLAN_DISPLAY_MODE_GRAPH = 2;

	public static final String MONITOR_FOLDER_NAME = ".StatusMonitor.";

	private static final String[] ALL_CHARSET = { "UTF-8", "ISO-8859-1",
			"EUC-KR", "EUC-JP", "GB2312", "GBK" };

	private static Preferences pref = PersistUtils
			.getPreference(CommonUIPlugin.PLUGIN_ID);
	private static Map<String, DatabaseEditorConfig> editorConfigMap = new HashMap<String, DatabaseEditorConfig>();

	// Constructor
	private QueryOptions() {
		// empty
	}

	/**
	 * 
	 * Set preference value
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void setPref(String key, String value) {
		pref.put(key, value);
	}

	/**
	 * 
	 * Remove all preference value from this server
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 */
	public static void removePref(ServerInfo serverInfo) {
		if (serverInfo == null) {
			return;
		}
		String prefix = getPrefix(serverInfo);
		if (prefix == null || prefix.trim().length() <= 0) {
			return;
		}
		prefix += ".";
		try {
			String[] keys = pref.keys();
			for (String key : keys) {
				if (key != null && key.trim().startsWith(prefix)) {
					pref.remove(key);
				}
			}
			savePref();
		} catch (BackingStoreException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * 
	 * Remove all preference value from this database
	 * 
	 * @param dbInfo
	 *            the DatabaseInfo obj
	 */
	public static void removePref(DatabaseInfo dbInfo) {
		if (dbInfo == null) {
			return;
		}
		String prefix = getPrefix(dbInfo);
		if (prefix == null || prefix.trim().length() <= 0) {
			return;
		}
		prefix += ".";
		try {
			String[] keys = pref.keys();
			for (String key : keys) {
				if (key != null && key.trim().startsWith(prefix)) {
					pref.remove(key);
				}
			}
			savePref();
		} catch (BackingStoreException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * 
	 * Get all charset including system charset and added charset
	 * 
	 * @param addedCharset
	 *            the charset
	 * @return all charset
	 */
	public static String[] getAllCharset(String addedCharset) {
		String charset = StringUtil.getDefaultCharset();
		boolean isExistSysCharset = false;
		List<String> charsetList = new ArrayList<String>();
		for (int i = 0; i < ALL_CHARSET.length; i++) {
			if (charset != null
					&& charset.trim().equalsIgnoreCase(ALL_CHARSET[i])) {
				isExistSysCharset = true;
			}
			charsetList.add(ALL_CHARSET[i]);
		}
		if (!isExistSysCharset && charset != null
				&& charset.trim().length() > 0) {
			charsetList.add(1, charset);
		}

		boolean isExistAddedCharset = false;
		for (int i = 0; i < charsetList.size(); i++) {
			if (addedCharset != null
					&& addedCharset.trim().equalsIgnoreCase(charsetList.get(i))) {
				isExistAddedCharset = true;
				break;
			}
		}
		if (!isExistAddedCharset && addedCharset != null
				&& addedCharset.trim().length() > 0) {
			charsetList.add(0, addedCharset);
		}
		String[] charsets = new String[charsetList.size()];
		return charsetList.toArray(charsets);
	}

	/**
	 * 
	 * Persistence preference
	 * 
	 */
	public static void savePref() {
		try {
			pref.flush();
		} catch (BackingStoreException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * 
	 * Get prefix string by ServerInfo;if serverInfo is null,return ""
	 * 
	 * @param serverInfo
	 *            the ServerInfo obj
	 * @return the prefix string
	 */
	public static String getPrefix(ServerInfo serverInfo) {
		String prefix = "";
		if (serverInfo != null
				&& !DatabaseNavigatorMenu.SELF_DATABASE_ID.equals(serverInfo
						.getServerName())) {
			prefix = serverInfo.getServerName();
		}
		return prefix;
	}

	/**
	 * 
	 * Get prefix string by DatabaseInfo;if databaseInfo is null,return ""
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo obj
	 * @return the prefix string
	 */
	public static String getPrefix(DatabaseInfo databaseInfo) {
		String prefix = "";
		if (databaseInfo != null) {
			prefix = databaseInfo.getServerInfo().getServerName() + "."
					+ databaseInfo.getDbName();
		}
		return prefix;
	}

	/**
	 * 
	 * Get full preference key string
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param key
	 *            the preference key
	 * @return the full key string
	 */
	private static String getPreKey(ServerInfo serverInfo, String key) {
		String fullKey = key;
		if (serverInfo != null) {
			String prefix = getPrefix(serverInfo);
			fullKey = prefix + key;
			String value = pref.get(fullKey, null);
			if (value != null) {
				return fullKey;
			}
		}
		return key;
	}

	/**
	 * 
	 * Get full preference key string
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo object
	 * @param key
	 *            the preference key
	 * @return the full key string
	 */
	private static String getPreKey(DatabaseInfo databaseInfo, String key) {
		String fullKey = key;
		if (databaseInfo != null) {
			String prefix = getPrefix(databaseInfo);
			fullKey = prefix + key;
			String value = pref.get(fullKey, null);
			if (value != null) {
				return fullKey;
			}
		}
		return getPreKey(
				databaseInfo == null ? null : databaseInfo.getServerInfo(), key);
	}

	/**
	 * 
	 * Get query character set from preference,if databaseInfo is null,return
	 * system charset
	 * 
	 * @param databaseInfo
	 *            the database information
	 * @return the charset
	 */
	public static String getCharset(DatabaseInfo databaseInfo) {
		String key = getPreKey(databaseInfo, QueryOptions.CHAR_SET);
		String defaultCharset = "UTF-8";
		String charset = pref.get(key, defaultCharset);
		if (charset == null || charset.trim().length() <= 0) {
			charset = defaultCharset;
		}
		return charset;
	}

	/**
	 * 
	 * Set the charSet of the database to preference
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo object
	 * @param charSet
	 *            the charSet
	 */
	public static void setCharset(DatabaseInfo databaseInfo, String charSet) {
		if (databaseInfo != null && charSet != null) {
			databaseInfo.setCharSet(charSet);
		}
		String prefix = getPrefix(databaseInfo);
		if (charSet != null) {
			pref.put(prefix + QueryOptions.CHAR_SET, charSet);
		}
	}

	/**
	 * 
	 * Get charSet of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return the charSet
	 */
	public static String getCharset(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.CHAR_SET);
		String defaultCharset = "UTF-8";
		String charset = pref.get(key, defaultCharset);
		if (charset == null || charset.trim().length() <= 0) {
			charset = defaultCharset;
		}
		return charset;
	}

	/**
	 * 
	 * Set the charSet of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param charSet
	 *            the charSet
	 */
	public static void setCharset(ServerInfo serverInfo, String charSet) {
		String prefix = getPrefix(serverInfo);
		if (charSet != null) {
			pref.put(prefix + QueryOptions.CHAR_SET, charSet);
		}
		if (serverInfo == null) {
			Iterator<Map.Entry<String, ServerInfo>> serverInfos = ServerManager.getAllServerInfos().entrySet().iterator(); 
			while(serverInfos.hasNext()){
				changeCharset(serverInfos.next().getValue());
			}
		} else {
			changeCharset(serverInfo);
		}
	}

	/**
	 * 
	 * Change the all database charset in this server
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 */
	private static void changeCharset(ServerInfo serverInfo) {
		List<DatabaseInfo> dbInfoList = serverInfo.isConnected() ? serverInfo
				.getLoginedUserInfo().getDatabaseInfoList() : null;
		for (int i = 0; dbInfoList != null && i < dbInfoList.size(); i++) {
			DatabaseInfo dbInfo = dbInfoList.get(i);
			String charSet = getCharset(dbInfo);
			dbInfo.setCharSet(charSet);
		}
	}

	/**
	 * 
	 * Get the oid enabled status of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return <code>true</code> if enabled;<code>false</code>otherwise
	 */
	public static boolean getEnableOidInfo(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.ENABLE_GET_OID);
		return pref.getBoolean(key, false);
	}

	/**
	 * Set the oid enabled status of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param isEnabled
	 *            whether it is enabled
	 */
	public static void setEnableOidInfo(ServerInfo serverInfo, boolean isEnabled) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.ENABLE_GET_OID, isEnabled);
	}

	/**
	 * 
	 * Get page limit of this server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return the limit
	 */
	public static int getPageLimit(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.PAGE_INSTANCES_COUNT);
		int limit = pref.getInt(key, DEFAULT_MAX_PAGE_LIMIT);
		return limit <= 0 ? DEFAULT_MAX_PAGE_LIMIT : limit;
	}

	/**
	 * 
	 * Set page limit of this server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param limit
	 *            the limit
	 */
	public static void setPageLimit(ServerInfo serverInfo, int limit) {
		String prefix = getPrefix(serverInfo);
		pref.putInt(prefix + QueryOptions.PAGE_INSTANCES_COUNT, limit);
	}

	/**
	 * 
	 * Get broker IP of the database from preference
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo object
	 * @return the ip
	 */
	public static String getBrokerIp(DatabaseInfo databaseInfo) {
		ServerUserInfo userInfo = databaseInfo.getServerInfo()
				.getLoginedUserInfo();
		String brokerIP = null;
		if (userInfo != null && userInfo.isAdmin()) {
			String prefix = getPrefix(databaseInfo);
			String ip = pref.get(prefix + QueryOptions.BROKER_IP, "");
			if (ip != null && !ip.equals("")) {
				brokerIP = ip;
			}
			if (brokerIP == null) {
				brokerIP = databaseInfo.getServerInfo().getHostAddress();
			}
		}
		if (brokerIP == null) {
			brokerIP = databaseInfo.getBrokerIP();
		}
		return brokerIP;
	}

	/**
	 * 
	 * Set broker IP of the database to preference
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo object
	 * @param ip
	 *            the ip
	 */
	public static void setBrokerIp(DatabaseInfo databaseInfo, String ip) {
		if (databaseInfo != null && databaseInfo.getServerInfo() != null
				&& databaseInfo.getServerInfo().getLoginedUserInfo() != null
				&& databaseInfo.getServerInfo().getLoginedUserInfo().isAdmin()
				&& ip != null) {
			databaseInfo.setBrokerIP(ip);
		}
		String prefix = getPrefix(databaseInfo);
		if (ip != null) {
			pref.put(prefix + QueryOptions.BROKER_IP, ip);
		}
	}

	/**
	 * get broker port for current user when admin login, use the item selected
	 * in host property page when un-admin user login, use the default broker
	 * port in database info.
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo object
	 * @return the ip
	 */
	public static String getBrokerPort(DatabaseInfo databaseInfo) {
		ServerUserInfo userInfo = databaseInfo.getServerInfo()
				.getLoginedUserInfo();
		String portInfo = null;
		if (userInfo != null && userInfo.isAdmin()) {
			String prefix = getPrefix(databaseInfo);
			String port = pref.get(prefix + QueryOptions.BROKER_PORT, "");
			if (port != null && !port.equals("")) {
				portInfo = port;
			}
		}
		if (portInfo == null) {
			portInfo = databaseInfo.getBrokerPort();
		}
		return portInfo;
	}

	/**
	 * 
	 * Set broker port of this database to preference
	 * 
	 * @param databaseInfo
	 *            the DatabaseInfo object
	 * @param port
	 *            the port
	 */
	public static void setBrokerPort(DatabaseInfo databaseInfo, String port) {
		if (databaseInfo != null && databaseInfo.getServerInfo() != null
				&& databaseInfo.getServerInfo().getLoginedUserInfo() != null
				&& databaseInfo.getServerInfo().getLoginedUserInfo().isAdmin()
				&& port != null) {
			databaseInfo.setBrokerPort(port);
		}
		String prefix = getPrefix(databaseInfo);
		if (port != null) {
			pref.put(prefix + QueryOptions.BROKER_PORT, port);
		}
	}

	/**
	 * 
	 * Get auto commit status of this server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return <code>true</code> if can auto commit;<code>false</code>otherwise
	 */
	public static boolean getAutoCommit(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.AUTO_COMMIT);
		return pref.getBoolean(key, true);
	}

	/**
	 * 
	 * Set auto commit status of this server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param isAutoCommit
	 *            whether auto commit
	 */
	public static void setAutoCommit(ServerInfo serverInfo, boolean isAutoCommit) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.AUTO_COMMIT, isAutoCommit);
	}

	/**
	 * 
	 * Get search unit enable status of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return <code>true</code> if enabled;<code>false</code>otherwise
	 */
	public static boolean getEnableSearchUnit(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.ENABLE_UNIT_INSTANCES);
		return pref.getBoolean(key, true);
	}

	/**
	 * 
	 * Set search unit enable status of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param isEnabled
	 *            whether enabled
	 */
	public static void setEnableSearchUnit(ServerInfo serverInfo,
			boolean isEnabled) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.ENABLE_UNIT_INSTANCES, isEnabled);
	}

	/**
	 * 
	 * Get search unit of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return the search unit
	 */
	public static int getSearchUnitCount(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.UNIT_INSTANCES_COUNT);
		int pageCount = pref.getInt(key, DEFAULT_MAX_RECORD_LIMIT);
		if (pageCount <= 0) {
			pageCount = QueryOptions.DEFAULT_MAX_RECORD_LIMIT;
		}
		return pageCount;
	}

	/**
	 * 
	 * Set search unit of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param limit
	 *            the limit
	 */
	public static void setSearchUnitCount(ServerInfo serverInfo, int limit) {
		String prefix = getPrefix(serverInfo);
		pref.putInt(prefix + QueryOptions.UNIT_INSTANCES_COUNT, limit);
	}

	/**
	 * 
	 * Get query plan is use tree model
	 * 
	 * @return <code>true</code> if enabled;<code>false</code>otherwise
	 */
	public static int getQueryPlanDisplayType() {
		return pref.getInt(QueryOptions.DISPLAY_TYPE_QUERY_PLAN,
				DEFAULT_QUERY_PLAN_DISPLAY_MODEL);
	}

	/**
	 * 
	 * Set query plan is use tree model
	 * 
	 * @param isEnabled
	 *            whether enabled
	 */
	public static void setQueryPlanDisplayType(int displayType) {
		pref.putInt(QueryOptions.DISPLAY_TYPE_QUERY_PLAN, displayType);
	}

	/**
	 * 
	 * Get font color read of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return the font color red
	 */
	public static int getFontColorRed(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.FONT_RGB_RED);
		return pref.getInt(key, 0);
	}

	/**
	 * 
	 * Set font color red of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param colorRed
	 *            the value
	 */
	public static void setFontColorRed(ServerInfo serverInfo, int colorRed) {
		String prefix = getPrefix(serverInfo);
		if (colorRed >= 0) {
			pref.putInt(prefix + QueryOptions.FONT_RGB_RED, colorRed);
		}
	}

	/**
	 * 
	 * Get font color green of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return the font name
	 */
	public static int getFontColorGreen(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.FONT_RGB_GREEN);
		return pref.getInt(key, 0);
	}

	/**
	 * 
	 * Set font color green of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param colorGreen
	 *            the value
	 */
	public static void setFontColorGreen(ServerInfo serverInfo, int colorGreen) {
		String prefix = getPrefix(serverInfo);
		if (colorGreen >= 0) {
			pref.putInt(prefix + QueryOptions.FONT_RGB_GREEN, colorGreen);
		}
	}

	/**
	 * 
	 * Get font color blue of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return the font color blue
	 */
	public static int getFontColorBlue(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.FONT_RGB_BLUE);
		return pref.getInt(key, 0);
	}

	/**
	 * 
	 * Set font color blue of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param colorBlue
	 *            the value
	 */
	public static void setFontColorBlue(ServerInfo serverInfo, int colorBlue) {
		String prefix = getPrefix(serverInfo);
		if (colorBlue >= 0) {
			pref.putInt(prefix + QueryOptions.FONT_RGB_BLUE, colorBlue);
		}
	}

	/**
	 * 
	 * Get font string of the server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return the font string
	 */
	public static String getFontString(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.FONT_STRING);
		String str = pref.get(key, "");
		if (str != null && str.length() > 0) {
			return str;
		}
		FontData[] fData = Display.getDefault().getSystemFont().getFontData();
		if (fData != null && fData.length > 0) {
			return fData[0].toString();
		}
		return null;
	}

	/**
	 * 
	 * Set font string of the server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param fontStr
	 *            the font string
	 */
	public static void setFontString(ServerInfo serverInfo, String fontStr) {
		String prefix = getPrefix(serverInfo);
		pref.put(prefix + QueryOptions.FONT_STRING, fontStr);
	}

	/**
	 * 
	 * Get default font
	 * 
	 * @return the font information array {name,style,size}
	 */
	public static String[] getDefaultFont() {
		String fontName = "";
		int fontStyle = 0;
		int fontSize = 10;
		return new String[] { fontName, String.valueOf(fontSize),
				String.valueOf(fontStyle) };
	}

	/**
	 * 
	 * Get font color information
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return the font color array {red,green,blue}
	 */
	public static int[] getFontColor(ServerInfo serverInfo) {
		int red = getFontColorRed(serverInfo);
		int green = getFontColorGreen(serverInfo);
		int blue = getFontColorBlue(serverInfo);
		return new int[] { red, green, blue };
	}

	/**
	 * 
	 * Get show style for query editor
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @return <code>true</code> if vertically;<code>true</code>otherwise
	 */
	public static boolean getShowStyle(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.SHOW_STYLE);
		return pref.getBoolean(key, true);
	}

	/**
	 * 
	 * Set show style for qeury editor
	 * 
	 * @param serverInfo
	 *            the ServerInfo Object
	 * @param isVertical
	 *            whether isVertical
	 */
	public static void setShowStyle(ServerInfo serverInfo, boolean isVertical) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.SHOW_STYLE, isVertical);
	}

	/**
	 * Get all the status monitor key related to the given instance of
	 * ServerInfo
	 * 
	 * @param serverInfo
	 *            the given instance of ServerInfo
	 * @return the string array
	 */
	public static String[] getAllStatusMonitorKey(ServerInfo serverInfo) {
		List<String> list = new ArrayList<String>();
		if (serverInfo == null) {
			return null;
		}
		String prefix = getPrefix(serverInfo);
		if (prefix == null || prefix.trim().length() <= 0) {
			return null;
		}
		prefix += MONITOR_FOLDER_NAME;
		try {
			String[] keys = pref.keys();
			for (String key : keys) {
				if (key != null && key.trim().startsWith(prefix)) {
					list.add(key);
				}
			}
		} catch (BackingStoreException e) {
			LOGGER.error("", e);
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * 
	 * Get keyword/functions lowercase status of this server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return boolean
	 */
	public static boolean getKeywordLowercase(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.KEYWORD_LOWERCASE);
		return pref.getBoolean(key, false);
	}

	/**
	 * 
	 * Set keyword/functions lowercase status of this server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param isLowercase
	 *            whether lowercase
	 */
	public static void setKeywordLowercase(ServerInfo serverInfo,
			boolean isLowercase) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.KEYWORD_LOWERCASE, isLowercase);
	}

	/**
	 * 
	 * Get no use keyword/functions uppercase automatically of this server from
	 * preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return boolean
	 */
	public static boolean getNoAutoUppercaseKeyword(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo,
				QueryOptions.KEYWORD_NO_AUTO_UPPERCASE);
		return pref.getBoolean(key, false);
	}

	/**
	 * 
	 * Set no use keyword/functions uppercase automatically of this server to
	 * preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param isDontUse
	 *            whether don't use this option
	 */
	public static void setNoAutoUppercaseKeyword(ServerInfo serverInfo,
			boolean isDontUse) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.KEYWORD_NO_AUTO_UPPERCASE,
				isDontUse);
	}

	/**
	 * 
	 * Get without prompt save of this server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return boolean
	 */
	public static boolean getWithoutPromptSave(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.WITHOUT_PROMPT_SAVE);
		return pref.getBoolean(key, true);
	}

	/**
	 * 
	 * Set without prompt save status of this server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param isSave
	 *            whether save
	 */
	public static void setWithoutPromptSave(ServerInfo serverInfo,
			boolean isSave) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.WITHOUT_PROMPT_SAVE, isSave);
	}

	/**
	 * Get is use scientific notation for query result
	 * 
	 * @param serverInfo
	 * @return
	 */
	public static boolean getUseScientificNotation(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.USE_SCIENTIFIC_NOTATION);
		return pref.getBoolean(key, true);
	}

	/**
	 * Set is use scientific notation for query result
	 * 
	 * @param serverInfo
	 * @param isUse
	 */
	public static void setUseScientificNotation(ServerInfo serverInfo,
			boolean isUse) {
		String prefix = getPrefix(serverInfo);
		pref.putBoolean(prefix + QueryOptions.USE_SCIENTIFIC_NOTATION, isUse);
	}

	/**
	 * Get lob data load size
	 * 
	 * @param serverInfo
	 * @return
	 */
	public static int getLobLoadSize(ServerInfo serverInfo) {
		String key = getPreKey(serverInfo, QueryOptions.LOB_LOAD_SIZE);
		return pref.getInt(key, 256);
	}

	/**
	 * Set lob data load size
	 * 
	 * @param serverInfo
	 * @param size
	 */
	public static void setLobLoadSize(ServerInfo serverInfo, int size) {
		String prefix = getPrefix(serverInfo);
		pref.putInt(prefix + QueryOptions.LOB_LOAD_SIZE, size);
	}

	/**
	 * 
	 * Get show confirmation prompt status of this server from preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @return boolean
	 */
	public static boolean getMultiPageConfirm() {
		return pref.getBoolean(QueryOptions.MULTI_PAGE_CONFIRM, true);
	}

	/**
	 * 
	 * Set show confirmation prompt status of this server to preference
	 * 
	 * @param serverInfo
	 *            the ServerInfo object
	 * @param isSave
	 *            whether save
	 */
	public static void setMultiPageConfirm(boolean useConfirm) {
		pref.putBoolean(QueryOptions.MULTI_PAGE_CONFIRM, useConfirm);
	}

	/**
	 * 
	 * Load added host from file preference to be compatible for the version
	 * before 8.4.0
	 * 
	 * @param optionPath
	 *            String
	 * @param serverInfo
	 *            ServerInfo
	 * 
	 */
	public static void load(String optionPath, ServerInfo serverInfo) {
		String newPrefix = getPrefix(serverInfo) + ".";

		String[] filePaths = {
				optionPath + File.separator
						+ "com.cubrid.cubridmanager.core.prefs",
				optionPath + File.separator
						+ "com.cubrid.cubridmanager.ui.prefs",
				optionPath + File.separator + "com.cubrid.common.ui.prefs" };

		for (String filePath : filePaths) {
			File file = new File(filePath);
			if (!file.exists()) {
				continue;
			}
			PreferenceStore preference = new PreferenceStore(filePath);
			try {
				preference.load();

				String[] supportedPrefixs = { "" };
				if (serverInfo != null) {
					String prefix1 = serverInfo.getHostAddress();
					String prefix2 = prefix1 + "."
							+ serverInfo.getHostMonPort();
					String prefix3 = prefix2 + "." + serverInfo.getUserName();
					String prefix4 = serverInfo.getServerName();
					supportedPrefixs = new String[] { prefix3, prefix2,
							prefix1, prefix4 };
				}

				String[] keys = preference.preferenceNames();
				for (String key : keys) {
					for (String prefix : supportedPrefixs) {
						if (key.trim().startsWith(prefix + ".")) {
							String newKey = key.replaceAll(prefix + "\\.",
									newPrefix);
							if (pref.get(newKey, null) == null) {
								pref.put(newKey, preference.getString(key));
							}
							break;
						}
					}
				}
				savePref();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	/**
	 * Save the DatabaseEditorConfig for the database
	 * 
	 * @param database
	 *            - CubridDatabase
	 */
	public static void putEditorConfig(CubridDatabase database,
			DatabaseEditorConfig editorConfig, boolean inCMMode) {
		if (database == null) {
			return;
		}
		
		String key = getDBMapKey(database, inCMMode);
		putEditorConfig(key, editorConfig);
	}

	/**
	 * Save the DatabaseEditorConfig for the database
	 * 
	 * @param dbUser
	 *            - String
	 * @param dbName
	 *            - String
	 * @param address
	 *            - String
	 * @param port
	 *            - String
	 * @param config
	 *            - DatabaseEditorConfig
	 */
	public static void putEditorConfig(String dbUser, String dbName,
			String address, String port, String serverName,
			DatabaseEditorConfig editorConfig, boolean isCMMode) {

		String key = getDBMapKey(dbUser, dbName, address, port, serverName, isCMMode);
		putEditorConfig(key, editorConfig);
	}

	/**
	 * Save the DatabaseEditorConfig for the database
	 * 
	 * @param key
	 *            - String
	 * @param config
	 *            - DatabaseEditorConfig
	 */
	private static void putEditorConfig(String key, DatabaseEditorConfig config) {
		if (config == null) {
			editorConfigMap.put(key, null);
		} else {
			editorConfigMap.put(key, config.clone());
		}
	}

	/**
	 * Get the DatabaseEditorConfig for the database
	 * 
	 * @param database
	 *            - CubridDatabase
	 * @return DatabaseEditorConfig
	 */
	public static DatabaseEditorConfig getEditorConfig(CubridDatabase database, boolean inCMMode) {
		if (database == null) {
			return null;
		}

		String key = getDBMapKey(database, inCMMode);
		return getEditorConfig(key);
	}

	/**
	 * Get the DatabaseEditorConfig for the database
	 * 
	 * @param dbUser
	 *            - String
	 * @param dbName
	 *            - String
	 * @param address
	 *            - String
	 * @param port
	 *            - String
	 * @return DatabaseEditorConfig
	 */
	public static DatabaseEditorConfig getEditorConfig(String dbUser,
			String dbName, String address, String port, String connectionName, boolean isCMMode) {
		String key = getDBMapKey(dbUser, dbName, address, port, connectionName, isCMMode);

		return getEditorConfig(key);
	}

	/**
	 * Get the DatabaseEditorConfig for the database
	 * 
	 * @param key
	 *            - String
	 * @return DatabaseEditorConfig
	 */
	private static DatabaseEditorConfig getEditorConfig(String key) {
		DatabaseEditorConfig editorConfig = editorConfigMap.get(key);
		if (editorConfig != null) {
			return editorConfig.clone();
		}
		return null;
	}

	/**
	 * Get the map key for the database
	 * 
	 * @param dbUser
	 *            - String
	 * @param dbName
	 *            - String
	 * @param address
	 *            - String
	 * @param port
	 *            - String
	 * @param serverName
	 *            - String
	 * @return String
	 */
	private static String getDBMapKey(String dbUser, String dbName,
			String address, String port, String serverName , boolean isCMMode) {
		StringBuffer sb = new StringBuffer();
		sb.append(dbUser);
		sb.append("@");
		sb.append(dbName);
		sb.append("@");
		sb.append(address);
		sb.append("@");
		sb.append(port);
		if (!isCMMode && serverName != null) {
			sb.append("@");
			sb.append(serverName);
		}
		sb.append("@").append(String.valueOf(isCMMode));
		
		return sb.toString();
	}

	/**
	 * Get the map key for the database
	 * 
	 * @param database
	 *            - CubridDatabase
	 * @return String
	 */
	public static String getDBMapKey(CubridDatabase database, boolean isCMMode) {
		String dbUser = "";
		String dbName = "";
		String address = "";
		String port = "";
		String serverName = "";

		if (database != null && database.getUserName() != null) {
			dbUser = database.getUserName();
		}

		if (database != null && database.getDatabaseInfo() != null
				&& database.getDatabaseInfo().getDbName() != null) {
			dbName = database.getDatabaseInfo().getDbName();
		}

		if (database != null) {
			if (isCMMode) {
				dbUser = database.getUserName();
				dbName = database.getName();
				if (database.getServer() != null && database.getServer().getHostAddress() != null) {
					address = database.getServer().getHostAddress();
				}
				if (database.getServer() != null && database.getServer().getMonPort() != null) {
					port = database.getServer().getMonPort();
				}
			} else {
				DatabaseInfo dbInfo = database.getDatabaseInfo();
				if (dbInfo != null && dbInfo.getBrokerIP() != null) {
					address = dbInfo.getBrokerIP();
				}
				if (dbInfo != null && dbInfo.getBrokerPort() != null) {
					port = dbInfo.getBrokerPort();
				}

				CubridServer server = database.getServer();
				if (server != null && server.getServerName() != null) {
					serverName = server.getServerName();
				}
			}
		}

		return getDBMapKey(dbUser, dbName, address, port, serverName, isCMMode);
	}
}
