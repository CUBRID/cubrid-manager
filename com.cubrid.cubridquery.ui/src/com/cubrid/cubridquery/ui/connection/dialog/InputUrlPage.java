/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridquery.ui.connection.dialog;

import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridquery.ui.connection.Messages;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The input url page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 20, 2012 created by Kevin.Wang
 */
public class InputUrlPage extends WizardPage {

    public static final String PAGENAME = "CreateConnectionByUrlWizard/InputUrlPage";

    private Text contentText;

    /** @param pageName */
    protected InputUrlPage() {
        super(PAGENAME);
    }

    public void createControl(Composite parent) {

        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);

        Label infoLabel = new Label(composite, SWT.None);
        infoLabel.setText(Messages.lblConnectionUrl);
        infoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        contentText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        contentText.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        contentText.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent e) {
                        if (contentText.getText().trim().length() > 0) {
                            setPageComplete(true);
                        } else {
                            setPageComplete(false);
                        }
                    }
                });

        setControl(composite);
        setPageComplete(false);

        setTitle(Messages.titleInputUrlPage);
        setMessage(Messages.msgCreateByURLDialog);
    }

    public String getUrls() {
        return contentText.getText();
    }
}
