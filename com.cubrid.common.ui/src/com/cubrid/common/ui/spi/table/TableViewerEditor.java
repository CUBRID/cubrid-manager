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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

/**
 * Table viewer editor can edit the column
 *
 * @author pangqiren
 * @version 1.0 - 2012-7-16 created by pangqiren
 */
public class TableViewerEditor extends ColumnViewerEditor {

    private TableEditor tableEditor;
    private TableFocusCellManager focusCellManager;

    /**
     * The constructor
     *
     * @param viewer the viewer the editor is attached to
     * @param focusCellManager the cell focus manager if one used or <code>null</code>
     * @param editorActivationStrategy the strategy used to decide about the editor activation
     * @param feature the feature mask
     */
    public TableViewerEditor(
            final TableViewer viewer,
            final TableFocusCellManager focusCellManager,
            ColumnViewerEditorActivationStrategy editorActivationStrategy,
            int feature) {
        super(viewer, editorActivationStrategy, feature);
        tableEditor = new TableEditor(viewer.getTable());
        this.focusCellManager = focusCellManager;

        // when press ENTER key, edit the foucs cell
        viewer.getTable()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyPressed(KeyEvent event) {
                                if (event.keyCode == SWT.CR) {
                                    IStructuredSelection selecion =
                                            (IStructuredSelection) getViewer().getSelection();
                                    if (selecion == null || selecion.isEmpty()) {
                                        return;
                                    }
                                    ViewerCell focusCell = getFocusCell();
                                    if (focusCell == null) {
                                        return;
                                    }
                                    int columnIndex = focusCell.getColumnIndex();
                                    Object obj = selecion.getFirstElement();
                                    viewer.editElement(obj, columnIndex);
                                }
                            }
                        });
        // Deactive the last table editor
        viewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        Control control = tableEditor.getEditor();
                        ViewerCell focusCell = getFocusCell();
                        if (focusCell == null || control == null || control.isDisposed()) {
                            return;
                        }
                        int columnIndex = focusCell.getColumnIndex();
                        TableItem[] items = viewer.getTable().getSelection();

                        TableItem editorItem = tableEditor.getItem();
                        int editorColumn = tableEditor.getColumn();
                        boolean isDeactive = false;
                        if (items == null || items.length == 0) {
                            isDeactive = true;
                        } else if (!items[0].equals(editorItem)) {
                            isDeactive = true;
                        } else if (columnIndex != editorColumn) {
                            isDeactive = true;
                        }
                        if (isDeactive) {
                            Listener[] listeners = control.getListeners(SWT.FocusOut);
                            if (listeners == null || listeners.length == 0) {
                                return;
                            }
                            for (Listener listener : listeners) {
                                Event focusEvent = new Event();
                                focusEvent.widget = control;
                                focusEvent.type = SWT.FocusOut;
                                listener.handleEvent(focusEvent);
                            }
                        }
                    }
                });
    }

    /**
     * Set the editor
     *
     * @param control Control
     * @param item Item
     * @param columnNumber int
     */
    protected void setEditor(Control control, Item item, int columnNumber) {
        tableEditor.setEditor(control, (TableItem) item, columnNumber);
    }

    /**
     * Set the layout data
     *
     * @param layoutData LayoutData
     */
    protected void setLayoutData(LayoutData layoutData) {
        tableEditor.grabHorizontal = layoutData.grabHorizontal;
        tableEditor.horizontalAlignment = layoutData.horizontalAlignment;
        tableEditor.minimumWidth = layoutData.minimumWidth;
        tableEditor.verticalAlignment = layoutData.verticalAlignment;

        if (layoutData.minimumHeight != SWT.DEFAULT) {
            tableEditor.minimumHeight = layoutData.minimumHeight;
        }
    }

    /**
     * Get the focus cell
     *
     * @return ViewerCell
     */
    public ViewerCell getFocusCell() {
        if (focusCellManager != null) {
            return focusCellManager.getFocusCell();
        }
        return super.getFocusCell();
    }

    /**
     * Update the focus cell
     *
     * @param focusCell ViewerCell
     * @param event ColumnViewerEditorActivationEvent
     */
    protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {
        // Update the focus cell when we activated the editor with these 2
        // events
        if (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
                || event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {

            IStructuredSelection selecion = (IStructuredSelection) getViewer().getSelection();
            @SuppressWarnings("rawtypes")
            List list = selecion == null ? new ArrayList() : selecion.toList();

            if (!list.contains(focusCell.getElement())) {
                getViewer().setSelection(new StructuredSelection(focusCell.getElement()), true);
            }

            // Set the focus cell after the selection is updated because else
            // the cell is not scrolled into view
            if (focusCellManager != null) {
                focusCellManager.setFocusCell(focusCell);
            }
        }
    }

    /**
     * Process traverse event
     *
     * @param columnIndex int
     * @param row ViewerRow
     * @param event TraverseEvent
     */
    protected void processTraverseEvent(int columnIndex, ViewerRow row, TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_ARROW_PREVIOUS) {
            event.detail = SWT.TRAVERSE_TAB_PREVIOUS;
            if (event.keyCode == SWT.ARROW_UP) {
                event.stateMask = SWT.CTRL;
            }

        } else if (event.detail == SWT.TRAVERSE_ARROW_NEXT) {
            event.detail = SWT.TRAVERSE_TAB_NEXT;
            if (event.keyCode == SWT.ARROW_DOWN) {
                event.stateMask = SWT.CTRL;
            }
        } else if (event.detail == SWT.TRAVERSE_RETURN) {
            event.detail = SWT.TRAVERSE_TAB_NEXT;
        }
        super.processTraverseEvent(columnIndex, row, event);
    }
}
