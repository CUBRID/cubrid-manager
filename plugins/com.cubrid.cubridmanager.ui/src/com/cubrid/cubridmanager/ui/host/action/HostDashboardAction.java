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
package com.cubrid.cubridmanager.ui.host.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.host.editor.HostDashboardEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * The Host Dashboard
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-9-26 created by Kevin.Wang
 */
public class HostDashboardAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(HostDashboardAction.class);
    public static final String ID = HostDashboardAction.class.getName();

    public HostDashboardAction(Shell shell, String text) {
        this(shell, text, null);
    }

    public HostDashboardAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public HostDashboardAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public void run() {
        final Object[] obj = this.getSelectedObj();
        if (obj == null || obj.length <= 0) {
            setEnabled(false);
            return;
        }

        Object object = obj[0];
        if (!(object instanceof CubridServer)) {
            setEnabled(false);
            return;
        }

        CubridServer cubridServer = (CubridServer) object;
        if (cubridServer.getServerInfo() == null) {
            LOGGER.error("cubridServer is null.");
            return;
        }

        doRun(cubridServer.getServerInfo());
    }

    public void doRun(ServerInfo serverInfo) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        // Check it open same editor
        HostDashboardEditor editorPart = getOpenedEditorPart(serverInfo.getServerName());
        if (editorPart == null) {
            HostDashboardEditorInput editorInput = new HostDashboardEditorInput(serverInfo);
            editorInput.setName(serverInfo.getServerName());
            editorInput.setToolTipText(serverInfo.getServerName());
            try {
                editorPart =
                        (HostDashboardEditor)
                                window.getActivePage()
                                        .openEditor(editorInput, HostDashboardEditor.ID);
            } catch (PartInitException ex) {
                LOGGER.error(ex.getMessage());
            }
        } else {
            window.getActivePage().activate(editorPart);
            editorPart.loadAllData();
        }
    }

    public void closeEditor(ServerInfo serverInfo) {
        HostDashboardEditor editor = getOpenedEditorPart(serverInfo.getServerName());
        if (editor != null) {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) {
                return;
            }

            window.getActivePage().closeEditor(editor, true);
        }
    }

    /**
     * Get all opened query editor
     *
     * @return
     */
    private HostDashboardEditor getOpenedEditorPart(String serverName) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();

        for (IEditorReference reference : editorReferences) {
            if (reference.getId().equals(HostDashboardEditor.ID)) {
                HostDashboardEditor editor = (HostDashboardEditor) reference.getEditor(false);
                if (editor != null
                        && editor.getServerInfo() != null
                        && editor.getServerInfo().getServerName().equals(serverName)) {
                    return editor;
                }
            }
        }
        return null;
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object object) {
        final Object[] obj = this.getSelectedObj();
        if (obj == null || obj.length <= 0) {
            return false;
        }

        if (!(object instanceof CubridServer)) {
            return false;
        }

        CubridServer server = (CubridServer) object;
        if (!server.isConnected()) {
            return false;
        }

        return true;
    }
}
