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
package com.cubrid.cubridmanager.ui.common.navigator;

import com.cubrid.common.ui.common.action.ShowToolTipAction;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.navigator.DeferredContentProvider;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.ISelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.common.action.RefreshAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.monstatistic.action.AddMonitorStatisticPageAction;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeTypeManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorDashboardPersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorStatisticPersistManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.contexts.IContextService;

/**
 * CUBRID Monitor dash board navigator view part
 *
 * @author pangqiren
 * @version 1.0 - 2010-5-27 created by pangqiren
 */
public class CubridMonitorNavigatorView extends CubridNavigatorView {

    public static final String ID = "com.cubrid.cubridmanager.dashboard.navigator";

    /**
     * It does't support group nodes.
     *
     * @return false.
     */
    protected boolean isSupportGroup() {
        return false;
    }

    /** Create the navigator */
    protected void createNavigator() {
        tv.setContentProvider(new DeferredContentProvider());
        tv.setLabelProvider(new NavigatorTreeLabelProvider());
        tv.addDoubleClickListener(LayoutManager.getInstance());
        tv.addTreeListener(
                new ITreeViewerListener() {
                    public void treeCollapsed(TreeExpansionEvent event) {
                        CommonUITool.clearExpandedElements(tv);
                    }

                    public void treeExpanded(TreeExpansionEvent event) {
                        CommonUITool.clearExpandedElements(tv);
                    }
                });

        tv.setInput(getTreeViewerInput());
    }

    /**
     * Build the context menu
     *
     * @param menuManager IMenuManager
     */
    protected void buildPopupMenu(final IMenuManager menuManager) {

        IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
        if (selection == null || selection.isEmpty()) {
            ActionManager.addActionToManager(
                    menuManager,
                    ActionManager.getInstance().getAction(AddMonitorDashboardAction.ID));
            ActionManager.addActionToManager(
                    menuManager,
                    ActionManager.getInstance().getAction(AddMonitorStatisticPageAction.ID));
            return;
        }
        ICubridNode node = null;
        Object obj = selection.getFirstElement();
        if (obj instanceof ICubridNode) {
            node = (ICubridNode) obj;
        } else {
            ActionManager.addActionToManager(
                    menuManager,
                    ActionManager.getInstance().getAction(AddMonitorDashboardAction.ID));
            ActionManager.addActionToManager(
                    menuManager,
                    ActionManager.getInstance().getAction(AddMonitorStatisticPageAction.ID));
            return;
        }
        ActionManager.getInstance().setActionsMenu(menuManager);
        if (CubridNodeTypeManager.isCanRefresh(node.getType())) {
            menuManager.add(new Separator());
            ActionManager.addActionToManager(
                    menuManager, ActionManager.getInstance().getAction(RefreshAction.ID));
        }
    }

    /** Active the navigator context */
    protected void activeContext() {
        IContextService contextService =
                (IContextService) getSite().getService(IContextService.class);
        if (contextService != null) {
            contextService.activateContext("com.cubrid.cubridmanager.contexts.navigator");
        }
    }

    /** Add listener */
    protected void addListener() {
        tv.getTree()
                .addMouseListener(
                        new MouseAdapter() {
                            public void mouseUp(MouseEvent event) {
                                if (event.button == 1
                                        && LayoutManager.getInstance().isUseClickOnce()) {
                                    ISelection selection = tv.getSelection();
                                    if (selection == null || selection.isEmpty()) {
                                        return;
                                    }
                                    Object obj =
                                            ((IStructuredSelection) selection).getFirstElement();
                                    if (!(obj instanceof ICubridNode)) {
                                        return;
                                    }
                                    ICubridNode cubridNode = (ICubridNode) obj;
                                    if (CubridNodeType.MONITOR_DASHBOARD.equals(
                                            cubridNode.getType())) {
                                        ISelectionAction action =
                                                (ISelectionAction)
                                                        ActionManager.getInstance()
                                                                .getAction(
                                                                        OpenMonitorDashboardAction
                                                                                .ID);
                                        if (action != null && action.isSupported(cubridNode)) {
                                            action.run();
                                            return;
                                        }
                                    } else {
                                        LayoutManager.getInstance()
                                                .getWorkbenchContrItem()
                                                .openEditorOrView(cubridNode);
                                    }
                                }
                            }
                        });
    }

    /** Call this method when focus */
    public void setFocus() {
        if (tv != null && tv.getControl() != null && !tv.getControl().isDisposed()) {
            tv.getControl().setFocus();
            // select the first element by default
            int count = tv.getTree().getSelectionCount();
            TreeItem[] items = tv.getTree().getItems();
            if (count == 0 && items != null && items.length > 0) {
                tv.getTree().select(items[0]);
            }
            ActionManager.getInstance().changeSelectionProvider(tv);
            LayoutManager.getInstance().changeSelectionProvider(tv);
            CubridNodeManager.getInstance()
                    .addCubridNodeChangeListener(LayoutManager.getInstance());
        }
    }

    /**
     * Get tree viewer
     *
     * @return the TreeViewer
     */
    public TreeViewer getViewer() {
        return tv;
    }

    /**
     * Build the view toolbar
     *
     * @param toolBarManager IToolBarManager
     */
    protected void buildToolBar(IToolBarManager toolBarManager) {
        // empty
    }

    /**
     * Build the view menu
     *
     * @param menuManager IMenuManager
     */
    protected void buildViewMenu(final IMenuManager menuManager) {
        super.buildViewMenu(menuManager);
        menuManager.remove(ShowToolTipAction.ID);
    }

    /**
     * Get the cubrid group node manager
     *
     * @return ICubridGroupNodeManager
     */
    public ICubridGroupNodeManager getGroupNodeManager() {
        return CMGroupNodePersistManager.getInstance();
    }

    /**
     * Get all group items
     *
     * @return Object
     */
    protected Object getGroupItems() {
        return getTreeViewerInput();
    }

    /**
     * Get tooltip string
     *
     * @param cubridNode ICubridNode
     * @return String
     */
    protected String getToolTip(ICubridNode cubridNode) {
        return "";
    }

    /**
     * Return whether support group
     *
     * @return boolean
     */
    public boolean isShowGroup() {
        return false;
    }

    /**
     * Does the node support DND.
     *
     * @param data the draged node.
     * @return support or not.
     */
    protected boolean dataSupportDND(TreeItem[] data) {
        return false;
    }

    /**
     * Does the node support DND.
     *
     * @param data the draged node.
     * @return support or not.
     */
    protected boolean dataSupportDragOver(ICubridNode data) {
        return false;
    }

    public static List<ICubridNode> getTreeViewerInput() {
        List<ICubridNode> nodeList = new ArrayList<ICubridNode>();
        List<ICubridNode> dashboardList =
                MonitorDashboardPersistManager.getInstance().getAllMonitorDashboards();
        List<MonitorStatistic> monitorStatisticList =
                MonitorStatisticPersistManager.getInstance().getMonitorStatisticListByHostId(null);
        nodeList.addAll(dashboardList);
        nodeList.addAll(monitorStatisticList);
        Collections.sort(nodeList);
        return nodeList;
    }
}
