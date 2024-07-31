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
package com.cubrid.common.ui.spi.table;

import com.cubrid.common.ui.spi.ResourceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Table Select Support
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-2-21 created by Kevin.Wang
 */
public class TableSelectSupport {
    private final Table table;
    private final TableCursor tableCursor;
    /*Selected cell background*/
    private Color selectedCellBG;
    /*Unselected cell background, but other cell has selected which are in the same row*/
    private Color unSelectedCellBG;
    private Color lineNumBG;
    private Color oddRowBG;
    private Color evenRowBG;

    private Point startPoint;
    private boolean pressState;
    private boolean ctrlPressed;
    private boolean shiftPressed;

    /*Save the select table items point*/
    private Set<Point> selectedList = new LinkedHashSet<Point>();
    private Point lastPoint = new Point(0, 0);
    private int handleMinDistance = 5;

    private List<ISelectionChangeListener> listenerList = new ArrayList<ISelectionChangeListener>();
    private Button moreButton;
    private int detailButtonWidth = 15;
    private IShowMoreOperator showMoreOperator;
    // private Color cursorFG;

    /** The constructor */
    public TableSelectSupport(Table table, TableCursor tableCursor) {
        this.table = table;
        this.tableCursor = tableCursor;
        selectedCellBG =
                ResourceManager.getColor(
                        51, 153,
                        255); // table.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
        unSelectedCellBG =
                ResourceManager.getColor(
                        204, 204, 204); // table.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
        oddRowBG =
                ResourceManager.getColor(
                        SWT.COLOR_WHITE); // ResourceManager.getColor(230, 230, 230);
        evenRowBG = ResourceManager.getColor(SWT.COLOR_WHITE);
        lineNumBG = ResourceManager.getColor(SWT.COLOR_GRAY);

        tableCursor.setBackground(selectedCellBG);
        // cursorFG = ResourceManager.getColor(SWT.COLOR_DARK_RED);
        // tableCursor.setForeground(cursorFG);

        bindListener();
    }

    /** Bind the linsteners */
    private void bindListener() {
        tableCursor.addMouseListener(
                new MouseListener() {
                    public void mouseUp(MouseEvent event) {
                        handleMouseUp(event);
                    }

                    public void mouseDown(MouseEvent event) {
                        handleMouseDown(event);
                    }

                    public void mouseDoubleClick(MouseEvent event) {
                        handleMouseDoubleClick(event);
                    }
                });

        tableCursor.addMouseMoveListener(
                new MouseMoveListener() {
                    public void mouseMove(MouseEvent event) {
                        handleMouseMove(event);
                    }
                });

        table.addMouseListener(
                new MouseListener() {
                    public void mouseUp(MouseEvent event) {
                        handleMouseUp(event);
                    }

                    public void mouseDown(MouseEvent event) {
                        handleMouseDown(event);
                    }

                    public void mouseDoubleClick(MouseEvent event) {
                        handleMouseDoubleClick(event);
                    }
                });
        table.addMouseTrackListener(
                new MouseTrackAdapter() {
                    public void mouseExit(MouseEvent e) {
                        Rectangle bound = table.getClientArea();
                        if (!bound.contains(e.x, e.y)) {
                            handleMouseExit(e);
                        }
                    }
                });

        table.addMouseMoveListener(
                new MouseMoveListener() {
                    public void mouseMove(MouseEvent event) {
                        handleMouseMove(event);
                    }
                });
        table.addKeyListener(
                new KeyListener() {
                    public void keyReleased(KeyEvent event) {
                        handleKeyReleased(event);
                    }

                    public void keyPressed(KeyEvent event) {
                        handleKeyPressed(event);
                    }
                });

        tableCursor.addKeyListener(
                new KeyListener() {

                    public void keyReleased(KeyEvent event) {
                        handleKeyReleased(event);
                    }

                    public void keyPressed(KeyEvent event) {
                        handleKeyPressed(event);

                        if (ctrlPressed) {
                            if (event.keyCode == SWT.ARROW_DOWN) {
                                int maxY = getMaxY();
                                int minX = getMinX();
                                int maxX = getMaxX();
                                int y = maxY + 1;
                                if (maxY > -1
                                        && minX > -1
                                        && maxX > -1
                                        && y < table.getItemCount()) {
                                    tableCursor.setSelection(y, maxX);
                                    for (int x = minX; x <= maxX; x++) {
                                        Point p = new Point(x, y);
                                        addSelectPoint(p, true);
                                        drawSelectedItem(p, true);
                                    }
                                }
                            } else if (event.keyCode == SWT.ARROW_UP) {
                                int maxY = getMaxY();
                                int minX = getMinX();
                                int maxX = getMaxX();
                                int y = maxY;
                                if (maxY > -1 && minX > -1 && maxX > -1 && y > 0) {
                                    tableCursor.setSelection(y - 1, maxX);
                                    for (int x = minX; x <= maxX; x++) {
                                        Point p = new Point(x, y);
                                        removeSelectPoint(p, true);
                                        drawUnselectedItem(p, true);
                                    }
                                }
                            } else if (event.keyCode == SWT.ARROW_LEFT) {
                                int maxY = getMaxY();
                                int minY = getMinY();
                                int maxX = getMaxX();
                                if (maxY > -1 && minY > -1 && maxX > 0) {
                                    tableCursor.setSelection(maxY, maxX - 1);
                                    for (int y = minY; y <= maxY; y++) {
                                        Point p = new Point(maxX, y);
                                        removeSelectPoint(p, true);
                                        drawUnselectedItem(p, true);
                                    }
                                }
                            } else if (event.keyCode == SWT.ARROW_RIGHT) {
                                int maxY = getMaxY();
                                int minY = getMinY();
                                int maxX = getMaxX();
                                int columnCount = table.getColumnCount();
                                if (maxY > -1 && minY > -1 && maxX + 1 < columnCount) {
                                    int x = maxX + 1;
                                    tableCursor.setSelection(maxY, x);
                                    for (int y = minY; y <= maxY; y++) {
                                        Point p = new Point(x, y);
                                        addSelectPoint(p, true);
                                        drawSelectedItem(p, true);
                                    }
                                }
                            }
                        } else if (!ctrlPressed && !shiftPressed) {
                            if (event.keyCode == SWT.ARROW_DOWN
                                    || event.keyCode == SWT.ARROW_UP
                                    || event.keyCode == SWT.ARROW_LEFT
                                    || event.keyCode == SWT.ARROW_RIGHT) {
                                selectedList.clear();
                                int y = table.indexOf(tableCursor.getRow());
                                int x = tableCursor.getColumn();
                                Point p = new Point(x, y);
                                addSelectPoint(p, true);
                                drawSelectedItems(true);
                            }
                        }
                    }
                });
    }

    /**
     * Handle mouse down event
     *
     * @param event
     */
    private void handleMouseDown(MouseEvent event) {
        /*Right button,Show the menu*/
        if (table == null || tableCursor == null || tableCursor.getRow() == null) {
            return;
        }
        if (event.button == 3 && table.getMenu() != null) {
            int rowIndex = table.indexOf(tableCursor.getRow());
            int columnIndex = tableCursor.getColumn();
            Point point = new Point(columnIndex, rowIndex);
            if (!selectedList.contains(point)) {
                selectedList.clear();

                addSelectPoint(point, true);
                drawSelectedItems(true);
            }
            table.getMenu().setVisible(true);
        }

        if (event.button == 1) {
            int rowIndex = table.indexOf(tableCursor.getRow());
            int columnIndex = tableCursor.getColumn();
            Point pt = new Point(columnIndex, rowIndex);

            if (ctrlPressed) {
                performCtrlMouseDown(pt);
            } else if (shiftPressed && startPoint != null) {
                performShiftMouseDown(startPoint, pt);
            } else {
                startPoint = pt;
                pressState = true;
                selectedList.clear();

                addSelectPoint(startPoint, true);
                drawSelectedItems(true);

                redrawMoreButton();
            }
        }
    }

    public void redrawMoreButton() {
        if (moreButton != null && !moreButton.isDisposed()) {
            moreButton.dispose();
        }

        if (showMoreOperator != null) {
            if (tableCursor != null && !tableCursor.isDisposed() && tableCursor.getRow() != null) {
                final int rowIndex = table.indexOf(tableCursor.getRow());
                final int columnIndex = tableCursor.getColumn();

                if (showMoreOperator.isShowButton(rowIndex, columnIndex)) {
                    moreButton = new Button(tableCursor, SWT.None);
                    moreButton.setText("...");
                    Rectangle location = tableCursor.getBounds();
                    int x = location.width - detailButtonWidth;
                    if (x < 0) {
                        x = 0;
                    }
                    moreButton.setBounds(x, 0, detailButtonWidth, location.height);
                    moreButton.addSelectionListener(
                            new SelectionListener() {
                                public void widgetSelected(SelectionEvent e) {
                                    widgetDefaultSelected(e);
                                }

                                public void widgetDefaultSelected(SelectionEvent e) {
                                    if (showMoreOperator != null) {
                                        showMoreOperator.handleButtonEvent(rowIndex, columnIndex);
                                    }
                                }
                            });
                    moreButton.addMouseListener(
                            new MouseListener() {
                                public void mouseUp(MouseEvent e) {
                                    handleMouseUp(e);
                                }

                                public void mouseDown(MouseEvent e) {}

                                public void mouseDoubleClick(MouseEvent e) {}
                            });
                }
            }
        }
    }

    /**
     * Handle mouse move event
     *
     * @param event
     */
    private void handleMouseMove(MouseEvent event) {
        if (!pressState) {
            return;
        }
        int pointX = event.x;
        int pointY = event.y;

        if (event.getSource() == tableCursor) {
            pointX = event.x + tableCursor.getBounds().x;
            pointY = event.y + tableCursor.getBounds().y;
        }

        if (Math.abs(pointX - lastPoint.x) < handleMinDistance
                && Math.abs(pointY - lastPoint.y) < handleMinDistance) {
            lastPoint = new Point(pointX, pointY);
            return;
        }

        Point location = getSelectedLocation(table, pointX, pointY);
        if (location == null) {
            return;
        }
        if (startPoint != null) {
            selectedList.clear();

            int minY = Math.min(startPoint.y, location.y);
            int maxY = Math.max(startPoint.y, location.y);

            int minX = Math.min(startPoint.x, location.x);
            int maxX = Math.max(startPoint.x, location.x);

            int colLen = table.getColumnCount();

            if (location.x != 0 && location.y != 0 && minX == 0) {
                maxX = colLen - 1;
            }

            for (int i = minX; i <= maxX; i++) {
                for (int j = minY; j <= maxY; j++) {
                    addSelectPoint(new Point(i, j), true);
                }
            }
        }
        drawSelectedItems(true);
    }

    /**
     * Perform mouse up
     *
     * @param event
     */
    private void handleMouseUp(MouseEvent event) {
        if (event.button != 1) {
            return;
        }
        pressState = false;
    }

    /**
     * Perform mouse double click
     *
     * @param event
     */
    private void handleMouseDoubleClick(MouseEvent event) {
        if (event.button != 1) {
            return;
        }
        pressState = false;
    }

    private void handleMouseExit(MouseEvent e) {
        pressState = false;
    }
    /**
     * Handler key release event
     *
     * @param event
     */
    private void handleKeyReleased(KeyEvent event) {

        if ((event.stateMask & SWT.CTRL) != 0 || (event.stateMask & SWT.COMMAND) != 0) {
            ctrlPressed = false;
        } else if ((event.stateMask & SWT.SHIFT) != 0 || event.keyCode == SWT.SHIFT) {
            shiftPressed = false;
        }
    }

    /**
     * Handle key press event
     *
     * @param event
     */
    private void handleKeyPressed(KeyEvent event) {
        if ((event.stateMask & SWT.CTRL) != 0 || (event.stateMask & SWT.COMMAND) != 0) {
            ctrlPressed = true;
        } else if ((event.stateMask & SWT.SHIFT) != 0 || event.keyCode == SWT.SHIFT) {
            shiftPressed = true;
        } else {
            ctrlPressed = false;
            shiftPressed = false;
        }
    }

    /**
     * Perform CTRL + mouse down
     *
     * @param location
     */
    private void performCtrlMouseDown(Point location) {
        addSelectPoint(location, true);
        drawSelectedItem(location, true);
    }

    /**
     * Perform SHIFT + mouse down
     *
     * @param location
     */
    private void performShiftMouseDown(Point start, Point end) {
        selectedList.clear();

        int startX = start.x < end.x ? start.x : end.x;
        int startY = start.y < end.y ? start.y : end.y;
        int endX = start.x > end.x ? start.x : end.x;
        int endY = start.y > end.y ? start.y : end.y;

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                addSelectPoint(new Point(i, j), true);
            }
        }

        drawSelectedItems(true);
    }

    /**
     * Get selected items min x
     *
     * @return
     */
    private int getMinX() {
        int minX = Integer.MAX_VALUE;
        for (Point point : selectedList) {
            if (point.x < minX) {
                minX = point.x;
            }
        }
        if (minX != Integer.MAX_VALUE) {
            return minX;
        }
        return -1;
    }

    /**
     * Get selected items max x
     *
     * @return
     */
    private int getMaxX() {
        int maxX = -1;
        for (Point point : selectedList) {
            if (point.x > maxX) {
                maxX = point.x;
            }
        }
        return maxX;
    }

    /**
     * Get selected items max y
     *
     * @return
     */
    private int getMaxY() {
        int maxY = -1;
        for (Point point : selectedList) {
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        return maxY;
    }

    /**
     * Get selected items min y
     *
     * @return
     */
    private int getMinY() {
        int minY = Integer.MAX_VALUE;
        for (Point point : selectedList) {
            if (point.y < minY) {
                minY = point.y;
            }
        }
        if (minY != Integer.MAX_VALUE) {
            return minY;
        }
        return -1;
    }

    /**
     * Draw all selected items
     *
     * @param isNotify
     */
    private void drawSelectedItems(boolean isNotify) {
        /*Clear all*/
        int itemLen = table.getItemCount();
        for (int itemIndex = 0; itemIndex < itemLen; itemIndex++) {
            drawUnselectedItem(itemIndex, false);
        }
        /*Draw selected items*/
        for (Point location : selectedList) {
            drawSelectedItem(location, false);
        }
        table.deselectAll();

        if (isNotify) {
            fireSelectionChanged();
        }
    }

    /**
     * Draw selected item by location
     *
     * @param location
     */
    private void drawSelectedItem(Point location, boolean isNotify) {
        int colLen = table.getColumnCount();
        TableItem item = table.getItem(location.y);
        for (int x = 0; x < colLen; x++) {
            if (x == location.x || location.x == 0) {
                item.setBackground(x, selectedCellBG);
            } else if (!selectedList.contains(new Point(x, location.y))) {
                item.setBackground(x, unSelectedCellBG);
            }
        }
        table.deselectAll();

        if (isNotify) {
            fireSelectionChanged();
        }
    }

    /**
     * Draw unselected item by location
     *
     * @param index
     */
    private void drawUnselectedItem(Point location, boolean isNotify) {
        TableItem item = table.getItem(location.y);

        boolean isSelectedInSameRow = isSelectSameRow(location);
        if (isSelectedInSameRow) {
            if (location.x == 0) {
                item.setBackground(0, lineNumBG);
            } else {
                item.setBackground(location.x, unSelectedCellBG);
            }
        } else {
            int columnCount = table.getColumnCount();
            Color bg = null;
            if (location.y % 2 == 0) {
                bg = oddRowBG;
            } else {
                bg = evenRowBG;
            }
            item.setBackground(0, lineNumBG);
            for (int i = 1; i < columnCount; i++) {
                item.setBackground(i, bg);
            }
        }
        table.deselectAll();

        if (isNotify) {
            fireSelectionChanged();
        }
    }

    /**
     * Draw unselected item by index
     *
     * @param index
     */
    private void drawUnselectedItem(int index, boolean isNotify) {
        int colLen = table.getColumnCount();
        TableItem item = table.getItem(index);
        for (int i = 0; i < colLen; i++) {
            if (i == 0) {
                item.setBackground(0, lineNumBG);
            } else if (index % 2 == 0) {
                item.setBackground(i, oddRowBG);
            } else {
                item.setBackground(i, evenRowBG);
            }
        }
        table.deselectAll();

        if (isNotify) {
            fireSelectionChanged();
        }
    }

    /**
     * Judge is selected other items on the same row
     *
     * @param location
     * @return
     */
    private boolean isSelectSameRow(Point location) {
        for (Point p : selectedList) {
            if (p.y == location.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add the table item location to selected list
     *
     * @param location
     * @param isInRole
     */
    private void addSelectPoint(Point location, boolean isInRole) {
        selectedList.add(location);
        if (isInRole && location.x == 0) {
            for (int x = 1; x < table.getColumnCount(); x++) {
                selectedList.add(new Point(x, location.y));
            }
        }
    }

    /**
     * Remove the table item location from selected list
     *
     * @param location
     * @param isInRole
     */
    private void removeSelectPoint(Point location, boolean isInRole) {
        selectedList.remove(location);

        if (isInRole && location.x == 0) {
            for (int x = 1; x < table.getColumnCount(); x++) {
                selectedList.remove(new Point(x, location.y));
            }
        }
    }

    /**
     * Get selected table item's point
     *
     * @param table
     * @param x
     * @param y
     * @return
     */
    private Point getSelectedLocation(Table table, int x, int y) {
        Point pt = new Point(x, y);
        TableItem item = table.getItem(pt);
        /*While the Table style is not full selection, the performance is low*/
        if (item == null) {
            int columnCount = table.getColumnCount();
            for (TableItem temp : table.getItems()) {
                for (int i = 0; i < columnCount; i++) {
                    if (temp.getBounds(i).contains(pt)) {
                        item = temp;
                        break;
                    }
                }
            }
        }

        if (item == null) {
            return null;
        }

        int columnCount = table.getColumnCount();
        for (int col = 0; col < columnCount; col++) {
            Rectangle rect = item.getBounds(col);
            if (rect.contains(pt)) {
                int row = table.indexOf(item);
                return new Point(col, row);
            }
        }

        return null;
    }

    /**
     * Judge is selected the table item
     *
     * @return
     */
    public boolean hasSelected() {
        if (selectedList.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Get selected table items.
     *
     * @return
     */
    public TableItem[] getSelectedTableItems() {
        List<TableItem> selectedItems = new ArrayList<TableItem>();

        Set<Integer> addedSet = new HashSet<Integer>();
        for (Point location : selectedList) {
            if (!addedSet.contains(location.y)) {
                TableItem item = table.getItem(location.y);
                selectedItems.add(item);
                addedSet.add(location.y);
            }
        }

        return selectedItems.toArray(new TableItem[0]);
    }

    /** Select all the table items */
    public void selectAll() {
        int colLen = table.getColumnCount();
        int rowLen = table.getItemCount();

        for (int i = 0; i < colLen; i++) {
            for (int j = 0; j < rowLen; j++) {
                selectedList.add(new Point(i, j));
            }
        }

        drawSelectedItems(true);
    }

    public void setSelection(TableItem item, int column) {
        selectedList.clear();
        int rowIndex = -1;
        if (item != null && table.getColumnCount() > column) {
            TableItem[] items = table.getItems();
            for (int i = 0; i < items.length; i++) {
                TableItem tempItem = items[i];
                if (tempItem.equals(item)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex >= 0) {
                tableCursor.setSelection(rowIndex, column);
                addSelectPoint(new Point(column, rowIndex), true);
                drawSelectedItems(true);
            }
        }
    }

    /**
     * Get the table
     *
     * @return
     */
    public Table getTable() {
        return table;
    }

    /**
     * Get the table cursor
     *
     * @return
     */
    public TableCursor getTableCursor() {
        return tableCursor;
    }

    /**
     * Get selected locations
     *
     * @return
     */
    public List<Point> getSelectedLocations() {
        List<Point> list = new ArrayList<Point>();
        list.addAll(selectedList);
        Collections.sort(list, new SelectedLoactionComparator());

        return list;
    }

    /**
     * Add select change listener
     *
     * @param listener
     */
    public void addSelectChangeListener(ISelectionChangeListener listener) {
        listenerList.add(listener);
    }

    /** Fire selection changed */
    private void fireSelectionChanged() {
        Point[] selectedArray = new Point[selectedList.size()];
        selectedList.toArray(selectedArray);

        for (ISelectionChangeListener listener : listenerList) {
            listener.selectionChanged(new SelectionChangeEvent(selectedArray));
        }
    }

    /**
     * Set the selected point list
     *
     * @param pointList List<Point>
     */
    public void setSelection(List<Point> pointList) {
        selectedList.clear();
        for (Point point : pointList) {
            addSelectPoint(point, true);
        }
        drawSelectedItems(true);
    }

    /**
     * Get show more operator
     *
     * @return the showDetailOperator
     */
    public IShowMoreOperator getShowDetailOperator() {
        return showMoreOperator;
    }

    /**
     * Set show more operator
     *
     * @param showDetailOperator the showDetailOperator to set
     */
    public void setShowDetailOperator(IShowMoreOperator showDetailOperator) {
        this.showMoreOperator = showDetailOperator;
    }
}

/**
 * Selected Loaction Comparator
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-2-21 created by Kevin.Wang
 */
class SelectedLoactionComparator implements Comparator<Point> {
    public int compare(Point o1, Point o2) {
        if (o1.y == o2.y) {
            if (o1.x < o2.x) {
                return -1;
            } else if (o1.x > o2.x) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (o1.y < o2.y) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
