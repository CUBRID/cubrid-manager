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
package com.cubrid.common.ui.common.preference;

import com.cubrid.common.core.task.impl.JDBCDriverDownloadTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * A composite to show JDBC preference page
 *
 * @author robinhood 2009-09-08
 */
public class JdbcManageComposite extends Composite {

    private static final Logger LOGGER = LogUtil.getLogger(JdbcManageComposite.class);
    private final List<Map<String, String>> jdbcListData = new ArrayList<Map<String, String>>();
    private TableViewer jdbcInfoTv;

    private static final String KEY_JDBC_LOAD_PATH = "JdbcManageComposite.JDBC_LOAD_PATH";

    public JdbcManageComposite(Composite parent) {
        super(parent, SWT.NONE);
        createContent();
    }

    /** load JDBC option from preference store */
    public void loadPreference() {
        Map<String, String> loadCubridJdbc = CubridJdbcManager.getInstance().getLoadedJdbc();
        Iterator<Entry<String, String>> iterator = loadCubridJdbc.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> next = iterator.next();
            String jdbcVersion = next.getKey();
            String url = next.getValue();
            Map<String, String> map = new HashMap<String, String>();
            map.put("0", jdbcVersion);
            map.put("1", url);
            jdbcListData.add(map);
        }
        jdbcInfoTv.refresh();
        CommonUITool.packTable(jdbcInfoTv);
    }

    /** save JDBC options */
    public void save() {
        Map<String, String> map = new HashMap<String, String>();
        for (Map<String, String> jdbc : jdbcListData) {
            map.put(jdbc.get("0"), jdbc.get("1"));
        }
        CubridJdbcManager.getInstance().resetCubridJdbcSetting(map);
    }

    /**
     * Create JDBC table group
     *
     * @param composite the composite
     */
    private void createJdbcTableGroup(Composite composite) {
        final String[] columnNameArr =
                new String[] {Messages.tblColDriverVersion, Messages.tblColJarPath};

        TableViewerSorter sorter = new TableViewerSorter();
        sorter.setColumnComparator(
                0,
                new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        if (o1 instanceof String && o2 instanceof String) {
                            String s1 = (String) o1;
                            String s2 = (String) o2;

                            String[] version1Tokens =
                                    s1.substring(s1.lastIndexOf('-') + 1).split("\\.");
                            String[] version2Tokens =
                                    s2.substring(s2.lastIndexOf('-') + 1).split("\\.");

                            int size = Math.min(version1Tokens.length, version2Tokens.length);

                            for (int i = 0; i < size; i++) {
                                Integer first = Integer.parseInt(version1Tokens[i]);
                                Integer second = Integer.parseInt(version2Tokens[i]);
                                if (first != second) {
                                    return first - second;
                                }
                            }
                            return version1Tokens.length - version2Tokens.length;
                        } else {
                            return 0;
                        }
                    }
                });
        sorter.setAsc(false);
        jdbcInfoTv =
                CommonUITool.createCommonTableViewer(
                        composite,
                        sorter,
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
        jdbcInfoTv.setInput(jdbcListData);
        jdbcInfoTv.getTable().setSortColumn(jdbcInfoTv.getTable().getColumn(0));
        jdbcInfoTv.getTable().setSortDirection(sorter.isAsc() ? SWT.UP : SWT.DOWN);

        TableLayout tableLayout = new TableLayout();
        jdbcInfoTv.getTable().setLayout(tableLayout);
        tableLayout.addColumnData(new ColumnWeightData(35, true));
        tableLayout.addColumnData(new ColumnWeightData(65, true));
    }

    /**
     * Get selected JDBC version
     *
     * @return the version String
     */
    @SuppressWarnings("unchecked")
    public String getSelectedJdbcVersion() {
        IStructuredSelection selection = (IStructuredSelection) jdbcInfoTv.getSelection();
        if (selection.isEmpty()) {
            return null;
        }
        Map<String, String> map = (Map<String, String>) selection.getFirstElement();
        return map.get("0");
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
            buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        }

        Button downLoadBtn = new Button(buttonComposite, SWT.PUSH);
        downLoadBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        downLoadBtn.setText(Messages.btnUpdateJdbc);
        downLoadBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (CommonUITool.openConfirmBox(Messages.msgAlertUpdateJdbc)) {
                            updateNewJdbcDrivers();
                        }
                    }
                });

        Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addButton.setText(Messages.btnAdd);
        addButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String filteredPath =
                                CommonUIPlugin.getPluginDialogSettings().get(KEY_JDBC_LOAD_PATH);
                        FileDialog dialog =
                                new FileDialog(
                                        PlatformUI.getWorkbench()
                                                .getActiveWorkbenchWindow()
                                                .getShell(),
                                        SWT.OPEN | SWT.MULTI);
                        dialog.setFilterExtensions(new String[] {"*.jar"});
                        dialog.setFilterNames(new String[] {"CUBRID JDBC Driver(*.jar)"});
                        if (filteredPath != null) {
                            dialog.setFilterPath(filteredPath);
                        }
                        String jdbcURL = dialog.open();
                        String[] fileNames = dialog.getFileNames();
                        if (jdbcURL == null || jdbcURL.trim().length() == 0) {
                            return;
                        }
                        CommonUIPlugin.getPluginDialogSettings().put(KEY_JDBC_LOAD_PATH, jdbcURL);
                        for (String fileName : fileNames) {
                            File file = new File(jdbcURL);
                            String filePath = file.getParent() + File.separator + fileName;
                            loadJDBC(filePath);
                        }
                    }
                });
        Button delButton = new Button(buttonComposite, SWT.PUSH);
        delButton.setText(Messages.btnDelete);
        delButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {

                        TableItem[] selection = jdbcInfoTv.getTable().getSelection();
                        if (selection == null || selection.length == 0) {
                            return;
                        }

                        for (TableItem item : selection) {
                            String version = item.getText(0);
                            String path = item.getText(1);
                            if (CubridJdbcManager.getInstance().isDefaultJdbc(path)) {
                                CommonUITool.openWarningBox(
                                        Messages.bind(Messages.warningDeleteJdbc, version));
                                return;
                            }
                            if (isJdbcDriverUsing(version)) {
                                return;
                            }
                        }

                        for (TableItem item : selection) {
                            String version = item.getText(0);
                            for (Map<String, String> map : jdbcListData) {
                                if (version.equals(map.get("0"))) {
                                    jdbcListData.remove(map);
                                    break;
                                }
                            }
                        }
                        jdbcInfoTv.refresh();
                        int count = jdbcInfoTv.getTable().getItemCount();
                        if (count > 0) {
                            jdbcInfoTv.getTable().select(count - 1);
                        }
                        jdbcInfoTv.getTable().setFocus();
                    }
                });
    }

    /** Update new jdbc drivers. */
    private void updateNewJdbcDrivers() {

        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    String downloadedFiles = "";
                                    JDBCDriverDownloadTask task = new JDBCDriverDownloadTask();
                                    String savedPath = getDefaultJDBCSavedPath();
                                    List<String> jdbcList = task.getJDBCFileList();
                                    if (jdbcList == null || jdbcList.size() == 0) {
                                        CommonUITool.openInformationBox(Messages.msgAlertNoDrivers);
                                        return;
                                    }
                                    task =
                                            new JDBCDriverDownloadTask(
                                                    jdbcList,
                                                    savedPath,
                                                    Messages.taskDownload,
                                                    Messages.subTaskDownload);
                                    CommonTaskExec taskExec =
                                            new CommonTaskExec(Messages.taskDownload);
                                    taskExec.addTask(task);
                                    new ExecTaskWithProgress(taskExec).busyCursorWhile();
                                    if (taskExec.isSuccess()) {
                                        List<String> downloadedList = task.getDriverList();
                                        if (downloadedList != null && downloadedList.size() > 0) {
                                            downloadedFiles =
                                                    StringUtils.join(downloadedList, '\n');
                                            for (int i = 0; i < jdbcList.size(); i++) {
                                                String path =
                                                        savedPath
                                                                + File.separator
                                                                + jdbcList.get(i);
                                                loadJDBC(path);
                                            }
                                        }
                                    }
                                    CommonUITool.openInformationBox(
                                            Messages.titleSuccess,
                                            Messages.jdbcDriverDownloadSuccessMsg,
                                            downloadedFiles);
                                } catch (Exception e) {
                                    LOGGER.error("", e);
                                }
                            }
                        });
    }

    /**
     * Get default JDBC saved path
     *
     * @return File
     */
    private String getDefaultJDBCSavedPath() {
        List<File> fileList = CubridJdbcManager.getInstance().getDefaultJDBCPath();
        for (File file : fileList) {
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * Validate the JDBC
     *
     * @param jdbcURL String
     */
    private void loadJDBC(String jdbcURL) {
        if (StringUtil.isEmpty(jdbcURL)) {
            return;
        }
        String jdbcVersion = null;
        try {
            jdbcVersion = JdbcClassLoaderFactory.getJdbcJarVersion(jdbcURL);
        } catch (IOException e) {
            // Do thing
        }
        if (StringUtil.isEmpty(jdbcVersion)) {
            return;
        }
        Map<String, String> selectedMap = null;
        for (Map<String, String> map : jdbcListData) {
            String version = map.get("0");
            if (jdbcVersion.equals(version)) {
                return;
            }
        }
        if (selectedMap == null) {
            selectedMap = new HashMap<String, String>();
            selectedMap.put("0", jdbcVersion);
            selectedMap.put("1", jdbcURL);
            jdbcListData.add(selectedMap);
        }
        jdbcInfoTv.refresh();
        jdbcInfoTv.setSelection(new StructuredSelection(selectedMap));
        jdbcInfoTv.getTable().setFocus();
    }

    /** Create page content */
    private void createContent() {
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createJdbcTableGroup(this);
        createButtonComp(this);
    }

    /**
     * Get using JDBC node list
     *
     * @param navigatorView CubridNavigatorView
     * @return List<ICubridNode>
     */
    @SuppressWarnings("unchecked")
    private List<ICubridNode> getUsingJdbcNodeList(CubridNavigatorView navigatorView) {
        TreeViewer viewer = navigatorView.getViewer();
        Object inputObj = viewer.getInput();
        if (!(inputObj instanceof List<?>)) {
            return null;
        }
        List<ICubridNode> list = new ArrayList<ICubridNode>();
        if (navigatorView.isShowGroup()) {
            List<ICubridNode> groupList = (List<ICubridNode>) inputObj;
            for (ICubridNode groupNode : groupList) {
                if (groupNode.getChildren() != null) {
                    list.addAll(groupNode.getChildren());
                }
            }
        } else {
            list = (List<ICubridNode>) inputObj;
        }
        return list;
    }

    /**
     * Return whether the JDBC driver is using
     *
     * @param jdbcVersion String
     * @return boolean
     */
    private boolean isJdbcDriverUsing(String jdbcVersion) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return false;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return false;
        }
        IViewReference[] viewReference = page.getViewReferences();
        for (int i = 0; viewReference != null && i < viewReference.length; i++) {
            IViewPart viewPart = viewReference[i].getView(false);
            if (!(viewPart instanceof CubridNavigatorView)) {
                continue;
            }
            CubridNavigatorView navigatorView = (CubridNavigatorView) viewPart;
            List<ICubridNode> list = getUsingJdbcNodeList(navigatorView);
            if (list == null) {
                continue;
            }

            for (ICubridNode node : list) {
                if (NodeType.SERVER.equals(node.getType())) {
                    CubridServer server = (CubridServer) node;
                    ServerInfo serverInfo = server.getServerInfo();
                    if (serverInfo == null) {
                        continue;
                    }

                    if (serverInfo.isConnected()
                            && serverInfo.getJdbcDriverVersion().equals(jdbcVersion)) {
                        CommonUITool.openErrorBox(
                                Messages.bind(
                                        Messages.errDeleteJdbcServer,
                                        new Object[] {serverInfo.getServerName(), jdbcVersion}));
                        return true;
                    }
                } else if (NodeType.DATABASE.equals(node.getType())) {
                    CubridDatabase database = (CubridDatabase) node;
                    if (database == null
                            || database.getServer() == null
                            || database.getServer().getServerInfo() == null) {
                        continue;
                    }

                    ServerInfo serverInfo = database.getServer().getServerInfo();
                    if (serverInfo == null) {
                        LOGGER.error("The serverInfo is a null.");
                        return false;
                    }
                    boolean isSameVersion =
                            StringUtil.isEqual(serverInfo.getJdbcDriverVersion(), jdbcVersion);
                    if (isSameVersion && database.isLogined()) {
                        CommonUITool.openErrorBox(
                                Messages.bind(
                                        Messages.errDeleteJdbcConn,
                                        new Object[] {database.getName(), jdbcVersion}));
                        return true;
                    }
                }
            }
        }
        IEditorReference[] editorReference = page.getEditorReferences();
        for (int i = 0; editorReference != null && i < editorReference.length; i++) {
            IEditorPart editorPart = editorReference[i].getEditor(false);
            if (!(editorPart instanceof QueryEditorPart)) {
                continue;
            }
            QueryEditorPart queryEditorPart = (QueryEditorPart) editorPart;
            CubridServer server = queryEditorPart.getSelectedServer();
            ServerInfo serverInfo = server == null ? null : server.getServerInfo();
            String partName = queryEditorPart.getPartName();
            if (serverInfo != null && serverInfo.getJdbcDriverVersion().equals(jdbcVersion)) {
                CommonUITool.openErrorBox(
                        Messages.bind(
                                Messages.errDeleteJdbcQuery, new Object[] {partName, jdbcVersion}));
                return true;
            }
        }
        return false;
    }
}
