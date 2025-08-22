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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.ISelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddBrokerMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddDatabaseMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddHostMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DashboardRefreshAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DbDashboardHistoryAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteBrokerMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteDatabaseMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteHostMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.EditAliasNameAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.HideHostAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.HostDashboardHistoryAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.MinimizeFigureAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.MonitorDetailAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenApplyLogDBLogAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenCopyLogDBLogAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenDatabaseLogAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ShowBrokerClientAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ShowBrokerDabaseAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ShowHostAction;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.BrokerDBListMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.BrokerMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.ClientMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DashboardPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DatabaseMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HostMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.MonitorEditPartFacotry;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorDashboardPersistManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Editor of monitor dashboard.
 *
 * @author cyl
 * @version 1.0 - 2010-6-2 created by cyl
 */
public class MonitorDashboardEditor extends GraphicalEditor {

    public static final String ID =
            "com.cubrid.cubridmanager.ui.mondashboard.editor.MonitorDashboardEditor";

    private static final String SEPARATOR = "Separator";

    /** Default constructor of MonitorDashboardEditor */
    public MonitorDashboardEditor() {
        setEditDomain(new DefaultEditDomain(this));
    }

    /**
     * @param site IEditorSite
     * @param input IEditorInput of ICubridNode
     * @throws PartInitException super
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        this.setPartName(input.getName());
    }

    /**
     * Change part name
     *
     * @param partName The String
     */
    public void changePartName(String partName) {
        setPartName(partName);
    }

    /** @see org.eclipse.gef.ui.parts.GraphicalEditor.configureGraphicalViewer */
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();
        ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart();
        viewer.setRootEditPart(rootEditPart);
        viewer.setEditPartFactory(new MonitorEditPartFacotry());

        ZoomManager manager = rootEditPart.getZoomManager();
        double[] zoomLevels = new double[] {1.0, 1.2, 1.4, 1.6, 1.8, 2.0};
        manager.setZoomLevels(zoomLevels);

        getActionRegistry().registerAction(new ZoomInAction(manager));
        getActionRegistry().registerAction(new ZoomOutAction(manager));

        KeyHandler keyHandler = new GefViewerKeyHandler(viewer);
        keyHandler.put(
                KeyStroke.getReleased('', 97, SWT.CONTROL),
                getActionRegistry().getAction(ActionFactory.SELECT_ALL.getId()));

        keyHandler.put(
                KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
                getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

        keyHandler.put(
                KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
                getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

        viewer.setProperty(
                MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL), MouseWheelZoomHandler.SINGLETON);

        viewer.setKeyHandler(keyHandler);

        // initialize context menu
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(
                new IMenuListener() {

                    /**
                     * get menus to show.
                     *
                     * @param manager IMenuManager
                     */
                    public void menuAboutToShow(IMenuManager manager) {
                        IStructuredSelection selection =
                                (IStructuredSelection) getGraphicalViewer().getSelection();
                        String[] ids = new String[] {};
                        if (null == selection
                                || selection.isEmpty()
                                || selection.getFirstElement() instanceof DashboardPart) {
                            manager.add(
                                    getActionRegistry()
                                            .getAction(ActionFactory.SELECT_ALL.getId()));
                            ids =
                                    new String[] {
                                        AddHostMonitorAction.ID,
                                        SEPARATOR,
                                        DashboardRefreshAction.ID
                                    };
                        } else if (selection.getFirstElement() instanceof HostMonitorPart) {
                            ids =
                                    new String[] {
                                        MonitorDetailAction.ID,
                                        DeleteHostMonitorAction.ID,
                                        EditAliasNameAction.ID,
                                        SEPARATOR,
                                        // TODO:Hide the role change until the server side supports
                                        // it well.
                                        // HARoleChangeAction.ID, SEPARATOR,
                                        HostDashboardHistoryAction.ID,
                                        AddDatabaseMonitorAction.ID,
                                        AddBrokerMonitorAction.ID,
                                        SEPARATOR,
                                        HideHostAction.ID,
                                        MinimizeFigureAction.ID,
                                        DashboardRefreshAction.ID
                                    };
                        } else if (selection.getFirstElement() instanceof DatabaseMonitorPart) {
                            ids =
                                    new String[] {
                                        MonitorDetailAction.ID,
                                        DeleteDatabaseMonitorAction.ID,
                                        EditAliasNameAction.ID,
                                        SEPARATOR,
                                        DbDashboardHistoryAction.ID,
                                        OpenApplyLogDBLogAction.ID,
                                        OpenCopyLogDBLogAction.ID,
                                        OpenDatabaseLogAction.ID,
                                        SEPARATOR,
                                        ShowHostAction.ID,
                                        MinimizeFigureAction.ID,
                                        DashboardRefreshAction.ID
                                    };
                        } else if (selection.getFirstElement() instanceof BrokerMonitorPart) {
                            ids =
                                    new String[] {
                                        MonitorDetailAction.ID,
                                        DeleteBrokerMonitorAction.ID,
                                        EditAliasNameAction.ID,
                                        SEPARATOR,
                                        ShowBrokerClientAction.ID,
                                        ShowBrokerDabaseAction.ID,
                                        SEPARATOR,
                                        ShowHostAction.ID,
                                        MinimizeFigureAction.ID,
                                        DashboardRefreshAction.ID
                                    };
                        } else if (selection.getFirstElement() instanceof ClientMonitorPart) {
                            ids = new String[] {EditAliasNameAction.ID};
                        } else if (selection.getFirstElement() instanceof BrokerDBListMonitorPart) {
                            ids = new String[] {EditAliasNameAction.ID};
                        }

                        for (String id : ids) {
                            if (id.equals(SEPARATOR)) {
                                manager.add(new Separator());
                            } else {
                                IAction action = ActionManager.getInstance().getAction(id);
                                ((ISelectionAction) action)
                                        .setSelectionProvider(getGraphicalViewer());
                                ActionManager.addActionToManager(manager, action);
                            }
                        }
                        manager.update(true);
                    }
                });
        viewer.setContextMenu(menuManager);
    }

    /**
     * initialize viewer
     *
     * @see org.eclipse.gef.ui.parts.GraphicalEditor.initializeGraphicalViewer
     */
    protected void initializeGraphicalViewer() {
        IEditorInput input = this.getEditorInput();
        if (input instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) input;
            Dashboard dashboard = (Dashboard) node.getAdapter(Dashboard.class);
            getGraphicalViewer().setContents(dashboard);
            dashboard.refresh();
        }
    }

    /**
     * Save current dashboard to file.
     *
     * @param monitor progress monitor
     */
    public void doSave(IProgressMonitor monitor) {
        // do nothing.auto saved before dispose.
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#isDirty()
     * @return <code>true</code> if the contents have been modified and need saving, and <code>false
     *     </code> if they have not changed since the last save
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * Auto save dashboard before editor is disposed.
     *
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#dispose()
     */
    public void dispose() {
        MonitorDashboardPersistManager.getInstance().saveDashboard();
        super.dispose();
    }

    /** Call this method when this editor is focus */
    public void setFocus() {
        super.setFocus();
        LayoutManager.getInstance()
                .getTitleLineContrItem()
                .changeTitleForViewOrEditPart(null, this);
        LayoutManager.getInstance()
                .getStatusLineContrItem()
                .changeStuatusLineForViewOrEditPart(null, this);
    }
}
