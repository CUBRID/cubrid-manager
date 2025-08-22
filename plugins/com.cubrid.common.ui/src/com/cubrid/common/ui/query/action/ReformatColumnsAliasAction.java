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
package com.cubrid.common.ui.query.action;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.dialog.ReformatColumnsAliasDialog;
import com.cubrid.common.ui.spi.action.FocusAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to reformat columns's alias
 *
 * @author Iasiah Choe 2012-10-19
 */
public class ReformatColumnsAliasAction extends FocusAction {

    public static final String ID = ReformatColumnsAliasAction.class.getName();

    public ReformatColumnsAliasAction(
            Shell shell, Control focusProvider, String text, ImageDescriptor icon) {
        super(shell, focusProvider, text, icon);
        this.setId(ID);
    }

    public ReformatColumnsAliasAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public void focusGained(FocusEvent event) {
        setEnabled(false);
        if (event.getSource() instanceof StyledText) {
            StyledText stext = (StyledText) event.getSource();
            boolean isEnabled =
                    stext != null
                            && stext.getSelectionText() != null
                            && stext.getSelectionText().trim().length() > 0;
            setEnabled(isEnabled);
        }
    }

    public void run() { // FIXME move this logic to core module
        Control control = getFocusProvider();
        if (!(control instanceof StyledText)) {
            return;
        }

        StyledText styledText = (StyledText) control;
        final String columnsRawText = styledText.getSelectionText();
        if (StringUtil.isEmpty(columnsRawText)) {
            return;
        }

        Point p = styledText.getSelectionRange();
        String prefix = ReformatColumnsAliasDialog.openDialog();
        prefix = prefix.trim();
        if (prefix.charAt(prefix.length() - 1) != '.') {
            prefix += ".";
        }

        String newcolumns = StringUtil.appendPrefixOnColumns(columnsRawText, prefix);
        styledText.replaceTextRange(p.x, p.y, newcolumns);
    }
}
