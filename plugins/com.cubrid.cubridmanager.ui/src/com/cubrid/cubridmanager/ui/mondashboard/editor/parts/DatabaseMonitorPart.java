/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.editor.parts;

import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.ui.mondashboard.editor.DatabaseDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.DatabaseMonitorFigure;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.FlashSupport;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.DatabaseMonitorPartHelper;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.policy.DatabaseMonitorEditPolicy;
import java.beans.PropertyChangeEvent;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * database monitor edit part.
 *
 * @author cyl
 * @version 1.0 - 2010-6-2 created by cyl
 */
public class DatabaseMonitorPart extends HANodePart {

    private final DatabaseMonitorPartHelper dbMonitorPartHelper;

    /** The constructor */
    public DatabaseMonitorPart() {
        dbMonitorPartHelper = new DatabaseMonitorPartHelper();
    }

    /**
     * get a database monitor figure.
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     * @return a database figure
     */
    protected IFigure createFigure() {
        DatabaseMonitorFigure dbMonitorFigure = new DatabaseMonitorFigure();
        DatabaseNode databaseNode = (DatabaseNode) getModel();
        HostNode hostNode = databaseNode.getParent();
        dbMonitorFigure.setTitle(databaseNode.getName());
        dbMonitorFigure.setHostName(hostNode.getName());
        String statusText = DBStatusType.getShowText(databaseNode.getDbStatusType());
        dbMonitorFigure.setStatus(
                true,
                hostNode.isConnected(),
                databaseNode.isConnected(),
                statusText,
                databaseNode.getErrorMsg(),
                databaseNode.hasNewErrorMsg());
        dbMonitorFigure.setHint(
                new StringBuffer(" ")
                        .append(databaseNode.getDbName())
                        .append("@")
                        .append(hostNode.getIp())
                        .append(":")
                        .append(hostNode.getPort())
                        .append(" ")
                        .toString());
        return dbMonitorFigure;
    }

    /**
     * Initialize edit policies of edit part
     *
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new DatabaseMonitorEditPolicy());
    }

    /**
     * Method to be executed when model's property changed.
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * @param evt PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (DatabaseNode.PROP_DB_STATUS.equals(evt.getPropertyName())) {
            refreshVisuals();
            getParent().refresh();
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
     * @param req Request
     */
    public void performRequest(Request req) {
        if (req.getType() == RequestConstants.REQ_OPEN) {
            DatabaseNode databaseNode = (DatabaseNode) getModel();
            if (!databaseNode.isConnected()) {
                return;
            }
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) {
                return;
            }
            IWorkbenchPage page = window.getActivePage();
            if (page == null) {
                return;
            }
            HostNode hn = databaseNode.getParent();
            String secondaryId =
                    new StringBuffer(databaseNode.getDbName())
                            .append("&")
                            .append(hn.getUserName())
                            .append("&")
                            .append(hn.getIp())
                            .append("&")
                            .append(hn.getPort())
                            .toString();
            IViewReference viewReference =
                    page.findViewReference(DatabaseDashboardViewPart.ID, secondaryId);
            if (viewReference == null) {
                try {
                    IViewPart viewPart =
                            page.showView(
                                    DatabaseDashboardViewPart.ID,
                                    secondaryId,
                                    IWorkbenchPage.VIEW_ACTIVATE);
                    ((DatabaseDashboardViewPart) viewPart).init(databaseNode);
                } catch (PartInitException ex) {
                    viewReference = null;
                }
            } else {
                IViewPart viewPart = viewReference.getView(false);
                window.getActivePage().bringToTop(viewPart);
                ((DatabaseDashboardViewPart) viewPart).init(databaseNode);
            }
        }
    }

    /**
     * Refreshes this EditPart's <i>visuals</i>. This method is called by {@link #refresh()}, and
     * may also be called in response to notifications from the model.
     */
    protected void refreshVisuals() {
        DatabaseMonitorFigure dmfg = (DatabaseMonitorFigure) this.getFigure();
        DatabaseNode dn = (DatabaseNode) this.getModel();
        dmfg.setTitle(dn.getName());
        HADatabaseStatusInfo haDatabaseStatus = dn.getHaDatabaseStatus();
        String dbStatus = "";
        if (haDatabaseStatus != null) {
            dmfg.setCpuUsage(haDatabaseStatus.getCpuUsage());
            dmfg.setMemUsage(haDatabaseStatus.getMemUsage());
            dmfg.setDelay(haDatabaseStatus.getDelay());
            dbStatus = DBStatusType.getShowText(haDatabaseStatus.getStatusType());
        }
        dmfg.setStatus(
                true,
                dn.getParent().isConnected(),
                dn.isConnected(),
                dbStatus,
                dn.getErrorMsg(),
                dn.hasNewErrorMsg());
        super.refreshVisuals();
    }

    /** Active the listener */
    public void activate() {
        if (isActive()) {
            return;
        }
        dbMonitorPartHelper.setDbNode((DatabaseNode) getModel());
        dbMonitorPartHelper.activate();
        super.activate();
    }

    /** Deactivate the listener */
    public void deactivate() {
        dbMonitorPartHelper.deactivate();
        ((FlashSupport) getFigure()).stopFlash();
        super.deactivate();
    }

    /** Stop monitor data gather. */
    public void stopMonitorGather() {
        dbMonitorPartHelper.deactivate();
    }

    /** Start monitor data gather */
    public void startMonitorGather() {
        dbMonitorPartHelper.activate();
    }
}
