/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.dialog.OIDNavigatorDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Show find data by OID dialog
 *
 * @author pangqiren
 * @version 1.0 - 2009-06-04 created by pangqiren
 * @version 1.1 - 2012-09-05 created by Isaiah Choe
 */
public class OIDNavigatorAction extends SelectionAction {

    public static final String ID = OIDNavigatorAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(OIDNavigatorAction.class);

    public OIDNavigatorAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public OIDNavigatorAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(obj, null, false);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            this.setEnabled(false);
            return;
        }

        ISchemaNode node = (ISchemaNode) obj[0];
        CubridDatabase database = node.getDatabase();
        Connection conn = null;
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);
            OIDNavigatorDialog dialog = new OIDNavigatorDialog(getShell(), conn, null);
            dialog.open();
        } catch (SQLException e) {
            LOGGER.error("Can not request oid information by jdbc.", e);
            String message =
                    Messages.bind(
                            Messages.errCommonTip,
                            e.getErrorCode(),
                            e.getMessage()); // TODO: error message localizing
            CommonUITool.openErrorBox(message);
        } finally {
            QueryUtil.freeQuery(conn);
        }
    }
}
