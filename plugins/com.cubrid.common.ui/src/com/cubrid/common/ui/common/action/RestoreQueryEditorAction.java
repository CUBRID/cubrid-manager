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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.query.autosave.CheckQueryEditorTask;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.contribution.StatusLineContrItem;
import com.cubrid.common.ui.spi.model.RestorableQueryEditorInfo;
import com.cubrid.common.ui.spi.persist.ApplicationPersistUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class RestoreQueryEditorAction extends Action {
    public static final String ID = RestoreQueryEditorAction.class.getName();

    public RestoreQueryEditorAction(String text, ImageDescriptor image) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
    }

    public void run() {
        ApplicationPersistUtil util = ApplicationPersistUtil.getInstance();
        List<ArrayList<RestorableQueryEditorInfo>> restoreList =
                util.getEditorStatusListAtLastSession();
        if (restoreList == null || restoreList.size() == 0) {
            CommonUITool.openInformationBox(Messages.errNoRestoreQueryEditor);
            return;
        }
        if (!CommonUITool.openConfirmBox(Messages.restoreQueryEditorConfirm)) {
            return;
        }
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            CommonUITool.openErrorBox(Messages.restoreQueryEditorRestoreFailed);
            return;
        }
        for (List<RestorableQueryEditorInfo> editorStatusList :
                ApplicationPersistUtil.getInstance().getEditorStatusListAtLastSession()) {
            QueryUnit input = new QueryUnit();
            try {
                QueryEditorPart editor =
                        (QueryEditorPart)
                                window.getActivePage().openEditor(input, QueryEditorPart.ID);
                if (editor == null) {
                    continue;
                }
                for (int i = 0; i < editorStatusList.size(); i++) {
                    RestorableQueryEditorInfo editorStatus = editorStatusList.get(i);
                    if (editorStatus == null) {
                        continue;
                    }
                    String sql = CheckQueryEditorTask.getQuery(editorStatus);
                    CombinedQueryEditorComposite combinedQueryComposite = null;
                    if (i == 0) {
                        combinedQueryComposite = editor.getCombinedQueryComposite();
                    } else {
                        combinedQueryComposite = editor.addEditorTab();
                    }
                    if (combinedQueryComposite != null) {
                        combinedQueryComposite.getSqlEditorComp().setQueries(sql);
                    }
                }
                editor.setCombinedQueryEditortabFolderSelecton(0);
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
        ApplicationPersistUtil.getInstance().clearRestorableQueryEditors();
        StatusLineContrItem statusCont = LayoutManager.getInstance().getStatusLineContrItem();
        statusCont.changeStuatusLineForNavigator(null);
        CommonUITool.openInformationBox(
                Messages.restoreQueryEditorTitle, Messages.restoreQueryEditorRestoreSuccess);
    }
}
