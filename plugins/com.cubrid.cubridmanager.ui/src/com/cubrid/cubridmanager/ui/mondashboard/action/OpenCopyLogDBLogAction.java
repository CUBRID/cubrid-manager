/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.ui.logs.action.LogViewAction;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DatabaseMonitorPart;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Open the copy log database
 *
 * @author SC13425
 * @version 1.0 - 2010-6-10 created by SC13425
 */
public class OpenCopyLogDBLogAction extends SelectionAction {

    public static final String ID = OpenCopyLogDBLogAction.class.getName();

    /**
     * constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public OpenCopyLogDBLogAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * constructor.
     *
     * @param shell window.getShell()
     * @param provider ISelectionProvider
     * @param text action text
     * @param icon ImageDescriptor
     */
    protected OpenCopyLogDBLogAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * not allow multi selctions
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return boolean false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object
     * @return is supported.
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof DatabaseMonitorPart) {
            DatabaseMonitorPart dbMonPart = (DatabaseMonitorPart) obj;
            DatabaseNode dbNode = (DatabaseNode) dbMonPart.getModel();
            HostNode hostNode = dbNode.getParent();
            if (hostNode != null
                    && hostNode.getServerInfo() != null
                    && hostNode.getServerInfo().isConnected()
                    && (dbNode.getDbStatusType() == DBStatusType.STANDBY
                            || dbNode.getDbStatusType() == DBStatusType.MAINTENANCE)
                    && hostNode.getHostStatusInfo() != null
                    && hostNode.getHostStatusInfo().getMasterHostStatusInfo() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * open apply database log
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0 || !isSupported(objArr[0])) {
            setEnabled(false);
            return;
        }
        DatabaseMonitorPart dbMonPart = (DatabaseMonitorPart) objArr[0];
        DatabaseNode dbNode = (DatabaseNode) dbMonPart.getModel();

        HostNode hostNode = dbNode.getParent();

        ServerInfo serverInfo = hostNode.getServerInfo();
        CubridServer server =
                new CubridServer(
                        serverInfo.getHostAddress(), serverInfo.getHostAddress(), null, null);
        server.setServerInfo(serverInfo);

        LogInfo logInfo = new LogInfo();
        String logPath =
                serverInfo.getEnvInfo().getRootDir()
                        + serverInfo.getPathSeparator()
                        + "log"
                        + serverInfo.getPathSeparator();
        String fileName =
                dbNode.getDbName()
                        + "@"
                        + hostNode.getHostStatusInfo().getMasterHostStatusInfo().getHostName()
                        + "_copylogdb.err";

        logInfo.setPath(logPath + fileName);

        DefaultCubridNode dbLogInfoNode =
                new DefaultCubridNode(
                        dbNode.getDbName() + ICubridNodeLoader.NODE_SEPARATOR + "copy_database_log",
                        logInfo.getName(),
                        "icons/navigator/log_item.png");
        dbLogInfoNode.setType(CubridNodeType.LOGS_COPY_DATABASE_LOG);
        dbLogInfoNode.setModelObj(logInfo);
        dbLogInfoNode.setEditorId(LogEditorPart.ID);
        dbLogInfoNode.setContainer(false);
        dbLogInfoNode.setServer(server);

        LogViewAction action =
                (LogViewAction) ActionManager.getInstance().getAction(LogViewAction.ID);
        action.setCubridNode(dbLogInfoNode);
        action.run();
    }
}
