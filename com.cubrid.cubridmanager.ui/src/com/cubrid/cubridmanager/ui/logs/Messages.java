/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

package com.cubrid.cubridmanager.ui.logs;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-4-3 created by wuyingshi
 */
public class Messages extends NLS {

    static {
        NLS.initializeMessages(CubridManagerUIPlugin.PLUGIN_ID + ".logs.Messages", Messages.class);
    }
    // title
    public static String titleCasRunnerResult;
    public static String titleSqlLogAnalyzeResultDialog;
    public static String msgSqlLogAnalyzeResultDialog;
    public static String titleSqlLogFileListDialog;
    public static String msgSqlLogFileListDialog;
    public static String titleCasRunnerConfigDialog;
    public static String msgCasRunnerConfigDialog;
    public static String titleCasRunnerResultDialog;
    public static String msgCasRunnerResultDialog;
    public static String titleLogPropertyDialog;
    public static String titleLogContentDialog;
    public static String titleTimeSetDialog;

    // message related
    public static String errYear;
    public static String errMonth;
    public static String errDay;
    public static String errHour;
    public static String errMinute;
    public static String errSecond;
    public static String warningRemoveLog;
    public static String warningRemoveManagerLog;
    public static String warningResetAdminLog;

    // other
    public static String msgSuccess;
    public static String msgOverwriteFile;
    public static String msgFileSize;
    public static String msgNullLogFile;
    public static String msgDeleteAllLog;
    public static String msgSelectTargeFile;
    public static String msgValidInputDbName;
    public static String msgValidInputBrokerName;
    public static String msgValidInputUserID;
    public static String msgValidInputPassword;

    // label
    public static String labelTargetFileList;
    public static String labelLogFile;
    public static String labelAnalysisResult;
    public static String labelCasLogFile;
    public static String labelLogContents;
    public static String labelExecuteResult;
    public static String labelBrokerName;
    public static String labelUserId;
    public static String labelPassword;
    public static String labelNumThread;
    public static String labelNumRepeatCount;
    public static String labelViewCasRunnerQueryResult;
    public static String labelViewCasRunnerQueryPlan;
    public static String labelDatabase;
    public static String labelCharset;

    // button
    public static String buttonOk;
    public static String buttonCancel;
    public static String buttonClose;
    public static String buttonSaveLogString;
    public static String buttonExecuteOriginalQuery;
    public static String buttonBeforeResultFile;
    public static String buttonNextResultFile;
    public static String buttonDeleteAll;

    // check
    public static String chkAnalizeOptionT;

    // table
    public static String tableIndex;
    public static String tableMax;
    public static String tableMin;
    public static String tableAvg;
    public static String tableTotalCount;
    public static String tableErrCount;
    public static String tableTransactionExeTime;
    public static String tableProperty;
    public static String tableValue;
    public static String tableLogType;
    public static String tableFileName;
    public static String tableFileOwner;
    public static String tableFileSize;
    public static String tableChangeDate;
    public static String tableFilePath;
    public static String tableUser;
    public static String tableTaskName;
    public static String tableTime;
    public static String tableDescription;
    public static String tableNumber;
    public static String tableCasId;
    public static String tableIp;
    public static String tableStartTime;
    public static String tableEndTime;
    public static String tableElapsedTime;
    public static String tableProcessId;
    public static String tableErrorInfo;
    public static String tableErrorType;
    public static String tableErrorCode;
    public static String tableTranId;
    public static String tableErrorId;
    public static String tableErrorMsg;
    public static String tableStatus;
    public static String tableContent;
    // context
    public static String contextCopy;
    public static String viewLogJobName;
    public static String replErrorLogViewName;

    public static String errLogFileNoExist;
    public static String loadLogTaskName;
    public static String loadSqlLogExecResultTaskName;
    public static String removeLogTaskName;
    public static String resetAdminLogTaskName;

    // Error Trace related message
    public static String errorTraceActionName;
    public static String titleErrorTraceDialog;
    public static String msgErrorTraceDialog;
    public static String errCannotFindTraceLog;
    public static String lblErrorTraceResultInfo;

    public static String errCharset;
}
