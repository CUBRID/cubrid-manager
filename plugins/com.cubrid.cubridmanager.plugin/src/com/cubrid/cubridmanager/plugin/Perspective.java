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

package com.cubrid.cubridmanager.plugin;

import com.cubrid.cubridmanager.ui.broker.editor.BrokerEnvStatusView;
import com.cubrid.cubridmanager.ui.broker.editor.BrokerStatusView;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.common.navigator.CubridMonitorNavigatorView;
import com.cubrid.cubridmanager.ui.mondashboard.editor.BrokerDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.DatabaseDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.DbDashboardHistoryViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HostDashboardHistoryViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HostDashboardViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.BrokerStatusHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.BrokerStatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusDumpMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbSystemMonitorHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbSystemMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.HostSystemMonitorHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.HostSystemMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.StatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monstatistic.editor.MonitorStatisticEditor;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.IViewLayout;

/**
 * This class is responsible for initial CUBRID Manager workbench Window layout
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class Perspective implements IPerspectiveFactory {
    // perspective ID
    public static final String ID = "com.cubrid.cubridmanager.plugin.Perspective";

    /**
     * create initial layout for CUBRID Manager workbench window
     *
     * @param layout the workbench page layout object
     */
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(true);

        IFolderLayout navigatorFolder =
                layout.createFolder(
                        "NavigatorFolder", IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
        navigatorFolder.addView(CubridHostNavigatorView.ID);
        navigatorFolder.addView(CubridMonitorNavigatorView.ID);
        IViewLayout viewLayout = layout.getViewLayout(CubridHostNavigatorView.ID);
        viewLayout.setCloseable(false);
        viewLayout.setMoveable(false);

        String statusMonitorViewId = StatusMonitorViewPart.ID + ":*";
        IPlaceholderFolderLayout monitorPlaceFolder =
                layout.createPlaceholderFolder(
                        "StatusFolder", IPageLayout.RIGHT, 0.5f, IPageLayout.ID_EDITOR_AREA);
        monitorPlaceFolder.addPlaceholder(statusMonitorViewId);

        String brokerStatusMonitorViewId = BrokerStatusMonitorViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(brokerStatusMonitorViewId);

        String dbStatusMonitorViewId = DbStatusDumpMonitorViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(dbStatusMonitorViewId);

        String brokerHistoryStatusViewId = BrokerStatusHistoryViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(brokerHistoryStatusViewId);

        String dbHistoryStatusViewId = DbStatusHistoryViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(dbHistoryStatusViewId);

        String hostSystemMonitorViewPart = HostSystemMonitorViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(hostSystemMonitorViewPart);

        String dbSystemMonitorViewPart = DbSystemMonitorViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(dbSystemMonitorViewPart);

        String brokerServerDashboardViewPart = HostDashboardViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(brokerServerDashboardViewPart);

        String databaseDashboardViewPart = DatabaseDashboardViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(databaseDashboardViewPart);

        String hostDashboardHistoryViewPart = HostDashboardHistoryViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(hostDashboardHistoryViewPart);

        String dbDashboardHistoryViewPart = DbDashboardHistoryViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(dbDashboardHistoryViewPart);

        String hostSystemMonitorHistoryViewPart = HostSystemMonitorHistoryViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(hostSystemMonitorHistoryViewPart);

        String dbSystemMonitorHistoryViewPart = DbSystemMonitorHistoryViewPart.ID + ":*";
        monitorPlaceFolder.addPlaceholder(dbSystemMonitorHistoryViewPart);

        String monitorStatisticEditor = MonitorStatisticEditor.ID + ":*";
        monitorPlaceFolder.addPlaceholder(monitorStatisticEditor);

        String brokerEnvSatausViewId = BrokerEnvStatusView.ID + ":*";
        IPlaceholderFolderLayout brokerEnvPlaceFolder =
                layout.createPlaceholderFolder(
                        "BrokerEnvFolder", IPageLayout.BOTTOM, 0.7f, IPageLayout.ID_EDITOR_AREA);
        brokerEnvPlaceFolder.addPlaceholder(brokerEnvSatausViewId);

        String brokerSatausViewId = BrokerStatusView.ID + ":*";
        brokerEnvPlaceFolder.addPlaceholder(brokerSatausViewId);

        String brokerDashboardViewId = BrokerDashboardViewPart.ID + ":*";
        brokerEnvPlaceFolder.addPlaceholder(brokerDashboardViewId);
    }
}
