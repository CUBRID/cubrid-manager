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
package com.cubrid.common.ui.cubrid.table.control;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.List;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Hash Partition Edit Page
 *
 * @author pangqiren
 * @version 1.0 - 2010-3-15 created by pangqiren
 */
public class PartitionEditHashPage extends WizardPage
        implements ModifyListener, IPageChangedListener {

    public static final String PAGENAME = "CreatePartitionWizard/PartitionEditHashPage";

    private PartitionInfo editedPartitionInfo = null;
    private final List<PartitionInfo> partitionInfoList;
    private boolean isCanFinished = true;
    private Text partitionNumText;

    private Text partitionTypeText;

    private Text partitionExprText;

    protected PartitionEditHashPage(List<PartitionInfo> partitionInfoList) {
        super(PAGENAME);
        this.partitionInfoList = partitionInfoList;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createHashPartitionGroup(composite);
        init();

        setTitle(Messages.titleHashPage);
        setMessage(Messages.msgHashPage);
        setControl(composite);
    }

    /** Initial the page content */
    private void init() {
        if (this.editedPartitionInfo != null) {
            partitionNumText.setText(String.valueOf(partitionInfoList.size()));
        }
    }

    /**
     * create hash partition
     *
     * @param parent Composite
     */
    private void createHashPartitionGroup(Composite parent) {
        Group partitionInfoGroup = new Group(parent, SWT.NONE);
        partitionInfoGroup.setText(Messages.grpPartitionInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        partitionInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        partitionInfoGroup.setLayout(layout);

        Composite composite = new Composite(partitionInfoGroup, SWT.NONE);
        composite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        Label partitionTypeLabel = new Label(composite, SWT.NONE);
        partitionTypeLabel.setText(Messages.lblPartitionType);
        partitionTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        partitionTypeText = new Text(composite, SWT.BORDER);
        partitionTypeText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        partitionTypeText.setEnabled(false);

        Label partitionExprLabel = new Label(composite, SWT.NONE);
        partitionExprLabel.setText(Messages.lblPartitionExpr);
        partitionExprLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        partitionExprText = new Text(composite, SWT.BORDER);
        partitionExprText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        partitionExprText.setEnabled(false);

        final Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.lblPartitionNumber);
        label.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        partitionNumText = new Text(composite, SWT.BORDER);
        partitionNumText.setText("1");
        partitionNumText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        partitionNumText.addModifyListener(this);
        partitionNumText.setFocus();
    }

    /**
     * When modify text and check the information validity
     *
     * @param event the Modify event
     */
    public void modifyText(ModifyEvent event) {
        setPageComplete(validate());
    }

    /**
     * Check the page content validity
     *
     * @return <code>true</code> if valid;otherwise <code>false</code>
     */
    private boolean validate() {
        int val = StringUtil.intValue(partitionNumText.getText().trim());
        if (val <= 0 || val > 1024) {
            setErrorMessage(Messages.errPartitionNumber);
            isCanFinished = false;
            return false;
        }
        isCanFinished = true;
        setErrorMessage(null);
        return true;
    }

    public boolean isCanFinished() {
        return this.isCanFinished;
    }

    /**
     * Call this method when page changed
     *
     * @param event the page changed event
     */
    public void pageChanged(PageChangedEvent event) {
        IWizardPage page = (IWizardPage) event.getSelectedPage();
        if (page.getName().equals(PAGENAME)) {
            PartitionTypePage partitionTypePage =
                    (PartitionTypePage) getWizard().getPage(PartitionTypePage.PAGENAME);
            String partitionType = partitionTypePage.getPartitionType();
            String partitionExpr = partitionTypePage.getPartitionExpr();
            partitionTypeText.setText(partitionType);
            partitionExprText.setText(partitionExpr);
            partitionNumText.selectAll();
            partitionNumText.setFocus();
        }
    }

    public String getNumberOfPartitions() {
        return partitionNumText.getText().trim();
    }

    public void setEditedPartitionInfo(PartitionInfo editedPartitionInfo) {
        this.editedPartitionInfo = editedPartitionInfo;
    }
}
