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

import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.AbstractMonitorFigure;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerDBListNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANodeConnection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.graphics.Color;

/**
 * Edit part class of connection between broker and database.
 *
 * @author cyl
 * @version 1.0 - 2010-8-18 created by cyl
 */
public class BrokerConnectionPart extends AbstractConnectionEditPart {

    public static final Color CONNECTION_DEFAULT_COLOR = ResourceManager.getColor(0, 0, 160);

    /**
     * create figure used by edit part.
     *
     * @return IFigure connectin figure
     */
    protected IFigure createFigure() {
        PolylineConnection conn = new PolylineConnection();
        conn.setTargetDecoration(new PolygonDecoration());
        conn.setConnectionRouter(new ManhattanConnectionRouter());
        conn.setForegroundColor(CONNECTION_DEFAULT_COLOR);
        conn.setLineWidth(3);
        return conn;
    }

    /** create edit policies. */
    protected void createEditPolicies() {
        // No Edit Policies to be created.
    }

    /** refresh Visuals */
    protected void refreshVisuals() {
        PolylineConnection conn = (PolylineConnection) getFigure();
        HANodeConnection model = (HANodeConnection) getModel();
        // BrokerDBListNode linewith should be 1.
        if (model.getTarget() instanceof BrokerDBListNode) {
            conn.setLineWidth(1);
        }
        BrokerNode broker = null;
        if (model.getSource() instanceof BrokerNode) {
            broker = (BrokerNode) model.getSource();
        } else if (model.getTarget() instanceof BrokerNode) {
            broker = (BrokerNode) model.getTarget();
        }
        if (broker != null) {
            if (broker.getParent().isConnected()) {
                conn.setForegroundColor(CONNECTION_DEFAULT_COLOR);
            } else {
                conn.setForegroundColor(AbstractMonitorFigure.DISABLED_COLOR);
            }
        }
        super.refreshVisuals();
    }
}
