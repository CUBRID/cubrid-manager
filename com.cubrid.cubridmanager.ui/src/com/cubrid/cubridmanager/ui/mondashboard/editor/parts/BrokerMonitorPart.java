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

import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.ui.mondashboard.editor.BrokerDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.BrokerMonitorFigure;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.FlashSupport;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.BrokerMonitorPartHelper;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.policy.BrokerMonitorEditPolicy;
import java.beans.PropertyChangeEvent;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
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
 * Edit part of broker monitor.
 *
 * @author cyl
 * @version 1.0 - 2010-8-18 created by cyl
 */
public class BrokerMonitorPart extends HANodePart {

    private final BrokerMonitorPartHelper brokerMonitorPartHelper;

    /** The constructor */
    public BrokerMonitorPart() {
        brokerMonitorPartHelper = new BrokerMonitorPartHelper();
    }

    /**
     * get a database monitor figure.
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     * @return a database figure
     */
    protected IFigure createFigure() {
        BrokerMonitorFigure figure = new BrokerMonitorFigure();
        BrokerNode broker = (BrokerNode) getModel();
        setMode2View(figure, broker);
        HostNode hostNode = broker.getParent();
        figure.setHint(
                new StringBuffer(" ")
                        .append(broker.getBrokerName())
                        .append("@")
                        .append(hostNode.getIp())
                        .append(":")
                        .append(hostNode.getPort())
                        .append(" ")
                        .toString());
        return figure;
    }

    /**
     * Initialize edit policies of edit part
     *
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new BrokerMonitorEditPolicy());
    }

    /**
     * Method to be executed when model's property changed.
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * @param evt PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (BrokerNode.PROP_BROKER_STATUS.equals(evt.getPropertyName())) {
            getParent().refresh();
            refresh();
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
     * @param req Request
     */
    public void performRequest(Request req) {
        if (req.getType() == RequestConstants.REQ_OPEN) {
            BrokerNode bn = (BrokerNode) getModel();
            String brokerName = bn.getBrokerName();
            HostNode hn = bn.getParent();
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) {
                return;
            }
            IWorkbenchPage page = window.getActivePage();
            if (page == null) {
                return;
            }
            String secondaryId =
                    new StringBuffer(brokerName + "@" + hn.getUserName())
                            .append("&")
                            .append(hn.getIp())
                            .append("&")
                            .append(hn.getPort())
                            .toString();
            IViewReference viewReference =
                    page.findViewReference(BrokerDashboardViewPart.ID, secondaryId);
            if (viewReference == null) {
                try {
                    IViewPart viewPart =
                            page.showView(
                                    BrokerDashboardViewPart.ID,
                                    secondaryId,
                                    IWorkbenchPage.VIEW_ACTIVATE);
                    ((BrokerDashboardViewPart) viewPart).init((BrokerNode) getModel());
                } catch (PartInitException ex) {
                    viewReference = null;
                }
            } else {
                IViewPart viewPart = viewReference.getView(false);
                window.getActivePage().bringToTop(viewPart);
                ((BrokerDashboardViewPart) viewPart).init((BrokerNode) getModel());
            }
        }
    }

    /**
     * Refreshes this EditPart's <i>visuals</i>. This method is called by {@link #refresh()}, and
     * may also be called in response to notifications from the model.
     */
    protected void refreshVisuals() {
        BrokerMonitorFigure figure = (BrokerMonitorFigure) this.getFigure();
        BrokerNode broker = (BrokerNode) this.getModel();
        setMode2View(figure, broker);

        BrokerDiagData dsr = broker.getBrokerDiagData();
        if (dsr == null) {
            figure.setSessionCount(0);
            figure.setActiveSessionCount(0);
            figure.setActiveSessionCount(0);
            figure.setTps(0);
        } else {
            figure.setSessionCount(Integer.parseInt(dsr.getCas_mon_session()));
            // Avoid looks like ActiveSessionCount larger then SessionCount
            figure.setActiveSessionCount(Integer.parseInt(dsr.getCas_mon_session()));
            figure.setActiveSessionCount(Integer.parseInt(dsr.getCas_mon_active()));
            figure.setTps(Integer.parseInt(dsr.getCas_mon_tran()));
        }
        super.refreshVisuals();
    }

    /**
     * Set model's value to view.
     *
     * @param figure BrokerMonitorFigure
     * @param broker BrokerNode
     */
    private void setMode2View(BrokerMonitorFigure figure, BrokerNode broker) {
        HostNode hostNode = broker.getParent();
        figure.setTitle(broker.getName());
        figure.setHostName(hostNode.getName());

        String brokerStatus = BrokerMonitorFigure.STATUS_UNKNOWN;
        String accessMode = OnOffType.OFF.getText();
        BrokerInfo brokerInfo = broker.getBrokerInfo();
        if (brokerInfo != null) {
            brokerStatus = brokerInfo.getState();
            accessMode = brokerInfo.getAccess_mode();
        }
        figure.setStatus(
                true,
                hostNode.isConnected(),
                brokerStatus,
                accessMode,
                broker.getErrorMsg(),
                broker.hasNewErrorMsg());
    }

    /** Active the listener */
    public void activate() {
        if (isActive()) {
            return;
        }
        brokerMonitorPartHelper.setBrokerNode((BrokerNode) getModel());
        brokerMonitorPartHelper.activate();
        super.activate();
    }

    /** Deactivate the listener */
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        brokerMonitorPartHelper.deactivate();
        ((FlashSupport) getFigure()).stopFlash();
        super.deactivate();
    }

    /** Stop monitor data gather. */
    public void stopMonitorGather() {
        brokerMonitorPartHelper.deactivate();
    }

    /** Start monitor data gather */
    public void startMonitorGather() {
        brokerMonitorPartHelper.activate();
    }

    /** refresh Target Connections */
    protected void refreshSourceConnections() {
        super.refreshSourceConnections();
        for (Object obj : getSourceConnections()) {
            ConnectionEditPart ep = (ConnectionEditPart) obj;
            ep.refresh();
        }
    }

    /** refresh Target Connections */
    protected void refreshTargetConnections() {
        super.refreshTargetConnections();
        for (Object obj : getTargetConnections()) {
            ConnectionEditPart ep = (ConnectionEditPart) obj;
            ep.refresh();
        }
    }
}
