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
package com.cubrid.cubridmanager.ui.mondashboard.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.control.DatabaseProvider;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.wizard.AddHostAndDbWizard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorDashboardPersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorStatisticPersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Add dash board dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class AddDashboardDialog extends CMTitleAreaDialog implements ModifyListener {
    private Text dashboardNameText;
    private Button deleteButton;
    private TreeViewer dbTv;
    private Tree dbTree;
    private Dashboard dashboard;
    private final List<HostNode> hostNodeList = new ArrayList<HostNode>();

    /**
     * The constructor
     *
     * @param parentShell
     */
    public AddDashboardDialog(Shell parentShell) {
        super(parentShell);
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
        {
            GridLayout compLayout = new GridLayout();
            compLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            compLayout.marginWidth =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            compLayout.verticalSpacing =
                    convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            compLayout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(compLayout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        final Group generalGroup = new Group(composite, SWT.NONE);
        generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        generalGroup.setText(Messages.grpGeneralInfo);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        generalGroup.setLayout(layout);

        final Label dashboardNameLabel = new Label(generalGroup, SWT.NONE);
        dashboardNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        dashboardNameLabel.setText(Messages.lblDashboardName);

        dashboardNameText = new Text(generalGroup, SWT.LEFT | SWT.BORDER);
        final GridData gdDashboardNameText = new GridData(GridData.FILL_HORIZONTAL);
        dashboardNameText.setLayoutData(gdDashboardNameText);

        createDbTree(composite);
        if (dashboard == null) {
            setTitle(Messages.titleAddDashboardDialog);
            setMessage(Messages.msgAddDashboardDialog);
        } else {
            setTitle(Messages.titleEditDashboardDialog);
            setMessage(Messages.msgEditDashboardDialog);
        }

        initial();
        return parentComp;
    }

    /**
     * Create table area
     *
     * @param parent the parent composite
     */
    private void createDbTree(Composite parent) {

        Label tipLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        tipLabel.setText(Messages.lblDashboardInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        tipLabel.setLayoutData(gridData);

        dbTv =
                new TreeViewer(
                        parent,
                        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        {
            dbTree = dbTv.getTree();
            gridData = new GridData(GridData.FILL_BOTH);
            gridData.horizontalSpan = 2;
            gridData.heightHint = 200;
            dbTree.setLayoutData(gridData);
            dbTree.setHeaderVisible(true);
            dbTree.setLinesVisible(true);

            TreeColumn column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colIP);
            column.setWidth(120);
            column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colPort);
            column.setWidth(50);
            column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colServerType);
            column.setWidth(100);
            column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colServerStatus);
            column.setWidth(100);
            column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colName);
            column.setWidth(150);
            column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colStatus);
            column.setWidth(150);
            column = new TreeColumn(dbTree, SWT.CENTER);
            column.setText(Messages.colType);
            column.setWidth(60);

            dbTv.setContentProvider(new DatabaseProvider());
            dbTv.setLabelProvider(new DatabaseProvider());
            dbTv.setInput(hostNodeList);

            dbTree.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            deleteButton.setEnabled(dbTree.getSelectionCount() > 0);
                        }
                    });
        }

        Composite composite = new Composite(parent, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 5;
        composite.setLayout(rowLayout);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.END;
        composite.setLayoutData(gridData);

        Button addButton = new Button(composite, SWT.NONE);
        addButton.setText(Messages.btnAdd);
        addButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        StructuredSelection selection = (StructuredSelection) dbTv.getSelection();
                        HostNode hostNode = null;
                        int addType = 0;
                        if (selection != null && !selection.isEmpty()) {
                            Object obj = selection.getFirstElement();
                            if (obj instanceof HostNode) {
                                hostNode = (HostNode) obj;
                                addType = 0;
                            } else if (obj instanceof DatabaseNode) {
                                hostNode = ((DatabaseNode) obj).getParent();
                                addType = 1;
                            } else if (obj instanceof BrokerNode) {
                                hostNode = ((BrokerNode) obj).getParent();
                                addType = 2;
                            }
                        }

                        AddHostAndDbWizard wizard =
                                new AddHostAndDbWizard(hostNode, hostNodeList, addType);
                        CMWizardDialog dialog = new CMWizardDialog(getShell(), wizard);
                        dialog.setPageSize(660, 380);
                        if (IDialogConstants.OK_ID == dialog.open()) {
                            List<HostNode> addedHostNodeList = wizard.getAddedHostNodeList();
                            HAUtil.mergeHostNode(hostNodeList, addedHostNodeList);
                            dbTv.refresh();
                        }
                        deleteButton.setEnabled(dbTree.getSelectionCount() > 0);
                        verify();
                    }
                });

        deleteButton = new Button(composite, SWT.NONE);
        deleteButton.setText(Messages.btnDelete);
        deleteButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        StructuredSelection selection = (StructuredSelection) dbTv.getSelection();
                        if (selection != null && !selection.isEmpty()) {
                            Object[] objs = selection.toArray();
                            for (int i = 0; i < objs.length; i++) {
                                Object obj = objs[i];
                                if (obj instanceof HostNode) {
                                    hostNodeList.remove((HostNode) obj);
                                } else if (obj instanceof DatabaseNode) {
                                    ((DatabaseNode) obj)
                                            .getParent()
                                            .getCopyedHaNodeList()
                                            .remove((DatabaseNode) obj);
                                } else if (obj instanceof BrokerNode) {
                                    ((BrokerNode) obj)
                                            .getParent()
                                            .getCopyedHaNodeList()
                                            .remove((BrokerNode) obj);
                                }
                            }
                            dbTv.refresh();
                        }
                        deleteButton.setEnabled(dbTree.getSelectionCount() > 0);
                        verify();
                    }
                });
        deleteButton.setEnabled(false);
    }

    /** Initial the value of dialog field */
    private void initial() {
        if (dashboard != null) {
            dashboardNameText.setText(dashboard.getName());
            hostNodeList.addAll(dashboard.getHostNodeList());
            dbTv.refresh();
        }
        dashboardNameText.addModifyListener(this);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        if (dashboard == null) {
            getShell().setText(Messages.titleAddDashboardDialog);
        } else {
            getShell().setText(Messages.titleEditDashboardDialog);
        }
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        if (dashboard == null) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (dashboard == null) {
                dashboard = new Dashboard();
            }
            String dashboardNode = dashboardNameText.getText();
            dashboard.setName(dashboardNode);
            for (int i = 0; i < hostNodeList.size(); i++) {
                HostNode node = hostNodeList.get(i);
                node.setDbNodeList(node.getCopyedDbNodeList());
                node.setBrokerNodeList(node.getCopyedBrokerNodeList());
            }
            dashboard.setChildNodeList(hostNodeList);
            HAUtil.calcLocation(dashboard.getHostNodeList());
        }
        super.buttonPressed(buttonId);
    }

    /**
     * When modify the page content and check the validation
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        verify();
    }

    /** Verify data */
    private void verify() {
        String dashboardName = dashboardNameText.getText();
        if (dashboardName.trim().length() == 0) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            setErrorMessage(Messages.errDashboardName);
            return;
        }

        List<ICubridNode> dashboardList =
                MonitorDashboardPersistManager.getInstance().getAllMonitorDashboards();
        for (int i = 0; i < dashboardList.size(); i++) {
            ICubridNode node = dashboardList.get(i);
            if (dashboardName.equals(node.getLabel())
                    && (dashboard == null || !dashboard.getName().equals(dashboardName))) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
                setErrorMessage(Messages.errDashboardNameExist);
                return;
            }
        }
        /*TOOLS-3672 Avoid to dashboard node and statistic node have the same name*/
        List<MonitorStatistic> monitorList =
                MonitorStatisticPersistManager.getInstance().getMonitorStatisticListByHostId(null);
        for (MonitorStatistic monitorStatistic : monitorList) {
            if (dashboardName.equals(monitorStatistic.getId())) {
                if (dashboardName.equals(monitorStatistic.getLabel())) {
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                    setErrorMessage(Messages.errDashboardNameExist);
                    return;
                }
            }
        }

        if (hostNodeList.isEmpty()) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            setErrorMessage(Messages.errHostAndDbList);
            return;
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        setErrorMessage(null);
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    /**
     * Set dash board
     *
     * @param dashboard The Dashboard
     */
    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
