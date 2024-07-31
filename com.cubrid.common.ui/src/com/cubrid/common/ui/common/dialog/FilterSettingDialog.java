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

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.navigator.NodeFilterManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Filter setting dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 */
public class FilterSettingDialog extends CMTitleAreaDialog {

    private final TreeViewer tv;
    private Button selectPatternBtn;
    private Text namePatternText;
    private CheckboxTreeViewer ctv;
    private final List<ICubridNode> defaultCheckedList;
    private final List<ICubridNode> defaultGrayCheckedList;

    /**
     * The constructor
     *
     * @param parentShell
     * @param tv
     */
    public FilterSettingDialog(
            Shell parentShell,
            TreeViewer tv,
            List<ICubridNode> defaultCheckedList,
            List<ICubridNode> defaultGrayCheckedList) {
        super(parentShell);
        this.tv = tv;
        this.defaultCheckedList = defaultCheckedList;
        this.defaultGrayCheckedList = defaultGrayCheckedList;
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
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        createPatternComposite(composite);
        createTreeComposite(composite);
        init();
        setTitle(Messages.titleFilterSettingDialog);
        setMessage(Messages.msgFilterSettingDialog);
        return parentComp;
    }

    /** Initial the page content */
    private void init() {
        NodeFilterManager nodeFilterManager = NodeFilterManager.getInstance();
        // inital the name filter
        List<String> nameFilterList = nodeFilterManager.getNameFilterList();
        if (nameFilterList == null || nameFilterList.isEmpty()) {
            selectPatternBtn.setSelection(false);
            namePatternText.setEnabled(false);
            namePatternText.setText("");
        } else {
            selectPatternBtn.setSelection(true);
            namePatternText.setEnabled(true);
            StringBuffer strBuffer = new StringBuffer();
            for (String name : nameFilterList) {
                strBuffer.append(name).append(",");
            }
            namePatternText.setText(strBuffer.substring(0, strBuffer.length() - 1));
        }

        ctv.setCheckedElements(defaultCheckedList.toArray());
        for (ICubridNode node : defaultGrayCheckedList) {
            ctv.setGrayChecked(node, true);
        }
    }

    /**
     * Create the name pattern composite
     *
     * @param parent Composite
     */
    private void createPatternComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);

        selectPatternBtn = new Button(composite, SWT.CHECK);
        selectPatternBtn.setText(Messages.btnNameFilter);
        selectPatternBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (selectPatternBtn.getSelection()) {
                            namePatternText.setEnabled(true);
                        } else {
                            namePatternText.setEnabled(false);
                            namePatternText.setText("");
                        }
                    }
                });

        namePatternText = new Text(composite, SWT.BORDER);
        namePatternText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        namePatternText.setEnabled(false);

        Label label = new Label(composite, SWT.LEFT);
        label.setText(Messages.lblNameFilter);
    }

    /**
     * Create the tree composite
     *
     * @param parent Composite
     */
    private void createTreeComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.LEFT);
        label.setText(Messages.lblTreeFilter);
        label.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        ctv =
                new CheckboxTreeViewer(
                        composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        ctv.getControl()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 300));
        ctv.setContentProvider(new FilterTreeContentProvider());
        ctv.setLabelProvider(tv.getLabelProvider(0));
        ctv.setInput(tv.getInput());

        ctv.addCheckStateListener(
                new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        ctv.setSubtreeChecked(event.getElement(), event.getChecked());
                        ctv.setGrayChecked(event.getElement(), false);
                        ctv.setChecked(event.getElement(), event.getChecked());
                        if (event.getElement() instanceof ICubridNode) {
                            ICubridNode node = (ICubridNode) event.getElement();
                            ICubridNode parent = node.getParent();
                            setParentStatus(parent);
                        }
                    }

                    /**
                     * Set parent element status
                     *
                     * @param parent ICubridNode
                     */
                    private void setParentStatus(ICubridNode parent) {
                        if (parent == null) {
                            return;
                        }

                        int state = getAllChildrenCheckedState(parent);
                        if (state == -1) {
                            ctv.setGrayChecked(parent, false);
                            ctv.setChecked(parent, false);
                        } else if (state == 1 || state == 0) {
                            ctv.setChecked(parent, false);
                            ctv.setGrayChecked(parent, true);
                        }
                        setParentStatus(parent.getParent());
                    }

                    /**
                     * Return checked children element status
                     *
                     * @param parent ICubridNode
                     * @return int <code>-1</code>all is not checked;<code>1</code>all is checked;
                     *     <code>0</code> some is checked,others is not checked
                     */
                    private int getAllChildrenCheckedState(ICubridNode parent) {
                        List<ICubridNode> childrenList = parent.getChildren();
                        boolean isAllChecked = true;
                        boolean isHasChecked = false;
                        for (ICubridNode node : childrenList) {
                            if (ctv.getGrayed(node)) {
                                isAllChecked = false;
                                isHasChecked = true;
                            } else if (ctv.getChecked(node)) {
                                isHasChecked = true;
                            } else {
                                isAllChecked = false;
                            }
                        }
                        if (!isHasChecked) {
                            return -1;
                        }
                        if (isAllChecked) {
                            return 1;
                        }
                        return 0;
                    }
                });

        Composite btnComposite = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginRight = 10;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = true;
        btnComposite.setLayoutData(gridData);
        btnComposite.setLayout(gridLayout);

        Button restoreBtn = new Button(btnComposite, SWT.PUSH);
        restoreBtn.setText(Messages.btnRest);
        restoreBtn.setLayoutData(CommonUITool.createGridData(GridData.END, 1, 1, -1, -1));
        restoreBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ctv.setCheckedElements(defaultCheckedList.toArray());
                        for (ICubridNode node : defaultGrayCheckedList) {
                            ctv.setGrayChecked(node, true);
                        }
                    }
                });
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleFilterSettingDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, true);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            NodeFilterManager nodeFilterManager = NodeFilterManager.getInstance();
            List<String> namePatterList = new ArrayList<String>();
            if (selectPatternBtn.getSelection()) {
                String str = namePatternText.getText();
                String[] patterns = str.split(",");
                for (String pattern : patterns) {
                    pattern = pattern.trim();
                    if (pattern.length() > 0) {
                        namePatterList.add(pattern);
                    }
                }
            }
            nodeFilterManager.setNameFilterList(namePatterList);

            List<String> idPatterList = new ArrayList<String>();
            List<String> idGrayPatterList = new ArrayList<String>();
            Object[] objs = ctv.getCheckedElements();
            for (int i = 0; objs != null && i < objs.length; i++) {
                Object obj = objs[i];
                if (obj instanceof ICubridNode) {
                    ICubridNode node = (ICubridNode) obj;
                    if (ctv.getGrayed(node)) {
                        idGrayPatterList.add(node.getId());
                    } else {
                        idPatterList.add(node.getId());
                    }
                }
            }
            nodeFilterManager.setIdFilterList(idPatterList);
            nodeFilterManager.setIdGrayFilterList(idGrayPatterList);
        }
        super.buttonPressed(buttonId);
    }
}
