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
package com.cubrid.common.ui.query.action;

import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.Vector;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * action to create sql code script
 *
 * @author Isaiah Choe 2012-05-03
 */
public class CreateSqlPhpCodeAction extends FocusAction {

    public static final String ID = "sqlphpcode";

    /**
     * The constructor
     *
     * @param shell
     * @param focusProvider
     * @param text
     * @param icon
     */
    protected CreateSqlPhpCodeAction(
            Shell shell, Control focusProvider, String text, ImageDescriptor icon) {
        super(shell, focusProvider, text, icon);
        this.setId(ID);
        setEnabled(true);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public CreateSqlPhpCodeAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() { // FIXME move this logic to core module
        Control control = getFocusProvider();
        if (control instanceof StyledText) {
            StyledText stext = (StyledText) control;
            String data = stext.getSelectionText();
            if (StringUtil.isNotEmpty(data)) {
                StringBuilder res = new StringBuilder();
                Vector<String> list = QueryUtil.queriesToQuery(data);
                for (int i = 0; i < list.size(); i++) {
                    String row = list.get(i);

                    if (res.length() > 0) {
                        res.append("\n\n");
                    }
                    res.append(parseToPhpCode(row));
                }

                if (res.length() == 0) {
                    CommonUITool.openErrorBox(Messages.errCreatedSqlNotSelected);
                    return;
                }

                Clipboard clipboard = CommonUITool.getClipboard();
                TextTransfer textTransfer = TextTransfer.getInstance();
                clipboard.setContents(new Object[] {res.toString()}, new Transfer[] {textTransfer});

                CommonUITool.openInformationBox(
                        Messages.titleCreateCode, Messages.msgCreatedSqlPhpCode);
            } else {
                CommonUITool.openErrorBox(Messages.errCreatedSqlNotSelected);
            }
        }
    }

    private String parseToPhpCode(String sql) { // FIXME move this logic to core module
        StringBuilder sb = new StringBuilder();

        String[] arr = sql.split("[\\r\\n]");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].trim().length() == 0) {
                continue;
            }

            String row = arr[i].replaceAll("\"", "\\\\\"");

            if (sb.length() == 0) {
                sb.append("$sql = \"");
            } else {
                sb.append("\n     . \"");
            }

            sb.append(row).append("\\n\"");
        }
        sb.append(";\n");

        return sb.toString();
    }
}
