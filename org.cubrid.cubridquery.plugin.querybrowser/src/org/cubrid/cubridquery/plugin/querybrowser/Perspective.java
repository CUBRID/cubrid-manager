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

package org.cubrid.cubridquery.plugin.querybrowser;

import com.cubrid.common.ui.common.navigator.CubridColumnNavigatorView;
import com.cubrid.common.ui.common.navigator.CubridDdlNavigatorView;
import com.cubrid.common.ui.common.navigator.CubridIndexNavigatorView;
import com.cubrid.common.ui.common.navigator.FavoriteQueryNavigatorView;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.er.editor.ERDThumbnailViewPart;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.IViewLayout;

/**
 * This class is responsible for initial CUBRID Query Browser workbench Window layout Perspective
 *
 * @author Kevin.Wang
 *     <p>Create at 2014-4-14
 */
public class Perspective implements IPerspectiveFactory {

    /**
     * Create initial layout for CUBRID Query Browser workbench window
     *
     * @param layout the workbench page layout object
     */
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(true);
        layout.setEditorAreaVisible(true);

        IFolderLayout navigatorFolder =
                layout.createFolder(
                        "NavigatorFolder", IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
        // disallowStateChanges(navigatorFolder);
        navigatorFolder.addView(CubridQueryNavigatorView.ID);
        IViewLayout viewLayout = layout.getViewLayout(CubridQueryNavigatorView.ID);
        viewLayout.setCloseable(false);
        viewLayout.setMoveable(false);

        IPlaceholderFolderLayout FavoriteSqlPlaceFolder =
                layout.createPlaceholderFolder(
                        "FavoriteSqlPlaceFolder",
                        IPageLayout.RIGHT,
                        0.7f,
                        IPageLayout.ID_EDITOR_AREA);
        FavoriteSqlPlaceFolder.addPlaceholder(FavoriteQueryNavigatorView.ID);

        boolean isAutoShowSchemaInfo = GeneralPreference.isAutoShowSchemaInfo();
        if (isAutoShowSchemaInfo) {
            IFolderLayout columnsFolder =
                    layout.createFolder(
                            "ColumnsFolder", IPageLayout.BOTTOM, 0.75f, "NavigatorFolder");

            columnsFolder.addView(CubridColumnNavigatorView.ID);
            columnsFolder.addView(CubridIndexNavigatorView.ID);
            columnsFolder.addView(CubridDdlNavigatorView.ID);
            columnsFolder.addView(ERDThumbnailViewPart.ID);

            viewLayout = layout.getViewLayout(CubridColumnNavigatorView.ID);
            viewLayout.setCloseable(false);
            viewLayout.setMoveable(false);

            viewLayout = layout.getViewLayout(CubridIndexNavigatorView.ID);
            viewLayout.setCloseable(false);
            viewLayout.setMoveable(false);

            viewLayout = layout.getViewLayout(CubridDdlNavigatorView.ID);
            viewLayout.setCloseable(false);
            viewLayout.setMoveable(false);

            viewLayout = layout.getViewLayout(ERDThumbnailViewPart.ID);
            viewLayout.setCloseable(false);
            viewLayout.setMoveable(false);
        }
    }
}
