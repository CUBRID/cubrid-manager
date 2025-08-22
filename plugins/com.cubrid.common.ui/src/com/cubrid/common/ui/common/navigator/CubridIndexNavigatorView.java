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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Index view part
 *
 * @author fulei
 */
public class CubridIndexNavigatorView extends ViewPart {
    public static final String ID = "com.cubrid.common.navigator.indexs";
    private TableViewer tableIndexTableViewer;

    public void createPartControl(Composite parent) {
        tableIndexTableViewer =
                new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);

        Table columnsTable = tableIndexTableViewer.getTable();
        columnsTable.setLinesVisible(true);
        columnsTable.setHeaderVisible(true);

        final TableColumn pkColumn = new TableColumn(columnsTable, SWT.NONE);
        pkColumn.setAlignment(SWT.LEFT);
        pkColumn.setWidth(40);
        pkColumn.setText(Messages.lblIndexType);

        final TableColumn indexNameColumn = new TableColumn(columnsTable, SWT.NONE);
        indexNameColumn.setAlignment(SWT.LEFT);
        indexNameColumn.setWidth(80);
        indexNameColumn.setText(Messages.tblColumnIndexName);

        final TableColumn onColumn = new TableColumn(columnsTable, SWT.NONE);
        onColumn.setAlignment(SWT.LEFT);
        onColumn.setWidth(90);
        onColumn.setText(Messages.tblColumnOnColumns);

        tableIndexTableViewer.setContentProvider(new CubridIndexNavigatorContentProvider());
        tableIndexTableViewer.setLabelProvider(new CubridIndexNavigatorLabelProvider());
    }

    public void setFocus() {
        CubridNavigatorView mainNav = CubridNavigatorView.findNavigationView();
        if (mainNav != null) {
            SchemaInfo schemaInfo = mainNav.getCurrentSchemaInfo();
            updateView(schemaInfo);
        }
    }

    public void updateView(SchemaInfo schemaInfo) {
        if (schemaInfo == null) {
            cleanView();
        }
        redrawView(schemaInfo);
    }

    public void cleanView() {
        try {
            tableIndexTableViewer.setInput(null);
        } catch (Exception ignored) {
        }
    }

    private void redrawView(SchemaInfo schemaInfo) {
        try {
            tableIndexTableViewer.setInput(schemaInfo);
        } catch (Exception ignored) {
        }
        if (schemaInfo != null) {
            // Auto set column size, maximum is 300px, minimum is 100px
            CommonUITool.packTable(tableIndexTableViewer.getTable(), 30, 100);
        }
    }

    public static CubridIndexNavigatorView getInstance() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return null;
        }

        IViewReference viewReference = page.findViewReference(ID);
        if (viewReference != null) {
            IViewPart viewPart = viewReference.getView(false);
            return viewPart instanceof CubridIndexNavigatorView
                    ? (CubridIndexNavigatorView) viewPart
                    : null;
        }

        return null;
    }
}
