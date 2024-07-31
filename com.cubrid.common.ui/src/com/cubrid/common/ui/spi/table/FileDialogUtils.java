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
package com.cubrid.common.ui.spi.table;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * File related dialog utility
 *
 * @author pangqiren 2009-6-4
 */
public final class FileDialogUtils {
    public static final String EXPORT_FILE_PATH_KEY = "Key_Export_Field_value";

    private FileDialogUtils() {}

    /**
     * Get imported file
     *
     * @param shell Shell
     * @return File
     */
    public static File getImportedFile(Shell shell, String[] extensions) {
        FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
        String filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        if (filepath != null) {
            dialog.setFilterPath(filepath);
        }
        if (extensions == null || extensions.length == 0) {
            extensions = new String[] {"*.*"};
        }
        dialog.setFilterExtensions(extensions);
        dialog.setFilterNames(extensions);
        String filePath = dialog.open();
        if (filePath != null) {
            File file = new File(filePath);
            CommonUIPlugin.putSettingValue(EXPORT_FILE_PATH_KEY, file.getParent());
            return file;
        }
        return null;
    }

    /**
     * Get saved file
     *
     * @param shell Shell
     * @param filterExts String[]
     * @param filterNames String[]
     * @param defaultExtName String
     * @param filterPath String
     * @return File
     */
    public static File getDataExportedFile(
            Shell shell, String[] filterExts, String[] filterNames, String defaultExtName) {
        FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        String filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        if (filepath == null || filepath.trim().length() == 0) {
            filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        }
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }
        if (null != filterExts) {
            dialog.setFilterExtensions(filterExts);
        }
        if (null != filterNames) {
            dialog.setFilterNames(filterNames);
        }

        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            File tmpFile = new File(filePath);
            if (tmpFile.exists() && !handelFileExist(filePath)) {
                return null;
            }
            File file = new File(filePath);
            if (file != null) {
                CommonUIPlugin.putSettingValue(
                        FileDialogUtils.EXPORT_FILE_PATH_KEY, file.getParent());
            }
            return file;
        }
    }

    /**
     * Handle with file exist
     *
     * @param filePath String
     * @return boolean
     */
    public static boolean handelFileExist(String filePath) {
        File tmpFile = new File(filePath);
        boolean isConfirm =
                CommonUITool.openConfirmBox(
                        Messages.bind(Messages.msgFileExist, tmpFile.getName()));
        if (!isConfirm) {
            return false;
        }
        String bakfile = filePath + ".bak";
        File oldBackFile = new File(bakfile);
        if (oldBackFile.exists() && !oldBackFile.delete()) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errFileDelete, oldBackFile.getName()));
            return false;
        }
        if (!tmpFile.renameTo(new File(bakfile))) {
            CommonUITool.openErrorBox(
                    Messages.bind(
                            Messages.errFileRename,
                            new String[] {tmpFile.getName(), oldBackFile.getName()}));
            return false;
        }
        return true;
    }

    /**
     * Get saved file
     *
     * @param shell Shell
     * @param filterPath String
     * @return File
     */
    public static File getDataExportedDir(Shell shell, String filterPath) {
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        String filepath = filterPath;
        if (filepath == null || filepath.trim().length() == 0) {
            filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        }
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }

        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            File file = new File(filePath);
            if (file != null) {
                CommonUIPlugin.putSettingValue(
                        FileDialogUtils.EXPORT_FILE_PATH_KEY, file.getAbsolutePath());
            }
            return file;
        }
    }
}
