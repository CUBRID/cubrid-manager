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
package com.cubrid.cubridmanager.ui.service;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * <p>Messages Description
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class Messages extends NLS {
    static {
        NLS.initializeMessages(
                CubridManagerUIPlugin.PLUGIN_ID + ".service.Messages", Messages.class);
    }

    public static String columnGroupOrHost;
    public static String columnStatus;
    public static String columnAddress;
    public static String columnPort;
    public static String columnUser;
    public static String columnData;
    public static String columnDataTip;
    public static String columnIndex;
    public static String columnIndexTip;
    public static String columnTemp;
    public static String columnTempTip;
    public static String columnGeneric;
    public static String columnGenericTip;
    public static String columnTps;
    public static String columnQps;
    public static String columnErrorQ;
    public static String columnMemory;
    public static String columnDisk;
    public static String columnCpu;
    public static String columnDbOnOff;
    public static String columnVersion;
    public static String columnBrokerPort;
    public static String taskGetServerVolumeInfo;
    public static String taskGetServerBrokerInfo;
    public static String taskGetServerHostInfo;
    public static String taskGetServerDbInfo;
    public static String taskGetServerEnvInfo;

    public static String serviceDashboardPartToolTip;
    public static String msgConnected;
    public static String msgDisconnected;
    public static String btnImport;
    public static String btnExport;
    public static String btnRefresh;
    public static String msgRefreshConfirm;
}
