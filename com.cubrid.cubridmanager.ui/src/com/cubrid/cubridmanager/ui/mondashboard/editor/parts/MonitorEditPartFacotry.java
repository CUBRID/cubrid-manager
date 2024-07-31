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

import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerConnection;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerDBListNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.ClientNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Host2ChildConnection;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.MonitorConnection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * Edit part facotry used by dashboard editor.
 *
 * @author cyl
 * @version 1.0 - 2010-6-2 created by cyl
 */
public class MonitorEditPartFacotry implements EditPartFactory {

    private static final Map<Class<?>, Class<?>> MODEL2PARTMAP = new HashMap<Class<?>, Class<?>>();

    static {
        MODEL2PARTMAP.put(Dashboard.class, DashboardPart.class);
        MODEL2PARTMAP.put(HostNode.class, HostMonitorPart.class);
        MODEL2PARTMAP.put(DatabaseNode.class, DatabaseMonitorPart.class);
        MODEL2PARTMAP.put(BrokerNode.class, BrokerMonitorPart.class);
        MODEL2PARTMAP.put(ClientNode.class, ClientMonitorPart.class);
        MODEL2PARTMAP.put(BrokerDBListNode.class, BrokerDBListMonitorPart.class);
        MODEL2PARTMAP.put(MonitorConnection.class, HAConnectionPart.class);
        MODEL2PARTMAP.put(Host2ChildConnection.class, Host2ChildConnectionPart.class);
        MODEL2PARTMAP.put(BrokerConnection.class, BrokerConnectionPart.class);
    }

    /**
     * PartFacotry used by {@link
     * com.cubrid.cubridmanager.ui.mondashboard.editor.MonitorDashboardEditor}
     *
     * @see org.eclipse.gef.EditPartFactory.createEditPart(EditPart context, Object model)
     * @param context parent edit part
     * @param model current model the new edit part will used
     * @return edit part
     */
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;
        Class<?> clazz = MODEL2PARTMAP.get(model.getClass());
        try {
            part = (EditPart) clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        if (null != part) {
            part.setModel(model);
            part.setParent(context);
        }
        return part;
    }
}
