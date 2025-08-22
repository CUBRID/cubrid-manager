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
package com.cubrid.common.ui.compare.schema.action;

import static com.cubrid.common.ui.spi.util.CommonUITool.openWarningBox;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.compare.schema.dialog.SchemaCompareDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

/**
 * Open the data compare wizard
 *
 * @author PCraft
 * @version 1.0 - 2013-01-22 created by PCraft
 */
public class SchemaCompareWizardAction extends SelectionAction {
    public static final String ID = SchemaCompareWizardAction.class.getName();

    public SchemaCompareWizardAction(Shell shell, String text, ImageDescriptor icon) {
        super(shell, null, text, icon);
        this.setId(ID);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return true;
    }

    public void run() {
        Object[] objs = this.getSelectedObj();
        if (objs == null || objs.length != 2) {
            openWarningBox(Messages.errSelectOverTwoDb);
            return;
        }

        List<ICubridNode> selections = new ArrayList<ICubridNode>();
        Set<CubridDatabase> addedDatabaseSet = new HashSet<CubridDatabase>();

        for (Object obj : objs) {
            if (obj instanceof CubridDatabase) {
                CubridDatabase db = (CubridDatabase) obj;
                if (db.getRunningType() == DbRunningType.STANDALONE) {
                    openWarningBox(Messages.errSelectNonRunningDb);
                    return;
                }

                if (addedDatabaseSet.contains(obj)) {
                    continue;
                }

                if (!db.isLogined()) {
                    openWarningBox(Messages.errSelectNonRunningDb);
                    return;
                }

                selections.add((ICubridNode) obj);
            }
        }

        new SchemaCompareDialog(getShell(), selections).open();
    }
}
