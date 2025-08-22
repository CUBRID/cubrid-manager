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

import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.BrokerDBListFigure;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerDBListNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.policy.BrokerDBListMonitorEditPolicy;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

/**
 * Edit part of database list whickh the broker connectted to.
 *
 * @author cyl
 * @version 1.0 - 2010-8-18 created by cyl
 */
public class BrokerDBListMonitorPart extends HANodePart {

    /** The constructor */
    public BrokerDBListMonitorPart() {
        // Do nothing.
    }

    /**
     * get a database monitor figure.
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     * @return a database figure
     */
    protected IFigure createFigure() {
        BrokerDBListFigure figure = new BrokerDBListFigure();
        BrokerDBListNode modelNode = (BrokerDBListNode) getModel();
        figure.setName(modelNode.getName());
        return figure;
    }

    /**
     * Initialize edit policies of edit part
     *
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new BrokerDBListMonitorEditPolicy());
    }

    /**
     * Method to be executed when model's property changed.
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * @param evt PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (BrokerDBListNode.PROP_DB_LIST.equals(evt.getPropertyName())) {
            refreshVisuals();
        }
    }

    /**
     * Refreshes this EditPart's <i>visuals</i>. This method is called by {@link #refresh()}, and
     * may also be called in response to notifications from the model.
     */
    protected void refreshVisuals() {
        BrokerDBListFigure figure = (BrokerDBListFigure) this.getFigure();
        BrokerDBListNode dn = (BrokerDBListNode) this.getModel();
        figure.setName(dn.getName());
        // Remove defined dabatases.
        List<String> dbList = new ArrayList<String>();
        dbList.addAll(dn.getDbList());
        Dashboard dashboard = (Dashboard) getParent().getModel();
        for (DatabaseNode dbNode : dashboard.getAllDatabaseNode()) {
            String dbName = dbNode.getDbName() + "@" + dbNode.getParent().getIp();
            if (dn.getDbList().contains(dbName)) {
                dbList.remove(dbName);
            }
        }
        figure.setDBList(dbList);
        BrokerNode broker = dn.getBrokerNode();
        figure.setHostConnected(broker.getParent().isConnected());
        super.refreshVisuals();
    }
}
