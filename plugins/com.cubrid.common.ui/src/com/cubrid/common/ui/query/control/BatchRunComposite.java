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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

/**
 * A composite to show the run batch sql page
 *
 * @author Isaiah Choe 2012-05-18
 */
public class BatchRunComposite extends Composite {
    public static final String ID = BatchRunComposite.class.getName();

    private static final String LIST_ID = "BatchRunList";

    private final List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
    private CheckboxTableViewer tv;
    private TableEditor editor = null;
    private Button addButton = null;
    private Button delButton = null;
    private Button runButton = null;
    private Button pasteButton = null;
    private String lastDirectory = null;
    private CubridDatabase cubridDatabase = null;

    public BatchRunComposite(Composite parent) {
        super(parent, SWT.NONE);

        loadBatchRunList();
        createContent();
    }

    public void setCubridDatabase(CubridDatabase cubridDatabase) {
        this.cubridDatabase = cubridDatabase;
    }

    public List<String> getFileList() {
        List<String> fileList = new ArrayList<String>();
        for (int i = 0; i < tv.getTable().getItemCount(); i++) {
            if (tv.getTable().getItem(i).getChecked()) {
                Map<String, String> map = listData.get(i);
                String filepath = map.get("4") + File.separator + map.get("1");
                fileList.add(filepath);
            }
        }

        return fileList;
    }

    public void setRunButton(Button runButton) {
        this.runButton = runButton;
    }

    public void setPasteButton(Button pasteButton) {
        this.pasteButton = pasteButton;
    }

    private void createTableGroup(Composite composite) {
        final String[] columnNames =
                new String[] {
                    "",
                    com.cubrid.common.ui.query.Messages.msgBatchRunSqlFile,
                    com.cubrid.common.ui.query.Messages.msgBatchRunMemo,
                    com.cubrid.common.ui.query.Messages.msgBatchRunRegdate
                };
        tv =
                (CheckboxTableViewer)
                        CommonUITool.createCheckBoxTableViewer(
                                composite,
                                null,
                                columnNames,
                                CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
        tv.setInput(listData);

        TableLayout tableLayout = new TableLayout();
        tv.getTable().setLayout(tableLayout);
        tableLayout.addColumnData(new ColumnPixelData(30));
        tableLayout.addColumnData(new ColumnPixelData(209));
        tableLayout.addColumnData(new ColumnPixelData(272));
        tableLayout.addColumnData(new ColumnPixelData(118));

        editor = new TableEditor(tv.getTable());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        tv.getTable()
                .addListener(
                        SWT.MouseUp,
                        new Listener() {
                            public void handleEvent(Event event) {
                                if (event.button != 1) {
                                    return;
                                }

                                validateCheck();

                                Point pt = new Point(event.x, event.y);
                                int newIndex = tv.getTable().getSelectionIndex();
                                if (tv.getTable().getItemCount() <= newIndex || newIndex < 0) {
                                    return;
                                }

                                final TableItem item = tv.getTable().getItem(newIndex);
                                if (item == null) {
                                    return;
                                }

                                Rectangle rect = item.getBounds(2);
                                if (rect.contains(pt)) {
                                    focusCell(item, newIndex, 2);
                                }
                            }
                        });
    }

    private void validateCheck() {
        int checkedCount = 0;
        for (int i = 0; i < tv.getTable().getItemCount(); i++) {
            if (tv.getTable().getItem(i).getChecked()) {
                checkedCount++;
            }
        }
        if (delButton != null) {
            delButton.setEnabled(checkedCount > 0);
        }
        if (runButton != null) {
            runButton.setEnabled(checkedCount > 0);
        }
        if (pasteButton != null) {
            pasteButton.setEnabled(checkedCount > 0);
        }
    }

    /**
     * Create button composite
     *
     * @param composite the composite
     */
    private void createButtonComp(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            buttonComposite.setLayout(layout);
            buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        }

        addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addButton.setText(Messages.btnAdd);
        addButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FileDialog dialog =
                                new FileDialog(
                                        PlatformUI.getWorkbench()
                                                .getActiveWorkbenchWindow()
                                                .getShell(),
                                        SWT.OPEN | SWT.MULTI);
                        // FIXME file extensions
                        dialog.setFilterExtensions(new String[] {"*.sql", "*.txt"});
                        dialog.setFilterNames(
                                new String[] {"SQL file (*.sql)", "SQL file (*.txt)"});
                        if (lastDirectory != null) {
                            dialog.setFilterPath(lastDirectory);
                        }
                        dialog.open();

                        String database =
                                cubridDatabase.getDatabaseInfo().getDbName()
                                        + "@"
                                        + cubridDatabase
                                                .getServer()
                                                .getServerInfo()
                                                .getHostAddress();

                        lastDirectory = dialog.getFilterPath();

                        TableItem itemFirst = null;
                        String[] filenames = dialog.getFileNames();
                        for (int i = 0; i < filenames.length; i++) {
                            TableItem itemNew = new TableItem(tv.getTable(), SWT.SINGLE);
                            itemNew.setText(1, filenames[i]);

                            Map<String, String> data = new HashMap<String, String>();
                            data.put("1", filenames[i]);

                            data.put("2", database);
                            itemNew.setText(2, database);

                            // FIXME dateformat
                            String datetime =
                                    DateUtil.getDatetimeString(
                                            new Date().getTime(), "yyyy-MM-dd HH:mm");
                            itemNew.setText(3, datetime);
                            data.put("3", datetime);

                            data.put("4", lastDirectory);

                            if (i == 0) {
                                itemFirst = itemNew;
                            }

                            listData.add(data);
                        }

                        if (itemFirst != null) {
                            int newIndex = tv.getTable().getItemCount() - 1;
                            focusCell(itemFirst, newIndex, 2);
                        }

                        saveBatchRunList();
                    }
                });

        delButton = new Button(buttonComposite, SWT.PUSH);
        delButton.setEnabled(false);
        delButton.setText(Messages.btnDelete);
        delButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!CommonUITool.openConfirmBox(
                                com.cubrid.common.ui.query.Messages.errBatchRunDel)) {
                            return;
                        }

                        int updateCount = 0;
                        for (int i = tv.getTable().getItemCount() - 1; i >= 0; i--) {
                            if (tv.getTable().getItem(i).getChecked()) {
                                tv.getTable().getItem(i).dispose();
                                listData.remove(i);
                                updateCount++;
                            }
                        }

                        if (updateCount > 0) {
                            saveBatchRunList();
                        }

                        validateCheck();
                    }
                });
    }

    /** Create page content */
    private void createContent() {
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createTableGroup(this);
        createButtonComp(this);
    }

    public void focusCell(final TableItem item, final int row, final int col) {
        final StyledText text = new StyledText(tv.getTable(), SWT.SINGLE);
        Listener textListener = new TableItemEditor(text, item, row, col);
        text.addListener(SWT.FocusOut, textListener);
        text.addListener(SWT.Traverse, textListener);
        text.addListener(SWT.FocusIn, textListener);
        text.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent e) {
                        if (e.detail == SWT.TRAVERSE_TAB_NEXT
                                || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                            e.doit = false;
                            int newColumn = col == 0 ? 1 : 0;
                            focusCell(item, row, newColumn);
                        } else if (e.detail == SWT.TRAVERSE_RETURN) {
                            e.doit = false;
                            addButton.setFocus();
                        }
                    }
                });
        text.setEditable(true);
        editor.setEditor(text, item, col);
        text.setText(item.getText(col));
        text.selectAll();
        try {
            text.setFocus();
        } catch (Exception e) {
        }
    }

    /**
     * Table item editor
     *
     * @author pangqiren
     * @version 1.0 - 2009-12-18 created by pangqiren
     */
    private class TableItemEditor implements Listener {
        private boolean isRunning = false;
        private final TableItem item;
        private final int row;
        private final int column;
        private final StyledText text;
        private Shell shell;

        public TableItemEditor(StyledText text, TableItem item, int row, int column) {
            this.text = text;
            this.item = item;
            this.row = row;
            this.column = column;
            shell =
                    new Shell(
                            Display.getDefault().getActiveShell(),
                            SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            shell.setText("");
            shell.setLayout(new GridLayout());
            shell.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        /**
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         * @param event the event which occurred
         */
        public void handleEvent(final Event event) {
            if (event.type == SWT.FocusOut) {
                if (isRunning) {
                    return;
                }
                isRunning = true;

                boolean isChanged = !text.getText().equals(item.getText(column));
                if (isChanged) {
                    item.setText(column, text.getText());

                    // save list
                    Map<String, String> data = listData.get(row);
                    data.put("2", text.getText());
                    saveBatchRunList();
                } else if ("".equals(text.getText())) {
                    item.setText(column, "");
                }
                text.dispose();
                isRunning = false;
            } else if (event.type == SWT.Traverse && event.detail == SWT.TRAVERSE_ESCAPE) {
                if (isRunning) {
                    return;
                }
                isRunning = true;
                text.dispose();
                event.doit = false;
                isRunning = false;
            } else if (event.type == SWT.FocusIn) {
            }
        }
    }

    public void loadBatchRunList() {
        // FIXME make a generic way to access configuration repository
        IXMLMemento memento = PersistUtils.getXMLMemento(BatchRunComposite.ID, LIST_ID);
        if (memento == null) {
            return;
        }

        try {
            listData.clear();

            for (IXMLMemento xmlMemento : memento.getChildren("BatchRun")) {
                Map<String, String> data = new HashMap<String, String>();
                data.put("0", "");
                data.put("1", xmlMemento.getString("filename"));
                data.put("2", xmlMemento.getString("memo"));
                data.put("3", xmlMemento.getString("regdate"));
                data.put("4", xmlMemento.getString("directory"));
                listData.add(data);
            }
        } catch (Exception e) {
        }
    }

    public void saveBatchRunList() {
        XMLMemento memento = XMLMemento.createWriteRoot("BatchRunList");
        for (Iterator<Map<String, String>> it = listData.iterator(); it.hasNext(); ) {
            IXMLMemento batchRunXMLMemento = memento.createChild("BatchRun");
            Map<String, String> data = it.next();
            batchRunXMLMemento.putString("filename", data.get("1"));
            batchRunXMLMemento.putString("memo", data.get("2"));
            batchRunXMLMemento.putString("regdate", data.get("3"));
            batchRunXMLMemento.putString("directory", data.get("4"));
        }
        PersistUtils.saveXMLMemento(BatchRunComposite.ID, LIST_ID, memento);
    }
}
