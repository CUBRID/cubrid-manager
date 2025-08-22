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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.TransactionInfoDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * This action is responsible to show transaction info.
 *
 * @author robin 2009-3-9
 */
public class TransactionInfoAction extends SelectionAction {
    public static final String ID = TransactionInfoAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(TransactionInfoAction.class);

    private final String USER_DBA = "dba";

    public TransactionInfoAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public TransactionInfoAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        if (obj instanceof CubridDatabase) {
            DatabaseInfo dbInfo = ((CubridDatabase) obj).getDatabaseInfo();
            if (CompatibleUtil.isAfter844(dbInfo.getServerInfo())) {
                if (dbInfo.isLogined()
                        && dbInfo.getAuthLoginedDbUserInfo() != null
                        && dbInfo.getAuthLoginedDbUserInfo().isDbaAuthority()) {
                    return true;
                }
            } else {
                if (dbInfo.isLogined()
                        && dbInfo.getAuthLoginedDbUserInfo() != null
                        && dbInfo.getAuthLoginedDbUserInfo().getName() != null
                        && USER_DBA.equals(
                                dbInfo.getAuthLoginedDbUserInfo().getName().toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ISchemaNode node = (ISchemaNode) obj[0];
        final CubridDatabase database = node.getDatabase();
        if (database == null) {
            CommonUITool.openErrorBox(getShell(), Messages.msgSelectDB);
            return;
        }
        TransactionInfoDialog dlg = new TransactionInfoDialog(getShell());
        dlg.setDatabase(database);
        try {
            if (dlg.loadData(getShell())) {
                dlg.open();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
