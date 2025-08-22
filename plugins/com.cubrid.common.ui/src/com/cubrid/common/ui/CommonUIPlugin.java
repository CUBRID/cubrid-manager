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
package com.cubrid.common.ui;

import com.cubrid.common.ui.common.navigator.NodeAdapterFactory;
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class CommonUIPlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "com.cubrid.common.ui";
    private static CommonUIPlugin plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        Platform.getAdapterManager().registerAdapters(new NodeAdapterFactory(), ICubridNode.class);
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static CommonUIPlugin getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        ImageDescriptor imageDesc = getDefault().getImageRegistry().getDescriptor(path);
        if (imageDesc == null) {
            imageDesc = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
            if (imageDesc != null) {
                getDefault().getImageRegistry().put(path, imageDesc);
            }
        }
        return imageDesc;
    }

    public static Image getImage(String path) {
        Image image = getDefault().getImageRegistry().get(path);
        if (image == null || image.isDisposed()) {
            ImageDescriptor imageDesc = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
            if (CommonUIPlugin.getDefault().getImageRegistry().get(path) != null) {
                CommonUIPlugin.getDefault().getImageRegistry().remove(path);
            }
            CommonUIPlugin.getDefault().getImageRegistry().put(path, imageDesc);
            return CommonUIPlugin.getDefault().getImageRegistry().get(path);
        }
        return image;
    }

    public static IDialogSettings getPluginDialogSettings() {
        IDialogSettings dialogSettings = getDefault().getDialogSettings();
        IDialogSettings pliginDialogSettings = dialogSettings.getSection(PLUGIN_ID);
        if (pliginDialogSettings == null) {
            return dialogSettings.addNewSection(PLUGIN_ID);
        }
        return pliginDialogSettings;
    }

    public static void savePluginDialogSettings() {
        getDefault().saveDialogSettings();
    }

    public static String getSettingValue(String key) {
        return getPluginDialogSettings().get(key);
    }

    public static void putSettingValue(String key, String value) {
        getPluginDialogSettings().put(key, value);
        savePluginDialogSettings();
    }

    public static String getLastCheckedNoticeVersion() {
        return getSettingValue("LastCheckedNoticeVersion");
    }

    public static void setLastCheckedNoticeVersion(String version) {
        putSettingValue("LastCheckedNoticeVersion", version);
        savePluginDialogSettings();
    }

    public static boolean getFindOptionCaseSensitive() {
        return "Y".equals(getSettingValue("FindOptionCaseSensitive"));
    }

    public static void setFindOptionCaseSensitive(boolean isCaseSensitive) {
        putSettingValue("FindOptionCaseSensitive", isCaseSensitive ? "Y" : "N");
        savePluginDialogSettings();
    }

    public static boolean getFindOptionCaseGuard() {
        return "Y".equals(getSettingValue("FindOptionCaseGuard"));
    }

    public static void setFindOptionCaseGuard(boolean isCaseGuard) {
        putSettingValue("FindOptionCaseGuard", isCaseGuard ? "Y" : "N");
        savePluginDialogSettings();
    }

    public static boolean getFindOptionMatchAll() {
        return "Y".equals(getSettingValue("FindOptionMatchAll"));
    }

    public static void setFindOptionMatchAll(boolean isMatchAll) {
        putSettingValue("FindOptionMatchAll", isMatchAll ? "Y" : "N");
        savePluginDialogSettings();
    }
}
