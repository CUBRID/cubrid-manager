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
package com.cubrid.cubridmanager.ui.shard;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenient methods for manipulating messages.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-3
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = CubridManagerUIPlugin.PLUGIN_ID + ".shard.Messages";

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getResourceString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    // Common
    public static String btnOK;
    public static String btnCancel;
    public static String btnAdd;
    public static String btnEdit;
    public static String btnDelete;
    public static String btnClose;
    public static String titleConfirm;
    public static String titleSuccess;

    // action
    public static String addShardActionName;
    public static String startShardEnvActionName;
    public static String stopShardEnvActionName;
    public static String stopShardConfirmTitle;
    public static String stopShardConfirmContent;
    public static String shardOffConfirmContent;
    public static String startShardActionName;
    public static String restartShardActionName;
    public static String stopShardActionName;
    public static String showShardsStatusActionName;
    public static String showShardStatusActionName;
    public static String shardEditorPropertyActionName;

    // message
    public static String shardBrokers;
    public static String shardBroker;
    public static String shardConnection;
    public static String shardKey;
    public static String msgUploading;
    public static String msgUploadingSuccess;
    public static String errShardMasterShmIdEmpty;
    public static String errShardAdminLogFileEmpty;
    public static String errParameterValue;
    public static String errMinNumApplServerValue;
    public static String errMaxNumApplServeValue;
    public static String errShardIdExist;
    public static String errShardIdNotNumeric;
    public static String errShardconnectionEmpty;
    public static String errShardConnectionParameterEmpty;
    public static String errShardKeyNameExist;
    public static String errShardKeyParameterNotNumeric;
    public static String errShardIdNotExist;
    public static String errShardKeyEmpty;
    public static String errShardKeyParameterEmpty;
    public static String errShardKeyParameterDataEmpty;
    public static String errShardKeyParameterDataParameterEmpty;
    public static String errShmIdExist;
    public static String errShardNameEmpty;
    public static String errShardBrokerParameterEmpty;
    public static String errShardKeyDataRange;
    public static String errConflictShmId;
    public static String errConflictPort;
    public static String errConflictName;
    public static String errStartShardNotConfigOrFailed;

    // label
    public static String addShardWizard;
    public static String shardGeneralInformation;
    public static String shardName;
    public static String shardBrokerInfomation;
    public static String shardConnnectionFileName;
    public static String addConnection;
    public static String deleteConnection;
    public static String shardKeyFileName;
    public static String shardKeyList;
    public static String AddKey;
    public static String deleteKey;
    public static String shardKeyInfo;
    public static String addData;
    public static String deleteData;

    public static String tblParameter;
    public static String tblValueType;
    public static String tblParamValue;
    public static String tblShardId;
    public static String tblDBName;
    public static String tblConInfo;
    public static String tblKeyColumnName;
    public static String tblMin;
    public static String tblMax;

    public static String errCanNotStartShardBroker;
    public static String errCanNotStopShardBroker;
    public static String msgShardGuide;
    public static String msgExitShardWizard;
    public static String errAddShardBroker;
    public static String restartShardBrokerMsg;
    public static String msgOperating;
    public static String msgOperationFailed;

    // shardBrokerEnvStatusView
    public static String tblBrokerName;
    public static String tblBrokerStatus;
    public static String tblBrokerProcess;
    public static String tblPort;
    public static String tblServer;
    public static String tblQueue;
    public static String tblLongTran;
    public static String tblLongQuery;
    public static String tblErrQuery;
    public static String tblRequest;
    public static String tblAutoAdd;
    public static String tblTps;
    public static String tblQps;
    public static String tblConn;
    public static String tblSession;
    public static String tblSqllog;
    public static String tblLog;
    public static String envHeadTitel;
    public static String envColumnSettingTxt;
}
