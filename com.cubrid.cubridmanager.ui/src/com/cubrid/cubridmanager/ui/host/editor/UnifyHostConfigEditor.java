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
package com.cubrid.cubridmanager.ui.host.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.dialog.CubridBrokerConfEditAnnotationDialog;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.model.CubridBrokerProperty;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.model.CubridCMConfConfig;
import com.cubrid.cubridmanager.ui.host.model.CubridConfConfig;
import com.cubrid.cubridmanager.ui.host.model.CubridConfProperty;
import com.cubrid.cubridmanager.ui.spi.util.UnifyHostConfigUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * this class is a unify host parameter edito
 *
 * <p>can edit multiple host 's cubrid.conf, broker.conf, cubrid_ha.conf, cm.conf, cm_httpd.conf,
 * acl files
 *
 * @author fulei
 * @version 1.0 - 2013-1-30 created by fulei
 */
public class UnifyHostConfigEditor extends CubridEditorPart {

    private static final Logger LOGGER = LogUtil.getLogger(UnifyHostConfigEditor.class);
    public static final String ID = "com.cubrid.cubridmanager.ui.host.editor.UnifyHostConfigEditor";

    public final String BROKERCONFCONFIGFILENAME = "broker.conf";
    public final String CUBRIDCONFCONFIGFILENAME = "cubrid.conf";
    public final String CUBRIDCMCONFCONFIGFILENAME = "cm.conf";

    public static String ANNOTATION = "_annotation";
    public static String BROKERNAMECOLUMNTITLE = "BROKER";
    public static String SERVERNAMECOLUMNTITLE = "HOST";
    public static String CUBRIDNAMECOLUMNTITLE = "SECTION";
    private UnifyHostConfigEditorInput editorInput;
    private boolean isDirty = false;
    public CTabFolder confTabFolder;
    private UnifyHostConfigUtil unifyHostConfigUtil = new UnifyHostConfigUtil();
    private BrokerConfPersistUtil cubridBrokerConfUtil = new BrokerConfPersistUtil();

    private LinkedHashMap<String, BrokerConfig> cubridBrokerConfigDataMap =
            new LinkedHashMap<String, BrokerConfig>();
    private LinkedHashMap<String, CubridConfConfig> cubridConfConfigDataMap =
            new LinkedHashMap<String, CubridConfConfig>();
    private LinkedHashMap<String, CubridCMConfConfig> cubridConfCMConfigDataMap =
            new LinkedHashMap<String, CubridCMConfConfig>();

    private final List<Map<String, String>> cubridBrokerConfListData =
            new ArrayList<Map<String, String>>();
    private final List<Map<String, String>> cubridConfConfigListData =
            new ArrayList<Map<String, String>>();
    private final List<Map<String, String>> cubridConfCMConfigListData =
            new ArrayList<Map<String, String>>();

    private TableViewer cubridConfTabTableViewer;
    private TableViewer brokerConfTabTableViewer;
    private TableViewer cubridCMConfTabTableViewer;

    private CTabItem brokerConfTableCTabItem;
    private CTabItem cubridConfTableCTabItem;
    private CTabItem cubridCMConfTableCTabItem;

    private Point cubridConfTableClickPoint;
    private Point cubridCMConfTableClickPoint;
    private Point cubridBrokerTableClickPoint;

    private long cubridConfTableClickPointTiming;
    private long cubridBrokerTableClickPointTiming;
    private long cubridCMConfTableClickPointTiming;

    public void createPartControl(Composite parent) {
        Composite topComp = new Composite(parent, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.verticalSpacing = 0;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            gridLayout.horizontalSpacing = 0;
            topComp.setLayout(gridLayout);
            topComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        }
        createToolBar(topComp);
        createConfTabFolder(topComp);
        createConfTabItem();
        loadData();
        parseDataToTableValue();
    }

    /** load edit data */
    public void loadData() {
        if (editorInput.getTaskCountValue() == 0) {
            return;
        }
        try {
            ProgressMonitorDialog progress = new ProgressMonitorDialog(getSite().getShell());
            progress.setCancelable(false);
            progress.run(
                    true,
                    true,
                    new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) throws InvocationTargetException {
                            monitor.beginTask(
                                    Messages.unifyHostConfigEditorLoadingDataMsg,
                                    editorInput.getTaskCountValue());
                            unifyHostConfigUtil = new UnifyHostConfigUtil();
                            for (CubridServer cubridServer : editorInput.getCubridServers()) {
                                if (editorInput.isEditCubridConf()) {
                                    monitor.subTask(
                                            Messages.bind(
                                                    Messages.unifyHostConfigEditorLoadingDataMsg2,
                                                    "cubrid.conf",
                                                    cubridServer.getName()));
                                    GetCubridConfParameterTask getCubridConfParameterTask =
                                            new GetCubridConfParameterTask(
                                                    cubridServer.getServerInfo());
                                    getCubridConfParameterTask.execute();
                                    if (getCubridConfParameterTask.isSuccess()) {
                                        List<String> contentsList =
                                                getCubridConfParameterTask.getConfContents();
                                        StringBuilder contentBuilder = new StringBuilder();
                                        for (String content : contentsList) {
                                            contentBuilder
                                                    .append(content)
                                                    .append(StringUtil.NEWLINE);
                                        }
                                        CubridConfConfig cubridConfConfig =
                                                unifyHostConfigUtil
                                                        .parseStringLineToCubridConfConfig(
                                                                contentBuilder.toString());
                                        cubridConfConfigDataMap.put(
                                                cubridServer.getName(), cubridConfConfig);
                                        monitor.worked(1);
                                    }
                                }

                                if (editorInput.isEditBrokerConf()) {
                                    monitor.subTask(
                                            Messages.bind(
                                                    Messages.unifyHostConfigEditorLoadingDataMsg2,
                                                    "broker",
                                                    cubridServer.getName()));
                                    GetBrokerConfParameterTask getBrokerConfParameterTask =
                                            new GetBrokerConfParameterTask(
                                                    cubridServer.getServerInfo());
                                    getBrokerConfParameterTask.execute();
                                    if (getBrokerConfParameterTask.isSuccess()) {
                                        List<String> contentsList =
                                                getBrokerConfParameterTask.getConfContents();
                                        StringBuilder contentBuilder = new StringBuilder();
                                        for (String content : contentsList) {
                                            contentBuilder
                                                    .append(content)
                                                    .append(StringUtil.NEWLINE);
                                        }

                                        BrokerConfig cubridBrokerConfig =
                                                cubridBrokerConfUtil.parseStringLineToBrokerConfig(
                                                        contentBuilder.toString());
                                        cubridBrokerConfigDataMap.put(
                                                cubridServer.getName(), cubridBrokerConfig);

                                        monitor.worked(1);
                                    }
                                }

                                if (editorInput.isEditCMConf()) {
                                    monitor.subTask(
                                            Messages.bind(
                                                    Messages.unifyHostConfigEditorLoadingDataMsg2,
                                                    "cm.conf",
                                                    cubridServer.getName()));
                                    GetCMConfParameterTask getCMConfParameterTask =
                                            new GetCMConfParameterTask(
                                                    cubridServer.getServerInfo());
                                    getCMConfParameterTask.execute();
                                    if (getCMConfParameterTask.isSuccess()) {
                                        List<String> contentsList =
                                                getCMConfParameterTask.getConfContents();
                                        StringBuilder contentBuilder = new StringBuilder();
                                        for (String content : contentsList) {
                                            contentBuilder
                                                    .append(content)
                                                    .append(StringUtil.NEWLINE);
                                        }
                                        CubridCMConfConfig cubridCMConfConfig =
                                                unifyHostConfigUtil
                                                        .parseStringLineToCubridCMConfConfig(
                                                                contentBuilder.toString());
                                        cubridConfCMConfigDataMap.put(
                                                cubridServer.getName(), cubridCMConfConfig);
                                        //								System.out.println(contentBuilder.toString());
                                    }

                                    monitor.worked(1);
                                }

                                if (editorInput.isEditHAConf()) {
                                    monitor.subTask(
                                            Messages.bind(
                                                    Messages.unifyHostConfigEditorLoadingDataMsg2,
                                                    "cubrid_ha.conf",
                                                    cubridServer.getName()));
                                    monitor.worked(1);
                                }

                                if (editorInput.isEditACLConf()) {
                                    monitor.subTask(
                                            Messages.bind(
                                                    Messages.unifyHostConfigEditorLoadingDataMsg2,
                                                    "acl",
                                                    cubridServer.getName()));
                                    monitor.worked(1);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    /** parse data to table value */
    public void parseDataToTableValue() {
        if (editorInput.isEditCubridConf()) {
            createCubridConfTableData();
        }

        if (editorInput.isEditBrokerConf()) {
            createBrokerConfTableData();
        }

        if (editorInput.isEditCMConf()) {
            createCubridCMConfTableData();
        }
    }
    /** create table data by cubridCMConfConfig model */
    public void createCubridCMConfTableData() {
        // remove column
        int oldCount = cubridCMConfTabTableViewer.getTable().getColumnCount();
        for (int i = 0; i < oldCount; i++) {
            cubridCMConfTabTableViewer.getTable().getColumn(0).dispose();
        }
        // create column
        TableViewerColumn column = new TableViewerColumn(cubridCMConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(175);
        column.getColumn().setText(Messages.unifyHostConfTableColumnPropName);
        column.setEditingSupport(new PropValueEditingSupport(cubridCMConfTabTableViewer, 0));
        int columnIndex = 0;
        for (int i = 0; i < cubridConfCMConfigDataMap.size(); i++) {
            TableViewerColumn propColumn =
                    new TableViewerColumn(cubridCMConfTabTableViewer, SWT.LEFT);
            propColumn.getColumn().setWidth(130);
            propColumn.getColumn().setText(Messages.unifyHostCubridConfTableTitle + columnIndex);
            propColumn.setEditingSupport(
                    new PropValueEditingSupport(cubridCMConfTabTableViewer, columnIndex + 1));
            propColumn
                    .getColumn()
                    .addSelectionListener(
                            new SelectionAdapter() {
                                @SuppressWarnings("all")
                                public void widgetSelected(SelectionEvent event) {}
                            });
            columnIndex++;
        }

        cubridCMConfTabTableViewer.setLabelProvider(new UnifyHostCubridCMConfTableLabelProvider());
        cubridCMConfTabTableViewer.setInput(cubridConfCMConfigListData);

        cubridConfCMConfigListData.clear();
        cubridConfCMConfigListData.addAll(
                unifyHostConfigUtil.parseCubridCMConfConfigToCommonTableValue(
                        cubridConfCMConfigDataMap));
        cubridCMConfTabTableViewer.refresh();
    }

    /** create table data by cubridConfConfig model */
    public void createCubridConfTableData() {
        // remove column
        int oldCount = cubridConfTabTableViewer.getTable().getColumnCount();
        for (int i = 0; i < oldCount; i++) {
            cubridConfTabTableViewer.getTable().getColumn(0).dispose();
        }
        // create column
        TableViewerColumn column = new TableViewerColumn(cubridConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(150);
        column.getColumn().setText(Messages.unifyHostConfTableColumnPropName);
        column.setEditingSupport(new PropValueEditingSupport(cubridConfTabTableViewer, 0));
        int columnIndex = 0;
        for (Entry<String, CubridConfConfig> entry : cubridConfConfigDataMap.entrySet()) {
            CubridConfConfig cubridConfConfig = entry.getValue();
            for (int i = 0; i < cubridConfConfig.getPropertyList().size(); i++) {
                TableViewerColumn propColumn =
                        new TableViewerColumn(cubridConfTabTableViewer, SWT.LEFT);
                propColumn.getColumn().setWidth(160);
                propColumn
                        .getColumn()
                        .setText(Messages.unifyHostCubridConfTableTitle + columnIndex);
                propColumn.setEditingSupport(
                        new PropValueEditingSupport(cubridConfTabTableViewer, columnIndex + 1));
                propColumn
                        .getColumn()
                        .addSelectionListener(
                                new SelectionAdapter() {
                                    @SuppressWarnings("all")
                                    public void widgetSelected(SelectionEvent event) {}
                                });
                columnIndex++;
            }
        }

        cubridConfTabTableViewer.setLabelProvider(new UnifyHostCubridConfTableLabelProvider());
        cubridConfTabTableViewer.setInput(cubridConfConfigListData);

        cubridConfConfigListData.clear();
        cubridConfConfigListData.addAll(
                unifyHostConfigUtil.parseCubridConfConfigToCommonTableValue(
                        cubridConfConfigDataMap));
        cubridConfTabTableViewer.refresh();
    }

    /** create table data by CubridBrokerConf model */
    public void createBrokerConfTableData() {
        // remove column
        int oldCount = brokerConfTabTableViewer.getTable().getColumnCount();
        for (int i = 0; i < oldCount; i++) {
            brokerConfTabTableViewer.getTable().getColumn(0).dispose();
        }
        // create column
        TableViewerColumn column = new TableViewerColumn(brokerConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(150);
        column.getColumn().setText(Messages.unifyHostConfTableColumnPropName);
        column.setEditingSupport(new PropValueEditingSupport(brokerConfTabTableViewer, 0));
        int columnIndex = 0;
        for (Entry<String, BrokerConfig> entry : cubridBrokerConfigDataMap.entrySet()) {
            BrokerConfig cubridBrokerConfig = entry.getValue();
            for (int i = 0; i < cubridBrokerConfig.getPropertyList().size(); i++) {
                TableViewerColumn propColumn =
                        new TableViewerColumn(brokerConfTabTableViewer, SWT.LEFT);
                propColumn.getColumn().setWidth(160);
                propColumn
                        .getColumn()
                        .setText(Messages.unifyHostBrokerConfTableTitle + columnIndex);
                propColumn.setEditingSupport(
                        new PropValueEditingSupport(brokerConfTabTableViewer, columnIndex + 1));
                propColumn
                        .getColumn()
                        .addSelectionListener(
                                new SelectionAdapter() {
                                    @SuppressWarnings("all")
                                    public void widgetSelected(SelectionEvent event) {}
                                });
                // if broker name is [broker] can't edit and don't display
                //				CubridBrokerProperty brokerProperty =
                // cubridBrokerConfig.getPropertyList().get(i);
                //				if (brokerProperty.getCubridBrokerPropKey().equals("[broker]")) {
                //					propColumn.getColumn().setWidth(0);
                //				}
                columnIndex++;
            }
        }

        brokerConfTabTableViewer.setLabelProvider(new UnifyHostCubridBrokerTableLabelProvider());
        brokerConfTabTableViewer.setInput(cubridBrokerConfListData);

        cubridBrokerConfListData.clear();
        cubridBrokerConfListData.addAll(
                unifyHostConfigUtil.parseCubridBrokerConfigToCommonTableValue(
                        cubridBrokerConfigDataMap));
        brokerConfTabTableViewer.refresh();
    }

    /**
     * Create the tool bar
     *
     * @param parent the Composite
     */
    private void createToolBar(Composite parent) {
        final Composite toolBarComp = new Composite(parent, SWT.NONE);
        {
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            toolBarComp.setLayoutData(gd);
            GridLayout gridLayout = new GridLayout();
            gridLayout.marginHeight = 0;
            gridLayout.horizontalSpacing = 0;
            gridLayout.marginWidth = 0;
            toolBarComp.setLayout(gridLayout);
        }
        ToolBar toolBar = new ToolBar(toolBarComp, SWT.FLAT);
        ToolItem saveItem = new ToolItem(toolBar, SWT.PUSH);
        saveItem.setImage(CubridManagerUIPlugin.getImage("icons/queryeditor/file_save.png"));
        saveItem.setToolTipText(Messages.msgTipSaveAction);
        saveItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (isDirty) {
                            String errMsg = validate();
                            if (errMsg != null) {
                                CommonUITool.openErrorBox(errMsg);
                                return;
                            }

                            doSave(new NullProgressMonitor());
                            setDirty(false);
                        }
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem addPropItem = new ToolItem(toolBar, SWT.PUSH);
        addPropItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/table_record_insert.png"));
        addPropItem.setToolTipText(
                com.cubrid.common.ui.common.Messages.cubridBrokerConfEditorAddPropItemLabel);
        addPropItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        HashMap<String, String> dataMap = new HashMap<String, String>();
                        dataMap.put("0", "new property");
                        TableViewer addDataTableViewer = null;
                        if (confTabFolder.getSelection() == brokerConfTableCTabItem) {
                            cubridBrokerConfListData.add(dataMap);
                            addDataTableViewer = brokerConfTabTableViewer;
                        } else if (confTabFolder.getSelection() == cubridConfTableCTabItem) {
                            cubridConfConfigListData.add(dataMap);
                            addDataTableViewer = cubridConfTabTableViewer;
                        } else if (confTabFolder.getSelection() == cubridCMConfTableCTabItem) {
                            cubridConfCMConfigListData.add(dataMap);
                            addDataTableViewer = cubridCMConfTabTableViewer;
                        }

                        for (int i = 1; i < addDataTableViewer.getTable().getColumnCount(); i++) {
                            dataMap.put(i + "", "");
                        }
                        addDataTableViewer.refresh();
                        setDirty(true);
                        addDataTableViewer
                                .getTable()
                                .showItem(
                                        addDataTableViewer
                                                .getTable()
                                                .getItem(
                                                        addDataTableViewer.getTable().getItemCount()
                                                                - 1));
                    }
                });

        ToolItem deletePropItem = new ToolItem(toolBar, SWT.PUSH);
        deletePropItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete.png"));
        deletePropItem.setToolTipText(
                com.cubrid.common.ui.common.Messages.cubridBrokerConfEditorDeletePropItemLabel);
        deletePropItem.addSelectionListener(
                new SelectionAdapter() {
                    @SuppressWarnings("all")
                    public void widgetSelected(SelectionEvent event) {
                        TableViewer delDataTableViewer = null;
                        List<Map<String, String>> delConfListData = null;
                        if (confTabFolder.getSelection() == brokerConfTableCTabItem) {
                            delDataTableViewer = brokerConfTabTableViewer;
                            delConfListData = cubridBrokerConfListData;
                        } else if (confTabFolder.getSelection() == cubridConfTableCTabItem) {
                            delDataTableViewer = cubridConfTabTableViewer;
                            delConfListData = cubridConfConfigListData;
                        } else if (confTabFolder.getSelection() == cubridCMConfTableCTabItem) {
                            delDataTableViewer = cubridCMConfTabTableViewer;
                            delConfListData = cubridConfCMConfigListData;
                        }

                        int selectionIndex = delDataTableViewer.getTable().getSelectionIndex();
                        if (selectionIndex < 0) {
                            CommonUITool.openErrorBox(
                                    com.cubrid.common.ui.common.Messages
                                            .cubridBrokerConfEditorDeletePropertyMsg);
                            return;
                        }
                        IStructuredSelection selection =
                                (IStructuredSelection) delDataTableViewer.getSelection();
                        HashMap<String, String> valueMap =
                                (HashMap<String, String>) selection.getFirstElement();
                        if (!CommonUITool.openConfirmBox(
                                Messages.bind(
                                        com.cubrid.common.ui.common.Messages
                                                .cubridBrokerConfEditorDeleteBrokerPropConfirm,
                                        valueMap.get("0")))) {
                            return;
                        }
                        delConfListData.remove(valueMap);
                        delDataTableViewer.refresh();
                        setDirty(true);
                    }
                });
    }

    /**
     * createCubridBrokerConfComp
     *
     * @param parent
     */
    public void createConfTabFolder(Composite parent) {
        confTabFolder = new CTabFolder(parent, SWT.BORDER);
        confTabFolder.setLayout(new FillLayout());
        confTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        confTabFolder.setSelectionBackground(ResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
        confTabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
    }

    /** create edit tab item */
    public void createConfTabItem() {
        if (editorInput.isEditCubridConf()) {
            createCubridConfTabItem();
        }
        if (editorInput.isEditBrokerConf()) {
            createBrokerConfTabItem();
        }

        if (editorInput.isEditCMConf()) {
            createCubridCMConfTabItem();
        }
        if (editorInput.isEditHAConf()) {
            createCubridHAConfTabItem();
        }

        if (editorInput.isEditACLConf()) {
            createCubridACLConfTabItem();
        }
    }

    /** create broker conf table */
    public void createBrokerConfTabItem() {
        Composite comp = new Composite(confTabFolder, SWT.NONE);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        brokerConfTableCTabItem = new CTabItem(confTabFolder, SWT.NONE);
        brokerConfTableCTabItem.setText(BROKERCONFCONFIGFILENAME);

        brokerConfTabTableViewer =
                new TableViewer(
                        comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        brokerConfTabTableViewer
                .getTable()
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        brokerConfTabTableViewer.getTable().setHeaderVisible(true);
        brokerConfTabTableViewer.getTable().setLinesVisible(true);
        brokerConfTabTableViewer.setUseHashlookup(true);

        // create column
        TableViewerColumn column = new TableViewerColumn(brokerConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(0);
        column.getColumn().setText("");

        brokerConfTabTableViewer.setContentProvider(
                new UnifyHostCubridBrokerTableContentProvider());
        brokerConfTabTableViewer.setLabelProvider(new UnifyHostCubridBrokerTableLabelProvider());
        brokerConfTabTableViewer.setSorter(new BrokerConfTableViewerSorter());

        // use to mark click point, the right click menu use this point
        brokerConfTabTableViewer
                .getTable()
                .addListener(
                        SWT.MouseDown,
                        new Listener() {
                            public void handleEvent(Event event) {
                                cubridBrokerTableClickPoint = new Point(event.x, event.y);
                                cubridBrokerTableClickPointTiming = System.currentTimeMillis();
                            }
                        });

        registerCubridBrokerTableContextMenu();
        brokerConfTableCTabItem.setControl(comp);
        if (confTabFolder.getSelection() == null) {
            confTabFolder.setSelection(brokerConfTableCTabItem);
        }
    }

    /** create cubrid table */
    public void createCubridConfTabItem() {
        Composite comp = new Composite(confTabFolder, SWT.NONE);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        cubridConfTableCTabItem = new CTabItem(confTabFolder, SWT.NONE);
        cubridConfTableCTabItem.setText(CUBRIDCONFCONFIGFILENAME);

        cubridConfTabTableViewer =
                new TableViewer(
                        comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        cubridConfTabTableViewer
                .getTable()
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        cubridConfTabTableViewer.getTable().setHeaderVisible(true);
        cubridConfTabTableViewer.getTable().setLinesVisible(true);
        cubridConfTabTableViewer.setUseHashlookup(true);

        // create column
        TableViewerColumn column = new TableViewerColumn(cubridConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(0);
        column.getColumn().setText("");

        cubridConfTabTableViewer.setContentProvider(new UnifyHostCubridConfTableContentProvider());
        cubridConfTabTableViewer.setLabelProvider(new UnifyHostCubridConfTableLabelProvider());

        // use to mark click point, the right click menu use this point
        cubridConfTabTableViewer
                .getTable()
                .addListener(
                        SWT.MouseDown,
                        new Listener() {
                            public void handleEvent(Event event) {
                                cubridConfTableClickPoint = new Point(event.x, event.y);
                                cubridConfTableClickPointTiming = System.currentTimeMillis();
                            }
                        });

        registerCubridConfTableContextMenu();
        cubridConfTableCTabItem.setControl(comp);
        if (confTabFolder.getSelection() == null) {
            confTabFolder.setSelection(cubridConfTableCTabItem);
        }
    }

    /** create cubrid CM table */
    public void createCubridCMConfTabItem() {
        Composite comp = new Composite(confTabFolder, SWT.NONE);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        cubridCMConfTableCTabItem = new CTabItem(confTabFolder, SWT.NONE);
        cubridCMConfTableCTabItem.setText("cm.conf");

        cubridCMConfTabTableViewer =
                new TableViewer(
                        comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        cubridCMConfTabTableViewer
                .getTable()
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        cubridCMConfTabTableViewer.getTable().setHeaderVisible(true);
        cubridCMConfTabTableViewer.getTable().setLinesVisible(true);
        cubridCMConfTabTableViewer.setUseHashlookup(true);

        // create column
        TableViewerColumn column = new TableViewerColumn(cubridCMConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(0);
        column.getColumn().setText("");

        cubridCMConfTabTableViewer.setContentProvider(
                new UnifyHostCubridCMConfTableContentProvider());
        cubridCMConfTabTableViewer.setLabelProvider(new UnifyHostCubridCMConfTableLabelProvider());

        // use to mark click point, the right click menu use this point
        cubridCMConfTabTableViewer
                .getTable()
                .addListener(
                        SWT.MouseDown,
                        new Listener() {
                            public void handleEvent(Event event) {
                                cubridCMConfTableClickPoint = new Point(event.x, event.y);
                                cubridCMConfTableClickPointTiming = System.currentTimeMillis();
                            }
                        });

        registerCubridCMConfTableContextMenu();
        cubridCMConfTableCTabItem.setControl(comp);
        if (confTabFolder.getSelection() == null) {
            confTabFolder.setSelection(cubridCMConfTableCTabItem);
        }
    }

    /** create cubrid HA table */
    public void createCubridHAConfTabItem() {
        Composite comp = new Composite(confTabFolder, SWT.NONE);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        CTabItem tableCTabItem = new CTabItem(confTabFolder, SWT.NONE);
        tableCTabItem.setText("cubrid_ha.conf");

        TableViewer cubridHAConfTabTableViewer =
                new TableViewer(
                        comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        cubridHAConfTabTableViewer
                .getTable()
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        cubridHAConfTabTableViewer.getTable().setHeaderVisible(true);
        cubridHAConfTabTableViewer.getTable().setLinesVisible(true);
        cubridHAConfTabTableViewer.setUseHashlookup(true);

        // create column
        TableViewerColumn column = new TableViewerColumn(cubridHAConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(0);
        column.getColumn().setText("");
        //
        //		brokerConfTabTableViewer.setContentProvider(new BrokerConfTableContentProvider());
        //		brokerConfTabTableViewer.setLabelProvider(new BrokerConfTableLabelProvider());
        //		brokerConfTabTableViewer.setSorter(new BrokerConfTableViewerSorter());

        tableCTabItem.setControl(comp);
        if (confTabFolder.getSelection() == null) {
            confTabFolder.setSelection(tableCTabItem);
        }
    }

    /** create cubrid ACL table */
    public void createCubridACLConfTabItem() {
        Composite comp = new Composite(confTabFolder, SWT.NONE);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        CTabItem tableCTabItem = new CTabItem(confTabFolder, SWT.NONE);
        tableCTabItem.setText("acl.conf");

        TableViewer cubridACLConfTabTableViewer =
                new TableViewer(
                        comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        cubridACLConfTabTableViewer
                .getTable()
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        cubridACLConfTabTableViewer.getTable().setHeaderVisible(true);
        cubridACLConfTabTableViewer.getTable().setLinesVisible(true);
        cubridACLConfTabTableViewer.setUseHashlookup(true);

        // create column
        TableViewerColumn column = new TableViewerColumn(cubridACLConfTabTableViewer, SWT.LEFT);
        column.getColumn().setWidth(0);
        column.getColumn().setText("");
        //
        //		brokerConfTabTableViewer.setContentProvider(new BrokerConfTableContentProvider());
        //		brokerConfTabTableViewer.setLabelProvider(new BrokerConfTableLabelProvider());
        //		brokerConfTabTableViewer.setSorter(new BrokerConfTableViewerSorter());

        tableCTabItem.setControl(comp);
        if (confTabFolder.getSelection() == null) {
            confTabFolder.setSelection(tableCTabItem);
        }
    }

    /** register CubridConfTable context menu */
    private void registerCubridConfTableContextMenu() {
        cubridConfTabTableViewer
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(cubridConfTabTableViewer.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(cubridConfTabTableViewer.getTable());
        cubridConfTabTableViewer.getTable().setMenu(contextMenu);

        Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
        final MenuItem itemEditAnnotation = new MenuItem(menu, SWT.PUSH);
        itemEditAnnotation.setText(Messages.confEditorTableMenuEditAnnotation);
        itemEditAnnotation.addSelectionListener(
                new SelectionAdapter() {
                    @SuppressWarnings("all")
                    public void widgetSelected(SelectionEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = cubridConfTableClickPoint;
                        int selectIndex = cubridConfTabTableViewer.getTable().getSelectionIndex();
                        final TableItem item =
                                cubridConfTabTableViewer.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 0;
                                i < cubridConfTabTableViewer.getTable().getColumnCount();
                                i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                if (i == 0) {
                                    CommonUITool.openErrorBox(
                                            getSite().getShell(),
                                            Messages.annotationDialogOpenErrorMsg);
                                    return;
                                }
                                IStructuredSelection selection =
                                        (IStructuredSelection)
                                                cubridConfTabTableViewer.getSelection();
                                HashMap<String, String> valueMap =
                                        (HashMap<String, String>) selection.getFirstElement();
                                String serverName = cubridConfConfigListData.get(0).get(i + "");
                                String parentPropertyKey = valueMap.get("0");
                                String parentKey = " ";
                                Map<String, String> cubridMap = cubridConfConfigListData.get(1);
                                String cubridName = "";
                                if (cubridMap != null) {
                                    cubridName = cubridMap.get(i + "");
                                }
                                if (selectIndex == 0) {
                                    parentKey += serverName;
                                } else {
                                    if (selectIndex == 1) {
                                        parentKey += serverName + "->" + cubridName;
                                    } else {
                                        parentKey +=
                                                serverName
                                                        + "->"
                                                        + cubridName
                                                        + "->"
                                                        + parentPropertyKey;
                                    }
                                }
                                String annotationKey =
                                        Integer.toString(i) + BrokerConfPersistUtil.ANNOTATION;
                                CubridBrokerConfEditAnnotationDialog dialog =
                                        new CubridBrokerConfEditAnnotationDialog(
                                                getSite().getShell(),
                                                parentKey,
                                                annotationKey,
                                                valueMap);
                                if (IDialogConstants.OK_ID == dialog.open()) {
                                    setDirty(true);
                                }
                            }
                        }
                    }
                });

        final MenuItem itemAddCubridConf = new MenuItem(menu, SWT.PUSH);
        itemAddCubridConf.setText(Messages.unifyHostConfigEditorAddCubridConfColumn);
        itemAddCubridConf.setImage(CommonUIPlugin.getImage("icons/action/column_insert.png"));
        itemAddCubridConf.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            ProgressMonitorDialog progress =
                                    new ProgressMonitorDialog(
                                            Display.getCurrent().getActiveShell());
                            progress.setCancelable(true);
                            progress.run(
                                    true,
                                    true,
                                    new IRunnableWithProgress() {
                                        public void run(final IProgressMonitor monitor)
                                                throws InvocationTargetException {
                                            Display.getDefault()
                                                    .asyncExec(
                                                            new Runnable() {
                                                                public void run() {
                                                                    //									int
                                                                    // horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y -
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    //									 =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    monitor.beginTask(
                                                                            Messages
                                                                                    .unifyHostConfigEditorAddColumnMsg,
                                                                            1);
                                                                    addCubridConfColumn();
                                                                    monitor.worked(1);
                                                                    //
                                                                    //	horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y - horizontalSelectionInt;
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().setSelection(horizontalSelectionInt + 160);

                                                                }
                                                            });
                                        }
                                    });
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                        setDirty(true);
                    }
                });

        final MenuItem itemDelCubridConf = new MenuItem(menu, SWT.PUSH);
        itemDelCubridConf.setText(Messages.unifyHostConfigEditorDelCubridConfColumn);
        itemDelCubridConf.setImage(CommonUIPlugin.getImage("icons/action/column_delete.png"));
        itemDelCubridConf.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            ProgressMonitorDialog progress =
                                    new ProgressMonitorDialog(
                                            Display.getCurrent().getActiveShell());
                            progress.setCancelable(true);
                            progress.run(
                                    true,
                                    true,
                                    new IRunnableWithProgress() {
                                        public void run(final IProgressMonitor monitor)
                                                throws InvocationTargetException {
                                            Display.getDefault()
                                                    .asyncExec(
                                                            new Runnable() {
                                                                public void run() {
                                                                    //									int
                                                                    // horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y -
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    //									 =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    monitor.beginTask(
                                                                            Messages
                                                                                    .unifyHostConfigEditorDelColumnMsg,
                                                                            1);
                                                                    delCubridConfColumn();
                                                                    monitor.worked(1);
                                                                    //
                                                                    //	horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y - horizontalSelectionInt;
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().setSelection(horizontalSelectionInt + 160);

                                                                }
                                                            });
                                        }
                                    });
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                    }
                });

        menu.addMenuListener(
                new MenuAdapter() {
                    public void menuShown(MenuEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = cubridConfTableClickPoint;

                        // click timing
                        if (System.currentTimeMillis() - cubridConfTableClickPointTiming > 300) {
                            itemEditAnnotation.setEnabled(false);
                            itemDelCubridConf.setEnabled(false);
                            itemAddCubridConf.setEnabled(false);
                            return;
                        }

                        int selectIndex = cubridConfTabTableViewer.getTable().getSelectionIndex();
                        if (selectIndex == -1) {
                            itemEditAnnotation.setEnabled(false);
                            itemDelCubridConf.setEnabled(false);
                            itemAddCubridConf.setEnabled(false);
                            return;
                        }
                        if (selectIndex == 0) {
                            itemEditAnnotation.setEnabled(false);
                            itemDelCubridConf.setEnabled(false);
                            itemAddCubridConf.setEnabled(true);
                            return;
                        }

                        final TableItem item =
                                cubridConfTabTableViewer.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 0;
                                i < cubridConfTabTableViewer.getTable().getColumnCount();
                                i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                if (i == 0) {
                                    itemEditAnnotation.setEnabled(false);
                                    itemDelCubridConf.setEnabled(false);
                                } else {
                                    itemEditAnnotation.setEnabled(true);
                                    itemDelCubridConf.setEnabled(true);
                                }
                            }
                        }
                        itemAddCubridConf.setEnabled(true);
                    }
                });
        cubridConfTabTableViewer.getTable().setMenu(menu);
    }

    /** register CubridCMConfTable context menu */
    private void registerCubridCMConfTableContextMenu() {
        cubridCMConfTabTableViewer
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(cubridCMConfTabTableViewer.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(cubridCMConfTabTableViewer.getTable());
        cubridCMConfTabTableViewer.getTable().setMenu(contextMenu);

        Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
        final MenuItem itemEditAnnotation = new MenuItem(menu, SWT.PUSH);
        itemEditAnnotation.setText(Messages.confEditorTableMenuEditAnnotation);
        itemEditAnnotation.addSelectionListener(
                new SelectionAdapter() {
                    @SuppressWarnings("all")
                    public void widgetSelected(SelectionEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = cubridCMConfTableClickPoint;
                        int selectIndex = cubridCMConfTabTableViewer.getTable().getSelectionIndex();
                        final TableItem item =
                                cubridCMConfTabTableViewer.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 0;
                                i < cubridCMConfTabTableViewer.getTable().getColumnCount();
                                i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                if (i == 0) {
                                    CommonUITool.openErrorBox(
                                            getSite().getShell(),
                                            Messages.annotationDialogOpenErrorMsg);
                                    return;
                                }
                                IStructuredSelection selection =
                                        (IStructuredSelection)
                                                cubridCMConfTabTableViewer.getSelection();
                                HashMap<String, String> valueMap =
                                        (HashMap<String, String>) selection.getFirstElement();
                                String serverName = cubridConfCMConfigListData.get(0).get(i + "");
                                String parentPropertyKey = valueMap.get("0");
                                String parentKey = " ";
                                Map<String, String> cubridMap = cubridConfCMConfigListData.get(1);
                                String cubridName = "";
                                if (cubridMap != null) {
                                    cubridName = cubridMap.get(i + "");
                                }
                                if (selectIndex == 0) {
                                    parentKey += serverName;
                                } else {
                                    if (selectIndex == 1) {
                                        parentKey += serverName + "->" + cubridName;
                                    } else {
                                        parentKey +=
                                                serverName
                                                        + "->"
                                                        + cubridName
                                                        + "->"
                                                        + parentPropertyKey;
                                    }
                                }
                                String annotationKey =
                                        Integer.toString(i) + BrokerConfPersistUtil.ANNOTATION;
                                CubridBrokerConfEditAnnotationDialog dialog =
                                        new CubridBrokerConfEditAnnotationDialog(
                                                getSite().getShell(),
                                                parentKey,
                                                annotationKey,
                                                valueMap);
                                if (IDialogConstants.OK_ID == dialog.open()) {
                                    setDirty(true);
                                }
                            }
                        }
                    }
                });

        menu.addMenuListener(
                new MenuAdapter() {
                    public void menuShown(MenuEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = cubridCMConfTableClickPoint;

                        // click timing
                        if (System.currentTimeMillis() - cubridCMConfTableClickPointTiming > 300) {
                            itemEditAnnotation.setEnabled(false);
                            return;
                        }

                        int selectIndex = cubridCMConfTabTableViewer.getTable().getSelectionIndex();
                        if (selectIndex == -1) {
                            itemEditAnnotation.setEnabled(false);
                            return;
                        }
                        if (selectIndex == 0) {
                            itemEditAnnotation.setEnabled(false);
                            return;
                        }

                        final TableItem item =
                                cubridCMConfTabTableViewer.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 0;
                                i < cubridCMConfTabTableViewer.getTable().getColumnCount();
                                i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                if (i == 0) {
                                    itemEditAnnotation.setEnabled(false);
                                } else {
                                    itemEditAnnotation.setEnabled(true);
                                }
                            }
                        }
                    }
                });
        cubridCMConfTabTableViewer.getTable().setMenu(menu);
    }

    /** register CubridConfTable context menu */
    private void registerCubridBrokerTableContextMenu() {
        brokerConfTabTableViewer
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(brokerConfTabTableViewer.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(brokerConfTabTableViewer.getTable());
        brokerConfTabTableViewer.getTable().setMenu(contextMenu);

        Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
        final MenuItem itemEditAnnotation = new MenuItem(menu, SWT.PUSH);
        itemEditAnnotation.setText(Messages.confEditorTableMenuEditAnnotation);
        itemEditAnnotation.addSelectionListener(
                new SelectionAdapter() {
                    @SuppressWarnings("all")
                    public void widgetSelected(SelectionEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = cubridBrokerTableClickPoint;
                        int selectIndex = brokerConfTabTableViewer.getTable().getSelectionIndex();
                        final TableItem item =
                                brokerConfTabTableViewer.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 0;
                                i < brokerConfTabTableViewer.getTable().getColumnCount();
                                i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                if (i == 0) {
                                    CommonUITool.openErrorBox(
                                            getSite().getShell(),
                                            Messages.annotationDialogOpenErrorMsg);
                                    return;
                                }
                                IStructuredSelection selection =
                                        (IStructuredSelection)
                                                brokerConfTabTableViewer.getSelection();
                                HashMap<String, String> valueMap =
                                        (HashMap<String, String>) selection.getFirstElement();

                                String serverName = cubridBrokerConfListData.get(0).get(i + "");
                                String parentPropertyKey = valueMap.get("0");
                                String parentKey = " ";
                                Map<String, String> brokerMap = cubridBrokerConfListData.get(1);
                                String brokerName = "";
                                if (brokerMap != null) {
                                    brokerName = brokerMap.get(i + "");
                                }
                                if (selectIndex == 0) {
                                    parentKey += serverName;
                                } else {
                                    if (selectIndex == 1) {
                                        parentKey += serverName + "->" + brokerName;
                                    } else {
                                        parentKey +=
                                                serverName
                                                        + "->"
                                                        + brokerName
                                                        + "->"
                                                        + parentPropertyKey;
                                    }
                                }

                                String annotationKey =
                                        Integer.toString(i) + BrokerConfPersistUtil.ANNOTATION;
                                CubridBrokerConfEditAnnotationDialog dialog =
                                        new CubridBrokerConfEditAnnotationDialog(
                                                getSite().getShell(),
                                                parentKey,
                                                annotationKey,
                                                valueMap);
                                if (IDialogConstants.OK_ID == dialog.open()) {
                                    setDirty(true);
                                }
                            }
                        }
                    }
                });

        final MenuItem itemAddBrokerConf = new MenuItem(menu, SWT.PUSH);
        itemAddBrokerConf.setText(
                com.cubrid.common.ui.common.Messages.cubridBrokerConfEditorAddBrokerConfItemLabel);
        itemAddBrokerConf.setImage(CommonUIPlugin.getImage("icons/action/column_insert.png"));
        itemAddBrokerConf.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            ProgressMonitorDialog progress =
                                    new ProgressMonitorDialog(
                                            Display.getCurrent().getActiveShell());
                            progress.setCancelable(true);
                            progress.run(
                                    true,
                                    true,
                                    new IRunnableWithProgress() {
                                        public void run(final IProgressMonitor monitor)
                                                throws InvocationTargetException {
                                            Display.getDefault()
                                                    .asyncExec(
                                                            new Runnable() {
                                                                public void run() {
                                                                    //									int
                                                                    // horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y -
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    //									 =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    monitor.beginTask(
                                                                            Messages
                                                                                    .unifyHostConfigEditorAddColumnMsg,
                                                                            1);
                                                                    addBrokerConfColumn();
                                                                    monitor.worked(1);
                                                                    //
                                                                    //	horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y - horizontalSelectionInt;
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().setSelection(horizontalSelectionInt + 160);

                                                                }
                                                            });
                                        }
                                    });
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }

                        setDirty(true);
                    }
                });

        final MenuItem itemDeleteBrokerConf = new MenuItem(menu, SWT.PUSH);
        itemDeleteBrokerConf.setText(
                com.cubrid.common.ui.common.Messages
                        .cubridBrokerConfEditorDeleteBrokerConfItemLabel);
        itemDeleteBrokerConf.setImage(CommonUIPlugin.getImage("icons/action/column_delete.png"));
        itemDeleteBrokerConf.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            ProgressMonitorDialog progress =
                                    new ProgressMonitorDialog(
                                            Display.getCurrent().getActiveShell());
                            progress.setCancelable(true);
                            progress.run(
                                    true,
                                    true,
                                    new IRunnableWithProgress() {
                                        public void run(final IProgressMonitor monitor)
                                                throws InvocationTargetException {
                                            Display.getDefault()
                                                    .asyncExec(
                                                            new Runnable() {
                                                                public void run() {
                                                                    //									int
                                                                    // horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y -
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    //									 =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSelection();
                                                                    monitor.beginTask(
                                                                            Messages
                                                                                    .unifyHostConfigEditorDelColumnMsg,
                                                                            1);
                                                                    delBrokerConfColumn();
                                                                    monitor.worked(1);
                                                                    //
                                                                    //	horizontalSelectionInt =
                                                                    // brokerConfTabTableViewer.getTable().getHorizontalBar().getSize().y - horizontalSelectionInt;
                                                                    //
                                                                    //	brokerConfTabTableViewer.getTable().getHorizontalBar().setSelection(horizontalSelectionInt + 160);

                                                                }
                                                            });
                                        }
                                    });
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }

                        setDirty(true);
                    }
                });

        menu.addMenuListener(
                new MenuAdapter() {
                    public void menuShown(MenuEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = cubridBrokerTableClickPoint;

                        // click timing
                        if (System.currentTimeMillis() - cubridBrokerTableClickPointTiming > 300) {
                            itemEditAnnotation.setEnabled(false);
                            itemDeleteBrokerConf.setEnabled(false);
                            itemAddBrokerConf.setEnabled(false);
                            return;
                        }

                        int selectIndex = brokerConfTabTableViewer.getTable().getSelectionIndex();
                        if (selectIndex == -1) {
                            itemEditAnnotation.setEnabled(false);
                            itemDeleteBrokerConf.setEnabled(false);
                            itemAddBrokerConf.setEnabled(false);
                            return;
                        }
                        if (selectIndex == 0) {
                            itemEditAnnotation.setEnabled(false);
                            itemDeleteBrokerConf.setEnabled(false);
                            itemAddBrokerConf.setEnabled(true);
                            return;
                        }

                        final TableItem item =
                                brokerConfTabTableViewer.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 0;
                                i < brokerConfTabTableViewer.getTable().getColumnCount();
                                i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                if (i == 0) {
                                    itemEditAnnotation.setEnabled(false);
                                    itemDeleteBrokerConf.setEnabled(false);
                                } else {
                                    itemEditAnnotation.setEnabled(true);
                                    itemDeleteBrokerConf.setEnabled(true);
                                }
                            }
                        }
                        itemAddBrokerConf.setEnabled(true);
                    }
                });
        brokerConfTabTableViewer.getTable().setMenu(menu);
    }

    /** addBrokerConfColumn */
    public void addBrokerConfColumn() {

        editorInput.setBrokerConfPropertyCount(
                brokerConfTabTableViewer.getTable().getColumnCount());
        LinkedHashMap<String, BrokerConfig> brokerConfMap =
                unifyHostConfigUtil.parseCommonTableValueToCubridBrokerConfig(
                        cubridBrokerConfListData, editorInput.getBrokerConfPropertyCount());

        Point pt = cubridBrokerTableClickPoint;
        int selectIndex = brokerConfTabTableViewer.getTable().getSelectionIndex();
        if (selectIndex < 0) {
            return;
        }
        final TableItem item = brokerConfTabTableViewer.getTable().getItem(selectIndex);
        if (item == null) {
            return;
        }
        for (int i = 0; i < brokerConfTabTableViewer.getTable().getColumnCount(); i++) {
            Rectangle rect = item.getBounds(i);
            if (rect.contains(pt)) {
                cubridBrokerConfListData.clear();
                cubridBrokerConfListData.addAll(
                        unifyHostConfigUtil.parseCubridBrokerConfigToCommonTableValue(
                                cubridBrokerConfigDataMap));

                String serverName = cubridBrokerConfListData.get(0).get(i + "");
                if (!CommonUITool.openConfirmBox(
                        Messages.bind(
                                Messages.unifyHostConfigEditorAddColumnConfirmMsg,
                                "broker",
                                serverName))) {

                    return;
                }
                BrokerConfig cubridBrokerConfig = brokerConfMap.get(serverName);
                CubridBrokerProperty property =
                        new CubridBrokerProperty(); // new property then set parameter
                property.setCubridBrokerPropKey(
                        "[%broker" + (cubridBrokerConfig.getPropertyList().size() + 1) + "]");
                property.setCubridBroker(true);
                property.setCubridBrokerPropAnnotation(StringUtil.NEWLINE);
                cubridBrokerConfig.addCubridBrokerProperty(property);

                cubridBrokerConfigDataMap.clear();
                cubridBrokerConfigDataMap.putAll(brokerConfMap);

                createBrokerConfTableData();
                setDirty(true);
                return;
            }
        }
    }

    /** delBrokerConfColumn */
    public void delBrokerConfColumn() {

        editorInput.setBrokerConfPropertyCount(
                brokerConfTabTableViewer.getTable().getColumnCount());
        LinkedHashMap<String, BrokerConfig> brokerConfMap =
                unifyHostConfigUtil.parseCommonTableValueToCubridBrokerConfig(
                        cubridBrokerConfListData, editorInput.getBrokerConfPropertyCount());

        Point pt = cubridBrokerTableClickPoint;
        int selectIndex = brokerConfTabTableViewer.getTable().getSelectionIndex();
        if (selectIndex < 0) {
            return;
        }
        final TableItem item = brokerConfTabTableViewer.getTable().getItem(selectIndex);
        if (item == null) {
            return;
        }
        for (int i = 0; i < brokerConfTabTableViewer.getTable().getColumnCount(); i++) {
            Rectangle rect = item.getBounds(i);
            if (rect.contains(pt)) {
                cubridBrokerConfListData.clear();
                cubridBrokerConfListData.addAll(
                        unifyHostConfigUtil.parseCubridBrokerConfigToCommonTableValue(
                                cubridBrokerConfigDataMap));

                String serverName = cubridBrokerConfListData.get(0).get(i + "");
                String brokerName = cubridBrokerConfListData.get(1).get(i + "");
                if (!CommonUITool.openConfirmBox(
                        Messages.bind(
                                Messages.unifyHostConfigEditorDelColumnConfirmMsg,
                                brokerName,
                                serverName))) {

                    return;
                }
                BrokerConfig cubridBrokerConfig = brokerConfMap.get(serverName);
                cubridBrokerConfUtil.deleteBrokerPropertyByBrokerName(
                        cubridBrokerConfig, brokerName);

                cubridBrokerConfigDataMap.clear();
                cubridBrokerConfigDataMap.putAll(brokerConfMap);

                createBrokerConfTableData();
                setDirty(true);
                return;
            }
        }
    }

    /** addCubridConfColumn */
    public void addCubridConfColumn() {

        editorInput.setCubridConfPropertyCount(
                cubridConfTabTableViewer.getTable().getColumnCount());
        LinkedHashMap<String, CubridConfConfig> cubridConfMap =
                unifyHostConfigUtil.parseCommonTableValueToCubridConfConfig(
                        cubridConfConfigListData, editorInput.getCubridConfPropertyCount());

        Point pt = cubridConfTableClickPoint;
        int selectIndex = cubridConfTabTableViewer.getTable().getSelectionIndex();
        if (selectIndex < 0) {
            return;
        }
        final TableItem item = cubridConfTabTableViewer.getTable().getItem(selectIndex);
        if (item == null) {
            return;
        }
        for (int i = 0; i < cubridConfTabTableViewer.getTable().getColumnCount(); i++) {
            Rectangle rect = item.getBounds(i);
            if (rect.contains(pt)) {
                cubridConfConfigListData.clear();
                cubridConfConfigListData.addAll(
                        unifyHostConfigUtil.parseCubridConfConfigToCommonTableValue(
                                cubridConfConfigDataMap));

                String serverName = cubridConfConfigListData.get(0).get(i + "");
                if (!CommonUITool.openConfirmBox(
                        Messages.bind(
                                Messages.unifyHostConfigEditorAddColumnConfirmMsg,
                                "section",
                                serverName))) {

                    return;
                }

                CubridConfConfig cubridConfConfig = cubridConfMap.get(serverName);
                CubridConfProperty property =
                        new CubridConfProperty(); // new property then set parameter
                property.setCubridConfPropKey(
                        "[%section" + (cubridConfConfig.getPropertyList().size() + 1) + "]");
                property.setCubridConf(true);
                property.setCubridConfPropAnnotation(StringUtil.NEWLINE);
                cubridConfConfig.addCubridConfProperty(property);

                cubridConfConfigDataMap.clear();
                cubridConfConfigDataMap.putAll(cubridConfMap);

                createCubridConfTableData();
                setDirty(true);
                return;
            }
        }
    }

    /** delCubridConfColumn */
    public void delCubridConfColumn() {

        editorInput.setCubridConfPropertyCount(
                cubridConfTabTableViewer.getTable().getColumnCount());
        LinkedHashMap<String, CubridConfConfig> cubridConfMap =
                unifyHostConfigUtil.parseCommonTableValueToCubridConfConfig(
                        cubridConfConfigListData, editorInput.getCubridConfPropertyCount());

        Point pt = cubridConfTableClickPoint;
        int selectIndex = cubridConfTabTableViewer.getTable().getSelectionIndex();
        if (selectIndex < 0) {
            return;
        }
        final TableItem item = cubridConfTabTableViewer.getTable().getItem(selectIndex);
        if (item == null) {
            return;
        }
        for (int i = 0; i < cubridConfTabTableViewer.getTable().getColumnCount(); i++) {
            Rectangle rect = item.getBounds(i);
            if (rect.contains(pt)) {
                cubridConfConfigListData.clear();
                cubridConfConfigListData.addAll(
                        unifyHostConfigUtil.parseCubridConfConfigToCommonTableValue(
                                cubridConfConfigDataMap));

                String serverName = cubridConfConfigListData.get(0).get(i + "");
                String sectionName = cubridConfConfigListData.get(1).get(i + "");
                if (!CommonUITool.openConfirmBox(
                        Messages.bind(
                                Messages.unifyHostConfigEditorDelColumnConfirmMsg,
                                sectionName,
                                serverName))) {

                    return;
                }
                CubridConfConfig cubridConfConfig = cubridConfMap.get(serverName);
                unifyHostConfigUtil.deleteCubridConfPropertyBySectionName(
                        cubridConfConfig, sectionName);

                cubridConfConfigDataMap.clear();
                cubridConfConfigDataMap.putAll(cubridConfMap);

                createCubridConfTableData();
                setDirty(true);
                return;
            }
        }
    }

    /**
     * PropValueEditingSupport
     *
     * @author fulei
     */
    public class PropValueEditingSupport extends EditingSupport {
        private TableViewer tableViewer;
        private TextCellEditor propTextCellEditor;
        private final int columnIndex;

        public PropValueEditingSupport(TableViewer tableViewer, int columnIndex) {
            super(tableViewer);
            this.tableViewer = tableViewer;
            this.columnIndex = columnIndex;
        }

        /**
         * canEdit
         *
         * @param element Object
         * @return boolean
         */
        @SuppressWarnings("all")
        protected boolean canEdit(Object element) {
            if (element instanceof HashMap) {
                HashMap<String, String> valueMap = (HashMap<String, String>) element;
                if ((valueMap.get("0").equals(BROKERNAMECOLUMNTITLE)
                                || valueMap.get("0").equals(SERVERNAMECOLUMNTITLE)
                                || valueMap.get("0").equals(CUBRIDNAMECOLUMNTITLE))
                        && columnIndex == 0) {
                    return false;
                }
            }
            if (tableViewer.getTable().getSelectionIndex() == 0) {
                return false;
            }
            return true;
        }

        /**
         * getCellEditor
         *
         * @param element Object
         * @return CellEditor
         */
        protected CellEditor getCellEditor(Object element) {
            // normal type use textCellEditor
            if (propTextCellEditor == null) {
                propTextCellEditor = new TextCellEditor(tableViewer.getTable());
                propTextCellEditor.addListener(
                        new ICellEditorListener() {

                            public void applyEditorValue() {}

                            public void cancelEditor() {}

                            public void editorValueChanged(
                                    boolean oldValidState, boolean newValidState) {}
                        });
            }
            return propTextCellEditor;
        }

        /**
         * getValue
         *
         * @param element Object
         * @return Object
         */
        @SuppressWarnings("all")
        protected Object getValue(Object element) {
            if (element instanceof HashMap) {
                HashMap<String, String> valueMap = (HashMap<String, String>) element;
                String value = valueMap.get(Integer.toString(columnIndex));
                return value == null ? "" : value;
            }
            return null;
        }

        /**
         * setValue
         *
         * @param element Object
         * @param value Object
         */
        @SuppressWarnings("all")
        protected void setValue(Object element, Object value) {
            if (element instanceof HashMap && value != null) {
                HashMap<String, String> valueMap = (HashMap<String, String>) element;
                String oldValue = valueMap.get(Integer.toString(columnIndex));
                oldValue = oldValue == null ? "" : oldValue;
                if (value != null && (!value.equals(oldValue))) {
                    setDirty(true);
                }
                valueMap.put(Integer.toString(columnIndex), (String) value);
            }
            tableViewer.refresh();
        }
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /**
     * check whether a prop is a uniform prop
     *
     * @param propName
     * @return
     */
    public boolean isUniformBrokerProp(String propName) {
        for (String uniformPropName : BrokerConfPersistUtil.UNIFORMCONFIG) {
            if (uniformPropName.equalsIgnoreCase(propName)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        editorInput = (UnifyHostConfigEditorInput) input;
        editorInput.getCubridServers();
        setPartName(Messages.unifyHostConfigEditorTitle);
    }

    public void nodeChanged(CubridNodeChangedEvent event) {}

    public void doSave(IProgressMonitor monitor) {
        final HashMap<String, List<String>> failedConfMap = new HashMap<String, List<String>>();
        if (editorInput.isEditCubridConf()) {
            editorInput.setCubridConfPropertyCount(
                    cubridConfTabTableViewer.getTable().getColumnCount());
        }
        if (editorInput.isEditCMConf()) {
            editorInput.setCubridCMConfPropertyCount(
                    cubridCMConfTabTableViewer.getTable().getColumnCount());
        }
        if (editorInput.isEditBrokerConf()) {
            editorInput.setBrokerConfPropertyCount(
                    brokerConfTabTableViewer.getTable().getColumnCount());
        }

        try {
            ProgressMonitorDialog progress = new ProgressMonitorDialog(getSite().getShell());
            progress.setCancelable(false);
            progress.run(
                    true,
                    true,
                    new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) throws InvocationTargetException {
                            monitor.beginTask(
                                    Messages.unifyHostConfigEditorSavingDataMsg,
                                    editorInput.getTaskCountValue());

                            if (editorInput.isEditCubridConf()) {
                                LinkedHashMap<String, CubridConfConfig> cubridConfMap =
                                        unifyHostConfigUtil.parseCommonTableValueToCubridConfConfig(
                                                cubridConfConfigListData,
                                                editorInput.getCubridConfPropertyCount());
                                List<String> failedcubridConfServer =
                                        unifyHostConfigUtil.saveCubridConf(
                                                monitor,
                                                cubridConfMap,
                                                editorInput.getCubridServers());
                                if (failedcubridConfServer.size() > 0) {
                                    failedConfMap.put("cubrid.conf", failedcubridConfServer);
                                }
                            }

                            if (editorInput.isEditCMConf()) {
                                LinkedHashMap<String, CubridCMConfConfig> cubridCMConfMap =
                                        unifyHostConfigUtil
                                                .parseCommonTableValueToCubridCMConfConfig(
                                                        cubridConfCMConfigListData,
                                                        editorInput.getCubridCMConfPropertyCount());
                                List<String> failedcubridConfServer =
                                        unifyHostConfigUtil.saveCubridCMConf(
                                                monitor,
                                                cubridCMConfMap,
                                                editorInput.getCubridServers());
                                if (failedcubridConfServer.size() > 0) {
                                    failedConfMap.put("cm.conf", failedcubridConfServer);
                                }
                            }

                            if (editorInput.isEditBrokerConf()) {
                                LinkedHashMap<String, BrokerConfig> brokerConfMap =
                                        unifyHostConfigUtil
                                                .parseCommonTableValueToCubridBrokerConfig(
                                                        cubridBrokerConfListData,
                                                        editorInput.getBrokerConfPropertyCount());
                                List<String> failedBrokerConfServer =
                                        unifyHostConfigUtil.saveBrokerConf(
                                                monitor,
                                                brokerConfMap,
                                                editorInput.getCubridServers());
                                if (failedBrokerConfServer.size() > 0) {
                                    failedConfMap.put("cubrid_broker.conf", failedBrokerConfServer);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        //		String errMsg = null;
        //		if (errMsg != null) {
        //			CommonUITool.openErrorBox(errMsg);
        //			return;
        //		}
        if (failedConfMap.size() == 0) {
            CommonUITool.openInformationBox(Messages.unifyHostConfigEditorSavingDataSuccessMsg);
        } else {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, List<String>> entry : failedConfMap.entrySet()) {
                sb.append(entry.getKey())
                        .append(" on ")
                        .append(entry.getValue())
                        .append(StringUtil.NEWLINE);
            }
            CommonUITool.openInformationBox(
                    Messages.bind(Messages.unifyHostConfigEditorSavingErrMsg, sb.toString()));
        }
    }

    /**
     * validate
     *
     * @return error messages
     */
    public String validateBrokerConf() {

        List<String> nameList = new ArrayList<String>();
        // check duplicate property name
        for (int i = 0; i < cubridBrokerConfListData.size(); i++) {
            Map<String, String> valueMap = cubridBrokerConfListData.get(i);
            String propName = valueMap.get("0");
            if (null == propName || "".equals(propName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg, BROKERCONFCONFIGFILENAME);
            }
            if (nameList.contains(propName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg1,
                        BROKERCONFCONFIGFILENAME,
                        propName);
            }
            nameList.add(propName);
        }
        nameList.clear();

        // check duplicate broker name
        HashMap<String, List<String>> serverBrokerMap = new HashMap<String, List<String>>();
        // check duplicate broker name
        Map<String, String> serverMap = cubridBrokerConfListData.get(0);
        Map<String, String> brokerMap = cubridBrokerConfListData.get(1);
        for (int i = 1; i < brokerConfTabTableViewer.getTable().getColumnCount(); i++) {
            String serverName = serverMap.get(Integer.toString(i));
            String brokerName = brokerMap.get(Integer.toString(i));
            List<String> brokerList = serverBrokerMap.get(serverName);
            if (brokerList == null) {
                brokerList = new ArrayList<String>();
                serverBrokerMap.put(serverName, brokerList);
            }
            if (null == brokerName || "".equals(brokerName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg2,
                        BROKERCONFCONFIGFILENAME,
                        serverName);
            }
            if (brokerList.contains(brokerName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg3,
                        BROKERCONFCONFIGFILENAME,
                        serverName + " -> " + brokerName);
            }
            brokerList.add(brokerName);
        }

        return null;
    }

    /**
     * validate
     *
     * @return error messages
     */
    public String validateCubridConf() {

        List<String> nameList = new ArrayList<String>();
        // check duplicate property name
        for (int i = 0; i < cubridConfConfigListData.size(); i++) {
            Map<String, String> valueMap = cubridConfConfigListData.get(i);
            String propName = valueMap.get("0");
            if (null == propName || "".equals(propName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg, CUBRIDCONFCONFIGFILENAME);
            }
            if (nameList.contains(propName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg1,
                        CUBRIDCONFCONFIGFILENAME,
                        propName);
            }
            nameList.add(propName);
        }
        nameList.clear();

        HashMap<String, List<String>> serverSectionMap = new HashMap<String, List<String>>();
        // check duplicate cubrid conf name
        Map<String, String> serverMap = cubridConfConfigListData.get(0);
        Map<String, String> sectionMap = cubridConfConfigListData.get(1);
        for (int i = 1; i < cubridConfTabTableViewer.getTable().getColumnCount(); i++) {
            String serverName = serverMap.get(Integer.toString(i));
            String sectionName = sectionMap.get(Integer.toString(i));
            List<String> sectionList = serverSectionMap.get(serverName);
            if (sectionList == null) {
                sectionList = new ArrayList<String>();
                serverSectionMap.put(serverName, sectionList);
            }
            if (null == sectionName || "".equals(sectionName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg4,
                        CUBRIDCONFCONFIGFILENAME,
                        serverName);
            }
            if (sectionList.contains(sectionName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg5,
                        CUBRIDCONFCONFIGFILENAME,
                        serverName + " -> " + sectionName);
            }
            sectionList.add(sectionName);
        }

        return null;
    }

    /**
     * validate
     *
     * @return error messages
     */
    public String validateCubridCMConf() {

        List<String> nameList = new ArrayList<String>();
        // check duplicate property name
        for (int i = 0; i < cubridConfCMConfigListData.size(); i++) {
            Map<String, String> valueMap = cubridConfCMConfigListData.get(i);
            String propName = valueMap.get("0");
            if (null == propName || "".equals(propName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg, CUBRIDCMCONFCONFIGFILENAME);
            }
            if (nameList.contains(propName)) {
                return Messages.bind(
                        Messages.unifyHostConfigEditorCheckErrMsg1,
                        CUBRIDCMCONFCONFIGFILENAME,
                        propName);
            }
            nameList.add(propName);
        }
        return null;
    }

    /**
     * validate
     *
     * @return
     */
    public String validate() {
        if (editorInput.isEditCubridConf()) {
            String errMsg = validateCubridConf();
            if (errMsg != null) {
                confTabFolder.setSelection(cubridConfTableCTabItem);
                return errMsg;
            }
        }
        if (editorInput.isEditCMConf()) {
            String errMsg = validateCubridCMConf();
            if (errMsg != null) {
                confTabFolder.setSelection(cubridCMConfTableCTabItem);
                return errMsg;
            }
        }
        if (editorInput.isEditBrokerConf()) {
            String errMsg = validateBrokerConf();
            if (errMsg != null) {
                confTabFolder.setSelection(brokerConfTableCTabItem);
                return errMsg;
            }
        }
        return null;
    }

    public void doSaveAs() {}

    public boolean isDirty() {
        return isDirty;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }
}
