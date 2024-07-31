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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;

/**
 * OID navigator will use this dialog to navigator data
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class OIDNavigatorDialog extends CMTitleAreaDialog implements ModifyListener {
    private static final Logger LOGGER = LogUtil.getLogger(OIDNavigatorDialog.class);

    private Text oidValueText = null;
    private Button findButton;
    private Tree resultTree;
    private final Connection conn;
    private final String oidStr;
    private static final String DUMY_ITEM_FLAG = "dumyItemFlag";
    private static final String OID_ITEM_FLAG = "oidItemFlag";
    private static final String EXPANDED_ITEM_FLAG = "expandedItemFlag";

    private Statement stmt; // NOPMD

    /**
     * The constructor
     *
     * @param parentShell
     */
    public OIDNavigatorDialog(Shell parentShell, Connection conn, String oidStr) {
        super(parentShell);
        this.conn = conn;
        this.oidStr = oidStr;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 5;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label oidValueLabel = new Label(composite, SWT.LEFT);
        oidValueLabel.setText(Messages.lblOIDValue);
        oidValueLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        oidValueText = new Text(composite, SWT.LEFT | SWT.BORDER);
        oidValueText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        if (oidStr != null) {
            oidValueText.setText(oidStr);
        }
        oidValueText.addModifyListener(this);

        findButton = new Button(composite, SWT.CENTER);
        findButton.setText(Messages.btnFind);
        findButton.setLayoutData(CommonUITool.createGridData(1, 1, 60, -1));
        if (oidStr == null || oidStr.trim().length() <= 0) {
            findButton.setEnabled(false);
        }
        findButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        find();
                    }
                });

        Composite findResultComp = new Composite(composite, SWT.BORDER);
        findResultComp.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 5, 1, -1, 200));
        findResultComp.setLayout(new GridLayout());
        resultTree = new Tree(findResultComp, SWT.NONE | SWT.VIRTUAL);
        GridData data = new GridData(GridData.FILL_BOTH);
        resultTree.setLayoutData(data);
        resultTree.addTreeListener(
                new TreeListener() {
                    public void treeCollapsed(TreeEvent event) {
                        // empty
                    }

                    public void treeExpanded(TreeEvent event) {
                        TreeItem item = (TreeItem) event.item;
                        if (item.getData(OID_ITEM_FLAG) == null
                                || (item.getData(EXPANDED_ITEM_FLAG) != null)) {
                            return;
                        }

                        if (item.getItemCount() > 0) {
                            Object obj = item.getItem(0).getData(DUMY_ITEM_FLAG);
                            if (obj instanceof String
                                    && OIDNavigatorDialog.DUMY_ITEM_FLAG.equals((String) obj)) {
                                item.removeAll();
                            }
                        }
                        searchOID((String) (item.getData(OID_ITEM_FLAG)), item);
                        item.setData(EXPANDED_ITEM_FLAG, "true");
                    }
                });
        setTitle(Messages.titleOIDNavigatorDialog);
        setMessage(Messages.msgOIDNavigatorDialog);
        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleOIDNavigatorDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
    }

    /**
     * When modify the page content and check the validation
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        String oidValue = oidValueText.getText();
        if (oidValue.length() > 0) {
            findButton.setEnabled(true);
            setErrorMessage(null);
        } else {
            findButton.setEnabled(false);
            setErrorMessage(Messages.errOIDValue1);
        }
    }

    /**
     * Execute to find result
     *
     * @return <code>true</code> if it is found;<code>false</code>otherwise
     */
    public boolean find() {
        boolean isOk = true;
        resultTree.removeAll();
        TreeItem root = new TreeItem(resultTree, SWT.NONE);
        isOk = searchOID(oidValueText.getText(), root);
        if (!root.isDisposed()) {
            root.setExpanded(true);
        }
        if (!resultTree.isDisposed()) {
            resultTree.layout(true);
        }
        return isOk;
    }

    /**
     * Get tree constructor by the result of execute oid
     *
     * @param strOid the oid string
     * @param parent the parent treeitem
     * @return <code>true</code> if found;<code>false</code> otherwise
     */
    private boolean searchOID(String strOid, TreeItem parent) {
        CUBRIDOIDProxy oid = null;
        String tblName = null;
        String[] columnName;
        String[] typeName;
        String[] value;
        String[] oidSet = null;
        boolean[] isOid = null;
        int cntColumn = 0;

        try {
            oid = CUBRIDOIDProxy.getNewInstance((CUBRIDConnectionProxy) conn, strOid);
            if (oid == null) {
                return false;
            }
            tblName = oid.getTableName();
        } catch (Exception e) {
            CommonUITool.openErrorBox(getShell(), Messages.errOIDValue2);
            return false;
        }

        if (tblName == null) {
            CommonUITool.openErrorBox(getShell(), Messages.errOIDValue2);
            return false;
        }

        parent.setText(strOid);
        TreeItem item = new TreeItem(parent, SWT.NONE);
        item.setText("table name: " + tblName);

        String sql = "SELECT * FROM " + QuerySyntax.escapeKeyword(tblName) + " WHERE ROWNUM = 1";
        stmt = null;
        CUBRIDResultSetProxy rs = null;
        try {
            stmt = conn.createStatement();

            rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql);
            ResultSetMetaData rsmt = rs.getMetaData();

            cntColumn = rsmt.getColumnCount();
            columnName = new String[cntColumn];
            typeName = new String[cntColumn];
            value = new String[cntColumn];

            for (int i = 0; i < cntColumn; i++) {
                columnName[i] = rsmt.getColumnName(i + 1);
                typeName[i] = rsmt.getColumnTypeName(i + 1);
            }
            rs.close();

            rs = (CUBRIDResultSetProxy) oid.getValues(columnName);
            while (rs.next()) {
                for (int i = 0; i < columnName.length; i++) {
                    if (rs.getObject(columnName[i]) == null) {
                        value[i] = "NULL";
                    } else {
                        if ("SET".equals(typeName[i])
                                || "MULTISET".equals(typeName[i])
                                || "SEQUENCE".equals(typeName[i])) {
                            Object[] set = (Object[]) rs.getCollection(columnName[i]);
                            oidSet = new String[set.length];
                            isOid = new boolean[set.length];
                            value[i] = "{";
                            if (set.length > 0) {
                                for (int j = 0; j < set.length; j++) {
                                    if (set[j].getClass() == oid.getCUBRIDOIDClass()) {
                                        value[i] += (new CUBRIDOIDProxy(set[j])).getOidString();
                                        oidSet[j] = (new CUBRIDOIDProxy(set[j])).getOidString();
                                        isOid[j] = true;
                                    } else {
                                        value[i] += set[j];
                                        oidSet[j] = null;
                                        isOid[j] = false;
                                    }

                                    if (i < set.length - 1) {
                                        value[i] += ", ";
                                    }
                                }
                            }
                            value[i] += "}";
                        } else {
                            value[i] = rs.getString(columnName[i]);
                        }
                    }
                }
            }
            rs.close();
            for (int i = 0; i < value.length; i++) {
                if ("CLASS".equals(typeName[i]) && !"NULL".equals(value[i])) {
                    item = new TreeItem(parent, SWT.NONE);
                    item.setText(columnName[i] + ": " + value[i]);
                    TreeItem treeItem = new TreeItem(item, SWT.NONE);
                    treeItem.setText(value[i]);
                    treeItem.setData(OID_ITEM_FLAG, value[i]);
                    TreeItem dumyItem = new TreeItem(treeItem, SWT.NONE);
                    dumyItem.setData(DUMY_ITEM_FLAG, DUMY_ITEM_FLAG);
                } else if ("SET".equals(typeName[i])
                        || "MULTISET".equals(typeName[i])
                        || "SEQUENCE".equals(typeName[i])) {
                    item = new TreeItem(parent, SWT.NONE);
                    item.setText(columnName[i] + ": " + value[i]);
                    if (isOid != null) {
                        for (int j = 0; j < oidSet.length; j++) {
                            if (isOid[j]) {
                                TreeItem treeItem = new TreeItem(item, SWT.NONE);
                                treeItem.setData(OID_ITEM_FLAG, oidSet[j]);
                                treeItem.setText(oidSet[j]);
                                TreeItem dumyItem = new TreeItem(treeItem, SWT.NONE);
                                dumyItem.setData(DUMY_ITEM_FLAG, DUMY_ITEM_FLAG);
                            }
                        }
                    }
                } else {
                    (new TreeItem(parent, SWT.NONE)).setText(columnName[i] + ": " + value[i]);
                }
            }
        } catch (SQLException e) {
            CommonUITool.openErrorBox(
                    getShell(),
                    Messages.bind(
                            com.cubrid.common.ui.common.Messages.errCommonTip,
                            e.getErrorCode(),
                            e.getMessage()));
            LOGGER.error("", e);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return true;
    }
}
