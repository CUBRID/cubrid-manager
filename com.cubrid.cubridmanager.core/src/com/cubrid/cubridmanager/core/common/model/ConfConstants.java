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
package com.cubrid.cubridmanager.core.common.model;

import com.cubrid.common.core.util.CompatibleUtil;

/**
 *
 * CUBRID Configuration parameter constants for cubrid.conf and cm.conf and
 * cubrid_broker.conf
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class ConfConstants implements
		CubridConfParaConstants,
		CubridBrokerConfParaConstants,
		CubridManagerConfParaConstants,
		HAConfParaConstants {

	public static final String DEFAULT_DATA_VOLUME_SIZE = "512";

	private static String[][] dbBaseParameters = {
			{DATA_BUFFER_PAGES, "25000", PARAMETER_TYPE_SERVER },
			{DATA_BUFFER_SIZE, "512M", PARAMETER_TYPE_SERVER },
			{SORT_BUFFER_PAGES, "16", PARAMETER_TYPE_SERVER },
			{SORT_BUFFER_SIZE, "2M", PARAMETER_TYPE_SERVER },
			{LOG_BUFFER_PAGES, "50", PARAMETER_TYPE_SERVER },
			{LOG_BUFFER_SIZE, "4M", PARAMETER_TYPE_SERVER },
			{LOCK_ESCALATION, "100000", PARAMETER_TYPE_SERVER },
			{LOCK_TIMEOUT_IN_SECS, "-1", PARAMETER_TYPE_SERVER },
			{DEADLOCK_DETECTION_INTERVAL_IN_SECS, "1", PARAMETER_TYPE_SERVER },
			{CHECKPOINT_INTERVAL_IN_MINS, "1000", PARAMETER_TYPE_SERVER },
			{ISOLATION_LEVEL, "\"" + TRAN_REP_CLASS_UNCOMMIT_INSTANCE + "\"",
					PARAMETER_TYPE_SERVER },
			{CUBRID_PORT_ID, "1523", PARAMETER_TYPE_CLIENT },
			{MAX_CLIENTS, "100", PARAMETER_TYPE_SERVER },
			{AUTO_RESTART_SERVER, "no", PARAMETER_TYPE_SERVER },
			{REPLICATION, "no", PARAMETER_TYPE_SERVER } };

	/**
	 * Get the static field dbBaseParameters
	 *
	 * @param serverInfo the ServerInfo
	 * @return String[][]
	 */
	public static String[][] getDbBaseParameters(ServerInfo serverInfo) {
		boolean isSupportSizes = CompatibleUtil.isSupportSizesPropInServer(serverInfo);
		String copy[][] = null;
		if (isSupportSizes) {
			copy = new String[dbBaseParameters.length][];
			for (int i = 0; i < dbBaseParameters.length; i++) {
				copy[i] = (String[]) dbBaseParameters[i].clone();
			}
		} else {
			copy = new String[dbBaseParameters.length - 3][];
			for (int i = 0, k = 0; i < dbBaseParameters.length; i++) {
				if (DATA_BUFFER_SIZE.equals(dbBaseParameters[i][0])
						|| SORT_BUFFER_SIZE.equals(dbBaseParameters[i][0])
						|| LOG_BUFFER_SIZE.equals(dbBaseParameters[i][0])) {
					continue;
				}
				if (MAX_CLIENTS.equals(dbBaseParameters[i][0])) {
					copy[k] = new String[3];
					copy[k][0] = MAX_CLIENTS;
					copy[k][1] = "50";
					copy[k][2] = dbBaseParameters[i][2];
					k++;
					continue;
				}
				copy[k] = (String[]) dbBaseParameters[i].clone();
				k++;
			}
		}
		return copy;
	}

	private static String[][] dbAdvancedParameters = {
			{ACCESS_IP_CONTROL, "bool(yes|no)", "no", PARAMETER_TYPE_SERVER },
			{ACCESS_IP_CONTROL_FILE, "string", "", PARAMETER_TYPE_SERVER },
			{ASYNC_COMMIT, "bool(yes|no)", "no", PARAMETER_TYPE_SERVER },
			{BACKUP_VOLUME_MAX_SIZE_BYTES, "int(v>=32*1024)", "-1",
					PARAMETER_TYPE_SERVER },
			{BLOCK_DDL_STATEMENT, "bool(yes|no)", "no", PARAMETER_TYPE_CLIENT },
			{BLOCK_NOWHERE_STATEMENT, "bool(yes|no)", "no",
					PARAMETER_TYPE_CLIENT },
			{CALL_STACK_DUMP_ACTIVATION_LIST, "string", "", PARAMETER_TYPE_BOTH },
			{CALL_STACK_DUMP_DEACTIVATION_LIST, "string", "",
					PARAMETER_TYPE_BOTH },
			{CALL_STACK_DUMP_ON_ERROR, "bool(yes|no)", "no",
					PARAMETER_TYPE_BOTH },
			{COMPACTDB_PAGE_RECLAIM_ONLY, "int", "0", PARAMETER_TYPE_UTILITY },
			{COMPAT_NUMERIC_DIVISION_SCALE, "bool(yes|no)", "no",
					PARAMETER_TYPE_BOTH },
			{COMPAT_PRIMARY_KEY, "bool(yes|no)", "no", PARAMETER_TYPE_CLIENT },
			{CSQL_HISTORY_NUM, "int(v>=1&&v<=200)", "50", PARAMETER_TYPE_CLIENT },
			{DB_HOSTS, "string", "", PARAMETER_TYPE_CLIENT },
			{DONT_REUSE_HEAP_FILE, "bool(yes|no)", "no", PARAMETER_TYPE_SERVER },
			{ERROR_LOG, "string", "cubrid.err", PARAMETER_TYPE_BOTH },
			{FILE_LOCK, "bool(yes|no)", "yes", PARAMETER_TYPE_SERVER },
			{GARBAGE_COLLECTION, "bool(yes|no)", "no", PARAMETER_TYPE_CLIENT },
			{GROUP_COMMIT_INTERVAL_IN_MSECS, "int(v>=0)", "0",
					PARAMETER_TYPE_SERVER },
			{HA_MODE, "string(on|off|yes|no)", "off", PARAMETER_TYPE_SERVER },
			{HA_NODE_LIST, "string", "", PARAMETER_TYPE_SERVER },
			{HA_PORT_ID, "int(v>=1024&&v<=65535)", "", PARAMETER_TYPE_SERVER },
			{HOSTVAR_LATE_BINDING, "bool(yes|no)", "no", PARAMETER_TYPE_CLIENT },
			{INDEX_SCAN_IN_OID_ORDER, "bool(yes|no)", "no",
					PARAMETER_TYPE_CLIENT },
			//if version>=8.2.2 float(v>=0.05&&v<=16);otherwise,int(v>=1&&v<=16)
			{INDEX_SCAN_OID_BUFFER_PAGES, "int(v>=1&&v<=16)", "4",
					PARAMETER_TYPE_SERVER },
			{INSERT_EXECUTION_MODE, "int(v>=1&&v<=7)", "1",
					PARAMETER_TYPE_CLIENT },
			{INTL_MBS_SUPPORT, "bool(yes|no)", "no", PARAMETER_TYPE_CLIENT },
			{LOCK_TIMEOUT_MESSAGE_TYPE, "int(v>=0&&v<=2)", "0",
					PARAMETER_TYPE_SERVER },
			{MAX_PLAN_CACHE_ENTRIES, "int", "1000", PARAMETER_TYPE_BOTH },
			{MAX_QUERY_CACHE_ENTRIES, "int", "-1", PARAMETER_TYPE_SERVER },
			{MEDIA_FAILURE_SUPPORT, "bool(yes|no)", "yes",
					PARAMETER_TYPE_SERVER },
			{ORACLE_STYLE_EMPTY_STRING, "bool(yes|no)", "no",
					PARAMETER_TYPE_CLIENT },
			{ORACLE_STYLE_OUTERJOIN, "bool(yes|no)", "no",
					PARAMETER_TYPE_CLIENT },
			{PTHREAD_SCOPE_PROCESS, "bool(yes|no)", "yes",
					PARAMETER_TYPE_SERVER },
			{QUERY_CACHE_MODE, "int(v>=0&&v<=2)", "0", PARAMETER_TYPE_SERVER },
			{QUERY_CACHE_SIZE_IN_PAGES, "int", "-1", PARAMETER_TYPE_SERVER },
			{SINGLE_BYTE_COMPARE, "bool(yes|no)", "no", PARAMETER_TYPE_SERVER },
			{TEMP_FILE_MAX_SIZE_IN_PAGES, "int", "-1", PARAMETER_TYPE_SERVER },
			{TEMP_FILE_MEMORY_SIZE_IN_PAGES, "int(v>=0&&v<=20)", "4",
					PARAMETER_TYPE_SERVER },
			{TEMP_VOLUME_PATH, "string", "", PARAMETER_TYPE_SERVER },
			{THREAD_STACK_SIZE, "int(v>=64*1024)", "100*1024",
					PARAMETER_TYPE_SERVER },
			{UNFILL_FACTOR, "float(v>=0&&v<=0.3)", "0.1", PARAMETER_TYPE_SERVER },
			{VOLUME_EXTENSION_PATH, "string", "", PARAMETER_TYPE_SERVER } };

	/**
	 *
	 * Get the static field dbAdvancedParameters
	 *
	 * @param serverInfo The ServerInfo object
	 * @param isCommon boolean
	 * @return String[][]
	 */
	public static String[][] getDbAdvancedParameters(ServerInfo serverInfo,
			boolean isCommon) {
		int length = dbAdvancedParameters.length;
		boolean isSupportNewDbPro = CompatibleUtil.isSupportParamNodeListAndPortId(serverInfo)
				&& isCommon;
		if (!isSupportNewDbPro) {
			length = length - 2;
		}

		boolean isSupportAccessIpControl = CompatibleUtil.isSupportAccessIpControl(serverInfo);
		if (!isSupportAccessIpControl) {
			length = length - 2;
		}
		String copy[][] = new String[length][];
		int j = 0;
		for (int i = 0; i < dbAdvancedParameters.length; i++) {
			if (!isSupportNewDbPro
					&& (dbAdvancedParameters[i][0].equals(ConfConstants.HA_NODE_LIST) || dbAdvancedParameters[i][0].equals(ConfConstants.HA_PORT_ID))) {
				continue;
			}
			if (!isSupportAccessIpControl
					&& (dbAdvancedParameters[i][0].equals(ConfConstants.ACCESS_IP_CONTROL) || dbAdvancedParameters[i][0].equals(ConfConstants.ACCESS_IP_CONTROL_FILE))) {
				continue;
			}
			copy[j] = (String[]) dbAdvancedParameters[i].clone();
			if (serverInfo != null
					&& dbAdvancedParameters[i][0].equals(INDEX_SCAN_OID_BUFFER_PAGES)) {
				if (CompatibleUtil.isSupportIndexScanOIDBufferSize(serverInfo)) {
					copy[j][0] = INDEX_SCAN_OID_BUFFER_SIZE;
					copy[j][1] = "int(v>=64*1024)";
					copy[j][2] = "64*1024";
					copy[j][3] = PARAMETER_TYPE_SERVER;
				} else {
					copy[j][1] = CompatibleUtil.getIndexScanOIDBufferPagesValueType(serverInfo);
				}

			} else if (serverInfo != null
					&& dbAdvancedParameters[i][0].equals(HA_MODE)) {
				copy[j][1] = CompatibleUtil.getHAModeValue(serverInfo);
			}
			j++;
		}
		return copy;
	}

	//HA parameters
	private static String[][] haConfParameters = {
			{HAConfParaConstants.HA_MODE, "string(on|off|yes|no|replica)",
					"off", PARAMETER_TYPE_SERVER },
			{HAConfParaConstants.HA_NODE_LIST, "string", "",
					PARAMETER_TYPE_SERVER },
			{HAConfParaConstants.HA_PORT_ID, "int(v>=1024&&v<=65535)", "59901",
					PARAMETER_TYPE_SERVER },
			{HA_REPLICA_LIST, "string", "", PARAMETER_TYPE_SERVER },
			{HA_PING_HOSTS, "string", "", PARAMETER_TYPE_SERVER },
			{HA_COPY_LOG_BASE, "string", "", PARAMETER_TYPE_SERVER },
			{HA_DB_LIST, "string", "", PARAMETER_TYPE_SERVER },
			{HA_APPLY_MAX_MEM_SIZE, "int", "0", PARAMETER_TYPE_SERVER },
			{HA_COPY_SYNC_MODE, "string", "", PARAMETER_TYPE_SERVER },
			{LOG_MAX_ARCHIVES, "int", "0", PARAMETER_TYPE_SERVER } };

	/**
	 * Get HA configuration parameters
	 *
	 * @return String[][]
	 */
	public static String[][] getHAConfParameters() {
		String copy[][] = new String[haConfParameters.length][];
		for (int i = 0; i < haConfParameters.length; i++) {
			copy[i] = (String[]) haConfParameters[i].clone();
		}
		return copy;
	}

	//manager parameter
	private static String[][] cmParameters = {{CM_PORT, "int", "8001" },
			{MONITOR_INTERVAL, "int", "5" },
			{ALLOW_USER_MULTI_CONNECTION, "string", "YES" },
			{AUTO_START_BROKER, "string", "YES" },
			{EXECUTE_DIAG, "string", "OFF" },
			{SERVER_LONG_QUERY_TIME, "int", "10" },
			{CM_TARGET, "string", "broker,server" } };

	/**
	 * Get the static field cmParameters
	 *
	 * @return String[][]
	 */
	public static String[][] getCmParameters() {
		String copy[][] = new String[cmParameters.length][];
		for (int i = 0; i < cmParameters.length; i++) {
			copy[i] = (String[]) cmParameters[i].clone();
		}
		return copy;
	}

	//broker parameter
	public static String[][] brokerParameters = {
			{MASTER_SHM_ID, "int", "30001", PARAMETER_TYPE_BROKER_GENERAL },
			{ADMIN_LOG_FILE, "string", "log/broker/cubrid_broker.log",
					PARAMETER_TYPE_BROKER_GENERAL },
			{ENABLE_ACCESS_CONTROL, "string(ON|OFF)", "",
					PARAMETER_TYPE_BROKER_GENERAL },
			{ACCESS_CONTROL_FILE, "string", "", PARAMETER_TYPE_BROKER_GENERAL },
			{SERVICE, "string(ON|OFF)", "ON", PARAMETER_TYPE_BROKER_COMMON },
			{BROKER_PORT, "int(1024~65535)", "", PARAMETER_TYPE_BROKER_COMMON },
			{MIN_NUM_APPL_SERVER, "int", "5", PARAMETER_TYPE_BROKER_COMMON },
			{MAX_NUM_APPL_SERVER, "int", "40", PARAMETER_TYPE_BROKER_COMMON },
			{APPL_SERVER_SHM_ID, "int(1024~65535)", "",
					PARAMETER_TYPE_BROKER_COMMON },
			//	{ APPL_SERVER_MAX_SIZE, "int", "20" },
			{LOG_DIR, "string", "log/broker/sql_log",
					PARAMETER_TYPE_BROKER_COMMON },
			{ERROR_LOG_DIR, "string", "log/broker/error_log",
					PARAMETER_TYPE_BROKER_COMMON },
			{SQL_LOG, "string(ON|OFF|ERROR|NOTICE|TIMEOUT)", "ON",
					PARAMETER_TYPE_BROKER_COMMON },
			{TIME_TO_KILL, "int", "120", PARAMETER_TYPE_BROKER_COMMON },
			{SESSION_TIMEOUT, "int", "300", PARAMETER_TYPE_BROKER_COMMON },
			{KEEP_CONNECTION, "string(ON|OFF|AUTO)", "AUTO",
					PARAMETER_TYPE_BROKER_COMMON },

			{STATEMENT_POOLING, "string(ON|OFF)", "ON",
					PARAMETER_TYPE_BROKER_ADVANCE },
			{LONG_QUERY_TIME, "int", "60", PARAMETER_TYPE_BROKER_ADVANCE },
			{LONG_TRANSACTION_TIME, "int", "60", PARAMETER_TYPE_BROKER_ADVANCE },
			{SQL_LOG_MAX_SIZE, "int", "100000", PARAMETER_TYPE_BROKER_ADVANCE },
			{LOG_BACKUP, "string(ON|OFF)", "OFF", PARAMETER_TYPE_BROKER_ADVANCE },
			{SOURCE_ENV, "string", "cubrid.env", PARAMETER_TYPE_BROKER_ADVANCE },
			{MAX_STRING_LENGTH, "int", "-1", PARAMETER_TYPE_BROKER_ADVANCE },
			{APPL_SERVER_PORT, "int", "", PARAMETER_TYPE_BROKER_ADVANCE },
			//	{ APPL_SERVER, "string(CAS)", "CAS" },
			{ACCESS_LOG, "string(ON|OFF)", "ON", PARAMETER_TYPE_BROKER_ADVANCE },
			{ACCESS_LIST, "string", "", PARAMETER_TYPE_BROKER_ADVANCE },
			{CCI_PCONNECT, "string(ON|OFF)", "OFF",
					PARAMETER_TYPE_BROKER_ADVANCE },
			{SELECT_AUTO_COMMIT, "string(ON|OFF)", "OFF",
					PARAMETER_TYPE_BROKER_ADVANCE },
			{ACCESS_MODE, "string(RW|RO|SO)", "RW",
					PARAMETER_TYPE_BROKER_ADVANCE },
			{PREFERRED_HOSTS, "string", "", PARAMETER_TYPE_BROKER_ADVANCE },
            {CCI_DEFAULT_AUTOCOMMIT, "string(ON|OFF)", "ON", PARAMETER_TYPE_BROKER_ADVANCE },
            {ENABLE_OPENSSL, "string(ON|OFF)", "OFF", PARAMETER_TYPE_BROKER_ADVANCE }};

	/**
	 * Get the static field brokerParameters
	 *
	 * @param serverInfo ServerInfo
	 * @return String[][]
	 */
	public static String[][] getBrokerParameters(ServerInfo serverInfo) {
		int length = brokerParameters.length;
		boolean isSupportNewBrokerParamProperty = CompatibleUtil.isSupportNewBrokerParamPropery1(serverInfo);
		boolean isSupportNewHAParam = CompatibleUtil.isSupportNewHABrokerParam(serverInfo);
		boolean isSupportEnableAccessControl = CompatibleUtil.isSupportEnableAccessControl(serverInfo);
		boolean isSupportEnableOpenSSL = CompatibleUtil.isSupportEnableOpenSSL(serverInfo);
		boolean isSupportCCIDefaultAutocommit = CompatibleUtil.isSupportCCIDefaultAutocommit(serverInfo);

		if (!isSupportNewBrokerParamProperty) {
			length = length - 3;
		}
		if (!isSupportNewHAParam) {
			length = length - 1;
		}
		if (!isSupportEnableAccessControl) {
			length = length - 2;
		}
		if (!isSupportEnableOpenSSL) {
            length = length - 1;
        }
		if (!isSupportCCIDefaultAutocommit) {
            length = length - 1;
        }
		String copy[][] = new String[length][];
		int j = 0;
		for (int i = 0; i < brokerParameters.length; i++) {
			if (!isSupportNewBrokerParamProperty
					&& (brokerParameters[i][0].equals(ConfConstants.CCI_PCONNECT)
							|| brokerParameters[i][0].equals(ConfConstants.SELECT_AUTO_COMMIT) || brokerParameters[i][0].equals(ConfConstants.ACCESS_MODE))) {
				continue;
			}
			if (!isSupportNewHAParam
					&& (brokerParameters[i][0].equals(ConfConstants.PREFERRED_HOSTS))) {
				continue;
			}
			if (!isSupportEnableAccessControl
					&& (brokerParameters[i][0].equals(ENABLE_ACCESS_CONTROL) || brokerParameters[i][0].equals(ACCESS_CONTROL_FILE))) {
				continue;
			}
			if (!isSupportEnableOpenSSL
                    && (brokerParameters[i][0].equals(ConfConstants.ENABLE_OPENSSL))) {
                continue;
            }
			if (!isSupportCCIDefaultAutocommit
                    && (brokerParameters[i][0].equals(ConfConstants.CCI_DEFAULT_AUTOCOMMIT))) {
                continue;
            }

			copy[j] = (String[]) brokerParameters[i].clone();
			if (serverInfo != null
					&& brokerParameters[i][0].equals(ACCESS_MODE)) {
				copy[j][1] = CompatibleUtil.getBrokerAccessModeValue(serverInfo);
			}
			j++;
		}
		return copy;
	}

	/**
	 * Judge whether is default broker parameter
	 *
	 * @param param the String
	 * @return boolean
	 */
	public static boolean isDefaultBrokerParameter(String param) { // FIXME more simple
		if (APPL_SERVER_PORT.equals(param)) {
			return true;
		} else if (ACCESS_LIST.equals(param)) {
			return true;
		} else if (ACCESS_LOG.equals(param)) {
			return true;
		} else if (LOG_BACKUP.equals(param)) {
			return true;
		} else if (SQL_LOG_MAX_SIZE.equals(param)) {
			return true;
		} else if (MAX_STRING_LENGTH.equals(param)) {
			return true;
		} else if (SOURCE_ENV.equals(param)) {
			return true;
		} else if (STATEMENT_POOLING.equals(param)) {
			return true;
		} else if (LONG_QUERY_TIME.equals(param)) {
			return true;
		} else if (LONG_TRANSACTION_TIME.equals(param)) {
			return true;
		} else if (CCI_PCONNECT.equals(param)) {
			return true;
		} else if (SELECT_AUTO_COMMIT.equals(param)) {
			return true;
		} else if (ACCESS_MODE.equals(param)) {
			return true;
		} else if (PREFERRED_HOSTS.equals(param)) {
			return true;
		}
		return false;

	}

	private ConfConstants() {

	}
}
