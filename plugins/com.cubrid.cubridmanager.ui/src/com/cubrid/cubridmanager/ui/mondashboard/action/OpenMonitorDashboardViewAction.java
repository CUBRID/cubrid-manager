/*
o * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.ui.common.navigator.CubridMonitorNavigatorView;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * This action is responsible to open Monitor Dashboard view
 *
 * @author pangqiren
 * @version 1.0 - 2010-5-27 created by pangqiren
 */
public class OpenMonitorDashboardViewAction extends Action {

    private static final Logger LOGGER = LogUtil.getLogger(OpenMonitorDashboardViewAction.class);
    public static final String ID = OpenMonitorDashboardViewAction.class.getName();

    public OpenMonitorDashboardViewAction(String text) {
        super(text);
        this.setId(ID);
    }

    /** Open HA monitor view part */
    public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        boolean isExist = false;
        IViewReference[] viewRefArr = page.getViewReferences();
        if (viewRefArr != null && viewRefArr.length > 0) {
            for (IViewReference viewRef : viewRefArr) {
                String id = viewRef.getId();
                if (CubridMonitorNavigatorView.ID.equals(id)) {
                    IViewPart viewPart = viewRef.getView(true);
                    if (viewPart == null) {
                        isExist = false;
                    } else {
                        page.bringToTop(viewRef.getView(true));
                        isExist = true;
                    }
                    break;
                }
            }
        }
        if (!isExist) {
            try {
                page.showView(CubridMonitorNavigatorView.ID);
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
