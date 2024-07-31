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
package com.cubrid.cubridmanager.ui.monitoring.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.ui.monitoring.dialog.AddMonitorInstanceDlg;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.CubridStatusMonitorInstance;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.StatusMonInstanceData;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * A action which make edit Monitor instance dialog pop out and complete edit a monitor instance
 * based on different type.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-31 created by lizhiqiang
 */
public class EditMonitorInstanceAction extends SelectionAction {
    public static final String ID = EditMonitorInstanceAction.class.getName();

    /**
     * Constructor
     *
     * @param shell the current shell
     * @param text the used text
     * @param icon the used icon
     */
    public EditMonitorInstanceAction(Shell shell, String text, ImageDescriptor icon) {
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
    protected EditMonitorInstanceAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** Override the run method in order to open an instance of status monitor dialog */
    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode selection = (ICubridNode) obj[0];

        String selectLbl = selection.getLabel();
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        String prefix = QueryOptions.getPrefix(serverInfo);
        String selectionKey = prefix + QueryOptions.MONITOR_FOLDER_NAME + selectLbl;

        CubridStatusMonitorInstance instance = CubridStatusMonitorInstance.getInstance();
        StatusMonInstanceData monData = instance.getData(selectionKey);

        AddMonitorInstanceDlg addMonitorInstanceDlg = new AddMonitorInstanceDlg(getShell());
        addMonitorInstanceDlg.setSelection(selection);
        addMonitorInstanceDlg.setMonData(monData);
        addMonitorInstanceDlg.open();
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
            if (!CubridNodeType.STATUS_MONITOR_TEMPLATE.equals(node.getType())) {
                return false;
            }
            ServerUserInfo userInfo = node.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo == null
                    || StatusMonitorAuthType.AUTH_ADMIN != userInfo.getStatusMonitorAuth()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
