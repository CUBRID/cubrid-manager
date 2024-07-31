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
package com.cubrid.common.ui.perspective;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * AbsActionAdvisor Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-4-23 created by Kevin.Wang
 */
public abstract class AbsActionAdvisor {
    public void hideMenu(IMenuManager menuManager) {
        for (IContributionItem item : menuManager.getItems()) {
            if (!IWorkbenchActionConstants.M_HELP.equals(item.getId())
                    && !IPerspectiveConstance.MIGRATION_MENU_ID.equals(item.getId())) {
                menuManager.remove(item);
            }
        }
        menuManager.update(true);
    }

    public void hideToolbar(ICoolBarManager coolBarManager) {
        for (IContributionItem item : coolBarManager.getItems()) {
            if (item instanceof ToolBarContributionItem && "new1".equals(item.getId())) {
                ToolBarContributionItem toolBarContributionItem = (ToolBarContributionItem) item;
                for (IContributionItem c : toolBarContributionItem.getToolBarManager().getItems()) {
                    if (!IPerspectiveConstance.HELP_ACTION_CONTRIBUTION_ID.equals(c.getId())
                            && !IPerspectiveConstance.SEARCH_ACTION_CONTRIBUTION_ID.equals(
                                    c.getId())
                            && !IPerspectiveConstance.MIGRATION_ACTION_CONTRIBUTION_ID.equals(
                                    c.getId())
                            && !IPerspectiveConstance.PERSPECTIVE_ACTION_CONTRIBUTION_ID.equals(
                                    c.getId())) {
                        toolBarContributionItem.getToolBarManager().remove(c);
                    } else {
                        break;
                    }
                }
            }
        }

        coolBarManager.update(true);
    }

    protected IToolBarManager getToolbarManaeger(ICoolBarManager coolBarManager) {
        IToolBarManager newToolbarManager = null;
        for (IContributionItem item : coolBarManager.getItems()) {
            if (item instanceof ToolBarContributionItem && "new1".equals(item.getId())) {
                ToolBarContributionItem toolBarContributionItem = (ToolBarContributionItem) item;
                newToolbarManager = toolBarContributionItem.getToolBarManager();
                break;
                // }
            }
        }
        return newToolbarManager;
    }

    protected String getToolbarInsertPoint(ICoolBarManager coolBarManager) {
        String id = "";
        for (IContributionItem item : coolBarManager.getItems()) {
            if (item instanceof ToolBarContributionItem && "new1".equals(item.getId())) {
                ToolBarContributionItem toolBarContributionItem = (ToolBarContributionItem) item;
                for (IContributionItem c : toolBarContributionItem.getToolBarManager().getItems()) {
                    if (c.isVisible()
                            && getToolbarContributionItemLevel(id)
                                    < getToolbarContributionItemLevel(c.getId())) {
                        id = c.getId();
                    }
                }
            }
        }
        return id;
    }

    protected String getMenuInsertPoint(IMenuManager menuManager) {
        String id = "";
        for (IContributionItem item : menuManager.getItems()) {
            if (item.isVisible()
                    && getMenuContributionItemLevel(id)
                            < getMenuContributionItemLevel(item.getId())) {
                id = item.getId();
            }
        }
        return id;
    }

    private int getMenuContributionItemLevel(String id) {
        if (IWorkbenchActionConstants.M_HELP.equals(id)) {
            return 1;
        } else if (IPerspectiveConstance.MIGRATION_MENU_ID.equals(id)) {
            return 2;
        } else {
            return 0;
        }
    }

    private int getToolbarContributionItemLevel(String id) {
        if (IPerspectiveConstance.SEARCH_ACTION_CONTRIBUTION_ID.equals(id)) {
            return 1;
        }
        if (IPerspectiveConstance.HELP_ACTION_CONTRIBUTION_ID.equals(id)) {
            return 2;
        }
        if (IPerspectiveConstance.MIGRATION_ACTION_CONTRIBUTION_ID.equals(id)) {
            return 3;
        }
        if (IPerspectiveConstance.PERSPECTIVE_ACTION_CONTRIBUTION_ID.equals(id)) {
            return 4;
        }
        return 0;
    }
}
