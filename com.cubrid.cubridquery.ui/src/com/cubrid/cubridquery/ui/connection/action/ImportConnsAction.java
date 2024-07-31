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
package com.cubrid.cubridquery.ui.connection.action;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.Messages;
import java.io.File;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Import the connections action
 *
 * @author pangqiren
 * @version 1.0 - 2011-8-22 created by pangqiren
 */
public class ImportConnsAction extends Action {

    public static final String ID = ImportConnsAction.class.getName();

    /**
     * The constructor
     *
     * @param text String
     * @param image ImageDescriptor
     */
    public ImportConnsAction(String text, ImageDescriptor image) {
        super(text);
        this.setId(ID);
        setImageDescriptor(image);
    }

    /** Import hosts and groups */
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
        String filePath = CommonUIPlugin.getSettingValue("IMPORT_HOSTS_WORKSPACE_KEY");
        if (null != filePath) {
            dialog.setFilterPath(filePath);
        }
        dialog.setMessage(Messages.msgSelectWorkspace);
        filePath = dialog.open();
        if (filePath == null) {
            return;
        }
        final String workspacePath = filePath;
        filePath =
                filePath
                        + File.separator
                        + ".metadata"
                        + File.separator
                        + ".plugins"
                        + File.separator
                        + "org.eclipse.core.runtime"
                        + File.separator
                        + ".settings"
                        + File.separator
                        + "com.cubrid.cubridquery.ui.prefs";
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            CommonUITool.openErrorBox(Messages.errInvalidWorkspace);
            return;
        } else {
            CommonUIPlugin.putSettingValue("IMPORT_HOSTS_WORKSPACE_KEY", workspacePath);
        }
        BusyIndicator.showWhile(
                Display.getDefault(),
                new Runnable() {
                    public void run() {
                        CQBGroupNodePersistManager groupNodePersistManager =
                                CQBGroupNodePersistManager.getInstance();
                        boolean isImportDb =
                                CQBDBNodePersistManager.getInstance().loadDatabases(workspacePath);
                        boolean isImportGroup =
                                groupNodePersistManager.loadGroupNode(workspacePath);
                        if (isImportDb || isImportGroup) {
                            CubridQueryNavigatorView view =
                                    (CubridQueryNavigatorView)
                                            CubridNavigatorView.getNavigatorView(
                                                    CubridQueryNavigatorView.ID);
                            if (view != null && view.getViewer() != null) {
                                view.getViewer().refresh(true);
                            }
                        }
                    }
                });
    }
}
