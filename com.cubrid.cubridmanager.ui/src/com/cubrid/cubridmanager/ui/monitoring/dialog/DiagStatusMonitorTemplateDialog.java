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
package com.cubrid.cubridmanager.ui.monitoring.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo;
import com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.EnumTargetConfig;
import com.cubrid.cubridmanager.ui.monitoring.editor.TargetConfig;
import com.cubrid.cubridmanager.ui.monitoring.editor.TargetConfigMap;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A dialog on which the all kinds of diagnostic objects can be set
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-5-3 created by lizhiqiang
 */
public class DiagStatusMonitorTemplateDialog extends CMTitleAreaDialog {

    private static final String TARGETOBJ_GROUP = Messages.targetObjGroup;
    private static final String ADD_BTN_TXT = Messages.addBtnTxt;
    private static final String REMOVE_BTN_TXT = Messages.removeBtnTxt;
    private static final String TEMPLATE_GROUP = Messages.templateGroup;
    private static final String TEMPLATE_NAME = Messages.templateName;
    private static final String SAMPLE_TERM = Messages.sammpleTerm;
    private static final String TEMPLATE_DESC = Messages.templateDesc;
    private static final String TARGET_DB = Messages.targetDb;

    private static final String ADD_TITLE = Messages.addTitle;
    private static final String EDIT_TITLE = Messages.editTitle;
    private static final String ADD_MESSAGE = Messages.addMessage;
    private static final String EDIT_MESSAGE = Messages.editMessage;
    private static final String STATUS_MONITOR_LST = Messages.statusMonitorList;
    private static final String STATUS_MONITOR_LST_DB = Messages.statusMonitorListDb;
    private static final String STATUS_MONITOR_LST_BROKER = Messages.statusMonitorListBroker;
    private static final String DIAG_CATEGORY = Messages.diagCategory;
    private static final String DIAG_NAME = Messages.diagName;

    private static final String EMPTY_NAME_TXT = Messages.emptyNameTxt;
    private static final String HAS_SAME_NAME = Messages.hasSameName;
    private static final String ERROR_SAMPLE_TERMS_TXT = Messages.errsamplingTermsTxt;
    private static final String OVERLMT_SAMPLE_TERMS_TXT = Messages.overLmtSamplingTermsTx;
    private static final String EMPTY_DESC_TXT = Messages.emptyDescTxt;
    private static final String EMPTY_DB_TXT = Messages.emptyDbTxt;
    private static final String NO_SUCH_DB = Messages.noSuchDb;
    private static final String NO_TARGET_OBJECT = Messages.noTargetObj;
    private static final String NOPERMIT_MONITOR_DB = Messages.noPermitMonitorDb;

    private boolean execDiagChecked;
    private ICubridNode selection;
    private AddEditType operation;
    private ServerInfo serverInfo;

    private Combo targetDbNameCombo;
    private Text discriptionText;
    private Combo samplingTermscombo;
    private Text nameText;

    private Table tagetTbl;
    private Tree targetTree;

    private final String[] samplingTermsItems;
    private boolean isOkenable[] = new boolean[8];;
    private final StatusTemplateInfo statusTemplateInfo;
    private Button addBtn;

    public DiagStatusMonitorTemplateDialog(Shell parent) {
        super(parent);
        statusTemplateInfo = new StatusTemplateInfo();
        samplingTermsItems = new String[100];
        for (int i = 0; i < 100; i++) {
            samplingTermsItems[i] = Integer.toString(i + 1);
        }
    }

    /**
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        final Composite composite = new Composite(parentComp, SWT.RESIZE);
        final GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdComposite.widthHint = 640;
        composite.setLayoutData(gdComposite);
        final GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        gridLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        gridLayout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(gridLayout);

        createTargetGroup(composite);
        createTemplateGroup(composite);

        // initialize values
        init();
        // add template listener
        addTemplateListener();
        return parentComp;
    }

    /**
     * Creates target group
     *
     * @param composite The composite to contain the target group
     */
    private void createTargetGroup(Composite composite) {
        final Group group = new Group(composite, SWT.RESIZE);
        group.setLayout(new GridLayout(3, false));
        final GridData gdGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdGroup.heightHint = 280;
        group.setLayoutData(gdGroup);
        group.setText(TARGETOBJ_GROUP);

        targetTree = new Tree(group, SWT.BORDER | SWT.RESIZE);
        final GridData gdTargetTree = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdTargetTree.widthHint = 200;
        targetTree.setLayoutData(gdTargetTree);
        targetTree.addSelectionListener(new TreeSelectionEvent());
        // targetTree.addFocusListener(new TreeFocusListener());
        final Composite compositeButton = new Composite(group, SWT.NONE);
        compositeButton.setLayout(new GridLayout());

        addBtn = new Button(compositeButton, SWT.PUSH);
        addBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addBtn.setText(ADD_BTN_TXT);
        addBtn.setFont(JFaceResources.getDialogFont());

        final Button removeBtn = new Button(compositeButton, SWT.PUSH);
        removeBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeBtn.setText(REMOVE_BTN_TXT);
        removeBtn.setFont(JFaceResources.getDialogFont());

        final Composite compositeTable = new Composite(group, SWT.NONE);
        final GridData gdCompositeTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdCompositeTable.heightHint = 280;
        gdCompositeTable.widthHint = 300;
        compositeTable.setLayoutData(gdCompositeTable);
        compositeTable.setLayout(new GridLayout());

        tagetTbl = new Table(compositeTable, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        final GridData gdTagetTbl = new GridData(SWT.FILL, SWT.FILL, true, true);
        tagetTbl.setLayoutData(gdTagetTbl);
        tagetTbl.setLinesVisible(true);
        tagetTbl.setHeaderVisible(true);
        CommonUITool.hackForYosemite(tagetTbl);

        // initialize the sub lever content
        insertTreeObject();
        createTableSelectedItem();
        // Add the listeners

        addBtn.addSelectionListener(new AddSelectionAdapter());
        removeBtn.addSelectionListener(new RemoveSelectionAdapter());
    }

    /**
     * Creates template group
     *
     * @param composite The composite to contains the template group
     */
    private void createTemplateGroup(Composite composite) {
        final Group group = new Group(composite, SWT.RESIZE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setLayout(new GridLayout(4, false));
        group.setText(TEMPLATE_GROUP);

        final Label nameLabel = new Label(group, SWT.NONE);

        final GridData gdNameLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdNameLabel.widthHint = 90;
        nameLabel.setLayoutData(gdNameLabel);
        nameLabel.setText(TEMPLATE_NAME);

        nameText = new Text(group, SWT.BORDER);
        final GridData gdNameText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdNameText.widthHint = 60;
        nameText.setLayoutData(gdNameText);

        final Label samplingTermsLbl = new Label(group, SWT.NONE);
        final GridData gdSamplingTermsLbl = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdSamplingTermsLbl.widthHint = 130;
        samplingTermsLbl.setLayoutData(gdSamplingTermsLbl);
        samplingTermsLbl.setText(SAMPLE_TERM);

        samplingTermscombo = new Combo(group, SWT.NONE);
        final GridData gdSamplingTermscombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdSamplingTermscombo.widthHint = 60;
        samplingTermscombo.setLayoutData(gdSamplingTermscombo);
        samplingTermscombo.setItems(samplingTermsItems);
        samplingTermscombo.setText(samplingTermsItems[0]);

        final Label descriptionLabel = new Label(group, SWT.NONE);
        final GridData gdDescriptionLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdDescriptionLabel.widthHint = 90;
        descriptionLabel.setLayoutData(gdDescriptionLabel);
        descriptionLabel.setText(TEMPLATE_DESC);

        discriptionText = new Text(group, SWT.BORDER);
        final GridData gdDiscriptionText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdDiscriptionText.widthHint = 60;
        discriptionText.setLayoutData(gdDiscriptionText);

        ServerType serverType = serverInfo.getServerType();
        if (serverType == ServerType.BOTH || serverType == ServerType.DATABASE) {
            final Label targetDbNameLbl = new Label(group, SWT.NONE);
            final GridData gdTargetDbNameLbl = new GridData(SWT.CENTER, SWT.CENTER, false, false);
            gdTargetDbNameLbl.widthHint = 130;
            targetDbNameLbl.setLayoutData(gdTargetDbNameLbl);
            targetDbNameLbl.setText(TARGET_DB);

            targetDbNameCombo = new Combo(group, SWT.NULL);
            final GridData gdTargetDbNameCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gdTargetDbNameCombo.widthHint = 60;
            targetDbNameCombo.setLayoutData(gdTargetDbNameCombo);
        }
    }

    /** Inserts terms to tree */
    private void insertTreeObject() {
        TargetConfigMap targetConfigMap = TargetConfigMap.getInstance();
        EnumMap<EnumTargetConfig, TargetConfig> group = targetConfigMap.getMap();

        TreeItem root = new TreeItem(targetTree, SWT.NONE);
        root.setText(STATUS_MONITOR_LST);
        serverInfo = selection.getServer().getServerInfo();
        ServerType serverType = serverInfo.getServerType();
        TargetConfig tc = null;
        if (serverType == ServerType.BOTH || serverType == ServerType.DATABASE) {

            TreeItem db = new TreeItem(root, SWT.NONE);
            db.setText(STATUS_MONITOR_LST_DB);

            TreeItem serverQuery = new TreeItem(db, SWT.NONE);
            serverQuery.setText(targetConfigMap.getQueryCategory());
            TreeItem serverConn = new TreeItem(db, SWT.NONE);
            serverConn.setText(targetConfigMap.getConnCategory());
            TreeItem serverBuffer = new TreeItem(db, SWT.NONE);
            serverBuffer.setText(targetConfigMap.getBufferCategory());
            TreeItem serverLock = new TreeItem(db, SWT.NONE);
            serverLock.setText(targetConfigMap.getLockCategory());

            TreeItem queryOpenedPage = new TreeItem(serverQuery, SWT.NONE);
            TreeItem querySlowQuery = new TreeItem(serverQuery, SWT.NONE);
            TreeItem queryFullScan = new TreeItem(serverQuery, SWT.NONE);
            TreeItem connCliRequest = new TreeItem(serverConn, SWT.NONE);
            TreeItem connAbortedClients = new TreeItem(serverConn, SWT.NONE);
            TreeItem connConnReq = new TreeItem(serverConn, SWT.NONE);
            TreeItem connConnReject = new TreeItem(serverConn, SWT.NONE);
            TreeItem bufferPageWrite = new TreeItem(serverBuffer, SWT.NONE);
            TreeItem bufferPageRead = new TreeItem(serverBuffer, SWT.NONE);
            TreeItem lockDeadlock = new TreeItem(serverLock, SWT.NONE);
            TreeItem lockRequest = new TreeItem(serverLock, SWT.NONE);

            tc = group.get(EnumTargetConfig.SERVER_QUERY_OPENED_PAGE);
            queryOpenedPage.setData(tc);
            queryOpenedPage.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_QUERY_SLOW_QUERY);
            querySlowQuery.setData(tc);
            querySlowQuery.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_QUERY_FULL_SCAN);
            queryFullScan.setData(tc);
            queryFullScan.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_CONN_CLI_REQUEST);
            connCliRequest.setData(tc);
            connCliRequest.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_CONN_ABORTED_CLIENTS);
            connAbortedClients.setData(tc);
            connAbortedClients.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_CONN_CONN_REQ);
            connConnReq.setData(tc);
            connConnReq.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_CONN_CONN_REJ);
            connConnReject.setData(tc);
            connConnReject.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_BUFFER_PAGE_WRITE);
            bufferPageWrite.setData(tc);
            bufferPageWrite.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_BUFFER_PAGE_READ);
            bufferPageRead.setData(tc);
            bufferPageRead.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_LOCK_DEADLOCK);
            lockDeadlock.setData(tc);
            lockDeadlock.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.SERVER_LOCK_REQUEST);
            lockRequest.setData(tc);
            lockRequest.setText(tc.getDisplayName());

            db.setExpanded(true);
        }
        if (serverType == ServerType.BOTH || serverType == ServerType.BROKER) {
            TreeItem broker = new TreeItem(root, SWT.NONE);
            broker.setText(STATUS_MONITOR_LST_BROKER);

            TreeItem activeSession = new TreeItem(broker, SWT.NONE);
            TreeItem requestSec = new TreeItem(broker, SWT.NONE);
            TreeItem querySec = new TreeItem(broker, SWT.NONE);
            TreeItem transactionSec = new TreeItem(broker, SWT.NONE);
            TreeItem longQuery = new TreeItem(broker, SWT.NONE);
            TreeItem longTran = new TreeItem(broker, SWT.NONE);
            TreeItem errQuery = new TreeItem(broker, SWT.NONE);

            tc = group.get(EnumTargetConfig.CAS_ST_ACTIVE_SESSION);
            activeSession.setData(tc);
            activeSession.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.CAS_ST_REQUEST);
            requestSec.setData(tc);
            requestSec.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.CAS_ST_QUERY);
            querySec.setData(tc);
            querySec.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.CAS_ST_TRANSACTION);
            transactionSec.setData(tc);
            transactionSec.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.CAS_ST_LONG_QUERY);
            longQuery.setData(tc);
            longQuery.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.CAS_ST_LONG_TRAN);
            longTran.setData(tc);
            longTran.setText(tc.getDisplayName());

            tc = group.get(EnumTargetConfig.CAS_ST_ERROR_QUERY);
            errQuery.setData(tc);
            errQuery.setText(tc.getDisplayName());

            broker.setExpanded(true);
        }
        root.setExpanded(true);
    }

    /** This method initializes tableSelectedItem */
    private void createTableSelectedItem() {

        TableLayout tlayout = new TableLayout();
        tlayout.addColumnData(new ColumnWeightData(150, 150, true));
        tlayout.addColumnData(new ColumnWeightData(150, 150, true));
        tagetTbl.setLayout(tlayout);

        TableColumn categoryColumn = new TableColumn(tagetTbl, SWT.CENTER);
        categoryColumn.setText(DIAG_CATEGORY);

        TableColumn nameColumn = new TableColumn(tagetTbl, SWT.CENTER);
        nameColumn.setText(DIAG_NAME);
    }

    /**
     * Inserts the items of target list
     *
     * @param item TreeItem
     * @return String
     */
    private String insertItemToTargetList(TreeItem item) {
        TreeItem parent = item.getParentItem();
        if (null == parent) {
            return ""; // root node clicked
        }
        if (item.getItems().length == 0) {
            boolean hasSame = false;
            for (TableItem tableItem : tagetTbl.getItems()) {
                if (tableItem.getData() == item.getData()) {
                    hasSame = true;
                    break;
                }
            }
            if (!hasSame) {
                TargetConfig tc = (TargetConfig) item.getData();
                TableItem newItem = new TableItem(tagetTbl, SWT.NONE);
                newItem.setText(0, tc.getCategory());
                newItem.setText(1, tc.getDisplayName());
                newItem.setData(tc);
            }

        } else {
            for (TreeItem treeItem : item.getItems()) {
                insertItemToTargetList(treeItem);
            }
        }
        return "";
    }

    /** Constrain shell size */
    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
    }

    /** Executes the task when "ok" button is pressed */
    @Override
    public void okPressed() {
        String name = nameText.getText().trim();
        statusTemplateInfo.setName(name);
        String desc = discriptionText.getText().trim();
        statusTemplateInfo.setDesc(desc);
        String samplingTerm = samplingTermscombo.getText().trim();
        statusTemplateInfo.setSampling_term(samplingTerm);
        String dbName = "";
        if (targetDbNameCombo != null && targetDbNameCombo.getEnabled()) {
            dbName = targetDbNameCombo.getText();
        }
        statusTemplateInfo.setDb_name(dbName);
        TargetConfigInfo targetConfigInfo = new TargetConfigInfo();
        TableItem[] tableItems = tagetTbl.getItems();
        for (TableItem item : tableItems) {
            assert (null != item);
            String[] values = new String[] {"1", "1"};
            TargetConfigMap tcm = TargetConfigMap.getInstance();
            if (item.getText(1).equals(tcm.getShowOpenedPage())) {
                targetConfigInfo.setServer_query_opened_page(values);
            } else if (item.getText(1).equals(tcm.getShowSlowQuery())) {
                targetConfigInfo.setServer_query_slow_query(values);
            } else if (item.getText(1).equals(tcm.getShowFullScan())) {
                targetConfigInfo.setServer_query_full_scan(values);
            } else if (item.getText(1).equals(tcm.getShowCliRequest())) {
                targetConfigInfo.setServer_conn_cli_request(values);
            } else if (item.getText(1).equals(tcm.getShowAboutedClients())) {
                targetConfigInfo.setServer_conn_aborted_clients(values);
            } else if (item.getText(1).equals(tcm.getShowConnReq())) {
                targetConfigInfo.setServer_conn_conn_req(values);
            } else if (item.getText(1).equals(tcm.getShowConnRej())) {
                targetConfigInfo.setServer_conn_conn_reject(values);
            } else if (item.getText(1).equals(tcm.getShowPageWrite())) {
                targetConfigInfo.setServer_buffer_page_write(values);
            } else if (item.getText(1).equals(tcm.getShowPageRead())) {
                targetConfigInfo.setServer_buffer_page_read(values);
            } else if (item.getText(1).equals(tcm.getShowLockDeadlock())) {
                targetConfigInfo.setServer_lock_deadlock(values);
            } else if (item.getText(1).equals(tcm.getShowLockRequest())) {
                targetConfigInfo.setServer_lock_request(values);
            } else if (item.getText(1).equals(tcm.getShowStRequest())) {
                targetConfigInfo.setCas_st_request(values);
            } else if (item.getText(1).equals(tcm.getShowStTransaction())) {
                targetConfigInfo.setCas_st_transaction(values);
            } else if (item.getText(1).equals(tcm.getShowStActiveSession())) {
                targetConfigInfo.setCas_st_active_session(values);
            } else if (item.getText(1).equals(tcm.getShowStQuery())) {
                targetConfigInfo.setCas_st_query(values);
            } else if (item.getText(1).equals(tcm.getShowStLongQuery())) {
                targetConfigInfo.setCas_st_long_query(values);
            } else if (item.getText(1).equals(tcm.getShowStLongTran())) {
                targetConfigInfo.setCas_st_long_tran(values);
            } else if (item.getText(1).equals(tcm.getShowStErrQuery())) {
                targetConfigInfo.setCas_st_error_query(values);
            }
        }
        statusTemplateInfo.addTarget_config(targetConfigInfo);

        super.okPressed();
    }

    /**
     * A adapter for the "add" button
     *
     * @author lizhiqiang 2009-4-28
     */
    private class AddSelectionAdapter extends SelectionAdapter {
        /**
         * Sent when selection occurs in the control. The default behavior is to do nothing.
         *
         * @param se an event containing information about the selection
         */
        public void widgetSelected(SelectionEvent se) {
            for (int i = 0; i < targetTree.getSelectionCount(); i++) {
                TreeItem item = (targetTree.getSelection())[i];
                String msg = insertItemToTargetList(item);
                if (!"".equals(msg)) {
                    CommonUITool.openErrorBox(msg);
                }
            }
            if (tagetTbl.getItemCount() == 0) {
                isOkenable[7] = false;
            } else {
                isOkenable[7] = true;
            }

            ServerType serverType = serverInfo.getServerType();
            if (serverType == ServerType.BROKER) {
                isOkenable[5] = true;
                isOkenable[6] = true;
                enableOk();
                return;
            }

            boolean hasServer = false;

            for (int i = 0; i < tagetTbl.getItemCount(); i++) {
                TargetConfig tc = (TargetConfig) tagetTbl.getItem(i).getData();
                if (tc.getTopCategory().equals(TargetConfigMap.getInstance().getDbCategory())) {
                    hasServer = true;
                    break;
                }
            }

            if (hasServer) {
                targetDbNameCombo.setEnabled(true);
                String targetDb = targetDbNameCombo.getText().trim();
                if (targetDb.length() == 0) {
                    isOkenable[5] = false;
                    isOkenable[6] = false;
                } else {
                    isOkenable[5] = true;
                    List<String> dbs = selection.getServer().getServerInfo().getAllDatabaseList();
                    if (dbs.contains(targetDb)) {
                        isOkenable[6] = true;
                    } else {
                        isOkenable[6] = false;
                    }
                }
            } else {
                targetDbNameCombo.setEnabled(false);
                isOkenable[5] = true;
                isOkenable[6] = true;
            }

            enableOk();
        }
    }

    /**
     * A adapter for the "remove" button
     *
     * @author lizhiqiang 2009-4-28
     */
    private class RemoveSelectionAdapter extends SelectionAdapter {
        /**
         * Sent when selection occurs in the control. The default behavior is to do nothing.
         *
         * @param se an event containing information about the selection
         */
        public void widgetSelected(SelectionEvent se) {
            TableItem selectedItem = null;

            for (int i = tagetTbl.getSelectionCount() - 1; i >= 0; i--) {
                selectedItem = tagetTbl.getSelection()[i];
                selectedItem.dispose();
            }
            if (tagetTbl.getItemCount() == 0) {
                isOkenable[7] = false;
            } else {
                isOkenable[7] = true;
            }
            ServerType serverType = serverInfo.getServerType();
            if (serverType == ServerType.BROKER) {
                isOkenable[5] = true;
                isOkenable[6] = true;
                enableOk();
                return;
            }
            boolean hasServer = false;
            for (int i = 0; i < tagetTbl.getItemCount(); i++) {
                TargetConfig tc = (TargetConfig) tagetTbl.getItem(i).getData();
                if (tc.getTopCategory().equals(TargetConfigMap.getInstance().getDbCategory())) {
                    hasServer = true;
                    break;
                }
            }

            if (hasServer) {
                targetDbNameCombo.setEnabled(true);
                String targetDb = targetDbNameCombo.getText().trim();
                if (targetDb.length() == 0) {
                    isOkenable[5] = false;
                } else {
                    isOkenable[5] = true;
                }

                List<String> dbs = selection.getServer().getServerInfo().getAllDatabaseList();
                if (dbs.contains(targetDb)) {
                    isOkenable[6] = true;
                } else {
                    isOkenable[6] = false;
                }
            } else {
                targetDbNameCombo.setEnabled(false);
                isOkenable[5] = true;
                isOkenable[6] = true;
            }

            enableOk();
        }
    }

    /** Initializes the parameter of this view */
    private void init() {
        // Sets the title and message
        if (operation == AddEditType.ADD) {
            setTitle(ADD_TITLE);
            setMessage(ADD_MESSAGE);
            getShell().setText(ADD_TITLE);
            isOkenable[2] = true;
            isOkenable[3] = true;

        } else if (operation == AddEditType.EDIT) {
            setTitle(EDIT_TITLE);
            setMessage(EDIT_MESSAGE);
            getShell().setText(EDIT_TITLE);
            for (int i = 0; i < isOkenable.length; i++) {
                isOkenable[i] = true;
            }
        }
        // Sets contents in controls
        if (null == selection) {
            return;
        }
        StatusTemplateInfo statusTemplateInfo = new StatusTemplateInfo();
        List<String> dbs = null;
        if (CubridNodeType.STATUS_MONITOR_FOLDER.equals(selection.getType())) {
            dbs = selection.getServer().getServerInfo().getAllDatabaseList();
            if (null != targetDbNameCombo) {
                targetDbNameCombo.setItems((String[]) (dbs.toArray(new String[dbs.size()])));
                targetDbNameCombo.setEnabled(false);
            }
            return;
        }
        if (CubridNodeType.STATUS_MONITOR_TEMPLATE.equals(selection.getType())) {
            dbs = selection.getServer().getServerInfo().getAllDatabaseList();
            statusTemplateInfo =
                    (StatusTemplateInfo) selection.getAdapter(StatusTemplateInfo.class);
        }
        if (null != dbs && null != targetDbNameCombo) {
            targetDbNameCombo.setItems((String[]) (dbs.toArray(new String[dbs.size()])));
        }
        String name = statusTemplateInfo.getName();
        nameText.setText(name);
        nameText.setEnabled(false);
        String sampling = statusTemplateInfo.getSampling_term();
        samplingTermscombo.setText(sampling);
        String desc = statusTemplateInfo.getDesc();
        discriptionText.setText(desc);
        if (null != targetDbNameCombo) {
            String dbName = statusTemplateInfo.getDb_name();
            targetDbNameCombo.setText(dbName);
        }

        List<TargetConfigInfo> list = statusTemplateInfo.getTargetConfigInfoList();
        EnumMap<EnumTargetConfig, TargetConfig> group = TargetConfigMap.getInstance().getMap();
        boolean hasServer = false;
        for (TargetConfigInfo tcf : list) {
            for (String[] strings : tcf.getList()) {
                for (Map.Entry<EnumTargetConfig, TargetConfig> entry : group.entrySet()) {
                    TargetConfig tc = entry.getValue();
                    if (strings[0].equals(tc.getName())) {
                        TableItem newItem = new TableItem(tagetTbl, SWT.NONE);
                        newItem.setText(0, tc.getCategory());
                        newItem.setText(1, tc.getDisplayName());
                        newItem.setData(tc);
                        if (tc.getTopCategory()
                                .equals(TargetConfigMap.getInstance().getDbCategory())) {
                            hasServer = true;
                        }
                        break;
                    }
                }
            }
        }
        if (null != targetDbNameCombo) {
            if (hasServer) {
                targetDbNameCombo.setEnabled(true);
            } else {
                targetDbNameCombo.setEnabled(false);
            }
        }
    }

    /**
     * Sets the value of selection
     *
     * @param selection ICubridNode
     */
    public void setSelection(ICubridNode selection) {
        this.selection = selection;
    }

    /**
     * Gets the value of statusTemplateInfo
     *
     * @return StatusTemplateInfo
     */
    public StatusTemplateInfo getStatusTemplateInfo() {
        return statusTemplateInfo;
    }

    /**
     * Sets the value of operation
     *
     * @param operation AddEditType
     */
    public void setOperation(AddEditType operation) {
        this.operation = operation;
    }

    /** Adds the listener to template */
    private void addTemplateListener() {
        // Add the listeners
        nameText.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent me) {
                        String name = nameText.getText().trim();
                        if (name.length() == 0) {
                            isOkenable[0] = false;
                        } else {
                            isOkenable[0] = true;
                            List<ICubridNode> children = selection.getChildren();
                            boolean hasSame = false;
                            for (ICubridNode child : children) {
                                if (child.getLabel().equals(name)) {
                                    hasSame = true;
                                    break;
                                }
                            }
                            if (hasSame) {
                                isOkenable[1] = false;
                            } else {
                                isOkenable[1] = true;
                            }
                        }
                        enableOk();
                    }
                });
        samplingTermscombo.addVerifyListener(
                new VerifyListener() {

                    public void verifyText(VerifyEvent ve) {
                        String text = ve.text;
                        if (("").equals(text)) {
                            return;
                        }
                        if (text.matches("^\\d$")) {
                            ve.doit = true;
                        } else {
                            ve.doit = false;
                        }
                    }
                });
        samplingTermscombo.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent me) {
                        String samplingTerms = samplingTermscombo.getText().trim();
                        if (ValidateUtil.isInteger(samplingTerms)) {
                            isOkenable[2] = true;
                            if (Arrays.asList(samplingTermsItems).contains(samplingTerms)) {
                                isOkenable[3] = true;
                            } else {
                                isOkenable[3] = false;
                            }
                        } else {
                            isOkenable[2] = false;
                        }
                        enableOk();
                    }
                });
        discriptionText.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent me) {
                        String desc = discriptionText.getText().trim();
                        if (desc.length() == 0) {
                            isOkenable[4] = false;
                        } else {
                            isOkenable[4] = true;
                        }
                        enableOk();
                    }
                });
        if (null != targetDbNameCombo) {
            targetDbNameCombo.addModifyListener(
                    new ModifyListener() {

                        public void modifyText(ModifyEvent me) {
                            String targetDb = targetDbNameCombo.getText().trim();
                            boolean hasServer = false;

                            for (int i = 0; i < tagetTbl.getItemCount(); i++) {
                                if (tagetTbl.getItem(i).getText(0).startsWith("server")) {
                                    hasServer = true;
                                    break;
                                }
                            }
                            if (hasServer && targetDb.length() == 0) {
                                isOkenable[5] = false;
                            } else {
                                isOkenable[5] = true;
                            }

                            List<String> dbs =
                                    selection.getServer().getServerInfo().getAllDatabaseList();
                            if (dbs.contains(targetDb)) {
                                isOkenable[6] = true;
                            } else {
                                isOkenable[6] = false;
                            }
                            enableOk();
                        }
                    });
        }
    }

    /** Enable the "OK" button */
    private void enableOk() {
        boolean is = true;
        for (int i = 0; i < isOkenable.length; i++) {
            is = is && isOkenable[i];
        }
        if (is) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            setErrorMessage(null);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }

        if (!isOkenable[7]) {
            setErrorMessage(NO_TARGET_OBJECT);
            return;
        }
        if (!isOkenable[0]) {
            setErrorMessage(EMPTY_NAME_TXT);
            return;
        }
        if (!isOkenable[1]) {
            setErrorMessage(HAS_SAME_NAME);
            return;
        }
        if (!isOkenable[2]) {
            setErrorMessage(ERROR_SAMPLE_TERMS_TXT);
            return;
        }
        if (!isOkenable[3]) {
            setErrorMessage(OVERLMT_SAMPLE_TERMS_TXT);
            return;
        }
        if (!isOkenable[4]) {
            setErrorMessage(EMPTY_DESC_TXT);
            return;
        }
        if (!isOkenable[5]) {
            setErrorMessage(EMPTY_DB_TXT);
            return;
        }
        if (!isOkenable[6]) {
            setErrorMessage(NO_SUCH_DB);
            return;
        }
    }

    /**
     * Adds buttons to this dialog's button bar.
     *
     * @param parent the button bar composite
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        if (operation == AddEditType.ADD) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    /** @param execDiagChecked the execDiagChecked to set */
    public void setExecDiagChecked(boolean execDiagChecked) {
        this.execDiagChecked = execDiagChecked;
    }

    /**
     * A inner class which is responsible for response to Tree Selection
     *
     * @author cn12978
     * @version 1.0 - 2009-7-10 created by cn12978
     */
    private class TreeSelectionEvent extends SelectionAdapter {
        /**
         * Sent when selection occurs in the control. The default behavior is to do nothing.
         *
         * @param se an event containing information about the selection
         */
        public void widgetSelected(SelectionEvent se) {
            TreeItem item = (TreeItem) se.item;
            if (item == null) {
                return;
            }
            if (!execDiagChecked) {
                if (item.getText().equals(STATUS_MONITOR_LST_DB)) {
                    setErrorMessage(NOPERMIT_MONITOR_DB);
                    addBtn.setEnabled(false);
                    return;
                }
                if (item.getParentItem() != null
                        && item.getParentItem().getText().equals(STATUS_MONITOR_LST_DB)) {
                    setErrorMessage(NOPERMIT_MONITOR_DB);
                    addBtn.setEnabled(false);
                    return;
                }
                if (item.getParentItem() != null
                        && item.getParentItem().getParentItem() != null
                        && item.getParentItem()
                                .getParentItem()
                                .getText()
                                .equals(STATUS_MONITOR_LST_DB)) {
                    setErrorMessage(NOPERMIT_MONITOR_DB);
                    addBtn.setEnabled(false);
                    return;
                }
                setErrorMessage(null);
                addBtn.setEnabled(true);
            }
        }
    }
}
