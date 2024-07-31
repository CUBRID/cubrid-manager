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

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetRecordCountTask;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible for executing the column count selection SQL and showing the result in
 * a dialog.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-28 created by lizhiqiang
 */
public class ColumnSelectCountAction extends SelectionAction {

    public static final String ID = ColumnSelectCountAction.class.getName();

    private String message;

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ColumnSelectCountAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ColumnSelectCountAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Sets this action whether supporting for multi-object selection
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSingleSelection(
                obj, new String[] {NodeType.TABLE_COLUMN});
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {

        BusyIndicator.showWhile(
                Display.getDefault(),
                new Runnable() {

                    public void run() {
                        Object[] obj = getSelectedObj();
                        if (!isSupported(obj)) {
                            setEnabled(false);
                            return;
                        }
                        ISchemaNode column = (ISchemaNode) obj[0];
                        ISchemaNode table = (ISchemaNode) column.getParent().getParent();
                        String columnName = column.getName().split(",")[0];
                        if ("".equals(columnName)) { // FIXME
                            return;
                        }
                        CubridDatabase db = table.getDatabase();
                        DatabaseInfo dbInfo = db.getDatabaseInfo();
                        GetRecordCountTask task = new GetRecordCountTask(dbInfo);
                        int count = task.getRecordCount(table.getName(), columnName, null);
                        String[] bindings =
                                new String[] {columnName, table.getName(), String.valueOf(count)};
                        if (count > 1) {
                            message = Messages.bind(Messages.columnSelectCountResult2, bindings);
                        } else {
                            message = Messages.bind(Messages.columnSelectCountResult1, bindings);
                        }
                    }
                });

        CommonUITool.openInformationBox(Messages.selectCountTitle, message);
    }
}
