/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;

/**
 * Copy the navigator node
 *
 * @author pangqiren
 * @version 1.0 - 2011-9-23 created by pangqiren
 * @version 1.1 - 2012-09-05 updated by Isaiah Choe
 */
public class CopyNodeAction extends SelectionAction {
    public static final String ID = CopyNodeAction.class.getName();

    public CopyNodeAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public CopyNodeAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode || obj instanceof Object[]) {
            return true;
        }
        return false;
    }

    public void run() {
        Object[] selectedObjs = this.getSelectedObj();
        if (selectedObjs == null || selectedObjs.length == 0) {
            return;
        }

        StringBuilder names = new StringBuilder();
        for (Object obj : selectedObjs) {
            if (!(obj instanceof ICubridNode)) {
                continue;
            }

            ICubridNode cubridNode = (ICubridNode) obj;
            String type = cubridNode.getType();

            if (names.length() > 0) {
                names.append(",");
            }

            if (type.equals(NodeType.TABLE_COLUMN)) {
                TableColumn tc = (TableColumn) cubridNode.getAdapter(TableColumn.class);
                names.append(tc.getColumnName());
            } else {
                names.append(cubridNode.getLabel());
            }
        }
        if (names.length() > 0) {
            TextTransfer textTransfer = TextTransfer.getInstance();
            Clipboard clipboard = CommonUITool.getClipboard();
            clipboard.setContents(new Object[] {names.toString()}, new Transfer[] {textTransfer});
        }
    }
}
