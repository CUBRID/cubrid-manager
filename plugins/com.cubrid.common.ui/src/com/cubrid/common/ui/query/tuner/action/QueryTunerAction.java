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
package com.cubrid.common.ui.query.tuner.action;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.tuner.dialog.QueryTunerDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * QueryTunerAction Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-9 created by Kevin.Wang
 */
public class QueryTunerAction extends SelectionAction {
    public static final String ID = QueryTunerAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public QueryTunerAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public QueryTunerAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSingleSelection(obj, new String[] {});
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISelectionProvider provider = getSelectionProvider();
        if (!(provider instanceof TreeViewer)) {
            return;
        }
        ISchemaNode node = (ISchemaNode) obj[0];

        String query = getQuery();
        if (StringUtil.isNotEmpty(query)) {
            new QueryTunerDialog(getShell(), node.getDatabase(), query).open();
        } else {
            CommonUITool.openInformationBox(Messages.errSelectQueryForTuning);
            new QueryTunerDialog(getShell(), node.getDatabase()).open();
        }
    }

    public String getQuery() { // FIXME extract to utility
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return null;
        }

        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor == null) {
            return null;
        }

        if (!(editor instanceof QueryEditorPart)) {
            return null;
        }

        QueryEditorPart queryEditorPart = (QueryEditorPart) editor;
        StyledText stext = queryEditorPart.getSqlEditorWidget();
        return stext.getSelectionText();
    }
}
