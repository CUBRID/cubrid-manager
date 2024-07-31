/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.common.ui.spi.table;

import org.eclipse.jface.viewers.CellNavigationStrategy;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * Table cell focus manager
 *
 * @author pangqiren
 * @version 1.0 - 2012-7-16 created by pangqiren
 */
public class TableFocusCellManager {

    private CellNavigationStrategy navigationStrategy = new CellNavigationStrategy();
    private EditableTableViewer viewer;
    private ViewerCell focusCell;
    private TableFocusHighLighter cellHighlighter;
    private DisposeListener itemDeletionListener =
            new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    setFocusCell(null);
                }
            };

    /**
     * The constructor
     *
     * @param viewer
     * @param focusDrawingDelegate
     */
    public TableFocusCellManager(
            EditableTableViewer viewer, TableFocusHighLighter focusDrawingDelegate) {
        this.viewer = viewer;
        this.cellHighlighter = focusDrawingDelegate;
        if (this.cellHighlighter != null) {
            this.cellHighlighter.setMgr(this);
        }
        hookListener(viewer);
    }

    /**
     * Handle with the mouse down event
     *
     * @param event Event
     */
    private void handleMouseDown(Event event) {
        ViewerCell cell = viewer.getCell(new Point(event.x, event.y));
        if (cell != null) {
            if (!cell.equals(focusCell)) {
                setFocusCell(cell);
            }
        }
    }

    /**
     * Handle with the key down event
     *
     * @param event Event
     */
    private void handleKeyDown(Event event) {
        ViewerCell tmp = null;
        if (navigationStrategy.isCollapseEvent(viewer, focusCell, event)) {
            navigationStrategy.collapse(viewer, focusCell, event);
        } else if (navigationStrategy.isExpandEvent(viewer, focusCell, event)) {
            navigationStrategy.expand(viewer, focusCell, event);
        } else if (navigationStrategy.isNavigationEvent(viewer, event)) {
            tmp = navigationStrategy.findSelectedCell(viewer, focusCell, event);
            if (tmp != null) {
                if (!tmp.equals(focusCell)) {
                    setFocusCell(tmp);
                }
            }
        }
        if (navigationStrategy.shouldCancelEvent(viewer, event)) {
            event.doit = false;
        }
    }

    /**
     * Handle the selection event
     *
     * @param event Event
     */
    private void handleSelection(Event event) {
        if ((event.detail & SWT.CHECK) == 0
                && focusCell != null
                && focusCell.getItem() != event.item
                && event.item != null
                && !event.item.isDisposed()) {
            ViewerRow row = viewer.getViewerRowByItem(event.item);
            ViewerCell tmp = row.getCell(focusCell.getColumnIndex());
            if (!focusCell.equals(tmp)) {
                setFocusCell(tmp);
            }
        }
    }

    /**
     * Handles the {@link SWT#FocusIn} event.
     *
     * @param event the event
     */
    private void handleFocusIn(Event event) {
        if (focusCell == null) {
            setFocusCell(getInitialFocusCell());
        }
    }

    /**
     * Hook the listener
     *
     * @param viewer ColumnViewer
     */
    private void hookListener(final ColumnViewer viewer) {
        Listener listener =
                new Listener() {

                    public void handleEvent(Event event) {
                        switch (event.type) {
                            case SWT.MouseDown:
                                handleMouseDown(event);
                                break;
                            case SWT.KeyDown:
                                handleKeyDown(event);
                                break;
                            case SWT.Selection:
                                handleSelection(event);
                                break;
                            case SWT.FocusIn:
                                handleFocusIn(event);
                                break;
                        }
                    }
                };

        viewer.getControl().addListener(SWT.MouseDown, listener);
        viewer.getControl().addListener(SWT.KeyDown, listener);
        viewer.getControl().addListener(SWT.Selection, listener);
        viewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (event.getSelection().isEmpty()) {
                            setFocusCell(null);
                        }
                    }
                });
        viewer.getControl().addListener(SWT.FocusIn, listener);
    }

    /**
     * Get the original focus cell
     *
     * @return ViewerCell
     */
    final ViewerCell _getFocusCell() {
        return focusCell;
    }

    /**
     * Set the focus cell
     *
     * @param focusCell
     */
    public void setFocusCell(ViewerCell focusCell) {

        ViewerCell oldCell = this.focusCell;

        if (this.focusCell != null && !this.focusCell.getItem().isDisposed()) {
            this.focusCell.getItem().removeDisposeListener(itemDeletionListener);
        }

        this.focusCell = focusCell;

        if (this.focusCell != null && !this.focusCell.getItem().isDisposed()) {
            this.focusCell.getItem().addDisposeListener(itemDeletionListener);
        }

        if (focusCell != null) {
            focusCell.scrollIntoView();
        }

        this.cellHighlighter.focusCellChanged(focusCell, oldCell);

        getViewer().getControl().getAccessible().setFocus(ACC.CHILDID_SELF);
    }

    /**
     * Get the viewer
     *
     * @return EditableTableViewer
     */
    public EditableTableViewer getViewer() {
        return viewer;
    }

    /**
     * Get the initial focus cell
     *
     * @return ViewerCell
     */
    public ViewerCell getInitialFocusCell() {
        Table table = (Table) getViewer().getControl();

        if (!table.isDisposed()
                && table.getItemCount() > 0
                && !table.getItem(table.getTopIndex()).isDisposed()) {
            final ViewerRow aViewerRow =
                    getViewer().getViewerRowByItem(table.getItem(table.getTopIndex()));
            if (table.getColumnCount() == 0) {
                return aViewerRow.getCell(0);
            }

            Rectangle clientArea = table.getClientArea();
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (aViewerRow.getBounds(i).width > 0
                        && columnInVisibleArea(clientArea, aViewerRow, i))
                    return aViewerRow.getCell(i);
            }
        }
        return null;
    }

    /**
     * Return whether column in visible area
     *
     * @param clientArea Rectangle
     * @param row ViewerRow
     * @param colIndex int
     * @return boolean
     */
    private boolean columnInVisibleArea(Rectangle clientArea, ViewerRow row, int colIndex) {
        return row.getBounds(colIndex).x >= clientArea.x;
    }

    /**
     * Get the focus cell
     *
     * @return ViewerCell
     */
    public ViewerCell getFocusCell() {
        ViewerCell cell = this.focusCell;
        Table t = (Table) getViewer().getControl();
        // It is possible that the selection has changed under the hood
        if (cell != null) {
            if (t.getSelection().length == 1 && t.getSelection()[0] != cell.getItem()) {
                setFocusCell(
                        getViewer()
                                .getViewerRowByItem(t.getSelection()[0])
                                .getCell(cell.getColumnIndex()));
            }
        }
        return this.focusCell;
    }
}
