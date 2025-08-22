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
package com.cubrid.common.ui.spi.contribution;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Title line contribution item, it show the title information
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-9 created by pangqiren
 */
public class TitleLineContrItem {

    /**
     * Get product name
     *
     * @return String
     */
    public String getProductName() {
        String productName = "";
        String viewName = "";
        if (ApplicationUtil.getApplicationType().equals(ApplicationType.CUBRID_MANAGER)) {
            productName = Messages.productNameCM;
        } else if (ApplicationUtil.getApplicationType()
                .equals(ApplicationType.CUBRID_QUERY_BROWSER)) {
            productName = Messages.productNameCQB;
        }

        if (ApplicationType.CUBRID_MANAGER.equals(
                PerspectiveManager.getInstance().getCurrentMode())) {
            viewName = Messages.viewNameCM;
        } else if (ApplicationType.CUBRID_QUERY_BROWSER.equals(
                PerspectiveManager.getInstance().getCurrentMode())) {
            viewName = Messages.viewNameCQB;
        }

        return productName + " - " + viewName;
    }

    /**
     * Change application title when click navigator tree
     *
     * @param selection the ISelection object
     */
    public void changeTitleForNavigator(ISelection selection) {
        String title = "";
        if (selection != null && !selection.isEmpty()) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj instanceof ICubridNode) {
                ICubridNode cubridNode = (ICubridNode) obj;
                title = getTitleForNavigator(cubridNode);
            }
        }
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        if (shell != null) {
            String productName = getProductName();
            shell.setText(productName);
            if (title != null && title.trim().length() > 0) {
                shell.setText(productName + " - " + title);
            }
        }
    }

    /**
     * Change application title when focus view part or editor part(not include query editor)
     *
     * @param cubridNode the ICubridNode object
     * @param workbenchPart the IWorkbenchPart object
     */
    public void changeTitleForViewOrEditPart(ICubridNode cubridNode, IWorkbenchPart workbenchPart) {
        String title = getTitleForViewOrEdit(cubridNode, workbenchPart);
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        if (shell != null) {
            shell.setText(getProductName());
            if (title != null && title.trim().length() > 0) {
                shell.setText(getProductName() + " - " + title);
            }
        }
    }

    /**
     * Change title line for query editor
     *
     * @param cubridNode the ICubridNode object
     */
    public void changeTitleForQueryEditor(ICubridNode cubridNode) {
        if (cubridNode == null) {
            return;
        }
        CubridDatabase database = null;
        String title = null;
        if (cubridNode instanceof ISchemaNode) {
            ISchemaNode schemaNode = (ISchemaNode) cubridNode;
            database = schemaNode.getDatabase();
            title = getTitleForQueryEditor(database);
        }
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        if (shell == null || shell.isDisposed()) {
            return;
        }
        String productName = getProductName();
        if (DatabaseNavigatorMenu.NULL_DATABASE.equals(database)) {
            shell.setText(productName + " - " + DatabaseNavigatorMenu.NO_DATABASE_SELECTED_LABEL);
        } else {
            shell.setText(productName);
            if (title != null && title.trim().length() > 0) {
                shell.setText(productName + " - " + title);
            }
        }
    }

    /**
     * Get title of application for navigator
     *
     * @param cubridNode the ICubridNode object
     * @return String
     */
    protected String getTitleForNavigator(ICubridNode cubridNode) {
        return "";
    }

    /**
     * Get title of application for query editor
     *
     * @param cubridNode ICubridNode
     * @return String
     */
    protected String getTitleForQueryEditor(ICubridNode cubridNode) {
        return "";
    }

    /**
     * Get the title of application for view or editor(not including query editor)
     *
     * @param cubridNode the ICubridNode object
     * @param workbenchPart the IWorkbenchPart object
     * @return String
     */
    protected String getTitleForViewOrEdit(ICubridNode cubridNode, IWorkbenchPart workbenchPart) {
        return "";
    }
}
