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
 * Cubrid.conf configuration file parameters constants
 *
 * @author pangqiren
 * @version 1.0 - 2011-9-7 created by pangqiren
 */
public interface CubridConfParaConstants { // NOPMD

    // common parameters
    public static final String SERVICE_SECTION = "[service]";
    public static final String SERVICE_SECTION_NAME = "service";
    public static final String COMMON_SERVICE = "service";
    public static final String COMMON_SERVER = "server";
    public static final String COMMON_SECTION = "[common]";
    public static final String COMMON_SECTION_NAME = "common";

    // 8.2.0~8.2.2 parameters
    public static final String CUBRID_PORT_ID = "cubrid_port_id";
    public static final String COMMUNICATION_HISTOGRAM = "communication_histogram";
    public static final String DB_HOSTS = "db_hosts";
    public static final String MAX_CLIENTS = "max_clients";
    public static final String BLOCK_DDL_STATEMENT = "block_ddl_statement";
    public static final String BLOCK_NOWHERE_STATEMENT = "block_nowhere_statement";
    public static final String INTL_MBS_SUPPORT = "intl_mbs_support";
    public static final String ORACLE_STYLE_EMPTY_STRING = "oracle_style_empty_string";
    public static final String DATA_BUFFER_PAGES = "data_buffer_pages";
    public static final String DONT_REUSE_HEAP_FILE = "dont_reuse_heap_file";
    public static final String INDEX_SCAN_OID_BUFFER_PAGES = "index_scan_oid_buffer_pages";
    public static final String SORT_BUFFER_PAGES = "sort_buffer_pages";
    public static final String TEMP_FILE_MEMORY_SIZE_IN_PAGES = "temp_file_memory_size_in_pages";
    public static final String THREAD_STACK_SIZE = "thread_stack_size";
    public static final String GARBAGE_COLLECTION = "garbage_collection";
    public static final String TEMP_FILE_MAX_SIZE_IN_PAGES = "temp_file_max_size_in_pages";
    public static final String TEMP_VOLUME_PATH = "temp_volume_path";
    public static final String UNFILL_FACTOR = "unfill_factor";
    public static final String VOLUME_EXTENSION_PATH = "volume_extension_path";
    public static final String CALL_STACK_DUMP_ACTIVATION_LIST = "call_stack_dump_activation_list";
    public static final String CALL_STACK_DUMP_DEACTIVATION_LIST =
            "call_stack_dump_deactivation_list";
    public static final String CALL_STACK_DUMP_ON_ERROR = "call_stack_dump_on_error";
    public static final String ERROR_LOG = "error_log";
    public static final String AUTO_RESTART_SERVER = "auto_restart_server";
    public static final String DEADLOCK_DETECTION_INTERVAL_IN_SECS =
            "deadlock_detection_interval_in_secs";
    public static final String FILE_LOCK = "file_lock";
    public static final String ISOLATION_LEVEL = "isolation_level";
    public static final String LOCK_ESCALATION = "lock_escalation";
    public static final String LOCK_TIMEOUT_IN_SECS = "lock_timeout_in_secs";
    public static final String LOCK_TIMEOUT_MESSAGE_TYPE = "lock_timeout_message_type";
    public static final String BACKGROUND_ARCHIVING = "background_archiving";
    // refer to HAConfParaConstants.LOG_MAX_ARCHIVES
    // public static final String LOG_MAX_ARCHIVES = "log_max_archives";
    public static final String LOG_FLUSH_INTERVAL_IN_MESCS = "log_flush_interval_in_msecs";
    public static final String PTHREAD_SCOPE_PROCESS = "pthread_scope_process";
    public static final String BACKUP_VOLUME_MAX_SIZE_BYTES = "backup_volume_max_size_bytes";
    public static final String CHECKPOINT_INTERVAL_IN_MINS = "checkpoint_interval_in_mins";
    public static final String LOG_BUFFER_PAGES = "log_buffer_pages";
    public static final String MEDIA_FAILURE_SUPPORT = "media_failure_support";
    public static final String INSERT_EXECUTION_MODE = "insert_execution_mode";
    public static final String MAX_PLAN_CACHE_ENTRIES = "max_plan_cache_entries";
    public static final String MAX_QUERY_CACHE_ENTRIES = "max_query_cache_entries";
    public static final String QUERY_CACHE_MODE = "query_cache_mode";
    public static final String QUERY_CACHE_SIZE_IN_PAGES = "query_cache_size_in_pages";
    public static final String REPLICATION = "replication";
    public static final String INDEX_SCAN_IN_OID_ORDER = "index_scan_in_oid_order";
    public static final String SINGLE_BYTE_COMPARE = "single_byte_compare";
    public static final String COMPACTDB_PAGE_RECLAIM_ONLY = "compactdb_page_reclaim_only";
    public static final String COMPAT_NUMERIC_DIVISION_SCALE = "compat_numeric_division_scale";
    public static final String CSQL_HISTORY_NUM = "csql_history_num";
    public static final String JAVA_STORED_PROCEDURE = "java_stored_procedure";
    public static final String ASYNC_COMMIT = "async_commit";
    public static final String GROUP_COMMIT_INTERVAL_IN_MSECS = "group_commit_interval_in_msecs";
    public static final String INDEX_UNFILL_FACTOR = "index_unfill_factor";

    // 8.3.0~8.3.1 parameter
    public static final String ANSI_QUOTES = "ansi_quotes";
    public static final String ONLY_FULL_GROUP_BY = "only_full_group_by";
    public static final String PIPES_AS_CONCAT = "pipes_as_concat";
    public static final String ERROR_LOG_LEVEL = "error_log_level";
    public static final String ERROR_LOG_WARNING = "error_log_warning";
    public static final String ERROR_LOG_SIZE = "error_log_size";
    public static final String PAGE_FLUSH_INTERVAL_IN_MSECS = "page_flush_interval_in_msecs";
    public static final String ADAPTIVE_FLUSH_CONTROL = "adaptive_flush_control";
    public static final String MAX_FLUSH_PAGES_PER_SECOND = "max_flush_pages_per_second";
    public static final String SYNC_ON_NFLUSH = "sync_on_nflush";
    public static final String CHECKPOINT_EVERY_NPAGES = "checkpoint_every_npages";

    // 8.4.0 parameter
    public static final String DATA_BUFFER_SIZE = "data_buffer_size";
    public static final String INDEX_SCAN_OID_BUFFER_SIZE = "index_scan_oid_buffer_size";
    public static final String SORT_BUFFER_SIZE = "sort_buffer_size";
    public static final String DB_VOLUME_SIZE = "db_volume_size";
    public static final String LOG_VOLUME_SIZE = "log_volume_size";
    public static final String LOG_BUFFER_SIZE = "log_buffer_size";
    public static final String FORCE_REMOVE_LOG_ARCHIVES = "force_remove_log_archives";
    public static final String ADD_COLUMN_UPDATE_HARD_DEFAULT = "add_column_update_hard_default";
    public static final String PLUS_AS_CONCAT = "plus_as_concat";
    public static final String RETURN_NULL_ON_FUNCTION_ERRORS = "return_null_on_function_errors";
    public static final String NO_BACKSLASH_ESCAPES = "no_backslash_escapes";
    public static final String REQUIRE_LIKE_ESCAPE_CHARACTER = "require_like_escape_character";
    public static final String ALTER_TABLE_CHANGE_TYPE_STRICT = "alter_table_change_type_strict";
    public static final String DEFAULT_WEEK_FORMAT = "default_week_format";
    public static final String GROUP_CONCAT_MAX_LEN = "group_concat_max_len";
    // refer to HAConfParaConstants.HA_MODE
    // public static final String HA_MODE = "ha_mode";
    public static final String USE_ORDERBY_SORT_LIMIT = "use_orderby_sort_limit";
    public static final String SESSION_STATE_TIMEOUT = "session_state_timeout";
    public static final String MULTI_RANGE_OPTIMIZATION_LIMIT = "multi_range_optimization_limit";
    public static final String ACCESS_IP_CONTROL = "access_ip_control";
    public static final String ACCESS_IP_CONTROL_FILE = "access_ip_control_file";

    // other parameters
    public static final String COMPAT_PRIMARY_KEY = "compat_primary_key";
    public static final String HOSTVAR_LATE_BINDING = "hostvar_late_binding";
    public static final String ORACLE_STYLE_OUTERJOIN = "oracle_style_outerjoin";
    // refer to HAConfParaConstants.HA_NODE_LIST
    // public final static String HA_NODE_LIST = "ha_node_list";
    // refer to HAConfParaConstants.HA_PORT_ID
    // public final static String HA_PORT_ID = "ha_port_id";

    // parameter type
    public static final String PARAMETER_TYPE_CLIENT = "client";
    public static final String PARAMETER_TYPE_SERVER = "server";
    public static final String PARAMETER_TYPE_BOTH = "client,server";
    public static final String PARAMETER_TYPE_UTILITY = "utility only";

    // parameter value
    // Transaction isolation level
    public static final String TRAN_SERIALIZABLE = "TRAN_SERIALIZABLE";
    public static final String TRAN_REP_CLASS_REP_INSTANCE = "TRAN_REP_CLASS_REP_INSTANCE";
    public static final String TRAN_REP_CLASS_COMMIT_INSTANCE = "TRAN_REP_CLASS_COMMIT_INSTANCE";
    public static final String TRAN_REP_CLASS_UNCOMMIT_INSTANCE =
            "TRAN_REP_CLASS_UNCOMMIT_INSTANCE";
    public static final String TRAN_COMMIT_CLASS_COMMIT_INSTANCE =
            "TRAN_COMMIT_CLASS_COMMIT_INSTANCE";
    public static final String TRAN_COMMIT_CLASS_UNCOMMIT_INSTANCE =
            "TRAN_COMMIT_CLASS_UNCOMMIT_INSTANCE";

    // All parameters in service section
    public static final String[][] CUBRID_CONF_PARAS_IN_SERVICE = {
        {
            COMMON_SERVICE,
            "string(server|broker|manager)",
            "",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {COMMON_SERVER, "string", "", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""}
    };

    // All parameters in common or database section
    // array {parameter name,value type and scope,default value, parameter type,supported
    // version,isDeprecated,desc }
    public static final String[][] CUBRID_CONF_PARAS_IN_COMMON_OR_DB = {
        {
            CUBRID_PORT_ID,
            "int(v>=1024&&v<=65535)",
            "1523",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            COMMUNICATION_HISTOGRAM,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {DB_HOSTS, "string", "", PARAMETER_TYPE_CLIENT, "version>=8.2.0", "false", ""},
        {MAX_CLIENTS, "int(v>0)", "50", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            BLOCK_DDL_STATEMENT,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            BLOCK_NOWHERE_STATEMENT,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            INTL_MBS_SUPPORT,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            ORACLE_STYLE_EMPTY_STRING,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            DATA_BUFFER_PAGES,
            "int(v>0)",
            "25000",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "version>=8.4.0",
            ""
        },
        {
            DONT_REUSE_HEAP_FILE,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            INDEX_SCAN_OID_BUFFER_PAGES,
            "int(v>=1&&v<=16)",
            "4",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "version>=8.4.0",
            ""
        },
        {
            INDEX_SCAN_OID_BUFFER_PAGES,
            "float(v>=0.05&&v<=16)",
            "4",
            PARAMETER_TYPE_SERVER,
            "version>=8.3.0",
            "version>=8.4.0",
            ""
        },
        {
            SORT_BUFFER_PAGES,
            "int(v>0)",
            "16",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "version>=8.4.0",
            ""
        },
        {
            TEMP_FILE_MEMORY_SIZE_IN_PAGES,
            "int(v>=0&&v<=20)",
            "4",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            THREAD_STACK_SIZE,
            "int(v>=64*1024)",
            "102400",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            GARBAGE_COLLECTION,
            "boolean(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            TEMP_FILE_MAX_SIZE_IN_PAGES,
            "int",
            "-1",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {TEMP_VOLUME_PATH, "string", "", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            UNFILL_FACTOR,
            "float(v>=0&&v<=0.3)",
            "0.1",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {VOLUME_EXTENSION_PATH, "string", "", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        /////////////////
        {
            CALL_STACK_DUMP_ACTIVATION_LIST,
            "string",
            "",
            PARAMETER_TYPE_BOTH,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            CALL_STACK_DUMP_DEACTIVATION_LIST,
            "string",
            "",
            PARAMETER_TYPE_BOTH,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            CALL_STACK_DUMP_ON_ERROR,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_BOTH,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            ERROR_LOG,
            "string",
            "cub_client.err,cub_server.err",
            PARAMETER_TYPE_BOTH,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            AUTO_RESTART_SERVER,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            DEADLOCK_DETECTION_INTERVAL_IN_SECS,
            "int(v>0)",
            "1",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {FILE_LOCK, "bool(yes|no)", "yes", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            ISOLATION_LEVEL,
            "string(\"TRAN_SERIALIZABLE\"|\"TRAN_REP_CLASS_REP_INSTANCE\"|\"TRAN_REP_CLASS_COMMIT_INSTANCE\"|"
                    + "\"TRAN_REP_CLASS_UNCOMMIT_INSTANCE\"|\"TRAN_COMMIT_CLASS_COMMIT_INSTANCE\"|\"TRAN_COMMIT_CLASS_UNCOMMIT_INSTANCE\")",
            "TRAN_REP_CLASS_UNCOMMIT_INSTANCE",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            LOCK_ESCALATION,
            "int(v>0)",
            "100000",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {LOCK_TIMEOUT_IN_SECS, "int", "-1", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            LOCK_TIMEOUT_MESSAGE_TYPE,
            "int(v>=0&&v<=2)",
            "0",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            BACKGROUND_ARCHIVING,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            HAConfParaConstants.LOG_MAX_ARCHIVES,
            "int",
            "INT_MAX",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            LOG_FLUSH_INTERVAL_IN_MESCS,
            "v>0",
            "1000",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            PTHREAD_SCOPE_PROCESS,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            BACKUP_VOLUME_MAX_SIZE_BYTES,
            "int(v>=32*1024)",
            "-1",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            CHECKPOINT_INTERVAL_IN_MINS,
            "int(v>0)",
            "720",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {LOG_BUFFER_PAGES, "int(v>0)", "50", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            MEDIA_FAILURE_SUPPORT,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            INSERT_EXECUTION_MODE,
            "int(v>=1&&v<=7)",
            "1",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {MAX_PLAN_CACHE_ENTRIES, "int", "1000", PARAMETER_TYPE_BOTH, "version>=8.2.0", "false", ""},
        {
            MAX_QUERY_CACHE_ENTRIES,
            "int",
            "-1",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            QUERY_CACHE_MODE,
            "int(v>=0&&v<=2)",
            "0",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            QUERY_CACHE_SIZE_IN_PAGES,
            "int",
            "-1",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {REPLICATION, "bool(yes|no)", "no", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            INDEX_SCAN_IN_OID_ORDER,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            SINGLE_BYTE_COMPARE,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            COMPACTDB_PAGE_RECLAIM_ONLY,
            "int",
            "0",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            COMPAT_NUMERIC_DIVISION_SCALE,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_BOTH,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            CSQL_HISTORY_NUM,
            "int(v>=1&&v<=200)",
            "50",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            JAVA_STORED_PROCEDURE,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {ASYNC_COMMIT, "bool(yes|no)", "no", PARAMETER_TYPE_SERVER, "version>=8.2.0", "false", ""},
        {
            GROUP_COMMIT_INTERVAL_IN_MSECS,
            "int(v>0)",
            "0",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            INDEX_UNFILL_FACTOR,
            "float(yes|no)",
            "0.2",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0",
            "false",
            ""
        },
        {ANSI_QUOTES, "bool(yes|no)", "yes", PARAMETER_TYPE_CLIENT, "version>=8.3.0", "false", ""},
        {
            ONLY_FULL_GROUP_BY,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.3.0",
            "false",
            ""
        },
        {
            PIPES_AS_CONCAT,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_CLIENT,
            "version>=8.3.0",
            "false",
            ""
        },
        {ERROR_LOG_LEVEL, "string", "syntax", PARAMETER_TYPE_BOTH, "version>=8.3.0", "false", ""},
        {ERROR_LOG_WARNING, "string", "no", PARAMETER_TYPE_BOTH, "version>=8.3.0", "false", ""},
        {ERROR_LOG_SIZE, "int(v>0)", "8000000", PARAMETER_TYPE_BOTH, "version>=8.3.0", "false", ""},
        {
            PAGE_FLUSH_INTERVAL_IN_MSECS,
            "int(v>0)",
            "0",
            PARAMETER_TYPE_SERVER,
            "version>=8.3.0",
            "false",
            ""
        },
        {
            ADAPTIVE_FLUSH_CONTROL,
            "bool(false|true)",
            "true",
            PARAMETER_TYPE_SERVER,
            "version>=8.3.0",
            "false",
            ""
        },
        {
            MAX_FLUSH_PAGES_PER_SECOND,
            "int(v>0)",
            "10000",
            PARAMETER_TYPE_SERVER,
            "version>=8.3.0",
            "false",
            ""
        },
        {SYNC_ON_NFLUSH, "int(v>0)", "200", PARAMETER_TYPE_SERVER, "version>=8.3.0", "false", ""},
        {
            CHECKPOINT_EVERY_NPAGES,
            "int(v>0)",
            "10000",
            PARAMETER_TYPE_SERVER,
            "version>=8.3.0",
            "false",
            ""
        },
        {
            DATA_BUFFER_SIZE,
            "int(v>0)",
            "512M",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            INDEX_SCAN_OID_BUFFER_SIZE,
            "int(v>0)",
            "64K",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {DB_VOLUME_SIZE, "int(v>0)", "512M", PARAMETER_TYPE_SERVER, "version>=8.4.0", "false", ""},
        {LOG_VOLUME_SIZE, "int(v>0)", "512M", PARAMETER_TYPE_SERVER, "version>=8.4.0", "false", ""},
        {LOG_BUFFER_SIZE, "int(v>0)", "2M", PARAMETER_TYPE_SERVER, "version>=8.4.0", "false", ""},
        {
            FORCE_REMOVE_LOG_ARCHIVES,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            ADD_COLUMN_UPDATE_HARD_DEFAULT,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            PLUS_AS_CONCAT,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_CLIENT,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            RETURN_NULL_ON_FUNCTION_ERRORS,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_BOTH,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            NO_BACKSLASH_ESCAPES,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_CLIENT,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            REQUIRE_LIKE_ESCAPE_CHARACTER,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            ALTER_TABLE_CHANGE_TYPE_STRICT,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_BOTH,
            "version>=8.4.0",
            "false",
            ""
        },
        {DEFAULT_WEEK_FORMAT, "int(v>0)", "0", PARAMETER_TYPE_BOTH, "version>=8.4.0", "false", ""},
        {
            GROUP_CONCAT_MAX_LEN,
            "int(v>0)",
            "1024",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HAConfParaConstants.HA_MODE,
            "string(on|off|yes|no|replica)",
            "off",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0&&os=linux",
            "false",
            ""
        },
        {
            HAConfParaConstants.HA_MODE,
            "string(on|off|yes|no)",
            "off",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.0&&version<8.4.0",
            "false",
            ""
        },
        {
            USE_ORDERBY_SORT_LIMIT,
            "bool(yes|no)",
            "yes",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            SESSION_STATE_TIMEOUT,
            "int(v>0)",
            "21600",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            MULTI_RANGE_OPTIMIZATION_LIMIT,
            "int(v>0)",
            "100",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            ACCESS_IP_CONTROL,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            ACCESS_IP_CONTROL_FILE,
            "string",
            "",
            PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            COMPAT_PRIMARY_KEY,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            HOSTVAR_LATE_BINDING,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            ORACLE_STYLE_OUTERJOIN,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_CLIENT,
            "version>=8.2.0",
            "false",
            ""
        },
        {
            HAConfParaConstants.HA_NODE_LIST,
            "bool(yes|no)",
            "no",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.2&&version<=8.4.0&&os=linux",
            "false",
            ""
        },
        {
            HAConfParaConstants.HA_PORT_ID,
            "int(v>=1024&&v<=65535)",
            "",
            PARAMETER_TYPE_SERVER,
            "version>=8.2.2&&version<=8.4.0&&os=linux",
            "false",
            ""
        }
    };
}
