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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.editor.TextEditorPart;
import com.cubrid.common.ui.query.editor.SubQueryEditorTabItem;
import com.cubrid.common.ui.spi.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;

/**
 * Combined query editor composite including a SQL editor and result composite. This result
 * composite include a query result composite and a query explain composite.
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-3 created by pangqiren
 */
public class CombinedQueryEditorComposite extends Composite {
    public static final Color BACK_COLOR = ResourceManager.getColor(204, 204, 204);
    private static final Logger LOGGER = LogUtil.getLogger(CombinedQueryEditorComposite.class);
    public static final int QUERY_EDITOR_BOTTOM_PCT = 5;
    public static final int QUERY_EDITOR_TOP_PCT = 5;
    public static final Color SASH_COLOR = ResourceManager.getColor(128, 128, 128);
    public static final int SASH_WIDTH = 7;

    private final TextEditorPart editor;
    private SubQueryEditorTabItem editorTabItem;
    private SQLEditorComposite sqlEditorComp;
    private SashForm topSash;

    public CombinedQueryEditorComposite(
            Composite parent,
            int style,
            TextEditorPart textEditorPart,
            SubQueryEditorTabItem editorTabItem) {
        super(parent, style);
        this.editor = textEditorPart;
        this.editorTabItem = editorTabItem;
        setLayout(new FillLayout());
        createTopSash();
    }

    /**
     * Create the SQL editor
     *
     * @param parent Composite
     * @return Composite
     */
    private Composite createSQLEditor(Composite parent) {
        Composite sqlEditorParentComp = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        sqlEditorParentComp.setLayout(gridLayout);
        sqlEditorParentComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sqlEditorComp =
                new SQLEditorComposite(sqlEditorParentComp, SWT.NONE, editor, editorTabItem);
        return sqlEditorParentComp;
    }

    /** Create the top sash for SQL editor and query result */
    private void createTopSash() {
        Composite topComp = new Composite(this, SWT.NONE);
        topComp.setLayout(new FillLayout());
        createSQLEditor(topComp);
    }

    public void dispose() {
        super.dispose();
    }

    /**
     * Whether the object information tab or not
     *
     * @param tabItem
     * @return
     */
    private boolean isObjectInfoTab(CTabItem tabItem) {
        return tabItem != null
                && !tabItem.isDisposed()
                && tabItem.getControl() != null
                && tabItem.getControl() instanceof ObjectInfoComposite;
    }

    /**
     * Judge is opened same Info tabItem
     *
     * @param schemaNode
     * @return
     */
    /** when switch tab item ,refresh some composite */
    public void refreshEditorComposite() {
//        editor.setRunItemStatus(sqlEditorComp.hasQueryString());
//        if (resultTabFolder.getMinimized()) {
//            Image image = CommonUIPlugin.getImage("icons/queryeditor/qe_panel_up.png");
//            editor.getShowResultItem().setImage(image);
//        } else if (!resultTabFolder.getMaximized() && !resultTabFolder.getMinimized()) {
//            Image image = CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png");
//            editor.getShowResultItem().setImage(image);
//        }
    }
    public SQLEditorComposite getSqlEditorComp() {
        return sqlEditorComp;
    }

    public SubQueryEditorTabItem getSubQueryEditorTabItem() {
        return editorTabItem;
    }

    public boolean isDirty() {
        return sqlEditorComp != null && sqlEditorComp.isDirty();
    }
}
