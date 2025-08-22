/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.monstatistic.action;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monstatistic.task.GetMonitorIntervalTask;
import com.cubrid.cubridmanager.core.monstatistic.task.SetMonitorIntervalTask;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.dialog.EditMonitorIntervalDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * A action which user can see the monitor interval and change the monitor interval(if "admin"
 * user).
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-06-25 created by Santiago Wang
 */
public class CheckMonitorIntervalAction extends SelectionAction {
    public static final String ID = CheckMonitorIntervalAction.class.getName();

    private final String CM_USER_ADMIN = "admin";

    /**
     * Constructor
     *
     * @param shell the current shell
     * @param text the used text
     * @param icon the used icon
     */
    public CheckMonitorIntervalAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * Constructor
     *
     * @param shell the current shell
     * @param provider the selected provider
     * @param text the used text
     * @param icon the used icon
     */
    protected CheckMonitorIntervalAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** Override the run method in order to open an instance of status monitor dialog */
    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode selection = (ICubridNode) obj[0];

        ServerInfo serverInfo = selection.getServer().getServerInfo();
        GetMonitorIntervalTask getMonitorIntervalTask = new GetMonitorIntervalTask(serverInfo);
        getMonitorIntervalTask.run();
        String interval = null;
        if (getMonitorIntervalTask.isSuccess()) {
            interval = getMonitorIntervalTask.getInterval();
        } else {
            CommonUITool.openErrorBox(getMonitorIntervalTask.getErrorMsg());
            return;
        }
        boolean isEditable = serverInfo.getUserName().equals(CM_USER_ADMIN);

        EditMonitorIntervalDialog dialog = new EditMonitorIntervalDialog(getShell());
        dialog.setInterval(interval);
        dialog.setEditable(isEditable);

        if (dialog.open() == Dialog.OK && dialog.isEditEnable()) {
            long newInterval = Long.parseLong(dialog.getInterval());
            SetMonitorIntervalTask setMonitorIntervalTask = new SetMonitorIntervalTask(serverInfo);
            setMonitorIntervalTask.setInterval(newInterval);
            setMonitorIntervalTask.run();
            if (setMonitorIntervalTask.isSuccess()) {
                CommonUITool.openInformationBox(
                        Messages.bind(Messages.msgChangeMonIntervalSuccess, newInterval));
            } else {
                CommonUITool.openErrorBox(setMonitorIntervalTask.getErrorMsg());
            }
        }
    }

    /**
     * Makes this action not support for select multiple object
     *
     * @return boolean true if allowed ,false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj Object the given object
     * @return boolean true if is supported , false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (!CubridNodeType.MONITOR_FOLDER.equals(node.getType())) {
                return false;
            }
            ServerInfo serverInfo = node.getServer().getServerInfo();
            if (serverInfo == null || !CompatibleUtil.isSupportMonitorStatistic(serverInfo)) {
                return false;
            } else if (!serverInfo.isSupportMonitorStatistic()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
