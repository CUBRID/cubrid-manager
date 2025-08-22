/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.spi.persist;

import com.cubrid.common.configuration.jdbc.IJDBCConnecInfo;
import com.cubrid.common.configuration.jdbc.IJDBCConnectionChangedObserver;
import com.cubrid.common.configuration.jdbc.IJDBCInfoChangedSubject;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.ui.spi.util.CQBConnectionUtils;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * CQBConnectionChangingObserver will respond the changing of other sytem's connection changing
 * event.
 *
 * @author Kevin Cao
 * @version 1.0 - 2014-2-12 created by Kevin Cao
 */
public class CQBConnectionChangingObserver implements IJDBCConnectionChangedObserver {
    // private static final Logger LOGGER = LogUtil.getLogger(CQBConnectionChangingObserver.class);

    /**
     * When add a new connection
     *
     * @param initiator IJDBCInfoChangedSubject who triggered the event.
     * @param newCon IJDBCConnecInfo
     */
    public void afterAdd(IJDBCInfoChangedSubject initiator, IJDBCConnecInfo newCon) {
        if (CQBDBNodePersistManager.getInstance().equals(initiator)) {
            return;
        }
        // If not a CUBRID connection
        if (newCon.getDbType() != 1) {
            return;
        }

        CubridDatabase database = CQBDatabaseFactory.getDatabaseJDBCConnectInfo(newCon);
        if (database != null) {
            return;
        }

        database = CQBDatabaseFactory.createDatabase(newCon);
        CQBDBNodePersistManager.getInstance().addDatabase(database, true);

        refreshNavigationTree(null);
    }

    /**
     * When modify an existed connection.
     *
     * @param initiator IJDBCInfoChangedSubject who triggered the event.
     * @param oldCon IJDBCConnecInfo
     * @param newCon IJDBCConnecInfo
     */
    public void afterModify(
            IJDBCInfoChangedSubject initiator, IJDBCConnecInfo oldCon, IJDBCConnecInfo newCon) {
        if (CQBDBNodePersistManager.getInstance().equals(initiator)) {
            return;
        }
        // If not a CUBRID connection
        if (oldCon.getDbType() != 1 || newCon.getDbType() != 1) {
            return;
        }

        /*Must fire database changed event first*/
        CubridDatabase database = CQBDatabaseFactory.getDatabaseJDBCConnectInfo(oldCon);
        if (database != null) {
            if (isNeedLogout(oldCon, newCon)) {
                CQBConnectionUtils.processConnectionLogout(database);
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_LOGOUT));
            }

            CubridDatabase newDatabase =
                    CQBDatabaseFactory.modifyDatabaseByJDBCConnectInfo(oldCon, newCon);
            CQBDBNodePersistManager.getInstance().saveDatabases();

            refreshNavigationTree(newDatabase);
        } else {
            database = CQBDatabaseFactory.createDatabase(newCon);
            CQBDBNodePersistManager.getInstance().addDatabase(database, true);

            refreshNavigationTree(null);
        }
    }

    /**
     * Judge current event is need logout
     *
     * @param oldCon
     * @param newCon
     * @return
     */
    private boolean isNeedLogout(IJDBCConnecInfo oldCon, IJDBCConnecInfo newCon) { // FIXME extract
        if (oldCon == null || newCon == null) {
            return false;
        }

        if (StringUtil.isEqualNotIgnoreNull(oldCon.getConName(), newCon.getConName())
                && StringUtil.isEqualNotIgnoreNull(oldCon.getHost(), newCon.getHost())
                && StringUtil.isEqualNotIgnoreNull(
                        String.valueOf(oldCon.getPort()), String.valueOf(newCon.getPort()))
                && StringUtil.isEqualNotIgnoreNull(oldCon.getDbName(), newCon.getDbName())
                && StringUtil.isEqualNotIgnoreNull(oldCon.getConUser(), newCon.getConUser())
                && StringUtil.isEqualNotIgnoreNull(oldCon.getConPassword(), newCon.getConPassword())
                && StringUtil.isEqualNotIgnoreNull(
                        oldCon.getDriverFileName(), newCon.getDriverFileName())
                && StringUtil.isEqualNotIgnoreNull(oldCon.getCharset(), newCon.getCharset())) {
            return false;
        }

        return true;
    }

    /** Refresh the navigation tree */
    private void refreshNavigationTree(CubridDatabase database) {
        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView(CubridNavigatorView.ID_CQB);
        TreeViewer treeViewer = navigatorView == null ? null : navigatorView.getViewer();
        if (treeViewer == null) {
            return;
        }

        if (database != null) {
            treeViewer.refresh(database, true);
            treeViewer.expandToLevel(database, 1);
        } else {
            CQBDBNodePersistManager.getInstance().reloadDatabases();
            CQBGroupNodePersistManager.getInstance().reloadGroups();

            if (treeViewer != null) {
                treeViewer.refresh(true);
            }
        }
    }

    /**
     * Delete a connection.
     *
     * @param initiator IJDBCInfoChangedSubject who triggered the event.
     * @param delCon IJDBCConnecInfo
     */
    public void afterDelete(IJDBCInfoChangedSubject initiator, IJDBCConnecInfo delCon) {
        if (CQBDBNodePersistManager.getInstance().equals(initiator)) {
            return;
        }
        // If not a CUBRID connection
        if (delCon.getDbType() != 1) {
            return;
        }

        CubridDatabase database = CQBDatabaseFactory.getDatabaseJDBCConnectInfo(delCon);
        if (database != null) {
            /*Remove in CQB*/
            CQBConnectionUtils.processConnectionDeleted(database);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    database, CubridNodeChangedEventType.DATABASE_LOGOUT));
            refreshNavigationTree(null);
        }
    }
}
