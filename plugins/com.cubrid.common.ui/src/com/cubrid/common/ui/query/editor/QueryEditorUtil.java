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
package com.cubrid.common.ui.query.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerStatusInfosTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Used for notify all the query editor some event.
 *
 * @author Kevin.Wang
 * @version 1.0 - Apr 26, 2012 created by Kevin.Wang
 */
public class QueryEditorUtil {
    private static final Logger LOGGER = LogUtil.getLogger(QueryEditorUtil.class);

    /**
     * Get all opened query editor
     *
     * @return
     */
    public static List<TextEditorPart> getAllQueryEditorPart() {
        List<TextEditorPart> editorPartList = new ArrayList<TextEditorPart>();

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return editorPartList;
        }

        IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();

        for (IEditorReference reference : editorReferences) {
            if (reference.getId().equals(TextEditorPart.ID)) {
                TextEditorPart editor = (TextEditorPart) reference.getEditor(false);

                editorPartList.add(editor);
            }
        }
        return editorPartList;
    }
}
