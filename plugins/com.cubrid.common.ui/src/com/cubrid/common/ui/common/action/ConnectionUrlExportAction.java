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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.dialog.ExportConnectionDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Export connection action
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 13, 2012 created by Kevin.Wang
 */
public class ConnectionUrlExportAction extends SelectionAction {

    public static final String ID = ConnectionUrlExportAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ConnectionUrlExportAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ConnectionUrlExportAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public void run() {
        Object[] objs = this.getSelectedObj();
        if (objs == null || objs.length == 0) {
            CommonUITool.openErrorBox(Messages.errNoDatabaseSelected);
            return;
        }

        List<ICubridNode> selections = new ArrayList<ICubridNode>();
        Set<CubridDatabase> addedDatabaseSet = new HashSet<CubridDatabase>();

        for (Object obj : objs) {
            if (obj instanceof CubridServer) {
                CubridServer server = (CubridServer) obj;

                selections.add(server);

                List<ICubridNode> children = server.getChildren();
                if (children != null) {
                    for (ICubridNode child : children) {
                        if (child instanceof DefaultCubridNode) {
                            List<ICubridNode> dbChildren =
                                    ((DefaultCubridNode) child).getChildren();
                            for (ICubridNode dbChild : dbChildren) {
                                if (dbChild instanceof CubridDatabase) {
                                    addedDatabaseSet.add((CubridDatabase) dbChild);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Object obj : objs) {
            if (obj instanceof CubridDatabase) {
                if (!addedDatabaseSet.contains(obj)) {
                    selections.add((ICubridNode) obj);
                }
            }
        }

        /*Process export*/
        ExportConnectionDialog dialog = new ExportConnectionDialog(this.getShell(), selections);
        dialog.open();
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        if (obj instanceof CubridServer) {
            CubridServer server = (CubridServer) obj;
            if (server.isConnected()) {
                return true;
            } else {
                return false;
            }
        }

        if (obj instanceof Object[]) {
            for (Object object : (Object[]) obj) {
                if (object instanceof CubridServer) {
                    CubridServer server = (CubridServer) object;
                    if (!server.isConnected()) {
                        return false;
                    }
                }
            }
            return true;
        }

        return true;
    }
}
