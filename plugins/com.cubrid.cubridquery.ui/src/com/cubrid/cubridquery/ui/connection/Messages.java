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

package com.cubrid.cubridquery.ui.connection;

import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class Messages extends NLS {
    static {
        NLS.initializeMessages(
                CubridQueryUIPlugin.PLUGIN_ID + ".connection.Messages", Messages.class);
    }

    // database query dialog related message
    public static String titleNewQueryConnDialog;
    public static String msgNewQueryConnDialog;
    public static String lblConnName;
    public static String errConnNameExist;
    public static String errConnJdbcNotSet;
    public static String lblComment;
    public static String btnSave;
    public static String btnConnect;
    public static String titleSuccess;
    public static String msgConfirmCharset;
    public static String btnTestConn;
    public static String msgTestConnSuccess;
    public static String msgUseDefaultPurpose;

    public static String titleLoginQueryConnDialog;
    public static String msgLoginQueryConnDialog;

    public static String msgConfirmDropConn;

    public static String titleDatabaseVersion;

    // Import connections
    public static String msgSelectWorkspace;
    public static String errInvalidWorkspace;

    public static String lblBackground;

    // multiple database login
    public static String multiDatabaseLoginDialogTitle;
    public static String multiDatabaseLoginDialogMessages;
    public static String multiDatabaseLoginDialogColumnHostAddress;
    public static String multiDatabaseLoginDialogColumnDbName;
    public static String multiDatabaseLoginDialogColumnUser;
    public static String multiDatabaseLoginDialogColumnErrMsg;
    public static String multiDatabaseLoginDialogColumnStatus;
    public static String multiDatabaseLoginDialogStatusLogin;
    public static String multiDatabaseLoginDialogStatusNotLogin;
    public static String multiDatabaseLoginDialogEditLabel;
    public static String multiDatabaseLoginDialogClose;

    // CreateByURLDialog
    public static String titleCreateByURLDialog;
    public static String msgCreateByURLDialog;
    public static String titleInputUrlPage;
    public static String titleConnectionPriviewPage;
    public static String lblConnectionUrl;
    public static String lblDatabaseInfo;
    public static String columnConnectionName;
    public static String columnDBName;
    public static String columnDBHost;
    public static String columnDBPort;
    public static String columnDBUser;
    public static String columnDBPassword;
    public static String columnDBCharset;
    public static String columnConnectAttr;
    public static String errNoParseDatabase;
    public static String msgInfoChangeName;

    // multiQueryConnEditDialog
    public static String multiQueryConnEditDialogTitle;
    public static String multiQueryConnEditDialogMessage;
    public static String multiQueryConnEditErrConnName;
    public static String multiQueryConnEditErrConnName2;
    public static String multiQueryConnEditErrDatabaseName;
    public static String multiQueryConnEditErrBrokerIP;
    public static String multiQueryConnEditErrBrokerPort;
    public static String multiQueryConnEditDialogColConName;
    public static String multiQueryConnEditDialogColConComment;
    public static String multiQueryConnEditDialogColConDBName;
    public static String multiQueryConnEditDialogColConUserName;
    public static String multiQueryConnEditDialogColConPassword;
    public static String multiQueryConnEditDialogColConSavePassword;
    public static String multiQueryConnEditDialogColConBrokerIP;
    public static String multiQueryConnEditDialogColConBrokerport;
    public static String multiQueryConnEditDialogBtnSave;

    // rename connection name
    public static String lblName;
    public static String errConnectionName;
    public static String renameInvalidConnectionNameMSG;
    public static String renameMSGTitle;
    public static String renameDialogMSG;
    public static String renameShellTitle;
    public static String renameOKBTN;
    public static String renameCancelBTN;
    public static String renameConnectionDialogConfirmMsg;

    public static String exportConnectionsSelectPathMsg;
    public static String exportConnectionsSuccessMsg;
    public static String importConnectionsSuccessMsg;
}
