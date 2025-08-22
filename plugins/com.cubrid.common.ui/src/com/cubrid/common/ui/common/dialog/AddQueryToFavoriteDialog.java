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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.util.Date;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddQueryToFavoriteDialog extends CMTitleAreaDialog {
    private Text txtFileName;
    private Text txtMemo;
    private Combo cbCharset;
    private String charset;
    private String basepath;
    private String query;
    private String filename;
    private String memo;

    public AddQueryToFavoriteDialog(
            Shell parentShell, String basepath, String query, String charset) {
        super(parentShell);
        this.basepath = basepath;
        this.query = query;
        this.charset = charset == null ? StringUtil.getDefaultCharset() : charset;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(400, 240);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleSqlFavorite);
        setTitle(Messages.titleSqlFavoriteDetail);
        setMessage(Messages.msgSqlFavoriteDetail);
    }

    /**
     * Create the dialog area
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblFileName = new Label(composite, SWT.LEFT);
        lblFileName.setText(Messages.lblFileName);
        lblFileName.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        txtFileName = new Text(composite, SWT.LEFT | SWT.BORDER);
        txtFileName.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Label lblFileExt = new Label(composite, SWT.LEFT);
        lblFileExt.setText(".sql");
        lblFileExt.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        cbCharset = new Combo(composite, SWT.LEFT);
        cbCharset.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        cbCharset.setItems(QueryOptions.getAllCharset(null));
        cbCharset.setText(charset);

        Label lblMemo = new Label(composite, SWT.LEFT);
        lblMemo.setText(Messages.lblMemo);
        lblMemo.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        txtMemo = new Text(composite, SWT.LEFT | SWT.BORDER);
        txtMemo.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));

        String now = DateUtil.getDatetimeString(new Date(), "yyyyMMdd_HHmmss");
        txtFileName.setText(now);
        txtFileName.setSelection(0, txtFileName.getText().length());

        return parentComp;
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnSaveToFavorite, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            File file = new File(basepath);
            try {
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errmsg =
                        Messages.bind(Messages.errCanNotSaveFileIntoFavorite, e.getMessage());
                CommonUITool.openErrorBox(errmsg);
                return;
            }

            filename = txtFileName.getText().trim() + ".sql";
            memo = txtMemo.getText().trim();

            String filepath = basepath + File.separator + filename;
            file = new File(filepath);
            try {
                if (file.exists()) {
                    CommonUITool.openErrorBox(Messages.errDuplicatedNameFavorite);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errmsg =
                        Messages.bind(Messages.errCanNotSaveFileIntoFavorite, e.getMessage());
                CommonUITool.openErrorBox(errmsg);
                return;
            }

            boolean success = FileUtil.writeToFile(filepath, query, charset);
            if (!success) {
                CommonUITool.openErrorBox(Messages.errSaveFailedFavorite);
                return;
            }
        }
        super.buttonPressed(buttonId);
    }

    public String getBasepath() {
        return basepath;
    }

    public String getFilename() {
        return filename;
    }

    public String getMemo() {
        return memo;
    }
}
