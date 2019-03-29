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
package com.cubrid.common.ui.common.preference;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;

/**
 * 
 * General preference store the value from general preference page
 * 
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public final class GeneralPreference {

	private static final Logger LOGGER = LogUtil.getLogger(GeneralPreference.class);
	public static final String MAXIMIZE_WINDOW_ON_START_UP = ".maximize_window_on_start_up";
	public static final String CHECK_NEW_INFO_ON_START_UP = ".check_new_information_on_start_up";
	public static final String USE_CLICK_SINGLE = ".use_click_single";
	public static final String IS_ALWAYS_EXIT = ".is_always_exit";
	public static final String IS_AUTO_CHECK_UPDATE = ".is_auto_check_update";
	public static final String IS_RUN_IN_DEBUG_MODE = ".is_run_in_debug";
	public static final String AUTO_COMPLETE_KEYWORD = ".auto_complete_keyword";
	public static final String AUTO_COMPLETE_TABLESORCOLUMNS = ".auto_complete_tablesorcolumns";
	public static final String AUTO_SHOW_SCHEMA_INFO = ".auto_show_schema_info";
	public static final String ALERT_MULTIQUERY_ALWAYS_CONFIRM = ".alert_multiquery_always_confirm";
	public static final String USE_HOST_DASHBOARD = ".use_host_dashboard";
	public static final String USE_DATABASE_DASHBOARD = ".use_database_dashboard";
	public static final String ALWAYS = MessageDialogWithToggle.ALWAYS;
	public static final String PROMPT = MessageDialogWithToggle.PROMPT;
	public static final String SHOW_ALERT_RUN_MODIFIED_QUERIES_AUTOCOMMIT = ".show_alert_when_run_ddl_dml_queries_on_autocommit";
	public static final String EXTERNAL_CMT_PATH = ".external_cmt_path";
	public static final String EXTERNAL_CM_PATH = ".external_cm_path";
	public static final String EXTERNAL_CQB_PATH = ".external_cqb_path";
	public static final String MAX_QUERY_TUNER_WINDOW=".max_query_tuner_window";
	public static final String TABLES_FETCH_SIZE = ".tables_fetch_size";
	private static IPreferenceStore pref = null;

	static {
		pref = CommonUIPlugin.getDefault().getPreferenceStore();
		pref.setDefault(CHECK_NEW_INFO_ON_START_UP, true);
		pref.setDefault(IS_AUTO_CHECK_UPDATE, PROMPT);
		pref.setDefault(USE_CLICK_SINGLE, false);
		pref.setDefault(AUTO_COMPLETE_KEYWORD, true);
		pref.setDefault(AUTO_COMPLETE_TABLESORCOLUMNS, true);
		pref.setDefault(AUTO_SHOW_SCHEMA_INFO, true);
		pref.setDefault(ALERT_MULTIQUERY_ALWAYS_CONFIRM, false);
		pref.setDefault(USE_HOST_DASHBOARD, true);
		pref.setDefault(USE_DATABASE_DASHBOARD, true);
		pref.setDefault(SHOW_ALERT_RUN_MODIFIED_QUERIES_AUTOCOMMIT, true);
		pref.setDefault(TABLES_FETCH_SIZE, "1000");
	}

	/**
	 * The constructor
	 */
	private GeneralPreference() {
		//empty
	}

	/**
	 * Return whether window is maximized when start up
	 * 
	 * @return <code>true</code>if maximize;<code>false</code> otherwise
	 */
	public static boolean isMaximizeWindowOnStartUp() {
		try {
			return pref.getBoolean(MAXIMIZE_WINDOW_ON_START_UP);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Set whether window is maximized when start up
	 * 
	 * @param isMax boolean
	 */
	public static void setMaximizeWindowOnStartUp(boolean isMax) {
		try {
			pref.setValue(GeneralPreference.MAXIMIZE_WINDOW_ON_START_UP, isMax);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * is Max Query Tuner window
	 * 
	 * @return
	 */
	public static boolean isMaxQueryTunerWindow() {
		try {
			return pref.getBoolean(MAX_QUERY_TUNER_WINDOW);
		} catch (Exception ignored) {
			return false;
		}
	}
	
	/**
	 * Set is max query tuner window
	 * 
	 * @param isMax
	 */
	public static void setMaxQueryTunerWindow(boolean isMax) {
		try {
			pref.setValue(GeneralPreference.MAX_QUERY_TUNER_WINDOW, isMax);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}
	/**
	 * Return whether show confirm dialog when running DDL/DML on auto commit mode.
	 * 
	 * @return <code>true</code>if maximize;<code>false</code> otherwise
	 */
	public static boolean isShowAlertModifiedQueryOnAutoCommit() {
		try {
			return false; // TODO now it will not be activated.
			//return pref.getBoolean(SHOW_ALERT_RUN_MODIFIED_QUERIES_AUTOCOMMIT);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Set whether show confirm dialog when running DDL/DML on auto commit mode.
	 * 
	 * @param isShow boolean
	 */
	public static void setShowAlertModifiedQueryOnAutoCommit(boolean isShow) {
		try {
			pref.setValue(GeneralPreference.SHOW_ALERT_RUN_MODIFIED_QUERIES_AUTOCOMMIT, isShow);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * 
	 * Return whether check new information of CUBRID
	 * 
	 * @return <code>true</code>if check;<code>false</code> otherwise
	 */
	public static boolean isCheckNewInfoOnStartUp() {
		try {
			return pref.getBoolean(CHECK_NEW_INFO_ON_START_UP);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * 
	 * Return whether auto complete keyword on query editor
	 * 
	 * @return <code>true</code>if check;<code>false</code> otherwise
	 */
	public static boolean isAutoCompleteKeyword() {
		try {
			return pref.getBoolean(AUTO_COMPLETE_KEYWORD);
		} catch (Exception ignored) {
			return false;
		}
	}
	
	/**
	 * 
	 * Return whether auto complete tablesOrColumns on query editor
	 * 
	 * @return <code>true</code>if check;<code>false</code> otherwise
	 */
	public static boolean isAutoCompleteTablesOrColumns() {
		try {
			return pref.getBoolean(AUTO_COMPLETE_TABLESORCOLUMNS);
		} catch (Exception ignored) {
			return false;
		}
	}

	public static boolean isAutoShowSchemaInfo() {
		try {
			return pref.getBoolean(AUTO_SHOW_SCHEMA_INFO);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * 
	 * Set whether check new information of CUBRID
	 * 
	 * @param isShowWelcomePage boolean
	 */
	public static void setCheckNewInfoOnStartUp(boolean isShowWelcomePage) {
		try {
			pref.setValue(GeneralPreference.CHECK_NEW_INFO_ON_START_UP,
					isShowWelcomePage);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * 
	 * Return whether always exit
	 * 
	 * @return <code>true</code>if always exit;<code>false</code> otherwise
	 */
	public static boolean isAlwaysExit() {
		try {
			return ALWAYS.equals(pref.getString(IS_ALWAYS_EXIT));
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * 
	 * Set whether always exit
	 * 
	 * @param isAlwaysExit boolean
	 */
	public static void setAlwaysExit(boolean isAlwaysExit) {
		try {
			pref.setValue(GeneralPreference.IS_ALWAYS_EXIT,
					isAlwaysExit ? GeneralPreference.ALWAYS
							: GeneralPreference.PROMPT);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * 
	 * Return whether auto check update when start
	 * 
	 * @return <code>true</code>if user click once;<code>false</code> otherwise
	 */
	public static boolean isAutoCheckUpdate() {
		try {
			return ALWAYS.equals(pref.getString(IS_AUTO_CHECK_UPDATE));
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * 
	 * Set whether auto check update when start
	 * 
	 * @param isAutoCheckUpdate boolean
	 */
	public static void setAutoCheckUpdate(boolean isAutoCheckUpdate) {
		try {
			pref.setValue(GeneralPreference.IS_AUTO_CHECK_UPDATE,
					isAutoCheckUpdate ? GeneralPreference.ALWAYS
							: GeneralPreference.PROMPT);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * 
	 * Set whether auto complete keyword
	 * 
	 * @param isAutoCompleteKeyword boolean
	 */
	public static void setAutoCompleteKeyword(boolean isAutoCompleteKeyword) {
		try {
			pref.setValue(GeneralPreference.AUTO_COMPLETE_KEYWORD,
					isAutoCompleteKeyword);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}
	
	/**
	 * 
	 * Set whether auto complete tablesOrColumns
	 * 
	 * @param isAutoCompleteTablesOrColumns boolean
	 */
	public static void setAutoCompleteTablesOrColumns(boolean isAutoCompleteTablesOrColumns) {
		try {
			pref.setValue(GeneralPreference.AUTO_COMPLETE_TABLESORCOLUMNS,
					isAutoCompleteTablesOrColumns);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	public static void setAutoShowSchemaInfo(boolean autoShowSchemaInfo) {
		try {
			pref.setValue(GeneralPreference.AUTO_SHOW_SCHEMA_INFO,
					autoShowSchemaInfo);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * Return whether always confirm automatically if it run the multiple query.
	 * @return
	 */
	public static boolean isAlertMultiQueryAlwaysConfirm() {
		try {
			return pref.getBoolean(ALERT_MULTIQUERY_ALWAYS_CONFIRM);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Set whether always confirm automatically if it run the multiple query.
	 * @return
	 */
	public static void setAlertMultiQueryAlwaysConfirm(boolean alwaysConfirm) {
		try {
			pref.setValue(GeneralPreference.ALERT_MULTIQUERY_ALWAYS_CONFIRM, alwaysConfirm);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}
	
	/**
	 * Return true by using host dashboard
	 * @return
	 */
	public static boolean isUseHostDashboard() {
		try {
			return pref.getBoolean(USE_HOST_DASHBOARD);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Set whether use the host dashboard.
	 * @return
	 */
	public static void setUseHostDashboard(boolean use) {
		try {
			pref.setValue(GeneralPreference.USE_HOST_DASHBOARD, use);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	/**
	 * Return true by using host dashboard
	 * @return
	 */
	public static boolean isUseDatabaseDashboard() {
		try {
			return pref.getBoolean(USE_DATABASE_DASHBOARD);
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Set whether use the host dashboard.
	 * @return
	 */
	public static void setUseDatabaseDashboard(boolean use) {
		try {
			pref.setValue(GeneralPreference.USE_DATABASE_DASHBOARD, use);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	public static String getExternalMigrationToolkitPath() {
		try {
			return pref.getString(EXTERNAL_CMT_PATH);
		} catch (Exception ignored) {
			return null;
		}
	}

	public static void setExternalMigrationToolkitPath(String path) {
		try {
			pref.setValue(GeneralPreference.EXTERNAL_CMT_PATH, path);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}

	public static String getExternalManagerPath() {
		try {
			return pref.getString(EXTERNAL_CM_PATH);
		} catch (Exception ignored) {
			return null;
		}
	}

	public static void setExternalManagerPath(String path) {
		try {
			pref.setValue(GeneralPreference.EXTERNAL_CM_PATH, path);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}
	
	public static String getExternalBrowserPath() {
		try {
			return pref.getString(EXTERNAL_CQB_PATH);
		} catch (Exception ignored) {
			return null;
		}
	}

	public static void setExternalBrowserPath(String path) {
		try {
			pref.setValue(GeneralPreference.EXTERNAL_CQB_PATH, path);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}
	
	public static String getTablesFetchSize() {
		try {
			return pref.getString(TABLES_FETCH_SIZE);
		} catch (Exception ignored) {
			return null;
		}
	}

	public static void setTablesFetchSize(String tablesFetchSize) {
		try {
			pref.setValue(GeneralPreference.TABLES_FETCH_SIZE, tablesFetchSize);
		} catch (Exception ignored) {
			LOGGER.error(ignored.getMessage());
		}
	}
}
