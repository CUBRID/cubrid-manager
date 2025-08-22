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
package com.cubrid.cubridquery.ui.spi;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.ui.query.preference.QueryOptionPreferencePage;
import com.cubrid.common.ui.spi.dialog.CMPreferenceDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is responsible to create the common dialog with perference
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class PreferenceUtil {

    private PreferenceUtil() {
        noOp();
    }

    /**
     * Create property dialog related with CUBRID node
     *
     * @param parentShell the parent shell
     * @param node the ICubridNode object
     * @return the Dialog object
     */
    public static Dialog createPropertyDialog(Shell parentShell, ICubridNode node) {
        PreferenceManager mgr = new PreferenceManager();
        String type = node.getType();

        if (NodeType.DATABASE.equals(type)) {
            CubridServer server = node.getServer();
            QueryOptionPreferencePage queryEditorPage = new QueryOptionPreferencePage(server);
            PreferenceNode queryEditorNode =
                    new PreferenceNode(com.cubrid.common.ui.query.Messages.queryTitle);
            queryEditorNode.setPage(queryEditorPage);
            mgr.addToRoot(queryEditorNode);
        }

        CMPreferenceDialog dlg =
                new CMPreferenceDialog(parentShell, mgr, Messages.titlePropertiesDialog);
        dlg.setPreferenceStore(CubridQueryUIPlugin.getDefault().getPreferenceStore());
        return dlg;
    }
}
