/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.core.util;

import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.IServerSpec;

/**
 * To make dbms tools have downward compatibility, this class provides functions
 * supported information in the versions.
 *
 * @author SC13425
 * @version 1.0 - 2010-11-17 created by SC13425
 */
public final class CompatibleUtil {
	private static final String VER_8_2_0 = "8.2.0";
	private static final String VER_8_2_1 = "8.2.1";
	private static final String VER_8_2_2 = "8.2.2";
	private static final String VER_8_3_0 = "8.3.0";
	private static final String VER_8_3_1 = "8.3.1";
	private static final String VER_8_4_0 = "8.4.0";
	private static final String VER_8_4_1 = "8.4.1";
	private static final String VER_8_4_3 = "8.4.3";
	private static final String VER_8_4_4 = "8.4.4";
	private static final String VER_8_4_9 = "8.4.9";
	private static final String VER_9_0_0 = "9.0.0";
	private static final String VER_9_1_0 = "9.1.0";
	private static final String VER_9_2_0 = "9.2.0";
	private static final String VER_9_3_0 = "9.3.0";
	private static final String VER_10_0_0 = "10.0.0";
	private static final String VER_10_1_0 = "10.1.0";

	private CompatibleUtil() {
	}

	/**
	 * Compare these two versions
	 *
	 * @param version1 the version string
	 * @param version2 the version string
	 * @return the value <code>0</code> if they are equal;<code>-1</code>
	 *         version1 less than version2;<code>1</code> version1 greater than
	 *         version2;<code>-2</code> the version string is not valid
	 */
	public static int compareVersion(String version1, String version2) {
		return compareVersion(version1, version2, 4);
	}

	/**
	 * Compare these two versions
	 *
	 * @param version1 the version string
	 * @param version2 the version string
	 * @param comparedLength the compared length
	 * @return the value <code>0</code> if they are equal;<code>-1</code>
	 *         version1 less than version2;<code>1</code> version1 greater than
	 *         version2;<code>-2</code> the version string is not valid
	 */
	public static int compareVersion(String version1, String version2, int comparedLength) {
		if (version1 == null || version2 == null) {
			return -2;
		}

		String[] versionParts = version1.split("\\.");
		String[] compareParts = version2.split("\\.");
		if (compareParts.length < 3 || versionParts.length < 3) {
			return -2;
		}

		int length = Math.min(compareParts.length, versionParts.length);
		for (int i = 0; i < length && i < comparedLength; i++) {
			if (!versionParts[i].trim().matches("\\d+")
					|| !compareParts[i].trim().matches("\\d+")) {
				return -2;
			}

			int iVersionPart = Integer.parseInt(versionParts[i]);
			int iComparePart = Integer.parseInt(compareParts[i]);
			if (iVersionPart > iComparePart) {
				return 1;
			} else if (iVersionPart < iComparePart) {
				return -1;
			}
		}

		return 0;
	}

	/**
	 * Is the version of cm server after the 8.2.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.2.0 or higher
	 */
	public static boolean isAfter820(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_2_0) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.2.2
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.2.2 or higher
	 */
	public static boolean isAfter822(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_2_2) >= 0;
	}

	/**
	 * Is the version of database after the 8.2.2
	 *
	 * @param database DatabaseInfo
	 * @return true:8.2.2 or higher
	 */
	public static boolean isAfter822(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_2_2) >= 0;
	}

	/**
	 * Is the version of database after the 8.3.0
	 *
	 * @param database DatabaseInfo
	 * @return true:8.3.0 or higher
	 */
	public static boolean isAfter830(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_3_0) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.3.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.3.0 or higher
	 */
	public static boolean isAfter830(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_3_0) >= 0;
	}

	/**
	 * Is the version of database after the 8.3.1
	 *
	 * @param database DatabaseInfo
	 * @return true:8.3.1 or higher
	 */
	public static boolean isAfter831(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_3_1) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.3.1
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.3.1 or higher
	 */
	public static boolean isAfter831(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_3_1) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.4.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.4.0 or higher
	 */
	public static boolean isAfter840(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_4_0) >= 0;
	}

	/**
	 * Is the version of database after the 8.4.0
	 *
	 * @param database DatabaseInfo
	 * @return true:8.4.0 or higher
	 */
	public static boolean isAfter840(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_4_0) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.4.1
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.4.1 or higher
	 */
	public static boolean isAfter841(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_4_1) >= 0;
	}

	/**
	 * Is the version of database after the 8.4.1
	 *
	 * @param database DatabaseInfo
	 * @return true:8.4.1 or higher
	 */
	public static boolean isAfter841(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_4_1) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.4.3
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.4.3 or higher
	 */
	public static boolean isAfter843(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_4_3) >= 0;
	}

	/**
	 * Is the version of database after the 8.4.3
	 *
	 * @param database DatabaseInfo
	 * @return true:8.4.3 or higher
	 */
	public static boolean isAfter843(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_4_3) >= 0;
	}

	/**
	 * Is the version of cm server after the 8.4.4
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.4.4 or higher
	 */
	public static boolean isAfter844(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_4_4) >= 0;
	}

	/**
	 * Is the version of database after the 8.4.4
	 *
	 * @param database DatabaseInfo
	 * @return true:8.4.4 or higher
	 */
	public static boolean isAfter844(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_8_4_4) >= 0;
	}

	/**
	 * Is the version of database after the 9.0.0
	 *
	 * @param database DatabaseInfo
	 * @return true:9.0.0 or higher
	 */
	public static boolean isAfter900(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_9_0_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.0.0
	 *
	 * @param serverInfo isAfter900
	 * @return true:9.0.0 or higher
	 */
	public static boolean isAfter900(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_9_0_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.1.0
	 *
	 * @param serverInfo isAfter910
	 * @return true:9.1.0 or higher
	 */
	public static boolean isAfter910(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_9_1_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.1.0
	 *
	 * @param database IDatabaseSpec
	 * @return true:9.1.0 or higher
	 */
	public static boolean isAfter910(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_9_1_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.2.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.2.0 or higher
	 */
	public static boolean isAfter920(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_9_2_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.2.0
	 *
	 * @param database IDatabaseSpec
	 * @return true:9.2.0 or higher
	 */
	public static boolean isAfter920(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_9_2_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.3.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.3.0 or higher
	 */
	public static boolean isAfter930(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_9_3_0) >= 0;
	}

	/**
	 * Is the version of database after the 9.3.0
	 *
	 * @param database IDatabaseSpec
	 * @return true:9.3.0 or higher
	 */
	public static boolean isAfter930(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_9_3_0) >= 0;
	}

	/**
	 * Is the version of database after the 10.0.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true:10.0.0 or higher
	 */
	public static boolean isAfter100(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_10_0_0) >= 0;
	}

	/**
	 * Is the version of database after the 10.0.0
	 *
	 * @param database IDatabaseSpec
	 * @return true:10.0.0 or higher
	 */
	public static boolean isAfter100(IDatabaseSpec database) {
		return compareVersion(database.getVersion(), VER_10_0_0) >= 0;
	}

	/**
	 * Retrieves whether the current client supports the cm server to connect.
	 * (supports 8.2.x, 8.3.x, 8.4.0, 8.4.1, 8.4.3, 8.4.9, 9.0.0, 9.1.0
	 *
	 * @param serverInfo IServerSpec
	 * @param clientVersion String
	 * @return true:client supports the cm server to connect.
	 */
	public static boolean isSupportCMServer(IServerSpec serverInfo, String clientVersion) {
		if (compareVersion(serverInfo.getServerVersionKey(), VER_8_2_0) < 0) {
			return false;
		}
		//		if (compareVersion(serverInfo.getServerVersionKey(), VER_8_4_3) < 0) {
		//			return true;
		//		}
		//client version bigger than or equal server version
		if (compareVersion(clientVersion, serverInfo.getServerVersionKey(), 3) >= 0) {
			return true;
		}
		/*if client version smaller than server version
		 but server version is 8.4.3/8.4.9/9.0.0 can support it too.*/
		if (compareVersion(serverInfo.getServerVersionKey(), VER_8_4_3) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_8_4_4) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_8_4_9) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_9_0_0) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_9_1_0, 2) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_9_2_0, 2) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_9_3_0, 2) == 0) {
			return true;
		}
		if (compareVersion(serverInfo.getServerVersionKey(), VER_10_0_0, 2) == 0) {
			return true;
		}

		if (compareVersion(serverInfo.getServerVersionKey(), VER_10_1_0, 2) == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Retrieves whether the server supports Log papge size setting.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support;
	 */
	public static boolean isSupportLogPageSize(IServerSpec serverInfo) {
		return isAfter822(serverInfo);
	}

	/**
	 * Retrieves whether the server supports verbose.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportVerbose(IServerSpec serverInfo) {
		return isAfter822(serverInfo);
	}

	/**
	 * Retrieves whether the server supports NO_USE_Statistics
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportNoUseStatistics(IServerSpec serverInfo) {
		return isAfter822(serverInfo);
	}

	/**
	 * Retrieves whether the server supports NewBrokerParamPropery
	 * ConfConstants.CCI_PCONNECT;ConfConstants.SELECT_AUTO_COMMIT;
	 * ConfConstants.ACCESS_MODE
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportNewBrokerParamPropery1(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}
		return isAfter822(serverInfo);
	}

	/**
	 * Retrieves whether the database supports serial cache.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportCache(IDatabaseSpec database) {
		return isAfter822(database);
	}

	/**
	 * Retrieves whether the database supports reuse OID in Create table
	 * process.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportReuseOID(IDatabaseSpec database) {
		return isAfter822(database);
	}

	/**
	 * Retrieves whether the database supports reorder table in edit table
	 * process.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportReorderColumn(IDatabaseSpec database) {
		return isAfter840(database);
	}

	/**
	 * Retrieves whether the database supports change command in edit table
	 * process.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportChangeColumn(IDatabaseSpec database) {
		return isAfter840(database);
	}

	/**
	 * Retrieves whether the server support status monitor.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportBrokerOrDBStatusMonitor(IServerSpec serverInfo) {
		return isAfter822(serverInfo);
	}

	/**
	 * Retrieves whether the server support plan/parameter dump.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportPlanAndParamDump(IServerSpec serverInfo) {
		return isAfter822(serverInfo);
	}

	/**
	 * Retrieves whether the server support query/transaction time with ms.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isQueryOrTransTimeUseMs(IServerSpec serverInfo) {
		return compareVersion(serverInfo.getServerVersionKey(), VER_8_2_1) >= 0;
	}

	/**
	 * Retrieves whether the database support querying plan with user.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportQueryPlanWithUser(IDatabaseSpec database) {
		return isAfter830(database);
	}

	/**
	 * Retrieves whether the server support restore path in database restoring.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportRestorePath(IServerSpec serverInfo) {
		return isAfter830(serverInfo);
	}

	/**
	 * Retrieves whether the database support set null in adding/editing foreign key.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportSetNull(IDatabaseSpec database) {
		return isAfter830(database);
	}

	/**
	 * Retrieves whether the database support "replace view" in adding/editing view
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportReplaceView(IDatabaseSpec database) {
		return isAfter830(database);
	}

	/**
	 * Retrieves whether the database support "Create table like"
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportCreateTableLike(IDatabaseSpec database) {
		return isAfter830(database);
	}

	/**
	 * Retrieves whether support get cup and memory info task
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportGetCPUAndMemoryInfo (IDatabaseSpec database) {
		return isAfter830(database);
	}
	/**
	 * Retrieves whether the database support "truncate table"
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportTruncateTable(IDatabaseSpec database) {
		return isAfter830(database);
	}

	/**
	 * Retrieves whether the server support some new configurations of cm
	 * server. the key and value separator is "=" or "\s", "=" is supported in
	 * the below.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportNewFormatOfCMConf(IServerSpec serverInfo) {
		return isAfter830(serverInfo);
	}

	/**
	 * Return whether to need to check ha_mode status when create database
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isNeedCheckHAModeOnNewDb(IServerSpec serverInfo) {
		return isAfter830(serverInfo);
	}

	/**
	 * Retrieves whether the server support getting parameter dump information
	 * of databases.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportGetParamDump(IServerSpec serverInfo) {
		return isAfter830(serverInfo);
	}

	/**
	 * Retrieves the value type of index_scan_oid_buffer_pages of databases'
	 * advanced parameters.
	 *
	 * @param serverInfo IServerSpec
	 * @return string
	 */
	public static String getIndexScanOIDBufferPagesValueType(
			IServerSpec serverInfo) {
		if (isAfter830(serverInfo)) {
			return "float(v>=0.05&&v<=16)";
		}

		return "int(v>=1&&v<=16)";
	}

	/**
	 * Return whether support parameter "index_scan_oid_buffer_size"
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportIndexScanOIDBufferSize(IServerSpec serverInfo) {
		return isAfter840(serverInfo);
	}

	/**
	 * GET broker access mode value
	 *
	 * @param serverInfo IServerSpec
	 * @return string
	 */
	public static String getBrokerAccessModeValue(IServerSpec serverInfo) {
		if (isSupportNewHABrokerParam(serverInfo)) {
			return "string(RW|RO|SO|PHRO)";
		}

		return "string(RW|RO|SO)";
	}

	/**
	 * Get ha_mode value
	 *
	 * @param serverInfo IServerSpec
	 * @return String
	 */
	public static String getHAModeValue(IServerSpec serverInfo) {
		if (isSupportNewHABrokerParam(serverInfo)) {
			return "string(on|off|yes|no|replica)";
		}

		return "string(on|off|yes|no)";
	}

	/**
	 * Retrieves whether the current server's version support lob.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportLobVersion(IDatabaseSpec database) {
		return isAfter831(database);
	}

	/**
	 * Retrieves whether the current server's version support enum.
	 *
	 * @param database DatabaseInfo
	 * @return true:support.
	 */
	public static boolean isSupportEnumVersion(IDatabaseSpec database) {
		return isAfter900(database);
	}

	/**
	 * Retrieves whether the server support system monitor.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportSystemMonitor(IServerSpec serverInfo) {
		return isAfter831(serverInfo);
	}

	/**
	 * Retrieves whether the server support system monitor.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportDBSystemMonitor(IServerSpec serverInfo) {
		return isAfter831(serverInfo) && !isWindows(serverInfo.getServerOsInfo());
	}

	/**
	 * Retrieves whether the server support ConfConstants.HA_NODE_LIST and
	 * ConfConstants.HA_PORT_ID
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportParamNodeListAndPortId(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		if (isWindows(serverInfo.getServerOsInfo())) {
			return false;
		}

		if (isAfter840(serverInfo)) {
			return false;
		}

		return isAfter822(serverInfo);
	}

	/**
	 * Return whether to support new HA broker parameter including the below
	 * <p>
	 * Add replica value in ConfConstants.HA_MODE parameter from cubrid.conf
	 * <p>
	 * Add ConfConstants.PREFERRED_HOSTS and PHRO value in
	 * ConfConstants.ACCESS_MODE parameter in cubrid_broker.conf
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportNewHABrokerParam(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		if (isWindows(serverInfo.getServerOsInfo())) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Retrieves whether the server support HA.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isSupportHA(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		if (isWindows(serverInfo.getServerOsInfo())) {
			return false;
		}

		return isAfter831(serverInfo);
	}

	/**
	 * Return whether to support new HA configuration file(ha.conf)
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportNewHAConfFile(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		if (isWindows(serverInfo.getServerOsInfo())) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Retrieves whether the server support "new broker diag" in monitoring dialog.
	 *
	 * @param serverInfo IServerSpec
	 * @return true:support.
	 */
	public static boolean isNewBrokerDiag(IServerSpec serverInfo) {
		return isAfter831(serverInfo);
	}

	/**
	 * Retrieves whether the server support broker port setting. Only windows system support.
	 *
	 * @param serverInfo the instance of IServerSpec
	 * @return true:support;
	 */
	public static boolean isSupportBrokerPort(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}
		FileUtil.OsInfoType osInfoType = serverInfo.getServerOsInfo();
		return isWindows(osInfoType);
	}

	/**
	 * Retrieves whether the os of server is windows
	 *
	 * @param osInfoType OsInfoType
	 * @return true:is;
	 */
	public static boolean isWindows(FileUtil.OsInfoType osInfoType) {
		return osInfoType == FileUtil.OsInfoType.NT;
	}

	/**
	 * Retrieves whether the OS of server is AIX
	 *
	 * @param osInfoType OsInfoType
	 * @return true:is;
	 */
	public static boolean isAIX(FileUtil.OsInfoType osInfoType) {
		return osInfoType == FileUtil.OsInfoType.AIX;
	}

	/**
	 * Return whether support JDBC cancel
	 *
	 * @param serverInfo the instance of IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportJDBCCancel(IServerSpec serverInfo) {
		FileUtil.OsInfoType osType = serverInfo == null ? null : serverInfo.getServerOsInfo();
		boolean isSupported = true;
		if (osType == FileUtil.OsInfoType.NT) {
			isSupported = false;
		}

		return isSupported;
	}

	/**
	 * Return whether support prefix index length
	 *
	 * @param database DatabaseInfo
	 * @return boolean
	 */
	public static boolean isSupportPrefixIndexLength(IDatabaseSpec database) {
		return isAfter830(database);
	}

	/**
	 * Return whether support cipher
	 *
	 * @param version String
	 * @return boolean
	 */
	public static boolean isSupportCipher(String version) {
		return compareVersion(parseServerVersion(version), VER_8_4_0) == 0;
	}

	/**
	 * Parse the server version from "CUBRID 2008 R2.0(8.2.0.1150)" to "8.2.0"
	 *
	 * @param version String
	 * @return version
	 */
	public static String parseServerVersion(String version) {
		if (version == null) {
			return "";
		}

		if (version.indexOf("(") != -1 && version.indexOf(")") != -1) {
			String tmp = version.substring(1 + version.indexOf("("));
			tmp = tmp.substring(0, tmp.indexOf(")"));
			return tmp.substring(0, tmp.lastIndexOf("."));
		}

		return version;
	}

	/**
	 * Return whether to support nlucene
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportNLucene(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Return whether support compact database online
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportOnlineCompactDb(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Return whether to support size properties including data_buffer_size,
	 * log_buffer_size, sort_buffer_size
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportSizesPropInServer(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Return whether to support access_ip_control and access_ip_control_file
	 * parameter
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportAccessIpControl(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Return whether to support enable_access_control and access_control_file
	 *
	 * @param serverInfo IServerSpec
	 * @return boolean
	 */
	public static boolean isSupportEnableAccessControl(IServerSpec serverInfo) {
		if (serverInfo == null) {
			return false;
		}

		return isAfter840(serverInfo);
	}

	/**
	 * Get supported page size
	 *
	 * @param serverInfo IServerSpec
	 * @return String[]
	 */
	public static String[] getSupportedPageSize(IServerSpec serverInfo) {
		if (isAfter840(serverInfo)) {
			return new String[]{"4096", "8192", "16384" };
		} else {
			return new String[]{"1024", "2048", "4096", "8192", "16384" };
		}
	}

	/**
	 * Get default page size
	 *
	 * @param serverInfo IServerSpec
	 * @return String
	 */
	public static String getDefaultPageSize(IServerSpec serverInfo) {
		if (isAfter840(serverInfo)) {
			return "16384";
		} else {
			return "4096";
		}
	}

	/**
	 * Get the "db_volume_size" which is config in cubrid.conf
	 *
	 * @param serverInfo
	 * @return the "db_volume_size" in the "cubrid.conf" of server.If
	 *         databaseName is null, return the common setting value
	 */
	public static String getConfigGenericVolumeSize(IServerSpec serverInfo, String databaseName) {
		if (serverInfo == null) {
			return null;
		}

		return serverInfo.getCubridConfPara("db_volume_size", databaseName);
	}

	/**
	 * Get the "log_volume_size" which is config in cubrid.conf
	 *
	 * @param serverInfo
	 * @return the "log_volume_size" in the "cubrid.conf" of server. If
	 *         databaseName is null, return the common setting value
	 */
	public static String getConfigLogVolumeSize(IServerSpec serverInfo, String databaseName) {
		if (serverInfo == null) {
			return null;
		}

		return serverInfo.getCubridConfPara("log_volume_size", databaseName);
	}

	/**
	 * Whether support query LIMIT clause
	 *
	 * @param databaseInfo
	 * @return
	 */
	public static boolean isSupportLimit(IDatabaseSpec databaseInfo) {
		if (databaseInfo == null) {
			return false;
		}

		return isAfter830(databaseInfo);
	}

	/**
	 * Whether support create db by charset
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.2.0 or higher
	 */
	public static boolean isSupportCreateDBByCharset(IServerSpec serverInfo) {
		return isAfter920(serverInfo);
	}

	/**
	 * Whether support create db by charset
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.2.0 or higher
	 */
	public static boolean isSupportCreateDBByCharset(IDatabaseSpec databaseInfo) {
		try {
			return isAfter920(databaseInfo);
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Whether can get sub-partition table when get table list
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.0.0 or higher
	 */
	public static boolean isNotSupportGetSubPartitionTable(IServerSpec serverInfo) {
		return isAfter910(serverInfo);
	}

	/**
	 * Whether can be supported Shard
	 *
	 * @param serverInfo IServerSpec
	 * @return true:8.4.3 or higher
	 */
	public static boolean isSupportShard(IServerSpec serverInfo) {
		return isAfter843(serverInfo);
	}

	/**
	 * Whether support auto backup/query plan by period or multi times
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.2.0(except AIX) or higher
	 */
	public static boolean isSupportPeriodicAutoJob(IServerSpec serverInfo){
		// After CMS support auto update, need get CMS version to judge true or false
		return isAfter920(serverInfo) && !isAIX(serverInfo.getServerOsInfo());
	}

	/**
	 * Whether support auto backup/query plan by period or multi times
	 *
	 * @param databaseInfo IDatabaseSpec
	 * @return true:9.2.0(except AIX) or higher
	 */
	public static boolean isSupportPeriodicAutoJob(IDatabaseSpec databaseInfo) {
		if (databaseInfo == null) {
			return false;
		}
		return isAfter920(databaseInfo)
				&& !isAIX(databaseInfo.getServerOsInfo());
	}

	/**
	 * Whether support monitor statistic feature
	 *
	 * @param serverInfo IServerSpec
	 * @return true:9.2.0(except AIX) or higher
	 */
	public static boolean isSupportMonitorStatistic(IServerSpec serverInfo){
		// After CMS support auto update, need get CMS version to judge true or false
		return isAfter920(serverInfo) && !isAIX(serverInfo.getServerOsInfo());
	}

	/**
	 * Whether support specify backup volume name
	 *
	 * @param serverInfo IServerSpec
	 * @return true: for CUBRID R2008, before 8.4.4, later version before 9.2.0
	 */
	public static boolean isSupportBackupVolumeName(IServerSpec serverInfo) {
		if (isAfter844(serverInfo) && !isAfter900(serverInfo)) {
			return false;
		} else if (isAfter920(serverInfo)) {
			return false;
		}
		return true;
	}

	/**
	 * Whether support specify backup volume name
	 *
	 * @param databaseInfo IDatabaseSpec
	 * @return true: for CUBRID R2008, before 8.4.4, later version before 9.2.0
	 */
	public static boolean isSupportBackupVolumeName(IDatabaseSpec databaseInfo) {
		if (databaseInfo == null) {
			return true;
		}
		if(isAfter844(databaseInfo) && !isAfter900(databaseInfo)){
			return false;
		}else if(isAfter920(databaseInfo)){
			return false;
		}
		return true;
	}

	/**
	 * Whether support new "killtransaction" API
	 *
	 * @param serverInfo IServerSpec
	 * @return true: after 9.2.0
	 */
	public static boolean isSupportNewKillTranTask(IServerSpec serverInfo) {
		return isAfter920(serverInfo);
	}

	/**
	 * Whether support new "killtransaction" API
	 *
	 * @param databaseInfo IDatabaseSpec
	 * @return true: after 9.2.0
	 */
	public static boolean isSupportNewKillTranTask(IDatabaseSpec databaseInfo) {
		if (databaseInfo == null) {
			return false;
		}

		return isAfter920(databaseInfo);
	}

	/**
	 * Whether need check DBA authority by JDBC task <br>
	 * <b>Note: <b>Only for 8.4.3/8.4.4/9.1.0/9.2.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true: after 8.4.3
	 */
	public static boolean isNeedCheckDbaAuthorityByJDBC(IServerSpec serverInfo) {
		//TODO: If new CUBRID version available or CMS patch for this, should update this method
		return isAfter843(serverInfo);
	}

	/**
	 * Whether need check DBA authority by JDBC task <br>
	 * <b>Note: <b>Only for 8.4.3/8.4.4/9.1.0/9.2.0
	 *
	 * @param databaseInfo IDatabaseSpec
	 * @return true: after 8.4.3
	 */
	public static boolean isNeedCheckDbaAuthorityByJDBC(
			IDatabaseSpec databaseInfo) {
		//TODO: If new CUBRID version available or CMS patch for this, should update this method
		if (databaseInfo == null) {
			return false;
		}

		return isAfter843(databaseInfo);
	}

	/**
	 * Whether supports to keep number of backups on the backup automation<br>
	 * <b>Note: <b>Only for 8.4.3/8.4.4/9.1.0/9.2.0
	 *
	 * @param databaseSpec IDatabaseSpec
	 * @return true: after 8.4.3
	 */
	public static boolean isBackupNumSupports(IDatabaseSpec databaseSpec) {
		return isAfter843(databaseSpec);
	}

	/**
	 * Whether supports to keep number of backups on the backup automation<br>
	 * <b>Note: <b>Only for 8.4.3/8.4.4/9.1.0/9.2.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true: after 8.4.3
	 */
	public static boolean isBackupNumSupports(IServerSpec serverInfo) {
		return isAfter843(serverInfo);
	}

	/**
	 * Whether supports to comment on engine
	 * <b>Note: <b>Only after 10.0.0
	 *
	 * @param serverInfo IServerSpec
	 * @return true: after 10.0.0
	 */
	public static boolean isCommentSupports(IServerSpec serverInfo) {
		return isAfter100(serverInfo);
	}

	/**
	 * Whether supports to comment on engine
	 * <b>Note: <b>Only after 10.0.0
	 *
	 * @param databaseSpec IDatabaseSpec
	 * @return
	 */
	public static boolean isCommentSupports(IDatabaseSpec databaseSpec) {
		return isAfter100(databaseSpec);
	}

	/**
	 * Whether supports functional index
	 * <b>Note: <b>Only after 9.0.0
	 *
	 * @param databaseSpec IDatabaseSpec
	 * @return
	 */
	public static boolean isSupportFuncIndex(IDatabaseSpec databaseSpec) {
		return isAfter900(databaseSpec);
	}

	/**
	 * Whether supports functional index
	 * <b>Note: <b>Only after 9.0.0
	 *
	 * @param serverInfo IServerSpec
	 * @return
	 */
	public static boolean isSupportFuncIndex(IServerSpec serverInfo) {
		return isAfter900(serverInfo);
	}
}
