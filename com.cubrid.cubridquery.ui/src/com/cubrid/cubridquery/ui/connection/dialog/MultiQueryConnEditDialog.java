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
package com.cubrid.cubridquery.ui.connection.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import com.cubrid.cubridquery.ui.connection.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MultiQueryConnEditDialog extends CMTitleAreaDialog {

    private List<CubridDatabase> tmpDatabaseList;
    public TableViewer dbTable = null;
    public static final int SAVE_ID = -4;
    public static final String COMMENTKEY = "commentKey";

    public MultiQueryConnEditDialog(Shell parentShell, List<CubridDatabase> editDBList) {
        super(parentShell);
        tmpDatabaseList = new ArrayList<CubridDatabase>();
        for (CubridDatabase db : editDBList) {
            CubridDatabase tmpCubridDatabase = cloneMultiEditQueryConnInfo(db);
            tmpDatabaseList.add(tmpCubridDatabase);
        }
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.multiQueryConnEditDialogTitle);
        Composite parentComp = (Composite) super.createDialogArea(parent);
        setTitle(Messages.multiQueryConnEditDialogMessage);
        //		setMessage(Messages.multiQueryConnEditDialogMessage);

        dbTable =
                new TableViewer(
                        parentComp,
                        SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        dbTable.getTable().setLayoutData(gridData);
        dbTable.getTable().setHeaderVisible(true);
        dbTable.getTable().setLinesVisible(true);

        @SuppressWarnings("all")
        // emty column can make image not take no use place at first column
        final TableViewerColumn empty = new TableViewerColumn(dbTable, SWT.NONE);

        final TableViewerColumn connName = new TableViewerColumn(dbTable, SWT.NONE);
        connName.getColumn().setWidth(130);
        connName.getColumn().setText(Messages.multiQueryConnEditDialogColConName);
        connName.setEditingSupport(new QueryConnEditingSupport(0, dbTable));

        final TableViewerColumn commentName = new TableViewerColumn(dbTable, SWT.NONE);
        commentName.getColumn().setWidth(100);
        commentName.getColumn().setText(Messages.multiQueryConnEditDialogColConComment);
        commentName.setEditingSupport(new QueryConnEditingSupport(1, dbTable));

        final TableViewerColumn dbName = new TableViewerColumn(dbTable, SWT.NONE);
        dbName.getColumn().setWidth(100);
        dbName.getColumn().setText(Messages.multiQueryConnEditDialogColConDBName);
        dbName.setEditingSupport(new QueryConnEditingSupport(2, dbTable));

        final TableViewerColumn userName = new TableViewerColumn(dbTable, SWT.NONE);
        userName.getColumn().setWidth(100);
        userName.getColumn().setText(Messages.multiQueryConnEditDialogColConUserName);
        userName.setEditingSupport(new QueryConnEditingSupport(3, dbTable));

        final TableViewerColumn passwordName = new TableViewerColumn(dbTable, SWT.NONE);
        passwordName.getColumn().setWidth(130);
        passwordName.getColumn().setText(Messages.multiQueryConnEditDialogColConPassword);
        passwordName.setEditingSupport(new QueryConnEditingSupport(4, dbTable));

        final TableViewerColumn savePassword = new TableViewerColumn(dbTable, SWT.NONE);
        savePassword.getColumn().setWidth(100);
        savePassword.getColumn().setText(Messages.multiQueryConnEditDialogColConSavePassword);
        savePassword.setEditingSupport(new AutosavePasswordSupport(dbTable));

        final TableViewerColumn brokerIP = new TableViewerColumn(dbTable, SWT.NONE);
        brokerIP.getColumn().setWidth(100);
        brokerIP.getColumn().setText(Messages.multiQueryConnEditDialogColConBrokerIP);
        brokerIP.setEditingSupport(new QueryConnEditingSupport(6, dbTable));

        final TableViewerColumn brokerPort = new TableViewerColumn(dbTable, SWT.NONE);
        brokerPort.getColumn().setWidth(100);
        brokerPort.getColumn().setText(Messages.multiQueryConnEditDialogColConBrokerport);
        brokerPort.setEditingSupport(new QueryConnEditingSupport(7, dbTable));

        dbTable.setContentProvider(new DatabaseListContentProvider());
        dbTable.setLabelProvider(new DatabaseListLabelProvider());
        dbTable.setInput(tmpDatabaseList);
        return parentComp;
    }

    /**
     * QueryConnEditingSupport
     *
     * @author fulei
     */
    public class QueryConnEditingSupport extends EditingSupport {
        private TextCellEditor textCellEditor;
        private MyCellEditorValidator validator = null;
        private TableViewer dbTable;
        private int type;

        public QueryConnEditingSupport(int type, TableViewer serverTable) {
            super(serverTable);
            this.type = type;
            this.dbTable = serverTable;
        }

        /**
         * MyCellEditorValidator
         *
         * @author fulei
         */
        class MyCellEditorValidator implements ICellEditorValidator {

            private CubridDatabase cubridDatabase = null;

            /**
             * isValid
             *
             * @param value Object
             * @return String
             */
            public String isValid(Object value) {
                if (value == null) {
                    return "empty";
                }
                if (type == 0) {
                    String hostName = (String) value;
                    boolean isValidConnName = hostName != null && hostName.length() > 0;
                    if (!isValidConnName) {
                        setErrorMessage(Messages.multiQueryConnEditErrConnName);
                        return Messages.multiQueryConnEditErrConnName;
                    }

                    if (isExistConnection(hostName)) {
                        setErrorMessage(Messages.multiQueryConnEditErrConnName2);
                        return Messages.multiQueryConnEditErrConnName2;
                    }
                } else if (type == 2) {
                    String dbName = (String) value;
                    boolean isValidDb = dbName != null && dbName.length() > 0;
                    if (!isValidDb) {
                        setErrorMessage(Messages.multiQueryConnEditErrDatabaseName);
                        return Messages.multiQueryConnEditErrDatabaseName;
                    }

                } else if (type == 6) {
                    String brokerIP = (String) value;
                    if (!brokerIP.equals("localhost") && !ValidateUtil.isIP(brokerIP)) {
                        setErrorMessage(Messages.multiQueryConnEditErrBrokerIP);
                        return Messages.multiQueryConnEditErrBrokerIP;
                    }
                } else if (type == 7) {
                    String brokerPort = (String) value;
                    boolean isValidPort =
                            brokerPort != null
                                    && brokerPort.length() > 0
                                    && ValidateUtil.isInteger(brokerPort)
                                    && !(Integer.parseInt(brokerPort.toString()) < 1024
                                            || Integer.parseInt(brokerPort.toString()) > 65535);
                    if (!isValidPort) {
                        setErrorMessage(Messages.multiQueryConnEditErrBrokerPort);
                        return Messages.multiQueryConnEditErrBrokerPort;
                    }
                }
                return null;
            }

            public CubridDatabase getCubridDatabase() {
                return cubridDatabase;
            }

            public void setCubridDatabase(CubridDatabase cubridDatabase) {
                this.cubridDatabase = cubridDatabase;
            }

            /**
             * Check the database connection whether exist
             *
             * @param name String
             * @return boolean
             */
            private boolean isExistConnection(String name) {
                List<CubridDatabase> databaseList =
                        CQBDBNodePersistManager.getInstance().getAllDatabase();
                for (CubridDatabase db : databaseList) {
                    if (!cubridDatabase.getId().equals(db.getId())
                            && name != null
                            && name.equals(db.getName())) {
                        return true;
                    }
                }
                return false;
            }
        }

        /**
         * canEdit
         *
         * @param element Object
         * @return boolean
         */
        protected boolean canEdit(Object element) {
            return true;
        }

        /**
         * getCellEditor
         *
         * @param element Object
         * @return CellEditor
         */
        protected CellEditor getCellEditor(Object element) {
            CubridDatabase cubridDatabase = (CubridDatabase) element;

            // normal type use textCellEditor
            if (textCellEditor == null) {
                Composite table = (Composite) dbTable.getControl();
                textCellEditor = new TextCellEditor(table);
                validator = new MyCellEditorValidator();
                textCellEditor.setValidator(validator);
                textCellEditor.addListener(
                        new ICellEditorListener() {

                            public void applyEditorValue() {}

                            public void cancelEditor() {}

                            public void editorValueChanged(
                                    boolean oldValidState, boolean newValidState) {}
                        });
            }
            validator.setCubridDatabase(cubridDatabase);
            return textCellEditor;
        }

        /**
         * getValue
         *
         * @param element Object
         * @return Object
         */
        protected Object getValue(Object element) {
            CubridDatabase cubridDatabase = (CubridDatabase) element;
            switch (type) {
                case 0:
                    return cubridDatabase.getLabel();
                case 1:
                    return cubridDatabase.getData(COMMENTKEY);
                case 2:
                    return cubridDatabase.getDatabaseInfo().getDbName();
                case 3:
                    return cubridDatabase.getDatabaseInfo().getAuthLoginedDbUserInfo().getName();
                case 4:
                    return cubridDatabase
                                            .getDatabaseInfo()
                                            .getAuthLoginedDbUserInfo()
                                            .getNoEncryptPassword()
                                    == null
                            ? ""
                            : cubridDatabase
                                    .getDatabaseInfo()
                                    .getAuthLoginedDbUserInfo()
                                    .getNoEncryptPassword();
                case 6:
                    return cubridDatabase.getDatabaseInfo().getBrokerIP();
                case 7:
                    return cubridDatabase.getDatabaseInfo().getBrokerPort();
            }
            return "";
        }

        /**
         * setValue
         *
         * @param element Object
         * @param value Object
         */
        protected void setValue(Object element, Object value) {
            if (value == null) {
                validate(false);
                return;
            }
            CubridDatabase cubridDatabase = (CubridDatabase) element;
            boolean flag = false;
            switch (type) {
                case 0:
                    if (!((String) value).equals(cubridDatabase.getLabel())) {
                        flag = true;
                    }
                    cubridDatabase.setLabel((String) value);
                    break;
                case 1:
                    if (!((String) value).equals(cubridDatabase.getData(COMMENTKEY))) {
                        flag = true;
                    }
                    cubridDatabase.setData(COMMENTKEY, (String) value);
                    break;
                case 2:
                    if (!((String) value).equals(cubridDatabase.getDatabaseInfo().getDbName())) {
                        flag = true;
                    }
                    cubridDatabase.getDatabaseInfo().setDbName((String) value);
                    break;
                case 3:
                    if (!((String) value)
                            .equals(
                                    cubridDatabase
                                            .getDatabaseInfo()
                                            .getAuthLoginedDbUserInfo()
                                            .getName())) {
                        flag = true;
                    }
                    cubridDatabase
                            .getDatabaseInfo()
                            .getAuthLoginedDbUserInfo()
                            .setName((String) value);
                    break;
                case 4:
                    if (!((String) value)
                            .equals(
                                    cubridDatabase
                                            .getDatabaseInfo()
                                            .getAuthLoginedDbUserInfo()
                                            .getNoEncryptPassword())) {
                        flag = true;
                    }
                    cubridDatabase
                            .getDatabaseInfo()
                            .getAuthLoginedDbUserInfo()
                            .setNoEncryptPassword((String) value);
                    break;
                case 6:
                    if (!((String) value).equals(cubridDatabase.getDatabaseInfo().getBrokerIP())) {
                        flag = true;
                    }
                    cubridDatabase.getDatabaseInfo().setBrokerIP((String) value);
                    break;
                case 7:
                    if (!((String) value)
                            .equals(cubridDatabase.getDatabaseInfo().getBrokerPort())) {
                        flag = true;
                    }
                    cubridDatabase.getDatabaseInfo().setBrokerPort((String) value);
                    break;
            }
            dbTable.refresh();
            validate(flag);
        }
    }

    /** @author fulei */
    public class AutosavePasswordSupport extends EditingSupport {
        private final TableViewer dbTable;
        private final CheckboxCellEditor checkboxCellEditor;

        public AutosavePasswordSupport(TableViewer dbTable) {
            super(dbTable);
            this.dbTable = dbTable;
            checkboxCellEditor = new CheckboxCellEditor(dbTable.getTable());
        }

        /**
         * canEdit
         *
         * @param element Object
         * @return boolean
         */
        protected boolean canEdit(Object element) {
            return true;
        }

        /**
         * getCellEditor
         *
         * @param element Object
         * @return CellEditor
         */
        protected CellEditor getCellEditor(Object element) {
            return checkboxCellEditor;
        }

        /**
         * getValue
         *
         * @param element Object
         * @return Object
         */
        protected Object getValue(Object element) {
            CubridDatabase cubridDatabase = (CubridDatabase) element;
            return cubridDatabase.isAutoSavePassword();
        }

        /**
         * setValue
         *
         * @param element Object
         * @param value Object
         */
        protected void setValue(Object element, Object value) {
            CubridDatabase cubridDatabase = (CubridDatabase) element;
            cubridDatabase.setAutoSavePassword((Boolean) value);
            validate(true);
            dbTable.refresh();
        }
    }

    /** @author fulei */
    class DatabaseListContentProvider implements IStructuredContentProvider {

        /**
         * getElements
         *
         * @param inputElement Object
         * @return Object[]
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<CubridDatabase> list = (List<CubridDatabase>) inputElement;
                CubridDatabase[] nodeArr = new CubridDatabase[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        /** dispose */
        public void dispose() {
            // do nothing
        }

        /**
         * inputChanged
         *
         * @param viewer Viewer
         * @param oldInput Object
         * @param newInput Object
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }
    }

    /** @author fulei */
    class DatabaseListLabelProvider extends LabelProvider implements ITableLabelProvider {
        private final Image checkImage =
                CubridQueryUIPlugin.getImage("icons/navigator/checked.gif");
        private final Image uncheckImage =
                CubridQueryUIPlugin.getImage("icons/navigator/unchecked.gif");

        /**
         * getColumnImage
         *
         * @param element Object
         * @param columnIndex int
         * @return Image
         */
        public final Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 6) {
                CubridDatabase cubridDatabase = (CubridDatabase) element;
                if (cubridDatabase.isAutoSavePassword()) {
                    return checkImage;
                } else {
                    return uncheckImage;
                }
            }
            return null;
        }

        /**
         * getColumnText
         *
         * @param element Object
         * @param columnIndex int
         * @return String
         */
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof CubridDatabase) {
                CubridDatabase cubridDatabase = (CubridDatabase) element;
                if (columnIndex == 1) {
                    return cubridDatabase.getLabel();
                } else if (columnIndex == 2) {
                    return cubridDatabase.getData(COMMENTKEY) == null
                            ? ""
                            : (String) cubridDatabase.getData(COMMENTKEY);
                } else if (columnIndex == 3) {
                    return cubridDatabase.getDatabaseInfo().getDbName();
                } else if (columnIndex == 4) {
                    return cubridDatabase.getDatabaseInfo().getAuthLoginedDbUserInfo().getName();
                } else if (columnIndex == 5) {
                    return "****";
                } else if (columnIndex == 7) {
                    return cubridDatabase.getDatabaseInfo().getBrokerIP();
                } else if (columnIndex == 8) {
                    return cubridDatabase.getDatabaseInfo().getBrokerPort();
                }
            }
            return null;
        }
    }

    /**
     * validate the data
     *
     * @return boolean
     */
    protected boolean validate(boolean flag) {
        // do nothing now
        setErrorMessage(null);
        if (flag) {
            getButton(SAVE_ID).setEnabled(flag);
        }
        return true;
    }

    /**
     * clone edit database
     *
     * @param cloneDB
     * @return
     */
    public CubridDatabase cloneMultiEditQueryConnInfo(CubridDatabase cloneDB) {
        CubridDatabase newCubridDatabase = new CubridDatabase(cloneDB.getId(), cloneDB.getLabel());
        DatabaseInfo cloneDBInfo = cloneDB.getDatabaseInfo();

        ServerInfo cloneServerInfo = cloneDBInfo.getServerInfo();
        String databaseName = cloneDBInfo.getDbName();
        String brokerIp = cloneDBInfo.getBrokerIP();
        String brokerPort = cloneDBInfo.getBrokerPort();
        String userName = cloneDBInfo.getAuthLoginedDbUserInfo().getName();
        String userPassword = cloneDBInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
        String charset = cloneDBInfo.getCharSet();
        String jdbcAttrs = cloneDBInfo.getJdbcAttrs();
        String jdbcVersion = cloneServerInfo.getJdbcDriverVersion();

        ServerInfo newServerInfo = new ServerInfo();
        newServerInfo.setServerName(databaseName);
        newServerInfo.setHostAddress(brokerIp);
        newServerInfo.setHostMonPort(Integer.parseInt(brokerPort));
        newServerInfo.setHostJSPort(Integer.parseInt(brokerPort) + 1);
        newServerInfo.setUserName(databaseName + "@" + brokerIp);
        newServerInfo.setJdbcDriverVersion(jdbcVersion);

        DatabaseInfo newDBInfo = new DatabaseInfo(databaseName, newServerInfo);
        newDBInfo.setBrokerIP(brokerIp);
        newDBInfo.setBrokerPort(brokerPort);
        newDBInfo.setCharSet(charset);
        newDBInfo.setJdbcAttrs(jdbcAttrs);

        DbUserInfo newUserInfo = new DbUserInfo();
        newUserInfo.setDbName(databaseName);
        newUserInfo.setName(userName);
        newUserInfo.setNoEncryptPassword(userPassword);
        newDBInfo.setAuthLoginedDbUserInfo(newUserInfo);

        newCubridDatabase.setAutoSavePassword(cloneDB.isAutoSavePassword());
        newCubridDatabase.setServer(cloneDB.getServer());
        newCubridDatabase.setDatabaseInfo(newDBInfo);

        DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(cloneDB, false);
        if (editorConfig != null) {
            newCubridDatabase.setData(COMMENTKEY, editorConfig.getDatabaseComment());
        } else {
            newCubridDatabase.setData(COMMENTKEY, "");
        }

        return newCubridDatabase;
    }

    public List<CubridDatabase> getNewDBList() {
        return tmpDatabaseList;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, SAVE_ID, Messages.multiQueryConnEditDialogBtnSave, false)
                .setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    /**
     * Call this method when button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }
}
