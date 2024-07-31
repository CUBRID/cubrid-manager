/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.er.model.PropertyChangeProvider;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Abstract Selection Action for all of the ER Designer action
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-17 created by Yu Guojia
 */
public abstract class AbstractSelectionAction extends SelectionAction
        implements ITaskExecutorInterceptor {
    // group id must be a action id
    public static String GLOBAL_GROUP_ID = ActionFactory.UNDO.getId();
    public static String MANAGE_GROUP_ID = ModifyTableNameAction.ID;

    protected AbstractSelectionAction(IWorkbenchPart part) {
        super(part);
    }

    protected boolean calculateEnabled() {
        CubridDatabase dbNode = getDataBaseNode();
        if (null == dbNode
                || null == dbNode.getDatabaseInfo()
                || !dbNode.getDatabaseInfo().isLogined()) {
            return false;
        }
        return true;
    }

    /**
     * Get the first selected node.
     *
     * @return
     */
    protected PropertyChangeProvider getSelectedNode() {
        List objects = getSelectedObjects();
        if (objects.isEmpty()) {
            return null;
        }
        if (!(objects.get(0) instanceof EditPart)) {
            return null;
        }

        EditPart part = (EditPart) objects.get(0);
        return (PropertyChangeProvider) part.getModel();
    }

    protected ERTable getERTable() {
        ERTable table = null;
        PropertyChangeProvider node = getSelectedNode();
        if (node instanceof ERTable) {
            table = (ERTable) node;
        } else if (node instanceof ERTableColumn) {
            ERTableColumn col = (ERTableColumn) node;
            table = col.getTable();
        }

        return table;
    }

    /**
     * Get ER Schema object
     *
     * @return
     */
    protected ERSchema getERSchema() {
        PropertyChangeProvider object = getSelectedNode();
        if (object == null) {
            return null;
        }
        return object.getERSchema();
    }

    /**
     * Get the database node vavigator tree viewer that the er based on.
     *
     * @return tv TreeViewer
     */
    protected TreeViewer getDBNodeTreeView() {
        return this.getERSchema().getInput().getTv();
    }

    /**
     * Get CubridDatabase object
     *
     * @return
     */
    protected CubridDatabase getDataBaseNode() {
        if (getERSchema() == null) {
            return null;
        }

        return getERSchema().getCubridDatabase();
    }

    /**
     * Get ER Schema'CubridDatabase information
     *
     * @return
     */
    protected DatabaseInfo getDatabaseInfo() {
        if (getDataBaseNode() == null) {
            return null;
        }
        return getDataBaseNode().getDatabaseInfo();
    }

    public IStatus postTaskFinished(ITask task) {
        return null;
    }

    public void completeAll() {}
}
