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
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.dialog.CMTrayDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import java.util.ArrayList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Filter setting dialog
 *
 * @author Kevin
 * @version 1.0 - 2011-03-24 created by Kevin
 */
public class GroupSettingDialog extends CMTrayDialog {

    private static final Logger LOGGER = LogUtil.getLogger(GroupSettingDialog.class);

    private List groupList;

    private final java.util.List<CubridGroupNode> groups = new ArrayList<CubridGroupNode>();

    private final ICubridGroupNodeManager nodeManager;

    private Button btnTop;

    private Button btnUp;

    private Button btnDown;

    private Button btnBottom;

    private Button btnEdit;

    private Button btnRemove;

    /**
     * The constructor
     *
     * @param parentShell
     * @param tv
     */
    public GroupSettingDialog(Shell parentShell, ICubridGroupNodeManager nodeManager) {
        super(parentShell);
        this.nodeManager = nodeManager;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        this.getShell().setMinimumSize(380, 420);
        Composite parentComp = (Composite) super.createDialogArea(parent);
        // parentComp.setLayoutData(new GridData(640, 480));
        createGroupList(parentComp);
        createManagerButtons(parentComp);
        return parentComp;
    }

    /**
     * Create the buttons about add ,edit and delete.
     *
     * @param parentComp parent composite.
     */
    private void createManagerButtons(Composite parentComp) {
        Composite group2 = new Composite(parentComp, SWT.NONE);
        {
            group2.setLayout(new GridLayout(3, true));
            group2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Button btnNew = new Button(group2, SWT.NONE);
            btnNew.setText(Messages.btnAdd);
            btnNew.addSelectionListener(
                    new SelectionAdapter() {

                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            GroupEditDialog dialog =
                                    new GroupEditDialog(
                                            GroupSettingDialog.this.getShell(),
                                            nodeManager,
                                            groups,
                                            null);
                            if (dialog.open() == Dialog.OK) {
                                CubridGroupNode groupNode = dialog.getGroup();
                                if (groupNode == null) {
                                    LOGGER.warn("The groupNode is a null.");
                                } else {
                                    groupList.add(groupNode.getName());
                                    groups.add(groupNode);
                                }
                                refreshDefaultNode();
                            }
                        }
                    });

            btnEdit = new Button(group2, SWT.NONE);
            btnEdit.setText(Messages.btnEdit);
            btnEdit.addSelectionListener(
                    new SelectionAdapter() {

                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            GroupEditDialog dialog =
                                    new GroupEditDialog(
                                            GroupSettingDialog.this.getShell(),
                                            nodeManager,
                                            groups,
                                            getGroupByName(groupList.getSelection()[0]));
                            if (dialog.open() == Dialog.OK) {
                                CubridGroupNode groupNode = dialog.getGroup();
                                if (groupNode == null) {
                                    LOGGER.warn("The groupNode is a null.");
                                } else {
                                    groupList.setItem(
                                            groupList.getSelectionIndex(), groupNode.getName());
                                }
                                refreshDefaultNode();
                            }
                        }
                    });

            btnRemove = new Button(group2, SWT.NONE);
            btnRemove.setText(Messages.btnDelete);
            btnRemove.addSelectionListener(
                    new SelectionAdapter() {

                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            for (String grpName : groupList.getSelection()) {
                                CubridGroupNode cgn = getGroupByName(grpName);
                                groups.remove(cgn);
                                int idx = groupList.getSelectionIndex();
                                if (idx == groupList.getItemCount() - 1) {
                                    idx--;
                                }
                                groupList.remove(grpName);
                                groupList.setSelection(idx);
                                setButtonStatus();
                            }
                            refreshDefaultNode();
                        }
                    });
        }
    }

    /** Refresh the default node's children. */
    private void refreshDefaultNode() {
        java.util.List<ICubridNode> nodeWithParent = new ArrayList<ICubridNode>();
        CubridGroupNode defaultGrp = null;
        for (CubridGroupNode grp : groups) {
            if (nodeManager.isDefaultGroup(grp)) {
                defaultGrp = grp;
            } else {
                nodeWithParent.addAll(grp.getChildren());
            }
        }

        if (defaultGrp == null) {
            LOGGER.warn("The defaultGrp is a null.");
            return;
        }

        defaultGrp.removeAllChild();
        java.util.List<ICubridNode> allItems = nodeManager.getAllGroupItems();
        for (ICubridNode item : allItems) {
            if (nodeWithParent.indexOf(item) < 0) {
                defaultGrp.addChild(item);
            }
        }
    }

    /**
     * Override the parent createContents
     *
     * @param parent Composite
     * @return the contents.
     */
    protected Control createContents(Composite parent) {
        Control createContents = super.createContents(parent);
        initDialogData();
        setButtonStatus();
        return createContents;
    }

    /**
     * Create group list.
     *
     * @param parentComp parent composite.
     */
    private void createGroupList(Composite parentComp) {
        Composite groupLabel = new Composite(parentComp, SWT.NONE);
        {
            groupLabel.setLayout(new GridLayout());
            groupLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Label label = new Label(groupLabel, SWT.LEFT);
            label.setText(Messages.labelManagerGroups);
        }

        Composite group1 = new Composite(parentComp, SWT.NONE);
        {
            group1.setLayout(new GridLayout(2, false));
            group1.setLayoutData(new GridData(GridData.FILL_BOTH));

            groupList = new List(group1, SWT.BORDER | SWT.V_SCROLL);
            {
                groupList.setLayoutData(new GridData(GridData.FILL_BOTH));
                groupList.addSelectionListener(
                        new SelectionAdapter() {
                            /**
                             * selection change event
                             *
                             * @param event SelectionEvent
                             */
                            public void widgetSelected(SelectionEvent event) {
                                setButtonStatus();
                            }
                        });
            }

            Composite btnCom = new Composite(group1, SWT.NONE);
            {
                btnCom.setLayout(new GridLayout());
                btnCom.setLayoutData(new GridData(GridData.FILL_VERTICAL));
            }
            btnTop = new Button(btnCom, SWT.NONE);
            btnTop.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            btnTop.setText(Messages.btnTop);
            btnTop.addSelectionListener(
                    new SelectionAdapter() {
                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            String current = groupList.getSelection()[0];
                            int index = groupList.getSelectionIndex();
                            groupList.remove(index);
                            groupList.add(current, 0);
                            groupList.setSelection(0);
                            setButtonStatus();
                        }
                    });

            btnUp = new Button(btnCom, SWT.NONE);
            btnUp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            btnUp.setText(Messages.btnUp);
            btnUp.addSelectionListener(
                    new SelectionAdapter() {
                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            String current = groupList.getSelection()[0];
                            int index = groupList.getSelectionIndex();
                            groupList.remove(index);
                            groupList.add(current, index - 1);
                            groupList.setSelection(index - 1);
                            setButtonStatus();
                        }
                    });

            btnDown = new Button(btnCom, SWT.NONE);
            btnDown.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            btnDown.setText(Messages.btnDown);
            btnDown.addSelectionListener(
                    new SelectionAdapter() {
                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            String current = groupList.getSelection()[0];
                            int index = groupList.getSelectionIndex();
                            groupList.remove(index);
                            groupList.add(current, index + 1);
                            groupList.setSelection(index + 1);
                            setButtonStatus();
                        }
                    });

            btnBottom = new Button(btnCom, SWT.NONE);
            btnBottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            btnBottom.setText(Messages.btnBottom);
            btnBottom.addSelectionListener(
                    new SelectionAdapter() {
                        /**
                         * push button event
                         *
                         * @param event SelectionEvent
                         */
                        public void widgetSelected(SelectionEvent event) {
                            String current = groupList.getSelection()[0];
                            int index = groupList.getSelectionIndex();
                            groupList.remove(index);
                            groupList.add(current);
                            groupList.setSelection(groupList.getItemCount() - 1);
                            setButtonStatus();
                        }
                    });
        }
    }

    /** Fill object data to dialog composites. */
    private void initDialogData() {
        groupList.removeAll();
        groups.clear();
        java.util.List<CubridGroupNode> nodes = nodeManager.getAllGroupNodes();
        for (CubridGroupNode node : nodes) {

            CubridGroupNode grp =
                    new CubridGroupNode(node.getId(), node.getName(), node.getIconPath());
            copyForSetup(node, grp);
            groups.add(grp);
            groupList.add(node.getName());
        }
    }

    /**
     * Copy the information from source to this.
     *
     * @param source the source node.
     * @param target the target node
     */
    private void copyForSetup(CubridGroupNode source, CubridGroupNode target) {
        target.setId(source.getId());
        target.setLabel(source.getLabel());
        target.removeAllChild();
        for (ICubridNode node : source.getChildren()) {
            // use reflect to clone a new group.
            ICubridNode newNode = null;
            if (node instanceof CubridServer) {
                newNode =
                        new CubridServer(
                                node.getId(),
                                node.getName(),
                                ((CubridServer) node).getConnectedIconPath(),
                                ((CubridServer) node).getDisConnectedIconPath());
            } else if (node instanceof CubridDatabase) {
                newNode = new CubridDatabase(node.getId(), node.getName());
            } else {
                throw new RuntimeException("Group item is not supported.");
            }
            target.addChild(newNode);
        }
    }

    /** Set buttons status. */
    private void setButtonStatus() {

        if (groupList.getSelectionCount() == 0) {
            btnEdit.setEnabled(false);
            btnRemove.setEnabled(false);
            btnTop.setEnabled(false);
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
            btnBottom.setEnabled(false);
            return;
        }
        boolean firstSelected = groupList.getSelectionIndex() == 0;
        boolean lastSelected = groupList.getSelectionIndex() == (groupList.getItemCount() - 1);
        CubridGroupNode currentGroup = getGroupByName(groupList.getSelection()[0]);
        boolean defaultSelected = nodeManager.isDefaultGroup(currentGroup);
        btnEdit.setEnabled(!defaultSelected);
        btnRemove.setEnabled(!defaultSelected);
        btnTop.setEnabled(!firstSelected);
        btnUp.setEnabled(!firstSelected);
        btnDown.setEnabled(!lastSelected);
        btnBottom.setEnabled(!lastSelected);
    }

    /**
     * Retrieves the group with input name.
     *
     * @param name of group which is being found.
     * @return the group with input name.
     */
    private CubridGroupNode getGroupByName(String name) {
        return nodeManager.getGroupByName(groups, name);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleGroupSettingDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /** If OK pressed. */
    protected void okPressed() {
        // copy edit result to persist.
        java.util.List<String> newIds = new ArrayList<String>();
        for (CubridGroupNode grp : groups) {
            newIds.add(grp.getId());

            CubridGroupNode oldgrp = nodeManager.getGroupById(grp.getId());
            if (oldgrp == null) {
                CubridGroupNode newgrp =
                        new CubridGroupNode(grp.getId(), grp.getName(), grp.getIconPath());
                copyGroupChild2Persist(grp, newgrp);
                nodeManager.addGroupNode(newgrp);
            } else {
                oldgrp.removeAllChild();
                copyGroupChild2Persist(grp, oldgrp);
            }
        }

        java.util.List<String> deleteIds = new ArrayList<String>();
        for (CubridGroupNode oldgrp : nodeManager.getAllGroupNodes()) {
            if (newIds.indexOf(oldgrp.getId()) < 0) {
                deleteIds.add(oldgrp.getId());
            }
        }
        for (String id : deleteIds) {
            nodeManager.removeGroup(id);
        }

        nodeManager.reorderGroup(groupList.getItems());
        super.okPressed();
    }

    /**
     * Persist the group node
     *
     * @param source the source node
     * @param persistGroup the target node
     */
    private void copyGroupChild2Persist(CubridGroupNode source, CubridGroupNode persistGroup) {
        for (ICubridNode cn : source.getChildren()) {
            persistGroup.addChild(nodeManager.getGroupItemByItemName(cn.getName()));
        }
    }
}
