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

/**
 * 
 * cubrid_broker.conf configuration file parameters constants
 * 
 * @author pangqiren
 * @version 1.0 - 2011-9-7 created by pangqiren
 */
public interface CubridBrokerConfParaConstants { //NOPMD

	//broker parameter
	public final static String BROKER_SECTION = "[broker]";
	public final static String BROKER_SECTION_NAME = "broker";

	public final static String MASTER_SHM_ID = "MASTER_SHM_ID";
	public final static String ADMIN_LOG_FILE = "ADMIN_LOG_FILE";
	public final static String ENABLE_ACCESS_CONTROL = "ENABLE_ACCESS_CONTROL";
	public final static String ACCESS_CONTROL_FILE = "ACCESS_CONTROL_FILE";
	public final static String SERVICE = "SERVICE";
	public final static String BROKER_PORT = "BROKER_PORT";
	public final static String MIN_NUM_APPL_SERVER = "MIN_NUM_APPL_SERVER";
	public final static String MAX_NUM_APPL_SERVER = "MAX_NUM_APPL_SERVER";
	public final static String APPL_SERVER_SHM_ID = "APPL_SERVER_SHM_ID";
	public final static String APPL_SERVER_MAX_SIZE = "APPL_SERVER_MAX_SIZE";
	public final static String LOG_DIR = "LOG_DIR";
	public final static String ERROR_LOG_DIR = "ERROR_LOG_DIR";
	public final static String SQL_LOG = "SQL_LOG";
	public final static String TIME_TO_KILL = "TIME_TO_KILL";
	public final static String SESSION_TIMEOUT = "SESSION_TIMEOUT";
	public final static String KEEP_CONNECTION = "KEEP_CONNECTION";
	public final static String ACCESS_LIST = "ACCESS_LIST";
	public final static String ACCESS_LOG = "ACCESS_LOG";
	public final static String APPL_SERVER_PORT = "APPL_SERVER_PORT";
	public final static String CCI_PCONNECT = "CCI_PCONNECT";
	public final static String SELECT_AUTO_COMMIT = "SELECT_AUTO_COMMIT";
	public final static String ACCESS_MODE = "ACCESS_MODE";
	public final static String PREFERRED_HOSTS = "PREFERRED_HOSTS";
	public final static String APPL_SERVER = "APPL_SERVER";
	public final static String LOG_BACKUP = "LOG_BACKUP";
	public final static String SQL_LOG_MAX_SIZE = "SQL_LOG_MAX_SIZE";
	public final static String MAX_STRING_LENGTH = "MAX_STRING_LENGTH";
	public final static String SOURCE_ENV = "SOURCE_ENV";
	public final static String STATEMENT_POOLING = "STATEMENT_POOLING";
	public final static String LONG_QUERY_TIME = "LONG_QUERY_TIME";
	public final static String LONG_TRANSACTION_TIME = "LONG_TRANSACTION_TIME";
	public final static String CCI_DEFAULT_AUTOCOMMIT = "CCI_DEFAULT_AUTOCOMMIT";
	public final static String ENABLE_OPENSSL = "SSL";

	public final static String PARAMETER_TYPE_BROKER_GENERAL = "general";
	public final static String PARAMETER_TYPE_BROKER_COMMON = "common";
	public final static String PARAMETER_TYPE_BROKER_ADVANCE = "advance";

	public final static String[][] BROKER_CONF_PARAS_IN_COMMON = {
			{MASTER_SHM_ID, "int(v>0)", "30001", PARAMETER_TYPE_BROKER_GENERAL,
					"version>=8.2.0", "false", "" },

			{ADMIN_LOG_FILE, "string", "log/broker/cubrid_broker.log",
					PARAMETER_TYPE_BROKER_GENERAL, "version>=8.2.0", "false",
					"" },
			{ENABLE_ACCESS_CONTROL, "string(ON|OFF)", "",
					PARAMETER_TYPE_BROKER_GENERAL, "version>=8.4.0", "false",
					"" },

			{ACCESS_CONTROL_FILE, "string", "", PARAMETER_TYPE_BROKER_GENERAL,
					"version>=8.4.0", "false", "" } };

	//broker parameter
	public final static String[][] BROKER_CONF_PARAS_IN_BROKER = {

			{SERVICE, "string(ON|OFF)", "ON", PARAMETER_TYPE_BROKER_COMMON,
					"version>=8.2.0", "false", "" },

			{BROKER_PORT, "int(v>=1024&&v<=65535)", "30000",
					PARAMETER_TYPE_BROKER_COMMON, "version>=8.4.0", "false", "" },

			{MIN_NUM_APPL_SERVER, "int", "5", PARAMETER_TYPE_BROKER_COMMON,
					"version>=8.2.0", "false", "" },

			{MAX_NUM_APPL_SERVER, "int", "40", PARAMETER_TYPE_BROKER_COMMON,
					"version>=8.2.0", "false", "" },

			{APPL_SERVER_SHM_ID, "int(v>=1024&&v<=65535)", "30000",
					PARAMETER_TYPE_BROKER_COMMON, "version>=8.2.0", "false", "" },

			{APPL_SERVER_MAX_SIZE, "int", "40", PARAMETER_TYPE_BROKER_COMMON,
					"version>=8.2.0", "false", "" },

			{LOG_DIR, "string", "log/broker/sql_log",
					PARAMETER_TYPE_BROKER_COMMON, "version>=8.2.0", "false", "" },

			{ERROR_LOG_DIR, "string", "log/broker/error_log",
					PARAMETER_TYPE_BROKER_COMMON, "version>=8.2.0", "false", "" },

			{SQL_LOG, "string(ON|OFF|ERROR|NOTICE|TIMEOUT)", "ON",
					PARAMETER_TYPE_BROKER_COMMON, "version>=8.2.0", "false", "" },

			{TIME_TO_KILL, "int(v>0(", "120", PARAMETER_TYPE_BROKER_COMMON,
					"version>=8.2.0", "false", "" },

			{SESSION_TIMEOUT, "int", "300", PARAMETER_TYPE_BROKER_COMMON,
					"version>=8.2.0", "false", "" },

			{KEEP_CONNECTION, "string(ON|OFF|AUTO)", "AUTO",
					PARAMETER_TYPE_BROKER_COMMON, "version>=8.2.0", "false", "" },

			{ACCESS_LIST, "string", "", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{ACCESS_LOG, "string(ON|OFF)", "ON", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{APPL_SERVER_PORT, "int", "", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{APPL_SERVER, "string(CAS)", "CAS", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{LOG_BACKUP, "string(ON|OFF)", "OFF",
					PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.2.0", "false",
					"" },

			{SQL_LOG_MAX_SIZE, "int", "100000", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{MAX_STRING_LENGTH, "int", "-1", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{SOURCE_ENV, "string", "cubrid.env", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{STATEMENT_POOLING, "string(ON|OFF)", "ON",
					PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.2.0", "false",
					"" },

			{CCI_PCONNECT, "string(ON|OFF)", "OFF",
					PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.2.2", "false",
					"" },

			{LONG_QUERY_TIME, "int", "60", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{LONG_TRANSACTION_TIME, "int", "60", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.2.0", "false", "" },

			{ACCESS_MODE, "string(RW|RO|SO)", "RW",
					PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.2.2", "false",
					"" },

			{ACCESS_MODE, "string(RW|RO|SO|PHRO)", "RW",
					PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.4.0&&os=linux",
					"false", "" },

			{SELECT_AUTO_COMMIT, "string(ON|OFF)", "OFF",
					PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.2.2", "false",
					"" },

			{PREFERRED_HOSTS, "string", "", PARAMETER_TYPE_BROKER_ADVANCE,
					"version>=8.4.0&&os=linux", "false", "" },

            {CCI_DEFAULT_AUTOCOMMIT, "string(ON|OFF)", "ON",
				    PARAMETER_TYPE_BROKER_ADVANCE, "version>=8.4.1", "false",
                    "" },

            {ENABLE_OPENSSL, "string(ON|OFF)", "OFF",
                    PARAMETER_TYPE_BROKER_ADVANCE, "version>=10.2.1", "false",
                    "" }
	};

}
