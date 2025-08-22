/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package org.cubrid.cubridquery.plugin.querybrowser;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.perspective.IPerspectiveChangedListener;
import com.cubrid.common.ui.perspective.IPerspectiveConstance;
import com.cubrid.common.ui.perspective.PerspectiveChangeEvent;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.MenuProvider;
import com.cubrid.cubridquery.ui.spi.action.CubridMenuProvider;
import com.cubrid.cubridquery.ui.spi.contribution.CubridStatusLineContrItem;
import com.cubrid.cubridquery.ui.spi.contribution.CubridTitleLineContrItem;
import com.cubrid.cubridquery.ui.spi.contribution.CubridWorkbenchContrItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * PerspectiveChangedListener Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-4-18 created by Kevin.Wang
 */
public class PerspectiveChangedListener implements IPerspectiveChangedListener {
    private final CubridStatusLineContrItem cubridStatusLineContrItem =
            new CubridStatusLineContrItem();
    private final CubridTitleLineContrItem cubridTitleLineContrItem =
            new CubridTitleLineContrItem();
    private final CubridWorkbenchContrItem cubridWorkbenchContrItem =
            new CubridWorkbenchContrItem();
    private final MenuProvider menuProvider = new CubridMenuProvider();

    public void showPerspective(PerspectiveChangeEvent event) {
        WorkbenchWindow window =
                (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            ICoolBarManager coolBarManager = window.getCoolBarManager2();
            IMenuManager menuManager = window.getMenuBarManager();
            ActionAdvisor.getInstance().showToolbar(coolBarManager);
            ActionAdvisor.getInstance().showMenu(menuManager);
        }
        ActionManager.getInstance().setMenuProvider(menuProvider);

        LayoutManager.getInstance().setStatusLineContrItem(cubridStatusLineContrItem);
        LayoutManager.getInstance().setTitleLineContrItem(cubridTitleLineContrItem);
        cubridTitleLineContrItem.changeTitleForNavigator(null);
        LayoutManager.getInstance().setWorkbenchContrItem(cubridWorkbenchContrItem);

        CubridNavigatorView navigatorView = CubridNavigatorView.findNavigationView();
        if (navigatorView != null) {
            navigatorView.setFocus();
            if (navigatorView.getViewer() != null) {
                navigatorView.getViewer().refresh(true);
            }
        }
    }

    public void hidePerspectiveHide(PerspectiveChangeEvent event) {
        WorkbenchWindow window =
                (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            ICoolBarManager coolBarManager = window.getCoolBarManager2();
            IMenuManager menuManager = window.getMenuBarManager();
            ActionAdvisor.getInstance().hideToolbar(coolBarManager);
            ActionAdvisor.getInstance().hideMenu(menuManager);
        }
    }

    public String getPerspectiveId() {
        return IPerspectiveConstance.CQB_PERSPECTIVE_ID;
    }

    public void perspectiveChanged(PerspectiveChangeEvent event) {}
}
