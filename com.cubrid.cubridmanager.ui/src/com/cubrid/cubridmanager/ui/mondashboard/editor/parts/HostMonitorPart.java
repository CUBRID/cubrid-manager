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
package com.cubrid.cubridmanager.ui.mondashboard.editor.parts;

import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HostDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.FlashSupport;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.HostMonitorFigure;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.HostMonitorPartHelper;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.policy.HostMonitorEditPolicy;
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
 * edit part used by Broker Host Monitor
 *
 * @author cyl
 * @version 1.0 - 2010-6-2 created by cyl
 */
public class HostMonitorPart extends HANodePart {

    private final HostMonitorPartHelper hostMonitorPartHelper;

    public HostMonitorPart() {
        hostMonitorPartHelper = new HostMonitorPartHelper();
    }

    /**
     * create a host figure
     *
     * @return a host figure
     */
    protected IFigure createFigure() {
        HostMonitorFigure hostMonFigure = new HostMonitorFigure();
        HostNode hn = (HostNode) getModel();
        hostMonFigure.setStatus(true, hn.isConnected(), hn.getErrorMsg(), hn.hasNewErrorMsg());
        hostMonFigure.setTitle(hn.getName());
        hostMonFigure.setHint(
                new StringBuffer(" ")
                        .append(hn.getUserName())
                        .append("@")
                        .append(hn.getIp())
                        .append(":")
                        .append(hn.getPort())
                        .append(" ")
                        .toString());
        return hostMonFigure;
    }

    /** initialize edit policies used by edit part */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new HostMonitorEditPolicy());
    }

    /**
     * Method to be executed when model's property changed.
     *
     * @param evt PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (HostNode.PROP_DB_NODES.equals(evt.getPropertyName())) {
            // notify parent to execute add or remove database action.
            getParent().refresh();
        } else if (HostNode.PROP_BROKER_NODES.equals(evt.getPropertyName())) {
            // notify parent to execute add or remove database action.
            getParent().refresh();
        } else if (HostNode.PROP_HOST_STATUS.equals(evt.getPropertyName())) {
            refreshVisuals();
            getParent().refresh();
        } else if (HostNode.PROP_HOST_CONNECTION_STATUS.equals(evt.getPropertyName())) {
            refreshVisuals();
            getParent().refresh();
        }
    }

    /**
     * Refreshes this EditPart's <i>visuals</i>. This method is called by {@link #refresh()}, and
     * may also be called in response to notifications from the model.
     */
    protected void refreshVisuals() {
        HostMonitorFigure fg = (HostMonitorFigure) this.getFigure();
        HostNode model = (HostNode) getModel();
        HAHostStatusInfo hostStatusInfo = model.getHostStatusInfo();
        fg.setTitle(model.getName());
        fg.setStatus(true, model.isConnected(), model.getErrorMsg(), model.hasNewErrorMsg());
        if (hostStatusInfo != null) {
            fg.setHostCpuUsage(hostStatusInfo.getCpuUsage());
            fg.setHostMemUsage(hostStatusInfo.getMemUsage());
            fg.setHostIO(hostStatusInfo.getIoWait());
        }
        super.refreshVisuals();
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
     * @param req Request
     */
    public void performRequest(Request req) {
        if (req.getType() == RequestConstants.REQ_OPEN) {
            HostNode hn = (HostNode) getModel();
            if (!hn.isConnected()) {
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
            String secondaryId =
                    new StringBuffer(hn.getUserName())
                            .append("&")
                            .append(hn.getIp())
                            .append("&")
                            .append(hn.getPort())
                            .toString();
            IViewReference viewReference =
                    page.findViewReference(HostDashboardViewPart.ID, secondaryId);
            if (viewReference == null) {
                try {
                    IViewPart viewPart =
                            page.showView(
                                    HostDashboardViewPart.ID,
                                    secondaryId,
                                    IWorkbenchPage.VIEW_ACTIVATE);
                    ((HostDashboardViewPart) viewPart).init((HostNode) getModel());
                } catch (PartInitException ex) {
                    viewReference = null;
                }
            } else {
                IViewPart viewPart = viewReference.getView(false);
                window.getActivePage().bringToTop(viewPart);
                ((HostDashboardViewPart) viewPart).init((HostNode) getModel());
            }
        }
    }

    /** Generate the DataUpdateListener */
    public void activate() {
        if (isActive()) {
            return;
        }
        hostMonitorPartHelper.setHostNode((HostNode) getModel());
        hostMonitorPartHelper.active();
        super.activate();
    }

    /** Deregister self as a PropertyChangeListener from Model. */
    public void deactivate() {
        hostMonitorPartHelper.deactivate();
        ((FlashSupport) getFigure()).stopFlash();
        super.deactivate();
    }

    /** Stop monitor data gather. */
    public void stopMonitorGather() {
        hostMonitorPartHelper.deactivate();
    }

    /** Start monitor data gather */
    public void startMonitorGather() {
        hostMonitorPartHelper.active();
    }
}
