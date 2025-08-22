/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to view database status.
 *
 * @author robin 2009-5-22
 */
public class DatabaseStatusViewAction extends SelectionAction {
    public static final String ID = DatabaseStatusViewAction.class.getName();

    public DatabaseStatusViewAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public DatabaseStatusViewAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        if (obj instanceof ISchemaNode) {
            ISchemaNode node = (ISchemaNode) obj;
            CubridDatabase database = node.getDatabase();
            boolean isDbNode = CubridNodeType.DATABASE.equals(node.getType());
            boolean isDbSpcFolderNode = CubridNodeType.DBSPACE_FOLDER.equals(node.getType());
            if ((isDbNode || isDbSpcFolderNode) && database.isLogined()) {
                return true;
            }
        }

        return false;
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode node = (ICubridNode) obj[0];
        LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(node);
    }
}
