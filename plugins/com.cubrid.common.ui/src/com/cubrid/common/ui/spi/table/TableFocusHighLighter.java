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

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Table cell focus highlighter
 *
 * @author pangqiren
 * @version 1.0 - 2012-7-16 created by pangqiren
 */
public class TableFocusHighLighter {

    private EditableTableViewer viewer;
    private TableFocusCellManager mgr;

    /**
     * The constructor
     *
     * @param viewer
     */
    public TableFocusHighLighter(EditableTableViewer viewer) {
        this.viewer = viewer;
        hookListener(viewer);
    }

    public void setMgr(TableFocusCellManager mgr) {
        this.mgr = mgr;
    }

    /**
     * Return the focus cell
     *
     * @return the focus cell
     */
    public ViewerCell getFocusCell() {
        // Mgr is normally not null because the highlighter is passed
        // to the SWTFocusCellManager instance
        if (mgr != null) {
            // Use this method because it ensure that no
            // cell update (which might cause scrolling) happens
            return mgr._getFocusCell();
        }
        return viewer.getColumnViewerEditor().getFocusCell();
    }

    private void markFocusedCell(Event event, ViewerCell cell) {
        Color background =
                (cell.getControl().isFocusControl())
                        ? getSelectedCellBackgroundColor(cell)
                        : getSelectedCellBackgroundColorNoFocus(cell);
        Color foreground =
                (cell.getControl().isFocusControl())
                        ? getSelectedCellForegroundColor(cell)
                        : getSelectedCellForegroundColorNoFocus(cell);

        if (foreground != null || background != null || onlyTextHighlighting(cell)) {
            GC gc = event.gc;

            if (background == null) {
                background = cell.getItem().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
            }

            if (foreground == null) {
                foreground =
                        cell.getItem().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
            }

            gc.setBackground(background);
            gc.setForeground(foreground);

            if (onlyTextHighlighting(cell)) {
                Rectangle area = event.getBounds();
                Rectangle rect = cell.getTextBounds();
                if (rect != null) {
                    area.x = rect.x;
                }
                gc.fillRectangle(area);
            } else {
                gc.fillRectangle(event.getBounds());
            }
            event.detail &= ~SWT.SELECTED;
        }
    }

    private void removeSelectionInformation(Event event, ViewerCell cell) {
        GC gc = event.gc;
        gc.setBackground(cell.getViewerRow().getBackground(cell.getColumnIndex()));
        gc.setForeground(cell.getViewerRow().getForeground(cell.getColumnIndex()));
        gc.fillRectangle(cell.getBounds());
        event.detail &= ~SWT.SELECTED;
    }

    private void hookListener(final EditableTableViewer viewer) {
        Listener listener =
                new Listener() {
                    public void handleEvent(Event event) {
                        if ((event.detail & SWT.SELECTED) > 0) {
                            ViewerCell focusCell = getFocusCell();
                            ViewerRow row = viewer.getViewerRowByItem(event.item);

                            ViewerCell cell = row.getCell(event.index);

                            if (focusCell == null || !cell.equals(focusCell)) {
                                removeSelectionInformation(event, cell);
                            } else {
                                markFocusedCell(event, cell);
                            }
                        }
                    }
                };
        viewer.getControl().addListener(SWT.EraseItem, listener);
    }

    /**
     * The color to use when rendering the background of the selected cell when the control has the
     * input focus
     *
     * @param cell the cell which is colored
     * @return the color or <code>null</code> to use the default
     */
    protected Color getSelectedCellBackgroundColor(ViewerCell cell) {
        return null;
    }

    /**
     * The color to use when rendering the foreground (=text) of the selected cell when the control
     * has the input focus
     *
     * @param cell the cell which is colored
     * @return the color or <code>null</code> to use the default
     */
    protected Color getSelectedCellForegroundColor(ViewerCell cell) {
        return null;
    }

    /**
     * The color to use when rendering the foreground (=text) of the selected cell when the control
     * has <b>no</b> input focus
     *
     * @param cell the cell which is colored
     * @return the color or <code>null</code> to use the same used when control has focus
     */
    protected Color getSelectedCellForegroundColorNoFocus(ViewerCell cell) {
        return null;
    }

    /**
     * The color to use when rendering the background of the selected cell when the control has
     * <b>no</b> input focus
     *
     * @param cell the cell which is colored
     * @return the color or <code>null</code> to use the same used when control has focus
     * @since 3.4
     */
    protected Color getSelectedCellBackgroundColorNoFocus(ViewerCell cell) {
        return null;
    }

    /**
     * Controls whether the whole cell or only the text-area is highlighted
     *
     * @param cell the cell which is highlighted
     * @return <code>true</code> if only the text area should be highlighted
     * @since 3.4
     */
    protected boolean onlyTextHighlighting(ViewerCell cell) {
        return false;
    }

    protected void focusCellChanged(ViewerCell newCell, ViewerCell oldCell) {
        // Redraw new area
        if (newCell != null) {
            Rectangle rect = newCell.getBounds();
            int x = newCell.getColumnIndex() == 0 ? 0 : rect.x;
            int width = newCell.getColumnIndex() == 0 ? rect.x + rect.width : rect.width;
            // 1 is a fix for Linux-GTK
            newCell.getControl().redraw(x, rect.y - 1, width, rect.height + 1, true);
        }

        if (oldCell != null) {
            Rectangle rect = oldCell.getBounds();
            int x = oldCell.getColumnIndex() == 0 ? 0 : rect.x;
            int width = oldCell.getColumnIndex() == 0 ? rect.x + rect.width : rect.width;
            // 1 is a fix for Linux-GTK
            oldCell.getControl().redraw(x, rect.y - 1, width, rect.height + 1, true);
        }
    }
}
