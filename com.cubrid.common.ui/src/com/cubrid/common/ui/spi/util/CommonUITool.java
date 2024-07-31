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
package com.cubrid.common.ui.spi.util;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.dialog.MessageDialogWithScrollableMessage;
import com.cubrid.common.ui.common.navigator.CubridDeferredTreeContentManager;
import com.cubrid.common.ui.common.navigator.DeferredContentProvider;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.CopyAllAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.control.SQLEditorComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.service.environment.Constants;
import org.eclipse.swt.BufferedImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * This tool class provide a lot of common convinence method
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class CommonUITool {
    private static final Logger LOGGER = LogUtil.getLogger(CommonUITool.class);

    private static Clipboard clipboard = null;

    public static final int CONFIRM_BOX_RESULT_CANCEL = 1;
    public static final int CONFIRM_BOX_RESULT_OK = 0;
    public static final int CONFIRM_BOX_RESULT_ALWAYS = 2;

    private CommonUITool() {
        // empty
    }

    /**
     * Convert string to int
     *
     * @param str the string value
     * @return the int value
     */
    public static int str2Int(String str) {
        int ret = 0;
        try {
            ret = Integer.parseInt(str);
        } catch (Exception e) {
            ret = 0;
        }
        return ret;
    }

    /**
     * Convert string to double
     *
     * @param str the string value
     * @return the double value
     */
    public static double str2Double(String str) {
        double ret = 0;
        try {
            ret = Double.parseDouble(str);
        } catch (Exception e) {
            ret = 0;
        }
        return ret;
    }

    /**
     * Convert "ON","y" or other string to boolean
     *
     * @param str the string value
     * @return the boolean value
     */
    public static boolean str2Boolean(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals(OnOffType.ON.getText()) || str.equals(YesNoType.Y.getText())) {
            return true;
        }
        return false;
    }

    /**
     * Center this shell
     *
     * @param shell the shell object
     */
    public static void centerShell(Shell shell) {
        if (shell == null) {
            return;
        }

        Rectangle mainBounds = shell.getBounds();
        Rectangle displayBounds = shell.getDisplay().getClientArea();
        if (shell.getShell() == null) {
            mainBounds = displayBounds;
        } else if (PlatformUI.isWorkbenchRunning()) {
            mainBounds =
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
        }
        Rectangle shellBounds = shell.getBounds();

        int x = mainBounds.x + (mainBounds.width - shellBounds.width) / 2;
        int y = mainBounds.y + (mainBounds.height - shellBounds.height) / 2;

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        if ((x + shellBounds.width) > displayBounds.width) {
            x = displayBounds.width - shellBounds.width;
        }
        if ((y + shellBounds.height) > displayBounds.height) {
            y = displayBounds.height - shellBounds.height;
        }

        shell.setLocation(x, y);
    }

    /**
     * Return clipboard object
     *
     * @return the Clipboard object
     */
    public static Clipboard getClipboard() {
        synchronized (CommonUITool.class) {
            if (clipboard == null) {
                clipboard = new Clipboard(Display.getDefault());
                PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getShell()
                        .addDisposeListener(
                                new DisposeListener() {
                                    public void widgetDisposed(DisposeEvent event) {
                                        clipboard.dispose();
                                        clipboard = null;
                                    }
                                });
            }
            return clipboard;
        }
    }

    /**
     * create grid data
     *
     * @param style the GridData style
     * @param horSpan the horizontal span
     * @param verSpan the vertical span
     * @param widthHint the hint width
     * @param heightHint the hint height
     * @return the GridData object
     */
    public static GridData createGridData(
            int style, int horSpan, int verSpan, int widthHint, int heightHint) {
        GridData gridData = new GridData(style);
        gridData.horizontalSpan = horSpan;
        gridData.verticalSpan = verSpan;
        if (widthHint >= 0) {
            gridData.widthHint = widthHint;
        }
        if (heightHint >= 0) {
            gridData.heightHint = heightHint;
        }
        return gridData;
    }

    /**
     * Create Grid data
     *
     * @param horSpan the horizontal span
     * @param verSpan the vertical span
     * @param widthHint the hint width
     * @param heightHint the hint height
     * @return the GridData object
     */
    public static GridData createGridData(int horSpan, int verSpan, int widthHint, int heightHint) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = horSpan;
        gridData.verticalSpan = verSpan;
        if (heightHint >= 0) {
            gridData.heightHint = heightHint;
        }
        if (widthHint >= 0) {
            gridData.widthHint = widthHint;
        }
        return gridData;
    }

    public static GridLayout createGridLayout(
            Composite parent,
            int columns,
            int marginTop,
            int marginRight,
            int marginBottom,
            int marginLeft,
            int marginWidth,
            int marginHeight,
            int horizontalSpacing,
            int verticalSpacing) {
        GridLayout layout = new GridLayout(columns, false);
        layout.verticalSpacing = verticalSpacing;
        layout.horizontalSpacing = horizontalSpacing;
        layout.marginLeft = marginLeft;
        layout.marginRight = marginRight;
        layout.marginTop = marginTop;
        layout.marginBottom = marginBottom;
        layout.marginWidth = marginWidth;
        layout.marginHeight = marginHeight;
        parent.setLayout(layout);
        return layout;
    }

    public static GridLayout createGridLayout(
            int columns, int marginTop, int marginRight, int marginBottom, int marginLeft) {
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        layout.marginBottom = marginBottom;
        layout.marginTop = marginTop;
        layout.marginLeft = marginLeft;
        layout.marginRight = marginRight;
        return layout;
    }

    public static GridLayout createGridLayout(int columns, int marginWidth, int marginHeight) {
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        layout.marginWidth = marginWidth;
        layout.marginHeight = marginHeight;
        return layout;
    }

    public static GridLayout createGridLayout(int columns) {
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        return layout;
    }

    /**
     * Open Message dialog
     *
     * @param sh the shell object
     * @param dialogImageType the image type
     * @param title the title
     * @param msg the detail message
     * @param dialogButton the button string array
     * @return the integer value
     */
    public static int openMsgBox(
            Shell sh, int dialogImageType, String title, String msg, String[] dialogButton) {
        return openMsgBox(sh, dialogImageType, title, msg, "", dialogButton);
    }

    public static int openMsgBox(
            Shell sh,
            int dialogImageType,
            String title,
            String mainMessage,
            String secondaryMessage,
            String[] dialogButton) {
        Shell shell = sh;
        if (shell == null) {
            shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        }
        MessageDialogWithScrollableMessage dialog =
                new MessageDialogWithScrollableMessage(
                        shell,
                        title,
                        null,
                        mainMessage,
                        secondaryMessage,
                        dialogImageType,
                        dialogButton);
        return dialog.open();
    }

    /**
     * Open confirm box
     *
     * @param sh the shell object
     * @param msg the detail message
     * @return <code>true</code> if confirm;<code>false</code> otherwise
     */
    public static boolean openConfirmBox(Shell sh, String msg) {
        return openMsgBox(
                        sh,
                        MessageDialog.WARNING,
                        Messages.titleConfirm,
                        msg,
                        new String[] {Messages.btnYes, Messages.btnNo})
                == 0;
    }

    /**
     * Open confirm box
     *
     * @param msg the detail message
     * @return <code>true</code> if confirm;<code>false</code> otherwise
     */
    public static boolean openConfirmBox(String msg) {
        return openConfirmBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), msg);
    }

    public static int openConfirmBoxWithAlways(String msg) {
        Shell sh = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        return openMsgBox(
                sh,
                MessageDialog.WARNING,
                Messages.titleConfirm,
                msg,
                new String[] {Messages.btnYes, Messages.btnNo, Messages.btnAlwaysYes});
    }

    public static int openConfirmBoxWithThreeButton(
            String msg, String btn1, String btn2, String btn3) {
        Shell sh = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        return openMsgBox(
                sh,
                MessageDialog.WARNING,
                Messages.titleConfirm,
                msg,
                new String[] {btn1, btn2, btn3});
    }

    /**
     * Open error box
     *
     * @param msg the detail message
     */
    public static void openErrorBox(String msg) {
        openErrorBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), msg);
    }

    /**
     * Open error box
     *
     * @param sh the shell object
     * @param msg the detail message
     */
    public static void openErrorBox(Shell sh, String msg) {
        openMsgBox(
                sh, MessageDialog.ERROR, Messages.titleError, msg, new String[] {Messages.btnOk});
    }

    /**
     * Open information box
     *
     * @param sh the shell object
     * @param title the title
     * @param msg the detail message
     */
    public static void openInformationBox(Shell sh, String title, String msg) {
        openMsgBox(sh, MessageDialog.INFORMATION, title, msg, new String[] {Messages.btnOk});
    }

    public static void openInformationBox(
            Shell sh, String title, String mainMessage, String secondaryMessage) {
        openMsgBox(
                sh,
                MessageDialog.INFORMATION,
                title,
                mainMessage,
                secondaryMessage,
                new String[] {Messages.btnOk});
    }

    /**
     * Open information box
     *
     * @param title the title
     * @param msg the detail message
     */
    public static void openInformationBox(String title, String msg) {
        openInformationBox(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, msg);
    }

    public static void openInformationBox(
            String title, String mainMessage, String secondaryMessage) {
        openInformationBox(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                title,
                mainMessage,
                secondaryMessage);
    }

    /**
     * Open information box
     *
     * @param msg the detail message
     */
    public static void openInformationBox(String msg) {
        openInformationBox(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                Messages.titleInformation,
                msg);
    }

    /**
     * Open Warning box
     *
     * @param msg the detail message
     */
    public static void openWarningBox(String msg) {
        openInformationBox(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                Messages.titleWarning,
                msg);
    }

    /**
     * Create the common table viewer that can be sorted by TableViewerSorter object,this viewer's
     * input object must be List<Map<String,Object>> and Map's key must be column index,Map's value
     * of the column must be String.
     *
     * @param parent the parent composite
     * @param sorter the table sorter
     * @param columnNameArr the column name array
     * @param columnwidthArr the column width array
     * @param gridData the gridData layout object
     * @return the TableViewer object
     */
    public static TableViewer createCommonTableViewer(
            Composite parent,
            ViewerSorter sorter,
            final String[] columnNameArr,
            int[] columnwidthArr,
            GridData gridData) {

        final TableViewer tableViewer =
                new TableViewer(
                        parent,
                        SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
        tableViewer.setContentProvider(new TableContentProvider());
        tableViewer.setLabelProvider(new TableLabelProvider());
        if (sorter != null) {
            tableViewer.setSorter(sorter);
        }

        tableViewer.getTable().setLinesVisible(true);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLayoutData(gridData);

        for (int i = 0; i < columnNameArr.length; i++) {
            final TableColumn tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
            tblColumn.setText(columnNameArr[i]);
            if (sorter != null) {
                tblColumn.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                TableColumn column = (TableColumn) event.widget;
                                int j = 0;
                                for (j = 0; j < columnNameArr.length; j++) {
                                    if (column.getText().equals(columnNameArr[j])) {
                                        break;
                                    }
                                }
                                TableViewerSorter sorter =
                                        ((TableViewerSorter) tableViewer.getSorter());
                                if (sorter == null) {
                                    return;
                                }
                                sorter.doSort(j);
                                tableViewer.getTable().setSortColumn(column);
                                tableViewer
                                        .getTable()
                                        .setSortDirection(sorter.isAsc() ? SWT.UP : SWT.DOWN);
                                tableViewer.refresh();
                                packTable(tableViewer);
                            }
                        });
            }
            if (columnwidthArr != null) {
                tblColumn.setWidth(columnwidthArr[i]);
            } else {
                tblColumn.pack();
            }
        }
        return tableViewer;
    }

    public static Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        if (text != null) {
            label.setText(text);
        }

        return label;
    }

    /**
     * Create the common table viewer that can be sorted by TableViewerSorter object,this viewer's
     * input object must be List<Map<String,Object>> and Map's key must be column index,Map's value
     * of the column must be String.
     *
     * @param parent the parent composite
     * @param sorter the table sorter
     * @param columnNameArr the column name array
     * @param gridData the gridData layout object
     * @return the TableViewer object
     */
    public static TableViewer createCommonTableViewer(
            Composite parent,
            ViewerSorter sorter,
            final String[] columnNameArr,
            GridData gridData) {
        return createCommonTableViewer(parent, sorter, columnNameArr, null, gridData);
    }

    /**
     * Create the common checkbox table viewer that can be sorted by TableViewerSorter object,this
     * viewer's input object must be List<Map<String,Object>> and Map's key must be column
     * index,Map's value of the column must be String.
     *
     * @param parent the parent composite
     * @param sorter the table sorter
     * @param columnNameArr the column name array
     * @param gridData the gridData layout object
     * @return the TableViewer object
     */
    public static TableViewer createCheckBoxTableViewer(
            Composite parent,
            ViewerSorter sorter,
            final String[] columnNameArr,
            GridData gridData) {
        final CheckboxTableViewer tableViewer =
                CheckboxTableViewer.newCheckList(
                        parent,
                        SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
        tableViewer.setContentProvider(new TableContentProvider());
        tableViewer.setLabelProvider(new TableLabelProvider());
        if (sorter != null) {
            tableViewer.setSorter(sorter);
        }

        tableViewer.getTable().setLinesVisible(true);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLayoutData(gridData);

        for (int i = 0; i < columnNameArr.length; i++) {
            final TableColumn tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
            tblColumn.setText(columnNameArr[i]);
            if (sorter != null) {
                tblColumn.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                TableColumn column = (TableColumn) event.widget;
                                int j = 0;
                                for (j = 0; j < columnNameArr.length; j++) {
                                    if (column.getText().equals(columnNameArr[j])) {
                                        break;
                                    }
                                }
                                TableViewerSorter sorter =
                                        ((TableViewerSorter) tableViewer.getSorter());
                                if (sorter == null) {
                                    return;
                                }
                                sorter.doSort(j);
                                tableViewer.getTable().setSortColumn(column);
                                tableViewer
                                        .getTable()
                                        .setSortDirection(sorter.isAsc() ? SWT.UP : SWT.DOWN);
                                tableViewer.refresh();
                                packTable(tableViewer);
                            }
                        });
            }
            tblColumn.pack();
        }
        return tableViewer;
    }

    /**
     * Pack table column width
     *
     * @param tv the tableViewer object
     */
    public static void packTable(TableViewer tv) {
        for (int i = 0; i < tv.getTable().getColumnCount(); i++) {
            tv.getTable().getColumn(i).pack();
        }
        for (int i = 0; i < tv.getTable().getColumnCount(); i++) {
            if (tv.getTable().getColumn(i).getWidth() > 200) {
                tv.getTable().getColumn(i).setWidth(200);
            }
        }
    }

    /**
     * Pack table column width
     *
     * @param table
     * @param minWidth
     * @param maxWidth
     */
    public static void packTable(Table table, int minWidth, int maxWidth) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumns()[i].pack();
            if (table.getColumns()[i].getWidth() > maxWidth) {
                table.getColumns()[i].setWidth(maxWidth);
            }
            if (table.getColumns()[i].getWidth() < minWidth) {
                table.getColumns()[i].setWidth(minWidth);
            }
        }
    }

    public static void packTableOnly(TableViewer tv) {
        for (int i = 0; i < tv.getTable().getColumnCount(); i++) {
            tv.getTable().getColumn(i).pack();
        }
    }

    public static void packTableOnly(Table table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumns()[i].pack();
        }
    }

    /**
     * Reload the children of this node and restore the expanded status
     *
     * @param viewer the TableViewer object
     * @param node the ICubridNode object
     */
    public static void refreshNavigatorTree(AbstractTreeViewer viewer, ICubridNode node) {
        if (node != null && node.getLoader() != null) {
            node.getLoader().setLoaded(false);
        }
        if (node != null && !viewer.getExpandedState(node)) {
            node.removeAllChild();
        }
        Object[] expandedElements = viewer.getExpandedElements();
        IContentProvider contentProvider = viewer.getContentProvider();
        if (contentProvider instanceof DeferredContentProvider) {
            CubridDeferredTreeContentManager manager =
                    ((DeferredContentProvider) contentProvider).getDeferredTreeContentManager();
            if (manager != null) {
                manager.setExpandedElements(expandedElements);
            }
        }
        if (node == null) {
            viewer.refresh(true);
        } else {
            viewer.refresh(node, true);
        }
    }

    /**
     * Reload the children of this node and restore the expanded status
     *
     * @param viewer the TableViewer object
     * @param node the ICubridNode object
     * @param expandedElements Object[]
     */
    public static void refreshNavigatorTree(
            TreeViewer viewer, ICubridNode node, Object[] expandedElements) {
        if (node != null && node.getLoader() != null) {
            node.getLoader().setLoaded(false);
        }
        if (node != null && !viewer.getExpandedState(node)) {
            node.removeAllChild();
        }
        IContentProvider contentProvider = viewer.getContentProvider();
        if (contentProvider instanceof DeferredContentProvider) {
            CubridDeferredTreeContentManager manager =
                    ((DeferredContentProvider) contentProvider).getDeferredTreeContentManager();
            if (manager != null) {
                manager.setExpandedElements(expandedElements);
            }
        }
        if (node == null) {
            viewer.refresh(true);
        } else {
            viewer.refresh(node, true);
        }
    }

    /**
     * Add new node to parent node in model and add this node to tree
     *
     * @param viewer The TreeViewer
     * @param parent The parent node
     * @param node The added node
     */
    public static void addNodeToTree(TreeViewer viewer, ICubridNode parent, ICubridNode node) {
        if (parent == null || node == null) {
            return;
        }
        parent.addChild(node);
        viewer.insert(parent, node, parent.position(node));
        viewer.setSelection(new StructuredSelection(node), true);
    }

    /**
     * Clear the expanded elements of treeviewer
     *
     * @param tv the TreeViewer object
     */
    public static void clearExpandedElements(TreeViewer tv) {
        IContentProvider contentProvider = tv.getContentProvider();
        if (contentProvider instanceof DeferredContentProvider) {
            CubridDeferredTreeContentManager manager =
                    ((DeferredContentProvider) contentProvider).getDeferredTreeContentManager();
            if (manager != null) {
                manager.setExpandedElements(null);
            }
        }
    }

    /**
     * format the string in fixed-length
     *
     * @param targetStr the target string
     * @param strLength the string length
     * @param isRight whether is right
     * @return the formated string
     */
    public static String formatString(String targetStr, int strLength, boolean isRight) {
        if (targetStr == null) {
            return null;
        }
        int curLength = targetStr.getBytes().length;
        if (targetStr != null && curLength > strLength) {
            return targetStr;
            //			targetStr = targetStr.substring(0, strLength);
        }
        StringBuffer newString = new StringBuffer();
        int cutLength = strLength - curLength;
        for (int i = 0; i < cutLength; i++) {
            newString.append(" ");
        }
        return isRight ? (targetStr + newString.toString()) : (newString.toString() + targetStr);
    }

    /**
     * This method encodes the url, removes the spaces from the url and replaces the same with
     * <code>"%20"</code>.
     *
     * @param input the input char array
     * @return the string
     */
    public static String urlEncodeForSpaces(char[] input) {
        StringBuffer retu = new StringBuffer(input.length);
        for (int i = 0; i < input.length; i++) {
            if (input[i] == ' ') {
                retu.append("%20");
            } else {
                retu.append(input[i]);
            }
        }
        return retu.toString();
    }

    /**
     * This method encodes the url, removes the spaces from the url and replaces the same with
     * <code>"%20"</code>.
     *
     * @param input the input string
     * @return the string
     */
    public static String urlEncodeForSpaces(String input) {
        return urlEncodeForSpaces(input.toCharArray());
    }

    /**
     * Register context menu copy action for styled text
     *
     * @param text StyledTextobject
     * @param isEditable whether the styleText object is editable
     */
    public static void registerContextMenu(final StyledText text, final boolean isEditable) {
        if (text == null || text.isDisposed()) {
            return;
        }
        Menu menu = new Menu(text.getShell(), SWT.POP_UP);
        final MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
        itemCopy.setText(Messages.bind(Messages.msgContextMenuCopy, "Ctrl+C"));
        itemCopy.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        copyContentToClipboard(text);
                    }
                });
        if (isEditable) {
            final MenuItem itemPaste = new MenuItem(menu, SWT.PUSH);
            itemPaste.setText(Messages.bind(Messages.msgContextMenuPaste, "Ctrl+V"));
            itemPaste.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            String content = getTextContentFromClipboard();
                            if (content != null) {
                                text.insert(content);
                            }
                        }
                    });
        }
        text.setMenu(menu);

        text.addKeyListener(
                new org.eclipse.swt.events.KeyAdapter() {
                    public void keyPressed(org.eclipse.swt.events.KeyEvent event) {
                        if ((event.stateMask & SWT.CTRL) != 0
                                && (event.stateMask & SWT.SHIFT) == 0
                                && event.keyCode == 'c') {
                            copyContentToClipboard(text);
                        } else if (isEditable
                                && (event.stateMask & SWT.CTRL) != 0
                                && (event.stateMask & SWT.SHIFT) == 0
                                && event.keyCode == 'v') {
                            String content = getTextContentFromClipboard();
                            if (content != null) {
                                text.insert(content);
                            }
                        }
                    }
                });
    }

    public static void registerCopyPasteContextMenu(
            final StyledText text, final boolean isEditable) {
        registerCopyPasteContextMenu(text, isEditable, true);
    }

    /**
     * Register context menu for styled text
     *
     * @param text StyledText
     * @param isEditable boolean
     */
    public static void registerCopyPasteContextMenu(
            final StyledText text, final boolean isEditable, final boolean isCopiable) {
        text.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent event) {
                        ActionManager.getInstance().changeFocusProvider(text);
                    }
                });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        if (isCopiable) {
                            IAction copyAction =
                                    ActionManager.getInstance().getAction(CopyAction.ID);
                            if (copyAction instanceof CopyAction) {
                                manager.add(copyAction);
                                if (!copyAction.isEnabled()) {
                                    FocusAction.changeActionStatus(copyAction, text);
                                }
                            }

                            IAction copyAllAction =
                                    ActionManager.getInstance().getAction(CopyAllAction.ID);
                            if (copyAllAction instanceof CopyAllAction) {
                                manager.add(copyAllAction);
                                if (!copyAllAction.isEnabled()) {
                                    FocusAction.changeActionStatus(copyAllAction, text);
                                }
                            }
                        }

                        if (!isEditable) {
                            return;
                        }

                        IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
                        if (pasteAction instanceof PasteAction) {
                            manager.add(pasteAction);
                            if (!pasteAction.isEnabled()) {
                                FocusAction.changeActionStatus(pasteAction, text);
                            }
                        }
                    }
                });
        Menu contextMenu = menuManager.createContextMenu(text);
        text.setMenu(contextMenu);

        text.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        IAction copyAction = ActionManager.getInstance().getAction(CopyAction.ID);
                        if (!copyAction.isEnabled()) {
                            FocusAction.changeActionStatus(copyAction, text);
                        }
                        IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
                        if (pasteAction != null && !pasteAction.isEnabled()) {
                            FocusAction.changeActionStatus(pasteAction, text);
                        }
                    }
                });
    }

    /**
     * Copy the data to clipboard
     *
     * @param data String
     */
    public static void copyContentToClipboard(String data) {
        if (data == null || data.trim().length() == 0) {
            return;
        }
        TextTransfer textTransfer = TextTransfer.getInstance();
        Clipboard clipboard = CommonUITool.getClipboard();
        if (clipboard != null) {
            clipboard.setContents(new Object[] {data}, new Transfer[] {textTransfer});
        }
    }

    /** Clear contents on the clipboard. */
    public static void clearClipboard() {
        Clipboard clipboard = CommonUITool.getClipboard();
        if (clipboard != null) {
            clipboard.clearContents();
        }
    }

    /**
     * Copy the styled text conent to clipboard
     *
     * @param text the StyledText object
     */
    public static void copyContentToClipboard(StyledText text) {
        TextTransfer textTransfer = TextTransfer.getInstance();
        Clipboard clipboard = CommonUITool.getClipboard();
        String data = text.getSelectionText();
        if (data == null || data.trim().length() == 0) {
            data = text.getText();
        }
        if (data != null && !data.equals("")) {
            clipboard.setContents(new Object[] {data}, new Transfer[] {textTransfer});
        }
    }

    /**
     * Get text content from clipboard
     *
     * @return the string from clipboard
     */
    public static String getTextContentFromClipboard() {
        Clipboard clipboard = CommonUITool.getClipboard();
        TextTransfer textTransfer = TextTransfer.getInstance();
        Object obj = clipboard.getContents(textTransfer);
        if (obj == null) {
            return "";
        } else {
            return (String) obj;
        }
    }

    /**
     * Format date for string according to data format ex:(1)yyyy-MM-dd hh:mm:ss.SSS (2)yyyy-MM-dd
     * hh:mm:ss
     *
     * @param date the Date object
     * @param sf the style
     * @return the formated string
     */
    public static String formatDate(Date date, String sf) {
        DateFormat dateformat = DateUtil.getDateFormat(sf);
        return dateformat.format(date);
    }

    /**
     * Because the height of table on LINUX platform cannot get the right result,this method deals
     * with this case.Note that the table only includes the head and one item.
     *
     * @param table the instance of Table
     * @return the heightHint
     */
    public static int getHeightHintOfTable(Table table) {
        int itemHeight = table.getItemHeight();
        if (itemHeight == 0) {
            itemHeight = 23;
        }
        int headerHeight = table.getHeaderHeight();
        if (headerHeight == 0) {
            headerHeight = 26;
        }
        int gridLineWidth = table.getGridLineWidth();
        if (gridLineWidth == 0) {
            gridLineWidth = 1;
        }
        return itemHeight + headerHeight + gridLineWidth;
    }

    /**
     * Check JRE version,only support JRE1.5 or later version
     *
     * @return <code>true</code> if JRE1.5 or later version and <code>false</code> otherwise
     */
    public static boolean jreVersionCheck() {
        String vmVersion = System.getProperty("java.version");
        if (vmVersion.startsWith("0")
                || vmVersion.startsWith("1.0")
                || vmVersion.startsWith("1.1")
                || vmVersion.startsWith("1.2")
                || vmVersion.startsWith("1.3")
                || vmVersion.startsWith("1.4")
                || vmVersion.startsWith("1.5")) {
            return false;
        }
        return true;
    }

    /**
     * Get version String
     *
     * @param buildId String
     * @param versionDateStr String
     * @return String
     */
    public static String getVersionStr(String buildId, String versionDateStr) {
        String versionStr = buildId;
        String[] versions = versionDateStr.split("\\.");
        if (versions.length == 4 && versions[3].length() == 12) {
            String str =
                    CommonUITool.convertTimeStr(versions[3], "yyyyMMddHHmm", "yyyy-MM-dd HH:mm");
            if (str != null) {
                versionStr = versionStr + " (" + str + ")";
            }
        }
        return versionStr;
    }

    /**
     * Convert the time string
     *
     * @param dateString String
     * @param datePattern String
     * @param changedPattern String
     * @return String
     */
    public static String convertTimeStr(
            String dateString, String datePattern, String changedPattern) {
        DateFormat formatter = new SimpleDateFormat(datePattern, Locale.getDefault());
        Date date;
        try {
            date = formatter.parse(dateString);
            formatter = new SimpleDateFormat(changedPattern, Locale.getDefault());
            return formatter.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Return date string
     *
     * @param dateString String
     * @return long
     */
    public static long getVersionDateMill(String dateString) {
        if (dateString.length() != 12) {
            return Long.parseLong(dateString);
        }
        try {
            DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            Date date = formatter.parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get splash shell
     *
     * @param display Display
     * @return Shell
     * @throws NumberFormatException The exception
     * @throws IllegalArgumentException The exception
     * @throws IllegalAccessException The exception
     * @throws InvocationTargetException The exception
     */
    public static Shell getSplashShell(Display display)
            throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
                    InvocationTargetException {

        final String dataSplashShell = "org.eclipse.ui.workbench.splashShell"; // $NON-NLS-1$
        final String propSplashHandle = "org.eclipse.equinox.launcher.splash.handle"; // $NON-NLS-1$

        Shell splashShell = (Shell) display.getData(dataSplashShell);
        if (splashShell != null) {
            return splashShell;
        }

        String splashHandle = System.getProperty(propSplashHandle);
        if (splashHandle == null) {
            return null;
        }

        // look for the 32 bit internal_new shell method
        try {
            Method method =
                    Shell.class.getMethod(
                            "internal_new", new Class[] {Display.class, int.class}); // $NON-NLS-1$
            // we're on a 32 bit platform so invoke it with splash
            // handle as an int
            splashShell =
                    (Shell) method.invoke(null, new Object[] {display, new Integer(splashHandle)});
        } catch (NoSuchMethodException e) {
            // look for the 64 bit internal_new shell method
            try {
                Method method =
                        Shell.class.getMethod(
                                "internal_new",
                                new Class[] {Display.class, long.class}); // $NON-NLS-1$

                // we're on a 64 bit platform so invoke it with a long
                splashShell =
                        (Shell) method.invoke(null, new Object[] {display, new Long(splashHandle)});
            } catch (NoSuchMethodException e2) {
                splashShell = null;
            }
        }

        display.setData(dataSplashShell, splashShell);
        return splashShell;
    }

    /**
     * Get available memory size
     *
     * @return long
     */
    public static long getAvailableMemorySize() {
        long maxMemorySize = 512 * 1024 * 1024;
        if (Runtime.getRuntime().maxMemory() > maxMemorySize) {
            maxMemorySize = Runtime.getRuntime().maxMemory();
        }
        long freeMemorySize = Runtime.getRuntime().freeMemory();
        long totalMemorySize = Runtime.getRuntime().totalMemory();
        long usedMemorySize = totalMemorySize - freeMemorySize;
        return maxMemorySize - usedMemorySize;
    }

    /**
     * Return whether have available memory
     *
     * @param remainingMemSize long
     * @return boolean
     */
    public static boolean isAvailableMemory(long remainingMemSize) {
        long availableMemSize = CommonUITool.getAvailableMemorySize();
        if (availableMemSize < remainingMemSize) {
            return false;
        }
        return true;
    }

    /**
     * Get eclipse runtime options -os <operating system> (OSGi) See
     * org.eclipse.osgi.service.environment.Constants for known values.
     *
     * @return String value of operating system
     */
    public static String getEclipseRuntimeOS() {
        String osTypeName = System.getProperty("osgi.os", "");
        // Eclipse OSGI takes both 32bit and 64bit of windows as 'win32', so it confuses users.
        // therefore, change to 'windows'.
        if (Constants.OS_WIN32.equals(osTypeName)) {
            osTypeName = "windows";
        }
        return osTypeName;
    }

    /**
     * Get eclipse runtime options -arch <processor architecture> (OSGi) See
     * org.eclipse.osgi.service.environment.Constants for known values.
     *
     * @return String value of processor architecture
     */
    public static String getEclipseRuntimeProcessorArch() {
        String processorArch = System.getProperty("osgi.arch", "");
        // for the sake of outstanding 32 or 64 bit x86 system, postfix the '_32bit' or '_64bit',
        if (Constants.ARCH_X86.equals(processorArch)) {
            processorArch = "32bit";
        } else if (Constants.ARCH_X86_64.equals(processorArch)) {
            processorArch = "64bit";
        }
        return processorArch;
    }

    /**
     * get normal SQL from pstmtSQL and the parameter bind log
     *
     * @param pstmtSQL
     * @param parameterLog
     * @return
     */
    public static String parseBrokerLogToSQL(String brokerLog) {
        //		if (brokerLog == null || "".equals(brokerLog)
        //				|| brokerLog.indexOf("?") < 0) {
        //			return "";
        //		}
        String questionMark = " ?.?.? "; // use to replace the original ?
        /* use to find the replaced ? so that can avoid the parameter has normal ?
         * for example if parameter has url https://play.google.com/store/apps/developer?id=tengriprod
         *  but when replace the ? at last step ,we don't know the ? in url is whether a valid ? to replace
         */
        String questionMarkFinder = "\\s\\?\\.\\?\\.\\?\\s";
        StringBuilder result = new StringBuilder();
        ;
        String pstmtSQLRegex = "(execute|execute_all)\\ssrv_h_id\\s\\d+\\s.+";
        Pattern pstmtSQLPattern = Pattern.compile(pstmtSQLRegex);
        Matcher pstmtSQLMatcher = pstmtSQLPattern.matcher(brokerLog);

        String pstmtSQLRegex2 = "(execute|execute_all)\\ssrv_h_id\\s\\d+\\s";
        Pattern pstmtSQLPattern2 = Pattern.compile(pstmtSQLRegex2);

        String paramRegex =
                "bind\\s\\d+\\s:\\s(INT\\s.+|DATETIME\\s.+|TIME\\s.+|DATE\\s.+|BIGINT\\s.+|DOUBLE\\s.+|FLOAT\\s.+|SHORT\\s.+|(VARCHAR\\s\\(\\d+\\).+)|VARCHAR\\s\\(\\d+\\)|NULL)";
        String valueRegex =
                "bind\\s\\d+\\s:\\s(INT\\s|DATETIME\\s|DATE\\s|TIME\\s|BIGINT\\s|DOUBLE\\s|FLOAT\\s|SHORT\\s|(VARCHAR\\s\\(\\d+\\)|.+)|NULL)";
        String typeRegex = "bind\\s\\d+\\s:\\s";
        Pattern paramPattern = Pattern.compile(paramRegex);
        Pattern valuePattern = Pattern.compile(valueRegex);
        Pattern typePattern = Pattern.compile(typeRegex);
        // find pstmtSql
        int index = 0;
        while (pstmtSQLMatcher.find()) {
            String onePstmtSQLString = pstmtSQLMatcher.group(0);
            Matcher pstmtSQLMatcher2 = pstmtSQLPattern2.matcher(onePstmtSQLString);
            if (pstmtSQLMatcher2.find()) {
                if (index != 0) {
                    result.append(System.getProperty("line.separator"));
                }
                String pstmtSQL = onePstmtSQLString.substring(pstmtSQLMatcher2.end());
                // replace more than one space to one space
                pstmtSQL = pstmtSQL.replaceAll("\\s{2,10}", " ");
                pstmtSQL = pstmtSQL.replaceAll("\\?", questionMark);
                result.append(pstmtSQL);
            }
            index++;
        }

        if (index == 0) {
            return "";
        }

        // find parameter type and value
        Matcher paramMatcher = paramPattern.matcher(brokerLog);
        List<HashMap<String, String>> paramTypeValueList = new ArrayList<HashMap<String, String>>();
        while (paramMatcher.find()) {
            // get bind 2 : VARCHAR (6)APP_A
            String paramString = paramMatcher.group(0);
            Matcher valueMatcher = valuePattern.matcher(paramString);
            Matcher typeMatcher = typePattern.matcher(paramString);
            String parameter = "";
            if (valueMatcher.find()) {
                // get APP_A
                parameter = paramString.substring(valueMatcher.end());
                String type = "";
                if (typeMatcher.find()) {
                    type = paramString.substring(typeMatcher.end(), valueMatcher.end());
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(parameter, type);
                paramTypeValueList.add(map);
            }
        }
        //		if (paramTypeValueList.size() == 0) {
        //			return "";
        //		}
        String pstmtSQL = result.toString();
        // replace ? to parameter by order
        for (Map<String, String> map : paramTypeValueList) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (isNumber(entry.getValue())) {
                    pstmtSQL = pstmtSQL.replaceFirst(questionMarkFinder, entry.getKey());
                } else if ("NULL".equals(entry.getValue())) {
                    pstmtSQL = pstmtSQL.replaceFirst(questionMarkFinder, "NULL");
                } else {
                    pstmtSQL =
                            pstmtSQL.replaceFirst(questionMarkFinder, "'" + entry.getKey() + "'");
                }
            }
        }

        pstmtSQL = pstmtSQL.replaceAll(questionMarkFinder, "?");
        return pstmtSQL;
    }

    /**
     * check whether a given type is a number type
     *
     * @param type
     * @return
     */
    public static boolean isNumber(String type) {
        if (type.indexOf("INT") > -1
                || type.indexOf("BIGINT") > -1
                || type.indexOf("DOUBLE") > -1
                || type.indexOf("FLOAT") > -1
                || type.indexOf("SHORT") > -1) {
            return true;
        }
        return false;
    }

    /**
     * getMostSevere
     *
     * @param status List<IStatus>
     * @return IStatus
     */
    public static IStatus getMostSevere(final java.util.List<IStatus> status) {
        IStatus max = null;

        for (IStatus curr : status) {
            if (curr == null) {
                continue;
            }

            if (curr.matches(IStatus.ERROR)) {
                max = curr;
                break;
            }

            if (max == null || curr.getSeverity() > max.getSeverity()) {
                max = curr;
            }
        }

        return max;
    }

    @SuppressWarnings("unused")
    public static void createSpaceRow(Composite composite) {
        Label label = new Label(composite, SWT.None);
    }

    /**
     * Open new query editor.
     *
     * @param database of query editor.
     * @param careDBRunType whether care the database running type that only whether donot open the
     *     query editor if the running type is not CS.
     * @throws PartInitException when open editor error.
     */
    public static QueryEditorPart openQueryEditor(CubridDatabase database, boolean careDBRunType)
            throws PartInitException {
        IWorkbenchPage page = LayoutUtil.getActivePage();
        if (page == null) {
            return null;
        }

        if (database != null && careDBRunType && database.getRunningType() != DbRunningType.CS) {
            return null;
        }

        QueryUnit input = new QueryUnit();
        input.setDatabase(database);
        IEditorPart editor = page.openEditor(input, QueryEditorPart.ID);
        if (editor != null) {
            ((QueryEditorPart) editor).connect(database);
        }
        return (QueryEditorPart) editor;
    }

    /**
     * Get active SQL editor composite
     *
     * @return SQLEditorComposite
     */
    public static SQLEditorComposite getActiveSQLEditorComposite() {
        QueryEditorPart queryEditor = getActiveQueryEditorPart();
        if (queryEditor != null) {
            return queryEditor.getCombinedQueryComposite().getSqlEditorComp();
        }
        return null;
    }

    /**
     * Get the active query editor
     *
     * @return
     */
    public static QueryEditorPart getActiveQueryEditorPart() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor instanceof QueryEditorPart) {
            return (QueryEditorPart) editor;
        }

        return null;
    }

    /**
     * update user or table label
     *
     * @param tv tree view
     * @param node
     */
    public static void updateFolderNodeLabelIncludingChildrenCount(
            AbstractTreeViewer tv, ICubridNode node) {
        if (!node.getType().equals(NodeType.TABLE_FOLDER)
                && !node.getType().equals(NodeType.USER_FOLDER)
                && !node.getType().equals(NodeType.VIEW_FOLDER)
                && !node.getType().equals(NodeType.SERIAL_FOLDER)
                && !node.getType().equals(NodeType.TRIGGER_FOLDER)
                && !node.getType().equals(NodeType.SYNONYM_FOLDER)) {
            return;
        }
        int count = 0;
        if (node.getType().equals(NodeType.TABLE_FOLDER)) {
            for (ICubridNode tableNode : node.getChildren()) {
                if (tableNode.getType().equals(NodeType.USER_TABLE)
                        || tableNode.getType().equals(NodeType.USER_PARTITIONED_TABLE_FOLDER)) {
                    count++;
                }
            }
        } else if (node.getType().equals(NodeType.VIEW_FOLDER)) {
            for (ICubridNode viewNode : node.getChildren()) {
                if (viewNode.getType().equals(NodeType.USER_VIEW)) {
                    count++;
                }
            }
        } else {
            count = node.getChildren().size();
        }
        String suffix = "(" + Integer.valueOf(count) + ")";
        String beforeLable = node.getLabel();
        if (beforeLable.endsWith(")") && beforeLable.indexOf("(") > -1) {
            beforeLable = beforeLable.substring(0, beforeLable.indexOf("("));
        }
        node.setLabel(beforeLable + suffix);
        CommonUITool.refreshNavigatorTree(tv, node);
    }

    /**
     * find node according type and name
     *
     * @param parent
     * @param typeSet
     * @param name
     * @return
     */
    public static ICubridNode findNode(ICubridNode parent, Set<String> typeSet, String name) {
        if (typeSet.contains(parent.getType()) && name.equals(parent.getName())) {
            return parent;
        }
        for (ICubridNode child : parent.getChildren()) {
            ICubridNode node = findNode(child, typeSet, name);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    /**
     * find node according type
     *
     * @param parent
     * @param typeSet
     * @return
     */
    public static ICubridNode findNode(ICubridNode parent, Set<String> typeSet) {
        if (typeSet.contains(parent.getType())) {
            return parent;
        }
        for (ICubridNode child : parent.getChildren()) {
            ICubridNode node = findNode(child, typeSet);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    public static String getWorkspacePath() {
        String workspacePath = null;

        try {
            URL url = Platform.getInstanceLocation().getURL();
            return new File(url.toURI()).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            workspacePath = null;
        }

        return workspacePath;
    }

    public static Image getImage(ImageDescriptor imageDescriptor) {
        if (imageDescriptor == null) {
            return null;
        }

        return imageDescriptor.createImage();
    }

    /**
     * Find the viewpart by id
     *
     * @param id
     * @return
     */
    public static IViewPart findView(String id) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null && window.getActivePage() != null) {
            return window.getActivePage().findView(id);
        }
        return null;
    }

    /**
     * Active the view by id
     *
     * @param id
     */
    public static void activeView(String id) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null && window.getActivePage() != null) {
            window.getActivePage().activate(findView(id));
        }
    }

    /**
     * Get AWT Image
     *
     * @param imageData
     * @return
     */
    public static BufferedImage getAWTImage(ImageData imageData) {
        return BufferedImageUtil.getAWTImage(imageData);
    }

    /**
     * Trim color value from a string like "[r=12,g=45,b=81]"
     *
     * @param paint a string represent for RGB value
     * @return the RGB value like "12,45,81"
     */
    public static String trimPaintColor(String paint) {
        String color = paint.substring(paint.indexOf("[") + 1, paint.indexOf("]"));
        color = color.replaceAll("r=|g=|b=", "");
        return color;
    }

    /**
     * Get the red,green,blue value from the string colorString, the format of colorString is like
     * "1,2,3"
     *
     * @param colorString the string represent for color
     * @param element 0-red,1-green,2-blue,other value will get the wrong value
     * @return the element value
     */
    public static int getColorElem(String colorString, int element) {
        String[] ele = colorString.split(",");
        int result = 0;
        switch (element) {
            case 0:
                result = Integer.parseInt(ele[0]);
                break;
            case 1:
                result = Integer.parseInt(ele[1]);
                break;
            case 2:
                result = Integer.parseInt(ele[2]);
                break;
            default:
        }
        return result;
    }

    public static void showToolTip(
            Control columnsTable, ToolTip toolTip, Rectangle rect, String title, String message) {
        if (toolTip == null || toolTip.isDisposed()) {
            return;
        }

        if (title == null && message == null) {
            return;
        }

        toolTip.setVisible(false);
        toolTip.setText(StringUtil.nvl(title));
        toolTip.setMessage(StringUtil.nvl(message));

        Point pt = new Point(rect.x + rect.width, rect.y + rect.height);
        pt = columnsTable.toDisplay(pt);
        toolTip.setLocation(pt);
        toolTip.setVisible(true);
    }

    public static void hideToolTip(ToolTip toolTip) {
        if (toolTip == null || toolTip.isDisposed()) {
            return;
        }

        toolTip.setVisible(false);
    }

    public static void showErrorBaloon(
            Control parent, Control control, ToolTip errorBaloon, String title, String message) {
        if (errorBaloon == null || errorBaloon.isDisposed()) {
            return;
        }

        if (title == null && message == null) {
            return;
        }

        errorBaloon.setVisible(false);
        errorBaloon.setText(StringUtil.nvl(title));
        errorBaloon.setMessage(StringUtil.nvl(message));

        Rectangle rect = control.getBounds();
        Point pt = new Point(rect.x, rect.y + rect.height);
        pt = parent.toDisplay(pt);
        errorBaloon.setLocation(pt);
        errorBaloon.setVisible(true);
    }

    public static void hideErrorBaloon(ToolTip errorBaloon) {
        if (errorBaloon == null || errorBaloon.isDisposed()) {
            return;
        }

        errorBaloon.setVisible(false);
    }

    public static TableItem findSelectItem(Table table) {
        int selectIndex = table.getSelectionIndex();
        if (selectIndex < 0) {
            return null;
        }

        final TableItem item = table.getItem(selectIndex);
        if (item == null) {
            return null;
        }

        return item;
    }

    /**
     * Because the table has some problems of SWT library on Yosemite, all the table need add this
     * patch. Fix for #64 Mac OSX yosemite didn't show all records while running a query on CUBRID
     * manager.
     *
     * @param table
     */
    public static void hackForYosemite(final Table table) {
        if (!isYosemite() || table == null || table.isDisposed()) {
            return;
        }
        if (table.getHeaderVisible()) {
            table.setHeaderVisible(true);
            Display.getDefault()
                    .asyncExec(
                            new Runnable() {
                                public void run() {
                                    if (!table.isDisposed()) {
                                        try {
                                            Method getViewMethod =
                                                    Control.class.getDeclaredMethod("contentView");
                                            getViewMethod.setAccessible(true);
                                            Object viewObject = getViewMethod.invoke(table);
                                            Method tileMethod =
                                                    viewObject.getClass().getMethod("tile");
                                            tileMethod.invoke(viewObject);
                                        } catch (NoSuchMethodException ex) {
                                            LOGGER.error(ex.getMessage());
                                        } catch (SecurityException ex) {
                                            LOGGER.error(ex.getMessage());
                                        } catch (IllegalAccessException ex) {
                                            LOGGER.error(ex.getMessage());
                                        } catch (IllegalArgumentException ex) {
                                            LOGGER.error(ex.getMessage());
                                        } catch (InvocationTargetException ex) {
                                            LOGGER.error(ex.getMessage());
                                        }
                                    }
                                }
                            });

            table.addPaintListener(
                    new PaintListener() {
                        boolean isAdjusted = false;

                        public void paintControl(PaintEvent e) {
                            if (!table.isDisposed() && !isAdjusted) {
                                Rectangle bounds = table.getBounds();
                                table.setSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                                table.setBounds(bounds);
                                isAdjusted = true;
                            }
                        }
                    });
        }
    }

    /**
     * Judge the OS whether Yosemite
     *
     * @return
     */
    private static boolean isYosemite() {
        if (!Util.isMac()) {
            return false;
        }
        String version = System.getProperty("os.version");
        if (StringUtils.isNotEmpty(version)) {
            String[] splitVersion = version.split("\\.");
            if (splitVersion.length == 2
                    && "10".endsWith(splitVersion[0].trim())
                    && "10".endsWith(splitVersion[1].trim())) {
                return true;
            }
        }
        return false;
    }
}
