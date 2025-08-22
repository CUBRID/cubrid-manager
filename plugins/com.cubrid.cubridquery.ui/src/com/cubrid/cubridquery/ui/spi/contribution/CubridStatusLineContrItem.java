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
package com.cubrid.cubridquery.ui.spi.contribution;

import com.cubrid.common.ui.common.action.RestoreQueryEditorAction;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.contribution.StatusLineContrItem;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.ApplicationPersistUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridquery.ui.common.Messages;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * CUBRID Query status line contribution item,it show database info(dbName:dbUser) and object number
 * information
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-21 created by pangqiren
 */
@SuppressWarnings("restriction")
public class CubridStatusLineContrItem extends StatusLineContrItem {
    private static final String DB_INFO_CONTR_ID = "QUERY_DB_INFO_CONTR_ID";
    private static final String OBJ_NUM_INFO_CONTR_ID = "QUERY_OBJ_NUM_INFO_CONTR_ID";
    private static final String RESTORE_QUERY_EDITORS_CONTR_ID =
            "MANAGER_RESTORE_QUERY_EDITORS_CONTR_ID";
    private static final String UPDATE_APP_CONTR_ID = "MANAGER_UPDATE_APP_CONTR_ID";

    /**
     * Update the status line information
     *
     * @param statusLineManager StatusLineManager
     * @param cubridNode The selected ICubridNode object
     */
    protected void updateStatusLine(StatusLineManager statusLineManager, ICubridNode cubridNode) {
        clearStatusLine();

        updateStatusLineForRestoreQueryEditor();

        if (!(cubridNode instanceof ISchemaNode)) {
            return;
        }

        StringBuffer dbInfoStrBuffer = new StringBuffer();
        ISchemaNode schemaNode = (ISchemaNode) cubridNode;
        CubridDatabase database = schemaNode.getDatabase();
        if (database == null || database.getDatabaseInfo() == null) {
            return;
        }

        DatabaseInfo dbInfo = database.getDatabaseInfo();
        DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
        if (dbUserInfo == null
                || dbUserInfo.getName() == null
                || dbUserInfo.getName().trim().length() == 0) {
            dbInfoStrBuffer.append(dbInfo.getDbName());
        } else {
            dbInfoStrBuffer.append(dbUserInfo.getName() + "@" + dbInfo.getDbName());
        }

        String brokerPort = database.getDatabaseInfo().getBrokerPort();
        if (brokerPort != null && brokerPort.trim().length() > 0) {
            dbInfoStrBuffer.append(":").append(brokerPort);
        }
        String charset = database.getDatabaseInfo().getCharSet();
        if (charset != null && charset.trim().length() > 0) {
            dbInfoStrBuffer.append(":charset=").append(charset);
        }

        String numberStr = getChilderenNumStr(cubridNode);
        int addWidth = 30;
        if (numberStr != null && numberStr.length() > 0) {
            StatusLineContributionItem item =
                    new StatusLineContributionItem(OBJ_NUM_INFO_CONTR_ID, numberStr.length() + 3);
            statusLineManager.add(item);
            item.setText(numberStr);
            addWidth = 10;
        }

        StatusLineContributionItem item =
                new StatusLineContributionItem(
                        DB_INFO_CONTR_ID, dbInfoStrBuffer.length() + addWidth);
        statusLineManager.add(item);
        item.setText(dbInfoStrBuffer.toString());
    }

    private void updateStatusLineForRestoreQueryEditor() {
        final int countOfRestorableQueryEditors =
                ApplicationPersistUtil.getInstance().countOfRestorableQueryEditorsAtLastSession();
        if (countOfRestorableQueryEditors <= 0) {
            return;
        }

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        StatusLineManager statusLineManager = null;
        if (window instanceof WorkbenchWindow) {
            statusLineManager = ((WorkbenchWindow) window).getStatusLineManager();
        }

        if (statusLineManager == null) {
            return;
        }

        IContributionItem scaleItem =
                new ControlContribution(RESTORE_QUERY_EDITORS_CONTR_ID) {
                    protected Control createControl(Composite parent) {
                        Button btn = new Button(parent, SWT.None);
                        String buttonTitle =
                                Messages.bind(
                                        Messages.restoreQueryEditorTitle,
                                        countOfRestorableQueryEditors);
                        btn.setText(buttonTitle);
                        btn.addSelectionListener(
                                new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent e) {
                                        ActionManager manager = ActionManager.getInstance();
                                        IAction action =
                                                manager.getAction(RestoreQueryEditorAction.ID);
                                        if (action != null
                                                && action instanceof RestoreQueryEditorAction) {
                                            ((RestoreQueryEditorAction) action).run();
                                        }
                                    }
                                });
                        return btn;
                    };
                };
        statusLineManager.add(scaleItem);
    }

    /**
     * Change status line for navigator selection
     *
     * @param selection the ISelection object
     */
    public void changeStuatusLineForNavigator(ISelection selection) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        // window.setStatus("");
        clearStatusLine();

        updateStatusLineForRestoreQueryEditor();

        if (selection == null || selection.isEmpty()) {
            return;
        }
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (!(obj instanceof ICubridNode)) {
            return;
        }
        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
        boolean isShowGroup = false;
        if (navigatorView != null) {
            isShowGroup = navigatorView.isShowGroup();
        }
        ICubridNode cubridNode = (ICubridNode) obj;
        String nodePath = cubridNode.getLabel();
        ICubridNode parent = cubridNode.getParent();
        while (parent != null) {
            if (!isShowGroup && NodeType.GROUP.equals(parent.getType())) {
                break;
            }
            nodePath = parent.getLabel() + "/" + nodePath;
            parent = parent.getParent();
        }
        // window.setStatus(nodePath);
        if (window instanceof WorkbenchWindow) {
            StatusLineManager statusLineManager = ((WorkbenchWindow) window).getStatusLineManager();
            if (statusLineManager != null) {
                updateStatusLine(statusLineManager, cubridNode);
            }
        }
    }

    /** Clear the status line information of CUBRID Query */
    public void clearStatusLine() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        if (window instanceof WorkbenchWindow) {
            StatusLineManager statusLineManager = ((WorkbenchWindow) window).getStatusLineManager();
            if (statusLineManager != null) {
                statusLineManager.remove(RESTORE_QUERY_EDITORS_CONTR_ID);
                statusLineManager.remove(UPDATE_APP_CONTR_ID);
                statusLineManager.remove(DB_INFO_CONTR_ID);
                statusLineManager.remove(OBJ_NUM_INFO_CONTR_ID);
                statusLineManager.update(true);
            }
        }
    }
}
