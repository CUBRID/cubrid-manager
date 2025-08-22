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
package com.cubrid.common.ui.external.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Launch CUBRID Manager
 *
 * @author PCraft
 * @version 1.0 - 2013-01-22 created by PCraft
 */
public class LaunchManagerAction extends Action {
    private static final Logger LOGGER = LogUtil.getLogger(LaunchManagerAction.class);

    public static final String ID = LaunchManagerAction.class.getName();
    public static final String WINDOWS_CM_EXECUTE_FILE = "cubridmanager.exe";
    public static final String LINUX_CM_EXECUTE_FILE = "cubridmanager";
    public static final String OSX_CM_EXECUTE_FILE = "cubridmanager.app";
    public static final String DEFAULT_CM_WORK_PATH = "C:/CUBRID/cubridmanager/";
    public static final String FTP_DOWNLOAD_URL =
            "ftp://ftp.cubrid.org/CUBRID_Tools/CUBRID_Manager/";

    public LaunchManagerAction(String text, ImageDescriptor image) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
    }

    public void run() {
        String cmWorkpath = GeneralPreference.getExternalManagerPath();
        if (StringUtil.isEmpty(cmWorkpath)) {
            cmWorkpath = findProgramOnDefaultPath();
        }

        // Confirm where is CM?
        if (StringUtil.isEmpty(cmWorkpath)) {
            Shell sh = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            int res =
                    CommonUITool.openMsgBox(
                            sh,
                            MessageDialog.WARNING,
                            Messages.titleConfirm,
                            Messages.confirmUseCM,
                            new String[] {
                                com.cubrid.common.ui.spi.Messages.btnYes,
                                com.cubrid.common.ui.spi.Messages.btnNo,
                                Messages.btnInstall
                            });
            if (res == 1) {
                return;
            } else if (res == 2) {
                try {
                    PlatformUI.getWorkbench()
                            .getBrowserSupport()
                            .getExternalBrowser()
                            .openURL(new URL(FTP_DOWNLOAD_URL));
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return;
            }

            cmWorkpath = findProgram();
        }

        String exePath = cmWorkpath + getExecuteFileName();
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec(new String[] {exePath}, null, new File(cmWorkpath));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String findProgramOnDefaultPath() {
        String path = DEFAULT_CM_WORK_PATH + WINDOWS_CM_EXECUTE_FILE;
        if (Util.isWindows() && new File(path).exists()) {
            return DEFAULT_CM_WORK_PATH;
        }

        return null;
    }

    private String getExecuteFileName() {
        if (Util.isWindows()) {
            return WINDOWS_CM_EXECUTE_FILE;
        } else if (Util.isMac() || Util.isCarbon() || Util.isCocoa()) {
            return OSX_CM_EXECUTE_FILE;
        } else {
            return LINUX_CM_EXECUTE_FILE;
        }
    }

    private String findProgram() {
        return null;
    }
}
