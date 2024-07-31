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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.UrlConnUtil;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Report bug action
 *
 * @author pangqiren
 * @version 1.0 - 2011-7-25 created by pangqiren
 */
public class ReportBugAction extends SelectionAction {

    private final Logger LOGGER = LogUtil.getLogger(getClass());
    public static final String ID = ReportBugAction.class.getName();
    public static final String ID_BIG = ReportBugAction.class.getName() + "Big";
    private String currentVersion = null;

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    protected ReportBugAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor icon,
            boolean isBig) {
        super(shell, provider, text, icon);
        if (isBig) {
            setId(ID_BIG);
        } else {
            setId(ID);
        }

        setToolTipText(text);
        setEnabled(true);
    }

    public ReportBugAction(Shell shell, String text, ImageDescriptor icon, boolean isBig) {
        this(shell, null, text, icon, isBig);
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    /** Open bug report page */
    public void run() {

        String url =
                Platform.getNL().equals("ko_KR")
                        ? UrlConnUtil.REPORT_BUG_URL_KO
                        : UrlConnUtil.REPORT_BUG_URL_EN;
        url = CommonUITool.urlEncodeForSpaces(url);
        try {
            PlatformUI.getWorkbench()
                    .getBrowserSupport()
                    .getExternalBrowser()
                    .openURL(new URL(url));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Return whether to allow multi selection
     *
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Return whether support this object
     *
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return true;
    }
}
