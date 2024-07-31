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
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.SubQueryEditorTabItem;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;

/**
 * Combined query editor composite including a SQL editor and result composite. This result
 * composite include a query result composite and a query explain composite.
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-3 created by pangqiren
 */
public class CombinedQueryEditorComposite extends Composite implements ITabSelection {
    public static final Color BACK_COLOR = ResourceManager.getColor(204, 204, 204);
    private static final Logger LOGGER = LogUtil.getLogger(CombinedQueryEditorComposite.class);
    public static final int QUERY_EDITOR_BOTTOM_PCT = 5;
    public static final int QUERY_EDITOR_TOP_PCT = 5;
    public static final Color SASH_COLOR = ResourceManager.getColor(128, 128, 128);
    public static final int SASH_WIDTH = 7;

    private final QueryEditorPart editor;
    private SubQueryEditorTabItem editorTabItem;
    private MultiDBQueryComposite multiDBQueryComp = null;
    private QueryPlanCompositeWithHistory queryPlanResultComp = null;
    private QueryResultComposite queryResultComp;
    private RecentlyUsedSQLComposite recentlyUsedSQLComposite;
    private CTabFolder resultTabFolder;
    private SQLEditorComposite sqlEditorComp;
    private SashForm topSash;

    public CombinedQueryEditorComposite(
            Composite parent,
            int style,
            QueryEditorPart queryEditorPart,
            SubQueryEditorTabItem editorTabItem) {
        super(parent, style);
        this.editor = queryEditorPart;
        this.editorTabItem = editorTabItem;
        setLayout(new FillLayout());
        createTopSash();
    }

    /**
     * Create database object info folder
     *
     * @param database
     * @param schemaNode
     */
    public void createObjInfoFolder(final DefaultSchemaNode schemaNode) {
        CTabItem objInfoTabItem = findTabItem(schemaNode);
        if (objInfoTabItem != null) {
            resultTabFolder.setSelection(objInfoTabItem);
            return;
        }

        String tabName = null;
        if (resultTabFolder.getItemCount() <= 9) {
            tabName = "[" + resultTabFolder.getItemCount() + "]" + schemaNode.getName();
        } else {
            tabName = schemaNode.getName();
        }

        objInfoTabItem = new CTabItem(resultTabFolder, SWT.NONE);
        objInfoTabItem.setText(tabName);
        objInfoTabItem.setShowClose(true);
        objInfoTabItem.addDisposeListener(
                new DisposeListener() {
                    public void widgetDisposed(DisposeEvent e) {
                        renameAllObjectInfoTabs(schemaNode);
                    }
                });
        resultTabFolder.setSelection(objInfoTabItem);

        ObjectInfoComposite objInfoComp = null;
        objInfoComp = new ObjectInfoComposite(resultTabFolder, SWT.NONE, schemaNode);
        objInfoComp.init();
        objInfoTabItem.setControl(objInfoComp);
    }

    /**
     * Create the result folder including query result and query plan result
     *
     * @param topSash SashForm
     * @param sqlEditorParentComp Composite
     */
    private void createResultFolder(final SashForm topSash, final Composite sqlEditorParentComp) {
        final Composite resultComp = new Composite(topSash, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.horizontalSpacing = 0;
            gridLayout.verticalSpacing = 0;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            resultComp.setLayout(gridLayout);
            resultComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }

        // result folder
        final CTabFolder resultTabFolder = new CTabFolder(resultComp, SWT.TOP);
        resultTabFolder.setLayout(new FillLayout());
        resultTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        resultTabFolder.setSimple(false);
        resultTabFolder.setUnselectedImageVisible(true);
        resultTabFolder.setUnselectedCloseVisible(true);
        resultTabFolder.setSelectionBackground(BACK_COLOR);
        resultTabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        resultTabFolder.setMinimizeVisible(true);
        resultTabFolder.setMaximizeVisible(true);
        this.resultTabFolder = resultTabFolder;

        resultTabFolder.addCTabFolder2Listener(
                new CTabFolder2Listener() {
                    public void close(CTabFolderEvent event) {}

                    public void maximize(CTabFolderEvent event) {
                        resultTabFolder.setMaximized(true);
                        topSash.setMaximizedControl(resultComp);
                        editor.getShowResultItem()
                                .setImage(
                                        CommonUIPlugin.getImage(
                                                "icons/queryeditor/qe_panel_down.png"));
                        topSash.layout(true);
                    }

                    public void minimize(CTabFolderEvent event) {
                        resultTabFolder.setMinimized(true);
                        topSash.setMaximizedControl(sqlEditorParentComp);
                        editor.getShowResultItem()
                                .setImage(
                                        CommonUIPlugin.getImage(
                                                "icons/queryeditor/qe_panel_up.png"));
                        topSash.layout(true);
                    }

                    public void restore(CTabFolderEvent event) {
                        resultTabFolder.setMinimized(false);
                        resultTabFolder.setMaximized(false);
                        topSash.setMaximizedControl(null);
                        editor.getShowResultItem()
                                .setImage(
                                        CommonUIPlugin.getImage(
                                                "icons/queryeditor/qe_panel_down.png"));
                        topSash.layout(true);
                    }

                    public void showList(CTabFolderEvent event) {}
                });

        // query result tab area
        queryResultComp = new QueryResultComposite(resultTabFolder, SWT.NONE, editor);

        // query plan result tab area
        queryPlanResultComp = new QueryPlanCompositeWithHistory(resultTabFolder, SWT.NONE, editor);

        // recently Used SQL tab area
        recentlyUsedSQLComposite = new RecentlyUsedSQLComposite(resultTabFolder, SWT.NONE, editor);
        recentlyUsedSQLComposite.initialize();

        // create multiple query
        multiDBQueryComp = new MultiDBQueryComposite(resultTabFolder, SWT.NONE, editor);
        multiDBQueryComp.initialize();
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
        topSash = new SashForm(topComp, SWT.VERTICAL);
        topSash.setBackground(SASH_COLOR);
        topSash.setLayout(new FillLayout());
        /* set top sash width to 0, the black line (topSash's background) won't be display
         * set the normal width when change the editor database(there's no better way to set width)
         * for TOOLS-2375
         */
        topSash.setSashWidth(0);

        Composite sqlEditorParentComp = createSQLEditor(topSash);
        createResultFolder(topSash, sqlEditorParentComp);
        topSash.setWeights(new int[] {QUERY_EDITOR_TOP_PCT, QUERY_EDITOR_BOTTOM_PCT});
    }

    public void dispose() {
        if (resultTabFolder != null && !resultTabFolder.isDisposed()) {
            resultTabFolder.dispose();
        }
        if (queryPlanResultComp != null && !queryPlanResultComp.isDisposed()) {
            queryPlanResultComp.dispose();
        }

        if (recentlyUsedSQLComposite != null && !recentlyUsedSQLComposite.isDisposed()) {
            recentlyUsedSQLComposite.dispose();
        }

        super.dispose();
    }

    /**
     * Find the CTabItem which contains same schemaNode
     *
     * @param schemaNode
     * @return
     */
    private CTabItem findTabItem(DefaultSchemaNode schemaNode) {
        CTabItem[] tabItems = resultTabFolder.getItems();
        for (CTabItem tabItem : tabItems) {
            if (!isObjectInfoTab(tabItem)) {
                continue;
            }

            ObjectInfoComposite comp = (ObjectInfoComposite) tabItem.getControl();
            if (comp.getSchemaNode().getName().equals(schemaNode.getName())) {
                return tabItem;
            }
        }

        return null;
    }

    /**
     * Fire the database changes.
     *
     * @param database - The new database
     */
    public void fireDatabaseChanged(CubridDatabase database) {
        CTabItem[] tabItems = resultTabFolder.getItems();
        for (CTabItem tabItem : tabItems) {
            if (!isObjectInfoTab(tabItem)) {
                continue;
            }

            ObjectInfoComposite infoCom = (ObjectInfoComposite) tabItem.getControl();
            /*If the scheme node's database is not equal, then close it*/
            CubridDatabase nodeDatabase = infoCom.getSchemaNode().getDatabase();
            if (database == null || (database != null && !database.equals(nodeDatabase))) {
                tabItem.dispose();
            }
        }
    }

    /**
     * Fire the schema node changed
     *
     * @param schemaNode
     */
    public void fireSchemaNodeChanged(DefaultSchemaNode schemaNode) {
        CTabItem[] tabItems = resultTabFolder.getItems();
        for (CTabItem tabItem : tabItems) {
            if (!isObjectInfoTab(tabItem)) {
                continue;
            }

            ObjectInfoComposite infoCom = (ObjectInfoComposite) tabItem.getControl();
            DefaultSchemaNode node = infoCom.getSchemaNode();
            /* If the schema node is equal, then close the tabitem */
            if (schemaNode != null
                    && node != null
                    && schemaNode.getDatabase() != null
                    && schemaNode.getDatabase().equals(node.getDatabase())
                    && schemaNode.equals(node)) {
                tabItem.dispose();
            }
        }
    }

    public MultiDBQueryComposite getMultiDBQueryComp() {
        return multiDBQueryComp;
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
    public boolean isOpenedInfoItem(DefaultSchemaNode schemaNode) {
        return findTabItem(schemaNode) != null;
    }

    public QueryPlanCompositeWithHistory newQueryPlanComp() {
        queryPlanResultComp = new QueryPlanCompositeWithHistory(resultTabFolder, SWT.NONE, editor);

        return queryPlanResultComp;
    }

    public QueryResultComposite newQueryResultComp() {
        queryResultComp = new QueryResultComposite(resultTabFolder, SWT.NONE, editor);

        return queryResultComp;
    }

    /** when switch tab item ,refresh some composite */
    public void refreshEditorComposite() {
        editor.setRunItemStatus(sqlEditorComp.hasQueryString());
        if (resultTabFolder.getMinimized()) {
            Image image = CommonUIPlugin.getImage("icons/queryeditor/qe_panel_up.png");
            editor.getShowResultItem().setImage(image);
        } else if (!resultTabFolder.getMaximized() && !resultTabFolder.getMinimized()) {
            Image image = CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png");
            editor.getShowResultItem().setImage(image);
        }
    }

    /**
     * Rename tab names of all object information tabs when there have any changed.
     *
     * @param schemaNode
     */
    public void renameAllObjectInfoTabs(DefaultSchemaNode schemaNode) {
        if (resultTabFolder == null) {
            return;
        }

        int total = resultTabFolder.getItemCount();
        try {
            for (int i = 2, index = 4; i < total; i++) {
                CTabItem item = resultTabFolder.getItem(i);
                if (item == null) {
                    continue;
                }

                Control ctrl = item.getControl();
                if (ctrl == null || !(ctrl instanceof ObjectInfoComposite)) {
                    continue;
                }

                ObjectInfoComposite comp = (ObjectInfoComposite) ctrl;
                if (schemaNode.getName() != null
                        && schemaNode.getName().equals(comp.getTargetName())) {
                    continue;
                }

                String tabName = null;
                if (i >= 9) {
                    tabName = comp.getTargetName();
                } else {
                    tabName = "[" + index + "] " + comp.getTargetName();
                }
                item.setText(tabName);
                index++;
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void rotateQueryPlanDisplayMode() {
        QueryPlanCompositeWithHistory comp = getQueryPlanResultComp();
        if (comp == null || comp.isDisposed()) {
            return;
        }
        comp.rotateQueryPlanDisplayMode();
    }

    /**
     * Select the tab with the main tab index and the sub tab index
     *
     * @param tabIndex
     * @param subTabIndex
     */
    public void select(int tabIndex, int subTabIndex) {
        if (resultTabFolder == null) {
            return;
        }

        if (tabIndex >= 0) {
            int total = resultTabFolder.getItemCount();
            if (tabIndex >= total) {
                return;
            }

            if (resultTabFolder.isDisposed()) {
                return;
            }

            resultTabFolder.setSelection(tabIndex);
        }

        if (subTabIndex >= 0 && resultTabFolder.getSelection() != null) {
            Control ctrl = resultTabFolder.getSelection().getControl();
            if (ctrl instanceof ISubTabSelection) {
                ((ISubTabSelection) ctrl).select(subTabIndex);
            }
        }
    }

    public void setTopSashWidth() {
        topSash.setSashWidth(SASH_WIDTH);
    }

    public void showQueryHistory() {
        resultTabFolder.setSelection(2);
    }

    public QueryPlanCompositeWithHistory getQueryPlanResultComp() {
        return queryPlanResultComp;
    }

    public QueryResultComposite getQueryResultComp() {
        return queryResultComp;
    }

    public RecentlyUsedSQLComposite getRecentlyUsedSQLComposite() {
        return recentlyUsedSQLComposite;
    }

    public CTabFolder getResultTabFolder() {
        return resultTabFolder;
    }

    public SQLEditorComposite getSqlEditorComp() {
        return sqlEditorComp;
    }

    public SashForm getTopSash() {
        return topSash;
    }

    public SubQueryEditorTabItem getSubQueryEditorTabItem() {
        return editorTabItem;
    }

    public boolean isDirty() {
        return sqlEditorComp != null && sqlEditorComp.isDirty();
    }
}
