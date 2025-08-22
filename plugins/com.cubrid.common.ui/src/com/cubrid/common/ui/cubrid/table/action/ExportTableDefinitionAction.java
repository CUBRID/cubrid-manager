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
package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.ui.cubrid.table.dialog.ExportTableDefinitionDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * action to export table definition to excel
 *
 * @author fulei 2012-12-06
 */
public class ExportTableDefinitionAction extends SelectionAction {

    public static final String ID = ExportTableDefinitionAction.class.getName();

    public ExportTableDefinitionAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public ExportTableDefinitionAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    private List<String> handleSelectionTables(Object[] objs) {
        List<String> returnArray = new ArrayList<String>();
        if (objs.length == 1) {
            Object obj = objs[0];
            if (obj instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) obj;
                if (node.getType().equals(NodeType.USER_TABLE)) {
                    returnArray.add(node.getName());
                }
            }

        } else {
            for (Object selectNode : objs) {
                if (selectNode instanceof ISchemaNode) {
                    ISchemaNode node = (ISchemaNode) selectNode;
                    if (node.getType().equals(NodeType.USER_TABLE)
                            || node.getType().equals(NodeType.USER_PARTITIONED_TABLE_FOLDER)) {
                        returnArray.add(node.getName());
                    }
                }
            }
        }
        return returnArray;
    }

    private CubridDatabase getCubridDatabase() {
        Object[] objs = this.getSelectedObj();

        if (objs.length == 1) {
            ISchemaNode node = (ISchemaNode) objs[0];
            if (node instanceof ISchemaNode) {
                return node.getDatabase();
            }

        } else {
            for (Object selectNode : objs) {
                if (selectNode instanceof ISchemaNode) {
                    if (((ISchemaNode) selectNode).getDatabase() != null) {
                        return ((ISchemaNode) selectNode).getDatabase();
                    }
                }
            }
        }
        return null;
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        Object[] objs = this.getSelectedObj();
        if (objs.length == 1) {
            if (obj instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) obj;
                if (node.getType().equals(NodeType.TABLE_FOLDER)) {
                    return true;
                } else if (node.getType().equals(NodeType.DATABASE)) {
                    CubridDatabase database = (CubridDatabase) node;
                    if (database == null
                            || !database.isLogined()
                            || database.getRunningType() != DbRunningType.CS) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (node.getType().equals(NodeType.USER_TABLE)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        for (Object selectNode : objs) {
            if (selectNode instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) selectNode;
                if (!node.getType().equals(NodeType.USER_TABLE)
                        && !node.getType().equals(NodeType.USER_PARTITIONED_TABLE_FOLDER)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void run() {
        boolean exportAllTables = checkExportAllTable();
        List<String> returnArray = null;
        if (!exportAllTables) {
            returnArray = handleSelectionTables(this.getSelectedObj());
        }
        doRun(exportAllTables, returnArray);
    }

    /**
     * Run
     *
     * @param nodes
     */
    public void run(ICubridNode[] nodes) {
        List<String> returnArray = handleSelectionTables(nodes);
        doRun(false, returnArray);
    }

    /**
     * Do run
     *
     * @param isExportAll
     * @param tables
     */
    private void doRun(boolean isExportAll, List<String> tables) {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        new ExportTableDefinitionDialog(shell, getCubridDatabase(), isExportAll, tables).open();
    }

    public boolean checkExportAllTable() {
        Object[] objs = this.getSelectedObj();
        if (objs.length == 1) {
            Object obj = objs[0];
            if (obj instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) obj;
                if (node.getType().equals(NodeType.TABLE_FOLDER)) {
                    return true;
                } else if (node.getType().equals(NodeType.DATABASE)) {
                    return true;
                }
            }
        }
        return false;
    }
}
