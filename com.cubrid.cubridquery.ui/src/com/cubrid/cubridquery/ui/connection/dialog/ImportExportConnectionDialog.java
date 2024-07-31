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
package com.cubrid.cubridquery.ui.connection.dialog;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.IModelAttributeConstants;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.Messages;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.action.ActionExecutor;
import com.cubrid.cubridquery.ui.connection.editor.DatabaseInfoTableViewerContentProvider;
import com.cubrid.cubridquery.ui.connection.editor.DatabaseInfoTableViewerLabelProvider;
import com.cubrid.cubridquery.ui.spi.model.DatabaseUIWrapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/** Import and export connection informations for CQB */
public class ImportExportConnectionDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(ImportExportConnectionDialog.class);
    private static final String IMPORT_DUPLICATED_HOST_SUFFIX_FORMAT = "HHmmss";
    private int WIDTH_NAME = 180;
    private int WIDTH_ADDRESS = 110;
    private int WIDTH_PORT = 80;
    private int WIDTH_JDBC = 150;
    private int WIDTH_USER = 60;
    private int WIDTH_SAVEPASS = 90;
    private static final int ERROR_CODE = -1;
    private static final int SUCCESS_CODE = 0;
    private static final String EMPTY_STR = "";
    private Label lblFromFolder;
    private Text txFromFolder;
    private Button btnBrowser;
    private Table tblHostList;
    private CheckboxTableViewer hostListView;
    private boolean export;
    private boolean selectAll;
    private static Map<String, Method> methodCache = new HashMap<String, Method>();
    private Map<String, DatabaseUIWrapper> servers = new HashMap<String, DatabaseUIWrapper>();
    private Map<String, AtomicLong> counterMap = new HashMap<String, AtomicLong>();
    private List<CubridDatabase> savedServer =
            new ArrayList<CubridDatabase>(); // both saved and imported

    public ImportExportConnectionDialog(Shell parentShell) {
        this(parentShell, false);
    }

    public ImportExportConnectionDialog(Shell parentShell, boolean export) {
        super(parentShell);
        this.export = export;
        selectAll = export;
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        if (!export) {
            lblFromFolder = new Label(composite, SWT.SHADOW_IN);
            lblFromFolder.setText(Messages.lblFromFile);
            lblFromFolder.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

            GridData fromData = CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1);
            fromData.horizontalIndent = 0;
            txFromFolder = new Text(composite, SWT.BORDER | SWT.LEFT | SWT.READ_ONLY);
            txFromFolder.setLayoutData(fromData);

            GridData browserData = CommonUITool.createGridData(1, 1, -1, -1);
            browserData.horizontalIndent = 10;
            browserData.widthHint = 80;
            btnBrowser = new Button(composite, SWT.CENTER);
            btnBrowser.setText(Messages.lblBrowser);
            btnBrowser.setLayoutData(browserData);

            btnBrowser.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent e) {
                            FileDialog dialog =
                                    new FileDialog(
                                            getParentShell(), (export) ? SWT.SAVE : SWT.OPEN);
                            dialog.setFilterExtensions(new String[] {"*.xml"});
                            String fileName = dialog.open();
                            if (fileName != null) {
                                txFromFolder.setText(fileName);
                            }

                            if (!export) {
                                List<DatabaseUIWrapper> servers = parseFile(fileName);
                                hostListView.setInput(servers);
                                ImportExportConnectionDialog.this
                                        .getButton(IDialogConstants.OK_ID)
                                        .setEnabled(hostListView.getCheckedElements().length > 0);
                            }
                        }
                    });
        }

        setMessage((export) ? Messages.msgExportServer : Messages.msgImportServer);
        setTitle((export) ? Messages.tlExportServer : Messages.tlImportServer);
        getShell().setText((export) ? Messages.tlExportServer : Messages.tlImportServer);
        createTable(composite);

        return super.createDialogArea(parent);
    }

    @SuppressWarnings("unchecked")
    private void handleSelectAll(boolean selected) {
        List<DatabaseUIWrapper> input = (List<DatabaseUIWrapper>) hostListView.getInput();
        for (DatabaseUIWrapper check : input) {
            if (check.isSeleted() != selected) check.setSeleted(selected);
        }
        hostListView.setAllChecked(selected);

        updateWidgetStatus();
    }

    @SuppressWarnings("unchecked")
    private void setSelectAll(boolean selected) {
        List<DatabaseUIWrapper> input = (List<DatabaseUIWrapper>) hostListView.getInput();
        for (DatabaseUIWrapper check : input) {
            if (check.isSeleted() != selected) check.setSeleted(selected);
        }
        hostListView.setAllChecked(selected);
    }

    private void createTable(Composite composite) {
        hostListView =
                CheckboxTableViewer.newCheckList(
                        composite,
                        SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
        tblHostList = hostListView.getTable();

        final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdColumnsTable.horizontalSpan = 3;
        gdColumnsTable.heightHint = 189;
        tblHostList.setLayoutData(gdColumnsTable);
        tblHostList.setLinesVisible(true);
        tblHostList.setHeaderVisible(true);

        final TableColumn hostName = new TableColumn(tblHostList, SWT.NONE);
        hostName.setAlignment(SWT.LEFT);
        hostName.setWidth(WIDTH_NAME);
        hostName.setText(Messages.columnHeaderServerInfo);

        if (export) {
            hostName.setImage(CommonUIPlugin.getImage("icons/checked_green.png"));
        } else {
            hostName.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
        }
        hostName.addListener(
                SWT.Selection,
                new Listener() {
                    public void handleEvent(Event event) {
                        selectAll = !selectAll;
                        hostListView.setAllChecked(selectAll);
                        Image image =
                                selectAll
                                        ? CommonUIPlugin.getImage("icons/checked_green.png")
                                        : CommonUIPlugin.getImage("icons/unchecked.gif");
                        hostName.setImage(image);
                        handleSelectAll(selectAll);
                    }
                });

        final TableColumn hostAddress = new TableColumn(tblHostList, SWT.NONE);
        hostAddress.setAlignment(SWT.CENTER);
        hostAddress.setWidth(WIDTH_ADDRESS);
        hostAddress.setText(Messages.columnHeaderServerAddress);

        final TableColumn hostPort = new TableColumn(tblHostList, SWT.NONE);
        hostPort.setAlignment(SWT.CENTER);
        hostPort.setWidth(WIDTH_PORT);
        hostPort.setText(Messages.columnHeaderServerPort);

        final TableColumn jdbcVer = new TableColumn(tblHostList, SWT.NONE);
        jdbcVer.setAlignment(SWT.CENTER);
        jdbcVer.setWidth(WIDTH_JDBC);
        jdbcVer.setText(Messages.columnHeaderServerJdbcVersion);

        final TableColumn hostUser = new TableColumn(tblHostList, SWT.NONE);
        hostUser.setAlignment(SWT.CENTER);
        hostUser.setWidth(WIDTH_USER);
        hostUser.setText(Messages.columnHeaderServerUserName);

        final TableColumn savedPass = new TableColumn(tblHostList, SWT.NONE);
        savedPass.setAlignment(SWT.CENTER);
        savedPass.setWidth(WIDTH_SAVEPASS);
        savedPass.setText(Messages.columnHeaderServerAutoSave);

        DatabaseInfoTableViewerContentProvider contentProvider =
                new DatabaseInfoTableViewerContentProvider();
        DatabaseInfoTableViewerLabelProvider labelProvider =
                new DatabaseInfoTableViewerLabelProvider();
        hostListView.setContentProvider(contentProvider);
        hostListView.setLabelProvider(labelProvider);
        hostListView.addCheckStateListener(
                new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        boolean isSelect = event.getChecked();
                        Object obj = event.getElement();
                        if (obj instanceof DatabaseUIWrapper) {
                            ((DatabaseUIWrapper) obj).setSeleted(isSelect);
                        }

                        updateWidgetStatus();
                    }
                });

        hostListView.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent arg0) {
                        ImportExportConnectionDialog.this
                                .getButton(IDialogConstants.OK_ID)
                                .setEnabled(hostListView.getCheckedElements().length > 0);
                    }
                });
        if (export) {
            initTableData();
            setSelectAll(true);
        }
    }

    private void updateWidgetStatus() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            if (hostListView.getCheckedElements().length > 0) {
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            } else {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }
        }
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

        updateWidgetStatus();
    }

    protected void okPressed() {

        ActionExecutor executor = null;
        if (export) {
            FileDialog dialog = new FileDialog(getParentShell(), (export) ? SWT.SAVE : SWT.OPEN);
            dialog.setText(
                    com.cubrid.cubridquery.ui.connection.Messages.exportConnectionsSelectPathMsg);
            dialog.setFilterExtensions(new String[] {"*.xml"});
            dialog.setFileName("export_connections");
            String fileName = dialog.open();
            if (fileName == null) {
                return;
            }

            executor = new ExportAction(fileName);
        } else {
            executor = new ImportAction();
        }

        if (executor.execute() == SUCCESS_CODE) {
            String msg =
                    export
                            ? com.cubrid.cubridquery.ui.connection.Messages
                                    .exportConnectionsSuccessMsg
                            : com.cubrid.cubridquery.ui.connection.Messages
                                    .importConnectionsSuccessMsg;
            CommonUITool.openInformationBox(com.cubrid.common.ui.common.Messages.titleSuccess, msg);
            super.okPressed();
        }
    }

    private static class ImportTask extends AbstractTask {
        private List<DatabaseUIWrapper> input;
        private boolean isSuccess = false;
        private boolean isCancel = false;

        public ImportTask(List<DatabaseUIWrapper> input) {
            this.input = input;
        }

        public void execute() {
            for (DatabaseUIWrapper ui : input) {
                /*Update Query options*/
                CubridDatabase database = ui.getDatabase();
                if (database.getData(CubridDatabase.DATA_KEY_EDITOR_CONFIG) != null) {
                    QueryOptions.putEditorConfig(
                            database,
                            (DatabaseEditorConfig)
                                    database.getData(CubridDatabase.DATA_KEY_EDITOR_CONFIG),
                            false);
                }
                /*Update to workbench*/
                CQBDBNodePersistManager.getInstance().getAllDatabase().add(database);
            }
            CQBDBNodePersistManager.getInstance().saveDatabases();
        }

        public void cancel() {
            isCancel = true;
        }

        public void finish() {
            isSuccess = true;
        }

        public boolean isCancel() {
            return isCancel;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    private class ImportAction implements ActionExecutor {
        public int execute() {
            String file = txFromFolder.getText();
            if (file == null || EMPTY_STR.equals(file)) {
                CommonUITool.openErrorBox(Messages.msgErrorMissingImportFile);
                txFromFolder.forceFocus();
                return ERROR_CODE;
            }

            TableItem[] items = tblHostList.getItems();
            List<DatabaseUIWrapper> input = new ArrayList<DatabaseUIWrapper>();
            for (TableItem item : items) {
                DatabaseUIWrapper ui = (DatabaseUIWrapper) item.getData();
                if (ui.isSeleted()) input.add(ui);
            }

            if (input.size() == 0) {
                CommonUITool.openErrorBox(Messages.msgErrorMissingImportServer);
                tblHostList.forceFocus();
                return ERROR_CODE;
            }

            TaskExecutor executor = new CommonTaskExec(Messages.taskNameImportServer);
            executor.addTask(new ImportTask(input));
            new ExecTaskWithProgress(executor).exec();
            if (executor.isSuccess()) {
                refreshTreeView();
                return SUCCESS_CODE;
            } else {
                return ERROR_CODE;
            }
        }

        private void refreshTreeView() {
            CQBDBNodePersistManager.getInstance().reloadDatabases();
            CQBGroupNodePersistManager.getInstance().reloadGroups();

            CubridQueryNavigatorView view =
                    (CubridQueryNavigatorView)
                            CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
            if (view != null && view.getViewer() != null) {
                view.getViewer().refresh(true);
            }
        }
    }

    private static class ExportTask extends AbstractTask {
        private List<CubridDatabase> exports;
        private String fileName;
        private boolean isCancel = false;
        private boolean isSuccess = false;

        public ExportTask(String fileName, List<CubridDatabase> exports) {
            this.setTaskname(Messages.taskNameExportServer);
            this.exports = exports;
            this.fileName = fileName;
        }

        public void execute() {
            CQBDBNodePersistManager.getInstance().saveServer(exports, fileName);
        }

        public void cancel() {
            isCancel = true;
        }

        public void finish() {
            isSuccess = true;
        }

        public boolean isCancel() {
            return isCancel;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    private class ExportAction implements ActionExecutor {
        private final String filePath;

        public ExportAction(String filePath) {
            this.filePath = filePath;
        }

        public int execute() {
            File f = new File(filePath);
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            TableItem[] items = tblHostList.getItems();
            if (items.length == 0) {
                return ERROR_CODE;
            }

            List<CubridDatabase> exports = new ArrayList<CubridDatabase>();
            for (TableItem item : items) {
                DatabaseUIWrapper wrapper = (DatabaseUIWrapper) item.getData();
                if (!wrapper.isSeleted()) {
                    continue;
                }
                exports.add(wrapper.getDatabase());
            }

            if (exports.size() == 0) {
                CommonUITool.openErrorBox(Messages.msgErrorMissingExportServer);
                txFromFolder.forceFocus();
                return ERROR_CODE;
            }

            TaskExecutor taskExecutor = new CommonTaskExec(Messages.taskNameExportServer);
            taskExecutor.addTask(new ExportTask(filePath, exports));
            new ExecTaskWithProgress(taskExecutor).exec();
            return SUCCESS_CODE;
        }
    }

    private void initTableData() {
        List<CubridDatabase> servers = CQBDBNodePersistManager.getInstance().getAllDatabase();
        List<CubridDatabase> input = new ArrayList<CubridDatabase>(servers);
        hostListView.setInput(input);
    }

    protected List<DatabaseUIWrapper> parseFile(String fileName) {
        if (methodCache.size() == 0) {
            initMethodCache();
        }

        counterMap.clear();
        savedServer.clear();
        savedServer.addAll(CQBDBNodePersistManager.getInstance().getAllDatabase());
        servers.clear();

        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        List<CubridDatabase> list =
                CQBDBNodePersistManager.getInstance().parseDatabaseFromXML(file);

        for (CubridDatabase server : list) {
            DatabaseUIWrapper wrapper = new DatabaseUIWrapper(server);
            servers.put(server.getId(), wrapper);
        }

        for (DatabaseUIWrapper wrapper : servers.values()) {
            handleDuplicateServer(wrapper);
        }

        return new ArrayList<DatabaseUIWrapper>(servers.values());
    }

    public void handleDuplicateServer(DatabaseUIWrapper server) {
        if (server == null || server.getDatabase() == null) return;
        isContainSameHost(server.getDatabase());
    }

    public boolean isContainSameHost(CubridDatabase server) {
        SimpleDateFormat format = new SimpleDateFormat(IMPORT_DUPLICATED_HOST_SUFFIX_FORMAT);

        for (int i = 0; i < savedServer.size(); i++) {
            CubridDatabase serv = savedServer.get(i);
            if (serv.getId().equals(server.getId())) {
                setMessage(Messages.msgInfoChangeName, IMessageProvider.WARNING);
                // check name has a time pattern
                String oldId = server.getId();
                updateTimePart(server, format);
                servers.put(oldId, new DatabaseUIWrapper(server));
            }
        }

        return false;
    }

    /**
     * check input str contains the timestamp part generated by default
     *
     * @param database database name
     * @param format DateFormat object
     */
    private void updateTimePart(CubridDatabase database, DateFormat format) {
        String suffix = format.format(Calendar.getInstance().getTime());
        database.setLabel(database.getName() + "_" + suffix);
        database.setId(
                database.getLabel() + ICubridNodeLoader.NODE_SEPARATOR + database.getLabel());
    }

    /** init CubridServerUIWrapper methods cache. */
    private void initMethodCache() {
        putSetMethod(IModelAttributeConstants.SERVER_INFO_ADDRESS);
        putSetMethod(IModelAttributeConstants.SERVER_INFO_JDBC_DRV);
        putSetMethod(IModelAttributeConstants.SERVER_INFO_NAME);
        putSetMethod(IModelAttributeConstants.SERVER_INFO_PASS);
        putSetMethod(IModelAttributeConstants.SERVER_INFO_PORT);
        putSetMethod(IModelAttributeConstants.SERVER_INFO_SAVE_PASS);
        putSetMethod(IModelAttributeConstants.SERVER_INFO_USER);
    }

    /**
     * invoke setter method by attribute name
     *
     * @param attributeName
     */
    private void putSetMethod(String attributeName) {
        try {
            Method method =
                    DatabaseUIWrapper.class.getMethod(
                            "set"
                                    + Character.toUpperCase(attributeName.charAt(0))
                                    + attributeName.substring(1),
                            String.class);
            methodCache.put(attributeName, method);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
