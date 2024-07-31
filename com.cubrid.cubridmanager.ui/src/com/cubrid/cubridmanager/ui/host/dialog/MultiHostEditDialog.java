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
package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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

public class MultiHostEditDialog extends CMTitleAreaDialog {

    private List<CubridServer> tmpServerList;
    public TableViewer serverTable = null;
    public static final int SAVE_ID = 4;

    public MultiHostEditDialog(Shell parentShell, List<CubridServer> editServerList) {
        super(parentShell);
        tmpServerList = new ArrayList<CubridServer>();
        for (CubridServer cubridServer : editServerList) {
            CubridServer tmpServer = cloneMultiEditHostInfo(cubridServer);
            tmpServerList.add(tmpServer);
        }
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.multiEditServerDialogTitle);
        Composite parentComp = (Composite) super.createDialogArea(parent);
        setTitle(Messages.multiEditServerDialogTitle);
        setMessage(Messages.multiEditServerDialogMessages);

        serverTable =
                new TableViewer(
                        parentComp,
                        SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        serverTable.getTable().setLayoutData(gridData);
        serverTable.getTable().setHeaderVisible(true);
        serverTable.getTable().setLinesVisible(true);

        @SuppressWarnings("all")
        // emty column can make image not take no use place at first column
        final TableViewerColumn empty = new TableViewerColumn(serverTable, SWT.NONE);

        final TableViewerColumn columnName = new TableViewerColumn(serverTable, SWT.NONE);
        columnName.getColumn().setWidth(130);
        columnName.getColumn().setText(Messages.multiEditServerDialogColumnName);
        columnName.setEditingSupport(new HostEditingSupport(0, serverTable));

        final TableViewerColumn columnHost = new TableViewerColumn(serverTable, SWT.NONE);
        columnHost.getColumn().setWidth(100);
        columnHost.getColumn().setText(Messages.multiConnectServerDialogColumnHostAddress);
        columnHost.setEditingSupport(new HostEditingSupport(1, serverTable));

        final TableViewerColumn columnPort = new TableViewerColumn(serverTable, SWT.NONE);
        columnPort.getColumn().setWidth(50);
        columnPort.getColumn().setText(Messages.multiEditServerDialogColumnPort);
        columnPort.setEditingSupport(new HostEditingSupport(2, serverTable));

        final TableViewerColumn columnDriver = new TableViewerColumn(serverTable, SWT.NONE);
        columnDriver.getColumn().setWidth(160);
        columnDriver.getColumn().setText(Messages.multiEditServerDialogColumnDriver);
        columnDriver.setEditingSupport(new HostEditingSupport(3, serverTable));

        final TableViewerColumn columnUser = new TableViewerColumn(serverTable, SWT.NONE);
        columnUser.getColumn().setWidth(80);
        columnUser.getColumn().setText(Messages.multiConnectServerDialogColumnUser);
        columnUser.setEditingSupport(new HostEditingSupport(4, serverTable));

        final TableViewerColumn columnPassword = new TableViewerColumn(serverTable, SWT.NONE);
        columnPassword.getColumn().setWidth(70);
        columnPassword.getColumn().setText(Messages.multiEditServerDialogColumnPassword);
        columnPassword.setEditingSupport(new HostEditingSupport(5, serverTable));

        final TableViewerColumn columnAutosavePassword =
                new TableViewerColumn(serverTable, SWT.NONE);
        columnAutosavePassword.getColumn().setWidth(40);
        columnAutosavePassword
                .getColumn()
                .setText(Messages.multiEditServerDialogColumnAutosavePassword);
        columnAutosavePassword
                .getColumn()
                .setToolTipText(Messages.multiEditServerDialogColumnAutosavePassword);
        columnAutosavePassword.setEditingSupport(new AutosavePasswordSupport(serverTable));

        serverTable.setContentProvider(new ServerListContentProvider());
        serverTable.setLabelProvider(new ServerListLabelProvider());
        serverTable.setInput(tmpServerList);

        return parentComp;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, SAVE_ID, Messages.btnConnectSave, false);
        createButton(
                parent, IDialogConstants.CANCEL_ID, Messages.multiEditServerDialogClose, false);
    }

    /** @author fulei */
    class ServerListContentProvider implements IStructuredContentProvider {

        /**
         * getElements
         *
         * @param inputElement Object
         * @return Object[]
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<CubridServer> list = (List<CubridServer>) inputElement;
                CubridServer[] nodeArr = new CubridServer[list.size()];
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
    class ServerListLabelProvider extends LabelProvider implements ITableLabelProvider {
        private final Image checkImage =
                CubridManagerUIPlugin.getImage("icons/navigator/checked.gif");
        private final Image uncheckImage =
                CubridManagerUIPlugin.getImage("icons/navigator/unchecked.gif");
        /**
         * getColumnImage
         *
         * @param element Object
         * @param columnIndex int
         * @return Image
         */
        public final Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 7) {
                CubridServer cubridServer = (CubridServer) element;
                if (cubridServer.isAutoSavePassword()) {
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
            if (element instanceof CubridServer) {
                CubridServer cubridServer = (CubridServer) element;
                if (columnIndex == 1) {
                    return cubridServer.getServerName();
                } else if (columnIndex == 2) {
                    return cubridServer.getHostAddress();
                } else if (columnIndex == 3) {
                    return cubridServer.getMonPort();
                } else if (columnIndex == 4) {
                    return cubridServer.getJdbcDriverVersion();
                } else if (columnIndex == 5) {
                    return cubridServer.getUserName();
                } else if (columnIndex == 6) {
                    return "****";
                } else if (columnIndex == 7) {
                    //					return Boolean.toString(cubridServer.isAutoSavePassword());
                }
            }
            return null;
        }
    }

    /** @author fulei */
    public class AutosavePasswordSupport extends EditingSupport {
        private final TableViewer serverTable;
        private final CheckboxCellEditor checkboxCellEditor;

        public AutosavePasswordSupport(TableViewer serverTable) {
            super(serverTable);
            this.serverTable = serverTable;
            checkboxCellEditor = new CheckboxCellEditor(serverTable.getTable());
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
            CubridServer cubridServer = (CubridServer) element;
            return cubridServer.isAutoSavePassword();
        }

        /**
         * setValue
         *
         * @param element Object
         * @param value Object
         */
        protected void setValue(Object element, Object value) {
            CubridServer cubridServer = (CubridServer) element;
            cubridServer.setAutoSavePassword((Boolean) value);
            serverTable.refresh();
        }
    }

    /**
     * HostEditingSupport
     *
     * @author fulei
     */
    public class HostEditingSupport extends EditingSupport {
        private TextCellEditor textCellEditor;
        private ComboBoxCellEditor jdbcVersionCellEditor;
        private MyCellEditorValidator validator = null;
        private TableViewer serverTable;
        private int type;
        private final String[] AUTOSAVEOPTIONS = {"true", "false"};

        public HostEditingSupport(int type, TableViewer serverTable) {
            super(serverTable);
            this.type = type;
            this.serverTable = serverTable;
        }

        /**
         * MyCellEditorValidator
         *
         * @author fulei
         */
        class MyCellEditorValidator implements ICellEditorValidator {

            private CubridServer cubridServer = null;

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
                    //					boolean isValidHostName = hostName.indexOf(" ") < 0
                    //							&& hostName.trim().length() >= 4
                    //							&& hostName.trim().length() <= ValidateUtil.MAX_NAME_LENGTH;
                    boolean isValidHostName =
                            hostName.trim().length() > 0
                                    && hostName.trim().length() <= ValidateUtil.MAX_NAME_LENGTH;
                    if (!isValidHostName) {
                        setErrorMessage(Messages.errHostName);
                        setEnabled(false);
                        return Messages.errHostName;
                    }

                    boolean isHostExist =
                            CMHostNodePersistManager.getInstance()
                                    .isContainedByName(hostName, getCubridServer());
                    if (isHostExist) {
                        setErrorMessage(Messages.errHostExist);
                        setEnabled(false);
                        return Messages.errHostExist;
                    }
                } else if (type == 1) {
                    String address = (String) value;
                    if (!address.equals("localhost") && !ValidateUtil.isIP(address)) {
                        setEnabled(false);
                        setErrorMessage(Messages.errAddress);
                        return Messages.errAddress;
                    }
                } else if (type == 2) {
                    String port = (String) value;
                    boolean isValidPort = ValidateUtil.isNumber(port);
                    if (isValidPort) {
                        int portVal = Integer.parseInt(port);
                        if (portVal < 1024 || portVal > 65535) {
                            isValidPort = false;
                        }
                    }
                    if (!isValidPort) {
                        setEnabled(false);
                        setErrorMessage(Messages.errPort);
                        return Messages.errPort;
                    }
                } else if (type == 4) {
                    String userName = (String) value;
                    boolean isValidUserName =
                            userName.indexOf(" ") < 0
                                    && userName.trim().length() >= 4
                                    && userName.trim().length() <= ValidateUtil.MAX_NAME_LENGTH;

                    if (!isValidUserName) {
                        setEnabled(false);
                        setErrorMessage(Messages.errUserName);
                        return Messages.errUserName;
                    }
                } else if (type == 5) {
                    String password = (String) value;
                    if (password.trim().length() == 0) {
                        setEnabled(false);
                        setErrorMessage(Messages.errUserPassword);
                        return Messages.errUserPassword;
                    }
                }
                return null;
            }

            public void setCubridServer(CubridServer cubridServer) {
                this.cubridServer = cubridServer;
            }

            public CubridServer getCubridServer() {
                return cubridServer;
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
            CubridServer cubridServer = (CubridServer) element;
            if (type == 3) {
                if (jdbcVersionCellEditor == null) {
                    jdbcVersionCellEditor =
                            new ComboBoxCellEditor(
                                    serverTable.getTable(), getJdbcVersion(), SWT.READ_ONLY) {
                                String[] jdbcVersionArr = getJdbcVersion();

                                protected void doSetValue(Object value) {
                                    for (int i = 0; i < jdbcVersionArr.length; i++) {
                                        if (jdbcVersionArr[i].equals((String) value)) {
                                            super.doSetValue(i);
                                        }
                                    }
                                }

                                protected Object doGetValue() {
                                    int selection = ((Integer) super.doGetValue()).intValue();
                                    return jdbcVersionArr[selection];
                                }
                            };
                }
                return jdbcVersionCellEditor;
            }

            // normal type use textCellEditor
            if (textCellEditor == null) {
                Composite table = (Composite) serverTable.getControl();
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
            validator.setCubridServer(cubridServer);
            return textCellEditor;
        }

        /**
         * getValue
         *
         * @param element Object
         * @return Object
         */
        protected Object getValue(Object element) {
            CubridServer cubridServer = (CubridServer) element;
            switch (type) {
                case 0:
                    return cubridServer.getServerName();
                case 1:
                    return cubridServer.getHostAddress();
                case 2:
                    return cubridServer.getMonPort();
                case 3:
                    String[] jdbcVersionArr = getJdbcVersion();
                    for (int i = 0; i < jdbcVersionArr.length; i++) {
                        if (jdbcVersionArr[i].equals(cubridServer.getJdbcDriverVersion())) {
                            return cubridServer.getJdbcDriverVersion();
                        }
                    }
                    return cubridServer.getJdbcDriverVersion();
                case 4:
                    return cubridServer.getUserName();
                case 5:
                    return cubridServer.getPassword() == null ? "" : cubridServer.getPassword();
                case 6:
                    for (int i = 0; i < AUTOSAVEOPTIONS.length; i++) {
                        if (AUTOSAVEOPTIONS[i].equals(
                                Boolean.toString(cubridServer.isAutoSavePassword()))) {
                            return Boolean.toString(cubridServer.isAutoSavePassword());
                        }
                    }
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
                validate();
                return;
            }
            CubridServer cubridServer = (CubridServer) element;
            switch (type) {
                case 0:
                    cubridServer.getServerInfo().setServerName((String) value);
                    break;
                case 1:
                    cubridServer.getServerInfo().setHostAddress((String) value);
                    break;
                case 2:
                    cubridServer.getServerInfo().setHostMonPort(Integer.valueOf((String) value));
                    break;
                case 3:
                    cubridServer.getServerInfo().setJdbcDriverVersion((String) value);
                    break;
                case 4:
                    cubridServer.getServerInfo().setUserName((String) value);
                    break;
                case 5:
                    cubridServer.getServerInfo().setUserPassword((String) value);
                    break;
                case 6:
                    cubridServer.setAutoSavePassword((Boolean) value);
                    break;
            }
            serverTable.refresh();
            validate();
        }
    }

    /**
     * Enable or disable the button
     *
     * @param isEnabled whether it is enabled
     */
    private void setEnabled(boolean isEnabled) {
        getButton(SAVE_ID).setEnabled(isEnabled);
    }

    /**
     * validate the data
     *
     * @return boolean
     */
    protected boolean validate() {
        // do nothing now
        setErrorMessage(null);

        getButton(SAVE_ID).setEnabled(true);
        return true;
    }

    public List<CubridServer> getNewServerList() {
        return tmpServerList;
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

    /**
     * cloneMultiEditHostInfo
     *
     * @param cubridServer
     * @return
     */
    public CubridServer cloneMultiEditHostInfo(CubridServer cubridServer) {
        CubridServer newCubridServer =
                new CubridServer(cubridServer.getId(), cubridServer.getLabel(), null, null);
        ServerInfo newServerInfo = new ServerInfo();
        ServerInfo oldCubridServer = cubridServer.getServerInfo();
        newCubridServer.setAutoSavePassword(cubridServer.isAutoSavePassword());
        newServerInfo.setServerName(oldCubridServer.getServerName());
        newServerInfo.setHostAddress(cubridServer.getHostAddress());
        newServerInfo.setHostMonPort(oldCubridServer.getHostMonPort());
        newServerInfo.setJdbcDriverVersion(oldCubridServer.getJdbcDriverVersion());
        newServerInfo.setUserName(oldCubridServer.getUserName());
        if (cubridServer.isAutoSavePassword()) {
            newServerInfo.setUserPassword(oldCubridServer.getUserPassword());
        }
        newCubridServer.setServerInfo(newServerInfo);
        return newCubridServer;
    }

    /**
     * getJdbcVersion
     *
     * @return
     */
    public String[] getJdbcVersion() { // FIXME extract
        Map<String, String> jdbcMap = CubridJdbcManager.getInstance().getLoadedJdbc();
        if (jdbcMap.isEmpty()) {
            return new String[] {""};
        }
        List<String> jdbcVersionList = new ArrayList<String>();
        Iterator<Entry<String, String>> iterator = jdbcMap.entrySet().iterator();
        jdbcVersionList.add(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        while (iterator.hasNext()) {
            Entry<String, String> next = iterator.next();
            jdbcVersionList.add(next.getKey());
        }
        String[] stringArr = new String[jdbcVersionList.size()];
        return jdbcVersionList.toArray(stringArr);
    }
}
