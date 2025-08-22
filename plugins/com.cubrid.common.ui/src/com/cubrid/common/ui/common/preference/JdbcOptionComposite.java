/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.preference;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.novocode.naf.swt.custom.Hyperlink;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * A composite to show JDBC advanced options page
 *
 * @author Isaiah Choe 2012-05-21
 */
public class JdbcOptionComposite extends Composite {
    private final List<Map<String, String>> jdbcListData = new ArrayList<Map<String, String>>();
    private TableViewer jdbcInfoTv;
    private String jdbcAttrs = null;
    private TableEditor editor = null;
    private Button addButton = null;

    private Text txtConnectTimeout = null;
    private Text txtQueryTimeout = null;
    private Combo cbZeroDateTimeBehavior = null;

    private Map<String, String> attributes = new HashMap<String, String>();

    public JdbcOptionComposite(Composite parent, String jdbcAttrs) {
        super(parent, SWT.NONE);
        this.jdbcAttrs = jdbcAttrs;
        createContent();
    }

    /** load JDBC option from preference store */
    public void loadPreference() {
        Properties props = JDBCConnectionManager.parseJdbcOptions(jdbcAttrs, false);
        for (Enumeration<Object> e = props.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String val = (String) props.get(key);

            if ("connectTimeout".equals(key)
                    || "queryTimeout".equals(key)
                    || "zeroDateTimeBehavior".equals(key)) {
                attributes.put(key, val);
                continue;
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("0", key);
            map.put("1", val);
            jdbcListData.add(map);
        }
        jdbcInfoTv.refresh();
        CommonUITool.packTable(jdbcInfoTv);

        if (attributes.get("connectTimeout") != null) {
            txtConnectTimeout.setText(attributes.get("connectTimeout"));
        }

        if (attributes.get("queryTimeout") != null) {
            txtQueryTimeout.setText(attributes.get("queryTimeout"));
        }
        if (attributes.get("zeroDateTimeBehavior") != null) {
            String val = attributes.get("zeroDateTimeBehavior");
            if ("exception".equals(val)) {
                cbZeroDateTimeBehavior.select(0);
            } else if ("round".equals(val)) {
                cbZeroDateTimeBehavior.select(1);
            } else if ("convertToNull".equals(val)) {
                cbZeroDateTimeBehavior.select(2);
            }
        }
    }

    /** save options */
    public void save() {
        attributes.remove("connectTimeout");
        int val = StringUtil.intValue(txtConnectTimeout.getText(), -1);
        if (val > 0) {
            attributes.put("connectTimeout", "" + val);
        }

        attributes.remove("queryTimeout");
        val = StringUtil.intValue(txtQueryTimeout.getText(), -1);
        if (val > 0) {
            attributes.put("queryTimeout", "" + val);
        }

        attributes.remove("zeroDateTimeBehavior");
        val = cbZeroDateTimeBehavior.getSelectionIndex();
        if (val >= 0 && val < 3) {
            String str = null;
            if (val == 0) {
                str = "exception";
            } else if (val == 1) {
                str = "round";
            } else if (val == 2) {
                str = "convertToNull";
            }

            if (str != null) {
                attributes.put("zeroDateTimeBehavior", str);
            }
        }

        StringBuilder sb = new StringBuilder();
        TableItem[] items = jdbcInfoTv.getTable().getItems();
        for (TableItem item : items) {
            String key = item.getText(0).trim();
            if ("connectTimeout".equals(key)
                    || "queryTimeout".equals(key)
                    || "zeroDateTimeBehavior".equals(key)
                    || key.length() == 0) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(item.getText(0).trim());
            sb.append("=");
            sb.append(item.getText(1).trim());
        }

        Set<String> keys = attributes.keySet();
        for (String key : keys) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key);
            sb.append("=");
            sb.append(attributes.get(key));
        }

        jdbcAttrs = sb.toString();
    }

    public boolean hasDuplicatedKey() {
        Set<String> set = new HashSet<String>();
        TableItem[] items = jdbcInfoTv.getTable().getItems();
        for (TableItem item : items) {
            String key = item.getText(0).trim();
            if (set.contains(key)) {
                return true;
            }
            set.add(key);
        }
        return false;
    }

    public String getJdbcAttrs() {
        return jdbcAttrs;
    }

    /**
     * Create JDBC table group
     *
     * @param composite the composite
     */
    private void createJdbcTableGroup(Composite composite) {
        final String[] columnNameArr =
                new String[] {Messages.tblColJdbcAttrName, Messages.tblColJdbcAttrValue};
        jdbcInfoTv =
                CommonUITool.createCommonTableViewer(
                        composite,
                        null,
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 150));
        jdbcInfoTv.setInput(jdbcListData);

        TableLayout tableLayout = new TableLayout();
        jdbcInfoTv.getTable().setLayout(tableLayout);
        tableLayout.addColumnData(new ColumnWeightData(35, true));
        tableLayout.addColumnData(new ColumnWeightData(65, true));

        editor = new TableEditor(jdbcInfoTv.getTable());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        jdbcInfoTv
                .getTable()
                .addListener(
                        SWT.MouseUp,
                        new Listener() {
                            public void handleEvent(Event event) {
                                if (event.button != 1) {
                                    return;
                                }

                                Point pt = new Point(event.x, event.y);

                                int newIndex = jdbcInfoTv.getTable().getSelectionIndex();

                                if (jdbcInfoTv.getTable().getItemCount() <= newIndex
                                        || newIndex < 0) {
                                    return;
                                }

                                final TableItem item = jdbcInfoTv.getTable().getItem(newIndex);
                                if (item == null) {
                                    return;
                                }

                                for (int i = 0; i < 2; i++) {
                                    Rectangle rect = item.getBounds(i);
                                    if (rect.contains(pt)) {
                                        focusCell(item, newIndex, i);
                                        break;
                                    }
                                }
                            }
                        });
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
            layout.numColumns = 3;
            buttonComposite.setLayout(layout);
            buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

        Hyperlink link = new Hyperlink(buttonComposite, SWT.None);
        link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        link.setText(Messages.titleJdbcAdvancedOptionView);
        link.addMouseListener(
                new MouseListener() {
                    public void mouseUp(MouseEvent e) {
                        IWorkbenchBrowserSupport browserSupport =
                                PlatformUI.getWorkbench().getBrowserSupport();
                        try {
                            IWebBrowser br = browserSupport.createBrowser(null);
                            br.openURL(new URL(Messages.msgCubridJdbcInfoUrl));
                        } catch (Exception ignored) {
                        }
                    }

                    public void mouseDown(MouseEvent e) {}

                    public void mouseDoubleClick(MouseEvent e) {}
                });

        addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        addButton.setText(Messages.btnAdd);
        addButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableItem itemNew = new TableItem(jdbcInfoTv.getTable(), SWT.SINGLE);
                        int newIndex = jdbcInfoTv.getTable().getItemCount() - 1;
                        focusCell(itemNew, newIndex, 0);
                    }
                });

        Button delButton = new Button(buttonComposite, SWT.PUSH);
        delButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        delButton.setText(Messages.btnDelete);
        delButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableItem[] selection = jdbcInfoTv.getTable().getSelection();
                        if (selection == null || selection.length == 0) {
                            return;
                        }

                        jdbcInfoTv.getTable().setSelection(-1);

                        for (TableItem item : selection) {
                            item.dispose();
                        }
                    }
                });
    }

    /** Create page content */
    private void createContent() {
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createQuickGroup(this);
        createJdbcTableGroup(this);
        createButtonComp(this);
    }

    private void createQuickGroup(Composite composite) {
        Group group = new Group(composite, SWT.None);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setText(Messages.lblJdbcAdvancedBasic);

        {
            Label label = new Label(group, SWT.None);
            label.setText(Messages.lblConnectionTimeout);

            Text text = new Text(group, SWT.LEFT | SWT.BORDER);
            text.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
            text.setText("0");
            txtConnectTimeout = text;
        }

        {
            Label label = new Label(group, SWT.None);
            label.setText(Messages.lblQueryTimeout);

            Text text = new Text(group, SWT.LEFT | SWT.BORDER);
            text.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
            text.setText("0");
            txtQueryTimeout = text;
        }

        {
            Label label = new Label(group, SWT.None);
            label.setText(Messages.lblZeroDateTimeBehavior);

            Combo combo = new Combo(group, SWT.READ_ONLY);
            combo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
            combo.setItems(
                    new String[] {
                        Messages.msgZeroDateTimeBehavior1,
                        Messages.msgZeroDateTimeBehavior2,
                        Messages.msgZeroDateTimeBehavior3,
                        ""
                    });
            cbZeroDateTimeBehavior = combo;
        }
    }

    public void focusCell(final TableItem item, final int row, final int col) {
        final StyledText text = new StyledText(jdbcInfoTv.getTable(), SWT.SINGLE);
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
     * @author Isaiah Choe
     * @version 1.0 - 2012-5-21 created by Isaiah Choe
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
                    String key = text.getText().toLowerCase(Locale.getDefault());
                    if ("charset".equalsIgnoreCase(key)) {
                        CommonUITool.openErrorBox(Messages.jdbcOptionsErrMsg);
                    } else {
                        item.setText(column, text.getText());
                    }
                } else if (text.getText().length() == 0) {
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
}
