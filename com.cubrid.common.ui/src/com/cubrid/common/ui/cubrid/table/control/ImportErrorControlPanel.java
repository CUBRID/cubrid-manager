/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.control;

import com.cubrid.common.ui.cubrid.table.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Import data Error Control Panel .
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-17 created by Kevin Cao
 */
public class ImportErrorControlPanel {

    private final Button btnIgnore;

    public ImportErrorControlPanel(Composite parent, int style) {
        Group group = new Group(parent, style);
        {
            group.setLayout(new GridLayout(2, false));
            group.setText(Messages.lableImportErrorControl);
            group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        }

        btnIgnore = new Button(group, SWT.RADIO);
        btnIgnore.setText(Messages.btnIgnore);
        btnIgnore.setToolTipText(Messages.toolTipIgnore);
        btnIgnore.setSelection(true);

        Button btnBreak = new Button(group, SWT.RADIO);
        btnBreak.setText(Messages.btnBreak);
        btnBreak.setToolTipText(Messages.toolTipBreak);
    }

    /**
     * Ignore the errors when importing.
     *
     * @return true:ignore;false:break.
     */
    public boolean isIgoreOrBreak() {
        return btnIgnore.getSelection();
    }
}
